<@jqGridMinimumLib/>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script>
	var billSelected = null;
	var facilitySelected = null;
	var containerTypeData = [];
	<#assign containerTypes = delegator.findList("ContainerType", null, null, null, null, true) />
	var billId = null;
	<#if parameters.billId?has_content>
		billId = '${parameters.billId?if_exists}';
		<#assign bill = delegator.findOne("BillOfLading", Static["org.ofbiz.base.util.UtilMisc"].toMap("billId", parameters.billId?if_exists), false)!/>
		<#if bill?has_content>
			if (billSelected == undefined) { 
				var billSelected = null;
			}
			if (billId) {
				billSelected = {"billId": billId, "billNumber": "${bill.billNumber?if_exists}"};
			}
		</#if>
	</#if>
	<#if containerTypes?has_content>
		<#list containerTypes as item>
			var item = {
				containerTypeId: '${item.containerTypeId?if_exists}',
				description: '${StringUtil.wrapString(item.get('description', locale)?if_exists)}'
			}
			containerTypeData.push(item);
		</#list>
	</#if>
	
	var getContainerTypeDesc = function (containerTypeId) {
		for (var i in containerTypeData) {
			var x = containerTypeData[i];
			if (x.containerTypeId == containerTypeId) {
				return x.description;
			}
		}
		return containerTypeId;
	}
	
	var locale = '${locale}';

	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.packingListNumber = "${StringUtil.wrapString(uiLabelMap.packingListNumber)}";
	uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.DAYouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.DAYouNotYetChooseProduct)}";
	uiLabelMap.NotChosenDateManu = "${StringUtil.wrapString(uiLabelMap.NotChosenDateManu)}";
	uiLabelMap.ExistsProductNotDate = "${StringUtil.wrapString(uiLabelMap.ExistsProductNotDate)}";
	uiLabelMap.SaveAndConPL = "${StringUtil.wrapString(uiLabelMap.SaveAndConPL)}";
	uiLabelMap.LoadFailPO = "${StringUtil.wrapString(uiLabelMap.LoadFailPO)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.BIEContainerId = "${StringUtil.wrapString(uiLabelMap.BIEContainerId)}";
	uiLabelMap.BIEBillId = "${StringUtil.wrapString(uiLabelMap.BIEBillId)}";
	uiLabelMap.BIEBillNumber = "${StringUtil.wrapString(uiLabelMap.BIEBillNumber)}";
	uiLabelMap.BIEContainer = "${StringUtil.wrapString(uiLabelMap.BIEContainer)}";
	uiLabelMap.BIEContainerNumber = "${StringUtil.wrapString(uiLabelMap.BIEContainerNumber)}";
	uiLabelMap.BIESealNumber = "${StringUtil.wrapString(uiLabelMap.BIESealNumber)}";
	uiLabelMap.Description = "${StringUtil.wrapString(uiLabelMap.Description)}";
	uiLabelMap.AddNew = "${StringUtil.wrapString(uiLabelMap.AddNew)}";
	uiLabelMap.BIEContainerType = "${StringUtil.wrapString(uiLabelMap.BIEContainerType)}";
	uiLabelMap.BIEDepartureDate = "${StringUtil.wrapString(uiLabelMap.BIEDepartureDate)}";
	uiLabelMap.BIEArrivalDate = "${StringUtil.wrapString(uiLabelMap.BIEArrivalDate)}";
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	uiLabelMap.testedDocument = "${StringUtil.wrapString(uiLabelMap.testedDocument)}";
	uiLabelMap.quarantineDocument = "${StringUtil.wrapString(uiLabelMap.quarantineDocument)}";

	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	
	uiLabelMap.FacilityId = "${StringUtil.wrapString(uiLabelMap.FacilityId)}";
	uiLabelMap.FacilityName = "${StringUtil.wrapString(uiLabelMap.FacilityName)}";
</script>

<div id="container-tab" class="tab-pane<#if activeTab?exists && activeTab == "container-tab"> active</#if>">
<div id="jqxGridContainers"></div>
<div id="popupWindowContainer" class="hide popup-bound">
	<div>${uiLabelMap.AddNew}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="row-fluid margin-top10">
					<div class="span3 align-right asterisk">${uiLabelMap.BIEBillOfLading}</div>
					<div class="span7">	
						<div id="billOfLading">
							<div id="jqxGridBOL"></div>
						</div>
					</div>
				</div>
				<div class="row-fluid margin-top10">
					<div class="span3 align-right asterisk">${uiLabelMap.BIEContainerType}</div>
					<div class="span7"><div id="containerTypeId"></div></div>
				</div>
				<div class="row-fluid margin-top10">
					<div class="span3 align-right asterisk">${uiLabelMap.containerNumber}</div>
					<div class="span7"><input type='text' id="containerNumber" /></div>
				</div>
				<div class="row-fluid margin-top10">
					<div class="span3 align-right">${uiLabelMap.ReceiveToFacility}</div>
					<div class="span7">
						<div id="facilityContainer">
							<div id="gridFacilityContainer">
							</div>
						</div>
					</div>
				</div>
				<div class="row-fluid margin-top10">
					<div class="span3 align-right">${uiLabelMap.sealNumber}</div>
					<div class="span7"><input type='text' id="sealNumber" /></div>
				</div>
				<div class="row-fluid margin-top10">
					<div class="span3 align-right">${uiLabelMap.Description}</div>
					<div class="span7"><textarea id="description" name="description" data-maxlength="250" rows="4" style="resize: vertical; margin-top:0px" class="span12"></textarea></div>
				</div>
			</div>
	        <div class="form-action popup-footer">
		        <button id="alterCancelContainer" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
		    	<button id="alterSaveContainer" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>

<div id='jqxContextMenu' class="hide">
	<ul>
	  	<li id="addContainer"><i class="icon-plus"></i><a>${uiLabelMap.AddNew}</a></li>
	  	<li id="editContainer"><i class="icon-edit"></i><a>${uiLabelMap.Edit}</a></li>
		<li id="viewQuarantine"><i class="icon-download-alt"></i><a>${uiLabelMap.DownloadquarantineDocument}</a></li>
		<li id="viewTested"><i class="icon-download-alt"></i><a>${uiLabelMap.DownloadtestedDocument}</a></li>
		<li id='agreementToQuarantineChild'><i class='icon-download-alt'></i><a>${uiLabelMap.DownloadAgreementToQuarantine}</a></li>
		<li id='agreementToValidationChild'><i class='icon-download-alt'></i><a>${uiLabelMap.DownloadAgreementToValidation}</a></li>
		<li id='refreshGrid'><i class='icon-refresh'></i><a>${uiLabelMap.BSRefresh}</a></li>
	</ul>
</div>
<#include "popup/QADocumentation.ftl"/>
</div>
<script type="text/javascript" src="/imexresources/js/import/listContainers.js"></script>