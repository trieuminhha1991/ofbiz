<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/poresources/js/supplier/ViewSupplier.js"></script>

<#if party?has_content>
<div class="row-fluid">
<div class="span12">
	<div class="widget-box transparent" id="recent-box">
		<div class="widget-header" style="border-bottom:none">
			<div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
				<div class="row-fluid">
					<div class="span10">
						<div class="tabbable">
							<ul class="nav nav-tabs" id="recent-tab"><#-- nav-tabs-menu-->
								<li class="active">
									<a data-toggle="tab" href="#info-tab">${uiLabelMap.BSOrderGeneralInfo}</a>
								</li>
								<li>
									<a data-toggle="tab" href="#product-tab">${uiLabelMap.BSProduct}</a>
								</li>
								<li>
								<a data-toggle="tab" href="#promotion-tab">${uiLabelMap.BSPromotion}</a>
								</li>
								<li>
								<a data-toggle="tab" href="#finAccount-tab">${uiLabelMap.BankAccountShort}</a>
								</li>
							</ul>
						</div><!--.tabbable-->
					</div>
				</div>
			</div>
		</div>
		<div class="widget-body" style="margin-top: -12px !important">
			<div class="widget-main padding-4">
				<div class="tab-content overflow-visible" style="padding:8px 0">
					
					<#include "supplierViewInfo.ftl"/>
					
					<#include "productOfSupplier.ftl"/>
					
					<#include "promotionOfSupplier.ftl"/>

					<#include "supplierBankAccount.ftl"/>
					
				</div>
			</div><!--/widget-main-->
		</div><!--/widget-body-->
	</div><!--/widget-box-->
</div><!-- /span12 -->
</div><!--/row-->
</#if>
<script>
	LocalUtil.setBreadcrumb("${StringUtil.wrapString(uiLabelMap.BSSupplierInformation)}");
	const partyId = "${parameters.partyId?if_exists}";
	
	var canDropShipData = [{id:"Y", description:"${uiLabelMap.CommonYes}"}, {id:"N", description:"${uiLabelMap.CommonNo}"}];
</script>