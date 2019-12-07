<div id="alterPopupAlterUomEdit" style="display:none">
	<div>${uiLabelMap.BSAlternativeUom}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div style="width:95%; margin-left: auto; margin-right: auto">
				<div class="row-fluid">
					<div class='span6'>
			   		</div>
					<div class='span6'>
						<div id="we_alteruom_uomId"></div>
			   		</div>
				</div>
				<div class="row-fluid">
					<div class='span12'>
						<div id="we_alteruom_gridUom"></div>
			   		</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="we_alteruom_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="we_alteruom_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		OlbPageAlterUom.init();
	});
	
	var OlbPageAlterUom = (function(){
		var alterUomDDL;
		var alterUomData = new Array();
		var alterUomIdData = new Array();
		
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#alterPopupAlterUomEdit"), {width: 740, height: 350, cancelButton: $("#we_alteruom_alterCancel")});
		};
		var initElementComplex = function(){
			<#if configPackingAppls?has_content>
				<#list configPackingAppls as item>
					alterUomData.push({	productId: "${item.productId}", uomFromId: "${item.uomFromId}", uomToId: "${item.uomToId}", fromDate: "${item.fromDate}", thruDate: "${item.thruDate?if_exists}", quantityConvert: "${item.quantityConvert?if_exists}", price: "${item.price?if_exists}", barcode: "${item.barcode?if_exists}"});
					alterUomIdData.push("${item.uomFromId}");
				</#list>
			</#if>

			var configAlterUom = {
				width: '100%',
				height: 25,
				key: "uomId",
	    		value: "description",
	    		displayDetail: false,
				dropDownWidth: 'auto',
				autoDropDownHeight: 'auto',
				multiSelect: false,
				checkboxes: true,
				placeHolder: uiLabelMap.BSClickToChoose,
				useUrl: false,
				//url: 'jqxGeneralServicer?sname=JQListProductUom&pagesize=0',
				<#--
				renderer: function (index, label, value) {
					var datasource = $("#we_alteruom_uomId").jqxDropDownList("source");
					if (datasource) {
						var datarecords = datasource.records;
						if (datarecords) {
							var datarecord = datarecords[index];
							if (datarecord) {
			            		return label + " [" + datarecord.abbreviation + "]";
							}
						}
					}
					return label;
		        },
				-->
			};
			alterUomDDL = new OlbDropDownList($("#we_alteruom_uomId"), quantityUomData, configAlterUom, alterUomIdData);
			
			var configGridAlterUom = {
				datafields: [
					{name: 'uomFromId', type: 'string'},
					{name: 'uomToId', type: 'string'},
					{name: 'quantityConvert', type: 'string'},
					{name: 'fromDate', type: 'date', other: 'Timestamp'},
					{name: 'thruDate', type: 'date', other: 'Timestamp'},
					{name: 'barcode', type: 'string'},
					{name: 'price', type: 'number'},
				],
				columns: [
					{text: '${uiLabelMap.BSAlternativeUomId}', dataField: 'uomFromId', width: '25%', editable: false,
						cellsrenderer: function(row, column, value){
					 		var returnVal = '<div class=\"innerGridCellContent align-center\">';
				   			for (var i = 0 ; i < uomData.length; i++){
								if (value == uomData[i].uomId){
									returnVal += uomData[i].description + '</div>';
			   						return returnVal;
								}
							}
				   			returnVal += value + '</div>';
			   				return returnVal;
						},
					}, 
					{text: '${uiLabelMap.BSQuantityConvert}', dataField: 'quantityConvert', width: '25%', cellsalign: 'right', columntype: 'numberinput', cellsformat: 'd',
						cellsrenderer: function(row, column, value) {
					 		var str = '<div class=\"innerGridCellContent align-right\">';
					 		if (typeof(value) != 'undefined') {
						 		str += formatnumber(value);
					 		} else {
								str += value;
							}
							str += '</div>';
							return str;
					 	},
					 	validation: function (cell, value) {
							if (value < 0) {
								return {result: false, message: '${uiLabelMap.BSQuantityMustBeGreaterThanZero}'};
							}
							return true;
						},
						createeditor: function (row, cellvalue, editor) {
							editor.jqxNumberInput({decimalDigits: 0, digits: 9, width: '100%', theme: OlbCore.theme});
						}
					},
					{text: '${uiLabelMap.BSBarcode}', dataField: 'barcode', width: '25%', columntype: 'input', 
						createeditor: function (row, cellvalue, editor) {
							editor.jqxInput({width: '100%', theme: OlbCore.theme});
						}
					},
					{text: '${uiLabelMap.BSUnitPrice}', dataField: 'price', cellsalign: 'right', columntype: 'numberinput', cellsformat: 'd',
						cellsrenderer: function(row, column, value) {
					 		var str = '<div class=\"innerGridCellContent align-right\">';
					 		if (typeof(value) != 'undefined') {
						 		str += formatnumber(value, "${locale}", 3);
					 		} else {
								str += value;
							}
							str += '</div>';
							return str;
					 	},
					 	validation: function (cell, value) {
							if (value < 0) {
								return {result: false, message: '${uiLabelMap.BSQuantityMustBeGreaterThanZero}'};
							}
							return true;
						},
						createeditor: function (row, cellvalue, editor) {
							editor.jqxNumberInput({spinButtons: false, digits: 8, decimalDigits: 3, allowNull: true, min: 0, width: '100%', theme: OlbCore.theme});
							setTimeout(function(){
								var locale = "${locale}";
								if (locale == "vi") editor.jqxNumberInput({decimalSeparator: ",", groupSeparator: "."});
							}, 50);
						}
					},
				],
				width: '100%',
				height: 200,
				sortable: false,
				filterable: false,
				pageable: true,
				pagesize: 5,
				showfilterrow: false,
				useUtilFunc: false,
				useUrl: false,
				url: '',
				groupable: false,
				showdefaultloadelement:true,
				autoshowloadelement:true,
				selectionmode:'singlerow',
				virtualmode: false,
				editable: true,
			};
			new OlbGrid($("#we_alteruom_gridUom"), alterUomData, configGridAlterUom, []);
		};
		var initEvent = function(){
			<#if updateMode>
			removeItemAlterUomSelect("${product.quantityUomId?if_exists}");
			deleteRowAlterUomGrid("${product.quantityUomId?if_exists}");
			</#if>
			
			alterUomDDL.on("checkChange", function(event){
				if (event.args) {
                    var item = event.args.item;
                    if (item) {
                    	var originalItem = item.originalItem;
                    	if (originalItem) {
                    		if (item.checked) {
								addRowAlterUomGrid(originalItem.uomId);
                    		} else {
                    			deleteRowAlterUomGrid(originalItem.uomId);
                    		}
                    	}
                    }
                }
			});
			$("#we_alteruom_alterSave").on("click", function(){
				$("#jqxGridAlterUom").jqxGrid("clear");
				var dataRows = $("#we_alteruom_gridUom").jqxGrid("getboundrows");
				if (OlbCore.isEmpty(dataRows) || dataRows.length <= 0) {
					$("#containerGridAlterUom").css("visibility", "hidden");
				} else {
					$("#containerGridAlterUom").css("visibility", "visible");
					for (var i = 0; i < dataRows.length; i++) {
						var dataItem = dataRows[i];
						if (dataItem != window) {
							if (typeof(dataItem.uomFromId) != "undefined" && dataItem.quantityConvert > 0) {
								var rowDataNew = {
									"uomFromId": dataItem.uomFromId,
									"quantityConvert": dataItem.quantityConvert,
									"fromDate": dataItem.fromDate,
									"thruDate": dataItem.thruDate,
									"barcode": dataItem.barcode,
									"price": dataItem.price,
								};
								$("#jqxGridAlterUom").jqxGrid('addRow', null, rowDataNew, "first");
								$("#jqxGridAlterUom").jqxGrid('clearSelection');
							}
						}
					}
				}
				closeWindowNew();
			});
		};
		var removeItemAlterUomSelect = function(uomId, isReload){
			if (uomId) {
				if (isReload) {
					alterUomDDL.updateSource(null, quantityUomData, function(){
						alterUomDDL.selectItem(alterUomIdData);
					});
				}
				alterUomDDL.getListObj().jqxDropDownList("removeItem", uomId);
				//deleteRowAlterUomGrid(uomId);
			}
		};
		var addRowAlterUomGrid = function(uomId){
			if (!uomId) return false;
			
			var rowDataNew = {"uomFromId": uomId};
			var isFinded = false;
			var dataRows = $("#we_alteruom_gridUom").jqxGrid("getboundrows");
			if (dataRows) {
				for (var i = 0; i < dataRows.length; i++) {
					var dataItem = dataRows[i];
					if (dataItem != window && dataItem.uomFromId == uomId) {
						isFinded = true;
						break;
					}
				}
			}
			if (!isFinded) {
				$("#we_alteruom_gridUom").jqxGrid('addRow', null, rowDataNew, "first");
			}
			$("#we_alteruom_gridUom").jqxGrid('clearSelection');
		};
		var deleteRowAlterUomGrid = function(uomId){
			if (!uomId) return false;
			
			var dataRows = $("#we_alteruom_gridUom").jqxGrid("getboundrows");
			if (dataRows) {
				for (var i = 0; i < dataRows.length; i++) {
					var dataItem = dataRows[i];
					if (dataItem != window && dataItem.uomFromId == uomId) {
						$("#we_alteruom_gridUom").jqxGrid('deleteRow', dataItem.uid);
					}
				}
			}
			$("#we_alteruom_gridUom").jqxGrid('clearSelection');
		};
		var openWindowNew = function(){
			$("#alterPopupAlterUomEdit").jqxWindow("open");
		};
		var closeWindowNew = function(){
			$("#alterPopupAlterUomEdit").jqxWindow("close");
			
			if ($("#popoverOtherUom").length > 0) {
				OlbProductNewInfo.resetPopoverOtherUom();
			}
		};
		return {
			init: init,
			openWindowNew: openWindowNew,
			removeItemAlterUomSelect: removeItemAlterUomSelect,
		};
	}());
</script>