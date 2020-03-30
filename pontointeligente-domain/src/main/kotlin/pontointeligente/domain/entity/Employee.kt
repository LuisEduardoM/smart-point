package pontointeligente.domain.entity

import com.amazonaws.services.dynamodbv2.datamodeling.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "employee")
@DynamoDBTable(tableName = "smart_point")
data class Employee(

    @Id
    @field:DynamoDBAttribute(attributeName = "id")
    @get:Column(name = "id")
    var id: String = "",

    @field:DynamoDBAttribute(attributeName = "name")
    @get:Column(name = "name")
    var name: String = "",

    @field:DynamoDBAttribute(attributeName = "email")
    @get:Column(name = "email")
    var email: String = "",

    @field:DynamoDBAttribute(attributeName = "password")
    @get:Column(name = "password")
    var password: String = "",

    @field:DynamoDBAttribute(attributeName = "cpf")
    @get:Column(name = "cpf")
    var cpf: String = "",

    @Transient
    @DynamoDBTypeConvertedJson
    @field:DynamoDBAttribute(attributeName = "address")
    var address: Map<String, Address> = emptyMap(),

    @field:DynamoDBAttribute(attributeName = "idCompany")
    @get:Column(name = "idCompany")
    var idCompany: String = ""
) {

    @Transient
    @field:DynamoDBHashKey(attributeName = "pk")
    var pk: String = "EMPLOYEE_ID-$id"

    @Transient
    @field:DynamoDBRangeKey(attributeName = "sk")
    var sk: String = "COMPANY_ID-$idCompany"
}