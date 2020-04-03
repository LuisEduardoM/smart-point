package api.response

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import pontointeligente.domain.enums.TypeEnum
import java.io.Serializable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@ApiModel("Launch response")
data class LaunchResponse(

    @ApiModelProperty("Id")
    val id: String,

    @ApiModelProperty("Date of launch")
    val dateLaunch: String,

    @ApiModelProperty("Type")
    @Enumerated(EnumType.STRING)
    val type: TypeEnum,

    @ApiModelProperty("Location")
    val location: String,

    @ApiModelProperty("Description")
    val description: String?,

    @ApiModelProperty("Employee response")
    val employeeResponse: EmployeeResponse
) : Serializable