package pontointeligente.repository.implemention.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pontointeligente.domain.entity.Employee
import pontointeligente.repository.contract.EmployeeRepository

@Repository("employeeRepositoryJpa")
interface EmployeeRepositoryImplementationJpa : JpaRepository<Employee, String>, EmployeeRepository {

    override fun findByCpf(cpf: String): Employee?
}