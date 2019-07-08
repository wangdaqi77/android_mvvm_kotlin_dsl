package com.wongki.demo.model.bean

/**
 * @author  wangqi
 * date:    2019/6/26
 * email:   wangqi7676@163.com
 * desc:    .
 */
class SearchMusic {


    data class Item(
        val author: String,
        val link: String,
        val lrc: String,
        val pic: String,
        val songid: Int,
        val title: String,
        val type: String,
        val url: String
    )
}