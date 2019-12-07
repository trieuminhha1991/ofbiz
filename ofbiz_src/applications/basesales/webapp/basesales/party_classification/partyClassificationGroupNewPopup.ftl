<div id="alterpopupWindow" style="display:none">
	<div>${uiLabelMap.BSCreateNewPartyClassificationGroup}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label for="partyClassificationGroupId" class="required">${uiLabelMap.BSPartyClassificationGroupId}</label>
						</div>
						<div class='span7'>
							<input id="partyClassificationGroupId"></input>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="partyClassificationTypeId" class="required">${uiLabelMap.BSPartyClassificationTypeId}</label>
						</div>
						<div class='span7'>
							<div id="partyClassificationTypeId"></div>
				   		</div>
					</div>
					<#--
					<div class='row-fluid'>
						<div class='span5'>
							<label for="parentGroupId">${uiLabelMap.BSParentGroupId}</label>
						</div>
						<div class='span7'>
							<div id="parentGroupId">
								<div id="parentGroupGrid"></div>
							</div>
				   		</div>
					</div>
					-->
					<div class='row-fluid'>
						<div class='span5'>
							<label for="description">${uiLabelMap.BSDescription}</label>
						</div>
						<div class='span7'>
							<input id="description"></input>
				   		</div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>
<div id="alterpopupWindowEdit" style="display:none">
	<div>${uiLabelMap.BSUpdatePartyClassificationGroup}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label for="partyClassificationGroupIdEdit">${uiLabelMap.BSPartyClassificationGroupId}</label>
						</div>
						<div class='span7'>
							<input id="partyClassificationGroupIdEdit"></input>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="partyClassificationTypeIdEdit" class="required">${uiLabelMap.BSPartyClassificationTypeId}</label>
						</div>
						<div class='span7'>
							<div id="partyClassificationTypeIdEdit"></div>
				   		</div>
					</div>
					<#--
					<div class='row-fluid'>
						<div class='span5'>
							<label for="parentGroupIdEdit">${uiLabelMap.BSParentGroupId}</label>
						</div>
						<div class='span7'>
							<div id="parentGroupIdEdit">
								<div id="parentGroupGridEdit"></div>
							</div>
				   		</div>
					</div>
					-->
					<div class='row-fluid'>
						<div class='span5'>
							<label for="descriptionEdit">${uiLabelMap.BSDescription}</label>
						</div>
						<div class='span7'>
							<input id="descriptionEdit"></input>
				   		</div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="we_alterUpdate" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonUpdate}</button>
				<button id="we_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<div class="container_loader">
	<div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
		<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div>
				<div class="jqx-grid-load"></div>
				<span>${uiLabelMap.BSLoading}...</span>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		OlbPartyClassifiGroupNew.init();
	});
	var OlbPartyClassifiGroupNew = (function(){
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			jOlbUtil.input.create($("#description"), {width: '97.5%'});
			jOlbUtil.input.create($("#descriptionEdit"), {width: '97.5%'});
			jOlbUtil.input.create($("#partyClassificationGroupId"), {width: '97.5%', disabled: false});
			jOlbUtil.input.create($("#partyClassificationGroupIdEdit"), {width: '97.5%', disabled: true});
			
			jOlbUtil.windowPopup.create($("#alterpopupWindow"), {width: 580, height: 215, cancelButton: $("#wn_alterCancel")});
			jOlbUtil.windowPopup.create($("#alterpopupWindowEdit"), {width: 580, height: 215, cancelButton: $("#we_alterCancel")});
		};
		var initElementComplex = function(){
			var configPartyClassificationType = {
				placeHolder: '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}',
				useUrl: false,
				key: 'partyClassificationTypeId',
				value: 'description',
				autoDropDownHeight: true, 
				width: "99%",
			}
			new OlbDropDownList($("#partyClassificationTypeId"), partyClassificationTypeData, configPartyClassificationType, []);
			new OlbDropDownList($("#partyClassificationTypeIdEdit"), partyClassificationTypeData, configPartyClassificationType, []);
		};
		var initEvent = function(){
			$("#wn_alterSave").on('click', function(){
				if(!$('#alterpopupWindow').jqxValidator('validate')) return false;
		    	var dataMap = {
		    		//parentGroupId: $('#parentGroupId').val(),
		    		description: $('#description').val(),
		    		partyClassificationTypeId: $('#partyClassificationTypeId').val(),
		    		partyClassificationGroupId: $('#partyClassificationGroupId').val(),
		    	};
		    	
		    	$.ajax({
					type: 'POST',
					url: 'createPartyClassificationGroupAjax',
					data: dataMap,
					beforeSend: function(){
						$("#loader_page_common").show();
					},
					success: function(data){
						processResult(data, "create");
						resetWindowPopupCreate();
					},
					error: function(data){
						alert("Send request is error");
					},
					complete: function(data){
						$("#loader_page_common").hide();
						$("#alterpopupWindow").jqxWindow('close');
						$("#jqxPartyClassificationGroup").jqxGrid('updatebounddata');
					},
				});
		    });
			
			$("#we_alterUpdate").on('click', function(){
				if(!$('#alterpopupWindowEdit').jqxValidator('validate')) return false;
		    	var dataMap = {
		    		partyClassificationGroupId: $('#partyClassificationGroupIdEdit').val(),
		    		//parentGroupId: $('#parentGroupIdEdit').val(),
		    		description: $('#descriptionEdit').val(),
		    		partyClassificationTypeId: $('#partyClassificationTypeIdEdit').val(),
		    	};
		    	
		    	$.ajax({
					type: 'POST',
					url: 'updatePartyClassificationGroupAjax',
					data: dataMap,
					beforeSend: function(){
						$("#loader_page_common").show();
					},
					success: function(data){
						processResult(data, "update");
					},
					error: function(data){
						alert("Send request is error");
					},
					complete: function(data){
						$("#loader_page_common").hide();
						$("#alterpopupWindowEdit").jqxWindow('close');
						$("#jqxPartyClassificationGroup").jqxGrid('updatebounddata');
					},
				});
		    });
		    
		};
		var resetWindowPopupCreate = function(){
	    	//$("#parentGroupId").jqxDropDownButton('setContent', null);
	    	$('#description').val(null);
	    	$('#partyClassificationGroupId').val(null);
	    	$('#partyClassificationTypeId').jqxDropDownList('clearSelection'); 
		};
		var processResult = function(data, option){
			jOlbUtil.processResultDataAjax(data, "default", function(){
				$('#container').empty();
	        	$('#jqxNotification').jqxNotification({ template: 'info'});
	        	if (option == 'create'){
	        		$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgaddsuccess)}");
	        	} else {
	        		$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
	        	}
	        	$("#jqxNotification").jqxNotification("open");
			});
		};
		var initValidateForm = function(){
			var mapRulesNew = [
				{input: '#partyClassificationTypeId', type: 'validObjectNotNull', objType: 'dropDownList'},
				{input: '#partyClassificationGroupId', type: 'validInputNotNull'},
				{input: '#partyClassificationGroupId', type: 'validCannotSpecialCharactor'},
			];
			var extendRulesNew = [];
			new OlbValidator($('#alterpopupWindow'), mapRulesNew, extendRulesNew, {position: 'bottom'});
			
			var mapRulesEdit = [
				{input: '#partyClassificationTypeIdEdit', type: 'validObjectNotNull', objType: 'dropDownList'},
			];
			var extendRulesEdit = [];
			new OlbValidator($('#alterpopupWindowEdit'), mapRulesEdit, extendRulesEdit, {position: 'bottom'});
		};
		return {
			init: init
		}
	}());
