package no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene

data class Reportee(
    val Name: String,
    val Type: String,
    val Status: String?,
    val OrganizationNumber: String,
    val ParentOrganizationNumber: String? = null,
    val OrganizationForm: String?,
    val Services: List<Service>? = null
)
