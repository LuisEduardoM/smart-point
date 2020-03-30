package api.contract

import api.request.LaunchCreateRequest
import api.request.LaunchUpdateRequest
import api.response.LaunchResponse
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import pontointeligente.domain.entity.CalculationHoursWorked
import javax.validation.Valid

@RequestMapping("/launch")
@Api("Launch connector API")
interface LaunchControllerApi {

    @ApiOperation("Find all launch")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun findAll(): List<LaunchResponse>

    @ApiOperation("Find all launches by employee")
    @GetMapping("/findLaunchByEmployee/{cpf}")
    @ResponseStatus(HttpStatus.OK)
    fun findLaunchByEmployee(@PathVariable cpf: String): List<LaunchResponse>

    @ApiOperation("Calculate hours worked by employee")
    @GetMapping("/calculateHoursByEmployee/{cpf}")
    @ResponseStatus(HttpStatus.OK)
    fun calculateHoursWorkedByEmployee(@PathVariable cpf: String): List<CalculationHoursWorked>

    @ApiOperation("Save new launch")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun save(@Valid @RequestBody request: LaunchCreateRequest): LaunchResponse

    @ApiOperation("Update launch")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun update(@PathVariable id: String, @Valid @RequestBody request: LaunchUpdateRequest): LaunchResponse

    @ApiOperation("Delete launch")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String)
}