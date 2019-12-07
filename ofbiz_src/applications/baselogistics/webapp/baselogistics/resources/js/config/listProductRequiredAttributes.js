$(function(){
	ConfigReqDateObj.init();
});
var ConfigReqDateObj = (function() {
	var validatorAdd;
	var validatorEdit;
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		jOlbUtil.windowPopup.create($("#alterpopupWindow"), {maxWidth: 1200, minWidth: 300, width: 550, minHeight: 200, height: 330, maxHeight: 1000, resizable: false, modalZIndex: 10000, isModal: true, autoOpen: false, cancelButton: $("#newCancel"), modalOpacity: 0.7, theme:theme});
		jOlbUtil.windowPopup.create($("#editpopupWindow"), {maxWidth: 1200, minWidth: 300, width: 550, minHeight: 200, height: 330, maxHeight: 1000, resizable: false, modalZIndex: 10000, isModal: true, autoOpen: false, cancelButton: $("#editCancel"), modalOpacity: 0.7, theme:theme});
		$("#productId").jqxDropDownButton({width: 300}); 
		$('#productId').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		$("#facilityId").jqxDropDownButton({width: 300}); 
		$('#facilityId').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
		$("#contextMenu").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
		
		$("#expRequiredId").jqxDropDownList({selectedIndex: 0, placeHolder : uiLabelMap.PleaseSelectTitle, source: yesNoData, displayMember: 'description', valueMember: 'typeId', theme: theme, width: '300', height: '25'});
		$("#mnfRequiredId").jqxDropDownList({selectedIndex: 0, placeHolder : uiLabelMap.PleaseSelectTitle, source: yesNoData, displayMember: 'description', valueMember: 'typeId', theme: theme, width: '300', height: '25'});
		$("#lotRequiredId").jqxDropDownList({selectedIndex: 0, placeHolder : uiLabelMap.PleaseSelectTitle, source: yesNoData, displayMember: 'description', valueMember: 'typeId', theme: theme, width: '300', height: '25'});
		$("#expRequiredIdEdit").jqxDropDownList({selectedIndex: 0, placeHolder : uiLabelMap.PleaseSelectTitle, source: yesNoData, displayMember: 'description', valueMember: 'typeId', theme: theme, width: '300', height: '25'});
		$("#mnfRequiredIdEdit").jqxDropDownList({selectedIndex: 0, placeHolder : uiLabelMap.PleaseSelectTitle, source: yesNoData, displayMember: 'description', valueMember: 'typeId', theme: theme, width: '300', height: '25'});
		$("#lotRequiredIdEdit").jqxDropDownList({selectedIndex: 0, placeHolder : uiLabelMap.PleaseSelectTitle, source: yesNoData, displayMember: 'description', valueMember: 'typeId', theme: theme, width: '300', height: '25'});
	};
	var initElementComplex = function() {
		var sourceProduct =
		{
		     datafields:[{name: 'productId', type: 'string'},
		                 {name: 'productCode', type: 'string'},	
		                 {name: 'productCategoryId', type: 'string'},
		                 {name: 'categoryName', type: 'string'},
		         		 {name: 'productName', type: 'string'},
		         		 {name: 'quantityUomId', type: 'string'},
		 				],
		     localdata: productData,
		     datatype: "array",
		};
		var dataAdapterProduct = new $.jqx.dataAdapter(sourceProduct);
			$("#jqxgridListProduct").jqxGrid({
			     source: dataAdapterProduct,
			     filterable: true,
			     showfilterrow: true,
			     theme: theme,
			     autoheight:true,
			     pageable: true, 
			     selectionmode: 'checkbox',
			     localization: getLocalization(),
			     columns: [{text: uiLabelMap.ProductId, datafield: 'productCode', width: '20%', },
	       		   {text: uiLabelMap.ProductName, datafield: 'productName', width: '50%',},
	       		   {text: uiLabelMap.Category, datafield: 'categoryName', width: '30%',},
	       		   {text: uiLabelMap.Unit, datafield: 'quantityUomId', width: '15%', filtertype: 'checkedlist',
		       			cellsrenderer: function(row, column, value){
							for (var i = 0; i < quantityUomData.length; i ++){
								if (value && value == quantityUomData[i].uomId){
									return '<span>' + quantityUomData[i].description + '<span>';
								}
							}
							return '<span>' + value + '<span>';
						},
						createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(quantityUomData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							widget.jqxDropDownList({source: records, displayMember: 'uomId', valueMember: 'uomId',
								renderer: function(index, label, value){
						        	if (quantityUomData.length > 0) {
										for(var i = 0; i < quantityUomData.length; i++){
											if(quantityUomData[i].uomId == value){
												return '<span>' + quantityUomData[i].description + '</span>';
											}
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
			   			}
	       		   },
	     		]
		});
			
		var sourceFacility =
			{
					datafields:[{name: 'facilityCode', type: 'string'},
					            {name: 'facilityName', type: 'string'},	
					            ],
					            localdata: facilityData,
					            datatype: "array",
			};
			var dataAdapterProduct = new $.jqx.dataAdapter(sourceFacility);
			$("#jqxgridListFacility").jqxGrid({
				source: dataAdapterProduct,
				filterable: true,
				showfilterrow: true,
				theme: theme,
				autoheight:true,
				pageable: true, 
				selectionmode: 'checkbox',
				localization: getLocalization(),
				columns: [{text: uiLabelMap.FacilityId, datafield: 'facilityCode', width: '20%', },
				          {text: uiLabelMap.FacilityName, datafield: 'facilityName', minwidth: '50%',},
				          ]
			});
	};
	var initEvents = function() {
		$("#alterpopupWindow").on('close', function (){
			$("#jqxgridListProduct").jqxGrid('clearselection');
			$('#productId').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
			$("#jqxgridListFacility").jqxGrid('clearselection');
			$('#facilityId').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		});
		
		$("#editpopupWindow").on('close', function (){
			$("#productIdEdit").val();
			$("#facilityIdEdit").val();
		});
		
		$("#jqxgridListProduct").on('rowselect', function (event) {
	        var args = event.args;
	        var rows = $("#jqxgridListProduct").jqxGrid('getselectedrowindexes');
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ rows.length + ' ' +uiLabelMap.Product+'</div>';
	        $('#productId').jqxDropDownButton('setContent', dropDownContent);
	    });
		
		$("#jqxgridListFacility").on('rowselect', function (event) {
			var args = event.args;
			var rows = $("#jqxgridListFacility").jqxGrid('getselectedrowindexes');
			var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ rows.length + ' ' +uiLabelMap.Facility+'</div>';
			$('#facilityId').jqxDropDownButton('setContent', dropDownContent);
		});
		
		$("#newSave").click(function (){
			var resultValidate = validatorAdd.validate();
			if(!resultValidate) return false;
			var products = $("#jqxgridListProduct").jqxGrid('getselectedrowindexes'); 
			var listProductIds = [];
			var facilitys = $("#jqxgridListFacility").jqxGrid('getselectedrowindexes'); 
			var listFacilityIds = [];
			if (products.length > 0 && facilitys.length > 0){
				for (var i = 0; i < products.length; i ++){
					var data = $("#jqxgridListProduct").jqxGrid('getrowdata', products[i]);
					var map = {
						productId: data.productId,
					}
					listProductIds.push(map);
				}
				for (var i = 0; i < facilitys.length; i ++){
					var data = $("#jqxgridListFacility").jqxGrid('getrowdata', facilitys[i]);
					var map = {
							facilityId: data.facilityId,
					}
					listFacilityIds.push(map);
				}
				var expRequired = $("#expRequiredId").jqxDropDownList('val'); 
				var mnfRequired = $("#mnfRequiredId").jqxDropDownList('val'); 
				var lotRequired = $("#lotRequiredId").jqxDropDownList('val'); 
				var list1 = JSON.stringify(listProductIds);
				var list2 = JSON.stringify(listFacilityIds);
				
				bootbox.dialog(uiLabelMap.AreYouSureCreate, 
				[{"label": uiLabelMap.CommonCancel, 
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
				    "callback": function() {bootbox.hideAll();}
				}, 
				{"label": uiLabelMap.OK,
				    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
				    "callback": function() {
				    	Loading.show('loadingMacro');
				    	setTimeout(function(){
							$.ajax({
					    		url: "updateProductRequiredAttributes",
					    		type: "POST",
					    		async: false,
					    		data: {
					    			listProductIds: list1,
					    			listFacilityIds: list2,
					    			expRequired: expRequired,
					    			mnfRequired: mnfRequired,
					    			lotRequired: lotRequired,
					    		},
					    		success: function (res){
					    			$("#jqxgridConfigRequiredDate").jqxGrid('updatebounddata'); 
					    			$("#alterpopupWindow").jqxWindow('close');
					    		}
					    	});
						Loading.hide('loadingMacro');
				    	}, 500);
				    }
				}]);
			} 
		});
		
		$("#editSave").click(function (){
			var resultValidate = validatorEdit.validate();
			if(!resultValidate) return false;
			
			var expRequired = $("#expRequiredIdEdit").jqxDropDownList('val'); 
			var mnfRequired = $("#mnfRequiredIdEdit").jqxDropDownList('val'); 
			var lotRequired = $("#lotRequiredIdEdit").jqxDropDownList('val'); 
			var productId = $("#editProductId").val();
			var facilityId = $("#editFacilityId").val();
			var mess = uiLabelMap.AreYouSureSave;
			var url = "updateProductRequiredAttribute";
			updateProductFacility(productId, facilityId, expRequired, mnfRequired, lotRequired, mess, url, $("#editpopupWindow"));
		});
		
		$("#contextMenu").on('itemclick', function (event) {
			var data = $('#jqxgridConfigRequiredDate').jqxGrid('getRowData', $("#jqxgridConfigRequiredDate").jqxGrid('selectedrowindexes'));
			var tmpStr = $.trim($(args).text());
			if (tmpStr == uiLabelMap.Edit){
				$("#productIdEdit").text(data.productCode);
				$("#facilityIdEdit").text(data.facilityCode);
				$("#editProductId").val(data.productId);
				$("#editFacilityId").val(data.facilityId);
				$("#expRequiredIdEdit").jqxDropDownList('val', data.expRequired);
				$("#mnfRequiredIdEdit").jqxDropDownList('val', data.mnfRequired);
				$("#lotRequiredIdEdit").jqxDropDownList('val', data.lotRequired);
				$("#editpopupWindow").jqxWindow('open');
			} else if (tmpStr == uiLabelMap.BSRefresh){
				$('#jqxgridConfigRequiredDate').jqxGrid('updatebounddata');
			} else if (tmpStr == uiLabelMap.Delete){
				var mess = uiLabelMap.AreYouSureDelete;
				var url = "updateProductRequiredAttribute";
				updateProductFacility(data.productId, data.facilityId, null, null, null, mess, url);
			}
		});
	};
	
	function updateProductFacility(productId, facilityId, expRequired, mnfRequired, lotRequired, message, url, element){
		bootbox.dialog(message, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		    "callback": function() {bootbox.hideAll();}
		}, 
		{"label": uiLabelMap.OK,
		    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		    "callback": function() {
		    	Loading.show('loadingMacro');
		    	setTimeout(function(){
					$.ajax({
			    		url: url,
			    		type: "POST",
			    		async: false,
			    		data: {
			    			productId: productId,
			    			facilityId: facilityId,
			    			expRequired: expRequired,
			    			mnfRequired: mnfRequired,
			    			lotRequired: lotRequired,
			    		},
			    		success: function (res){
			    			$("#jqxgridConfigRequiredDate").jqxGrid('updatebounddata'); 
			    			if (element) {
			    				element.jqxWindow('close');
			    			}
			    		}
			    	});
				Loading.hide('loadingMacro');
		    	}, 500);
		    }
		}]);
	}
	
	var initValidateForm = function(){
		var extendRules1 = [
			{
				input: '#productId', 
			    message: uiLabelMap.FieldRequired, 
			    action: 'blur', 
			    position: 'right',
			    rule: function (input) {
			    	var products = $("#jqxgridListProduct").jqxGrid('getselectedrowindexes'); 
			    	if (products.length <= 0){
			    		return false;
			    	}
				   	return true;
			    }
			},
			{
				input: '#facilityId', 
				message: uiLabelMap.FieldRequired, 
				action: 'blur', 
				position: 'right',
				rule: function (input) {
					var products = $("#jqxgridListFacility").jqxGrid('getselectedrowindexes'); 
					if (products.length <= 0){
						return false;
					}
					return true;
				}
			},
           ];
   		var mapRules1 = [
        	{input: '#expRequiredId', type: 'validObjectNotNull', objType: 'dropDownList' },
        	{input: '#mnfRequiredId', type: 'validObjectNotNull', objType: 'dropDownList' },
        	{input: '#lotRequiredId', type: 'validObjectNotNull', objType: 'dropDownList' },
           ];
   		validatorAdd = new OlbValidator($('#alterpopupWindow'), mapRules1, extendRules1, {position: 'right'});
   		
   		var extendRules2 = [
	                    ];
 		var mapRules2 = [
			{input: '#expRequiredId', type: 'validObjectNotNull', objType: 'dropDownList' },
			{input: '#mnfRequiredId', type: 'validObjectNotNull', objType: 'dropDownList' },
			{input: '#lotRequiredId', type: 'validObjectNotNull', objType: 'dropDownList' },
         ];
 		validatorEdit = new OlbValidator($('#editpopupWindow'), mapRules2, extendRules2, {position: 'right'});
   		
	};
	
	return {
		init: init,
	}
}());