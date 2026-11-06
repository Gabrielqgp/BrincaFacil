package br.pucpr.brincafacil.locacao.clientes

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/clientes")
@SecurityRequirement(name = "jwt")
@Validated
class ClienteController(private val service: ClienteService) {
    @GetMapping
    fun list() = service.list()
        .map { ClienteResponse(it) }
        .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    fun get(@PathVariable @Min(1) id: Long) = service.get(id)
        .let { ClienteResponse(it) }
        .let { ResponseEntity.ok(it) }

    @PostMapping
    fun insert(@Valid @RequestBody request: ClienteRequest) = service.insert(request.toCliente())
        .let { ClienteResponse(it) }
        .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }
}
