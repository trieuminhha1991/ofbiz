<div class="asktopic">
<div class="title"><h2>Chủ đề được hỏi nhiều nhất</h2></div>
<span class="topicname">Tên chủ đề</span>
<span class="numberask">Số câu hỏi/hướng dẫn</span>
<ul>
<#assign listFAQCategory = delegator.findList("FAQCategory", null, null, null, null, false) />

	<#if listFAQCategory?exists>
		<#list listFAQCategory as FAQCategory>

			<li>
			    <a href="<@ofbizUrl>ListFAQ?contentTypeId=${(FAQCategory.contentTypeId)?if_exists}</@ofbizUrl>" title="">${(FAQCategory.topicName)?if_exists}</a>
			    <span>${(FAQCategory.numberOfAsked)?if_exists}</span>
			</li>

		</#list>
	</#if>

</ul>
</div>