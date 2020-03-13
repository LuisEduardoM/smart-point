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
import pontointeligente.domain.entity.Launch
import pontointeligente.domain.enums.TypeEnum
import pontointeligente.infrastructure.exception.NotFoundException
import pontointeligente.repository.contract.LaunchRepository
import pontointeligente.repository.helper.ConvertJsonToObject
import java.time.LocalDate
import java.util.*

@Repository("launchRepositoryDynamo")
class LaunchRepositoryImplementationDynamo(private val dynamoDBMapper: DynamoDBMapper, val dynamoDB: DynamoDB) :
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
        if (resultQuery.isNullOrEmpty()) return Optional.empty()
        return Optional.of(resultQuery.first())
        return Optional.ofNullable(dynamoDBMapper.load(Launch::class.java, pk))
    }

    override fun findLaunchByEmployee(employeeCpf: String): List<Launch> {
        val launchListFound: ArrayList<Launch> = arrayListOf()
        val table = dynamoDB.getTable("smart_point")
        val index = table.getIndex("sk-pk-index")
        val expression = "#pk = :cpf and begins_with(#sk, :sk)"
        val query = QuerySpec()
            .withKeyConditionExpression(expression)
            .withNameMap(
                mapOf(
                    "#pk" to "sk",
                    "#sk" to "pk"
                )
            )
            .withValueMap(
                mapOf(
                    ":cpf" to "EMPLOYEE_CPF-$employeeCpf",
                    ":sk" to "LAUNCH_ID"
                )
            )
        val employeeFound = index.query(query)
        val iterator: IteratorSupport<Item, QueryOutcome> = employeeFound.iterator()
        while (iterator.hasNext()) {
            launchListFound.add(ConvertJsonToObject().jsonFromObject(iterator.next().toJSONPretty(), Launch::class.java))
        }
        return launchListFound
    }

    override fun findLaunchByEmployeeDateAndType(employeeCpf: String, dateLaunch: LocalDate, type: TypeEnum): Launch? {
        val launchListFound: ArrayList<Launch> = arrayListOf()
        val table = dynamoDB.getTable("smart_point")
        val index = table.getIndex("sk-type-index")
        val expression = "#pk = :cpf and #sk = :sk"
        val query = QuerySpec()
            .withKeyConditionExpression(expression)
            .withNameMap(
                mapOf(
                    "#pk" to "sk",
                    "#sk" to "type"
                )
            )
            .withValueMap(
                mapOf(
                    ":cpf" to "EMPLOYEE_CPF-$employeeCpf",
                    ":sk" to type.toString()
                )
            )
        val employeeFound = index.query(query)
        val iterator: IteratorSupport<Item, QueryOutcome> = employeeFound.iterator()
        while (iterator.hasNext()) {
            launchListFound.add(ConvertJsonToObject().jsonFromObject(iterator.next().toJSONPretty(), Launch::class.java))
        }
        return launchListFound.find { LocalDate.parse(it.dateOfLaunch.substring(0, 10)) == dateLaunch }
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