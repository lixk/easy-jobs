# dynamic-schedule
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
	<img src="https://raw.githubusercontent.com/lixk/dynamic-schedule/master/screenshot/%E7%A8%8B%E5%BA%8F%E7%9B%B4%E6%8E%A5%E8%B0%83%E7%94%A8%E6%96%B9%E5%BC%8F.png" alt="" />
	界面管理的方式:
	<img src="https://raw.githubusercontent.com/lixk/dynamic-schedule/master/screenshot/web%E7%AE%A1%E7%90%86%E7%95%8C%E9%9D%A2.png" alt="" />
	<img src="https://raw.githubusercontent.com/lixk/dynamic-schedule/master/screenshot/%E6%B7%BB%E5%8A%A0%E5%AE%9A%E6%97%B6%E4%BB%BB%E5%8A%A1.png" alt="" />
	<img src="https://raw.githubusercontent.com/lixk/dynamic-schedule/master/screenshot/%E4%BF%AE%E6%94%B9%E5%AE%9A%E6%97%B6%E4%BB%BB%E5%8A%A1.png" alt="" /><br />
</p>

<p>
	<span style="font-size:18px; color:#ff0000"><strong>注意：本项目采用JDK1.8+springboot开发，本地调试请确认JDK版本不低于1.8</strong></span>
</p>
<p>
	<br />
	
</p>