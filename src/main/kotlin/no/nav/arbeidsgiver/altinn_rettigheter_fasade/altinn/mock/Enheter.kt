package no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn.mock

import no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene.Reportee


val gunnarsGrønnsakshandelHovedenhet = Reportee(
    Name = "Gunnars «hovedenhet» grønnsakshandel",
    OrganizationNumber = "112233445",
    Type = "",
    OrganizationForm = "",
    Status = "Active",
)

val gunnarsGrønnsakshandelUnderenhet = Reportee(
    Name = "Gunnars «underenhet» grønnsakshandel",
    OrganizationNumber = "321321321",
    ParentOrganizationNumber = gunnarsGrønnsakshandelHovedenhet.OrganizationNumber,
    Type = "",
    OrganizationForm = "",
    Status = "Active"
)

val reidunsRevisortjenesterHovedenhet = Reportee(
    Name = "Reiduns «hovedenhet» revisjortjenester",
    OrganizationNumber = "321321221",
    Type = "",
    OrganizationForm = "",
    Status = "Active"
)

val reidunsRevisortjenesterUnderenhet = Reportee(
    Name = "Reiduns «hovedenhet» revisjortjenester",
    OrganizationNumber = "321311211",
    ParentOrganizationNumber = reidunsRevisortjenesterHovedenhet.OrganizationNumber,
    Type = "",
    OrganizationForm = "",
    Status = "Active"
)

val alleEnheter = listOf(
    gunnarsGrønnsakshandelHovedenhet,
    gunnarsGrønnsakshandelUnderenhet,
    reidunsRevisortjenesterHovedenhet,
    reidunsRevisortjenesterUnderenhet
)