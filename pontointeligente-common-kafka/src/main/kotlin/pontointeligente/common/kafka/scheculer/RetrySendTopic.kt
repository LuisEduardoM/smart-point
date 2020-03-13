package pontointeligente.common.kafka.scheculer

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@Service
class RetrySendTopic(val kafkaTemplate: KafkaTemplate<String, Any>) {

    @Retryable(
        value = [ExecutionException::class, InterruptedException::class, TimeoutException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 2000, multiplier = 2.0)
    )
    @Throws(InterruptedException::class, ExecutionException::class, TimeoutException::class)
    fun sendTopicToKafkaTemplate(topic: String, key: String, data: Any) {
        kafkaTemplate.send(topic, key, data).get(100, TimeUnit.MILLISECONDS)
    }

    @Recover
    fun getBackendResponseFallBack(t: Throwable) = println("All retries completed, so Fallback method called!!!");
}