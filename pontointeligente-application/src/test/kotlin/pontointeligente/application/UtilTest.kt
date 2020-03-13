package pontointeligente.application

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.json.JSONArray

class UtilTest {
    val objectMapper: ObjectMapper by lazy {
        ObjectMapper()
            .registerModule(JavaTimeModule())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .registerModule(KotlinModule())
    }

    fun <T> jsonFromObject(json: String, type: Class<T>): T = UtilTest().objectMapper.readValue(json, type)

    fun <T> jsonFromListObject(json: String, type: Class<T>): List<T> {
        var objectList: ArrayList<T> = ArrayList()
        val jsonParse = JSONArray(json)
        for (i in 0 until jsonParse.length() step 1) {
            objectList.add(jsonFromObject(jsonParse[i].toString(), type))
        }
        return objectList
    }
}

