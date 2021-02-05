package no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn


import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene.DelegationRequest
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene.CreateDelegationRequest
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene.Reportee
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene.Service
import org.slf4j.LoggerFactory

fun realAltinn(baseUrl: String, altinnApikey: String, navGatewayApikey: String): Altinn = Altinn(
    httpClientEngine = Apache,
    baseUrl = baseUrl,
    altinnApikey = altinnApikey,
    navGatewayApikey = navGatewayApikey
)

class Altinn private constructor (
    private val httpClient: HttpClient,
    baseUrl: String,
    altinnApikey: String = "",
    navGatewayApikey: String = ""
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    private val altinnheaders = headersOf(
        "apikey" to listOf(altinnApikey),
        "x-nav-apikey" to listOf(navGatewayApikey),
        "accept" to listOf("application/json")
    )

    private val reporteesUrl = URLBuilder(baseUrl)
        .pathComponents(
            "api", "serviceowner", "reportees"
        ).apply {
            parameters.appendAll("ForceEIAuthentication", listOf())
        }

    private val delegationRequestUrl = URLBuilder(baseUrl)
        .pathComponents(
            "api", "serviceowner", "delegationRequests"
        )
        .apply {
            parameters.appendAll("ForceEIAuthentication", listOf())
        }

    init {
        log.info("reporteesUrl={} delegationRequestUrl={}",
            reporteesUrl.buildString(),
            delegationRequestUrl.buildString()
        )
    }

    companion object {
        operator fun <T : HttpClientEngineConfig> invoke(
            httpClientEngine: HttpClientEngineFactory<T>,
            httpClientEngineConfig: T.() -> Unit = {},
            baseUrl: String,
            altinnApikey: String = "",
            navGatewayApikey: String = ""
        ) =
            Altinn(
                httpClient = HttpClient(httpClientEngine) {
                    engine(httpClientEngineConfig)

                    install(JsonFeature) {
                        serializer = JacksonSerializer()
                    }
                },
                baseUrl,
                altinnApikey,
                navGatewayApikey
            )
    }

    private suspend inline fun <reified T> HttpClient.authenticatedGet(
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

    suspend fun getReportees(fnr: String): Collection<Reportee> =
        httpClient.authenticatedGet(reporteesUrl,
            "subject" to fnr,
            "\$filter" to "Type ne 'Person' and Status eq 'Active'"
        )

    suspend fun getReportees(fnr: String, service: Service): Collection<Reportee> =
        httpClient.authenticatedGet( reporteesUrl,
            "subject" to fnr,
            "\$filter" to "Type ne 'Person' and Status eq 'Active'",
            "serviceCode" to service.code,
            "serviceEditionCode" to service.editionCode
        )

    suspend fun getDelegationRequests(fnr: String): Collection<DelegationRequest> =
        httpClient.authenticatedGet(delegationRequestUrl, "subject" to fnr)

    suspend fun getDelegationRequests(fnr: String, service: Service): Collection<DelegationRequest> =
        httpClient.authenticatedGet(
            delegationRequestUrl,
            "subject" to fnr,
            "serviceCode" to service.code,
            "serviceEditionCode" to service.editionCode
        )

    suspend fun postDelegationRequest(skjema: CreateDelegationRequest): DelegationRequest =
        httpClient.post(delegationRequestUrl.build()) {
            headers.appendAll(altinnheaders)
            body = skjema
        }
}
