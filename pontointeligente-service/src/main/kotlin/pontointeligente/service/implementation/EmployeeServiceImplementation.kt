package pontointeligente.service.implementation

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
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

    private val log = LoggerFactory.getLogger(EmployeeServiceImplementation::class.java)

    // @Cacheable(cacheNames = ["Employee"], key = "#root.method.name")
    override fun findAll(): List<Employee> = employeeRepository.findAll()

    @Cacheable(cacheNames = ["Employee"], key = "#root.method.name + #cpf")
    override fun findByCpf(cpf: String): Employee {
        return employeeRepository.findByCpf(cpf) ?: throw NotFoundException(
            "Cpf employee $cpf not found."
        )
    }

    @Cacheable(cacheNames = ["Employee"], key = "#root.method.name + #id")
    override fun findById(id: String): Optional<Employee> = employeeRepository.findById(id)

    @CachePut(cacheNames = ["Employee"], key = "#root.method.name + #employee.id")
    override fun save(employee: Employee): Employee {
        checkEmployeeAlreadyRegisteredByCpf(employee)
        companyServiceImplementation.checkCompanyExists(employee.idCompany)
        val employeeSaved = employeeRepository.save(employee)
        log.debug("Company response [$employeeSaved] returned from the method save with id [${employeeSaved.cpf}]")
        producerTopicKafka(saveEmployeeTopic, employeeSaved.cpf, employeeSaved)
        return employeeSaved
    }

    @CachePut(cacheNames = ["Employee"], key = "#root.method.name + #id")
    override fun update(id: String, employee: Employee): Employee {
        checkEmployeeExists(id)
        checkEmployeeAlreadyRegisteredByCpf(employee)
        companyServiceImplementation.checkCompanyExists(employee.idCompany)
        val employeeSaved = employeeRepository.save(employee)
        log.debug("Company response [$employeeSaved] returned from the method update with cpf [${employeeSaved.cpf}]")
        producerTopicKafka(updateEmployeeTopic, employeeSaved.cpf, employeeSaved)
        return employeeSaved
    }

    @CacheEvict(cacheNames = ["Employee"], key = "#root.method.name + #id")
    override fun delete(id: String) {
        employeeRepository.deleteById(id)
        log.debug("Company [$id] id deleted")
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
            log.debug("Data [$data] published on [$topic] topic")
        } catch (e: Exception) {
            when (e) {
                is InterruptedException, is ExecutionException, is TimeoutException -> {
                    log.error("error:${e.printStackTrace()} when trying to publish data [$data] on [$topic] topic")
                }
            }
        }
    }
}