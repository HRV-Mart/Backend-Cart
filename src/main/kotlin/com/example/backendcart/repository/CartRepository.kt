package com.example.backendcart.repository

import com.example.backendcart.model.Cart
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface CartRepository : ReactiveMongoRepository<Cart, String> {
    fun findByUserId(userId: String, pageRequest: PageRequest): Flux<Cart>
    fun findByUserIdAndProductId(userId: String, productId: String): Mono<Cart>
    fun countByUserId(userId: String): Mono<Long>
    fun existsByUserIdAndProductId(userId: String, productId: String): Mono<Boolean>
    fun existsByUserId(userId: String): Mono<Boolean>
    fun deleteByUserIdAndProductId(userId: String, productId: String): Mono<Void>
    fun deleteByUserId(userId: String): Flux<Void>
}