package pontointeligente.service.service

import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.util.ReflectionTestUtils
import pontointeligente.infrastructure.exception.BusinessRuleException
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
        ReflectionTestUtils.setField(employeeService, "saveEmployeeTopic", "PONTO_INTELIGENTE_SAVE_EMPLOYEE")
        ReflectionTestUtils.setField(employeeService, "updateEmployeeTopic", "PONTO_INTELIGENTE_UPDATE_EMPLOYEE")
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
    fun naoSalvarQuandoEmpresaNaoExiste() {
        val idEmpesa = "10"
        //company = company.copy(id = idEmpesa)
        val employee = employee.copy(idCompany = idEmpesa)

        whenever(companyServiceImplementation.checkCompanyExists(idEmpesa)).thenThrow(BusinessRuleException::class.java)
        assertThrows(BusinessRuleException::class.java) { employeeService.save(employee) }
        verify(employeeRepository, times(0)).save(employee)
    }

    @Test
    fun update() {
        val employeeUpdate = employee
        employeeUpdate.copy(name = "Luis Eduardo", cpf = "98765432115")
        whenever(employeeRepository.findById(employee.id)).thenReturn(Optional.of(employee))
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
    fun nãoAtualizarQuandoEmpresaNaoExiste() {
        val idEmpresa = "10"
        val funcionarioAtualizar = employee.copy(name = "Luis Eduardo", cpf = "98765432115", idCompany = idEmpresa)
        whenever(employeeRepository.findById(employee.id)).thenReturn(Optional.of(employee))

        whenever(companyServiceImplementation.checkCompanyExists(idEmpresa)).thenThrow(BusinessRuleException::class.java)
        assertThrows(BusinessRuleException::class.java) {
            employeeService.update(
                funcionarioAtualizar.id,
                funcionarioAtualizar
            )
        }
        verify(employeeRepository, times(0)).save(funcionarioAtualizar)
    }

    @Test
    fun nãoAtualizarQuandoFuncionarioNaoExiste() {
        val idFuncionario = "10"
        val funcionarioAtualizar = employee.copy(name = "Luis Eduardo", cpf = "98765432115", id = idFuncionario)

        whenever(employeeServiceImplementation.checkEmployeeExists(idFuncionario)).thenThrow(BusinessRuleException::class.java)
        assertThrows(BusinessRuleException::class.java) {
            employeeService.update(
                funcionarioAtualizar.id,
                funcionarioAtualizar
            )
        }
        verify(employeeRepository, times(0)).save(funcionarioAtualizar)
    }

    @Test
    fun delete() {
        whenever(employeeRepository.findById(employee.id)).thenReturn(Optional.of(employee))
        employeeService.delete(employee.id)
        verify(employeeRepository, times(1)).deleteById(employee.id)
    }

//    @Test
//    fun naoDeletarQuandoFuncionarioNaoExiste() {
//        val idFuncionario = "10"
//        val funcionario = employee.copy(id = idFuncionario)
//        whenever(employeeServiceImplementation.checkEmployeeExists(idFuncionario)).thenThrow(BusinessRuleException::class.java)
//        assertThrows(BusinessRuleException::class.java) { employeeService.delete(funcionario.id) }
//        verify(employeeRepository, times(0)).deleteById(funcionario.id)
//    }
}