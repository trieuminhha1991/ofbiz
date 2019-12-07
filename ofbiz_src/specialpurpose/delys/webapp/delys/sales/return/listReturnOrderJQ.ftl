<style type="text/css">
	#horizontalScrollBarjqxContactMechGrid {
	  	visibility: inherit !important;
	}
</style>
<script type="text/javascript">
	<#assign statusList = delegator.findByAnd("StatusItem", {"statusTypeId" : "ORDER_RETURN_STTS"}, null, false) />
	var statusData = new Array();
	<#list statusList as statusItem>
		<#assign description = StringUtil.wrapString(statusItem.get("description", locale)) />
		var row = {};
		row['statusId'] = '${statusItem.statusId}';
		row['description'] = "${description}";
		statusData[${statusItem_index}] = row;
	</#list>
	
	<#assign reasonList = delegator.findByAnd("ReturnReason", null, null, false) />
	var reasonData = new Array();
	<#list reasonList as reasonItem>
		<#assign description = StringUtil.wrapString(reasonItem.get("description", locale)) />
		var row = {};
		row['reasonId'] = '${reasonItem.returnReasonId}';
		row['description'] = "${description}";
		reasonData[${reasonItem_index}] = row;
	</#list>
</script>
<#assign uomList = delegator.findByAnd("Uom", {"uomTypeId" : "PRODUCT_PACKING"}, null, false)/>
<script type="text/javascript">
	var uomData = new Array();
	<#list uomList as uomItem>
		<#assign description = StringUtil.wrapString(uomItem.get("description", locale)) />
		var row = {};
		row['uomId'] = '${uomItem.uomId}';
		row['description'] = "${description?default('')}";
		uomData[${uomItem_index}] = row;
	</#list>
	
	<#assign returnHeaderTypeList = delegator.findByAnd("ReturnHeaderType", null, null, false)/>
	var returnHeaderTypeData = new Array();
	<#list returnHeaderTypeList as item>
		<#assign description = StringUtil.wrapString(item.get("description", locale)) />
		var row = {};
		row['typeId'] = '${item.returnHeaderTypeId}';
		row['description'] = "${description?default('')}";
		returnHeaderTypeData[${item_index}] = row;
	</#list>
</script>

<#assign dataField="[{ name: 'returnId', type: 'string'},
					{name: 'returnHeaderTypeId', type: 'string'},
	             	{name: 'statusId', type: 'string'},
	             	{name: 'createdBy', type: 'string'},
	             	{name: 'fromPartyId', type: 'string'},
					{name: 'toPartyId', type: 'string'},
	             	{name: 'paymentMethodId', type: 'string'},
	             	{name: 'finAccountId', type: 'string'},
	             	{name: 'billingAccountId', type: 'string'},
	             	{name: 'entryDate', type: 'date', other: 'Timestamp'},
					{name: 'originContactMechId', type: 'string'},
					{name: 'destinationFacilityId', type: 'string'},
					{name: 'needsInventoryReceive', type: 'string'},
					{name: 'currencyUomId', type: 'string'},
					{name: 'supplierRmaId', type: 'string'},
	 		 	]"/>
	 		 	
