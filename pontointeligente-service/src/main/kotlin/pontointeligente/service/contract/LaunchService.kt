package pontointeligente.service.contract

import pontointeligente.domain.entity.CalculationHoursWorked
import pontointeligente.domain.entity.Launch
import java.util.*

interface LaunchService {

    fun findAll(): List<Launch>

    fun findById(id: String): Optional<Launch>

    fun findLaunchByEmployee(cpf: String) : List<Launch>

    fun calculateHoursWorkedByEmployee(cpf: String): List<CalculationHoursWorked>

    fun save(launch: Launch): Launch

    fun update(id: String, launch: Launch): Launch

    fun delete(id: String)
}