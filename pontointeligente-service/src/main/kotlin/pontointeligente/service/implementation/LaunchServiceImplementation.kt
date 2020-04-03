package pontointeligente.service.implementation

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import pontointeligente.common.kafka.scheculer.RetrySendTopic
import pontointeligente.domain.entity.CalculationHoursWorked
import pontointeligente.domain.entity.Launch
import pontointeligente.domain.enums.TypeEnum
import pontointeligente.infrastructure.exception.BusinessRuleException
import pontointeligente.infrastructure.exception.GenerateMessageForException
import pontointeligente.infrastructure.exception.MessagesValidationsErrors.LAUNCH_DOES_NOT_EXISTS_ERROR
import pontointeligente.infrastructure.exception.MessagesValidationsErrors.LAUNCH_POINT_ALREADY_EXISTS_ERROR
import pontointeligente.repository.contract.LaunchRepository
import pontointeligente.service.contract.LaunchService
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeoutException

@Service
class LaunchServiceImplementation(
    @Qualifier("launchRepositoryDynamo")
    val launchRepository: LaunchRepository,
    val employeeServiceImplementation: EmployeeServiceImplementation,
    val retrySendTopic: RetrySendTopic
) : LaunchService {

    @Value("\${kafka.smart.point.save.launch.topic}")
    private lateinit var saveLaunchTopic: String

    @Value("\${kafka.smart.point.update.launch.topic}")
    private lateinit var updateLaunchTopic: String

    @Cacheable(cacheNames = ["Launch"], key = "#root.method.name")
    override fun findAll(): List<Launch> = launchRepository.findAll()

    @Cacheable(cacheNames = ["Launch"], key = "#root.method.name + #id")
    override fun findById(id: String): Optional<Launch> = launchRepository.findById(id)

    @Cacheable(cacheNames = ["Launch"], key = "#root.method.name + #cpf")
    override fun findLaunchByEmployee(cpf: String): List<Launch> =
        launchRepository.findLaunchByEmployee(cpf)

    @Cacheable(cacheNames = ["Launch"], key = "#root.method.name + #cpf")
    override fun calculateHoursWorkedByEmployee(cpf: String): List<CalculationHoursWorked> =
        findLaunchByEmployee(cpf)
            .groupBy(
                keySelector = { it.dateOfLaunch.substring(0,10) },
                valueTransform = { it })
            .map { extractDateToCalculateHours(it.key, it.value) }

    @CachePut(cacheNames = ["Launch"], key = "#root.method.name + #launch.id")
    override fun save(launch: Launch): Launch {
        employeeServiceImplementation.findByCpf(launch.employeeCpf)
        checkPointAlreadyExists(launch)
        var launchSaved = launchRepository.save(launch)
        producerTopicKafka(saveLaunchTopic, launchSaved.id, launchSaved)
        return launchSaved
    }

    @CachePut(cacheNames = ["Launch"], key = "#root.method.name + #launch.id")
    override fun update(id: String, launch: Launch): Launch {
        checkLaunchExists(id)
        employeeServiceImplementation.findByCpf(launch.employeeCpf)
        val launchSaved = launchRepository.save(launch)
        producerTopicKafka(updateLaunchTopic, launchSaved.id, launchSaved)
        return launchSaved
    }

    @CacheEvict(cacheNames = ["Launch"], key = "#root.method.name + #id")
    override fun delete(id: String) {
        launchRepository.deleteById(id)
    }

    private fun checkLaunchExists(id: String) {
        if (findById(id).isEmpty) {
            throw BusinessRuleException(
                GenerateMessageForException(
                    LAUNCH_DOES_NOT_EXISTS_ERROR,
                    arrayOf(id)
                )
            )
        }
    }

    private fun extractDateToCalculateHours(data: String, launchList: List<Launch>): CalculationHoursWorked {
        if (launchList.size == 4) {
            val formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            var startWork = launchList.find { it.type == TypeEnum.START_WORK }
                ?.dateOfLaunch?.let { LocalDateTime.parse(it.replace("T", " "), formatterDate) }
            var startLunch =
                launchList.find { it.type == TypeEnum.START_LUNCH }?.dateOfLaunch?.let {
                    LocalDateTime.parse(
                        it.replace("T", " "),
                        formatterDate
                    )
                }
            var endLunch =
                launchList.find { it.type == TypeEnum.END_LUNCH }?.dateOfLaunch?.let {
                    LocalDateTime.parse(
                        it.replace("T", " "),
                        formatterDate
                    )
                }
            var endWork = launchList.find { it.type == TypeEnum.END_WORK }?.dateOfLaunch?.let {
                LocalDateTime.parse(
                    it.replace("T", " "),
                    formatterDate
                )
            }
            if (startWork == null || startLunch == null || endLunch == null || endWork == null) {
                return CalculationHoursWorked(data, "Incorrect point registration!")
            }
            return calculateHours(startWork, startLunch, endLunch, endWork)
        }
        return CalculationHoursWorked(data, "Incorrect point registration!")
    }

    private fun calculateHours(startWork: LocalDateTime, startLunch: LocalDateTime, endLunch: LocalDateTime, endWork: LocalDateTime): CalculationHoursWorked {
        val difference = startWork.until(startLunch, ChronoUnit.MILLIS) + endLunch.until(endWork, ChronoUnit.MILLIS)
        val hours = difference / 3600000
        val minutes = (difference - hours * 3600000) / 60000
        return CalculationHoursWorked(startWork.toLocalDate().toString(), "Hours worked $hours:$minutes")
    }

    private fun checkPointAlreadyExists(launch: Launch) {
        val dateLaunch = LocalDate.parse(launch.dateOfLaunch.substring(0, 10))
        if (launchRepository.findLaunchByEmployeeDateAndType(
                launch.employeeCpf,
                dateLaunch,
                launch.type
            ) != null
        ) {
            throw BusinessRuleException(
                GenerateMessageForException(
                    LAUNCH_POINT_ALREADY_EXISTS_ERROR,
                    arrayOf(launch.type.toString(), dateLaunch.toString())
                )
            )
        }
    }

    private fun producerTopicKafka(topic: String, key: String, data: Any) {
        try {
            retrySendTopic.sendTopicToKafkaTemplate(topic, key, data)
        } catch (e: Exception) {
            when (e) {
                is InterruptedException, is ExecutionException, is TimeoutException -> {
                    println("error:${e.printStackTrace()}")
                }
            }
        }
    }
}