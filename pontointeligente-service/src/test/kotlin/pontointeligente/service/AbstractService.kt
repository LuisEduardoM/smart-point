package pontointeligente.service

import com.nhaarman.mockitokotlin2.mock
import org.springframework.test.context.TestPropertySource
import pontointeligente.common.kafka.scheculer.RetrySendTopic
import pontointeligente.domain.entity.Company
import pontointeligente.domain.entity.Employee
import pontointeligente.domain.entity.Launch
import pontointeligente.repository.contract.CompanyRepository
import pontointeligente.repository.contract.EmployeeRepository
import pontointeligente.repository.contract.LaunchRepository
import pontointeligente.service.builder.CompanyBuilder
import pontointeligente.service.builder.EmployeeBuilder
import pontointeligente.service.builder.LaunchBuilder
import pontointeligente.service.contract.EmployeeService
import pontointeligente.service.implementation.CompanyServiceImplementation
import pontointeligente.service.implementation.EmployeeServiceImplementation

@TestPropertySource(locations=["classpath:application.properties"])
abstract class AbstractService {

    protected val companyRepository: CompanyRepository = mock()

    protected val employeeRepository: EmployeeRepository = mock()

    protected val companyServiceImplementation: CompanyServiceImplementation = mock()

    protected val launchRepository: LaunchRepository = mock()

    protected val employeeServiceMock: EmployeeService = mock()

    protected val employeeServiceImplementation: EmployeeServiceImplementation = mock()

    protected val retrySendTopic: RetrySendTopic = mock()

    protected lateinit var company: Company
    protected lateinit var employee: Employee
    protected lateinit var launch: Launch
    protected lateinit var launchList: ArrayList<Launch>

    fun start() {
        company = CompanyBuilder.builder()
        employee = EmployeeBuilder.builder(company)
        launch = LaunchBuilder.builder(employee)
        launchList = LaunchBuilder.builderToCalculateHoursWorkedByLaunch(employee)
    }
}