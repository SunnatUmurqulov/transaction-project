package com.company.springdemo

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.Date

interface UserService {
    fun create(dto: UserCreateDto)
    fun update(id: Long, dto: UserUpdateDto): ResponseEntity<String>
    fun fillBalance(dto: UserPaymentTransactionCreateDto)
    fun getOne(id: Long): GetOneUserDto
    fun getAll(pageable: Pageable): Page<GetOneUserDto>
    fun delete(id: Long)
}

interface ProductService {
    fun create(dto: ProductCreateDto)
    fun update(id: Long, dto: ProductUpdateDto)
    fun getOne(id: Long): GetOneProductDto
    fun getAll(pageable: Pageable): Page<GetOneProductDto>
    fun delete(id: Long)
}

interface CategoryService {
    fun create(dto: CategoryCreateDto)
    fun update(id: Long, dto: CategoryUpdateDto)
    fun getOne(id: Long): GetCategoryDto
    fun getAll(pageable: Pageable): Page<GetCategoryDto>
    fun delete(id: Long)
}

interface TransactionService {
    fun create(dto: TransactionCreateDto)
    fun getOne(id: Long): GetOneTransactionDto
    fun getAll(pageable: Pageable): Page<GetOneTransactionDto>
    fun delete(id: Long)
    fun getAllPurchasedProducts(id:Long,pageable: Pageable): Page<UserProductDto>
    fun getTransactionProducts(id: Long,pageable: Pageable):Page<GetOneTransactionItemDto>
}

interface TransactionItemService {
    fun getOne(id: Long): GetOneTransactionItemDto
    fun getAll(pageable: Pageable): Page<GetOneTransactionItemDto>
    fun delete(id: Long)
}

interface UserPaymentTransactionService {
    fun paymentHistory(id: Long,pageable: Pageable):Page<PaymentHistoryDto>
    fun getAll(pageable: Pageable): Page<GetOneUserPaymentTransactionDto>
    fun delete(id: Long)
}

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val userPaymentTransactionRepository: UserPaymentTransactionRepository
) : UserService {
    override fun create(dto: UserCreateDto) {
        dto.run {
            if (userRepository.existsByUsername(username)) {
                throw UsernameExistsException(username)
            }
            val user = User(fullName, username, balance)
            userRepository.save(toEntity(user))
        }
    }

    override fun update(id: Long, dto: UserUpdateDto): ResponseEntity<String> {
        val user = userRepository.findByIdAndDeletedFalse(id) ?: throw UserNotFoundException(id)
        dto.run {
            fullName?.let { user.fullName = it }
            username?.let { user.username = it }
            balance?.let { user.balance = it }
        }
        userRepository.save(user)
        return ResponseEntity.ok("Edited")
    }

    @Transactional
    override fun fillBalance(dto: UserPaymentTransactionCreateDto) {
        dto.run {
            val user = userRepository.findByIdAndDeletedFalse(userId) ?: throw UserNotFoundException(userId)
            val totalBalance = user.balance + amount
            userRepository.save(user)
            userPaymentTransactionRepository.save(toEntity(user, Date(), totalBalance))
        }
    }

    override fun getOne(id: Long): GetOneUserDto {
        return userRepository.findByIdAndDeletedFalse(id)?.let { GetOneUserDto.toDto(it) }
            ?: throw UserNotFoundException(id)
    }

    override fun getAll(pageable: Pageable): Page<GetOneUserDto> {
        return userRepository.findAllNotDeleted(pageable).map { GetOneUserDto.toDto(it) }
    }

    override fun delete(id: Long) {
        userRepository.trash(id) ?: throw UserNotFoundException(id)
    }
}

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val entityManager: EntityManager
) : ProductService {
    override fun create(dto: ProductCreateDto) {
        dto.run {
            val category = categoryId.let {
                categoryRepository.existsByIdAndDeletedFalse(it).runIfFalse {
                    throw CategoryNotFoundException(it)
                }
                entityManager.getReference(Category::class.java, it)
            }
            productRepository.save(toEntity(category))
        }
    }

    override fun update(id: Long, dto: ProductUpdateDto) {
        val product = productRepository.findByIdAndDeletedFalse(id) ?: throw ProductNotFoundException(id)

        dto.run {
            val category = categoryId?.let {
                categoryRepository.existsByIdAndDeletedFalse(it).runIfFalse {
                    throw CategoryNotFoundException(it)
                }
                entityManager.getReference(Category::class.java, it)
            }

            name?.let { product.name = it }
            count?.let { product.count = it }
            category?.let { product.categoryId = it }
            productRepository.save(product)
        }
    }

    override fun getOne(id: Long): GetOneProductDto {
        return productRepository.findByIdAndDeletedFalse(id)?.let { GetOneProductDto.toDTO(it) }
            ?: throw ProductNotFoundException(id)
    }

    override fun getAll(pageable: Pageable): Page<GetOneProductDto> {
        return productRepository.findAllNotDeleted(pageable).map { GetOneProductDto.toDTO(it) }
    }

    override fun delete(id: Long) {
        productRepository.trash(id) ?: throw ProductNotFoundException(id)
    }

}

