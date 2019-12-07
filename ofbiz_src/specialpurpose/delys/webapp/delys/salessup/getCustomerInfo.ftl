<#--
<#assign listAll = delegator.findList("FindCustomerInfoByRoute", null, null, null, null,false) />
-->

<style type="text/css">
	table.table-padding48 td {
		padding: 4px 8px;
	}
</style>
<#if listAll?exists>
	<#-- <input type="hidden" id="partyIdTo" value="${listAll?size}"/> -->

	<table class="table table-bordered table-padding48">
		<tr>
			<td style="width:10px"><b>${uiLabelMap.DANo}</b></td>
			<td class="center"><b>RouteId</b></td>
			<td class="center"><b>${uiLabelMap.DSSalesCode}</b></td>
			<td class="center"><b>${uiLabelMap.DSFullName}</b></td>
			
		</tr>
		<#list listAll as partyItem>
			<tr>
				<td>${partyItem_index + 1}</td>
				<td>${partyItem.routeId?if_exists}</td>
				<td>${partyItem.customerId?if_exists}</td>
				<td>${partyItem.customerName?if_exists}</td>
				
			</tr>
		</#list>
	</table>
<#else>
	<div class="alert alert-info">${uiLabelMap.DANoItemToDisplay}</div>
</#if>

<#--

			  <div style="float:left; height:auto; width:auto;  margin-left:300px;">
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
								<td>${ls.PartyIdFrom?if_exists}</td>
								<td></td>
								<td></td>
								<td></td>	
								<td></td>
							</tr>
						</#list>
						</tbody>
					</table>
				</div>
-->