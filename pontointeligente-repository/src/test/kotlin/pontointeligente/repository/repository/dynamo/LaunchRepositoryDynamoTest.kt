package pontointeligente.repository.repository.dynamo

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.annotation.Rollback
import pontointeligente.repository.AbstractRepository
import pontointeligente.repository.implemention.dynamo.LaunchRepositoryImplementationDynamo

class LaunchRepositoryDynamoTest : AbstractRepository() {

    @BeforeEach
    fun init() {
        launchRepository = LaunchRepositoryImplementationDynamo(dynamoDBMapper = dynamoDBMapper, dynamoDB = dynamoDB)
        super.start()
    }

    @Test
    @Rollback
    fun save() {
        Assertions.assertNotNull(launchRepository.save(launch))
    }
}