
<div class="row-fluid">
	<div class="basic-form form-horizontal">
		<div class="control-group no-left-margin">
			<label class="control-label">${uiLabelMap.EmplClaimId}</label>
			<div class="controls">
				${emplClaim.emplClaimId}
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label">${uiLabelMap.EmplClaimType}</label>
			<div class="controls">
				<#assign claimType = delegator.findOne("ClaimType", Static["org.ofbiz.base.util.UtilMisc"].toMap("claimTypeId", emplClaim.claimTypeId), false)>									
				${claimType.description?if_exists}
			</div>
		</div>
		<#if emplClaimAssoc?has_content>
			<div class="control-group no-left-margin">
				<label class="control-label">${uiLabelMap.EmplClaimIdAssoc}</label>
				<div class="controls">
					<#list emplClaimAssoc as tempEmplClaimAssoc>
						<a href="<@ofbizUrl>EmplClaimApproval?emplClaimId=${tempEmplClaimAssoc.emplClaimIdTo}</@ofbizUrl>">
							${tempEmplClaimAssoc.emplClaimIdTo}
						</a>
						<#if tempEmplClaimAssoc_has_next>
						,
						</#if>
					</#list>
				</div>
			</div>
		</#if>
		
		<div class="control-group no-left-margin">
			<label class="control-label">${uiLabelMap.NotificationHeader}</label>
			<div class="controls">
				${emplClaim.title?if_exists}
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label">${uiLabelMap.CommonDescription}</label>
			<div class="controls">
				<#if emplClaim.description?exists>
					${StringUtil.wrapString(emplClaim.description)}
				<#else>
					&nbsp;	
				</#if>
				
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label">${uiLabelMap.EmplCreatedClaim}</label>
			<div class="controls">
				${partyClaimming.lastName?if_exists} ${partyClaimming.middleName?if_exists} ${partyClaimming.firstName?if_exists}
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label">${uiLabelMap.CreatedClaimDate}</label>
			<div class="controls">
				<#if emplClaim.createdDate?exists>
					${emplClaim.createdDate?string["dd/MM/yyyy"]}
				<#else>
					&nbsp;	
				</#if>
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label">${uiLabelMap.ClaimSettlement}</label>
			<div class="controls">
				<#list emplClaimSettlementList as settlement>
					<#assign partySettlement = delegator.findOne("Person", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", settlement.partyId), false)>
					${partySettlement.lastName?if_exists} ${partySettlement.middleName?if_exists} ${partySettlement.firstName?if_exists} 
					<#if settlement_has_next>,</#if>		 
				</#list>
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label">${uiLabelMap.CommonStatus}</label>
			<div class="controls">
				<#assign statusItem = delegator.findOne("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", emplClaim.statusId), false)>
				${statusItem.description?if_exists}
			</div>
		</div>
		<#if emplClaim.comment?exists>	
			<div class="control-group no-left-margin">
			<label class="control-label">${uiLabelMap.HRFeedback}</label>
			<div class="controls">
				${emplClaim.comment}
			</div>
		</div>
		</#if>
		<#assign partySettlementList = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(emplClaimSettlementList, "partyId", true)>
		<#if partySettlementList.contains(userLogin.partyId) && emplClaim.statusId == "EMPL_CLAIM_CREATED">
			
			<div class="control-group no-left-margin">
				<label class="control-label">&nbsp;</label>
				<div class="controls" style="margin-left: 100px !important">
					<a href="#responeClaim" data-toggle="modal" role="button" class="btn btn-small btn-primary">
						<i class="icon-share open-sans"></i>
						${uiLabelMap.CommonRespone}
					</a>
					&nbsp;
					<a href="#rejectClaim" data-toggle="modal" role="button" class="btn btn-small btn-danger">
						<i class="icon-remove open-sans"></i>
						${uiLabelMap.ClaimReject}
					</a>
					
				</div>
			</div>
			<div id="responeClaim" class="modal hide fade" tabindex="-1">
				<div class="modal-header no-padding">
					<div class="table-header">
						<button type="button" class="close" data-dismiss="modal">&times;</button>
						${uiLabelMap.ClaimApproval}
					</div>
				</div>
				<div class="modal-body no-padding">
					<form id="responeClaimForm" class="basic-form form-horizontal" action="<@ofbizUrl>updateEmplClaimStatus</@ofbizUrl>" method="post">
						<input type="hidden" name="emplClaimId" value="${emplClaim.emplClaimId}">
						<input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}">
						<input type="hidden" name="statusId" value="EMPL_CLAIM_RESPONED">
						<div class="control-group no-left-margin">
							<label class="control-label asterisk">${uiLabelMap.HRFeedback}</label>
							<div class="controls">
								<input type="text" name="comment">
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">&nbsp;</label>
							<div class="controls">
								<button class="btn btn-small btn-primary" type="submit">
									<i class="icon-ok"></i>
									${uiLabelMap.CommonSubmit}
								</button>
							</div>
						</div>
					</form>
				</div>
			</div>	
			<div id="rejectClaim" class="modal hide fade" tabindex="-1">
				<div class="modal-header no-padding">
					<div class="table-header">
						<button type="button" class="close" data-dismiss="modal">&times;</button>
						${uiLabelMap.ClaimApproval}
					</div>
				</div>
				<div class="modal-body no-padding">
					<form id="rejectClaimForm" class="basic-form form-horizontal" action="<@ofbizUrl>updateEmplClaimStatus</@ofbizUrl>" method="post">
						<input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}">
						<input type="hidden" name="statusId" value="EMPL_CLAIM_REJECTED">
						<input type="hidden" name="emplClaimId" value="${emplClaim.emplClaimId}">
						<div class="control-group no-left-margin">
							<label class="control-label asterisk">${uiLabelMap.ClaimRejectReason}</label>
							<div class="controls">
								<input type="text" name="comment">
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">&nbsp;</label>
							<div class="controls">
								<button class="btn btn-small btn-primary" type="submit">
									<i class="icon-ok"></i>
									${uiLabelMap.CommonSubmit}
								</button>
							</div>
						</div>
					</form>
				</div>
			</div>
		</#if>
		<#if emplClaim.statusId == "EMPL_CLAIM_RESPONED" && userLogin.partyId == emplClaim.partyId>
			<div class="control-group no-left-margin">
				<label class="control-label">&nbsp;</label>
				<div class="controls" style="margin-left: 100px !important;">
					<form action="<@ofbizUrl>updateEmplClaimStatus</@ofbizUrl>" method="post" style=" display: inline;">
						<input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}">
						<input type="hidden" name="statusId" value="EMPL_CLAIM_COMPLETED">
						<input type="hidden" name="emplClaimId" value="${emplClaim.emplClaimId}">
						<button class="btn btn-small btn-primary" type="submit">
							<i class="icon-ok open-sans"></i>
							${uiLabelMap.CommonSubmit}
						</button>
					</form>					
					&nbsp;
					<a href="#ReEmplClaim" data-toggle="modal" role="button" class="btn btn-small btn-danger">
						<i class="icon-remove open-sans"></i>
						${uiLabelMap.CommonNoOk}
					</a>
				</div>
			</div>
			<div id="ReEmplClaim" class="modal hide fade" tabindex="-1">
				<div class="modal-header no-padding">
					<div class="table-header">
						<button type="button" class="close" data-dismiss="modal">&times;</button>
						${uiLabelMap.ReSendClaim}
					</div>
				</div>
				<div class="modal-body no-padding">
					<form id="reEmplClaim" class="basic-form form-horizontal" action="<@ofbizUrl>createEmplClaim</@ofbizUrl>" method="post">
						<input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}">
						<input type="hidden" name="statusId" value="EMPL_CLAIM_CREATED">
						<input type="hidden" name="emplClaimIdTo" value="${emplClaim.emplClaimId}">
						<input type="hidden" name="claimTypeId" value="KHIEU_NAI_LAI">
						
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.EmplClaimIdAssoc}</label>
							<div class="controls">
								${emplClaim.emplClaimId}
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.EmplClaimType}</label>
							<div class="controls">
								<#assign reClaimType = delegator.findOne("ClaimType", Static["org.ofbiz.base.util.UtilMisc"].toMap("claimTypeId", "KHIEU_NAI_LAI"), false)>
								${reClaimType.description?if_exists}
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.ClaimSettlement}</label>
							<div class="controls">
								<#list emplClaimSettlementList as settlement>
									<#assign partySettlement = delegator.findOne("Person", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", settlement.partyId), false)>
									${partySettlement.lastName?if_exists} ${partySettlement.middleName?if_exists} ${partySettlement.firstName?if_exists} 
									<#if settlement_has_next>,</#if>		 
								</#list>
								<select name="partyClaimSettlement" style="display: none;">
									<#list emplClaimSettlementList as settlement>
										<option value="${settlement.partyId}" selected="selected"></option>
									</#list>
								</select>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label asterisk">${uiLabelMap.NotificationHeader}</label>
							<div class="controls">
								<input type="text" name="title">
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.CommonDescription}</label>
							<div class="controls">
								<input type="text" name="description">
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">&nbsp;</label>
							<div class="controls">
								<button class="btn btn-small btn-primary" type="submit">
									<i class="icon-ok"></i>
									${uiLabelMap.SendClaim}
								</button>
							</div>
						</div>
					</form>
				</div>
			</div>
		</#if>
	</div>
