package no.nav.arbeidsgiver.altinn_rettigheter_fasade

import no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn.mock.mockAltinn
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.http_server.Issuer
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.http_server.createAuthenticationConfig

fun main() {
    start(
        createAuthenticationConfig(
            Issuer(
                name = "https://fakedings.dev-gcp.nais.io/fake",
                discoveryurl = "https://fakedings.dev-gcp.nais.io/fake/.well-known/openid-configuration",
                accepted_audience = "mockedaudience",
                cookiename = "fakedings"
            )
        ),
        mockAltinn()
    )
}
