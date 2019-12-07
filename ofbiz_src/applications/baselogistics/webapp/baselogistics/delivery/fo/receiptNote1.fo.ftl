<#escape x as x?xml>
	<#assign labelInstance = uiLabelMap.FirstInstance+ ': '+ uiLabelMap.OriginalSave>
	<#include "receiptNoteContent.fo.ftl"/>
</#escape>