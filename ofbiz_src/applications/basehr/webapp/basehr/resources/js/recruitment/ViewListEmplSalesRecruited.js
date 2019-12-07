var listEmplSalesRecruitedObj = (function(){
	var init = function(){
		initJqxDropDownList();
		initJqxTreeButton();
		initEvent();
	};
	var initJqxDropDownList = function(){
		createJqxDropDownList(globalVar.customTimePeriodArr, $("#yearCustomTimePeriod"), "customTimePeriodId", "periodName", 25, 90);
		createJqxDropDownList([], $("#monthCustomTimePeriod"), "customTimePeriodId", "periodName", 25, 90);
		
	};
	var initJqxTreeButton = function(){
		var config = {dropDownBtnWidth: 270, treeWidth: 270};
		globalObject.createJqxTreeDropDownBtn($("#jqxTree"), $("#dropDownButton"), globalVar.rootPartyArr, "tree", "treeChild", config);
	};
	var initEvent = function(){
		var nowDate = new Date();
		$('#jqxTree').on('select', function(event){
			var id = event.args.element.id;
			var item = $('#jqxTree').jqxTree('getItem', event.args.element);
			setDropdownContent(event.args.element, $("#jqxTree"), $("#dropDownButton"));
			var partyId = item.value;
			var yearCustomTimePeriodId = null; 
			var monthCustomTimePeriodId = $("#monthCustomTimePeriod").val();
			if(monthCustomTimePeriodId == "ALL"){
				yearCustomTimePeriodId = $("#yearCustomTimePeriod").val();
				monthCustomTimePeriodId = null;
			}
			updateJqxGrid(monthCustomTimePeriodId, yearCustomTimePeriodId, partyId);
		});
		if(globalVar.rootPartyArr.length > 0){
			$("#jqxTree").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_tree")[0]);
		}
		$("#monthCustomTimePeriod").on('select', function(event){
			var args = event.args;
			if(args){
				var value = args.item.value;
				var monthCustomTimePeriodId = null; 
				var yearCustomTimePeriodId = null; 
				if(value == "ALL"){
					yearCustomTimePeriodId = $("#yearCustomTimePeriod").val();
				}else{
					monthCustomTimePeriodId = value;
				}
				var partyIdSelectedItem = $("#jqxTree").jqxTree('getSelectedItem');
				if(partyIdSelectedItem){
					var partyId = partyIdSelectedItem.value;			
					updateJqxGrid(monthCustomTimePeriodId, yearCustomTimePeriodId, partyId);
				}
			}
		});
		$("#yearCustomTimePeriod").on('select', function(event){
			var args = event.args;
			if(args){
				 var value = args.item.value;
				 $.ajax({
					url: "getCustomTimePeriodByParent",
					data: {parentPeriodId: value},
					type: 'POST',
					success: function(data){
						if(data.listCustomTimePeriod){
							var listCustomTimePeriod = data.listCustomTimePeriod;
							listCustomTimePeriod.unshift({customTimePeriodId: 'ALL', periodName: '-----------'});
							updateSourceDropdownlist($("#monthCustomTimePeriod"), listCustomTimePeriod);
						}
					},
					complete: function(jqXHR, textStatus){
						$("#monthCustomTimePeriod").jqxDropDownList({selectedIndex: nowDate.getMonth() + 1});
					}
				});
			}
		});
		$("#yearCustomTimePeriod").jqxDropDownList({selectedIndex: 0});
	};
	var updateJqxGrid = function(monthCustomTimePeriodId, yearCustomTimePeriodId, partyId){
		var source = $("#jqxgrid").jqxGrid('source');
		if(partyId && (monthCustomTimePeriodId || yearCustomTimePeriodId)){
			if(monthCustomTimePeriodId){
				source._source.url = "jqxGeneralServicer?sname=JQGetListEmplSalesRecruited&monthCustomTimePeriodId=" + monthCustomTimePeriodId + "&partyId=" + partyId;
			}else if(yearCustomTimePeriodId){
				source._source.url = "jqxGeneralServicer?sname=JQGetListEmplSalesRecruited&yearCustomTimePeriodId=" + yearCustomTimePeriodId + "&partyId=" + partyId;
			}
			$("#jqxgrid").jqxGrid('source', source);
		}
	};
	return{
		init: init
	}
}());

$(document).ready(function(){
	listEmplSalesRecruitedObj.init();
});