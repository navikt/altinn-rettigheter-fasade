package no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn_integration

import com.fasterxml.jackson.databind.MapperFeature
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn_integration.AltinnDTO
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn_integration.mock.mockAltinnServer
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene.Service
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.util.logger

typealias AltinnHttpClientBuilder = (HttpClientConfig<*>.() -> Unit) -> HttpClient

val MOCK_ALTINN_HTTP_CLIENT_BUILDER: AltinnHttpClientBuilder = {
    HttpClient(MockEngine) {
        it()
        engine {
            mockAltinnServer()
        }
    }
}

val REAL_ALTINN_HTTP_CLIENT_BUILDER: AltinnHttpClientBuilder = {
    HttpClient(Apache) {
        it()
    }
}

class AltinnHttpClient(
    altinnHttpClientBuilder: AltinnHttpClientBuilder,
    baseUrl: String,
    altinnApikey: String,
    navGatewayApikey: String
) {
    companion object {
        private val log = logger()
    }

    private val httpClient = altinnHttpClientBuilder {
        install(JsonFeature) {
            serializer = JacksonSerializer {
                enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
            }
        }
    };

    private val altinnheaders = headersOf(
        "apikey" to listOf(altinnApikey),
        "x-nav-apikey" to listOf(navGatewayApikey),
        "accept" to listOf("application/json")
    )

    private val reporteesUrl = URLBuilder(baseUrl)
        .pathComponents("api", "serviceowner", "reportees")
        .apply {
            parameters.appendAll("ForceEIAuthentication", listOf())
        }

    private val delegationRequestUrl = URLBuilder(baseUrl)
        .pathComponents("api", "serviceowner", "delegationRequests")
        .apply {
            parameters.appendAll("ForceEIAuthentication", listOf())
        }

    init {
        log.info(
            "reporteesUrl={} delegationRequestUrl={}",
            reporteesUrl.buildString(),
            delegationRequestUrl.buildString()
        )
    }

    private suspend inline fun <reified T>HttpClient.authenticatedGet(
        urlBuilder: URLBuilder,
        vararg withParameters: Pair<String, String>
    ): T {
        val url = urlBuilder.clone().apply {
            withParameters.forEach {
                parameters.append(it.first, it.second)
            }
        }.build()

        return this.get(url) {
            headers.appendAll(altinnheaders)
        }
    }

    suspend fun getReportees(fnr: String): List<AltinnDTO.Reportee> =
        httpClient.authenticatedGet(
            reporteesUrl,
            "subject" to fnr,
            "\$filter" to "Type ne 'Person' and Status eq 'Active'"
        )

    suspend fun getReportees(fnr: String, service: Service): List<AltinnDTO.Reportee> =
        httpClient.authenticatedGet(
            reporteesUrl,
            "subject" to fnr,
            "\$filter" to "Type ne 'Person' and Status eq 'Active'",
            "serviceCode" to service.code,
            "serviceEditionCode" to service.edition.toString()
        )

    suspend fun getDelegationRequests(fnr: String): List<AltinnDTO.DelegationRequest> =
        httpClient.authenticatedGet(delegationRequestUrl, "subject" to fnr)

    suspend fun getDelegationRequests(fnr: String, service: Service): List<AltinnDTO.DelegationRequest> =
        httpClient.authenticatedGet(
            delegationRequestUrl,
            "subject" to fnr,
            "serviceCode" to service.code,
            "serviceEditionCode" to service.edition.toString()
        )

    suspend fun postDelegationRequest(skjema: AltinnDTO.CreateDelegationRequest): AltinnDTO.DelegationRequest =
        httpClient.post(delegationRequestUrl.build()) {
            headers.appendAll(altinnheaders)
            body = skjema
        }
}
