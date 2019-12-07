var searchProductSupplier = function(container){
	var str = "<div id='supplierDropdown' class='pull-right margin-top5' style='margin-top: 4px;'>" +
				"<div id='supplierListGrid'></div>" +
			  "</div>";
	container.append(str);
	$('#supplierDropdown').jqxDropDownButton({width: 300, height: 25, theme: 'olbius', dropDownHorizontalAlignment: 'right'});
    $("#supplierDropdown").jqxDropDownButton('setContent', '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + BPOSChooseSupplier + '</div>');
	$('#supplierDropdown').on('close', function (event) {
		var rowindexes = $('#supplierListGrid').jqxGrid('getselectedrowindexes');
		var supplierIds = new Array();
		var partyId = "";
		var groupName = "";
		for ( var x in rowindexes) {
			var data = $('#supplierListGrid').jqxGrid('getrowdata', rowindexes[x]);
			partyId += data.partyId + ",";
			groupName += data.groupName + ", ";
		}
		if (groupName.length < 40) {
			groupName = groupName.substring(0, groupName.length - 2);
		} else {
			groupName = groupName.substring(0, 40) + "...";
		}
		if (groupName == ""){
			groupName = BPOSChooseSupplier;
		}
		partyId = partyId.substring(0, partyId.length - 1);
		var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + groupName + '</div>';
        $("#supplierDropdown").jqxDropDownButton('setContent', dropDownContent);
		searchSupplier(partyId);
	});
	
	var sourceSupplier =
    {
        datatype: "json",
        datafields: [
            { name: 'partyId', type: 'string' },
            { name: 'groupName', type: 'string' },
            { name: 'companyEmail', type: 'string' },
            { name: 'companyAddress', type: 'string' },
            { name: 'companyPhone', type: 'string' }
        ],
        updaterow: function (rowid, rowdata) {
			// synchronize with the server - send update command   
		},
		beforeprocessing: function (data) {
			sourceSupplier.totalrecords = data.TotalRows;
		},
		filter: function () {
			// update the grid and send a request to the server.
			$("#supplierListGrid").jqxGrid('updatebounddata');
		},
		pager: function (pagenum, pagesize, oldpagenum) {
			// callback called when a page or page size is changed.
		},
		sort: function () {
			$("#supplierListGrid").jqxGrid('updatebounddata');
		},
		sortcolumn: 'partyId',
		sortdirection: 'asc',
		type: 'POST',
		data: {
			noConditionFind: 'Y',
			conditionsFind: 'N'
		},
		pagesize:15,
        root: "results",
        url: "jqxGeneralServicer?sname=JQListSupplier"
    };
	
    var supplierDataAdapter = new $.jqx.dataAdapter(sourceSupplier,
    {
		 formatData: function (data) {
            if (data.filterscount) {
                var filterListFields = "";
                var tmpFieldName = "";
                for (var i = 0; i < data.filterscount; i++) {
                    var filterValue = data["filtervalue" + i];
                    var filterCondition = data["filtercondition" + i];
                    var filterDataField = data["filterdatafield" + i];
                    var filterOperator = data["filteroperator" + i];
                    if(getFieldType(filterDataField)=='number'){
                        filterListFields += "|OLBIUS|" + filterDataField + "(BigDecimal)";
                    }else if(getFieldType(filterDataField)=='date'){
                        filterListFields += "|OLBIUS|" + filterDataField + "(Date)";
                    }else if(getFieldType(filterDataField)=='Timestamp'){
                        filterListFields += "|OLBIUS|" + filterDataField + "(Timestamp)[dd/MM/yyyy hh:mm:ss aa]";
                    }
                    else{
                        filterListFields += "|OLBIUS|" + filterDataField;
                    }
                    if(getFieldType(filterDataField)=='Timestamp'){
                        if(tmpFieldName != filterDataField){
                            filterListFields += "|SUIBLO|" + filterValue + " 00:00:00 am";
                        }else{
                            filterListFields += "|SUIBLO|" + filterValue + " 11:59:59 pm";
                        }
                    }else{
                        filterListFields += "|SUIBLO|" + filterValue;
                    }
                    filterListFields += "|SUIBLO|" + filterCondition;
                    filterListFields += "|SUIBLO|" + filterOperator;
                    tmpFieldName = filterDataField;
                }
                data.filterListFields = filterListFields;
            }
            data.$skip = data.pagenum * data.pagesize;
            data.$top = data.pagesize;
            data.$inlinecount = "allpages";
            return data;
		 }
	});
    
    $("#supplierListGrid").jqxGrid({
        width: 850,
        source: supplierDataAdapter,
        columnsresize: true,
        filterable: true,
		showfilterrow: true,
		virtualmode: true,
		selectionmode: 'checkbox',
		rendergridrows: function(obj){
			return obj.data;
		},
		sortable:true,
		theme: theme,
		editable: false,
		autoheight:true,
		pageable: true,
		localization: getLocalization(),
        columns: [
            { text: BPOSSupplierId, datafield: 'partyId', width: 120 },
            { text: BPOSSupplierName, datafield: 'groupName', width: 250 },
            { text: BPOSMobile, datafield: 'companyPhone', width: 150 },
            { text: BPOSEmail, datafield: 'companyEmail', width: 150 },
            { text: BPOSAddress, datafield: 'companyAddress'}
        ]
    });
};

function searchSupplier(supplierId){
	var tmpS = $("#jqxgrid").jqxGrid('source');
	tmpS._source.url = "jqxGeneralServicer?sname=jqGetProductAndProductFacility&hasrequest=Y&supplierId=" + supplierId;
	$("#jqxgrid").jqxGrid('source', tmpS);
}