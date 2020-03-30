package pontointeligente.repository.repository.dynamo

import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.annotation.Rollback
import pontointeligente.infrastructure.exception.NotFoundException
import pontointeligente.repository.AbstractRepository
import pontointeligente.repository.implemention.dynamo.CompanyRepositoryImplementationDynamo
import java.util.*

class CompanyRepositoryDynamoTest : AbstractRepository() {


    @BeforeEach
    fun init() {
        companyRepository = CompanyRepositoryImplementationDynamo(dynamoDBMapper = dynamoDBMapper)
        super.start()
    }

    @Test
    @Rollback
    fun findById() {
        assertNotNull(companyRepository.findById(company.id))
    }

    @Test
    @Rollback
    fun save() {
        assertNotNull(companyRepository.save(company))
    }

    @Test
    @Rollback
    fun deleteById() {
        whenever(companyRepository.findById(company.id)).thenReturn(Optional.of(company))
        companyRepository.deleteById(company.id)
    }

    @Test
    @Rollback
    fun doNotDeleteByIdWhenIdDoesNotExists() {
        assertThrows(NotFoundException::class.java) { companyRepository.deleteById(company.id) }
    }
}