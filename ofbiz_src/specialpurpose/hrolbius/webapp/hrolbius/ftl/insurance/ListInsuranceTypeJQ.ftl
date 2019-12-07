<#assign dataField="[{ name: 'insuranceTypeId', type: 'string' },
					 { name: 'description', type: 'string' },
					 { name: 'employerRate', type: 'string' },
					 { name: 'employeeRate', type: 'string' },
					 { name: 'isCompulsory', type: 'string'}]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.InsuranceTypeCode}', datafield: 'insuranceTypeId', width: 200, editable: false},
 					 { text: '${uiLabelMap.InsuranceTypeEmployerRate}', datafield: 'employerRate', width: 250,
 					 validation: function (cell, value) {
                          if (isNaN(value) || value > 1) {
                              return { result: false, message: \"${StringUtil.wrapString(uiLabelMap.invalidInsuranceRating?default(''))}\" };
                          }
                          return true;
                     }},
					 { text: '${uiLabelMap.InsuranceTypeEmployeeRate}', datafield: 'employeeRate', width: 250, 
					 validation: function (cell, value) {
                          if (isNaN(value) || value > 1) {
                              return { result: false, message: \"${StringUtil.wrapString(uiLabelMap.invalidInsuranceRating?default(''))}\" };
                          }
                          return true;
                     }},
                     { text: '${uiLabelMap.InsuranceTypeIsCompulsory}', datafield: 'isCompulsory', width: 100, columntype: 'dropdownlist',
					 	createeditor: function (row, column, editor) {
                            var sourceGlat =
				            {
				                localdata: [\"N\", \"Y\"],
				                datatype: \"array\"
				            };
				            var current = $('#jqxgrid').jqxGrid('getrowdata', row);
				            var selectedIndex = 0;
				            if(current.isCompulsory == 'Y'){
				            	selectedIndex = 1;
				            } 
				            var dataAdapterGlat = new $.jqx.dataAdapter(sourceGlat);
                            editor.jqxDropDownList({source: dataAdapterGlat, dropDownHeight:70, selectedIndex: selectedIndex}); 
					 }},
					 { text: '${uiLabelMap.HRolbiusRecruitmentTypeDescription}', datafield: 'description'}"/>
					 
<@jqGrid url="jqxGeneralServicer?sname=JQGetInsuranceType" dataField=dataField columnlist=columnlist
	clearfilteringbutton="true"
	editable="true" 
	editrefresh ="true"
	editmode="click"
	showtoolbar = "true" deleterow="true"
	removeUrl="jqxGeneralServicer?sname=deleteInsuranceType&jqaction=D" deleteColumn="insuranceTypeId"
	createUrl="jqxGeneralServicer?jqaction=C&sname=CreateInsuranceType" alternativeAddPopup="popupAddRow" addrow="true" addType="popup" 
	addColumns="insuranceTypeId;description;employerRate;employeeRate;isCompulsory" addrefresh="true"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updateInsuranceType"  editColumns="insuranceTypeId;description;employerRate;employeeRate;isCompulsory"
/>
	
<div id="popupAddRow" >
    <div>${uiLabelMap.AddNewInsuranceType}</div>
    <div style="overflow: hidden;">
    	<form name="popupAddRow" action="" id="popupAddRow" class="form-horizontal">
			<div class="control-group no-left-margin">
					<label class="control-label asterisk">
						${uiLabelMap.InsuranceTypeCode}
					</label>			
					<div class="controls">
						<input type="text" name="insuranceTypeIdAdd" id="insuranceTypeIdAdd" value="">
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">
						${uiLabelMap.CommonDescription}
					</label>			
					<div class="controls">
						<input type="text" name="descriptionAdd" id="descriptionAdd" value="">
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">
						${uiLabelMap.InsuranceTypeIsCompulsory}
					</label>			
					<div class="controls">
						<select name="isCompulsoryAdd" id="isCompulsoryAdd">
							<option value="Y">${uiLabelMap.CommonYes}</option>
							<option value="N">${uiLabelMap.CommonNo}</option>
						</select>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">
						${uiLabelMap.InsuranceTypeEmployerRate} (%)
					</label>			
					<div class="controls">
						<input type="number" id="insuranceTypeEmployerRateAdd" name="employerRate" value=""/>
					</div>
				</div>
				
				<div class="control-group no-left-margin">
					<label class="control-label">
						${uiLabelMap.InsuranceTypeEmployeeRate} (%)
					</label>			
					<div class="controls">
						<input type="number"  id="insuranceTypeEmployeeRateAdd" name="employeeRate" value="" />
					</div>
				</div>	
				<div class="control-group no-left-margin">
					<label class="control-label">&nbsp;</label>
					<div class="controls">
						<button type="button" class='btn btn-primary btn-mini' style="margin-right: 5px; margin-top: 10px;" id="alterSave"><i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
						<button type="button" class='btn btn-danger btn-mini' style="margin-right: 5px; margin-top: 10px;" id="alterCancel"><i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
					</div>
				</div>
			</div>	
    	</form>
    </div>
</div>  
<script type="text/javascript">
	$(document).ready(function(){
		var skillJqx = $("#jqxgrid");
		var popup = $("#popupAddRow");
		popup.jqxWindow({
	        width: 600, height: 400, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#Cancel"), modalOpacity: 0.01           
	    });
	    popup.on('close', function (event) { 
	    	popup.jqxValidator('hide');
	    }); 
		popup.jqxValidator({
		   	rules: [{
				input: '#insuranceTypeIdAdd',
				message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}',
				action: 'blur',
				rule: 'required'
			}, { 
				input: '#insuranceTypeEmployerRateAdd', 
				message: '${StringUtil.wrapString(uiLabelMap.invalidRating?default(''))}', 
				action: 'blur', 
				rule: function (input, commit) {
                    var selected = $("#insuranceTypeEmployerRateAdd").val();
                    if(!selected || isNaN(selected || parseDouble(selected) > 0)){
                    	return false;
                    }
                    var i = parseInt(selected);
                	if(i < 0 || i > 100){
                		return false;
                	}
                	var tmp = $("#insuranceTypeEmployeeRateAdd").val();
                	var te = tmp && !isNaN(tmp) ? parseInt(tmp) : 0;
                	var total = te + i;
                    return total <= 100 && total >= 0;
                }
			}, { 
				input: '#insuranceTypeEmployeeRateAdd', 
				message: '${StringUtil.wrapString(uiLabelMap.invalidRating?default(''))}', 
				action: 'blur', 
				rule: function (input, commit) {
                    var selected = $("#insuranceTypeEmployeeRateAdd").val();
                    if(!selected || isNaN(selected)){
                    	return false;
                    }
                    var i = parseInt(selected);
                    if(i < 0 || i > 100){
                		return false;
                	}
                	var tmp = $("#insuranceTypeEmployerRateAdd").val();
                	var te = tmp && !isNaN(tmp) ? parseInt(tmp) : 0;
                	var total = te + i;
                    return total <= 100 && total >= 0;
                }
			}]
		 });
		$("#alterSave").click(function () {
			if(!$('#popupAddRow').jqxValidator('validate')){
				return;
			}
	    	var row = { 
	    		insuranceTypeId : $("#insuranceTypeIdAdd").val(),
	    		description : $("#descriptionAdd").val(),
	    		isCompulsory : $("#isCompulsoryAdd").val(),
	    		employerRate : $("#insuranceTypeEmployerRateAdd").val(),
	    		employeeRate : $("#insuranceTypeEmployeeRateAdd").val()
        	  };
		    skillJqx.jqxGrid('addRow', null, row, "first");
	        // select the first row and clear the selection.
	        skillJqx.jqxGrid('clearSelection');                        
	        skillJqx.jqxGrid('selectRow', 0);  
	        popup.jqxWindow('close');
	    });
		$("#alterCancel").click(function(event){
			popup.jqxWindow('close');
		});
	});
    function openPopupCreatePartySkill(){
    	$("#popupAddRow").jqxWindow('open');
    }
</script>