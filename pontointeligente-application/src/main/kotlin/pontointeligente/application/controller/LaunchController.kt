package pontointeligente.application.controller

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import pontointeligente.application.controller.request.LaunchCreateRequest
import pontointeligente.application.controller.request.LaunchUpdateRequest
import pontointeligente.application.controller.response.LaunchResponse
import pontointeligente.application.converter.toEntity
import pontointeligente.application.converter.toResponse
import pontointeligente.domain.entity.CalculationHoursWorked
import pontointeligente.service.contract.LaunchService
import javax.validation.Valid

@RestController
@RequestMapping("/launch")
class LaunchController(val launchService: LaunchService) {

    private val log = LoggerFactory.getLogger(LaunchController::class.java)

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun findAll(): List<LaunchResponse> =
        also { log.info("request to find all launches") }
            .run { launchService.findAll() }
            .map { it.toResponse() }

    @GetMapping("/findLaunchByEmployee/{cpf}")
    @ResponseStatus(HttpStatus.OK)
    fun findLaunchByEmployee(@PathVariable cpf: String): List<LaunchResponse> =
        also { log.info("request to find launch by cpf employee") }
            .run { launchService.findLaunchByEmployee(cpf) }
            .map { it.toResponse() }

    @GetMapping("/calculateHoursByEmployee/{cpf}")
    @ResponseStatus(HttpStatus.OK)
    fun calculateHoursWorkedByEmployee(@PathVariable cpf: String): List<CalculationHoursWorked> =
        also { log.info("request to calculate hours worked by employee / cpf") }
            .run { launchService.calculateHoursWorkedByEmployee(cpf) }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun save(@Valid @RequestBody request: LaunchCreateRequest): LaunchResponse =
        also { log.info("request to save a new launch") }
            .run { launchService.save(request.toEntity()) }
            .toResponse()

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun update(@PathVariable id: String, @Valid @RequestBody request: LaunchUpdateRequest): LaunchResponse =
        also { log.info("request to update launch") }
            .run { launchService.update(id, request.toEntity()) }
            .toResponse()

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String) =
        also { log.info("request to delete launch by id") }
            .run { launchService.delete(id) }
}