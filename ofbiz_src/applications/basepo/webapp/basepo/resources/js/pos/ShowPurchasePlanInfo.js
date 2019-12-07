var showPurchasePlanObject = (function() {
	var cellCanEdit = function(row, columnfield, value) {
		return "yellow";
	};
	var dataField = [ {
		name : "productId",
		type : "string"
	}, {
		name : "facilityId",
		type : "string"
	}, {
		name : "facilityName",
		type : "string"
	}, {
		name : "qoh",
		type : "number"
	}, {
		name : "qoo",
		type : "number"
	}, {
		name : "qpdL",
		type : "number"
	}, {
		name : "qpdS",
		type : "number"
	}, {
		name : "lidL",
		type : "number"
	}, {
		name : "lidS",
		type : "number"
	}, {
		name : "quantity",
		type : "number"
	}

	];
	var initColum = function(idGrid) {
		var column = [ {
			text : uiLabelMap.SettingFacilityId,
			datafield : "facilityId",
			editable : false,
			width : 70,
			cellsalign : "left"
		}, {
			text : uiLabelMap.SettingFacilityName,
			datafield : "facilityName",
			editable : false,
			cellsalign : "left",
			width : 180
		}, {
			text : uiLabelMap.SettingSummaryQOH,
			datafield : "qoh",
			editable : false,
			cellsalign : "left",
			width : 70
		}, {
			text : uiLabelMap.SettingQty,
			datafield : "quantity",
			width : 100,
			editable : false,
			cellsalign : "right",
			cellsformat : "d",
		}, {
			text : uiLabelMap.SettingQOO,
			datafield : "qoo",
			editable : false,
			width : 70,
			cellsalign : "left"
		}, {
			text : uiLabelMap.SettingQPDL,
			datafield : "qpdL",
			editable : false,
			width : 70,
			cellsalign : "right"
		}, {
			text : uiLabelMap.SettingQPDS,
			datafield : "qpdS",
			editable : false,
			width : 70,
			cellsalign : "right"
		}, {
			text : uiLabelMap.SettingLIDL,
			datafield : "lidL",
			editable : false,
			width : 70,
			cellsalign : "right"
		}, {
			text : uiLabelMap.SettingLIDS,
			datafield : "lidS",
			editable : false,
			width : 70,
			cellsalign : "right"
		}

		];
		return column;
	};

	var initRowDetail = function(index, parentElement, gridElement, datarecord) {
		var productId = datarecord.productId;
		var planId = datarecord.planId;
		var urlStr = "JQGetRowDetailTransferPlanHistory&productId=" + productId
				+ "&planId=" + planId;
		var id = datarecord.uid.toString();
		var grid = $($(parentElement).children()[0]);
		$(grid).attr("id", productId + "jqxgridRowDetail");
		var config = {
			url : urlStr,
			width : "98%",
			autoheight : true,
			showtoolbar : false,
			editable : true,
			editmode : "click",
			showheader : true,
			selectionmode : "singlecell",
			theme : "energyblue",
			pageable : false,
			localization : getLocalization()
		};
		Grid.initGrid(config, dataField, initColum(productId
				+ "jqxgridRowDetail"), null, grid);
	};
	return {
		initRowDetail : initRowDetail
	}
}());
