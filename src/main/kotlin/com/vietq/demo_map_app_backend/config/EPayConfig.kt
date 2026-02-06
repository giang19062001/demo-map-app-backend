package com.vietq.demo_map_app_backend.config
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "epay")
class EpayConfig {
    lateinit var merId: String
    lateinit var cancelPw: String
    lateinit var callBackUrl: String
    lateinit var notiUrl: String
    lateinit var reqDomain: String
    lateinit var encodeKey: String
    lateinit var createLinkUrl: String
    lateinit var inquiryStatusUrl: String
    lateinit var inquiryNoStatusUrl: String
    lateinit var cancelUrl: String
}