package pontointeligente.application.controller

import api.contract.CompanyControllerApi
import api.request.CompanyCreateRequest
import api.request.CompanyUpdateRequest
import api.response.CompanyResponse
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RestController
import pontointeligente.application.controller.feign.ViaCepClient
import pontointeligente.application.converter.toEntity
import pontointeligente.application.converter.toResponse
import pontointeligente.domain.entity.Company
import pontointeligente.service.contract.CompanyService

@RestController
class CompanyController(val companyService: CompanyService, val viaCepClient: ViaCepClient) : CompanyControllerApi {

    private val log = LoggerFactory.getLogger(CompanyController::class.java)

    override fun findAll(): List<CompanyResponse> =
        also { log.info("request to find all companies") }
            .run { companyService.findAll() }
            .map { it.toResponse() }

    override fun findByCnpj(cnpj: String): CompanyResponse =
        cnpj.also { log.info("request to find company by cnpj") }
            .run { companyService.findByCnpj(cnpj) }
            .toResponse()

    override fun save(request: CompanyCreateRequest): CompanyResponse =
        request.also { log.info("request to create new company") }
            .run { companyService.save(request.toEntity(viaCepClient)) }
            .toResponse()

    override fun update(id: String, request: CompanyUpdateRequest): CompanyResponse =
        request.also { log.info("request to update company") }
            .run { companyService.update(id, request.toEntity(viaCepClient)) }
            .toResponse()

    override fun delete(id: String) =
        also { log.info("request to delete company by id") }
            .run { companyService.delete(id) }
}