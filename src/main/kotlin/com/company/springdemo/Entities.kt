package com.company.springdemo

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import java.math.BigDecimal
import java.util.*


@MappedSuperclass
class BaseEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @Column(nullable = false) @ColumnDefault(value = "false") var deleted: Boolean = false,
)


@Entity(name = "users")
class User(
    @Column(nullable = false) var fullName: String,
    @Column( nullable = false, unique = true) var username: String,
    var balance: BigDecimal,
) : BaseEntity()




@Entity
class Product(
    var name: String,
    var count: Long,
    @ManyToOne var categoryId: Category?
) : BaseEntity()




@Entity
class Category(
    var name: String,
    @Column(name = "order_number")
    var order: Long,
    var description: String?
) : BaseEntity()




@Entity
class Transaction(
    @ManyToOne var user: User? = null,
    var totalAmount: BigDecimal,
    var date: Date
) : BaseEntity()




@Entity
class TransactionItem(
    @ManyToOne val product: Product,
    var count: Long,
    var amount: BigDecimal,
    var totalAmount: BigDecimal,
    @ManyToOne val transaction: Transaction
) : BaseEntity()




@Entity
class UserPaymentTransaction(
    @ManyToOne val user: User,
    var amount: BigDecimal,
    val date: Date
) : BaseEntity()