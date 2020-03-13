package pontointeligente.service.implementation

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import pontointeligente.common.kafka.scheculer.RetrySendTopic
import pontointeligente.domain.entity.Employee
import pontointeligente.infrastructure.exception.BusinessRuleException
import pontointeligente.infrastructure.exception.GenerateMessageForException
import pontointeligente.infrastructure.exception.MessagesValidationsErrors
import pontointeligente.infrastructure.exception.NotFoundException
import pontointeligente.repository.contract.EmployeeRepository
import pontointeligente.service.contract.EmployeeService
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeoutException

@Service
class EmployeeServiceImplementation(
    @Qualifier("employeeRepositoryDynamo")
    val employeeRepository: EmployeeRepository,
    val companyServiceImplementation: CompanyServiceImplementation,
    val retrySendTopic: RetrySendTopic
) : EmployeeService {

    @Value("\${kafka.smart.point.save.employee.topic}")
    private lateinit var saveEmployeeTopic: String

    @Value("\${kafka.smart.point.update.employee.topic}")
    private lateinit var updateEmployeeTopic: String

    override fun findAll(): List<Employee> = employeeRepository.findAll()

    override fun findByCpf(cpf: String): Employee {
        return employeeRepository.findByCpf(cpf) ?: throw NotFoundException(
            "Cpf employee $cpf not found."
        )
    }

    override fun findById(id: String): Optional<Employee> = employeeRepository.findById(id)

    override fun save(employee: Employee): Employee {
        checkEmployeeAlreadyRegisteredByCpf(employee)
        companyServiceImplementation.checkCompanyExists(employee.idCompany)
        val employeeSaved = employeeRepository.save(employee)
        producerTopicKafka(saveEmployeeTopic, employeeSaved.cpf, employeeSaved)
        return employeeSaved
    }

    override fun update(id: String, employee: Employee): Employee {
        checkEmployeeExists(id)
        checkEmployeeAlreadyRegisteredByCpf(employee)
        companyServiceImplementation.checkCompanyExists(employee.idCompany)
        val employeeSaved = employeeRepository.save(employee)
        producerTopicKafka(updateEmployeeTopic, employeeSaved.cpf, employeeSaved)
        return employeeSaved
    }

    override fun delete(id: String) {
        employeeRepository.deleteById(id)
    }

    fun checkEmployeeExists(id: String): Employee {
        val employee: Optional<Employee> = findById(id)
        if (employee.isEmpty) {
            throw BusinessRuleException(
                GenerateMessageForException(
                    MessagesValidationsErrors.EMPLOYEE_DOES_NOT_EXISTS_ERROR,
                    arrayOf(id)
                )
            )
        }
        return employee.get()
    }

    private fun checkEmployeeAlreadyRegisteredByCpf(employee: Employee) {
        val employeeFound = employeeRepository.findByCpf(employee.cpf)
        if (employeeFound != null && employee.id != employeeFound.id) {
            throw BusinessRuleException(
                GenerateMessageForException(
                    MessagesValidationsErrors.EMPLOYEE_ALREADY_EXISTS_ERROR,
                    arrayOf(employeeFound.cpf, employeeFound.id)
                )
            )
        }
    }

    private fun producerTopicKafka(topic: String, key: String, data: Any) {
        try {
            retrySendTopic.sendTopicToKafkaTemplate(topic, key, data)
        } catch (e: Exception) {
            when (e) {
                is InterruptedException, is ExecutionException, is TimeoutException -> {
                    println("error:${e.printStackTrace()}")
                }
            }
        }
    }
}