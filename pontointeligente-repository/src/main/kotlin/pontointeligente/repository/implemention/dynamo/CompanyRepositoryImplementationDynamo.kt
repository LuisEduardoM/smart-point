package pontointeligente.repository.implemention.dynamo

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.document.QueryOutcome
import com.amazonaws.services.dynamodbv2.document.internal.IteratorSupport
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import org.springframework.stereotype.Repository
import pontointeligente.domain.entity.Company
import pontointeligente.infrastructure.exception.NotFoundException
import pontointeligente.repository.contract.CompanyRepository
import pontointeligente.repository.helper.ConvertJsonToObject
import java.util.*

@Repository("companyRepositoryDynamo")
class CompanyRepositoryImplementationDynamo(val dynamoDBMapper: DynamoDBMapper, val dynamoDB: DynamoDB) :
    CompanyRepository {

    override fun findAll(): List<Company> {
        val pkBeginWith = "COMPANY_ID"
        val expression = "begins_with(pk, :pk)"
        val query = DynamoDBScanExpression()
            .withFilterExpression(expression)
            .addExpressionAttributeValuesEntry(":pk", AttributeValue(pkBeginWith))
        return dynamoDBMapper.scan(Company::class.java, query, null)
    }

    override fun findById(id: String): Optional<Company> {
        val pk = "COMPANY_ID-$id"
        val sk = "COMPANY_ID-$id"
        return Optional.ofNullable(dynamoDBMapper.load(Company::class.java, pk, sk))
    }

    override fun findByCnpj(cnpj: String): Company? {
        val table = dynamoDB.getTable("smart_point")
        val index = table.getIndex("cnpj-index")
        val expression = "#pk = :cnpj"
        val query = QuerySpec()
            .withKeyConditionExpression(expression)
            .withNameMap(
                mapOf(
                    "#pk" to "cnpj"
                )
            )
            .withValueMap(
                mapOf(
                    ":cnpj" to cnpj
                )
            )
        val companyFound = index.query(query)
        val iterator: IteratorSupport<Item, QueryOutcome> = companyFound.iterator()
        if (iterator.hasNext()) {
            return ConvertJsonToObject()
                .jsonFromObject(iterator.next().toJSONPretty(), Company::class.java)
        }
        return null
    }

    override fun save(company: Company): Company {
        dynamoDBMapper.save(company)
        return company
    }

    override fun deleteById(id: String) {
        val deleteCompany = findById(id)
        if (deleteCompany.isPresent)
            dynamoDBMapper.delete(deleteCompany.get())
        else
            throw NotFoundException("Id company $id not found.")
    }
}