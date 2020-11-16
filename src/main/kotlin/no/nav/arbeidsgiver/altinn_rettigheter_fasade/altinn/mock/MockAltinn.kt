package no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn.mock

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn.Altinn

fun mockAltinn(): Altinn = Altinn(
    httpClientEngine =  MockEngine,
    httpClientEngineConfig = mockAltinnServer,
    baseUrl = "http://altinn",
)

val mockAltinnServer: MockEngineConfig.() -> Unit = {
    addHandler {
        respond("[]", headers= headersOf("content-type", "application/json"))
    }
}
