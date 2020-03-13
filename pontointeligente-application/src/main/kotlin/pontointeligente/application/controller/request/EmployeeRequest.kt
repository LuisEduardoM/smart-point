package pontointeligente.application.controller.request

import javax.validation.constraints.NotBlank

data class EmployeeRequest(
    @get:NotBlank
    val cpf: String
)