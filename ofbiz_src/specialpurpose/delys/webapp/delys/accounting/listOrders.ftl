<script>
	<#assign orderStatuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "ORDER_STATUS"), null, null, null, false) />
	var orderData =  new Array();
	<#list orderStatuses as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists)>
		row['statusId'] = "${item.statusId?if_exists}";
		row['description'] = "${description}";
		orderData[${item_index}] = row;
	</#list>
	
	<#assign orderTypes = delegator.findList("OrderType", null, null, null, null, false) />
	var orderTypeData =  new Array();
	<#list orderTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists)>
		row['orderTypeId'] = "${item.orderTypeId?if_exists}";
		row['description'] = "${description}";
		orderTypeData[${item_index}] = row;
	</#list>

	<#assign productStores = delegator.findList("ProductStore", null, null, null, null, false) />
	var psData =  new Array();
	<#list productStores as item>
		var row = {};
		<#assign storeName = StringUtil.wrapString(item.storeName?if_exists)>
		row['productStoreId'] = "${item.productStoreId?if_exists}";
		row['storeName'] = "${storeName}";
		psData[${item_index}] = row;
	</#list>

	<#assign Parties = delegator.findList("PartyNameView", null, null, null, null, false) />
	var partyData =  new Array();
	<#list Parties as item>
		var row = {};
		<#assign firstName = StringUtil.wrapString(item.firstName?if_exists)>
		<#assign middleName = StringUtil.wrapString(item.middleName?if_exists)>
		<#assign lastName = StringUtil.wrapString(item.lastName?if_exists)>
		<#assign groupName = StringUtil.wrapString(item.groupName?if_exists)>

		row['partyId'] = "${item.partyId?if_exists}";
		row['firstName'] = "${firstName}";
		row['middleName'] = "${middleName}";
		row['lastName'] = "${lastName}";
		row['groupName'] = "${groupName}";
		partyData[${item_index}] = row;
	</#list>
	
</script>
<div id="jqxwindowcustomer">
	<div>${uiLabelMap.accCreateNew}</div>
	<div style="overflow: hidden;">
		<table id="Customer">
			<tr>
				<td colspan="2">
					<input type="hidden" id="jqxwindowcustomerkey" value=""/>
					<input type="hidden" id="jqxwindowcustomervalue" value=""/>
					<div id="jqxgridcustomer"></div>
				</td>
			</tr>
		    <tr>
		        <td align="right"></td>
		        <td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="alterSave" value="${uiLabelMap.CommonSave}" /><input id="alterCancel" type="button" value="${uiLabelMap.CommonCancel}" /></td>
		    </tr>
		</table>
	</div>
