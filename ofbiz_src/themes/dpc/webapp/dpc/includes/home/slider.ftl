<#if Static["org.ofbiz.base.util.UtilValidate"].isNotEmpty(mainSlide)>
<aside class="homebanner">
	<div data-ride="carousel" class="carousel slide" id="myCarousel">
		<ol class="carousel-indicators">
			<#assign active = "active"/>
			<#list mainSlide as slide>
				<li data-slide-to="${slide_index}" data-target="#myCarousel" class="${active?if_exists}"></li>
			<#assign active = ""/>
			</#list>
		</ol>
		<div class="carousel-inner">
			<#assign active = "active"/>
			<#list mainSlide as slide>
				<div class="item ${active?if_exists}">
					<img width="760" height="300" title="${StringUtil.wrapString((slide.description)?if_exists)}" alt="${StringUtil.wrapString((slide.description)?if_exists)}" src="${StringUtil.wrapString((slide.originalImageUrl)?if_exists)}"/>
				</div>
			<#assign active = ""/>
			</#list>
		</div>
		<a data-slide="prev" href="#myCarousel" class="left carousel-control"><span class="glyphicon glyphicon-chevron-left"></span></a>
		<a data-slide="next" href="#myCarousel" class="right carousel-control"><span class="glyphicon glyphicon-chevron-right"></span></a>
	</div>
</aside>
</#if>