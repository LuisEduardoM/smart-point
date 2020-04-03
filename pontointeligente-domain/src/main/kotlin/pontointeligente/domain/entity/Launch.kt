package pontointeligente.domain.entity

import com.amazonaws.services.dynamodbv2.datamodeling.*
import pontointeligente.domain.enums.TypeEnum
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "launch")
@DynamoDBTable(tableName = "smart_point")
data class Launch(

    @Id
    @field:DynamoDBAttribute(attributeName = "id")
    @get:Column(name = "id")
    var id: String = "",

    @field:DynamoDBAttribute(attributeName = "dateOfLaunch")
    @get:Column(name = "dateOfLaunch")
    var dateOfLaunch: String = "",

    @DynamoDBTypeConvertedEnum
    @field:DynamoDBAttribute(attributeName = "type")
    @Enumerated(EnumType.STRING)
    @get:Column(name = "type")
    var type: TypeEnum = TypeEnum.START_WORK,

    @field:DynamoDBAttribute(attributeName = "location")
    @get:Column(name = "location")
    var location: String = "",

    @field:DynamoDBAttribute(attributeName = "description")
    @get:Column(name = "description")
    var description: String? = "",

    @field:DynamoDBAttribute(attributeName = "employeeCpf")
    @get:Column(name = "employeeCpf")
    var employeeCpf: String = ""
) : Serializable {

    @Transient
    @field:DynamoDBHashKey(attributeName = "pk")
    var pk: String = "LAUNCH_ID-$id"

    @Transient
    @field:DynamoDBRangeKey(attributeName = "sk")
    var sk: String = "EMPLOYEE_CPF-$employeeCpf"
}