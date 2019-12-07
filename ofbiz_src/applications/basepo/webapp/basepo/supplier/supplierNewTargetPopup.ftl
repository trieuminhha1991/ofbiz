<div id="alterpopupWindow" style="display: none;">
	<div>${uiLabelMap.BPOAddSupplierTargets}</div>
	<div style="overflow: hidden;">
		<div id="formAdd" class="form-horizontal">
            <div class="row-fluid">
                <div class="span6">
                    <div class="row-fluid" style="margin-top:10px;">
                        <div class="span4 div-inline-block">
                            <label class="asterisk" style="text-align:right; margin-top: 3px;"> ${uiLabelMap.ProductSupplier}</label>
                        </div>
                        <div class="span8 div-inline-block">
                            <div id="supplierId"></div>
                        </div>
                    </div>
                    <div class="row-fluid" style="margin-top:10px;">
                        <div class="span4 div-inline-block">
                            <label class="asterisk" style="text-align:right; margin-top: 3px;"> ${uiLabelMap.POProduct}</label>
                        </div>
                        <div class="span8 div-inline-block">
                            <div id="productIdBtn">
                                <div id="jqxgridProduct">
                                </div>
                            </div>
                            <input type="hidden" id="productIdTmp" />
                        </div>
                    </div>
                    <div class="row-fluid" style="margin-top:10px;">
                        <div class="span4 div-inline-block">
                            <label class="asterisk" style="text-align:right; margin-top: 3px;"> ${uiLabelMap.FromDate}</label>
                        </div>
                        <div class="span8 div-inline-block">
                            <div id="fromDateDiv"></div>
                        </div>
                    </div>
                </div>
                <div class="span6">
                    <div class="row-fluid no-left-margin" style="margin-top:10px;">
                        <div class="span4 div-inline-block">
                            <label class="asterisk" style="text-align:right; margin-top: 3px;"> ${uiLabelMap.DAUom}</label>
                        </div>
                        <div class="span8">
                            <div id="quantityUomId" style="width: 100%"></div>
                        </div>
                    </div>
                    <div class="row-fluid" style="margin-top:10px;">
                        <div class="span4 div-inline-block">
                            <label class="asterisk" style="text-align:right; margin-top: 3px;"> ${uiLabelMap.BPOQuantity}</label>
                        </div>
                        <div class="span8 div-inline-block">
                            <div id="quantity"></div>
                        </div>
                    </div>
                    <div class="row-fluid" style="margin-top:10px;">
                        <div class="span4 div-inline-block">
                            <label class="asterisk" style="text-align:right; margin-top: 3px;"> ${uiLabelMap.ThruDate}</label>
                        </div>
                        <div class="span8 div-inline-block">
                            <div id="thruDateDiv"></div>
                        </div>
                    </div>
                </div>
            </div>
			<hr style="margin: 5px !important;"/>
			<div class="control-group no-left-margin" style="float:right">
				<button class="btn btn-danger form-action-button pull-right" id="alterCancel"><i class="icon-remove"></i>&nbsp;${uiLabelMap.CommonCancel}</button>
				<button class="btn btn-primary form-action-button pull-right" id="alterSave"><i class="fa fa-check"></i>&nbsp;${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/poresources/js/supplier/supplierNewTargetPopup.js"></script>