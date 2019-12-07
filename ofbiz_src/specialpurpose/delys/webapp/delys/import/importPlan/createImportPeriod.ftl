<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/delys/images/js/import/notify.js"></script>
<script>
var currentTime = new Date();
var currentYear = currentTime.getFullYear();
</script>
<form action="createImportPeriodExe" id="createImportPeriod" method="POST">
	<div class="row-fluid margin-top10">
		<div class="span12">
			<div class="span4"><label class="pull-right asterisk" style="margin-top: 4px;">${uiLabelMap.TimeImport}</label></div>
			<div class="span8"><div id="yearPeriod" name="yearPeriod"></div></div>
		</div>
	</div>
	<br/>
	<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity: 0.4;">
	<div class="row-fluid">
	    <div class="span12 margin-top10">
	    	<div class="span12">
	    		<button id='alterSave' type="button" class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.Create}</button>
			</div>
	    </div>
	</div>
</form>
<script>
//	$("#customTimePeriodImport").jqxNumberInput({ width: '218px', height: '30px', inputMode: 'simple', spinButtons: true, decimalDigits: 0, min: 2015  });
	$("#yearPeriod").jqxNumberInput({ width: '218px', height: '30px', inputMode: 'simple', spinButtons: true, min: currentYear , max: 2037, decimal: currentYear, decimalDigits: 0 });
	$("#alterSave").click(function() {
		$('#createImportPeriod').submit();
	});
	$('#createImportPeriod').jqxValidator({
	    rules: [
	            
	           ]
	});
</script>