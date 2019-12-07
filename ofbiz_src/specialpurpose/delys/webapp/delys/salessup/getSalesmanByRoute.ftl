<#--
<#assign listAll = delegator.findList("FindSalesmanOfSup", null, null, null, null,false) />
-->


<#--
<div>
	<ul id="test">
		
	</ul>
</div>
<button onclick="updateBuzz()">Ok</button>
-->

<div class="widget-body">	 
	<div class="widget-main">
		<div class="row-fluid">
			<div style="text-align:right">
				<h5 class="lighter block green" style="float:left"><b>${uiLabelMap.DSSalesmanInfo}</b></h5>
			</div>
			<div style="clear:both"></div>
			
			<#-- <#if hasApproved && currentId?exists && currentId == "salessup1"> -->
			<div id="list-agreement-term">
				<#if listAll?exists && listAll?has_content>
					<table class="table table-striped table-bordered table-hover">
						<thead>
							<tr>
								<th style="width:10px">${uiLabelMap.DANo}</th>
								<th class="center">${uiLabelMap.DSSalesCode}</th>
								<th class="center">${uiLabelMap.DSFullName}</th>
								<th class="center">${uiLabelMap.DSBirthDay}</th>
								<th class="center">${uiLabelMap.DSRoleType}</th>
								<th class="center">${uiLabelMap.DSManagementRegional}</th>
								<th class="center">${uiLabelMap.DSNotes}</th>
							</tr>
						</thead>
						
						<tbody>
						<#list listAll as ls>
							<tr>
								<td>${ls_index + 1}</td>
								<td>${ls.Role?if_exists}</td>
								<td>${ls.FirstName?if_exists} ${ls.MiddleName?if_exists} ${ls.LastName?if_exists}</td>
								<td>
									<#if ls.BirthDate?has_content>
		            					${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(ls.BirthDate, "dd/MM/yyyy", locale, timeZone)!}
	            					</#if>
								</td>	
								<td></td>
								<td></td>	
								<td></td>
							</tr>
						</#list>
						</tbody>
						
					</table>
				<#else>
					<div class="alert alert-info">${uiLabelMap.DANoItemToDisplay}</div>
				</#if>
			</div>

			<#--
			<#else>	
				<div class="alert alert-info">${uiLabelMap.DAQuotationUpdatePermissionError}</div>
			</#if>
			-->

		</div>
	</div>
</div>
