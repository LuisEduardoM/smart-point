package pontointeligente.application

import api.request.*
import api.response.CompanyResponse
import api.response.EmployeeResponse
import api.response.LaunchResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import pontointeligente.application.converter.toRequest
import pontointeligente.domain.enums.TypeEnum
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

abstract class AbstractController {

    protected val BASE_PATH_LAUNCH = "/launch"
    protected val BASE_PATH_EMPLOYEE = "/employee"
    protected val BASE_PATH_COMPANY = "/company"

    @Autowired
    lateinit var mvc: MockMvc

    protected fun saveLaunch(launchCreate: LaunchCreateRequest = dummyLaunchRequest()): LaunchResponse {
        var jsonResponse = mvc.perform(
            post(BASE_PATH_LAUNCH).contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding(
                "UTF-8"
            ).content(
                UtilTest().objectMapper.writeValueAsBytes(launchCreate)
            )
        )
            .andExpect(status().isCreated)
            .andReturn().response.contentAsString

        return UtilTest().jsonFromObject(jsonResponse, LaunchResponse::class.java)
    }

    protected fun saveEmployee(employeeCreate: EmployeeCreateRequest = dummyEmployeeRequest()): EmployeeResponse {
        var jsonResponse = mvc.perform(
            post(BASE_PATH_EMPLOYEE).contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding(
                "UTF-8"
            ).content(
                UtilTest().objectMapper.writeValueAsBytes(employeeCreate)
            )
        )
            .andExpect(status().isCreated)
            .andReturn().response.contentAsString

        return UtilTest().jsonFromObject(jsonResponse, EmployeeResponse::class.java)
    }

    protected fun saveCompany(companyCreate: CompanyCreateRequest = dummyCompanyRequest()): CompanyResponse {
        val jsonResponse = mvc.perform(
            post(BASE_PATH_COMPANY).contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding(
                "UTF-8"
            ).content(
                UtilTest().objectMapper.writeValueAsBytes(companyCreate)
            )
        )
            .andExpect(status().isCreated)
            .andReturn().response.contentAsString
        return UtilTest().jsonFromObject(jsonResponse, CompanyResponse::class.java)
    }

    protected fun dummyCompanyRequest(
        corporateName: String = "Razao Social",
        cnpj: String = createCnpjRandom(),
        corporateCep: String = "38408562",
        address: Map<String, String> = mapOf("Matriz" to "38408562")
    ): CompanyCreateRequest =
        CompanyCreateRequest(
            corporateName = corporateName,
            cnpj = cnpj,
            corporateCep = corporateCep,
            address = address
        )

    protected fun dummyEmployeeRequest(
        cpf: String = createCpfRandom(),
        password: String = "123456",
        email: String = "luis.marques@zup.com.br",
        name: String = "Luis Eduardo",
        address: Map<String, String> = mapOf("Home" to "38408563", "Office" to "38408564"),
        companyRequest: CompanyRequest = saveCompany().toRequest()
    ): EmployeeCreateRequest = EmployeeCreateRequest(
        cpf = cpf,
        password = password,
        email = email,
        name = name,
        address = address,
        companyRequest = companyRequest
    )

    protected fun dummyLaunchRequest(
        dateLaunch: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
        type: TypeEnum = TypeEnum.START_WORK,
        location: String = "Uberlandia Center Shoppping",
        description: String = "1 semana",
        employeeRequest: EmployeeRequest = saveEmployee().toRequest()
    ): LaunchCreateRequest = LaunchCreateRequest(
        dateLaunch = dateLaunch.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        type = type,
        location = location,
        description = description,
        employeeRequest = employeeRequest
    )

    protected fun dummyLaunchForCalculateHoursWorked(employeeRequest: EmployeeRequest = saveEmployee().toRequest()): ArrayList<LaunchCreateRequest> {
        var launchList: ArrayList<LaunchCreateRequest> = ArrayList()
        launchList.add(
            dummyLaunchRequest(
                dateLaunch = LocalDateTime.of(2020, 1, 28, 9, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                type = TypeEnum.START_WORK,
                description = "calculation of hours worked",
                employeeRequest = employeeRequest
            )
        )
        launchList.add(
            dummyLaunchRequest(
                dateLaunch = LocalDateTime.of(2020, 1, 28, 12, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                type = TypeEnum.START_LUNCH,
                description = "calculation of hours worked",
                employeeRequest = employeeRequest
            )
        )
        launchList.add(
            dummyLaunchRequest(
                dateLaunch = LocalDateTime.of(2020, 1, 28, 13, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                type = TypeEnum.END_LUNCH,
                description = "calculation of hours worked",
                employeeRequest = employeeRequest
            )
        )
        launchList.add(
            dummyLaunchRequest(
                dateLaunch = LocalDateTime.of(2020, 1, 28, 18, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                type = TypeEnum.END_WORK,
                description = "calculation of hours worked",
                employeeRequest = employeeRequest
            )
        )

        launchList.add(
            dummyLaunchRequest(
                dateLaunch = LocalDateTime.of(2020, 1, 27, 8, 33).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                type = TypeEnum.START_WORK,
                description = "calculation of hours worked",
                employeeRequest = employeeRequest
            )
        )
        launchList.add(
            dummyLaunchRequest(
                dateLaunch = LocalDateTime.of(2020, 1, 27, 11, 31).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                type = TypeEnum.START_LUNCH,
                description = "calculation of hours worked",
                employeeRequest = employeeRequest
            )
        )
        launchList.add(
            dummyLaunchRequest(
                dateLaunch = LocalDateTime.of(2020, 1, 27, 12, 15).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                type = TypeEnum.END_LUNCH,
                description = "calculation of hours worked",
                employeeRequest = employeeRequest
            )
        )
        launchList.add(
            dummyLaunchRequest(
                dateLaunch = LocalDateTime.of(2020, 1, 27, 18, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                type = TypeEnum.END_WORK,
                description = "calculation of hours worked",
                employeeRequest = employeeRequest
            )
        )
        return launchList
    }

    private fun createCnpjRandom(): String {
        var cnpjRandon = ""
        for (i in 0 until 14 step 1) {
            cnpjRandon += createNumberRandom(maxNumber = 10)
        }
        return cnpjRandon
    }

    private fun createCpfRandom(): String {
        var cpfRandon = ""
        for (i in 0 until 11 step 1) {
            cpfRandon += createNumberRandom(maxNumber = 10)
        }
        return cpfRandon
    }

    private fun createNumberRandom(maxNumber: Int): Int {
        return (Math.random() * maxNumber).toInt()
    }
}