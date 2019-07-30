package com.wongki.demo.view

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.wongki.demo.R
import com.wongki.demo.model.bean.SearchMusic
import com.wongki.demo.vm.MusicViewModel
import com.wongki.framework.base.BaseActivity
import com.wongki.framework.extensions.dialogDismiss
import com.wongki.framework.extensions.showLoadingDialog
import com.wongki.framework.extensions.toast
import com.wongki.framework.mvvm.getLiveDataViewModel
import com.wongki.framework.mvvm.lifecycle.observe

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : BaseActivity() {

    //1.获取ViewModel对象
    private val musicViewModel by lazy { getLiveDataViewModel<MusicViewModel>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViewModel()
        initView()
    }


    private fun initViewModel() {
        // 2.fork的目的就是生成对应的MutableLiveData对象
        musicViewModel.forkForArrayList(SearchMusic.Item::class)
            .observe(// 3.订阅
                owner = this,
                onStart = {
                    /*开始*/
                    showLoadingDialog(seqNo = 1)
                },
                onCancel = {
                    /*取消，当activity销毁时，准确的说是musicViewModel被onCleared()*/
                    dialogDismiss(seqNo = 1)
                },
                onFailed = { _, message ->
                    // 失败
                    dialogDismiss(seqNo = 1)
                    message?.toast()
                    true // 返回true代表上层处理，返回false代表框架处理，目前框架层会弹Toast
                }
                ,
                onSuccess = { result ->
                    //成功
                    dialogDismiss(seqNo = 1)
                    result?.let { list ->
                        if (list.isNotEmpty()) {
                            val item = list.first()
                            Snackbar.make(fab, "《${item.title}》 - ${item.author}", Snackbar.LENGTH_LONG).setAction("Action", null).show()
                        }

                    }
                }
            )


    }

    private fun initView() {
        fab.setOnClickListener { view ->
            val name = et_primary_key.text.toString()

            checkEmpty(name) {
                Snackbar.make(view, "请输入关键字~", Snackbar.LENGTH_LONG).setAction("Action", null).show()
                return@setOnClickListener
            }

            // 搜索音乐
            musicViewModel.searchMusic(name)
        }
    }

    private inline fun checkEmpty(name: String, onEmpty: () -> Unit): Boolean {
        if (name.isEmpty()) {
            onEmpty()
            return true
        }

        return false
    }
}
