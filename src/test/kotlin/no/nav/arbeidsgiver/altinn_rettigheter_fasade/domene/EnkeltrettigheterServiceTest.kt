package no.nav.arbeidsgiver.altinn_rettigheter_fasade.domene

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

//private val orgnr1 = "1"
//private val orgnr2 = Orgnr("2")
//private val orgnr3 = Orgnr("3")
//private val fnr1 = Fnr("11111111111")
//private val fnr2 = Fnr("22222222222")
//private val fnr3 = Fnr("33333333333")
//private val tjeneste1 = Tjeneste("1", "1")
//private val tjeneste2 = Tjeneste("2", "1")

/*
class EnkeltrettigheterServiceTest : BehaviorSpec({
    given("Oppsett") {

        val organisasjoner = mapOf(
            orgnr1 to Organisasjon(
                name = "Organisasjon nummer en",
                organizationNumber = orgnr1,
                parentOrganizationNumber = null,
                type = "Tja",
                organizationForm = "XYZ",
                status = "ZZZ"
            ),
            orgnr2 to Organisasjon(
                name = "Organisasjon nummer to",
                organizationNumber = orgnr2,
                parentOrganizationNumber = orgnr1,
                type = "hmm",
                organizationForm = "hmm2",
                status = "tja"
            )
        )
        val rettigheter = mapOf(
            fnr1 to mapOf(
                tjeneste1 to setOf(orgnr1, orgnr2),
                tjeneste2 to setOf(orgnr1)
            )
        )
        val altinnClient = AltinnMock(organisasjoner, rettigheter)
        val forespørselStore = ForespørselStoreMock()
        val forespørselService = ForespørselService(forespørselStore, altinnClient)
        val enkeltrettigheterService = EnkeltrettigheterService(altinnClient, forespørselService)

        `when`("henter organisasjoner") {
            val x = enkeltrettigheterService.alleOrganisasjoner(fnr1, emptySet())
            then("har tilgang til to organisasjoner") {
                (x?.size ?: 0) shouldBe 2
            }
        }

        `when`("sjekker rettigheter tjenester") {
            val x = enkeltrettigheterService.alleOrganisasjoner(fnr1, setOf(tjeneste1, tjeneste2))
            then("har tilgang til tjeneste 1 i begge orgs") {
                x.shouldNotBeNull().forAll {
                    it.rettigheter[tjeneste1] shouldBe Rettighet.TILGANG
                }
            }
            then("har tilgang til tjeneste 2 i org1") {
                x.shouldNotBeNull().forOne {
                    it.organizationNumber shouldBe orgnr1
                    it.rettigheter[tjeneste2] shouldBe Rettighet.TILGANG
                }
            }
        }

        `when`("Sjekker tilgang til bedrift") {
            val x = enkeltrettigheterService.alleOrganisasjoner(fnr2, setOf())
            then("Oppslag feilet") {
                x shouldBe null
            }
        }
    }
})
 */