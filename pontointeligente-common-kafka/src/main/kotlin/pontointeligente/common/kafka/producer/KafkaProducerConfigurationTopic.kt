package pontointeligente.common.kafka.producer

import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer
import pontointeligente.common.kafka.helper.HelperKafkaTopics

@Configuration
class KafkaProducerConfigurationTopic {

    @Value("\${kafka.smart.point.server.port}")
    private lateinit var serverPort: String

    @Bean
    fun kafKaTemplate(): KafkaTemplate<String, Any> {
        return KafkaTemplate(producerFactory())
    }

    private fun producerFactory(): ProducerFactory<String, Any> {
        val conf = HashMap<String, Any>()
        conf[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = serverPort
        conf[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        conf[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        return DefaultKafkaProducerFactory(conf)
    }
}