package com.company.springdemo

enum class ErrorCode(val code: Int){
    USER_NAME_EXISTS(100),
    USER_NOT_FOUND(101),
    CATEGORY_NOT_FOUND(102),
    PRODUCT_NOT_FOUND(103),
    WRONG_AMOUNT_EXCEPTION(104),
    NOT_ENOUGH_PRODUCT(105),
    TRANSACTION_NOT_FOUND(106),
    NEGATIVE_BALANCE(107),
    USER_PAYMENT_TRANSACTION_NOT_FOUND(108),
    TRANSACTION_ITEM_NOT_FOUND(109),
    NOT_ENOUGH_BALANCE(110)
}