package pontointeligente.application.converter

import pontointeligente.application.controller.request.CompanyCreateRequest
import pontointeligente.application.controller.request.CompanyRequest
import pontointeligente.application.controller.request.CompanyUpdateRequest
import pontointeligente.application.controller.response.CompanyResponse
import pontointeligente.domain.entity.Company
import java.util.*

fun CompanyCreateRequest.toEntity(): Company =
    Company(
        id = UUID.randomUUID().toString(),
        corporationName = this.corporateName,
        cnpj = this.cnpj
    )

fun CompanyUpdateRequest.toEntity(): Company =
    Company(
        id = this.id,
        corporationName = this.corporateName,
        cnpj = this.cnpj
    )

fun Company.toResponse(): CompanyResponse =
    CompanyResponse(
        id = this.id,
        corporateName = this.corporationName,
        cnpj = this.cnpj
    )

fun CompanyResponse.toRequest(): CompanyRequest =
    CompanyRequest(
        id = this.id
    )
