# dynamic-schedule
<p>
	最近项目里需要添加定时任务，定时任务要求可以动态控制，虽然quartz和spring Schedule Task可以实现，但是感觉不够灵活，简单。于是，索性自己实现一个。
</p>
<p>
	本Java 动态定时器基于Java的定时器线程池，阻塞队列实现，定时调度时间采用cron表达式配置的方式，其中cron表达式解析工具类提取自spring。&nbsp;
</p>
<p>
	用法极其简单，只需要将ScheduleUtils工具类复制到项目里，然后调用ScheduleUtils.add()和ScheduleUtils.cancel()方法即可实现定时任务的添加和关闭。
</p>
<p>
	也可以采用界面管理的方式，用法如下图所示：
</p>
<p>
	<img src="http://img.blog.csdn.net/20170830182524425?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMzMxNDc4Ng==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center" alt="" />
	界面管理的方式:
	<img src="http://img.blog.csdn.net/20170830182610449?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMzMxNDc4Ng==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center" alt="" />
	<img src="http://img.blog.csdn.net/20170830182624637?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMzMxNDc4Ng==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center" alt="" />
	<img src="http://img.blog.csdn.net/20170830182638022?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMzMxNDc4Ng==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center" alt="" /><br />
	
</p>

<p>
	<span style="font-size:18px; color:#ff0000"><strong>注意：本项目采用JDK1.8+springboot开发，本地调试请确认JDK版本不低于1.8</strong></span>
</p>
<p>
	<br />
	
</p>