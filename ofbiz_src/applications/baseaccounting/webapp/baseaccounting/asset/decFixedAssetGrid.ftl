<#--=================================Init Grid======================================================-->
<#assign dataField="[
						{ name: 'fixedAssetId', type: 'string' },
						{ name: 'fixedAssetTypeId', type: 'string'},
						{ name: 'fixedAssetName', type: 'string'},
						{ name: 'dateAcquired', type: 'date', other:'Timestamp'},
						{ name: 'expectedEndOfLife', type: 'date', other:'Timestamp'},
						{ name: 'purchaseCost', type: 'number'},
						{ name: 'salvageValue', type: 'number'},
						{ name: 'depreciation', type: 'number'}
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.BACCFixedAssetId}', dataField: 'fixedAssetId', width: 180, pinned: true,
						cellsrenderer: function(row, column, value){
							var data = $('#jqxgridAsset').jqxGrid('getrowdata', row);
							return '<span><a href=ViewFixedAsset?fixedAssetId=' + data.fixedAssetId + '>' + data.fixedAssetId + '</a></span>';
						}
		             },
					 { text: '${uiLabelMap.BACCFixedAssetTypeId}', dataField: 'fixedAssetTypeId', width: 200, pinned: true,  filtertype: 'checkedlist',
						cellsrenderer: function(row, column, value){
							for(var i = 0; i < globalVar.fixedAssetTypeData.length; i++){
								if(value == globalVar.fixedAssetTypeData[i].fixedAssetTypeId){
									return '<span title=' + value + '>' + globalVar.fixedAssetTypeData[i].description + '</span>';
								}
							}
							return '<span>' + value + '</span>';
						},
						createfilterwidget: function (column, columnElement, widget) {
			   				var uniqueRecords2 = [] ;
			   				if(globalVar.fixedAssetTypeData && globalVar.fixedAssetTypeData.length > 0 ){
			   					var filterBoxAdapter2 = new $.jqx.dataAdapter(globalVar.fixedAssetTypeData,
						                {
						                    autoBind: true
						                });
				                uniqueRecords2 = filterBoxAdapter2.records;
			   				}
			   				widget.jqxDropDownList({ filterable:true,source: uniqueRecords2, displayMember: 'description', valueMember : 'statusId'});			   				
						}
					 },
					 { text: '${uiLabelMap.BACCFixedAssetName}', dataField: 'fixedAssetName', pinned: true},
					 { text: '${uiLabelMap.BACCDateAcquired}', dataField: 'dateAcquired', width: 150, filtertype: 'range', cellsformat:'dd/MM/yyyy' },
					 { text: '${uiLabelMap.BACCExpectedEndOfLife}', dataField: 'expectedEndOfLife', width: 180, filtertype: 'range', cellsformat:'dd/MM/yyyy' },
					 { text: '${uiLabelMap.BACCPurchaseCost}', dataField: 'purchaseCost', width: 150,
						 cellsrenderer: function(row, columns, value){
	                		  var data = $('#jqxgridAsset').jqxGrid('getrowdata',row);
	                		  return '<span>'+formatcurrency(value, data.uomId)+'</span>';
	                	  }
					 }
					 "/>

<@jqGrid id="jqxgridAsset" filtersimplemode="true" editable="false" addType="popup" customcontrol1="icon-plus open-sans@${uiLabelMap.accAddNewRow}@javascript: void(0);@OLBNewDecFA.openWindow()" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JqxGetListDecFixedAssets&decrementId=${parameters.decrementId?if_exists}" dataField=dataField columnlist=columnlist
		 />
                     
<#--=================================/Init Grid======================================================-->