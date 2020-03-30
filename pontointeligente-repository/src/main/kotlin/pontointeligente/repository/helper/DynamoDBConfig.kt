package pontointeligente.repository.helper

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.model.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Controller

@Controller
class DynamoDBConfig {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @Value("\${amazon.aws.accesskey}")
    private lateinit var amazonAWSAccessKey: String

    @Value("\${amazon.aws.secretkey}")
    private lateinit var amazonAWSSecretKey: String

    @Value("\${amazon.dynamodb.endpoint}")
    private lateinit var amazonDynamoDBEndpoint: String

    @Bean
    fun dynamoDBMapper(): DynamoDBMapper = DynamoDBMapper(amazonDynamoDB())

    @Bean
    fun amazonDynamoDB(): AmazonDynamoDB =
        AmazonDynamoDBClientBuilder.standard()
            .withCredentials(amazonAWSCredentials())
            .withRegion(Regions.US_EAST_1)
            .build()

//    @Bean
//    fun amazonDynamoDB(): AmazonDynamoDB {
//        val client = AmazonDynamoDBClientBuilder.standard()
//            .withEndpointConfiguration(
//                AwsClientBuilder.EndpointConfiguration(
//                    amazonDynamoDBEndpoint,
//                    Regions.US_EAST_1.toString()
//                )
//            )
//            .withCredentials(amazonAWSCredentials())
//            //.withRegion(Regions.US_EAST_1)
//            .build()
//        createTableForEntity(client, Company::class.java)
//        return client
//    }


    @Bean
    fun amazonAWSCredentials(): AWSCredentialsProvider =
        AWSStaticCredentialsProvider(BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey))

    private fun <T> createTableForEntity(amazonDynamoDB: AmazonDynamoDB, entity: Class<T>) {

        val tableRequest = DynamoDBMapper(amazonDynamoDB)
            .generateCreateTableRequest(entity)
            .withGlobalSecondaryIndexes(
                GlobalSecondaryIndex().withIndexName("cnpj-index")
                    .withProvisionedThroughput(ProvisionedThroughput(1L, 1L))
                    .withKeySchema(
                        KeySchemaElement().withAttributeName("cnpj").withKeyType(KeyType.HASH)
                    )
                    .withProjection(Projection().withProjectionType("ALL"))
            )
            .withProvisionedThroughput(ProvisionedThroughput(1L, 1L))

        try {
            DynamoDB(amazonDynamoDB).createTable(tableRequest).waitForActive()
            log.info("Table created! [entity={}]", entity)
        } catch (e: ResourceInUseException) {
            log.info("Table already exists - skip creation! [entity={}]", entity)
        }
    }
}