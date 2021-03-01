package no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene

import no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn_integration.AltinnService
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn_integration.AltinnDTO
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.util.logger

data class Service(
    val code: String,
    val edition: Int
)

data class OrganizationInformation(
    val reportee: AltinnDTO.Reportee,
    val serviceacceess: List<Service>?,
    val delegationRequests: List<AltinnDTO.DelegationRequest>?
)

class Interaksjoner(private val altinnService: AltinnService) {
    companion object {
        private val log = logger()
    }

    suspend fun alleOrganisasjoner(
        fnr: String,
        services: Collection<Service>?
    ): Collection<OrganizationInformation> {
        val reporteesAsync = altinnService.reporteesAsync(fnr)
        val reporteesWithServiceAccessAsync = altinnService.reporteesWithServiceAccessAsync(fnr, services ?: emptyList())
        val delegationRequestsAsync = altinnService.delegationRequestsAsync(fnr)

        val reportees = reporteesAsync.await()
        val reporteesWithServiceAccess = reporteesWithServiceAccessAsync.await()
        val delegationRequests = delegationRequestsAsync.await();

        return reportees.map { (orgnr, reportee) ->
            OrganizationInformation(
                reportee = reportee,
                serviceacceess = services?.filter { service ->
                    reporteesWithServiceAccess[service]?.contains(orgnr) ?: false
                },
                delegationRequests =
                if (services == null)
                    null
                else delegationRequests.filter {
                    it.OfferedBy == orgnr && it.RequestResources.any { requestedResource ->
                        services.any { queriedResource ->
                            requestedResource.ServiceCode == queriedResource.code &&
                                    requestedResource.ServiceEditionCode == queriedResource.edition
                        }
                    }
                }
            )
        }
    }
}


