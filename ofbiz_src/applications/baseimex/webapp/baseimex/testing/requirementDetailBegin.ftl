<script>
	<#assign requirementId = parameters.requirementId?if_exists/>
	<#assign requirement = delegator.findOne("Requirement", {"requirementId" : requirementId}, false)/>
		 
</script>
<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
					<div class="row-fluid">
						<div class="span8">
							<div class="tabbable">
								<ul class="nav nav-tabs" id="recent-tab"><#-- nav-tabs-menu-->
									<#if requirement?has_content>
										<li <#if activeTab?exists && activeTab == "general-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#general-tab">${uiLabelMap.GeneralInfo}</a>
										</li>
									</#if>
								</ul>
							</div><!--.tabbable-->
						</div>
						<div class="span4" style="height:34px; text-align:right">
							<#if requirement.statusId == "REQ_CREATED">
								<#if requirement.createdByUserLogin == userLogin.userLoginId || hasOlbPermission("MODULE", "IMEX_TESTING", "ADMIN")>
									<a href="javascript:ReqDetailObj.sendRequirementNotify('${requirement.requirementId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.SendRequest}" data-placement="bottom" class="button-action"><i class="fa-paper-plane-o"></i></a>
									<a href="javascript:ReqDetailObj.cancelRequirement('${requirement.requirementId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.CommonCancel}" data-placement="bottom" class="button-action"><i class="icon-trash red"></i></a>
								</#if>
				            </#if>
						</div>
					</div>
				</div>
			</div>
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div class="tab-content overflow-visible" style="padding:8px 0">