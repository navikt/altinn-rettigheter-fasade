package no.nav.arbeidsgiver.altinn_rettigheter_fasade.http_server

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene.*

fun createEndpoints(authenticationConfig: AuthenticationConfig, operasjoner: Operasjoner): Route.() -> Unit {
    val fnr = authenticationConfig.fnrExtractor

    return {
        get("reportees") {
            call.respond(operasjoner.alleOrganisasjoner(fnr()))
        }
    }
}
