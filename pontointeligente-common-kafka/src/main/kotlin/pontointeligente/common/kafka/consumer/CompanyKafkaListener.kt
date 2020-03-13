package pontointeligente.common.kafka.consumer

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class CompanyKafkaListener {

    @KafkaListener(
        topics = ["\${kafka.smart.point.save.company.topic}", "\${kafka.smart.point.update.company.topic}"],
        groupId = "\${kafka.smart.point.log.company.group}"
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