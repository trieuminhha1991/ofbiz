<style type="text/css">
	.contentTab {
		padding:10px
	}
	.buttonsContainer {
		position:absolute;
		top:22px;
		right:25px;
	}
</style>
<@jqTreeGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<@jqOlbCoreLib hasGrid=true hasTreeGrid=true/>
<div class="tabDivInner">
	<ul style="margin-left: 30px;">
		<li>${uiLabelMap.BSCategory}</li>
		<li class="titleTab2">${uiLabelMap.BSPSSalesChannel}</li>
	</ul>
	<div class="contentTab1 contentTab">
		<#-- list categories -->
		<div class="row-fluid">
			<div class="span12">
				<div style="width: 98%; margin-left: 1%">
					<#include "productCategoryListInner.ftl"/>
				</div>
			</div><!--.span12-->
		</div>
	</div>
	<#-- list product store -->
	<div class="contentTab2 contentTab">
		<div class="row-fluid">
			<div class="span12">
				<div style="width: 98%; margin-left: 1%">
					<#include "productStoreListInner.ftl"/>
				</div>
			</div><!--.span12-->
		</div><!--.row-fluid-->
	</div><!--.contentTab2-->
</div>
<div class="buttonsContainer">
	<#if hasOlbPermission("MODULE", "SALES_STORECATALOG_EDIT", "")>
	<a href="javascript:void(0)" onClick="addProductStore('${parameters.prodCatalogId?if_exists}')"><i class="fa fa-plus"></i>${uiLabelMap.BSAddSalesChannel}</a>
	</#if>
	<#if hasOlbPermission("MODULE", "CATALOGS_NEW", "") && hasOlbPermission("MODULE", "CATALOGS_EDIT", "")>
	&nbsp;&nbsp;
	<a href="javascript:void(0)" onClick="changeRootCategory('${parameters.prodCatalogId?if_exists}')"><i class="fa fa-exchange"></i>${uiLabelMap.BSChangeRootCategory}</a>
	</#if>
</div>
<script type="text/javascript">
	$(function(){
		$(".tabDivInner").jqxTabs({ theme: "energyblue", width: "100%", height: 250});<#--bootstrap-->
	});
</script>