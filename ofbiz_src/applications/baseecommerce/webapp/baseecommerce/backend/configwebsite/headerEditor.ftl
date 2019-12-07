<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<script type="text/javascript" src="/ecommerceresources/js/backend/configwebsite/header/jquery-sortable.js"></script>
<script type="text/javascript" src="/ecommerceresources/js/backend/configwebsite/header/headerEditor.js"></script>
<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>
<@jqGridMinimumLib/>

<div id="container"></div>

<div class='row-fluid'>
	<div class="span5">
		<ol class="nested_with_switch vertical" id="olMenu">

		</ol>
	</div>
	<div class='span7'>
		<div class="edit-box" id="editBox">

			<div class="row-fluid margin-top20">
		        <div class="span12 no-left-margin">
				<div class="span4"><label class="text-right asterisk">${uiLabelMap.BSText}</label></div>
				<div class="span8"><input type="text" id="txtText"/></div>
		        </div>
		    </div>

		    <div class="row-fluid">
			    <div class="span12 no-left-margin">
				    <div class="span4"><label class="text-right">${uiLabelMap.BSClassIcon}&nbsp;&nbsp;&nbsp;</label></div>
				    <div class="span8"><input type="text" id="txtClassIcon"/></div>
			    </div>
		    </div>

		    <div class="row-fluid">
			    <div class="span12 no-left-margin">
				    <div class="span4"><label class="text-right asterisk">${uiLabelMap.BSLink}</label></div>
				    <div class="span8"><input type="text" id="txtLink" value="#"/></div>
			    </div>
		    </div>

		    <div class="row-fluid">
			    <div class="span12 no-left-margin">
				    <div class="span4"><label class="text-right">${uiLabelMap.BSIncludeChildMenu}&nbsp;&nbsp;&nbsp;</label></div>
				    <div class="span8"><div id="includeChildMenu" style="margin-left: -3px !important;"></div></div>
			    </div>
		    </div>

			<div class="row-fluid">
				<div class="span12 margin-top">
					<button id='btnDeleteMenu' class="btn btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.BSDeleteMenu}</button>
					<button id='btnSaveMenu' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.BSCreateMenu}</button>
				</div>
			</div>
		<div>

	</div>
</div>

<div class="row-fluid">
	<div class="span12 margin-top10">
		<button id='btnSave' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
	</div>
</div>

<div id="jqxNotificationNested">
	<div id="notificationContentNested">
	</div>
</div>

<#assign header = Static["com.olbius.baseecommerce.backend.ConfigWebSiteServices"].header(delegator, userLogin) />
<script>
	var header = "${StringUtil.wrapString((header.get("longDescription"))?if_exists)}";
	var contentId = "${StringUtil.wrapString((header.get("contentId"))?if_exists)}";
</script>