<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/delys/images/js/util/DateUtil.js" ></script>
<script type="text/javascript" src="/delys/images/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript">
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "DELI_ENTRY_STATUS"), null, null, null, false)/>
	var statusDataDE = new Array();
	<#list statuses as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("description",locale))>
		row['statusId'] = '${item.statusId}';
		row['description'] = '${description}';
		statusDataDE[${item_index}] = row;
	</#list>
//	<#assign parties = delegator.findList("PartyPersonPartyRole", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("roleTypeId", "CARRIER"), null, null, null, false)/>
//	var partyData = new Array();
//	<#list parties as item>
//		<#assign isEmpl = delegator.findOne("PartyRole", {"partyId" : item.partyId, "roleTypeId" : "EMPLOYEE"}, false)!>
//		<#if isEmpl?exists>
//			var row = {};
//			row['partyId'] = '${item.partyId?if_exists}';
//			row['partyName'] = '${item.firstName?if_exists} ${item.middleName?if_exists} ${item.lastName?if_exists} [${item.partyId?if_exists}]';
//			partyData[${item_index}] = row;
//		</#if>	
//	</#list>
	
	<#assign facis = delegator.findList("Facility", null, null, null, null, false)>
	var faciData = new Array();
	<#list facis as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("facilityName",locale)?if_exists)/>
		row['facilityId'] = '${item.facilityId?if_exists}';
		row['description'] = '${description?if_exists}';
		faciData[${item_index}] = row;
	</#list>
	
//	<#assign vehicles = delegator.findList("Vehicle", null, null, null, null, false)>
//	var vehicleData = new Array();
//	<#list vehicles as item>
//		var row = {};
//		<#assign description = StringUtil.wrapString(item.vehicleName?if_exists)/>
//		row['vehicleId'] = '${item.vehicleId?if_exists}';
//		row['description'] = '${description?if_exists}';
//		vehicleData[${item_index}] = row;
//	</#list>
	
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var uomData = new Array();
	<#list uoms as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get('description', locale)?if_exists)/>
		row['weightUomId'] = '${item.uomId?if_exists}';
		row['description'] = '${description?if_exists}';
		row['abbreviation'] = '${item.abbreviation?if_exists}';
		uomData[${item_index}] = row;
	</#list>
	
</script>
<#assign dataField="[{ name: 'deliveryEntryId', type: 'string' },
					 { name: 'description', type: 'string'},
					 { name: 'fromDate', type: 'date'},
					 { name: 'thruDate', type: 'date'},
					 { name: 'weight', type: 'number'},
					 { name: 'facilityId', type: 'string'},
					 { name: 'weightUomId', type: 'string'},
					 { name: 'statusId', type: 'string'}
					 ]"/>
<#assign columnlist="
					{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true, 
				        groupable: false, draggable: false, resizable: false,
				        datafield: '', columntype: 'number', width: 50,
				        cellsrenderer: function (row, column, value) {
				            return '<span style=margin:4px;>' + (value + 1) + '</span>';
				        }
					},					
					{ text: '${uiLabelMap.deliveryEntryId}', datafield: 'deliveryEntryId', width: 150, editable: false, pinned: true,
						cellsrenderer: function(row, column, value){
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							return '<span><a href=/delys/control/deliveryEntryDetail?deliveryEntryId=' + value + '&facilityId='+data.facilityId+'&fromDate='+data.fromDate.getTime()+'>' + value + '</a></span>'
						}
					 },
					 { text: '${uiLabelMap.FacilityFrom}', datafield: 'facilityId', width: 200, editable: false,
						 cellsrenderer: function(row, column, value){
							 for(var i = 0; i < faciData.length; i++){
								 if(faciData[i].facilityId == value){
									 return '<span title=' + value + '>' + faciData[i].description + '</span>';
								 }
							 }
						 }
					 },
					 { text: '${uiLabelMap.fromDate}', datafield: 'fromDate', width: 150, cellsformat:'dd/MM/yyyy', editable: false},
					 { text: '${uiLabelMap.weight}', datafield: 'weight', width: 200, editable: false,  
						cellsrenderer: function(row, colum, value){
						   	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						   	var weightUomId = data.weightUomId;
						   	var weightUomIdAbb = '';
						   	for(var i = 0; i < uomData.length; i++){
								 if(uomData[i].weightUomId == weightUomId){
									 weightUomIdAbb = uomData[i].abbreviation;
								 }
							 }
					        return '<span style=\"text-align: right\">' + value +' (' + weightUomIdAbb +  ')</span>';
			        	}
					 },"/>
