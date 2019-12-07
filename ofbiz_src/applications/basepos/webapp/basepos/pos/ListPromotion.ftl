<#include "component://widget/templates/jqwLocalization.ftl" />
<style type="text/css">
#expandCollapsePromo.jqx-fill-state-pressed{
	border-color: #aaa !important;
  	background: #efefef !important;
}
.jqx-widget-content{
	font-family: 'Roboto';
}
#listPromotionWindow .jqx-widget-header.jqx-grid-header{
	height: 56px !important; 
}
#splitterPromo .jqx-splitter-collapse-button-vertical.jqx-fill-state-pressed, #splitterPromo .jqx-splitter-collapse-button-horizontal.jqx-fill-state-pressed{
	background: #0099cc !important;
}
#detailPromotion .jqx-widget-header.jqx-grid-header{
	height: 50px !important;
}
.span-product-category {
    font-weight: 700;
    background-color: #e7e7e7;
    font-size: 8pt;
    display: inline-block!important;
    width: 20px!important;
    min-height: 5px!important;
    margin: 3px 5px 0 0 !important;
    padding: 0 5px!important;
}
</style>
<div id="listPromotionWindow" style="display: none;">
	<div style="background-color: #438EB9; border-color: #0077BC; color: white; font-size: 15px; font-family: 'Open-sans'">
		${uiLabelMap.BPOSListProductPromo} 
		<input type="button" value="${uiLabelMap.BPOSExpand}" id='expandCollapsePromo' />
	</div>
	<div style="overflow: hidden;">
		<div id="splitterPromo">
			<div>
		        <div style="border: none;" id='listPromotion'>
		        </div>
		    </div>
		    <div>
		    	<div id="nestedSpliterPromo">
		    		<div id="overviewPromotion">
		            	<div class="span12 row-fluid" style="margin-left: 10px; margin-top: 10px">
		            		<div class="span6">
		            			<div class="grey-text beauty-label" id="productPromoId"><b> ${uiLabelMap.BSProductPromoId}:</b> </div>
		            			<div class="grey-text beauty-label" id="promoName" ><b>${uiLabelMap.BSPromoName}:</b> </div>
		            			<div class="grey-text beauty-label" id="promoText" ><b>${uiLabelMap.BSContent}:</b></div>
		            			<div class="grey-text beauty-label" id="fromDate"><b>${uiLabelMap.BSFromDate}:</b></div>
		            			<div class="grey-text beauty-label" id="thruDate"><b>${uiLabelMap.BSThruDate}:</b></div>
		            		</div>
		            		<div class="span6">
		            			<div class="grey-text beauty-label" id="partyApply" ><b>${uiLabelMap.BSPartyApply}:</b></div>
		            			<div class="grey-text beauty-label" id="requiredVoucherCode"><b>${uiLabelMap.BSRequireVoucherCode}:</b></div>
		            			<div class="grey-text beauty-label" id="limitPerOrder"> <b>${uiLabelMap.BSAbbUseLimitPerOrder}:</b> </div>
		            			<div class="grey-text beauty-label" id="limitPerCustomer"> <b>${uiLabelMap.BSAbbUseLimitPerCustomer}:</b> </div>
		            			<div class="grey-text beauty-label" id="limitPerPromotion"><b>${uiLabelMap.BSAbbUseLimitPerPromotion}</b></div>
		            		</div>
		            	</div>
		            </div>
		    		<div id="detailPromotion">
		            </div>
		    	</div>
		    </div>
	    </div>
    </div>
