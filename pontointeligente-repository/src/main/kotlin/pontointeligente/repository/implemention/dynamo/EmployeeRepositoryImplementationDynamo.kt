package pontointeligente.repository.implemention.dynamo

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import org.springframework.stereotype.Repository
import pontointeligente.domain.entity.Employee
import pontointeligente.infrastructure.exception.NotFoundException
import pontointeligente.repository.contract.EmployeeRepository
import java.util.*

@Repository("employeeRepositoryDynamo")
class EmployeeRepositoryImplementationDynamo(private val dynamoDBMapper: DynamoDBMapper) :
    EmployeeRepository {

    override fun findAll(): List<Employee> {
        val pkBeginWith = "EMPLOYEE_ID"
        val expression = "begins_with(pk, :pk)"
        val query = DynamoDBScanExpression()
            .withFilterExpression(expression)
            .addExpressionAttributeValuesEntry(":pk", AttributeValue(pkBeginWith))
        return dynamoDBMapper.scan(Employee::class.java, query, null)
    }

    override fun findById(id: String): Optional<Employee> {
        val pk = "EMPLOYEE_ID-$id"
        val expression = "pk = :pk"

        val query = DynamoDBQueryExpression<Employee>()
            .withKeyConditionExpression(expression)
            .addExpressionAttributeValuesEntry(":pk", AttributeValue(pk))
        val resultQuery = dynamoDBMapper.query(Employee::class.java, query)
        return Optional.of(resultQuery.first())
    }

    override fun findByCpf(cpf: String): Employee? {
        val expression = "#pk = :cpf"
        val query = DynamoDBQueryExpression<Employee>()
            .withIndexName("cpf-index")
            .withKeyConditionExpression(expression)
            .addExpressionAttributeNamesEntry("#pk", "cpf")
            .addExpressionAttributeValuesEntry(":cpf", AttributeValue(cpf))
            .withConsistentRead(false)
        return dynamoDBMapper.query(Employee::class.java, query).firstOrNull()
    }

    override fun save(employee: Employee): Employee {
        dynamoDBMapper.save(employee)
        return employee
    }

    override fun deleteById(id: String) {
        val deleteEmployee = findById(id)
        if (deleteEmployee.isPresent)
            dynamoDBMapper.delete(deleteEmployee.get())
        else
            throw NotFoundException("Id employee $id not found.")
    }
}