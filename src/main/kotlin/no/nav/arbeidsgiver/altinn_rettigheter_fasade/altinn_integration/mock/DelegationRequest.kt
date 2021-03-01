package no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn_integration.mock

import no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn_integration.AltinnDTO


val alleDelegationRequests = listOf(
    AltinnDTO.DelegationRequest(
        Guid = "123",
        RequestStatus = "Accepted",
        CoveredBy = gunnar.fnr,
        OfferedBy = gunnarsGrønnsakshandelUnderenhet.OrganizationNumber,
        RedirectUrl = "13",
        Created = "321",
        LastChanged = "2",
        RequestResources = listOf(AltinnDTO.RequestResource(inkluderingService.code, inkluderingService.edition)),
        _links = listOf()
    )
)
