package pontointeligente.service.contract

import pontointeligente.domain.entity.Employee
import java.util.*

interface EmployeeService {

    fun findAll(): List<Employee>

    fun findById(id: String): Optional<Employee>

    fun findByCpf(cpf: String): Employee

    fun save(employee: Employee): Employee

    fun update(id: String, employee: Employee): Employee

    fun delete(id: String)
}