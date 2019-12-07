	<#--
	name="viewcreated" 		ORDER_CREATED	state.hasStatus('viewcreated')			${uiLabelMap.CommonCreated}
	name="viewprocessing"	ORDER_PROCESSING	state.hasStatus('viewprocessing')		${uiLabelMap.CommonProcessing}
	name="viewapproved" 	ORDER_APPROVED	state.hasStatus('viewapproved')			${uiLabelMap.CommonApproved}
	name="viewsupapproved"  ORDER_SUPAPPROVED	state.hasStatus('viewsupapproved')		${uiLabelMap.DASupApproved}
    name="viewsadapproved" 	ORDER_SADAPPROVED	state.hasStatus('viewsadapproved')		${uiLabelMap.DASadApproved}
    name="viewnppapproved" 	ORDER_NPPAPPROVED	state.hasStatus('viewnppapproved')		${uiLabelMap.DADistributorApproved}
    name="viewhold" 		ORDER_HOLD	state.hasStatus('viewhold')				${uiLabelMap.DAHeld}
    name="viewcompleted" 	ORDER_COMPLETED	state.hasStatus('viewcompleted')		${uiLabelMap.CommonCompleted}
    name="viewrejected" 	ORDER_REJECTED	state.hasStatus('viewrejected')			${uiLabelMap.CommonRejected}
    name="viewcancelled" 	ORDER_CANCELLED	state.hasStatus('viewcancelled')		${uiLabelMap.CommonCancelled}
	-->
