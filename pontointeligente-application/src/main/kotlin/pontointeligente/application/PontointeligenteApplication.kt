package pontointeligente.application

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.retry.annotation.EnableRetry

@SpringBootApplication
@ComponentScan(basePackages = ["pontointeligente"])
@EntityScan(basePackages = ["pontointeligente"])
@EnableJpaRepositories(basePackages = ["pontointeligente"])
@EnableRetry
class PontointeligenteApplication

fun main(args: Array<String>) {
    runApplication<PontointeligenteApplication>(*args)
}