@Service
class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository, private val entityManager: EntityManager
) : CategoryService {
    override fun create(dto: CategoryCreateDto) {
        dto.run {
            categoryRepository.save(toEntity())
        }
    }

    override fun update(id: Long, dto: CategoryUpdateDto) {
        val category = categoryRepository.findByIdAndDeletedFalse(id) ?: throw CategoryNotFoundException(id)
        dto.run {
            name?.let { category.name = it }
            order?.let { category.order = it }
            description?.let { category.description = it }
        }
        categoryRepository.save(category)
    }

    override fun getOne(id: Long): GetCategoryDto {
        return categoryRepository.findByIdAndDeletedFalse(id)?.let { GetCategoryDto.toDTO(it) }
            ?: throw ProductNotFoundException(id)
    }

    override fun getAll(pageable: Pageable): Page<GetCategoryDto> {
        return categoryRepository.findAllNotDeleted(pageable).map { GetCategoryDto.toDTO(it) }

    }

    override fun delete(id: Long) {
        categoryRepository.trash(id) ?: throw CategoryNotFoundException(id)
    }

}

@Service
class TransactionServiceImpl(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val transactionItemRepository: TransactionItemRepository
) : TransactionService {
    @Transactional
    override fun create(dto: TransactionCreateDto) {
        dto.run {
            val user = userId.let {
                userRepository.findByIdAndDeletedFalse(it) ?: throw UserNotFoundException(it)
            }
            val totalAmount  = BigDecimal.ZERO

            for (itemDto in transactionItems){
                totalAmount.plus(itemDto.amount.multiply(itemDto.count.toBigDecimal()))
            }
            if (user.balance < totalAmount) throw NotEnoughBalanceException()

            val transaction = transactionRepository.save(toEntity(user, totalAmount, Date()))

            transactionItems.forEach {
                val product = productRepository.findByIdAndDeletedFalse(it.productId) ?: throw ProductNotFoundException(it.productId)
                product.let { p ->
                    if (p.count < it.count) throw NotEnoughProductException(p.count)
                    val transactionItem = TransactionItem(p, it.count, it.amount,it.amount.multiply(it.count.toBigDecimal()) , transaction)
                    transactionItemRepository.save(transactionItem)
                }
            }
        }
    }



    override fun getOne(id: Long): GetOneTransactionDto {
        return transactionRepository.findByIdAndDeletedFalse(id)?.let { GetOneTransactionDto.toDto(it) }
            ?: throw TransactionNotFound(id)
    }

    override fun getAll(pageable: Pageable): Page<GetOneTransactionDto> {
        return transactionRepository.findAllNotDeleted(pageable).map { GetOneTransactionDto.toDto(it) }
    }

    override fun delete(id: Long) {
        transactionRepository.trash(id) ?: throw TransactionNotFound(id)
    }

    override fun getAllPurchasedProducts(id: Long, pageable: Pageable): Page<UserProductDto> {
        val user = userRepository.findByIdAndDeletedFalse(id) ?: throw UserNotFoundException(id)
        return transactionItemRepository.findAllByTransactionUserId(pageable,user).map { UserProductDto.toDto(it) }

    }

    override fun getTransactionProducts(id: Long, pageable: Pageable): Page<GetOneTransactionItemDto> {
        val transaction = transactionRepository.findByIdAndDeletedFalse(id)?:throw TransactionNotFound(id)
        return transactionItemRepository.findAllByTransaction(transaction,pageable).map { GetOneTransactionItemDto.toDto(it) }
    }


}

@Service
class TransactionItemServiceImpl(
    private val transactionItemRepository: TransactionItemRepository
) : TransactionItemService {
    override fun getOne(id: Long): GetOneTransactionItemDto {
        return transactionItemRepository.findByIdAndDeletedFalse(id)?.let { GetOneTransactionItemDto.toDto(it) }
            ?: throw TransactionItemNotFoundException(id)

    }

    override fun getAll(pageable: Pageable): Page<GetOneTransactionItemDto> {
        return transactionItemRepository.findAllNotDeleted(pageable).map { GetOneTransactionItemDto.toDto(it) }
    }

    override fun delete(id: Long) {
        transactionItemRepository.trash(id)?:throw TransactionItemNotFoundException(id)
    }

}

@Service
class UserPaymentTransactionServiceImpl(
    private val userPaymentTransactionRepository: UserPaymentTransactionRepository,
    private val userRepository: UserRepository
) : UserPaymentTransactionService {
    override fun paymentHistory(id: Long, pageable: Pageable): Page<PaymentHistoryDto> {
        val user = userRepository.findByIdAndDeletedFalse(id)?:throw UserNotFoundException(id)
        return userPaymentTransactionRepository.findAllByUserIdAndDeletedFalse(user,pageable).map{PaymentHistoryDto.toDto(it)}

    }


    override fun getAll(pageable: Pageable): Page<GetOneUserPaymentTransactionDto> {
        return userPaymentTransactionRepository.findAllNotDeleted(pageable)
            .map { GetOneUserPaymentTransactionDto.toDto(it) }
    }

    override fun delete(id: Long) {
        userPaymentTransactionRepository.trash(id) ?: throw UserPaymentTransactionNotFound(id)
    }
}