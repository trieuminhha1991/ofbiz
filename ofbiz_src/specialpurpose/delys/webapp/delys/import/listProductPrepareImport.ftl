<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/delys/images/js/import/Underscore1.8.3.js"></script>
<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>

<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	var grid = $($(parentElement).children()[0]);
	$(grid).attr('id','jqxgridDetail'+index);
	var sourceGridDetail =
    {
        localdata: datarecord.rowDetail,
        datatype: 'local',
        datafields:
        [
           { name: 'productId', type: 'string'},
		   { name: 'internalName', type: 'string'},
		   { name: 'planQuantity', type: 'number'},
		   { name: 'thruDateQA', type: 'date', other: 'Timestamp'},
		   { name: 'expireDateQA', type: 'number'}
        ]
    };
    var dataAdapterGridDetail = new $.jqx.dataAdapter(sourceGridDetail);
    grid.jqxGrid({
    	localization: getLocalization(),
        width: '98%',
        height: '92%',
        theme: 'olbius',
        source: dataAdapterGridDetail,
        sortable: true,
        pagesize: 5,
 		pageable: true,
        selectionmode: 'singlerow',
        columns: [
					{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductId)}', datafield: 'productId', width: 200, align: 'center' },
					{ text: '${StringUtil.wrapString(uiLabelMap.DAInternalName)}', datafield: 'internalName', align: 'center'},
					{ text: '${StringUtil.wrapString(uiLabelMap.ImportVolume)}', datafield: 'planQuantity', width: 250, align: 'center',
						cellsrenderer: function(row, colum, value){
							return '<span class=\"text-right\">' + value.toLocaleString('${locale}') + '</span>';
						}
					},
					{ text: '${uiLabelMap.thruDateOfPubich}', datafield: 'thruDateQA', width: 180, filtertype: 'range', cellsformat: 'dd/MM/yyyy', align: 'center'},
					{ text: '${StringUtil.wrapString(uiLabelMap.ImportRemainDays)}', datafield: 'expireDateQA', width: 150, align: 'center',
					   cellsrenderer: function(row, colum, value){
						   var data = grid.jqxGrid('getrowdata', row);
						   var thruDateQA = data.thruDateQA;
						   return '<span class=\"text-right\">' + excuteDate(thruDateQA) + '</span>';
						}
					}
                 ]
    });
    
    grid.on('contextmenu', function () {
        return false;
    });
    grid.on('rowclick', function (event) {
        if (event.args.rightclick) {
        	grid.jqxGrid('selectrow', event.args.rowindex);
            var scrollTop = $(window).scrollTop();
            var scrollLeft = $(window).scrollLeft();
            contextMenu.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
		    var rowindex = event.args.rowindex;
		    var productId = grid.jqxGrid('getcellvalue', rowindex, 'productId');
		    bindProductIdToGlobal(productId);
            return false;
        }
    });
}"/>
<#assign dataField="[{ name: 'productPlanId', type: 'string'},
					{ name: 'internalPartyId', type: 'string'},
					{ name: 'productPlanName', type: 'string'},
					{ name: 'periodName', type: 'string'},
					{ name: 'rowDetail', type: 'string'}
					]"/>

<#assign columnlist="{ text: '${StringUtil.wrapString(uiLabelMap.PlanId)}', datafield: 'productPlanId', align: 'center', width: 270, editable: false},
					{ text: '${StringUtil.wrapString(uiLabelMap.accGeoName)}', datafield: 'internalPartyId', align: 'center', width: 300},
					{ text: '${StringUtil.wrapString(uiLabelMap.PlanName)}', datafield: 'productPlanName', align: 'center',
						cellsrenderer: function(row, colum, value){
							value.split(': ')[1]?value='${StringUtil.wrapString(uiLabelMap.Month)} ' + value.split(': ')[1]:value='${StringUtil.wrapString(uiLabelMap.Month)} ' + value;
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	    			        var periodName = data.periodName;
	    			        periodName.split(': ')[1]?periodName=periodName.split(': ')[1]:periodName=periodName;
							return '<span class=\"text-left\">' + value + ' - ' + periodName + '</span>';
						}
					}
					"/>

<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="false"
		showtoolbar="true" addrow="true" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true"
		initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="203"
		url="jqxGeneralServicer?sname=JQGetListProductPrepareImport&strProductPlanId=${strProductPlanId?if_exists}&productPlanId=${productPlanId?if_exists}"
		/>

<div id='contextMenu' style="display:none;">
	<ul>
		<li id="CreateQuality"><i class="icon-plus"></i>&nbsp;&nbsp;${uiLabelMap.CreateQuality}</li>
	</ul>
</div>

<script>
	$(document).ready(function() {
		if (!"${strProductPlanId?if_exists}" && !"${productPlanId?if_exists}") {
			window.location.href = 'getImportPlans';
		}
	});
	var productIdGlobal = "";
	function bindProductIdToGlobal(productId) {
		productIdGlobal = productId;
	}
	$(document).on('click', function() {
		contextMenu.jqxMenu('close');
	});
	var contextMenu = $("#contextMenu").jqxMenu({ width: 250, height: 30, autoOpenPopup: false, mode: 'popup'});
	$("#CreateQuality").on("click", function() {
		window.location.href = "CreateProductQuality?productId=" + productIdGlobal;
	});
	function excuteDate(value) {
		if (value) {
			value = value.toString().toMilliseconds();
			var now = new Date().getTime();
			var leftTime;
			leftTime = value - now;
			leftTime = Math.ceil(leftTime/86400000);
			return leftTime;
		} else {
			return "";
		}
	}
	var getLocalization = function () {
        var localizationobj = {};
        localizationobj.pagergotopagestring = "${StringUtil.wrapString(uiLabelMap.wgpagergotopagestring)}:";
        localizationobj.pagershowrowsstring = "${StringUtil.wrapString(uiLabelMap.wgpagershowrowsstring)}:";
        localizationobj.pagerrangestring = " ${StringUtil.wrapString(uiLabelMap.wgpagerrangestring)} ";
        localizationobj.pagernextbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagernextbuttonstring)}";
        localizationobj.pagerpreviousbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagerpreviousbuttonstring)}";
        localizationobj.sortascendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortascendingstring)}";
        localizationobj.sortdescendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortdescendingstring)}";
        localizationobj.sortremovestring = "${StringUtil.wrapString(uiLabelMap.wgsortremovestring)}";
        localizationobj.emptydatastring = "${StringUtil.wrapString(uiLabelMap.DANoDataToDisplay)}";
        localizationobj.filterselectstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectstring)}";
        localizationobj.filterselectallstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectallstring)}";
        localizationobj.filterchoosestring = "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}";
        localizationobj.groupsheaderstring = "${StringUtil.wrapString(uiLabelMap.Groupsheaderstring)}";
        return localizationobj;
    }
</script>