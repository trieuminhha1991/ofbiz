<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script>
	//Prepare for product data
	<#assign ownerPartyId = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	<#assign products = delegator.findList("Product", null, null, null, null, false) />
	var mapProductData = {
			<#if products?exists>
				<#list products as item>
					<#assign s1 = StringUtil.wrapString(item.get('productName', locale)?if_exists)/>
					"${item.productId?if_exists}": "${s1}",
				</#list>
			</#if>	
	};
	
	<#assign facilitys = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("ownerPartyId", ownerPartyId)), null, null, null, false) />
	var mapFacilityData = {  
			<#if facilitys?exists>
				<#list facilitys as item>
					"${item.facilityId?if_exists}": '${StringUtil.wrapString(item.get('facilityName', locale)?if_exists)}',
				</#list>
			</#if>	
	};
	
	<#assign enumerationTypes = delegator.findList("EnumerationType", null, null, null, null, false) />
	var mapEnumerationTypeData = {
			<#if enumerationTypes?exists>
				<#list enumerationTypes as item>
					<#assign s1 = StringUtil.wrapString(item.get('description', locale)?if_exists)/>
					"${item.enumTypeId?if_exists}": "${s1}",
				</#list>
			</#if>	
	};
	
	<#assign enumerations = delegator.findList("Enumeration", null, null, null, null, false) />
	var mapEnumerationData = {
			<#if enumerations?exists>
				<#list enumerations as item>
					<#assign s1 = StringUtil.wrapString(item.get('description', locale)?if_exists)/>
					"${item.enumId?if_exists}": "${s1}",
				</#list>
			</#if>	
	};
	
	var enumerationDataSoure = [
   		<#if enumerations?exists>
   			<#list enumerations as item>
   				{
   					enumId: "${item.enumId?if_exists}",
   					enumTypeId: "${item.enumTypeId?if_exists}",
   				},
   			</#list>
   		</#if>
   	];

	
	<#assign invoiceItemTypes = delegator.findList("InvoiceItemType", null, null, null, null, false) />
	var mapInvoiceItemTypeData = {
			<#if invoiceItemTypes?exists>
				<#list invoiceItemTypes as item>
					<#assign s1 = StringUtil.wrapString(item.get('description', locale)?if_exists)/>
					"${item.invoiceItemTypeId?if_exists}": "${s1}",
				</#list>
			</#if>	
	};
</script>
<div id="contentNotificationAddSuccess">
</div>
<#assign dataField="[
				{ name: 'enumId', type: 'string'},
				{ name: 'invoiceItemTypeId', type: 'string'},
				{ name: 'fromDate', type: 'date', other: 'Timestamp' },
				{ name: 'thruDate', type: 'date', other: 'Timestamp' },
				{ name: 'description', type: 'string'},
			]"/>
<#assign columnlist="
				{
				    text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (value + 1) + '</div>';
				    }
				},
				{ text: '${uiLabelMap.LogReasonReturnId}', datafield: 'enumId', align: 'center', width: 250,
					cellsrenderer: function (row, column, value){
						if(value){
							return '<span>' + mapEnumerationData[value] + '<span>';
						}
					}
				}, 
				{ text: '${uiLabelMap.LogInvoiceItemTypeId}', datafield: 'invoiceItemTypeId', align: 'center', width: 250,
					cellsrenderer: function (row, column, value){
						if(value){
							return '<span>' + mapInvoiceItemTypeData[value] + '<span>';
						}
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.FromDate)}', datafield: 'fromDate', align: 'left', columntype: 'datetimeinput', editable: true, cellsformat: 'dd/MM/yyyy', filtertype:'range', width: '150', align: 'center', 
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.ThruDate)}', datafield: 'thruDate', align: 'left', columntype: 'datetimeinput', editable: true, cellsformat: 'dd/MM/yyyy', filtertype:'range', width: '150', align: 'center', 
				},
				{ text: '${uiLabelMap.Description}', datafield: 'description', align: 'center'
				},
			"/>

