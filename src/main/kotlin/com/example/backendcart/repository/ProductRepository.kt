package com.example.backendcart.repository

import com.hrv.mart.apicall.APICaller
import com.hrv.mart.product.Product
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient

@Repository
class ProductRepository (
    @Autowired
    private val webClientBuilder: WebClient.Builder
)
{
    private val productURL = "http://localhost:8081/product"
    fun getCostOfProduct(productId: String) =
        APICaller(webClientBuilder)
            .getData("${productURL}/${productId}", Product::class.java)
            .map {
                it.price
            }
}