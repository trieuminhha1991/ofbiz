<script src="/crmresources/js/generalUtils.js"></script>
<script src="/administrationresources/js/security/ModuleDetail.js"></script>

<#include "component://administration/webapp/administration/common/AdministrationConfig.ftl"/>
<#include "popup/OverridePermission.ftl"/>

<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
					<div class="row-fluid">
						<div class="span12">
							<div class="tabbable">
								<ul class="nav nav-tabs" id="recent-tab"><#-- nav-tabs-menu-->
									<li<#if !activeTab?exists || activeTab == "" || activeTab == "OverView-tab"> class="active"</#if>>
										<a data-toggle="tab" href="#OverView-tab">${uiLabelMap.BSOrderGeneralInfo}</a>
									</li>
									<li<#if activeTab?exists && activeTab == "Action-tab"> class="active"</#if>>
										<a data-toggle="tab" href="#Action-tab">${uiLabelMap.ADAction}</a>
									</li>
									<li<#if activeTab?exists && activeTab == "ModuleChild-tab"> class="active"</#if>>
										<a data-toggle="tab" href="#ModuleChild-tab">${uiLabelMap.ADModuleChild}</a>
									</li>
									<li<#if activeTab?exists && activeTab == "UserInModule-tab"> class="active"</#if>>
										<a data-toggle="tab" href="#UserInModule-tab">${uiLabelMap.ADUsersHasPermission}</a>
									</li>
									<li<#if activeTab?exists && activeTab == "GroupInModule-tab"> class="active"</#if>>
										<a data-toggle="tab" href="#GroupInModule-tab">${uiLabelMap.ADGroupHasPermission}</a>
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
						
						<#include "ModuleOverView.ftl"/>
						
						<#include "ActionInModule.ftl"/>
						
						<div id="ModuleChild-tab" class="tab-pane<#if activeTab?exists && activeTab == "ModuleChild-tab"> active</#if>">
							<div style="position:relative"><!-- class="widget-body"-->
								<div><!--class="widget-main"-->
									<div class="row-fluid">
										<#include "Module.ftl"/>
									</div><!--.row-fluid-->
								</div>
							</div>
						</div>
						
						<#include "UserInModule.ftl"/>
						
						<#include "GroupInModule.ftl"/>
						
					</div>
				</div><!--/widget-main-->
			</div><!--/widget-body-->
		</div><!--/widget-box-->
	</div><!-- /span12 -->
</div><!--/row-->