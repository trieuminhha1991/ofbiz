<#assign quotationStatusList = delegator.findByAnd("StatusItem", {"statusTypeId" : "QUOTATION_STATUS"}, null, false) />
<#assign salesMethodChannels = delegator.findByAnd("Enumeration", {"enumTypeId" : "SALES_METHOD_CHANNEL"}, null, false)/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript">
	<#if quotationStatusList?exists>
		var quotationStatusData = [
		<#list quotationStatusList as quotationStatus>
			<#assign description = StringUtil.wrapString(quotationStatus.description) />
			<#assign descriptionLo = StringUtil.wrapString(quotationStatus.get("description", locale)) />
			{statusId: "${quotationStatus.statusId}",
				description: "${description}",
				descriptionLo: "${descriptionLo}"}, 
		</#list>
		];
	<#else>
		var quotationStatusData = [];
	</#if>
	<#if salesMethodChannels?exists>
		var salesMethodChannelData = [
			<#list salesMethodChannels as salesChannel>
				{enumId: "${salesChannel.enumId}",
					description: "${StringUtil.wrapString(salesChannel.get("description", locale))}"
				},
			</#list>
		];
	<#else>
		var salesMethodChannelData = [];
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
	var myTimeoutVar;
	var objectMoveVar;
	function showMore(data, objectMove) {
		objectMoveVar = objectMove;
		$(objectMove).jqxTooltip('destroy');
		data = data.trim();
		var dataPart = data.replace("<p>", "");
		dataPart = dataPart.replace("</p>", "");
	    data = "<i onmouseenter='notDestroy()' onmouseleave='destroy()'>" + dataPart + "</i>";
	    $(objectMove).jqxTooltip({ content: data, position: 'right', autoHideDelay: 3000, closeOnClick: false, autoHide: false});
	    myTimeoutVar = setTimeout(function(){ 
			$(objectMove).jqxTooltip('destroy');
	    }, 2000);
	}
	function notDestroy() {
		clearTimeout(myTimeoutVar);
	}
	function destroy() {
		clearTimeout(myTimeoutVar);
		myTimeoutVar = setTimeout(function(){
			if (objectMoveVar != undefined && objectMoveVar != null) {
				$(objectMoveVar).jqxTooltip('destroy');
			}
		}, 2000);
	}
</script>
<script src="/delys/images/js/bootbox.min.js"></script>

<#assign dataField="[{name: 'productQuotationId', type: 'string'}, 
						{name: 'quotationName', type: 'string'}, 
						{name: 'salesChannel', type: 'string'}, 
						{name: 'currencyUomId', type: 'string'}, 
						{name: 'partyApplies', type: 'string'}, 
						{name: 'fromDate', type: 'date', other: 'Timestamp'}, 
						{name: 'thruDate', type: 'date', other: 'Timestamp'}, 
						{name: 'statusId', type: 'string'},
						{name: 'createDate', type: 'date', other: 'Timestamp'}
						]"/>
