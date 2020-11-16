package no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene

import no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn.Altinn
import org.slf4j.LoggerFactory


class Operasjoner(private val altinn: Altinn) {
    private val log = LoggerFactory.getLogger(Operasjoner::class.java)

    /* Hent alle organisasjoner som brukeren kan representere (reportee). */
    suspend fun alleOrganisasjoner(fnr: String): Collection<Reportee> {
        log.info("fnr={}", fnr)
        return altinn.getReportees(fnr)
    }

    /* Hent alle organisasjoner hvor brukeren har tilgang til den gitte tjenesten. */
    suspend fun alleRettigheter(fnr: String, service: Service): Collection<Reportee> =
        altinn.getReportees(fnr, service)

    /* Hent de organisasjoner hvor brukeren har tilgang til minst en av de gitte tjenestene. */
    suspend fun alleRettigheter(fnr: String, services: Collection<Service>): Collection<Reportee> {
        return listOf()
    }

    /* Hent alle forespørsler om tilgang for brukeren. */
    suspend fun alleForespørsler(fnr: String): Collection<DelegationRequest> =
        altinn.getDelegationRequests(fnr)

    /* Hent alle forespørsler om tilgang for den gitte tjenesten for brukeren */
    suspend fun alleForespørsler(fnr: String, service: Service): Collection<DelegationRequest> =
        altinn.getDelegationRequests(fnr, service)
}