<script type="text/javascript">
	<#assign state = Static["org.ofbiz.order.order.OrderListState"].getInstance(request) />
	<#assign listOrderStatus = [] />
	<#assign itemStatusTemp = {}/>
	<#if state.hasStatus('viewcreated')>
		<#assign itemStatusTemp = {"name" : "viewcreated", "id" : "ORDER_CREATED", "hasStatus" : "Y", "description" : "${uiLabelMap.CommonCreated}"}/>
		<#assign listOrderStatus = listOrderStatus + [itemStatusTemp] />
	<#else>
		<#assign itemStatusTemp = {"name" : "viewcreated", "id" : "ORDER_CREATED", "hasStatus" : "N", "description" : "${uiLabelMap.CommonCreated}"}/>
		<#assign listOrderStatus = listOrderStatus + [itemStatusTemp] />
	</#if>
	<#if state.hasStatus('viewprocessing')>
		<#assign itemStatusTemp = {"name" : "viewprocessing", "id" : "ORDER_PROCESSING", "hasStatus" : "Y", "description" : "${uiLabelMap.CommonProcessing}"}/>
		<#assign listOrderStatus = listOrderStatus + [itemStatusTemp] />
	<#else>
		<#assign itemStatusTemp = {"name" : "viewprocessing", "id" : "ORDER_PROCESSING", "hasStatus" : "N", "description" : "${uiLabelMap.CommonProcessing}"}/>
		<#assign listOrderStatus = listOrderStatus + [itemStatusTemp] />
	</#if>
	<#if state.hasStatus('viewapproved')>
		<#assign itemStatusTemp = {"name" : "viewapproved", "id" : "ORDER_APPROVED", "hasStatus" : "Y", "description" : "${uiLabelMap.CommonApproved}"}/>
		<#assign listOrderStatus = listOrderStatus + [itemStatusTemp] />
	<#else>
		<#assign itemStatusTemp = {"name" : "viewapproved", "id" : "ORDER_APPROVED", "hasStatus" : "N", "description" : "${uiLabelMap.CommonApproved}"}/>
		<#assign listOrderStatus = listOrderStatus + [itemStatusTemp] />
	</#if>
	<#if state.hasStatus('viewsupapproved')>
		<#assign itemStatusTemp = {"name" : "viewsupapproved", "id" : "ORDER_SUPAPPROVED", "hasStatus" : "Y", "description" : "${uiLabelMap.DASupApproved}"}/>
		<#assign listOrderStatus = listOrderStatus + [itemStatusTemp] />
	<#else>
		<#assign itemStatusTemp = {"name" : "viewsupapproved", "id" : "ORDER_SUPAPPROVED", "hasStatus" : "N", "description" : "${uiLabelMap.DASupApproved}"}/>
		<#assign listOrderStatus = listOrderStatus + [itemStatusTemp] />
	</#if>
	<#if state.hasStatus('viewsadapproved')>
		<#assign itemStatusTemp = {"name" : "viewsadapproved", "id" : "ORDER_SADAPPROVED", "hasStatus" : "Y", "description" : "${uiLabelMap.DASadApproved}"}/>
		<#assign listOrderStatus = listOrderStatus + [itemStatusTemp] />
	<#else>
		<#assign itemStatusTemp = {"name" : "viewsadapproved", "id" : "ORDER_SADAPPROVED", "hasStatus" : "N", "description" : "${uiLabelMap.DASadApproved}"}/>
		<#assign listOrderStatus = listOrderStatus + [itemStatusTemp] />
	</#if>
	<#if state.hasStatus('viewnppapproved')>
		<#assign itemStatusTemp = {"name" : "viewnppapproved", "id" : "ORDER_NPPAPPROVED", "hasStatus" : "Y", "description" : "${uiLabelMap.DADistributorApproved}"}/>
		<#assign listOrderStatus = listOrderStatus + [itemStatusTemp] />
	<#else>
		<#assign itemStatusTemp = {"name" : "viewnppapproved", "id" : "ORDER_NPPAPPROVED", "hasStatus" : "N", "description" : "${uiLabelMap.DADistributorApproved}"}/>
		<#assign listOrderStatus = listOrderStatus + [itemStatusTemp] />
	</#if>
	<#if state.hasStatus('viewhold')>
		<#assign itemStatusTemp = {"name" : "viewhold", "id" : "ORDER_HOLD", "hasStatus" : "Y", "description" : "${uiLabelMap.DAHeld}"}/>
		<#assign listOrderStatus = listOrderStatus + [itemStatusTemp] />
	<#else>
		<#assign itemStatusTemp = {"name" : "viewhold", "id" : "ORDER_HOLD", "hasStatus" : "N", "description" : "${uiLabelMap.DAHeld}"}/>
		<#assign listOrderStatus = listOrderStatus + [itemStatusTemp] />
	</#if>
	<#if state.hasStatus('viewcompleted')>
		<#assign itemStatusTemp = {"name" : "viewcompleted", "id" : "ORDER_COMPLETED", "hasStatus" : "Y", "description" : "${uiLabelMap.CommonCompleted}"}/>
		<#assign listOrderStatus = listOrderStatus + [itemStatusTemp] />
	<#else>
		<#assign itemStatusTemp = {"name" : "viewcompleted", "id" : "ORDER_COMPLETED", "hasStatus" : "N", "description" : "${uiLabelMap.CommonCompleted}"}/>
		<#assign listOrderStatus = listOrderStatus + [itemStatusTemp] />
	</#if>
	<#if state.hasStatus('viewrejected')>
		<#assign itemStatusTemp = {"name" : "viewrejected", "id" : "ORDER_REJECTED", "hasStatus" : "Y", "description" : "${uiLabelMap.CommonRejected}"}/>
		<#assign listOrderStatus = listOrderStatus + [itemStatusTemp] />
	<#else>
		<#assign itemStatusTemp = {"name" : "viewrejected", "id" : "ORDER_REJECTED", "hasStatus" : "N", "description" : "${uiLabelMap.CommonRejected}"}/>
		<#assign listOrderStatus = listOrderStatus + [itemStatusTemp] />
	</#if>
	<#if state.hasStatus('viewcancelled')>
		<#assign itemStatusTemp = {"name" : "viewcancelled", "id" : "ORDER_CANCELLED", "hasStatus" : "Y", "description" : "${uiLabelMap.CommonCancelled}"}/>
		<#assign listOrderStatus = listOrderStatus + [itemStatusTemp] />
	<#else>
		<#assign itemStatusTemp = {"name" : "viewcancelled", "id" : "ORDER_CANCELLED", "hasStatus" : "N", "description" : "${uiLabelMap.CommonCancelled}"}/>
		<#assign listOrderStatus = listOrderStatus + [itemStatusTemp] />
	</#if>
	
	<#assign itlength = listOrderStatus?size />
	<#if itlength gt 0>
		<#assign vaSI="var vaSI = ['" + StringUtil.wrapString(listOrderStatus[0].id?if_exists) + "'"/>
		<#assign vaSIChecked="var vaSIChecked = ['" + StringUtil.wrapString(listOrderStatus[0].hasStatus?if_exists) + "'"/>
		<#assign vaSIValue="var vaSIValue = [\"" + StringUtil.wrapString(listOrderStatus[0].description?if_exists) + "\""/>
		<#if listOrderStatus?size gt 1>
			<#list 1..(itlength - 1) as i>
				<#assign vaSI=vaSI + ",'" + StringUtil.wrapString(listOrderStatus[i].id?if_exists) + "'"/>
				<#assign vaSIChecked = vaSIChecked + ",'" + StringUtil.wrapString(listOrderStatus[i].hasStatus?if_exists) + "'"/>
				<#assign vaSIValue=vaSIValue + ",\"" + StringUtil.wrapString(listOrderStatus[i].description?if_exists) +"\""/>
			</#list>
		</#if>
		<#assign vaSI=vaSI + "];"/>
		<#assign vaSIChecked = vaSIChecked + "];"/>
		<#assign vaSIValue=vaSIValue + "];"/>
	<#else>
    	<#assign vaSI="var vaSI = [];"/>
    	<#assign vaSIChecked = "var vaSIChecked = [];"/>
    	<#assign vaSIValue="var vaSIValue = [];"/>
	</#if>
	${vaSI}
	${vaSIChecked}
	${vaSIValue}
	var dataStatusType = new Array();
	for(i=0;i < vaSI.length;i++){
		var row = {};
	    row["statusId"] = vaSI[i];
	    row["description"] = vaSIValue[i];
	    dataStatusType[i] = row;
	}
	<#assign orderTypeList = delegator.findList("OrderType", null, null, null, null, false)! />
	<#if orderTypeList?exists>
		var orderTypeData = [
		<#list orderTypeList as orderType>
			<#assign description = StringUtil.wrapString(orderType.get("description", locale)) />
			{
				orderTypeId : "${orderType.orderTypeId}",
				description : "${description}",
			},
		</#list>
		];
	<#else>
		var orderTypeData = [];
	</#if>
	
	<#assign resultValue = dispatcher.runSync("getListStoreDisViewedByUserLogin", Static["org.ofbiz.base.util.UtilMisc"].toMap("userLogin", userLogin))/>
	<#if Static["org.ofbiz.service.ServiceUtil"].isSuccess(resultValue)>
		<#assign productStoreList = resultValue.get("listProductStore")>
		var productStoreData = [<#list productStoreList as productStore>{
				storeName : "${productStore.storeName?default('')}",
				productStoreId : "${productStore.productStoreId}"
			},</#list>];
	<#else>
		var productStoreData = [];
	</#if>
	
	function addZero(i) {
	    if (i < 10) {i = "0" + i;}
	    return i;
	}
	function formatFullDate(value) {
		if (value != undefined && value != null && !(/^\s*$/.test(value))) {
			var dateStr = "";
			dateStr += addZero(value.getDate()) + '/';
			dateStr += addZero(value.getMonth()+1) + '/';
			dateStr += addZero(value.getFullYear()) + ' ';
			dateStr += addZero(value.getHours()) + ':';
			dateStr += addZero(value.getMinutes()) + ':';
			dateStr += addZero(value.getSeconds());
			return dateStr;
		} else {
			return "";
		}
	}
