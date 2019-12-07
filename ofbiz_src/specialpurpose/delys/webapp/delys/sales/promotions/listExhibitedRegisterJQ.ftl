<#--
<#assign resultService = dispatcher.runSync("getProductPromoTypesByChannel", Static["org.ofbiz.base.util.UtilMisc"].toMap("salesMethodChannel","SALES_GT_CHANNEL"))/>
<#if resultService?exists && resultService.listProductPromoType?exists>
	<#assign listPromoType = resultService.listProductPromoType/>
</#if>
-->
<#assign statusList = delegator.findByAnd("StatusItem", {"statusTypeId" : "REG_PROMO_STTS"}, null, false) />
<#assign statusMarkList = delegator.findByAnd("StatusItem", {"statusTypeId" : "EXH_MARKING_STTS"}, null, false)/>
<script type="text/javascript">
	<#--
	var promoTypeData = new Array();
	<#list listPromoType as promoTypeItem>
		var row = {};
		row['typeId'] = "${promoTypeItem.productPromoTypeId}";
		row['description'] = "${StringUtil.wrapString(promoTypeItem.get("description", locale))}";
		promoTypeData[${promoTypeItem_index}] = row;
	</#list>
	-->
	
	var statusData = new Array();
	<#list statusList as statusItem>
		<#assign description = StringUtil.wrapString(statusItem.get("description", locale)) />
		var row = {};
		row['statusId'] = '${statusItem.statusId}';
		row['description'] = "${description}";
		statusData[${statusItem_index}] = row;
	</#list>
	var statusMarkData = new Array();
	var row0 = {};
	row0['statusId'] = '';
	row0['description'] = '__';
	statusMarkData[0] = row0;
	<#list statusMarkList as statusItem>
		<#assign description = StringUtil.wrapString(statusItem.get("description", locale)) />
		var row = {};
		row['statusId'] = '${statusItem.statusId}';
		row['description'] = "${description}";
		statusMarkData[${statusItem_index + 1}] = row;
	</#list>
</script>
<#assign dataField="[{name: 'productPromoRegisterId', type: 'string'}, 
						{name: 'partyId', type: 'string'}, 
						{name: 'productPromoId', type: 'string'}, 
						{name: 'productPromoRuleId', type: 'string'},
						{name: 'ruleName', type: 'string'}, 
						{name: 'registerStatus', type: 'string'}, 
						{name: 'promoMarkValue', type: 'string'}, 
						{name: 'createdDate', type: 'date', other: 'Timestamp'},
						{name: 'createdBy', type: 'string'},
						{name: 'agreementId', type: 'string'}
						]"/>
<#assign columnlist="{text: '${uiLabelMap.DAProductPromoRegisterId}', dataField: 'productPromoRegisterId', width: '180px', 
							cellsrenderer: function(row, colum, value) {
	                        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	                        	return \"<span><a href='/delys/control/listExhibitedMark?productPromoRegisterId=\" + data.productPromoRegisterId + \"'>\" + data.productPromoRegisterId + \"</a></span>\";
	                        }
						}, 
						{text: '${uiLabelMap.DACustomer}', dataField: 'partyId', width: '180px'},
						{text: '${uiLabelMap.DAProductPromoId}', dataField: 'productPromoId', width: '180px'}, 
						{text: '${uiLabelMap.DALevel}', dataField: 'ruleName', width: '100px'}, 
						{text: '${uiLabelMap.DARegisterStatus}', dataField: 'registerStatus', width: '160px', filtertype: 'checkedlist', 
							cellsrenderer: function(row, column, value){
						 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
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
						{text: '${uiLabelMap.DAMarkValue}', dataField: 'promoMarkValue', width: '100px', filtertype: 'checkedlist', 
							cellsrenderer: function(row, column, value){
						 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	    						for(var i = 0 ; i < statusMarkData.length; i++){
	    							if (value == statusMarkData[i].statusId){
	    								return '<span title = ' + statusMarkData[i].description +'>' + statusMarkData[i].description + '</span>';
	    							}
	    						}
	    						return '<span title=' + value +'>' + value + '</span>';
						 	}, 
						 	createfilterwidget: function (column, columnElement, widget) {
								var filterDataAdapter = new $.jqx.dataAdapter(statusMarkData, {
									autoBind: true
								});
								var records = filterDataAdapter.records;
								records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
									renderer: function(index, label, value){
										for(var i = 0; i < statusMarkData.length; i++){
											if(statusMarkData[i].statusId == value){
												return '<span>' + statusMarkData[i].description + '</span>';
											}
										}
										return value;
									}
								});
								widget.jqxDropDownList('checkAll');
				   			}
				   		}, 
						{text: '${uiLabelMap.DACreatedDate}', dataField: 'createdDate', width: '160px', cellsformat: 'dd/MM/yyyy - hh:mm:ss'},
						{text: '${uiLabelMap.DACreatedBy}', dataField: 'createdBy', width: '160px'},
						{text: '${uiLabelMap.DAAgreementId}', dataField: 'agreementId', width: '160px'}
              		"/>
<#assign tmpCreateUrl = ""/>
<#if security.hasPermission("REGPROMO_SUP_APPROVE", session) || security.hasPermission("REGPROMO_ASM_APPROVE", session) || security.hasPermission("REGPROMO_ROLE_APPROVE", session)>
	<#assign tmpCreateUrl = "fa fa-bolt@${uiLabelMap.DAApprove}@exhibitedRegisterUpdate"/>
</#if>
<#assign tmpCreateUrl2 = ""/>
<#if security.hasPermission("MARKEXH_ROLE_MARK", session)>
	<#assign tmpCreateUrl2 = "fa fa-key@${uiLabelMap.DAMarkValue}@exhibitedRegisterMark"/>
</#if>
<@jqGrid id="jqxgrid" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
		viewSize="25" showtoolbar="true" defaultSortColumn="partyId;createdDate" sortdirection="desc" filtersimplemode="true" showstatusbar="false" 
		customcontrol1=tmpCreateUrl customcontrol2=tmpCreateUrl2 
		url="jqxGeneralServicer?sname=JQGetListExhibitedRegister&productPromoTypeId=EXHIBITED"/>
