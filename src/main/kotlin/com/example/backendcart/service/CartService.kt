package com.example.backendcart.service

import com.example.backendcart.model.Cart
import com.example.backendcart.repository.CartRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CartService (
    @Autowired
    private val cartRepository: CartRepository
)
{
    fun addProductToCart(cart: Cart) =
        cartRepository.insert(cart)
    fun getProductQuantityInCart(userId: String, productId:String) =
        cartRepository.findByUserIdAndProductId(userId, productId)
            .map{
                it.quantity
            }
            .defaultIfEmpty(0)
    fun getUserCart(userId: String) =
        cartRepository.findByUserId(userId)
    fun updateProductQuantity(cart: Cart) =
        cartRepository.existsByUserIdAndProductId(cart.userId, cart.productId)
            .flatMap {
                if (it) {
                    cartRepository.deleteByUserIdAndProductId(cart.userId, cart.productId)
                        .flatMap {
                            addProductToCart(cart)
                        }
                        .then(Mono.just("Product quantity updated"))
                }
                else {
                    Mono.just("Product not found")
                }
            }
    fun deleteProductFromCart(cart: Cart) =
        cartRepository.existsByUserIdAndProductId(cart.userId, cart.productId)
            .flatMap {
                if (it) {
                    cartRepository.deleteByUserIdAndProductId(cart.userId, cart.productId)
                        .then(Mono.just("Product removed from cart"))
                }
                else {
                    Mono.just("Product not found in cart")
                }
            }
    fun emptyUserCart(userId: String) =
        cartRepository.deleteByUserId(userId)
            .then(Mono.just("Successful"))
}