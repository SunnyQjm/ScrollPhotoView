package com.sunny.scrollphotoview

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout

/**
 * Created by sunny on 17-12-8.
 */
class ScrollPhotoView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null
                                                , defStyleAttr: Int = 0)
    : RelativeLayout(context, attrs, defStyleAttr) {

    private var viewPager: ViewPager? = null
    private var adapter: MyAdapter? = null

    var imgLoader: ((url: String, view: ImageView) -> Unit)? = null
        set(value) {
            field = value
            adapter?.imgLoader = value
        }

    var onScrollPhotoViewClickListener: OnScrollPhotoViewClickListener? = null
    var onPageChangeListener : OnPageChangeListener?= null

    init {
        initView()
    }

    private fun initView() {
        adapter = MyAdapter(context, imgLoader = imgLoader)
        adapter?.listener = object : ZoomImageView.OnZoomImageViewClickListener {
            override fun onClick(e: MotionEvent?) {
                onScrollPhotoViewClickListener?.onClick(e)
            }

            override fun onDoubleTap(e: MotionEvent?) {
                onScrollPhotoViewClickListener?.onDoubleTap(e)
            }
        }
        viewPager = ViewPager(context)
        viewPager?.adapter = adapter
        viewPager?.offscreenPageLimit = 3

        viewPager?.setOnPageChangeListener(object:ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
	              return;
	          }
	
	          override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                return;
            }
	
            override fun onPageSelected(position: Int) {
                onPageChangeListener?.onPageSelected(position);
	          }
	
	      });

        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(viewPager, layoutParams)


    }

    fun setUrls(urls: Array<String>) {
        adapter?.addUrls(urls)
        adapter?.notifyDataSetChanged()
    }

    class MyAdapter(private val context: Context,
                    private val urls: MutableList<String> = mutableListOf(),
                    var imgLoader: ((url: String, view: ImageView) -> Unit)?) : PagerAdapter() {
        private val images: MutableList<ZoomImageView> = mutableListOf()

        init {
            for (url in urls) {
                images.add(ZoomImageView(context))
            }
        }
        var listener: ZoomImageView.OnZoomImageViewClickListener? = null
        fun addUrls(urls: Array<String>) {
            urls.forEach {
                this.urls.add(it)
                images.add(ZoomImageView(context))
            }
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

        override fun getCount() = urls.size

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            //println("destroyItem")
            container.removeView(images[position])
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            //println("instantiateItem")
            val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
            if (images[position].parent != container)
                container.addView(images[position], layoutParams)
            images[position].listener = this.listener
            return images[position]
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            //println("setPrimaryItem")
            imgLoader?.invoke(urls[position], images[position])
        }
    }

    //////////////////////////////////////////
    ////////// interface
    //////////////////////////////////////////

    fun getCurPosition() = viewPager?.currentItem

    fun setCurrentItem(position: Int){
        viewPager?.currentItem = position
    }
    interface OnScrollPhotoViewClickListener{
        fun onClick(e: MotionEvent?)

        fun onDoubleTap(e: MotionEvent?)
    }
    
    interface OnPageChangeListener{
        fun onPageSelected(position: Int);
    }

}