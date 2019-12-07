<#if requestAttributes.errorMessageList?has_content><#assign errorMessageList=requestAttributes.errorMessageList></#if>
<#if requestAttributes.eventMessageList?has_content><#assign eventMessageList=requestAttributes.eventMessageList></#if>
<#if requestAttributes.serviceValidationException?exists><#assign serviceValidationException = requestAttributes.serviceValidationException></#if>
<#if requestAttributes.uiLabelMap?has_content><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>

<#if !errorMessage?has_content>
  <#assign errorMessage = requestAttributes._ERROR_MESSAGE_?if_exists>
</#if>
<#if !errorMessageList?has_content>
  <#assign errorMessageList = requestAttributes._ERROR_MESSAGE_LIST_?if_exists>
</#if>
<#if !eventMessage?has_content>
  <#assign eventMessage = requestAttributes._EVENT_MESSAGE_?if_exists>
</#if>
<#if !eventMessageList?has_content>
  <#assign eventMessageList = requestAttributes._EVENT_MESSAGE_LIST_?if_exists>
</#if>

<#if !errorMessage?has_content>
	<#assign errorMessage = requestAttributes._ERROR_MESSAGE_?if_exists>
</#if>

<style type="text/css">
.warning{
	color:#F81700;
	padding:5px;
	width: 100%;
	font-size:11pt;
	margin-left:auto;
	margin-right:auto;
}
</style>

<div id="messages_product_view" style="margin:0 auto;" onclick="document.getElementById('messages_product_view').style.display='none'">
	<ul class="messages" id="messages">
		<#-- display the error messages -->
		<#if (errorMessage?has_content || errorMessageList?has_content)>
			<li class="error-msg">
				<span id="content-messages" class="warning woocommerce-error">
				    ${uiLabelMap.CommonFollowingErrorsOccurred}:
				    <#if errorMessage?has_content>
				      ${errorMessage}
				    </#if>
				    <#if errorMessageList?has_content>
				      <#list errorMessageList as errorMsg>
				        ${errorMsg}
				      </#list>
				    </#if>
				  </span>
		    </li>
		</#if>
		<#-- display the event messages -->
		<#if (eventMessage?has_content || eventMessageList?has_content)>
			<li class="success-msg">
			  <div id="content-messages" class="success woocommerce-error">
			    ${uiLabelMap.CommonFollowingOccurred}:
			    <#if eventMessage?has_content>
			      ${eventMessage}
			    </#if>
			    <#if eventMessageList?has_content>
			      <#list eventMessageList as eventMsg>
			        ${eventMsg}
			      </#list>
			    </#if>
			  </div>
		    </li>
		</#if>
	</ul>
</div>