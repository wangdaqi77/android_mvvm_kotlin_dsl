package com.wongki.demo

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.wongki.demo.model.bean.SearchMusic
import com.wongki.demo.vm.MusicViewModel
import com.wongki.framework.base.BaseActivity
import com.wongki.framework.extensions.toast
import com.wongki.framework.mvvm.getRetrofitLiveDataViewModel
import com.wongki.framework.mvvm.lifecycle.observe

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : BaseActivity() {

    val musicViewModel by lazy { getRetrofitLiveDataViewModel(MusicViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViewModel()
        initView()
    }

    private fun initViewModel() {
        // fork的目的就是生成对应的MutableLiveData对象
        musicViewModel.forkForArrayList(SearchMusic.Item::class.java)
            .observe(
                owner = this,
                onStart = {/*开始*/},
                onCancel = {/*取消，当activity销毁时，准确的说是musicViewModel被onCleared()*/},
                onFailed = { _, message ->
                    // 失败
                    message?.toast()
                    true
                }
                ,
                onSuccess = { result ->
                    //成功
                    result?.let { list ->
                        if (list.isNotEmpty()) {
                            val item = list.first()
                            Snackbar.make(fab, "《${item.title}》 - ${item.author}", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show()
                        }

                    }
                }
            )


    }

    private fun initView() {


        fab.setOnClickListener { view ->
            val name = et_primary_key.text.toString()
            if (name.isEmpty()) {
                Snackbar.make(view, "请输入关键字~", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                return@setOnClickListener
            }
            // 搜索音乐
            musicViewModel.searchMusic(name)
        }
    }
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        return when (item.itemId) {
//            R.id.action_settings -> true
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
}
