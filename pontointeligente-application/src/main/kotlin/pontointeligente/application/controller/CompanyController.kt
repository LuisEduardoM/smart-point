package pontointeligente.application.controller

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import pontointeligente.application.controller.request.CompanyCreateRequest
import pontointeligente.application.controller.request.CompanyUpdateRequest
import pontointeligente.application.controller.response.CompanyResponse
import pontointeligente.application.converter.toEntity
import pontointeligente.application.converter.toResponse
import pontointeligente.service.contract.CompanyService
import javax.validation.Valid

@RestController
@RequestMapping("/company")
class CompanyController(val companyService: CompanyService) {

    private val log = LoggerFactory.getLogger(CompanyController::class.java)

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun findAll(): List<CompanyResponse> =
        also { log.info("request to find all companies") }
            .run { companyService.findAll() }
            .map { it.toResponse() }

    @GetMapping("/{cnpj}")
    @ResponseStatus(HttpStatus.OK)
    fun findByCnpj(@PathVariable cnpj: String): CompanyResponse =
        cnpj.also { log.info("request to find company by cpj") }
            .run { companyService.findByCnpj(cnpj) }
            .toResponse()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun save(@Valid @RequestBody request: CompanyCreateRequest): CompanyResponse =
        request.also { log.info("request to create new company") }
            .run { companyService.save(request.toEntity()) }
            .toResponse()

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun update(@PathVariable id: String, @Valid @RequestBody request: CompanyUpdateRequest): CompanyResponse =
        request.also { log.info("request to update company") }
            .run { companyService.update(id, request.toEntity()) }
            .toResponse()

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String) =
        also { log.info("request to delete company by id") }
            .run { companyService.delete(id) }
}