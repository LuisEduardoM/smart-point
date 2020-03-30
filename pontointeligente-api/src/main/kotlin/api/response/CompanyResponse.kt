package api.response

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import pontointeligente.domain.entity.Address

@ApiModel("Company response")
data class CompanyResponse(
    @ApiModelProperty("Id")
    val id: String
) {

    constructor(
        id: String,
        corporateName: String,
        cnpj: String,
        address: Map<String, Address> = emptyMap()
    ) : this(id) {
        this.corporateName = corporateName
        this.cnpj = cnpj
        this.address = address
    }

    @ApiModelProperty("Corporate name")
    var corporateName: String = ""

    @ApiModelProperty("cnpj")
    var cnpj: String = ""

    @ApiModelProperty("Address response")
    var address: Map<String, Address> = emptyMap()
}