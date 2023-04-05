package com.example.backendcart.controller

import com.example.backendcart.model.CartRequest
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
    fun addProductInCart(@RequestBody cart: CartRequest, response: ServerHttpResponse) =
        cartService.addProductToCart(cart, response)
    @GetMapping("/{userId}/{productId}")
    fun getProductQuantityInCart(
        @PathVariable productId: String,
        @PathVariable userId: String
    ) =
        cartService.getProductQuantityInCart(userId, productId)
    @GetMapping("/{userId}")
    fun getUserCart(@PathVariable userId: String) =
        cartService.getUserCart(userId)
    @GetMapping("/purchase/{userId}")
    fun purchaseAllItemsInCart(@PathVariable userId: String, response: ServerHttpResponse) =
        cartService.purchaseAllItems(userId, response)
    @GetMapping("/computeCost/{userId}")
    fun computeCost(@PathVariable userId: String, response: ServerHttpResponse) =
        cartService.getCartCost(userId, response)
    @PutMapping
    fun updateProductQuantity(
        @RequestBody cartRequest: CartRequest,
        response: ServerHttpResponse
    ) =
        cartService.updateProductQuantity(cartRequest, response)
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