</div>
<@jqGridMinimumLib/>
<script type="text/javascript">
	$("#jqxwindowcustomer").jqxWindow({
        resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, minWidth: 1000, maxWidth: 1200, height: 'auto', minHeight: 300     
    });
    $('#jqxWindow').on('open', function (event) {
    	//var offset = $("#jqxgrid").offset();
   		//$("#jqxwindowcustomer").jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 60 } });
	});
	$("#alterSave").click(function () {
		var tIndex = $('#jqxgridcustomer').jqxGrid('selectedrowindex');
		
		var data = $('#jqxgridcustomer').jqxGrid('getrowdata', tIndex);
		$('#' + $('#jqxwindowcustomerkey').val()).val(data.partyId);
		$("#jqxwindowcustomer").jqxWindow('close');
		var e = jQuery.Event("keydown");
		e.which = 50; // # Some key code value
		$('#' + $('#jqxwindowcustomerkey').val()).trigger(e);
	});
	// FromParty
    var sourceP =
    {
        datafields:
        [
            { name: 'partyId', type: 'string' },
            { name: 'partyTypeId', type: 'string' },
            { name: 'firstName', type: 'string' },
            { name: 'lastName', type: 'string' },
            { name: 'groupName', type: 'string' }
        ],
        cache: false,
        root: 'results',
        datatype: "json",
        updaterow: function (rowid, rowdata) {
            // synchronize with the server - send update command   
        },
        beforeprocessing: function (data) {
            sourceP.totalrecords = data.TotalRows;
        },
        filter: function () {
            // update the grid and send a request to the server.
            $("#jqxgridcustomer").jqxGrid('updatebounddata');
        },
        pager: function (pagenum, pagesize, oldpagenum) {
            // callback called when a page or page size is changed.
        },
        sort: function () {
            $("#jqxgridcustomer").jqxGrid('updatebounddata');
        },
        sortcolumn: 'partyId',
		sortdirection: 'asc',
        type: 'POST',
        data: {
	        noConditionFind: 'Y',
	        conditionsFind: 'N',
	    },
	    pagesize:5,
        contentType: 'application/x-www-form-urlencoded',
        url: 'jqxGeneralServicer?sname=getFromParty',
    };
    var dataAdapterP = new $.jqx.dataAdapter(sourceP,
    {
    	autoBind: true,
    	formatData: function (data) {
    		if (data.filterscount) {
                var filterListFields = "";
                for (var i = 0; i < data.filterscount; i++) {
                    var filterValue = data["filtervalue" + i];
                    var filterCondition = data["filtercondition" + i];
                    var filterDataField = data["filterdatafield" + i];
                    var filterOperator = data["filteroperator" + i];
                    filterListFields += "|OLBIUS|" + filterDataField;
                    filterListFields += "|SUIBLO|" + filterValue;
                    filterListFields += "|SUIBLO|" + filterCondition;
                    filterListFields += "|SUIBLO|" + filterOperator;
                }
                data.filterListFields = filterListFields;
            }
            return data;
        },
        loadError: function (xhr, status, error) {
            alert(error);
        },
        downloadComplete: function (data, status, xhr) {
                if (!sourceP.totalRecords) {
                    sourceP.totalRecords = parseInt(data['odata.count']);
                }
        }
    });
    $('#jqxgridcustomer').jqxGrid(
    {
        width:1000,
        source: dataAdapterP,
        filterable: true,
        virtualmode: true, 
        sortable:true,
        editable: false,
        autoheight:true,
        pageable: true,
        ready:function(){
        },
        rendergridrows: function(obj)
		{
			return obj.data;
		},
         columns: [
			  { text: '${uiLabelMap.accApInvoice_ToPartyId}', datafield: 'partyId'},
	          { text: '${uiLabelMap.accApInvoice_ToPartyTypeId}', datafield: 'partyTypeId'},
	          { text: '${uiLabelMap.FormFieldTitle_firstName}', datafield: 'firstName'},
	          { text: '${uiLabelMap.FormFieldTitle_lastName}', datafield: 'lastName'},
	          { text: '${uiLabelMap.accAccountingToParty}', datafield: 'groupName'}
			]
    });
    
    $(document).keydown(function(event){
	    if(event.ctrlKey)
	        cntrlIsPressed = true;
	});
	
	$(document).keyup(function(event){
		if(event.which=='17')
	    	cntrlIsPressed = false;
	});
	var cntrlIsPressed = false;
	var objGlobalParty = new Object();
