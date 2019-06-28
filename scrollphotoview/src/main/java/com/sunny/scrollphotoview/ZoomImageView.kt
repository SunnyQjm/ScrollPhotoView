package com.sunny.scrollphotoview

import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.os.Build
import androidx.core.view.GestureDetectorCompat
import android.util.AttributeSet
import android.view.*
import android.widget.ImageView


/**
 * 一个可自如缩放的ImageView
 * Created by sunny on 17-12-9.
 */
class ZoomImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null
                                              , defStyleAttr: Int = 0)
    : ImageView(context, attrs, defStyleAttr), View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener {

    init {
        initView()
    }

    private val scaleMatrix = Matrix()
    private var mGestureDetector: GestureDetectorCompat? = null
    private var mScaleGestureDetector: ScaleGestureDetector? = null

    //第一次布局完成后获取图片的宽高，调整图片等
    private var first = true

    /**
     * 最大放大倍数
     */
    private val SCALE_MAX = 3f

    /**
     * 默认缩放
     */
    private var initScale = 1.0f

    /**
     * Matrix矩阵的9个值
     */
    private val matrixValue = FloatArray(9)

    private var mode = 0
    private val MODE_INIT = 0x01
    private val MODE_ZOOM = 0x02
    private val MODE_DRAG = 0x04

    /**
     * 是否允许缩放状态下双击恢复
     */
    var enableDoubleClickToRestore = true

    /**
     * 是否允许在原始状态下双击放大
     */
    var enableDoubleClickToScale = true

    /**
     * 原始状态下双击放大的倍数
     */
    var doubleClickFactor = 2f
        set(value) {                    //保证这个倍数大于1
            field = if(value <= 1)
                1f
            else
                value
        }

    /**
     * 拖动阻尼，越大则实际移动距离/手指移动距离越大
     */
    var dragDamping = 1.2f

    /**
     * 返回的Scale是相对于初始时候的Scale
     */
    private fun getScale(): Float {
        scaleMatrix.getValues(matrixValue)
        return matrixValue[Matrix.MSCALE_X]
    }

    interface OnZoomImageViewClickListener {
        fun onClick(e: MotionEvent?)

        fun onDoubleTap(e: MotionEvent?)
    }

    var listener: OnZoomImageViewClickListener? = null

    private fun initView() {
        mode = mode or MODE_INIT
        //println("initMode: $mode")
        mGestureDetector = GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
            /**
             * 检测单击
             */
            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                listener?.onClick(e)
                return true
            }

            override fun onDown(e: MotionEvent?): Boolean {
                if (mode and MODE_ZOOM > 0)
                    parent?.requestDisallowInterceptTouchEvent(true)
                else
                    parent?.requestDisallowInterceptTouchEvent(false)
                return super.onDown(e)
            }

            /**
             * 双击放大放小
             */
            override fun onDoubleTap(e: MotionEvent): Boolean {
                listener?.onDoubleTap(e)
                if((mode and MODE_ZOOM) == 0 && enableDoubleClickToRestore){  //处于初始状态，则放大1.5倍
                    scaleMatrix.postScale(doubleClickFactor, doubleClickFactor, e.x,e.y)
                    checkBorderAndCenterWhenScale(scaleMatrix)
                    imageMatrix = scaleMatrix
                    //切换到缩放状态
                    mode = mode or MODE_ZOOM
                } else if(enableDoubleClickToScale){        //处于放大状态，则回复到原大小
                    val targetScale = (initScale * 1.0 / getScale()).toFloat()
                    //大小恢复到初始状态
                    scaleMatrix.postScale(targetScale, targetScale)
                    checkBorderAndCenterWhenScale(scaleMatrix)
                    //清除缩放状态
                    mode = mode and MODE_ZOOM.inv()
                    imageMatrix = scaleMatrix
                }
                return true
            }

            /**
             * 检测滚动事件，用于拖拽图片
             */
            override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                //println("mode: $mode")

                //处于缩放状态，并且可拖拽
                if ((mode and MODE_DRAG > 0) && (mode and MODE_ZOOM > 0)) {
                    //println("drag: ($distanceX, $distanceY)")
                    scaleMatrix.postTranslate(-distanceX * dragDamping, -distanceY * dragDamping)
                    checkBorderAndCenterWhenScale(scaleMatrix)
                    imageMatrix = scaleMatrix
                    return true
                }
                return super.onScroll(e1, e2, distanceX, distanceY)
            }
        })

        mScaleGestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector?) {
            }

            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                val scale = getScale()
                //如果图片大小超过ImageView，认为处于缩放状态
                mode = if (scale - initScale > 0.1f)
                    mode or MODE_ZOOM
                else
                    mode and MODE_ZOOM.inv()
                var scaleFactor = mScaleGestureDetector!!.scaleFactor

                if (drawable == null || detector == null) {
                    return true
                }

                if ((scale < initScale * SCALE_MAX && scaleFactor > 1.0f)
                        || (scale > initScale && scaleFactor < 1.0f)) {
                    if (scaleFactor * scale < initScale)
                        scaleFactor = initScale / scale
                    if (scaleFactor * scale > initScale * SCALE_MAX)
                        scaleFactor = initScale * SCALE_MAX / scale
                    scaleMatrix.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
                    checkBorderAndCenterWhenScale(scaleMatrix)
                    imageMatrix = scaleMatrix
                }
                return true
            }
        })

        this.scaleType = ScaleType.MATRIX
        this.setOnTouchListener(this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
        } else {
            viewTreeObserver.removeGlobalOnLayoutListener(this)
        }
    }

    override fun onGlobalLayout() {
        if (!first)
            return
        if (drawable == null)
            return
        first = false

        //获取图片的宽高
        val imgWidth = drawable.intrinsicWidth
        val imgHeight = drawable.intrinsicHeight

        var scale = 1.0f
        //如果图片的宽度大于ImageView的宽度，但是图片的高不是
        if (imgWidth > width && imgHeight <= height) {
            scale = (width * 1.0 / imgWidth).toFloat()
        }

        if (imgWidth <= width && imgHeight > height) {
            scale = (height * 1.0 / imgHeight).toFloat()
        }
        //如果图片的宽高都大于ImageView，按比例缩小
        if (imgWidth > width && imgHeight > height)
            scale = Math.min(width * 1.0 / imgWidth, height * 1.0 / imgHeight).toFloat()

        //如果宽高都小于ImageView
        if (imgWidth < width && imgHeight < height)
            scale = Math.min(width * 1.0 / imgWidth, height * 1.0 / imgHeight).toFloat()
        //将图片移至屏幕中心
        scaleMatrix.postTranslate(((width - imgWidth) / 2.0).toFloat(), ((height - imgHeight) / 2.0).toFloat())
        scaleMatrix.postScale(scale, scale, (width / 2.0).toFloat(), (height / 2.0).toFloat())
        imageMatrix = scaleMatrix
        initScale = scale
        //println("initScale: $initScale")
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        //println("pointerCount: ${event?.pointerCount}")
        mode = if (event?.pointerCount ?: 0 <= 1)
            mode or MODE_DRAG
        else
            mode and MODE_DRAG.inv()
        if (mGestureDetector?.onTouchEvent(event) == true) {
            return true
        }
        return mScaleGestureDetector!!.onTouchEvent(event)        //缩放监听
    }

    /**
     * 在缩放的时候检测图片边界
     */
    private fun checkBorderAndCenterWhenScale(mMatrix: Matrix) {
        val rectF = RectF()

        if (drawable != null) {
            rectF.set(0f, 0f, drawable.intrinsicWidth.toFloat()
                    , drawable.intrinsicHeight.toFloat())
            mMatrix.mapRect(rectF)
        }
        var deltaX = 0f
        var deltaY = 0f

        //如果宽或高大于屏幕，则控制范围
        if (rectF.width() >= width) {
            if (rectF.left > 0)
                deltaX = -rectF.left
            if (rectF.right < width)
                deltaX = width - rectF.right
        }
        if (rectF.height() >= height) {
            if (rectF.top > 0)
                deltaY = -rectF.top
            if (rectF.bottom < height)
                deltaY = height - rectF.bottom
        }

        //如果宽或高小鱼屏幕，让其居中
        if (rectF.width() < width) {
            deltaX = width * 0.5f - rectF.right + 0.5f * rectF.width();
        }
        if (rectF.height() < height) {
            deltaY = height * 0.5f - rectF.bottom + 0.5f * rectF.height();
        }

        mMatrix.postTranslate(deltaX, deltaY)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }


}