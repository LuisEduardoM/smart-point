package api.request

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotBlank

@ApiModel("Employee request")
data class EmployeeRequest(

    @ApiModelProperty("Cpf")
    @get:NotBlank
    val cpf: String
)