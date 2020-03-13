package pontointeligente.application.converter

import pontointeligente.application.controller.request.EmployeeRequest
import pontointeligente.application.controller.request.LaunchCreateRequest
import pontointeligente.application.controller.request.LaunchUpdateRequest
import pontointeligente.application.controller.response.EmployeeResponse
import pontointeligente.application.controller.response.LaunchResponse
import pontointeligente.domain.entity.Launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


fun LaunchCreateRequest.toEntity(): Launch =
    Launch(
        id = UUID.randomUUID().toString(),
        dateOfLaunch = this.dateLaunch.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        description = this.description,
        location = this.location,
        type = this.type,
        employeeCpf = this.employeeRequest.cpf
    )

fun LaunchUpdateRequest.toEntity(): Launch =
    Launch(
        id = this.id,
        dateOfLaunch = this.dateLaunch.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        description = this.description,
        location = this.location,
        type = this.type,
        employeeCpf = this.employeeRequest.cpf
    )

fun Launch.toResponse(): LaunchResponse =
    LaunchResponse(
        id = this.id,
        dateLaunch = this.dateOfLaunch,
        description = this.description,
        location = this.location,
        type = this.type,
        employeeResponse = EmployeeResponse(
            cpf = this.employeeCpf
        )
    )

fun LaunchResponse.toEntity(): Launch =
    Launch(
        id = this.id,
        dateOfLaunch = this.dateLaunch.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        description = this.description,
        location = this.location,
        type = this.type,
        employeeCpf = this.employeeResponse.cpf
        /*employee = Employee(
            id = this.employeeResponse.id,
            name = this.employeeResponse.name,
            cpf = this.employeeResponse.cpf,
            email = this.employeeResponse.email,
            password = this.employeeResponse.password,
            idCompany = this.employeeResponse.companyResponse.id
        )*/
    )

fun LaunchResponse.toUpdateRequest(): LaunchUpdateRequest =
    LaunchUpdateRequest(
        id = this.id,
        dateLaunch = this.dateLaunch,
        description = this.description,
        location = this.location,
        type = this.type,
        employeeRequest = EmployeeRequest(cpf = this.employeeResponse.cpf)
    )