</div>
<script type="text/javascript">
	var flagSalesPromoWindow = 0;
	$(document).ready(function () {
		var focusSPromo = 0;
		var toggledSPromo = $("#expandCollapsePromo").jqxToggleButton('toggled');
		$('#splitterPromo').on('expanded', function (event) {
			toggledSPromo = false;
        	$("#expandCollapsePromo")[0].value = '${StringUtil.wrapString(uiLabelMap.BPOSExpand)}';
        });

        $('#splitterPromo').on('collapsed', function (event) {
        	toggledSPromo = true;
        	$("#expandCollapsePromo")[0].value = '${StringUtil.wrapString(uiLabelMap.BPOSCollapse)}';
        });
		
		$("#expandCollapsePromo").on('click', function () {
			if (focusSPromo == 0){
				$('#splitterPromo').jqxSplitter('expand');
	            $("#expandCollapsePromo")[0].value = '${StringUtil.wrapString(uiLabelMap.BPOSExpand)}';
	            focusSPromo = 1;
			} 
            if (toggledSPromo) {
            	$('#splitterPromo').jqxSplitter('expand');
                $("#expandCollapsePromo")[0].value = '${StringUtil.wrapString(uiLabelMap.BPOSExpand)}';
            }else {
            	$('#splitterPromo').jqxSplitter('collapse');
            	$("#expandCollapsePromo")[0].value = '${StringUtil.wrapString(uiLabelMap.BPOSCollapse)}';
            } 
        });
		
		$('#listPromotionWindow').on('close', function (event) {
			flagPopup = true;
			productToSearchFocus();
		});
		
		$('#listPromotionWindow').on('open', function (event) { 
			flagPopup = false;
			Loading.hide('loadingMacro');
			$("#listPromotion").jqxGrid('selectrow', 0);
			$("#listPromotion").jqxGrid('focus');
		});
		
		$('body').keydown(function(e) {
   		    var code = (e.keyCode ? e.keyCode : e.which);
   		    //76 la ma cua L
   		    if(e.ctrlKey && code == 76 && POSPermission.has("POS_PROMOTION_CTRL_L", "VIEW")){
   		    	if (flagPopup){
   		    		if(flagSalesPromoWindow==0){
   		    			updateListPromotion();
   		    		}else{
   		    			Loading.show('loadingMacro');
	    				$("#listPromotion").jqxGrid('updatebounddata');
	    				$('#listPromotionWindow').jqxWindow('open');
   		    		}
   		    	}
   		    	e.preventDefault();
   	 		    return false;
   		    }
   		});
		
		initPromotion();
	});
	
	var source;
	function getFieldType(fName){
    	for (i=0;i < source.datafields.length;i++) {
        	if(source.datafields[i]['name'] == fName){
            	if(!(typeof source.datafields[i]['other'] === 'undefined' || source.datafields[i]['other'] =="")){
                	return  source.datafields[i]['other'];
                } else {
                    return  source.datafields[i]['type'];
                }
            }
        }
	}
	
	function updateListPromotion(){
		Loading.show('loadingMacro');
		flagSalesPromoWindow = 1; 
		$('#listPromotionWindow').jqxWindow('open');
		var urlStr = 'jqxGeneralServicer?sname=JQListPromotion&productStoreId=' + '${productStoreId?if_exists}'; 
		source =
		{
			datafields:
		      [
		           { name: 'productPromoId', type: 'string'},
		           { name: 'promoName', type: 'string'},
		           { name: 'fromDate', type: 'date', other: 'Timestamp'},
		           { name: 'thruDate', type: 'date', other: 'Timestamp'},
		      ],
			cache: false,
		    root: 'results',
		    datatype: "json",
		    updaterow: function (rowid, rowdata) {
		         
		    },
		    beforeprocessing: function (data) {
		   		source.totalrecords = data.TotalRows;
		    },
		    pager: function (pagenum, pagesize, oldpagenum) {
		         
		    },
		    filter: function () {
	            // update the grid and send a request to the server.
	            $("#listPromotion").jqxGrid('updatebounddata');
	        },
	        sort: function () {
	            // update the grid and send a request to the server.
	            $("#listPromotion").jqxGrid('updatebounddata');
	        },
		    sortcolumn: '',
			sortdirection: '',
		    type: 'POST',
		    data:{
		        noConditionFind: 'Y',
		        conditionsFind: 'N',
			},
			pagesize:20,
		    contentType: 'application/x-www-form-urlencoded',
		    url: urlStr
		};
	
		var dataAdapter = new $.jqx.dataAdapter(source, {	
			formatData: function (data) {
		    	if (data.filterscount) {
		        	var filterListFields = "";
		            var tmpFieldName = "";
		            for (var i = 0; i < data.filterscount; i++) {
		                var filterValue = data["filtervalue" + i];
		                var filterCondition = data["filtercondition" + i];
		                var filterDataField = data["filterdatafield" + i];
		                var filterOperator = data["filteroperator" + i];
		                if(getFieldType(filterDataField)=='number'){
		                    filterListFields += "|OLBIUS|" + filterDataField + "(BigDecimal)";
		                }else if(getFieldType(filterDataField)=='date'){
		                    filterListFields += "|OLBIUS|" + filterDataField + "(Date)";
		                }else if(getFieldType(filterDataField)=='Timestamp'){
		                    filterListFields += "|OLBIUS|" + filterDataField + "(Timestamp)[dd/MM/yyyy hh:mm:ss aa]";
		                }
		                else{
		                    filterListFields += "|OLBIUS|" + filterDataField;
		                }
		                if(getFieldType(filterDataField)=='Timestamp'){
		                    if(tmpFieldName != filterDataField){
		                        filterListFields += "|SUIBLO|" + filterValue + " 00:00:00 am";
		                    }else{
		                        filterListFields += "|SUIBLO|" + filterValue + " 11:59:59 pm";
		                    }
		                }else{
		                    filterListFields += "|SUIBLO|" + filterValue;
		                }
		                filterListFields += "|SUIBLO|" + filterCondition;
		                filterListFields += "|SUIBLO|" + filterOperator;
		                tmpFieldName = filterDataField;
		            }
		            data.filterListFields = filterListFields;
				}
	            data.$skip = data.pagenum * data.pagesize;
	            data.$top = data.pagesize;
	            data.$inlinecount = "allpages";
	            return data;
			},
		});
		$("#listPromotion").jqxGrid({
		    width: '100%',
		    height: 550,
		    source: dataAdapter,
		    filterable: true,
		    showfilterrow : true,
		    sortable: true,
		    virtualmode: true,
		    rendergridrows: function () {
		    	return dataAdapter.records;
		    },
		 	autoheight: false,
		    columnsresize: true,
		    pagesize: 20,
		    pageable: true,
		    localization: getLocalization(),
		    columns: [
	                 { text: '${StringUtil.wrapString(uiLabelMap.BSProductPromoId)}', datafield: 'productPromoId', cellclassname: cellclassname, width: 100},
	              	 { text: '${StringUtil.wrapString(uiLabelMap.BSPromoName)}', datafield: 'promoName', cellclassname: cellclassname, width: 600},
	              	 { text: '${StringUtil.wrapString(uiLabelMap.BSFromDate)}', filtertype: 'range', cellclassname: cellclassname, cellsformat: 'dd/MM/yyyy', datafield: 'fromDate'},
	              	 { text: '${StringUtil.wrapString(uiLabelMap.BSThruDate)}', filtertype: 'range', cellclassname: cellclassname, cellsformat: 'dd/MM/yyyy', datafield: 'thruDate'},
	         ],
	         ready: function(){   
	 	    	$("#listPromotion").jqxGrid('selectrow', 0);
	 	     },
		});
	
		$("#listPromotion").on("bindingcomplete", function (event){
			$("#listPromotion").on("filter", function (event){
				var getRows = $("#listPromotion").jqxGrid("getrows");
				if(getRows[0]){
					var firstRow = getRows[0];
					var productPromoId = firstRow.productPromoId;
					updateOverviewPromotion(productPromoId);
					updateContentPromotion(productPromoId);
				}else{
					resetOverviewPromotion();
					var promotionData = [{}];
					updateSorucePromotion(promotionData);
				}
			});
   	
			$("#listPromotion").on("sort", function (event){
				var getRows = $("#listPromotion").jqxGrid("getrows");
				if(getRows[0]){
					var firstRow = getRows[0];
					var productPromoId = firstRow.productPromoId;
					updateOverviewPromotion(productPromoId);
					updateContentPromotion(productPromoId);
				}else{
					resetOverviewPromotion();
					var promotionData = [{}];
					updateSorucePromotion(promotionData);
				}
		 	});

	 		var data = $("#listPromotion").jqxGrid('getrows');
			if (data.length > 0){
				$("#listPromotion").jqxGrid('selectrow', 0);
				$("#listPromotion").jqxGrid('focus');
			} else {
				$("#listPromotionWindow").jqxWindow('focus');
				resetOverviewPromotion();
				var promotionData = [{}];
				updateSorucePromotion(promotionData);
			}
		    	 
		});
	
		$('#listPromotion').on('rowselect', function (event) {
			var args = event.args;
		 	var row = event.args.row;
			if (row && row.productPromoId){
				updateOverviewPromotion(row.productPromoId);
				updateContentPromotion(row.productPromoId);
			}
  		});
      
  		$('#listPromotion').on('pagechanged', function (event) {
			var args = event.args;
			var pagenum = args.pagenum;
			var pagesize = args.pagesize;
			$("#listPromotion").on("bindingcomplete", function (event) {
				$("#listPromotion").jqxGrid('selectrow', pagenum*pagesize);
			});
 		});
	}
	
	function updateOverviewPromotion(productPromoId){
		var param = 'productPromoId=' + productPromoId;
   	    $.ajax({url: 'GetOverviewPromotion',
   	        data: param,
   	        type: 'post',
   	        async: false,
   	        success: function(data) {
   	            getResultOfUpdateOverviewPromotion(data);
   	        },
   	        error: function(data) {
   	        	getResultOfUpdateOverviewPromotion(data);
   	        }
   	    });
	}
	
	function getResultOfUpdateOverviewPromotion(data){
		var serverError = getServerError(data);
   	    if (serverError != "") {
   	    	bootbox.hideAll();
   	    	$("#listPromotionWindow").jqxWindow('close');
   	    	bootbox.alert(serverError);
   	    } else {
   	    	var productPromoId = data.productPromoId;
   	    	var promoName = data.promoName;
   	    	var promoText = data.promoText;
   	    	var fromDate = data.fromDate;
   	    	var thruDate = data.thruDate;
   	    	var roleTypeId = data.roleTypeId;
   	    	var requireCode = data.requireCode;
   	    	var useLimitPerOrder = data.useLimitPerOrder;
   	    	var useLimitPerCustomer = data.useLimitPerCustomer;
   	    	var useLimitPerPromotion = data.useLimitPerPromotion;
   	    	if(productPromoId){
   	    		$("#productPromoId").html("<b>${StringUtil.wrapString(uiLabelMap.BSProductPromoId)}: </b>" + "<b style='color: #037c07'>" + productPromoId + "</b>");
   	    	}else{
   	    		$("#productPromoId").html("<b>${StringUtil.wrapString(uiLabelMap.BSProductPromoId)}: </b>");
   	    	}
   	    	if(promoName){
   	    		$("#promoName").html("<b>${StringUtil.wrapString(uiLabelMap.BSPromoName)}: </b>" + "<span style='color: #037c07'>" + promoName + "</span>");
   	    	}else{
   	    		$("#promoName").html("<b>${StringUtil.wrapString(uiLabelMap.BSPromoName)}: </b>");
   	    	}
   	    	if(promoText){
   	    		$("#promoText").html("<b>${StringUtil.wrapString(uiLabelMap.BSContent)}: </b>" + "<span style='color: #037c07'>" + promoText + "</span>");
   	    	}else{
   	    		$("#promoText").html("<b>${StringUtil.wrapString(uiLabelMap.BSContent)}: </b>");
   	    	}
   	    	if(fromDate){
   	    		$("#fromDate").html("<b>${StringUtil.wrapString(uiLabelMap.BSFromDate)}: </b>" + "<span style='color: #037c07'>" + fromDate + "</span>");
   	    	}else{
   	    		$("#fromDate").html("<b>${StringUtil.wrapString(uiLabelMap.BSFromDate)}: </b>");
   	    	}
   	    	if(thruDate){
   	    		$("#thruDate").html("<b>${StringUtil.wrapString(uiLabelMap.BSThruDate)}: </b>" + "<span style='color: #037c07'>" + thruDate + "</span>");
   	    	}else{
   	    		$("#thruDate").html("<b>${StringUtil.wrapString(uiLabelMap.BSThruDate)}: </b>");
   	    	}
   	    	if(roleTypeId){
   	    		$("#partyApply").html("<b>${StringUtil.wrapString(uiLabelMap.BSPartyApply)}: </b>" + "<span style='color: #037c07'>" + roleTypeId + "</span>");
   	    	}else{
   	    		$("#partyApply").html("<b>${StringUtil.wrapString(uiLabelMap.BSPartyApply)}: </b>");
   	    	}
   	    	if(requireCode){
   	    		$("#requiredVoucherCode").html("<b>${StringUtil.wrapString(uiLabelMap.BSRequireVoucherCode)}: </b>" + "<span style='color: #037c07'>" + requireCode + "</span>");
   	    	}else{
   	    		$("#requiredVoucherCode").html("<b>${StringUtil.wrapString(uiLabelMap.BSRequireVoucherCode)}: </b>");
   	    	}
   	    	if(useLimitPerOrder){
   	    		$("#limitPerOrder").html("<b>${StringUtil.wrapString(uiLabelMap.BSAbbUseLimitPerOrder)}: </b> " + "<span style='color: #037c07'>" + useLimitPerOrder + "</span>");
   	    	}else{
   	    		$("#limitPerOrder").html("<b>${StringUtil.wrapString(uiLabelMap.BSAbbUseLimitPerOrder)}: </b> ");
   	    	}
   	    	if(useLimitPerCustomer){
   	    		$("#limitPerCustomer").html("<b>${StringUtil.wrapString(uiLabelMap.BSAbbUseLimitPerCustomer)}: </b> " + "<span style='color: #037c07'>" + useLimitPerCustomer + "</span>");
   	    	}else{
   	    		$("#limitPerCustomer").html("<b>${StringUtil.wrapString(uiLabelMap.BSAbbUseLimitPerCustomer)}: </b> ");
   	    	}
   	    	if(useLimitPerPromotion){
   	    		$("#limitPerPromotion").html("<b>${StringUtil.wrapString(uiLabelMap.BSAbbUseLimitPerPromotion)}: </b> " + "<span style='color: #037c07'>" + useLimitPerPromotion + "</span>");
   	    	}else{
   	    		$("#limitPerPromotion").html("<b>${StringUtil.wrapString(uiLabelMap.BSAbbUseLimitPerPromotion)}: </b> ");
   	    	}
   	    }
	}
	
	function resetOverviewPromotion(){
   		$('#productPromoId').html("<b>${StringUtil.wrapString(uiLabelMap.BSProductPromoId)}: </b>");
   		$("#promoName").html("<b>${StringUtil.wrapString(uiLabelMap.BSPromoName)}: </b>");
   		$("#promoText").html("<b>${StringUtil.wrapString(uiLabelMap.BSContent)}: </b>");
   		$("#fromDate").html("<b>${StringUtil.wrapString(uiLabelMap.BSFromDate)}: </b>");
   		$("#thruDate").html("<b>${StringUtil.wrapString(uiLabelMap.BSThruDate)}: </b>");
   		$("#partyApply").html("<b>${StringUtil.wrapString(uiLabelMap.BSPartyApply)}: </b>");
   		$("#requiredVoucherCode").html("<b>${StringUtil.wrapString(uiLabelMap.BSRequireVoucherCode)}: </b>");
   		$("#limitPerOrder").html("<b>${StringUtil.wrapString(uiLabelMap.BSAbbUseLimitPerOrder)}: </b> ");
   		$("#limitPerCustomer").html("<b>${StringUtil.wrapString(uiLabelMap.BSAbbUseLimitPerCustomer)}: </b> ");
   		$("#limitPerPromotion").html("<b>${StringUtil.wrapString(uiLabelMap.BSAbbUseLimitPerPromotion)}: </b> ");
   	}
	
	var sourcePromoItems = 
	{
   		localdata: [],
   	   	dataType: "array",
       	datafields: [
          	{ name: 'productPromoRuleId', type: 'string' },
          	{ name: 'ruleName', type: 'string' },
          	{ name: 'productCategoryCond', type: 'string' },
          	{ name: 'condition', type: 'string' },
          	{ name: 'productCategoryAction', type: 'string' },
          	{ name: 'action', type: 'string' },
       	]
	};
	
	function initPromotion(){
		var dataAdapterItems = new $.jqx.dataAdapter(sourcePromoItems);
   		$("#detailPromotion").jqxGrid(
        {
        	width: '100%',
            height: '100%',
            source: dataAdapterItems,
            filterable: false,
            pageable: false,
            sortable: false,
            columnsresize: true,
            localization: getLocalization(),
            autorowheight:true,
            autoheight: true,
            columns: [
            	{ text: '${StringUtil.wrapString(uiLabelMap.BSId)}', datafield: 'productPromoRuleId', width: 50},
            	{ text: '${StringUtil.wrapString(uiLabelMap.BSRuleName)}', datafield: 'ruleName', width: 90},
                { text: '${StringUtil.wrapString(uiLabelMap.BSCategoryProductApply)}', datafield: 'productCategoryCond', columngroup: 'Condition', width: 220},
                { text: '${StringUtil.wrapString(uiLabelMap.BSCondition)}', datafield: 'condition', columngroup: 'Condition', width: 160},
                { text: '${StringUtil.wrapString(uiLabelMap.BSCategoryProductApply)}', datafield: 'productCategoryAction', columngroup: 'Action', width: 220},
                { text: '${StringUtil.wrapString(uiLabelMap.BSAction)}', datafield: 'action', columngroup: 'Action', width: 160},
            ],
           	columngroups: [
           		{ text: '${StringUtil.wrapString(uiLabelMap.BSCondition)}', align: 'center', name: 'Condition' },
            	{ text: '${StringUtil.wrapString(uiLabelMap.BSAction)}', align: 'center', name: 'Action' },
           	],
        });
	}
	
	function updateContentPromotion(productPromoId){
		var param = "productPromoId=" + productPromoId;
   		$.ajax({url: 'GetContentPromotion',
   	        data: param,
   	        type: 'post',
   	        async: false,
   	        success: function(data) {
   	            getResultOfUpdateContentPromotion(data);
   	        },
   	        error: function(data) {
   	        	getResultOfUpdateContentPromotion(data);
   	        }
   	    });
	}
	
	function getResultOfUpdateContentPromotion(data){
		var serverError = getServerError(data);
   	    if (serverError != "") {
   	    	bootbox.hideAll();
   	    	$("#listPromotionWindow").jqxWindow('close');
   	    	bootbox.alert(serverError);
   	    } else {
   	    	updateSorucePromotion(data);
   		}
	}
	
	function updateSorucePromotion(data){
		sourcePromoItems.localdata = data.listRuleItems;
	    var dataAdapterItems = new $.jqx.dataAdapter(sourcePromoItems);
	    $("#detailPromotion").jqxGrid({source: dataAdapterItems});
	    $("#detailPromotion").jqxGrid('updatebounddata');
	}
	var cellclassname = function (row, column, value, data) {
		if (data.thruDate) {
			if (data.thruDate < new Date()) {
				return "expired";
			} else if (data.fromDate > new Date()) {
				return "expired";
			}
		}
		return "available";
	};
</script>
<script type="text/javascript">
	$.jqx.theme = 'basic';
	$(document).ready(function() {
		theme = $.jqx.theme;
		$("#listPromotionWindow").jqxWindow({
			width: "95%", maxWidth: "95%", minHeight: "95%", resizable: false, draggable: false, isModal: true, autoOpen: false, modalOpacity: 0.7, theme:theme,
			position: { x: '2%', y: '5%' }
		});
		$("#listPromotionWindow").disableTab();
		$("#splitterPromo").jqxSplitter({ width: "100%", height: "95%", splitBarSize: 0, panels: [{size: "30%", min: "30%", collapsible: false}, { size: 100 }] });
		$("#nestedSpliterPromo").jqxSplitter({ width: "100%", height: "100%", splitBarSize: 0, orientation: 'horizontal', panels: [{ size: 170, min: 150 }] });
		$("#expandCollapsePromo").jqxToggleButton({toggled: true});
	});
</script>