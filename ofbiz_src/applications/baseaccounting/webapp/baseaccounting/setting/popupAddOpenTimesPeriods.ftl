<@jqOlbCoreLib hasDropDownList=true/>
<div id="alterpopupWindow" class ='hide'>
    <div>${uiLabelMap.BACCCreateNew}</div>
    <div style="overflow: hidden;">
    	<div class='row-fluid form-window-content'>
			<form id="formAdd">
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.BACCParentPeriodId}
    				</div>
    				<div class='span7'>
						<div id="parentPeriodIdAdd">
 						</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.BACCPeriodTypeId}
    				</div>
    				<div class='span7'>
						<div id="periodTypeIdAdd">
 						</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.BACCPeriodNumber}
    				</div>
    				<div class='span7'>
						<div id="periodNum">
 						</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.BACCPeriodName}
    				</div>
    				<div class='span7'>
						<input id="periodNameAdd">
 						</input>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.BACCStartDate}
    				</div>
    				<div class='span7'>
						<div id="fromDate">
 						</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.BACCEndDate}
    				</div>
    				<div class='span7'>
						<div id="thruDate">
 						</div>
    				</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
    </div>
</div>
<div id='contextMenu' style="display:none;">
<ul>
    <li><i class="icon-ok open-sans"></i>${StringUtil.wrapString(uiLabelMap.BACCRefresh)}</li>
    <li><i class="icon-trash open-sans"></i>${StringUtil.wrapString(uiLabelMap.BACCIsClosed)}</li>
</ul>
</div>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script src="/accresources/js/setting/addOpenTimesPeriods.js"></script>