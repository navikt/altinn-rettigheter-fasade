package no.nav.arbeidsgiver.altinn_rettigheter_fasade

import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn.Altinn
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene.Operasjoner
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.http_server.*

fun start(
    authenticationConfig: AuthenticationConfig,
    altinn: Altinn
) {
    val meterRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    val operasjoner = Operasjoner(altinn)
    val endpoints = createEndpoints(authenticationConfig, operasjoner)
    startHttpServer(authenticationConfig, endpoints, meterRegistry)
}

