<div class="widget-body">	 
	<div class="widget-main">
		<div class="row-fluid">
				<form name="ListPayrollReport" id="ListPayrollReport" method = "post" action = "<@ofbizUrl>calcPayroll</@ofbizUrl>">
					   	<table cellspacing="0" style="width: 100%;">
					   		<tbody>
					   			<tr>
									<td style="width: 200px"><label class="padding-bottom5 padding-right15" for="formulaList">${uiLabelMap.formulaCode}</label></td>
      								<td>
      									<@htmlTemplate.renderPayrollFormula name="formulaList" id="formulaList" payrollFormulaList=parollFormula divId="jqxPayrollBox1" width="90%" height="200px"/>
      								</td>
								</tr>
								<tr>
									<td>
										&nbsp
									</td>
									<td>
										&nbsp
									</td>
								</tr>
								<tr>
									<td>
										<label class="padding-bottom5 padding-right15" >
											${uiLabelMap.Department}
										</label>
									</td>
									<td>
										<select name="departmentList" multiple="multiple" class="chzn-select">
											<#list directChildDepartment as department>
												<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, department.partyId, false)>
												<option value="${department.partyId}" <#if parameters.departmentList?exists && parameters.departmentList?contains(department.partyId)>selected="selected"</#if>>${partyName}</option>
											</#list> 			
										</select>
									</td>
								</tr>
								<tr>
									<td>
										<label class="padding-bottom5 padding-right15" >
											${uiLabelMap.CalcSalaryPeriod}
										</label>
									</td>
									<td>
										<select name="periodTypeId">
											<#list periodTypes as period>
												<option value="${period.periodTypeId}" <#if parameters.periodTypeId?exists && parameters.periodTypeId==period.periodTypeId>selected="selected"</#if>>${period.description}</option>
											</#list>
										</select>
									</td>
								</tr>
								<tr>
									<td><label class="padding-bottom5 padding-right15" for="fromDate">${uiLabelMap.FromDate}</label></td>
      								<td>
      									<@htmlTemplate.renderDateTimeField name="fromDate" value="${parameters.fromDate?if_exists}" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="fromDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      								</td>
								</tr>
								
								<tr>
									<td><label class="padding-bottom5 padding-right15" for="thruDate">${uiLabelMap.ThruDate}</label></td>
      								<td>
      									<@htmlTemplate.renderDateTimeField name="thruDate" value="${parameters.thruDate?if_exists}" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="thruDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
      								</td>
								</tr>
							</tbody>
						</table>
							<div style="margin-left:20%">
	  							<button class="btn btn-primary btn-small" type="submit">
											 <i class = "icon-ok" ></i>
	              							${uiLabelMap.HrolbiusFomular}
	          					</button>
      						</div>
					</form>
		</div>

	</div><!--/widget-main-->
</div> <!-- /widget-body-->