<#assign columnlist="{text: '${uiLabelMap.DAReturnOrderId}', dataField: 'returnId', width: 150,
						cellsrenderer: function(row, colum, value) {
                        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
                        	return \"<span><a href='/delys/control/viewReturnOrderGeneral?returnId=\" + data.returnId + \"'>\" + data.returnId + \"</a></span>\";
                        }
					},
					{text: '${uiLabelMap.DAReturnHeaderTypeId}', dataField: 'returnHeaderTypeId', width: 150, filtertype: 'checkedlist', 
						cellsrenderer: function(row, column, value){
							for(var i = 0; i < returnHeaderTypeData.length; i++){
								if(returnHeaderTypeData[i].typeId == value){
									return '<span title=' + value + '>' + returnHeaderTypeData[i].description + '</span>'
								}
							}
						}, 
					 	createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(returnHeaderTypeData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							widget.jqxDropDownList({source: records, displayMember: 'typeId', valueMember: 'typeId',
								renderer: function(index, label, value){
									for(var i = 0; i < returnHeaderTypeData.length; i++){
										if(returnHeaderTypeData[i].typeId == value){
											return '<span>' + returnHeaderTypeData[i].description + '</span>';
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
			   			}
					},
				 	{text: '${uiLabelMap.DAStatus}', dataField: 'statusId', width: 150, filtertype: 'checkedlist', 
						cellsrenderer: function(row, column, value){
							for(var i = 0; i < statusData.length; i++){
								if(statusData[i].statusId == value){
									return '<span title=' + value + '>' + statusData[i].description + '</span>'
								}
							}
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
				 	{text: '${uiLabelMap.DACreatedBy}', dataField: 'createdBy', width: 150},
				 	{text: '${uiLabelMap.DAFromPartyId}', dataField: 'fromPartyId', width: 150},
				 	{text: '${uiLabelMap.DAToPartyId}', dataField: 'toPartyId', width: 150, filterable: false, sortable: false},
				 	{text: '${uiLabelMap.DAEntryDate}', dataField: 'entryDate', width: 150, cellsformat: 'd', filtertype: 'range'},
				 	{text: '${uiLabelMap.DAOriginContactMechId}', dataField: 'originContactMechId', width: 150},
				 	{text: '${uiLabelMap.DACurrencyUomId}', dataField: 'currencyUomId', width: 150},
				 "/>

<#assign tmpCreateUrl = ""/>
<#if security.hasPermission("RETURNREQ_ROLE_UPDATE", session)>
	<#assign tmpCreateUrl = "fa fa-bolt@${uiLabelMap.DAApprove}@updateReturnProductReq"/>
</#if>
<#assign hasPermissionCreate = false/>
<#if security.hasPermission("RETURNREQ_ROLE_CREATE", session)>
	<#assign hasPermissionCreate = true/>
</#if>
<@jqGrid url="jqxGeneralServicer?sname=JQGetListReturnOrderFromCustomer" dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
		 showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addType="popup" deleterow="false" defaultSortColumn="entryDate" sortdirection="desc" 
		 createUrl="jqxGeneralServicer?sname=createReturnRequirement&jqaction=C" addrow="${hasPermissionCreate?string}" 
		 addColumns="contactMechId;customerId;description;distributorId;listProducts(java.util.List);reason;requirementStartDate(java.sql.Timestamp);requiredByDate(java.sql.Timestamp);requirementTypeId[RETURN_PRODDIS_REQ];" 
		 mouseRightMenu="true" contextMenuId="contextMenu" customcontrol1=tmpCreateUrl 
		 />
<div id='contextMenu' style="display:none">
	<ul>
	    <li><i class="icon-refresh open-sans"></i>${StringUtil.wrapString(uiLabelMap.DARefresh)}</li>
	    <li><i class="fa fa-search"></i>${StringUtil.wrapString(uiLabelMap.DAViewDetail)}</li>
	</ul>
</div>

<#if hasPermissionCreate>
<div id="alterpopupWindow" style="display:none">
	<div>${uiLabelMap.accCreateNew}</div>
	<div style="overflow: hidden;">
		<form id="alterpopupWindowCreatSalesmanForm" class="form-horizontal">
			<input type="hidden" name="requirementTypeId" value="RETURN_PRODDIS_REQ"></input>
			<div class="row-fluid form-window-content">
				<div class="span6">
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.DACustomer}
						</div>
						<div class="span7">
							<div id="customerIdAdd">
								<div style="border-color: transparent;" id="jqxCustomerBySupGrid"></div>
							</div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.DADistributor}
						</div>
						<div class="span7">
							<div id="distributorIdAdd">
								<div style="border-color: transparent;" id="jqxDistributorGrid"></div>
							</div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.DAAddress}
						</div>
						<div class="span7">
							<div id="contactMechIdAdd">
								<div style="border-color: transparent;" id="jqxContactMechGrid"></div>
							</div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.DADescription}
						</div>
						<div class="span7">
							<textarea id="descriptionValueAdd" rows="2"></textarea>
						</div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.DARequirementStartDateOrigin}
						</div>
						<div class="span7">
							<div id="requirementStartDateAdd"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.DARequiredByDateOrigin}
						</div>
						<div class="span7">
							<div id="requiredByDateAdd"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">
							${uiLabelMap.DAReason}
						</div>
						<div class="span7">
							<div id="reasonValueAdd"></div>
						</div>
					</div>
				</div>
				<div class="span12" style="margin-left:0px">
					<div class="row-fluid margin-bottom10">
						<div id="jqxgridProduct"></div>
					</div>
				</div>
			</div>
		</form>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
	</div>
</div>
</#if>
<#--<div id="dialog-message" title="${StringUtil.wrapString(uiLabelMap.DAYouNotYetChooseProduct)}!" style="display:none">-->
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	$("#contextMenu").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#contextMenu").on('itemclick', function (event) {
		var args = event.args;
        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
        var tmpKey = $.trim($(args).text());
        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DARefresh)}") {
        	$("#jqxgrid").jqxGrid('updatebounddata');
        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DAViewDetail)}") {
        	var data = $("#jqxgrid").jqxGrid("getrowdata", rowindex);
			if (data != undefined && data != null) {
				var returnId = data.returnId;
				var url = 'viewReturnOrderGeneral?returnId=' + returnId;
				var win = window.open(url, '_blank');
				win.focus();
			}
        }
	});
