package com.wongki.demo.view

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.wongki.demo.R
import com.wongki.demo.model.bean.SearchMusic
import com.wongki.demo.vm.MusicViewModel
import com.wongki.framework.base.BaseActivity
import com.wongki.framework.extensions.dialogDismiss
import com.wongki.framework.extensions.showLoadingDialog
import com.wongki.framework.mvvm.lifecycle.viewModel

import kotlinx.android.synthetic.main.activity_music.*
import kotlinx.android.synthetic.main.content_main.*

class MusicActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)

        initViewModel()
        initView()
    }


    private fun initViewModel() {
        // 1.attach的目的是在对应的ViewModel生成对应的LiveData对象
        // 2.LiveData会缓存在ViewModel中(有唯一的Key绑定，Key的生成与kClass、key相关)
        // 3.observe的目的是订阅，观察数据变动
        viewModel<MusicViewModel> {

            // 结果总数量
            attachObserve<Int> {

                key {
                    kClass = Int::class
                    method = "setTotalCount"
                }

                // 订阅，观察数据变动
                observe {
                    owner = this@MusicActivity
                    onChange {
                        tv_total.text = "$this"
                    }
                }
            }


            // 结果总数量
            attachObserve<String> {
                key {
                    kClass = String::class
                    method = "setResultList"
                }

                // 订阅，观察数据变动
                observe {
                    owner = this@MusicActivity
                    onChange {
                        tv_result.text = "$this"
                    }
                }
            }


            // 搜索音乐结果
            attachEventObserveForArrayList<SearchMusic.Item> {

                key {
                    kClass = SearchMusic.Item::class
                }

                // 订阅，观察网络请求状态和结果
                observe {
                    owner = this@MusicActivity
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
                        Snackbar
                            .make(fab, "成功", Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .show()
                    }

                    onFailed { _, message ->
                        // 失败
                        dialogDismiss(seqNo = 1)
                        Snackbar
                            .make(fab, "失败：$message", Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .show()

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
            viewModel<MusicViewModel> {
                searchMusic(name)
            }
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
