<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script>
$('#popupWindowContainer').jqxValidator({
    rules: [
				{ input: '#containerNumber', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup', rule: 'required' },
				{ input: '#totalNetWeight', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup', rule: 'required' },
				{ input: '#invoiceNumber', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup', rule: 'required' },
				{ input: '#orderNumberSupp', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup', rule: 'required' },
				{ input: '#totalGrossWeight', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup', rule: 'required' },
				{ input: '#totalNetWeight', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup', rule: 'required' },
				{ input: '#orderPurchaseId', message: '${StringUtil.wrapString(uiLabelMap.FieldRequireSelected)}', action: 'change', 
					rule: function () {
						var selected = $("#orderPurchaseId").jqxComboBox('getSelectedItem');
						if(selected){
							return true;
						}else{
							return false;
						}
					}
				},
				{ input: '#orderTypeSupp', message: '${StringUtil.wrapString(uiLabelMap.FieldRequireSelected)}', action: 'change', 
					rule: function () {
						var selected = $("#orderTypeSupp").jqxComboBox('getSelectedItem');
						if(selected){
							return true;
						}else{
							return false;
						}
					}
				},
				{ input: '#packingListNumber', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
					rule: function (input, commit) {
						var value = $("#packingListNumber").val();
						if (value) {
							return true;
						}
						return false;
					}
				},
				{ input: '#invoiceDate', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
					rule: function (input, commit) {
						var value = $('#invoiceDate').jqxDateTimeInput('getDate');
						if (value > 0) {
							return true;
						}
						return false;
					}
				},
				{ input: '#packingListDate', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
					rule: function (input, commit) {
						var value = $('#packingListDate').jqxDateTimeInput('getDate');
						if (value > 0) {
							return true;
						}
						return false;
					}
				}
				
           ],
           position: 'bottom'
});
</script>