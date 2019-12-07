<h3>${uiLabelMap.NewestContentTitle}</h3>
<ul>
	<#if latestNews?exists>
		<#list latestNews as news>
			 <li> <a href="#" title=""><img width="100" height="67" src="DemoImg/6-cong-dung-lam-dep-tu-qua-chanh.jpg" alt="">
		    <h4>${news.contentName}</h4>
		    <span>5 giờ trước | 23 <i class="allicon-cmt"></i></span>
		    </a></li>
		</#list>
	</#if>
</ul>