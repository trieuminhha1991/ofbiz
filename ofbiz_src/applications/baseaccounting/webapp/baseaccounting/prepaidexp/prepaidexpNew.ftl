<#--IMPORT LIB-->
<@jqGridMinimumLib/>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
</#--IMPORT LIB-->
<#include "prepaidexpNewCommon.ftl">
<#include "prepaidexpNewAllocGrid.ftl">
<#include "prepaidexpNewControl.ftl">
<script>
	$(document).on('ready', function(){
		common = new OLBNewCommon();
		alloc = new OLBNewAlloc();
		ctrl = new OLBCtrl();
		common.initForm();
		alloc.initGrid();
		alloc.bindEvent();
		ctrl.bindEvent();
	});
</script>