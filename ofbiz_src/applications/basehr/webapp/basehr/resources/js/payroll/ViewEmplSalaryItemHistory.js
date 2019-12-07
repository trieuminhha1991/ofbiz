var viewEmplSalaryItemHisObject = (function(){
	var init = function(){
		initJqxDropdownList();
		initJqxNotification();
		initJqxTreeBtn();
		initJqxTreeBtnEvent();
		initBtnEvent();
		if(globalVar.rootPartyArr.length > 0){
			$("#jqxTree").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_tree")[0]);
		}
	};
	
	var initBtnEvent = function(){
		$("#removeFilter").click(function(){
			$("#jqxgrid").jqxGrid('clearfilters');
		});
	}
	
	var initJqxNotification = function(){
		$("#jqxNotify").jqxNotification({width: "100%", appendContainer: "#jqxcontainer", 
			opacity: 0.9, autoClose: true, template: "info" });
	};
	
	var initJqxTreeBtn = function(){
		var config = {dropDownBtnWidth: 280, treeWidth: 280};
		globalObject.createJqxTreeDropDownBtn($("#jqxTree"), $("#dropDownButton"), globalVar.rootPartyArr, "tree", "treeChild", config);
	};
	
	var initJqxTreeBtnEvent = function(){
		$("#jqxTree").on('select', function(event){
			var id = event.args.element.id;
			var item = $("#jqxTree").jqxTree('getItem', args.element);
			setDropdownContent(item, $(this), $("#dropDownButton"));
			var partyGroupId = item.value;
			var customTimePeriodId = $("#monthCustomTime").val();
			refreshGridData(customTimePeriodId, partyGroupId);
		});
	};
	
	var initJqxDropdownList = function(){
		createJqxDropDownList([], $('#monthCustomTime'), "customTimePeriodId", "periodName", 25, 100);
		createJqxDropDownList(yearCustomTimePeriod, $('#yearCustomTime'), "customTimePeriodId", "periodName", 25, 100);
		initJqxDropdownlistEvent();
		if(typeof(globalVar.selectYearCustomTimePeriodId) != "undefined"){
			$("#yearCustomTime").jqxDropDownList('selectItem', globalVar.selectYearCustomTimePeriodId);
		}else{
			$("#yearCustomTime").jqxDropDownList('selectIndex', 0 ); 
		}
	};
	
	var initJqxDropdownlistEvent = function(){
		$("#monthCustomTime").on('select', function(event){
			var args = event.args;
			if(args){			
				var item = args.item;
				var value = item.value;
				var itemTree = $('#jqxTree').jqxTree('getSelectedItem');	
				refreshGridData(value, itemTree.value);
			}
		});
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
							$("#monthCustomTime").jqxDropDownList({source: tmpDataAdapter, autoDropDownHeight: false});
							
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
	
	var refreshGridData = function(customTimePeriodId, partyGroupId){
		if(partyGroupId &&  customTimePeriodId && partyGroupId.length > 0 &&  customTimePeriodId.length > 0){
			var tempSource = $("#jqxgrid").jqxGrid('source');
			tempSource._source.url = "jqxGeneralServicer?sname=JQListEmplSalaryItemPayroll&hasrequest=Y&partyGroupId=" + partyGroupId + "&customTimePeriodId=" + customTimePeriodId;
			$("#jqxgrid").jqxGrid('source', tempSource);
		}
	};
	
	return{
		init: init
	}
}());

$(document).ready(function(){
	viewEmplSalaryItemHisObject.init();
});