<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true"
	id="jqxgirdEnumInvoiceType" addrefresh="true" filterable="true"
	url="jqxGeneralServicer?sname=JQGetListEnumInvoiceItemType"
	customTitleProperties="LogConfigImportExportOther"
	customcontrol1="fa fa-plus-circle@${uiLabelMap.AddNew}@javascript:addEnumInvoiceItemType()"
	mouseRightMenu="true" contextMenuId="menuEnumInvoiceItemType" />	
				
<div id="alterpopupWindow" class="hide">
	<div class="row-fluid">
		${uiLabelMap.LogAddConfigImportExportOther}
	</div>
	<div class='form-window-container'>
		<div class="row-fluid">
			<input type="hidden" id="enumTypeIdInput"></input>
			<input type="hidden" id="enumIdInput"></input> 
			<input type="hidden" id="invoiceItemTypeIdInput"></input>
			<div class="span6">
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<label class="asterisk"> ${uiLabelMap.LogEnumTypeId}: </label>
					</div>
					<div class="span7">
						<div id="enumTypeId" style="width: 100%" class="green-label">
							<div id="jqxgridEnumTypeId">
				            </div>
						</div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<label class="asterisk"> ${uiLabelMap.LogInvoiceItemTypeId}: </label>
					</div>
					<div class="span7">
						<div id="invoiceItemTypeId" style="width: 100%" class="green-label">
							<div id="jqxgridInvoiceItemTypeId">
				            </div>
						</div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<label class="asterisk"> ${uiLabelMap.FromDate}: </label>
					</div>
					<div class="span7">
						<div id="fromDate" style="width: 100%">
						</div>
					</div>
				</div>
			</div> 
			<div class="span6 no-left-margin">
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<label class="asterisk"> ${uiLabelMap.LogReasonReturnId}: </label>
					</div>
					<div class="span7">
						<div id="enumId" style="width: 100%" class="green-label">
							<div id="jqxgridEnumId">
				            </div>
						</div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<label> ${uiLabelMap.Description}: </label>
					</div>
					<div class="span7">
						<input id="description" style="width: 100%">
						</input>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<label> ${uiLabelMap.ThruDate}: </label>
					</div>
					<div class="span7">
						<div id="thruDate" style="width: 100%">
						</div>
					</div>
				</div>
			</div>
	    </div>
		<div class="form-action">
	        <div class='row-fluid'>
	            <div class="span12 margin-top20" style="margin-bottom:10px;">
	                <button id="addButtonCancel" class='btn btn-danger form-action-button pull-right' style="margin-right:20px !important;"><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	                <button id="addButtonSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	            </div>
	        </div>
	    </div>
	</div>
</div>	

<div id='menuEnumInvoiceItemType' style="display:none;">
	<ul>
	    <li><i class="fa-trash"></i>&nbsp;&nbsp;${uiLabelMap.PODeleteRowGird}</li>
	    <li><i class="fa fa-pencil-square-o"></i>&nbsp;&nbsp;${uiLabelMap.Edit}</li>
	</ul>
</div>
<div id="jqxNotificationAddSuccess" >
	<div id="notificationAddSuccess"> 
	</div>
