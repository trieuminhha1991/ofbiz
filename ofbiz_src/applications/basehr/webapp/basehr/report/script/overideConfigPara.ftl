<script>
	//Get past_date : beginning of current month
	//Get cur_date : today
	var cur_date = new Date();
	var past_date = new Date(cur_date);
	past_date.setDate(1);
	//Get Organization Tree
	<#if !rootOrgList?exists>
	<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
	</#if>
	var globalVar = {
		rootPartyArr: [
				<#if rootOrgList?has_content>
					<#list rootOrgList as rootOrgId>
					<#assign rootOrg = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", rootOrgId), false)/>
					{
						partyId: "${rootOrgId}",
						partyName: "${rootOrg.groupName}"
					},
					</#list>
				</#if>
			],
	};
	var rootPartyArr = [];
	for(var x in globalVar.rootPartyArr){
		var tmp = {
				partyId : globalVar.rootPartyArr[x].partyId,
				partyName : globalVar.rootPartyArr[x].partyName
		};
		rootPartyArr.push(tmp);
	};
	
	var emplList = new Array();
	//Send request to get Employee
	var getEmployee = function(partyId){
		$.ajax({
			url: 'getEmplByOrg',
			type: 'POST',
			data: {'partyId': partyId},
			async: false,
			dataType: 'json',
			success: function(data) {
				emplList = data['listIterator'];
			}
		});
	}
	getEmployee(rootPartyArr[0]['partyId']);
	//Get Code List
	<#if !codeList?exists>
		<#assign codeList = delegator.findList("PayrollFormula", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("payrollCharacteristicId", Static["org.ofbiz.entity.condition.EntityJoinOperator"].NOT_EQUAL, null), null, null, null, true) />
		var codeList = [<#list codeList as item>
							{ 'value' : '${item.code}', 'text' : '${StringUtil.wrapString(item.name)}'},
						</#list>
	                ]
	</#if>
var customDate = [
	          		/* {'text': '${StringUtil.wrapString(uiLabelMap.DayLabel)}', 'value': 'dd'},
	          		{'text': '${StringUtil.wrapString(uiLabelMap.WeekLabel)}', 'value': 'ww'}, */
	          		{'text': '${StringUtil.wrapString(uiLabelMap.MonthLabel)}', 'value': 'mm'},
	          		{'text': '${StringUtil.wrapString(uiLabelMap.QuarterLabel)}', 'value': 'qq'},
	          		{'text': '${StringUtil.wrapString(uiLabelMap.YearLabel)}', 'value': 'yy'},
	          		{'text': '${StringUtil.wrapString(uiLabelMap.OtherLabel)}', 'value': 'oo'}
	          	];
</script>