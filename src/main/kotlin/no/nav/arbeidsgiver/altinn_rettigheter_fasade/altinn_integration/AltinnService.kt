package no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn_integration

import kotlinx.coroutines.*
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn_integration.AltinnDTO
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene.Service

class AltinnService(private val altinnHttpClient: AltinnHttpClient) {

    suspend fun reporteesAsync(fnr: String): Deferred<Map<String, AltinnDTO.Reportee>> = coroutineScope {
        async {
            altinnHttpClient.getReportees(fnr)
                .associateBy { it.OrganizationNumber }
        }
    }

    suspend fun delegationRequestsAsync(fnr: String): Deferred<List<AltinnDTO.DelegationRequest>> = coroutineScope {
        async {
            altinnHttpClient.getDelegationRequests(fnr)
        }
    }

    suspend fun reporteesWithServiceAccessAsync(
        fnr: String,
        services: Collection<Service>
    ): Deferred<Map<Service, Collection<String>>> = coroutineScope {
        val queries = services.map {
            async {
                it to
                        altinnHttpClient
                            .getReportees(fnr, it)
                            .map { it.OrganizationNumber }.toSet()
            }
        }
        async {
            queries.awaitAll().toMap()
        }
    }
}
