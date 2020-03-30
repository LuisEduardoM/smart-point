package pontointeligente.repository

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.nhaarman.mockitokotlin2.mock
import pontointeligente.domain.entity.Company
import pontointeligente.domain.entity.Employee
import pontointeligente.domain.entity.Launch
import pontointeligente.repository.builder.CompanyBuilder
import pontointeligente.repository.builder.EmployeeBuilder
import pontointeligente.repository.builder.LaunchBuilder
import pontointeligente.repository.contract.CompanyRepository
import pontointeligente.repository.contract.EmployeeRepository
import pontointeligente.repository.contract.LaunchRepository
import pontointeligente.repository.implemention.dynamo.CompanyRepositoryImplementationDynamo

abstract class AbstractRepository {

    protected var companyRepository: CompanyRepository = mock()
    protected var employeeRepository: EmployeeRepository = mock()
    protected var launchRepository: LaunchRepository = mock()
    protected val dynamoDBMapper: DynamoDBMapper = mock()

    protected val companyRepositoryImplementationDynamo: CompanyRepositoryImplementationDynamo = mock()

    protected lateinit var company: Company
    protected lateinit var employee: Employee
    protected lateinit var launch: Launch

    fun start() {
        company = CompanyBuilder.builder()
        employee = EmployeeBuilder.builder(company)
        launch = LaunchBuilder.builder(employee)
    }
}