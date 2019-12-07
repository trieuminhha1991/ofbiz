<@jqGridMinimumLib/>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script>
	<#assign listUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "SHIPMENT_PACKING"), null, null, null, false)>
	var shipmentPackingUom = [];
	
	<#if listUoms?has_content>
		<#list listUoms as item>
			var item = {
				uomFromId: '${item.uomId?if_exists}',
				description: '${StringUtil.wrapString(item.get("description", locale))}'
			}
			shipmentPackingUom.push(item);
		</#list>
	</#if>
	
	<#assign listUom3s = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var uomData = [];
	
	<#if listUom3s?has_content>
		<#list listUom3s as item>
			var item = {
				uomId: '${item.uomId?if_exists}',
				description: '${StringUtil.wrapString(item.get("description", locale))}'
			}
			uomData.push(item);
		</#list>
	</#if>
	
	var getUomDesc = function (uomId) {
		for (var i in uomData) {
			var x = uomData[i];
			if (x.uomId == uomId) {
				return x.description;
			}
		}
		for (var i in shipmentPackingUom) {
			var x = shipmentPackingUom[i];
			if (x.uomFromId == uomId) {
				return x.description;
			}
		}
		return uomId;
	}
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.SupplierId = "${StringUtil.wrapString(uiLabelMap.SupplierId)}";
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.SupplierName = "${StringUtil.wrapString(uiLabelMap.SupplierName)}";
	uiLabelMap.Delete = "${StringUtil.wrapString(uiLabelMap.Delete)}";
	uiLabelMap.AddNew = "${StringUtil.wrapString(uiLabelMap.AddNew)}";
	uiLabelMap.Edit = "${StringUtil.wrapString(uiLabelMap.Edit)}";
	uiLabelMap.BPThruDateNow = "${StringUtil.wrapString(uiLabelMap.BPThruDateNow)}";
	uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}";
	uiLabelMap.AreYouSureDelete = "${StringUtil.wrapString(uiLabelMap.AreYouSureDelete)}";
	uiLabelMap.BIENeedGreaterThanZero = "${StringUtil.wrapString(uiLabelMap.BIENeedGreaterThanZero)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.uomFromId = "${StringUtil.wrapString(uiLabelMap.uomFromId)}";
	uiLabelMap.uomToId = "${StringUtil.wrapString(uiLabelMap.uomToId)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.ConversionFactor = "${StringUtil.wrapString(uiLabelMap.ConversionFactor)}";
	uiLabelMap.ConfigCapacity = "${StringUtil.wrapString(uiLabelMap.ConfigCapacity)}";
	
</script>

<div id='contextMenu' class="hide">
	<ul>
    	<li><i class="fa fa-plus"></i>${uiLabelMap.AddNew}</li>
    	<li><i class="fa fa-edit"></i>${uiLabelMap.Edit}</li>
    	<li><i class="fa fa-trash red"></i>${uiLabelMap.Delete}</li>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>
<div id="jqxGridConfigs"></div>

<div id="alterpopupWindow" class="hide popup-bound">
	<div>${uiLabelMap.ConfigCapacity}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid margin-top20">
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk">${uiLabelMap.Product}</div>
					</div>
					<div class="span7">	
						<div id="product" class="green-label">
							<div id="jqxGridProduct">
				            </div>
						</div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk">${uiLabelMap.uomFromId}</div>
					</div>
					<div class="span7">	
						<div id="uomFromId" class="green-label">
						</div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk">${uiLabelMap.uomToId}</div>
					</div>
					<div class="span7">	
						<div id="uomToId" class="green-label">
			            </div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk">${uiLabelMap.ConversionFactor}</div>
					</div>
					<div class="span7">	
						<div id="quantityConvert">
			            </div>
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

<div id="editPopupWindow" class="hide popup-bound">
	<div>${uiLabelMap.Edit} ${uiLabelMap.ConfigCapacity?lower_case}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid margin-top20">
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div>${uiLabelMap.Product}</div>
					</div>
					<div class="span7">	
						<div id="productDT" class="green-label">
						</div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div >${uiLabelMap.uomFromId}</div>
					</div>
					<div class="span7">	
						<div id="uomFromIdDT" class="green-label">
						</div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div>${uiLabelMap.uomToId}</div>
					</div>
					<div class="span7">	
						<div id="uomToIdDT">
			            </div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk">${uiLabelMap.ConversionFactor}</div>
					</div>
					<div class="span7">	
						<div id="quantityConvertEdit">
			            </div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action popup-footer">
	        <button id="editCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	    	<button id="editSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<script src="/imexresources/js/config/configCapacitys.js?v=1.0.0"></script>