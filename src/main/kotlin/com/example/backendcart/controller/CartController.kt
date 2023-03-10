package com.example.backendcart.controller

import com.example.backendcart.fixture.CustomPageRequest
import com.example.backendcart.model.Cart
import com.example.backendcart.service.CartService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/cart")
class CartController (
    @Autowired
    private val cartService: CartService
)
{
    @PostMapping
    fun addProductInCart(@RequestBody cart: Cart, response: ServerHttpResponse) =
        cartService.addProductToCart(cart, response)
    @GetMapping("/{userId}/{productId}")
    fun getProductQuantityInCart(
        @PathVariable productId: String,
        @PathVariable userId: String
    ) =
        cartService.getProductQuantityInCart(userId, productId)
    @GetMapping("/{userId}")
    fun getUserCart(
        @PathVariable userId: String,
        @RequestParam size: Optional<Int>,
        @RequestParam page: Optional<Int>,
        response: ServerHttpResponse
    ) =
        cartService.getUserCart(userId, CustomPageRequest.getPageRequest(
            optionalSize = size,
            optionalPage = page
        ))
    @PutMapping
    fun updateProductQuantity(
        @RequestBody cart: Cart,
        response: ServerHttpResponse
    ) =
        cartService.updateProductQuantity(cart, response)
    @DeleteMapping("/{userId}/{productId}")
    fun deleteProductFromCart(
        @PathVariable productId: String,
        @PathVariable userId: String,
        response: ServerHttpResponse
    ) =
        cartService.deleteProductFromCart(
            userId,
            productId,
            response
        )
    @DeleteMapping("/{userId}")
    fun emptyCart(
        @PathVariable userId: String,
        response: ServerHttpResponse
    ) =
        cartService.emptyUserCart(
            userId,
            response
        )
}