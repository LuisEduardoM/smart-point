package pontointeligente.service.service

import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.util.ReflectionTestUtils
import pontointeligente.domain.enums.TypeEnum
import pontointeligente.infrastructure.exception.BusinessRuleException
import pontointeligente.service.AbstractService
import pontointeligente.service.contract.LaunchService
import pontointeligente.service.implementation.LaunchServiceImplementation
import java.time.LocalDate
import java.util.*

class LaunchServiceTest : AbstractService() {

    private lateinit var launchService: LaunchService

    @BeforeEach
    fun init() {
        launchService = LaunchServiceImplementation(
            launchRepository,
            employeeServiceImplementation,
            retrySendTopic
        )
        super.start()
        ReflectionTestUtils.setField(launchService, "saveLaunchTopic", "\${kafka.smart.point.save.launch.topic}")
        ReflectionTestUtils.setField(launchService, "updateLaunchTopic", "\${kafka.smart.point.update.launch.topic}")
    }

    @Test
    fun findAll() {
        val launchList = listOf(launch)
        whenever(launchRepository.findAll()).thenReturn(launchList)
        val launchFound = launchService.findAll()
        assertTrue(launchFound.isNotEmpty())
        assertTrue(launchFound.contains(launch))
        assertEquals(launch.id, launchList[0].id)
        assertEquals(launch.dateOfLaunch, launchList[0].dateOfLaunch)
        assertEquals(launch.type, launchList[0].type)
        assertEquals(launch.location, launchList[0].location)
        assertEquals(launch.description, launchList[0].description)
        assertEquals(launch.employeeCpf, launchList[0].employeeCpf)
    }

    @Test
    fun findById() {
        whenever(launchRepository.findById(launch.id)).thenReturn(Optional.of(launch))
        val launchFound = launchService.findById(launch.id)
        assertNotNull(launchFound)
        assertEquals(launch, launchFound.get())
    }

    @Test
    fun findLaunchByEmployee() {
        val launchList = listOf(launch)
        whenever(launchRepository.findLaunchByEmployee(employee.cpf)).thenReturn(launchList)
        val launchesFound = launchService.findLaunchByEmployee(employee.cpf)
        assertTrue(launchesFound.isNotEmpty())
        assertTrue(launchesFound.contains(launch))
        assertEquals(launch.id, launchesFound[0].id)
        assertEquals(launch.dateOfLaunch, launchesFound[0].dateOfLaunch)
        assertEquals(launch.type, launchesFound[0].type)
        assertEquals(launch.location, launchesFound[0].location)
        assertEquals(launch.description, launchesFound[0].description)
        assertEquals(launch.employeeCpf, launchesFound[0].employeeCpf)
    }

    @Test
    fun calculateHoursWorkedByEmployee() {
        whenever(launchRepository.findLaunchByEmployee(employee.cpf)).thenReturn(launchList)
        val hoursWorkedList = launchService.calculateHoursWorkedByEmployee(employee.cpf)
        assertTrue(hoursWorkedList.isNotEmpty())
        hoursWorkedList.forEach {
            if (it.data == "2020-01-28") {
                assertTrue(it.horas == "Hours worked 8:0")
            }
            if (it.data == "2020-01-27") {
                assertTrue(it.horas == "Hours worked 8:43")
            }
        }
    }

    @Test
    fun calculateHoursWorkedByEmployeeWithIncorrectPoint() {
        launchList.remove(launchList.find {
            it.dateOfLaunch.substring(
                0,
                10
            ) == "2020-01-28" && it.type == TypeEnum.START_WORK
        })
        whenever(launchRepository.findLaunchByEmployee(employee.cpf)).thenReturn(launchList)
        val hoursWorkedList = launchService.calculateHoursWorkedByEmployee(employee.cpf)
        assertTrue(hoursWorkedList.isNotEmpty())
        hoursWorkedList.forEach {
            if (it.data == "2020-01-28") {
                assertTrue(it.horas == "Incorrect point registration!")
            }
            if (it.data == "2020-01-27") {
                assertTrue(it.horas == "Hours worked 8:43")
            }
        }
    }

    @Test
    fun save() {
        whenever(employeeServiceImplementation.checkEmployeeExists(launch.employeeCpf)).thenReturn(employee)
        whenever(launchRepository.save(launch)).thenReturn(launch)
        val launchSaved = launchService.save(launch)
        assertNotNull(launchSaved)
        assertEquals(launch.dateOfLaunch, launchSaved.dateOfLaunch)
        assertEquals(launch.type, launchSaved.type)
        assertEquals(launch.location, launchSaved.location)
        assertEquals(launch.description, launchSaved.description)
        assertEquals(launch.employeeCpf, launchSaved.employeeCpf)
    }

    @Test
    fun doNotSaveWhenPointAlreadyExists() {
        val dateOfLaunch = LocalDate.parse(launch.dateOfLaunch.substring(0, 10))
        val launchSave = launch
        whenever(
            launchRepository.findLaunchByEmployeeDateAndType(
                launch.employeeCpf,
                dateOfLaunch,
                launch.type
            )
        ).thenReturn(launchSave)
        assertThrows(BusinessRuleException::class.java) { launchService.save(launch) }
        verify(launchRepository, times(0)).save(launch)
    }

    @Test
    fun update() {
        val launchUpdate = launch
        launchUpdate.copy(type = TypeEnum.START_LUNCH, description = "Almoço")
        whenever(launchRepository.findById(launch.id)).thenReturn(Optional.of(launch))
        whenever(employeeServiceImplementation.checkEmployeeExists(launch.employeeCpf)).thenReturn(employee)
        whenever(launchRepository.save(launchUpdate)).thenReturn(launchUpdate)
        val launchSaved = launchService.update(launchUpdate.id, launchUpdate)
        assertNotNull(launchSaved)
        assertEquals(launchUpdate.id, launchSaved.id)
        assertEquals(launchUpdate.dateOfLaunch, launchSaved.dateOfLaunch)
        assertEquals(launchUpdate.type, launchSaved.type)
        assertEquals(launchUpdate.location, launchSaved.location)
        assertEquals(launchUpdate.description, launchSaved.description)
        assertEquals(launchUpdate.employeeCpf, launchSaved.employeeCpf)
    }

    @Test
    fun doNotUpdateWhenLaunchDoesNotExistsById() {
        val launchUpdate = launch.copy(id = "10")
        assertThrows(BusinessRuleException::class.java) { launchService.update(launchUpdate.id, launchUpdate) }
        verify(launchRepository, times(0)).save(launchUpdate)
    }

    @Test
    fun delete() {
        whenever(launchRepository.findById(launch.id)).thenReturn(Optional.of(launch))
        launchRepository.deleteById(launch.id)
        launchService.delete(launch.id)
    }
}