package pontointeligente.repository.builder

import pontointeligente.domain.entity.Company

class CompanyBuilder() {

    companion object {
        fun builder(): Company =
            Company(
                id = "1",
                corporationName = "Razao Social",
                cnpj = "01234567891288"
            )
    }
}