package no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn_integration.mock

import no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene.Service

val personalService = Service(code = "11", edition = 1)
val regnskapService = Service(code = "22", edition = 3)
val inkluderingService = Service(code = "33", edition = 2)

val alleServices = listOf(personalService, regnskapService, inkluderingService)