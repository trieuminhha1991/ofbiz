<@jqGridMinimumLib />
<style type="text/css">
.span6 label{
	text-align:right;
	width:300px;
} 
.ace-file-input .remove {
	right: 20px;
	padding-left: 2px;
}
.ace-file-input label span {
	margin-right: 110px;
  	padding-right: 50px;
}
.ace-file-input {
    position: relative;
    height: 25px;
    line-height: 25px;
    margin-bottom: 0px;
}
</style>
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	uiLabelMap.MustBeAfterOwnerFromDate = "${StringUtil.wrapString(uiLabelMap.MustBeAfterOwnerFromDate)}";
	uiLabelMap.MustBeAfterManageFromDate = "${StringUtil.wrapString(uiLabelMap.MustBeAfterManageFromDate)}";
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.BLEmployeeId = "${StringUtil.wrapString(uiLabelMap.BLEmployeeId)}";
	uiLabelMap.BLEmployeeName = "${StringUtil.wrapString(uiLabelMap.BLEmployeeName)}";
	uiLabelMap.BLEmployee = "${StringUtil.wrapString(uiLabelMap.BLEmployee)}";
	
</script>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script type="text/javascript" src="/logresources/js/facility/facilityNewFacilityRole.js?v=1.1.1"></script>