package com.example.backendcart.controller

import com.example.backendcart.model.Cart
import com.example.backendcart.repository.CartRepository
import com.example.backendcart.service.CartService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.springframework.http.server.reactive.ServerHttpResponse
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
}