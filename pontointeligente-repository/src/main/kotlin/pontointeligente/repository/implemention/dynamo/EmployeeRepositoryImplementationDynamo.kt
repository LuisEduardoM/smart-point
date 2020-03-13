package pontointeligente.repository.implemention.dynamo

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.document.QueryOutcome
import com.amazonaws.services.dynamodbv2.document.internal.IteratorSupport
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import org.springframework.stereotype.Repository
import pontointeligente.domain.entity.Employee
import pontointeligente.infrastructure.exception.NotFoundException
import pontointeligente.repository.contract.EmployeeRepository
import pontointeligente.repository.helper.ConvertJsonToObject
import java.util.*

@Repository("employeeRepositoryDynamo")
class EmployeeRepositoryImplementationDynamo(private val dynamoDBMapper: DynamoDBMapper, val dynamoDB: DynamoDB) :
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
        if (resultQuery.isNullOrEmpty()) return Optional.empty()
        return Optional.of(resultQuery.first())
        return Optional.ofNullable(dynamoDBMapper.load(Employee::class.java, pk))
    }

    override fun findByCpf(cpf: String): Employee? {
        val table = dynamoDB.getTable("smart_point")
        val index = table.getIndex("cpf-index")
        val expression = "#pk = :cpf"
        val query = QuerySpec()
            .withKeyConditionExpression(expression)
            .withNameMap(
                mapOf(
                    "#pk" to "cpf"
                )
            )
            .withValueMap(
                mapOf(
                    ":cpf" to cpf
                )
            )
        val employeeFound = index.query(query)
        val iterator: IteratorSupport<Item, QueryOutcome> = employeeFound.iterator()
        if (iterator.hasNext()) {
            return ConvertJsonToObject().jsonFromObject(iterator.next().toJSONPretty(), Employee::class.java)
        }
        return null
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