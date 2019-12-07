<#assign dataField="[{name : 'emailSettingId', type : 'string'},
					 {name : 'authUser', type : 'string'},
					 {name : 'authPass', type : 'string'},
					 {name : 'primaryFlag', type : 'string'},
					 ]"/>

<#assign columnlist="{text : '${uiLabelMap.CommonId}', dataField : 'emailSettingId', width : '100px', editable : false},
					 {text : '${uiLabelMap.authUser}', dataField : 'authUser', minwidth : '250px', editable : false},
					 {text : '${uiLabelMap.authPass}', dataField : 'authPass', width : '150px', editable : false},
					 {text : '${uiLabelMap.primaryFlag}', dataField : 'primaryFlag', columntype: 'dropdownlist',
					    createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
					    	var primaryFlagData = [{primaryFlag: 'Y'}, {primaryFlag: 'N'}];
					        editor.jqxDropDownList({source: primaryFlagData, valueMember: 'primaryFlag'});
					    }
					 }"/>

<@jqGrid filtersimplemode="true"  deleterow="true" filterable="true" addrow="true" addType="popup" addrefresh="true" alternativeAddPopup="alterpopupWindow" dataField=dataField columnlist=columnlist  clearfilteringbutton="true" editable="true" 
	url="jqxGeneralServicer?sname=JQGetEmailSetting"
	createUrl="jqxGeneralServicer?sname=createEmailSetting&jqaction=C" addColumns="authUser;authPass;primaryFlag"
	removeUrl="jqxGeneralServicer?sname=deleteEmailSetting&jqaction=D" deleteColumn="emailSettingId"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updateEmailSetting" editColumns="emailSettingId;authUser;authPass;primaryFlag)"
/>	

<div class="row-fluid">
	<div class="span12">	
		<div id="alterpopupWindow" style="display : none;">
			<div>${uiLabelMap.NewEmailSetting}</div>
				<div style="overflow: hidden;">
					<form id="formAddEmailSetting" class="form-horizontal">
						<div class="row-fluid" >
							<div class="span12">
								<div class="control-group no-left-margin">
									<label class="control-label asterisk">${uiLabelMap.authUser}:</label>
									<div class="controls">
										<input id="authUserAdd">
									</div>
								</div>
								<div class="control-group no-left-margin">
									<label class="control-label asterisk">${uiLabelMap.authPass}: </label>
									<div class="controls">
										<input id="authPassAdd">
									</div>
								</div>
								<div class="control-group no-left-margin">
									<label class="control-label">${uiLabelMap.primaryFlag}: </label>
									<div class="controls">
										<div id="primaryFlagAdd" style="margin-left: -3px !important;">
					       				</div>
									</div>
								</div>
								<div class="control-group no-left-margin">
									<label class="control-label">&nbsp;</label>
									<div class="controls">
										<button id="alterCancel" type="button" class='btn btn-danger btn-mini'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
										<button id="alterSave"  type="button" class='btn btn-primary btn-mini'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
									</div>
								</div>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript">
	var action = (function(){
		$.jqx.theme = 'olbius';
		var theme = theme;
		
		//Init popup window
		var initWindow = function(){
			$('#alterpopupWindow').jqxWindow({ width: '50%', height :300,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7,
				initContent: function() {
					$('#authUserAdd').jqxInput({width : 195});
					$('#authPassAdd').jqxInput({width : 195});
					$('#primaryFlagAdd').jqxCheckBox({ width: 120, height: 25});
				}
			});
		};
		
		//Init rule
		var initRule = function(){
			$('#formAddEmailSetting').jqxValidator({
				rules : [
					{input : '#authUserAdd', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required' },
					{input : '#authPassAdd', message: '${uiLabelMap.FieldRequired}', action : 'keyup, blur',rule : 'required' },
					{input: '#authUserAdd', message: '${uiLabelMap.EmailFieldRequired}', action: 'keyup, blur', rule: 'email'}
					
				]
			});
		};
		
		var bindEvent = function(){
			$('#alterSave').on('click', function(){
				$('#formAddEmailSetting').jqxValidator('validate');
			});
			
			$('#formAddEmailSetting').on('validationSuccess',function(){
				var row = {};
				row = {
						authUser : $('#authUserAdd').val(),
						authPass : $('#authPassAdd').val(),
						primaryFlag : $('#primaryFlagAdd').jqxCheckBox('val') == true ? 'Y' : 'N',
				};
				$("#jqxgrid").jqxGrid('addRow', null, row, "first");
				// select the first row and clear the selection.
			    $("#jqxgrid").jqxGrid('clearSelection');                        
			    $("#jqxgrid").jqxGrid('selectRow', 0);  
			    $("#alterpopupWindow").jqxWindow('close');
			});
			
			$("#alterpopupWindow").on('close', function(){
				action.clearForm();
			});
		};
		
		var clearForm = function(){
			$('#authUserAdd').val('');
			$('#authPassAdd').val('');
			$('#primaryFlagAdd').jqxCheckBox('uncheck');
		};
		
		 return {
			 	clearForm : clearForm,
		    	init : function(){
		    		initWindow();
		    		initRule();
		    		bindEvent();
		    		
		    	}
		    };
	})();
	
	$(document).ready(function(){
		action.init();
	});
</script>