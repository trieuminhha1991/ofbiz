<div class="box">
  <div class="box-heading"><span>${uiLabelMap.BigshopAccount}</span></div>
  <div class="box-content">
	<ul class="list-item">
		  <#if userLogin?has_content && userLogin.userLoginId != "anonymous">
			<li>	<a href="<@ofbizUrl>logout</@ofbizUrl>">${uiLabelMap.CommonLogout}</a></li>
		  <#else/>
			<li>	<a href="<@ofbizUrl>${checkLoginUrl}</@ofbizUrl>">${uiLabelMap.CommonLogin}</a></li>
			<li>	<a href="<@ofbizUrl>newcustomer</@ofbizUrl>">${uiLabelMap.EcommerceRegister}</a></li>
		  </#if>
	      <#if !userLogin?has_content || (userLogin.userLoginId)?if_exists != "anonymous">
	        <li><a href="<@ofbizUrl>viewprofile</@ofbizUrl>">${uiLabelMap.CommonProfile}</a></li>
	        <li><a href="<@ofbizUrl>messagelist</@ofbizUrl>">${uiLabelMap.CommonMessages}</a></li>
	        <li><a href="<@ofbizUrl>ListQuotes</@ofbizUrl>">${uiLabelMap.OrderOrderQuotes}</a></li>
	        <li><a href="<@ofbizUrl>ListRequests</@ofbizUrl>">${uiLabelMap.OrderRequests}</a></li>
	        <li><a href="<@ofbizUrl>editShoppingList</@ofbizUrl>">${uiLabelMap.EcommerceShoppingLists}</a></li>
	        <li><a href="<@ofbizUrl>orderhistory</@ofbizUrl>">${uiLabelMap.EcommerceOrderHistory}</a></li>
	        <li><a href="#" onclick="opencompare();">${uiLabelMap.BigshopViewCompare}</a></li>
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
	      </#if>
	      <#if catalogQuickaddUse>
	        <li><a class="invarseColor" href="<@ofbizUrl>quickadd</@ofbizUrl>">${uiLabelMap.CommonQuickAdd}</a></li>
	      </#if>
		</ul>
  </div>
</div>