package api.contract

import api.request.CompanyCreateRequest
import api.request.CompanyUpdateRequest
import api.response.CompanyResponse
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import pontointeligente.domain.entity.Company
import javax.validation.Valid

@RequestMapping("/company")
@Api("Company connector API")
interface CompanyControllerApi {

    @ApiOperation("Find all company")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun findAll(): List<CompanyResponse>

    @Cacheable(cacheNames = ["Company"], key = "#cnpj")
    @ApiOperation("Find company by cnpj")
    @GetMapping("/{cnpj}")
    @ResponseStatus(HttpStatus.OK)
    fun findByCnpj(@PathVariable cnpj: String): CompanyResponse

    @ApiOperation("Save new company")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun save(@Valid @RequestBody request: CompanyCreateRequest): CompanyResponse

    @ApiOperation("Update company by id")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun update(@PathVariable id: String, @Valid @RequestBody request: CompanyUpdateRequest): CompanyResponse

    @ApiOperation("Delete company by id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String)
}