</div>
<script type="text/javascript">
	jQuery(document).ready(function() {
		$('#responeClaimForm').validate({
			errorElement: 'span',
			errorClass: 'help-inline red-color',
			errorPlacement: function(error, element) {
				element.addClass("border-error");
	    		if (element.parent() != null ){   
					element.parent().find("button").addClass("button-border");     			
	    			error.appendTo(element.parent());
				}
	    	  },
	    	unhighlight: function(element, errorClass) {
	    		$(element).removeClass("border-error");
	    		$(element).parent().find("button").removeClass("button-border");
	    	},
			focusInvalid: false,
			rules: {
				comment: {
					required: true,
				}
				
			},

			messages: {
				comment: {
					required: "<span style='color:red;'>${uiLabelMap.CommonRequired}</span>",
				},			
			},

			invalidHandler: function (event, validator) { //display error alert on form submit   
				$('.alert-error', $('.login-form')).show();
			},

			highlight: function (e) {
				$(e).closest('.control-group').removeClass('info').addClass('error');
			},

			success: function (e) {
				$(e).closest('.control-group').removeClass('error').addClass('info');
				$(e).remove();
			},

			submitHandler: function (form) {
				form.submit();
			},
			invalidHandler: function (form) {
			}
			
		});
		
		$('#rejectClaimForm').validate({
			errorElement: 'span',
			errorClass: 'help-inline red-color',
			errorPlacement: function(error, element) {
				element.addClass("border-error");
	    		if (element.parent() != null ){   
					element.parent().find("button").addClass("button-border");     			
	    			error.appendTo(element.parent());
				}
	    	  },
	    	unhighlight: function(element, errorClass) {
	    		$(element).removeClass("border-error");
	    		$(element).parent().find("button").removeClass("button-border");
	    	},
			focusInvalid: false,
			rules: {
				comment: {
					required: true,
				}
				
			},

			messages: {
				comment: {
					required: "<span style='color:red;'>${uiLabelMap.CommonRequired}</span>",
				},			
			},

			invalidHandler: function (event, validator) { //display error alert on form submit   
				$('.alert-error', $('.login-form')).show();
			},

			highlight: function (e) {
				$(e).closest('.control-group').removeClass('info').addClass('error');
			},

			success: function (e) {
				$(e).closest('.control-group').removeClass('error').addClass('info');
				$(e).remove();
			},

			submitHandler: function (form) {
				form.submit();
			},
			invalidHandler: function (form) {
			}
			
		});
		$('#reEmplClaim').validate({
			errorElement: 'span',
			errorClass: 'help-inline red-color',
			errorPlacement: function(error, element) {
				element.addClass("border-error");
	    		if (element.parent() != null ){   
					element.parent().find("button").addClass("button-border");     			
	    			error.appendTo(element.parent());
				}
	    	  },
	    	unhighlight: function(element, errorClass) {
	    		$(element).removeClass("border-error");
	    		$(element).parent().find("button").removeClass("button-border");
	    	},
			focusInvalid: false,
			rules: {
				title: {
					required: true,
				}
				
			},

			messages: {
				title: {
					required: "<span style='color:red;'>${uiLabelMap.CommonRequired}</span>",
				},			
			},

			invalidHandler: function (event, validator) { //display error alert on form submit   
				$('.alert-error', $('.login-form')).show();
			},

			highlight: function (e) {
				$(e).closest('.control-group').removeClass('info').addClass('error');
			},

			success: function (e) {
				$(e).closest('.control-group').removeClass('error').addClass('info');
				$(e).remove();
			},

			submitHandler: function (form) {
				form.submit();
			},
			invalidHandler: function (form) {
			}
			
		});
	});
</script>