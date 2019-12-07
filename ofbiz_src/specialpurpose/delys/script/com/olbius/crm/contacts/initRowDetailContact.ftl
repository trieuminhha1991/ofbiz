<script>
	var currentGrid;
	var datf = [{name: 'communicationEventId', type: 'string'},
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
            {name: 'content', type: 'string'}];
	var cll = [{text: '${uiLabelMap.sender}', datafield: 'firstNameFrom', cellsalign: 'left', width: '200', 
						cellsrenderer: function (row, column, value, a, b, data) {
							var first = data.firstNameFrom ? data.firstNameFrom : '';
							var middle = data.middleNameFrom ?  data.middleNameFrom : '';
							var last = data.lastNameFrom ? data.lastNameFrom : '';
				    		return "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>"+ first + ' ' + middle + ' ' + last +"</div>";
                      	}
					},
				 	{text: '${uiLabelMap.receiver}',datafield: 'firstNameTo', cellsalign: 'left', width: '200', 
				 		cellsrenderer: function (row, column, value, a, b, data) {
							var first = data.firstNameTo ? data.firstNameTo : '';
							var middle = data.middleNameTo ?  data.middleNameTo : '';
							var last = data.lastNameTo ? data.lastNameTo : '';
				    		return "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>"+ first + ' ' + middle + ' ' + last+"</div>";
                      	}
			 		},
				 	{text: '${uiLabelMap.DAContent}',datafield: 'content', cellsalign: 'left'},
				 	{text: '${uiLabelMap.dateSend}', datafield: 'entryDate', cellsalign: 'left', width: '150', cellsformat: 'dd/MM/yyyy', filtertype: 'range'},
				 	{text: '${uiLabelMap.CommunicationType}',datafield: 'communicationEventTypeId', cellsalign: 'left', width: '150',filtertype: 'checkedlist',
				 		cellsrenderer: function(row, column, value){
				 			for(var x in commType){
				 				if(commType[x].communicationEventTypeId == value){
				 					return "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>"+commType[x].description+"</div>";
				 				}
				 			}
				 		},
				 		createfilterwidget: function (column, columnElement, widget) {
				 			var filterBoxAdapter = new $.jqx.dataAdapter(commType, {autoBind: true});
							var dataSoureList = filterBoxAdapter.records;
						    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
						    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', autoDropDownHeight: false,valueMember : 'communicationEventTypeId'});
   						}
				 	}];
</script>
<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord){
	var partyId = datarecord.partyId;
	if(!partyId){
		var partyId = datarecord.partyIdFrom;
	}
	var canEditEntry = false;
	var urlStr = 'JqxGetCommunication&partyId=' + partyId;
	var id = 'rowdetail' + datarecord.uid.toString();
	var grid = $($(parentElement).children()[0]);
	var str = '<div id=\"container'+id+'\" style=\"background-color: transparent; overflow: auto;\"></div>'
			+ '<div id=\"jqxNotification'+id+'\"><div id=\"notificationContent'+id+'\"></div></div>';
	$(parentElement).prepend(str);
	grid.attr('id', id);	
	currentGrid = grid;
	GridUtils.initGrid({
			url: urlStr, 
			width: '95%', 
			autorowheight: true,
			filterable: true, 
			showtoolbar:true,
			virtualmode: true,
			editable: canEditEntry,
			height: 250,
			scrollmode: 'logical',
			rendertoolbar : function (toolbar) {
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
            source :{
            	pagesize: 10,
            	editable: canEditEntry,
            	addColumns: 'partyId['+partyId+'];support;type;content',
            	createUrl : 'raiseCustomerIssue'
            }
		}, datf, cll, null, grid);
}">		