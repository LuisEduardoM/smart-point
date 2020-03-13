package pontointeligente.application.controller


import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import pontointeligente.application.AbstractController
import pontointeligente.application.PontointeligenteApplication
import pontointeligente.application.UtilTest
import pontointeligente.application.controller.request.CompanyCreateRequest
import pontointeligente.application.controller.response.CompanyResponse

@SpringBootTest(classes = [PontointeligenteApplication::class])
@AutoConfigureMockMvc
class CompanyControllerTest() : AbstractController() {

    @Test
    fun save() {
        val companyCreate: CompanyCreateRequest = dummyCompanyRequest()
        val companySaved: CompanyResponse
        val jsonResponse = mvc.perform(
            post(BASE_PATH_COMPANY).contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding(
                "UTF-8"
            ).content(
                UtilTest().objectMapper.writeValueAsBytes(companyCreate)
            )
        )
            .andExpect(status().isCreated)
            .andReturn().response.contentAsString
        companySaved = UtilTest()
            .jsonFromObject(jsonResponse, CompanyResponse::class.java)
        assertNotNull(companySaved.id)
        assertEquals(companyCreate.corporateName, companySaved.corporateName)
        assertEquals(companyCreate.cnpj, companySaved.cnpj)
    }

    @Test
    fun update() {
        val companySaved: CompanyResponse
        var companyUpdate = saveCompany()
        companyUpdate.corporateName = "Teste"
        val url = "$BASE_PATH_COMPANY/${companyUpdate.id}"
        val jsonResponse = mvc.perform(
            put(url).contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8").content(
                UtilTest().objectMapper.writeValueAsBytes(companyUpdate)
            )
        )
            .andExpect(status().isOk)
            .andReturn().response.contentAsString
        companySaved = UtilTest()
            .jsonFromObject(jsonResponse, CompanyResponse::class.java)
        assertEquals(companyUpdate.id, companySaved.id)
        assertEquals(companyUpdate.corporateName, companySaved.corporateName)
        assertEquals(companyUpdate.cnpj, companySaved.cnpj)
    }

    @Test
    fun findByCnpj() {
        val companySaved = saveCompany()
        val companyFound: CompanyResponse
        val url = "$BASE_PATH_COMPANY/${companySaved.cnpj}"
        val jsonResponse =
            mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8"))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString
        companyFound = UtilTest()
            .jsonFromObject(jsonResponse, CompanyResponse::class.java)
        assertEquals(companySaved, companyFound)
        assertEquals(companySaved.id, companyFound.id)
        assertEquals(companySaved.cnpj, companyFound.cnpj)
        assertEquals(companySaved.corporateName, companyFound.corporateName)
    }

    @Test
    fun findAll() {
        saveCompany()
        mvc.perform(get(BASE_PATH_COMPANY).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isNotEmpty)
    }

    @Test
    fun delete() {
        val company = saveCompany()
        var uri = "$BASE_PATH_COMPANY/${company.id}"
        mvc.perform(delete(uri).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNoContent)
    }
}