<@jqGridMinimumLib/>
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
	
	var enumTypeData = [];
	<#assign listTypeIds = ["EXPORT_REASON", "RECEIVE_REASON"]/>
	<#assign enumTypes  = delegator.findList("EnumerationType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("enumTypeId", Static["org.ofbiz.entity.condition.EntityJoinOperator"].IN, listTypeIds), null, null, null, false) />
	<#list enumTypes as item>
		var row = {
			enumTypeId: "${item.enumTypeId}",
			description: "${StringUtil.wrapString(item.description?if_exists)}",
		};
		enumTypeData.push(row);
	</#list>
</script>

<div id="contentNotificationAddSuccess">
</div>
<div id="jqxNotificationAddSuccess" >
	<div id="notificationAddSuccess"> 
	</div>
</div>

<#assign dataField="[
				{ name: 'enumId', type: 'string'},
				{ name: 'enumTypeId', type: 'string'},
				{ name: 'enumCode', type: 'string'},
				{ name: 'descriptionEnumType', type: 'string'},
				{ name: 'descriptionEnum', type: 'string'},
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
				{ text: '${uiLabelMap.EnumCode}', datafield: 'enumCode', align: 'left', width: 350,
				},
				{ text: '${uiLabelMap.EnumTypeId}', datafield: 'enumTypeId', align: 'left', width: 350, filtertype: 'checkedlist',
					cellsrenderer: function(row, column, value){
						for (var i = 0; i < enumTypeData.length; i ++){
							if (value && value == enumTypeData[i].enumTypeId){
								return '<span>' + enumTypeData[i].description + '<span>';
							}
						}
						return '<span>' + value + '<span>';
					},
					createfilterwidget: function (column, columnElement, widget) {
						var filterDataAdapter = new $.jqx.dataAdapter(enumTypeData, {
							autoBind: true
						});
						var records = filterDataAdapter.records;
						widget.jqxDropDownList({source: records, displayMember: 'enumTypeId', valueMember: 'enumTypeId',
							renderer: function(index, label, value){
					        	if (enumTypeData.length > 0) {
									for(var i = 0; i < enumTypeData.length; i++){
										if(enumTypeData[i].enumTypeId == value){
											return '<span>' + enumTypeData[i].description + '</span>';
										}
									}
								}
								return value;
							}
						});
						widget.jqxDropDownList('checkAll');
		   			}
				},
				{ text: '${uiLabelMap.Description}', datafield: 'descriptionEnum', align: 'left'
				},
			"/>

<#if hasOlbPermission("MODULE", "LOG_CONFIG_IM_EX_REASON", "CREATE")>
<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true"
	id="jqxgirdEnumReason" addrefresh="true" filterable="true"
	url="jqxGeneralServicer?sname=JQGetListEnumerationTypeReasonWarehouse"
	customTitleProperties="ListResonExportReceiveFacility"
	customcontrol1="fa fa-plus@${uiLabelMap.AddNew}@javascript:addEnumerationReasonExportReceive()"
	mouseRightMenu="true" contextMenuId="menuEnumerationReason" />	
<#else>
<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true"
	id="jqxgirdEnumReason" addrefresh="true" filterable="true"
	url="jqxGeneralServicer?sname=JQGetListEnumerationTypeReasonWarehouse"
	customTitleProperties="ListResonExportReceiveFacility"
	mouseRightMenu="true" contextMenuId="menuEnumerationReason" />	
</#if>
<div id="alterpopupWindow" class="hide popup-bound">
	<div class="row-fluid">
		${uiLabelMap.AddResonExportReceiveFacility}
	</div>
	<div class='form-window-container'>
		<div class="row-fluid">
			<div class="span12">
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<label class="asterisk"> ${uiLabelMap.EnumTypeId} </label>
					</div>
					<div class="span7">
						<div id="enumTypeId" style="width: 100%">
						</div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<label class="asterisk"> ${uiLabelMap.EnumCode} </label>
					</div>
					<div class="span7">
						<input id="enumId" style="width: 100%">
						</input>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<label class="asterisk"> ${uiLabelMap.Description} </label>
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
				
<div id='menuEnumerationReason' style="display:none;">
	<ul>
		<#if hasOlbPermission("MODULE", "LOG_CONFIG_IM_EX_REASON", "CREATE")>
		    <li><i class="fa fa-trash red"></i>${StringUtil.wrapString(uiLabelMap.CommonDelete)}</li>
		    <li><i class="fa fa-pencil-square-o"></i>${StringUtil.wrapString(uiLabelMap.Edit)}</li>
	    </#if>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>

