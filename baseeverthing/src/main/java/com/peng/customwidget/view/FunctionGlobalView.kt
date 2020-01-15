package com.peng.customwidget.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.peng.baseeverthing.R
import com.peng.baseeverthing.util.DimenUtil
import kotlin.math.*

/**
 * 功能球
 */
class FunctionGlobalView : View {

    enum class ShowMode {
        GLOBAL,     //功能球旋转动画模式
        BUTTON      //按钮模式
    }


    //画笔
    private val mPaint = Paint()

    //内圆半径，计算得到
    private var mInnerCircleRadius = 0
    //外圆半径，计算得到
    private var mExternalCircleRadius = 0
    //内部button半径，用户赋值
    private var mInnerButtonRadius = 0f
    //内部button的path
    private var innerButtonPath: Path? = null
    //menu之间的margin,对角线直接的距离，用户赋值
    private var mMenuMarginDiagonal = 0.0
    //内外圆之间的空隙，用户赋值
    private var mCircleMargin = 0
    //当前view的旋转角度
    private var mAngle = 0.0
    //目标角度
    private var destAngle = 0.0

    //文本字体大小，用户赋值
    private var mTextSizePx = DimenUtil.dip2px(context, 14f)
    //文字垂直方向的padding，用户赋值
    private var mTextPaddingVerticalPx = DimenUtil.dip2px(context, 4f)
    //边框颜色
    private var mStockColor = Color.parseColor("#05aee0")

    //菜单
    private var mMenus = mutableListOf<Menu>()


    private val mRect = Rect()
    //旋转动画数值生成器
    private var animator: ValueAnimator? = null


    //选中的menu下标
    private var checkedIndex = -1

    //当前展示模式
    private var showMode = ShowMode.GLOBAL

    //    ========================================= 功能球模式下的变量 ========================================
    private var globalAngle = 0f
    private var globalAngle1 = 0f
    private var mExternCirclePath = Path()
    private var globalAnimator: ValueAnimator? = null
    private var globalAnimator1: ValueAnimator? = null
    private var mBackgroundBitmap = drawableToBitmap(
        ResourcesCompat.getDrawable(
            context.resources,
            R.drawable.u2276,
            context.theme
        )!!, null
    )
    private var mBackground2Bitmap = drawableToBitmap(
        ResourcesCompat.getDrawable(
            context.resources,
            R.drawable.qiu,
            context.theme
        )!!, null
    )
    private var mBackground3Bitmap = drawableToBitmap(
        ResourcesCompat.getDrawable(
            context.resources,
            R.drawable.u2277,
            context.theme
        )!!, null
    )
    private var mBackground4Bitmap = drawableToBitmap(
        ResourcesCompat.getDrawable(
            context.resources,
            R.drawable.radar,
            context.theme
        )!!, null
    )


    var mOnMenuClickedListener: OnMenuClickedListener? = null

