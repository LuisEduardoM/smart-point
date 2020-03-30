package api.request

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.hibernate.validator.constraints.Length
import pontointeligente.domain.entity.Address
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@ApiModel("Company update request")
data class CompanyUpdateRequest(

    @ApiModelProperty("Id")
    @get:NotNull
    val id: String,

    @ApiModelProperty("Name")
    @get:NotBlank
    @get:Length(min = 5, max = 200)
    val corporateName: String,

    @ApiModelProperty("Cnpj")
    @get:NotBlank
    @get:Length(min = 14, max = 14)
    val cnpj: String,

    @ApiModelProperty("Address")
    @get:NotNull
    val address: Map<String, String>
)