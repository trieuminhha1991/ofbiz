<@jqGridMinimumLib/>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script>

	<#assign countries = delegator.findList("Geo", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("geoTypeId", "COUNTRY")), null, null, null, false) />
	var countryData = new Array();
	<#list countries as item>
		var row = {};
		row['geoId'] = "${item.geoId}";
		row['description'] = "${StringUtil.wrapString(item.geoName?if_exists)}";
		countryData[${item_index}] = row;
	</#list>
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.AddNew = "${StringUtil.wrapString(uiLabelMap.AddNew)}";
	uiLabelMap.Edit = "${StringUtil.wrapString(uiLabelMap.Edit)}";
	uiLabelMap.BPThruDateNow = "${StringUtil.wrapString(uiLabelMap.BPThruDateNow)}";
	uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.PortOfDischarge = "${StringUtil.wrapString(uiLabelMap.PortOfDischarge)}";
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.BIEPortCode = "${StringUtil.wrapString(uiLabelMap.BIEPortCode)}";
	uiLabelMap.BIEPortName = "${StringUtil.wrapString(uiLabelMap.BIEPortName)}";

	
</script>
<div id="jqxGridImportPort"></div>
<div id="AddImportPort" class="hide popup-bound">
	<div>${uiLabelMap.AddNew} ${uiLabelMap.PortOfDischarge?lower_case}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid margin-top20">
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk">${uiLabelMap.BIEPortCode}</div>
					</div>
					<div class="span7">	
						<input id="facilityCode">
						</input>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk">${uiLabelMap.BIEPortName}</div>
					</div>
					<div class="span7">	
						<input id="facilityName">
						</input>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk"> ${uiLabelMap.Country} </div>
					</div>
					<div class="span7">	
						<div id="countryGeoId" style="width: 100%;" class="green-label"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk"> ${uiLabelMap.Provinces} </div>
					</div>
					<div class="span7">	
						<div id="provinceGeoId" style="width: 100%;" class="green-label"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk"> ${uiLabelMap.County} </div>
					</div>
					<div class="span7">	
						<div id="districtGeoId" style="width: 100%;" class="green-label"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk"> ${uiLabelMap.Ward} </div>
					</div>
					<div class="span7">	
						<div id="wardGeoId" style="width: 100%;" class="green-label"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk"> ${uiLabelMap.Address} </div>
					</div>
					<div class="span7">	
						<input id="address"></input>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action popup-footer">
	        <button id="addCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	    	<button id="addSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<div id='contextMenu' class="hide">
	<ul>
    	<li><i class="fa fa-plus"></i>${uiLabelMap.AddNew}</li>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>

<script src="/imexresources/js/config/listImportPort.js"></script>