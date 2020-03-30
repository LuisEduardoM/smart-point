package pontointeligente.application.controller

import api.contract.LaunchControllerApi
import api.request.LaunchCreateRequest
import api.request.LaunchUpdateRequest
import api.response.LaunchResponse
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RestController
import pontointeligente.application.converter.toEntity
import pontointeligente.application.converter.toResponse
import pontointeligente.domain.entity.CalculationHoursWorked
import pontointeligente.service.contract.LaunchService

@RestController
class LaunchController(val launchService: LaunchService) : LaunchControllerApi {

    private val log = LoggerFactory.getLogger(LaunchController::class.java)

    override fun findAll(): List<LaunchResponse> =
        also { log.info("request to find all launches") }
            .run { launchService.findAll() }
            .map { it.toResponse() }

    override fun findLaunchByEmployee(cpf: String): List<LaunchResponse> =
        also { log.info("request to find launch by cpf employee") }
            .run { launchService.findLaunchByEmployee(cpf) }
            .map { it.toResponse() }

    override fun calculateHoursWorkedByEmployee(cpf: String): List<CalculationHoursWorked> =
        also { log.info("request to calculate hours worked by employee / cpf") }
            .run { launchService.calculateHoursWorkedByEmployee(cpf) }

    override fun save(request: LaunchCreateRequest): LaunchResponse =
        also { log.info("request to save a new launch") }
            .run { launchService.save(request.toEntity()) }
            .toResponse()

    override fun update(id: String, request: LaunchUpdateRequest): LaunchResponse =
        also { log.info("request to update launch") }
            .run { launchService.update(id, request.toEntity()) }
            .toResponse()

    override fun delete(id: String) =
        also { log.info("request to delete launch by id") }
            .run { launchService.delete(id) }
}