package no.nav.arbeidsgiver.altinn_rettigheter_fasade.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass


fun getLogger(name: String): Logger = LoggerFactory.getLogger(name)!!
fun getLogger(clazz: KClass<*>): Logger = LoggerFactory.getLogger(clazz.java)