package com.company.springdemo

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource
import java.math.BigDecimal
import java.util.*

sealed class DemoException(message: String? = null) : RuntimeException(message) {
    abstract fun errorType(): ErrorCode

    fun getErrorMessage(errorMessageSource: ResourceBundleMessageSource, vararg array: Any?): BaseMessage {
        return BaseMessage(
            errorType().code,
            errorMessageSource.getMessage(
                errorType().toString(),
                array,
                Locale(LocaleContextHolder.getLocale().language)
            )
        )
    }
}

class UsernameExistsException(val username: String) : DemoException() {
    override fun errorType(): ErrorCode = ErrorCode.USER_NAME_EXISTS
}

class UserNotFoundException(val id: Long) : DemoException() {
    override fun errorType(): ErrorCode = ErrorCode.USER_NOT_FOUND

}

class CategoryNotFoundException(val id: Long) : DemoException() {
    override fun errorType(): ErrorCode = ErrorCode.CATEGORY_NOT_FOUND

}

class ProductNotFoundException(val id: Long) : DemoException() {
    override fun errorType(): ErrorCode = ErrorCode.PRODUCT_NOT_FOUND

}

class WrongAmountException(val totalAmount: BigDecimal) : DemoException() {
    override fun errorType(): ErrorCode = ErrorCode.WRONG_AMOUNT_EXCEPTION

}

class NotEnoughProductException(val count: Long) : DemoException() {
    override fun errorType(): ErrorCode = ErrorCode.NOT_ENOUGH_PRODUCT
}

class TransactionNotFound(val id: Long) : DemoException() {
    override fun errorType(): ErrorCode = ErrorCode.TRANSACTION_NOT_FOUND
}

class NegativeBalanceException(val amount:BigDecimal):DemoException(){
    override fun errorType(): ErrorCode = ErrorCode.NEGATIVE_BALANCE

}

class UserPaymentTransactionNotFound(val id: Long):DemoException(){
    override fun errorType(): ErrorCode = ErrorCode.USER_PAYMENT_TRANSACTION_NOT_FOUND
}

class TransactionItemNotFoundException(val id: Long):DemoException(){
    override fun errorType(): ErrorCode = ErrorCode.TRANSACTION_ITEM_NOT_FOUND
}

class NotEnoughBalanceException():DemoException(){
    override fun errorType(): ErrorCode = ErrorCode.NOT_ENOUGH_BALANCE

}