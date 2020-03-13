package pontointeligente.application.controller

import org.junit.jupiter.api.Assertions.*
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
import pontointeligente.application.controller.request.LaunchCreateRequest
import pontointeligente.application.controller.response.LaunchResponse
import pontointeligente.application.converter.toEntity
import pontointeligente.application.converter.toRequest
import pontointeligente.application.converter.toUpdateRequest
import pontointeligente.domain.entity.CalculationHoursWorked
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@SpringBootTest(classes = [PontointeligenteApplication::class])
@AutoConfigureMockMvc
class LaunchControllerTest : AbstractController() {

    @Test
    fun save() {
        val launchCreate: LaunchCreateRequest = dummylaunchRequest()
        val launchSaved: LaunchResponse
        var jsonResponse = mvc.perform(
            post(BASE_PATH_LAUNCH).contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding(
                "UTF-8"
            ).content(
                UtilTest().objectMapper.writeValueAsBytes(launchCreate)
            )
        )
            .andExpect(status().isCreated)
            .andReturn().response.contentAsString
        launchSaved = UtilTest()
            .jsonFromObject(jsonResponse, LaunchResponse::class.java)
        assertNotNull(launchSaved.id)
        assertEquals(
            launchCreate.dateLaunch.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            launchSaved.dateLaunch.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
        assertEquals(launchCreate.description, launchSaved.description)
        assertEquals(launchCreate.location, launchSaved.location)
        assertEquals(launchCreate.type, launchSaved.type)
    }

    @Test
    fun update() {
        var launchUpdate = saveLaunch().toUpdateRequest()
        val launchSalved: LaunchResponse
        val url = "$BASE_PATH_LAUNCH/${launchUpdate.id}"
        launchUpdate = launchUpdate.copy(description = "${launchUpdate.employeeRequest.cpf}")
        val jsonResponse = mvc.perform(
            put(url).contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8").content(
                UtilTest().objectMapper.writeValueAsBytes(launchUpdate)
            )
        )
            .andExpect(status().isOk)
            .andReturn().response.contentAsString

        launchSalved = UtilTest()
            .jsonFromObject(jsonResponse, LaunchResponse::class.java)
        assertEquals(launchUpdate.id, launchSalved.id)
        assertEquals(
            launchUpdate.dateLaunch.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            launchSalved.dateLaunch.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
        assertEquals(launchUpdate.description, launchSalved.description)
        assertEquals(launchUpdate.location, launchSalved.location)
        assertEquals(launchUpdate.type, launchSalved.type)
    }

    @Test
    fun findAll() {
        saveLaunch()
        mvc.perform(get(BASE_PATH_LAUNCH).contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isNotEmpty)
    }

    @Test
    fun findLaunchByEmployee() {
        val employee = saveEmployee()
        val launchSaved = saveLaunch(dummylaunchRequest(employeeRequest = employee.toRequest()))
        val uri = "$BASE_PATH_LAUNCH/findLaunchByEmployee/${employee.cpf}"
        val jsonResponse =
            mvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$").isNotEmpty)
                .andReturn().response.contentAsString
        val launchesFound = UtilTest().jsonFromListObject(jsonResponse, LaunchResponse::class.java)
        assertTrue(launchesFound.isNotEmpty())
        assertEquals(launchSaved.id, launchesFound[0].id)
        assertEquals(launchSaved.dateLaunch, launchesFound[0].dateLaunch)
        assertEquals(launchSaved.description, launchesFound[0].description)
        assertEquals(launchSaved.location, launchesFound[0].location)
        assertEquals(launchSaved.type, launchesFound[0].type)
    }

    @Test
    fun delete() {
        val launchSaved = saveLaunch()
        val uri = "$BASE_PATH_LAUNCH/${launchSaved.id}"
        mvc.perform(
            delete(uri).contentType(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding(
                "UTF-8"
            )
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun calculateHoursWorkedByEmployee() {
        val employee = saveEmployee()
        val launchList = dummyLaunchForCalculateHoursWorked(employee.toRequest())
        launchList.map { saveLaunch(it).toEntity() }

        val uri = "$BASE_PATH_LAUNCH/calculateHoursByEmployee/${employee.cpf}"
        val jsonResponse = mvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isNotEmpty)
            .andReturn().response.contentAsString
        val calculateHoursList = UtilTest()
            .jsonFromListObject(jsonResponse, CalculationHoursWorked::class.java)
        assertTrue(calculateHoursList.size == 2)

        calculateHoursList.forEach { it ->
            assertTrue(
                launchList.groupBy(
                    keySelector = { it.dateLaunch.substring(0, 10) },
                    valueTransform = { it }).keys.contains(it.data)
            )
            if (it.data == "2020-01-28") {
                assertTrue(it.horas == "Hours worked 8:0")
            }
            if (it.data == "2020-01-27") {
                assertTrue(it.horas == "Hours worked 8:43")
            }
        }
    }
}