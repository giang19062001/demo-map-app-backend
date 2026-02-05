package com.vietq.demo_map_app_backend.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class EpayConfig {

    @Value("\${epay.merId}")
    lateinit var merId: String

    @Value("\${epay.cancelPw}")
    lateinit var cancelPw: String

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

    @Value("\${epay.inquiryStatusUrl}")
    lateinit var inquiryStatusUrl: String

    @Value("\${epay.inquiryNoStatusUrl}")
    lateinit var inquiryNoStatusUrl: String

    @Value("\${epay.cancelUrl}")
    lateinit var cancelUrl: String



}