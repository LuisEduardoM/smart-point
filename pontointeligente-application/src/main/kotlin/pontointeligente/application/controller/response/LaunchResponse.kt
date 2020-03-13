package pontointeligente.application.controller.response

import pontointeligente.domain.enums.TypeEnum
import java.time.LocalDateTime
import javax.persistence.EnumType
import javax.persistence.Enumerated

data class LaunchResponse(
    val id: String,
    val dateLaunch: String,
    @Enumerated(EnumType.STRING)
    val type: TypeEnum,
    val location: String,
    val description: String?,
    val employeeResponse: EmployeeResponse
)