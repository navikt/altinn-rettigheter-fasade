package no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn


import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
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

    private val reporteesUrl = "$baseUrl/api/serviceowner/reportees?ForceEIAuthentication"

    private val delegationRequestUrl = "$baseUrl/api/serviceowner/delegationRequests?ForceEIAuthentication"

    init {
        log.info("reporteesUrl={} delegationRequestUrl={}", reporteesUrl, delegationRequestUrl)
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

    private suspend inline fun <reified T> HttpClient.authenticatedGet(url: String): T =
        this.get(url) {
            headers.appendAll(altinnheaders)
        }

    suspend fun getReportees(fnr: String): Collection<Reportee> =
        httpClient.authenticatedGet(
            "$reporteesUrl&subject=$fnr"
        )

    suspend fun getReportees(fnr: String, service: Service): Collection<Reportee> =
        httpClient.authenticatedGet(
            "$reporteesUrl&subject=$fnr&serviceCode=${service.code}&serviceEditionCode=${service.editionCode}"
        )

    suspend fun getDelegationRequests(fnr: String): Collection<DelegationRequest> =
        httpClient.authenticatedGet(
            "$delegationRequestUrl&subject=$fnr"
        )

    suspend fun getDelegationRequests(fnr: String, service: Service): Collection<DelegationRequest> =
        httpClient.authenticatedGet(
            "$delegationRequestUrl&subject=$fnr&serviceCode=${service.code}&serviceEditionCode=${service.editionCode}"
        )

    suspend fun postDelegationRequest(skjema: CreateDelegationRequest): DelegationRequest =
        httpClient.post(delegationRequestUrl) {
            headers.appendAll(altinnheaders)
            body = skjema
        }
}
