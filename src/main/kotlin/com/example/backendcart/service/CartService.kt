package com.example.backendcart.service

import com.example.backendcart.model.CartRequest
import com.example.backendcart.repository.CartRepository
import com.example.backendcart.repository.OrderRepository
import com.example.backendcart.repository.ProductRepository
import com.hrv.mart.apicall.APICaller
import com.hrv.mart.orderlibrary.model.OrderRequest
import com.hrv.mart.product.Product
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class CartService (
    @Autowired
    private val cartRepository: CartRepository,
    @Autowired
    private val productRepository: ProductRepository,
    @Autowired
    private val orderRepository: OrderRepository
)
{
    fun addProductToCart(cart: CartRequest, response: ServerHttpResponse) =
        cartRepository.insert(cart)
            .map {
                response.statusCode = HttpStatus.OK
                "Product added to cart"
            }
            .onErrorResume {
                response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
                Mono.just("Product already exist in cart")
            }
    fun getProductQuantityInCart(userId: String, productId: String) =
        cartRepository.findByUserIdAndProductId(userId, productId)
            .map{
                it.quantity
            }
            .defaultIfEmpty(0)
    fun getUserCart(userId: String) =
        cartRepository.findByUserId(userId)
            .map {
                it.getCartResponse()
            }
    fun purchaseAllItems(userId: String, response: ServerHttpResponse) =
        getUserCart(userId)
            .collectList()
            .flatMap {cart ->
                getCartCost(userId, response)
                    .flatMap { cost ->
                        val order = OrderRequest(
                            userId = userId,
                            products = cart,
                            price = cost
                        )
                        response.statusCode = HttpStatus.OK
                        print(order)
                        print(cost)
                        orderRepository.createOrder(order)
                    }
            }
            .then(emptyUserCart(userId, response))
            .flatMap{
                Mono.just("Order purchase successfully")
            }
    fun updateProductQuantity(cart: CartRequest, response: ServerHttpResponse) =
        cartRepository.existsByUserIdAndProductId(cart.userId, cart.productId)
            .flatMap {
                if (it) {
                    cartRepository.deleteByUserIdAndProductId(cart.userId, cart.productId)
                        .then(Mono.defer { cartRepository.insert(cart) })
                        .then(Mono.just("Product quantity updated"))
                }
                else {
                    response.statusCode = HttpStatus.NOT_FOUND
                    Mono.just("Product not found")
                }
            }
    fun deleteProductFromCart(userId: String, productId: String, response: ServerHttpResponse) =
        cartRepository.existsByUserIdAndProductId(userId, productId)
            .flatMap {
                if (it) {
                    cartRepository.deleteByUserIdAndProductId(userId, productId)
                        .then(Mono.just("Product removed from cart"))
                }
                else {
                    response.statusCode = HttpStatus.NOT_FOUND
                    Mono.just("Product not found in cart")
                }
            }
    fun emptyUserCart(userId: String, response: ServerHttpResponse) =
        cartRepository.existsByUserId(userId)
            .flatMap {
                if (! it) {
                    response.statusCode = HttpStatus.NOT_FOUND
                    Mono.just("Cart not found")
                }
                else {
                    cartRepository.deleteAllByUserId(userId)
                        .then(Mono.just("Successful"))
                }
            }
    fun getCartCost(userId: String, response: ServerHttpResponse) =
        getUserCart(userId)
            .flatMap { cart->
                productRepository.getCostOfProduct(cart.productId)
                    .map {price ->
                        cart.quantity * price
                    }
            }
            .reduce{ x, y ->
                x + y
            }
            .onErrorResume {
                response.statusCode = HttpStatus.NOT_FOUND
                Mono.empty()
            }
}