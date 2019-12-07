<div style="position:relative">
	<div class="row-fluid">
		<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
			<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BSRequiredByDate}:</label>
						</div>
						<div class="div-inline-block">
							<span id="strRequiredByDate"></span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BSRequirementStartDate}:</label>
						</div>
						<div class="div-inline-block">
							<span id="strRequirementStartDate"></span>
						</div>
					</div>
				</div><!--.span6-->
				<div class="span6">
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BSDescription}:</label>
						</div>
						<div class="div-inline-block">
							<span id="strDescription"></span>
						</div>
					</div>
				</div><!--.span6-->
			</div>
		</div><!-- .form-horizontal -->
	</div><!--.row-fluid-->
	<div class="row-fluid">
		<div class="span12">
			<#assign columnlistConfirm = "
					{text: '${StringUtil.wrapString(uiLabelMap.BSOrderId)}', dataField: 'orderId', pinned: true, width: '14%'},
					{ text: '${uiLabelMap.BSCreateDate}', dataField: 'orderDate', width: '14%', cellsformat: 'dd/MM/yyyy', filtertype:'range',
						cellsrenderer: function(row, colum, value) {
							return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
						}
					},
					{text: '${StringUtil.wrapString(uiLabelMap.BSDesiredDeliveryDate)}', dataField: 'fullDeliveryDate', cellsformat: 'dd/MM/yyyy', filtertype:'range',
						cellsrenderer: function(row, colum, value) {
							var data = $('#jqxgridOrderConfirm').jqxGrid('getrowdata', row);
							if (typeof(data) != 'undefined') {
								var returnStr = \"<span>\";
								if (data.estimatedDeliveryDate != null) {
									returnStr += jOlbUtil.dateTime.formatFullDate(data.estimatedDeliveryDate)
									if (data.shipAfterDate != null || data.shipBeforeDate != null) {
										returnStr += ' (';
										returnStr += jOlbUtil.dateTime.formatFullDate(data.shipAfterDate) + ' - ' + jOlbUtil.dateTime.formatFullDate(data.shipBeforeDate);
										returnStr += ')';
									}
								} else {
									returnStr += jOlbUtil.dateTime.formatFullDate(data.shipAfterDate) + ' - ' + jOlbUtil.dateTime.formatFullDate(data.shipBeforeDate);
								}
								returnStr += \"</span>\";
								return returnStr;
							}
						}
					},
					{text: '${StringUtil.wrapString(uiLabelMap.BSTotalWeight)}', dataField: 'totalWeight', width: '12%', cellsalign: 'right', cellsformat: 'd'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}', dataField: 'customerCode', width: '16%'},
				"/>
			
			<div id="jqxgridOrderConfirm" style="width: 100%"></div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		if (typeof(dataSelected) == "undefined") var dataSelected = [];
		OlbQuotationConfirm.init();
	});
	var OlbQuotationConfirm = (function(){
		var init = function(){
			initElementComplex();
		};
		var initElementComplex = function(){
			var configProductList = {
				datafields: ${dataField},
				columns: [${columnlistConfirm}],
				useUrl: false,
				pageable: true,
				pagesize: 15,
				showtoolbar: false,
				width: '100%',
				bindresize: true,
				groupable: false,
				localization: getLocalization(),
				showtoolbar: true,
				showdefaultloadelement: true,
				autoshowloadelement: true,
				virtualmode: false,
			};
			new OlbGrid($("#jqxgridOrderConfirm"), dataSelected, configProductList, []);
		};
		return {
			init: init
		};
	}());
</script>
