<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/delys/images/js/import/notify.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification.js"></script>
<script>
var monthArray = [
					<#if monthList?has_content>
						<#list monthList as month>
						{
   							value: "${month.value?if_exists}"
						},
						</#list>
					</#if>
                  ];
var monthIndex = 26;
if(monthArray != null && monthArray.length >0 ){
	if(monthArray[0].value >0 && monthArray[0].value <= 28) monthIndex = monthArray[0].value ;
}

var weekArray = [
					<#if weekList?has_content>
						<#list weekList as week>
						{
 							value: "${week.value?if_exists}"
						},
						</#list>
					</#if>
                ];
var weekIndex = 4;
if(weekArray != null && weekArray.length >0 ){
	if(weekArray[0].value >0 && weekArray[0].value <= 7) weekIndex = parseInt(weekArray[0].value)-1 ;
}
var currentTime = new Date();
var currentYear = currentTime.getFullYear();
var dayOfWeek = [
                {
                	 id: 1,
                	 description: "${uiLabelMap.wgsunday}"
                },
          		{
					id: 2,
					description: "${uiLabelMap.wgmonday}"
				},
				{
					id: 3,
					description: "${uiLabelMap.wgtuesday}"
				},
				{
					id: 4,
					description: "${uiLabelMap.wgwednesday}"
				},
				{
					id: 5,
					description: "${uiLabelMap.wgthursday}"
				},
				{
					id: 6,
					description: "${uiLabelMap.wgfriday}"
				},
				{
					id: 7,
					description: "${uiLabelMap.wgsaturday}"
				},
	        ];
</script>

<div id="jqxNotificationContainerNoChange">
	<div style="width: inherit; max-width: 100%; overflow: hidden; margin-left: 1%">
	${uiLabelMap.importPeriodConfigNoChange}
	</div>
</div>

<div id="jqxNotificationContainerSuccess">
	<div style="width: inherit; max-width: 100%; overflow: hidden; margin-left: 1%">
	${uiLabelMap.importPeriodConfigSuccess}
	</div>
</div>

<div id="containerSuccess">  </div>
<div id="containerNoChange"> </div>

<div id="importPeriodConfig">
	<div class="row-fluid margin-top10">
	<div class="span12">
		<div class="span4"><label class="pull-right asterisk" style="margin-top: 4px;">${uiLabelMap.monthTime}</label></div>
		<div class="span8"><div id="monthTime"> <div/></div>
	</div>
	</div>
	<br/>
	<br/>
	<div class="row-fluid margin-top10">
	<div class="span12">
		<div class="span4"><label class="pull-right asterisk" style="margin-top: 5px;">${uiLabelMap.weekTime}</label></div>
		<div class="span8"><div id="weekTime"></div></div>
	</div>
	</div>
	</br>
		<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity: 0.4;">
		<div class="row-fluid">
		    <div class="span12 margin-top10">
		    	<div class="span12">
		    		<button id='alterSave' type="button" class="btn btn-primary btn-mini pull-right"><i class='icon-ok'></i>${uiLabelMap.Create}</button>
				</div>
		    </div>
		</div>
</div>

<script>
	 $("#monthTime").jqxNumberInput({ width: '218px', height: '30px',  inputMode: 'simple', spinButtons: true, min: 1 , max: 28, decimal: monthIndex, decimalDigits: 0 });
	 $("#weekTime").jqxDropDownList({ source: dayOfWeek, displayMember: "description",
    	 valueMember : "id", selectedIndex: weekIndex, width: 218, height: 30 , theme: "olbius", autoDropDownHeight: true, rtl: true });
	$("#alterSave").click(function() {
		$('#importPeriodConfig').jqxValidator('validate');
	});
	$('#importPeriodConfig').jqxValidator({
	    rules: [
					{ input: '#monthTime',message: '${uiLabelMap.importPeriodConfigValidateError}', action: 'keyup, blur', rule: function(){
						   var monthTime = $('#monthTime').jqxNumberInput('val');
						   if(parseInt(monthTime) < 0 || parseInt(monthTime) > 28) return false;
						   else return true;
					} },
	           ]
	});
	$('#importPeriodConfig').on('validationError', function (event) {
		window.alert('error');
	  });
	$('#importPeriodConfig').on('validationSuccess', function (event) {
//		$('#importPeriodConfig').submit();
		var monthTime = $('#monthTime').jqxNumberInput('val');
		var weekTime = $("#weekTime").jqxDropDownList('getSelectedItem').value; 
		
		if( monthTime == monthIndex && weekTime == (weekIndex+1))
			{
			$("#jqxNotificationContainerNoChange").jqxNotification("open");
			} 
		else
			{
				$.ajax({
					url: 'createImportPeriodConfig',
					type: 'POST',
					async: false,
					data: {monthTime: monthTime, weekTime: weekTime },
					success: function(data){
						if(data._FORWARDED_FROM_SERVLET_ ==true){
							$("#jqxNotificationContainerSuccess").jqxNotification("open");
							monthIndex = monthTime;
							weekIndex = weekTime-1;
							
						}
					},
					complete: function(){
					}
				});
			}
		
	});
	$(document).ready(function () {
         $("#weekTime").jqxTooltip({ content: '${uiLabelMap.weekTimeTooltip}', position: 'mouse', name: 'configTooltip'});
         $("#monthTime").jqxTooltip({ content: '${uiLabelMap.monthTimeTooltip}', position: 'mouse', name: 'configTooltip'});
         
         $("#jqxNotificationContainerNoChange").jqxNotification({ width: "100%", appendContainer: "#containerNoChange", opacity: 1, autoClose: false, template: "info" });
         $("#jqxNotificationContainerSuccess").jqxNotification({ width: "100%", appendContainer: "#containerSuccess", opacity: 1, autoClose: false, template: "info" });
         
          
         
     });
</script>
