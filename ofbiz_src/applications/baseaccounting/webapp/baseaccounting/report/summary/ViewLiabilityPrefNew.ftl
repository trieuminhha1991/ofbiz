<#include "script/ViewLiabilityPrefScript.ftl"/>
<div class="row-fluid" style="text-align: center; margin-bottom: 15px">
	<h4><b>${StringUtil.wrapString(uiLabelMap.BACCDebtReconciliation)?upper_case}</b></h4>
	<div class="row-fluid">
		<div class="span12">
			<div class="span6"></div>
			<div class="span6">
				<div class="span8"></div>
				<div class="span4">
					<a href="#" onclick="return exportExcel()"><i class="fa fa-file-excel-o open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSExportExcel)}</a>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="row-fluid">
	<form class="form-horizontal form-window-content-custom" id="liabilityPrefInfoForm">
		<div class="span12">
			<div class="span6">
				<div class='row-fluid'>
					<div class='span4'>
						<label>${uiLabelMap.CommonFromDate}</label>
					</div>
					<div class="span8">
						<div id="fromDate"></div>
					</div>
				</div>
				<div class='row-fluid' style="position: relative;">
					<div class='span4'>
						<label><b>${uiLabelMap.BACCAParty} <i>(${StringUtil.wrapString(uiLabelMap.BACCThePurchaser)})</i></b></label>
					</div>
					<div class="span8">
						<input type="text" id="partyIdFrom">
						<a id="editPartyFromBtn" tabindex="-1" href="javascript:void(0);" class="clear-value-jqx" style="left: 93%; top: 0px">
							<i class="fa fa-pencil blue" aria-hidden="true"></i>
						</a>
					</div>
				</div>
				<div class='row-fluid'>
					<div class='span4'>
						<label>${uiLabelMap.CommonAddress1}</label>
					</div>
					<div class="span8">
						<input type="text" id="address1From">
					</div>
				</div>
				<div class='row-fluid'>
					<div class='span4'>
						<label>${uiLabelMap.CommonTelephoneAbbr}</label>
					</div>
					<div class="span8">
						<div class="row-fluid" style="margin-bottom: 0px">
							<div class="span5">
								<input type="text" id="phoneNbrFrom">								
							</div>
							<div class="span7">
								<div class='row-fluid' style="margin-bottom: 0px">
									<div class='span2'>
										<label>${uiLabelMap.Fax}</label>
									</div>
									<div class="span10">
										<input type="text" id="faxFrom">
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class='row-fluid'>
					<div class='span4'>
						<label>${uiLabelMap.PartyRepresent}</label>
					</div>
					<div class="span8">
						<input type="text" id="representativeFrom">
					</div>
				</div>
				<div class='row-fluid'>
					<div class='span4'>
						<label>${uiLabelMap.HRCommonPosition}</label>
					</div>
					<div class="span8">
						<input type="text" id="positionFrom">
					</div>
				</div>
			</div><!-- ./span6 -->
			<div class="span6">
				<div class='row-fluid'>
					<div class='span4'>
						<label>${uiLabelMap.AccountingThruDate}</label>
					</div>
					<div class="span8">
						<div id="thruDate"></div>
					</div>
				</div>
				<div class='row-fluid' style="position: relative;">
					<div class='span4'>
						<label><b>${uiLabelMap.BACCBParty} <i>(${StringUtil.wrapString(uiLabelMap.BACCTheSeller)})</i></b></label>
					</div>
					<div class="span8">
						<input type="text" id="partyIdTo">
						<a id="editPartyToBtn" tabindex="-1" href="javascript:void(0);" class="clear-value-jqx" style="left: 93%; top: 0px">
							<i class="fa fa-pencil blue" aria-hidden="true"></i>
						</a>
					</div>
				</div>
				<div class='row-fluid'>
					<div class='span4'>
						<label>${uiLabelMap.CommonAddress1}</label>
					</div>
					<div class="span8">
						<input type="text" id="address1To">
					</div>
				</div>
				<div class='row-fluid'>
					<div class='span4'>
						<label>${uiLabelMap.CommonTelephoneAbbr}</label>
					</div>
					<div class="span8">
						<div class="row-fluid" style="margin-bottom: 0px">
							<div class="span5">
								<input type="text" id="phoneNbrTo">								
							</div>
							<div class="span7">
								<div class='row-fluid' style="margin-bottom: 0px">
									<div class='span2'>
										<label>${uiLabelMap.Fax}</label>
									</div>
									<div class="span10">
										<input type="text" id="faxTo">
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class='row-fluid'>
					<div class='span4'>
						<label>${uiLabelMap.PartyRepresent}</label>
					</div>
					<div class="span8">
						<input type="text" id="representativeTo">
					</div>
				</div>
				<div class='row-fluid'>
					<div class='span4'>
						<label>${uiLabelMap.HRCommonPosition}</label>
					</div>
					<div class="span8">
						<input type="text" id="positionTo">
					</div>
				</div>
			</div><!-- ./span6 -->
		</div><!-- ./span12 -->
	</form>
