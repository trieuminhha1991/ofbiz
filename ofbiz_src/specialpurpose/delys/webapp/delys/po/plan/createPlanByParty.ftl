<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/delys/images/js/util/DateUtil.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/delys/images/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script>
<@jqGridMinimumLib/>

<script>
	<#assign listCustomTimePeriod = delegator.findList("CustomTimePeriod", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("periodTypeId", "PO_YEAR"), null, null, null, false)>
	var listCustomTimePeriodData = 
	[
		<#list listCustomTimePeriod as customTimePeriod>
		{
			customTimePeriodId: "${customTimePeriod.customTimePeriodId}",  
			periodName: "${StringUtil.wrapString(customTimePeriod.get('periodName', locale)?if_exists)}"
		},
		</#list>
	];
	
	<#assign listPeriodType = delegator.findList("PeriodType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("groupPeriodTypeId", "PERIOD_TYPE_PO"), null, null, null, false)>
	var periodTypeData = 
	[
		<#list listPeriodType as periodType>
		{
			periodTypeId: "${periodType.periodTypeId}",  
			description: "${StringUtil.wrapString(periodType.get('description', locale)?if_exists)}"
		},
		</#list>
	];
	
	<#assign listProductPlanHeader = delegator.findList("ProductPlanHeader", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusId", "PLAN_APPROVED"), null, null, null, false)>
	var productPlanHeaderData = 
	[
		<#list listProductPlanHeader as productPlanHeader>
		{
			productPlanId: "${productPlanHeader.productPlanId}",  
			productPlanName: "${StringUtil.wrapString(productPlanHeader.get('productPlanName', locale)?if_exists)}"
		},
		</#list>
	];
</script>

<div id="contentNotificationAddPeriodSuccess">
</div>
<div id="contentNotificationAddPeriodCustomExits">
</div>
<form id="alterpopupWindow" action="createPlanByPO">
	<div class='form-window-container'>
		<div class='form-window-content'>
    		<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<label class="asterisk"> ${uiLabelMap.POCustomTimePeriodYeah}: </label>
						</div>
						<div class="span7">
							<div id="customTimePeriodPO" style="width: 100%"></div>
						</div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<label class="asterisk">${uiLabelMap.POPlanName}:</label>
						</div>
						<div class="span7">
							<div style="width: 195px; display: inline-block; margin-bottom: 3px;"><input id="planName"></input></div><a onclick="showEditor()" style="display: inline-block"><i style="padding-left: 24px" class="icon-edit"></i></a>
						</div>
					</div>
				</div>
			</div>	
			
			<#if security.hasEntityPermission("PO_PLAN_APPROVED", "_VIEW", session)>
				<div class="row-fluid">
					<div class="span6">
						<div class="row-fluid margin-bottom10">	
							<div class="span5" style="text-align: right">
								<label> ${uiLabelMap.POPlanProposalByParty}: </label>
							</div>
							<div class="span7">
								<div id="productPlanId" style="width: 100%"></div>
							</div>
						</div>
					</div>
				</div>
			<#else>   
			</#if>
		</div>
	</div>
</form>
<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity: 0.4;">
<div style="margin-top:10px;">
	 <button id="addButtonSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
</div>

<div id="jqxEditorWindow" style="display: none">
	<div id="windowHeader">
		<span>
		    ${uiLabelMap.Description}
		</span>
	</div>
	<div style="overflow: hidden;" id="windowContent">
		<textarea id="editor">
		</textarea>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelButton" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="okButton" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>

<div id="alterpopupWindowAddPeriodPlan" class='hide'>
	<div>${uiLabelMap.AddPeriodPlanTitle}</div>
	<div class='form-window-container'>
		<div class='row-fluid'>
			<div id="contentNotificationAddPeriodExits" class="popup-notification">
			</div>
			<div class='span12' class="margin-bottom10">
				<div class='span4 text-algin-right'>
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.POYeahTitle)}</label>
				</div>  
				<div class="span8">
					<div id="fromYear"></div>
		   		</div>
			</div>
			<div class="form-action">
		        <div class='row-fluid'>
		            <div class="span12 margin-top20" style="margin-bottom:10px;">
		                <button id="alterCancel" class='btn btn-danger form-action-button pull-right' style="margin-right:20px !important;"><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
		                <button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		            </div>
		        </div>
		    </div>
		</div> 
	</div>
