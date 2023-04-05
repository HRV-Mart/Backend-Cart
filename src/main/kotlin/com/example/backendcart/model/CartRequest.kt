package com.example.backendcart.model

import com.hrv.mart.cartresponse.model.CartResponse
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document

@CompoundIndex(name = "cart_idx", def = "{'userId': 1, 'productId': 1}",
    unique = true)
@Document("Cart")
data class CartRequest (
    val userId: String,
    val productId: String,
    val quantity: Long
) {
    fun getCartResponse() =
        CartResponse(
            productId = this.productId,
            quantity = this.quantity
        )
}