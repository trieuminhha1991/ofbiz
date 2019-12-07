var viewEmplSalHisDetailObject = (function(){
	var init = function(){
		initContextMenu();
		initJqxTabs();
		initJqxEmplSalaryItemGrid();	
		initJqxInputDetail();
		initJqxValidator();
		initJqxTreeBtn();
		initGridDetailEvent();
		initJqxBtnEventDetail();
		initJqxDropDownList();
		initJqxTreeBtnEvent();
		initJqxNotification();
		initJqxWindow();
	};
	
	var initJqxNotification = function(){
		$("#jqxNotificationinvoiceItemSalaryGrid").jqxNotification({width: "100%", appendContainer: "#containerinvoiceItemSalaryGrid", opacity: 0.9, autoClose: true, template: "info" });
	};
	
	var initJqxTreeBtnEvent = function(){
		$("#jqxTreeOrgPaid").on('select', function(event){
			var id = event.args.element.id;
	    	var item = $(this).jqxTree('getItem', args.element);
	    	setDropdownContent(item, $(this), $("#dropdownBtnOrgPaid"));
	    	
	    	var tempS = $("#orgDetailPaidGrid").jqxGrid('source');
	    	var customTimePeriodId = $("#monthCustomTime").val();
	    	var selectIndex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	    	var data = $("#jqxgrid").jqxGrid('getrowdata', selectIndex);
	    	tempS._source.url = "jqxGeneralServicer?sname=JQListOrgPaidItemDetail&hasrequest=Y&orgId=" + item.value + "&customTimePeriodId=" + customTimePeriodId + "&partyIdTo="+data.partyIdTo;
	    	$("#orgDetailPaidGrid").jqxGrid('source', tempS);
		});
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList([], $('#editInvoiceItemTypeEdit'), "invoiceItemTypeId", "description", 25, '98%');
	};
	
	var getCustomTimePeriod = function(){
		return {customTimePeriodId: $("#monthCustomTime").val()};
	};
	
	var initJqxTreeBtn = function(){
		var config = {dropDownBtnWidth: 250, treeWidth: 250, url: "getSubsidiaryOfParty", callbackGetExtData: getCustomTimePeriod};
		globalHistoryDetailObject.createJqxTreeDropDownBtn($("#jqxTreeOrgPaid"), $("#dropdownBtnOrgPaid"), globalHistoryDetailVar.rootPartyArr, "treeOrgPaid", "treeChildOrgPaid", config);
	};
	var initJqxEmplSalaryItemGrid = function(){
		var config = {
		   		width: '100%', 
		   		height: '99%',
		   		autoheight: false,
		   		virtualmode: true,
		   		filterable: false,
		   		showtoolbar: false,
		   		selectionmode: 'singlerow',
		   		pageable: true,
		   		sortable: false,
		        filterable: false,	        
		        editable: false,
		        rowsheight: 26,
		        url: '',                
		        source: {pagesize: 15}
		};
		//==================================income==============================================
		var datafieldIncome = [
				{name: 'salaryItem', type: 'string'},			
				{name: 'name', type: 'string'},			
				{name: 'amount', type: 'number'},				           
				{name: 'partyIdFrom', type: 'string'},                       
				{name: 'groupName', type: 'string'}                       
		];
		
		var columnlistIncome = [
				{datafield: 'salaryItem', hidden: true},
				{datafield: 'partyIdFrom', hidden: true},
				{text: uiLabelMapDetail.HRIncome, datafield: 'name', width: '30%'},        	
	       		{text: uiLabelMapDetail.HRCommonAmount, datafield: 'amount', width: '30%',
		        	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
						return "<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>" + formatcurrency(value) + "</div>";				
					}	
	        	},
	        	{text: uiLabelMapDetail.OrganizationPaid, datafield: 'groupName'}
		];
		Grid.initGrid(config, datafieldIncome, columnlistIncome, null, $("#emplSalaryItemIncomeGrid"));
		
		//===========================================deduction=================================
		var datafieldDeduction = [
	   			{name: 'salaryItem', type: 'string'},			
	   			{name: 'name', type: 'string'},			
	   			{name: 'amount', type: 'number'},				           
	   	];
		
	   	var columnlistDeduction = [
	   			{text: '', datafield: 'salaryItem', hidden: true},
	   			{text: uiLabelMapDetail.HRDeduction, datafield: 'name', width: '50%'},        	
	          		{text: uiLabelMapDetail.HRCommonAmount, datafield: 'amount',
	   	        	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
	   					return "<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>" + formatcurrency(value) + "</div>";				
	   				}	
	           	},
	   	];
	   	Grid.initGrid(config, datafieldDeduction, columnlistDeduction, null, $("#emplSalaryItemDeductionGrid"));
	   	
	   	
	   	//===============================org Paid=======================
	   	var datafieldOrgPaid = [
				{name: 'salaryItem', type: 'string'},			
				{name: 'name', type: 'string'},			
				{name: 'amount', type: 'number'},				           			                      
		];
	   	
	   	var columnlistOrgPaid = [
				{text: '', datafield: 'salaryItem', hidden: true},
				{text: uiLabelMapDetail.OrgPaidType, datafield: 'name', width: '50%'},        	
				{text: uiLabelMapDetail.HRCommonAmount, datafield: 'amount',
	   				cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
					return "<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>" + formatcurrency(value) + "</div>";				
					}	
				}
	   	];
	   	config.height = 400;
	   	Grid.initGrid(config, datafieldOrgPaid, columnlistOrgPaid, null, $("#orgDetailPaidGrid"));
	   	
	   	//====================invoiceItem for salary============
	   	var datafieldInvoiceItem = [
				{name: 'invoiceItemTypeId', type: 'string'},			
				{name: 'partyIdFrom', type: 'string'},			
				{name: 'salaryItem', type: 'string'},	
				{name: 'amount', type: 'number'},
				{name: 'groupName', type: 'string'},
				{name: 'name', type: 'string'},				           			                      
				{name: 'description', type: 'string'}				           			                      
		];
	   	
	   	var columnlistInvoiceItem = [
				{datafield: 'salaryItem', hidden: true},
				{datafield: 'partyIdFrom', hidden: true},
				{datafield: 'invoiceItemTypeId', hidden: true},
				{text: uiLabelMapDetail.PayrollItemType, datafield: 'name', width: '30%'},        	
				{text: uiLabelMapDetail.AccountingInvoiceItemType, datafield: 'description', width: '50%'}, 
				{text: '', datafield: 'groupName', hidden: true},
				{text: uiLabelMapDetail.HRCommonAmount, datafield: 'amount',
	   				cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
					return "<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>" + formatcurrency(value) + "</div>";				
					}	
				},
				
	   	];	
		config.height = 460;
		config.source = {
				pagesize: 15,
				updateUrl: 'jqxGeneralServicer?jqaction=U&sname=updatePaySalaryItemHistory',
				editColumns: 'customTimePeriodId;invoiceItemTypeId;partyIdTo;partyIdFrom;salaryItem'		
		};
		Grid.initGrid(config, datafieldInvoiceItem, columnlistInvoiceItem, null, $("#invoiceItemSalaryGrid"));
		//initContextMenuInvoiceItem();
	};
	
	
	var initJqxWindow = function(){
		createJqxWindow($("#emplSalaryItemDetail"), 800, 500);
		createJqxWindow($("#orgDetailPaidWindow"), 800, 500);
		createJqxWindow($("#paySalaryInvoiceItemWindow"), 800, 555);
		createJqxWindow($("#editPaySalaryInvoiceItemWindow"), 500, 300);
		$("#orgDetailPaidWindow").on('open', function(event){
			if(globalHistoryDetailVar.rootPartyArr && globalHistoryDetailVar.rootPartyArr.length > 0){
				$("#jqxTreeOrgPaid").jqxTree('selectItem', $("#" + globalHistoryDetailVar.rootPartyArr[0].partyId + "_treeOrgPaid")[0]);
			}
		});
		$("#orgDetailPaidWindow").on('close', function(event){
			$("#jqxTreeOrgPaid").jqxTree('selectItem', null);
		});
		$("#editPaySalaryInvoiceItemWindow").on('close', function(event){
			Grid.clearForm($(this));
		});
	};
	
	var initJqxValidator = function(){
		$("#editPaySalaryInvoiceItemWindow").jqxValidator({
			rules: [
				{
				    input: '#editInvoiceItemTypeEdit',
				    message: uiLabelMapDetail.FieldRequired, 
				    action: 'keyup, focus', 
				    rule: function (input, commit) {
				        if (input.val()) {
				            return true;
				        }
				        return false;
				    }
				},		        
			]
		});
	};
	
	var initGridDetailEvent = function (){
		$("#invoiceItemSalaryGrid").on('rowdoubleclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var dataInvoice = $("#invoiceItemSalaryGrid").jqxGrid('getrowdata', boundIndex);
			var jqxgridIndex = $("#jqxgrid").jqxGrid('getselectedrowindex');
			var partyData = $("#jqxgrid").jqxGrid('getrowdata', jqxgridIndex);
			$("#partyId").val(partyData.partyIdTo);
			$("#partyName").val(partyData.partyName);
			$("#organizationList").val(partyData.currDept);
			$("#salaryItem").val(dataInvoice.name);
			$("#editInvoiceItemTypeEdit").jqxDropDownList({disabled: true});
			var dataSubmit = {};
			dataSubmit.customTimePeriodId = $("#monthCustomTime").val();
			dataSubmit.partyId = partyData.partyIdTo;
			dataSubmit.salaryItem = dataInvoice.salaryItem;
			updateSourceDropdownlist($("#editInvoiceItemTypeEdit"), []);
			$.ajax({
				url: 'getInvoiceItemTypeByPartyAndFormual',
				data: dataSubmit,
				type: 'POST',
				success: function(response){
					if(response.responseMessage == 'success'){
						var listReturn = response.listReturn;
						if(listReturn && listReturn.length > 0){
							updateSourceDropdownlist($("#editInvoiceItemTypeEdit"), listReturn);
							if(dataInvoice.invoiceItemTypeId){
								$("#editInvoiceItemTypeEdit").jqxDropDownList('selectItem', dataInvoice.invoiceItemTypeId);
							}
						}
					}	
				},
				complete: function(jqXHR, textStatus){
					$("#editInvoiceItemTypeEdit").jqxDropDownList({disabled: false});
				}
			});
			openJqxWindow($("#editPaySalaryInvoiceItemWindow"));
		});
	};

	var updateGridDetailData = function(rowData){
		var partyId = rowData.partyIdTo;
		var customTimePeriodId = rowData.customTimePeriodId;
		var url = 'jqxGeneralServicer?sname=JQListEmplSalaryItemDetail&hasrequest=Y';
		var paramIncome = "partyId=" + partyId + "&customTimePeriodId=" + customTimePeriodId + "&payrollCharacteristicId=INCOME";
		var paramDeduction = "partyId=" + partyId + "&customTimePeriodId=" + customTimePeriodId + "&payrollCharacteristicId=DEDUCTION";
		executeUpdateGridDetailData(url, paramIncome, $("#emplSalaryItemIncomeGrid"));
		executeUpdateGridDetailData(url, paramDeduction, $("#emplSalaryItemDeductionGrid"));
	};

	var executeUpdateGridDetailData = function (url, parametersStr, gridEle){
		var tempS = gridEle.jqxGrid('source');
		tempS._source.url = url + "&" + parametersStr;
		gridEle.jqxGrid('source', tempS);
	};

	var initJqxTabs = function (){
		$('#jqxTabEmplSalaryItemDetail').jqxTabs({ width: '100%', height: 445, position: 'top', theme: 'olbius'});
	};

	var initContextMenu = function(){
		var liElement = $("#contextMenu>ul>li").length;
		var contextMenuHeight = 30 * liElement; 
		$("#contextMenu").jqxMenu({ width: 240, height: contextMenuHeight, autoOpenPopup: false, mode: 'popup' , theme:'olbius'});	
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	        var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
	        var selectMenuAction = $(args).attr("action"); 
	        if(selectMenuAction == 'viewEmplSalaryDetail'){
	        	openJqxWindow($("#emplSalaryItemDetail"));
	        	updateGridDetailData(dataRecord);        	
	        }else if(selectMenuAction == 'viewOrgDetailPaid'){
	        	openJqxWindow($("#orgDetailPaidWindow"));
	        }else if(selectMenuAction == 'viewInvoiceItem'){
	        	openJqxWindow($("#paySalaryInvoiceItemWindow"));
	        	var url = 'jqxGeneralServicer?sname=JQListInvoiceItemSalary&hasrequest=Y';
	        	var param = 'partyId=' + dataRecord.partyIdTo + '&customTimePeriodId=' + dataRecord.customTimePeriodId;
	        	executeUpdateGridDetailData(url, param, $("#invoiceItemSalaryGrid"));
	        }
		});
	};
	
	var initJqxInputDetail = function (){
		$("#organizationList").jqxInput({width: '96%', height: 20, theme: 'olbius', disabled: true});
		$("#partyName").jqxInput({width: '96%', height: 20, theme: 'olbius', disabled: true});
		$("#partyId").jqxInput({width: '96%', height: 20, theme: 'olbius', disabled: true});
		$("#salaryItem").jqxInput({width: '96%', height: 20, theme: 'olbius', disabled: true});
	};

	var initJqxBtnEventDetail = function(){
		$("#cancelEditSalaryIIT").click(function(event){
			$("#editPaySalaryInvoiceItemWindow").jqxWindow('close');
		});
		$("#saveEditSalaryIIT").click(function(event){
			var valid = $("#editPaySalaryInvoiceItemWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var index = $("#invoiceItemSalaryGrid").jqxGrid('selectedrowindex');
			var dataInvoice = $("#invoiceItemSalaryGrid").jqxGrid('getrowdata', index);
			var selectItem = $("#editInvoiceItemTypeEdit").jqxDropDownList('getSelectedItem');
			dataInvoice.customTimePeriodId = $("#monthCustomTime").val();
			dataInvoice.invoiceItemTypeId = selectItem.value;
			dataInvoice.description = selectItem.description;
			dataInvoice.partyIdTo = $("#partyId").val();
			var id = $("#invoiceItemSalaryGrid").jqxGrid('getrowid', index)
			$("#invoiceItemSalaryGrid").jqxGrid('updaterow', id, dataInvoice);
			$("#editPaySalaryInvoiceItemWindow").jqxWindow('close');
		});
	};

	var initContextMenuInvoiceItem = function(){
		var liElement = $("#paySalaryInvoiceItemContextMenu>ul>li").length;
		var contextMenuHeight = 30 * liElement;	
		$("#paySalaryInvoiceItemContextMenu").jqxMenu({ width: 240, height: 30, autoOpenPopup: false, mode: 'popup' , theme:'olbius'});
		Grid.createContextMenu($("#invoiceItemSalaryGrid"), $("#paySalaryInvoiceItemContextMenu"), false);
	};
	
	return{
		init: init
	}
}());

$(document).ready(function(){
	viewEmplSalHisDetailObject.init();
});

