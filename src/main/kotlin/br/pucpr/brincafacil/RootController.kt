package br.pucpr.brincafacil

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RootController {
    @GetMapping("/health")
    fun health() = mapOf("status" to "OK")
}