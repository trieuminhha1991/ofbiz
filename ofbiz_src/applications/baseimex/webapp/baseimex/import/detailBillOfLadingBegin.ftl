<script>
	<#assign billId = parameters.billId?if_exists/>
	<#assign bill = delegator.findOne("BillOfLading", {"billId" : billId}, false)!>
	
	function createAndPrintInvoiceTotal (billId) {
		window.location.href = "CreateInvoiceTotal?billId=" + billId;
	}
	
	function createAndPrintListAttachments (billId) {
		window.location.href = "CreateListAttachments?billId=" + billId;
	}
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.AreYouSureApprove = "${StringUtil.wrapString(uiLabelMap.AreYouSureApprove)}";
	uiLabelMap.AreYouSureCancel = "${StringUtil.wrapString(uiLabelMap.AreYouSureCancel)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.AreYouSureSend = "${StringUtil.wrapString(uiLabelMap.AreYouSureSend)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.CreateSuccessfully = "${StringUtil.wrapString(uiLabelMap.CreateSuccessfully)}";
</script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.util.js"></script>
<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
					<div class="row-fluid">
						<div class="span10">
							<div class="tabbable">
								<ul class="nav nav-tabs" id="recent-tab"><#-- nav-tabs-menu-->
									<#if bill?has_content>
										<li <#if activeTab?exists && activeTab == "general-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#general-tab">${uiLabelMap.GeneralInfo}</a>
										</li>
										<li <#if activeTab?exists && activeTab == "container-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#container-tab">${uiLabelMap.BIEContainer}</a>
										</li>
										<li <#if activeTab?exists && activeTab == "packing-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#packing-tab">${uiLabelMap.BIEPackingList}</a>
										</li>
										<li <#if activeTab?exists && activeTab == "cost-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#cost-tab">${uiLabelMap.BIECost}</a>
										</li>
									</#if>
								</ul>
							</div><!--.tabbable-->
						</div>
						<div class="span2" style="height:34px; text-align:right">
							<a style="cursor: pointer;" href="javascript:createAndPrintInvoiceTotal('${billId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.CreateInvoiceTotal}" data-placement="bottom" class="button-action"><i class="fa fa-file-text-o"></i></a>
							<a style="cursor: pointer;" href="javascript:createAndPrintListAttachments('${billId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.CreateListAttachments}" data-placement="bottom" class="button-action"><i class="fa fa-file-excel-o"></i></a>
						</div>
					</div>
				</div>
			</div>
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div class="tab-content overflow-visible" style="padding:8px 0">