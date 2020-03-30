package pontointeligente.application.controller

import api.contract.EmployeeControllerApi
import api.request.EmployeeCreateRequest
import api.request.EmployeeUpdateRequest
import api.response.EmployeeResponse
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RestController
import pontointeligente.application.controller.feign.ViaCepClient
import pontointeligente.application.converter.toEntity
import pontointeligente.application.converter.toResponse
import pontointeligente.service.contract.EmployeeService

@RestController
class EmployeeController(val employeeService: EmployeeService, val viaCepClient: ViaCepClient) : EmployeeControllerApi {

    private val log = LoggerFactory.getLogger(EmployeeController::class.java)

    override fun findAll(): List<EmployeeResponse> =
        also { log.info("request to find all employees") }
            .run { employeeService.findAll() }
            .map { it.toResponse() }

    override fun findByCpf(cpf: String): EmployeeResponse =
        also { log.info("request to find employee by cpf") }
            .run { employeeService.findByCpf(cpf) }
            .toResponse()

    override fun save(request: EmployeeCreateRequest): EmployeeResponse =
        also { log.info("request to save a new employee") }
            .run { employeeService.save(request.toEntity(viaCepClient)) }
            .toResponse()

    override fun update(id: String, request: EmployeeUpdateRequest): EmployeeResponse =
        also { log.info("request to update employee") }
            .run { employeeService.update(id, request.toEntity(viaCepClient)) }
            .toResponse()

    override fun delete(id: String) =
        also { log.info("request to delete employee by id ") }
            .run { employeeService.delete(id) }
}