  <!-- Top Right part Links-->
    <div id="welcome">
      <#include "language.ftl">
      <#if userLogin?has_content && userLogin.userLoginId != "anonymous">
			<a href="#">${uiLabelMap.CommonWelcome}&nbsp;${sessionAttributes.autoName?if_exists}!</a><a href="<@ofbizUrl>logout</@ofbizUrl>">${uiLabelMap.CommonLogout}</a>
	  <#else/>
			<script type="text/javascript">
				function opencompare(){
					var params = [
				    'height='+screen.height,
				    'width='+screen.width,
				    'fullscreen=yes' // only works in IE, but here for completeness
					].join(',');
					     // and any other options from
					     // https://developer.mozilla.org/en/DOM/window.open

					var popup = window.open('<@ofbizUrl>compareProducts</@ofbizUrl>', 'compareProducts', params);
					popup.moveTo(0,0);
				}
			</script>
			<a href="#">${uiLabelMap.CommonWelcome}!</a><a href="<@ofbizUrl>${checkLoginUrl}</@ofbizUrl>">${uiLabelMap.CommonLogin}</a><a href="<@ofbizUrl>newcustomer</@ofbizUrl>">${uiLabelMap.EcommerceRegister}</a>
	  </#if>
	  <div class="links">...
        <ul>
	      <#if !userLogin?has_content || (userLogin.userLoginId)?if_exists != "anonymous">
	        <li><a href="<@ofbizUrl>viewprofile</@ofbizUrl>">${uiLabelMap.CommonProfile}</a></li>
	        <li><a href="<@ofbizUrl>messagelist</@ofbizUrl>">${uiLabelMap.CommonMessages}</a></li>
	        <li><a href="<@ofbizUrl>ListQuotes</@ofbizUrl>">${uiLabelMap.OrderOrderQuotes}</a></li>
	        <li><a href="<@ofbizUrl>ListRequests</@ofbizUrl>">${uiLabelMap.OrderRequests}</a></li>
	        <li><a href="<@ofbizUrl>editShoppingList</@ofbizUrl>">${uiLabelMap.EcommerceShoppingLists}</a></li>
	        <li><a href="<@ofbizUrl>orderhistory</@ofbizUrl>">${uiLabelMap.EcommerceOrderHistory}</a></li>
	        <li><a href="#" onclick="opencompare();">${uiLabelMap.BigshopViewCompare}</a></li>
	      </#if>
	      <#if catalogQuickaddUse>
	        <li><a class="invarseColor" href="<@ofbizUrl>quickadd</@ofbizUrl>">${uiLabelMap.CommonQuickAdd}</a></li>
	      </#if>
		</ul>
      </div>
    </div>
    <div id="logo">
      <#if sessionAttributes.overrideLogo?exists>
        <img src="<@ofbizContentUrl>${sessionAttributes.overrideLogo}</@ofbizContentUrl>" alt="Logo"/>
      <#elseif catalogHeaderLogo?exists>
        <img src="<@ofbizContentUrl>${catalogHeaderLogo}</@ofbizContentUrl>" alt="Logo"/>
      <#elseif layoutSettings.VT_HDR_IMAGE_URL?has_content>
        <img src="<@ofbizContentUrl>${layoutSettings.VT_HDR_IMAGE_URL.get(0)}</@ofbizContentUrl>" alt="Logo"/>
      </#if>
    </div>
    <#include "search/searchform.ftl">
    <#include "cart/minicart.ftl">