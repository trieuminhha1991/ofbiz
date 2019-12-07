<@jqGridMinimumLib/>
<script src="/aceadmin/jqw/jqwidgets/jqxdata.js" type="text/javascript"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js" type="text/javascript"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxdatatable.js" type="text/javascript"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxlistbox.js" type="text/javascript" ></script>
<script src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js" type="text/javascript"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxdropdownlist.js" type="text/javascript" ></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets//jqxnotification.js"></script>
<script type="text/javascript" src="/aceadmin/assets/jsdelys/formatCurrency.js"></script>

<div class="row-fluid" id="jqxNotifyContainer">
	
</div>
<div id="jqxNotify">
	<div id="ntfContent"></div>
</div>
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.PayrollTableCalcResults}</h4>
		<div class="widget-toolbar none-content" style="width: 550px">
			<div id="dropdownlist" style="margin-top: 5px; float: right;"></div>			
			<#if payrollTableRecord.statusId == "PYRLL_TABLE_CALC">
				<button id="sendProposalPayrollTable" class="grid-action-button fa-paper-plane" style="float: right;">${uiLabelMap.sendProposalPayrollTable}</button>
			</#if>
			<#if parameters.requestId?exists>
				<#assign isApprovalPerm = Static["com.olbius.workflow.WorkFlowUtils"].checkApprvalPerm(delegator, userLogin.partyId, parameters.requestId)/>
				<#if payrollTableRecord.statusId == "PYRLL_WAIT_APPR" && isApprovalPerm>
					<button id="recalculateRequest" class="grid-action-button icon-remove" style="float: right;">${uiLabelMap.RequestRecalculate}</button>
					<button id="approvalPayroll" class="grid-action-button icon-ok" style="float: right;">${uiLabelMap.CommonOk}</button>
				</#if>
			
				<#assign workFlowRequestAction = delegator.findOne("WorkFlowRequest", Static["org.ofbiz.base.util.UtilMisc"].toMap("requestId", parameters.requestId), false)/>
				<#if payrollTableRecord.statusId == "PYRLL_TABLE_RECALC" && userLogin.partyId == workFlowRequestAction.partyId>
					<button id="ppslPrllTableAfterReCalc" class="grid-action-button fa-paper-plane" style="float: right;">${uiLabelMap.SendProposalAfterRecalc}</button>
				</#if>
			</#if>
		</div>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div class="clearfix" style="margin-bottom: 5px">
				<div class="pull-left alert alert-success inline no-margin">
					<i class="bigger-120 blue"></i>
					${uiLabelMap.CommonStatus}:&nbsp;
					<#assign statusItem = delegator.findOne("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", payrollTableRecord.statusId), false)/>
					<span id='payrolStt'>${statusItem.description?if_exists}</span>
				</div>
			</div>
		</div>
		<div id="payrollTableData">
			${screens.render("component://hrolbius/widget/PayrollScreens.xml#PayrollTableRecord")}
		</div>
			
	</div>	
</div>

