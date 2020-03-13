package pontointeligente.service.contract

import pontointeligente.domain.entity.Company
import java.util.*

interface CompanyService {

    fun findAll(): List<Company>

    fun findById(id: String): Optional<Company>

    fun findByCnpj(cnpj: String): Company

    fun save(company: Company): Company

    fun update(id: String, company: Company): Company

    fun delete(id: String)
}