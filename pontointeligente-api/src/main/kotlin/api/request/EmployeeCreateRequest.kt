package api.request

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.hibernate.validator.constraints.Length
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@ApiModel("Employee create request")
data class EmployeeCreateRequest(

    @ApiModelProperty("Name")
    @get:NotBlank
    @get:Length(min = 5, max = 200)
    val name: String,

    @ApiModelProperty("Email")
    @get:NotBlank
    @get:Length(min = 5, max = 200)
    @get:Email
    val email: String,

    @ApiModelProperty("Password")
    @get:NotBlank
    @get:Length(min = 5, max = 15)
    val password: String,

    @ApiModelProperty("Cpf")
    @get:NotBlank
    @get:Length(min = 11, max = 11)
    val cpf: String,

    @ApiModelProperty("Address")
    @get:NotNull
    val address: Map<String, String>,

    @ApiModelProperty("Company request")
    @get:NotNull
    val companyRequest: CompanyRequest
)