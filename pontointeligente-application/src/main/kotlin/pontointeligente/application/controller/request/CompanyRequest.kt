package pontointeligente.application.controller.request

import javax.validation.constraints.NotBlank

data class CompanyRequest(
    @get:NotBlank
    val id: String
)