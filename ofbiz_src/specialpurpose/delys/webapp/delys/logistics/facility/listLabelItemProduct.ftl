<script>
</script>

<#assign localeStr = "VI" />
<#if locale = "en">
    <#assign localeStr = "EN" />
</#if>
<#assign columnlistProduct="
				 { text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
				 },
				 { text: '${uiLabelMap.LogLableId}', dataField: 'productId', width: '150', align: 'center', editable: false, pinned: true},
				 { text: '${uiLabelMap.ProductName}', dataField: 'productName', align: 'center', width: '150', editable: false },
				 { text: '${uiLabelMap.LogQuantityPurchaseLabelItem}', dataField: 'quantity', width: '150', align: 'center', editable: true, filterable: true, cellsalign: 'right', columntype: 'numberinput',
					 validation: function (cell, value) {
                         if (value < 0) {
                             return { result: false, message: '${uiLabelMap.QuantityMustBeGreateThanZero}'};
                         }
                         return true;
                     },
                     createeditor: function (row, cellvalue, editor) {
                         editor.jqxNumberInput({spinButtons: true , spinMode: 'simple',  min:0, decimalDigits: 0 });
                     },
                     cellsrenderer: function(row, column, value){
                    	 if (value){
                    		 return '<span style=\"text-align: right\">' + value.toLocaleString(value) +'</span>';
                    	 }
                     },
				 },
				 { text: '${uiLabelMap.Unit}', dataField: 'quantityUomIdToTransfer', align: 'center', cellsalign: 'right', filterable: false, columntype: 'dropdownlist', 
					 cellsrenderer: function (row, column, value) {
						 if (value){
                    		 return '<span style=\"text-align: right\">' + getDescriptionByUomId(value) +'</span>';
                    	 }
					 },
					 initeditor: function (row, cellvalue, editor) {
						 editor.jqxDropDownList({ source: packingData, displayMember: 'description', valueMember: 'uomId', placeHolder: '${StringUtil.wrapString(uiLabelMap.LogPleaseSelect)}',
						 });
					 },
				 },
				 "/>
<#assign dataFieldProduct="[{ name: 'productId', type: 'string' },
             	{ name: 'productName', type: 'string' },
             	{ name: 'productTypeId', type: 'string' },
             	{ name: 'quantity', type: 'number' },
             	{ name: 'quantityUomId', type: 'string' },
             	{ name: 'quantityUomIdToTransfer', type: 'string' },
	 		 	]"/>

<@jqGrid selectionmode="checkbox" idExisted="true" filtersimplemode="true" width="890" viewSize="5" pagesizeoptions="['5', '10', '15', '20', '25', '30', '50', '100']" id="jqxgridProduct" dataField=dataFieldProduct columnlist=columnlistProduct 
	clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" editable="true" customTitleProperties="ListProductTransfer" rowselectfunction="rowselectfunction(event);"
	url="jqxGeneralServicer?sname=JQGetListProductsLabelByProductTypeId" editmode="click" bindresize="false" jqGridMinimumLibEnable="false" autoheight="false" height="275" otherParams="qtyUomIds:S-getProductPackingUoms(productId)<listPackingUoms>" offmode="true"/>

