package pontointeligente.service.implementation

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
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

    private val log = LoggerFactory.getLogger(CompanyServiceImplementation::class.java)

    // @Cacheable(cacheNames = ["Company"], key = "#root.method.name")
    override fun findAll(): List<Company> = companyRepository.findAll()

    @Cacheable(cacheNames = ["Company"], key = "#root.method.name + #id")
    override fun findById(id: String): Optional<Company> = companyRepository.findById(id)

    @Cacheable(cacheNames = ["Company"], key = "#root.method.name + #cnpj")
    override fun findByCnpj(cnpj: String): Company {
        return companyRepository.findByCnpj(cnpj) ?: throw NotFoundException(
            "Cnpj company $cnpj not found"
        )
    }

    @CachePut(cacheNames = ["Company"], key = "#root.method.name + #company.id")
    override fun save(company: Company): Company {
        checkCompanyAlreadyRegisteredByCnpj(company)
        val companySaved = companyRepository.save(company)
        log.debug("Company response [$companySaved] returned from the method save with id [${companySaved.id}]")
        producerTopicKafka(saveCompanyTopic, companySaved.cnpj, companySaved)
        return companySaved
    }

    @CachePut(cacheNames = ["Company"], key = "#root.method.name + #company.id")
    override fun update(id: String, company: Company): Company {
        checkCompanyExists(id)
        checkCompanyAlreadyRegisteredByCnpj(company)
        val companySaved = companyRepository.save(company.copy(id = id))
        log.debug("Company response [$companySaved] returned from the method update with id [${companySaved.id}]")
        producerTopicKafka(updateCompanyTopic, companySaved.cnpj, companySaved)
        return companySaved
    }

    @CacheEvict(cacheNames = ["company"], key = "#root.method.name + #id")
    override fun delete(id: String) {
        companyRepository.deleteById(id)
        log.debug("Company [$id] id deleted")
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