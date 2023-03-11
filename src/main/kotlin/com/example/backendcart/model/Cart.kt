package com.example.backendcart.model

import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document

@CompoundIndex(name = "cart_idx", def = "{'userId': 1, 'productId': 1}",
    unique = true)
@Document("Cart")
data class Cart (
    val userId: String,
    val productId: String,
    val quantity: Int
)