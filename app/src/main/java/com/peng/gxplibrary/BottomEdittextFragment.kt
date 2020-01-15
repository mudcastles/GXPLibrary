package com.peng.gxplibrary


import android.graphics.Color
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.gyf.immersionbar.ImmersionBar
import com.peng.baseeverthing.widget.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_bottom_edittext.*

class BottomEdittextFragment : BaseFragment() {


    override fun getLayout(): Int =R.layout.fragment_bottom_edittext
    override fun getTitleBarView(): View? = toolbar
    override fun initView() {
        super.initView()
        (getTitleBarView() as Toolbar).title = "fragment测试"
        getTitleBarView()!!.setBackgroundResource(R.drawable.shape_main)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            BottomEdittextFragment().apply {

            }
    }

    override fun initImmersionBar() {
        super.initImmersionBar()
//        ImmersionBar.with(this).keyboardEnable(false).init()
    }
}
