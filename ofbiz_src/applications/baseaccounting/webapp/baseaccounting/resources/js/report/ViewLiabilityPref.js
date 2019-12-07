var partyIdInfor ={};
var viewLiabilityPrefObj = (function(){
	var _currentPartyInfo = null;
	var _partyIdFrom = null;
	var _partyIdTo = null;
	var _partyA = "FROM";
	var _partyB = "TO";
	var init = function(){
		initInput();
		initGrid();
		initEvent();
		initValidator();
	};
	
	var initInput = function(){
		$("#partyIdFrom").jqxInput({width: '85%', height: 22, disabled: true});
		$("#address1From").jqxInput({width: '85%', height: 22});
		$("#phoneNbrFrom").jqxInput({width: '100%', height: 22});
		$("#faxFrom").jqxInput({width: '69%', height: 22});
		$("#representativeFrom").jqxInput({width: '85%', height: 22});
		$("#positionFrom").jqxInput({width: '85%', height: 22});
		
		$("#partyIdTo").jqxInput({width: '85%', height: 22, disabled: true});
		$("#address1To").jqxInput({width: '85%', height: 22});
		$("#phoneNbrTo").jqxInput({width: '100%', height: 22});
		$("#faxTo").jqxInput({width: '69%', height: 22});
		$("#representativeTo").jqxInput({width: '85%', height: 22});
		$("#positionTo").jqxInput({width: '85%', height: 22});
		
		var date = new Date();
		var fromDate = new Date(date.getFullYear(), date.getMonth(), 1);
		var thruDate = new Date(date.getFullYear(), date.getMonth() + 1, 0);

		$("#fromDate").jqxDateTimeInput({width: '86%', height: 22, value: fromDate});
		$("#thruDate").jqxDateTimeInput({width: '86%', height: 22, value: thruDate});
		
	};
	
	var initGrid = function(){
		var grid = $("#productListGrid");
		var datafield = [{name: 'productId', type: 'string'},
		                 {name: 'productName', type: 'string'},
		                 {name: 'quantityUomId', type: 'string'},
		                 {name: 'quantityUomDesc', type: 'string'},
                         {name: 'currencyUomId', type: 'string'},
                         {name: 'conversionFactor', type: 'number'},
		                 {name: 'quantity', type: 'number'},
		                 {name: 'amount', type: 'number'},
		                 {name: 'subTotalAmount', type: 'number'},
		                 ];
		
		var columns = [{text: uiLabelMap.BACCSeqId, sortable: false, filterable: false, editable: false, groupable: false, draggable: false, resizable: false,
							datafield: '', columntype: 'number', width: '5%',
							cellsrenderer: function (row, column, value) {
							    return "<div style='margin:4px;'>" + (value + 1) + "</div>";
							}
				       },
		               {text: uiLabelMap.BACCProductName, datafield: 'productName', width: '35%'},
		               {text: uiLabelMap.BACCEquipQuantityUom, datafield: 'quantityUomDesc', width: '15%'},
		               {text: uiLabelMap.BACCQuantity, datafield: 'quantity', width: '10%', columntype: 'numberinput', filtertype: 'number', 
		            	   cellsrenderer: function (row, column, value) {
		            		   if(typeof(value) == 'number'){
		            			   return '<span class="align-right">' + value + '</span>';
		            		   }
		            	   }
		               },
		               {text: uiLabelMap.BACCUnitPrice, datafield: 'amount', width: '15%', columntype: 'numberinput', filtertype: 'number',
		            	   cellsrenderer: function (row, column, value) {
                               var data = grid.jqxGrid('getrowdata', row);
                               if(typeof(value) == 'number'){
		            			   return '<span class="align-right">' + formatcurrency(value, data.currencyUomId) + '</span>';
		            		   }
		            	   }
		               },
                        {text: uiLabelMap.BACCConversionFactor, datafield: 'conversionFactor', width: '15%', columntype: 'numberinput', filtertype: 'number',
                            cellsrenderer: function (row, column, value) {
                                var data = grid.jqxGrid('getrowdata', row);
                                if(typeof(value) == 'number'){
                                    return '<span class="align-right">' + formatcurrency(value) + '</span>';
                                }
                            }
                        },
		               {text: uiLabelMap.BACCTotal, datafield: 'subTotalAmount', width: '20%', columntype: 'numberinput', filtertype: 'number',
		            	   cellsrenderer: function (row, column, value) {
		            		   if(typeof(value) == 'number'){
		            			   return '<span class="align-right">' + formatcurrency(value) + '</span>';
		            		   }
		            	   }
		               },
		               ];
		var config = {
				width: '100%', 
		   		virtualmode: true,
		   		showfilterrow: true,
		   		pageable: true,
		   		sortable: true,
		        filterable: true,
		        editable: false,
		        url: '', 
		        showtoolbar: false,
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	
	var initEvent = function(){
		$("#editPartyFromBtn").click(function(e){
			_currentPartyInfo = _partyA;
			editPartyObj.openWindow();
			$("#liabilityPrefInfoForm").jqxValidator('hide');
		});
		$("#editPartyToBtn").click(function(e){
			_currentPartyInfo = _partyB;
			editPartyObj.openWindow();
			$("#liabilityPrefInfoForm").jqxValidator('hide');
		});
		$("#editPartyWindow").on("getPartyInfoComplete", function(e){
			var data = editPartyObj.getPartyInfo();
			if(_currentPartyInfo === _partyA){
				_partyIdTo = data.partyId;
				partyIdInfor.partyTo = data.partyId;
				$("#partyIdFrom").val(data.fullName.trim());
				$("#address1From").val(data.address);
				$("#phoneNbrFrom").val(data.phoneNbr);
				$("#faxFrom").val(data.faxNbr);

                _partyIdFrom = globalVar.orgId;
                partyIdInfor.partyFrom = globalVar.orgId;
                $("#partyIdTo").val(globalVar.orgFullName.trim());
                $("#address1To").val(globalVar.orgAddress);
                $("#phoneNbrTo").val(globalVar.orgTelephone);
                $("#faxTo").val(globalVar.orgFax);
			}else if(_currentPartyInfo === _partyB){
				_partyIdFrom = data.partyId;
				partyIdInfor.partyFrom = data.partyId;
				$("#partyIdTo").val(data.fullName.trim());
				$("#address1To").val(data.address);
				$("#phoneNbrTo").val(data.phoneNbr);
				$("#faxTo").val(data.faxNbr);

                _partyIdTo = globalVar.orgId;
                partyIdInfor.partyTo = globalVar.orgId;
                $("#partyIdFrom").val(globalVar.orgFullName.trim());
                $("#address1From").val(globalVar.orgAddress);
                $("#phoneNbrFrom").val(globalVar.orgTelephone);
                $("#faxFrom").val(globalVar.orgFax);
			}
			_currentPartyInfo = "";
		});
		$("#liabilityPrefBtn").click(function(e){
			var valid = $("#liabilityPrefInfoForm").jqxValidator('validate');
			if(!valid){
				return;
			}
			Loading.show('loadingMacro');
			var fromDate = $("#fromDate").jqxDateTimeInput('val', 'date');
			var thruDate = $("#thruDate").jqxDateTimeInput('val', 'date');
			var data = {partyIdFrom: _partyIdFrom, partyIdTo: _partyIdTo, fromDate: fromDate.getTime(), thruDate: thruDate.getTime()};
			$.ajax({
				url: 'getLiabilityPreferenceInfo',
				data: data,
				type: 'POST',
				success: function(response){
					if(response._ERROR_MESSAGE_){
						bootbox.dialog(response._ERROR_MESSAGE_,
								[
								 {
									 "label" : uiLabelMap.CommonClose,
									 "class" : "btn-danger btn-small icon-remove open-sans",
								 }]		
						);	
						return;
					}
					$("#openLiability").html(formatcurrency(response.openingBalance));
					$("#amountPaid").html(formatcurrency(response.amountPaid));
					$("#amountOther").html(formatcurrency(response.amountOther));
					var thruDateDesc = (thruDate.getDate() > 9? thruDate.getDate() : ("0" + thruDate.getDate())) + "/"
									+ (thruDate.getMonth() >= 9? (thruDate.getMonth() + 1) : ("0" + (thruDate.getMonth() + 1))) + "/"
									+ (thruDate.getFullYear());
					$("#thruDateDesc").html(thruDateDesc);
					$("#partyFromName").html($("#partyIdTo").val());
					$("#amountNotPaid").html(formatcurrency(response.amountNotPaid));
					var url = 'jqxGeneralServicer?sname=JQGetListProductLiabilityPref&partyIdFrom=' + _partyIdFrom + "&partyIdTo=" + _partyIdTo 
								+ "&fromDate=" + fromDate.getTime() + "&thruDate=" + thruDate.getTime();
					updateGridUrl($("#productListGrid"), url);
				},
				complete: function(){
					Loading.hide('loadingMacro');
				}
			});
		});
	};
	
	var updateGridUrl = function(grid, url){
		var source = grid.jqxGrid('source');
		source._source.url = url;
		grid.jqxGrid('source', source);
	};
	
	var initValidator = function(){
		$("#liabilityPrefInfoForm").jqxValidator({
			position: 'bottom',
			rules: [
				{ input: '#partyIdFrom', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if($(input).val()){
							return true;
						}
						return false;
					}
				},
				{ input: '#partyIdTo', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if($(input).val()){
							return true;
						}
						return false;
					}
				},
				{ input: '#fromDate', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if($(input).val()){
							return true;
						}
						return false;
					}
				},
				{ input: '#fromDate', message: uiLabelMap.FromDateLessThanEqualThruDate, action: 'keyup, change', 
					rule: function (input, commit) {
						var fromDate = $(input).jqxDateTimeInput('val', 'date');
						var thruDate = $("#thruDate").jqxDateTimeInput('val', 'date');
						if(fromDate && thruDate && fromDate > thruDate){
							return false;
						}
						return true;
					}
				},
				{ input: '#thruDate', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if($(input).val()){
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
	}
}());

var editPartyObj = (function(){
	var _partyInfo = {};
	var init = function(){
		initGrid();
		initWindow();
		initEvent();
	};
	var initGrid = function(){
		var grid = $("#listPartyGrid");
		var datafield = [
		                 {name: 'partyId', type: 'string'}, 
		                 {name: 'partyCode', type: 'string'}, 
		                 {name: 'fullName', type: 'string'}
		                 ];
		
		var columns = [
						{text: uiLabelMap.BACCOrganizationId, datafield: 'partyCode', width: '30%'},
						{text: uiLabelMap.BACCFullName, datafield: 'fullName'}
					];
		var customControlAdvance = "<div id='enumPartyTypeIdList'></div>";
		var initEnumTypeList = function(e){
			accutils.createJqxDropDownList($("#enumPartyTypeIdList"), globalVar.enumPartyTypeArr, {valueMember: 'enumId', displayMember: 'description', width: 180, height: 22});
			$("#enumPartyTypeIdList").on('select', function(event){
				var args = event.args;
				if(args){
					var item = args.item;
			    	var value = item.value;
			    	updateGridUrl($("#listPartyGrid"), 'jqxGeneralServicer?sname=JqxGetParties&enumId=' + value);
				}
			});
			$("#enumPartyTypeIdList").jqxDropDownList({selectedIndex: 0});
		};
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "listPartyGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.BACCListObject + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        grid.on('loadCustomControlAdvance', function(event){
	        	initEnumTypeList();
	        });
	        Grid.triggerToolbarEvent(grid, container, customControlAdvance);
		};
		var config = {
		   		width: '100%', 
		   		virtualmode: true,
		   		showfilterrow: true,
		   		pageable: true,
		   		sortable: true,
		        filterable: true,
		        editable: false,
		        url: '', 
		        showtoolbar: true,
		        rendertoolbar: rendertoolbar,
	        	source: {
	        		pagesize: 10,
	        	}
	   	};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#editPartyWindow"), 550, 480);
	};
	var initEvent = function(){
		$("#editPartyWindow").on('open', function(e){
			if($("#enumPartyTypeIdList").length > 0){
				$("#enumPartyTypeIdList").jqxDropDownList({selectedIndex: 0});
			}
		});
		$("#editPartyWindow").on('close', function(e){
			$("#enumPartyTypeIdList").jqxDropDownList('clearSelection');
			$("#listPartyGrid").jqxGrid('clearselection');
			$("#listPartyGrid").jqxGrid('clearfilters');
			updateGridUrl($("#listPartyGrid"), '');
			$("#listPartyGrid").jqxGrid('gotopage', 0);
			_partyInfo = {};
		});
		$("#cancelChooseParty").click(function(e){
			$("#editPartyWindow").jqxWindow('close');
		});
		$("#selectedParty").click(function(e){
			var rowindex = $("#listPartyGrid").jqxGrid('getselectedrowindex');
			if(rowindex > -1){
				var rowData = $("#listPartyGrid").jqxGrid('getrowdata', rowindex);
				var partyId = rowData.partyId;
				Loading.show('loadingMacro');
				$.ajax({
					url: 'getPartyContact',
					data: {partyId: partyId},
					type: 'POST',
					success: function(response){
						if(response.responseMessage == "error"){
							bootbox.dialog(response.errorMessage,
									[
									 {
										 "label" : uiLabelMap.CommonClose,
										 "class" : "btn-danger btn-small icon-remove open-sans",
									 }]		
							);	
							return;
						}
						_partyInfo = response;
						_partyInfo.partyId = partyId;
						_partyInfo.fullName = rowData.fullName;
						$("#editPartyWindow").trigger('getPartyInfoComplete');
						$("#editPartyWindow").jqxWindow('close');
					},
					complete: function(){
						Loading.hide('loadingMacro');
					}
				});
			}
		});
	};
	var updateGridUrl = function(grid, url){
		var source = grid.jqxGrid('source');
    	source._source.url = url;
    	grid.jqxGrid('source', source);
	};
	var openWindow = function(){
		accutils.openJqxWindow($("#editPartyWindow"));
	};
	var getPartyInfo = function(){
		return _partyInfo;
	};
	return{
		init: init,
		openWindow: openWindow,
		getPartyInfo: getPartyInfo
	}
}());
$(document).on('ready', function(){
	$.jqx.theme = 'olbius';
	viewLiabilityPrefObj.init();
	editPartyObj.init();
});

function exportExcel(){
	if($('#productListGrid').jqxGrid('getrows').length >0){
		var winName='ExportExcel';
		var winURL = 'exportViewLiabilityPref';
		var form = document.createElement("form");
		form.setAttribute("method", "post");
		form.setAttribute("action", winURL);
		form.setAttribute("target", "_blank");
		var data ={};
		data.partyFromName = $("#partyIdFrom").jqxInput('val');
		data.partyIdFrom =partyIdInfor.partyFrom;
		data.address1From = $("#address1From").jqxInput('val');
		data.phoneNbrFrom = $("#phoneNbrFrom").jqxInput('val');
		data.faxFrom = $("#faxFrom").jqxInput('val');
		data.representativeFrom = $("#representativeFrom").jqxInput('val');
		data.positionFrom = $("#positionFrom").jqxInput('val');
		
		data.partyToName = $("#partyIdTo").jqxInput('val');
		data.partyIdTo = partyIdInfor.partyTo;
		data.address1To = $("#address1To").jqxInput('val');
		data.phoneNbrTo = $("#phoneNbrTo").jqxInput('val');
		data.faxTo = $("#faxTo").jqxInput('val');
		data.representativeTo = $("#representativeTo").jqxInput('val');
		data.positionTo = $("#positionTo").jqxInput('val');
		
		var fromdate = $("#fromDate").jqxDateTimeInput('val', 'date');
		var thruDate = $("#thruDate").jqxDateTimeInput('val', 'date');
		data.fromDate = fromdate.getTime();
		data.thruDate = thruDate.getTime();
		
		for(var key in data){
			if(data.hasOwnProperty(key)){
				var input = document.createElement('input');
				input.type = 'hidden';
				input.name = key;
				input.value = data[key];
				form.appendChild(input);
			}
		}
		document.body.appendChild(form);
		window.open(' ', winName);
		form.target = winName;
		form.submit();                 
		document.body.removeChild(form);
	}else{
		bootbox.alert(uiLabelMap.ReportCheckNotData);
	}
}