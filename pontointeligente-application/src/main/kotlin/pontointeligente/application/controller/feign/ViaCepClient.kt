package pontointeligente.application.controller.feign

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import pontointeligente.domain.entity.Address

@FeignClient(name = "viaCep", url = "https://viacep.com.br/ws/")
//@FeignClient(name = "viaCep", url = "https://api.postmon.com.br/v1/cep/")
interface ViaCepClient {

    @GetMapping("{cep}/json")
    fun findAddressByCep(@PathVariable("cep") cep: String): Address
}