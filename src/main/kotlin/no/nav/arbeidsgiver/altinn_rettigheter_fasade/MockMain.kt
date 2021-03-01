package no.nav.arbeidsgiver.altinn_rettigheter_fasade

import no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn_integration.MOCK_ALTINN_HTTP_CLIENT_BUILDER
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.http_rest_server.Issuer

fun main() {
    start(
        Config(
            issuers = listOf(
                Issuer(
                    name = "https://fakedings.dev-gcp.nais.io/fake",
                    discoveryurl = "https://fakedings.dev-gcp.nais.io/fake/.well-known/openid-configuration",
                    accepted_audience = "mockedaudience",
                    cookiename = "fakedings"
                )
            ),
            altinn = AltinnConfig(
                altinnHttpClientConfig = MOCK_ALTINN_HTTP_CLIENT_BUILDER,
                baseUrl = "https://mock-altinn",
                altinnApikey = "mock-altinn-apikey",
                navGatewayApikey = "mock-nav-gateway-apikey"
            )
        )
    )
}
