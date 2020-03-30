package pontointeligente.application.converter

import api.request.CompanyRequest
import api.request.EmployeeCreateRequest
import api.request.EmployeeRequest
import api.request.EmployeeUpdateRequest
import api.response.CompanyResponse
import api.response.EmployeeResponse
import com.fasterxml.jackson.databind.ObjectMapper
import pontointeligente.application.controller.feign.ViaCepClient
import pontointeligente.domain.entity.Address
import pontointeligente.domain.entity.Employee
import java.util.*

fun EmployeeCreateRequest.toEntity(viaCepClient: ViaCepClient): Employee {
    var addressMap: Map<String, Address> = emptyMap()
    this.address.map { addressMap += mapOf(it.key to viaCepClient.findAddressByCep(it.value)) }
    return Employee(
        id = UUID.randomUUID().toString(),
        name = this.name,
        cpf = this.cpf,
        email = this.email,
        password = this.password,
        address = addressMap,
        idCompany = this.companyRequest.id
    )
}

fun EmployeeUpdateRequest.toEntity(viaCepClient: ViaCepClient): Employee {
    var addressMap: Map<String, Address> = emptyMap()
    this.address.map { addressMap += mapOf(it.key to viaCepClient.findAddressByCep(it.value)) }
    return Employee(
        id = this.id,
        name = this.name,
        cpf = this.cpf,
        email = this.email,
        password = this.password,
        address = addressMap,
        idCompany = this.companyRequest.id
    )
}

fun Employee.toResponse(): EmployeeResponse {
    var addressResponse: Map<String, Address> = emptyMap()
    this.address.map { addressResponse += mapOf(it.key to ObjectMapper().convertValue(it.value, Address::class.java)) }
    return EmployeeResponse(
        id = this.id,
        name = this.name,
        cpf = this.cpf,
        email = this.email,
        password = this.password,
        address = addressResponse,
        companyResponse = CompanyResponse(
            id = this.idCompany
        )
    )
}

fun EmployeeResponse.toRequest(): EmployeeRequest =
    EmployeeRequest(
        cpf = this.cpf
    )

fun EmployeeResponse.toUpdateRequest(): EmployeeUpdateRequest {
    var addressMap: Map<String, String> = emptyMap()
    this.address.map { addressMap += mapOf(it.key to it.value.cep.toString()) }
    return EmployeeUpdateRequest(
        id = this.id,
        name = this.name,
        cpf = this.cpf,
        email = this.email,
        password = this.password,
        address = addressMap,
        companyRequest = CompanyRequest(id = this.companyResponse?.id.toString())
    )
}
