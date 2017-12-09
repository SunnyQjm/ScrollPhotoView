package com.sunny.scrollphotoview

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide

/**
 * Created by sunny on 17-12-8.
 */
class ScrollPhotoView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null
                                                , defStyleAttr: Int = 0)
    : RelativeLayout(context, attrs, defStyleAttr) {

    private var viewPager: ViewPager? = null
    private var adapter: MyAdapter? = null

    var imgLoader: ((url: String, view: ImageView) -> Unit)? = null

    init {
        initView()
    }

    private fun initView() {
        adapter = MyAdapter(context, imgLoader = imgLoader)
        viewPager = ViewPager(context)
        viewPager?.adapter = adapter
        viewPager?.offscreenPageLimit = 3
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

        private val images: MutableList<ImageView> = mutableListOf()

        init {
            for (url in urls) {
                images.add(ZoomImageView(context))
            }
        }

        fun addUrls(urls: Array<String>) {
            urls.forEach {
                this.urls.add(it)
                images.add(ZoomImageView(context))
            }
        }

        override fun isViewFromObject(view: View?, `object`: Any?) = view == `object`

        override fun getCount() = urls.size

        override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
            println("destroyItem")
            container?.removeView(images[position])
        }

        override fun instantiateItem(container: ViewGroup?, position: Int): Any {
            println("instantiateItem")
            val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
            if (images[position].parent != container)
                container?.addView(images[position], layoutParams)
            return images[position]
        }

        override fun setPrimaryItem(container: ViewGroup?, position: Int, `object`: Any?) {
            println("setPrimaryItem")
            imgLoader?.invoke(urls[position], images[position])
        }
    }

}