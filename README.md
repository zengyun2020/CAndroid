# CAndroid
一款由crazychen开发的android开发框架，由依赖注入，网络请求，orm框架等部分组成。用于加速android快速开发，目前还在不断完善中。

ViewUtil
这个注解库是使用的编译期依赖注入，所以几乎不会带来反射造成性能损失
目前实现了
contentView,onClick(各种事件),viewById等注解功能
使用时，要将Annotation Processing勾等选上（怎么使用编译器注解功能，可以自行百度或者查看我的csdn博客）

DBUtil

HttpUtil
实现文件下载,图像下载,字符串数据请求等功能