</script>
<script type="text/javascript">
	<#if hasPermissionCreate>
	// Create Window
	$("#alterpopupWindow").jqxWindow({
		maxWidth: 1500, minWidth: 950, minHeight: 580, maxHeight: 1200, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
	});
	$("#requirementStartDateAdd").jqxDateTimeInput({height: '25px',width: 200, formatString: 'dd-MM-yyyy HH:mm:ss'});
	$("#requiredByDateAdd").jqxDateTimeInput({height: '25px',width: 200, formatString: 'dd-MM-yyyy HH:mm:ss', allowNullDate: true, value: null});
	
	<#assign returnReasons = delegator.findByAnd("ReturnReason", null, ["sequenceId"], false)/>
	var dataReason = new Array();
	var reasonSelectedIndex = 0;
	<#if returnReasons?exists>
		<#list returnReasons as returnReason>
			var mapItem = {};
			<#if returnReason.returnReasonId == "RTN_NORMAL_RETURN">reasonSelectedIndex = ${returnReason_index}</#if>
			<#assign description = StringUtil.wrapString(returnReason.get("description", locale))/>
			mapItem.returnReasonId = "${returnReason.returnReasonId}";
			mapItem.description = "${description}";
			dataReason[${returnReason_index}] = mapItem;
		</#list>
	</#if>
	// var sourceReason = ["${StringUtil.wrapString(uiLabelMap.DACollectionProductIsAboutToExpire)}", "${StringUtil.wrapString(uiLabelMap.DAConsumptionDifficultProduct)}", "${StringUtil.wrapString(uiLabelMap.DAOther)}"];
    // Create a jqxDropDownList
	// $("#reasonValueAdd").jqxDropDownList({source: sourceReason, width: '200', placeHolder: "${uiLabelMap.DASelectAReason}:"});
	var sourceReason = {
        localdata: dataReason,
        datatype: "array"
    };
    var dataReasonAdapter = new $.jqx.dataAdapter(sourceReason);
    $('#reasonValueAdd').jqxDropDownList({selectedIndex: reasonSelectedIndex, source: dataReasonAdapter, displayMember: "description", valueMember: "returnReasonId", width: 200});
		
