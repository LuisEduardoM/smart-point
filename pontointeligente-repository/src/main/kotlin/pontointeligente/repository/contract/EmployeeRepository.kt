package pontointeligente.repository.contract

import org.springframework.stereotype.Repository
import pontointeligente.domain.entity.Employee
import java.util.*

@Repository
interface EmployeeRepository {

    fun findAll(): List<Employee>

    fun findById(id: String): Optional<Employee>

    fun findByCpf(cpf: String): Employee?

    fun save(employee: Employee): Employee

    fun deleteById(id: String)
}