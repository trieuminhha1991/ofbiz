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
	$("#maritalStatus").jqxDropDownList({source: maritalStatusData, selectedIndex: 0, valueMember: 'maritalStatusId', displayMember: 'description'});
	
	//Create numberChildren
	$("#numberChildren").jqxNumberInput({spinButtons: true, decimalDigits: 0, min: 0, inputMode: 'simple'});
	
	//Create ethnicOrigin
	$("#ethnicOrigin").jqxDropDownList({source: ethnicOriginData, selectedIndex: 0, valueMember: 'ethnicOriginId', displayMember: 'description'});
	
	//Create religion
	$("#religion").jqxDropDownList({source: religionData, selectedIndex: 0, valueMember: 'religionId', displayMember: 'description'});
	
	//Create sourceTypeId
	$("#sourceTypeId").jqxDropDownList({source: emplAppSourceTypeData, selectedIndex: 0, valueMember: 'employmentAppSourceTypeId', displayMember: 'description'});
	
	//Create nativeLand
	$("#nativeLand").jqxInput({width: 195});
	
	//Party Grid
	var sourceParty =
	{
			datafields:
				[
				 { name: 'partyId', type: 'string' },
				 { name: 'groupName', type: 'string' },
				 { name: 'firstName', type: 'string' },
				 { name: 'middleName', type: 'string' },
				 { name: 'lastName', type: 'string' }
				],
			cache: false,
			root: 'results',
			datatype: "json",
			updaterow: function (rowid, rowdata) {
				// synchronize with the server - send update command   
			},
			beforeprocessing: function (data) {
				sourceParty.totalrecords = data.TotalRows;
			},
			filter: function () {
				// update the grid and send a request to the server.
				$("#jqxReferredByPartyGridId").jqxGrid('updatebounddata');
			},
			pager: function (pagenum, pagesize, oldpagenum) {
				// callback called when a page or page size is changed.
			},
			sort: function () {
				$("#jqxReferredByPartyGridId").jqxGrid('updatebounddata');
			},
			sortcolumn: 'partyId',
			sortdirection: 'asc',
			type: 'POST',
			data: {
				noConditionFind: 'Y',
				conditionsFind: 'N',
			},
			pagesize:15,
			contentType: 'application/x-www-form-urlencoded',
			url: 'jqxGeneralServicer?sname=getListParty',
	};
	var dataAdapterParty = new $.jqx.dataAdapter(sourceParty);
	$("#referredByPartyId").jqxDropDownButton({ width: 200, height: 25});
	$("#jqxReferredByPartyGridId").jqxGrid({
		source: dataAdapterParty,
		filterable: true,
		showfilterrow: true,
		virtualmode: true, 
		sortable:true,
		theme: theme,
		editable: false,
		autoheight:true,
		pageable: true,
		width: 800,
		rendergridrows: function(obj)
		{
			return obj.data;
		},
	columns: [
	  { text: '${uiLabelMap.CommonId}', datafield: 'partyId', filtertype: 'input', width: 150},
	  { text: '${uiLabelMap.Department}', datafield: 'groupName', filtertype: 'input'},
	  { text: '${uiLabelMap.firstName}', datafield: 'firstName', filtertype: 'input'},
	  { text: '${uiLabelMap.middleName}', datafield: 'middleName', filtertype: 'input'},
	  { text: '${uiLabelMap.lastName}', datafield: 'lastName', filtertype: 'input'}
	]
	});
	$("#jqxReferredByPartyGridId").on('rowselect', function (event) {
		var args = event.args;
		var row = $("#jqxReferredByPartyGridId").jqxGrid('getrowdata', args.rowindex);
		selectedPartyId = row['partyId'];
		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['partyId'] +'</div>';
		$('#referredByPartyId').jqxDropDownButton('setContent', dropDownContent);
	});
	
	$("#createNewApplicant").on('validationSuccess', function (event) {
		$("#jqxTabs").jqxTabs('enableAt', 1);
	});
	
	$("#createNewApplicant").jqxValidator({
		rules: [
			{input: '#firstName', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required'},
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
			{input: '#birthDate', message: '${uiLabelMap.LTCurrentDateRequired}', action: 'keyup, change, close',
				rule: function (input, commit) {
	                if (input.jqxDateTimeInput('getDate') < new Date()) {
	                    return true;
	                }
	                return false;
	            }
			}
		]
	});