//	$("#alterSave").jqxButton({width: 100});
//	$("#alterCancel").jqxButton({width: 100});
	
	// update the edited row when the user clicks the 'Save' button.
	$("#alterSave").click(function () {
		if(!$('#alterpopupWindow').jqxValidator('validate')) return false;
		var row;
		var selectedIndexs = $('#jqxgridProduct').jqxGrid('getselectedrowindexes');
		var listProducts = new Array();
		for(var i = 0; i < selectedIndexs.length; i++){
			var data = $('#jqxgridProduct').jqxGrid('getrowdata', selectedIndexs[i]);
			var map = {};
			map['productId'] = data.productId;
			map['quantity'] = data.quantity;
			map['quantityUomId'] = data.quantityUomId;
			map['expireDate'] = (new Date(data.expireDate)).getTime();
			listProducts[i] = map;
		}
		if (listProducts == null || listProducts.length <= 0) {
			<#--
			$("#dialog-message").text("${StringUtil.wrapString(uiLabelMap.DAYouNotYetChooseProduct)}!" );
			$("#dialog-message").dialog({
		      	resizable: false,
		      	height:180,
		      	modal: true,
		      	buttons: {
		        	"${StringUtil.wrapString(uiLabelMap.wgcancel)}": function() {
		          		$(this).dialog("close");
		        	}
		      	}
			});
			-->
			bootbox.dialog("${uiLabelMap.DAYouNotYetChooseProduct}!", [{
				"label" : "OK",
				"class" : "btn-small btn-primary",
				}]
			);
			return false;
		}
		listProducts = JSON.stringify(listProducts);
		var today = new Date();
		row = {	customerId: $('#customerIdAdd').val(),
				createdByUserLogin: '${userLogin.userLoginId}',
				statusId: 'RETURREQ_CREATED',
				createdDate: today,
				distributorId: $('#distributorIdAdd').val(),
				contactMechId:$('#contactMechIdAdd').val(),
				description:$('#descriptionValueAdd').val(),
				requirementStartDate:$('#requirementStartDateAdd').val(),
				requiredByDate:$('#requiredByDateAdd').val(),
				reason:$('#reasonValueAdd').val(),
				listProducts:listProducts
	  	};
		$("#jqxgrid").jqxGrid('addRow', null, row, "first");
		$("#jqxgrid").jqxGrid('updatebounddata');
    	$("#alterpopupWindow").jqxWindow('close');
	});
	
	$('#alterpopupWindow').jqxValidator({
		rules: [
			{input: '#customerIdAdd', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
				function (input, commit) {
					if($('#customerIdAdd').val() == null || $('#customerIdAdd').val() == ''){
						return false;
					}
					return true;
				}
			},
			{input: '#distributorIdAdd', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
				function (input, commit) {
					if($('#distributorIdAdd').val() == null || $('#distributorIdAdd').val() == ''){
						return false;
					}
					return true;
				}
			},
			{input: '#contactMechIdAdd', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
				function (input, commit) {
					if($('#contactMechIdAdd').val() == null || $('#contactMechIdAdd').val() == ''){
						return false;
					}
					return true;
				}
			},
			{input: '#reasonValueAdd', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule:
				function (input, commit) {
					if($('#reasonValueAdd').val() == null || $('#reasonValueAdd').val() == ''){
						return false;
					}
					return true;
				}
			}
		]
	});
	
	// ==========================================================================================================
	// List customer by supervisor
	var sourceCUST = {datafields: [
						      {name: 'partyId', type: 'string'},
						      {name: 'partyTypeId', type: 'string'},
						      {name: 'groupName', type: 'string'},
						      {name: 'lastName', type: 'string'},
						      {name: 'firstName', type: 'string'},
						    ],
				cache: false,
				root: 'results',
				datatype: "json",
				updaterow: function (rowid, rowdata) {
					// synchronize with the server - send update command   
				},
				beforeprocessing: function (data) {
				    sourceCUST.totalrecords = data.TotalRows;
				},
				filter: function () {
				   // update the grid and send a request to the server.
				   $("#jqxCustomerBySupGrid").jqxGrid('updatebounddata');
				},
				pager: function (pagenum, pagesize, oldpagenum) {
				  // callback called when a page or page size is changed.
				},
				sort: function () {
				  $("#jqxCustomerBySupGrid").jqxGrid('updatebounddata');
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
				url: 'jqxGeneralServicer?sname=JQGetListCustomerBySup',
			};
			
    var dataAdapterPG = new $.jqx.dataAdapter(sourceCUST, {
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
	                if (!sourceCUST.totalRecords) {
	                    sourceCUST.totalRecords = parseInt(data['odata.count']);
	                }
		        }
    });
    
	$("#customerIdAdd").jqxDropDownButton({width: 215, height: 25});
	$("#jqxCustomerBySupGrid").jqxGrid({
		width:600,
		source: dataAdapterPG,
		filterable: true,
		virtualmode: true, 
		showfilterrow: true,
		sortable:true,
		editable: false,
		autoheight:true,
		columnsresize: true,
		pageable: true,
		rendergridrows: function(obj) {	
			return obj.data;
		},
		columns: [{text: '${uiLabelMap.accPartyId}', datafield: 'partyId', width:'25%'},
				{text: '${uiLabelMap.accGroupName}', datafield: 'groupName', width:'35%'},
				{text: '${uiLabelMap.accFirstName}', datafield: 'lastName', width:'20%'},
				{text: '${uiLabelMap.accLastName}', datafield: 'firstName', width:'20%'},
		]
	});
	//{text: '${uiLabelMap.accPartyTypeId}', datafield: 'partyTypeId', width:'10%'},
	$("#jqxCustomerBySupGrid").on('rowselect', function (event) {
        var args = event.args;
        var row = $("#jqxCustomerBySupGrid").jqxGrid('getrowdata', args.rowindex);
        var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['partyId'] + '</div>';
        $("#customerIdAdd").jqxDropDownButton('setContent', dropDownContent);
        
        /*updateMultiElement(
	        {customerId: $("#customerIdAdd").val()
			}, 
			'getDistributorByCustomer', 
			'listFacilities', 'listFacilitiesTo', 
			'listContactMechsFrom', 'listContactMechsTo', 
			'facilityId', 'facilityName', 
			'facilityId', 'facilityName', 
			'contactMechId',  'address1', 
			'contactMechId', 'address1', 
			'facilityIdFrom', 'facilityIdTo', 'originContactMechId', 'destContactMechId'
		);*/
	 	var tmpCustomer = $("#jqxDistributorGrid").jqxGrid('source');
 		var customerId = $("#customerIdAdd").val();
	 	tmpCustomer._source.url = "jqxGeneralServicer?sname=JQGetListDistributorByCustomer&noUserlogin=Y&customerId="+customerId;
	 	$("#jqxDistributorGrid").jqxGrid('source', tmpCustomer);
	 	
	 	var tmpAddress = $("#jqxContactMechGrid").jqxGrid('source');
	 	tmpAddress._source.url = "jqxGeneralServicer?sname=JQGetPartyPostalAddresses&partyId="+customerId;
	 	$("#jqxContactMechGrid").jqxGrid('source', tmpAddress);
    });
    
    // List distributor by customer
	var sourceDIS = {datafields: [
						      {name: 'partyId', type: 'string'},
						      {name: 'partyTypeId', type: 'string'},
						      {name: 'groupName', type: 'string'},
						      {name: 'lastName', type: 'string'},
						      {name: 'firstName', type: 'string'},
						    ],
				cache: false,
				root: 'results',
				datatype: "json",
				updaterow: function (rowid, rowdata) {
					// synchronize with the server - send update command   
				},
				beforeprocessing: function (data) {
				    sourceDIS.totalrecords = data.TotalRows;
				},
				filter: function () {
				   // update the grid and send a request to the server.
				   $("#jqxDistributorGrid").jqxGrid('updatebounddata');
				},
				pager: function (pagenum, pagesize, oldpagenum) {
				  // callback called when a page or page size is changed.
				},
				sort: function () {
				  $("#jqxDistributorGrid").jqxGrid('updatebounddata');
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
				url: 'jqxGeneralServicer?sname=JQGetListDistributorByCustomer&noUserlogin=Y',
			};
			
    var dataAdapterDIS = new $.jqx.dataAdapter(sourceDIS, {
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
	                if (!sourceDIS.totalRecords) {
	                    sourceDIS.totalRecords = parseInt(data['odata.count']);
	                }
		        }
    });
    
	$("#distributorIdAdd").jqxDropDownButton({width: 215, height: 25});
	$("#jqxDistributorGrid").jqxGrid({
		width:600,
		source: dataAdapterDIS,
		filterable: true,
		virtualmode: true, 
		showfilterrow: true,
		sortable:true,
		editable: false,
		autoheight:true,
		columnsresize: true,
		pageable: true,
		rendergridrows: function(obj) {	
			return obj.data;
		},
		columns: [{text: '${uiLabelMap.accPartyId}', datafield: 'partyId', width:'25%'},
				{text: '${uiLabelMap.accGroupName}', datafield: 'groupName', width:'35%'},
				{text: '${uiLabelMap.accFirstName}', datafield: 'lastName', width:'20%'},
				{text: '${uiLabelMap.accLastName}', datafield: 'firstName', width:'20%'},
		]
	});
	//{text: '${uiLabelMap.accPartyTypeId}', datafield: 'partyTypeId', width:'10%'},
	$("#jqxDistributorGrid").on('rowselect', function (event) {
        var args = event.args;
        var row = $("#jqxDistributorGrid").jqxGrid('getrowdata', args.rowindex);
        var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['partyId'] + '</div>';
        $("#distributorIdAdd").jqxDropDownButton('setContent', dropDownContent);
    });
    
    // List address by customer
	var sourceADDR = {datafields: [
						      {name: 'contactMechId', type: 'string'},
						      {name: 'address1', type: 'string'},
						      {name: 'address2', type: 'string'},
						      {name: 'directions', type: 'string'},
						      {name: 'city', type: 'string'},
						      {name: 'postalCode', type: 'string'},
						      {name: 'stateProvinceGeoId', type: 'string'},
						      {name: 'countyGeoId', type: 'string'},
						      {name: 'countryGeoId', type: 'string'},
						      {name: 'contactMechPurposeTypeId', type: 'string'},
						    ],
				cache: false,
				root: 'results',
				datatype: "json",
				updaterow: function (rowid, rowdata) {
					// synchronize with the server - send update command   
				},
				beforeprocessing: function (data) {
				    sourceDIS.totalrecords = data.TotalRows;
				},
				filter: function () {
				   // update the grid and send a request to the server.
				   $("#jqxDistributorGrid").jqxGrid('updatebounddata');
				},
				pager: function (pagenum, pagesize, oldpagenum) {
				  // callback called when a page or page size is changed.
				},
				sort: function () {
				  $("#jqxDistributorGrid").jqxGrid('updatebounddata');
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
				url: 'jqxGeneralServicer?sname=JQGetPartyPostalAddresses',
			};
			
    var dataAdapterADDR = new $.jqx.dataAdapter(sourceADDR, {
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
	                if (!sourceDIS.totalRecords) {
	                    sourceADDR.totalRecords = parseInt(data['odata.count']);
	                }
		        }
    });
    
	$("#contactMechIdAdd").jqxDropDownButton({width: 215, height: 25});
	$("#jqxContactMechGrid").jqxGrid({
		width:700,
		source: dataAdapterADDR,
		filterable: true,
		virtualmode: true, 
		showfilterrow: true,
		sortable:true,
		editable: false,
		autoheight:true,
		columnsresize: true,
		pageable: true,
		rendergridrows: function(obj) {	
			return obj.data;
		},
		columns: [{text: '${uiLabelMap.DAContactMechId}', datafield: 'contactMechId', width:'150px'},
				{text: '${uiLabelMap.DAAddress1}', datafield: 'address1', width:'150px'},
				{text: '${uiLabelMap.DAAddress2}', datafield: 'address2', width:'150px'},
				{text: '${uiLabelMap.DADirections}', datafield: 'directions', width:'100px'},
				{text: '${uiLabelMap.DACity}', datafield: 'city', width:'100px'},
				{text: '${uiLabelMap.DAPostalCode}', datafield: 'postalCode', width:'100px'},
				{text: '${uiLabelMap.DAStateProvinceGeoId}', datafield: 'stateProvinceGeoId', width:'100px'},
				{text: '${uiLabelMap.DACountyGeoId}', datafield: 'countyGeoId', width:'100px'},
				{text: '${uiLabelMap.DACountryGeoId}', datafield: 'countryGeoId', width:'100px'},
				{text: '${uiLabelMap.DAContactMechPurposeTypeId}', datafield: 'contactMechPurposeTypeId', width:'200px'},
		]
	});
	$("#jqxContactMechGrid").on('rowselect', function (event) {
        var args = event.args;
        var row = $("#jqxContactMechGrid").jqxGrid('getrowdata', args.rowindex);
        var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['contactMechId'] + '</div>';
        $("#contactMechIdAdd").jqxDropDownButton('setContent', dropDownContent);
    });
    </#if>
