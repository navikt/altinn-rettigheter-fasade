package no.nav.arbeidsgiver.altinn_rettigheter_fasade.http_server

import com.fasterxml.jackson.databind.MapperFeature
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
import io.ktor.util.*
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.slf4j.event.Level
import java.util.*

fun startHttpServer(
    authenticationConfig: AuthenticationConfig,
    endpoints: Route.() -> Unit,
    meterRegistry: PrometheusMeterRegistry
) {
    val callIdKey = AttributeKey<String>("callId")

    embeddedServer(Netty, port = 8080) {

        install(Authentication, authenticationConfig.ktorConfig)

        install(ContentNegotiation) {
            jackson {
               configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            }
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
            mdc("callId") { call ->
                UUID.randomUUID().toString().also {
                    call.attributes.put(callIdKey, it)
                }
            }
        }

        install(StatusPages) {
            exception<Throwable> { ex ->
                log.warn("unhandle exception in ktor pipeline: {}", ex::class.qualifiedName, ex)
                call.respond(
                    HttpStatusCode.InternalServerError, mapOf(
                        "error" to "unexpected error",
                        "callId" to call.attributes[callIdKey]
                    )
                )
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
