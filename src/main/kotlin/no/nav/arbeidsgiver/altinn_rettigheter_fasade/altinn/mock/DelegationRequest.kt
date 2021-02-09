package no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn.mock

import no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene.DelegationRequest


val alleDelegationRequests = listOf(
    DelegationRequest(
        Guid = "123",
        RequestStatus = "Accepted",
        CoveredBy = "",
        OfferedBy = "",
        RedirectUrl = "13",
        Created = "321",
        LastChanged = "2",
        RequestResources = listOf(),
        _links = listOf()
    )
)
