<ul class="catenews">
	<#list contentbycategory as content>
		<li class='news-item'>
			<div class='row'>
				<div class='col-lg-5 col-md-5 col-sm-5'>
					<a href="viewcontent?cId=${(content.contentId)?if_exists}" title="${(content.contentName)?if_exists}">
						<#if (content.images)?exists><#if content.images != "">
							<img alt="${(content.contentName)?if_exists}" src="${StringUtil.wrapString((content.images)?if_exists)}">
						</#if></#if>
					</a>
				</div>
				<div class='col-lg-7 col-md-7 col-sm-7 news-content-container'>
					<a href="viewcontent?cId=${(content.contentId)?if_exists}" title="${(content.contentName)?if_exists}">
						<h3 class='news-title'>${(content.contentName)?if_exists}</h3>
					</a>
					<div class='news-time'><i class='fa fa-calendar'>&nbsp;</i>&nbsp;${(content.ago)?if_exists}</div>
					<div class='news-content'>
						${StringUtil.wrapString((content.description)?if_exists)}
					</div>
				</div>
			</div>
		</li>
	</#list>
</ul>
