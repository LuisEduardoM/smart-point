package pontointeligente.repository.repository

import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.annotation.Rollback
import pontointeligente.repository.AbstractRepository

class CompanyRepositoryTest : AbstractRepository() {

    @BeforeEach
    fun init() {
        super.start()
    }

    @Test
    @Rollback
    fun save() {
        whenever(companyRepository.save(company)).thenReturn(company)
        val company = companyRepository.save(company)
        assertNotNull(company)
        assertNotNull(company.id)
    }

    @Test
    @Rollback
    fun findByCnpj() {
        whenever(companyRepository.findByCnpj(company.cnpj)).thenReturn(company)
        val companyFound = companyRepository.findByCnpj(company.cnpj)
        assertNotNull(companyFound)
    }

}