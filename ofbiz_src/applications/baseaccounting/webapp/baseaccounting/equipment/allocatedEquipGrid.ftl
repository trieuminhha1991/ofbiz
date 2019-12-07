<#assign dataField="[
					{ name: 'equipmentId', type: 'string' },
					{ name: 'equipmentTypeId', type: 'string'},
					{ name: 'equimentName', type: 'string'},
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
			   					var filterBoxAdapter2 = new $.jqx.dataAdapter(equipmentTypeData,
						                {
						                    autoBind: true
						                });
				                uniqueRecords2 = filterBoxAdapter2.records;
			   				}
			   				widget.jqxDropDownList({ filterable:true,source: uniqueRecords2, displayMember: 'description', valueMember : 'statusId'});			   				
						}
					 },
					 { text: '${uiLabelMap.BACCEquimentName}', dataField: 'equimentName', width: 200, pinned: true},
					 { text: '${uiLabelMap.BACCDateAcquired}', dataField: 'dateAcquired', width: 150, filtertype: 'range', cellsformat:'dd/MM/yyyy' },
					 { text: '${uiLabelMap.BACCQuantity}', dataField: 'quantity', width: 150},
					 { text: '${uiLabelMap.BACCUnitPrice}', dataField: 'unitPrice', width: 150,
						 cellsrenderer: function(row, columns, value){
	                		  var data = $('#equipGrid').jqxGrid('getrowdata',row);
	                		  return '<span>'+formatcurrency(value, data.uomId)+'</span>';
	                	  }
					 }
					 "/>

<@jqGrid id="equipGrid" customcontrol1="icon-plus open-sans@${uiLabelMap.accAddNewRow}@javascript: void(0);@OLBSelEquip.openWindow()" filtersimplemode="true" editable="false" addType="popup" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JqxGetListAllowEquips&customTimePeriodId=${parameters.customTimePeriodId}" dataField=dataField columnlist=columnlist 
	 />