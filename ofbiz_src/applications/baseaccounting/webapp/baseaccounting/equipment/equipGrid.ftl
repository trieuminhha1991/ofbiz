<#assign dataField="[
					{ name: 'equipmentId', type: 'string' },
					{ name: 'equipmentTypeId', type: 'string'},
					{ name: 'equipmentName', type: 'string'},
					{ name: 'dateAcquired', type: 'date', other:'Timestamp'},
					{ name: 'uomId', type: 'date', other:'Timestamp'},
					{ name: 'quantity', type: 'number'},
					{ name: 'unitPrice', type: 'number'},
					{ name: 'statusId', type: 'string'}
				 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.BACCEquipmentId}', dataField: 'equipmentId', width: 100, pinned: true,
						cellsrenderer: function(row, column, value){
						},
		             },
					 { text: '${uiLabelMap.BACCEquipmentTypeId}', dataField: 'equipmentTypeId', pinned: true,  filtertype: 'checkedlist',
						cellsrenderer: function(row, column, value){
							for(var i = 0; i < equipmentTypeData.length; i++){
								if(value == equipmentTypeData[i].equipmentTypeId){
									return '<span title=' + value + '>' + equipmentTypeData[i].description + '</span>';
								}
							}
							return '<span>' + value + '</span>';
						},
						createfilterwidget: function (column, columnElement, widget) {
			   				var uniqueRecords2 = [] ;
			   				if(equipmentTypeData && equipmentTypeData.length > 0 ){
			   					var filterBoxAdapter2 = new $.jqx.dataAdapter(equipmentTypeData,{
				                    autoBind: true
				                });
				                uniqueRecords2 = filterBoxAdapter2.records;
			   				}
			   				widget.jqxDropDownList({ filterable:true,source: uniqueRecords2, displayMember: 'description', valueMember : 'equipmentTypeId'});			   				
						}
					 },
					 { text: '${uiLabelMap.BACCEquimentName}', dataField: 'equipmentName', width: 200, pinned: true},
					 { text: '${uiLabelMap.BACCDateAcquired}', dataField: 'dateAcquired', width: 150, filtertype: 'range', cellsformat:'dd/MM/yyyy' },
					 { text: '${uiLabelMap.BACCQuantity}', dataField: 'quantity', width: 150, filtertype: 'number'},
					 { text: '${uiLabelMap.BACCUnitPrice}', dataField: 'unitPrice', width: 150, filtertype: 'number',
						 cellsrenderer: function(row, columns, value){
	                		  var data = $('#equipGrid').jqxGrid('getrowdata',row);
	                		  return '<span>'+formatcurrency(value, data.uomId)+'</span>';
	                	  }
					 },
					 { text: '${uiLabelMap.CommonStatus}', dataField: 'statusId', width: 150,  filtertype: 'checkedlist',
				    	  cellsrenderer: function(row, colum, value){
				      		   var data = $('#equipGrid').jqxGrid('getrowdata',row);
				      		   for(var i = 0 ; i < statusData.length ; i++ ){
				  					if(statusData[i].statusId == data.statusId){
				  						return '<span>' + statusData[i].description  + '</span>';
				  						}
				 					}
								return '<span>' + data.statusId ? data.statusId : ''  + '</span>';
				          },
				          createfilterwidget: function (column, columnElement, widget) {
			   					var uniqueRecords2 = [] ;
			   					if(statusData && statusData.length > 0 ){
			   						var filterBoxAdapter2 = new $.jqx.dataAdapter(statusData,{
			   							autoBind: true
			   						});
			   						uniqueRecords2 = filterBoxAdapter2.records;
			   					}
			   					widget.jqxDropDownList({ filterable:true,source: uniqueRecords2, displayMember: 'description', valueMember : 'statusId'});			   				
				          }
				      }
					 "/>

<@jqGrid id="equipGrid" customcontrol1="icon-plus open-sans@${uiLabelMap.accAddNewRow}@javascript: window.open('CreateEquipment', '_blank')" 
		  filtersimplemode="true" editable="false" addType="popup" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JqxGetListEquipments" dataField=dataField columnlist=columnlist 
		 jqGridMinimumLibEnable="false"
		 />