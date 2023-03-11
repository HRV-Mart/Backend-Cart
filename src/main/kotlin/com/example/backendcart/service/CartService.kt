package com.example.backendcart.service

import com.example.backendcart.model.Cart
import com.example.backendcart.repository.CartRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CartService (
    @Autowired
    private val cartRepository: CartRepository
)
{
    fun addProductToCart(cart: Cart, response: ServerHttpResponse) =
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
                it.productId
            }
    fun updateProductQuantity(cart: Cart, response: ServerHttpResponse) =
        cartRepository.existsByUserIdAndProductId(cart.userId, cart.productId)
            .flatMap {
                if (it) {
                    cartRepository.deleteByUserIdAndProductId(cart.userId, cart.productId)
                        .flatMap {
                            cartRepository.insert(cart)
                        }
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
                    cartRepository.deleteByUserId(userId)
                        .then(Mono.just("Successful"))
                }
            }
}