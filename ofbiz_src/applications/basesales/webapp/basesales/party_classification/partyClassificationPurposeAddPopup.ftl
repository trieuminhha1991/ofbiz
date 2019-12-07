<div id="windowPromoRuleNameEdit" style="display:none">
	<div>${uiLabelMap.BSEditPromoRuleInfo}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span6 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span3'>
							<label class="required">${uiLabelMap.BSFromDate}</label>
						</div>
						<div class='span9'>
							<div id="wn_pp_fromDate"></div>
				   		</div>
					</div>
				</div>
				<div class="span6 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span3'>
							<label class="required">${uiLabelMap.BSThruDate}</label>
						</div>
						<div class='span9'>
							<div id="wn_pp_thruDate"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span3'>
						</div>
						<div class='span9'>
							<button class="btn btn-small btn-primary" id="wn_pp_btnFind">${uiLabelMap.BSFind}</button>
				   		</div>
					</div>
				</div>
			</div>
			<div>
				<div id="jqgridMostProfitableCustomer"></div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_pp_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.BSChoose}</button>
				<button id="wn_pp_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<div id="windowChoosePeriodNew" style="display:none">
	<div>${uiLabelMap.BSChoosePeriodApply}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSFromDate}</label>
						</div>
						<div class='span7'>
							<div id="wn_period_fromDate"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSThruDate}</label>
						</div>
						<div class='span7'>
							<div id="wn_period_thruDate"></div>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_period_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.BSAdd}</button>
				<button id="wn_period_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<@jqOlbCoreLib hasGrid=true/>
