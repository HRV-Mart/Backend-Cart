package com.example.backendcart.config

import com.hrv.mart.orderlibrary.model.OrderRequest
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import reactor.kafka.sender.SenderOptions

@Configuration
class ReactiveKafkaProducerConfig {
    @Bean
    fun reactiveKafkaProducerTemplate(
        properties: KafkaProperties
    ): ReactiveKafkaProducerTemplate<String, OrderRequest> {
        val props = properties.buildProducerProperties()
        return ReactiveKafkaProducerTemplate<String, OrderRequest>(SenderOptions.create(props))
    }
}