<#if security.hasPermission("DELIVERY_ADMIN", userLogin)> 
	<#assign columnlist= columnlist + "
					 { text: '${uiLabelMap.statusId}', datafield: 'statusId', width: 150, editable: true, columntype: 'dropdownlist',
						 cellsrenderer: function(row, column, value){
							 for(var i = 0; i < statusDataDE.length; i++){
								 if(statusDataDE[i].statusId == value){
									 return '<span title=' + value + '>' + statusDataDE[i].description + '</span>';
								 }
							 }
							 return value;
						 },
						 cellbeginedit: function (row, datafield, columntype) {
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								if(data.statusId == 'DELI_ENTRY_COMPLETED' || data.statusId == 'DELI_ENTRY_DELIVERED' || data.statusId == 'DELI_ENTRY_CANCELED'){
									tmpEditable = true;
									return false;
								}else{
									tmpEditable = false;
									return true;
								}
						 },
						 createeditor: function(row, cellvalue, editor){
							 var statusList = new Array();
							 switch (cellvalue) {
								case 'DELI_ENTRY_CREATED':
								     var row = {};
                                     row['statusId'] = 'DELI_ENTRY_CREATED';
                                     row['description'] = '${uiLabelMap.Created}';
                                     statusList[0] = row;
									 row = {};
									 row['statusId'] = 'DELI_ENTRY_SCHEDULED';
									 row['description'] = '${uiLabelMap.Scheduled}';
									 statusList[1] = row;
									 row = {};
									 row['statusId'] = 'DELI_ENTRY_CANCELED';
									 row['description'] = '${uiLabelMap.Canceled}';
									 statusList[2] = row;
									 break;
								case 'DELI_ENTRY_SCHEDULED':
								     var row = {};
                                     row['statusId'] = 'DELI_ENTRY_SCHEDULED';
                                     row['description'] = '${uiLabelMap.Scheduled}';
                                     statusList[0] = row;
									 row = {};
									 row['statusId'] = 'DELI_ENTRY_SHIPED';
									 row['description'] = '${uiLabelMap.Shipped}';
									 statusList[1] = row;
									 break;
								case 'DELI_ENTRY_SHIPED':
								     var row = {};
                                     row['statusId'] = 'DELI_ENTRY_SHIPED';
                                     row['description'] = '${uiLabelMap.Shipped}';
                                     statusList[0] = row;
									 row = {};
									 row['statusId'] = 'DELI_ENTRY_DELIVERED';
									 row['description'] = '${uiLabelMap.Delivered}';
									 statusList[1] = row;
									 break;
								default:
									break;
								}
							 editor.jqxDropDownList({source: statusList, valueMember: 'statusId', displayMember: 'description'});
						 }
					 },
				 "/>
<#else>
		 <#assign columnlist= columnlist + "
		 	{ text: '${uiLabelMap.statusId}', datafield: 'statusId', width: 150, editable: false, columntype: 'dropdownlist',
			 cellsrenderer: function(row, column, value){
				 for(var i = 0; i < statusDataDE.length; i++){
					 if(statusDataDE[i].statusId == value){
						 return '<span title=' + value + '>' + statusDataDE[i].description + '</span>';
					 }
				 }
				 return value;
			 }
		 	},"/>
