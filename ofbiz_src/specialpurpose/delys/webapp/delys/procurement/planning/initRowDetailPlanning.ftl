<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord){
	var year = datarecord.year;
	var partyId = datarecord.partyId;
	var urlStr = 'jqxGeneralServicer?sname=JqxGetPOPlanDetail';
	var id = datarecord.uid.toString();
	var grid = $($(parentElement).children()[0]);
	currentGrid = grid;
    $(grid).attr('id', 'jqxgridDetail_' + id);
  		var emplPosTypeSalarySource = {datafields: [
            {name: 'poPlanSeqId', type: 'string'}, 
            {name: 'productId', type: 'string'}, 
            {name: 'productName', type: 'string'},      
            {name: 'quantity', type: 'string'},      
            {name: 'quantityUomId', type: 'string'},      
            {name: 'unitCost', type: 'string'},     
            {name: 'currencyUomId', type: 'string'},
            {name: 'reason', type: 'string'}      		            
		],
		cache: false,
		datatype: 'json',
		type: 'POST',
		data: {
			partyId: partyId,
			year: year,
			noConditionFind: 'N',
	        conditionsFind: 'N'
		},
		pagenum: 0,
        pagesize: 10,
        filter: function () {
            // update the grid and send a request to the server.
            grid.jqxGrid('updatebounddata');
        },
        beforeprocessing: function (data) {
		    emplPosTypeSalarySource.totalrecords = data.TotalRows;
		},
        addrow: function (rowid, rowdata, position, commit) {
           	var data = {
  				columnList: 'partyId;year;productId;quantity(java.math.BigDecimal);quantityUomId;unitCost(java.math.BigDecimal);currencyUomId;reason', 
  				columnValues: partyId + '#;' + year + '#;' + rowdata.productId + '#;' + rowdata.quantity + '#;' + rowdata.quantityUomId + '#;' + rowdata.unitCost + '#;' + rowdata.currencyUomId + '#;' + rowdata.reason 
           	};
            addRow(grid, 'jqxGeneralServicer?sname=createPlanItemPO&jqaction=C', data, commit, 'jqxgridsupplier');
        },
        deleterow: function (rowid, commit) {
           	var dataRecord = grid.jqxGrid('getrowdata', rowid);
           	var data = {columnList: 'partyId;year;poPlanSeqId', columnValues: partyId + '#;' + year + '#;' +  dataRecord.poPlanSeqId};
           	deleteRow(grid, 'jqxGeneralServicer?sname=deletePOPlanItem&jqaction=D', data, commit, 'jqxgridsupplier');
        },
        updaterow: function (rowid, newdata, commit) {
           	var data = {
           		rl: 1,
  				columnList0: 'partyId;year;poPlanSeqId;quantity(java.math.BigDecimal);quantityUomId;unitCost(java.math.BigDecimal);currencyUomId;reason', 
  				columnValues0: partyId + '#;' + year + '#;' + newdata.poPlanSeqId + '#;' + newdata.quantity + '#;' + newdata.quantityUomId + '#;' + newdata.unitCost + '#;' + newdata.currencyUomId + '#;' + newdata.reason 
           	};
            updateRow(grid, 'jqxGeneralServicer?sname=updatePOPlanItem&jqaction=U', data, commit);
        },
        contentType: 'application/x-www-form-urlencoded',
        url: urlStr
     };
    var nestedGridAdapter = new $.jqx.dataAdapter(emplPosTypeSalarySource, {
	   	formatData: function (data) {
	   		if (data.filterscount) {
	               var filterListFields = '';
	               for (var i = 0; i < data.filterscount; i++) {
	                   var filterValue = data['filtervalue' + i];
	                   var filterCondition = data['filtercondition' + i];
	                   var filterDataField = data['filterdatafield' + i];
	                   var filterOperator = data['filteroperator' + i];
	                   filterListFields += '|OLBIUS|' + filterDataField;
	                   filterListFields += '|SUIBLO|' + filterValue;
	                   filterListFields += '|SUIBLO|' + filterCondition;
	                   filterListFields += '|SUIBLO|' + filterOperator;
	               }
	               data.filterListFields = filterListFields;
	           }
	           return data;
	       },
       loadError: function (xhr, status, error) {
           alert(error);
       },
       downloadComplete: function (data, status, xhr) {
               if (!emplPosTypeSalarySource.totalRecords) {
                   emplPosTypeSalarySource.totalRecords = parseInt(data['odata.count']);
               }
       },
    });
    if (grid != null) {
    	grid.jqxGrid({
   			source: nestedGridAdapter,
   			width: '95%',
			autoheight: true,
            showtoolbar:true,
			editable: true,
			editmode:'selectedrow',
			filterable: true,
			virtualmode: true, 
			pageable: true,
			showfilterrow: true,
			autorowheight: true,
			showheader: true,
			selectionmode:'singlerow',
			theme: 'energyblue',
			rendergridrows: function(obj) {	
				return obj.data;
			},
			rendertoolbar: function (toolbar) {
                var me = this;
                var container = $('<div style=\"margin: 5px; float: right\"></div>');
                toolbar.append(container);
                container.append('<input id=\"addrowbutton'+id+'\"  type=\"button\" value=\"Add New Row\" />');
                container.append('<input style=\"margin-left: 5px;\" id=\"deleterowbutton'+id+'\" type=\"button\" value=\"Delete Selected Row\" />');
                $('#addrowbutton'+id).jqxButton();
                $('#deleterowbutton'+id).jqxButton();
                // create new row.
                $('#addrowbutton'+id).on('click', function () {
                	currentGrid = grid;
                    $('#jqxwindowproduct').jqxWindow('open');
                });
                // delete row.
                $('#deleterowbutton'+id).on('click', function () {
                    var selectedrowindex = grid.jqxGrid('getselectedrowindex');
                    var rowscount = grid.jqxGrid('getdatainformation').rowscount;
                    if (selectedrowindex >= 0 && selectedrowindex < rowscount) {
                        var id = grid.jqxGrid('getrowid', selectedrowindex);
                        var commit = grid.jqxGrid('deleterow', id);
                    }
                });
            },
			columns: [{ text: '${uiLabelMap.productName}', width:200, datafield: 'productName'},
					  { text: '${uiLabelMap.quantity}', width:100, datafield: 'quantity',align: 'right', 
					  	cellsrenderer: function(row, column, value){
					  		var str = '<div class=\"custom-cell-grid align-right\">'
					  				+ convertLocalNumber(value) + '</div>';
					  		return str;
					  	}
					  },
					  { text: '${uiLabelMap.DAUnit}', width:120, datafield: 'quantityUomId',
					  	cellsrenderer: function(row, column, value){
					  		var str = '<div class=\"custom-cell-grid\">';
					  		for(var x in dataLU){
					  			if(dataLU[x].uomId == value){
					  				str += dataLU[x].description + '</div>';
					  				return str;
					  			}
					  		}
					  		str += value + '</div>';
					  		return str;
					  	}
					  },
					  { text: '${uiLabelMap.OrderAmount}', width:100, datafield: 'unitCost', align: 'right',
				  		cellsrenderer: function(row, column, value){
					  		var str = '<div class=\"custom-cell-grid align-right\">'
					  				+ convertLocalNumber(value) + '</div>';
					  		return str;
					  	}
					  },
					  { text: '${uiLabelMap.DACurrency}', datafield: 'currencyUomId', width: 180, 
					  	cellsrenderer: function(row, column, value){
					  		var str = '<div class=\"custom-cell-grid\">';
					  		for(var x in dataLC){
					  			if(dataLC[x].uomId == value){
					  				str += dataLC[x].description + '</div>';
					  				return str;
					  			}
					  		}
					  		str += value + '</div>';
					  		return str;
					  	}
					  },
					  { text: '${uiLabelMap.Reason}', datafield: 'reason'}
				 	]
   		});
    }
}">		