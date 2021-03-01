package no.nav.arbeidsgiver.altinn_rettigheter_fasade.http_rest_server

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.util.pipeline.*
import no.nav.arbeidsgiver.altinn_rettigheter_fasade.util.logger
import no.nav.security.token.support.core.context.TokenValidationContext
import no.nav.security.token.support.core.jwt.JwtTokenClaims
import no.nav.security.token.support.ktor.TokenValidationContextPrincipal
import java.lang.IllegalStateException

fun lagFnrExtractor(issuers: List<Issuer>): PipelineContext<Unit, ApplicationCall>.() -> String {
    val issuerNames = issuers.map { it.name }

    val log = object{}.logger()

    return {
        val principal = call.authentication.principal
            ?: throw IllegalStateException("principal missing")

        val context = (principal as? TokenValidationContextPrincipal)?.context
            ?: throw IllegalStateException("found principal of type ${principal::class.qualifiedName}")

        val validSubjects = issuerNames.mapNotNull { context.tryGetClaims(it)?.subject }

        when (validSubjects.size) {
            1 -> validSubjects[0]
            0 -> {
                log.error("Caller authenticated, but no subject from allowed issuers.")
                throw IllegalStateException()
            }
            else -> {
                log.info("multiple ({}) valid subjects supplied", validSubjects.size)
                validSubjects[0]
            }
        }
    }
}


fun TokenValidationContext.tryGetClaims(issuer: String): JwtTokenClaims? =
    if (this.hasTokenFor(issuer))
        this.getClaims(issuer)
    else
        null
