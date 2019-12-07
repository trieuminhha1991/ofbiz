<#assign statusList = delegator.findByAnd("StatusItem", {"statusTypeId" : "SETTLE_GRP_STATUS"}, null, false) />
<script type="text/javascript">
	var statusData = new Array();
	<#list statusList as statusItem>
		<#assign description = StringUtil.wrapString(statusItem.get("description", locale)) />
		var row = {};
		row['statusId'] = '${statusItem.statusId}';
		row['description'] = "${description}";
		statusData[${statusItem_index}] = row;
	</#list>
</script>
<#assign dataField="[{ name: 'promoSettleGroupId', type: 'string' },
       		{ name: 'promoSettleRecordId', type: 'string'}, 
       		{ name: 'partyId', type: 'string'},
       		{ name: 'statusId', type: 'string'}
        	]"/>
<#assign columnlist="{ text: '${uiLabelMap.DAPromoSettleRecordId}', dataField: 'promoSettleRecordId', width: '180px',
					 	cellsrenderer: function(row, colum, value) {
                        	var data = $('#jqxgridPOSR').jqxGrid('getrowdata', row);
                        	return \"<span><a href='/delys/control/viewPromoSettleRecord?promoSettleRecordId=\" + data.promoSettleRecordId + \"'>\" + data.promoSettleRecordId + \"</a></span>\";
                        }
					 },
					 { text: '${uiLabelMap.DAPromoSettleGroupId}', dataField: 'promoSettleGroupId', width: '180px',
					 	cellsrenderer: function(row, colum, value) {
                        	var data = $('#jqxgridPOSR').jqxGrid('getrowdata', row);
                        	return \"<span><a href='/delys/control/viewPromoSettleGroupItem?promoSettleGroupId=\" + data.promoSettleGroupId + \"'>\" + data.promoSettleGroupId + \"</a></span>\";
                        }
					 },
					 { text: '${uiLabelMap.DADistributorId}', dataField: 'partyId', width: '200px'}, 
              		 { text: '${uiLabelMap.DAStatus}', dataField: 'statusId', filtertype: 'checkedlist', 
					 	cellsrenderer: function(row, column, value){
					 		var data = $('#jqxgridPOSR').jqxGrid('getrowdata', row);
    						for(var i = 0 ; i < statusData.length; i++){
    							if (value == statusData[i].statusId){
    								return '<span title = ' + statusData[i].description +'>' + statusData[i].description + '</span>';
    							}
    						}
    						return '<span title=' + value +'>' + value + '</span>';
					 	}, 
					 	createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(statusData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
								renderer: function(index, label, value){
									for(var i = 0; i < statusData.length; i++){
										if(statusData[i].statusId == value){
											return '<span>' + statusData[i].description + '</span>';
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
			   			}
			   		},
			   		{ text: '${uiLabelMap.DAAction}', dataField: 'actionBtn', filterable:false, sortable:false, width: '200px', 
					 	cellsrenderer: function(row, column, value){
					 		var data = $('#jqxgridPOSR').jqxGrid('getrowdata', row);
							if ('STLE_GRP_CREATED' == data.statusId){
								var str = '<div style=\"text-overflow: ellipsis; overflow: hidden; padding-bottom: 2px; text-align: center; margin-top: 4px;\">';"/>
								<#if security.hasPermission("PSETTLE_ROLE_UPDATE", session)>
									<#assign columnlist = columnlist + "str += '<a href=\"javascript:void(0)\" class=\"btn btn-mini btn-primary\" onClick=\"changeStatusGroup(&#39;' + data.promoSettleGroupId +'&#39;, &#39;STLE_GRP_PROCESSING&#39;)\">' + '${uiLabelMap.DAProcess}' + '</a>';">
								</#if>
								<#assign columnlist = columnlist + "str += '&nbsp;&nbsp;<a href=\"javascript:void(0)\" class=\"btn btn-mini btn-primary\" onClick=\"changeStatusGroup(&#39;' + data.promoSettleGroupId +'&#39;, &#39;STLE_GRP_CANCELED&#39;)\">' + '${uiLabelMap.DACancelStatus}' + '</a>';
								str += '</div>';
								return str;
							}
    						return '<span></span>';
					 	}
				 	}
              		"/>
<#-- Promotion by order settlement record -->
<@jqGrid id="jqxgridPOSR" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
		viewSize="20" showtoolbar="false" editmode="click" selectionmode="multiplecellsadvanced" 
		url="jqxGeneralServicer?sname=JQGetListPromoSettleGroup&promoSettleRecordId=${parameters.promoSettleRecordId?if_exists}"/>

<script type="text/javascript">
	function changeStatusGroup (groupId, newStatusId) {
		if (groupId != null && newStatusId != null) {
			$.ajax({ // ajax call starts
				url : "changeStatusPromoSettleGroup",
				type : "POST",
				data :{
					promoSettleGroupId: groupId,
					statusId: newStatusId
				},
				success : function(data) {
					$("#jqxgridPOSR").jqxGrid("updatebounddata");
					if(data.responseMessage == "error"){
			        	//commit(false);
			        	$('#jqxNotification').jqxNotification({ template: 'info'});
			        	$("#jqxNotification").text(data.errorMessage);
			        	$("#jqxNotification").jqxNotification("open");
			        }else{
			        	//commit(true);
			        	$('#container').empty();
			        	$('#jqxNotification').jqxNotification({ template: 'info'});
			        	$("#jqxNotification").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
			        	$("#jqxNotification").jqxNotification("open");
			        }
				},
				error : function(textStatus, errorThrown) {	
				}
			});
		}
	}
</script>