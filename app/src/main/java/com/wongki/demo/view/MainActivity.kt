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
import com.wongki.framework.mvvm.lifecycle.LiveDataViewModelDslMarker
import com.wongki.framework.mvvm.getLiveDataViewModel

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : BaseActivity() {

    //1.获取ViewModel对象
    private val musicViewModel by lazy { getLiveDataViewModel<MusicViewModel>() }
    @LiveDataViewModelDslMarker
    private fun musicViewModel(action:MusicViewModel.()->Unit){musicViewModel.action()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViewModel()
        initView()
    }


    private fun initViewModel() {
        // 2.attach的目的就是生成对应的MutableLiveData对象
        musicViewModel {

            attachLiveDataWrapperForArrayList<SearchMusic.Item> {

                observe {
                    owner = this@MainActivity

                    onStart {
                        /*开始*/
                        showLoadingDialog(seqNo = 1)
                    }

                    onCancel {
                        /*取消，当activity销毁时，准确的说是musicViewModel被onCleared()*/
                        dialogDismiss(seqNo = 1)
                    }

                    onSuccess {
                        //成功
                        dialogDismiss(seqNo = 1)
                        val result = this
                        result?.let { list ->
                            if (list.isNotEmpty()) {
                                val item = list.first()
                                Snackbar.make(
                                    fab,
                                    "《${item.title}》 - ${item.author}",
                                    Snackbar.LENGTH_LONG
                                )
                                    .setAction("Action", null).show()
                            }

                        }
                    }

                    onFailed { _, message ->
                        // 失败
                        dialogDismiss(seqNo = 1)
                        message.toast()
                        true // 返回true代表上层处理，返回false代表框架处理，目前框架层会弹Toast
                    }

                }
            }



        }

    }

    private fun initView() {
        fab.setOnClickListener { view ->
            val name = et_primary_key.text.toString()

            checkEmpty(name) {
                Snackbar.make(view, "请输入关键字~", Snackbar.LENGTH_LONG).setAction("Action", null)
                    .show()
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
