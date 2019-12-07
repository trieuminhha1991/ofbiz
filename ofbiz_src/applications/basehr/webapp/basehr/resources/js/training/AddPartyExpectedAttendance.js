var addPartyExpectedObj = (function(){
	var _partyChooseData = {};
	var init = function(){
		initJqxGridSearchEmpl();
		initWindow();
		initEvent();
	};
	var initJqxGridSearchEmpl = function(){
		createJqxGridSearchEmpl($("#EmplListInOrg"), {uiLabelMap: uiLabelMap, selectionmode: 'checkbox', sourceId: "partyId"});
	};
	var initJqxSplitter = function(){
		$("#splitterEmplList").jqxSplitter({  width: '100%', height: '100%', panels: [{ size: '25%'}, {size: '75%'}] });
	};
	var initWindow = function(){
		createJqxWindow($('#popupWindowEmplList'), 900, 560, initJqxSplitter);
	};
	var initEvent = function(){
		var grid = $("#EmplListInOrg");
		grid.on('rowselect', function (event){
			var args = event.args;
			var rowData = args.row;
			if(rowData){
				_partyChooseData[rowData.partyId] = rowData;
			}else{
				var datainformation = grid.jqxGrid('getdatainformation');
				var paginginformation = datainformation.paginginformation;
				var pagenum = paginginformation.pagenum;
				var pagesize = paginginformation.pagesize;
				var start = pagenum * pagesize;
				var end = start + pagesize;
				for(var rowIndex = start; rowIndex < end; rowIndex++){
					var data = grid.jqxGrid('getrowdata', rowIndex);
					if(data){
						_partyChooseData[data.partyId] = data;
					}
				}
			}
		});
		grid.on('rowunselect', function (event){
			var args = event.args;
			var rowData = args.row;
			if(rowData){
				delete _partyChooseData[rowData.partyId];
			}else{
				var datainformation = grid.jqxGrid('getdatainformation');
				var paginginformation = datainformation.paginginformation;
				var pagenum = paginginformation.pagenum;
				var pagesize = paginginformation.pagesize;
				var start = pagenum * pagesize;
				var end = start + pagesize;
				for(var rowIndex = start; rowIndex < end; rowIndex++){
					var data = grid.jqxGrid('getrowdata', rowIndex);
					if(data){
						delete _partyChooseData[data.partyId];
					}
				}
			}
		});
		grid.on("bindingcomplete", function (event) {
			var datainformation = grid.jqxGrid('getdatainformation');
			var paginginformation = datainformation.paginginformation;
			var pagenum = paginginformation.pagenum;
			var pagesize = paginginformation.pagesize;
			var start = pagenum * pagesize;
			var end = start + pagesize;
			for(var rowIndex = start; rowIndex < end; rowIndex++){
				var data = grid.jqxGrid('getrowdata', rowIndex);
				if(data){
					var partyId = data.partyId;
					if(partyId && _partyChooseData.hasOwnProperty(partyId)){
						grid.jqxGrid('selectrow', rowIndex);
					}else{
						grid.jqxGrid('unselectrow', rowIndex);
					}
				}
			}
		}); 
		$('#popupWindowEmplList').on('open', function(event){
			if(typeof(globalVar.expandTreeId) != 'undefined'){
				$("#jqxTreeEmplList").jqxTree('expandItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList")[0]);
				$('#jqxTreeEmplList').jqxTree('selectItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList")[0]);
			}
		});
		$('#popupWindowEmplList').on('close', function(event){
			$("#EmplListInOrg").jqxGrid('clearselection');
			_partyChooseData = {};
		});
		$("#cancelAddPartyExpected").click(function(event){
			$('#popupWindowEmplList').jqxWindow('close');
		});
		$("#saveAddPartyExpected").click(function(event){
			var partyIds = Object.keys(_partyChooseData);
			if(partyIds.length > 0){
				$("#cancelAddPartyExpected").attr("disabled", "disabled");
				$("#saveAddPartyExpected").attr("disabled", "disabled");
				$("#EmplListInOrg").jqxGrid('showloadelement');
				$.ajax({
					url: 'createTrainingPartyExpectedAtt',
					data: {trainingCourseId: globalVar.trainingCourseId, partyIds: JSON.stringify(partyIds)},
					type: 'POST',
					success: function(response){
						if(response.responseMessage == "success"){
							Grid.renderMessage('EditTraining', response.successMessage, {autoClose : true,
								template : 'info', appendContainer: "#containerEditTraining", opacity : 0.9});
							$("#gridPtyExpectedAtt").jqxGrid('updatebounddata');
							$('#popupWindowEmplList').jqxWindow('close');
						}else{
							bootbox.dialog(response.errorMessage,
									[
									{
										"label" : uiLabelMap.CommonClose,
										"class" : "btn-danger btn-small icon-remove open-sans",
									}]		
							);
						}
					},
					complete: function(jqXHR, textStatus){
						$("#cancelAddPartyExpected").removeAttr("disabled");
						$("#saveAddPartyExpected").removeAttr("disabled");
						$("#EmplListInOrg").jqxGrid('hideloadelement');
					}
				});
			}else{
				$('#popupWindowEmplList').jqxWindow('close');
			}
		});
	};
	return{
		init: init
	}
}());
$(document).ready(function(){
	addPartyExpectedObj.init();
});