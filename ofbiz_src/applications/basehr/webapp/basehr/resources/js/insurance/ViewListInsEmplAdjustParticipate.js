var viewListEmplInsStatus = (function(){
	var init = function(){
		initDateTime();
		initJqxTreeDropDownBtn();
		initMenu();
		initContextMenu();
		initData();
		initEvent();
	};
	
	var initJqxTreeDropDownBtn = function(){
		var config = {dropDownBtnWidth: 220, treeWidth: 220};
		globalObject.createJqxTreeDropDownBtn($("#jqxTree"), $("#dropDownButton"), globalVar.rootPartyArr, "tree", "treeChild", config);
	};
	
	var initDateTime = function(){
		var monthData = [];
		for(var i = 0; i < 12; i++){
			monthData.push({month: i, description: uiLabelMap.CommonMonth + " " + (i + 1)});
		}
		createJqxDropDownList(monthData, $("#month"), "month", "description", 25, 90);
		$("#year").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});
	};
	
	var initEvent = function(){
		$("#removeFilter").click(function(){
			$("#jqxgrid").jqxGrid('clearfilters');
		});
		$('#jqxTree').on('select', function(event){
			var id = event.args.element.id;
			var item = $('#jqxTree').jqxTree('getItem', event.args.element);
		  	setDropdownContent(event.args.element, $("#jqxTree"), $("#dropDownButton"));
		});
		$("#month").on('select', function(event){
			var args = event.args;
			if(args){			
				var args = event.args;
				if (args) {
					var month = args.item.value;
					var year = $("#year").val();
					var org = $('#jqxTree').jqxTree('getSelectedItem').value;
					refreshGridData(month, year, org);
				}
			}
		});
		
		$("#year").on('valueChanged', function(event){
			var year = event.args.value;
			var month = $("#month").val();
			var org = $('#jqxTree').jqxTree('getSelectedItem').value;
			refreshGridData(month, year, org);
		});
		
		$("#jqxTree").on('select', function(event){
			var args = event.args;
			if(args){			
				var args = event.args;
				if (args) {
					var month = $("#month").jqxDropDownList('getSelectedItem').value;
					var year = $("#year").val();
					var org = $('#jqxTree').jqxTree('getSelectedItem').value;
					refreshGridData(month, year, org);
				}
			}
		})
		$("#jqxMenu").on('itemclick', function(event){
			var id = event.args.id;
			if(id == "newParticipate"){
				adjParticipateObj.openWindow();//adjParticipateObj is defined in InsuranceNewlyParticipate.js
			}else if(id == "suspend"){
				insuranceAdjSuspendStopObj.openWindow();//insuranceAdjSuspendStopObj is defined in InsuranceAdjustSuspendStop.js
			}else if(id == "reparticipate"){
				reparticipateInsObj.openWindow();//reparticipateInsObj is defined in InsuranceReparticipate.js
			}else if(id == "jobAndTitle"){
				insAdjSalAndJobObj.openWindow();//insAdjSalAndJobObj is defined in InsuranceAdjustSalaryAndJobTitle.js
			}
		});
	};
	var initData = function(){
		if(globalVar.rootPartyArr.length > 0){
			$("#jqxTree").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_tree")[0]);
		}
		var item = $('#jqxTree').jqxTree('getSelectedItem');
		setDropdownContent(item.element, $("#jqxTree"), $("#dropDownButton"));
		var date = new Date();
		var month = date.getMonth();
		var year = date.getFullYear();
		$("#month").val(month);
		$("#year").val(year);
		refreshGridData(month, year, item.value);
	};
	var initMenu = function(){
		var data = [
		            {id: 'ajustment', text: uiLabelMap.InsuranceAjustment, parentId: -1, subMenuWidth: "180px"},
		            {id: 'participate', text: uiLabelMap.EmplParticipateInsurance, parentId: -1},
		            {id: 'suspend', text: uiLabelMap.EmplSuspendInsurance, parentId: -1},
		            {id: 'newParticipate', text: uiLabelMap.InsuranceNewlyParticipate, parentId: 'participate'}, 
		            {id: 'reparticipate', text: uiLabelMap.InsuranceReparticipate, parentId: 'participate'},
		            {id: 'jobAndTitle', text: uiLabelMap.SalaryAndJobTitle, parentId: 'ajustment'},
		            {id: 'unemploymentInsurance', text: uiLabelMap.InsuranceUnemployment, parentId: 'ajustment'},
		            ];
		var source = {
		     datatype: "json",
		     datafields: [
			     { name: 'id' },
			     { name: 'parentId' },
			     { name: 'text' },
			     { name: 'subMenuWidth' },
		     ],
			id: 'id',
			localdata: data
		};
		var dataAdapter = new $.jqx.dataAdapter(source);
		dataAdapter.dataBind();
		var records = dataAdapter.getRecordsHierarchy('id', 'parentId', 'items', [{ name: 'text', map: 'label'}]);
		$("#jqxMenu").jqxMenu({ 
			source: records, width: 'auto', 
			height: 30, showTopLevelArrows: true,
			autoOpen: false, theme: 'metro'
		});
		$("#jqxMenu").show();
		$("#jqxMenu").jqxMenu('setItemOpenDirection', 'ajustment', 'left');
		$("#jqxMenu").jqxMenu('setItemOpenDirection', 'participate', 'left');
	};
	
	var initContextMenu = function(){
		createJqxMenu("contextMenu", 30, 200);
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action");
            if(action == 'updateInsuranceHealth'){
            	insuranceHealthUpdateObj.openWindow(dataRecord);
            }
		});
	};
	
	var refreshGridData = function(month, year, org){
		var tmpS = $("#jqxgrid").jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?sname=JQGetListInsEmplAdjustParticipate&month=" + month + "&year=" + year +"&org=" + org;
		$("#jqxgrid").jqxGrid('source', tmpS);
	};
	
	return{
		init: init
	}
	
}());

