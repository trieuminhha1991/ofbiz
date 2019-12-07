<div id="popupAddRole" style="display : none;">
	<div>
		${uiLabelMap.CommonAdd}
	</div>
	<div style="overflow: hidden;">
		<form id="HistoryForm" class="form-horizontal">
			<div class="row-fluid">
				<div class="row-fluid">
					<div class="row-fluid no-left-margin">
						<label class="span5 line-height-25 asterisk align-right line-height-25">${uiLabelMap.DmsPartyId}</label>
						<div class="span7 margin-bottom10">
							<div id="partyIdAdd">
								<div id="jqxGridParty"></div>
							</div>
						</div>
					</div>
					<div class="row-fluid no-left-margin">
						<label class="span5 line-height-25 align-right line-height-25">${uiLabelMap.FormTitle_roleTypeId}</label>
						<div class="span7 margin-bottom10">
							<div id="roleTypeAdd"></div>
						</div>
					</div>
					<div class="row-fluid no-left-margin">
						<label class="span5 line-height-25 align-right line-height-25">${uiLabelMap.CommonPlace}</label>
						<div class="span7 margin-bottom10">
							<div id="marketingPlaceRoleAdd"></div>
						</div>
					</div>
					<div class="row-fluid no-left-margin">
						<label class="span5 line-height-25 align-right line-height-25">${uiLabelMap.Description}</label>
						<div class="span7 margin-bottom10">
							<textarea id="descriptionRoleAdd" row="3" class="no-resize" style="width: 240px;"></textarea>
						</div>
					</div>
				</div>
			</div>

			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="cancelRole" class='btn btn-danger form-action-button pull-right'>
							<i class='fa fa-remove'> </i> ${uiLabelMap.Cancel}
						</button>
						<button type="button" id="saveRoleAndContinue" class='btn btn-primary form-action-button pull-right'>
							<i class='fa fa-plus'> </i> ${uiLabelMap.SaveAndContinue}
						</button>
						<button type="button" id="saveRole" class='btn btn-primary form-action-button pull-right'>
							<i class='fa fa-check'> </i> ${uiLabelMap.Save}
						</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>

<script type="text/javascript">
	var roleType = [];
	var MKRole = ( function() {
			$.jqx.theme = 'olbius';
			theme = $.jqx.theme;
			var form = $('#popupAddRole');
			var width = 250;
			var grid = $("#MarketingRole");
			var initRepresentToSelect = function(dropdown, grid, width){
				var datafields = [
				                  { name: 'partyId', type: 'string' },
				                  { name: 'firstName', type: 'string' },
				                  { name: 'middleName', type: 'string' },
				                  { name: 'lastName', type: 'string' }
				                  ];
				var columns = [
				               {text: '${StringUtil.wrapString(uiLabelMap.DmsPartyId)}', datafield: 'partyId', width: 150},
				               {text: '${StringUtil.wrapString(uiLabelMap.PartyLastName)}', datafield: 'lastName', width: 150},
				               {text: '${StringUtil.wrapString(uiLabelMap.PartyMiddleName)}', datafield: 'middleName', width: 150},
				               {text: '${StringUtil.wrapString(uiLabelMap.PartyFirstName)}', datafield: 'firstName'}
				               ];
				dropdown.on("DOMSubtreeModified", function(){
					var val = $(this).val();
					if(val){
						getPartyRole(val);
					}
				});
				Grid.initDropDownButton({url: "JQGetListEmployee", autorowheight: true, filterable: true, showfilterrow: true, width: width ? width : 600, source: {id: 'partyId'},
						handlekeyboardnavigation: function (event) {
			                var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
			                if (key == 70 && event.ctrlKey) {
						$('#jqxgridFindRepresentTo').jqxGrid('clearfilters');
						return true;
			                }
						},
						dropdown: {
							width: 250
						}
				}, datafields, columns, null, grid, dropdown, "partyId");
			};
			var getPartyRole = function(partyId){
				$.ajax({
					url : "jqxGetPartyRole",
					type : "POST",
					data: {
						partyId : partyId
					},
					success: function(res){
						if(res.listRoleTypes){
							changeDropDownSource(res.listRoleTypes);
						}
					}
				});
			};
			var changeDropDownSource = function(data){
				var roleType = $("#roleTypeAdd");
				roleType.jqxDropDownList("source", data);
			};
			var initElement = function() {
				initRepresentToSelect($('#partyIdAdd'),$('#jqxGridParty'), 600);
				$("#marketingPlaceRoleAdd").jqxComboBox({
					placeHolder : "",
					source : marketingPlace,
					width : width,
					checkboxes: true,
					displayMember : "groupName",
					valueMember : "marketingPlaceId",
					autoDropDownHeight : true
				});
				$("#roleTypeAdd").jqxDropDownList({
					source : roleType,
					width : width,
					autoDropDownHeight: true,
					displayMember : "description",
					valueMember : "uomId",
					placeHolder : "${StringUtil.wrapString(uiLabelMap.ChooseRole?default(''))}"
				});
			};
			var initWindow = function() {
				form.jqxWindow({
					width : 500,
					height : 300,
					resizable : true,
					isModal : true,
					autoOpen : false,
					cancelButton : $("#cancelRole"),
					modalOpacity : 0.7
				});
				form.on("close", function() {
					clear();
				});
			};
			var initRules = function() {
				form.jqxValidator({
					rules : [{
						input : '#partyIdAdd',
						message : "${StringUtil.wrapString(uiLabelMap.CommonRequired?default(''))}",
						action : 'change, close',
						rule : function(input, commit) {
							var value = input.val();
							if (!value)
								return false;
							return true;
						}
					}]
				});
			};
			var bindEvent = function() {
				$("#saveRole").click(function() {
					if (!save()) {
						return;
					}
					form.jqxWindow('close');
				});
				$("#saveRoleAndContinue").click(function() {
					save();
					clear();
				});

				form.on('close', function() {

				});
			};
			var save = function() {
				if (!form.jqxValidator('validate')) {
					return false;
				}
				var index = $('#roleTypeAdd').jqxDropDownList('getSelectedItem');
				var partyId = $('#partyIdAdd').val();
				var value = index && index.value ? index.value : "";
				var party = $('#jqxGridParty').jqxGrid('getrowdatabyid', partyId);
				var row = {
					roleTypeId : value,
					partyId : partyId,
					firstName : party.firstName,
					middleName : party.middleName,
					lastName : party.lastName,
					partyId : partyId,
					description : $('#descriptionRoleAdd').val()
				};
				grid.jqxGrid('addRow', null, row, "last");
				return true;
			};
			var clear = function() {
				Grid.clearForm(form);
			};

			var quickAddRole = function(){
				grid.jqxGrid('addRow', null, {}, "last");
			};
			return {
				init : function() {
					initElement();
					initWindow();
					bindEvent();
					initRules();
				},
				quickAddRole: quickAddRole
			};
		}());

	$(document).ready(function() {
		MKRole.init();
	});
</script>