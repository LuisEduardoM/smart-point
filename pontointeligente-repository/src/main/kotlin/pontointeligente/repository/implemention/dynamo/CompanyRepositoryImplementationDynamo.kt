package pontointeligente.repository.implemention.dynamo

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import org.springframework.stereotype.Repository
import pontointeligente.domain.entity.Company
import pontointeligente.infrastructure.exception.NotFoundException
import pontointeligente.repository.contract.CompanyRepository
import java.util.*
import kotlin.collections.ArrayList

@Repository("companyRepositoryDynamo")
class CompanyRepositoryImplementationDynamo(val dynamoDBMapper: DynamoDBMapper) :
    CompanyRepository {

    override fun findAll(): List<Company> {
        val pkBeginWith = "COMPANY_ID"
        val expression = "begins_with(pk, :pk)"
        val query = DynamoDBScanExpression()
            .withFilterExpression(expression)
            .addExpressionAttributeValuesEntry(":pk", AttributeValue(pkBeginWith))
        return dynamoDBMapper.scan(Company::class.java, query, null).toList()
    }

    override fun findById(id: String): Optional<Company> {
        val pk = "COMPANY_ID-$id"
        val sk = "COMPANY_ID-$id"
        return Optional.ofNullable(dynamoDBMapper.load(Company::class.java, pk, sk))
    }

    override fun findByCnpj(cnpj: String): Company? {
        val expression = "#pk = :cnpj"
        val query = DynamoDBQueryExpression<Company>()
            .withIndexName("cnpj-index")
            .withKeyConditionExpression(expression)
            .addExpressionAttributeNamesEntry("#pk", "cnpj")
            .addExpressionAttributeValuesEntry(":cnpj", AttributeValue(cnpj))
            .withConsistentRead(false)
        return dynamoDBMapper.query(Company::class.java, query).firstOrNull()
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