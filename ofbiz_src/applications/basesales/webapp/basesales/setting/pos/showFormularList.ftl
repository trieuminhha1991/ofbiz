<#include "script/ShowFormularListScript.ftl"/>
<div id="jqxNotificationNested">
	<div id="notificationContentNested">
	</div>
</div>
<div id="container" style"width:100%;"></div>
<#assign dataField = "[{ name: 'formularId', type: 'string' },
						{ name: 'name', type: 'string' },
						{ name: 'formular', type: 'string' },
						{ name: 'description', type: 'string' },
						{ name: 'createdDate',  type: 'data', other: 'Timestamp' },
						{ name: 'createdBy', type: 'string' }]"/>
<#assign columnlist = "
					{ text: '${uiLabelMap.SettingFormularId}',  datafield: 'formularId', width: 80 },
					{ text: '${uiLabelMap.SettingFormularName}', datafield: 'name', width: 150 },
					{ text: '${uiLabelMap.SettingFormularSetup}', datafield: 'formular' },
					{ text: '${uiLabelMap.SettingFormularDescription}', datafield: 'description' }"/>
<#assign customcontrol1="icon-plus-sign open-sans@${uiLabelMap.SettingPeriodSetup}@javascript:setupFormularObject.openTimePeriodWindow()" />
<#assign customcontrol2="icon-plus-sign open-sans@${uiLabelMap.SettingSetupFormular}@javascript:setupFormularObject.openFormularWindow()" />
<@jqGrid filtersimplemode="true" 
	dataField=dataField
	filterable="true"  
	id="jqxGridFormular"
	columnlist=columnlist
	clearfilteringbutton="true"
	showtoolbar="true"
	editable="false" 
	editmode="click"
	viewSize="15"
	bindresize="true"
	customcontrol1 = customcontrol1 
	customcontrol2 = customcontrol2 
	url="jqxGeneralServicer?sname=JQListFormular"/>

<div id="setupPeriodTimeWindow" style="display:none;">
	<div>${uiLabelMap.SettingPeriodSetup}</div>
	<div style="overflow: hidden;">
		<div class="row-fluid">
			<div class="span12 margin-top10">
				<div class="span3"><label class="asterisk align-right margin-top5">${uiLabelMap.SettingTimePeriod}</label></div>
				<div class="span9"><div id="inputTimePeriod"></div></div>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span12 margin-top10">
				<div class="span3"><label class="asterisk align-right margin-top5">${uiLabelMap.SettingTimePeriodName}</label></div>
				<div class="span9"> 
					<input type="text" id="inputTimePeriodName"/>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span12 margin-top10">
				<div class="span12" id="buttonForm">
					<button type="button" id='cancelTimePeriodButton' class="btn btn-danger form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.SettingCancel}</button>
					<button type="button" id='createTimePeriodButton' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.SettingCreate}</button>
				</div>
			</div>
		</div>
	</div>
</div>
<div id="setupFormularWindow" style="display:none;">
	<div>${uiLabelMap.SettingSetupFormular}</div>
	<div style="overflow: hidden;">
		<div class="row-fluid">
			<div class="span12 margin-top10">
				<div id="mainSplitter">
					<div class="splitter-panel">
						<textarea id="jqxEditorFormular"></textarea>
					</div>
					<div class="splitter-panel">
						<div id="jqxGridFormularOperation"></div>
					</div>
			 	</div>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span12 margin-top10">
				<div class="span12" id="buttonForm">
					<button type="button" id='cancelFormularButton' class="btn btn-danger form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.SettingCancel}</button>
					<button type="button" id='createFormularButton' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.SettingCreate}</button>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/basepos/resources/js/common/jqx.window.js"></script>
<script type="text/javascript" src="/basepos/resources/js/common/jqx.notification.js"></script>
<script type="text/javascript" src="/basepos/resources/js/common/jqx.input.js"></script>
<script type="text/javascript" src="/basepos/resources/js/common/jqx.numberinput.js"></script>
<script type="text/javascript" src="/basepos/resources/js/common/jqx.splitter.js"></script>
<script type="text/javascript" src="/basepos/resources/js/common/jqx.editor.js"></script>
<script type="text/javascript" src="/webposSetting/images/js/setup/SetupFormular.js"></script>

