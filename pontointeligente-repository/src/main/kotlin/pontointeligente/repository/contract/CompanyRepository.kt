package pontointeligente.repository.contract

import pontointeligente.domain.entity.Company
import java.util.*

interface CompanyRepository {

    fun findAll(): List<Company>

    fun findById(id: String): Optional<Company>

    fun findByCnpj(cnpj: String): Company?

    fun save(company: Company): Company

    fun deleteById(id: String)
}