</script>
<#assign columnlist="{ text: '${uiLabelMap.orderId}', dataField: 'orderId', width: 150,
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        					return '<a style = \"margin-left: 10px\" href=' + 'orderView?orderId=' + data.orderId + '>' +  data.orderId + '</a>'
    					}
					  },
 					  { text: '${uiLabelMap.orderDate}',filtertype: 'range', dataField: 'orderDate', width: 150, cellsformat: 'dd/MM/yyyy HH:mm:ss'},
                      { text: '${uiLabelMap.orderName}', dataField: 'orderName', width: 200 },
                      { text: '${uiLabelMap.orderTypeId}', dataField: 'orderTypeId', width: 150 , filtertype: 'checkedlist',
					  	createfilterwidget: function (column, columnElement, widget) {
					  		var sourceOrdType =
						    {
						        localdata: orderTypeData,
						        datatype: \"array\"
						    };
			   				var filterBoxAdapter = new $.jqx.dataAdapter(sourceOrdType,
			                {
			                    autoBind: true
			                });
			                var uniqueRecords = filterBoxAdapter.records;
			   				uniqueRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			   				
        					widget.jqxDropDownList({selectedIndex: 0,  source: uniqueRecords, displayMember: 'orderTypeId', valueMember: 'orderTypeId', dropDownWidth: 250,
        						renderer: function (index, label, value) {
                    				for(i = 0; i < orderTypeData.length; i++){
										if(orderTypeData[i].orderTypeId==value){
											return '<span>' + orderTypeData[i].description + '</span>'
										}
                    				}
                    			return value;
        					}});
							//widget.jqxDropDownList('checkAll');
    					},
    					cellsrenderer: function (row, column, value) {
							for(i = 0; i < orderTypeData.length; i++){
								if(value == orderTypeData[i].orderTypeId){
									return '<span title=' + orderTypeData[i].orderTypeId + '>' + orderTypeData[i].description + '</span>';
								}
							}
    					}
    				  },
                      { text: '${uiLabelMap.customer}',filtertype: 'olbiusdropgrid', dataField: 'customer', width: 150,
						createfilterwidget: function (column, columnElement, widget) {
			   				widget.width(140);
			   				widget.on('click',function(){
			   					 if(cntrlIsPressed){
			   					 	$(\"#jqxwindowcustomer\").jqxWindow('open');
			   					 	cntrlIsPressed = false;
			   					 }
			   				});
			   				},
    					cellsrenderer: function (row, column, value) {
							for(i = 0; i < partyData.length; i++){
								if(value == partyData[i].partyId){
									return '<span title='+ value +'>' + partyData[i].firstName + partyData[i].middleName + partyData[i].lastName + partyData[i].groupName + '</span>'
									}
								}
    						}
						 },
                      { text: '${uiLabelMap.productStoreId}', dataField: 'productStoreId', width: 200, filtertype: 'checkedlist',
					  	createfilterwidget: function (column, columnElement, widget) {
					  		var sourcePS =
						    {
						        localdata: psData,
						        datatype: \"array\"
						    };
			   				var filterBoxAdapter = new $.jqx.dataAdapter(sourcePS,
			                {
			                    autoBind: true
			                });
			                var uniqueRecords = filterBoxAdapter.records;
			   				uniqueRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			   				
        					widget.jqxDropDownList({selectedIndex: 0,  source: uniqueRecords, displayMember: 'productStoreId', valueMember: 'productStoreId', dropDownWidth: 250,
        						renderer: function (index, label, value) {
                    				for(i = 0; i < psData.length; i++){
										if(psData[i].productStoreId==value){
											return '<span>' + psData[i].storeName + '</span>'
										}
                    				}
                    			return value;
        					}});
							//widget.jqxDropDownList('checkAll');
    					},
    					cellsrenderer: function (row, column, value) {
							for(i = 0; i < psData.length; i++){
								if(value == psData[i].productStoreId){
									return '<span title=' + psData[i].productStoreId + '>' + psData[i].storeName + '</span>'
								}
							}
    					}
                      },
                      { text: '${uiLabelMap.grandTotal}', dataField: 'grandTotal', width: 200 , cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		return \"<span>\" + data.grandTotal +\"&nbsp;${defaultOrganizationPartyCurrencyUomId?if_exists}\" + \"</span>\";
					 	}},                     
					  { text: '${uiLabelMap.statusId}', datafield: 'statusId', filtertype: 'checkedlist',
					  	createfilterwidget: function (column, columnElement, widget) {
					  		var sourceOrd =
						    {
						        localdata: orderData,
						        datatype: \"array\"
						    };
			   				var filterBoxAdapter = new $.jqx.dataAdapter(sourceOrd,
			                {
			                    autoBind: true
			                });
			                var uniqueRecords = filterBoxAdapter.records;
			   				uniqueRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			   				
        					widget.jqxDropDownList({selectedIndex: 0, source: uniqueRecords, displayMember: 'statusId', valueMember: 'statusId', dropDownWidth: 250,
        						renderer: function (index, label, value) {
                    				for(i = 0; i < orderData.length; i++){
										if(orderData[i].statusId==value){
											return '<span>' + orderData[i].description + '</span>'
										}
                    				}
                    			return value;
        					}});
							//widget.jqxDropDownList('checkAll');
    					},
    					cellsrenderer: function (row, column, value) {
							for(i = 0; i < orderData.length; i++){
								if(value == orderData[i].statusId){
									return '<span>' + orderData[i].description + '</span>'
								}
							}
    					}
					   }
					 "/>
<#assign dataField="[{ name: 'orderId', type: 'string' },
                 	{ name: 'orderName', type: 'string' },
                 	{ name: 'orderDate', type: 'date' },
					{ name: 'customer', type: 'string' },
					{ name: 'grandTotal', type: 'string' },
                 	{ name: 'orderTypeId', type: 'string' },
                 	{ name: 'productStoreId', type: 'string' }, 
                 	{ name: 'statusId', type: 'string'}                                           
		 		 	]"/>	
<@jqGrid defaultSortColumn="orderDate" sortdirection="DESC" filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" alternativeAddPopup="alterpopupWindow" editable="false" 
		 url="jqxGeneralServicer?sname=JQListOrders" width="200"
		 otherParams="customer:S-getOrderCustomer(inputValue{orderId})<outputValue>"
		 />     