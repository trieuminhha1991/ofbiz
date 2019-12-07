<#assign dataField="[{ name: 'insurancePaymentId', type: 'string' },
					 { name: 'insurancePaymentName', type: 'string' },
					 { name: 'insuranceTypeId', type: 'string' },
					 { name: 'fromDate', type: 'date' },
					 { name: 'thruDate', type: 'date'}]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.InsurancePayment}', datafield: 'insurancePaymentId', width: 200, editable: false,
						cellsrenderer: function(row, column, value){
							return '<a href=\"InsurancePayment?insurancePaymentId='+ value + '\">' + value + '</a>';
						}
					 },
 					 { text: '${uiLabelMap.InsurancePaymentName}', datafield: 'insurancePaymentName', width: 250, 
 					 validation: function (cell, value) {
                          if (!value) {
                              return { result: false, message: \"${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}\" };
                          }
                          return true;
                     }},
					 { text: '${uiLabelMap.PaymentInsuranceType}', datafield: 'insuranceTypeId', width: 250, editable: false },
                     { text: '${uiLabelMap.fromDate}', width: 100, datafield: 'fromDate', cellsformat: 'dd/MM/yyyy', filtertype:'range', columntype: 'datetimeinput',  editable: false},
					 { text: '${uiLabelMap.thruDate}', datafield: 'thruDate', cellsformat: 'dd/MM/yyyy', filtertype:'range', columntype: 'datetimeinput',  editable: false}"/>
					 
<@jqGrid url="jqxGeneralServicer?sname=JQGetInsurancePayment" dataField=dataField columnlist=columnlist
	clearfilteringbutton="true"
	editable="true" 
	editrefresh ="true"
	editmode="click"
	showtoolbar = "true" deleterow="true"
	removeUrl="jqxGeneralServicer?sname=deleteInsurancePayment&jqaction=D" deleteColumn="insurancePaymentId"
	createUrl="jqxGeneralServicer?jqaction=C&sname=createInsurancePayment&hasrequest=Y" alternativeAddPopup="popupAddRow" addrow="true" addType="popup" 
	addColumns="insuranceTypeId;insurancePaymentName;year(java.lang.Integer);month(java.lang.Integer)" addrefresh="true"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updateInsurancePayment"  editColumns="insurancePaymentName;insurancePaymentId"
/>
<div id="popupAddRow" >
    <div>${uiLabelMap.ConstructFormular}</div>
    <div style="overflow: hidden;">
    	<form name="popupAddRow" action="" id="popupAddRow" class="form-horizontal">
			<div class="control-group no-left-margin">
				<label class="">
					<label for="insurancePaymentName" class="asterisk" id="InsuranceReportName_title">${uiLabelMap.InsurancePaymentName}</label>  
				</label>
				<div class="controls">
					<input type="text" name="insurancePaymentName" id="insurancePaymentNameAdd">
				</div>
			</div>
			<div class="control-group no-left-margin" style="margin-bottom: 15px !important">
				<label class="">
					<label for="InsuranceTypeId" class="asterisk" id="InsuranceType_title">${uiLabelMap.InsuranceType}</label>
				</label>
				<div class="controls">
					<div id="insuranceTypeIdAdd">
						<script>
							var insuranceTypes = [
								<#if insuranceTypeList?exists>
									<#list insuranceTypeList as insuranceType>
										{
											insuranceTypeId : "${insuranceType.insuranceTypeId}",
											description: "${insuranceType.description?if_exists}"
											
										},
									</#list>
								</#if>
							];
						</script>
						
					</div>
				</div>
			</div>
			<div class="control-group no-left-margin ">
				<label class="">
					<label for="reportDate" class="asterisk" id="InsuranceReportName_title">${uiLabelMap.DatePayment}</label>  
				</label>
				<div class="controls">
					<div class="row-fluid">
						<div class="span12">
							<div class="row-fluid">
								<div class="span2" style="margin: 0; padding: 0">
									<label style="display: inline;">${uiLabelMap.CommonMonth}</label>
								</div>
								<div class="span3" style="margin: 0">
									<input type="text" style="margin-bottom:0px;" name="month" class="input-mini" id="month" />
								</div>
								
								<div class="span2" style="margin: 0; padding: 0">
									<label style="display: inline;">${uiLabelMap.CommonYear}</label>
								</div>
								<div class="span3" style="margin: 0">
									<input type="text" style="margin-bottom:0px;"class="input-mini" id="year" name="year"/>
								</div>
							</div>		
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid wizard-actions pull-right">
				<button type="button" class='btn btn-primary btn-save' id="alterSave"><i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
			</div>
    	</form>
    </div>
</div>
<#assign currentYear = currentDateTime.get(Static["java.util.Calendar"].YEAR)>
<#assign currentMonth = currentDateTime.get(Static["java.util.Calendar"].MONTH) + 1>
<#assign minYear = currentYear-10>
<#assign maxYear = currentYear+10>  
<script type="text/javascript">
	$(function() {
		jQuery('#month').ace_spinner({value:${currentMonth},min:1,max:12,step:1, icon_up:'icon-caret-up', icon_down:'icon-caret-down'});
		jQuery('#year').ace_spinner({value:${currentYear},min:${minYear},max:${maxYear},step:1, icon_up:'icon-caret-up', icon_down:'icon-caret-down'});
		
		jQuery("#${createNewLinkId}").attr("data-toggle", "modal");
		jQuery("#${createNewLinkId}").attr("role", "button");
		jQuery("#${createNewLinkId}").attr("href", "#${dataToggleModalId}");
	});
	$(document).ready(function(){
		var skillJqx = $("#jqxgrid");
		var popup = $("#popupAddRow");
		popup.jqxWindow({
	        width: 600, height: 250, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#Cancel"), modalOpacity: 0.01           
	    });
	    popup.on('close', function (event) { 
	    	popup.jqxValidator('hide');
	    }); 
	    var insuranceTypeDd = $('#insuranceTypeIdAdd');
		insuranceTypeDd.jqxDropDownList({
			theme: 'olbius',
			source: insuranceTypes,
			width: 218,
			dropDownHeight: 100,
			displayMember: "description",
			valueMember: 'insuranceTypeId'
		});
		popup.jqxValidator({
		   	rules: [{
				input: '#insurancePaymentNameAdd',
				message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}',
				action: 'blur',
				rule: 'required'
			},{
				input: '#month',
				message: '${StringUtil.wrapString(uiLabelMap.FieldMonthIsBetween?default(''))}',
				action: 'blur',
				rule: function (input, commit) {
	                var value = $("#month").val();
	                if(value){
	                	var valInt = parseInt(value);
	                	if(valInt > 0 && valInt < 13){
	                		return true;
	                	}
	                }
	                return false;
                }
			},{
				input: '#year',
				message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}',
				action: 'blur',
				rule: 'required'
			}, {
                input: "#insuranceTypeIdAdd", 
                message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}", 
                action: 'blur', 
                rule: function (input, commit) {
                    var index = $("#insuranceTypeIdAdd").jqxDropDownList('getSelectedIndex');
                    return index != -1;
                }
            }]
		 });
		$("#alterSave").click(function () {
			if(!$('#popupAddRow').jqxValidator('validate')){
				return;
			}
			var i = insuranceTypeDd.jqxDropDownList("getSelectedItem");
			var insuranceTypeId = i ? i.value : "";
	    	var row = { 
	    		insurancePaymentName : $("#insurancePaymentNameAdd").val(),
	    		insuranceTypeId : insuranceTypeId,
	    		month : $("#month").val(),
	    		year : $("#year").val()
        	  };
		    skillJqx.jqxGrid('addRow', null, row, "first");
	        // select the first row and clear the selection.
	        skillJqx.jqxGrid('clearSelection');                        
	        skillJqx.jqxGrid('selectRow', 0);  
	        popup.jqxWindow('close');
	    });
	});
    function openPopupCreatePartySkill(){
    	$("#popupAddRow").jqxWindow('open');
    }
</script>