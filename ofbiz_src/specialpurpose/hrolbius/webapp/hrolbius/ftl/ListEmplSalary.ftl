<div style="overflow-x:scroll">
	<table class="table table-striped table-bordered table-hover" style="max-width: 100%">
		<thead>
			<tr>
				<th>${uiLabelMap.employee}</th>
				<th>${uiLabelMap.CommonFromDate}</th>
				<th>${uiLabelMap.CommonThruDate}</th>
				<#list allCodeList?if_exists as code>
					<#assign formula = delegator.findOne("PayrollFormula", Static["org.ofbiz.base.util.UtilMisc"].toMap("code", code), false)>
					<th>
					<#if formula?has_content>
					${formula.name?if_exists}
					<#else>
						${code}
					</#if>
					</th>
				</#list>
				<th>${uiLabelMap.CommonStatus}</th>
			</tr>
		</thead>
		<tbody>
			<#list entityEmplParametersList as entityEmplParameters>
				<tr>
					<#assign tmpPartyId = entityEmplParameters.partyId/>
					<#assign fromDate = entityEmplParameters.getEmplParameters().get(0).fromDate>
					<#assign thruDate = entityEmplParameters.getEmplParameters().get(0).thruDate>
					<#assign code = entityEmplParameters.getEmplParameters().get(0).code>
					<#assign payrollTable = delegator.findOne("PayrollTable", Static["org.ofbiz.base.util.UtilMisc"].toMap("code", code, "partyId", tmpPartyId, "fromDate", fromDate, "thruDate", thruDate), false)>  
					<td>
					<#assign employeeName= Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator,tmpPartyId,false)>
					${employeeName}
					</td>	
					<td>${fromDate?string["dd/MM/yyyy"]}</td>
					<td>${thruDate?string["dd/MM/yyyy"]}</td>				
					<#assign emplParameters = entityEmplParameters.getEmplParameters()>
					<#list emplParameters as emplParam>
						<td>
							<b class="green">
								<@ofbizCurrency amount= emplParam.value?number?round isoCode=currencyUomId />
							</b>
						</td>
					</#list>
					<td>
						<b class="blue">
						<#assign statusItem = delegator.findOne("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", payrollTable.statusId), false)>	
						${statusItem.description}
						</b>
					</td>
				</tr>
			</#list>			
		</tbody>
	</table>
</div>				