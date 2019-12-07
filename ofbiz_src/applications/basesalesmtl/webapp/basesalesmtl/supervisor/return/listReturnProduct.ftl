<#assign columnlist="{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.OrderReturnId}', pinned: true, dataField: 'returnId', width: 120, 
						cellsrenderer: function(row, column, value){
							 return '<span><a href=\"CustomerReturnDetailForSup?returnId=' + value + '\"> ' + value  + '</a></span>'
						}
					},
					{ text: '${uiLabelMap.Status}', dataField: 'statusId', width: 150, filtertype: 'checkedlist',
						cellsrenderer: function(row, column, value){
							for (var i = 0; i < cusStatusData.length; i ++){
								if (value && value == cusStatusData[i].statusId){
									return '<span>' + cusStatusData[i].description + '<span>';
								}
							}
							return '<span>' + value + '<span>';
						},
						createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(cusStatusData);
							var records = filterDataAdapter.records;
							widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
								renderer: function(index, label, value){
						        	if (cusStatusData.length > 0) {
										for(var i = 0; i < cusStatusData.length; i++){
											if(cusStatusData[i].statusId == value){
												return '<span>' + cusStatusData[i].description + '</span>';
											}
										}
									}
									return value;
								}
							});
			   			}
					},
					{ text: '${uiLabelMap.ReturnFrom}', dataField: 'fromPartyId', minwidth: 200,
						cellsrenderer: function(row, column, value){
							var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
							if (data.fromPartyFullName && '  ' != data.fromPartyFullName){
								return '<span>' + data.fromPartyFullName + ' [ '+ value +' ]' +'<span>';
							} else {
								if (data.fromGroupName){
									return '<span>' + data.fromGroupName + ' [ '+ value +' ]' +'<span>';
								}
							}
						}
					},
					{ text: '${uiLabelMap.ReturnTo}', dataField: 'toPartyId', minwidth: 200,
						cellsrenderer: function(row, column, value){
							var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
							if (data.toPartyFullName && '  ' != data.toPartyFullName){
								return '<span>' + data.toPartyFullName + ' [ '+ value +' ]' + '<span>';
							} else {
								if (data.toGroupName){
									return '<span>' + data.toGroupName + ' [ '+ value +' ]' + '<span>';
								}
							}
						}
					},
					{ text: '${uiLabelMap.ReceiveToFacility}', dataField: 'destinationFacilityId', width: 150,
						cellsrenderer: function(row, column, value){
							var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
							if (value){
								return '<span>' + data.facilityName + '<span>';
							} else {
								return '<span><span>';
							}
						}
					},
					{ text: '${uiLabelMap.ReturnType}', dataField: 'returnHeaderTypeId', width: 150, 
						cellsrenderer: function(row, column, value){
							var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
							return '<span>' + data.returnTypeDesc + '<span>';
						}
					},
					{ text: '${uiLabelMap.OrderEntryDate}', width: 150, dataField: 'entryDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', cellsalign: 'right'}"/>

<#assign dataField="[{ name: 'returnId', type: 'string' },
					{ name: 'returnHeaderTypeId', type: 'string'},
					{ name: 'statusId', type: 'string'},
					{ name: 'createdBy', type: 'string'},
					{ name: 'fromPartyId', type: 'string' },
					{ name: 'toPartyId', type: 'string'},
					{ name: 'paymentMethodId', type: 'string'},
					{ name: 'findAccountId', type: 'string'},
					{ name: 'entryDate', type: 'date', other: 'Timestamp' },
					{ name: 'originContactMechId', type: 'string'},
				 	{ name: 'destinationFacilityId', type: 'string' },
					{ name: 'needsInventoryReceive', type: 'string'},
					{ name: 'currencyUomId', type: 'string'},
					{ name: 'toPartyFullName', type: 'string'},
					{ name: 'toGroupName', type: 'string'},
					{ name: 'fromPartyFullName', type: 'string'},
					{ name: 'fromGroupName', type: 'string'},
					{ name: 'facilityName', type: 'string'},
					{ name: 'returnTypeDesc', type: 'string'},
					{ name: 'statusDesc', type: 'string'}]"/>
	
<@jqGrid filtersimplemode="true" id="jqxgridProductReturn" usecurrencyfunction="true" dataField=dataField columnlist=columnlist
	clearfilteringbutton="true" filterable="true" editable="false" url="jqxGeneralServicer?sname=JQGetListProductReturnForSales"/>

<script type="text/javascript">
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))/>
	<#assign returnHeaderTypeId = parameters.returnHeaderTypeId?if_exists/>
	var returnHeaderTypeId = '${returnHeaderTypeId?if_exists}';
	
	<#assign cusReturnStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "ORDER_RETURN_STTS"), null, null, null, false) />
	var cusStatusData = new Array();
	<#list cusReturnStatus as item>
		var row = {};
		row['statusId'] = "${item.statusId}";
	row['description'] = "${StringUtil.wrapString(item.get('description', locale))}";
		cusStatusData.push(row);
	</#list>
	
	<#assign supReturnStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "PORDER_RETURN_STTS"), null, null, null, false) />
	var supStatusData = new Array();
	<#list supReturnStatus as item>
		var row = {};
		row['statusId'] = "${item.statusId}";
	row['description'] = "${StringUtil.wrapString(item.get('description', locale))}";
		supStatusData.push(row);
	</#list>
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
</script>
