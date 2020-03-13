package pontointeligente.application.controller.request

import org.hibernate.validator.constraints.Length
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class EmployeeUpdateRequest(
    @get:NotNull
    val id: String,

    @get:NotBlank
    @get:Length(min = 5, max = 200)
    val name: String,

    @get:NotBlank
    @get:Length(min = 5, max = 200)
    @get:Email
    val email: String,

    @get:NotBlank
    @get:Length(min = 5, max = 15)
    val password: String,

    @get:NotBlank
    @get:Length(min = 11, max = 11)
    val cpf: String,

    @get:NotNull
    val companyRequest: CompanyRequest
)