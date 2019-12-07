<@jqGridMinimumLib />
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.AccountingEditAcctRecon}</h4>
	</div>
</div>
<div id="alterpopupWindow">
	<form action="<@ofbizUrl>updateGlReconciliation</@ofbizUrl>" method="post" id="updateGlReconciliation">
		<table>
			<tr>
				<td align="right">
					<span>${uiLabelMap.glReconciliationId}</span>
				</td>
				<td align="left">
					<span>
						<div id="glReconciliationId"></div>
						<input id="glReconciliationIdInput" name="glReconciliationId" type="hidden"/>
					</span>
				</td>
			</tr>
			<tr>
				<td align="right">
					<span>${uiLabelMap.glReconciliationName}</span>
				</td>
				<td align="left">
			       <span><input id="glReconciliationName" name="glReconciliationName"></input></span>
			    </td>
			</tr>
			<tr>
				<td align="right">
					<span>${uiLabelMap.description}</span>
				</td>
				<td align="left">
			       <span><input id="description" name="description"/></span>
			    </td>
			</tr>
			<tr>
			    <td align="right" style="min-width: 200px;">
					<span>${uiLabelMap.glAccountId}</span>
				</td>
				<td align="left">
					<span><div id="glAccountId" name="glAccountId"></div></span>
					<input name="glAccountId" type="hidden" />
				</td>
			</tr>
			<tr>
				<td align="right">
					<span>${uiLabelMap.statusId}</span>
				</td>
				<td align="left">
					<span><div id="statusId" name="statusId"></div></span>
				</td>
			</tr>
			<tr>
				<td align="right">
					<span>${uiLabelMap.reconciledDate}</span>
				</td>
				<td align="left">
					<span>
						<div id="reconciledDate" name="reconciledDate"></div>
					</span>
				</td>
			</tr>
			<tr>
				<td align="right">
					<span>${uiLabelMap.organizationPartyId}</span>
				</td>
				<td align="left">
					<span><div id="organizationPartyId" ></div></span>
					<input type="hidden" name="organizationPartyId"></input>
				</td>
			</tr>
			<tr>
				<td align="right">
					<span>${uiLabelMap.reconciledBalance}</span>
				</td>
				<td align="left">
					<span><div id="reconciledBalance" ></div></span>
					<input  name="reconciledBalance" type="hidden"></input>
				</td>
			</tr>
			<tr>
				<td align="right">
					<span>${uiLabelMap.openingBalance}</span>
				</td>
				<td align="left">
					<span><div id="openingBalance"></div></span>
					<input name="openingBalance" type="hidden"></input>
				</td>
			</tr>
			<tr>
				<td align="right">
					<span>${uiLabelMap.createdDate}</span>
				</td>
				<td align="left">
					<span><div id="createdDate" name="createdDate" ></div></span>
				</td>
			</tr>
			<tr>
				<td align="right">
					<span>${uiLabelMap.lastModifiedDate}</span>
				</td>
				<td align="left">
					<span><div id="lastModifiedDate" name="lastModifiedDate" ></div></span>
				</td>
			</tr>
			<tr>
		        <td align="right"></td>
		        <td style="padding-top: 10px;" align="left"><input style="margin-right: 5px;" type="button" id="alterSave" value="${uiLabelMap.CommonSave}" /></td>
		    </tr>
		</table>
	<form>
</div>		
<script>
	//get statusId data
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "GLREC_STATUS"), null, null, null, false) />
	var statData = new Array();
	<#list statuses as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists)/>
		row['statusId'] = '${item.statusId}';
		row['description'] = '${description}';
		statData[${item_index}] = row;
	</#list>
	
	//set glReconciliationId
	<#assign glReconciliationId = glReconciliation.glReconciliationId?if_exists />
	
	//set glReconciliationName
	<#assign glReconciliationName = StringUtil.wrapString(glReconciliation.glReconciliationName?if_exists) />
	
	//set description
	<#assign description = StringUtil.wrapString(glReconciliation.description?if_exists) />
	
	//set statusId
	<#assign statusId = glReconciliation.statusId?if_exists />
	var statusIndex = 0;
	for(i = 0; i < statData.length; i++){
		if(statData[i].statusId == '${statusId}'){
			statusIndex = i;
			break;
		}
	}
	
	//set glAccountId
	<#assign glAccountId = glReconciliation.glAccountId?if_exists />
	
	//set transactionDate
	<#assign reconciledDate = glReconciliation.reconciledDate?if_exists />
	var reconciledDate = new Date();
	if('${reconciledDate}' != null && '${reconciledDate}' != ''){
		reconciledDate = new Date('${reconciledDate}');
	}
	
	//set createdDate
	<#assign createdDate = glReconciliation.createdDate?if_exists />
	var createdDate = new Date();
	if('${createdDate}' != null && '${createdDate}' != ''){
		createdDate = new Date('${createdDate}');
	}
	
	//set lastModifiedDate
	<#assign lastModifiedDate = glReconciliation.lastModifiedDate?if_exists />
	var lastModifiedDate = new Date();
	if('${lastModifiedDate}' != null && '${lastModifiedDate}' != ''){
		lastModifiedDate = new Date('${lastModifiedDate}');
	}
	
	//set organizationPartyId
	<#assign organizationPartyId = glReconciliation.organizationPartyId?if_exists />
	
	//set reconciledBalance
	<#assign reconciledBalance = glReconciliation.reconciledBalance?if_exists />
	
	//set openingBalance
	<#assign openingBalance = glReconciliation.openingBalance?if_exists />
	
	//Create theme
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;  
	$('#glReconciliationId').text('${glReconciliationId}');
	$('input[name=glReconciliationId]').val('${glReconciliationId}');
	$('#glAccountId').text('${glAccountId}');
	$('input[name=glAccountId]').val('${glAccountId}');
	$('#organizationPartyId').text('${organizationPartyId}');
	$('input[name=organizationPartyId]').val('${organizationPartyId}');
	$('#reconciledBalance').text('${reconciledBalance}');
	$('input[name=reconciledBalance]').val('${reconciledBalance}');
	$('#openingBalance').text('${openingBalance}');
	$('input[name=openingBalance]').val('${openingBalance}');
	
	$("#alterSave").jqxButton({theme: theme, width: 100, height: 25});
	$("#glReconciliationName").jqxInput({width:195}).jqxInput('val', '${glReconciliationName}');
	$("#description").jqxInput({width:195}).jqxInput('val', '${description}');
	$("#statusId").jqxDropDownList({ theme: theme, source: statData, displayMember: "description", valueMember: "statusId", selectedIndex: statusIndex, width: '200', height: '25'});
	$("#reconciledDate").jqxDateTimeInput({value: reconciledDate, width: '200px', height: '25px', formatString: 'yyyy-MM-dd hh:mm:ss'});
	$("#createdDate").jqxDateTimeInput({value: createdDate, width: '200px', height: '25px', formatString: 'yyyy-MM-dd hh:mm:ss'});
	$("#lastModifiedDate").jqxDateTimeInput({value: lastModifiedDate, width: '200px', height: '25px', formatString: 'yyyy-MM-dd hh:mm:ss'});

	$('#alterSave').click(function(){
		$('#updateGlReconciliation').submit();
	});
</script>
<style type="text/css">
td span{
	margin-bottom: 10px;
	display: block;
	padding-right: 15px;
}
</style>