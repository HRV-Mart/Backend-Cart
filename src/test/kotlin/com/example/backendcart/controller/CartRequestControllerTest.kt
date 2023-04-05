package com.example.backendcart.controller

import com.example.backendcart.model.CartRequest
import com.example.backendcart.repository.CartRepository
import com.example.backendcart.repository.ProductRepository
import com.example.backendcart.service.CartService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.springframework.http.server.reactive.ServerHttpResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class CartRequestControllerTest {
    private val response = mock(ServerHttpResponse::class.java)
    private val cartRepository = mock(CartRepository::class.java)
    private val productRepository = mock(ProductRepository::class.java)
    private val cartService = CartService(cartRepository, productRepository)
    private val cartController = CartController(cartService)
    private val cartRequest = CartRequest(
        userId = "User ID",
        productId = "Product ID",
        quantity = 2
    )
    @Test
    fun `should add product in cart if it is not present in cart`() {
        doReturn(Mono.just(cartRequest))
            .`when`(cartRepository)
            .insert(cartRequest)
        StepVerifier.create(cartController.addProductInCart(cartRequest, response))
            .expectNext("Product added to cart")
            .verifyComplete()
    }
    @Test
    fun `should not add product in cart if it is present in cart`() {
        doReturn(Mono.error<Exception>(Exception("Product already exist in cart")))
            .`when`(cartRepository)
            .insert(cartRequest)
        StepVerifier.create(cartController.addProductInCart(cartRequest, response))
            .expectNext("Product already exist in cart")
            .verifyComplete()
    }
    @Test
    fun `should get product quantity if product available in cart`() {
        doReturn(Mono.just(cartRequest))
            .`when`(cartRepository)
            .findByUserIdAndProductId(cartRequest.userId, cartRequest.productId)
        StepVerifier.create(cartController.getProductQuantityInCart(cartRequest.productId, cartRequest.userId))
            .expectNext(cartRequest.quantity)
            .verifyComplete()
    }
    @Test
    fun `should get product quantity as 0 if product is not available in cart`() {
        doReturn(Mono.empty<CartRequest>())
            .`when`(cartRepository)
            .findByUserIdAndProductId(cartRequest.userId, cartRequest.productId)
        StepVerifier.create(cartController.getProductQuantityInCart(cartRequest.productId, cartRequest.userId))
            .expectNext(0)
            .verifyComplete()
    }
    @Test
    fun `should get product and prouduct quantity in user cart`() {
        doReturn(Flux.just(cartRequest))
            .`when`(cartRepository)
            .findByUserId(cartRequest.userId)
        StepVerifier.create(cartController.getUserCart(cartRequest.userId))
            .expectNext(cartRequest.getCartResponse())
            .verifyComplete()
    }
    @Test
    fun `should update product quantity when it is available in cart`() {
        val newCart = CartRequest(
            userId = cartRequest.userId,
            productId = cartRequest.productId,
            quantity = 20
        )
        doReturn(Mono.just(true))
            .`when`(cartRepository)
            .existsByUserIdAndProductId(cartRequest.userId, cartRequest.productId)
        doReturn(Mono.empty<CartRequest>())
            .`when`(cartRepository)
            .deleteByUserIdAndProductId(cartRequest.userId, cartRequest.productId)
        doReturn(Mono.just(newCart))
            .`when`(cartRepository)
            .insert(newCart)
        StepVerifier.create(cartController.updateProductQuantity(newCart, response))
            .expectNext("Product quantity updated")
            .verifyComplete()
    }
    @Test
    fun `should not update product quantity when it is not available in cart`() {
        val newCart = CartRequest(
            userId = cartRequest.userId,
            productId = cartRequest.productId,
            quantity = 20
        )
        doReturn(Mono.just(false))
            .`when`(cartRepository)
            .existsByUserIdAndProductId(cartRequest.userId, cartRequest.productId)
        StepVerifier.create(cartController.updateProductQuantity(newCart, response))
            .expectNext("Product not found")
            .verifyComplete()
    }
    @Test
    fun `should delete product from cart when it is available in cart`() {
        doReturn(Mono.just(true))
            .`when`(cartRepository)
            .existsByUserIdAndProductId(cartRequest.userId, cartRequest.productId)
        doReturn(Mono.empty<Void>())
            .`when`(cartRepository)
            .deleteByUserIdAndProductId(cartRequest.userId, cartRequest.productId)
        StepVerifier.create(cartController.deleteProductFromCart(cartRequest.productId, cartRequest.userId, response))
            .expectNext("Product removed from cart")
            .verifyComplete()
    }
    @Test
    fun `should not delete product from cart when it is not available in cart`() {
        doReturn(Mono.just(false))
            .`when`(cartRepository)
            .existsByUserIdAndProductId(cartRequest.userId, cartRequest.productId)
        StepVerifier.create(cartController.deleteProductFromCart(cartRequest.productId, cartRequest.userId, response))
            .expectNext("Product not found in cart")
            .verifyComplete()
    }
    @Test
    fun `should empty cart when products are available in cart`() {
        doReturn(Mono.just(true))
            .`when`(cartRepository)
            .existsByUserId(cartRequest.userId)
        doReturn(Flux.empty<Void>())
            .`when`(cartRepository)
            .deleteByUserId(cartRequest.userId)
        StepVerifier.create(cartController.emptyCart(cartRequest.userId, response))
            .expectNext("Successful")
            .verifyComplete()
    }
    @Test
    fun `should not empty cart when products are not available in cart`() {
        doReturn(Mono.just(false))
            .`when`(cartRepository)
            .existsByUserId(cartRequest.userId)
        StepVerifier.create(cartController.emptyCart(cartRequest.userId, response))
            .expectNext("Cart not found")
            .verifyComplete()
    }
    @Test
    fun `should return cost of cart if it is not empty`() {
        val userId = "userId"
        val productId = listOf("product1", "product2", "product3")
        val quantity = listOf(1L, 2L, 3L)
        val price = listOf(10L, 20L, 30L)

        val expectedPrice = quantity.zip(price) {a, b -> a*b}.sum()
        val cartList = mutableListOf<CartRequest>()
        for (i in productId.indices) {
            cartList.add(CartRequest(
                productId = productId[i],
                quantity = quantity[i],
                userId = userId
            ))
            doReturn(Mono.just(price[i]))
                .`when`(productRepository)
                .getCostOfProduct(productId[i])
        }
        val cart = Flux.just(*cartList.toTypedArray())

        doReturn(cart)
            .`when`(cartRepository)
            .findByUserId(userId)

        StepVerifier.create(cartController.computeCost(userId, response))
            .expectNext(expectedPrice)
            .verifyComplete()
    }
    @Test
    fun `should return nothing if product is not available`() {
        val userId = "userId"
        val productId = listOf("product1", "product2", "product3")
        val quantity = listOf(1L, 2L, 3L)

        val cartList = mutableListOf<CartRequest>()
        for (i in productId.indices) {
            cartList.add(CartRequest(
                productId = productId[i],
                quantity = quantity[i],
                userId = userId
            ))
            doReturn(Mono.error<Exception>(Exception("Product not available")))
                .`when`(productRepository)
                .getCostOfProduct(productId[i])
        }
        val cart = Flux.just(*cartList.toTypedArray())

        doReturn(cart)
            .`when`(cartRepository)
            .findByUserId(userId)

        StepVerifier.create(cartController.computeCost(userId, response))
            .verifyComplete()
    }
}