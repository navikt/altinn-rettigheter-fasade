package no.nav.arbeidsgiver.altinn_rettigheter_fasade.http_rest_server

import com.fasterxml.jackson.databind.MapperFeature
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.config.*
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
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene.Interaksjoner
import no.nav.security.token.support.ktor.tokenValidationSupport
import org.slf4j.event.Level
import java.util.*

data class Issuer(
    val name: String,
    val discoveryurl: String,
    val accepted_audience: String,
    val cookiename: String?
)

fun runHttpServer(
    issuers: List<Issuer>,
    meterRegistry: PrometheusMeterRegistry,
    interaksjoner: Interaksjoner
) {
    val callIdKey = AttributeKey<String>("callId")

    val getFnr = lagFnrExtractor(issuers)

    embeddedServer(Netty, port = 8080) {

        install(Authentication) {
            val config = mutableListOf("no.nav.security.jwt.issuers.size" to issuers.size.toString())

            issuers.flatMapIndexedTo(config) { i, issuer ->
                listOfNotNull(
                    "no.nav.security.jwt.issuers.$i.issuer_name" to issuer.name,
                    "no.nav.security.jwt.issuers.$i.discoveryurl" to issuer.discoveryurl,
                    "no.nav.security.jwt.issuers.$i.accepted_audience" to issuer.accepted_audience,
                    issuer.cookiename?.let {
                        "no.nav.security.jwt.issuers.$i.cookie_name" to it
                    }
                )
            }

            log.info("token validation configuration: {}", issuers.joinToString("\n"))

            tokenValidationSupport(config = MapApplicationConfig(*config.toTypedArray()))
        }

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
                    get("tilganger") {
                        val fnr = getFnr()
                        val requestBody = call.receiveOrNull<HttpDTO.GetTilgangerRequestBody>()

                        val result = interaksjoner.alleOrganisasjoner(
                            fnr,
                            requestBody?.services?.map {it.toDomain () }
                        )

                        call.respond(HttpDTO.createGetTilgangerResponseBody(result))
                    }
                }
            }
        }
    }.start(true)
}

