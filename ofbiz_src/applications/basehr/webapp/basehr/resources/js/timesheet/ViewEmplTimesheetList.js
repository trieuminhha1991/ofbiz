var nowDate = new Date(globalVar.startDate);
var previousFirstDate = new Date(nowDate.getFullYear(), nowDate.getMonth() - 1, 1);
var previousLastDate = new Date(nowDate.getFullYear(), nowDate.getMonth(), 0);

var viewEmplTimesheetListObject = (function(){
	var init = function(){
			initJqxDropDownList();
			initJqxNotification();
			initJqxInput();
		    initJqxCheckBox();	    
		    initJqxValidator();
		    initJqxWindow();
		    initJqxTree();
	};
	
	var initJqxNotification = function(){
		$("#jqxNotifyEmplTimesheets").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, template: "info", appendContainer: "#jqxNotifyEmplTimesheetContainer"});
	};
	
	var initJqxCheckBox = function (){
	    $("#checkImportData").jqxCheckBox({width: 50, height: 25, checked: false, theme: 'olbius', checked: true});
	}
	
	var initJqxInput = function(){
	    $("#emplTimesheetNameAdd").jqxInput({width: 247, height: 22, theme: 'olbius'});
	};
	
	var initJqxValidator = function(){
		$("#popupAddRow").jqxValidator({
		   	rules: [
			   	{
					input: '#emplTimesheetNameAdd',
					message: uiLabelMap.FieldRequired,
					action: 'blur',
					rule: 'required'
				},
				{
					input: "#dropDownButtonAddNew", message: uiLabelMap.FieldRequired, action: 'blur',
				    rule: function (input, commit) {
						var items = $("#jqxTreeAddNew").jqxTree('getSelectedItem');
						if(!items){
							return false;
						}
						return true;
				   }
				},
				{
					input : '#emplTimesheetNameAdd',
					message : uiLabelMap.IllegalCharacters,
					action : 'blur',
					rule : function(input, commit){
						var specialCharacter = "<>@!#$%^&*()_+[]{}?:;|'\"\\,.~`-=";
						for(var i=0; i<specialCharacter.length; i++){
							if($(input).val().indexOf(specialCharacter[i]) > -1){
								return false;
							}
						}
						return true;
//						if(isContainSpecialChar($(input).val().trim())){
//							return false;
//						}
//						return true;
					}
				}
		   	],
		 });
	};
	
	var initJqxDropDownList = function (){
		createJqxDropDownList(yearCustomTimePeriod, $('#yearCustomTime'), "customTimePeriodId", "periodName", 25, "100%");
		createJqxDropDownList([], $('#monthCustomTime'), "customTimePeriodId", "periodName", 25, "100%");
		
		initJqxDropdownlistEvent();
		if(typeof(globalVar.selectYearCustomTimePeriodId) != "undefined"){
			$("#yearCustomTime").jqxDropDownList('selectItem', globalVar.selectYearCustomTimePeriodId);
		}else{
			$("#yearCustomTime").jqxDropDownList('selectIndex', 0 );
		}
	};
	
	var initJqxDropdownlistEvent = function(){
		$("#yearCustomTime").on('select', function(event){
			var args = event.args;
			if(args){
				var item = args.item;
				var value = item.value;
				$.ajax({
					url: "getCustomTimePeriodByParent",
					data: {parentPeriodId: value},
					type: 'POST',
					success: function(data){
						if(data.listCustomTimePeriod){
							var listCustomTimePeriod = data.listCustomTimePeriod;
							var selectItem = listCustomTimePeriod.filter(function(item, index, array){
								var nowTimestamp = globalVar.startDate;
								if(item.fromDate <= nowTimestamp && item.thruDate >= nowTimestamp){
									return item;
								}
							});
							var tempSource = {
									localdata: listCustomTimePeriod,
					                datatype: "array"
							}
							var tmpDataAdapter = new $.jqx.dataAdapter(tempSource);
							$("#monthCustomTime").jqxDropDownList('clearSelection');
							$("#monthCustomTime").jqxDropDownList({source: tmpDataAdapter});
							if(selectItem.length > 0){
								$("#monthCustomTime").jqxDropDownList('selectItem', selectItem[0].customTimePeriodId);
							}else{
								$("#monthCustomTime").jqxDropDownList({selectedIndex: 0 });
							}
						}
					},
					complete: function(jqXHR, textStatus){
						
					}
				});
			}
		});
	};
	
	var initJqxWindow = function(){
		$("#popupAddRow").jqxWindow({
	        width: 500, height: 260, resizable: true, isModal: true, autoOpen: false, 
	        theme: 'olbius', modalZIndex: 11000   
	    });
		$("#popupAddRow").on("open", function(event){
			$("#emplTimesheetNameAdd").val("");
			$("#fromDateJQ").val(previousFirstDate);
			$("#thruDateJQ").val(previousLastDate);
		});
		
		//var jqxWindowEmplTimesheetInDayHeight = jqxGridEmplTimekeepingSign.jqxGrid('height') + 140;
		$("#jqxWindowEmplTimesheetInDay").jqxWindow({
			width: 520, height: 533, resizable: true, isModal: true, autoOpen: false, theme: 'olbius',
			initContent: function(){
				$("#jqxNotificationTimesheetInDay").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, 
					template: "info", appendContainer: "#notifyContainer"});
			}
	    });
		createJqxWindow($("#emplTimesheetAttendancePopup"), 1000, 580);
		createJqxWindow($("#proposalApprvalTimesheet"), 400, 125);
		createJqxWindow($("#jqxWindowEmplTimesheetGeneral"), 1000, 580);
		
		$("#emplTimesheetAttendancePopup").on('close', function (event){
			$("#jqxTimesheetAtt").jqxGrid('clearfilters');
			refreshBeforeReloadGrid($("#jqxTimesheetAtt"));
			$("#jqxTree").jqxTree('selectItem', null);
		});
		$("#emplTimesheetAttendancePopup").on('open', function (event){
			if(globalVar.rootPartyArr.length > 0){
				$("#jqxTree").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_tree")[0]);
			}
		});
		$("#jqxWindowEmplTimesheetGeneral").on('close', function (event){
			$("#jqxEmplTimesheetGeneral").jqxGrid('clearfilters');
			refreshBeforeReloadGrid($("#jqxEmplTimesheetGeneral"));
			$("#jqxTreeGeneral").jqxTree('selectItem', null);
		});
		$("#jqxWindowEmplTimesheetGeneral").on('open', function (event){
			if(globalVar.rootPartyArr.length > 0){
				$("#jqxTreeGeneral").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_treeGeneral")[0]);
			}
		});
		
		$("#jqxWindowEmplTimesheetInDay").on('close', function (event){
			//$("#jqxGridEmplTimekeepingSign").jqxGrid("clear");
			refreshBeforeReloadGrid($("#jqxGridEmplTimekeepingSign"));
			$("#jqxNotificationTimesheetInDay").jqxNotification('closeLast');
			$("#updateEmplTimekeepingSignBtn").removeAttr("disabled");
			$("#jqxGridEmplTimekeepingSign").jqxGrid('clearselection');
		});
			
		$("#popupAddRow").on('close', function (event) { 
			$("#popupAddRow").jqxValidator('hide');
	    });
	};
	var initJqxTree = function(){
		createOrgTree($("#jqxTreeAddNew"), $("#dropDownButtonAddNew"));
	};
	
	var createOrgTree = function(jqxTreeEle, dropdownButton){
		dropdownButton.jqxDropDownButton({ width: '250px', height: 25, theme: 'olbius'});
		var data = [
				{
					"id": globalVar.rootOrgId + "_paid",
					"parentid": "-1",
					"text": globalVar.groupName,
					"value": globalVar.rootOrgId
				},
				{
					"id": globalVar.rootOrgId + "_paidChild",
					"parentid": globalVar.rootOrgId + "_paid",
					"text": "Loading...",
					"value": "getSubsidiaryOfParty"
				}
	   	];
	   	
	   	var source =
	    {
	        datatype: "json",
	        datafields: [
	            { name: 'id' },
	            { name: 'parentid' },
	            { name: 'text' },
	            { name: 'value' }
	        ],
	        id: 'id',
	        localdata: data
	    };
	    var dataAdapter = new $.jqx.dataAdapter(source);
		dataAdapter.dataBind();
		var records = dataAdapter.getRecordsHierarchy('id', 'parentid', 'items', [{ name: 'text', map: 'label'}]);
		jqxTreeEle.jqxTree({ source: records, width: 250, theme: 'olbius'});
		jqxTreeEle.on('select', function(event){
			var id = event.args.element.id;
	    	var item = $(this).jqxTree('getItem', args.element);
	    	setDropdownContent(item, $(this), dropdownButton);
		});
		createExpandEventJqxTree(jqxTreeEle);
	};
	
	return{
		init: init
	}
}());

$(document).ready(function(){
	viewEmplTimesheetListObject.init();
});
function removeFilter(){
	$('#jqxgrid').jqxGrid('clearfilters');
}
