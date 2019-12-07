<#--<!-- <table>
	<tr>
		<td><label class="padding-bottom5 padding-right15" for="employeePositionTypeId">${uiLabelMap.EmployeePositionTypeId}</label></td>
							<td>
 								<#assign emplPositionTypeList = delegator.findList("EmplPositionType", null , null, orderBy,null, false)>
			 <select name = "emplPositionTypeId" id = "employeePositionTypeId">
			 	<option value="">
					&nbsp
			 	</option>
			 	<#list emplPositionTypeList as emplPositionType>
			 		<option value="${emplPositionType.emplPositionTypeId}">
			 			${emplPositionType.description?if_exists}
			 		</option>
			 	</#list>
			 </select>
							</td>
	</tr>
	<tr>
		<td><label class="padding-bottom5 padding-right15" for="actualFromDate">${uiLabelMap.ActualFromDate}</label></td>
							<td>
 								<@htmlTemplate.renderDateTimeField name="actualFromDate" value="" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="actualFromDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
							</td>
	</tr>
	<tr>
		<td><label class="padding-bottom5 padding-right15" for="actualThruDate">${uiLabelMap.ActualThruDate}</label></td>
							<td>
 								<@htmlTemplate.renderDateTimeField name="actualThruDate" value="" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="actualThruDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
							</td>
	</tr>
</table> -->
<div class="row-fluid" style="margin-top: 20px">
	<div class="span12">
		<div class="row-fluid form-window-content">
			<form action="" id="workInfoForm">
				<div class='span6'>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right asterisk'>
							${uiLabelMap.EmployeePositionTypeId}
						</div>
						<div class='span7'>
							<div id="jqxDropdownEmplPositionType"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right asterisk'>
							${uiLabelMap.ActualFromDate}
						</div>
						<div class='span7'>
							<div id="actualFromDate"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right'>
							${uiLabelMap.ActualThruDate}
						</div>
						<div class='span7'>
							<div id="actualThruDate"></div>
						</div>
					</div>
				</div>
			</form>
		</div>
	</div>
</div>
<script type="text/javascript">
	<#assign emplPositionTypeList = delegator.findList("EmplPositionType", 
			Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("emplPositionTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].NOT_EQUAL, "_NA_"), null, null, null, false)/>
	var emplPositionTypeArr = [
		<#list emplPositionTypeList as emplPositionType>
			{
				emplPositionTypeId: "${emplPositionType.emplPositionTypeId}",
				description: "${StringUtil.wrapString(emplPositionType.description)}"
			}
			<#if emplPositionType_has_next>
				,
			</#if>
		</#list>		                           
	];
	var sourceEmplPositionType = {
			localdata: emplPositionTypeArr,
            datatype: "array"
	};
	var dataAdapter = new $.jqx.dataAdapter(sourceEmplPositionType);
	$(document).ready(function () {
		$("#workInfoForm").jqxValidator({
			rules: [
				{input: "#jqxDropdownEmplPositionType", message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur',
					rule: function (input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}	
				},
				{input: "#actualFromDate", message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur',rule: 'required'},
				{input: "#actualThruDate", 
					message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))} ${StringUtil.wrapString(uiLabelMap.CommonAnd)} ${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherFromDate)}',
					action: 'keyup, blur',
					rule: function (input, commit) {
						var fromDate = $("#actualFromDate").jqxDateTimeInput('val', 'date');
						var thruDate = input.jqxDateTimeInput('val', 'date');
						if(thruDate){
							if(thruDate.getTime() <= fromDate.getTime()){
								return false;
							}							
						}
						return true;
					}
				}
	        ]
		});
		
		$("#workInfoForm").on('validationSuccess', function (event) {
			$("#jqxTabs").jqxTabs('enableAt', 6);
		});
		$("#jqxDropdownEmplPositionType").jqxDropDownList({
			source: dataAdapter,  displayMember: "description", valueMember: "emplPositionTypeId", theme: 'olbius',
	    		height: 25, width: 195, theme: 'olbius', placeHolder:'${StringUtil.wrapString(uiLabelMap.ChooseEmplPositionType)}',
	    		renderer: function (index, label, value) {
					for(i=0; i < emplPositionTypeArr.length; i++){
						if(emplPositionTypeArr[i].emplPositionTypeId == value){
							return emplPositionTypeArr[i].description;
						}
					}
				    return value;
				}
		});
		$("#actualFromDate").jqxDateTimeInput({width: '195px', height: '25px', theme: 'olbius'});
		$("#actualThruDate").jqxDateTimeInput({width: '195px', height: '25px', theme: 'olbius'});
		$("#actualThruDate").val(null);
	});
	
	function getEmployeeWorkInfo(){
		var workInfo = new Array();
		workInfo.push({"emplPositionTypeId": $("#jqxDropdownEmplPositionType").val()});
		workInfo.push({"actualFromDate": $("#actualFromDate").jqxDateTimeInput('getDate').getTime()});
		if($("#actualThruDate").jqxDateTimeInput('getDate')){
			workInfo.push({"actualThruDate": $("#actualThruDate").jqxDateTimeInput('getDate').getTime()});			
		}
		return workInfo; 
	}
</script>