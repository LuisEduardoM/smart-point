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
import pontointeligente.application.controller.request.EmployeeCreateRequest
import pontointeligente.application.controller.response.EmployeeResponse
import pontointeligente.application.converter.toUpdateRequest

@SpringBootTest(classes = [PontointeligenteApplication::class])
@AutoConfigureMockMvc
class EmployeeControllerTest : AbstractController() {

    @Test
    fun save() {
        val employeeCreate: EmployeeCreateRequest = dummyEmployeeRequest()
        val employeeSaved: EmployeeResponse
        var jsonResponse = mvc.perform(
            post(BASE_PATH_EMPLOYEE).contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding(
                "UTF-8"
            ).content(
                UtilTest().objectMapper.writeValueAsBytes(employeeCreate)
            )
        )
            .andExpect(status().isCreated)
            .andReturn().response.contentAsString
        employeeSaved = UtilTest()
            .jsonFromObject(jsonResponse, EmployeeResponse::class.java)
        assertNotNull(employeeSaved.cpf)
        assertEquals(employeeCreate.cpf, employeeSaved.cpf)
        assertEquals(employeeCreate.name, employeeSaved.name)
        assertEquals(employeeCreate.email, employeeSaved.email)
        assertEquals(employeeCreate.password, employeeSaved.password)
    }

    @Test
    fun update() {
        var employeeUpdate = saveEmployee().toUpdateRequest()
        val employeeSaved: EmployeeResponse
        val url = "$BASE_PATH_EMPLOYEE/${employeeUpdate.id}"
        employeeUpdate = employeeUpdate.copy(name = "employee ${employeeUpdate.cpf}")
        val jsonResponse = mvc.perform(
            put(url).contentType(MediaType.APPLICATION_JSON_VALUE).content(
                UtilTest().objectMapper.writeValueAsBytes(employeeUpdate)
            )
        )
            .andExpect(status().isOk)
            .andReturn().response.contentAsString
        employeeSaved = UtilTest()
            .jsonFromObject(jsonResponse, EmployeeResponse::class.java)
        assertEquals(employeeUpdate.id, employeeSaved.id)
        assertEquals(employeeUpdate.cpf, employeeSaved.cpf)
        assertEquals(employeeUpdate.name, employeeSaved.name)
        assertEquals(employeeUpdate.email, employeeSaved.email)
        assertEquals(employeeUpdate.password, employeeSaved.password)
    }

    @Test
    fun findAll() {
        saveEmployee()
        mvc.perform(get(BASE_PATH_EMPLOYEE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isNotEmpty)
    }

    @Test
    fun findByCpf() {
        val employeeSaved = saveEmployee()
        val employeeFound: EmployeeResponse
        val uri = "$BASE_PATH_EMPLOYEE/${employeeSaved.cpf}"
        var jsonResponse = mvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk)
            .andReturn().response.contentAsString
        employeeFound = UtilTest()
            .jsonFromObject(jsonResponse, EmployeeResponse::class.java)
        assertEquals(employeeSaved, employeeFound)
        assertEquals(employeeSaved.cpf, employeeFound.cpf)
        assertEquals(employeeSaved.cpf, employeeFound.cpf)
        assertEquals(employeeSaved.name, employeeFound.name)
        assertEquals(employeeSaved.email, employeeFound.email)
        assertEquals(employeeSaved.password, employeeFound.password)
    }

    @Test
    fun delete() {
        val employeeSaved = saveEmployee()
        val uri = "$BASE_PATH_EMPLOYEE/${employeeSaved.id}"
        mvc.perform(delete(uri).contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8"))
            .andExpect(status().isNoContent)
    }
}