<script type="text/javascript">
$(document).ready(function () {  
	<#assign listTimestamp = payrollTimestampResult.get("listTimestamp")>
	<#if listTimestamp?has_content>
		var theme = 'olbius';
		var dataTimestamp = new Array();
		<#list listTimestamp as timestamp>
			dataTimestamp.push({"date": '${uiLabelMap.CommonFromDate} ${timestamp.get("fromDate")?string["dd/MM/yyyy"]} ${uiLabelMap.CommonThruDate} ${timestamp.get("thruDate")?string["dd/MM/yyyy"]}', 'valueParam': '${timestamp.get("fromDate").getTime()}'});
		</#list>
		var source = {
           localdata: dataTimestamp,
           datatype: "array"
        };
		var dataAdapter = new $.jqx.dataAdapter(source);
		$('#dropdownlist').jqxDropDownList({ selectedIndex: 0,  source: dataAdapter, displayMember: "date", valueMember: "valueParam", height: 25, width: 280, autoDropDownHeight:true, theme: theme});
		
		$('#dropdownlist').on('select', function (event){
		    var args = event.args;
		    if (args) {
			    // index represents the item's index.                
			    var index = args.index;
			    var item = args.item;
			    // get item's label and value.
			    var label = item.label;
			    var value = item.value;
			    jQuery.ajax({
			    	url: "<@ofbizUrl>getPayrollTableRecord</@ofbizUrl>",
			    	type: "POST",
			    	data:{fromDate: value, payrollTableId: "${parameters.payrollTableId}"},
			    	success:function (data){
			    		jQuery("#payrollTableData").html(data);
			    	}
			    });
			}                        
		});
	</#if>
	
	<#if payrollTableRecord.statusId == "PYRLL_TABLE_CALC">
		$("#sendProposalPayrollTable").click(function(event){
			var data = {payrollTableId: "${parameters.payrollTableId}"};
	   		sendAjaxRequest('sendProposalPayrollTable', data, $("#jqxNotify"), $("#ntfContent"), $('#treeGrid'), removeSendProposalBtn);
		});
	</#if>
	<#if payrollTableRecord.statusId == "PYRLL_TABLE_RECALC">
		$("#ppslPrllTableAfterReCalc").click(function(){
			<#if parameters.requestId?exists>	
				var data = {requestId: "${parameters.requestId}", actionTypeId: "RESTART"};
				<#if parameters.ntfId?exists>
					data["ntfId"] = "${parameters.ntfId}";
				</#if>
				sendAjaxRequest('approvalPayrollTable', data, $("#jqxNotify"), $("#ntfContent"), $('#treeGrid'), removePpslRecalcBtn);
			</#if>
		});
	</#if>
	<#if payrollTableRecord.statusId == "PYRLL_WAIT_APPR"> 
		$("#approvalPayroll").click(function(){
			<#if parameters.requestId?exists>
				var data = {requestId: "${parameters.requestId}", actionTypeId: "APPROVE"};
				<#if parameters.ntfId?exists>
					data["ntfId"] = "${parameters.ntfId}";
				</#if>
	    		sendAjaxRequest('approvalPayrollTable', data, $("#jqxNotify"), $("#ntfContent"), $('#treeGrid'), removeApprBtn);
    		</#if>
		});
		$("#recalculateRequest").click(function(){
			<#if parameters.requestId?exists>
				var data = {requestId: "${parameters.requestId}", actionTypeId: "DENY"};
				<#if parameters.ntfId?exists>
					data["ntfId"] = "${parameters.ntfId}";
				</#if>
	    		sendAjaxRequest('approvalPayrollTable', data, $("#jqxNotify"), $("#ntfContent"), $('#treeGrid'), removeApprBtn);
			</#if>
		});
	 </#if> 
	//initJqxNotify();
});

<#if payrollTableRecord.statusId == "PYRLL_TABLE_CALC">
	function removeSendProposalBtn(){
		$("#sendProposalPayrollTable").remove();
	}
</#if>
<#if payrollTableRecord.statusId == "PYRLL_TABLE_RECALC">
	function removePpslRecalcBtn(){
		$("#ppslPrllTableAfterReCalc").remove();
	}
</#if>

<#if payrollTableRecord.statusId == "PYRLL_WAIT_APPR">
	function removeApprBtn(){
		$("#approvalPayroll").remove();
		$("#recalculateRequest").remove();
	}
</#if>

function sendAjaxRequest(url, data, notifyEle, ntfContentEle, treeGridEle, callBackFunc){
	treeGridEle.jqxTreeGrid({disabled: true});
	notifyEle.jqxNotification('closeLast');
	$.ajax({
		url: url,
		data: data,
		type: 'POST',
		success: function(data){
			if(data.responseMessage == "success" || data._EVENT_MESSAGE_){
				var messages = "";
				if(data.successMessage){
					messages = data.successMessage;
				}else if(data._EVENT_MESSAGE_){
					messages = data._EVENT_MESSAGE_;
				}
				$("#jqxNotifyContainer").empty();
				notifyEle.empty();
				notifyEle.jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, 
					autoClose: false, template: "info", appendContainer: "#jqxNotifyContainer"});
				notifyEle.text(messages);
				notifyEle.jqxNotification("open");
				if(callBackFunc){
					callBackFunc();
				}
				if(data.statusPayroll){
					$("#payrolStt").text(data.statusPayroll);
				}
			}else{
				var messageErr = "";
				if(data.errorMessage){
					messageErr = data.errorMessage;
				}else if(data._ERROR_MESSAGE_){
					messageErr = data._ERROR_MESSAGE_;
				}
				$("#jqxNotifyContainer").empty();
				notifyEle.empty();
				notifyEle.jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, 
					autoClose: false, template: "error", appendContainer: "#jqxNotifyContainer"});
				notifyEle.text(messageErr);
				notifyEle.jqxNotification("open");
			}
		},
		complete: function(){
			treeGridEle.jqxTreeGrid({disabled: false});	
		}
	});
}

/* function initJqxNotify(){
	$("#jqxNotify").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, 
		autoClose: false, template: "info", appendContainer: "#jqxNotifyContainer"});
} */ 
</script>