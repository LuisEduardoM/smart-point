package pontointeligente.repository.contract

import pontointeligente.domain.entity.Launch
import pontointeligente.domain.enums.TypeEnum
import java.time.LocalDate
import java.util.*

interface LaunchRepository {

    fun findAll(): List<Launch>

    fun findById(id: String): Optional<Launch>

    fun findLaunchByEmployee(employeeCpf: String): List<Launch>

    fun findLaunchByEmployeeDateAndType(employeeCpf: String, dateLaunch: LocalDate, type: TypeEnum): Launch?

    fun save(launch: Launch): Launch

    fun deleteById(id: String)
}