</script>

<#assign columnlist="{ text: '${uiLabelMap.DACreateDate}', dataField: 'orderDate', width: '12.5%', cellsformat: 'dd/MM/yyyy', filtertype:'range',
					 	cellsrenderer: function(row, colum, value) {
                        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
                        	var newDate = new Date(value);
                        	return \"<span>\" + formatFullDate(value) + \"</span>\";
                        }
					 },
					 { text: '${uiLabelMap.DAOrderId}', dataField: 'orderId',width: '12.5%', 
					 	cellsrenderer: function(row, colum, value) {
                        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
                        	return \"<span><a href='viewOrder?orderId=\" + data.orderId + \"'>\" + data.orderId + \"</a></span>\";
                        }
                     },
					 { text: '${uiLabelMap.DAOrderName}', dataField: 'orderName',width: '12.5%'},
					 { text: '${uiLabelMap.DAOrderType}', dataField: 'orderTypeId', sortable: false, filterable: false,width: '12.5%', 
					 	cellsrenderer: function(row, column, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
    						for(i = 0 ; i < orderTypeData.length; i++){
    							if (value == orderTypeData[i].orderTypeId){
    								return '<span title = ' + orderTypeData[i].description +'>' + orderTypeData[i].description + '</span>';
    							}
    						}
    						return '<span title=' + value +'>' + value + '</span>';
					 	}
					 },
					 { text: '${uiLabelMap.DACustomer}', dataField: 'customerId', width: '12.5%'},
					 { text: '${uiLabelMap.OrderProductStore}', dataField: 'productStoreId', width: '12.5%', filtertype: 'checkedlist', 
					 		cellsrenderer: function(row, column, value){
						 		// var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						 		if (productStoreData.length > 0) {
						 			for(var i = 0 ; i < productStoreData.length; i++){
		    							if (value == productStoreData[i].productStoreId){
		    								return '<span title = ' + productStoreData[i].storeName +'>' + productStoreData[i].storeName + '</span>';
		    							}
		    						}
						 		}
	    						return '<span title=' + value +'>' + value + '</span>';
						 	},
						 	createfilterwidget: function (column, columnElement, widget) {
						 		if (productStoreData.length > 0) {
									var filterDataAdapter = new $.jqx.dataAdapter(productStoreData, {
										autoBind: true
									});
									var records = filterDataAdapter.records;
									widget.jqxDropDownList({source: records, displayMember: 'storeName', valueMember: 'productStoreId',
										renderer: function(index, label, value){
											for(var i = 0; i < productStoreData.length; i++){
												if(productStoreData[i].productStoreId == value){
													return '<span>' + productStoreData[i].storeName + '</span>';
												}
											}
											return value;
										}
									});
									widget.jqxDropDownList('checkAll');
								}
				   			}
					 },
					 { text: '${uiLabelMap.CommonAmount}', dataField: 'grandTotal', width: '12.5%',filterable : false, 
					 	cellsrenderer: function(row, column, value) {
					 		var data = $(\"#jqxgrid\").jqxGrid(\"getrowdata\", row);
					 		var str = \"<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: right; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>\";
					 		str += formatcurrency(value,data.currencyUom);
					 		str += \"</div>\";
							return str;
					 	}
					 },
					 { text: '${uiLabelMap.CommonStatus}', dataField: 'statusId',filtertype: 'checkedlist', 
						cellsrenderer: function (row, colum, value){
	                    	for(i=0; i < vaSI.length;i++){
	                        	if(value==vaSI[i]){
	                        		return \"<span>\" + vaSIValue[i] + \"</span>\";
	                        	}
                        	}
	                    	return \"<span>\" + value + \"</span>\";
                		},
                		createfilterwidget: function (column, columnElement, widget) {
			   				var sourceST = {
						        localdata: dataStatusType,
						        datatype: \"array\"
						    };
			   				var filterBoxAdapter2 = new $.jqx.dataAdapter(sourceST, {autoBind: true});
			                var uniqueRecords2 = filterBoxAdapter2.records;
			   				widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'statusId', valueMember : 'statusId', 
			   					renderer: function (index, label, value) {
									for(i=0;i < dataStatusType.length; i++){
										if(dataStatusType[i].statusId == value){
											return dataStatusType[i].description;
										}
									}
								    return value;
								}
							});
			   			}
	                 }"/>
