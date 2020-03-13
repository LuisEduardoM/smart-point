package pontointeligente.repository.repository

import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.annotation.Rollback
import pontointeligente.repository.AbstractRepository

class EmployeeRepositoryTest : AbstractRepository() {

    @BeforeEach
    fun init() {
        super.start()
    }

    @Test
    @Rollback
    fun save() {
        whenever(employeeRepository.save(employee)).thenReturn(employee)
        val employeeSaved = employeeRepository.save(employee)
        assertNotNull(employeeSaved)
        assertNotNull(employeeSaved.id)
    }

    @Test
    @Rollback
    fun findByCpf() {
        whenever(employeeRepository.findByCpf(employee.cpf)).thenReturn(employee)
        val employeeFound = employeeRepository.findByCpf(employee.cpf)
        assertNotNull(employeeFound)
    }
}