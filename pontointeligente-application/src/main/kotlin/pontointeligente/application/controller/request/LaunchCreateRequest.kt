package pontointeligente.application.controller.request

import org.hibernate.validator.constraints.Length
import pontointeligente.domain.enums.TypeEnum
import java.time.LocalDateTime
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class LaunchCreateRequest(
    @get:NotNull
    val dateLaunch: String,

    @get:NotNull
    @Enumerated(EnumType.STRING)
    val type: TypeEnum,

    @get:NotBlank
    @get:Length(min = 5, max = 500)
    val location: String,

    @get:Length(max = 1000)
    val description: String?,

    @get:NotNull
    var employeeRequest: EmployeeRequest
)