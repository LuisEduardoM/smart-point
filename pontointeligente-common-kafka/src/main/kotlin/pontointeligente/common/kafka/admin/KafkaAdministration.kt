package pontointeligente.common.kafka.admin

import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.stereotype.Component

@Component
class KafkaAdministration {

    @Value("\${kafka.smart.point.server.port}")
    private lateinit var serverPort: String

    @Value("\${kafka.smart.point.save.company.topic}")
    private lateinit var saveCompanyTopic: String

    @Value("\${kafka.smart.point.update.company.topic}")
    private lateinit var updateCompanyTopic: String

    @Value("\${kafka.smart.point.save.employee.topic}")
    private lateinit var saveEmployeeTopic: String

    @Value("\${kafka.smart.point.update.employee.topic}")
    private lateinit var updateEmployeeTopic: String

    @Value("\${kafka.smart.point.save.launch.topic}")
    private lateinit var saveLaunchTopic: String

    @Value("\${kafka.smart.point.update.launch.topic}")
    private lateinit var updateLaunchTopic: String

    @Bean
    fun admin(): KafkaAdmin {
        val configs = HashMap<String, Any>()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = serverPort
        return KafkaAdmin(configs)
    }

    @Bean
    fun topicSaveCompany(): NewTopic {
        return TopicBuilder.name(saveCompanyTopic)
            .partitions(1)
            .replicas(1)
            .compact()
            .build()
    }

    @Bean
    fun topicUpdateCompany(): NewTopic {
        return TopicBuilder.name(updateCompanyTopic)
            .partitions(1)
            .replicas(1)
            .compact()
            .build()
    }

    @Bean
    fun topicSaveEmployee(): NewTopic {
        return TopicBuilder.name(saveEmployeeTopic)
            .partitions(1)
            .replicas(1)
            .compact()
            .build()
    }

    @Bean
    fun topicUpdateEmployee(): NewTopic {
        return TopicBuilder.name(updateEmployeeTopic)
            .partitions(1)
            .replicas(1)
            .compact()
            .build()
    }

    @Bean
    fun topicSaveLaunch(): NewTopic {
        return TopicBuilder.name(saveLaunchTopic)
            .partitions(1)
            .replicas(1)
            .compact()
            .build()
    }

    @Bean
    fun topicUpdateLaunch(): NewTopic {
        return TopicBuilder.name(updateLaunchTopic)
            .partitions(1)
            .replicas(1)
            .compact()
            .build()
    }
}