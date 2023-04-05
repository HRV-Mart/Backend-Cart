package com.example.backendcart.repository

import com.hrv.mart.orderlibrary.model.OrderRequest
import com.hrv.mart.orderlibrary.service.OrderProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Repository

@Repository
class OrderRepository (
    @Autowired
    private val kafkaTemplate: ReactiveKafkaProducerTemplate<String, OrderRequest>,
) {
    private val orderProducer = OrderProducer(kafkaTemplate)
    fun createOrder(orderRequest: OrderRequest) =
        orderProducer.createOrder(orderRequest)
}