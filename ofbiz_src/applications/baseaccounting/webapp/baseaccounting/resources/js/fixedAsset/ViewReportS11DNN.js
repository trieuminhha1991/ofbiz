var reportS11DNNObj = (function(){
	var init = function(){
		initInput();
		initDropDownFA();
		initDropDown();
		initWindow();
		initEvent();
		initValidator();
	};
	var initInput = function(){
		$("#yearS11").jqxNumberInput({width: '34%', height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0, digits: 4});
	};
	var initDropDownFA = function(){
		$("#fixedAssetDropDown").jqxDropDownButton({width: "97%", height: 25});
		var grid = $("#fixedAssetGrid");
		var datafields = [{name: 'fixedAssetId', type: 'string'},
		                  {name: 'fixedAssetName', type: 'string'},
		                  ];
		var columns = [{text: uiLabelMap.BACCFixedAssetIdShort, datafield: 'fixedAssetId', width: '30%'},
		               {text: uiLabelMap.BACCFixedAssetName, datafield: 'fixedAssetName'},
		               ];
		var config = {
      		width: 500, 
      		virtualmode: true,
      		showfilterrow: true,
      		showtoolbar: false,
      		selectionmode: 'singlerow',
      		pageable: true,
      		sortable: true,
	        filterable: true,
	        editable: false, 
	        url: 'JqxGetListAssets',
	        source: {
	        	pagesize: 5
	        }
      	};
      	Grid.initGrid(config, datafields, columns, null, grid);
	};
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#monthQuarterS11"), globalVar.monthQuarterArr, {valueMember: 'id', displayMember: 'description', width: '60%', height: 25, dropDownWidth: 100});
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#S11DNNWindow"), 420, 180);
		$('#S11DNNWindow').jqxWindow({ resizable: false });
	};
	
	var initEvent = function(){
		$("#reportS11DNNBtn").click(function(e){
			accutils.openJqxWindow($("#S11DNNWindow"));
		});
		$("#S11DNNWindow").on('open', function(event){
			$("#S11DNNWindow").jqxWindow('focus');
			var defaultContent = '<div class="innerDropdownContent">' + uiLabelMap.BACCPleaseChooseAcc + '</div>';
			$('#fixedAssetDropDown').jqxDropDownButton('setContent', defaultContent);
			var date = new Date();
			$("#yearS11").val(date.getFullYear());
			$("#monthQuarterS11").val('month' + date.getMonth());
		});
		$("#S11DNNWindow").on('close', function(event){
			$("#S11DNNWindow").jqxValidator('hide');
			$("#fixedAssetGrid").jqxGrid('clearselection');
			$("#fixedAssetGrid").jqxGrid('gotopage', 0);
			$("#yearS11").val(0);
			$("#monthQuarterS11").jqxDropDownList('clearSelection');
			$("#fixedAssetDropDown").jqxDropDownButton('setContent', "");
		});
		
		$("#fixedAssetGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#fixedAssetGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.fixedAssetName + ' [' + rowData.fixedAssetId + ']</div>';
			$("#fixedAssetDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#fixedAssetDropDown").attr("data-value", rowData.fixedAssetId);
			$("#fixedAssetDropDown").jqxDropDownButton('close');
		});
		
		$("#cancelFixedAssetReportS11").click(function(e){
			$("#S11DNNWindow").jqxWindow('close');
		});
		$("#saveFixedAssetReportS11").click(function(e){
			var valid = $("#S11DNNWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var param = {};
			var selectedIndex = $("#monthQuarterS11").jqxDropDownList('getSelectedIndex');
			var monthQuarterData = globalVar.monthQuarterArr[selectedIndex];
			var type = monthQuarterData.type;
			var year = $("#yearS11").val();
			var url = 'exportFixedAssetReportS11DNNExcel';
			param.dateType = type;
			if(typeof(monthQuarterData.value) != 'undefined'){
				param.monthQuarterValue = monthQuarterData.value; 
			}
			param.fixedAssetId = $("#fixedAssetDropDown").attr("data-value");
			param.year = year;
			exportFunction(param, url);
		});
	};
	
	var exportFunction = function(parameters, url){
		var form = document.createElement("form");
		form.setAttribute("method", "post");
		form.setAttribute("action", url);
		form.setAttribute("target", "_blank");
		for(var key in parameters){
			if (parameters.hasOwnProperty(key)) {
				var input = document.createElement('input');
				input.type = 'hidden';
				input.name = key;
				input.value = parameters[key];
				form.appendChild(input);
			}
		}
		document.body.appendChild(form);
		form.submit();  
	};
	
	var initValidator = function(){
		$("#S11DNNWindow").jqxValidator({
			rules: [
				{ input: '#fixedAssetDropDown', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(input.val() != uiLabelMap.BACCPleaseChooseAcc){
							return true;
						}
						return false;
					}
				},
			]
		});
	};
	
	return{
		init: init
	};
}());

$(document).ready(function(){
	$.jqx.theme = 'olbius';
	reportS11DNNObj.init();
});