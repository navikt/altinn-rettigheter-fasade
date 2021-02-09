package no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn.mock

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn.Altinn
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene.CreateDelegationRequest
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene.DelegationRequest
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene.Reportee
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene.Service

fun mockAltinn(): Altinn = Altinn(
    httpClientEngine =  MockEngine,
    httpClientEngineConfig = mockAltinnServer,
    baseUrl = "http://altinn",
)

private val objectMapper = ObjectMapper()

private fun MockRequestHandleScope.jsonResponse(body: Any): HttpResponseData =
    respond(
        objectMapper.writeValueAsBytes(body),
        headers = headersOf("content-type", "application/json")
    )

private val mockAltinnServer: MockEngineConfig.() -> Unit = {
    addHandler { request ->
        val fnr = request.url.parameters["subject"]
        val person = allePersoner.find { it.fnr == fnr }
        val path = request.url.encodedPath
        when {
            person == null ->
                respond("unknown subject $fnr", status = HttpStatusCode.Unauthorized)

            path == "/api/serviceowner/reportees" -> {
                val code = request.url.parameters["ServiceCode"]
                val edition = request.url.parameters["ServiceEditionCode"]
                val service = if (code != null && edition != null) Service(code, edition) else null
                when (request.method) {
                    HttpMethod.Get -> jsonResponse(mockReportees(person, service))
                    else -> respond("unexpected method", HttpStatusCode.NotFound)
                }
            }

            path == "/api/serviceowner/delegationRequests" -> {
                when (request.method) {
                    HttpMethod.Get -> jsonResponse(mockGetDelegationRequests(person))
                    HttpMethod.Post ->  {
                        val request = objectMapper.readValue(request.body.toByteArray(), CreateDelegationRequest::class.java)
                        jsonResponse(mockPostDelegationRequests(request))
                    }
                    else -> respond("unexpected method", HttpStatusCode.NotFound)
                }
            }

            else ->
                respond("", HttpStatusCode.NotFound)
        }
    }
}

private fun mockReportees(person: Person, service: Service?): Collection<Reportee> =
    if (service == null) {
        alleEnheter.filter {
            it.OrganizationNumber in person.tilganger.keys
        }
    } else {
        alleEnheter.filter {
            person.tilganger[it.OrganizationNumber]?.contains(service) ?: false
        }
    }

private fun mockGetDelegationRequests(person: Person): Collection<DelegationRequest> {
    return alleDelegationRequests.filter {
        it.CoveredBy == person.fnr
    }
}

private fun mockPostDelegationRequests(request: CreateDelegationRequest): DelegationRequest {
    return alleDelegationRequests[0]
}