    private val animatorListener = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            //将mAngle转为0 - 360范围内
            mAngle += 360.0
            mAngle %= 360.0
            destAngle += 360.0
            destAngle %= 360.0
        }
    }
    private val animatorUpdateListener =
        ValueAnimator.AnimatorUpdateListener { animation ->
            mAngle = (animation.animatedValue as Float).toDouble()
            invalidate()
        }


    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        mPaint.apply {
            isAntiAlias = true
            textSize = mTextSizePx.toFloat()
        }
        globalAnimator = ValueAnimator.ofFloat(0f, 360f).setDuration(5 * 1000)
        globalAnimator!!.apply {
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = LinearInterpolator()
            addUpdateListener {
                globalAngle = it.animatedValue as Float
                invalidate()
            }
        }
        globalAnimator1 = ValueAnimator.ofFloat(0f, 360f).setDuration(4 * 1000)
        globalAnimator1!!.apply {
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = LinearInterpolator()
            addUpdateListener {
                globalAngle1 = it.animatedValue as Float
                invalidate()
            }
        }
        globalAnimator!!.start()
        globalAnimator1!!.start()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            //判断是否点击了中心按钮
            val region: Region = Region()
            //构造一个区域对象。
            val r = RectF()
            if (showMode == ShowMode.GLOBAL) {
                mExternCirclePath.computeBounds(r, true)
                region.setPath(
                    mExternCirclePath, Region(
                        r.left.toInt(), r.top.toInt(),
                        r.right.toInt(), r.bottom.toInt()
                    )
                )
                if (region.contains(
                        event.x.toInt(),
                        event.y.toInt()
                    )
                ) {
                    setShowMode(ShowMode.BUTTON)
                    return true
                }
            } else {

                //计算path的边界
                innerButtonPath?.computeBounds(r, true)
                region.setPath(
                    innerButtonPath, Region(
                        r.left.toInt(), r.top.toInt(),
                        r.right.toInt(), r.bottom.toInt()
                    )
                )
                if (region.contains(event.x.toInt(), event.y.toInt())) {
                    mOnMenuClickedListener?.onCenterButtonClicked()
                    return true
                }

                mMenus.forEach {
                    if (it.buttonPath != null) {
                        //构造一个区域对象。
                        val r = RectF()
                        //计算path的边界
                        it.buttonPath!!.computeBounds(r, true)
                        region.setPath(
                            it.buttonPath!!, Region(
                                r.left.toInt(), r.top.toInt(),
                                r.right.toInt(), r.bottom.toInt()
                            )
                        )
                        if (region.contains(event.x.toInt(), event.y.toInt())) {
                            checkMenu(it, 1)
                            return true
                        }
                    }
                    //计算path的边界
                    it.path.computeBounds(r, true)
                    region.setPath(
                        it.path, Region(
                            r.left.toInt(), r.top.toInt(),
                            r.right.toInt(), r.bottom.toInt()
                        )
                    )
                    if (region.contains(event.x.toInt(), event.y.toInt())) {
                        checkMenu(it, 0)
                        return true
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * @param clickWhat 点击的是什么？0：Menu，1：menu上的“列表”按钮
     */
    private fun checkMenu(menu: Menu, clickWhat: Int) {
        val index = mMenus.indexOf(menu)
        if (index == checkedIndex) {    //如果点击了相同的，则应该取消选择
            menu.isChecked = !menu.isChecked
            invalidate()
            if (menu.isChecked) {
                if (clickWhat == 0) mOnMenuClickedListener?.onMenuClicked(menu, checkedIndex)
                else if (clickWhat == 1) mOnMenuClickedListener?.onMenuButtonClicked(
                    menu,
                    checkedIndex
                )
            } else
                mOnMenuClickedListener?.onCancelCheckMenu(menu, index)
            return
        } else {
            //改变背景色
            for (mMenu in mMenus) {
                mMenu.isChecked = mMenu == menu
            }
            invalidate()
            if (checkedIndex < 0) checkedIndex = 0
            if (abs(index - checkedIndex) == 2) {
                destAngle += 180
            } else if (index - checkedIndex == 1 || checkedIndex - index == 3) {
                destAngle -= 90
            } else if (checkedIndex - index == 1 || index - checkedIndex == 3) {
                destAngle += 90
            }

            //正在旋转时移除监听并重新开始动画
            if (animator != null && animator!!.isRunning) {
                animator!!.removeAllUpdateListeners()
                animator!!.removeAllListeners()
                animator!!.cancel()
            }
            animator = ValueAnimator.ofFloat(mAngle.toFloat(), destAngle.toFloat()).setDuration(300)
            animator!!.interpolator = LinearInterpolator()
            animator!!.addUpdateListener(animatorUpdateListener)
            animator!!.addListener(animatorListener)
            checkedIndex = mMenus.indexOf(menu)
            animator!!.start()

            if (clickWhat == 0) mOnMenuClickedListener?.onMenuClicked(menu, checkedIndex)
            else if (clickWhat == 1) mOnMenuClickedListener?.onMenuButtonClicked(menu, checkedIndex)
        }
    }


    override fun onDraw(canvas: Canvas) {
        if (showMode == ShowMode.GLOBAL) {
            val destRect = Rect(0, 0, measuredWidth, measuredHeight)
            //画外圆
            mPaint.style = Paint.Style.FILL
            mPaint.color = Color.parseColor("#012f5e")
            mExternCirclePath.reset()
            mExternCirclePath.addCircle(
                (measuredWidth / 2).toFloat(),
                (measuredHeight / 2).toFloat(), (measuredWidth / 2).toFloat(), Path.Direction.CW
            )
            canvas.drawPath(mExternCirclePath, mPaint)
            //画背景
            canvas.drawBitmap(mBackgroundBitmap, null, destRect, mPaint)
            //画图标
            val height = measuredHeight / 2
            val width = mBackground2Bitmap.width.toFloat() / mBackground2Bitmap.height * height
            val destRectIcon = Rect(
                (measuredWidth / 2 - width / 2).toInt(),
                measuredHeight / 2 - height / 2,
                (measuredWidth / 2 + width / 2).toInt(),
                measuredHeight / 2 + height / 2
            )
            canvas.drawBitmap(mBackground2Bitmap, null, destRectIcon, mPaint)
            //画背景
            canvas.save()
            canvas.rotate(
                globalAngle,
                (measuredWidth / 2).toFloat(),
                (measuredHeight / 2).toFloat()
            )
            canvas.drawBitmap(mBackground3Bitmap, null, destRect, mPaint)
            canvas.restore()
            canvas.save()
            canvas.rotate(
                globalAngle1,
                (measuredWidth / 2).toFloat(),
                (measuredHeight / 2).toFloat()
            )
            canvas.drawBitmap(mBackground4Bitmap, null, destRect, mPaint)
            canvas.restore()
        } else if (showMode == ShowMode.BUTTON) {
            //画外圆
            mPaint.style = Paint.Style.FILL
            mPaint.color = Color.parseColor("#012f5e")
            canvas.drawCircle(
                (measuredWidth / 2).toFloat(),
                (measuredHeight / 2).toFloat(), (measuredWidth / 2).toFloat(), mPaint
            )
            mPaint.style = Paint.Style.STROKE
            mPaint.strokeWidth = 2f
            mPaint.color = Color.parseColor("#05aee0")
            canvas.drawCircle(
                (measuredWidth / 2).toFloat(),
                (measuredHeight / 2).toFloat(), (measuredWidth / 2).toFloat(), mPaint
            )

            //画menu
            var center: PointF
            mMenus.forEach {
                if (it.isChecked) {
                    mPaint.style = Paint.Style.FILL
                } else {
                    mPaint.style = Paint.Style.STROKE
                }
                val index = mMenus.indexOf(it)
                center = calArcCenter(index)
                val path = Path()
                path.moveTo(center.x, center.y)

                path.lineTo(
                    (center.x - mInnerCircleRadius * cos((90 * index + mAngle) / 360 * 2 * PI)).toFloat(),
                    (center.y + mInnerCircleRadius * sin((90 * index + mAngle) / 360 * 2 * PI)).toFloat()
                )
                path.arcTo(
                    center.x - mInnerCircleRadius,
                    center.y - mInnerCircleRadius,
                    center.x + mInnerCircleRadius,
                    center.y + mInnerCircleRadius,
                    (180 - 90 * index - mAngle).toFloat(),
                    90f, false
                )
                path.close()
                canvas.drawPath(path, mPaint)
                it.path.set(path)
                it.center.set(center)
            }

            //画文字
            mMenus.forEach {
                mPaint.color = Color.WHITE
                val location = calTextLocation(it)
                it.textLocation.set(location)

                val fontMetrics = mPaint.fontMetrics
                val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
                var baseline = (location.bottom - location.top) / 4 + distance
                canvas.drawText(
                    it.text,
                    0,
                    it.text.length,
                    location.left,
                    location.top + baseline,
                    mPaint
                )

                //画圆角矩形
                mPaint.style = Paint.Style.FILL
                mPaint.color = Color.parseColor("#012f5e")
                val roundRectTop = location.top + (location.bottom - location.top) / 2
                it.buttonPath = Path()
                it.buttonPath!!.addRoundRect(
                    location.left,
                    roundRectTop,
                    location.right,
                    location.bottom,
                    (it.textBound.bottom / 2).toFloat(),
                    (it.textBound.bottom / 2).toFloat(), Path.Direction.CW
                )
                canvas.drawPath(it.buttonPath!!, mPaint)
                mPaint.style = Paint.Style.STROKE
                mPaint.color = Color.WHITE
                canvas.drawPath(it.buttonPath!!, mPaint)

                //画圆角矩形里面的文字
                baseline = (location.bottom + roundRectTop) / 2 + distance
                val text = "列表"
                mPaint.getTextBounds(text, 0, 2, mRect)
                canvas.drawText(
                    text,
                    0,
                    2,
                    location.right - (mRect.right - mRect.left) - it.textBound.bottom / 2,
                    baseline,
                    mPaint
                )
            }

            //画中心的按钮
            //找到所有menu左侧menu的right、右侧menu的left、上方menu的bottom、下方menu的top
            var right = Float.MIN_VALUE
            var bottom = Float.MIN_VALUE
            var left = Float.MAX_VALUE
            var top = Float.MAX_VALUE
            mMenus.forEach {
                right = max(it.textLocation.left, right)
                bottom = max(it.textLocation.top, bottom)
                left = min(it.textLocation.right, left)
                top = min(it.textLocation.bottom, top)
            }
            if (mInnerButtonRadius == 0f)
                mInnerButtonRadius = min((right - left) , (bottom - top))

            innerButtonPath = Path()
            innerButtonPath!!.addCircle(
                (measuredWidth / 2).toFloat(),
                (measuredHeight / 2).toFloat(), mInnerButtonRadius, Path.Direction.CW
            )
            mPaint.style = Paint.Style.FILL
            mPaint.color = Color.parseColor("#2d8cf0")
            canvas.drawPath(innerButtonPath!!, mPaint)
            val toolImg = drawableToBitmap(
                ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.ic_tool,
                    context.theme
                )!!, Color.WHITE
            )
            canvas.drawBitmap(
                toolImg, null,
                Rect(
                    (measuredWidth / 2 - mInnerButtonRadius / 4 * 3).toInt(),
                    (measuredHeight / 2 - mInnerButtonRadius / 4 * 3).toInt(),
                    (measuredWidth / 2 + mInnerButtonRadius / 4 * 3).toInt(),
                    (measuredHeight / 2 + mInnerButtonRadius / 4 * 3).toInt()
                ),
                mPaint
            )
        }

    }

    private fun drawableToBitmap(drawable: Drawable, @ColorInt color: Int?): Bitmap {
        val drawableWrap = DrawableCompat.wrap(drawable).mutate()
        // 获取 drawable 长宽
        val width = drawableWrap.intrinsicWidth
        val heigh = drawableWrap.intrinsicHeight

        drawableWrap.setBounds(0, 0, width, heigh)

        if (color != null) {
            drawableWrap.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }

        // 获取drawable的颜色格式
        val config = if (drawableWrap.opacity != PixelFormat.OPAQUE)
            Bitmap.Config.ARGB_8888
        else
            Bitmap.Config.RGB_565
        // 创建bitmap
        val bitmap = Bitmap.createBitmap(width, heigh, config)
        // 创建bitmap画布
        val canvas = Canvas(bitmap)
        // 将drawable 内容画到画布中
        drawableWrap.draw(canvas)
        return bitmap
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var menuMaxWidth = 0
        mMenus.forEach {
            mPaint.getTextBounds(it.text, 0, it.text.length, mRect)
            menuMaxWidth = max(menuMaxWidth, mRect.right - mRect.left)
            it.textBound.set(
                0,
                0,
                mRect.right - mRect.left,
                mRect.bottom - mRect.top + 2 * mTextPaddingVerticalPx
            )
        }
        mInnerCircleRadius = (3 / 2.0 * menuMaxWidth).toInt()
        var diameter = 0.0
        //计算外圆的直径-->内圆的半径，两个
        diameter += mInnerCircleRadius * 2
        //menu的间隙的sqrt（mMenuMargin * mMenuMargin + mMenuMargin * mMenuMargin）
        diameter += mMenuMarginDiagonal
        diameter += mCircleMargin * 2
        setMeasuredDimension(diameter.toInt(), diameter.toInt())
    }

    fun addMenu(vararg menus: Menu) {
        mMenus.addAll(menus)
        requestLayout()
    }

    fun setTextSize(sp: Float) {
        mTextSizePx = DimenUtil.sp2px(context, sp)
        mPaint.textSize = mTextSizePx.toFloat()
        requestLayout()
    }

    fun setCircleMargin(dp: Float) {
        mCircleMargin = DimenUtil.dip2px(context, dp)
        requestLayout()
    }

    fun setTextPaddingVertical(dp: Float) {
        mTextPaddingVerticalPx = DimenUtil.dip2px(context, dp)
        requestLayout()
    }

    fun setMenuMargin(marginDp: Float) {
        val marginPx = DimenUtil.dip2px(context, marginDp).toDouble()
        mMenuMarginDiagonal = hypot(marginPx, marginPx)
        requestLayout()
    }

    /**
     * 计算每个menu内圆的中心
     */
    private fun calArcCenter(menuIndex: Int): PointF {
        val center = PointF()
        center.x =
            (measuredWidth / 2 - mMenuMarginDiagonal / 2 * sin((45 + 90 * menuIndex + mAngle) / 360 * 2 * PI)).toFloat()
        center.y =
            (measuredHeight / 2 - mMenuMarginDiagonal / 2 * cos((45 + 90 * menuIndex + mAngle) / 360 * 2 * PI)).toFloat()
        return center
    }

    /**
     * 计算每个menu的文字部分的边界
     */
    private fun calTextLocation(menu: Menu): RectF {
        val menuIndex = mMenus.indexOf(menu)
        //计算文字部分的中心
        val textCenter = PointF()
        textCenter.x =
            (menu.center.x - mInnerCircleRadius / 12 * 7 * sin((45 + 90 * menuIndex + mAngle) / 360 * 2 * PI)).toFloat()
        textCenter.y =
            (menu.center.y - mInnerCircleRadius / 12 * 7 * cos((45 + 90 * menuIndex + mAngle) / 360 * 2 * PI)).toFloat()

        val rect = RectF()
        rect.left = textCenter.x - menu.textBound.right / 2
        rect.top = textCenter.y - menu.textBound.bottom
        rect.right = textCenter.x + menu.textBound.right / 2
        rect.bottom = textCenter.y + menu.textBound.bottom
        return rect
    }


    interface OnMenuClickedListener {
        fun onMenuButtonClicked(menu: Menu, menuIndex: Int)
        fun onMenuClicked(menu: Menu, menuIndex: Int)
        fun onCenterButtonClicked()
        fun onCancelCheckMenu(menu: Menu, menuIndex: Int)
    }

    class Menu(val text: String) {
        constructor(text: String, childs: List<Child>) : this(text) {
            this.childs.addAll(childs)
        }

        var childs = mutableListOf<Child>()
        internal var isChecked = false
        internal var path = Path()
        internal var center = PointF()
        internal var textLocation = RectF()
        internal var textBound = Rect()
        internal var buttonPath: Path? = null

        class Child(val text: String)
    }

    /**
     * 设置showMode
     */
    fun setShowMode(showMode: ShowMode) {
        when(showMode){
            ShowMode.GLOBAL->{
                this.showMode = ShowMode.GLOBAL
                globalAnimator?.resume()
                globalAnimator1?.resume()
                mMenus.forEach {
                    if (it.isChecked){
                        it.isChecked = false
                        mOnMenuClickedListener?.onCancelCheckMenu(it, mMenus.indexOf(it))
                    }
                }
                invalidate()
            }
            ShowMode.BUTTON->{
                this.showMode = ShowMode.BUTTON
                globalAnimator?.pause()
                globalAnimator1?.pause()
                invalidate()
            }
        }
    }
}