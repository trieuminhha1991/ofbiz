<#include "script/fixedAssetListData.ftl">

<#assign dataField="[
						{ name: 'fixedAssetId', type: 'string' },
						{ name: 'fixedAssetTypeId', type: 'string'},
						{ name: 'fixedAssetName', type: 'string'},
						{ name: 'fullName', type: 'string'},
						{ name: 'quantity', type: 'number', other: 'Long'},
						{ name: 'dateAcquired', type: 'date', other:'Timestamp'},
						{ name: 'dateOfIncrease', type: 'date', other:'Timestamp'},
						{ name: 'expectedEndOfLife', type: 'date', other:'Timestamp'},
						{ name: 'purchaseCost', type: 'number'},
						{ name: 'salvageValue', type: 'number'},
						{ name: 'depreciation', type: 'number'},
						{ name: 'statusId', type: 'string'},
						{ name: 'uomId', type: 'string'},
					 ]"/>

<#assign columnlist="{text: '${uiLabelMap.BACCFixedAssetIdShort}', dataField: 'fixedAssetId', width: '10%', pinned: true,
						cellsrenderer: function(row, column, value){
							var data = $('#jqxgridAsset').jqxGrid('getrowdata', row);
							return '<span><a href=ViewFixedAsset?fixedAssetId=' + data.fixedAssetId + '>' + data.fixedAssetId + '</a></span>';
						},
		             },
		             {text: '${uiLabelMap.BACCFixedAssetName}', dataField: 'fixedAssetName', width: '19%', pinned: true},
					 {text: '${uiLabelMap.BACCFixedAssetTypeId}', dataField: 'fixedAssetTypeId', width: '15%', filtertype: 'checkedlist',
						cellsrenderer: function(row, column, value){
							for(var i = 0; i < globalVar.fixedAssetTypeData.length; i++){
								if(value == globalVar.fixedAssetTypeData[i].fixedAssetTypeId){
									return '<span title=' + value + '>' + globalVar.fixedAssetTypeData[i].description + '</span>';
								}
							}
							return '<span>' + value + '</span>';
						},
						createfilterwidget: function (column, columnElement, widget) {
							accutils.createJqxDropDownList(widget, globalVar.fixedAssetTypeData, {valueMember: 'fixedAssetTypeId', displayMember: 'description', filterable:true});
						}
					 },
					 {text: '${StringUtil.wrapString(uiLabelMap.OrganizationUsed)}', datafield: 'fullName', width: '18%'},
					 {text: '${StringUtil.wrapString(uiLabelMap.BACCQuantity)}', datafield: 'quantity', width: '11%', columntype: 'numberinput', filtertype: 'number',
					 	cellsrenderer: function(row, columns, value){
	                		  var data = $('#jqxgridAsset').jqxGrid('getrowdata',row);
	                		  return '<span class=\"align-right\">'+ value +'</span>';
	                	  }
					 },
					 {text: '${uiLabelMap.BACCDateAcquired}', dataField: 'dateAcquired', width: '14%', filtertype: 'range', cellsformat:'dd/MM/yyyy' },
					 {text: '${uiLabelMap.DateOfIncrease}', dataField: 'dateOfIncrease', width: '14%', filtertype: 'range', cellsformat:'dd/MM/yyyy' },
					 {text: '${uiLabelMap.BACCPurchaseCost}', dataField: 'purchaseCost', width: '14%', columntype: 'numberinput', filtertype: 'number',
						 cellsrenderer: function(row, columns, value){
	                		  var data = $('#jqxgridAsset').jqxGrid('getrowdata',row);
	                		  return '<span class=\"align-right\">'+formatcurrency(value, data.uomId)+'</span>';
	                	  }
					 },
					 {text: '${uiLabelMap.CommonStatus}', dataField: 'statusId', width: '14%', filtertype: 'checkedlist', columntype: 'dropdownlist',
				    	  cellsrenderer: function(row, colum, value){
				      		   var data = $('#jqxgridAsset').jqxGrid('getrowdata',row);
				      		   for(var i = 0 ; i < globalVar.statusData.length ; i++ ){
				  					if(globalVar.statusData[i].statusId == data.statusId){
				  						return '<span>' + globalVar.statusData[i].description  + '</span>';
			  						}
			 					}
								return '<span>' + data.statusId ? data.statusId : ''  + '</span>';
				          },
				          createfilterwidget: function (column, columnElement, widget) {
							accutils.createJqxDropDownList(widget, globalVar.statusData, {valueMember: 'statusId', displayMember: 'description', filterable:true});
						} 
				      }
					 "/>

<@jqGrid id="jqxgridAsset" filtersimplemode="true" editable="false" addType="popup" addrow="true" alternativeAddPopup="addNewFixedAssetWindow" 
		 showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JqxGetListAssets" dataField=dataField columnlist=columnlist jqGridMinimumLibEnable="false"
		 />
		 
<#include "fixedAssetNew.ftl"/>		 
