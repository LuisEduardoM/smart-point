package pontointeligente.repository.repository

import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.annotation.Rollback
import pontointeligente.domain.entity.Launch
import pontointeligente.repository.AbstractRepository
import java.time.LocalDate

class LaunchRepositoryTest : AbstractRepository() {

    @BeforeEach
    fun init() {
        super.start()
    }

    @Test
    @Rollback
    fun save() {
        whenever(launchRepository.save(launch)).thenReturn(launch)
        val launchSaved = launchRepository.save(launch)
        assertNotNull(launchSaved)
        assertNotNull(launchSaved.id)
    }

    @Test
    @Rollback
    fun findLaunchByEmployee() {
        val launchList: List<Launch> = listOf(launch)
        whenever(launchRepository.findLaunchByEmployee(launch.employeeCpf)).thenReturn(launchList)
        val launchesFound = launchRepository.findLaunchByEmployee(launch.employeeCpf)
        assertTrue(launchesFound.isNotEmpty())
        assertTrue(launchesFound.contains(launch))
        assertEquals(launch.id, launchesFound[0].id)
        assertEquals(launch.dateOfLaunch, launchesFound[0].dateOfLaunch)
        assertEquals(launch.location, launchesFound[0].location)
        assertEquals(launch.description, launchesFound[0].description)
        assertEquals(launch.employeeCpf, launchesFound[0].employeeCpf)
    }

    @Test
    @Rollback
    fun findLaunchByEmployeeDateAndType() {
        val dateLaunch = LocalDate.parse(launch.dateOfLaunch.substring(0, 10))
        whenever(
            launchRepository.findLaunchByEmployeeDateAndType(
                launch.employeeCpf,
                dateLaunch,
                launch.type
            )
        ).thenReturn(launch)
        val launchFound = launchRepository.findLaunchByEmployeeDateAndType(
            launch.employeeCpf,
            dateLaunch,
            launch.type
        )
        assertNotNull(launchFound)
        assertEquals(launch.id, launchFound?.id)
        assertEquals(launch.dateOfLaunch, launchFound?.dateOfLaunch)
        assertEquals(launch.location, launchFound?.location)
        assertEquals(launch.description, launchFound?.description)
        assertEquals(launch.employeeCpf, launchFound?.employeeCpf)
    }

    @Test
    @Rollback
    fun notFindLaunchByEmployeeDateAndTypeWhenCpfNotExists() {
        val dateLaunch = LocalDate.parse(launch.dateOfLaunch.substring(0, 10))
        val launchToSearch: Launch? = null
        whenever(
            launchRepository.findLaunchByEmployeeDateAndType(
                launch.employeeCpf,
                dateLaunch,
                launch.type
            )
        ).thenReturn(launchToSearch)
        val launchFound = launchRepository.findLaunchByEmployeeDateAndType(
            launch.employeeCpf,
            dateLaunch,
            launch.type
        )
        assertNull(launchFound)
    }
}