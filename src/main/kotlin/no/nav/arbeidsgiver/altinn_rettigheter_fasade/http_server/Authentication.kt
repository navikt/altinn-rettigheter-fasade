package no.nav.arbeidsgiver.altinn_rettigheter_fasade.http_server

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.config.*
import io.ktor.util.pipeline.*
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.util.getLogger
import no.nav.security.token.support.ktor.TokenValidationContextPrincipal
import no.nav.security.token.support.ktor.tokenValidationSupport
import java.lang.IllegalStateException

data class Issuer(
    val name: String,
    val discoveryurl: String,
    val accepted_audience: String,
    val cookiename: String?
)

data class AuthenticationConfig(
    val ktorConfig: io.ktor.auth.Authentication.Configuration.() -> Unit,
    val fnrExtractor: PipelineContext<*, ApplicationCall>.() -> String
)

private class Authentication private constructor()
private val log = getLogger(Authentication::class)

fun createAuthenticationConfig(vararg issuers: Issuer): AuthenticationConfig {
    val issuerNames = issuers.map { it.name }

    return AuthenticationConfig(

        ktorConfig = {
            val config = mutableListOf<Pair<String, String>>()

            issuers.flatMapIndexedTo(config) { i, issuer ->
                listOf(
                    "no.nav.security.jwt.issuers.$i.issuer_name" to issuer.name,
                    "no.nav.security.jwt.issuers.$i.discoveryurl" to issuer.discoveryurl,
                    "no.nav.security.jwt.issuers.$i.accepted_audience" to issuer.accepted_audience,
                )
            }

            issuers.mapIndexedNotNullTo(config) { i, issuer ->
                issuer.cookiename?.let {
                    "no.nav.security.jwt.issuers.$i.cookie_name" to it
                }
            }

            config.add("no.nav.security.jwt.issuers.size" to issuers.size.toString())

            log.info("token validation configuration: {}", issuers.joinToString("\n"))

            tokenValidationSupport(config = MapApplicationConfig(*config.toTypedArray()))
        },

        fnrExtractor = {
            val principal = call.authentication.principal
            val context = (principal as? TokenValidationContextPrincipal)
                ?.context
                ?: if (principal == null)
                    throw IllegalStateException("principal missing")
                else
                    throw IllegalStateException("found principal of type ${principal::class.qualifiedName}")
            val validSubjects = issuerNames.mapNotNull {
                if (context.hasTokenFor(it)) context.getClaims(it).subject else null
            }

            val subject = validSubjects.firstOrNull()
            if (subject == null) {
                log.error("Caller authenticated, but no subject from allowed issuers.")
                throw IllegalStateException()
            }

            if (validSubjects.size >= 2) {
                log.info("multiple(={}) valid subjects supplied", validSubjects.size)
            }

            subject
        }
    )
}