var emplListInOrgObj = (function(){
	var _data = null;
	var init = function(){
		initJqxGridSearchEmpl();
		initWindow();
		initEvent();
	};
	var initJqxSplitter = function(){
		$("#splitterEmplList").jqxSplitter({  width: '100%', height: '100%', panels: [{ size: '25%'}, {size: '75%'}] });
	};
	var initWindow = function(){
		createJqxWindow($('#popupWindowEmplList'), 880, 530, initJqxSplitter);
	};
	var initEvent = function(){
		$('#popupWindowEmplList').on('open', function(event){
			$("#EmplListInOrg").jqxGrid('clearselection');
			if(typeof(globalVar.expandTreeId) != 'undefined'){
				$("#jqxTreeEmplList").jqxTree('expandItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList")[0]);
				$('#jqxTreeEmplList').jqxTree('selectItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList")[0]);
			}
		});
		$('#popupWindowEmplList').on('close', function(event){
			$("#EmplListInOrg").jqxGrid('clearselection');
			$(document).trigger('chooseEmplAdjParticipate');
		});
		$("#EmplListInOrg").on('rowdoubleclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var data = $("#EmplListInOrg").jqxGrid('getrowdata', boundIndex);
			_data = data;
			$('#popupWindowEmplList').jqxWindow('close');
		});
	};
	var getData = function(){
		return _data;
	};
	var initJqxGridSearchEmpl = function(){
		createJqxGridSearchEmpl($("#EmplListInOrg"), {uiLabelMap: uiLabelMap, selectionmode: 'singlerow', sourceId: "partyId"});
	};
	var openWindow = function(){
		openJqxWindow($('#popupWindowEmplList'));
	};
	return{
		init: init,
		openWindow: openWindow,
		getData: getData
	}
}());

$(document).ready(function () {
	viewListEmplInsStatus.init();
	emplListInOrgObj.init();
});

