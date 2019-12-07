<div id='contextMenu' style="display:none;">
	<ul>
	    <li><i class="icon-ok open-sans"></i>${StringUtil.wrapString(uiLabelMap.DARefresh)}</li>
	    <li><i class="icon-trash open-sans"></i>${StringUtil.wrapString(uiLabelMap.DACopy)}</li>
	    <li><i class="fa-file-pdf-o"></i>${StringUtil.wrapString(uiLabelMap.DAExportToPDF)}</li>
	</ul>
</div>
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	$("#contextMenu").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#contextMenu").on('itemclick', function (event) {
		var args = event.args;
        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
        var tmpKey = $.trim($(args).text());
        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DARefresh)}") {
        	$("#jqxgrid").jqxGrid('updatebounddata');
        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DACopy)}") {
        	var data = $("#jqxgrid").jqxGrid("getrowdata", rowindex);
			var agreementId = data.agreementId;
			if (agreementId != undefined && agreementId != null) {
				copyAgreement(agreementId);
			}
        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DAExportToPDF)}") {
        	var data = $("#jqxgrid").jqxGrid("getrowdata", rowindex);
			if (data != undefined && data != null) {
				var agreementId = data.agreementId;
				var agreementTypeId = data.agreementTypeId;
				if ("PROMO_EXHIBITED_AGRE" == agreementTypeId) {
					var url = 'exhibitedAgreement.pdf?agreementId=' + agreementId;
					var win = window.open(url, '_blank');
  					win.focus();
				}
			}
        }
        
	});
</script>