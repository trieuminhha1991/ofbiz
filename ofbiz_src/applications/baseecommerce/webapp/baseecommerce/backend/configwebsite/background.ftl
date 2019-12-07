<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<script type="text/javascript" src="/ecommerceresources/js/backend/configwebsite/background.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets//jqxnotification.js"></script>
<div id="container"></div>
<div id="jqxNotificationNested">
	<div id="notificationContentNested"></div>
</div>

<div class="row-fluid margin-top10">
	<div class="span12 no-left-margin boder-all-profile">
		<span class="text-header">${uiLabelMap.Header}</span>
		
		<div class="pull-right switcher">
			<label><input id="switchHeader" class="ace-switch ace-switch-6" type="checkbox" /><span class="lbl"></span></label>
		</div>
	
		<div class="bg-image">
			<img id="headerImage" src=""/>
		</div>
		<input type="file" id="txtHeaderImage" style="visibility:hidden;" accept="image/*"/>
	</div>
</div>
<div class="row-fluid margin-top10">
	<div class="span12 no-left-margin boder-all-profile">
		<span class="text-header">${uiLabelMap.Info}</span>
		
		<div class="pull-right switcher">
			<label><input id="switchInfo" class="ace-switch ace-switch-6" type="checkbox" /><span class="lbl"></span></label>
		</div>
		
		<div class="bg-image">
			<img id="infoImage" src=""/>
		</div>
		<input type="file" id="txtInfoImage" style="visibility:hidden;" accept="image/*"/>
	</div>
</div>
<div class="row-fluid margin-top10">
	<div class="span12 no-left-margin boder-all-profile">
		<span class="text-header">${uiLabelMap.Footer}</span>
		
		<div class="pull-right switcher">
			<label><input id="switchFooter" class="ace-switch ace-switch-6" type="checkbox" /><span class="lbl"></span></label>
		</div>
		
		<div class="bg-image">
			<img id="footerImage" src=""/>
		</div>
		<input type="file" id="txtFooterImage" style="visibility:hidden;" accept="image/*"/>
	</div>
</div>
<div class="row-fluid">
		<div class="span12 margin-top10">
			<button id='btnSaveBG' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
