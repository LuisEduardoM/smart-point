package pontointeligente.repository.repository.jpa

import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.annotation.Rollback
import pontointeligente.repository.AbstractRepository

class EmployeeRepositoryJpaTest : AbstractRepository() {

    @BeforeEach
    fun init() {
        super.start()
    }

    @Test
    @Rollback
    fun save() {
        whenever(this.employeeRepository.save(employee)).thenReturn(employee)
        val employeeSaved = this.employeeRepository.save(employee)
        assertNotNull(employeeSaved)
        assertNotNull(employeeSaved.id)
    }

    @Test
    @Rollback
    fun findByCpf() {
        whenever(this.employeeRepository.findByCpf(employee.cpf)).thenReturn(employee)
        val employeeFound = this.employeeRepository.findByCpf(employee.cpf)
        assertNotNull(employeeFound)
    }
}