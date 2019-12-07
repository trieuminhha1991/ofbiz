<style>
div[id^="statusbartreeGrid"] {
	  width: 0 !important;
	 }
</style>
<script type="text/javascript">
	var placeHolder = "${StringUtil.wrapString(uiLabelMap.BSPleaseChoose)}";
	var filterPlaceHolder = "${StringUtil.wrapString(uiLabelMap.BSLookingFor)}";
</script>
<script type="text/javascript" src="/salesresources/js/popup.extend.js"></script>
<script type="text/javascript">
	<#assign ownerPartyId = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	
	var levelSource = [
		{'text': 'DSA', 'value': 'DSA'},
		{'text': 'CSM', 'value': 'CSM'},
		{'text': 'RSM', 'value': 'RSM'},
		{'text': 'ASM', 'value': 'ASM'},
		{'text': 'SUP', 'value': 'SUP'},
    ];
	
	var gLevel;	
	var dateCurrent = new Date();
	var currentFirstDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);	
	
	var gProduct; var gSalesChannel; var gChannelType; var gDateType; var gGrid1; var gGrid2;
	
	$($(".breadcrumb").children()[1]).html("${uiLabelMap.Report} <span class='divider'><i class='icon-angle-right'></i></span>${StringUtil.wrapString(uiLabelMap.BSSynthesisReport)}");

</script>

<div class="grid grid-value">
	<script id="gridTest2">
		$(function(){
			var column = [];
			var groups = [];
			
			column.push({text: '${StringUtil.wrapString(uiLabelMap.OrgUnitName)}', datafield: {name: 'depName', type: 'string'}, pinned: true, width: '25%'});
			column.push({text: '${StringUtil.wrapString(uiLabelMap.OrgUnitId)}', datafield: {name: 'depId', type: 'string'}, pinned: true, width: '20%', hidden: true});
			 
			$.ajax({url: 'getStoreListColumn',
			    type: 'post',
			    async: false,
			    success: function(data) {
			    	var listDatafield = data.listResultStore;
			    	for (var i = 0; i < listDatafield.length; i++){
			    		var name = listDatafield[i].internal_name ? listDatafield[i].internal_name : "";
			    		var code = listDatafield[i].product_code;
			    		var full_title = "";
			    		if (OlbiusConfig.report.show.productName) {
			    			full_title += listDatafield[i].internal_name ? listDatafield[i].internal_name : "";
						}
			    		if (OlbiusConfig.report.show.productCode) {
			    			if (full_title != "") {
			    				full_title += "</b><br><b>";
							}
			    			full_title += listDatafield[i].product_code;
			    		}
			    		if(full_title){
			    			var field2 = {text: full_title, align: 'center', name: listDatafield[i].product_code};
				    		var field = {text: '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', datafield: {name: listDatafield[i].product_id+"_q", type: 'string'}, width: '10%', cellsalign: 'right', cellsformat: 'n2', align: 'center', filterable: false, columngroup: listDatafield[i].product_code};
				    		var field3 = {text: '${StringUtil.wrapString(uiLabelMap.BSValue)}', datafield:{name: listDatafield[i].product_id+"_t", type: 'string'}, width: '10%', cellsalign: 'right', cellsformat: 'n2', align: 'center', filterable: false, columngroup: listDatafield[i].product_code};
			    		}
			    		column.push(field);
			    		column.push(field3);
			    		groups.push(field2);
			    	}
			    },
			    error: function(data) {
			    	alert('Error !!');
			    }
		}); 
			
			gGrid2 = OlbiusUtil.treeGrid({
				id: 'gridTest2',
				columns: column,
				columnGroups: groups,
				url: 'evaluateSynthesisRevenueV2',
				hierarchy: {
					keyDataField: {name: 'depId'},
					parentDataField: 'levelId'
				},
				pageable: true,
				pagerMode: 'advanced',
			// pageSizeMode: 'root'
				theme: 'olbius',
				width: '100%',
				title: '${StringUtil.wrapString(uiLabelMap.BSSynthesisReport)}',
				columnsHeight: 30,
				showStatusbar: false,
				popup: [
					{
						group: "dateTime",
						id: "dateTime",
					},
					<#if products?if_exists?index_of(",", 0) != -1>
					{
						action: 'jqxGridMultiple',
						params: {
							id : 'product',  
							label : '${StringUtil.wrapString(uiLabelMap.POProduct)}',
							grid: {
								source: ${StringUtil.wrapString(products)},
								id: "productId",
								width: 550,
								sortable: true,
								pagesize: 5,
								columnsresize: true,
								pageable: true,
								altrows: true,
								showfilterrow: true,
								filterable: true,
								columns: [
									{ text: "${StringUtil.wrapString(uiLabelMap.ProductProductId)}", datafield: 'productCode', width: 150 }, 
									{ text: "${StringUtil.wrapString(uiLabelMap.ProductProductName)}", datafield: 'productName' }
								]
							}
						}
					},</#if>
					<#if salesChannel?if_exists?index_of(",", 0) != -1>
					{
						action: 'jqxGridMultiple',
						params: {
							id : 'salesChannel',  
							label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}',
							grid: {
								source: ${StringUtil.wrapString(salesChannel)},
								id: "productStoreId",
								width: 500,
								sortable: true,
								pagesize: 5,
								columnsresize: true,
								pageable: true,
								altrows: true,
								showfilterrow: true,
								filterable: true,
								columns: [
									{ text: "${StringUtil.wrapString(uiLabelMap.BSSalesChannelId)}", datafield: 'productStoreId', width: 150 }, 
									{ text: "${StringUtil.wrapString(uiLabelMap.BSPSSalesChannelName)}", datafield: 'storeName' }
								]
							}
						}
					},</#if>
					<#if channelType?if_exists?index_of(",", 0) != -1>
					{
						action: 'jqxGridMultiple',
						params: {
							id : 'channelType',  
							label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannelType2)}',
							grid: {
								source: ${StringUtil.wrapString(channelType)},
								id: "enumId",
								width: 500,
								sortable: true,
								pagesize: 5,
								columnsresize: true,
								pageable: true,
								altrows: true,
								showfilterrow: true,
								filterable: true,
								columns: [
									{ text: "${StringUtil.wrapString(uiLabelMap.BSSalesChannelTypeCode)}", datafield: 'enumId', width: 200 }, 
									{ text: "${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}", datafield: 'description' }
								]
							}
						}
					},</#if>
				],
				apply: function (grid, popup) {
	                return $.extend({
		                product: popup.val('product'),
		                salesChannel: popup.val('salesChannel'),
		                channelType: popup.val('channelType'),
	                }, popup.group("dateTime").val());
            	},
			});
			
	    });
	    
	</script>
</div>