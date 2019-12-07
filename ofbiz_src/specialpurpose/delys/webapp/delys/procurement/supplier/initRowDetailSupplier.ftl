<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord){
	var partyId = datarecord.partyId;
	if(!partyId){
		var partyId = datarecord.partyIdFrom;
	}
	var urlStr = 'jqxGeneralServicer?sname=JqxGetCommunication';
	var id = datarecord.uid.toString();
	var grid = $($(parentElement).children()[0]);
    $(grid).attr('id', 'jqxgridDetail_' + id);
  		var emplPosTypeSalarySource = {datafields: [
            {name: 'communicationEventId', type: 'string'},
            {name: 'entryDate', type: 'date', other:'Timestamp'},
            {name: 'communicationEventTypeId', type: 'string'},
            {name: 'partyIdFrom', type: 'string'},        
            {name: 'firstNameFrom', type: 'string'},
            {name: 'middleNameFrom', type: 'string'},
            {name: 'lastNameFrom', type: 'string'},
            {name: 'partyIdTo', type: 'string'},      
            {name: 'middleNameTo', type: 'string'},      
            {name: 'lastNameTo', type: 'string'},      
            {name: 'firstNameTo', type: 'string'},     
            {name: 'groupNameTo', type: 'string'},
            {name: 'groupNameFrom  ', type: 'string'},
            {name: 'content', type: 'string'}      		            
		],
		cache: false,
		datatype: 'json',
		type: 'POST',
		data: {
			partyId: partyId,
			noConditionFind: 'N',
	        conditionsFind: 'N',
	        //dictionaryColumns: '',
	        //otherCondition: ''
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
           	var content = rowdata.content;
           	var columnValues = partyId +  '#;' +rowdata.support + '#;' + rowdata.type +  '#;' + content;
           	var data = {
  				columnList: 'partyId;support;type;content',
  				columnValues: columnValues
           	};
            addRow(grid, 'jqxGeneralServicer?sname=raiseCustomerIssue&jqaction=C', data, commit,\"${id}\");
        },
        contentType: 'application/x-www-form-urlencoded',
        url: urlStr
     };
    var nestedGridAdapter = new $.jqx.dataAdapter(emplPosTypeSalarySource, {
    	autoBind: true,
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
			editable: false,
			editmode:'selectedrow',
			filterable: true,
			virtualmode: true, 
			pageable: true,
			showfilterrow: true,
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
                $('#addrowbutton'+id).jqxButton();
                // create new row.
                $('#addrowbutton'+id).on('click', function () {
                	currentGrid = grid;
                    $('#issueFormWindow').jqxWindow('open');
                });
            },
			columns: [{text: '${uiLabelMap.sender}', datafield: 'firstNameFrom', cellsalign: 'left', width: '200', 
						cellsrenderer: function (row, column, value) {
							var data = grid.jqxGrid('getrowdata', row);
							var first = data.firstNameFrom ? data.firstNameFrom : '';
							var middle = data.middleNameFrom ?  data.middleNameFrom : '';
							var last = data.lastNameFrom ? data.lastNameFrom : '';
				    		return \"<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>\"+ first + ' ' + middle + ' ' + last +\"</div>\";
                      	}
					},
				 	{text: '${uiLabelMap.receiver}',datafield: 'firstNameTo', cellsalign: 'left', width: '200', 
				 		cellsrenderer: function (row, column, value) {
							var data = grid.jqxGrid('getrowdata', row);
							var first = data.firstNameTo ? data.firstNameTo : '';
							var middle = data.middleNameTo ?  data.middleNameTo : '';
							var last = data.lastNameTo ? data.lastNameTo : '';
				    		return \"<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>\"+ first + ' ' + middle + ' ' + last+\"</div>\";
                      	}
			 		},
				 	{text: '${uiLabelMap.DAContent}',datafield: 'content', cellsalign: 'left', width: '200'},
				 	{text: '${uiLabelMap.dateSend}',datafield: 'entryDate', cellsalign: 'left', width: '110', cellsformat: 'dd/MM/yyyy', filtertype: 'range', },
				 	{text: '${uiLabelMap.CommunicationType}',datafield: 'communicationEventTypeId', cellsalign: 'left', width: '25%',filtertype: 'checkedlist',
				 		cellsrenderer: function(row, column, value){
				 			for(var x in commType){
				 				if(commType[x].communicationEventTypeId == value){
				 					return \"<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>\"+commType[x].description+\"</div>\";
				 				}
				 			}
				 		},
				 		createfilterwidget: function (column, columnElement, widget) {
				 			var filterBoxAdapter = new $.jqx.dataAdapter(commType, {autoBind: true});
							var dataSoureList = filterBoxAdapter.records;
						    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
						    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', autoDropDownHeight: false,valueMember : 'communicationEventTypeId'});
   						}
				 	},
				 	]
   		});
    }
}">		