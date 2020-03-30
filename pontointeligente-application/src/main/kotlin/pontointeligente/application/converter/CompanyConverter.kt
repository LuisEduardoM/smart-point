package pontointeligente.application.converter

import api.request.CompanyCreateRequest
import api.request.CompanyRequest
import api.request.CompanyUpdateRequest
import api.request.EmployeeUpdateRequest
import api.response.CompanyResponse
import api.response.EmployeeResponse
import com.fasterxml.jackson.databind.ObjectMapper
import pontointeligente.application.controller.feign.ViaCepClient
import pontointeligente.domain.entity.Address
import pontointeligente.domain.entity.Company
import java.util.*

fun CompanyCreateRequest.toEntity(viaCepClient: ViaCepClient): Company {
    var addressMap: Map<String, Address> = emptyMap()
    this.address.map { addressMap += mapOf(it.key to viaCepClient.findAddressByCep(it.value)) }
    return Company(
        id = UUID.randomUUID().toString(),
        corporationName = this.corporateName,
        cnpj = this.cnpj,
        address = addressMap

    )
}

fun CompanyUpdateRequest.toEntity(viaCepClient: ViaCepClient): Company {
    var addressMap: Map<String, Address> = emptyMap()
    this.address.map { addressMap += mapOf(it.key to viaCepClient.findAddressByCep(it.value)) }
    return Company(
        id = this.id,
        corporationName = this.corporateName,
        cnpj = this.cnpj,
        address = addressMap
    )
}


fun Company.toResponse(): CompanyResponse {
    var addressResponse: Map<String, Address> = mapOf()
    this.address.map { addressResponse += mapOf(it.key to ObjectMapper().convertValue(it.value, Address::class.java)) }
    return CompanyResponse(
        id = this.id,
        corporateName = this.corporationName,
        cnpj = this.cnpj,
        address = addressResponse
    )
}

fun CompanyResponse.toRequest(): CompanyRequest =
    CompanyRequest(
        id = this.id
    )

fun CompanyResponse.toUpdateRequest(): CompanyUpdateRequest {
    var addressMap: Map<String, String> = emptyMap()
    this.address.map { addressMap += mapOf(it.key to it.value.cep.toString()) }
    return CompanyUpdateRequest(
        id = this.id,
        corporateName = this.corporateName,
        cnpj = this.cnpj,
        address = addressMap
    )
}

