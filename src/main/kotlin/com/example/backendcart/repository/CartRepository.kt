package com.example.backendcart.repository

import com.example.backendcart.model.CartRequest
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface CartRepository : ReactiveMongoRepository<CartRequest, String> {
    fun findByUserId(userId: String): Flux<CartRequest>
    fun findByUserIdAndProductId(userId: String, productId: String): Mono<CartRequest>
    fun existsByUserIdAndProductId(userId: String, productId: String): Mono<Boolean>
    fun existsByUserId(userId: String): Mono<Boolean>
    fun deleteByUserIdAndProductId(userId: String, productId: String): Mono<Void>
    fun deleteAllByUserId(userId: String): Mono<Void>
}