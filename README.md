# ScrollPhotoView
[![](https://jitpack.io/v/SunnyQjm/ScrollPhotoView.svg)](https://jitpack.io/#SunnyQjm/ScrollPhotoView)

> 简介：这是一个可以滚动的Viewpager视图，里面内嵌了一个可以缩放，拖拽的ImageView视图。
* 图片加载是会自适应大小和位置
* 提供单击和双击回调（双击时图片会回到初始大小）

> ### 使用

* step 1: Add it in your root build.gradle at the end of repositories:
	```gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	```
* Step 2. Add the dependency
	``gradle
	dependencies {
	        compile 'com.github.SunnyQjm:ScrollPhotoView:v1.0.3'
	}
	```
