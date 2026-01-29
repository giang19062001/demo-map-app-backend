package com.vietq.demo_map_app_backend.component

import org.springframework.http.HttpEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap

@Component
class CallApiComponent(
    private val restTemplate: RestTemplate
) {
    fun <T : Any> postJson(
        url: String,
        body: Any,
        responseType: Class<T>
    ): T? = post(url, body, MediaType.APPLICATION_JSON, responseType)

    fun <T : Any> postForm(
        url: String,
        body: MultiValueMap<String, String>,
        responseType: Class<T>
    ): T? = post(url, body, MediaType.APPLICATION_FORM_URLENCODED, responseType)

    private fun <T : Any> post(
        url: String,
        body: Any,
        mediaType: MediaType,
        responseType: Class<T>
    ): T? {
        val headers = HttpHeaders().apply {
            contentType = mediaType
        }
        val requestEntity = HttpEntity(body, headers)

        val response: ResponseEntity<T> = restTemplate.postForEntity(
            url,
            requestEntity,
            responseType
        )
        return response.body
    }
}