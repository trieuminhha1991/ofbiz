<div id="editAcctgTransEntryWindow" class="hide">
	<div>${uiLabelMap.CommonEdit}</div>
	<div class='form-window-container' style="position: relative;">
		<div class="form-window-content">
			<div class="row-fluid">
				<div class="row-fluid form-horizontal form-window-content-custom label-text-left content-description">
					<div class="span12">
						<div class="span6">
							<div class='row-fluid'>
								<div class="span6 text-algin-right">
									<span style="float: right;">${uiLabelMap.BACCAcctgTransId}</span>
								</div>								
								<div class="span6">
									<div id="acctgTransIdEdit" class="green-label" style="text-align: left;"></div>
								</div>								
							</div>	
							<div class='row-fluid'>
								<div class="span6 text-algin-right">
									<span style="float: right;">${uiLabelMap.BACCAcctgTransTypeId}</span>
								</div>								
								<div class="span6">
									<div id="acctgTransTypeIdEdit" class="green-label" style="text-align: left;"></div>
								</div>								
							</div>	
						</div>
						<div class="span6">
							<div class='row-fluid'>
								<div class="span6 text-algin-right">
									<span style="float: right;">${uiLabelMap.BACCPostedDate}</span>
								</div>								
								<div class="span6">
									<div id="transactionDateEdit" class="green-label" style="text-align: left;"></div>
								</div>								
							</div>
							<div class='row-fluid'>
								<div class="span6 text-algin-right">
									<span style="float: right;">${uiLabelMap.CommonStatus}</span>
								</div>								
								<div class="span6">
									<div id="isPostedEdit" class="green-label" style="text-align: left;"></div>
								</div>								
							</div>
						</div>
					</div><!-- ./span12 -->
				</div>
			</div><!-- ./row-fluid -->
			<hr style="margin: 10px 0 5px">
			<div class="row-fluid">
				<div id="editTransEntryGrid"></div>
			</div>
			<div class="row-fluid hide" id="lastUpdateByUser" >
				<div style="font-size: 12px"><i>${StringUtil.wrapString(uiLabelMap.LastUpdateAt)}</i>&nbsp;<i><b id="changeDateLstUpdate"></b></i>&nbsp;<i>${StringUtil.wrapString(uiLabelMap.CommonBy)?lower_case}</i>&nbsp;<i><b id="changeDateByUser"></b></i>&nbsp;<i><a href="javascript:void(0)" id="viewAcctgTransHis">(${StringUtil.wrapString(uiLabelMap.BSViewDetail)})</a></i></div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelEditTransEntry" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveEditTransEntry">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<#include "ViewAcctgTransEntryHistory.ftl"/>
<script type="text/javascript" src="/accresources/js/transaction/editAcctgTransEntry.js?v=0.0.1"></script>