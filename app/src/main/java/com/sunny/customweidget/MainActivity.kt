package com.sunny.customweidget

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Glide.with(this)
                .load("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1713583005,1596471499&fm=27&gp=0.jpg")
                .into(imageView)
        tv?.linksClickable = true
        tv?.movementMethod = LinkMovementMethod.getInstance()
        tv?.text = Html.fromHtml("<h4>更新内容：</h4>\n" +
                "<p>1.支持发帖时添加图片；\n" +
                "<br>2.新增匿名评论与私信匿名发帖人功能；\n" +
                "<br>3.新增举报与删帖功能，净化卧谈会信息</p>\n" +
                "<h4>下载链接：</h4>\n" +
                "<a href=\"http://android.myapp.com/myapp/detail.htm?apkName=com.stu.tool\">应用宝下载地址</a>\n" +
                "<br><a href=\"http://openbox.mobilem.360.cn/index/d/sid/2356297 \">360手机助手下载地址</a>")
        spv?.setUrls(arrayOf(
                "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1713583005,1596471499&fm=27&gp=0.jpg",
                "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1405374724,1615888454&fm=11&gp=0.jpg",
                "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2045639695,3778480851&fm=11&gp=0.jpg"))

    }
}
