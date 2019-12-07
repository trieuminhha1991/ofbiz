<script>
var facilityData = new Array();
<#assign facilityList = delegator.findList("Facility", null, null, null, null, false) /> 
<#if facilityList?exists>
	<#list facilityList as facility >
		var row = {};
		row['facilityId'] = '${facility.facilityId?if_exists}';
		row['description'] = "${facility.facilityName?if_exists}";
		facilityData[${facility_index}] = row;
	</#list>
</#if>

var orderStatusData = new Array();
<#assign orderStatusList = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "ORDER_STATUS"), null, null, null, false) /> 
<#if orderStatusList?exists>
	<#list orderStatusList as status >
		<#assign description =  status.get("description" locale) />
		var row = {};
		row['statusId'] = '${status.statusId?if_exists}';
		row['description'] = "${description?if_exists}";
		orderStatusData[${status_index}] = row;
	</#list>
</#if>
</script>
<#assign dataField = "[	 { name: 'orderId', type: 'string' },
						 { name: 'orderDate', type: 'date', other:'Timestamp'},
						 { name: 'statusId', type: 'string' },
						 { name: 'grandTotal', type: 'number'},
						 { name: 'originFacilityId', type: 'string'},
						 { name: 'available', type: 'bool' },
						 { name: 'createdBy', type: 'string' },
						 { name: 'fullName', type: 'string' },
						 { name: 'currencyUom', type: 'string' },
						 { name: 'planPOId', type: 'string' },
					]"/>
<#assign columnlist = "
					{ text: '${uiLabelMap.BPOSOrderId}', datafield: 'orderId',editable:false, width: 100,
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							if (data && data.orderId){
        						return '<div><a style = \"margin-left: 10px\" href=' + 'viewDetailPO?orderId=' + data.orderId + '>' +  data.orderId + '</a>' + '</div>'
    						}
    					}
					},
					{ text: '${uiLabelMap.BPOSOrderDate}', datafield: 'orderDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range'},
					{ text: '${uiLabelMap.BPOSOrderStatus}', datafield: 'statusId', filtertype: 'checkedlist',
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							for(i = 0; i < orderStatusData.length; i++){
					 			if(data.statusId == orderStatusData[i].statusId){
					 				return '<span style=\"margin-left: 10px;\">' + orderStatusData[i].description + '</span>';
					 			}
					 		}
    					},
    					createfilterwidget: function (column, columnElement, widget) {
					  		var sourceOs =
						    {
						        localdata: orderStatusData,
						        datatype: \"array\"
						    };
			   				var filterBoxAdapterOs = new $.jqx.dataAdapter(sourceOs,
			                {
			                    autoBind: true
			                });
			                var uniqueRecordsOs = filterBoxAdapterOs.records;
        					widget.jqxDropDownList({selectedIndex: 0,  source: uniqueRecordsOs, displayMember: 'description', valueMember: 'statusId', autoDropDownHeight:false, dropDownHeight:200,
        						renderer: function (index, label, value) {
                    				for(i = 0; i < orderStatusData.length; i++){
										if(orderStatusData[i].statusId==value){
											return '<span>' + orderStatusData[i].description + '</span>'
										}
                    				}
                    			return value;
        					}});
    					}
					},
					{ text: '${uiLabelMap.BPOSGrandTotal}', datafield: 'grandTotal', cellsalign: 'right', filtertype: 'number',
						cellsrenderer: function (row, column, value) {
	     					var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	     					if (data && data.grandTotal){
	     						return '<div style=\"text-align: right;\">' + formatcurrency(data.grandTotal, data.currencyUom) + '</div>';
	     					} else {
	     						return '<div style=\"text-align: right;\">' + formatcurrency(0, data.currencyUom) + '</div>';
	     					}
     					}
					},
					{ text: '${uiLabelMap.BPOSOrderCreatedBy}', datafield: 'fullName', cellsalign: 'left', width: '250px'},
					{ text: '${uiLabelMap.BPOSFacilityName}', datafield: 'originFacilityId', width: '150px',filtertype: 'checkedlist',
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							for(i = 0; i < facilityData.length; i++){
					 			if(data.originFacilityId == facilityData[i].facilityId){
					 				return '<span style=\"margin-left: 10px;\">' + facilityData[i].description + '</span>';
					 			}
					 		}
    					},
    					createfilterwidget: function (column, columnElement, widget) {
					  		var sourceFa =
						    {
						        localdata: facilityData,
						        datatype: \"array\"
						    };
			   				var filterBoxAdapterFa = new $.jqx.dataAdapter(sourceFa,
			                {
			                    autoBind: true
			                });
			                var uniqueRecordsFa = filterBoxAdapterFa.records;
        					widget.jqxDropDownList({selectedIndex: 0,  source: uniqueRecordsFa, displayMember: 'description', valueMember: 'facilityId', autoDropDownHeight:false, dropDownHeight:200,
        						renderer: function (index, label, value) {
                    				for(i = 0; i < facilityData.length; i++){
										if(facilityData[i].facilityId==value){
											return '<span>' + facilityData[i].description + '</span>'
										}
                    				}
                    			return value;
        					}});
    					}
					},
					{ text: '${uiLabelMap.BPOSPlanPOId}', datafield: 'planPOId', editable:false, width: 170,
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							if (data && data.planPOId){
        						return '<div><a style = \"margin-left: 10px\" href=' + 'viewPlanPO?orderId=' + data.orderId + '>' +  data.planPOId + '</a>' + '</div>'
    						}
    					}
					}
				"/>	

<@jqGrid filtersimplemode="true" dataField=dataField filterable="true" columnlist=columnlist clearfilteringbutton="true" showtoolbar="true"
		 editable="false" bindresize="true" selectionmode= "singlecell" viewSize="15"
		 url="jqxGeneralServicer?sname=JQPurchaseOrderHistory"
/>