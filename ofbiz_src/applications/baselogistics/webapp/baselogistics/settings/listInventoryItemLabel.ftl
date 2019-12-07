<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
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
	
	<#assign inventoryItemLabelTypes = delegator.findList("InventoryItemLabelType", null, null, null, null, false) />
	var mapInventoryItemLabelTypeData = {  
			<#if inventoryItemLabelTypes?exists>
				<#list inventoryItemLabelTypes as item>
					"${item.inventoryItemLabelTypeId?if_exists}": '${StringUtil.wrapString(item.get('description', locale)?if_exists)}',
				</#list>
			</#if>	
	};
	
	var inventoryItemLabelTypeDataSoure = [
   		<#if inventoryItemLabelTypes?exists>
   			<#list inventoryItemLabelTypes as item>
   				{
   					inventoryItemLabelTypeId: "${item.inventoryItemLabelTypeId?if_exists}",
   					description: "${item.description?if_exists}",
   				},
   			</#list>
   		</#if>
   	];
</script>
<div id="contentNotificationAddSuccess">
</div>
<#assign dataField="[
				{ name: 'inventoryItemLabelId', type: 'string'},
				{ name: 'inventoryItemLabelTypeId', type: 'string'},
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
				{ text: '${uiLabelMap.BLInventoryItemLabelId}', datafield: 'inventoryItemLabelId', align: 'center', width: 200, 
				},
				{ text: '${uiLabelMap.BLInventoryItemLabeTypelId}', datafield: 'inventoryItemLabelTypeId', align: 'center', width: 300, filtertype: 'checkedlist',	
					cellsrenderer: function(row, column, value){
				 		if (inventoryItemLabelTypeDataSoure.length > 0) {
				 			for(var i = 0 ; i < inventoryItemLabelTypeDataSoure.length; i++){
    							if (value == inventoryItemLabelTypeDataSoure[i].inventoryItemLabelTypeId){
    								return '<span title = ' + inventoryItemLabelTypeDataSoure[i].description +'>' + inventoryItemLabelTypeDataSoure[i].description + '</span>';
    							}
    						}
				 		}
						return '<span title=' + value +'>' + value + '</span>';
				 	}, 
				 	createfilterwidget: function (column, columnElement, widget) {
				 		if (inventoryItemLabelTypeDataSoure.length > 0) {
							var filterDataAdapter = new $.jqx.dataAdapter(inventoryItemLabelTypeDataSoure, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							widget.jqxDropDownList({source: records, displayMember: 'description', valueMember: 'inventoryItemLabelTypeId',
								renderer: function(index, label, value){
									for(var i = 0; i < inventoryItemLabelTypeDataSoure.length; i++){
										if(inventoryItemLabelTypeDataSoure[i].inventoryItemLabelTypeId == value){
											return '<span>' + inventoryItemLabelTypeDataSoure[i].description + '</span>';
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
						}
		   			}
				},  
				{ text: '${uiLabelMap.Description}', datafield: 'description', align: 'center',
				},
			"/>

<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true"
	id="jqxgirdLabel" addrefresh="true" filterable="true"
	url="jqxGeneralServicer?sname=JQGetListInventoryItemLabel" 
	customTitleProperties="BLInventoryItemLabel"
	customcontrol1="fa fa-plus@${uiLabelMap.AddNew}@javascript:addInventoryItemLabel()"
	mouseRightMenu="true" contextMenuId="menuProductFacility" 
	customcontrol2="fa fa-plus@${uiLabelMap.AddNewInventoryLabelType}@javascript:popupCreateInventoryLabelType();"	
/>	
				
<div id="alterpopupWindow" class="hide popup-bound">
	<div class='form-window-container'>
		<div class="row-fluid">
			<div class="span12">
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<label class="asterisk"> ${uiLabelMap.BLInventoryItemLabelId}: </label>
					</div>
					<div class="span7">
						<input id="inventoryItemLabelId" style="width: 100%">
						</input>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<label class="asterisk"> ${uiLabelMap.BLInventoryItemLabeTypelId}: </label>
					</div>
					<div class="span7">
						<div id="inventoryItemLabelTypeId" style="width: 100%">
						</div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<label class="asterisk"> ${uiLabelMap.Description}: </label>
					</div>
					<div class="span7">
						<input id="description" style="width: 100%">
						</input>
					</div>
				</div>
			</div> 
	    </div>
	    <div class="form-action popup-footer">
            <button id="addButtonCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
            <button id="addButtonSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	    </div>
	</div>
</div>	

<div id="labelTypePopupWindow" class="hide popup-bound">
	<div>${uiLabelMap.BLInventoryItemLabeTypelId}</div>
	<div class='form-window-container'>
		<div class="row-fluid">
			<div class="span12">
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<label class="asterisk"> ${uiLabelMap.InventoryItemLabelTypeId}: </label>
					</div>
					<div class="span7">
						<input id="inventoryItemLabelTypeIdNew" style="width: 100%">
						</input>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<label> ${uiLabelMap.ParentLabelTypeId}: </label>
					</div>
					<div class="span7">
						<div id="parentTypeId" style="width: 100%">
						</div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<label class="asterisk"> ${uiLabelMap.Description}: </label>
					</div>
					<div class="span7">
						<input id="descriptionTypeNew" style="width: 100%">
						</input>
					</div>
				</div>
			</div> 
	    </div>
	    <div class="form-action popup-footer">
            <button id="addTypeCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
            <button id="addTypeSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	    </div>
	</div>
</div>	

<div id='menuProductFacility' style="display:none;">
	<ul>
	    <li><i class="fa fa-trash red"></i>${StringUtil.wrapString(uiLabelMap.CommonDelete)}</li>
	    <li><i class="fa fa-pencil-square-o"></i>${StringUtil.wrapString(uiLabelMap.Edit)}</li>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>
<div id="jqxNotificationAddSuccess" >
	<div id="notificationAddSuccess"> 
	</div>
</div>
<script>
	$("#jqxNotificationAddSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationAddSuccess", opacity: 0.9, autoClose: true, template: "success" });
	$("#inventoryItemLabelId").jqxInput({width: 200, height: 20});
	$("#inventoryItemLabelTypeId").jqxDropDownList({source: inventoryItemLabelTypeDataSoure, width:205, autoDropDownHeight: true, placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', displayMember: 'description', valueMember: 'inventoryItemLabelTypeId'});
	$("#description").jqxInput({width: 200, height: 25});
	$("#alterpopupWindow").jqxWindow({
		maxWidth: 700, minWidth: 550, height:220 ,minHeight: 100, maxHeight: 1200, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#addButtonCancel"), modalOpacity: 0.7, theme:'olbius'           
	});
	$("#labelTypePopupWindow").jqxWindow({
		maxWidth: 700, minWidth: 550, height:220 ,minHeight: 100, maxHeight: 1200, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#addTypeCancel"), modalOpacity: 0.7, theme:'olbius'           
	});
	
	$("#parentTypeId").jqxDropDownList({source: inventoryItemLabelTypeDataSoure, width:205, height: 25, autoDropDownHeight: true, placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', displayMember: 'description', valueMember: 'inventoryItemLabelTypeId'});
	
	$("#inventoryItemLabelTypeIdNew").jqxInput({width: 200, placeHolder: '${StringUtil.wrapString(uiLabelMap.AToZ_)}', height: 20});
	$("#descriptionTypeNew").jqxInput({width: 200, height: 25});
	
	$("#menuProductFacility").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: 'olbius'});
	
	var checkUpdate = false;
	$("#menuProductFacility").on('itemclick', function (event) {
        var args = event.args;
        var rowindex = $("#jqxgirdLabel").jqxGrid('getselectedrowindex');
        var dataRecord = $("#jqxgirdLabel").jqxGrid('getrowdata', rowindex);
        var inventoryItemLabelId = dataRecord.inventoryItemLabelId;
        var inventoryItemLabelTypeId = dataRecord.inventoryItemLabelTypeId;
        var description = dataRecord.description;
        var tmp = $.trim($(args).text());
        if (tmp == "${StringUtil.wrapString(uiLabelMap.CommonDelete)}") {
        	bootbox.dialog("${uiLabelMap.ConfirmDelete}", 
				[{"label": "${StringUtil.wrapString(uiLabelMap.wgcancel)}", 
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		            "callback": function() {bootbox.hideAll();}
		        }, 
		        {"label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
		            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		            "callback": function() {
		            	deleteInventoryItemLabel(inventoryItemLabelId);
		        }
	        }]);
        } else if (tmp == "${StringUtil.wrapString(uiLabelMap.Edit)}"){ 
        	checkUpdate = true;
        	$('#alterpopupWindow').jqxWindow('setTitle', '${StringUtil.wrapString(uiLabelMap.LogEditReturnsReason)}: ' + inventoryItemLabelId);
        	$("#alterpopupWindow").jqxWindow('open'); 
        	editinventoryItemLabel(inventoryItemLabelId, inventoryItemLabelTypeId, description);
        } else if (tmp == "${StringUtil.wrapString(uiLabelMap.BSRefresh)}"){ 
        	$('#jqxgirdLabel').jqxGrid('updatebounddata');
        }
    });
	
	$('#alterpopupWindow').jqxValidator({
		rules: 
		[
	        { input: '#inventoryItemLabelId', message: '${uiLabelMap.FieldRequired}', action: 'valueChanged, blur', 
        	   rule: function () {
        		    var inventoryItemLabelId = $('#inventoryItemLabelId').val();
            	    if(inventoryItemLabelId === "" || inventoryItemLabelId === null || inventoryItemLabelId === undefined){
            	    	return false; 
            	    }else{
            	    	return true; 
            	    }
            	    return true; 
        	    }
            }, 
            { input: '#inventoryItemLabelTypeId', message: '${uiLabelMap.FieldRequired}', action: 'valueChanged, blur', 
         	   rule: function () {
         		    var inventoryItemLabelTypeId = $('#inventoryItemLabelTypeId').val();
             	    if(inventoryItemLabelTypeId === "" || inventoryItemLabelTypeId === null || inventoryItemLabelTypeId === undefined){
             	    	return false; 
             	    }else{
             	    	return true; 
             	    }
             	    return true; 
         	    }
            }, 
	        { input: '#description', message: '${uiLabelMap.FieldRequired}', action: 'valueChanged, blur', 
        	   rule: function () {
        		    var description = $('#description').val();
            	    if(description === "" || description === null || description === undefined){
            	    	return false; 
            	    }else{
            	    	return true; 
            	    }
            	    return true; 
        	    }
            },
	    ]
	});
	
	$('#labelTypePopupWindow').jqxValidator({
		rules: 
		[
	        { input: '#inventoryItemLabelTypeIdNew', message: '${uiLabelMap.FieldRequired}', action: 'valueChanged, blur', 
        	   rule: function () {
        		    var inventoryItemLabelTypeId = $('#inventoryItemLabelTypeIdNew').val();
            	    if(inventoryItemLabelTypeId === "" || inventoryItemLabelTypeId === null || inventoryItemLabelTypeId === undefined){
            	    	return false; 
            	    } else {
            	    	return true; 
            	    }
            	    return true; 
        	    }
            }, 
            {input: '#inventoryItemLabelTypeIdNew', message: '${uiLabelMap.ThisFieldMustNotByContainSpecialCharacter}' + '. ' + '${uiLabelMap.AToZ_}', action: 'blur', rule: 
                function (input, commit) {
                	var value = $(input).val();
        			if(value && !(/^[a-zA-Z0-9_]/.test(value))){
        				return false;
        			}
        			return true;
            	}
            },
	        { input: '#descriptionTypeNew', message: '${uiLabelMap.FieldRequired}', action: 'valueChanged, blur', 
        	   rule: function () {
        		    var description = $('#descriptionTypeNew').val();
            	    if(description === "" || description === null || description === undefined){
            	    	return false; 
            	    }else{
            	    	return true; 
            	    }
            	    return true; 
        	    }
            },
	    ]
	});
	
	function addInventoryItemLabel(){
		checkUpdate = false;
		$('#alterpopupWindow').jqxWindow('setTitle', '${StringUtil.wrapString(uiLabelMap.BLAddInventoryItemLabe)}');
    	$("#alterpopupWindow").jqxWindow('open'); 
	}
	
	$("#addTypeSave").click(function () {
		var inventoryItemLabelTypeId = $('#inventoryItemLabelTypeIdNew').val();
		var parentTypeId = $('#parentTypeId').val();
		var description = $('#descriptionTypeNew').val();
		var validate = $('#labelTypePopupWindow').jqxValidator('validate');
		if(validate != false){
			bootbox.dialog("${uiLabelMap.AreYouSureCreate}", 
			[{"label": "${uiLabelMap.CommonCancel}", 
				"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
			    "callback": function() {bootbox.hideAll();}
			}, 
			{"label": "${uiLabelMap.OK}",
			    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
			    "callback": function() {
			    	Loading.show('loadingMacro');
			    	setTimeout(function(){
						$.ajax({
				    		url: "createInventoryItemLabelType",
				    		type: "POST",
				    		async: false,
				    		data: {
				    			inventoryItemLabelTypeId: inventoryItemLabelTypeId,
				    			parentTypeId: parentTypeId,
				    			description: description,
				    		},
				    		success: function (res){
				    			$("#labelTypePopupWindow").jqxWindow('close');
				    			window.location.replace("getInventoryItemLabel");
				    		}
				    	});
					Loading.hide('loadingMacro');
			    	}, 500);
			    }
			}]);
		}
	});
	
	$("#addButtonSave").click(function () {
		var inventoryItemLabelId = $('#inventoryItemLabelId').val();
		var inventoryItemLabelTypeId = $('#inventoryItemLabelTypeId').val();
		var description = $('#description').val();
		var validate = $('#alterpopupWindow').jqxValidator('validate');
		if(validate != false){
			if(checkUpdate == true){
				bootbox.dialog("${uiLabelMap.AreYouSureUpdate}", 
					[{"label": "${StringUtil.wrapString(uiLabelMap.wgcancel)}", 
						"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
			            "callback": function() {bootbox.hideAll();}
			        }, 
			        {"label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
			            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
			            "callback": function() {
			            	eventInventoryItemLabel(inventoryItemLabelId, inventoryItemLabelTypeId, description);
			        }
		        }]);
			}else{
				bootbox.dialog("${uiLabelMap.POAreYouSureAddItem}", 
					[{"label": "${StringUtil.wrapString(uiLabelMap.wgcancel)}", 
						"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
			            "callback": function() {bootbox.hideAll();}
			        }, 
			        {"label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
			            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
			            "callback": function() {
			            	eventInventoryItemLabel(inventoryItemLabelId, inventoryItemLabelTypeId, description);
			        }
		        }]);
			}
		}
	});
	
	function eventInventoryItemLabel(inventoryItemLabelId, inventoryItemLabelTypeId, description){
		$.ajax({
			url: "addInventoryItemLabelSetting",
			type: "POST",
			data: {inventoryItemLabelId: inventoryItemLabelId, inventoryItemLabelTypeId: inventoryItemLabelTypeId, description: description},
			dataType: "json",  
			success: function(data) { 
			}
		}).done(function(data) { 
			var value = data["value"];
			$('#jqxgirdLabel').jqxGrid('updatebounddata');
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
	
	$('#labelTypePopupWindow').on('close', function (event) { 
		$('#labelTypePopupWindow').jqxValidator('hide');
	});
	
	$('#alterpopupWindow').on('close', function (event) { 
		$('#alterpopupWindow').jqxValidator('hide');
		$('#inventoryItemLabelId').val("");
		$('#description').val("");
		checkUpdate = false;
		$('#inventoryItemLabelId').jqxInput({disabled: false });
		$("#inventoryItemLabelTypeId").jqxDropDownList('clearSelection'); 
	}); 
	
	function deleteInventoryItemLabel(inventoryItemLabelId){
		$.ajax({
			url: "deleteInventoryItemLabelSetting",
			type: "POST",
			data: {inventoryItemLabelId: inventoryItemLabelId},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) { 
			var value = data["value"];
			if(value == "success"){
				$("#notificationAddSuccess").text('${StringUtil.wrapString(uiLabelMap.NotifiDeleteSucess)}');
				$("#jqxNotificationAddSuccess").jqxNotification('open');
	    	}
	    	if(value == "exits" || value == undefined || value == null){
	    		$("#notificationAddSuccess").text('${StringUtil.wrapString(uiLabelMap.CheckLinkedData)}');
				$("#jqxNotificationAddSuccess").jqxNotification('open');
	    	}
	    	$('#jqxgirdLabel').jqxGrid('updatebounddata');
		});
	}
	
	function editinventoryItemLabel(inventoryItemLabelId, inventoryItemLabelTypeId, description){
		$('#inventoryItemLabelId').jqxInput({disabled: true });
		$('#inventoryItemLabelId').val(inventoryItemLabelId);
		$('#inventoryItemLabelTypeId').val(inventoryItemLabelTypeId);
		$('#description').val(description);
	}
	
	function getListInventoryLabelType(inventoryItemLabelId){
		$.ajax({
			url: "getListInventoryLabelTypes",
			type: "POST",
			data: {},
			dataType: "json",
			success: function(data) {
				mapInventoryItemLabelTypeData = data.listInventoryItemLabelTypes;
			}
		});
	}
	function popupCreateInventoryLabelType (){
		$("#labelTypePopupWindow").jqxWindow('open');
	}
</script>
