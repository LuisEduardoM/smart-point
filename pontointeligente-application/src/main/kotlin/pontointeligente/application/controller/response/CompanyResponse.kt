package pontointeligente.application.controller.response

data class CompanyResponse(val id: String) {

    constructor(id: String, corporateName: String, cnpj: String) : this(id) {
        this.corporateName = corporateName
        this.cnpj = cnpj
    }

    var corporateName: String = ""
    var cnpj: String = ""
}