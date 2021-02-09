package no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn.mock

import no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene.Service

data class Person(
    val navn: String,
    val fnr: String,
    val tilganger: Map<String, List<Service>>
)

val gunnar = Person(
    navn = "Gunnar",
    fnr = "32112312345",
    tilganger = mapOf(
        gunnarsGrønnsakshandelHovedenhet.OrganizationNumber to alleServices,
        gunnarsGrønnsakshandelUnderenhet.OrganizationNumber to alleServices
    )
)

val reidun = Person(
    navn = "Reidun",
    fnr = "1122334455",
    tilganger = mapOf(
        reidunsRevisortjenesterUnderenhet.OrganizationNumber to alleServices,
        reidunsRevisortjenesterHovedenhet.OrganizationNumber to alleServices,
        gunnarsGrønnsakshandelUnderenhet.OrganizationNumber to listOf(regnskapService)
    )
)

val allePersoner = listOf(gunnar, reidun)

