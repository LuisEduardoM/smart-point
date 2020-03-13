package pontointeligente.service.implementation

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import pontointeligente.common.kafka.scheculer.RetrySendTopic
import pontointeligente.domain.entity.Company
import pontointeligente.infrastructure.exception.BusinessRuleException
import pontointeligente.infrastructure.exception.GenerateMessageForException
import pontointeligente.infrastructure.exception.MessagesValidationsErrors
import pontointeligente.infrastructure.exception.NotFoundException
import pontointeligente.repository.contract.CompanyRepository
import pontointeligente.service.contract.CompanyService
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeoutException

@Service
open class CompanyServiceImplementation(
    @Qualifier("companyRepositoryDynamo")
    private val companyRepository: CompanyRepository,
    private val retrySendTopic: RetrySendTopic
) : CompanyService {

    @Value("\${kafka.smart.point.save.company.topic}")
    private lateinit var saveCompanyTopic: String

    @Value("\${kafka.smart.point.update.company.topic}")
    private lateinit var updateCompanyTopic: String

    override fun findAll(): List<Company> = companyRepository.findAll()

    override fun findById(id: String): Optional<Company> = companyRepository.findById(id)

    override fun findByCnpj(cnpj: String): Company {
        return companyRepository.findByCnpj(cnpj) ?: throw NotFoundException(
            "Cnpj company $cnpj not found"
        )
    }

    override fun save(company: Company): Company {
        checkCompanyAlreadyRegisteredByCnpj(company)
        val companySaved = companyRepository.save(company)
        producerTopicKafka(saveCompanyTopic, companySaved.cnpj, companySaved)
        return companySaved
    }

    override fun update(id: String, company: Company): Company {
        checkCompanyExists(id)
        checkCompanyAlreadyRegisteredByCnpj(company)
        val companySaved = companyRepository.save(company.copy(id = id))
        producerTopicKafka(updateCompanyTopic, companySaved.cnpj, companySaved)
        return companySaved
    }

    override fun delete(id: String) {
        companyRepository.deleteById(id)
    }

    fun checkCompanyExists(id: String): Company {
        val company: Optional<Company> = findById(id)
        if (company.isEmpty) {
            throw BusinessRuleException(
                GenerateMessageForException(
                    MessagesValidationsErrors.COMPANY_DOES_NOT_EXISTS_ERROR,
                    arrayOf(id)
                )
            )
        }
        return company.get()
    }

    private fun checkCompanyAlreadyRegisteredByCnpj(company: Company) {
        val companyFound = companyRepository.findByCnpj(company.cnpj)
        if (companyFound != null && company.id != companyFound.id) {
            throw BusinessRuleException(
                GenerateMessageForException(
                    MessagesValidationsErrors.COMPANY_ALREADY_EXISTS_ERROR,
                    arrayOf(companyFound.cnpj, companyFound.id)
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