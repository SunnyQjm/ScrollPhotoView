package com.example.demo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import com.bumptech.glide.Glide
import com.sunny.scrollphotoview.ScrollPhotoView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spv.setUrls(
                arrayOf("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1514351983845&di=3eb9096b6c38dfa82d14c26a65ea032f&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimgad%2Fpic%2Fitem%2F0d338744ebf81a4cb91b80f5dc2a6059252da6e5.jpg",
                        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1514351983844&di=a7c7ac553b3e1552c77f168f9ff792c1&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimgad%2Fpic%2Fitem%2F314e251f95cad1c88870b61a743e6709c83d51c7.jpg",
                        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1514351983844&di=d38c274fabf665695b9be5aa9372aad1&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimage%2Fc0%253Dshijue1%252C0%252C0%252C294%252C40%2Fsign%3D183f043fb6315c60579863ace5d8a166%2F35a85edf8db1cb13b1959439d754564e92584b18.jpg")
        )
        spv.imgLoader = {
            url, view ->
            Glide.with(this)
                    .load(url)
                    .into(view)
        }

        spv.onScrollPhotoViewClickListener = object : ScrollPhotoView.OnScrollPhotoViewClickListener{
            override fun onDoubleTap(e: MotionEvent?) {
            }

            override fun onClick(e: MotionEvent?) {

            }

        }


    }
}
