<div id="newAssignedParty" style="display:none">
	<div>${uiLabelMap.BACCNewAssignedParty}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<form id="formNewAssignedParty">
					<div class="span12 form-window-content-custom">
						<div class='row-fluid'>
							<div class='span5'>
								<label class="required">${uiLabelMap.BACCPartyId}</label>
							</div>
							<div class='span7'>
								<div id="wn_partyId">
									<div id="wn_allocPartyTree"></div>
								</div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label class="required">${uiLabelMap.BACCRoleTypeId}</label>
							</div>
							<div class='span7'>
								<div id="roleTypeId">
								</div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label class="required">${uiLabelMap.BACCFromDate}</label>
							</div>
							<div class='span7'>
								<div id="fromDate"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label>${uiLabelMap.BACCThruDate}</label>
							</div>
							<div class='span7'>
								<div id="thruDate"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label class="required">${uiLabelMap.BACCAllocatedDate}</label>
							</div>
							<div class='span7'>
								<div id="allocatedDate"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label>${uiLabelMap.BACCStatusId}</label>
							</div>
							<div class='span7'>
								<div id="statusId"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label>${uiLabelMap.BACCDescription}</label>
							</div>
							<div class='span7'>
								<textarea id="comments" class="text-popup" style="width: 78% !important"></textarea>
					   		</div>
						</div>
					</form>
				</div>
			</div>
		</div>
		<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.BACCSave}</button>
				<button id="alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.BACCCancel}</button>
	   		</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script>
	var THEME = 'olbius';
	var OLBNewAssignedParty = function(){
	}
	OLBNewAssignedParty.initWindow = function(){
		$("#newAssignedParty").jqxWindow({
			width: '1200', height: 480, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme: THEME,
			initContent: function () {
				/* var configParty = {
						useUrl: true,
						root: 'results',
						widthButton: '80%',
						dropDownHorizontalAlignment: 'right',
						showdefaultloadelement: false,
						autoshowloadelement: false,
						datafields: [{name: 'partyId', type: 'string'}, {name: 'fullName', type: 'string'}],
						columns: [
							{text: '${uiLabelMap.BACCPartyId}', datafield: 'partyId', width: '30%'},
							{text: '${uiLabelMap.BACCFullName}', datafield: 'fullName'}
						],
						url: "JqxGetParties",
						useUtilFunc: true,
						
						key: 'partyId',
						description: ['fullName'],
				};
				
				accutils.initDropDownButton($("#wn_partyId"), $("#wn_partyGrid"), null, configParty, []); */
				var config = {dropDownBtnWidth: "80%", treeWidth: 300};
				globalObject.createJqxTreeDropDownBtn($("#wn_allocPartyTree"), $("#wn_partyId"), globalVar.rootPartyArr, "treeAlloc", "treeChildAlloc", config);
				$("#wn_allocPartyTree").on('select', function(event){
					var item = $('#wn_allocPartyTree').jqxTree('getItem', event.args.element);
					var dropDownContent = '<div class="innerDropdownContent">' + item.label + '</div>';
			        $("#wn_partyId").jqxDropDownButton('setContent', dropDownContent);
			        $("#wn_partyId").jqxDropDownButton('close');
			        accutils.setAttrDataValue('wn_partyId', item.value);
				});
				
				$("#roleTypeId").jqxDropDownList({ source: roleTypeData, filterable: true, placeHolder: '${uiLabelMap.filterchoosestring}', theme: THEME, width: '80%', height: '25px', valueMember: 'roleTypeId', displayMember: 'description'});
				$("#fromDate").jqxDateTimeInput({formatString: "dd/MM/yyyy", height: '25px', width: '80%', theme: THEME});
				$("#thruDate").jqxDateTimeInput({formatString: "dd/MM/yyyy", height: '25px', width: '80%', theme: THEME});
				$("#allocatedDate").jqxDateTimeInput({formatString: "dd/MM/yyyy", height: '25px', width: '80%', theme: THEME});
				$("#statusId").jqxDropDownList({ source: statusData, filterable: true, placeHolder: '${uiLabelMap.filterchoosestring}', theme: THEME, width: '80%', height: '25px', valueMember: 'statusId', displayMember: 'description'});
			}
		});
		
		$("#wn_partyGrid").on('rowselect', function (event) {
			setTimeout(function(){
				$("#wn_partyGrid").jqxGrid('clearSelection');
				clearTimeout(this);
			},200);
		});
	}
	
	OLBNewAssignedParty.initValidate = function(){
		$('#newAssignedParty').jqxValidator({
			rules : [
			         {input : '#wn_partyId',message :'${uiLabelMap.validFieldRequire}',action : 'change,close,blur',rule : function(input){
			        	 var val = input.val();
			        	 if(!val) return false;
			        	 return true;
			         	}
			         },
			         {input : '#roleTypeId',message :'${uiLabelMap.validFieldRequire}',action : 'change,close,blur',rule : function(input){
			        	 var val = input.val();
			        	 if(!val) return false;
			        	 return true;
			         	}
			         },
			         {input : '#fromDate',message :'${uiLabelMap.validFieldRequire}',action : 'change,close,blur',rule : function(input){
			        	 var val = input.val();
			        	 if(!val) return false;
			        	 return true;
			         	}
			         },
			         {input : '#allocatedDate',message :'${uiLabelMap.validFieldRequire}',action : 'change,close,blur',rule : function(input){
			        	 var val = input.val();
			        	 if(!val) return false;
			        	 return true;
			         	}
			         }
			]
		})
	}
	
	
	OLBNewAssignedParty.openWindow = function(){
		$("#newAssignedParty").jqxWindow('open');
	}
	
	OLBNewAssignedParty.init = function(){
		OLBNewAssignedParty.initWindow();
		OLBNewAssignedParty.initValidate();
		OLBNewAssignedParty.bindEvent();
	}
	
	OLBNewAssignedParty.closeWindow = function(){
		$("#newAssignedParty").jqxWindow('close');
	}
	
	OLBNewAssignedParty.bindEvent = function(){
		$('#alterSave').on('click', function(){
			var validate = $("#newAssignedParty").jqxValidator('validate');
			if(!(typeof validate == "boolean" && validate == true)) return;
			var submitedData = {};
			submitedData['fixedAssetId'] = '${parameters.fixedAssetId}';
			submitedData['partyId'] = $('#wn_partyId').val();
			submitedData['roleTypeId'] = $('#roleTypeId').val();
			var fromDate = ($('#fromDate').jqxDateTimeInput('getDate'));
			submitedData['fromDate'] = accutils.getTimestamp(fromDate);
			var thruDate = ($('#thruDate').jqxDateTimeInput('getDate'));
			submitedData['thruDate'] = accutils.getTimestamp(thruDate);
			var allocatedDate = ($('#allocatedDate').jqxDateTimeInput('getDate'));
			submitedData['allocatedDate'] = accutils.getTimestamp(allocatedDate);
			submitedData['statusId'] = $('#statusId').val();
			submitedData['comments'] = $('#comments').val();
			
			var setting = {};
			setting['url'] = 'createPartyFixedAssetAssignment';
			setting['data'] = submitedData;
			//Send Ajax Request
			accutils.callAjax(setting, $("#newAssignedParty"), $('#jqxgridAssignedParties'), '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
		});
		
		$("#wn_partyGrid").on('rowselect', function (event) {
    		var row = $("#wn_partyGrid").jqxGrid('getrowdata', args.rowindex);
    		partyId = row['partyId'];
		 	if (partyId) {
		 		var roleData = new Array();
			    var index = 0;
			    
			    var submitedData = {};
				submitedData['partyId'] = partyId;
				//Send Ajax Request
				$.ajax({
					url: 'getListRoleByParty',
					type: "POST",
					data: submitedData,
					dataType: 'json',
					async: false,
					success : function(data) {
						if(!data._ERROR_MESSAGE_ && !data._ERROR_MESSAGE_LIST_){
							var listRoles = data['listRoles'];
							for(var i = 0; i < listRoles.length; i++){
					    		roleData[index++] = getRoleType(listRoles[i].roleTypeId);
						    }
							 $("#roleTypeId").jqxDropDownList({ source: roleData});
						}
					}
				});
		 	}
		});
		
		//Clear form
		$('#newAssignedParty').on('close', function (event) {
			$('#fixedAssetId').val('');
			$('#comments').val('');
			$('#statusId').jqxDropDownList('clearSelection');
			$("#wn_partyId").jqxDropDownButton('setContent', '');
			$("#wn_partyId").attr('data-value', '');
			$('#fromDate').jqxDateTimeInput('setDate', new Date());
			$('#thruDate').jqxDateTimeInput('setDate', new Date());
			$('#allocatedDate').jqxDateTimeInput('setDate', new Date());
			$("#roleTypeId").jqxDropDownList('clearSelection');
		});
	}
	
	$(function(){
		 OLBNewAssignedParty.init();
	})
</script>