<#assign columnlist="{text: '${uiLabelMap.DAQuotationId}', dataField: 'productQuotationId', editable:false, width: '150px', 
		        			cellsrenderer: function(row, colum, value) {
	                        	var data = $('#jqxgridQuotation').jqxGrid('getrowdata', row);
	                        	return \"<span><a href='/delys/control/viewQuotation?productQuotationId=\" + data.productQuotationId + \"'>\" + data.productQuotationId + \"</a></span>\";
	                        }
	                  	}, 
						{text: '${uiLabelMap.DAQuotationName}', dataField: 'quotationName', 
							 cellsrenderer: function(row, colum, value){
						        return '<span onmouseenter=\"showMore(' + \"'\" + value + \"'\" + ',this)\" >' + value + '</span>';
					        }
						},
						{text: '${uiLabelMap.DAChannel}', dataField: 'salesChannel', filtertype: 'checkedlist', width: '80px', 
							cellsrenderer: function(row, column, value){
								var data = $('#jqxgridQuotation').jqxGrid('getrowdata', row);
	    						for(var i = 0 ; i < salesMethodChannelData.length; i++){
	    							if (value == salesMethodChannelData[i].enumId){
	    								return '<span title = ' + salesMethodChannelData[i].description +'>' + salesMethodChannelData[i].description + '</span>';
	    							}
	    						}
	    						return '<span title=' + value +'>' + value + '</span>';
							}, 
							createfilterwidget: function (column, columnElement, widget) {
				   				var filterBoxAdapter2 = new $.jqx.dataAdapter(salesMethodChannelData,
				                {
				                    autoBind: true
				                });
				                var uniqueRecords2 = filterBoxAdapter2.records;
				   				uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
				   				widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'enumId', valueMember : 'enumId', renderer: function (index, label, value) 
								{
									for(i=0;i < salesMethodChannelData.length; i++){
										if(salesMethodChannelData[i].enumId == value){
											return salesMethodChannelData[i].description;
										}
									}
								    return value;
								}});
								//widget.jqxDropDownList('checkAll');
				   			}
						}, 
						{text: '${uiLabelMap.DACreateDate}', dataField: 'createDate', cellsformat: 'dd/MM/yyyy - HH:mm:ss'}, 
						{text: '${uiLabelMap.DACurrencyUomId}', dataField: 'currencyUomId', cellsalign: 'center', width: '50px'}, 
						{text: '${uiLabelMap.DAPartyApply}', dataField: 'partyApplies', width: '150px', filterable : false, sortable : false}, 
						{text: '${uiLabelMap.DAStatus}', dataField: 'statusId', filtertype: 'checkedlist', 
							cellsrenderer: function(row, column, value){
						 		var data = $('#jqxgridQuotation').jqxGrid('getrowdata', row);
	    						for(var i = 0 ; i < quotationStatusData.length; i++){
	    							if (value == quotationStatusData[i].statusId){
	    								return '<span title = ' + quotationStatusData[i].description +'>' + quotationStatusData[i].descriptionLo + '</span>';
	    							}
	    						}
	    						return '<span title=' + value +'>' + value + '</span>';
						 	}, 
						 	createfilterwidget: function (column, columnElement, widget) {
								var filterDataAdapter = new $.jqx.dataAdapter(quotationStatusData, {
									autoBind: true
								});
								var records = filterDataAdapter.records;
								records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
									renderer: function(index, label, value){
										for(var i = 0; i < quotationStatusData.length; i++){
											if(quotationStatusData[i].statusId == value){
												return '<span>' + quotationStatusData[i].descriptionLo + '</span>';
											}
										}
										return value;
									}
								});
								widget.jqxDropDownList('checkAll');
				   			}
					 	}, 
						{text: '${uiLabelMap.DAFromDate}', dataField: 'fromDate', cellsformat: 'dd/MM/yyyy', filtertype:'range', width: '140px', 
						 	cellsrenderer: function(row, colum, value) {
	                        	var newDate = new Date(value);
	                        	return \"<span>\" + formatFullDate(value) + \"</span>\";
	                        }
					 	}, 
						{text: '${uiLabelMap.DAThroughDate}', dataField: 'thruDate', cellsformat: 'dd/MM/yyyy', filtertype:'range', width: '140px', 
						 	cellsrenderer: function(row, colum, value) {
	                        	var newDate = new Date(value);
	                        	return \"<span>\" + formatFullDate(value) + \"</span>\";
	                        }
					 	}
              		"/>
<#-- defaultSortColumn="productId" statusbarjqxgridSO -->
<#assign tmpCreateUrl = ""/>
<#assign tmpDelete = "false"/>
<#if security.hasPermission("DELYS_QUOTATION_CREATE", session)>
	<#assign tmpCreateUrl = "icon-plus open-sans@${uiLabelMap.DANewQuotation}@newQuotation"/>
</#if>
<#if security.hasPermission("DELYS_QUOTATION_DELETE", session)>
	<#assign tmpDelete = "true"/>
</#if>

