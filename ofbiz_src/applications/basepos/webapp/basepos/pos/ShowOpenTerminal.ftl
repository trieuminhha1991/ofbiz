<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/posresources/js/OpenTerminal.js"> </script>
<style type="text/css">
    .jqx-widget-content{
        font-family: 'Roboto';
    }
    #startingDrawerAmount{
		width: 95%;
        font-size: 20px;
        font-weight: bold;
        color: #cc0000;
        text-align: right;
    }
</style>
<div id="openTerminalJqxWindow">
    <div style="background-color: #438EB9; border-color: #0077BC;">${uiLabelMap.BPOSStartShift}</div>
    <div class='row-fluid form-window-content'>
        <div class='span12'>
            <div class='row-fluid'>
                <div class='span3 align-right asterisk' style="padding-top: 5px; font-size: 15px">
					${uiLabelMap.BPOSAmount}
                </div>
                <div class='span9'>
                    <input type="text" id="startingDrawerAmount" name="startingDrawerAmount">
                </div>
            </div>
        </div>
        <div class="form-action">
            <div class='row-fluid'>
                <div class="span12">
                    <button id="cancelButton" class='btn btn-mini btn-danger form-action-button pull-right'><i class='icon-remove'></i> ${uiLabelMap.BPOSCancel}</button>
                    <button id="saveButton" class='btn btn-mini btn-primary form-action-button pull-right'><i class='icon-ok'></i> ${uiLabelMap.BPOSSubmit}</button>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
	var BPOSAreYouSure = "${StringUtil.wrapString(uiLabelMap.BPOSAreYouSureOpenTerminal)}";
	var BPOSEnterAmount = "${StringUtil.wrapString(uiLabelMap.BPOSEnterAmount)}";
	var CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
	var CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
</script>