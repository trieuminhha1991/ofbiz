<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/delys/images/js/import/notify.js"></script>

<form action="createNewImportPlan" id="createImportPlan" method="POST">

	<div class="row-fluid margin-top10">
		<div class="span12">
			<div class="span4"><label class="pull-right asterisk" style="margin-top: 4px;">${uiLabelMap.TimeImport}</label></div>
			<div class="span8"><div id="customTimePeriodImport" name="customTimePeriodImport"></div></div>
		</div>
	</div>
	
	<div class="row-fluid margin-top10">
		<div class="span12">
			<div class="span4"><label class="pull-right asterisk" style="margin-top: 5px;">${uiLabelMap.FormFieldTitle_areaId}</label></div>
			<div class="span8"><div id="areaId" name="areaId"></div></div>
		</div>
	</div>
	
	<div class="row-fluid margin-top10">
		<div class="span12">
			<div class="span4"><label class="pull-right asterisk" style="margin-top: 5px;">${uiLabelMap.PlanName}</label></div>
			<div class="span8"><input type="text" id="namePlan" name="namePlan" /></div>
		</div>
	</div>
	
	<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity: 0.4;">
	<div class="row-fluid">
	    <div class="span12 margin-top10">
	    	<div class="span12">
	    		<button id='alterSave' type="button" class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.Create}</button>
			</div>
	    </div>
	</div>

</form>
<#assign listCustomTimePeriod = delegator.findList("CustomTimePeriod", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("periodTypeId", "IMPORT_YEAR")), null, Static["org.ofbiz.base.util.UtilMisc"].toList("-fromDate"), null, false)>

<script>
	var listCustomTimePeriod = [
							<#if listCustomTimePeriod?exists>
								<#list listCustomTimePeriod as item>
								{
									customTimePeriodId: '${item.customTimePeriodId?if_exists}',
									periodName: getPeriodName("${StringUtil.wrapString(item.periodName?if_exists)}")
								},
								</#list>
							</#if>
	                     ];
	var listArea = [
	                {areaId: "RSM_R1", description: "${uiLabelMap.northArea}" },
	                {areaId: "RSM_R2", description: "${uiLabelMap.southArea}" }
	                ];
	function getPeriodName(periodNameOriginal) {
		periodNameOriginal = periodNameOriginal.toString();
		return periodNameOriginal.split(": ")[1]?periodNameOriginal=periodNameOriginal.split(": ")[1]:periodNameOriginal=periodNameOriginal;
	}
	$("#customTimePeriodImport").jqxDropDownList({ source: listCustomTimePeriod, width: 218, height: 30, displayMember: 'periodName', valueMember: 'customTimePeriodId', theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', dropDownHeight: 250 });
	$("#areaId").jqxDropDownList({ source: listArea, width: 218, height: 30, displayMember: 'description', valueMember: 'areaId', theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', autoDropDownHeight: true });
	
	$("#alterSave").click(function() {
		if ($('#createImportPlan').jqxValidator('validate')) {
			$('#createImportPlan').submit();
		}
	});
	$('#createImportPlan').jqxValidator({
	    rules: [
					{ input: '#namePlan', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' },
					{ input: '#customTimePeriodImport', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
						rule: function (input, commit) {
							var value = $("#customTimePeriodImport").val();
							if (value) {
								return true;
							}
							return false;
						}
					},
					{ input: '#areaId', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
						rule: function (input, commit) {
							var value = $("#areaId").val();
							if (value) {
								return true;
							}
							return false;
						}
					},
	           ]
	});
	$('#customTimePeriodImport').on('change', function (event){ 
		var internalPartyId = $("#areaId").val();
		if (internalPartyId) {
			var args = event.args;
		    if (args) {
			    var index = args.index;
			    var item = args.item;
			    var label = item.label;
			    var value = item.value;
			    getInfoImportPlan(value, internalPartyId);
			}
		}
	});
	$('#areaId').on('change', function (event){ 
		var customTimePeriodId = $("#customTimePeriodImport").val();
		if (customTimePeriodId) {
			var args = event.args;
			if (args) {
				var index = args.index;
				var item = args.item;
				var label = item.label;
				var value = item.value;
				getInfoImportPlan(customTimePeriodId, value, changeWhenImportPlanAvalible);
			}
		}
	});
	function getInfoImportPlan(customTimePeriodId, internalPartyId, callback) {
		var importPlan = [];
		$.ajax({
			  url: "getInfoImportPlanAjax",
			  type: "POST",
			  data: {customTimePeriodId: customTimePeriodId, internalPartyId: internalPartyId},
			  success: function(res) {
				  importPlan = res["importPlan"];
			  }
		  	}).done(function() {
		  		callback(importPlan);
		  	});
	}
	function changeWhenImportPlanAvalible(importPlan) {
		if (importPlan) {
			$("#namePlan").notify("${StringUtil.wrapString(uiLabelMap.ImportPlanAvalible)}", "info");
			$("#alterSave").text("${StringUtil.wrapString(uiLabelMap.CommonUpdate)}");
			$("#namePlan").val(importPlan.productPlanName);
		} else {
			$("#alterSave").text("${StringUtil.wrapString(uiLabelMap.Create)}");
			$("#namePlan").val("");
		}
	}
</script>