package com.jy.switchview

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart

class SwitchView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet,
    def: Int = 0
) : View(context, attr, def) {

    companion object {
        private const val DEFAULT_DURATION = 300L
        private const val DEFAULT_CIRCLE_COLOR = Color.WHITE
        private const val DEFAULT_CIRCLE_MARGIN = 6
        private val DEFAULT_WIDTH = dp2px(38)
        private val DEFAULT_HEIGHT = dp2px(22)
        private val DEFAULT_SWITCH_OFF_COLOR = Color.parseColor("#b2d9ff")
        private val DEFAULT_SWITCH_ON_COLOR = Color.parseColor("#185bff")
        private fun dp2px(value: Int) =
            (value * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
    }

    //开关关闭状态的背景颜色
    @ColorInt
    private var mSwitchOffColor: Int = DEFAULT_SWITCH_OFF_COLOR

    //开关开启状态的背景颜色
    @ColorInt
    private var mSwitchOnColor: Int = DEFAULT_SWITCH_ON_COLOR

    //圆圈背景颜色
    @ColorInt
    private var mCircleColor: Int = DEFAULT_CIRCLE_COLOR

    //开关宽度
    private var mSwiWidth: Int = DEFAULT_WIDTH

    //开关高度
    private var mSwiHeight: Int = DEFAULT_HEIGHT

    //圆圈距离父布局的margin值
    private var mCircleMargin: Int = DEFAULT_CIRCLE_MARGIN

    //圆圈半径
    private var mCircleR: Int = mSwiHeight / 2 + 2 * mCircleMargin

    //圆圈随动画位移距离父布局的值
    private var mCircleTranslationX: Float = 0f

    //圆圈画笔
    private val mCirclePaint = Paint()

    //开关画笔
    private val mSwitchPaint = Paint()

    //开关状态
    private var isChecked = false

    //是否正在进行动画
    private var isAnimating = false

    //状态切换回调
    private var iCheckChangedListener: ICheckChangedListener? = null

    init {
        initAttr(attr, def)
        initPaint()
    }

    private fun initAttr(attr: AttributeSet, defStyleAttr: Int) {
        context.obtainStyledAttributes(
            attr,
            R.styleable.SwitchView,
            defStyleAttr,
            0
        ).apply {
            mCircleColor =
                getColor(R.styleable.SwitchView_switch_circle_color, DEFAULT_CIRCLE_COLOR)
            mCircleMargin =
                getDimensionPixelOffset(R.styleable.SwitchView_switch_circle_margin, mCircleMargin)
            mCircleR = getDimensionPixelOffset(R.styleable.SwitchView_switch_circle_r, mCircleR)
            mSwitchOnColor = getColor(R.styleable.SwitchView_switch_on_color, mSwitchOnColor)
            mSwitchOffColor = getColor(R.styleable.SwitchView_switch_off_color, mSwitchOffColor)
            isChecked = getBoolean(R.styleable.SwitchView_switch_checked, false)
            recycle()
        }
    }

    private fun initPaint() {
        mSwitchPaint.apply {
            style = Paint.Style.FILL
            isAntiAlias = true
            color = if (isChecked) mSwitchOnColor else mSwitchOffColor
        }
        mCirclePaint.apply {
            style = Paint.Style.FILL
            isAntiAlias = true
            color = mCircleColor
        }
    }

    fun setICheckChanged(iCheckChangedListener: ICheckChangedListener) {
        this.iCheckChangedListener = iCheckChangedListener
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        //宽高分别为 match_parent or 指定大小
        if (widthMode == MeasureSpec.EXACTLY) {
            mSwiWidth = widthSize
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mSwiHeight = heightSize
        }

        //防止二次测量onMeasure()时mCircleR再次/2
        if (mCircleR * 2 + mCircleMargin * 2 > mSwiHeight) {
            mCircleR = (mSwiHeight - mCircleMargin * 2) / 2
        } else {
            mCircleMargin = (mSwiHeight - mCircleR * 2) / 2
        }

        mCircleTranslationX =
            if (isChecked) mSwiWidth.toFloat() - mCircleMargin * 2 - mCircleR * 2 else 0f

        setMeasuredDimension(mSwiWidth, mSwiHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRoundRect(
            0f,
            0f,
            mSwiWidth.toFloat(),
            mSwiHeight.toFloat(),
            mSwiHeight.toFloat() / 2,
            mSwiHeight.toFloat() / 2,
            mSwitchPaint
        )

        canvas.drawCircle(
            mCircleTranslationX + mCircleR.toFloat() + mCircleMargin,
            mCircleR.toFloat() + mCircleMargin,
            mCircleR.toFloat(),
            mCirclePaint
        )
    }

    @SuppressLint("Recycle")
    private fun startAnim(toOn: Boolean) {

        val start = if (toOn) 0f else mSwiWidth.toFloat() - mCircleMargin * 2 - mCircleR * 2
        val end = if (toOn) mSwiWidth.toFloat() - mCircleMargin * 2 - mCircleR * 2 else 0f
        val curColor = mSwitchPaint.color
        val targetColor = if (toOn) mSwitchOnColor else mSwitchOffColor

        val transXAnimator =
            ValueAnimator.ofFloat(start, end).apply {
                addUpdateListener {
                    mCircleTranslationX = it.animatedValue as Float
                    invalidate()
                }
            }

        val colorAnimator =
            ValueAnimator.ofArgb(curColor, targetColor).apply {
                addUpdateListener {
                    mSwitchPaint.color = it.animatedValue as Int
                }
            }

        AnimatorSet().apply {
            playTogether(transXAnimator, colorAnimator)
            duration = DEFAULT_DURATION
            doOnStart {
                isAnimating = true
            }
            doOnEnd {
                isAnimating = false
                isChecked = !isChecked
                iCheckChangedListener?.onCheckChanged(isChecked)
            }
            start()
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_UP -> {
                if (!isAnimating) {
                    startAnim(!isChecked)
                }
            }
        }
        return super.onTouchEvent(event)
    }

    interface ICheckChangedListener {
        fun onCheckChanged(isChecked: Boolean)
    }

}