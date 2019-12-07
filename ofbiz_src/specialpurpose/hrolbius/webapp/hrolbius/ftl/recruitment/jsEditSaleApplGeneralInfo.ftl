/******************************************************Edit General Info********************************************************************/
	//Create lastName
	$("#lastName").jqxInput({width: 195});
	
	//Create middleName
	$("#middleName").jqxInput({width: 195});
	
	//Create firstName
	$("#firstName").jqxInput({width: 195});
	
	//Create firstName
	$("#firstName").jqxInput({width: 195});
	
	//Create height
	$("#height").jqxNumberInput({spinButtons: true, decimalDigits: 0, min: 0, inputMode: 'simple'});
	
	//Create weight
	$("#weight").jqxNumberInput({spinButtons: true, decimalDigits: 0, min: 0, inputMode: 'simple'});
	
	//Create idNumber
	$("#idNumber").jqxMaskedInput({
		 width: 195,
		 height: 21,
	     mask:'999999999999'
	 });
	
	//Create idIssuePlace
	$("#idIssuePlace").jqxInput({width: 195});
	
	//Create Gender
	$("#gender").jqxDropDownList({source: genderData, selectedIndex: 0, valueMember: "gender", displayMember: "description"});
	
	//Create birthDate
	$("#birthDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy', value: null});
	
	//Create nationality
	$("#birthPlace").jqxInput({width: 195});
	
	//Create idIssueDate
	$("#idIssueDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy', value: null});
	
	//Create maritalStatus
	$("#maritalStatus").jqxDropDownList({source: maritalStatusData, valueMember: 'maritalStatusId', displayMember: 'description'});
	
	//Create numberChildren
	$("#numberChildren").jqxNumberInput({spinButtons: true, decimalDigits: 0, min: 0, inputMode: 'simple'});
	
	//Create ethnicOrigin
	$("#ethnicOrigin").jqxDropDownList({source: ethnicOriginData, selectedIndex: 0, valueMember: 'ethnicOriginId', displayMember: 'description'});
	
	//Create religion
	$("#religion").jqxDropDownList({source: religionData, selectedIndex: 0, valueMember: 'religionId', displayMember: 'description'});
	
	//Create finAccountName
	$("#finAccountName").jqxInput({width: 195});
	
	//Create finAccountCode
	$("#finAccountCode").jqxInput({width: 195});
	
	//Create nativeLand
	$("#nativeLand").jqxInput({width: 195});
	
	$("#createNewApplicant").on('validationSuccess', function (event) {
		$("#jqxTabs").jqxTabs('enableAt', 1);
	});
	
	$("#createNewApplicant").jqxValidator({
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
			{input: '#maritalStatus', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close',
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
			{input: '#numberChildren', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur',
				rule: function (input, commit) {
	                if ($("#maritalStatus").val() == 'HADCHILD' && !input.val()) {
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