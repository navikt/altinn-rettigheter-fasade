package no.nav.arbeidsgiver.altinn_rettigheter_fasade.http_server

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.metrics.micrometer.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.slf4j.event.Level
import java.util.*

fun startHttpServer(
    authenticationConfig: AuthenticationConfig,
    endpoints: Route.() -> Unit,
    meterRegistry: PrometheusMeterRegistry
) {
    embeddedServer(Netty, port = 8080) {

        install(Authentication, authenticationConfig.ktorConfig)

        install(ContentNegotiation) {
            jackson()
        }

        install(MicrometerMetrics) {
            registry = meterRegistry
        }

        install(CallLogging) {
            filter { call ->
                !call.request.path().startsWith("/internal/")
            }

            level = Level.INFO
            mdc("method") { call ->
                call.request.httpMethod.value
            }
            mdc("path") { call ->
                call.request.path()
            }
            mdc("callId") {
                UUID.randomUUID().toString()
            }
        }

        install(StatusPages) {
            exception<Throwable> { ex ->
                log.warn("Exception {}", ex.message, ex)
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        routing {
            route("internal") {
                get("liveness") {
                    call.respond(200)
                }

                get("readiness") {
                    call.respond(200)
                }

                get("metrics") {
                    call.respond(meterRegistry.scrape())
                }
            }

            authenticate {
                route("api") {
                    endpoints()
                }
            }
        }
    }.start(true)
}
