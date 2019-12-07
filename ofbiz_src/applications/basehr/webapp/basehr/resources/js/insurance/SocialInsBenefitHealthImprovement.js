var socialInsuranceBenefitObject = (function(){
	var init = function(){
		initJqxDropDownList();
		initJqxGrid();
		initJqxTreeDropDownBtn();
		initJqxWindow();
		initJqxNotification();
		//initJqxValidator();
	};
	
	var initJqxTreeDropDownBtn = function(){
		var config = {dropDownBtnWidth: 250, treeWidth: 250};
		globalObject.createJqxTreeDropDownBtn($("#jqxTreeEmplLeave"), $("#dropDownButtonEmplLeave"), globalVar.rootPartyArr, "treeLeave", "treeChildLeave", config);
		$('#jqxTreeEmplLeave').on('select', function(event){
			var id = event.args.element.id;			
			var item = $('#jqxTreeEmplLeave').jqxTree('getItem', event.args.element);
		  	setDropdownContent(event.args.element, $("#jqxTreeEmplLeave"), $("#dropDownButtonEmplLeave"));
		  	actionObject.getEmplLeaveList(item.value, "HEALTH_IMPROVEMENT");
		});
	};
	
	var initJqxGrid = function(){
		var datafield = [
				{name: 'partyId', type: 'string'},
				{name: 'emplLeaveId', type: 'string'},
				{name: 'partyName', type: 'string'},
				{name: 'emplLeaveReasonTypeId', type: 'string'},
				{name: 'benefitTypeId', type: 'string'},
				{name: 'dateParticipateIns', type: 'date'},
				{name: 'insuranceParticipatePeriod', type: 'string'},
				{name: 'fromDate', type: 'date'},
				{name: 'thruDate', type: 'date'},
				{name: 'statusConditionBenefit', type: 'string'},
				{name: 'dayLeaveConcentrate', type: 'string'},
				{name: 'dayLeaveFamily', type: 'string'},
				{name: 'allowanceAmount', type: 'string'},
        ];
		
		var columnlist = [
		    {datafield: 'emplLeaveId', hidden: true},              
			{text: uiLabelMap.EmployeeId, datafield: 'partyId' , editable: false, cellsalign: 'left', width: '12%', filterable: false, editable: false},
			{text: uiLabelMap.EmployeeName, datafield: 'partyName', editable: false, cellsalign: 'left', width: '15%', filterable: false, editable: false},
			{text: uiLabelMap.CommonReason, datafield: 'emplLeaveReasonTypeId', width: '18%', editable: false,
				cellsrenderer: function(row, column, value){
					for(var i = 0; i < globalVar.emplLeaveReasonArr.length; i++){
						if(globalVar.emplLeaveReasonArr[i].emplLeaveReasonTypeId == value){
							return '<span title=' + value + '>' + globalVar.emplLeaveReasonArr[i].description + '</span>';		
						}
					}
					return '<span>' + value + '</span>';
				}
			},
			{text: uiLabelMap.InsuranceBenefitTypeFull, datafield: 'benefitTypeId', width: '20%',sortable: false, columntype: 'dropdownlist',
				cellsrenderer: function (row, column, value) {
					for(var i = 0; i < insuranceAllowanceBenefitTypeArr.length; i++){
						if(insuranceAllowanceBenefitTypeArr[i].benefitTypeId == value){
							return '<span>' + insuranceAllowanceBenefitTypeArr[i].name + '</span>';
						}
					}
					return '<span>' + value + '</span>';
				},
				createeditor: function (row, column, editor) {
					var datarow = $("#emplLeaveListGrid").jqxGrid('getrowdata', row);
					var localdata = getbenefitTypeArrLeaveReason(datarow.emplLeaveReasonTypeId);
					var source = {
							localdata: localdata,
			                datatype: 'array'
					};
					var dataAdapter= new $.jqx.dataAdapter(source);
					editor.jqxDropDownList({source: dataAdapter, displayMember: 'name', valueMember: 'benefitTypeId', 
						height: 25, width: 170,
					});
					if(localdata.length < 8){
						editor.jqxDropDownList({autoDropDownHeight: true});
					}else{
						editor.jqxDropDownList({autoDropDownHeight: false});
					}
				},
				validation: function (cell, value){
					if(!value){
						 return {result: false, message: uiLabelMap.BenefitTypeIsNotSelected};
					}
					return true;
				},
			},
			{text: uiLabelMap.ParticipateFrom, datafield: 'dateParticipateIns', width: '11%', cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput', 
				createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight){
					editor.jqxDateTimeInput({width: cellwidth, height: cellheight});
					if(cellvalue){
						editor.val(cellvalue);
					}
				},
				cellendedit: function (row, datafield, columntype, oldvalue, newvalue){
					if(newvalue){
						var insuranceParticipatePeriod = getDescInsParticipatePeriod(newvalue);
						$("#emplLeaveListGrid").jqxGrid('setcellvalue', row, "insuranceParticipatePeriod", insuranceParticipatePeriod);
					}
				}
			},
			{text: uiLabelMap.InsuranceParticipatePeriod, datafield: 'insuranceParticipatePeriod', width: '18%', editable: false},
			{text: uiLabelMap.InsuranceBenefitCondition, datafield: 'statusConditionBenefit', width: '20%'},
			{text: uiLabelMap.DayLeaveConcentrate, datafield: 'dayLeaveConcentrate', width: '14%', cellsalign: 'right',
				editable: true, filterType:'number', columntype: 'numberinput', columngroup: 'dayLeaveInPeriod',
				cellendedit: function (row, datafield, columntype, oldvalue, newvalue){
					
				}
			},
			{text: uiLabelMap.DayLeaveFamily, datafield: 'dayLeaveFamily', width: '14%', editable: true, 
				filterType:'number', columntype: 'numberinput', columngroup: 'dayLeaveInPeriod', cellsalign: 'right',
				cellendedit: function (row, datafield, columntype, oldvalue, newvalue){
					
				}
			},
			{text: uiLabelMap.HRCommonAmount, datafield: 'allowanceAmount', width: '15%', cellsalign: 'right',
				filterType :'number', columngroup: 'nbrOfProposal', columntype: 'numberinput',
				cellsrenderer: function (row, column, value) {
					if(value){
						return "<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>" + formatcurrency(value) + "</div>";
					}
 		 		}	
			},
			{text: uiLabelMap.CommonFromDate, datafield: 'fromDate', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', width: '12%', editable: false},
			{text: uiLabelMap.CommonThruDate, datafield: 'thruDate', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', width: '12%', editable: false},
	    ];
		columngroups = [
		       		 {text: uiLabelMap.NumberOfProposal, name: 'nbrOfProposal', align: 'center'},
		    		 {text: uiLabelMap.InsDayLeaveInPeriod, name: 'dayLeaveInPeriod', align: 'center', parentgroup: 'nbrOfProposal'}];
		
		var config = {
				width: '100%', 
				height: 410,
				autoheight: false,
				virtualmode: true,
				showfilterrow: true,
				showtoolbar: false,
				selectionmode: 'checkbox',
				pageable: true,
				sortable: false,
		        filterable: false,
		        editable: true,
		        editmode: 'click',
		        url: '',
		        columngroups: columngroups,
		        source: {pagesize: 10, id: "emplLeaveId"}
			};
			Grid.initGrid(config, datafield, columnlist, null, $("#emplLeaveListGrid"));
	};
	var getDescInsParticipatePeriod = function(dateParticipateIns){
		var nowDate = new Date();
		var totalMonth = (nowDate.getFullYear() - dateParticipateIns.getFullYear()) * 12 + nowDate.getMonth() - dateParticipateIns.getMonth();
		var nbrYear = Math.floor(totalMonth / 12);
		var nbrMonth = totalMonth - 12 * nbrYear;
		var retVal = "";
		if(nbrYear > 0){
			retVal = nbrYear + " " + uiLabelMap.HRCommonYearLowercase + " "; 
		}
		if(nbrMonth > 0){
			retVal += nbrMonth + " " + uiLabelMap.HRCommonMonthLowercase;
		}
		return retVal; 
	};
	var getbenefitTypeArrLeaveReason = function(emplLeaveReasonTypeId){
		var retData = new Array();
		for(var i = 0; i < insuranceAllowanceBenefitTypeArr.length; i++){
			var tempEmplLeaveReasonTypeId = insuranceAllowanceBenefitTypeArr[i].emplLeaveReasonTypeId; 
			if(tempEmplLeaveReasonTypeId && tempEmplLeaveReasonTypeId == emplLeaveReasonTypeId){
				retData.push(insuranceAllowanceBenefitTypeArr[i]);
			} 
		}
		return retData;
	};
	
	var initJqxDropDownList = function(){
		//createJqxDropDownList(insuranceAllowanceBenefitTypeArr, $("#benefitType"), "benefitTypeId", "description", 25, '97%');
		createJqxDropDownList(yearCustomTimePeriod, $("#yearCustomTime"), "customTimePeriodId", "periodName", 25, 150);
		createJqxDropDownList([], $("#monthCustomTime"), "customTimePeriodId", "periodName", 25, 150);
		createJqxDropDownList([], $("#timeSetting"), "insAllowancePaymentDeclId", "sequenceNum", 25, 150);
		$("#timeSetting").jqxDropDownList({placeHolder:''});
	};
	
	var initJqxSplitter = function(){
		$("#splitterEmplList").jqxSplitter({  width: '100%', height: '100%', panels: [{ size: '25%'}, {size: '75%'}] });
	};
	
	var setDefaultValueJqxDropDownList = function(){
		if(typeof(globalVar.selectYearCustomTimePeriodId) != 'undefined'){
			$("#yearCustomTime").jqxDropDownList('selectItem', globalVar.selectYearCustomTimePeriodId);
		}else{
			$("#yearCustomTime").jqxDropDownList('selectIndex', 0 );
		}
	};
	var initJqxWindow = function(){
		createJqxWindow($("#emplLeaveBenefitListWindow"), 900, 550);
		$("#emplLeaveBenefitListWindow").on('close', function(event){
			$("#emplLeaveListGrid").jqxGrid('clearselection');
			$("#jqxTreeEmplLeave").jqxTree('selectItem', null);
		});
		
	};
	
	
	var initJqxNotification = function (){
		$("#jqxNtf").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, template: "info", appendContainer: "#notifyContainer"});
	};
	
	return{
		init: init,
		setDefaultValueJqxDropDownList: setDefaultValueJqxDropDownList
	}
}());


$(document).ready(function(){
	socialInsuranceBenefitObject.init();
});