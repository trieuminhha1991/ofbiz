<#assign resultValue = dispatcher.runSync("getFAQ", Static["org.ofbiz.base.util.UtilMisc"].toMap("contentId", parameters.cId))/>
<#if Static["org.ofbiz.service.ServiceUtil"].isSuccess(resultValue)>
	<#assign faq = resultValue.get("faq")/>
</#if>

<style>
	.user-name {
	    line-height: 1.3em;
	    font-size: 18px;
	    color: #288ad6;
		display: inline;
	}
	.created {
		margin-top: -16px;
	margin-left: 60px;
	}
	.faq {
	    float: right;
	    padding: 15px;
	    overflow: hidden;
	    width: 100%;
	    margin: 25px 0;
	    border: 1px solid #ededed;
	    border-radius: 4px;
	    -webkit-border-radius: 4px;
	}
	.faq-reply {
	    margin-top: 15px;
	}
	.faq-content {
	    display: block;
	margin: 5px 0px 8px 0px;
	    position: relative;
	    background: #f1f1f1;
	    border: 1px solid #e7e7e7;
	    padding: 15px 10px;
	    font-size: 14px;
	    color: #333;
	    margin-left: 50px;
	}
</style>

<#if faq?exists>
<div class="faq">
	<div class="faq-ask">
		<#assign shortcut = faq.contentName?substring(0, 1)/>
		<div class="span12">
			<div class="icon-shortcut-larger linkimg">${shortcut}</div>
			<div class="user-name">${(faq.contentName)?if_exists}</div>
	        <div class="created">${(faq.createdStamp)?if_exists}</div>
		</div>
        <div class="faq-content">${StringUtil.wrapString((faq.longDescription)?if_exists)}</div>
	</div>

	<hr style="color: #e0e0e0;"/>

	<#if Static["org.ofbiz.base.util.UtilValidate"].isNotEmpty(faq.answerFAQ)>

		<div class="faq-reply replay">
			<#assign answerFAQ = faq.answerFAQ/>
			<#assign shortcut = answerFAQ.contentName?substring(0, 1)/>
			<div class="span12">
				<div class="icon-shortcut-larger linkimg">${shortcut}</div>
				<div class="user-name">${(answerFAQ.contentName)?if_exists}</div><#if (answerFAQ.partyRole)?exists><span class="mob">${(answerFAQ.partyRole)?if_exists}</span></#if>
		        <div class="created">${(answerFAQ.createdStamp)?if_exists}</div>
			</div>
	        <div class="faq-content">${StringUtil.wrapString((answerFAQ.longDescription)?if_exists)}</div>
		</div>

	 </#if>
</div>
</#if>