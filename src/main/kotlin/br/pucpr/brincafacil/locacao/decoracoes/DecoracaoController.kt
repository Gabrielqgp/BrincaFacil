package br.pucpr.brincafacil.locacao.decoracoes

import br.pucpr.brincafacil.locacao.ItemRequest
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/decoracoes")
@Validated
class DecoracaoController(private val service: DecoracaoService) {
    @GetMapping
    fun list(@RequestParam nome: String?, @RequestParam(defaultValue = "ASC") sortDir: String) =
        service.list(nome, sortDir)

    @SecurityRequirement(name = "jwt")
    @PreAuthorize("hasAnyRole('ADMIN', 'PREMIUM')")
    @GetMapping("/{id}")
    fun get(@PathVariable @Min(1) id: Long) = service.get(id)


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "jwt")
    @PreAuthorize("hasAnyRole('ADMIN', 'PREMIUM')")
    fun insert(@Valid @RequestBody request: ItemRequest) = service.insert(request)

    @PutMapping("/{id}")
    @SecurityRequirement(name = "jwt")
    @PreAuthorize("hasAnyRole('ADMIN', 'PREMIUM')")
    fun update(@PathVariable @Min(1) id: Long, @Valid @RequestBody request: ItemRequest) =
        service.update(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "jwt")
    @PreAuthorize("hasAnyRole('ADMIN', 'PREMIUM')")
    fun delete(@PathVariable @Min(1) id: Long) = service.delete(id)
}
