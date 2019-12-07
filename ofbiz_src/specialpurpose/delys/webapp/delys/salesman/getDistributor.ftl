<div class="widget-box" id="distributorList" style="border-bottom: none;">
		 <div class="widget-header widget-header-small header-color-blue2">
		  	<h5>${uiLabelMap.PageTitleListDistributorItems}</h5>
	  	</div>
		 <div class="widget-body" style="border-bottom: none;">
		 <#if parameters.distributorList?has_content>
			
				<table width="100%" border="0" cellspacing="0" cellpadding="0" class="table table-striped table-bordered dataTable table-hover">
					<tr>
						<th>${uiLabelMap.distributorId}</th>
						<th>${uiLabelMap.distributorName}</th>
						<th>&nbsp</th>
					</tr>
					<#list parameters.distributorList as distributor>
					<tr>
						<td>${distributor.partyId}</td>
						<td>${distributor.groupName}</td>
						<td> <a class="btn btn-primary btn-mini" href="/hrdelys/control/OrganizationProfile?partyId=${distributor.partyId}">Details&nbsp;<i class="icon-info-sign"></i></a></td>
					</tr>
					</#list>
					<#else>
						<p class="alert alert-info">No record found</p>
					</#if>
				</table>
		</div>
</div>	 
