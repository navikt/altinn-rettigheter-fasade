package no.nav.arbeidsgiver.altinn_rettigheter_fasade

import no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn.realAltinn
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.http_server.Issuer
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.http_server.createAuthenticationConfig

fun main() {
    start(
        createAuthenticationConfig(
            Issuer(
                name = "loginservice",
                discoveryurl = System.getenv("LOGINSERVICE_IDPORTEN_DISCOVERY_URL"),
                accepted_audience = System.getenv("LOGINSERVICE_IDPORTEN_AUDIENCE"),
                cookiename = "selvbetjening-idtoken"
            )
        ),
        realAltinn(
            baseUrl = System.getenv("ALTINN_URL"),
            altinnApikey = System.getenv("ALTINN_APIKEY"),
            navGatewayApikey = System.getenv("NAV_GATEWAY_APIKEY")
        ))
}
