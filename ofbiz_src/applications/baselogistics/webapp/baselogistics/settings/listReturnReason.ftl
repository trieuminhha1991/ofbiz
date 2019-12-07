<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script>
	//Prepare for product data
	<#assign ownerPartyId = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
</script>

<#assign dataField="[{ name: 'returnReasonId', type: 'string'},
					{ name: 'description', type: 'string'}]"/>

<#assign columnlist="{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
						groupable: false, draggable: false, resizable: false,
						datafield: '', columntype: 'number', width: 50,
						cellsrenderer: function (row, column, value) {
							return '<div style=margin:4px;>' + (value + 1) + '</div>';
						}
					},
					{ text: '${uiLabelMap.LogReasonReturnId}', datafield: 'returnReasonId', width: 350 },
					{ text: '${uiLabelMap.Description}', datafield: 'description' }"/>

<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true"
	id="jqxgirdReturnReason" addrefresh="true" filterable="true"
	url="jqxGeneralServicer?sname=JQGetListReturnReason"
	customTitleProperties="LogListRejectReasonReturnProduct"
	customcontrol1="fa fa-plus@${uiLabelMap.AddNew}@javascript:addProductFacility()"
	mouseRightMenu="true" contextMenuId="menuProductFacility" />

<div id="alterpopupWindow" class="hide popup-bound">
	<div class="row-fluid">
		${uiLabelMap.LogAddReturnsReason}
	</div>
	<div class='form-window-container'>
		<div class="row-fluid">
			<div class="row-fluid margin-bottom10">	
				<div class="span4" style="text-align: right">
					<label class="asterisk"> ${uiLabelMap.LogReasonReturnId}: </label>
				</div>
				<div class="span8">
					<input id="returnReasonId" style="width: 100%">
					</input>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">	
				<div class="span4" style="text-align: right">
					<label class="asterisk"> ${uiLabelMap.Description}: </label>
				</div>
				<div class="span8">
					<input id="description" style="width: 100%">
					</input>
				</div>
			</div>
		</div>
		<div class="form-action popup-footer">
			<button id="addButtonCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="addButtonSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
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
	$("#jqxNotificationAddSuccess").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "success" });
	$("#returnReasonId").jqxInput({width: 250, height: 20});
	$("#description").jqxInput({width: 250,height: 20});
	$("#alterpopupWindow").jqxWindow({
		width: 500, height:180, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#addButtonCancel"), modalOpacity: 0.7, theme: theme
	});
	 
	$("#menuProductFacility").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: 'olbius'});
	
	var checkUpdate = false;
	$("#menuProductFacility").on('itemclick', function (event) {
		var args = event.args;
		var rowindex = $("#jqxgirdReturnReason").jqxGrid('getselectedrowindex');
		var dataRecord = $("#jqxgirdReturnReason").jqxGrid('getrowdata', rowindex);
		var returnReasonId = dataRecord.returnReasonId;
		var description = dataRecord.description;
		var tmpStr = $.trim($(args).text());
		if (tmpStr == "${StringUtil.wrapString(uiLabelMap.CommonDelete)}") {
			bootbox.dialog("${uiLabelMap.ConfirmDelete}",
				[{"label": "${StringUtil.wrapString(uiLabelMap.wgcancel)}",
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
					"callback": function() {bootbox.hideAll();}
				}, 
				{"label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
					"icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
					"callback": function() {
						deleteReturnReason(returnReasonId);
				}
			}]);
		} else if (tmpStr == "${StringUtil.wrapString(uiLabelMap.Edit)}"){
			checkUpdate = true;
			$('#alterpopupWindow').jqxWindow('setTitle', '${StringUtil.wrapString(uiLabelMap.LogEditReturnsReason)}: ' + returnReasonId);
			$("#alterpopupWindow").jqxWindow('open');
			editReturnReason(returnReasonId, description);
		} else if(tmpStr == "${StringUtil.wrapString(uiLabelMap.BSRefresh)}"){
			$('#jqxgirdReturnReason').jqxGrid('updatebounddata');
		}
	});
	
	$('#alterpopupWindow').jqxValidator({
		rules: 
		[
		 	{ input: '#returnReasonId', message: '${uiLabelMap.POCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur',
		 		rule: function () {
		 			var returnReasonId = $('#returnReasonId').val();
		 			if (returnReasonId == "") {
		 				return false;
		 			}
		 			return true;
		 		}
		 	},
		 	{ input: '#description', message: '${uiLabelMap.POCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur',
		 		rule: function () {
		 			var description = $('#description').val();
		 			if (description == "") {
		 				return false;
		 			}
		 			return true;
		 		}
		 	}
	 	],
	 	scroll: false
	});
	
	function addProductFacility(){
		checkUpdate = true;
		$('#alterpopupWindow').jqxWindow('setTitle', '${StringUtil.wrapString(uiLabelMap.LogAddReturnsReason)}');
		$("#alterpopupWindow").jqxWindow('open');
	}
	
	$("#addButtonSave").click(function () {
		var returnReasonId = $('#returnReasonId').val();
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
							addReturnReason(returnReasonId, description);
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
							addReturnReason(returnReasonId, description);
					}
				}]);
			}
		}
	});
	
	function addReturnReason(returnReasonId, description){
		$.ajax({
			url: "addReturnReasonSetting",
			type: "POST",
			data: {returnReasonId: returnReasonId, description: description},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			var value = data["value"];
			$('#jqxgirdReturnReason').jqxGrid('updatebounddata');
			$('#alterpopupWindow').jqxWindow('close');
			if(value == "update"){
				$("#notificationAddSuccess").text('${StringUtil.wrapString(uiLabelMap.UpdateSuccessfully)}');
				$("#jqxNotificationAddSuccess").jqxNotification('open');
			}
			if(value == "create"){
				$("#notificationAddSuccess").text('${StringUtil.wrapString(uiLabelMap.CreateSuccessfully)}');
				$("#jqxNotificationAddSuccess").jqxNotification('open');
			}
			checkUpdate = true;
		});
	}
	
	$('#alterpopupWindow').on('close', function (event) {
		$('#alterpopupWindow').jqxValidator('hide');
		$('#returnReasonId').val("");
		$('#description').val("");
		$('#returnReasonId').jqxInput({disabled: false });
	}); 
	
	function deleteReturnReason(returnReasonId){
		$.ajax({
			url: "deleteReturnReasonSetting",
			type: "POST",
			data: {returnReasonId: returnReasonId},
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
			$('#jqxgirdReturnReason').jqxGrid('updatebounddata');
		});
	}
	
	function editReturnReason(returnReasonId, description){
		$('#returnReasonId').jqxInput({disabled: true });
		$('#returnReasonId').val(returnReasonId);
		$('#description').val(description);
	}
	
</script>
