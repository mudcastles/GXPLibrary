package com.peng.customwidget.view

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.view.Gravity
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peng.baseeverthing.util.DimenUtil

/**
 * 列表样式的popupwindow
 */
class SimpleListPopupWindow(val context: Context, onItemClickListener: OnItemClickListener) :
    PopupWindow(context) {
    private val contents = mutableListOf<String>()
    private var recyclerView: RecyclerView = RecyclerView(context)

    init {
        val popAdapter = PopListAdapter(contents)
        popAdapter.listener = onItemClickListener
        val outerRadii = floatArrayOf(20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f)
        val shape = ShapeDrawable().apply {
            this.shape = RoundRectShape(outerRadii, RectF(0f, 0f, 0f, 0f), outerRadii)
            this.paint.isAntiAlias = true
            this.paint.style = Paint.Style.FILL
            this.paint.color = Color.WHITE
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = popAdapter
        }
        this.apply {
            contentView = recyclerView
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            isFocusable = true
            isOutsideTouchable = true
            update()
            setBackgroundDrawable(shape)
        }
    }

    fun addData(contents: List<String>) {
        this.contents.clear()
        this.contents.addAll(contents)
        //必须通过该方法告知adapter数据有变化，如果不调用该方法则不会显示数据
        (recyclerView.adapter as ListAdapter<String, PopViewHolder>).submitList(this.contents)
        //在执行完submitList之后必须代用该方法，否则在数据改变时有可能会造成数据混乱
        recyclerView.adapter?.notifyDataSetChanged()
        update()
    }
}

class PopListAdapter(val list: MutableList<String>) :
    ListAdapter<String, PopViewHolder>(
        DiffCallback()
    ) {
    var listener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopViewHolder {
        var textview = TextView(parent.context)
        val viewHolder = PopViewHolder(textview)
        viewHolder.textview.setOnClickListener {
            listener?.onItemClicked(list[viewHolder.adapterPosition], viewHolder.adapterPosition)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: PopViewHolder, position: Int) {
        holder.textview.text = list[position]
    }
}

class DiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}

class PopViewHolder(val textview: TextView) : RecyclerView.ViewHolder(textview) {

    private val paddingHorizontal = DimenUtil.dip2px(textview.context, 10f)
    private val paddingVertical = DimenUtil.dip2px(textview.context, 6f)

    init {
        textview.apply {
            setPadding(
                paddingHorizontal,
                paddingVertical,
                paddingHorizontal,
                paddingVertical
            )
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            gravity = Gravity.CENTER
        }
    }
}

interface OnItemClickListener {
    fun onItemClicked(value: Any, position: Int)
}