package com.sunny.scrollphotoview

import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.os.Build
import android.support.v4.view.GestureDetectorCompat
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

    //第一次布局完成后获取图片的宽搞，调整图片等
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

    /**
     * 返回的Scale是相对于初始时候的Scale
     */
    private fun getScale(): Float{
        scaleMatrix.getValues(matrixValue)
        return matrixValue[Matrix.MSCALE_X]
    }

    interface OnZoomImageViewClickListener{
        fun onClick(e: MotionEvent?)

        fun onDoubleTap(e: MotionEvent?)
    }

    var listener: OnZoomImageViewClickListener? = null

    private fun initView() {
        mGestureDetector = GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
            /**
             * 检测单击
             */
            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                listener?.onClick(e)
                return true
            }

            /**
             * 双击放大放小
             */
            override fun onDoubleTap(e: MotionEvent?): Boolean {
                listener?.onDoubleTap(e)
                return true
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
                var scaleFactor = mScaleGestureDetector!!.scaleFactor

                if (drawable == null || detector == null) {
                    return true
                }

                if((scale < initScale * SCALE_MAX && scaleFactor > 1.0f)
                        || (scale > initScale && scaleFactor < 1.0f)){
                    if(scaleFactor * scale < initScale)
                        scaleFactor = initScale / scale
                    if(scale * scale > SCALE_MAX)
                        scaleFactor = SCALE_MAX / scale
                    scaleMatrix.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
                    checkBorderAndCenterWhenScale()
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
        if(!first)
            return
        if(drawable == null)
            return
        first = false

        //获取图片的宽高
        val imgWidth = drawable.intrinsicWidth
        val imgHeight = drawable.intrinsicHeight

        var scale = 1.0f
        //如果图片的宽度大于ImageView的宽度，但是图片的高不是
        if(imgWidth > width && imgHeight <= height){
            scale = (width * 1.0 / imgWidth).toFloat()
        }

        if(imgWidth <= width && imgHeight > height){
            scale = (height * 1.0 / imgHeight).toFloat()
        }
        //如果图片的宽高都大于ImageView，按比例缩小
        if(imgWidth > width && imgHeight > height)
            scale = Math.min(imgWidth * 1.0 / width, imgHeight * 1.0 / height).toFloat()

        //将图片移至屏幕中心
        scaleMatrix.postTranslate(((width - imgWidth) / 2.0).toFloat(), (height - imgHeight).toFloat())
        scaleMatrix.postScale(scale, scale, (width / 2.0).toFloat(), (height / 2.0).toFloat())
        imageMatrix = scaleMatrix
        initScale = scale
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if(mGestureDetector?.onTouchEvent(event) == true){
            return true
        }
        return mScaleGestureDetector!!.onTouchEvent(event)        //缩放监听
    }

    private fun checkBorderAndCenterWhenScale(){
        val matrix = scaleMatrix
        val rectF = RectF()

        if(drawable != null) {
            rectF.set(0f, 0f, drawable.intrinsicWidth.toFloat()
                    , drawable.intrinsicHeight.toFloat())
            matrix.mapRect(rectF)
        }
        var deltaX = 0f
        var deltaY = 0f

        //如果宽或高大于屏幕，则控制范围
        if(rectF.width() >= width){
            if(rectF.left > 0)
                deltaX = -rectF.left
            if(rectF.right < width)
                deltaX = width - rectF.right
        }
        if(rectF.height() >= height){
            if(rectF.top > 0)
                deltaY = - rectF.top
            if(rectF.bottom < height)
                deltaY = height - rectF.bottom
        }

        //如果宽或高小鱼屏幕，让其居中
        if (rectF.width() < width) {
            deltaX = width * 0.5f - rectF.right + 0.5f * rectF.width();
        }
        if (rectF.height() < height) {
            deltaY = height * 0.5f - rectF.bottom + 0.5f * rectF.height();
        }
        scaleMatrix.postTranslate(deltaX, deltaY)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

}