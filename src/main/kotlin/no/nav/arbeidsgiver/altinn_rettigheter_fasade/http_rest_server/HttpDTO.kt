package no.nav.arbeidsgiver.altinn_rettigheter_fasade.http_rest_server

import no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn_integration.AltinnDTO
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene.OrganizationInformation

object HttpDTO {

    fun createGetTilgangerResponseBody(
        result: Collection<OrganizationInformation>
    ): Map<String, GetTilgangerResponseBodyElement> {
        return result.associate {
            it.run {
                it.reportee.OrganizationNumber to
                        GetTilgangerResponseBodyElement(
                            Name = reportee.Name,
                            Type = reportee.Type,
                            Status = reportee.Status,
                            OrganizationNumber = reportee.OrganizationNumber,
                            ParentOrganizationNumber = reportee.ParentOrganizationNumber,
                            OrganizationForm = reportee.OrganizationForm,
                            AccessToService = serviceacceess?.map { Service(it) },
                            AccessRequests = delegationRequests?.map { DelegationRequest(it) }
                        )
            }
        }
    }

    data class GetTilgangerRequestBody(
        val services: List<Service>? = null,
    )

    data class GetTilgangerResponseBodyElement(
        val Name: String,
        val Type: String,
        val Status: String?,
        val OrganizationNumber: String,
        val ParentOrganizationNumber: String? = null,
        val OrganizationForm: String?,
        val AccessToService: List<Service>? = null,
        val AccessRequests: List<DelegationRequest>? = null
    )

    data class Service(
        val code: String,
        val edition: Int
    ) {
        constructor(service: no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene.Service):
                this(service.code, service.edition)

        fun toDomain() = no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene.Service(
            code =  this.code,
            edition = this.edition
        )
    }

    data class PostBeOmTilgangRequestBody(
        val orgnr: String,
        val RedirectUrl: String,
        val RequestResources: List<RequestResource>,
    )

    data class DelegationRequest(
        val Guid: String,
        val RequestStatus: String,
        val OfferedBy: String,
        val RedirectUrl: String,
        val Created: String,
        val LastChanged: String,
        val RequestResources: List<RequestResource>,
        val SubmitRequestWebpageUrl: String
    ) {
        constructor(request: AltinnDTO.DelegationRequest): this(
            Guid = request.Guid,
            RequestStatus = request.RequestStatus,
            OfferedBy = request.OfferedBy,
            RedirectUrl = request.RedirectUrl,
            Created = request.Created,
            LastChanged = request.LastChanged,
            RequestResources = request.RequestResources.map { RequestResource(it) },
            SubmitRequestWebpageUrl = request._links.find { it.Rel == "sendRequest" }!!.Href
        )
    }

    data class RequestResource(
        val ServiceCode: String,
        val ServiceEditionCode: Int,
        val Operations: List<String>?
    ) {
        constructor(resource: AltinnDTO.RequestResource): this(
            resource.ServiceCode,
            resource.ServiceEditionCode,
            resource.Operations
        )
    }
}
