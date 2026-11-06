package br.pucpr.brincafacil

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class BrincaFacilApplication

fun main(args: Array<String>) {
    runApplication<BrincaFacilApplication>(*args)
}
