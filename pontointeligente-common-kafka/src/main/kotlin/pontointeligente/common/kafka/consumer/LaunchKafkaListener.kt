package pontointeligente.common.kafka.consumer

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class LaunchKafkaListener {

    @KafkaListener(
        topics = ["\${kafka.smart.point.save.launch.topic}", "\${kafka.smart.point.update.launch.topic}"],
        groupId = "\${kafka.smart.point.log.launch.group}"
    )
    fun listen(record: ConsumerRecord<String, Any>) {
        println("-------------------------")
        println(record.topic())
        println(record.key())
        println(record.value())
        println(record.partition())
        println(record.offset())
    }
}