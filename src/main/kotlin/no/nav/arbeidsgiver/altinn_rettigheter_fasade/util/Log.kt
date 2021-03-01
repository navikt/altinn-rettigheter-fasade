package no.nav.arbeidsgiver.altinn_rettigheter_fasade.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun <reified R : Any> R.logger(): Logger =
    LoggerFactory.getLogger(this.javaClass.enclosingClass ?: this.javaClass)

