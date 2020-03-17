package pontointeligente.application.exception

import org.springframework.context.MessageSource
import org.springframework.context.NoSuchMessageException
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import pontointeligente.infrastructure.exception.BusinessRuleException
import pontointeligente.infrastructure.exception.ErrorCode
import pontointeligente.infrastructure.exception.MessagesValidationsErrors.INVALID_OPERATION
import pontointeligente.infrastructure.exception.MessagesValidationsErrors.OPERATION_NOT_ALLOWED
import pontointeligente.infrastructure.exception.NotFoundException

@ControllerAdvice
class ExceptionHandler(
    private val messageSource: MessageSource
) : ResponseEntityExceptionHandler() {

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val errorsCode: List<ErrorCode> = gerarListaDeErros(ex.bindingResult)
        return handleExceptionInternal(ex, errorsCode, headers, HttpStatus.BAD_REQUEST, request);
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val userMessage: String = tryGetMessageFromProperties(INVALID_OPERATION)
        val developerMessage: String = ex.toString();
        val errorsCode: List<ErrorCode> = listOf(
            ErrorCode(
                userMessage,
                developerMessage
            )
        )
        return handleExceptionInternal(ex, errorsCode, headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(BusinessRuleException::class)
    fun handleBusinessRuleException(ex: BusinessRuleException, request: WebRequest?): ResponseEntity<Any> {
        val userMessage: String = tryGetMessageFromProperties(
            ex.generateMessageForException.messageException,
            *ex.generateMessageForException.parameters
        )
        val developerMessage: String = ex.toString()
        val errorsCode: List<ErrorCode> = arrayListOf(
            ErrorCode(
                userMessage,
                developerMessage
            )
        )
        return handleExceptionInternal(ex, errorsCode, HttpHeaders(), HttpStatus.BAD_REQUEST, request!!)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(ex: NotFoundException, request: WebRequest?): ResponseEntity<Any> {
        val userMessage: String = ex.message.toString()
        val developerMessage: String = ex.message.toString()
        val errorsCode: List<ErrorCode> = arrayListOf(
            ErrorCode(
                userMessage,
                developerMessage
            )
        )
        return handleExceptionInternal(ex, errorsCode, HttpHeaders(), HttpStatus.NOT_FOUND, request!!)
    }

    @ExceptionHandler(EmptyResultDataAccessException::class)
    fun handleEmptyResultDataAccessException(
        ex: EmptyResultDataAccessException,
        request: WebRequest?
    ): ResponseEntity<Any> {
        val userMessage: String = ex.message.toString()
        val developerMessage: String = ex.message.toString()
        val errorsCode: List<ErrorCode> = arrayListOf(
            ErrorCode(
                userMessage,
                developerMessage
            )
        )
        return handleExceptionInternal(ex, errorsCode, HttpHeaders(), HttpStatus.NOT_FOUND, request!!)
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationException(
        ex: DataIntegrityViolationException,
        request: WebRequest?
    ): ResponseEntity<Any> {
        val userMessage: String = tryGetMessageFromProperties(OPERATION_NOT_ALLOWED)
        val developerMessage: String = ex.rootCause.toString()
        val errorsCode: List<ErrorCode> = arrayListOf(
            ErrorCode(
                userMessage,
                developerMessage
            )
        )
        return handleExceptionInternal(ex, errorsCode, HttpHeaders(), HttpStatus.BAD_REQUEST, request!!)
    }

    private fun gerarListaDeErros(bindingResult: BindingResult): List<ErrorCode> {

        val errors = arrayListOf<ErrorCode>()
        bindingResult.getFieldErrors().forEach {
            val userMessage: String = it.defaultMessage!!
            val developerMessage: String = it.toString();
            errors.add(
                ErrorCode(
                    userMessage,
                    developerMessage
                )
            );
        }
        return errors
    }

    private fun tryGetMessageFromProperties(key: String, vararg args: String): String {
        return try {
            messageSource.getMessage(key, args, LocaleContextHolder.getLocale())
        } catch (ex: NoSuchMessageException) {
            key
        }
    }
}