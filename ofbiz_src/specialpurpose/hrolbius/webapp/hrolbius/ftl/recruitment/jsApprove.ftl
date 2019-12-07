//Create new family window

<#assign emplPositionTypeList = delegator.findByAnd("EmplPositionType", null, null, false)/>
var emplPositionTypeArr = [
	<#list emplPositionTypeList as emplPositionType>
		{emplPositionTypeId: '${emplPositionType.emplPositionTypeId}', description: '${StringUtil.wrapString(emplPositionType.description)?default("")}'}
		<#if emplPositionType_has_next>
		,
		</#if>
	</#list>
]; 

$("#wdwApprove").jqxWindow({
    showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "80%", height: 350, minWidth: '40%', width: "45%", isModal: true,
    theme:'olbius', collapsed:false, cancelButton:$("#alterCancelAppr"),modalZIndex: 10000,
    initContent: function() {
    	$("#jqxAccepted").jqxRadioButton({ width: 100, checked: true});
    	$("#jqxRejected").jqxRadioButton({ width: 100});
    	$("#apprComment").jqxEditor({ 
    		width: '100%',
            theme: 'olbiuseditor',
            tools: 'datetime | clear | backcolor | font | bold italic underline',
            height: 200
       	});
	}
});