<script>
	function rowselectfunction(event){
		var gridId = $(event.currentTarget).attr("id");
		if(typeof event.args.rowindex != 'number'){
	        var tmpArray = event.args.rowindex;
	        for(i = 0; i < tmpArray.length; i++){
	            if(checkRequiredPurchaseLabelItem(tmpArray[i]), gridId){
	                $('#jqxgridProduct').jqxGrid('clearselection');
	                break; // Stop for first item
	            }
	        }
	    }else{
	        if(checkRequiredPurchaseLabelItem(event.args.rowindex, gridId)){
	            $('#jqxgridProduct').jqxGrid('unselectrow', event.args.rowindex);
	        }
	    }
	}
	
	function checkRequiredPurchaseLabelItem(rowindex, gridId){
		var data = $('#jqxgridProduct').jqxGrid('getrowdata', rowindex);
		if(data == undefined){
	        bootbox.dialog("${uiLabelMap.DLYItemMissingFieldsDlv}", [{
	            "label" : "${uiLabelMap.CommonOk}",
	            "class" : "btn btn-primary standard-bootbox-bt",
	            "icon" : "fa fa-check",
	            "callback": function() {
	            		$('#jqxgridProduct').jqxGrid('begincelledit', rowindex, "quantity");
	                }
	            }]
	        );
	        return true;
		}else{
			var quantity = data.quantity;
			var quantityUomIdToTransfer = data.quantityUomIdToTransfer;
	        if(quantity == 0 || quantity == undefined){
	        	$('#jqxgridProduct').jqxGrid('unselectrow', rowindex);
	            bootbox.dialog("${uiLabelMap.DLYItemMissingFieldsDlv}", [{
	                "label" : "${uiLabelMap.CommonOk}",
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                "callback": function() {
	                		$('#jqxgridProduct').jqxGrid('begincelledit', rowindex, "quantity");
	                    }
	                }]
	            );
	            return true;
	        }else{
	        	if(quantityUomIdToTransfer == undefined){
	        		$('#jqxgridProduct').jqxGrid('unselectrow', rowindex);
	                bootbox.dialog("${uiLabelMap.LogSelectQuantiyUomId}", [{
	                    "label" : "${uiLabelMap.CommonOk}",
	                    "class" : "btn btn-primary standard-bootbox-bt",
	                    "icon" : "fa fa-check",
	                    "callback": function() {
	                    		$('#jqxgridProduct').jqxGrid('begincelledit', rowindex, "quantityUomIdToTransfer");
	                        }
	                    }]
	                );
	                return true;
	            }
	        }
		}
	}
	
	$('#alterpopupWindow').jqxValidator({
		rules: 
			[
		        { input: '#originFacilityId', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur', 
	        	   rule: function () {
	        		    var originFacilityId = $('#originFacilityId').val();
	            	    if(originFacilityId == '${StringUtil.wrapString(uiLabelMap.LogPleaseSelect)}'){
	            	    	return false; 
	            	    }else{
	            	    	return true; 
	            	    }
	            	    return true; 
	        	    }
                } 
		    ]
	});
	
	var checkCreateRequirementOrUpdate = 0;
	$("#addButtonSave").click(function () {
		if(checkCreateRequirementOrUpdate == 0){
			var row;
			var selectedIndexs = $('#jqxgridProduct').jqxGrid('getselectedrowindexes');
			var validate = $('#alterpopupWindow').jqxValidator('validate');
			if(validate != false){
				if(selectedIndexs.length == 0){
					bootbox.dialog("${uiLabelMap.DAYouNotYetChooseProduct}!", [{
		                "label" : "${uiLabelMap.CommonOk}",
		                "class" : "btn btn-primary standard-bootbox-bt",
		                "icon" : "fa fa-check",
		                }]
		            );
		            return false;
				}else{
					createRequirementPurchaseLabelItemForFacility(selectedIndexs);
				}
			}
		}
		if(checkCreateRequirementOrUpdate == 1){
			
		}
	});
	
	function createRequirementPurchaseLabelItemForFacility(selectedIndexs){
		bootbox.confirm("${uiLabelMap.DAAreYouSureCreate}",function(result){ 
			if(result){	
				var listProducts = new Array();
				for(var i = 0; i < selectedIndexs.length; i++){
					var data = $('#jqxgridProduct').jqxGrid('getrowdata', selectedIndexs[i]);
					var map = {};
					map['productId'] = data.productId;
					map['quantityUomIdToTransfer'] = data.quantityUomIdToTransfer;
					map['quantity'] = data.quantity;
					if (data.quantityUomIdToTransfer != null){
						map['quantityUomId'] = data.quantityUomIdToTransfer;
					} 
					listProducts[i] = map;
				}
				listProducts = JSON.stringify(listProducts);
				var requirementByDate = $('#requirementByDate').jqxDateTimeInput('value');
				var requirementStartDate = $('#requirementStartDate').jqxDateTimeInput('value');
				var dataRequirementByDate;
				var dataRequirementStartDate;
				if(requirementByDate == null){
					dataRequirementByDate = requirementByDate;
				}
				if(requirementByDate != null){
					dataRequirementByDate = requirementByDate.getTime();
				}
				if(requirementStartDate == null){
					dataRequirementStartDate = requirementStartDate;
				}
				if(requirementStartDate != null){
					dataRequirementStartDate = requirementStartDate.getTime();
				}
				row = { 
						requirementTypeId:"PURCHASING_LABEL_REQ", 
						facilityId:$('#facilityId').val(),
						description:$('#description').val(),
						requirementByDate:dataRequirementByDate,
						requirementStartDate:dataRequirementStartDate,
						listProducts:listProducts
		    	  };
				$("#jqxgirdLableItem").jqxGrid('addRow', null, row, "first");
				$("#alterpopupWindow").jqxWindow('close');
				$('#jqxgridProduct').jqxGrid('clearselection');
				$("#jqxgirdLableItem").jqxGrid('updatebounddata');
			}
		});
	}
	
	function showPopupDetail(requirementId){
		checkCreateRequirementOrUpdate = 1;
		var tmpS = $("#jqxgirdLableItem").jqxGrid('source');
		loadPurchaseRequirementAndRequirementItemByRequirementId(requirementId);
	}
	
	function loadPurchaseRequirementAndRequirementItemByRequirementId(requirementId){
		var listRequirementItem;
		var requirement;
		$.ajax({
			url: "loadPurchaseRequirementAndRequirementItemByRequirementId",
			type: "POST",
			data: {requirementId: requirementId},
			dataType: "json",
			async: false,
			success: function(data) {
			}
		}).done(function(data) {
			listRequirementItem = data["listRequirementItem"];
			requirement = data["requirement"];
		});
		bindingDataToUpdateRequirementPurchase(requirementId, requirement, listRequirementItem);
	}
	
	function bindingDataToUpdateRequirementPurchase(requirementId, requirement, listRequirementItem){
		$('#requirementByDate ').jqxDateTimeInput('setDate', null);
		$('#requirementStartDate ').jqxDateTimeInput('setDate', null);
		var facilityId = requirement.facilityId;
		var requiredByDate = requirement.requiredByDate;
		var requirementStartDate = requirement.requirementStartDate;
		var description = requirement.description;
		var requiredByDateConvert = null;
		var requirementStartDateConvert = null;
		if(requiredByDate != null){
			requiredByDateConvert =  new Date(requiredByDate.time);
		}
		if(requirementStartDate != null){
			requirementStartDateConvert =  new Date(requirementStartDate.time);
		}
		$('#facilityId').val(facilityId);
		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ getDescriptionByFacilityId(facilityId) +'</div>';
	    $('#originFacilityId').jqxDropDownButton('setContent', dropDownContent);
	    $('#requirementByDate').jqxDateTimeInput('val', requiredByDateConvert);
	    $('#requirementStartDate').jqxDateTimeInput('val', requirementStartDateConvert);
	    $("#description").val(description);
	    $('#alterpopupWindow').jqxWindow('setTitle', '${StringUtil.wrapString(uiLabelMap.LogUpdatePurchaseRequirementForRequirementId)}: ' + requirementId);
	    $('#alterpopupWindow').jqxWindow('open');
	}
	
	$('#alterpopupWindow').on('close', function (event) { 
		checkCreateRequirementOrUpdate = 0;
		$('#alterpopupWindow').jqxValidator('hide');
		$('#requirementByDate ').jqxDateTimeInput('setDate', null);
		$('#requirementStartDate ').jqxDateTimeInput('setDate', null);
		$('#originFacilityId').jqxDropDownButton('setContent', '${StringUtil.wrapString(uiLabelMap.LogPleaseSelect)}'); 
    	$("#jqxgridProduct").jqxGrid('updatebounddata');
    	$('#alterpopupWindow').jqxWindow('setTitle', '${StringUtil.wrapString(uiLabelMap.LogAddRequestPurchaseLabelProductTitle)}');
    });
	
	$('#alterpopupWindow').on('open', function (event) { 
    });
</script>
