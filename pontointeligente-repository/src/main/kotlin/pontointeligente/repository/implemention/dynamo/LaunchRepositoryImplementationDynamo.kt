package pontointeligente.repository.implemention.dynamo

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import org.springframework.stereotype.Repository
import pontointeligente.domain.entity.Launch
import pontointeligente.domain.enums.TypeEnum
import pontointeligente.infrastructure.exception.NotFoundException
import pontointeligente.repository.contract.LaunchRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Repository("launchRepositoryDynamo")
class LaunchRepositoryImplementationDynamo(private val dynamoDBMapper: DynamoDBMapper) :
    LaunchRepository {

    override fun findAll(): List<Launch> {
        val pkBeginWith = "LAUNCH_ID"
        val expression = "begins_with(pk, :pk)"
        val query = DynamoDBScanExpression()
            .withFilterExpression(expression)
            .addExpressionAttributeValuesEntry(":pk", AttributeValue(pkBeginWith))
        return dynamoDBMapper.scan(Launch::class.java, query, null)
    }

    override fun findById(id: String): Optional<Launch> {
        val pk = "LAUNCH_ID-$id"
        val expression = "pk = :pk"

        val query = DynamoDBQueryExpression<Launch>()
            .withKeyConditionExpression(expression)
            .addExpressionAttributeValuesEntry(":pk", AttributeValue(pk))
        val resultQuery = dynamoDBMapper.query(Launch::class.java, query)
        return Optional.of(resultQuery.first())
    }

    override fun findLaunchByEmployee(employeeCpf: String): List<Launch> {
        val expression = "#pk = :cpf and begins_with(#sk, :sk)"
        val query = DynamoDBQueryExpression<Launch>()
            .withIndexName("sk-pk-index")
            .withKeyConditionExpression(expression)
            .addExpressionAttributeNamesEntry("#pk", "sk")
            .addExpressionAttributeNamesEntry("#sk", "pk")
            .addExpressionAttributeValuesEntry(":cpf", AttributeValue("EMPLOYEE_CPF-$employeeCpf"))
            .addExpressionAttributeValuesEntry(":sk", AttributeValue("LAUNCH_ID"))
            .withConsistentRead(false)
        return dynamoDBMapper.query(Launch::class.java, query)
    }

    override fun findLaunchByEmployeeDateAndType(employeeCpf: String, dateLaunch: LocalDate, type: TypeEnum): Launch? {
        val expression = "#pk = :cpf and #sk = :sk"
        val filterExpression = "begins_with(#dateOfLaunch, :dateOfLaunch)"

        val query = DynamoDBQueryExpression<Launch>()
            .withIndexName("sk-type-index")
            .withKeyConditionExpression(expression)
            .withFilterExpression(filterExpression)
            .addExpressionAttributeNamesEntry("#pk", "sk")
            .addExpressionAttributeNamesEntry("#sk", "type")
            .addExpressionAttributeNamesEntry("#dateOfLaunch", "dateOfLaunch")
            .addExpressionAttributeValuesEntry(":cpf", AttributeValue("EMPLOYEE_CPF-$employeeCpf"))
            .addExpressionAttributeValuesEntry(":sk", AttributeValue(type.toString()))
            .addExpressionAttributeValuesEntry(":dateOfLaunch", AttributeValue(dateLaunch.format(DateTimeFormatter.ISO_LOCAL_DATE)))
            .withConsistentRead(false)
        return dynamoDBMapper.query(Launch::class.java, query).firstOrNull()
    }

    override fun save(launch: Launch): Launch {
        dynamoDBMapper.save(launch)
        return launch
    }

    override fun deleteById(id: String) {
        val deleteLaunch = findById(id)
        if (deleteLaunch.isPresent)
            dynamoDBMapper.delete(deleteLaunch.get())
        else
            throw NotFoundException("Id launch $id not found.")
    }
}