</#if>
		 <#assign columnlist= columnlist + "
					 { text: '${uiLabelMap.thruDate}', datafield: 'thruDate', width: 150, cellsformat:'dd/MM/yyyy', editable: false},
					 { text: '${uiLabelMap.description}', datafield: 'description', minwidth: 200, editable: false}
					 "/>
<#if security.hasPermission("DELIVERY_ADMIN", userLogin)> 
	 <@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist sortable="true" sortdirection="desc" defaultSortColumn="fromDate" clearfilteringbutton="true" showtoolbar="true" addrow="true" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true"
		 url="jqxGeneralServicer?sname=JQListDeliveryEntry" addrefresh="true" editrefresh="true" 
		 updateUrl="jqxGeneralServicer?sname=updateDeliveryEntry&jqaction=U" editColumns="deliveryEntryId;statusId"
		 createUrl="jqxGeneralServicer?sname=createDeliveryEntry&jqaction=C"
		 addColumns="description;fromDate(java.sql.Timestamp);facilityId;weightUomId;listShipments(java.util.List);"
		/>
<#else>
	<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist sortable="true" sortdirection="desc" defaultSortColumn="fromDate" clearfilteringbutton="true" showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true"
	 url="jqxGeneralServicer?sname=JQListDeliveryEntry" addrefresh="true" editrefresh="true" 
	 updateUrl="" editColumns="" createUrl=""
	/>
</#if>
<div id="alterpopupWindow" style="display:none;">
	<div>${uiLabelMap.accCreateNew}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
	    	<h4 class="row header smaller lighter blue" style="margin-right:25px !important;margin-left: 20px !important;font-weight:500;line-height:20px;font-size:18px;">
	    			${uiLabelMap.DeliveryEntry}
			</h4>
			<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid margin-bottom10">
					    <div class="span5" style="text-align: right;">${uiLabelMap.FacilityFrom}:</div>
					    <div class="span7"><div id="facilityIdAdd" class="green-label"></div></div>
				    </div>
				    <div class="row-fluid margin-bottom10">
					    <div class="span5" style="text-align: right;">${uiLabelMap.WeightUomId}:</div>
					    <div class="span7"><div id="weightUomIdAdd" class="green-label"></div></div>
				    </div>
			 	</div>
			 	<div class="span6">
				 	<div class="row-fluid margin-bottom10">	
				 	    <div class="span5" style="text-align: right;">${uiLabelMap.EstimatedShipDate}:</div>
				 	    <div class="span7"><div id="fromDateAdd"></div></div>
			 	    </div>
			 	   <div class="row-fluid margin-bottom10">	
				 	    <div class="span5" style="text-align: right;">${uiLabelMap.description}:</div>
				 	    <div class="span7">
				 	    	<div style="width: 195px; display: inline-block; margin-bottom:3px"><input id="descriptionAdd"></input></div><a onclick="showEditor()" style="display: inline-block"><i style="padding-left: 20px;" class="icon-edit"></i></a>
				 	    </div>
		 	    	</div>
		        </div>
		        <div class="row-fluid">
			 		<div class='span12'><div style="margin-left: 20px;margin-top:10px;"><#include "listShipmentFilter.ftl"/></div></div>
			    </div>
	        </div>
	        <div class="form-action">
	            <div class='row-fluid'>
	                <div class="span12 margin-top20" style="margin-bottom:10px;">
	                    <button id="addCancelButton" class='btn btn-danger form-action-button pull-right' style="margin-right:20px !important;"><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	                    <button id="addSaveButton" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	                </div>
	            </div>
	        </div>
	    </div>
	</div>
