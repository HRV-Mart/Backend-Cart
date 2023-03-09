package com.example.backendcart.controller

import com.example.backendcart.model.Cart
import com.example.backendcart.service.CartService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/cart")
class CartController (
    @Autowired
    private val cartService: CartService
)
{
    @PostMapping
    fun addProductInCart(@RequestBody cart: Cart) =
        cartService.addProductToCart(cart)
    @GetMapping("/{userId}/{productId}")
    fun getProductQuantityInCart(@PathVariable productId: String, @PathVariable userId: String) =
        cartService.getProductQuantityInCart(userId, productId)
    @GetMapping("/{userId}")
    fun getUserCart(@PathVariable userId: String) =
        cartService.getUserCart(userId)
    @PutMapping
    fun updateProductQuantity(@RequestBody cart: Cart) =
        cartService.updateProductQuantity(cart)
    @DeleteMapping("/{userId}/{productId}")
    fun deleteProductFromCart(@PathVariable productId: String, @PathVariable userId: String) =
        cartService.deleteProductFromCart(userId, productId)
    @DeleteMapping("/{userId}")
    fun emptyCart(@PathVariable userId: String) =
        cartService.emptyUserCart(userId)
}