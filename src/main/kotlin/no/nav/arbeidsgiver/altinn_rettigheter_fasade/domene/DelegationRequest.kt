package no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene

data class CreateDelegationRequest(
    val CoveredBy: String,
    val OfferedBy: String,
    val RedirectUrl: String,
    val RequestResources: List<RequestResource>,
)

data class DelegationRequest(
    val Guid: String,
    val RequestStatus: String,
    val CoveredBy: String,
    val OfferedBy: String,
    val RedirectUrl: String,
    val Created: String,
    val LastChanged: String,
    val RequestResources: List<RequestResource>,
    val _links: List<Links>
)

data class RequestResource(
    val ServiceCode: String,
    val ServiceEditionCode: Int,
    val Operations: List<String>
)

data class Links(
    val Rel: String,
    val Href: String
)
