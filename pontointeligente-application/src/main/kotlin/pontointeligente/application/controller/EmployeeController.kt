package pontointeligente.application.controller

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import pontointeligente.application.controller.request.EmployeeCreateRequest
import pontointeligente.application.controller.request.EmployeeUpdateRequest
import pontointeligente.application.controller.response.EmployeeResponse
import pontointeligente.application.converter.toEntity
import pontointeligente.application.converter.toResponse
import pontointeligente.service.contract.EmployeeService
import javax.validation.Valid

@RestController
@RequestMapping("/employee")
class EmployeeController(val employeeService: EmployeeService) {

    private val log = LoggerFactory.getLogger(EmployeeController::class.java)

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun findAll(): List<EmployeeResponse> =
        also { log.info("request to find all employees") }
            .run { employeeService.findAll() }
            .map { it.toResponse() }

    @GetMapping("/{cpf}")
    @ResponseStatus(HttpStatus.OK)
    fun findByCpf(@PathVariable cpf: String): EmployeeResponse =
        also { log.info("request to find employee by cpf") }
            .run { employeeService.findByCpf(cpf) }
            .toResponse()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun save(@Valid @RequestBody request: EmployeeCreateRequest): EmployeeResponse =
        also { log.info("request to save a new employee") }
            .run { employeeService.save(request.toEntity()) }
            .toResponse()

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun update(@PathVariable id: String, @Valid @RequestBody request: EmployeeUpdateRequest): EmployeeResponse =
        also { log.info("request to update employee") }
            .run { employeeService.update(id, request.toEntity()) }
            .toResponse()

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String) =
        also { log.info("request to delete employee by id ") }
            .run { employeeService.delete(id) }
}