package pontointeligente.infrastructure.exception

data class GenerateMessageForException(val messageException: String, val parameters: Array<String> = emptyArray())