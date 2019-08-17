package com.wongki.framework.http.ssl

import java.security.SecureRandom
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory

/**
 * @author  wangqi
 * date:    2019/6/4
 * email:   wangqi7676@163.com
 * desc:
 */
class DefaultSSL : ISSL {
    private var mSSLSocketFactory: SSLSocketFactory? = null
    private var mTrustAllHostnameVerifier: HostnameVerifier? = null

    init {
        try {
            val sc = SSLContext.getInstance("TLS")
            sc.init(null, arrayOf(TrustAllManager()),
                    SecureRandom())
            mSSLSocketFactory = sc.socketFactory
        } catch (e: Exception) {
        }


        mTrustAllHostnameVerifier = TrustAllHostnameVerifier()
    }

    override fun getSSLSocketFactory(): SSLSocketFactory? = mSSLSocketFactory

    override fun getHostnameVerifier(): HostnameVerifier? = mTrustAllHostnameVerifier
}