</div><!-- ./row-fluid -->
<div class="row-fluid">
	<button type="button" class='btn btn-primary form-action-button pull-right' style="margin-right: 3% !important" id="liabilityPrefBtn">
		<i class='fa fa-exchange'></i>&nbsp;${uiLabelMap.BACCPreference}
	</button>	
</div>
<div class="hr hr8 hr-double hr-dotted"></div>

<div class="row-fluid margin-bottom-10">
	<b>I. ${StringUtil.wrapString(uiLabelMap.BACCOpeningBalance)?upper_case} </b>	
	<div style="margin-left:50px;">1. ${StringUtil.wrapString(uiLabelMap.BACCOpeningDrBalance)}: <span id="openingDrAmount" style="color: red"></span></div>
	<div style="margin-left:50px;">2. ${StringUtil.wrapString(uiLabelMap.BACCOpeningCrBalance)}: <span id="openingCrAmount" style="color: red"></span></div>
</div>
<div class="row-fluid margin-bottom-10">
	<b>II. ${StringUtil.wrapString(uiLabelMap.BACCPostedDrAmount)?upper_case} </b>	
	<div style="margin-left:50px;">1. <span id="returnSupplierTitle">${StringUtil.wrapString(uiLabelMap.BACCXuatTraNCC)}</span>: <span id="returnSupplierAmount" style="color: red"></span></div>
	<div style="margin-left:50px;">2. ${StringUtil.wrapString(uiLabelMap.BACCPayment)}: <span id="paymentAmount" style="color: red"></span></div>
</div>
<div class="row-fluid margin-bottom-10">
	<b>III. ${StringUtil.wrapString(uiLabelMap.BACCPostedCrAmount)?upper_case} </b>
	<div style="margin-left:50px;">1. ${StringUtil.wrapString(uiLabelMap.BACCTienHang)}: <span id="goodsAmount" style="color: red"></span></div>
	<div style="margin-left:50px;">2. ${StringUtil.wrapString(uiLabelMap.BACCThuTienXuatTra)}: <span id="receiveableAmount" style="color: red"></span></div>
</div>
<div class="row-fluid margin-bottom-10">
	<b>IV. ${StringUtil.wrapString(uiLabelMap.BACCEndingBalance)?upper_case} </b>	
	<div style="margin-left:50px;">1. ${StringUtil.wrapString(uiLabelMap.BACCOpeningDrBalance)}: <span id="endingDrAmount" style="color: red"></span></div>
	<div style="margin-left:50px;">2. ${StringUtil.wrapString(uiLabelMap.BACCOpeningCrBalance)}: <span id="endingCrAmount" style="color: red"></span></div>
</div>

<div class="row-fluid">
	<b>${StringUtil.wrapString(uiLabelMap.HRCommonConclusion)}:</b>
	<span id="amountNotPaidTitle">${StringUtil.wrapString(uiLabelMap.BACCBenAThanhToanChoBenB)}</span>&nbsp; 
	<span id="amountNotPaidText" style="color: red"></span>
</div>

<div id="editPartyWindow" class="hide">
	<div>${uiLabelMap.BSSelect}....</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div id="listPartyGrid"></div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right' id="cancelChooseParty">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="selectedParty">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSubmit}</button>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/accresources/js/report/ViewLiabilityPrefNew.js?v=0.0.1"></script>