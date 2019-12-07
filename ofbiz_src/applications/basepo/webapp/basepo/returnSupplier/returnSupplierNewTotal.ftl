<#include "script/returnSupplierNewScript.ftl"/>
<div class="row-fluid">
	<div class="span1"></div>
	<div class="span2" style="margin-top:5px;"></div>
	<div class="span6" style="margin-top:5px;font-size: 25px; text-align: center">
		<b>${uiLabelMap.DmsCreateNewReturnSupplier}</b>
	</div>
	<div class="span3" style="margin-top:5px;"></div>
</div>
<hr style="margin:0px 0px 0px !important;"/>
<@loading id="process-loading-css" fixed="false" zIndex="9998" top="20%" option=7 background="rgba(255, 255, 255, 1)"/>
<div id="newReturnSupplier" style="margin-top: 10px;">	
	<div class="row-fluid">
		<div class="row-fluid span6" style="margin-top:5px;">
			<div class="span4 div-inline-block">
				<label class="asterisk" style="margin-top: 3px !important;">${uiLabelMap.POSupplier}</label>
			</div>
			<div class="span7 div-inline-block">
				<div id="toPartyId"></div>
			</div>
		</div>
		<div class="row-fluid span6" style="margin-top:5px;">
			<div class="span4 div-inline-block">
				<label class="asterisk" style="margin-top: 3px !important;">${uiLabelMap.POEntryDate}</label>
			</div>
			<div class="span7 div-inline-block">
				<div id="entryDate"></div>
			</div>
		</div>
	</div>
	
	<div class="row-fluid">
		<div class="span6 row-fluid" style="margin-top:5px;">
			<div class="span4 div-inline-block">
				<label class="asterisk" style="margin-top: 4px;">${uiLabelMap.POOrderId}</label>
			</div>
			<div class="span7 div-inline-block">
				<div class="">
					<div id="orderHeaderBtn" style="width: 100%;">
						<div style="border-color: transparent;" id="orderHeaderGrid"></div>
					</div>
				</div>
			</div>
			<input type="hidden" id="orderHeaderId"/>
		</div>	
		<div class="row-fluid span6" style="margin-top:5px;">
			<div class="span4 div-inline-block">
				<label class="asterisk" style="margin-top: 3px !important;">${uiLabelMap.BPOCurrencyUomId}</label>
			</div>
			<div class="span7 div-inline-block">
				<div id="currencyUomId" style="width: 100%" class=""></div>
			</div>
		</div>
	</div>
</div>
<hr/>
<div class="row-fluid">
	<#include "returnSupplierListProduct.ftl"/>
</div>
<hr/>
<div class="row-fluid">
	<div class="span10"></div>
	<div class="span2">
		<button id="createReturn" class="btn btn-small btn-primary pull-right"><i class="icon-ok"></i>${uiLabelMap.DmsCreateNew}</button>
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
<@jqOlbCoreLib />
<script type="text/javascript" src="/poresources/js/returnSupplier/returnSupplierNewTotal.js"></script>
