<#include "script/returnSupplierDetailBeginScript.ftl"/>
<@jqGridMinimumLib />
<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
					<div class="row-fluid">
						<div class="span10">
							<div class="tabbable">
								<ul class="nav nav-tabs" id="recent-tab"><#-- nav-tabs-menu-->
									<li<#if !activeTab?exists || activeTab == "" || activeTab == "general-tab"> class="active"</#if>>
										<a data-toggle="tab" href="#general-tab">${uiLabelMap.GeneralInfo}</a>
									</li>
								</ul>
							</div><!--.tabbable-->
						</div>
						<div class="span2" style="height:34px; text-align:right">
							<a href="javascript:SupDetailOpenObj.exportPDFReturn('${returnId}')" data-rel="tooltip" title="${uiLabelMap.PDF}" data-placement="bottom" class="button-action"><i class="fa fa-file-pdf-o"></i></a>
							<#if returnHeader.statusId == "SUP_RETURN_ACCEPTED">
								<#if hasOlbPermission("MODULE", "RETURNPO", "UPDATE") && hasOlbPermission("MODULE", "LOGISTICS", "VIEW")>
				    				<a href="javascript:SupDetailOpenObj.prepareExportReturn('${returnId}')" data-rel="tooltip" title="${uiLabelMap.ExportProduct}" data-placement="bottom" class="button-action"><i class="fa fa-upload"></i></a>
				    			</#if>
				    		</#if>
				    		<#if returnHeader.statusId == "SUP_RETURN_REQUESTED">
				    			<#if hasOlbPermission("MODULE", "RETURNPO", "ADMIN")>
									<a href="javascript:SupDetailOpenObj.approveReturn('${returnHeader.returnId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.Approve}" data-placement="bottom" class="button-action button-size"><i class="fa fa-check-circle-o"></i></a>
								</#if>
								<#if returnHeader.createdBy == userLogin.userLoginId || hasOlbPermission("MODULE", "RETURNPO", "ADMIN")>
									<a href="javascript:SupDetailOpenObj.editReturn('${returnHeader.returnId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.Edit}" data-placement="bottom" class="button-action button-size"><i class="fa fa-edit"></i></a>
									<a href="javascript:SupDetailOpenObj.cancelReturn('${returnHeader.returnId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.CommonCancel}" data-placement="bottom" class="button-action button-size"><i class="icon-trash red"></i></a>
								</#if>
							</#if>
						</div>
					</div>
				</div>
			</div>
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div class="tab-content overflow-visible" style="padding:8px 0">
