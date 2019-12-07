<script>
	var initProductSelect = function(dropdown, grid) {
		var datafields = [{
			name : 'productId',
			type : 'string'
		}, {
			name : 'brandName',
			type : 'string'
		}, {
			name : 'internalName',
			type : 'string'
		}, {
			name : 'productTypeId',
			type : 'string'
		}];
		var columns = [{
			text : '${uiLabelMap.FormFieldTitle_productId}',
			datafield : 'productId',
			width : 150
		}, {
			text : '${uiLabelMap.ProductBrandName}',
			datafield : 'brandName'
		}, {
			text : '${uiLabelMap.ProductInternalName}',
			datafield : 'internalName'
		}, {
			text : '${uiLabelMap.ProductProductType}',
			datafield : 'productTypeId'
		}];
		GridUtils.initDropDownButton({
			url : "getListProduct",
			autorowheight : true,
			filterable : true,
			source : {
				cache : true,
				pagesize : 5
			}
		}, datafields, columns, null, grid, dropdown, "productId");
	};
	var initPartySelect = function(dropdown, grid, width) {
		var datafields = [{
			name : 'partyId',
			type : 'string'
		},
		<#if isGetListEmpl?exists && isGetListEmpl?has_content && isGetListEmpl == "true">
		{
			name : 'fullNameFirstNameFirst',
			type : 'string'
		}	
		<#else>
		 {
			name : 'partyTypeId',
			type : 'string'
		}, {
			name : 'firstName',
			type : 'string'
		}, {
			name : 'lastName',
			type : 'string'
		}, {
			name : 'groupName',
			type : 'string'
		}
		</#if>
		];
		var columns = [{
		<#if isGetListEmpl?exists && isGetListEmpl?has_content && isGetListEmpl == "true">
			text : '${uiLabelMap.EmployeeId}',
		<#else>
			text : '${uiLabelMap.accApInvoice_partyId}',
		</#if>	
			datafield : 'partyId',
			width : 200,
			pinned : true
		},
		<#if isGetListEmpl?exists && isGetListEmpl?has_content && isGetListEmpl == "true">
		 {
			text : '${uiLabelMap.FormFieldTitle_firstName}',
			datafield : 'fullNameFirstNameFirst',
			cellsrenderer : function(row, columns, value, defaulthtml, columnproperties, rowdata) {
				var name = rowdata.fullNameFirstNameFirst ? rowdata.fullNameFirstNameFirst : "";
				return "<div class='custom-cell-grid'>" + name + "</div>";
			}
		}
		<#else>
			 {
			text : '${uiLabelMap.accApInvoice_partyTypeId}',
			datafield : 'partyTypeId',
			width : 200,
			cellsrenderer : function(row, columns, value) {
				var group = "${uiLabelMap.PartyGroup}";
				var person = "${uiLabelMap.Person}";
				if (value == "PARTY_GROUP") {
					return "<div class='custom-cell-grid'>" + group + "</div>";
				} else if (value == "PERSON") {
					return "<div class='custom-cell-grid'>" + person + "</div>";
				}
				return value;
			}
			}, {
				text : '${uiLabelMap.accAccountingFromParty}',
				datafield : 'groupName',
				width : 200
			},
			 {
			text : '${uiLabelMap.FormFieldTitle_firstName}',
			datafield : 'firstName',
			width : 200,
			cellsrenderer : function(row, columns, value, defaulthtml, columnproperties, rowdata) {
				var first = rowdata.firstName ? rowdata.firstName : "";
				var last = rowdata.lastName ? rowdata.lastName : "";
				return "<div class='custom-cell-grid'>" + first + " " + last + "</div>";
			}
		}
		</#if>
		];
		GridUtils.initDropDownButton({
			<#if isGetListEmpl?exists && isGetListEmpl?has_content && isGetListEmpl == "true">
			url : "getListEmployeeInOrganization",
			<#else>
			url : "getFromParty",
			</#if>
			autorowheight : true,
			filterable : true,
			width : width ? width : 600,
			source : {
				cache : true,
				pagesize : 5
			}
		}, datafields, columns, null, grid, dropdown, "partyId");
	};
	var initPaymentSelect = function(dropdown, grid) {
		var datafields = [{
			name : 'paymentId',
			type : 'string'
		}, {
			name : 'partyIdFrom',
			type : 'string'
		}, {
			name : 'partyIdTo',
			type : 'string'
		}, {
			name : 'effectiveDate',
			type : 'string'
		}, {
			name : 'amount',
			type : 'string'
		}, {
			name : 'currencyUomId',
			type : 'string'
		}];
		var columns = [{
			text : '${uiLabelMap.paymentId}',
			datafield : 'paymentId',
			width : 150
		}, {
			text : '${uiLabelMap.paymentFrom}',
			datafield : 'partyIdFrom',
			width : 150
		}, {
			text : '${uiLabelMap.paymentTo}',
			datafield : 'partyIdTo',
			width : 150
		}, {
			text : '${uiLabelMap.effectiveDate}',
			datafield : 'effectiveDate',
			width : 150
		}, {
			text : '${uiLabelMap.amount}',
			datafield : 'amount',
			width : 150
		}, {
			text : '${uiLabelMap.currencyUomId}',
			datafield : 'currencyUomId',
			width : 150
		}];
		GridUtils.initDropDownButton({
			url : "getListPayment",
			autorowheight : true,
			filterable : true,
			width : 400,
			source : {
				cache : true,
				pagesize : 5
			}
		}, datafields, columns, null, grid, dropdown, "paymentId");
	};
	var initAssetsSelect = function(dropdown, grid) {
		var datafields = [{
			name : 'fixedAssetId',
			type : 'string'
		}, {
			name : 'fixedAssetName',
			type : 'string'
		}, {
			name : 'fixedAssetTypeId',
			type : 'string'
		}, {
			name : 'parentFixedAssetId',
			type : 'string'
		}, {
			name : 'dateAcquired',
			type : 'date',
			other : 'Timestamp'
		}, {
			name : 'expectedEndOfLife',
			type : 'date',
			other : 'Timestamp'
		}, {
			name : 'purchaseCost',
			type : 'number'
		}, {
			name : 'salvageValue',
			type : 'number'
		}, {
			name : 'depreciation',
			type : 'number'
		}, {
			name : 'plannedPastDepreciationTotal',
			type : 'number'
		}];
		var columns = [{
			text : '${StringUtil.wrapString(uiLabelMap.AccountingFixedAssetId)}',
			datafield : 'fixedAssetId',
			width : 150,
			cellsrenderer : function(row, colum, value) {
				var data = grid.jqxGrid('getrowdata', row);
				return '<span><a href="' + 'EditFixedAsset?fixedAssetId=' + data.fixedAssetId + '">' + data.fixedAssetId + '</a></span>';
			}
		}, {
			text : '${StringUtil.wrapString(uiLabelMap.AccountingFixedAssetName)}',
			datafield : 'fixedAssetName',
			width : 200
		}, {
			text : '${StringUtil.wrapString(uiLabelMap.AccountingFixedAssetTypeId)}',
			datafield : 'fixedAssetTypeId',
			width : 150,
			filtertype : 'checkedlist',
			cellsrenderer : function(row, column, value) {
				var data = grid.jqxGrid('getrowdata', row);
				for ( i = 0; i < fixedAssetTypeData.length; i++) {
					if (data.fixedAssetTypeId == fixedAssetTypeData[i].fixedAssetTypeId) {
						return '<span title=' + value + '>' + fixedAssetTypeData[i].description + '</span>';
					}
				}

				return '<span title=' + value + '>' + value + '</span>';
			},
			createfilterwidget : function(column, columnElement, widget) {
				var filterBoxAdapter2 = new $.jqx.dataAdapter(fixedAssetTypeData, {
					autoBind : true
				});
				var uniqueRecords2 = filterBoxAdapter2.records;
				uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
				widget.jqxDropDownList({
					source : uniqueRecords2,
					displayMember : 'fixedAssetTypeId',
					valueMember : 'fixedAssetTypeId',
					height : '21px',
					renderer : function(index, label, value) {
						for ( i = 0; i < uniqueRecords2.length; i++) {
							if (uniqueRecords2[i].roleTypeId == value) {
								return uniqueRecords2[i].description;
							}
						}
						return value;
					}
				});
				widget.jqxDropDownList('checkAll');
			}
		}, {
			text : '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_dateAcquired)}',
			width : 150,
			datafield : 'dateAcquired',
			cellsformat : 'dd/MM/yyyy',
			filtertype : 'range'
		}, {
			text : '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_expectedEndOfLife)}',
			width : 220,
			datafield : 'expectedEndOfLife',
			cellsformat : 'dd/MM/yyyy',
			filtertype : 'range'
		}, {
			text : '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_purchaseCost)}',
			width : 150,
			datafield : 'purchaseCost',
			cellsrenderer : function(row, colum, value) {
				var data = grid.jqxGrid('getrowdata', row);
				return "<span>" + formatcurrency(data.purchaseCost, data.purchaseCostUomId) + "</span>";
			}
		}, {
			text : '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_salvageValue)}',
			width : 150,
			datafield : 'salvageValue',
			cellsrenderer : function(row, colum, value) {
				var data = grid.jqxGrid('getrowdata', row);
				return "<span>" + formatcurrency(data.salvageValue, data.purchaseCostUomId) + "</span>";
			}
		}, {
			text : '${StringUtil.wrapString(uiLabelMap.AccountingDepreciation)}',
			width : 150,
			datafield : 'depreciation',
			cellsrenderer : function(row, colum, value) {
				var data = grid.jqxGrid('getrowdata', row);
				return "<span>" + formatcurrency(data.depreciation, data.purchaseCostUomId) + "</span>";
			}
		}, {
			text : '${StringUtil.wrapString(uiLabelMap.accPlannedPastDepreciationTotal)}',
			width : 200,
			datafield : 'plannedPastDepreciationTotal',
			cellsrenderer : function(row, colum, value) {
				var data = grid.jqxGrid('getrowdata', row);
				return "<span>" + formatcurrency(data.plannedPastDepreciationTotal, data.purchaseCostUomId) + "</span>";
			}
		}];
		GridUtils.initDropDownButton({
			url : "listFixedAssetsJqx",
			autorowheight : true,
			filterable : true,
			width : 400,
			source : {
				cache : true,
				pagesize : 3
			}
		}, datafields, columns, null, grid, dropdown, "fixedAssetId");
	};

	var initInvoiceSelect = function(dropdown, grid) {
		var datafields = [{
			name : 'invoiceId',
			type : 'string'
		}, {
			name : 'invoiceTypeId',
			type : 'string'
		}, {
			name : 'partyIdFrom',
			type : 'string'
		}, {
			name : 'partyId',
			type : 'string'
		}, {
			name : 'statusId',
			type : 'string'
		}, {
			name : 'description',
			type : 'string'
		}];
		var columns = [{
			text : '${uiLabelMap.FormFieldTitle_invoiceId}',
			datafield : 'invoiceId',
			width : 150
		}, {
			text : '${uiLabelMap.FormFieldTitle_invoiceTypeId}',
			datafield : 'invoiceTypeId',
			width : 150
		}, {
			text : '${uiLabelMap.partyIdFrom}',
			datafield : 'partyIdFrom',
			width : 150
		}, {
			text : '${uiLabelMap.partyIdTo}',
			datafield : 'partyId',
			width : 150
		}, {
			text : '${uiLabelMap.statusIdDT}',
			datafield : 'statusId',
			width : 150
		}, {
			text : '${uiLabelMap.Description}',
			datafield : 'description',
			width : 200
		}];
		GridUtils.initDropDownButton({
			url : "getListInvoice",
			autorowheight : true,
			filterable : true,
			source : {
				cache : true,
				pagesize : 5
			}
		}, datafields, columns, null, grid, dropdown, "invoiceId");
	};

	var initWorkEffortSelect = function(dropdown, grid,width) {
		var datafields = [{
			name : 'workEffortId',
			type : 'string'
		}, {
			name : 'workEffortName',
			type : 'string'
		}, {
			name : 'workEffortTypeId',
			type : 'string'
		}, {
			name : 'contactMechTypeId',
			type : 'string'
		}];
		var columns = [{
			text : '${uiLabelMap.FormFieldTitle_workEffortId}',
			datafield : 'workEffortId',
			width : 150
		}, {
			text : '${uiLabelMap.FormFieldTitle_workEffortName}',
			datafield : 'workEffortName',
			width : 150
		}, {
			text : '${uiLabelMap.FormFieldTitle_workEffortTypeId}',
			datafield : 'workEffortTypeId',
			width : 150
		}, {
			text : '${uiLabelMap.FormFieldTitle_contactMechTypeId}',
			datafield : 'contactMechTypeId',
			width : 150
		}];
		GridUtils.initDropDownButton({
			url : "getListWorkEffort",
			autorowheight : true,
			filterable : true,
			width : width ? width : 400,
			source : {
				cache : true,
				pagesize : 5
			}
		}, datafields, columns, null, grid, dropdown, "workEffortId");
	};
	var initShipmentSelect = function(dropdown, grid) {
		var datafields = [{
			name : 'shipmentId',
			type : 'string'
		}, {
			name : 'shipmentTypeId',
			type : 'string'
		}, {
			name : 'statusId',
			type : 'string'
		}, {
			name : 'partyIdFrom',
			type : 'string'
		}, {
			name : 'partyIdTo',
			type : 'string'
		}];
		var columns = [{
			text : '${uiLabelMap.shipmentId}',
			datafield : 'shipmentId',
			width : 150
		}, {
			text : '${uiLabelMap.shipmentTypeId}',
			datafield : 'shipmentTypeId',
			width : 150
		}, {
			text : '${uiLabelMap.statusId}',
			datafield : 'statusId',
			width : 150
		}, {
			text : '${uiLabelMap.partyIdFrom}',
			datafield : 'partyIdFrom',
			width : 150
		}, {
			text : '${uiLabelMap.partyIdTo}',
			datafield : 'partyIdTo',
			width : 150
		}];
		GridUtils.initDropDownButton({
			url : "getListShipment",
			autorowheight : true,
			filterable : true,
			source : {
				cache : true,
				pagesize : 5
			}
		}, datafields, columns, null, grid, dropdown, "shipmentId");
	};
	
	var initOrganizationSelect = function(dropdown, grid) {
		var datafields = [{
			name : 'partyId',
			type : 'string'
		}, {
			name : 'firstName',
			type : 'string'
		}, {
			name : 'lastName',
			type : 'string'
		}, {
			name : 'middleName',
			type : 'string'
		}, {
			name : 'groupName',
			type : 'string'
		}];
		var columns = [{
			text : '${uiLabelMap.DAFormFieldTitle_organizationPartyId}',
			datafield : 'partyId',
			width : 150
		}, {
			text : '${uiLabelMap.accPeriodPartyTypeId}',
			datafield : 'partyTypeId',
			width : 150
		}, {
			text : '${uiLabelMap.FormFieldTitle_firstName}',
			datafield : 'firstName',
			width : 150
		}, {
			text : '${uiLabelMap.FormFieldTitle_lastName}',
			datafield : 'lastName',
			width : 150
		}, {
			text : '${uiLabelMap.organizationName}',
			datafield : 'groupName',
			width : 150
		}];
		GridUtils.initDropDownButton({
			url : "JQGetListPartiesAcctgPreference",
			autorowheight : true,
			filterable : true,
			source : {
				cache : true,
				pagesize : 5
			},
			dropdown  : {
				width : 250,
				height : 25
			}
		}, datafields, columns, null, grid, dropdown, "partyId");
	};
	
</script>