<@jqGrid id="jqxgridQuotation" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
		viewSize="25" showtoolbar="true" defaultSortColumn="createDate" sortdirection="desc" filtersimplemode="true" showstatusbar="false" 
		customcontrol1=tmpCreateUrl 
		removeUrl="jqxGeneralServicer?sname=deleteProductQuotation&jqaction=C" deleteColumn="productQuotationId" deleterow=tmpDelete 
		url="jqxGeneralServicer?sname=JQGetListProductQuotation" mouseRightMenu="true" contextMenuId="contextMenu"/>

<div id='contextMenu' style="display:none">
	<ul>
	    <li><i class="icon-refresh open-sans"></i>${StringUtil.wrapString(uiLabelMap.DARefresh)}</li>
	    <li><i class="fa fa-search"></i>${StringUtil.wrapString(uiLabelMap.DAViewDetail)}</li>
	    <li><i class="fa fa-search"></i>${StringUtil.wrapString(uiLabelMap.DAViewDetailInNewTab)}</li>
	    <#if tmpDelete?exists && tmpDelete == "true"><li><i class="icon-trash open-sans"></i>${StringUtil.wrapString(uiLabelMap.accDeleteSelectedRow)}</li></#if>
	    <#if tmpCreateUrl != ""><li><i class="fa-plus-circle open-sans"></i>${StringUtil.wrapString(uiLabelMap.DANewQuotation)}</li></#if>
	</ul>
</div>
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	$("#contextMenu").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#contextMenu").on('itemclick', function (event) {
		var args = event.args;
        var rowindex = $("#jqxgridQuotation").jqxGrid('getselectedrowindex');
        var tmpKey = $.trim($(args).text());
        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DARefresh)}") {
        	$("#jqxgridQuotation").jqxGrid('updatebounddata');
        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DAViewDetail)}") {
        	var data = $("#jqxgridQuotation").jqxGrid("getrowdata", rowindex);
			if (data != undefined && data != null) {
				var productQuotationId = data.productQuotationId;
				var url = 'viewQuotation?productQuotationId=' + productQuotationId;
				var win = window.open(url, '_self');
				win.focus();
			}
        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DAViewDetailInNewTab)}") {
        	var data = $("#jqxgridQuotation").jqxGrid("getrowdata", rowindex);
			if (data != undefined && data != null) {
				var productQuotationId = data.productQuotationId;
				var url = 'viewQuotation?productQuotationId=' + productQuotationId;
				var win = window.open(url, '_blank');
				win.focus();
			}
        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.accDeleteSelectedRow)}") {
        	$("#deleterowbuttonjqxgridQuotation").click();
        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DANewQuotation)}") {
        	var win = window.open("newQuotation", "_self");
        	window.focus();
        }
	});
</script>
<style type="text/css">
	button.jqx-button.jqx-fill-state-hover#deleterowbuttonjqxgridQuotation, button.jqx-button.jqx-fill-state-hover#clearfilteringbuttonjqxgridQuotation,  
	button.jqx-button.jqx-fill-state-focus#deleterowbuttonjqxgridQuotation, button.jqx-button.jqx-fill-state-focus#clearfilteringbuttonjqxgridQuotation {
		  color: #438eb9 !important;
		  border-color: #FFF !important;
		  margin-top: 5px;
		  font-size: 14px!important;
		  background: none!important;
		  cursor: pointer;
	}
</style>
<#--
<@jqGrid url="jqxGeneralServicer?sname=JQGetListApAgreement" dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
	 showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addrow="true" addType="popup" addrow="true" addType="popup" deleterow="true"
	 createUrl="jqxGeneralServicer?sname=createAgreement&jqaction=C" addColumns="productId;partyIdFrom;partyIdTo;roleTypeIdFrom;roleTypeIdTo;agreementTypeId;agreementDate(java.sql.Timestamp);fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);description;textData"
	 removeUrl="jqxGeneralServicer?sname=cancelAgreement&jqaction=C" deleteColumn="agreementId" 
	 />
-->
