<div class="widget-box" id="salesmanList" style="border-bottom: none">
	  	<div class="widget-header widget-header-small header-color-blue2">
		  	<h5>${uiLabelMap.PageTitleListSalemanItems}</h5>
	  	</div>
		 <div class="widget-body" style="border-bottom: none;">
		 <#if parameters.salesmanList?has_content>
			
				<table width="100%" border="0" cellspacing="0" cellpadding="0" class="table table-striped table-bordered dataTable table-hover">
					<tr>
						<th>${uiLabelMap.salesmanId}</th>
						<th>${uiLabelMap.firstName}</th>
						<th>${uiLabelMap.middleName}</th>
						<th>${uiLabelMap.lastName}</th>
						<th>&nbsp</th>
					</tr>
					<#list parameters.salesmanList as salesman>
					<tr>
						<td>${salesman.partyId?if_exists}</td>
						<td>${salesman.firstName?if_exists}</td>
						<td>${salesman.middleName?if_exists}</td>
						<td>${salesman.lastName?if_exists}</td>
						<td> <a class="btn btn-primary btn-mini" href="/hrdelys/control/EmployeeProfile?&partyId=${salesman.partyId}">Details&nbsp;<i class="icon-info-sign"></i></a></td>
					</tr>
					</#list>
					<#else>
						<p class="alert alert-info">No record found</p>
					</#if>
				</table>
		</div>
</div>	 