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
import pontointeligente.service.contract.CompanyService
import pontointeligente.service.implementation.CompanyServiceImplementation
import java.util.*

class CompanyServiceTest : AbstractService() {

    private lateinit var companyService: CompanyService

    @BeforeEach
    fun init() {
        companyService = CompanyServiceImplementation(companyRepository, retrySendTopic)
        super.start()
        ReflectionTestUtils.setField(companyService, "saveCompanyTopic", "\${kafka.smart.point.save.company.topic}")
        ReflectionTestUtils.setField(companyService, "updateCompanyTopic", "\${kafka.smart.point.update.company.topic}")
    }

    @Test
    fun findAll() {
        var companyList = listOf(company)
        whenever(companyRepository.findAll()).thenReturn(companyList)
        companyList = companyService.findAll();
        assertTrue(companyList.isNotEmpty())
        assertTrue(companyList.contains(company))
        assertEquals(company.id, companyList[0].id)
        assertEquals(company.cnpj, companyList[0].cnpj)
        assertEquals(company.corporationName, companyList[0].corporationName)
    }


    @Test
    fun findById() {
        whenever(companyRepository.findById(company.id)).thenReturn(Optional.of(company))
        val companyFound = companyService.findById(company.id)
        assertTrue(companyFound.isPresent)
        assertEquals(company, companyFound.get())
        verify(companyRepository, times(1)).findById(company.id)
    }

    @Test
    fun findByCnpj() {
        whenever(companyRepository.findByCnpj(company.cnpj)).thenReturn(company)
        val companyFound = companyService.findByCnpj(company.cnpj)
        assertNotNull(companyFound)
        assertEquals(company.cnpj, companyFound.cnpj)
        verify(companyRepository, times(1)).findByCnpj(company.cnpj)
    }

    @Test
    fun doNotFindByCnpjWhenCnpjDoesNotExists(){
        val companyCnpj = "11111111111111"
        whenever(companyRepository.findByCnpj(companyCnpj)).thenThrow(NotFoundException::class.java)
        assertThrows(NotFoundException::class.java) {companyService.findByCnpj(company.cnpj)}
        verify(companyRepository, times(0)).findByCnpj(companyCnpj)
    }

    @Test
    fun save() {
        whenever(companyRepository.save(company)).thenReturn(company)
        val companySaved = companyService.save(company)
        assertNotNull(companySaved)
        assertEquals(company.corporationName, companySaved.corporationName)
        assertEquals(company.cnpj, companySaved.cnpj)
        verify(companyRepository, times(1)).save(company)
    }

    @Test
    fun update() {
        var companyUpdate = company
        companyUpdate.copy(corporationName = "Zup")
        whenever(companyRepository.findById(company.id)).thenReturn(Optional.of(company))
        whenever(companyRepository.findByCnpj(company.cnpj)).thenReturn(companyUpdate)
        whenever(companyRepository.save(companyUpdate)).thenReturn(companyUpdate)
        val companySaved = companyService.update(companyUpdate.id, companyUpdate)
        assertNotNull(companySaved)
        assertEquals(companyUpdate.id, companySaved.id)
        assertEquals(companyUpdate.cnpj, companySaved.cnpj)
        assertEquals(companyUpdate.corporationName, companySaved.corporationName)
        verify(companyRepository, times(1)).save(companyUpdate)
    }

    @Test
    fun doNotUpdateOrSaveWhenCompanyAlreadyRegisteredByCnpj(){
        var saveCompany = company.copy(id = "10")
        whenever(companyRepository.findByCnpj(company.cnpj)).thenReturn(saveCompany)
        assertThrows(BusinessRuleException::class.java){companyService.save(company)}
        verify(companyRepository, times(0)).save(company)
    }

    @Test
    fun doNotUpdateWhenCompanyDoesNotExist() {
        val idCompany = "10"
        var companyUpdate = company.copy(corporationName = "Zup", id = idCompany)
        whenever(companyServiceImplementation.checkCompanyExists(idCompany)).thenThrow(BusinessRuleException::class.java)
        assertThrows(BusinessRuleException::class.java) {
            companyService.update(
                companyUpdate.id,
                companyUpdate
            )
        }
        verify(companyRepository, times(0)).save(companyUpdate)
    }

    @Test
    fun delete() {
        whenever(companyRepository.findById(company.id)).thenReturn(Optional.of(company))
        companyService.delete(company.id)
        verify(companyRepository, times(1)).deleteById(company.id)
    }
}