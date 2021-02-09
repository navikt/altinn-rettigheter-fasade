package no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn.mock

import no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene.Service

val personalService = Service(code = "11", editionCode = "1")
val regnskapService = Service(code = "22", editionCode = "3")
val inkluderingService = Service(code = "33", editionCode = "2")

val alleServices = listOf(personalService, regnskapService, inkluderingService)