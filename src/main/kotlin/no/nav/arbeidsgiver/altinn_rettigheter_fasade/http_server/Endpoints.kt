package no.nav.arbeidsgiver.altinn_rettigheter_fasade.http_server

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene.CreateDelegationRequest
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene.Operasjoner
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene.Service
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.util.getLogger

data class RettigheterRequestBody(
    val services: List<Service>
)

private class Endpoints private constructor()
private val log = getLogger(Endpoints::class)

fun createEndpoints(authenticationConfig: AuthenticationConfig, operasjoner: Operasjoner): Route.() -> Unit {
    val fnr = authenticationConfig.fnrExtractor

    return {
        get("reportees") {
            val requestBody = call.receiveOrNull<RettigheterRequestBody>()
            call.respond(operasjoner.alleOrganisasjoner(
                fnr(),
                requestBody?.services ?: emptyList()
            ))
        }

        route("delegationRequests") {
            get {
                call.respond(
                    operasjoner.alleForespørsler(fnr())
                )
            }

            post {
                val body = call.receive<CreateDelegationRequest>()
                call.respond(
                    operasjoner.opprettForespørsel(fnr(), body)
                )
            }
        }
    }
}
