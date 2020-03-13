package pontointeligente.application.converter

import pontointeligente.application.controller.request.CompanyRequest
import pontointeligente.application.controller.request.EmployeeCreateRequest
import pontointeligente.application.controller.request.EmployeeRequest
import pontointeligente.application.controller.request.EmployeeUpdateRequest
import pontointeligente.application.controller.response.CompanyResponse
import pontointeligente.application.controller.response.EmployeeResponse
import pontointeligente.domain.entity.Employee
import java.util.*

fun EmployeeCreateRequest.toEntity(): Employee =
    Employee(
        id = UUID.randomUUID().toString(),
        name = this.name,
        cpf = this.cpf,
        email = this.email,
        password = this.password,
        idCompany = this.companyRequest.id
    )

fun EmployeeUpdateRequest.toEntity(): Employee =
    Employee(
        id = this.id,
        name = this.name,
        cpf = this.cpf,
        email = this.email,
        password = this.password,
        idCompany = this.companyRequest.id
    )

fun Employee.toResponse(): EmployeeResponse =
    EmployeeResponse(
        id = this.id,
        name = this.name,
        cpf = this.cpf,
        email = this.email,
        password = this.password,
        companyResponse = CompanyResponse(
            id = this.idCompany
        )
    )

fun EmployeeResponse.toRequest(): EmployeeRequest =
    EmployeeRequest(
        cpf = this.cpf
    )

fun EmployeeResponse.toUpdateRequest(): EmployeeUpdateRequest =
    EmployeeUpdateRequest(
        id = this.id,
        name = this.name,
        cpf = this.cpf,
        email = this.email,
        password = this.password,
        companyRequest = CompanyRequest(id = this.companyResponse?.id.toString())
    )