<script>
	$("#menuEnumerationReason").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: 'olbius'});
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
	
	var checkUpdate = false;
	$("#menuEnumerationReason").on('itemclick', function (event) {
        var args = event.args;
        var rowindex = $("#jqxgirdEnumReason").jqxGrid('getselectedrowindex');
        var dataRecord = $("#jqxgirdEnumReason").jqxGrid('getrowdata', rowindex);
        var enumId = dataRecord.enumId;
        var enumTypeId = dataRecord.enumTypeId;
        var descriptionEnum = dataRecord.descriptionEnum;
        var descriptionEnumType = dataRecord.descriptionEnumType;
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
		            	deleteEnumerationReason(enumId);
		        }
	        }]);
        } else if (tmp == "${StringUtil.wrapString(uiLabelMap.Edit)}"){ 
        	checkUpdate = true;
        	$('#alterpopupWindow').jqxWindow('setTitle', '${StringUtil.wrapString(uiLabelMap.EditResonExportReceiveFacility)}: ' + enumId);
        	$("#alterpopupWindow").jqxWindow('open'); 
        	editEnumerationReason(enumId, enumTypeId, descriptionEnum, descriptionEnumType);
        } else if (tmp == "${StringUtil.wrapString(uiLabelMap.BSRefresh)}"){
        	$('#jqxgirdEnumReason').jqxGrid('updatebounddata');
        }
    });
	
	function editEnumerationReason(enumId, enumTypeId, descriptionEnum, descriptionEnumType){
		$('#enumId').jqxInput({disabled: true });
		$('#enumId').val(enumId);  
		$('#enumTypeId').val(enumTypeId);  
		$("#enumTypeId").jqxDropDownList('setContent', descriptionEnumType); 
		$('#description').val(descriptionEnum);
	}

	$("#jqxNotificationAddSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationAddSuccess", opacity: 0.9, autoClose: true, template: "success" });
	$("#enumId").jqxInput({width: 245, height: 20});
	$("#description").jqxInput({width: 245, height: 20});
	$("#enumTypeId").jqxDropDownList({ source: enumerationTypeData, width: 250, displayMember: 'description', valueMember: 'enumTypeId', placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', autoDropDownHeight: true});
	$("#alterpopupWindow").jqxWindow({
		maxWidth: 700, minWidth: 550, height:210 ,minHeight: 100, maxHeight: 1200, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#addButtonCancel"), modalOpacity: 0.7, theme:'olbius'           
	});
	
	function addEnumerationReasonExportReceive(){
		checkUpdate = false;
		$('#alterpopupWindow').jqxWindow('setTitle', '${StringUtil.wrapString(uiLabelMap.AddResonExportReceiveFacility)}');
    	$("#alterpopupWindow").jqxWindow('open'); 
	}
	
	$('#alterpopupWindow').jqxValidator({
		rules: 
		[
	        { input: '#enumId', message: '${uiLabelMap.POCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur', 
        	   rule: function () {
        		    var enumId = $('#enumId').val();
            	    if(enumId == "" || enumId == " "){
            	    	return false; 
            	    }else{
            	    	return true; 
            	    }
            	    return true; 
        	    }
            }, 
            { input: '#enumTypeId', message: '${uiLabelMap.POCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur', 
         	   rule: function () {
         		    var enumTypeId = $('#enumTypeId').val();
             	    if(enumTypeId == ""){
             	    	return false; 
             	    }else{
             	    	return true; 
             	    }
             	    return true; 
         	    }
             },
	        { input: '#description', message: '${uiLabelMap.POCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur', 
        	   rule: function () {
        		    var description = $('#description').val();
            	    if(description == "" || description == " "){
            	    	return false; 
            	    }else{
            	    	return true; 
            	    }
            	    return true; 
        	    }
            },
	    ]
	});
	
	$("#addButtonSave").click(function () {
		var enumId = $('#enumId').val();
		var enumTypeId = $('#enumTypeId').val();
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
			        	addEnumerationReason(enumId, enumTypeId, description);
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
			        	addEnumerationReason(enumId, enumTypeId, description);
			        }
		        }]);
			}
		}
	});
	
	function addEnumerationReason(enumId, enumTypeId, description){
		$.ajax({
			url: "addEnumerationReasonSetting",
			type: "POST",
			data: {enumId: enumId, enumTypeId: enumTypeId, description: description}, 
			dataType: "json", 
			success: function(data) { 
			}
		}).done(function(data) { 
			var value = data["value"];
			$('#jqxgirdEnumReason').jqxGrid('updatebounddata');
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
		$('#alterpopupWindow').jqxValidator('hide');
		$('#enumId').val("");
		$("#enumTypeId").jqxDropDownList('clearSelection'); 
		$('#description').val("");
		$('#enumId').jqxInput({disabled: false });
	}); 
	
	function deleteEnumerationReason(enumId){
		$.ajax({
			url: "deleteEnumerationReasonExportReceiveSetting",
			type: "POST",
			data: {enumId: enumId},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) { 
			var value = data["value"];
			if(value == "success"){
				$("#notificationAddSuccess").text('${StringUtil.wrapString(uiLabelMap.NotifiDeleteSucess)}');
				$("#jqxNotificationAddSuccess").jqxNotification('open');
	    	}
	    	if(value == "exits"){
	    		$("#notificationAddSuccess").text('${StringUtil.wrapString(uiLabelMap.CheckLinkedData)}');
				$("#jqxNotificationAddSuccess").jqxNotification('open');
	    	}
	    	$('#jqxgirdEnumReason').jqxGrid('updatebounddata');
		});
	}
</script>
				