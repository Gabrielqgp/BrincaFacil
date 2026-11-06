package br.pucpr.brincafacil.locacao.reservas

import br.pucpr.brincafacil.locacao.brinquedos.BrinquedoService
import br.pucpr.brincafacil.locacao.clientes.ClienteService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/reservas")
@SecurityRequirement(name = "jwt")
@Validated
class ReservaController(
    private val service: ReservaService,
    private val clienteService: ClienteService,
    private val brinquedoService: BrinquedoService
) {
    @GetMapping
    fun list() = service.findAll()
        .map { ReservaResponse(it) }
        .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    fun getById(@PathVariable @Min(1) id: Long) = service.findByIdOrNull(id)
        ?.let { ReservaResponse(it) }
        ?.let { ResponseEntity.ok(it) }
        ?: ResponseEntity.notFound().build()

    @PostMapping
    fun insert(@Valid @RequestBody request: ReservaRequest) =
        request.toReserva(
            clienteService.get(request.clienteId!!),
            request.brinquedoIds.distinct().map { brinquedoService.get(it) }
        ).let { service.insert(it) }
            .let { ReservaResponse(it) }
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @PutMapping("/{id}/brinquedos/{brinquedoId}")
    fun addBrinquedo(@PathVariable @Min(1) id: Long, @PathVariable @Min(1) brinquedoId: Long) =
        service.addBrinquedo(id, brinquedoService.get(brinquedoId))
            .let { ResponseEntity.noContent().build<Void>() }

    @DeleteMapping("/{id}/brinquedos/{brinquedoId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun removeBrinquedo(@PathVariable @Min(1) id: Long, @PathVariable @Min(1) brinquedoId: Long) =
        service.removeBrinquedo(id, brinquedoId)
            .let { ResponseEntity.noContent().build<Void>() }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun cancelar(@PathVariable @Min(1) id: Long) = service.cancelar(id)
        .let { ResponseEntity.noContent().build<Void>() }
}
