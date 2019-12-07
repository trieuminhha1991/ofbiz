<style>
.cell-green-color {
    color: black !important;
    background-color: #FFCCFF !important;
}
.cell-gray-color {
	color: black !important;
	background-color: #87CEEB !important;
}
</style>
<@jqGridMinimumLib/>
<#assign localeStr = "VI" />
<#if locale = "en">
<#assign localeStr = "EN" />
</#if>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxradiobutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<div style="position:relative">
	<div id="loader_page_common" style="overflow: hidden; position: fixed; display: none; left: 50%; top: 50%; z-index: 99998;" class="jqx-rc-all jqx-rc-all-olbius">
		<div style="z-index: 99999; position: relative; width: auto; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;" class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div style="float: left;">
				<div style="float: left; overflow: hidden; width: 32px; height: 32px;" class="jqx-grid-load"></div>
				<span style="margin-top: 10px; float: left; display: block; margin-left: 5px;"></span>
			</div>
		</div>
	</div>
</div>
<div id='main' class=''>
	<div class='' id='alterpopupWindow'>
		<div class="row-fluid">
			<div class="span1" id="notificationCreatePurchaseOrder"></div>
			<div class="span3" style="margin-top:5px;">
			</div>
			<div class="span4" style="margin-top:5px;font-size: 25px; text-align: center">
				<b>${uiLabelMap.DmsCreateNewPlanForYear}</b>
			</div>
			<div class="span4" style="margin-top:5px;">
			</div>
		</div>
		
		<div class="row-fluid">
			<div class="span10" id="notificationCreatePurchaseOrder"></div>
			<div class="span2" style="margin-top:5px;">
				<#-- <button id='createOrder' class="btn btn-mini btn-primary pull-right"><i class='icon-plus'></i>${uiLabelMap.DmsCreateNew}</button> -->
			</div>
		</div>
		
		<hr style="margin:0px 0px 0px !important;" />
		<div class="row-fluid">
			<div class="row-fluid span2" style="margin-top:5px;">
			</div>
			<div class="row-fluid span8" style="margin-top:5px;">
				<div class="span5 div-inline-block" style="text-align: right">
					<label>${uiLabelMap.DmsYearPlan}<span style="color: red;"> *</span>:</label>
				</div>
				<div class="span5 div-inline-block">
					<div id="yearPeriod" class=""></div>
				</div>
			</div>
			<div class="row-fluid span2" style="margin-top:5px;">
			</div>
		</div>
		
		<div class="row-fluid">
			<div class="row-fluid span2" style="margin-top:5px;">
			</div>
			<div class="row-fluid span8" style="margin-top:5px;">
				<div class="span5 div-inline-block" style="text-align: right">
					<label>${uiLabelMap.DmsNamePlan}<span style="color: red;"> *</span>:</label>
				</div>
				<div class="span5 div-inline-block">
					<div id="shipBeforeDate">
						<input id="planName" style="width: 85%;" class=""/>
					</div>
				</div>
			</div>
			<div class="row-fluid span2" style="margin-top:5px;">
			</div>
		</div>
	</div>
	
	<hr />
	<div class="row-fluid wizard-actions">
		<button id='createPlan' class="btn btn-mini btn-primary pull-right"><i class='icon-plus'></i>${uiLabelMap.POAdd}</button>
	</div>
</div>

<script type="text/javascript">

var listPeriod =
	[
		<#list listPeriod as period>
		{
			customTimePeriodId: "${period.customTimePeriodId?if_exists}",
			periodName: "${period.periodName?if_exists}",
		},
		</#list>
	];

//BEGIN init jqx
$("#yearPeriod").jqxDropDownList({ source: listPeriod, width: '85%',  theme:'olbius', displayMember: 'periodName', valueMember: 'customTimePeriodId', disabled: false, placeHolder: '${StringUtil.wrapString(uiLabelMap.POPleaseSelect)}', dropDownHeight:'130px'});

$('#createPlan').on('click', function(){
	var validate = $('#alterpopupWindow').jqxValidator('validate');
	if(validate != false){
		var item = $("#yearPeriod").jqxDropDownList('getSelectedItem');
		if(item != null){
			var valuePeriod = item.value;
			var productPlanName = $('#planName').val();
			
			$.ajax({
				beforeSend: function(){
					$("#loader_page_common").show();
			    },
			    complete: function(){
			    	$("#loader_page_common").hide();
			    },
				url: "createProductPlan",
				type: "POST",
				data: {customTimePeriodId: valuePeriod, productPlanName: productPlanName, productPlanTypeId: "IMPORT_PLAN", statusId: "PO_PLAN_CREATED"},
				dataType: "json",
				success: function(data) {
					var productPlanId = data.productPlanId;
					window.location.href = 'listImExPlanItem?productPlanId='+productPlanId;
				}
			});
		}
	}
});

$('#alterpopupWindow').jqxValidator({
	rules: 
		[
		 	{ input: '#planName', message: '${uiLabelMap.POCheckIsEmptyCreateLocationFacility }', action: 'keyup, blur', rule: 'required' },
	        { input: '#yearPeriod', message: '${uiLabelMap.POCheckIsEmptyCreateLocationFacility }', action: 'valueChanged, blur', 
        	   rule: function () {
        		    var supplier = $('#yearPeriod').val();
            	    if(supplier == ""){
            	    	return false; 
            	    }else{
            	    	return true; 
            	    }
            	    return true; 
        	    }
            }
	    ]
});
</script>