<div>
	<div style="position:relative"><!-- class="widget-body"-->
		<div><!--class="widget-main"-->
			<div class="row-fluid">
				<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
					<div class="row-fluid">
						<div class="span6">
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSRequirementId}:</label>
								</div>
								<div class="div-inline-block">
									<span><i>${requirement.requirementId?if_exists}</i></span>
								</div>
							</div>
							<#assign requirementType = requirement.getRelatedOne("RequirementType")!>
							<#assign reqTypeDesc = StringUtil.wrapString(requirementType.get('description', locale))>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.RequirementType}:</label>
								</div>
								<div class="div-inline-block">
									<span>${reqTypeDesc}</span>
								</div>
							</div>
							<#assign requirementStatus = requirement.getRelatedOne("StatusItem")!>
							<#assign descStatus = StringUtil.wrapString(requirementStatus.get('description', locale))>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.Status}:</label>
								</div>
								<div class="div-inline-block">
									<span>${descStatus}</span>
								</div>
							</div>
							<#assign requirementStatuses = delegator.findList("RequirementStatus", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("requirementId", requirement.requirementId), null, null, null, false) />
                            	<#if requirementStatuses?has_content>
                            		<div class="row-fluid">
                            			<div class="div-inline-block">
                            				<label></label>
                            			</div>
                            			<div class="div-inline-block">
                            				<span style="font-weight: normal">
                            				<#list requirementStatuses as requirementStatus>
                                                <#assign loopStatusItem = delegator.findOne("StatusItem", {"statusId" : requirementStatus.statusId}, false)/>
                                                <div class="margin-left: 20px;">
                                                    ${loopStatusItem.get("description",locale)} <#if requirementStatus.statusDate?has_content>- ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(requirementStatus.statusDate, "", locale, timeZone)?default("0000-00-00 00:00:00")}</#if>
                                                                                        &nbsp;
                                                    <#if requirementStatus.statusUserLogin?has_content>
                                                        ${uiLabelMap.CommonBy} - [${requirementStatus.statusUserLogin}]
                                                    </#if>
                                                </div>
                            				</#list>
                            				</span>
                            			</div>
                            		</div>
                            	</#if>
						</div><!--.span6-->
						<div class="span6">
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSRequiredByDate}:</label>
								</div>
								<div class="div-inline-block">
									<span>
										<#if requirement.requiredByDate?exists>
											${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(requirement.requiredByDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
										</#if>
									</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSRequirementStartDate}:</label>
								</div>
								<div class="div-inline-block">
									<span>
										<#if requirement.requirementStartDate?exists>
											${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(requirement.requirementStartDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
										</#if>
									</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSDescription}:</label>
								</div>
								<div class="div-inline-block">
									<span>${requirement.description?if_exists}</span>
								</div>
							</div>
						</div><!--.span6-->
					</div>
				</div><!-- .form-horizontal -->
				<div class="row-fluid">
					<div class="span12">
						<#include "reqDeliveryOrderViewItems.ftl"/>
					</div>
				</div><!--.form-horizontal-->

			</div><!--.row-fluid-->
		</div><!--.widget-main-->
	</div><!--.widget-body-->
</div>

<script type="text/javascript">
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.AreYouSureAccept = "${uiLabelMap.AreYouSureAccept}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";

</script>
<@jqOlbCoreLib />
<script type="text/javascript" src="/salesmtlresources/js/requirement/reqDeliveryOrderView.js"></script>