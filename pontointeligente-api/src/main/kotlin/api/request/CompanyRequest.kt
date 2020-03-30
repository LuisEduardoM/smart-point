package api.request

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotBlank

@ApiModel("Company request")
data class CompanyRequest(

    @ApiModelProperty("Id")
    @get:NotBlank
    val id: String
)