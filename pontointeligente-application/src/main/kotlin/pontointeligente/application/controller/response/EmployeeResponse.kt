package pontointeligente.application.controller.response

data class EmployeeResponse(val cpf: String) {

    constructor(
        cpf: String,
        name: String,
        email: String,
        password: String,
        id: String,
        companyResponse: CompanyResponse
    ) : this(cpf) {
        this.name = name
        this.email = email
        this.password = password
        this.id = id
        this.companyResponse = companyResponse
    }

    var name: String = ""
    var email: String = ""
    var password: String = ""
    var id: String = ""
    var companyResponse: CompanyResponse? = null
}