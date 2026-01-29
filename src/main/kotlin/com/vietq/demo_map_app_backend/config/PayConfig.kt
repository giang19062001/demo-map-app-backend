package com.vietq.demo_map_app_backend.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class EpayConfig {

    @Value("\${epay.sandbox:true}")
    val sandbox: Boolean = true

    @Value("\${epay.merId}")
    lateinit var merId: String

    @Value("\${epay.callBackUrl}")
    lateinit var callBackUrl: String

    @Value("\${epay.notiUrl}")
    lateinit var notiUrl: String

    @Value("\${epay.reqDomain}")
    lateinit var reqDomain: String

    @Value("\${epay.encodeKey}")
    lateinit var encodeKey: String

    @Value("\${epay.createLinkUrl}")
    lateinit var createLinkUrl: String

    @Value("\${epay.inquiryStatus}")
    lateinit var inquiryStatus: String

    @Value("\${epay.inquiryNoStatus}")
    lateinit var inquiryNoStatus: String

}