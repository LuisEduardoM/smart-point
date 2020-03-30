package api.request

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@ApiModel("Company create request")
data class CompanyCreateRequest(

    @ApiModelProperty("Name")
    @get:NotBlank
    @get:Length(min = 5, max = 200)
    val corporateName: String,

    @ApiModelProperty("Cnpj")
    @get:NotBlank
    @get:Length(min = 14, max = 14)
    val cnpj: String,

    @ApiModelProperty("Cep")
    @get:NotBlank
    @get:Length(min = 8, max = 8)
    val corporateCep: String,

    @ApiModelProperty("Address")
    @get:NotNull
    val address: Map<String, String>
)