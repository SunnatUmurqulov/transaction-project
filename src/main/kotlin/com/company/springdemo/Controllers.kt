package com.company.springdemo

import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@ControllerAdvice
class ExceptionHandlers(
    private val errorMessageSource: ResourceBundleMessageSource
) {
    @ExceptionHandler(DemoException::class)
    fun handleException(exception: DemoException): ResponseEntity<*> {
        return when (exception) {
            is UsernameExistsException -> ResponseEntity.badRequest()
                .body(exception.getErrorMessage(errorMessageSource, exception.username))

            is UserNotFoundException -> ResponseEntity.badRequest()
                .body(exception.getErrorMessage(errorMessageSource, exception.id))

            is CategoryNotFoundException -> ResponseEntity.badRequest()
                .body(exception.getErrorMessage(errorMessageSource, exception.id))

            is NotEnoughProductException -> ResponseEntity.badRequest()
                .body(exception.getErrorMessage(errorMessageSource, exception.count))

            is ProductNotFoundException -> ResponseEntity.badRequest()
                .body(exception.getErrorMessage(errorMessageSource, exception.id))

            is TransactionNotFound -> ResponseEntity.badRequest()
                .body(exception.getErrorMessage(errorMessageSource, exception.id))

            is WrongAmountException -> ResponseEntity.badRequest()
                .body(exception.getErrorMessage(errorMessageSource, exception.totalAmount))

            is NegativeBalanceException -> ResponseEntity.badRequest()
                .body(exception.getErrorMessage(errorMessageSource, exception.amount))

            is TransactionItemNotFoundException -> ResponseEntity.badRequest()
                .body(exception.getErrorMessage(errorMessageSource, exception.id))

            is UserPaymentTransactionNotFound -> ResponseEntity.badRequest()
                .body(exception.getErrorMessage(errorMessageSource, exception.id))

            is NotEnoughBalanceException -> ResponseEntity.badRequest()
                .body(exception.getErrorMessage(errorMessageSource, exception))
        }
    }
}

@RestController
@RequestMapping("api/v1/user")
class UserController(
    private val service: UserService,
    private val userPaymentTransactionService: UserPaymentTransactionService
) {

    @PostMapping
    fun create(@RequestBody dto: UserCreateDto) = service.create(dto)

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: UserUpdateDto) = service.update(id, dto)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): GetOneUserDto = service.getOne(id)

    @GetMapping
    fun getAll(pageable: Pageable): Page<GetOneUserDto> = service.getAll(pageable)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)

    @PostMapping("add-balance")
    fun fillBalance(@RequestBody dto: UserPaymentTransactionCreateDto) = service.fillBalance(dto)

    @GetMapping("payment-history/{id}")
    fun paymentHistory(@PathVariable id: Long,pageable: Pageable):
            Page<PaymentHistoryDto> = userPaymentTransactionService.paymentHistory(id,pageable)
}


@RestController
@RequestMapping("api/v1/category")
class CategoryController(private val service: CategoryService) {

    @PostMapping
    fun create(@RequestBody dto: CategoryCreateDto) = service.create(dto)

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: CategoryUpdateDto) = service.update(id, dto)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): GetCategoryDto = service.getOne(id)

    @GetMapping()
    fun getAll(pageable: Pageable): Page<GetCategoryDto> = service.getAll(pageable)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)
}


@RestController
@RequestMapping("api/v1/product")
class ProductController(private val service: ProductService) {

    @PostMapping
    fun create(@RequestBody dto: ProductCreateDto) = service.create(dto)

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: ProductUpdateDto) = service.update(id, dto)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): GetOneProductDto = service.getOne(id)

    @GetMapping()
    fun getAll(pageable: Pageable): Page<GetOneProductDto> = service.getAll(pageable)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)
}

@RestController
@RequestMapping("api/v1/transaction")
class TransactionalController(private val service: TransactionService) {

    @PostMapping
    fun create(@RequestBody dto: TransactionCreateDto) = service.create(dto)


    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): GetOneTransactionDto = service.getOne(id)

    @GetMapping
    fun getAll(pageable: Pageable): Page<GetOneTransactionDto> = service.getAll(pageable)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)

    @GetMapping("purchased-product/{id}")
    fun getAllPurchasedProducts(@PathVariable id: Long, pageable: Pageable):
            Page<UserProductDto> = service.getAllPurchasedProducts(id, pageable)


    @GetMapping("products/{id}")
    fun getTransactionProducts(@PathVariable id: Long, pageable: Pageable):
            Page<GetOneTransactionItemDto> = service.getTransactionProducts(id, pageable)
}


@RestController
@RequestMapping("api/v1/transaction-item")
class TransactionItemController(private val service: TransactionItemService) {
    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): GetOneTransactionItemDto = service.getOne(id)

    @GetMapping()
    fun getAll(pageable: Pageable): Page<GetOneTransactionItemDto> = service.getAll(pageable)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)
}
