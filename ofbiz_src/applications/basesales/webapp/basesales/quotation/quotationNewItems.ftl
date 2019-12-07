<div style="width:100%">
	<div id="qni-case1" style="position:relative" class="form-window-content-custom">
		<#--${screens.render("component://basesales/widget/ProductQuotationScreens.xml#NewQuotationProductItems")}-->
		<#include "quotationNewItemsProd.ftl"/>
	</div>
	<div id="qni-case2" style="display:none" class="form-window-content-custom">
		<#include "quotationNewCategoryItems.ftl"/>
	</div>
</div>