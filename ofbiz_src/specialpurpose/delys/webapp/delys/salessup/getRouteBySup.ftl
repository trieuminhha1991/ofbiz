<#--
<#assign listRoute = delegator.findList("FindRouteBySup", null, null, null, null,false) />
-->

<div class="widget-body">	 
	<div class="widget-main">
		<div class="row-fluid">
			<#-- <#if hasApproved && currentId?exists && currentId == "salessup1"> -->

			<div id="list-agreement-term">
				<div style="float:left; height:auto; width:auto;">
					<#if listRoute?exists && listAll?has_content>
						<ul>
							<#list listRoute as ls>	
								<li><a id="customerInfo"  onclick="showCustomer('${ls.PartyIdTo?if_exists}')">${ls.PartyIdTo?if_exists}</a></li>			
							</#list>
						</ul>
					<#else>
						<div class="alert alert-info">${uiLabelMap.DANoItemToDisplay}</div>
					</#if>
				</div>
				 
				<div style="float:left; height:auto; width:auto;  margin-left:100px;" id="createCustomerInfo" >				
				</div>
				
				
				 <script type="text/javascript">
				 	function showCustomer(PartyIdTo){
				 		$.ajax({
			    			url: '<@ofbizUrl>customerInformation</@ofbizUrl>',
							type: 'GET',
							data: {
								partyIdTo:PartyIdTo
							},
							success: function(data){
								$("#createCustomerInfo").html(data);
							}  
			        	});
				 	}
			    	
			    </script>   
			<#--
			<#else>	
				<div class="alert alert-info">${uiLabelMap.DAQuotationUpdatePermissionError}</div>
			</#if>
			-->
			</div>
		</div>
	</div>
</div>