</div>
<div id="jqxEditorWindow" style="display: none">
	<div id="windowHeader">
		<span>
		    ${uiLabelMap.Description}
		</span>
	</div>
	<div style="overflow: hidden;" id="windowContent">
		<textarea id="editor">
		</textarea>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelButton" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="okButton" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
	//Create theme
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	$("#jqxEditorWindow").jqxWindow({
		maxWidth: 640, minWidth: 640, minHeight: 360, maxHeight: 360, resizable: true,  isModal: true, autoOpen: false, initContent : function(){
			$('#editor').jqxEditor({
	            height: '85%',
	            width: '100%',
	            theme: theme,
	        });
		},
	});
	function showEditor(){
		$("#jqxEditorWindow").jqxWindow('open');
	}
	$("#okButton").click(function () {
		var des = $('#editor').val();
		var tmp = des.substring(5, des.length - 6);
		$("#descriptionAdd").val(tmp);
		$("#jqxEditorWindow").jqxWindow('close');
	});
	$("#cancelButton").click(function () {
		$("#jqxEditorWindow").jqxWindow('close');
	});
	//Create description
	$("#descriptionAdd").jqxInput({placeHolder: ". . .", width: 195, theme: theme, height: '21px'});
	
	//Create fromDate
	$("#fromDateAdd").jqxDateTimeInput({width: 200, theme: theme, height: '24px'});
	
	//Create facilityId
	$("#facilityIdAdd").jqxDropDownList({width: 200, selectedIndex: 0, theme: theme, source: faciData, valueMember:'facilityId', displayMember:'description', height: '24px'});
	
	//Create Vehicle 
//	$("#vehicleId").jqxDropDownList({width: 200, selectedIndex: 0, theme: theme, source: vehicleData, valueMember:'vehicleId', displayMember:'description'});
	
	//Create party carrier
