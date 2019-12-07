<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<#assign agreementId = parameters.agreementId />
<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
					<div class="row-fluid">
					<div class="span10">
						<div class="tabbable">
								<ul class="nav nav-tabs" id="recent-tab"><#-- nav-tabs-menu-->
									<li<#if !activeTab?exists || activeTab == "" || activeTab == "agreementView-tab"> class="active"</#if>>
										<a href="editAgreement?agreementId=${agreementId}">${uiLabelMap.AccountingAgreement}</a>
									</li>
									<li<#if activeTab?exists && activeTab == "agreementTerm-tab"> class="active"</#if>>
										<a href="editAgreementTerm?agreementId=${agreementId}">${uiLabelMap.AccountingAgreementItemTerms}</a>
									</li>
								<#--	<li<#if activeTab?exists && activeTab == "agreementItem-tab"> class="active"</#if>>
										<a href="editAgreementItem?agreementId=${agreementId}">${uiLabelMap.AccountingAgreementItems}</a>
									</li>
									<li<#if activeTab?exists && activeTab == "agreementRoles-tab"> class="active"</#if>>
										<a href="editAgreementRoles?agreementId=${agreementId}">${uiLabelMap.AccountingAgreementRoles}</a>
									</li> -->
								</ul>
							</div><!--.tabbable-->
						</div>
					</div>
					<script type="text/javascript">
						$('[data-rel=tooltip]').tooltip();
					</script>
					<style type="text/css">
						.button-action {
							font-size:18px; padding:0 0 0 8px;
						}
					</style>
				</div>
			</div>
		</div>
	</div>
</div>