</script>
<#--
, 
				 		validation: function (cell, value) {
                          	if (value == '') return true;
                          	var year = value.getFullYear();
                          	if (year >= 2015) {
                              	return {result: false, message: 'Ship Date should be before 1/1/2015'};
                          	}
                          	return true;
                  		}
-->
<#if hasPermissionCreate>
<#assign dataFieldProduct="[{name: 'productId', type: 'string'},
             	{name: 'internalName', type: 'string'},
             	{name: 'quantity', type: 'string'},
             	{name: 'quantityUomId', type: 'string'},
             	{name: 'productPackingUomId', type: 'string'},
             	{name: 'packingUomId', type: 'string'}
	 		 	]"/>
<#assign columnlistProduct="{text: '${uiLabelMap.DAProductId}', dataField: 'productId', width: '150px', align: 'center', editable: false},
				 	{text: '${uiLabelMap.ProductName}', dataField: 'internalName', align: 'center', editable: false},
				 	{ text: '${uiLabelMap.DAUom}', dataField: 'quantityUomId', width: '120px', columntype: 'dropdownlist', 
					 	cellsrenderer: function(row, column, value){
    						for (var i = 0 ; i < uomData.length; i++){
    							if (value == uomData[i].uomId){
    								return '<span title = ' + uomData[i].description +'>' + uomData[i].description + '</span>';
    							}
    						}
    						return '<span title=' + value +'>' + value + '</span>';
						},
					 	initeditor: function (row, cellvalue, editor) {
					 		var packingUomData = new Array();
							var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
							
							var itemSelected = data['quantityUomId'];
							var packingUomIdArray = data['packingUomId'];
							for (var i = 0; i < packingUomIdArray.length; i++) {
								var packingUomIdItem = packingUomIdArray[i];
								var row = {};
								if (packingUomIdItem.description == undefined || packingUomIdItem.description == '') {
									row['description'] = '' + packingUomIdItem.uomId;
								} else {
									row['description'] = '' + packingUomIdItem.description;
								}
								row['uomId'] = '' + packingUomIdItem.uomId;
								packingUomData[i] = row;
							}
					 		var sourceDataPacking =
				            {
				                localdata: packingUomData,
				                datatype: \"array\"
				            };
				            var dataAdapterPacking = new $.jqx.dataAdapter(sourceDataPacking);
				            editor.jqxDropDownList({ source: dataAdapterPacking, displayMember: 'description', valueMember: 'uomId'
				            	//renderer: function (index, label, value) {
						        //	return '[' + value + '] ' + label;
						        //}
				            });
				            
                          	//editor.jqxDropDownList({source: dataAdapterPacking, displayMember:'description', valueMember: 'uomId'});
				            editor.jqxDropDownList('selectItem', itemSelected);
                      	}
                 	},
				 	{text: '${uiLabelMap.DAExpireDate}', dataField: 'expireDate', width: '150px', editable: true, columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', filterable: false, sortable:false},
				 	{text: '${uiLabelMap.QuantityRequest}', dataField: 'quantity', width: '200px', align: 'center', cellsalign: 'right', filterable: false, sortable:false, columntype: 'numberinput', editable: true,
                     	validation: function (cell, value) {
                         	if (value < 0) {
                             	return { result: false, message: '${uiLabelMap.QuantityMustBeGreateThanZero}'};
                         	}
                         	return true;
                     	},
                     	createeditor: function (row, cellvalue, editor) {
                         	editor.jqxNumberInput({ decimalDigits: 0, digits: 10 });
                     	}
				 	}
				 "/>
<#--
<@jqGrid selectionmode="checkbox" idExisted="true" filtersimplemode="true" width="930" viewSize="5" pagesizeoptions="['5', '10', '15', '20', '25', '30', '50', '100']" 
		id="jqxgridProduct" dataField=dataFieldProduct columnlist=columnlistProduct clearfilteringbutton="true" showtoolbar="false" addrow="false" filterable="true" editable="true" 
	 	url="jqxGeneralServicer?sname=JQGetListProductSales" editmode="click" bindresize="false" jqGridMinimumLibEnable="false" offmode="true"/>
JQGetListRequirement&requirementTypeId=RETURN_PRODDIS_REQ
-->
<@jqGrid id="jqxgridProduct" idExisted="true" filtersimplemode="true" viewSize="5" pagesizeoptions="['5', '10', '15', '20', '25', '30', '50', '100']" 
		dataField=dataFieldProduct columnlist=columnlistProduct clearfilteringbutton="true" showtoolbar="false" addrow="false" editable="true" 
		url="jqxGeneralServicer?sname=JQGetListProductSales" filterable="true" width="930" bindresize="false" alternativeAddPopup="alterpopupWindow" addType="popup" 
		deleterow="false" offmode="true" editmode="click" selectionmode="checkbox"/>
</#if>		
