<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<title>${uiLabelMap.HRPayslips}</title>
</head>
<body>
	<div id="wrapper" style="width:800px;font-family: Arial;font-size:16px;margin:0 auto;">
		<table border="0" cellpadding="1" cellspacing="1" style="width:800px">			
			<tbody>
				<tr>
					<td colspan="2" rowspan="3">
						<#-- <img style="margin-right:20px;" alt="Delys" src="data:image/png;base64,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"/> -->
						<img style="margin-right:20px;" alt="Delys" src="http://www.uploadimgfast.com/images/2014/12/13/delys-logo.png"/>
					</td>
					<td colspan="10" rowspan="1"><b style="font-size:20px;font-weight:bold;">${companyName}</b></td>
				</tr>
				<tr>
					<td colspan="10" rowspan="1"><span style="padding:0;">
						${uiLabelMap.CommonAddress}: ${companyAddress?if_exists}</span></td>
				</tr>
				<tr>
					<td colspan="10" rowspan="1">
						<span style="padding:0;">
							${uiLabelMap.PartyPhoneNumber}: 
							<#if phoneNumber?has_content>
								<#list phoneNumber as number>
									${number.countryCode?if_exists} ${number.areaCode?if_exists} ${number.contactNumber?if_exists} 
									<#if number_has_next>
										–
									</#if> 
								</#list>
							<#else>
								${uiLabelMap.PhoneNotExists}
							</#if>
						</span>
					</td>
				</tr>
				<tr>
					<td colspan="12">&nbsp;</td>
				</tr>
				<tr>
					<td colspan="12"><center><span style="font-size:28px;font-weight:bold;">${uiLabelMap.HRPayslips}</span></center></td>
				</tr>
				<tr>
					<td colspan="12">&nbsp;</td>
				</tr>
				<tr>
					<td colspan="3" rowspan="1">${uiLabelMap.EmployeeName}:</td>
					<td colspan="3" rowspan="1"><b>${employeeName?if_exists}</b></td>
					<td colspan="3" rowspan="1">${uiLabelMap.HRSalaryPeriod}:</td>
					<td colspan="3" rowspan="1"><b>${uiLabelMap.CommonFrom} ${fromDate?string["dd/MM/yyyy"]} ${uiLabelMap.CommonTo} ${thruDate?string["dd/MM/yyyy"]}</b></td>
				</tr>
				<tr>
					<td colspan="3" rowspan="1">${uiLabelMap.EmployeeId}:</td>
					<td colspan="3" rowspan="1"><b>${employeeId}</b></td>
					<td colspan="3" rowspan="1">${uiLabelMap.DateJoinCompany}:</td>
					<td colspan="3" rowspan="1">
						<b>
							<#if dateJoin?exists>
								${dateJoin?string["dd/MM/yyyy"]}
							</#if> 
						</b>
					</td>
				</tr>
				<tr>
					<td colspan="3" rowspan="1">${uiLabelMap.Department}:</td>
					<td colspan="3" rowspan="1"><b>${emplDept?if_exists}</b></td>
					<td colspan="3" rowspan="1">${uiLabelMap.AccountNumber}:</td>
					<td colspan="3" rowspan="1"><b>1902895962673</b></td>
				</tr>
				<tr>
					<td colspan="3" rowspan="1">${uiLabelMap.HREmployeePosition}:</td>
					<td colspan="3" rowspan="1"><b>${emplPosition?if_exists}</b></td>
					<td colspan="3" rowspan="1">${uiLabelMap.NumberWorkDay}:</td>
					<td colspan="3" rowspan="1"><b>${numberWorkDay?if_exists}</b></td>
				</tr>
				<tr>
					<td colspan="12">&nbsp;</td>
				</tr>
				<tr>
					<td colspan="12"><span style="float:right; margin: 0 10px 10px 0;">${uiLabelMap.HRCurrency}: ${uomId}</span></td>
				</tr>
			</tbody>
		</table>
		<table id="bang-luong" cellpadding="0" cellspacing="0" style="width:800px;border-collapse:collapse;">
			<thead>
				<tr>
					<th style="border:1px solid #999;padding:8px 15px;">
						<center><span style="font-weight:bold;">${uiLabelMap.HRNotes}</span></center>
					</th>
					<th style="border:1px solid #999;padding:8px 15px;">
						<center><span style="font-weight:bold;">${uiLabelMap.SalaryType}</span></center>
					</th>
					<th style="border:1px solid #999;padding:8px 15px;">
						<center><span style="font-weight:bold;">${uiLabelMap.HRAmount}</span></center>
					</th>
				</tr>
			</thead>
			<tbody>
				<#if payrollIncomes?exists>
					<#assign rowspan = payrollIncomes?size + 1>
				<#else>
					<#assign rowspan = 1>	 
				</#if>
				<#if payrollDeductions?exists>
					<#assign rowspanDeduction = payrollDeductions?size + 1>
				<#else>
					<#assign rowspanDeduction = 1>	 
				</#if>
				<tr>
					<td style="border:1px solid #999;padding:8px 15px;" colspan="1" rowspan="${rowspan}"><span>I. ${uiLabelMap.HRIncomes}</span></td>					
				</tr>								
				<#if payrollIncomes?has_content>
					<#list payrollIncomes as income>
						<tr>
							<td style="border:1px solid #999;padding:8px 15px;"><span>${income.get("description", locale)}</span></td>
							<td style="border:1px solid #999;padding:8px 15px;"><span style="float:right;"><@ofbizCurrency amount=income.amount isoCode=uomId/></span></td>
						</tr>	
					</#list>
				</#if>
				<tr>
					<td style="border:1px solid #999;padding:8px 15px;" colspan="1" rowspan="${rowspanDeduction}">II. ${uiLabelMap.HRDeductions}</td>
				</tr>
				<#if payrollDeductions?has_content>
					<#list payrollDeductions as deduction>
						<tr>
							<td style="border:1px solid #999;padding:8px 15px;"><span>${deduction.get("description", locale)}</span></td>
							<td style="border:1px solid #999;padding:8px 15px;"><span style="float:right;"><@ofbizCurrency amount=deduction.amount isoCode=uomId/></span></td>
						</tr>	
					</#list>
				</#if>
				
				<tr>
					<td style="border:1px solid #999;padding:8px 15px;">III. ${uiLabelMap.SumSalary}</td>
					<td style="border:1px solid #999;padding:8px 15px;"><span>${uiLabelMap.ActualReceipt}</span></td>
					<td style="border:1px solid #999;padding:8px 15px;">
						<span style="font-weight:bold;font-size:120%;color:red;float:right;">
							<@ofbizCurrency amount=actualReceipt isoCode=uomId/>
						</span>
					</td>
				</tr>
			</tbody>
		</table>
		<p>Nếu có thắc mắc về bảng lương, xin vui lòng liên hệ phòng kế toán, điện thoại (04) 3555 8118. Xin cảm ơn!</p>
	</div>
</body>
</html>