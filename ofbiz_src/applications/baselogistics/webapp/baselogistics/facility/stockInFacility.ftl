<script type="text/javascript" src="/logresources/js/distributor/stockInFacility.js"></script>
<div id="stockIn-tab" class="tab-pane<#if activeTab?exists && activeTab == "stockIn-tab"> active</#if>">
<script>
	<#assign localeStr = "VI" />
	<#if locale = "en">
		<#assign localeStr = "EN" />
	</#if>
	
</script>
<div id="contentNotificationAddSuccess">
</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span6">
					<div class='row-fluid'>
						<div class='span4' style="text-align: right">
							<span class="asterisk">${uiLabelMap.LOGEnterWarehouses}</span>
						</div>
						<div class="span8">
							<div id="facilityId"></div>
				   		</div>
					</div>
				</div>
			</div><!--row-fluid-->
		</div>
	</div>
	
	<div class="row-fluid">
		<a style="float:right;font-size:14px; margin-right: 5px" id="addRow" href="javascript:StockInObj.addNewRow()" data-rel="tooltip" title="${uiLabelMap.AddRow}" data-placement="bottom"><i class="icon-plus-sign open-sans"></i></a>
		<div class="span12"><div id="jqxgridOrderInven"></div></div>
	</div>
	<div>
	    <div class='row-fluid'>
	        <div class="span12 margin-top10" style="margin-bottom:10px;">
	            <button id="addButtonSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	        </div>
	    </div>
	</div>
	<div id="jqxNotificationAddSuccess" >
		<div id="notificationAddSuccess"> 
		</div>
	</div>
	<#include 'script/stockInFacilityInfoScript.ftl'/>	
</div>