<#assign dataField="[{ name: 'orderDate', type: 'date', other: 'Timestamp'},
					{ name: 'orderId', type: 'string'},
					{ name: 'orderName', type: 'string'},
					{ name: 'orderTypeId', type: 'string'},
					{ name: 'customerId', type: 'string'},
					{ name: 'productStoreId', type: 'string'},
					{ name: 'grandTotal', type: 'string'},
					{ name: 'statusId', type: 'string'}, 
					{ name: 'currencyUom', type: 'string'}]"/>
<#if parameters.partyId?exists>
	<#assign paramExtend = "&partyId=" + parameters.partyId/>
</#if>
<@jqGrid filtersimplemode="true" filterable="true" addType="popup" dataField=dataField columnlist=columnlist usecurrencyfunction="true" 
		 url="jqxGeneralServicer?sname=JQGetListOrderByDis"/>
<#--
<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="false"
		 url="jqxGeneralServicer?sname=JQGetListOrderByDis"
		 createUrl="jqxGeneralServicer?sname=createAgreementGeographicalApplic&jqaction=C&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 removeUrl="jqxGeneralServicer?sname=removeAgreementGeographicalApplic&jqaction=D&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 updateUrl="jqxGeneralServicer?sname=updateAgreementGeographicalApplic&jqaction=U&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 deleteColumn="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];geoId"
		 addColumns="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];geoId"
		 editColumns="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];geoId"
		 />
-->