</script>

<#--
		$('#alterpopupWindow').on('open', function (event) { 
			$("#parentGroupGrid").jqxGrid('updatebounddata');
			$("#parentGroupGridEdit").jqxGrid('updatebounddata');
		}); 
		$('#alterpopupWindowEdit').on('open', function (event) { 
			$("#parentGroupGrid").jqxGrid('updatebounddata');
			$("#parentGroupGridEdit").jqxGrid('updatebounddata');
		}); 
		
		$("#parentGroupGrid").on('rowselect', function (event) {
	        var args = event.args;
	        var row = $("#parentGroupGrid").jqxGrid('getrowdata', args.rowindex);
	        var dropDownContent = '<div class="innerDropdownContent">' + row['partyClassificationGroupId'] + '</div>';
	        $("#parentGroupId").jqxDropDownButton('setContent', dropDownContent);
	    });
		
		$("#parentGroupGridEdit").on('rowselect', function (event) {
	        var args = event.args;
	        var row = $("#parentGroupGridEdit").jqxGrid('getrowdata', args.rowindex);
	        var dropDownContent = '<div class="innerDropdownContent">' + row['partyClassificationGroupId'] + '</div>';
	        $("#parentGroupIdEdit").jqxDropDownButton('setContent', dropDownContent);
	    });
		
			var configParentGroup = {
				useUrl: true,
				root: 'results',
				widthButton: '99%',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [
					{name: 'partyClassificationGroupId', type: 'string'}, 
					{name: 'partyClassificationTypeId', type: 'string'},
					{name: 'parentGroupId', type: 'string'},
					{name: 'description', type: 'string'},
				],
				columns: [
					{text: '${StringUtil.wrapString(uiLabelMap.BSId)}', datafield: 'partyClassificationGroupId', width: 70},
					{text: '${StringUtil.wrapString(uiLabelMap.BSPartyClassificationTypeId)}', datafield: 'partyClassificationTypeId', filtertype: 'checkedlist',
						cellsrenderer: function(row, column, value){
							if (partyClassificationTypeData.length > 0) {
								for(var i = 0 ; i < partyClassificationTypeData.length; i++){
									if (value == partyClassificationTypeData[i].partyClassificationTypeId){
										return '<span title ="' + partyClassificationTypeData[i].description +'">' + partyClassificationTypeData[i].description + '</span>';
									}
								}
							}
							return '<span title=' + value +'>' + value + '</span>';
					 	}, 
					 	createfilterwidget: function (column, columnElement, widget) {
					 		if (partyClassificationTypeData.length > 0) {
								var filterDataAdapter = new $.jqx.dataAdapter(partyClassificationTypeData, {
									autoBind: true
								});
								var records = filterDataAdapter.records;
								widget.jqxDropDownList({source: records, displayMember: 'description', valueMember: 'partyClassificationTypeId',
									renderer: function(index, label, value){
										if (partyClassificationTypeData.length > 0) {
											for(var i = 0; i < partyClassificationTypeData.length; i++){
												if(partyClassificationTypeData[i].partyClassificationTypeId == value){
													return '<span>' + partyClassificationTypeData[i].description + '</span>';
												}
											}
										}
										return value;
									}
								});
								widget.jqxDropDownList('checkAll');
							}
			   			}	
					},
					{text: '${StringUtil.wrapString(uiLabelMap.BSParentGroupId)}', datafield: 'parentGroupId'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSDescription)}', datafield: 'description', width: 200},
				],
				url: 'JQListPartyClassificationGroup',
				useUtilFunc: true,
				dropDownHorizontalAlignment: 'right'
			};
			new OlbDropDownButton($("#parentGroupId"), $("#parentGroupGrid"), null, configParentGroup, []);
			new OlbDropDownButton($("#parentGroupIdEdit"), $("#parentGroupGridEdit"), null, configParentGroup, []);
-->