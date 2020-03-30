package pontointeligente.repository.repository.dynamo

import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.annotation.Rollback
import pontointeligente.infrastructure.exception.NotFoundException
import pontointeligente.repository.AbstractRepository
import pontointeligente.repository.implemention.dynamo.EmployeeRepositoryImplementationDynamo
import java.util.*

class EmployeeRepositoryDynamoTest : AbstractRepository() {

    @BeforeEach
    fun init() {
        employeeRepository = EmployeeRepositoryImplementationDynamo(dynamoDBMapper = dynamoDBMapper)
        super.start()
    }

//    @Test
//    fun findById() {
//        Assertions.assertNotNull(employeeRepository.findById(employee.id))
//    }

    @Test
    @Rollback
    fun save() {
        Assertions.assertNotNull(employeeRepository.save(employee))
    }

//    @Test
//    fun deleteById() {
//        whenever(employeeRepository.findById(employee.id)).thenReturn(Optional.of(employee))
//        employeeRepository.deleteById(employee.id)
//    }

//    @Test
//    fun doNotDeleteByIdWhenIdDoesNotExists() {
//        Assertions.assertThrows(NotFoundException::class.java) { employeeRepository.deleteById(employee.id) }
//    }
}