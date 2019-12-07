<div class="mainnav-inner">
	<!-- BEGIN: NAV -->
	<div id="jm-mainnav" class="has-toggle">
		<div class="btn-toggle menu-toggle">
			 <i class="fa fa-bars">&nbsp;</i>
		</div>
		<div class="inner-toggle  jm-menu-top" id="jm-mainnav-inner">
			<div id="jm-mnutop" class="none jm-megamenu clearfix">
				<ul class="megamenu level0">
					<li class="mega first active"><a href="<@ofbizUrl>main</@ofbizUrl>">${uiLabelMap.ObbHome}</a></li>
					<li class="mega mega-normal"><a href="<@ofbizUrl>viewcontent?cId=customerservice</@ofbizUrl>">${uiLabelMap.ObbCustomerService}</a></li>
					<li class="mega mega-normal"><a href="<@ofbizUrl>viewcontent?cId=termcond</@ofbizUrl>">${uiLabelMap.ObbTermACond}</a></li>
					<li class="mega mega-normal"><a href="<@ofbizUrl>viewcontent?cId=about</@ofbizUrl>">${uiLabelMap.ObbAboutUs}</a></li>
					<li class="mega mega-normal">
					<#if userLogin?has_content && userLogin.userLoginId != "anonymous">
			          <a href="<@ofbizUrl>contactus</@ofbizUrl>">${uiLabelMap.ObbContactUs}</a></li>
			        <#else>
			          <a href="<@ofbizUrl>AnonContactus</@ofbizUrl>">${uiLabelMap.ObbContactUs}</a></li>
			        </#if>
			        <li class="mega mega-normal"><a href="<@ofbizUrl>contentcategory?catContentId=TIN-TUC</@ofbizUrl>">${uiLabelMap.BENews}</a></li>
				</ul>
			</div>
		</div>
	</div>
	<!-- END: NAV -->
</div>

<#-- FIXME need to get from content -->
<div id="jm-tops1" class="jm-position wrap clearfix delivery-fk">
	<div class="main col1-set">
		<div class="inner clearfix">
			<ul>
				<li class="first"><em class="fa fa-thumbs-up"></em> <span class="4featured"><b class="orange">${uiLabelMap.BEHighQuality}</b></span>${uiLabelMap.BEPreOwnedAuthentic}</li>
				<li><em class="fa fa-truck"></em> <span class="4featured"><b class="orange">${uiLabelMap.BEFreeShipping}</b></span>${uiLabelMap.BEOverThan1000000}</li>
				<li><em class="fa fa-usd"></em> <span class="4featured"><b class="orange">${uiLabelMap.BEPayOnDelivery}</b></span>${uiLabelMap.BEJustOnDelivery}</li>
				<li class="last"><em class="fa fa-refresh"></em> <span class="4featured"><b class="orange">${uiLabelMap.BEMoneyBack}</b></span>${uiLabelMap.BEGuarantee100}</li>
			</ul>
		</div>
	</div>
</div>
