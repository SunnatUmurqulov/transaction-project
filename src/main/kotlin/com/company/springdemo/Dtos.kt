package com.company.springdemo

import java.math.BigDecimal
import java.util.Date


data class BaseMessage(val code: Int, val message: String?)

data class UserCreateDto(
    val fullName: String,
    val username: String,
    val balance: BigDecimal,
) {
    fun toEntity(user: User) = User(fullName, username, balance)
}


data class UserUpdateDto(
    val fullName: String?,
    val username: String?,
    val balance: BigDecimal?
)


data class GetOneUserDto(
    val fullName: String,
    val username: String,
    val balance: BigDecimal
) {
    companion object {
        fun toDto(user: User): GetOneUserDto {
            return user.run {
                GetOneUserDto(fullName, username, balance)
            }
        }
    }
}


data class ProductCreateDto(
    val name: String,
    val count: Long,
    val categoryId: Long
) {
    fun toEntity(category: Category?) = Product(name, count, category)
}


data class ProductUpdateDto(
    val name: String?,
    val count: Long?,
    val categoryId: Long?
)


data class GetOneProductDto(
    val name: String,
    val count: Long,
    val categoryId: Category?
) {
    companion object {
        fun toDTO(product: Product): GetOneProductDto {
            product.run {
                return GetOneProductDto(name, count, categoryId)
            }
        }
    }


}

data class CategoryCreateDto(
    val name: String,
    val order: Long,
    val description: String?
) {
    fun toEntity() = Category(name, order, description)
}


data class CategoryUpdateDto(
    val name: String?,
    val order: Long?,
    val description: String?
)

data class GetCategoryDto(
    val name: String,
    val order: Long,
    val description: String?
) {
    companion object {
        fun toDTO(category: Category): GetCategoryDto {
            category.run {
                return GetCategoryDto(name, order, description)
            }
        }
    }


}


data class TransactionCreateDto(
    val userId: Long,
    val transactionItems: MutableList<TransactionItemCreateDto>
) {
    fun toEntity(user: User, totalAmount: BigDecimal, date: Date) = Transaction(user, totalAmount, date)
}


data class GetOneTransactionDto(
    val id: Long?,
    val userId: User?,
    val totalAmount: BigDecimal,
    val date: Date
) {
    companion object {
        fun toDto(transaction: Transaction): GetOneTransactionDto {
            transaction.run {
                return GetOneTransactionDto(id, user, totalAmount, date)
            }
        }
    }
}




data class TransactionItemCreateDto(
    val productId: Long,
    val count: Long,
    val amount: BigDecimal,
    val transactionId: Long
)


data class GetOneTransactionItemDto(
    val productId: Product,
    val count: Long,
    val amount: BigDecimal,
    val totalAmount: BigDecimal,
    val transactionId: Transaction
){
    companion object{
        fun toDto(transactionItem:TransactionItem):GetOneTransactionItemDto{
           transactionItem.run {
               return GetOneTransactionItemDto(product, count, amount, totalAmount, transaction)
           }
        }
    }
}

data class UserPaymentTransactionCreateDto(
    val userId: Long,
    val amount: BigDecimal,
) {
    fun toEntity(user: User, date: Date, amount: BigDecimal) = UserPaymentTransaction(user, amount, date)
}

data class UserProductDto(
    val productName:String,
    val amount:BigDecimal,
    val count: Long,
    val totalAmount: BigDecimal
){
    companion object{
        fun toDto(transactionItem: TransactionItem):UserProductDto{
            transactionItem.run {
                return UserProductDto(
                    transactionItem.product.name,
                    amount,
                    count,
                    totalAmount
                )
            }
        }
    }
}




data class GetOneUserPaymentTransactionDto(
    val userId: Long,
    val amount: BigDecimal,
    val date: Date
) {
    companion object {
        fun toDto(userPaymentTransaction: UserPaymentTransaction): GetOneUserPaymentTransactionDto? {
            userPaymentTransaction.run {
                return userPaymentTransaction.user.id?.let {
                    GetOneUserPaymentTransactionDto(
                        it,
                        amount,
                        date
                    )
                }
            }

        }
    }
}

data class PaymentHistoryDto(
    val amount: BigDecimal,
    val date: Date
){
    companion object{
        fun toDto(userPaymentTransaction: UserPaymentTransaction):PaymentHistoryDto{
           return userPaymentTransaction.run {
                PaymentHistoryDto(amount,date)
            }
        }
    }
}

