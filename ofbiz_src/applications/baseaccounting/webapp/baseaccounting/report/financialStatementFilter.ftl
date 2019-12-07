<div id="jqxPanel" style="position:relative" class="form-horizontal form-window-content-custom" >	
	<div class="row-fluid">
		<div class="span12">
			<div class="row-fluid">
				<div class="span5">
					<label class="text-info">${uiLabelMap.BACCChoosePeriodName}</label>
				</div>
				<div class="span6">
					<div id="customTimePeriods"></div>
		   		</div>
			</div>				
		</div><!--.span12-->
	</div><!--.row-fluid-->
</div>
<script type="text/javascript">
	$.jqx.theme = "olbius";
	$(function(){
		OlbBalanceTrial.init();
	})
	
	var customTimePeriodDefault = ''; 
	<#if customTimePeriodDefault?has_content>
		customTimePeriodDefault = '${customTimePeriodDefault.customTimePeriodId?if_exists}';
	</#if>
	
	var OlbBalanceTrial = (function(){
		var init  = function(){
			initDropDownList();
			initPanel();
			bindEvent();
		};
		
		var initDropDownList = function(){
			$('#customTimePeriods').jqxDropDownList({theme: $.jqx.theme, filterable: true, source: customTimePeriods, valueMember: 'customTimePeriodId', displayMember: 'description', width: 300, height : 30});
			accutils.setValueDropDownListOnly($('#customTimePeriods'), customTimePeriodDefault, 'customTimePeriodId', customTimePeriods);
		};
		
		var initPanel = function(){
			$("#jqxPanel").jqxPanel({ height: 50, theme:'olbius'});
		};
		
		var bindEvent = function(){
			$('#customTimePeriods').on('select', function (event) {
		        var args = event.args;
		        var customTimePeriodId = $('#customTimePeriods').jqxDropDownList('getItem', args.index).value;
		        if (customTimePeriodId != null) {
		        	var flag = '${parameters.flag?if_exists}';
		        	var reportTypeId = '${parameters.reportTypeId?if_exists}';
		        	if (flag === 'T') {
		        		FinancialStatementObj.getDataFinancialStatement(customTimePeriodId, '${parameters.organizationPartyId}', reportTypeId, flag);
		        		$("#errorNotify").css("display", "none");
		        	} else {
		        		var newStatus = "N";
		        		Loading.show('loadingMacro');
		        		$.ajax({
				    		url: "getStateCustomTimePeriod",
				    		type: "POST",
				    		aync: false,
				    		data: {
				    			customTimePeriodId: customTimePeriodId
				    		},
				    		dataType: "json",
				    		success: function(res) {
				    			newStatus = res["isClosed"];
				    		}
				    	}).done(function() {
				    		Loading.hide('loadingMacro');
				    		if (newStatus == 'Y') {
				    			$("#treeGrid").css("display", "block");
				    			$("#errorNotify").css("display", "none");
			    				FinancialStatementObj.getDataFinancialStatement(customTimePeriodId, '${parameters.organizationPartyId}', reportTypeId, flag);
			    			} else {
			    				$("#treeGrid").css("display", "none");
			    				$("#errorNotify").css("display", "block");
			    			}
				    	});
					}
		        }
		    });
		};
		
		return {
			init : init
		}
	}());
</script>