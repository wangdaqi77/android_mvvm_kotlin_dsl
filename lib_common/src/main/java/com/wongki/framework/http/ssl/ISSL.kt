package com.wongki.framework.http.ssl

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSocketFactory

/**
 * @author  wangqi
 * date:    2019/6/4
 * email:   wangqi@feigeter.com
 * desc:
 */
interface ISSL {
    fun getSSLSocketFactory(): SSLSocketFactory?
    fun getHostnameVerifier(): HostnameVerifier?
}