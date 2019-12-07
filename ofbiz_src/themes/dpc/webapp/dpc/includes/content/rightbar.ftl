<aside class="renews">
	<div class="usage_tips">
		<#if cat?exists>
			<h2>Đọc tin "${cat.description}" khác (10 tin)</h2>
			<#if relatedPost?exists>
				<#list relatedPost as post>
					<a href="#" title="">
						<img width="140" height="85" alt="" src="${post.images}">
						<h3>${post.contentName}</h3>
						<div>
							<i class="allicon-comm"></i>
							<label>${content.totalComment?if_exists}</label>
						</div>
					</a>
				</#list>
			</#if>
		</#if>
	</div>
</aside>