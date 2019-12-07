var viewAcctgTransHistoryObj = (function(){
	var _data = {};
	var init = function(){
		initDropDownGrid();
		initGrid();
		initWindow();
		initEvent();
	};
	var initDropDownGrid = function(){
		$("#acctgTransHistoryDropDown").jqxDropDownButton({width: 300, height: 25});
		var grid = $("#acctgTransHistoryGrid");
		var datafield = [{name: 'acctgTransId', type: 'string'}, 
		                 {name: 'changeDate', type: 'date'},
		                 {name: 'changeUserLoginId', type: 'string'}, 
		                 {name: 'partyCode', type: 'string'}, 
		                 {name: 'fullName', type: 'string'}];
		
		var columns = [{text: uiLabelMap.BACCModifiedDate, datafield: 'changeDate', width: '45%', columtype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy HH:mm:ss'},
					   {text: uiLabelMap.BACCUserModified, datafield: 'fullName',
							cellsrenderer: function(row, column, value){
								var data = grid.jqxGrid('getrowdata', row);
								if(data){
									return '<span>' + value + ' [' + data.partyCode +']</span>';
								}
							}
					   }
					   ];
		var config = {
				url: '',
				showtoolbar : false,
				width : 550,
				virtualmode: true,
				editable: false,
				filterable: true,
				localization: getLocalization(),
				pageable: true,
				source:{
					pagesize: 10
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initGrid = function(){
		var grid = $("#acctgTransEntryHistoryGrid");
		var datafield = [{name: 'acctgTransEntrySeqId', type: 'string'}, 
		                 {name: 'glAccountIdTo', type: 'string'}, 
		                 {name: 'amountTo', type: 'number'}, 
		                 {name: 'descriptionTo', type: 'string'},
		                 {name: 'glAccountId', type: 'string'}, 
		                 {name: 'amount', type: 'number'},
		                 {name: 'description', type: 'string'}
		                 ];
		
		var columns = [{text: uiLabelMap.BACCAcctgTransEntrySeqId, datafield: 'acctgTransEntrySeqId', width: '12%'},
		               {text: uiLabelMap.BACCGlAccountId, datafield: 'glAccountIdTo', width: '12%', columngroup: 'newValue'},
		               {text: uiLabelMap.BACCAmount, datafield: 'amountTo', width: '15%', columntype: 'numberinput', columngroup: 'newValue',
		            	   cellsrenderer: function(row, column, value){
		            		   if(typeof(value) == 'number'){
		            			   return "<span style='text-align: right'>" + formatcurrency(value) + "</span>";
		            		   }
		            	   },
		               },
		               {text: uiLabelMap.AccountingComments, datafield: 'descriptionTo', width: '17%', columngroup: 'newValue'},
		               {text: uiLabelMap.BACCGlAccountId, datafield: 'glAccountId', width: '12%', columngroup: 'oldValue'},
		               {text: uiLabelMap.BACCAmount, datafield: 'amount', width: '15%', columntype: 'numberinput', columngroup: 'oldValue',
		            	   cellsrenderer: function(row, column, value){
		            		   if(typeof(value) == 'number'){
		            			   return "<span style='text-align: right'>" + formatcurrency(value) + "</span>";
		            		   }
		            	   },
		               },
		               {text: uiLabelMap.AccountingComments, datafield: 'description', width: '17%', columngroup: 'oldValue'},
					   ];
		
		var columngroups =  [
             { text: uiLabelMap.BACCNewValue, align: 'center', name: 'newValue' },
             { text: uiLabelMap.BACCOldValue, align: 'center', name: 'oldValue' }
           ];
		
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "acctgTransEntryHistoryGrid";
			var me = this;
	        var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.BACCContentChange + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
	     	toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
    		
    	};
		
		var config = {
				url: '',
				showtoolbar : true,
				rendertoolbar: rendertoolbar,
				width : '100%',
				virtualmode: true,
				editable: false,
				localization: getLocalization(),
				pageable: true,
				columngroups: columngroups,
				source:{
					pagesize: 5
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	
	var initWindow = function(){
		accutils.createJqxWindow($("#AcctgTransEntryHistoryWindow"), 830, 450);
	};
	var openWindow = function(data){
		_data = data;
		accutils.openJqxWindow($("#AcctgTransEntryHistoryWindow"));
	};
	var initEvent = function(){
		$("#AcctgTransEntryHistoryWindow").on('close', function(event){
			_data = {};
			$("#acctgTransHistoryDropDown").jqxDropDownButton('setContent', "");
			$("#acctgTransHistoryGrid").jqxGrid('clearselection');
			updateHistoryGrid($("#acctgTransHistoryGrid"), "");
			updateHistoryGrid($("#acctgTransEntryHistoryGrid"), "");
		});
		
		$("#AcctgTransEntryHistoryWindow").on('open', function(event){
			updateHistoryGrid($("#acctgTransHistoryGrid"), 'jqxGeneralServicer?sname=JQGetListAcctgTransHistory&acctgTransId=' + _data.acctgTransId);
		});
		
		var renderDateTime = function(date){
			var str = (date.getDate() > 9? "" + date.getDate() : "0" + date.getDate()) + "/"
			 + (date.getMonth() >= 9? "" + (date.getMonth() + 1) : "0" + (date.getMonth() + 1)) + "/"
			 + date.getFullYear() + " "
			 + (date.getHours() > 9? "" + date.getHours() : "0" + date.getHours()) + ":"
			 + (date.getMinutes() > 9? "" + date.getMinutes() : "0" + date.getMinutes()) + ":"
			 + (date.getSeconds() > 9? "" + date.getSeconds() : "0" + date.getSeconds());
			return str
		};
		
		$("#acctgTransHistoryGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var data = $("#acctgTransHistoryGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + renderDateTime(data.changeDate) + '</div>';
			$("#acctgTransHistoryDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#acctgTransHistoryDropDown").jqxDropDownButton('close');
		});
		$("#acctgTransHistoryGrid").on('rowselect', function(event){
			var args = event.args;
			var data = args.row;
			if(data){
				var dropDownContent = '<div class="innerDropdownContent">' + renderDateTime(data.changeDate) + '</div>';
				$("#acctgTransHistoryDropDown").jqxDropDownButton('setContent', dropDownContent);
				var changeDateStr = data.changeDate.getTime();
				var acctgTransEntryHisUrl = "jqxGeneralServicer?sname=JQGetListAcctgTransEntryHistory&acctgTransId=" + data.acctgTransId + "&changeDate=" + changeDateStr;
				updateHistoryGrid($("#acctgTransEntryHistoryGrid"), acctgTransEntryHisUrl);
			}
		});
		
		$("#acctgTransHistoryGrid").on('bindingcomplete', function(event){
			$("#acctgTransHistoryGrid").jqxGrid('selectrow', 0);
		});
		
		$("#closeViewHistory").click(function(){
			$("#AcctgTransEntryHistoryWindow").jqxWindow('close');
		});
	};
	var updateHistoryGrid = function(grid, url){
		var source = grid.jqxGrid('source');
		source._source.url = url;
		grid.jqxGrid('source', source);
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());
$(document).on('ready', function(){
	viewAcctgTransHistoryObj.init();
});