package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
//    private var animator: ValueAnimator = ValueAnimator.ofInt(0, 100)
    var buttonAnimator = ValueAnimator.ofFloat(0F, 100F).apply {
        duration = 1000
        addUpdateListener { valueAnimator ->
            progressArc = valueAnimator.animatedValue as Float
            valueAnimator.repeatCount = ValueAnimator.INFINITE
            valueAnimator.interpolator = LinearInterpolator()
            this@LoadingButton.invalidate() // -> Important
        }
        start()
    }

    private var progress: Float = 0.0f
    private var progressArc: Float = 0.0f
    var colorButton: String?
    var colorText: String?

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0, 0).apply {

            try {
                colorText = getString(R.styleable.LoadingButton_colorText)
                colorButton = getString(R.styleable.LoadingButton_colorButton)
            } finally {
                recycle()
            }
        }

    }

    fun setProgressValue(progress: Float) {
        buttonState = ButtonState.Loading
//        animator.start()
        this.progress = progress
        invalidate()
    }

    fun setDownloadComplete() {
//        animator.cancel()
        buttonState = ButtonState.Completed
    }


    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private val rect = RectF(
        740f,
        50f,
        810f,
        110f
    )

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.strokeWidth = 0f
        paint.color = Color.parseColor("#00A5D9")
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        if (buttonState == ButtonState.Loading) {
            Log.i("custom-button", width.toString())
            Log.i("custom-calc", (90/100).toFloat().toString())
            paint.color = Color.parseColor(colorButton)
            canvas.drawRect(
                0f, 0f,
                (width * (progress / 100)), height.toFloat(), paint
            )
            paint.color = if (progress < 50) Color.BLACK else Color.parseColor(colorText)
            canvas.drawArc(rect, 0f, (360 * (progressArc / 100)), true, paint)
        }

        val buttonText =
            if (buttonState == ButtonState.Loading)
                "Loading"
            else "Download"
        paint.color = Color.BLACK
        canvas.drawText(buttonText, (width / 2).toFloat(), ((height + 30) / 2).toFloat(),
            paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }
}