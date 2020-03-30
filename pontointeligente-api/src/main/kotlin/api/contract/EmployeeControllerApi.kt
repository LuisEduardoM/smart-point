package api.contract

import api.request.EmployeeCreateRequest
import api.request.EmployeeUpdateRequest
import api.response.EmployeeResponse
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RequestMapping("/employee")
@Api("Employee connect API")
interface EmployeeControllerApi {

    @ApiOperation("Find all employee")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun findAll(): List<EmployeeResponse>

    @ApiOperation("Find employee by cpf")
    @GetMapping("/{cpf}")
    @ResponseStatus(HttpStatus.OK)
    fun findByCpf(@PathVariable cpf: String): EmployeeResponse

    @ApiOperation("Save new company")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun save(@Valid @RequestBody request: EmployeeCreateRequest): EmployeeResponse

    @ApiOperation("Update company")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun update(@PathVariable id: String, @Valid @RequestBody request: EmployeeUpdateRequest): EmployeeResponse

    @ApiOperation("Delete company by id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String)
}