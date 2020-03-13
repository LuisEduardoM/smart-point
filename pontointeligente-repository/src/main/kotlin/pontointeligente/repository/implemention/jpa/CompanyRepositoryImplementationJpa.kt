package pontointeligente.repository.implemention.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pontointeligente.domain.entity.Company
import pontointeligente.repository.contract.CompanyRepository

@Repository("companyRepositoryJpa")
interface CompanyRepositoryImplementationJpa : JpaRepository<Company, String>, CompanyRepository {

    override fun findByCnpj(cnpj: String): Company?
}