</div>

<div id="jqxNotificationPeriodCustomSuccess" >
	<div id="notificationAddPeriodCustomSuccess">
	</div>
</div>

<div id="jqxNotificationPeriodCustomExits" >
	<div id="notificationAddPeriodCustomExits">
	</div>
</div>

<div id="jqxNotificationPeriodCustomExitsByPO" >
	<div id="notificationAddPeriodCustomExitsByPO">
	</div>
</div>
 
<script>
	$('#document').ready(function(){
		$("#jqxEditorWindow").jqxWindow({
			maxWidth: 640, minWidth: 640, minHeight: 360, maxHeight: 360, resizable: true, theme: 'olbius' ,isModal: true, autoOpen: false, initContent : function(){
				$('#editor').jqxEditor({
		            height: '85%',
		            width: '100%',
		            theme: 'olbius',
		        });
			},
		});
		$("#planName").jqxInput({placeHolder: '${StringUtil.wrapString(uiLabelMap.LogEnterDataText)}', height: 25, width: '200'});
	});
	
	$("#jqxNotificationPeriodCustomSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationAddPeriodSuccess", opacity: 0.9, autoClose: true, template: "success" });
	$("#jqxNotificationPeriodCustomExits").jqxNotification({ width: "100%", appendContainer: "#contentNotificationAddPeriodExits", opacity: 0.9, autoClose: true, template: "error" });
	$("#jqxNotificationPeriodCustomExitsByPO").jqxNotification({ width: "100%", appendContainer: "#contentNotificationAddPeriodCustomExits", opacity: 0.9, autoClose: true, template: "error" });
	
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	
	$('#alterpopupWindow').jqxValidator({
	    rules: [
					{ input: '#planName', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' },
					{ input: '#customTimePeriodPO', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
						rule: function (input, commit) {
							var value = $("#customTimePeriodPO").val();
							if (value) {
								return true;
							}
							return false;
						}
					},
	           ]
	});
	
	$("#okButton").click(function () {
		var des = $('#editor').val();
		var tmp = des.substring(5, des.length - 6);
		$("#planName").val(tmp);
		$("#jqxEditorWindow").jqxWindow('close');
	});
	$("#cancelButton").click(function () {
		$("#jqxEditorWindow").jqxWindow('close');
	});
	function showEditor(){
		$("#jqxEditorWindow").jqxWindow('open');
	}
	$("#addButtonSave").click(function() {
		<#if security.hasEntityPermission("PO_PLAN_APPROVED", "_VIEW", session)>
			if ($('#alterpopupWindow').jqxValidator('validate')) {
				var customTimePeriodPO = $("#customTimePeriodPO").val();
				var planName = $("#planName").val();
				var productPlanId = $("#productPlanId").val();
				var productPlanIdData = [];
				if(productPlanId != ""){
					productPlanIdData = productPlanId.split(",");
				}
				bootbox.confirm("${uiLabelMap.LogAddNewReally}", function(result) {
		            if(result) {
		            	$.ajax({
		    				url: "createPlanByPO",
		    				type: "POST",
		    				data: {customTimePeriodPO: customTimePeriodPO, planName: planName, productPlanId: productPlanIdData},
		    				dataType: "json",
		    				success: function(data) {
		    				}
		    			}).done(function(data) {
		    				if(data["value"] == "success"){
		    					$("#customTimePeriodPO").jqxDropDownList('clearSelection'); 
			    				$("#planName").val("");
			    				window.location.href = "ListPlanByPO";
		    				}else{
		    					$("#notificationAddPeriodCustomExitsByPO").text('${StringUtil.wrapString(uiLabelMap.PONotifiCheckProductPlanItemExits)}');
		    					$("#jqxNotificationPeriodCustomExitsByPO").jqxNotification('open');
		    				}
		    			});
		            }
				});    
			}
		<#else>   
			if ($('#alterpopupWindow').jqxValidator('validate')) {
				var customTimePeriodPO = $("#customTimePeriodPO").val();
				var planName = $("#planName").val();
				bootbox.confirm("${uiLabelMap.LogAddNewReally}", function(result) {
		            if(result) {
		            	$.ajax({
		    				url: "createPlanByPONotPO",
		    				type: "POST",
		    				data: {customTimePeriodPO: customTimePeriodPO, planName: planName},
		    				dataType: "json",
		    				success: function(data) {
		    				}
		    			}).done(function(data) {
		    				if(data["value"] == "success"){
		    					$("#customTimePeriodPO").jqxDropDownList('clearSelection'); 
			    				$("#planName").val("");
			    				window.location.href = "ListPlanByPO";
		    				}else{
		    					$("#notificationAddPeriodCustomExitsByPO").text('${StringUtil.wrapString(uiLabelMap.PONotifiCheckProductPlanItemExits)}');
		    					$("#jqxNotificationPeriodCustomExitsByPO").jqxNotification('open'); 
		    				}
		    			});
		            }
				});    
			}
		</#if>
	});
	
	$('#alterpopupWindowAddPeriodPlan').jqxValidator({
        rules: [
	               { input: '#fromYear', message: '${uiLabelMap.POCheckYeahPeriodTitle}', action: 'valueChanged, blur', 
	            	   rule: function () {
	            		    var fromYear = $('#fromYear').val();
		            	    if(fromYear == ""){
		            	    	return false; 
		            	    }else{
		            	    	var dateCurrent = new Date();
		            	    	var yyyy = dateCurrent.getFullYear();
								if(yyyy <= fromYear){
									return true; 
								}else{
									return false; 
								}
		            	    }
	            	    }
	               } 
	           ]
    });
	
	$("#customTimePeriodPO").jqxDropDownList({ source: listCustomTimePeriodData, displayMember: 'periodName', valueMember: 'customTimePeriodId', theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', autoDropDownHeight: true, disabled: false });
	<#if security.hasEntityPermission("PO_PLAN_APPROVED", "_VIEW", session)>
	$("#productPlanId").jqxDropDownList({ checkboxes: true, source: productPlanHeaderData, displayMember: 'productPlanName', valueMember: 'productPlanId', theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', autoDropDownHeight: true, disabled: false,
		renderer: function (index, label, value) 
		{
		    var datarecord = productPlanHeaderData[index];
		    return datarecord.productPlanName + " [" + datarecord.productPlanId + "]";
		}
	});
	<#else>   
	</#if>
	$("#fromYear").jqxNumberInput({spinButtons: true , min:0, decimalDigits: 0 });
	$("#alterpopupWindowAddPeriodPlan").jqxWindow({
		maxWidth: 1000, minWidth: 300, height: 140, width:450, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
	});
	
	function addPeriodPlanByPO(){
		$('#alterpopupWindowAddPeriodPlan').jqxWindow('open');
	}
	
	$("#alterSave").click(function () {
		var fromYear = $('#fromYear').val();
		var validate = $('#alterpopupWindowAddPeriodPlan').jqxValidator('validate');
		if(validate != false){
			bootbox.confirm("${uiLabelMap.LogAddNewReally}", function(result) {
	            if(result) {
	            	createCustomTimePeriodWeekOfMonthByPO(fromYear);
	            }
			});    
		}
    });
	
	function createCustomTimePeriodWeekOfMonthByPO(fromYear){
		$.ajax({
			url: "customTimePeriodWeekOfMonthByPO",
			type: "POST",
			async: false,
			data: {fromYear: fromYear},
			dataType: "json",
			success: function(data) {  
			}
		}).done(function(data) {
			var value = data["value"];
			if(value == "success"){
				$('#alterpopupWindowAddPeriodPlan').jqxWindow('close');
				$("#notificationAddPeriodCustomSuccess").text('${StringUtil.wrapString(uiLabelMap.NotifiAddSucess)}');
				$("#jqxNotificationPeriodCustomSuccess").jqxNotification('open');
			}
			if(value == "exits"){
				$("#notificationAddPeriodCustomExits").text('${StringUtil.wrapString(uiLabelMap.PONotifiAddCustomTimeExits)}');
				$("#jqxNotificationPeriodCustomExits").jqxNotification('open');
			}
		});
	}
	
	$('#alterpopupWindowAddPeriodPlan').on('close', function (event) { 
		$('#fromYear').val("");
		$('#alterpopupWindowAddPeriodPlan').jqxValidator('hide');
	}); 

</script>
