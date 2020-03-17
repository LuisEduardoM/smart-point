package pontointeligente.domain.entity

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "company")
@DynamoDBTable(tableName = "smart_point")
data class Company(
    @Id
    @field:DynamoDBAttribute(attributeName = "id")
    @get:Column(name = "id")
    var id: String = "",

    @field:DynamoDBAttribute(attributeName = "corporationName")
    @get:Column(name = "corporationName")
    var corporationName: String = "",

    @field:DynamoDBAttribute(attributeName = "cnpj")
    @get:Column(name = "cnpj")
    var cnpj: String = ""
) {

    @Transient
    @field:DynamoDBHashKey(attributeName = "pk")
    var pk: String = "COMPANY_ID-$id"

    @Transient
    @field:DynamoDBRangeKey(attributeName = "sk")
    var sk: String = "COMPANY_ID-$id"
}