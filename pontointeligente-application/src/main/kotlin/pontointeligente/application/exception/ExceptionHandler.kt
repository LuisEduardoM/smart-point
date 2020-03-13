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
import pontointeligente.infrastructure.exception.ErrorCode
import pontointeligente.infrastructure.exception.BusinessRuleException
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
        val ErrorCodes: List<ErrorCode> = gerarListaDeErros(ex.bindingResult)
        return handleExceptionInternal(ex, ErrorCodes, headers, HttpStatus.BAD_REQUEST, request);
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val mensagemUsuario: String = tryGetMessageFromProperties(INVALID_OPERATION)
        val mensagemDesenvolvedor: String = ex.toString();
        val ErrorCodes: List<ErrorCode> = listOf(
            ErrorCode(
                mensagemUsuario,
                mensagemDesenvolvedor
            )
        )
        return handleExceptionInternal(ex, ErrorCodes, headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(BusinessRuleException::class)
    fun handleRegraNegocioException(ex: BusinessRuleException, request: WebRequest?): ResponseEntity<Any> {
        val mensagemUsuario: String = tryGetMessageFromProperties(
            ex.generateMessageForException.messageException,
            *ex.generateMessageForException.parameters
        )
        val mensagemDesenvolvedor: String = ex.toString()
        val ErrorCodes: List<ErrorCode> = arrayListOf(
            ErrorCode(
                mensagemUsuario,
                mensagemDesenvolvedor
            )
        )
        return handleExceptionInternal(ex, ErrorCodes, HttpHeaders(), HttpStatus.BAD_REQUEST, request!!)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(ex: NotFoundException, request: WebRequest?): ResponseEntity<Any> {
        val mensagemUsuario: String = ex.message.toString()
        val mensagemDesenvolvedor: String = ex.message.toString()
        val ErrorCodes: List<ErrorCode> = arrayListOf(
            ErrorCode(
                mensagemUsuario,
                mensagemDesenvolvedor
            )
        )
        return handleExceptionInternal(ex, ErrorCodes, HttpHeaders(), HttpStatus.NOT_FOUND, request!!)
    }

    @ExceptionHandler(EmptyResultDataAccessException::class)
    fun handleEmptyResultDataAccessException(
        ex: EmptyResultDataAccessException,
        request: WebRequest?
    ): ResponseEntity<Any> {
        val mensagemUsuario: String = ex.message.toString()
        val mensagemDesenvolvedor: String = ex.message.toString()
        val ErrorCodes: List<ErrorCode> = arrayListOf(
            ErrorCode(
                mensagemUsuario,
                mensagemDesenvolvedor
            )
        )
        return handleExceptionInternal(ex, ErrorCodes, HttpHeaders(), HttpStatus.NOT_FOUND, request!!)
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationException(
        ex: DataIntegrityViolationException,
        request: WebRequest?
    ): ResponseEntity<Any> {
        val mensagemUsuario: String = tryGetMessageFromProperties(OPERATION_NOT_ALLOWED)
        val mensagemDesenvolvedor: String = ex.rootCause.toString()
        val ErrorCodes: List<ErrorCode> = arrayListOf(
            ErrorCode(
                mensagemUsuario,
                mensagemDesenvolvedor
            )
        )
        return handleExceptionInternal(ex, ErrorCodes, HttpHeaders(), HttpStatus.BAD_REQUEST, request!!)
    }

    private fun gerarListaDeErros(bindingResult: BindingResult): List<ErrorCode> {

        val erros = arrayListOf<ErrorCode>()
        bindingResult.getFieldErrors().forEach {
            val mensagemUsuario: String = it.defaultMessage!!
            val mensagemDesenvolvedor: String = it.toString();
            erros.add(
                ErrorCode(
                    mensagemUsuario,
                    mensagemDesenvolvedor
                )
            );
        }
        return erros
    }

    private fun tryGetMessageFromProperties(key: String, vararg args: String): String {
        return try {
            messageSource.getMessage(key, args, LocaleContextHolder.getLocale())
        } catch (ex: NoSuchMessageException) {
            key
        }
    }
}