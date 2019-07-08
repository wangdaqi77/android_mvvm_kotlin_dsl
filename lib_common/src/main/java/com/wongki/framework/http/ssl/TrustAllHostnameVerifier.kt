package com.wongki.framework.http.ssl

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

/**
 * @author  wangqi
 * date:    2019/6/4
 * email:   wangqi@feigeter.com
 * desc:
 */
class TrustAllHostnameVerifier : HostnameVerifier {
    override fun verify(hostname: String, session: SSLSession): Boolean {
        return true
    }
}
