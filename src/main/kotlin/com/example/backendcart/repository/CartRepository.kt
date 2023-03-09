package com.example.backendcart.repository

import com.example.backendcart.model.Cart
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface CartRepository : ReactiveMongoRepository<Cart, String> {
    fun findByUserId(userId: String): Flux<Cart>
    fun findByUserIdAndProductId(userId: String, productId: String): Mono<Cart>
    fun existsByUserIdAndProductId(userId: String, productId: String): Mono<Boolean>
    fun deleteByUserIdAndProductId(userId: String, productId: String): Mono<Void>
    fun deleteByUserId(userId: String): Flux<Void>
}