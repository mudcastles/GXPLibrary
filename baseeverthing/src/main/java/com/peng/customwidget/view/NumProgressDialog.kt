package com.peng.customwidget.view

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.daimajia.numberprogressbar.NumberProgressBar
import com.peng.baseeverthing.R

class NumProgressDialog : Dialog {
    private lateinit var mContentView: NumberProgressBar

    constructor(context: Context, themeResId: Int) : super(context, themeResId)

    init {
        initNumProgressBar()
    }

    private fun initNumProgressBar() {
        mContentView = LayoutInflater.from(context).inflate(
            R.layout.dialog_num_progress,
            null
        ) as NumberProgressBar
        setContentView(mContentView)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }

    fun updateProgress(newProgress: Int) {
        mContentView.progress = newProgress
    }
}