</div>
<script>
	var enumerationTypeData = [
	    {
	    	enumTypeId: 'EXPORT_REASON',
	    	description: 'Lý do xuất kho'
	    },
	    {
	    	enumTypeId: 'RECEIVE_REASON',
	    	description: 'Lý do nhập kho'
	    }
	];

	$("#jqxNotificationAddSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationAddSuccess", opacity: 0.9, autoClose: true, template: "success" });
	
	$("#enumTypeIdInput").jqxInput(); 
	$("#enumTypeId").jqxDropDownButton();  
	$("#enumIdInput").jqxInput();  
	$("#enumId").jqxDropDownButton();
	$("#invoiceItemTypeId").jqxDropDownButton(); 
	$("#invoiceItemTypeIdInput").jqxInput(); 
	$("#description").jqxInput({ width: 195}); 
	$("#fromDate").jqxDateTimeInput(); 
	$("#thruDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy', showFooter:true,
	     clearString:'Clear'});
	$('#fromDate').on('valueChanged', function (event){  
		var jsDate = event.args.date; 
		$('#thruDate').jqxDateTimeInput('setMinDate', jsDate);
	});
	$("#alterpopupWindow").jqxWindow({
		maxWidth: 900, minWidth: 830, height:215 ,minHeight: 100, maxHeight: 1200, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#addButtonCancel"), modalOpacity: 0.7, theme:'olbius'           
	});
	 
	$('#alterpopupWindow').on('open', function (event) { 
		var fromDate= $('#fromDate').jqxDateTimeInput('getDate'); 
		$('#thruDate').jqxDateTimeInput('setMinDate', fromDate);
	});
	
	var productIdEditInput = ""; 
	var facilityIdEditInput = ""; 
	$("#menuEnumInvoiceItemType").jqxMenu({ width: 170, autoOpenPopup: false, mode: 'popup', theme: 'olbius'});
	$("#menuEnumInvoiceItemType").on('itemclick', function (event) {
		requirementIdData = "";
        var args = event.args;
        var rowindex = $("#jqxgirdEnumInvoiceType").jqxGrid('getselectedrowindex');
        var dataRecord = $("#jqxgirdEnumInvoiceType").jqxGrid('getrowdata', rowindex);
        var enumId = dataRecord.enumId;
        var invoiceItemTypeId = dataRecord.invoiceItemTypeId;
        var fromDate = dataRecord.fromDate;
        var thruDate = dataRecord.thruDate;
        var description = dataRecord.description;
        if ($.trim($(args).text()) == "${StringUtil.wrapString(uiLabelMap.PODeleteRowGird)}") {
        	bootbox.dialog("${uiLabelMap.ConfirmDelete}", 
				[{"label": "${StringUtil.wrapString(uiLabelMap.wgcancel)}", 
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		            "callback": function() {bootbox.hideAll();}
		        }, 
		        {"label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
		            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		            "callback": function() {
		            	deleteEnumerationInvoiceItemType(enumId, invoiceItemTypeId, fromDate);
		        }
	        }]);
        }
        else{ 
        	$('#thruDate').jqxDateTimeInput('setMinDate', fromDate);
        	editEnumerationInvoiceItemType(enumId, invoiceItemTypeId, fromDate, thruDate, description);
        }
    });
	
	$('#alterpopupWindow').jqxValidator({
		rules: 
		[
	        { input: '#enumTypeId', message: '${uiLabelMap.POCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur', 
        	   rule: function () {
        		    var enumTypeIdInput = $('#enumTypeIdInput').val();
            	    if(enumTypeIdInput == ""){
            	    	return false; 
            	    }else{
            	    	return true; 
            	    }
            	    return true; 
        	    }
            }, 
	        { input: '#enumId', message: '${uiLabelMap.POCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur', 
        	   rule: function () {
        		    var enumIdInput = $('#enumIdInput').val();
            	    if(enumIdInput == ""){
            	    	return false; 
            	    }else{
            	    	return true; 
            	    }
            	    return true; 
        	    }
            },
            { input: '#invoiceItemTypeId', message: '${uiLabelMap.POCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur', 
         	   rule: function () {
         		    var invoiceItemTypeIdInput = $('#invoiceItemTypeIdInput').val();
             	    if(invoiceItemTypeIdInput == ""){
             	    	return false; 
             	    }else{
             	    	return true; 
             	    }
             	    return true; 
         	    }
             },
             { input: '#fromDate', message: '${uiLabelMap.POCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur', 
           	   rule: function () {
           		    var fromDate = $('#fromDate').val();
               	    if(fromDate == ""){
               	    	return false; 
               	    }else{
               	    	return true; 
               	    }
               	    return true; 
           	    }
             },
	    ]
	});
	
	
	var sourceEnumTypeId =
	{
	     datafields:[{name: 'enumTypeId', type: 'string'},
	                 {name: 'description', type: 'string'},	
	 				],
	     localdata: enumerationTypeData,
	     datatype: "array",
	};
	var dataAdapterEnumTypeId = new $.jqx.dataAdapter(sourceEnumTypeId);
	$("#jqxgridEnumTypeId").jqxGrid({
	     source: dataAdapterEnumTypeId,
	     filterable: true,
	     showfilterrow: true,
	     theme: theme,
	     autoheight:true,
	     pageable: true, 
	     columns: [{text: '${uiLabelMap.LogEnumTypeId}', datafield: 'enumTypeId'},
	               {text: '${uiLabelMap.Description}', datafield: 'description'},
	     		   ]
	 });
	
	 $("#jqxgridEnumTypeId").on('rowselect', function (event) {
        var args = event.args;
        var row = $("#jqxgridEnumTypeId").jqxGrid('getrowdata', args.rowindex);
        var dropDownContent = row['enumTypeId'];
        if(mapEnumerationTypeData[row['enumTypeId']] != undefined){
        	dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ mapEnumerationTypeData[row['enumTypeId']] +'</div>';
        }
        $('#enumTypeId').jqxDropDownButton('setContent', dropDownContent);
        $('#enumTypeIdInput').val(row['enumTypeId']);
    });
	 
	var enumerationData = [];
	$('#enumTypeId').on('close', function () { 
		var enumTypeId = $('#enumTypeIdInput').val();
		$('#enumIdInput').val("");
		$('#jqxgridEnumId').jqxGrid('clearselection');
		$('#enumId').jqxDropDownButton('setContent', "");
		$('#invoiceItemTypeIdInput').val("");
		$('#jqxgridInvoiceItemTypeId').jqxGrid('clearselection');
		$('#invoiceItemTypeId').val("");
		if(enumTypeId != null && enumTypeId != ''){
			$.ajax({
				url: "loadEnumerationByEnumTypeId",
				type: "POST",
				data: {enumTypeId: enumTypeId},
				dataType: "json",
				success: function(data) {
				}
			}).done(function(data) { 
				enumerationData = data["listEnum"];
				var sourceEnumId =
				{
				     datafields:[{name: 'enumId', type: 'string'},
				                 {name: 'description', type: 'string'},	
				 				],
				     localdata: enumerationData,
				     datatype: "array",
				};
				var dataAdapterEnumId = new $.jqx.dataAdapter(sourceEnumId);
				$("#jqxgridEnumId").jqxGrid({
				     source: dataAdapterEnumId,
				     filterable: true,
				     showfilterrow: true,
				     theme: theme,
				     autoheight:true,
				     pageable: true, 
				     columns: [
				              	{text: '${uiLabelMap.LogEnumId}', datafield: 'enumId', width: '180'},
				              	{text: '${uiLabelMap.Description}', datafield: 'description'},
				     		  ]
				 });
			});
		}
	}); 
	
	$("#jqxgridEnumId").on('rowselect', function (event) {
        var args = event.args;
        var row = $("#jqxgridEnumId").jqxGrid('getrowdata', args.rowindex);
        var dropDownContent = row['enumId'];
        if(mapEnumerationData[row['enumId']] != undefined){
        	dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ mapEnumerationData[row['enumId']] +'</div>';
        }
        $('#enumId').jqxDropDownButton('setContent', dropDownContent);
        $('#enumIdInput').val(row['enumId']);
    }); 
	
	var invoiceItemTypeData = [];
	$('#enumId').on('close', function () { 
		var enumTypeId = $('#enumTypeIdInput').val();
		$('#invoiceItemTypeIdInput').val("");
		$('#jqxgridInvoiceItemTypeId').jqxGrid('clearselection');
		$('#invoiceItemTypeId').val("");
		if(enumTypeId != null && enumTypeId != ''){
			$.ajax({
				url: "loadInvoiceItemType",
				type: "POST",
				data: {enumTypeId: enumTypeId},
				dataType: "json",
				success: function(data) {
				}
			}).done(function(data) { 
				invoiceItemTypeData  = data["listInvoiceItemType"];
				var sourceInvoiceItemType =
				{
				     datafields:[{name: 'invoiceItemTypeId', type: 'string'},
				                 {name: 'description', type: 'string'},	
				 				],
				     localdata: invoiceItemTypeData,
				     datatype: "array",
				};
				var dataAdapterInvoiceItemType = new $.jqx.dataAdapter(sourceInvoiceItemType);
				$("#jqxgridInvoiceItemTypeId").jqxGrid({
				     source: dataAdapterInvoiceItemType,
				     filterable: true,
				     showfilterrow: true,
				     theme: theme,
				     autoheight:true,
				     pageable: true, 
				     columns: [{text: '${uiLabelMap.LogInvoiceItemTypeId}', datafield: 'invoiceItemTypeId', width: '180'},
				               {text: '${uiLabelMap.Description}', datafield: 'description'},
				     		   ]
				 });
			});
		}
	}); 
	
	$("#jqxgridInvoiceItemTypeId").on('rowselect', function (event) {
        var args = event.args;
        var row = $("#jqxgridInvoiceItemTypeId").jqxGrid('getrowdata', args.rowindex);
        var dropDownContent = row['invoiceItemTypeId'];
        if(mapInvoiceItemTypeData[row['invoiceItemTypeId']] != undefined){
        	dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ mapInvoiceItemTypeData[row['invoiceItemTypeId']] +'</div>';
        }
        $('#invoiceItemTypeId').jqxDropDownButton('setContent', dropDownContent);
        $('#invoiceItemTypeIdInput').val(row['invoiceItemTypeId']);
    });
	
	var checkUpdateInsert = false;
	function addEnumInvoiceItemType(){
		checkUpdateInsert = false;
		$('#alterpopupWindow').jqxWindow('setTitle', '${StringUtil.wrapString(uiLabelMap.LogAddConfigImportExportOther)}');
    	$("#alterpopupWindow").jqxWindow('open'); 
	}
	
	$("#addButtonSave").click(function () {
		var validate = $('#alterpopupWindow').jqxValidator('validate');
		if(validate != false){
			var enumId = $('#enumIdInput').val();
			var invoiceItemTypeId = $('#invoiceItemTypeIdInput').val();
			var description = $('#description').val();
			var fromDate= $('#fromDate').jqxDateTimeInput('getDate'); 
			var thruDate= $('#thruDate').jqxDateTimeInput('getDate');   
			var fromDateTime = fromDate.getTime();
			var thruDateTime = thruDate;
			if(thruDate != null){
				thruDateTime = thruDate.getTime();
			}
			if(checkUpdateInsert == false){
				bootbox.dialog("${uiLabelMap.POAreYouSureAddItem}", 
						[{"label": "${StringUtil.wrapString(uiLabelMap.wgcancel)}", 
							"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
				            "callback": function() {bootbox.hideAll();}
				        }, 
				        {"label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
				            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
				            "callback": function() {
				            	addEnumerationInvoiceItemType(enumId, invoiceItemTypeId, description, fromDateTime, thruDateTime);
				        }
			        }]);
			}
			if(checkUpdateInsert == true){
				bootbox.dialog("${uiLabelMap.AreYouSureUpdate}", 
						[{"label": "${StringUtil.wrapString(uiLabelMap.wgcancel)}", 
							"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
				            "callback": function() {bootbox.hideAll();}
				        }, 
				        {"label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
				            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
				            "callback": function() {
				            	addEnumerationInvoiceItemType(enumId, invoiceItemTypeId, description, fromDateTime, thruDateTime);
				        }
			        }]);
			}
		}
	});
	
	function addEnumerationInvoiceItemType(enumId, invoiceItemTypeId, description, fromDate, thruDate){
		$.ajax({
			url: "addEnumerationInvoiceItemType",
			type: "POST",
			data: {enumId: enumId, invoiceItemTypeId: invoiceItemTypeId, description: description, fromDate: fromDate, thruDate: thruDate},
			dataType: "json", 
			success: function(data) { 
			}
		}).done(function(data) { 
			var value = data["value"];
			$('#jqxgirdEnumInvoiceType').jqxGrid('updatebounddata');
	    	$('#alterpopupWindow').jqxWindow('close');
	    	if(value == "update"){
	    		$("#notificationAddSuccess").text('${StringUtil.wrapString(uiLabelMap.UpdateSuccessfully)}');
				$("#jqxNotificationAddSuccess").jqxNotification('open');
	    	}
	    	if(value == "create"){ 
	    		$("#notificationAddSuccess").text('${StringUtil.wrapString(uiLabelMap.CreateSuccessfully)}');
				$("#jqxNotificationAddSuccess").jqxNotification('open');
	    	}
		});
	}
	
	$('#alterpopupWindow').on('close', function (event) { 
		var dateCurrent = new Date();
		$('#alterpopupWindow').jqxValidator('hide');
		$('#enumTypeIdInput').val("");
		$('#jqxgridEnumTypeId').jqxGrid('clearselection');
		$('#enumTypeId').jqxDropDownButton('setContent', "");
		$('#enumIdInput').val("");
		$('#jqxgridEnumId').jqxGrid('clearselection');
		$('#enumId').jqxDropDownButton('setContent', "");
		$('#invoiceItemTypeIdInput').val("");
		$('#jqxgridInvoiceItemTypeId').jqxGrid('clearselection');
		$('#invoiceItemTypeId').jqxDropDownButton('setContent', "");
		$('#description').val("");
		$('#fromDate ').jqxDateTimeInput('setDate', dateCurrent);
		$('#thruDate').val("");
		$('#enumTypeId').jqxDropDownButton({disabled: false });
		$('#enumId').jqxDropDownButton({disabled: false });
		$('#invoiceItemTypeId').jqxDropDownButton({disabled: false });
		$('#fromDate').jqxDateTimeInput({disabled: false});
	}); 
	
	function deleteEnumerationInvoiceItemType(enumId, invoiceItemTypeId, fromDate){
		var fromDateStr = fromDate.getTime();
		$.ajax({
			url: "deleteEnumerationInvoiceItemType",
			type: "POST",
			data: {enumId: enumId, invoiceItemTypeId: invoiceItemTypeId, fromDate: fromDateStr},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) { 
			$("#notificationAddSuccess").text('${StringUtil.wrapString(uiLabelMap.NotifiDeleteSucess)}');
			$("#jqxNotificationAddSuccess").jqxNotification('open');
	    	$('#jqxgirdEnumInvoiceType').jqxGrid('updatebounddata');
		});
	}
	function editEnumerationInvoiceItemType(enumId, invoiceItemTypeId, fromDate, thruDate, description){
		checkUpdateInsert = true;
		var enumTypeId = null;
		for (var x in enumerationDataSoure) {
			if(enumerationDataSoure[x].enumId = enumId){
				enumTypeId = enumerationDataSoure[x].enumTypeId;
			}
		}
		$('#enumTypeIdInput').val(enumTypeId);
		$('#enumTypeId').jqxDropDownButton('setContent', mapEnumerationTypeData[enumTypeId]);
		$('#enumIdInput').val(enumId);
		$('#enumId').jqxDropDownButton('setContent', mapEnumerationData[enumId]);
		$('#invoiceItemTypeIdInput').val(invoiceItemTypeId);
		$('#invoiceItemTypeId').jqxDropDownButton('setContent', mapInvoiceItemTypeData[invoiceItemTypeId]);
		$('#description').val(description);
		$('#fromDate').jqxDateTimeInput('setDate', fromDate);
		$('#enumTypeId').jqxDropDownButton({disabled: true });
		$('#enumId').jqxDropDownButton({disabled: true });
		$('#invoiceItemTypeId').jqxDropDownButton({disabled: true });
		$('#fromDate').jqxDateTimeInput({disabled: true});
		$('#thruDate').jqxDateTimeInput('setDate', thruDate);
		$('#alterpopupWindow').jqxWindow('setTitle', '${StringUtil.wrapString(uiLabelMap.LogEditConfigImportExportOther)}: ' + mapInvoiceItemTypeData[invoiceItemTypeId]);
    	$("#alterpopupWindow").jqxWindow('open'); 
	}
	
</script>
