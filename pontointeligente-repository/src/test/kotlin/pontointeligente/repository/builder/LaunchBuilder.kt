package pontointeligente.repository.builder

import pontointeligente.domain.entity.Employee
import pontointeligente.domain.entity.Launch
import pontointeligente.domain.enums.TypeEnum
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LaunchBuilder {

    companion object {
        fun builder(employee: Employee): Launch =
            Launch(
                id = "1",
                dateOfLaunch = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                type = TypeEnum.START_WORK,
                location = "Uberlandia Center Shoppping",
                description = "1 semana",
                employeeCpf = employee.id
                //, employee = employee
            )
    }
}