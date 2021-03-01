package no.nav.arbeidsgiver.altinn_rettigheter_fasade

import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn_integration.AltinnService
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn_integration.AltinnHttpClient
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn_integration.AltinnHttpClientBuilder
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene.Interaksjoner
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.http_rest_server.*

data class Config(
    val issuers: List<Issuer>,
    val altinn: AltinnConfig,
)

data class AltinnConfig(
    val altinnHttpClientConfig: AltinnHttpClientBuilder,
    val baseUrl: String,
    val altinnApikey: String,
    val navGatewayApikey: String
)

fun start(config: Config) {
    val meterRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    val interaksjoner = Interaksjoner(
        AltinnService(
            config.altinn.run {
                AltinnHttpClient(
                    altinnHttpClientBuilder = altinnHttpClientConfig,
                    baseUrl = baseUrl,
                    altinnApikey = altinnApikey,
                    navGatewayApikey = navGatewayApikey
                )
            }
        )
    )
    runHttpServer(config.issuers,  meterRegistry, interaksjoner)
}

