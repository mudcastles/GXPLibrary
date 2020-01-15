package com.peng.baseeverthing.widget.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.KeyEvent
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.Nullable
import com.peng.baseeverthing.eventBus.MessageEvent
import com.peng.baseeverthing.util.SomeCompat
import com.peng.customwidget.view.swipebacklayout.app.SwipeBackActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


abstract class GXPBaseActivity : SwipeBackActivity() {
    protected var mActivity: Activity? = null
    /** -------------------生命周期----------------------*/
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = this
        setContentView(getRootLayoutId())
        EventBus.getDefault().register(this)
        rxPermissions = RxPermissions(this)
        //初始化数据
        initData()
        //view与数据绑定
        initView()
        //初始化沉浸式
        initImmersionBar()
        //设置监听
        setListener()

    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }


    /** -------------------按需求实现相关初始化方法----------------------*/
    open fun initData() {}

    open fun initView() {}

    open fun setListener() {}
    /*默认非根Activity，用以判断back键点击时直接退出还是执行双击验证*/
    open fun isBaseActivity() = false



    /**
     * 子类设置布局Id
     *
     * @return the layout id
     */
    protected abstract fun getRootLayoutId(): Int

    /**
     * 初始化沉浸式
     * Init immersion bar.
     */
    open fun initImmersionBar() { //设置共同沉浸式样式
    }


    /** -------------------Toast显示----------------------*/
    var toast: Toast? = null

    fun showToast(context: Context, content: String) {
        if (toast == null) {
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT)
        }
        toast?.setText(content)
        toast?.show()
    }


    /** -------------------双击back键退出应用----------------------*/
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (isBaseActivity() && keyCode == KeyEvent.KEYCODE_BACK && event?.action == KeyEvent.ACTION_DOWN) {
            doubleClickExit()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    var firstTime: Long = 0
    internal fun doubleClickExit(): Boolean {
        if (System.currentTimeMillis() - firstTime > 2000) {
            showToast(applicationContext, "再按一次退出应用")
            firstTime = System.currentTimeMillis()
        } else {
            EventBus.getDefault().post(
                MessageEvent(
                    MessageEvent.FINISH
                )
            )
            return true
        }
        return false
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(messageEvent: MessageEvent) {
        when (messageEvent.getMessage()) {
            MessageEvent.FINISH -> finish()
        }
    }


    /** -------------------工具方法----------------------*/
    protected fun bindActionDrawable(actionView: ImageView, @DrawableRes drawable: Int) {
        actionView.setImageDrawable(SomeCompat.getImageDrawable(this@GXPBaseActivity, drawable))
    }

    protected fun polishDrawable(drawable: Drawable, @ColorRes color: Int) {
        SomeCompat.polishDrawable(this@GXPBaseActivity, drawable, color)
    }

    lateinit var rxPermissions: RxPermissions
    /**
     * 检查权限，获取到权限后调用
     */
    @SuppressLint("CheckResult")
    fun checkPermissions(permissionList: List<String>, onHasPermissions: () -> Unit) {
        rxPermissions.request(* permissionList.toTypedArray())
            .subscribe {
                if (it) {
                    onHasPermissions.invoke()
                }
            }
    }
}