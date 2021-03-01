package no.nav.arbeidsgiver.altinn_rettigheter_fasade

import no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn_integration.REAL_ALTINN_HTTP_CLIENT_BUILDER
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.http_rest_server.Issuer


private fun env(name: String): String =
    System.getenv(name)
        ?: throw IllegalArgumentException("No environment variable with name '$name'")

fun main() {
    start(
        Config(
            issuers = listOf(
                Issuer(
                    name = "loginservice",
                    discoveryurl = env("LOGINSERVICE_IDPORTEN_DISCOVERY_URL"),
                    accepted_audience = env("LOGINSERVICE_IDPORTEN_AUDIENCE"),
                    cookiename = "selvbetjening-idtoken"
                )
            ),
            altinn = AltinnConfig(
                altinnHttpClientConfig = REAL_ALTINN_HTTP_CLIENT_BUILDER,
                baseUrl = env("ALTINN_URL"),
                altinnApikey = env("ALTINN_APIKEY"),
                navGatewayApikey = env("NAV_GATEWAY_APIKEY")
            )
        )
    )
}
