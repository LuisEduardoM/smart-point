package pontointeligente.service.builder

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
                employeeCpf = employee.cpf
            )

        fun builderToCalculateHoursWorkedByLaunch(employee: Employee) : ArrayList<Launch> {
            var launchList: ArrayList<Launch> = ArrayList()
            launchList.add(
                Launch(
                    dateOfLaunch = LocalDateTime.of(2020, 1, 28, 9, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    type = TypeEnum.START_WORK,
                    description = "calculation of hours worked",
                    employeeCpf = employee.cpf
                )
            )
            launchList.add(
                Launch(
                    dateOfLaunch = LocalDateTime.of(2020, 1, 28, 12, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    type = TypeEnum.START_LUNCH,
                    description = "calculation of hours worked",
                    employeeCpf = employee.cpf
                )
            )
            launchList.add(
                Launch(
                    dateOfLaunch = LocalDateTime.of(2020, 1, 28, 13, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    type = TypeEnum.END_LUNCH,
                    description = "calculation of hours worked",
                    employeeCpf = employee.cpf
                )
            )
            launchList.add(
                Launch(
                    dateOfLaunch = LocalDateTime.of(2020, 1, 28, 18, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    type = TypeEnum.END_WORK,
                    description = "calculation of hours worked",
                    employeeCpf = employee.cpf
                )
            )

            launchList.add(
                Launch(
                    dateOfLaunch = LocalDateTime.of(2020, 1, 27, 8, 33).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    type = TypeEnum.START_WORK,
                    description = "calculation of hours worked",
                    employeeCpf = employee.cpf
                )
            )
            launchList.add(
                Launch(
                    dateOfLaunch = LocalDateTime.of(2020, 1, 27, 11, 31).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    type = TypeEnum.START_LUNCH,
                    description = "calculation of hours worked",
                    employeeCpf = employee.cpf
                )
            )
            launchList.add(
                Launch(
                    dateOfLaunch = LocalDateTime.of(2020, 1, 27, 12, 15).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    type = TypeEnum.END_LUNCH,
                    description = "calculation of hours worked",
                    employeeCpf = employee.cpf
                )
            )
            launchList.add(
                Launch(
                    dateOfLaunch = LocalDateTime.of(2020, 1, 27, 18, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    type = TypeEnum.END_WORK,
                    description = "calculation of hours worked",
                    employeeCpf = employee.cpf
                )
            )
            return launchList
        }
    }
}