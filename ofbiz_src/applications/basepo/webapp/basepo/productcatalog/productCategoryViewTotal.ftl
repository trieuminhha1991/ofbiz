<@jqGridMinimumLib/>
<@jqOlbCoreLib hasGrid=true hasTreeGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>

<div id="container" style="background-color: transparent; overflow: auto; position:fixed; top:0; right:0; z-index: 99999; width:auto">
</div>
<div id="jqxNotification" style="margin-bottom:5px">
    <div id="notificationContent"></div>
</div>

<div class="row-fluid">
	<div class="span3">
		<#include "productCategoryViewListChild.ftl"/>
	</div>
	<div class="span9">
		<#include "productCategoryViewProduct.ftl"/>
	</div>
</div>

<div class="container_loader">
	<div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
		<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div>
				<div class="jqx-grid-load"></div>
				<span>${uiLabelMap.BSLoading}...</span>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		 jOlbUtil.notification.create("#container", "#jqxNotification", null, {width: 'auto', autoClose: true});
	});
</script>