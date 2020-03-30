package api.request

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.hibernate.validator.constraints.Length
import pontointeligente.domain.enums.TypeEnum
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@ApiModel("Launch update request")
data class LaunchUpdateRequest(

    @ApiModelProperty("Id")
    @get:NotBlank
    val id: String,

    @ApiModelProperty("Date of launch")
    @get:NotNull
    val dateLaunch: String,

    @ApiModelProperty("Type")
    @get:NotNull
    @Enumerated(EnumType.STRING)
    val type: TypeEnum,

    @ApiModelProperty("Location")
    @get:NotBlank
    @get:Length(min = 5, max = 500)
    val location: String,

    @ApiModelProperty("Description")
    @get:Length(max = 1000)
    val description: String?,

    @ApiModelProperty("Employee request")
    @get:NotNull
    var employeeRequest: EmployeeRequest
)