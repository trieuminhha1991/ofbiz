<div class="basic-form form-horizontal" style="margin-top: 10px">
	<form name="createEmployee" id="createEmployee">	
		<div class="row-fluid" >
    		<div class="span12">
    			<div class="span6">
    				<div class="control-group no-left-margin">
						<label class="control-label asterisk">${uiLabelMap.LastName}:</label>
						<div class="controls">
							<input type="text" name="lastName" id="lastName">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.MiddleName}:</label>
						<div class="controls">
							<input type="text" name="middleName" id="middleName">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label asterisk">${uiLabelMap.FirstName}:</label>
						<div class="controls">
							<input type="text" name="firstName" id="firstName">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.Gender}:</label>
						<div class="controls">
							<div id="gender"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label asterisk">${uiLabelMap.BirthDate}:</label>
						<div class="controls">
							<div id="birthDate"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.birthPlace}:</label>
						<div class="controls">
							<input id="birthPlace"></input>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.Height}(cm):</label>
						<div class="controls">
							<div id="height"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.Weight}(kg):</label>
						<div class="controls">
							<div id="weight"></div>
						</div>
					</div>
    			</div>
    			<div class="span6">
	    			<div class="control-group no-left-margin">
						<label class="control-label asterisk">${uiLabelMap.IDNumber}:</label>
						<div class="controls">
							<input id="idNumber" name="idNumber"></input>
						</div>
					</div>
	    			<div class="control-group no-left-margin">
						<label class="control-label asterisk">${uiLabelMap.IDIssuePlace}:</label>
						<div class="controls">
							<input id="idIssuePlace" name="idIssuePlace"></input>
						</div>
					</div>
	    			<div class="control-group no-left-margin">
						<label class="control-label asterisk">${uiLabelMap.IDIssueDate}:</label>
						<div class="controls">
							<div id="idIssueDate"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label asterisk">${uiLabelMap.MaritalStatus}:</label>
						<div class="controls">
							<div id="maritalStatus"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.NumberChildren} (${uiLabelMap.HRCommonIfHave}):</label>
						<div class="controls">
							<div id="numberChildren"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.EthnicOrigin}:</label>
						<div class="controls">
							<div id="ethnicOrigin"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.Religion}:</label>
						<div class="controls">
							<div id="religion"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.NativeLand}:</label>
						<div class="controls">
							<input id="nativeLand" name="nativeLand"></input>
						</div>
					</div>
				</div>
			</div>
		</div>
	</form>
