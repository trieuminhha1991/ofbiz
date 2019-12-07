<#-- first solution
<style type="text/css">
	.menu-float li{
		display:inline-block;
		margin:2px
	}
	.menu-float li div{
		height:128px;
	}
</style>
<li>
	<a href="<@ofbizUrl>listRoleTypeProductStore</@ofbizUrl>" class="btn btn-success" target="_blank">
		<label>${uiLabelMap.BSRoleType}</label>
		<div><img src="/salesresources/css/icons/person_role.png" width="128" height="128"/></div>
	</a>
</li>
 -->

<style type="text/css">
	.menu-float {
		margin-top:20px;
		padding-left:30px;
	}
	.menu-float li{
		display:inline-block;
		margin:5px;
		padding: 5px;
		width: 30%;
	}
	.menu-float li a{
		display:block;
		height: 100%;
		width: 100%;
	}
	.menu-float li img, .menu-float li label{
		display: inline-block;
	}
	.menu-float li label {
		margin-left: 10px
	}
	.menu-float li:hover {
	    background-color: rgba(111, 179, 224, 0.26);
	}
</style>
<div class="row-fluid">
	<div class="span12">
		<ul class="unstyled spaced2 menu-float">
			<#if hasOlbPermission("MODULE", "SALES_CUSTPERIOD_VIEW", "")>
			<li>
				<a href="<@ofbizUrl>listCustomTimePeriod</@ofbizUrl>">
					<img src="/salesresources/css/icons/custom_time_period.png" width="48" height="48"/>
					<label>${uiLabelMap.BSSalesCustomTimePeriod}</label>
				</a>
			</li>
			</#if>
			<#if hasOlbPermission("MODULE", "SALES_CHANNELEST", "")>
			<li>
				<a href="<@ofbizUrl>listChannel</@ofbizUrl>">
					<img src="/salesresources/css/icons/32x32_sales_channel.png" width="48" height="48"/>
					<label>${uiLabelMap.BSSalesChannelType}</label>
				</a>
			</li>
			</#if>
			<#if hasOlbPermission("MODULE", "SALES_PRIORITYST", "")>
			<li>
				<a href="<@ofbizUrl>listPriority</@ofbizUrl>">
					<img src="/salesresources/css/icons/32x32_priority.png" width="48" height="48"/>
					<label>${uiLabelMap.BSPriority}</label>
				</a>
			</li>
			</#if>
			<#if hasOlbPermission("MODULE", "SALES_ROLETYPEST", "")>
			<li>
				<a href="<@ofbizUrl>listRoleTypeProductStore</@ofbizUrl>">
					<img src="/salesresources/css/icons/32x32_person_role.png" width="48" height="48"/>
					<label>${uiLabelMap.BSRoleType}</label>
				</a>
			</li>
			</#if>
			<#if hasOlbPermission("MODULE", "SALES_FEATUREST", "")>
			<li>
				<a href="<@ofbizUrl>listFeature</@ofbizUrl>">
					<img src="/salesresources/css/icons/32x32_prod_feature.png" width="48" height="48"/>
					<label>${uiLabelMap.BSProductFeature}</label>
				</a>
			</li>
			</#if>
			<#if hasOlbPermission("MODULE", "SALES_CUSTOMERTYPE_VIEW", "")>
			<li>
				<a href="<@ofbizUrl>listCustomerType</@ofbizUrl>">
					<img src="/salesresources/css/icons/32x32_partyType.png" width="48" height="48"/>
					<label>${uiLabelMap.BSCustomerType}</label>
				</a>
			</li>
			</#if>
		</ul>
	</div>
</div>