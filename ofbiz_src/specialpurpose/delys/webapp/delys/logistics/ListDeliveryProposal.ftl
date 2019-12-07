
<style type="text/css">
	.nav-pagesize select {
		padding:2px !important;
		margin:0 !important;
	}
</style>

<#-- delivery requirement list -->
<#-- <#if hasPermission> -->
  	<div class="widget-body">	
	 	<div class="widget-main">
			<div class="row-fluid">
				<div class="form-horizontal basic-custom-form form-decrease-padding" style="display: block;"> 
        			<table class="table table-striped table-bordered table-hover dataTable" cellspacing='0' style="width: 100%; margin-bottom: 2%;">
			          	<tr class="header-row">
			          		<td width="3%">${uiLabelMap.DANo}</td>
				            <td>${uiLabelMap.DADeliveryProposalId}</td>
				            <td>${uiLabelMap.DADescription}</td>
				            <td>${uiLabelMap.DARequirementStartDate}</td>
				            <td>${uiLabelMap.DARequiredByDate}</td>
				            <td>${uiLabelMap.DAStatus}</td>
			          	</tr>
          				<#list deliveryReqList as deliveryReqItem>
            				<#assign status = deliveryReqItem.getRelatedOne("StatusItem", true)>
            				<tr>
            					<td>${deliveryReqItem_index + 1}</td>
				              	<td><a href="<@ofbizUrl>getDetailDeliveryProposal?requirementId=${deliveryReqItem.requirementId}</@ofbizUrl>">${deliveryReqItem.requirementId}</a></td>
				              	<td>${deliveryReqItem.description?if_exists}</td>
				              	<td><#if deliveryReqItem.requirementStartDate?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(deliveryReqItem.requirementStartDate, "dd/MM/yyyy - HH:mm:ss", locale, timeZone)!}</#if></td>
				              	<td><#if deliveryReqItem.requiredByDate?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(deliveryReqItem.requiredByDate, "dd/MM/yyyy", locale, timeZone)!}</#if></td>
        						<td>${status.description?if_exists}</td>
        					</tr>
          				</#list>
          				<#if !deliveryReqList?has_content>
            				<tr><td colspan="10"><p class="alert alert-info">${uiLabelMap.DADeliveryProposalNoOrderFound}</p></td></tr>
          				</#if>
        			</table>
        			
    			</div>
    		</div>
    	</div>
    	<#-- Pagination -->
  	</div>
  	
  	<div id="modal-table" class="modal hide fade" tabindex="-1">
		<div class="modal-header no-padding">
			<div class="table-header">
				<button type="button" class="close" data-dismiss="modal" onclick="javascript:closeModelTable()">&times;</button>
				${uiLabelMap.DAViewCustomerDetail}
			</div>
		</div>
		<div class="modal-body no-padding">
			<div class="row-fluid">
				<table class="table table-striped table-bordered table-hover no-margin-bottom no-border-top">
					<tbody>
						<tr>
							<td>${uiLabelMap.DACustomerId}</td>
							<td><div id="modal-customerId"></div></td>
						</tr>
						<tr>
							<td>${uiLabelMap.DACustomerName}</td>
							<td><div id="modal-customerName"></div></td>
						</tr>
						<tr>
							<td>${uiLabelMap.DAOfficeSite}</td>
							<td><div id="modal-officeSite"></div></td>
						</tr>
						<tr>
							<td>${uiLabelMap.DALocal}</td>
							<td><div id="modal-local"></div></td>
						</tr>
						<tr>
							<td>${uiLabelMap.DAComments}</td>
							<td><div id="modal-comments"></div></td>
						</tr>
						<tr>
							<td>${uiLabelMap.DALogoImage}</td>
							<td><div id="modal-logoImage"></div></td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>

		<div class="modal-footer">
			<button class="btn btn-small btn-danger pull-left" data-dismiss="modal" onclick="javascript:closeModelTable();">
				<i class="icon-remove"></i>
				Close
			</button>
		</div>
	</div>
  	<script type="text/javascript">
  		function showCustomerDetail(partyId, groupName, officeSiteName, groupNameLocal, comments, logoImageUrl) {
  			$('#modal-customerId').html(partyId);
  			$('#modal-customerName').html(groupName);
  			$('#modal-officeSite').html(officeSiteName);
  			$('#modal-local').html(groupNameLocal);
  			$('#modal-comments').html(comments);
  			$('#modal-logoImage').html(logoImageUrl);
  			
  			$('#modal-table').slideDown(500);
  			$('#modal-table-background').show();
			$('#modal-table').css("opacity", "1", "important");
			$('#modal-table').css("top", "10%");
  		}
  		function closeModelTable() {
  			$('#modal-table').slideUp(500);
			setTimeout(function(){
			    $('#modal-table-background').hide();
		  	}, 600);
  		}
  	</script>
  	<style type="text/css">
  		#partyDetailModel:hover {
  			text-decoration: underline;
  		}
  	</style>
  	<div id="modal-table-background" class="modal-backdrop hide fade in" 
		onclick="javascript:closeModelTable();" style="z-index:990"></div>
<#--
<#else>
  	<div><p class="alert alert-danger">${uiLabelMap.OrderViewPermissionError}</p></div>
</#if>
-->
