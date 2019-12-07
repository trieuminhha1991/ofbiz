<div id="alterpopupWindow" style="display:none">
	<div>${uiLabelMap.BSCreateNewCustomTimePeriod}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div id="containerCTP" style="background-color: transparent; overflow: auto;"></div>
		    <div id="jqxNotificationCTP" style="margin-bottom:5px">
		        <div id="notificationContentCustomTimePeriod">
		        </div>
		    </div>
		    <input type="hidden" id="wn_periodNum" value="1"/>
			<div class="row-fluid">
				<div class="span6 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_periodName" class="required">${uiLabelMap.BSSalesPeriodName}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_periodName" class="span12" maxlength="100" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_periodTypeId" class="required">${uiLabelMap.BSPeriodType}</label>
						</div>
						<div class='span7'>
							<div id="wn_periodTypeId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_parentPeriodId">${uiLabelMap.BSParentPeriodId}</label>
						</div>
						<div class='span7'>
							<div id="wn_parentPeriodId">
								<div id="wn_parentPeriodGrid"></div>
							</div>
				   		</div>
					</div>
				</div>
				<div class="span6 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_fromDate" class="required">${uiLabelMap.BSFromDate}</label>
						</div>
						<div class='span7'>
							<div id="wn_fromDate"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_thruDate" class="required">${uiLabelMap.BSThruDate}</label>
						</div>
						<div class='span7'>
							<div id="wn_thruDate"></div>
				   		</div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		pageCommonNewCTP.init();
	});
	var pageCommonNewCTP = (function(){
		$.jqx.theme = 'olbius';
		var theme = $.jqx.theme;
		var formatString = 'dd/MM/yyyy HH:mm:ss';
		
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			$("#wn_periodName").jqxInput({height: 25, theme: theme, maxLength: 100});
			
			$("#alterpopupWindow").jqxWindow({
				width: 960, height: 220, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme: theme
			});
			$("#containerCTP").width('100%');
            $("#jqxNotificationCTP").jqxNotification({ 
            	icon: { width: 25, height: 25, url: '/aceadmin/assets/images/info.jpg'}, 
            	width: '100%', 
            	appendContainer: "#containerCTP", 
            	opacity: 1, autoClose: true, template: "success" 
            });
            
            $("#wn_fromDate").jqxDateTimeInput({width: '98%', height: 25, allowNullDate: true, value: null, formatString: formatString});
			$("#wn_thruDate").jqxDateTimeInput({width: '98%', height: 25, allowNullDate: true, value: null, formatString: formatString});
			
			<#--
			$("#wn_yearPeriod").jqxNumberInput({width: '98%', height: 25, spinButtons: true, decimalDigits: 0, min: 1970, max: 9999, digits: 4, groupSeparator: '', promptChar: '#'});
			-->
			var nowTimestamp = new Date();
			var nowYear = nowTimestamp.getFullYear();
			var localDataYear = [];
			for (var i = (nowYear + 10); i > (nowYear - 10); i--) {
				localDataYear.push({'yearValue': i});
			}
			var configYearPeriod = {
				width: '95%',
				placeHolder: '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}',
				key: 'yearValue',
				value: 'yearValue',
				autoDropDownHeight: false,
				useUrl: false,
			};
			new OlbDropDownList($("#wn_yearPeriod"), localDataYear, configYearPeriod, [nowYear]);
		};
		var initElementComplex = function(){
			var configPeriodType = {
				placeHolder: '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}',
				useUrl: false,
				key: 'periodTypeId',
				value: 'description',
				autoDropDownHeight: true
			}
			new OlbDropDownList($("#wn_periodTypeId"), periodTypeData, configPeriodType, []);
			
			var configParentPeriod = {
				useUrl: true,
				root: 'results',
				widthButton: '100%',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [
					{name: 'customTimePeriodId', type: 'string'}, 
					{name: 'parentPeriodId', type: 'string'},
					{name: 'periodNum', type: 'string'},
					{name: 'periodName', type: 'string'},
					{name: 'fromDate', type: 'date', other: 'Timestamp'},
					{name: 'thruDate', type: 'date', other: 'Timestamp'},
				],
				columns: [
					{text: '${StringUtil.wrapString(uiLabelMap.BSCustomTimePeriodId)}', datafield: 'customTimePeriodId', width: '20%'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSParentPeriodId)}', datafield: 'parentPeriodId'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSPeriodName)}', datafield: 'periodName', width: '20%'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSFromDate)}', datafield: 'fromDate', cellsformat: 'dd/MM/yyyy', filtertype:'range', width: '16%'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSThruDate)}', datafield: 'thruDate', cellsformat: 'dd/MM/yyyy', filtertype:'range', width: '16%'},
				],
				url: 'JQListCustomTimePeriodSales',
				useUtilFunc: true,
				dropDownHorizontalAlignment: 'right'
			};
			new OlbDropDownButton($("#wn_parentPeriodId"), $("#wn_parentPeriodGrid"), null, configParentPeriod, []);
		};
		var initEvent = function(){
			$("#alterSave").on('click', function(){
				if(!$('#alterpopupWindow').jqxValidator('validate')) return false;
		    	var dataMap = {
		    		parentPeriodId: $('#wn_parentPeriodId').val(),
		    		periodName: $('#wn_periodName').val(),
		    		periodTypeId: $('#wn_periodTypeId').val(),
		    		fromDate: $("#wn_fromDate").jqxDateTimeInput('getDate') != null ? $("#wn_fromDate").jqxDateTimeInput('getDate').getTime() : "",
		    		thruDate: $("#wn_thruDate").jqxDateTimeInput('getDate') != null ? $("#wn_thruDate").jqxDateTimeInput('getDate').getTime() : "",
		    		periodNum: $("#wn_periodNum").val(),
		    	};
		    	
		    	$.ajax({
					type: 'POST',
					url: 'createCustomTimePeriodAjax',
					data: dataMap,
					beforeSend: function(){
						$("#loader_page_common").show();
					},
					success: function(data){
						processResult(data, "CTP");
						resetWindowPopupCreate();
					},
					error: function(data){
						alert("Send request is error");
					},
					complete: function(data){
						$("#loader_page_common").hide();
						$("#alterpopupWindow").jqxWindow('close');
					},
				});
		    });
		    
		};
		var resetWindowPopupCreate = function(){
	    	$('#wn_parentPeriodId').val(null);
	    	$('#wn_periodName').val(null);
	    	$('#wn_periodTypeId').jqxDropDownList('clearSelection'); 
	    	$("#wn_fromDate").jqxDateTimeInput('val', null);
	    	$("#wn_thruDate").jqxDateTimeInput('val', null);
	    	$("#wn_periodNum").val(null);
		};
		var processResult = function(data, suffix){
			if (data.thisRequestUri == "json") {
        		var errorMessage = "";
		        if (data._ERROR_MESSAGE_LIST_ != null) {
		        	for (var i = 0; i < data._ERROR_MESSAGE_LIST_.length; i++) {
		        		errorMessage += "<p><b>${uiLabelMap.BSErrorUper}</b>: " + data._ERROR_MESSAGE_LIST_[i] + "</p>";
		        	}
		        }
		        if (data._ERROR_MESSAGE_ != null) {
		        	errorMessage += "<p><b>${uiLabelMap.BSErrorUper}</b>: " + data._ERROR_MESSAGE_ + "</p>";
		        }
		        if (errorMessage != "") {
		        	$('#container' + suffix).empty();
		        	$('#jqxNotification' + suffix).jqxNotification({ template: 'info'});
		        	$("#jqxNotification" + suffix).html(errorMessage);
		        	$("#jqxNotification" + suffix).jqxNotification("open");
		        } else {
		        	$('#container').empty();
		        	$('#jqxNotification').jqxNotification({ template: 'info'});
		        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
		        	$("#jqxNotification").jqxNotification("open");
		        	
		        	reloadCustomTimePeriodTreeGrid();
		        }
		        return false;
        	} else {
        		return true;
        	}
		};
		var reloadCustomTimePeriodTreeGrid = function(){
			$("#jqxCustomTimePeriod").jqxTreeGrid('updateBoundData');
		};
		var initValidateForm = function(){
			var extendRules = [
				{input: '#wn_fromDate, #wn_thruDate', message: '${uiLabelMap.BSStartDateMustLessThanOrEqualFinishDate}', action: 'valueChanged', 
					rule: function(input, commit){
						return OlbValidatorUtil.validElement(input, commit, 'validCompareTwoDate', {paramId1 : "wn_fromDate", paramId2 : "wn_thruDate"});
					}
				},
			];
			var mapRules = [
				{input: "#wn_periodTypeId", type: "validObjectNotNull", objType: "dropDownList"},
				{input: "#wn_periodName", type: "validInputNotNull"},
				{input: "#wn_fromDate", type: "validDateTimeInputNotNull"},
				{input: "#wn_thruDate", type: "validDateTimeInputNotNull"},
			];
			new OlbValidator($("#alterpopupWindow"), mapRules, extendRules, {position: 'bottom'});
		};
		return {
			init: init
		}
	}());
</script>
<#--
<div class='row-fluid'>
	<div class='span5'>
		<label for="wn_customTimePeriodId">${uiLabelMap.BSSalesCustomTimePeriodId}</label>
	</div>
	<div class='span7'>
		<input type="text" id="wn_customTimePeriodId" class="span12" maxlength="20" value=""/>
	</div>
</div>
-->
<#--$("#wn_customTimePeriodId").jqxInput({height: 25, theme: theme, maxLength: 20});-->
<#--customTimePeriodId: $('#wn_customTimePeriodId').val(),-->
<#--{input: '#wn_customTimePeriodId', message: '${uiLabelMap.BSThisFieldMustNotByContainSpecialCharacter}', action: 'blur', rule: 
	function (input, commit) {
		return OlbValidatorUtil.validElement(input, commit, 'validCannotSpecialCharactor');
	}
},-->