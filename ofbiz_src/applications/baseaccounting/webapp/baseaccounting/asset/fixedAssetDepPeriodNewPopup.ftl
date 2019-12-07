<div id="newDepPeriod" style="display:none">
	<div>${uiLabelMap.BACCNewDepreciationPeriod}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<form id="formNewDepPeriod">
					<div class="span12 form-window-content-custom">
						<div class='row-fluid'>
							<div class='span5'>
								<label class="required">${uiLabelMap.BACCPeriod}</label>
							</div>
							<div class='span7'>
								<div id="month" style="display: inline-block; float: left;">
								</div>
								<div id="year" style="display: inline-block; float: left; margin-left: 2%;">
								</div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label class="required">${uiLabelMap.CommonFromDate}</label>
							</div>
							<div class='span7'>
								<div id="fromDate"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label>${uiLabelMap.CommonThruDate}</label>
							</div>
							<div class='span7'>
								<div id="thruDate"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label>${uiLabelMap.BACCDescription}</label>
							</div>
							<div class='span7'>
								<textarea id="comments" class="text-popup" style="width: 78% !important"></textarea>
					   		</div>
						</div>
					</form>
				</div>
			</div>
		</div>
		<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.BACCSave}</button>
				<button id="alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.BACCCancel}</button>
	   		</div>
		</div>
	</div>
</div>
<script>
	var THEME = 'olbius';
	var OLBNewDepPeriod = function(){
	}
	OLBNewDepPeriod.initWindow = function(){
		$("#newDepPeriod").jqxWindow({
			width: '600', height: 300, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme: THEME,
			initContent: function () {
				$("#month").jqxDropDownList({ source: monthPeriodData, placeHolder: '${uiLabelMap.filterchoosestring}', theme: THEME, width: '40%', height: '25px', valueMember: 'value', displayMember: 'text'});
				$('#year').jqxNumberInput({digits: 5, inputMode: 'simple', decimal: yyyy, width: '37%',decimalDigits: 0, theme: THEME, spinButtons: true });
				$("#fromDate").jqxDateTimeInput({formatString: "dd/MM/yyyy", disabled: true, height: '25px', width: '80%', theme: THEME});
				$("#thruDate").jqxDateTimeInput({formatString: "dd/MM/yyyy", disabled: true, height: '25px', width: '80%', theme: THEME});
				accutils.setValueDropDownListOnly($("#month"), mm, 'value', monthPeriodData);
				$("#fromDate").jqxDateTimeInput('setDate', new Date(yyyy, mm - 1, 1));
				$("#thruDate").jqxDateTimeInput('setDate', new Date(yyyy, mm, 0));
				$("#comments").val('Kỳ khấu hao tháng ' + mm + '/' + yyyy);
				$('#month').on('change', function (event) {
				 	var args = event.args;
				    if (args && $('#year').val()) {
					    var mm = args.item.value;
					    var yyyy = $('#year').val();
					    $("#fromDate").jqxDateTimeInput('setDate', new Date(yyyy, mm - 1, 1));
						$("#thruDate").jqxDateTimeInput('setDate', new Date(yyyy, mm, 0));
						$("#comments").val('Kỳ khấu hao tháng ' + mm + '/' + yyyy);
				    }
				});
				$('#year').on('valueChanged', function (event) {
					var args = event.args;
					if (args && $('#month').val()) {
					    var yyyy = args.value;
					    var mm = $('#month').val();
					    $("#fromDate").jqxDateTimeInput('setDate', new Date(yyyy, mm - 1, 1));
						$("#thruDate").jqxDateTimeInput('setDate', new Date(yyyy, mm, 0));
						$("#comments").val('Kỳ khấu hao tháng ' + mm + '/' + yyyy);
				    }
				});
			}
		});
	}
	
	OLBNewDepPeriod.openWindow = function(){
		
		$("#newDepPeriod").jqxWindow('open');
	}
	
	OLBNewDepPeriod.closeWindow = function(){
		$("#newDepPeriod").jqxWindow('close');
	}
	
	OLBNewDepPeriod.bindEvent = function(){
		$('#alterSave').on('click', function(){
			var submitedData = {};
			submitedData['periodName'] = $('#comments').val();
			submitedData['periodTypeId'] = 'DEP_MONTH';
			submitedData['organizationPartyId'] = currentOrgId;
			submitedData['isClosed'] = 'N';
			var fromDate = ($('#fromDate').jqxDateTimeInput('getDate'));
			submitedData['fromDate'] = accutils.getTimestamp(fromDate);
			var thruDate = ($('#thruDate').jqxDateTimeInput('getDate'));
			submitedData['thruDate'] = accutils.getTimestamp(thruDate);
			
			//Send Ajax Request
			$.ajax({
				url: 'createCustomTimePeriod',
				type: "POST",
				data: submitedData,
				dataType: 'json',
				async: false,
				success : function(data) {
					if(!data._ERROR_MESSAGE_ && !data._ERROR_MESSAGE_LIST_){
						OLBNewDepPeriod.closeWindow();
						$('#jqxgridDepPeriod').jqxGrid('updatebounddata');
						$('#containerjqxgridDepPeriod').empty();
                        $('#jqxNotificationjqxgridDepPeriod').jqxNotification({ template: 'success'});
                        $("#notificationContentjqxgridDepPeriod").text(wgaddsuccess);
                        $("#jqxNotificationjqxgridDepPeriod").jqxNotification("open");
					}else if(data._ERROR_MESSAGE_){
						accutils.confirm.confirm(data._ERROR_MESSAGE_, function(){}, '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
					}else if(data._ERROR_MESSAGE_LIST_){
						accutils.confirm.confirm(data._ERROR_MESSAGE_LIST_, function(){}, '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
					}
				}
			});
		});
	}
	
	$(function(){
		OLBNewDepPeriod.initWindow();
		OLBNewDepPeriod.bindEvent();
	})
	
</script>