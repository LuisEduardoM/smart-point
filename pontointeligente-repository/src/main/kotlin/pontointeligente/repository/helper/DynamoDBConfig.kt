package pontointeligente.repository.helper

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Controller

@Controller
class DynamoDBConfig {

    private val amazonAWSAccessKey: String = ""
    private val amazonAWSSecretKey: String = ""

    @Bean
    fun dynamoDB(): DynamoDB = DynamoDB(amazonDynamoDB())

    @Bean
    fun dynamoDBMapper(): DynamoDBMapper = DynamoDBMapper(amazonDynamoDB())

    @Bean
    fun amazonDynamoDB(): AmazonDynamoDB =
        AmazonDynamoDBClientBuilder.standard().withCredentials(amazonAWSCredentials()).withRegion(Regions.US_EAST_1).build()

    @Bean
    fun amazonAWSCredentials(): AWSCredentialsProvider =
        AWSStaticCredentialsProvider(BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey))
}