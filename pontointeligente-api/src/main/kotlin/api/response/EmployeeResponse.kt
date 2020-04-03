package api.response

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import pontointeligente.domain.entity.Address
import java.io.Serializable

@ApiModel("Employee response")
data class EmployeeResponse(
    @ApiModelProperty("Cpf")
    val cpf: String
) : Serializable {

    constructor(
        cpf: String,
        name: String,
        email: String,
        password: String,
        id: String,
        address: Map<String, Address>,
        companyResponse: CompanyResponse
    ) : this(cpf) {
        this.name = name
        this.email = email
        this.password = password
        this.id = id
        this.address = address
        this.companyResponse = companyResponse
    }

    @ApiModelProperty("Name")
    var name: String = ""

    @ApiModelProperty("Email")
    var email: String = ""

    @ApiModelProperty("Password")
    var password: String = ""

    @ApiModelProperty("Id")
    var id: String = ""

    @ApiModelProperty("Address")
    var address: Map<String, Address> = emptyMap()

    @ApiModelProperty("Company response")
    var companyResponse: CompanyResponse? = null
}