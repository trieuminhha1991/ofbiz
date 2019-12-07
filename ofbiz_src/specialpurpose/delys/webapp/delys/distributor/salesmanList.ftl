<div class="widget-body">	 
	<div class="widget-main">
		<div class="row-fluid">
			<#if listSalesman?exists && listSalesman?has_content>
				<table class="table table-striped table-bordered table-hover">
					<thead>
						<tr>
							<th style="width:10px">${uiLabelMap.DANo}</th>
							<th class="center">${uiLabelMap.DASalesmanId}</th>
							<th class="center">${uiLabelMap.DAName}</th>
							<th class="center">${uiLabelMap.DAAddress}</th>
						</tr>
					</thead>
					<tbody>
					<#list listSalesman as smItem>
						<tr>
							<td>${smItem_index + 1}</td>
							<td>${smItem.partyId?if_exists}</td>
							<td>
								<#if "PERSON" == smItem.partyTypeId>
									${smItem.lastName?if_exists} ${smItem.middleName?if_exists} ${smItem.firstName?if_exists}
								<#elseif "PARTY_GROUP" == smItem.partyTypeId>
									${smItem.groupName?if_exists}
								</#if>
							</td>
							<td>
								<#assign addresses = delegator.findByAnd("PartyAndContactMech", {"partyId" : smItem.partyId, "contactMechTypeId" : "POSTAL_ADDRESS"})/>
								<#list addresses as address>
									${address.paAddress1?if_exists}<#if address.paCity?exists>, ${address.paCity}</#if>
									<#if address?has_content><br/></#if>
								</#list>
							</td>
						</tr>
					</#list>
					</tbody>
				</table>
			<#else>
				<div class="alert alert-info">${uiLabelMap.DANoItemToDisplay}</div>
			</#if>
		</div>
	</div>
</div>