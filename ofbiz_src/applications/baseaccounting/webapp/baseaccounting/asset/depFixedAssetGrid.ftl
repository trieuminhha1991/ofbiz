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
							if(data !== (undefined || null))
							{
								return '<span><a href=ViewFixedAsset?fixedAssetId=' + data.fixedAssetId + '>' + data.fixedAssetId + '</a></span>';
							}
							return '<span></span>';
						},
		             },
					 { text: '${uiLabelMap.BACCFixedAssetTypeId}', dataField: 'fixedAssetTypeId', width: 200, pinned: true,  filtertype: 'checkedlist',
						cellsrenderer: function(row, column, value){
							for(var i = 0; i < fixedAssetTypeData.length; i++){
								if(value == fixedAssetTypeData[i].fixedAssetTypeId){
									return '<span title=' + value + '>' + fixedAssetTypeData[i].description + '</span>';
								}
							}
							return '<span>' + value + '</span>';
						},
						createfilterwidget: function (column, columnElement, widget) {
			   				var uniqueRecords2 = [] ;
			   				if(fixedAssetTypeData && fixedAssetTypeData.length > 0 ){
			   					var filterBoxAdapter2 = new $.jqx.dataAdapter(fixedAssetTypeData,
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
	                		  if(data !== (undefined || null))
	                		  {
	                			  return '<span>'+formatcurrency(value, data.uomId)+'</span>';
	                		  }
	                			return '<span></span>';
	                	  }
					 }
					 "/>
<#assign customTime = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : parameters.customTimePeriodId?if_exists}, true)>
<#if customTime.isClosed == 'Y'>
	<@jqGrid id="jqxgridAsset" filtersimplemode="true" editable="false" addType="popup" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JqxGetDepListAssets&customTimePeriodId=${parameters.customTimePeriodId}" dataField=dataField columnlist=columnlist
		 />
<#else>
	<@jqGrid id="jqxgridAsset" filtersimplemode="true" editable="false" addType="popup" customcontrol1="icon-plus open-sans@${uiLabelMap.accAddNewRow}@javascript: void(0);@OLBNewDepFA.openWindow()" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JqxGetDepListAssets&customTimePeriodId=${parameters.customTimePeriodId}" dataField=dataField columnlist=columnlist
		 />
</#if>
                     
<#--=================================/Init Grid======================================================-->