package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private val valueAnimator = ValueAnimator()
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }

    init {
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
        paint.color = Color.BLUE
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        buttonState = ButtonState.Loading
        if (buttonState == ButtonState.Loading) {
            Log.i("custom-button", "executing")
            paint.color = Color.BLACK
            canvas.drawRect(
                0f, 0f,
                (width * (90 / 100)).toFloat(), height.toFloat(), paint
            )
//            paint.color = Color.parseColor("#F9A825")
//            canvas.drawArc(rect, 0f, (360 * (20 / 100)).toFloat(), true, paint)
        }
        val buttonText = "Loading1"
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