</div>
<script type="text/javascript">
	var genderData = new Array();
	var row = {};
	row['gender'] = 'M';
	row['description'] = "${uiLabelMap.Male}";
	genderData[0] = row;
	
	var row = {};
	row['gender'] = 'F';
	row['description'] = "${uiLabelMap.Female}";
	genderData[1] = row;
	
	//Prepare data for maritalStatus 
	<#assign listMaritalStatus = delegator.findList("MaritalStatus", null, null, null, null, false) >
	var maritalStatusData = new Array();
	<#list listMaritalStatus as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['maritalStatusId'] = '${item.maritalStatusId?if_exists}';
		row['description'] = '${description}';
		maritalStatusData[${item_index}] = row;
	</#list>
	
	//Prepare data for ethnicOrigin 
	<#assign listEthnicOrigin = delegator.findList("EthnicOrigin", null, null, null, null, false) >
	var ethnicOriginData = new Array();
	<#list listEthnicOrigin as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['maritalStatusId'] = '${item.ethnicOriginId?if_exists}';
		row['description'] = '${description}';
		ethnicOriginData[${item_index}] = row;
	</#list>
	
	//Prepare data for religion 
	<#assign listReligion = delegator.findList("Religion", null, null, null, null, false) >
	var religionData = new Array();
	<#list listReligion as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['religionId'] = '${item.religionId?if_exists}';
		row['description'] = '${description}';
		religionData[${item_index}] = row;
	</#list>
	$(document).ready(function () {
		//Create lastName
		$("#lastName").jqxInput({width: 198, height: 25, theme: 'olbius'});
		
		//Create middleName
		$("#middleName").jqxInput({width: 198, height: 25, theme: 'olbius'});
		
		//Create firstName
		$("#firstName").jqxInput({width: 198, height: 25, theme: 'olbius'});
		
		//Create height
		$("#height").jqxNumberInput({spinButtons: true, decimalDigits: 0, min: 0, theme: 'olbius'});
		
		//Create weight
		$("#weight").jqxNumberInput({spinButtons: true, decimalDigits: 0, min: 0, theme: 'olbius'});
		
		//Create idNumber
		$("#idNumber").jqxMaskedInput({
			 width: 195,
			 height: 25,
		     mask:'999999999999',
		     theme: 'olbius'
		 });
		
		//Create idIssuePlace
		$("#idIssuePlace").jqxInput({width: 198, height: 25, theme: 'olbius'});
		
		//Create Gender
		$("#gender").jqxDropDownList({source: genderData, selectedIndex: 0, valueMember: "gender", displayMember: "description", theme: 'olbius', autoDropDownHeight: true});
		
		//Create birthDate
		$("#birthDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy', value: null, theme: 'olbius'});
		
		//Create nationality
		$("#birthPlace").jqxInput({width: 198, height: 25, theme: 'olbius'});
		
		//Create idIssueDate
		$("#idIssueDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy', value: null, theme: 'olbius'});
		
		//Create maritalStatus
		$("#maritalStatus").jqxDropDownList({source: maritalStatusData, selectedIndex: 0, valueMember: 'maritalStatusId', displayMember: 'description', theme: 'olbius'});
		<#if (listMaritalStatus?size < 8) >
			$("#maritalStatus").jqxDropDownList({autoDropDownHeight: true});
		</#if>
		
		//Create numberChildren
		$("#numberChildren").jqxNumberInput({spinButtons: true, decimalDigits: 0, min: 0});
		
		//Create ethnicOrigin
		$("#ethnicOrigin").jqxDropDownList({source: ethnicOriginData, selectedIndex: 0, valueMember: 'ethnicOriginId', displayMember: 'description', theme: 'olbius'});
		<#if (listEthnicOrigin?size < 8)>
			$("#ethnicOrigin").jqxDropDownList({autoDropDownHeight: true});
		</#if>
		//Create religion
		$("#religion").jqxDropDownList({source: religionData, selectedIndex: 0, valueMember: 'religionId', displayMember: 'description', theme: 'olbius'});
		<#if (listReligion?size < 8)>
			$("#religion").jqxDropDownList({autoDropDownHeight: true});
		</#if>
		
		//Create nativeLand
		$("#nativeLand").jqxInput({width: 198, height: 25, theme: 'olbius'});
		
		$("#createEmployee").on('validationSuccess', function (event) {
			$("#jqxTabs").jqxTabs('enableAt', 1);
		});
		
		$("#createEmployee").jqxValidator({
			rules: [
				{input: '#firstName', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required'},
				{input: '#idIssuePlace', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required'},
				{input: '#lastName', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required'},
				{input: '#height', message: '${uiLabelMap.NumberFieldRequired}', action: 'keyup, blur',
					rule: function (input, commit) {
	                    if (isFinite(input.val())) {
	                        return true;
	                    }
	                    return false;
	                }
				},
				{input: '#birthDate', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close',
					rule: function (input, commit) {
		                if (input.val()) {
		                    return true;
		                }
		                return false;
		            }
				},
				{input: '#idNumber', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur',
					rule: function (input, commit) {
		                if (input.val().includes("_")) {
		                    return false;
		                }
		                return true;
		            }
				},
				{input: '#birthDate', message: '${uiLabelMap.LTCurrentDateRequired}', action: 'keyup, change, close',
					rule: function (input, commit) {
		                if (input.jqxDateTimeInput('getDate') < new Date()) {
		                    return true;
		                }
		                return false;
		            }
				},
				{input: '#idIssueDate', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close',
					rule: function (input, commit) {
			            if (input.val()) {
			                return true;
			            }
			            return false;
			        }
				},
				{input: '#idIssueDate', message: '${uiLabelMap.LTCurrentDateRequired}', action: 'keyup, change, close',
					rule: function (input, commit) {
		                if (input.jqxDateTimeInput('getDate') < new Date()) {
		                    return true;
		                }
		                return false;
		            }
				}
			]
		});
	});
	function getEmployeeGeneralInfo(){
		var infoArray = new Array();
		infoArray.push({"lastName": $("#lastName").val()});
		infoArray.push({"middleName": $("#middleName").val()});
		infoArray.push({"firstName": $("#firstName").val()});
		infoArray.push({'gender': $("#gender").val()});
		var birthDate = $("#birthDate").val('date');
		if(birthDate){
			infoArray.push({"birthDate": $("#birthDate").val('date').getTime()});			
		}
		infoArray.push({'birthPlace': $("#birthPlace").val()});
		infoArray.push({'height': $("#height").val()});
		infoArray.push({'weight': $("#weight").val()});
		infoArray.push({'idNumber': $("#idNumber").val()});
		infoArray.push({'idIssuePlace': $("#idIssuePlace").val()});
		infoArray.push({'idIssueDate': $("#idIssueDate").jqxDateTimeInput('getDate').getTime()});
		infoArray.push({'maritalStatus': $("#maritalStatus").val()});
		infoArray.push({'numberChildren': $("#numberChildren").val()});
		infoArray.push({'ethnicOrigin': $("#ethnicOrigin").val()});
		infoArray.push({'religion': $("#religion").val()});
		infoArray.push({'nativeLand': $("#nativeLand").val()});
		return infoArray; 
	}
</script>