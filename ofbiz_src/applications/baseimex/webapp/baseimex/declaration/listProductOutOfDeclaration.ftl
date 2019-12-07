<@jqGridMinimumLib/>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script>
	if (uiLabelMap == undefined) var uiLabelMap = {};

	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.Status = "${StringUtil.wrapString(uiLabelMap.Status)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.BIEProductOutOfDeclaration = "${StringUtil.wrapString(uiLabelMap.BIEProductOutOfDeclaration)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.FromDate = "${StringUtil.wrapString(uiLabelMap.FromDate)}";
	uiLabelMap.ThruDate = "${StringUtil.wrapString(uiLabelMap.ThruDate)}";

	
</script>
<div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div id="jqxGridProduct"></div>
			</div>
		</div>
	</div>
</div>


<script type="text/javascript" src="/imexresources/js/declaration/listProductOutOfDeclaration.js"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>