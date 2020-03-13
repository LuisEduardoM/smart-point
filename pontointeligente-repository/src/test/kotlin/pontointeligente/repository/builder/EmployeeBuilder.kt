package pontointeligente.repository.builder

import pontointeligente.domain.entity.Company
import pontointeligente.domain.entity.Employee

class EmployeeBuilder {

    companion object {
        fun builder(company: Company): Employee =
            Employee(
                id = "1", cpf = "12345678910", password = "123456",
                email = "luis.marques@zup.com.br", name = "Luis", idCompany = company.id //, company = company
            )
    }
}