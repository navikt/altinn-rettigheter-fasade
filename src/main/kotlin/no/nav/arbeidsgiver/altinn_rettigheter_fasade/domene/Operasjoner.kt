package no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene

import kotlinx.coroutines.*
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn.Altinn
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.util.getLogger

class Operasjoner(private val altinn: Altinn) {
    private val log = getLogger(this::class)

    /* Hent alle organisasjoner som brukeren kan representere (reportee). Slå også opp tilganger. */
    suspend fun alleOrganisasjoner(fnr: String, services: Collection<Service>): Collection<Reportee> = withContext(Dispatchers.IO) {
        val reporteesAsync = async { altinn.getReportees(fnr) }

        val reporteesWithServiceAccessAsync = services.map {
            async { it to
                    altinn.getReportees(fnr, it)
                        .map { it.OrganizationNumber }
            }
        }

        val reportees = reporteesAsync.await()

        val reporteesWithServiceAccess = reporteesWithServiceAccessAsync
            .awaitAll()
            .toMap()


        reportees.map { reportee ->
            reportee.copy(
                Services = services.filter { service ->
                    reporteesWithServiceAccess[service]?.contains(reportee.OrganizationNumber) ?: false
                }
            )
        }
    }

    suspend fun alleForespørsler(fnr: String): Collection<DelegationRequest> {
        return altinn.getDelegationRequests(fnr)
    }

    suspend fun opprettForespørsel(fnr: String, delegationRequest: CreateDelegationRequest): DelegationRequest {
        TODO()
    }
}
