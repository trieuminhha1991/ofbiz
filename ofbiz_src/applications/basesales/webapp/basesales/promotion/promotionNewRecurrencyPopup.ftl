<div id="popupWindowRecurrenceNew" style="display:none">
	<div>${uiLabelMap.BSAddNewRecurrenceInfo}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="position:relative">
			<input type="hidden" id="recurrenceObjIdCurrentSelected" value=""/>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span3'>
							<label>${uiLabelMap.BSId}</label>
						</div>
						<div class='span8'>
							<input type="text" id="wn_recur_recurrenceInfoId" maxlength="60" value=""/>
				   		</div>
				   		<div class='span1'>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span3'>
							<label>${uiLabelMap.BSHourList}</label>
						</div>
						<div class='span2' style="padding-left: 17px">
							<div id="wn_recur_hour1">1</div>
							<div id="wn_recur_hour2">2</div>
							<div id="wn_recur_hour3">3</div>
							<div id="wn_recur_hour4">4</div>
							<div id="wn_recur_hour5">5</div>
							<div id="wn_recur_hour6">6</div>
				   		</div>
						<div class='span2' style="padding-left: 17px">
							<div id="wn_recur_hour7">7</div>
							<div id="wn_recur_hour8">8</div>
							<div id="wn_recur_hour9">9</div>
							<div id="wn_recur_hour10">10</div>
							<div id="wn_recur_hour11">11</div>
							<div id="wn_recur_hour12">12</div>
				   		</div>
						<div class='span2' style="padding-left: 17px">
							<div id="wn_recur_hour13">13</div>
							<div id="wn_recur_hour14">14</div>
							<div id="wn_recur_hour15">15</div>
							<div id="wn_recur_hour16">16</div>
							<div id="wn_recur_hour17">17</div>
							<div id="wn_recur_hour18">18</div>
				   		</div>
						<div class='span2' style="padding-left: 17px">
							<div id="wn_recur_hour19">19</div>
							<div id="wn_recur_hour20">20</div>
							<div id="wn_recur_hour21">21</div>
							<div id="wn_recur_hour22">22</div>
							<div id="wn_recur_hour23">23</div>
							<div id="wn_recur_hour24">24</div>
				   		</div>
				   		<div class='span1'>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span3'>
							<label>${uiLabelMap.BSDayList}</label>
						</div>
						<div class='span2' style="padding-left: 17px">
							<div id="wn_recur_day2">${uiLabelMap.CommonMonday}</div>
							<div id="wn_recur_day3">${uiLabelMap.CommonTuesday}</div>
				   		</div>
						<div class='span2' style="padding-left: 17px">
							<div id="wn_recur_day4">${uiLabelMap.CommonWednesday}</div>
							<div id="wn_recur_day5">${uiLabelMap.CommonThursday}</div>
				   		</div>
						<div class='span2' style="padding-left: 17px">
							<div id="wn_recur_day6">${uiLabelMap.CommonFriday}</div>
							<div id="wn_recur_day7">${uiLabelMap.CommonSaturday}</div>
				   		</div>
						<div class='span2' style="padding-left: 17px">
							<div id="wn_recur_day1">${uiLabelMap.CommonSunday}</div>
				   		</div>
				   		<div class='span1'>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_recur_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_recur_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		OlbRecurrenceInfoNew.init();
	});
	var OlbRecurrenceInfoNew = (function(){
		var init = function (){
			initElement();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.input.create($("#wn_recur_recurrenceInfoId"));
			<#list 1..24 as i>
			jOlbUtil.checkBox.create($("#wn_recur_hour${i}"));
			</#list>
			<#list 1..7 as i>
			jOlbUtil.checkBox.create($("#wn_recur_day${i}"));
			</#list>

			jOlbUtil.windowPopup.create($("#popupWindowRecurrenceNew"), {width: 620, height: 360, cancelButton: $("#wn_recur_alterCancel")});
		};
		var initEvent = function(){
			$("#popupWindowRecurrenceNew").on("close", function(){
				resetData();
			});
			
			var resetData = function(){
				<#list 1..24 as i>
					$("#wn_recur_hour${i}").jqxCheckBox('checked', false);
				
				</#list>
				<#list 1..7 as i>
					$("#wn_recur_day${i}").jqxCheckBox('checked', false);
				</#list>
				$("#wn_recur_recurrenceInfoId").val("");
			}
			
			$("#wn_recur_alterSave").on("click", function(){
				var hourList = [];
				var dayList = [];
				
				var valueTmp = null;
				<#list 1..24 as i>
				valueTmp = $("#wn_recur_hour${i}").jqxCheckBox('checked');
				if (valueTmp) hourList.push(${i});
				</#list>
				valueTmp = $("#wn_recur_day2").jqxCheckBox('checked');
				if (valueTmp) dayList.push("MO");
				valueTmp = $("#wn_recur_day3").jqxCheckBox('checked');
				if (valueTmp) dayList.push("TU");
				valueTmp = $("#wn_recur_day4").jqxCheckBox('checked');
				if (valueTmp) dayList.push("WE");
				valueTmp = $("#wn_recur_day5").jqxCheckBox('checked');
				if (valueTmp) dayList.push("TH");
				valueTmp = $("#wn_recur_day6").jqxCheckBox('checked');
				if (valueTmp) dayList.push("FR");
				valueTmp = $("#wn_recur_day7").jqxCheckBox('checked');
				if (valueTmp) dayList.push("SA");
				valueTmp = $("#wn_recur_day1").jqxCheckBox('checked');
				if (valueTmp) dayList.push("SU");

				if (hourList.length < 1) {
					jOlbUtil.alert.error("Chua chon gio");
					return false;
				}
				if (dayList.length < 1) {
					jOlbUtil.alert.error("Chua chon ngay");
					return false;
				}
				
				var dataMap = {
					recurrenceInfoId: $("#wn_recur_recurrenceInfoId").val(),
					byHourList: hourList.join(","),
					byDayList: dayList.join(",")
				};
				$.ajax({
					type: 'POST',
					url: "createRecurrenceInfoRule",
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
						        	if (data.recurrenceInfoId) {
						        		$('body').trigger('createRecurrenceInfoComplete');
						        		$("#popupWindowRecurrenceNew").jqxWindow("close");
						        	}
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
		var showWindowRecurrenceNew = function(idDropDownButton){
			$("#recurrenceObjIdCurrentSelected").val(idDropDownButton);
			$("#popupWindowRecurrenceNew").jqxWindow("open");
		};
		return {
			init: init,
			showWindowRecurrenceNew: showWindowRecurrenceNew
		}
	}());
</script>