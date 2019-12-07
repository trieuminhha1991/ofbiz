<#assign dataField="[{ name: 'suspendReasonId', type: 'string' },
					 { name: 'insuranceParticipateTypeId', type: 'string' },
					 { name: 'description', type: 'string' },
					 { name: 'functionCalcBenefit', type: 'string' }]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.SuspendReasonId}', datafield: 'suspendReasonId', width: 200, editable: false},
					 { text: '${uiLabelMap.SuspendInsuranceParticipateType}', datafield: 'insuranceParticipateTypeId', width: 250},
                     { text: '${uiLabelMap.HRolbiusRecruitmentTypeDescription}', width: 200, datafield: 'description'},
					 { text: '${uiLabelMap.FormulaCalcBenefitSuspendReason}', datafield: 'functionCalcBenefit'}"/>
					 
<@jqGrid url="jqxGeneralServicer?sname=JQGetSuspendInsuranceReason" dataField=dataField columnlist=columnlist
	clearfilteringbutton="true"
	editable="true" 
	editrefresh ="true"
	editmode="click"
	showtoolbar = "true" deleterow="true"
	removeUrl="jqxGeneralServicer?sname=deleteSuspendInsuranceReason&jqaction=DL" deleteColumn="suspendReasonId" deletelocal="true"
	createUrl="jqxGeneralServicer?jqaction=C&sname=createSuspendInsuranceReason" alternativeAddPopup="popupAddRow" addrow="true" addType="popup" 
	addColumns="suspendReasonId;description;insuranceParticipateTypeId;functionCalcBenefit" addrefresh="true" updateoffline="true" 
	updateUrl="jqxGeneralServicer?jqaction=UL&sname=updateSuspendInsuranceReason"  editColumns="suspendReasonId;description;insuranceParticipateTypeId;functionCalcBenefit"
/>
<div id="popupAddRow" >
    <div>${uiLabelMap.ConstructFormular}</div>
    <div style="overflow: hidden;">
    	<form name="popupAddRow" action="" id="popupAddRow" class="form-horizontal">
			<div class="control-group no-left-margin ">
				<label class="">
					<label for="EditFormula_code" class="asterisk" id="">${uiLabelMap.SuspendReasonId}</label>  
				</label>
				<div class="controls">
					<input type="text" name="suspendReasonId" id="suspendReasonIdAdd">
			 	</div>
			</div>
			<div class="control-group no-left-margin">
				<label class="">
					<label for="EditFormula_code" class="asterisk" id="">${uiLabelMap.CommonDescription}</label>  
				</label>
				<div class="controls">
					<input type="text" name="description" id="descriptionAdd"/>
				</div>
			</div>
			<div class="control-group no-left-margin">
			    <label class="">
			    	<label for="EditFormula_name" class="asterisk" id="">${uiLabelMap.SuspendInsuranceParticipateType}</label>  
		    	</label>
			    <div class="controls">
			    	<div id="insuranceParticipateTypeDd"></div>
			    	<script>
			    		var ipTypes = [
			    			<#if insuranceParticipateType?exists>
				    			<#list insuranceParticipateType as type>
				    			{
				    				insuranceParticipateTypeId : "${type.insuranceParticipateTypeId}",
				    				description : "${type.description}"
				    			},
								</#list>
							</#if>
		    				];
			    	</script>
			    </div>
		    </div>
		    <div class="control-group no-left-margin margin-top-10">
			    <label class="">
			    	<label for="EditFormula_name"  id="">${uiLabelMap.FunctionCalcBenefit}</label>  
		    	</label>
			    <div class="controls">
			    	<div id="insuranceFormulaDd">
			    		
			    	</div>
			    	<script>
			    		var ifLists = [
			    			<#if insuranceFormulaList?exists>
				    			<#list insuranceFormulaList as formula>
				    				{
				    					code : "${formula.code}",
				    					description: "${formula.description?if_exists}"
				    				},
								</#list>
							</#if>
			    		];	
			   	 	</script>
			    </div>
		    </div>
		    <div class="control-group no-left-margin margin-top-10 hide" id="functionContainer">
		    	<label class="">
			    	<label for="EditFormula_name"id="">${uiLabelMap.FunctionFormula}</label>  
		    	</label>
		    	<div class="controls">
					<div id="function">
						&nbsp;
					</div>  
			    </div>
		    </div>
			<div class="row-fluid wizard-actions popup-action-al">
				<button type="button" class='btn btn-primary' style="margin-right: 5px; margin-top: 10px;" id="alterSave"><i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
			</div>
    	</form>
    </div>
</div>

<script type="text/javascript">
	
	$(document).ready(function(){
		var skillJqx = $("#jqxgrid");
		var popup = $("#popupAddRow");
		popup.jqxWindow({
	        width: 800, height: 320, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#Cancel"), modalOpacity: 0.01           
	    });
	    popup.on('close', function (event) { 
	    	popup.jqxValidator('hide');
	    }); 
	    var ipTypeDd = $('#insuranceParticipateTypeDd');
		ipTypeDd.jqxDropDownList({
			theme: 'olbius',
			source: ipTypes,
			width: 218,
			autoDropDownHeight: true,
			displayMember: "description"
		});
		var ifDd = $('#insuranceFormulaDd');
		ifDd.jqxDropDownList({
			theme: 'olbius',
			source: ifLists,
			width: 218,
			autoDropDownHeight: true,
			dropDownWidth:380,
			displayMember: "description"
		});
		ifDd.on("change", function(){
			var j = ifDd.jqxDropDownList("getSelectedIndex");
			var fcBenefit = ifLists[j].code;
			getFunction(fcBenefit);
		});
		popup.jqxValidator({
		   	rules: [{
				input: '#suspendReasonIdAdd',
				message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}',
				action: 'blur',
				rule: 'required'
			},{
				input: '#descriptionAdd',
				message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}',
				action: 'blur',
				rule: 'required'
			},{
                input: "#insuranceParticipateTypeDd", 
                message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}", 
                action: 'blur', 
                rule: function (input, commit) {
                    var index = ipTypeDd.jqxDropDownList('getSelectedIndex');
                    return index != -1;
                }
            },{
                input: "#insuranceFormulaDd", 
                message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}", 
                action: 'blur', 
                rule: function (input, commit) {
                    var index = ifDd.jqxDropDownList('getSelectedIndex');
                    return index != -1;
                }
            }]
		 });
		$("#alterSave").click(function () {
			if(!$('#popupAddRow').jqxValidator('validate')){
				return;
			}
			var i = ipTypeDd.jqxDropDownList("getSelectedIndex");
			var ipTypeId = ipTypes[i].insuranceParticipateTypeId;
			var j = ifDd.jqxDropDownList("getSelectedIndex");
			var fcBenefit = ifLists[j].code;
	    	var row = { 
	    		suspendReasonId : $("#suspendReasonIdAdd").val(),
	    		description : $("#descriptionAdd").val(),
	    		insuranceParticipateTypeId : ipTypeId,
	    		functionCalcBenefit : fcBenefit
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
    function getFunction(selectId){
		jQuery.ajax({
			url: "<@ofbizUrl>getFunctionFormula</@ofbizUrl>",
			data: {code: selectId},
			type: 'POST',
			success: function(data){
				if(data.functionStr){
					$("#functionContainer").show();
					jQuery("#function").empty();
					jQuery("#function").html(data.functionStr);
				}
			}
		});
	}
</script>