# ScrollPhotoView
[![](https://jitpack.io/v/SunnyQjm/ScrollPhotoView.svg)](https://jitpack.io/#SunnyQjm/ScrollPhotoView)

> 简介：这是一个可以滚动的Viewpager视图，里面内嵌了一个可以缩放，拖拽的ImageView视图。
* 图片加载是会自适应大小和位置
* 提供单击和双击回调（双击时图片会回到初始大小）

> ### 演示
![演示](https://github.com/SunnyQjm/ScrollPhotoView/blob/master/presentatin.gif?raw=true)
> ### 使用

* step 1: Add it in your root build.gradle at the end of repositories:
	```gradle
	allprojects {
		repositories {
			maven { url 'https://jitpack.io' }
		}
	}
	```
* Step 2: Add the dependency
	```gradle
	dependencies {
	        implementation 'com.github.SunnyQjm:ScrollPhotoView:1.1.2'
	}
	```

* Step 3 : 在布局中使用
    ```xml
    <com.sunny.scrollphotoview.ScrollPhotoView
	    android:id="@+id/spv"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent">
    </com.sunny.scrollphotoview.ScrollPhotoView>
    ```
    
* Step 4 : 在代码中设置
	* 设置显示的图片集合（传入一个string数组，可以是url，也可以是图片路径，都在下面的回调中自行处理）
	    ```java
	    spv.setUrls(arrayOf("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1514351983845&di=3eb9096b6c38dfa82d14c26a65ea032f&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimgad%2Fpic%2Fitem%2F0d338744ebf81a4cb91b80f5dc2a6059252da6e5.jpg","https://timgsa.baidu.com/timgimage&quality=80&size=b9999_10000&sec=1514351983844&di=a7c7ac553b3e1552c77f168f9ff792c1&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimgad%2Fpic%2Fitem%2F314e251f95cad1c88870b61a743e6709c83d51c7.jpg","https://timgsa.baidu.com/timgimage&quality=80&size=b9999_10000&sec=1514351983844&di=d38c274fabf665695b9be5aa9372aad1&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimage%2Fc0%253Dshijue1%252C0%252C0%252C294%252C40%2Fsign%3D183f043fb6315c60579863ace5d8a166%2F35a85edf8db1cb13b1959439d754564e92584b18.jpg"))
	    ```
	* 设置回调加载（必须设置，建议用Glide来加载，可加载网络图片，本地图片，gif等）
	    ```kotlin
	    spv.imgLoader = {
                url, view ->
                Glide.with(this)
                        .load(url)
                        .into(view)
            }
	    ```
	    
	* 设置点击回调，支持单击和双击
	    
	    ```kotlin
	    
	    spv.onScrollPhotoViewClickListener = object : ScrollPhotoView.OnScrollPhotoViewClickListener{
                override fun onDoubleTap(e: MotionEvent?) {
                }
                override fun onClick(e: MotionEvent?) {
                }
            }
	    ```
