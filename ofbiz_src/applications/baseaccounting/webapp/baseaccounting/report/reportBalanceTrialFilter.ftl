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
$(function(){
	OlbBalanceTrial.init();
})

var OlbBalanceTrial = (function(){
	var accCm = new accCommon();
	
	var init  = function(){
		initDropDownList();
		initPanel();
		bindEvent();
	}
	
	var initDropDownList = function(){
		accCm.createDropDownList('#customTimePeriods',{source: customTimePeriods,dropDownWidth : 300, valueMember: 'customTimePeriodId', displayMember: 'description',obj : accCm,width: 300,height : 30});
	}
	
	var initPanel = function(){
		$("#jqxPanel").jqxPanel({ height: 50, theme:theme});
	}
	
	var bindEvent = function(){
		$('#customTimePeriods').on('select', function (event) {
	        var args = event.args;
	        var customTimePeriodId = $('#customTimePeriods').jqxDropDownList('getItem', args.index).value;
	        if (customTimePeriodId != null) {
	           	var tmpS = $("#jqxgrid").jqxGrid('source');
				tmpS._source.url = "jqxGeneralServicer?sname=getListJqTrialBalanceAccount&organizationPartyId=${userLogin.lastOrg}&customTimePeriodId=" + customTimePeriodId;
				$("#jqxgrid").jqxGrid('source', tmpS);    
	        }
	    });
	}
	
	return {
		init : init
	}
	}());
</script>	