<script type="text/javascript">
	if (!customerArrayData) var customerArrayData = [];
	$(function(){
		OlbPartyClassifiPurposeAdd.init();
	});
	var OlbPartyClassifiPurposeAdd = (function(){
		var mostProfitableCustomerGRID;
		
		var init = function() {
			initElement();
			initElementComplex();
			initEvent();
		};
		var initElement = function() {
			jOlbUtil.windowPopup.create($("#windowPromoRuleNameEdit"), {maxWidth: 1080, width: 1080, height: 580, cancelButton: $("#wn_pp_alterCancel")});
			jOlbUtil.windowPopup.create($("#windowChoosePeriodNew"), {width: 380, height: 200, cancelButton: $("#wn_period_alterCancel")});
		};
		var initElementComplex = function() {
			var nowTime = new Date();
			var firstDayLastMonth = new Date(nowTime.getFullYear(), nowTime.getMonth() - 1, 1);
			var lastDayLastMonth = new Date(nowTime.getFullYear(), nowTime.getMonth(), 0);
			var firstDayMonth = new Date(nowTime.getFullYear(), nowTime.getMonth(), 1);
			var lastDayMonth = new Date(nowTime.getFullYear(), nowTime.getMonth() + 1, 0);
			
			jOlbUtil.dateTimeInput.create("#wn_pp_fromDate", {width: '98%', allowNullDate: true, value: null, showFooter: true});
			jOlbUtil.dateTimeInput.create("#wn_pp_thruDate", {width: '98%', allowNullDate: true, value: null, showFooter: true});
			$("#wn_pp_fromDate").jqxDateTimeInput('setDate', firstDayLastMonth);
			$("#wn_pp_thruDate").jqxDateTimeInput('setDate', lastDayLastMonth);
			
			jOlbUtil.dateTimeInput.create("#wn_period_fromDate", {width: '98%', allowNullDate: true, value: null, showFooter: true});
			jOlbUtil.dateTimeInput.create("#wn_period_thruDate", {width: '98%', allowNullDate: true, value: null, showFooter: true});
			$("#wn_period_fromDate").jqxDateTimeInput('setDate', firstDayMonth);
			$("#wn_period_thruDate").jqxDateTimeInput('setDate', lastDayMonth);
			
			var datafields = [
				{ name: 'customerId', type: 'string'},
				{ name: 'customerCode', type: 'string'},
				{ name: 'customerName', type: 'string'},
				{ name: 'channelName', type: 'string'},
				{ name: 'storeName', type: 'string'},
				{ name: 'total1', type: 'string'},
			];
			var columns = [
            	{ text: '${uiLabelMap.BSNo2}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
		    	  datafield: 'stt', columntype: 'number', width: '5%',
		    	  cellsrenderer: function (row, column, value) {
		    		  return '<div style=margin:4px;>' + (value + 1) + '</div>';
		    	  }
			 	}, 
                { text: '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}', datafield: 'customerCode', type: 'string', width: '14%', cellsalign: 'left'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSFullName)}', datafield: 'customerName', type: 'string'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSChannel)}', datafield: 'channelName', type: 'string', width: '16%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSProductStore)}', datafield: 'storeName', type: 'string', width: '16%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSValue)}', datafield: 'total1', type: 'number', width: '16%', cellsformat: 'f0', cellsalign: 'right'}
            ];
            var requestUrl = "JQGetListEvaluateMostProfitableCustomer&fromDate=" + firstDayLastMonth.getTime() + "&thruDate=" + lastDayLastMonth.getTime();
			var configPartyContactMechPurpose = {
				showdefaultloadelement: false,
				autoshowloadelement: false,
				dropDownHorizontalAlignment: 'right',
				datafields: datafields,
				columns: columns,
				useUrl: true,
				root: 'data',
				url: requestUrl,
				useUtilFunc: true,
				clearfilteringbutton: false,
				editable: false,
				filterable: false,
				sortable: false,
				alternativeAddPopup: 'alterpopupWindow',
				pagesize: 15,
				showtoolbar: false,
				editmode: 'click',
				width: '100%',
				bindresize: true,
				selectionmode: "checkbox",
			};
			mostProfitableCustomerGRID = new OlbGrid($("#jqgridMostProfitableCustomer"), null, configPartyContactMechPurpose, []);
		};
		var initEvent = function(){
			initEventGrid();
			
			$('#wn_pp_btnFind').on('click', function(){
				var fromDate = (new Date($("#wn_pp_fromDate").jqxDateTimeInput('getDate'))).getTime();
				var thruDate = (new Date($("#wn_pp_thruDate").jqxDateTimeInput('getDate'))).getTime();
				mostProfitableCustomerGRID.updateSource("jqxGeneralServicer?sname=JQGetListEvaluateMostProfitableCustomer&fromDate=" + fromDate + "&thruDate=" + thruDate);
			});
			$("#wn_pp_alterSave").on("click", function(){
				if (customerArrayData.length <= 0) {
					jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseRow}");
					return false;
				}
				$("#windowChoosePeriodNew").jqxWindow("open");
			});
			$("#wn_period_alterSave").on("click", function(){
				var dataMap = {};
				dataMap.partyClassificationGroupId = "${partyClassificationGroup.partyClassificationGroupId?if_exists}";
				dataMap.fromDate = $("#wn_period_fromDate").jqxDateTimeInput('getDate').getTime();
				dataMap.thruDate = $("#wn_period_thruDate").jqxDateTimeInput('getDate').getTime();
				dataMap.listCustomers = customerArrayData;
				$.ajax({
					type: 'POST',
					url: "addPartyToPartyClassification",
					data: dataMap,
					beforeSend: function(){
						$("#loader_page_common").show();
					},
					success: function(data){
						jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
						        	$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'info'});
						        	$("#jqxNotification").html(errorMessage);
						        	$("#jqxNotification").jqxNotification("open");
						        	return false;
								}, function(){
									$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'info'});
						        	$("#jqxNotification").html("${uiLabelMap.wgupdatesuccess}");
						        	$("#jqxNotification").jqxNotification("open");
						        	
						        	$("#windowChoosePeriodNew").jqxWindow("close");
						        	$("#windowPromoRuleNameEdit").jqxWindow("close");
						        	
						        	mostProfitableCustomerGRID.updateBoundData();
								}
						);
					},
					error: function(data){
						alert("Send request is error");
					},
					complete: function(data){
						$("#loader_page_common").hide();
					},
				});
			});
		};
		var initEventGrid = function(){
			mostProfitableCustomerGRID.on("bindingcomplete", function (event) {
				customerArrayData = [];
			});
			mostProfitableCustomerGRID.on('rowselect', function (event) {
				var args = event.args;
			    var rowBoundIndex = args.rowindex;
			    if (Object.prototype.toString.call(rowBoundIndex) === '[object Array]') {
			    	for (var i = 0; i < rowBoundIndex.length; i++) {
			    		processDataRowSelect(rowBoundIndex[i]);
			    	}
			    } else {
			    	processDataRowSelect(rowBoundIndex);
			    }
			});
			mostProfitableCustomerGRID.on('rowunselect', function (event) {
			    var args = event.args;
			    var rowBoundIndex = args.rowindex;
		    	var data = $("#jqgridMostProfitableCustomer").jqxGrid("getrowdata", rowBoundIndex);
		    	if (typeof(data) != 'undefined') {
		    		var quantityUomId = data.quantityUomId;
		    		var idStr = data.customerCode;
		    		if (idStr) {
		    			var index = customerArrayData.indexOf(idStr);
		    			if (index > -1) {
		    				customerArrayData.splice(index, 1);
		    			}
		    		}
		    	}
			});
			var processDataRowSelect = function(rowBoundIndex) {
				var data = $("#jqgridMostProfitableCustomer").jqxGrid("getrowdata", rowBoundIndex);
		    	if (data) {
		    		var idStr = data.customerCode;
		    		if (idStr) {
		    			if (customerArrayData.indexOf(idStr) < 0) {
		    				customerArrayData.push(idStr);
		    			}
		    		}
		    	}
			};
		};
		var openWindow = function(){
			$("#windowPromoRuleNameEdit").jqxWindow("open");
		};
		return {
			init: init,
			openWindow: openWindow,
		};
	}());
</script>