package pontointeligente.infrastructure.exception

import java.lang.RuntimeException

data class BusinessRuleException(val generateMessageForException: GenerateMessageForException) :
        RuntimeException(generateMessageForException.messageException)