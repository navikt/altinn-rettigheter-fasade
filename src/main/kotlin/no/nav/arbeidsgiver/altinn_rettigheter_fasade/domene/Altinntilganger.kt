package no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene
//
//import kotlinx.coroutines.CompletableDeferred
//import kotlinx.coroutines.Deferred
//import no.nav.arbeidsgiver.altinn_rettigheter_fasade.altinn.Altinn
//import no.nav.arbeidsgiver.altinn_rettigheter_fasade.util.getLog
//
//private val log = getLog("")
//
///* Hent alle organisasjoner man har en tilgang i. */
//fun Altinn.mineOrganisasjoner(): Map<Orgnr, Organisasjon> {
//    return mapOf()
//}
//
///* Slå opp tilgang for hver tjeneste, på tvers av alle organisasjoner. */
//suspend fun Altinn.mineTilganger(tjenester: Tjenester): Map<Pair<Orgnr, Tjeneste>, Rettighet> {
//    val organisasjoner: Deferred<Organisasjonsnummer> = CompletableDeferred<Organisasjonsnummer>().apply {
//        complete(listOf())
//    }
//    val rettigheter: Deferred<Rettigheter> = CompletableDeferred<Rettigheter>().apply {
//        complete(mapOf())
//    }
//    val forespørsler: Deferred<Forespørsler> = CompletableDeferred<Forespørsler>().apply { complete(listOf()) }
//    return beregnTilganger(tjenester, organisasjoner.await(), rettigheter.await(), forespørsler.await())
//}
//
//
///* Slå opp tilgang for hver tjeneste i de gitte organisasjonene. */
//suspend fun Altinn.mineTilganger(organisasjoner: Organisasjonsnummer, tjenester: Tjenester): Map<Pair<Orgnr, Tjeneste>, Rettighet> {
//    val rettigheter: Deferred<Rettigheter> = CompletableDeferred<Rettigheter>().apply {
//        complete(mapOf())
//    }
//    val forespørsler: Deferred<Forespørsler> = CompletableDeferred<Forespørsler>().apply { complete(listOf()) }
//    return beregnTilganger(tjenester, organisasjoner, rettigheter.await(), forespørsler.await())
//}
//
//private fun beregnTilganger(tjenester: Tjenester, organisasjoner: Organisasjonsnummer, rettigheter: Rettigheter, forespørsler: Forespørsler): Map<Pair<Orgnr, Tjeneste>, Rettighet> {
//    val resultat =  mutableMapOf<Pair<Orgnr, Tjeneste>, Rettighet>()
//
//    for (orgnr in organisasjoner) {
//        for (tjeneste in tjenester) {
//            resultat[Pair(orgnr, tjeneste)] = rettigheter[Pair(orgnr, tjeneste)] ?: Rettighet.IKKE_TILGANG
//        }
//    }
//
//    for (forespørsel in forespørsler.sortedBy { it.sistEndret }) {
//        for (tjeneste in forespørsel.tjenester) {
//            val orgnrTjeneste = Pair(forespørsel.orgnr, tjeneste)
//            val potensiellNyRettighet = forespørsel.status.somRettighet
//            val eksiterendeRettighet = resultat[orgnrTjeneste] ?: Rettighet.IKKE_TILGANG
//            if (potensiellNyRettighet.minstLikeRelevantSom(eksiterendeRettighet)) {
//                resultat[orgnrTjeneste] = potensiellNyRettighet
//            }
//        }
//    }
//
//    return resultat
//}
//
//
//fun Altinn.beOmTilgang(orgnr: Orgnr, tjenester: Collection<Tjeneste>, returUrl: String): Forespørsel? {
//    /* hent rettigheter for orgnr/tjeneste-kombinasjonene. */
//    /* har tilgang? return: allerede tilgang */
//
//    /* hent alle forespørsler, filtrert på orgnr */
//    /* hvis relevant, returner den, ellers */
//
//    /* opprett or returner tilgang  */
//
//    return null
//}