//	$("#partyId").jqxComboBox({ source: partyData, selectedIndex: 0, width: '200', height: '25', displayMember: 'partyName', valueMember:'partyId'});
	$("#weightUomIdAdd").jqxDropDownList({width: 200, selectedIndex: 0, theme: theme, source: uomData, valueMember:'weightUomId', displayMember:'description', height: '24px'});
	// Set default to Kilogram
	for(i = 0; i < uomData.length; i++){
	    if(uomData[i].weightUomId == 'WT_kg'){
	        $("#weightUomIdAdd").jqxDropDownList('selectItem', uomData[i].weightUomId);
	        break;
	    }
	}
	$('#alterpopupWindow').on('open', function (event) {
		initGridjqxgridfilterGrid();
		var tmpS = $("#jqxgridfilterGrid").jqxGrid('source');
		var curFacilityId = "${parameters.facilityId?if_exists}";
		var curFromDate = "${parameters.fromDate?if_exists}";
		if (curFacilityId == "" || curFacilityId == null){
	 		curFacilityId = $("#facilityIdAdd").val();
	 	}
	 	if (curFromDate == "" || curFromDate == null){
	 		curFromDate = $("#fromDateAdd").jqxDateTimeInput('getDate').getTime();
	 	}
	 	tmpS._source.url = "jqxGeneralServicer?sname=JQGetListFilterShipment&deliveryEntryId=${parameters.deliveryEntryId?if_exists}&facilityId="+curFacilityId+"&fromDate="+curFromDate;
	 	$("#jqxgridfilterGrid").jqxGrid('source', tmpS);
	});
	$("#facilityIdAdd").on('change', function(event){
		var tmpS = $("#jqxgridfilterGrid").jqxGrid('source');
	 	var curFacilityId = $("#facilityIdAdd").val();
	 	var curFromDate = $("#fromDateAdd").jqxDateTimeInput('getDate').getTime();
	 	tmpS._source.url = "jqxGeneralServicer?sname=JQGetListFilterShipment&deliveryEntryId=${parameters.deliveryEntryId?if_exists}&facilityId="+curFacilityId+"&fromDate="+curFromDate;
	 	$("#jqxgridfilterGrid").jqxGrid('source', tmpS);
	});
	$("#fromDateAdd").on('change', function(event){
		var tmpS = $("#jqxgridfilterGrid").jqxGrid('source');
	 	var curFacilityId = $("#facilityIdAdd").val();
	 	var curFromDate = $("#fromDateAdd").jqxDateTimeInput('getDate').getTime();
	 	tmpS._source.url = "jqxGeneralServicer?sname=JQGetListFilterShipment&deliveryEntryId=${parameters.deliveryEntryId?if_exists}&facilityId="+curFacilityId+"&fromDate="+curFromDate;
	 	$("#jqxgridfilterGrid").jqxGrid('source', tmpS);
	});
	
	$("#addCancelButton").click(function () {
		$('#jqxgridfilterGrid').jqxGrid('clearSelection');
		$('#alterpopupWindow').jqxWindow('close');
	});
	//add row when the user clicks the 'Save' button.
    $("#addSaveButton").click(function () {
    	var row;
    	var selectedIndexs = $('#jqxgridfilterGrid').jqxGrid('getselectedrowindexes');
    	if(selectedIndexs.length == 0 || selectedIndexs == undefined){
    		bootbox.dialog("${uiLabelMap.DAYouNotYetChooseProduct}!", [{
                "label" : "OK",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
            return false;
    	}else{
    		var listShipments = new Array();
    		for(var i = 0; i < selectedIndexs.length; i++){
    			var data = $('#jqxgridfilterGrid').jqxGrid('getrowdata', selectedIndexs[i]);
    			var map = {};
    			map['shipmentId'] = data.shipmentId;
    			listShipments[i] = map;
    		}
    		listShipments = JSON.stringify(listShipments);
            row = { 
            		description:$('#descriptionAdd').val(),
            		weightUomId:$('#weightUomIdAdd').val(),
            		facilityId:$('#facilityIdAdd').val(),
            		fromDate: new Date($('#fromDateAdd').jqxDateTimeInput('getDate')),
            		listShipments:listShipments
            	  };
    	    $("#jqxgrid").jqxGrid('addRow', null, row, "first");
            // select the first row and clear the selection.
    	   	//$("#jqxgrid").jqxGrid("updatebounddata");
            $("#alterpopupWindow").jqxWindow('close');
    	}
    });
</script>
<style>
	<#--
    #popupDeliveryDetailWindow #addSaveButton, #popupDeliveryDetailWindow #addCancelButton {
        padding: 5px 10px;
        border-radius: 5px;
        -webkit-border-radius: 5px;
        -moz-border-radius: 5px;
    } -->
    .bootbox{
    	  z-index: 20001 !important;
    	 }
	 .modal-backdrop{
	  z-index: 20000 !important;
	 }
</style> 
<script>
	<#assign shipmentTypes = delegator.findList("ShipmentType", null, null, null, null, false) />
	var shipmentTypeData = new Array();
	<#list shipmentTypes as item>
	 	<#assign description = StringUtil.wrapString(item.get("description",locale))>
		var row = {};
		row['shipmentTypeId'] = '${item.shipmentTypeId}';
		row['description'] = '${description?if_exists}';
		shipmentTypeData[${item_index}] = row;
	</#list>
	
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["SHIPMENT_STATUS","PURCH_SHIP_STATUS"]), null, null, null, false) />
	var statusData = new Array();
	<#list statuses as item>
	    <#assign description = StringUtil.wrapString(item.get("description",locale))>
		var row = {};
		row['statusId'] = '${item.statusId}';
		row['description'] = "${description?if_exists}";
		statusData[${item_index}] = row;
	</#list>
	
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var weightUomData = new Array();
	<#list weightUoms as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.abbreviation) />
		row['uomId'] = '${item.uomId}';
		row['description'] = '${description?if_exists}';
		weightUomData[${item_index}] = row;
	</#list>
	//Create theme
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	//Create popup window
	var wheight = '';
	if(typeof windowheight !== 'undefined'){
	    wheight = windowheight;
	}else{
	    windowheight = '525px';
	}
	$("#alterpopupWindow").jqxWindow({maxWidth: '1000px',width: '1200px', minHeight: '525px', resizable: true,  isModal: true, autoOpen: false, cancelButton:$('#alterCancel'), modalOpacity: 0.7});
</script>