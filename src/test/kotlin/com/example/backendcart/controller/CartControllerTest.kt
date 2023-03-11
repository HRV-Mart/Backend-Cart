package com.example.backendcart.controller

import com.example.backendcart.model.Cart
import com.example.backendcart.repository.CartRepository
import com.example.backendcart.service.CartService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.springframework.http.server.reactive.ServerHttpResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class CartControllerTest {
    private val response = mock(ServerHttpResponse::class.java)
    private val cartRepository = mock(CartRepository::class.java)
    private val cartService = CartService(cartRepository)
    private val cartController = CartController(cartService)
    private val cart = Cart(
        userId = "User ID",
        productId = "Product ID",
        quantity = 2
    )
    @Test
    fun `should add product in cart if it is not present in cart`() {
        doReturn(Mono.just(cart))
            .`when`(cartRepository)
            .insert(cart)
        StepVerifier.create(cartController.addProductInCart(cart, response))
            .expectNext("Product added to cart")
            .verifyComplete()
    }
    @Test
    fun `should not add product in cart if it is present in cart`() {
        doReturn(Mono.error<Exception>(Exception("Product already exist in cart")))
            .`when`(cartRepository)
            .insert(cart)
        StepVerifier.create(cartController.addProductInCart(cart, response))
            .expectNext("Product already exist in cart")
            .verifyComplete()
    }
    @Test
    fun `should get product quantity if product available in cart`() {
        doReturn(Mono.just(cart))
            .`when`(cartRepository)
            .findByUserIdAndProductId(cart.userId, cart.productId)
        StepVerifier.create(cartController.getProductQuantityInCart(cart.productId, cart.userId))
            .expectNext(cart.quantity)
            .verifyComplete()
    }
    @Test
    fun `should get product quantity as 0 if product is not available in cart`() {
        doReturn(Mono.empty<Cart>())
            .`when`(cartRepository)
            .findByUserIdAndProductId(cart.userId, cart.productId)
        StepVerifier.create(cartController.getProductQuantityInCart(cart.productId, cart.userId))
            .expectNext(0)
            .verifyComplete()
    }
    @Test
    fun `should get productId present in user cart`() {
        doReturn(Flux.just(cart))
            .`when`(cartRepository)
            .findByUserId(cart.userId)
        StepVerifier.create(cartController.getUserCart(cart.userId))
            .expectNext(cart.productId)
            .verifyComplete()
    }
    @Test
    fun `should update product quantity when it is available in cart`() {
        val newCart = Cart(
            userId = cart.userId,
            productId = cart.productId,
            quantity = 20
        )
        doReturn(Mono.just(true))
            .`when`(cartRepository)
            .existsByUserIdAndProductId(cart.userId, cart.productId)
        doReturn(Mono.empty<Cart>())
            .`when`(cartRepository)
            .deleteByUserIdAndProductId(cart.userId, cart.productId)
        doReturn(Mono.just(newCart))
            .`when`(cartRepository)
            .insert(newCart)
        StepVerifier.create(cartController.updateProductQuantity(newCart, response))
            .expectNext("Product quantity updated")
            .verifyComplete()
    }
    @Test
    fun `should not update product quantity when it is not available in cart`() {
        val newCart = Cart(
            userId = cart.userId,
            productId = cart.productId,
            quantity = 20
        )
        doReturn(Mono.just(false))
            .`when`(cartRepository)
            .existsByUserIdAndProductId(cart.userId, cart.productId)
        StepVerifier.create(cartController.updateProductQuantity(newCart, response))
            .expectNext("Product not found")
            .verifyComplete()
    }
    @Test
    fun `should delete product from cart when it is available in cart`() {
        doReturn(Mono.just(true))
            .`when`(cartRepository)
            .existsByUserIdAndProductId(cart.userId, cart.productId)
        doReturn(Mono.empty<Void>())
            .`when`(cartRepository)
            .deleteByUserIdAndProductId(cart.userId, cart.productId)
        StepVerifier.create(cartController.deleteProductFromCart(cart.productId, cart.userId, response))
            .expectNext("Product removed from cart")
            .verifyComplete()
    }

}