package pontointeligente.service.service

import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.util.ReflectionTestUtils
import pontointeligente.infrastructure.exception.BusinessRuleException
import pontointeligente.infrastructure.exception.NotFoundException
import pontointeligente.service.AbstractService
import pontointeligente.service.contract.EmployeeService
import pontointeligente.service.implementation.EmployeeServiceImplementation
import java.util.*

class EmployeeServiceTest : AbstractService() {

    lateinit var employeeService: EmployeeService

    @BeforeEach
    fun init() {
        employeeService = EmployeeServiceImplementation(
            employeeRepository,
            companyServiceImplementation,
            retrySendTopic
        )
        super.start()
        ReflectionTestUtils.setField(employeeService, "saveEmployeeTopic", "\${kafka.smart.point.save.employee.topic}")
        ReflectionTestUtils.setField(employeeService, "updateEmployeeTopic", "\${kafka.smart.point.update.employee.topic}"
        )
    }

    @Test
    fun findAll() {
        val employeeList = listOf(employee)
        whenever(employeeRepository.findAll()).thenReturn(employeeList)
        val employeesFound = employeeService.findAll();
        assertTrue(employeesFound.isNotEmpty())
        assertTrue(employeesFound.contains(employee))
        assertEquals(employee.id, employeeList[0].id)
        assertEquals(employee.name, employeeList[0].name)
        assertEquals(employee.cpf, employeeList[0].cpf)
        assertEquals(employee.email, employeeList[0].email)
        assertEquals(employee.password, employeeList[0].password)
        assertEquals(employee.idCompany, employeeList[0].idCompany)
    }

    @Test
    fun findByCpf() {
        whenever(employeeRepository.findByCpf(employee.cpf)).thenReturn(employee)
        val employeeFound = employeeService.findByCpf(employee.cpf)
        assertNotNull(employeeFound)
        assertEquals(employee, employeeFound)
        verify(employeeRepository, times(1)).findByCpf(employee.cpf)
    }

    @Test
    fun doNotFindByCpfWhenCpfDoesNotExists() {
        val employeeCpf = "111111111"
        whenever(employeeRepository.findByCpf(employeeCpf)).thenThrow(NotFoundException::class.java)
        assertThrows(NotFoundException::class.java) { employeeService.findByCpf(employee.cpf) }
        verify(employeeRepository, times(0)).findByCpf(employeeCpf)
    }

    @Test
    fun findById() {
        whenever(employeeRepository.findById(employee.id)).thenReturn(Optional.of(employee))
        val employeeFound = employeeService.findById(employee.id)
        assertTrue(employeeFound.isPresent)
        assertEquals(employee, employeeFound.get())
        verify(employeeRepository, times(1)).findById(employee.id)
    }

    @Test
    fun save() {
        whenever(employeeRepository.save(employee)).thenReturn(employee)
        whenever(companyServiceImplementation.checkCompanyExists(employee.idCompany)).thenReturn(company)
        val employeeSaved = employeeService.save(employee)
        assertNotNull(employeeSaved)
        assertEquals(employee.name, employeeSaved.name)
        assertEquals(employee.cpf, employeeSaved.cpf)
        assertEquals(employee.email, employeeSaved.email)
        assertEquals(employee.password, employeeSaved.password)
        assertEquals(employee.idCompany, employeeSaved.idCompany)
        verify(employeeRepository, times(1)).save(employee)
    }

    @Test
    fun update() {
        val employeeUpdate = employee
        employeeUpdate.copy(name = "Luis Eduardo", cpf = "98765432115")
        whenever(employeeRepository.findById(employee.id)).thenReturn(Optional.of(employee))
        whenever(employeeRepository.findByCpf(employee.cpf)).thenReturn(employeeUpdate)
        whenever(employeeRepository.save(employeeUpdate)).thenReturn(employeeUpdate)
        whenever(companyServiceImplementation.checkCompanyExists(employee.idCompany)).thenReturn(company)
        val employeeSaved = employeeService.update(employeeUpdate.id, employeeUpdate)
        assertNotNull(employeeSaved)
        assertEquals(employeeUpdate.id, employeeSaved.id)
        assertEquals(employeeUpdate.name, employeeSaved.name)
        assertEquals(employeeUpdate.cpf, employeeSaved.cpf)
        assertEquals(employeeUpdate.email, employeeSaved.email)
        assertEquals(employeeUpdate.password, employeeSaved.password)
        assertEquals(employeeUpdate.idCompany, employeeSaved.idCompany)
        verify(employeeRepository, times(1)).save(employeeUpdate)
    }

    @Test
    fun doNotSaveOrUpdateWhenCompanyDoesNotExists() {
        val companyId = "10"
        val employeeUpdate = employee.copy(name = "Luis Eduardo", cpf = "98765432115", idCompany = companyId)
        whenever(employeeRepository.findById(employee.id)).thenReturn(Optional.of(employee))

        whenever(companyServiceImplementation.checkCompanyExists(companyId)).thenThrow(BusinessRuleException::class.java)
        assertThrows(BusinessRuleException::class.java) {
            employeeService.update(
                employeeUpdate.id,
                employeeUpdate
            )
            employeeService.save(employeeUpdate)
        }
        verify(employeeRepository, times(0)).save(employeeUpdate)
    }

    @Test
    fun doNotUpdateOrSaveWhenEmployeeAlreadyRegisteredByCpf() {
        val saveEmployee = employee.copy(id = "10")
        whenever(employeeRepository.findByCpf(employee.cpf)).thenReturn(saveEmployee)
        assertThrows(BusinessRuleException::class.java) { employeeService.save(employee) }
        verify(employeeRepository, times(0)).save(employee)
    }

    @Test
    fun doNotUpdateWhenEmployeeDoesNotExists() {
        val employeeId = "10"
        val employeeUpdate = employee.copy(name = "Luis Eduardo", cpf = "98765432115", id = employeeId)

        whenever(employeeServiceImplementation.checkEmployeeExists(employeeId)).thenThrow(BusinessRuleException::class.java)
        assertThrows(BusinessRuleException::class.java) {
            employeeService.update(
                employeeUpdate.id,
                employeeUpdate
            )
        }
        verify(employeeRepository, times(0)).save(employeeUpdate)
    }

    @Test
    fun delete() {
        whenever(employeeRepository.findById(employee.id)).thenReturn(Optional.of(employee))
        employeeService.delete(employee.id)
        verify(employeeRepository, times(1)).deleteById(employee.id)
    }
}