<script>
	<#assign orderStatuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "ORDER_STATUS"), null, null, null, false) />
	var orderData =  new Array();
	<#list orderStatuses as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("description", locale)?if_exists)>
		row['statusId'] = "${item.statusId?if_exists}";
		row['description'] = "${description}";
		orderData[${item_index}] = row;
	</#list>
	
	<#assign orderTypes = delegator.findList("OrderType", null, null, null, null, false) />
	var orderTypeData =  new Array();
	<#list orderTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("description", locale)?if_exists)>
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
<div id="jqxwindowpartyId" style="display:none;">
	<div>${uiLabelMap.accList} ${uiLabelMap.accCustomers}</div>
	<div style="overflow: hidden;">
		<table id="Customer">
			<tr>
				<td>
					<input type="hidden" id="jqxwindowpartyIdkey" value=""/>
					<input type="hidden" id="jqxwindowpartyIdvalue" value=""/>
					<div id="jqxgridcustomer"></div>
				</td>
			</tr>
		    <tr>
		        <td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="alterSave" value="${uiLabelMap.CommonSave}" /><input id="alterCancel" type="button" value="${uiLabelMap.CommonCancel}" /></td>
		    </tr>
		</table>
	</div>
</div>
<@jqGridMinimumLib/>
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme; 
	$("#jqxwindowpartyId").jqxWindow({
       theme: theme, isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, minWidth: 820, maxWidth: 1200, height: 'auto', minHeight: 515       
    });
    $('#jqxWindow').on('open', function (event) {
    	//var offset = $("#jqxgrid").offset();
   		//$("#jqxwindowpartyId").jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 60 } });
	});
	$("#alterSave").jqxButton({theme: theme});
	$("#alterCancel").jqxButton({theme: theme});
	$("#alterSave").click(function () {
		var tIndex = $('#jqxgridcustomer').jqxGrid('selectedrowindex');
		
		var data = $('#jqxgridcustomer').jqxGrid('getrowdata', tIndex);
		$('#' + $('#jqxwindowpartyIdkey').val()).val(data.partyId);
		$("#jqxwindowpartyId").jqxWindow('close');
		var e = jQuery.Event("keydown");
		e.which = 50; // # Some key code value
		$('#' + $('#jqxwindowpartyIdkey').val()).trigger(e);
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
        width:800,
        source: dataAdapterP,
        filterable: true,
        virtualmode: true, 
        sortable:true,
        editable: false,
        showfilterrow: false,
        theme: theme, 
        autoheight:true,
        pageable: true,
        pagesizeoptions: ['5', '10', '15'],
        ready:function(){
        },
        rendergridrows: function(obj)
		{
			return obj.data;
		},
         columns: [
	          { text: '${uiLabelMap.accApInvoice_ToPartyId}', datafield: 'partyId', width:150},
	          { text: '${uiLabelMap.accApInvoice_ToPartyTypeId}', datafield: 'partyTypeId', width:200},
	          { text: '${uiLabelMap.FormFieldTitle_firstName}', datafield: 'firstName', width:150},
	          { text: '${uiLabelMap.FormFieldTitle_lastName}', datafield: 'lastName', width:150},
	          { text: '${uiLabelMap.accAccountingToParty}', datafield: 'groupName', width:150}
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
</script>
<#assign columnlist="
					{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},	
					{ text: '${uiLabelMap.DAOrderId}', dataField: 'orderId', pinned: true, width: 120,
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        					return '<a style = \"margin-left: 10px\" href=' + 'orderView?orderId=' + data.orderId + '>' +  data.orderId + '</a>'
    					}
					  },
 					  { text: '${uiLabelMap.DACreateDate}',filtertype: 'range',  width: 120, dataField: 'orderDate', cellsformat: 'dd/MM/yyyy'},
                      { text: '${uiLabelMap.DAOrderName}', dataField: 'orderName', width: 120 },
                      { text: '${uiLabelMap.DACustomer}',filtertype: 'olbiusdropgrid', width: 200, dataField: 'partyId',
						createfilterwidget: function (column, columnElement, widget) {
			   				widget.width(490);
			   				},
    					cellsrenderer: function (row, column, value) {
							for(i = 0; i < partyData.length; i++){
								if(value == partyData[i].partyId){
									return '<span title='+ value +'>' + partyData[i].groupName + '[' + value + ']' +  '</span>'
									}
								}
    						}
						 },
                      { text: '${uiLabelMap.OrderProductStore}', dataField: 'productStoreId', width: 200, filtertype: 'checkedlist',
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
							widget.jqxDropDownList('checkAll');
    					},
    					cellsrenderer: function (row, column, value) {
							for(i = 0; i < psData.length; i++){
								if(value == psData[i].productStoreId){
									return '<span title=' + psData[i].productStoreId + '>' + psData[i].storeName + '</span>'
								}
							}
    					}
                      },                    
					  { text: '${uiLabelMap.CommonStatus}', datafield: 'statusId', width: 200, filtertype: 'checkedlist',
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
							widget.jqxDropDownList('checkAll');
    					},
    					cellsrenderer: function (row, column, value) {
							for(i = 0; i < orderData.length; i++){
								if(value == orderData[i].statusId){
									return '<span>' + orderData[i].description + '</span>'
								}
							}
    					}
					   },
                      { text: '${uiLabelMap.CommonAmount}', filterable: false, dataField: 'grandTotal', width: 200 , cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		return '<span style=\"text-align: right\">' + formatcurrency(data.grandTotal,data.currencyUom) + '</span>';
					 	}}					   
					 "/>
<#assign dataField="[{ name: 'orderId', type: 'string' },
                 	{ name: 'orderName', type: 'string' },
                 	{ name: 'orderDate', type: 'date', other:'Timestamp' },
					{ name: 'partyId', type: 'string' },
					{ name: 'grandTotal', type: 'string' },
                 	{ name: 'productStoreId', type: 'string' }, 
                 	{ name: 'statusId', type: 'string'},
                 	{ name: 'currencyUom', type: 'string'}                                           
		 		 	]"/>	
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownlist.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<@jqGrid defaultSortColumn="orderDate" sortdirection="DESC" filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" alternativeAddPopup="alterpopupWindow" editable="false" 
		 url="jqxGeneralServicer?sname=JQListSaleOrders" jqGridMinimumLibEnable="false"	
		 />     