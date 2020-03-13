package pontointeligente.application.controller.request

import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class CompanyUpdateRequest(
    @get:NotNull
    val id: String,

    @get:NotBlank
    @get:Length(min = 5, max = 200)
    val corporateName: String,

    @get:NotBlank
    @get:Length(min = 14, max = 14)
    // @get:CNPJ
    val cnpj: String
)