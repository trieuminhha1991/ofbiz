<style>
	
	.modal-backdrop{
		z-index: 19001;
	}
	
	#bodyCont {
	    overflow: hidden;
	    position: relative;
	}
	.bootbox{
		z-index: 19002;
		margin-top: 100px;
	}
	
	#showDisplayAgreement {
        overflow: auto;
        position: relative;
	}
</style>
<#assign localeStr = "VI" />
<#if locale = "en">
<#assign localeStr = "EN" />
</#if>
<#assign orderMode = 'PURCHASE_ORDER' !>
<#assign productPlanIdHeaderWeek = parameters.productPlanId !>
<#assign listProductPlanItem = parameters.listProduct !>
<#assign size = listProductPlanItem?size />
<#assign columnText = "{ text: '${uiLabelMap.lotId}', hidden: false, datafield: 'lotId', editable: false, filterable: false,
			cellsrenderer: function (index, datafield, value, defaultvalue, column, rowdata) {
				var agreementId = 0;
				var statusId = '';
				if(rowdata.agreementId){
					agreementId = rowdata.agreementId;
					statusId = rowdata.statusId;
				}
	            return '<div class=\"lotIdTooltip\" style=\"margin-top: 5px;\"><a class=\"green fa-pencil-square-o\" href=\"javascript:showPopup2('+value+', '+agreementId+', &quot;'+statusId+'&quot;)\">'+value+'</a></div>';
	        }
		}," />
<#if listProductPlanItem?size &gt; 4>
	<#assign columnText = "{ text: '${uiLabelMap.lotId}', hidden: false, datafield: 'lotId', editable: false, filterable: false, width: 120,
							cellsrenderer: function (index, datafield, value, defaultvalue, column, rowdata) {
								var agreementId = 0;
								var statusId = '';
								if(rowdata.agreementId){
									agreementId = rowdata.agreementId;
									statusId = rowdata.statusId;
								}
					            return '<div class=\"lotIdTooltip\" style=\"margin-top: 5px;\"><a class=\"green fa-pencil-square-o\" href=\"javascript:showPopup2('+value+', '+agreementId+', &quot;'+statusId+'&quot;)\">'+value+'</a></div>';
				            }
	}," />
</#if>

<div id="pos-show-hold-cart">
	<div>
		<span>${uiLabelMap.DetailCont}</span>
	</div>
	<div style="overflow: hidden;">
		
			<div class="" style="height: 405px; margin-left: 10px;" id="bodyCont">
				<#assign dataField="[{ name: 'lotId', type: 'string' }," +
				"{name: 'productPlanId', type: 'string'}," +
				"{name: 'agreementId', type: 'string'}," +
				"{name: 'agreementName', type: 'string'}," +
				"{name: 'statusId', type: 'string'}," +
				"{name: 'statusDescription', type: 'string'}," +
				"{name: 'orderId', type: 'string'}"
				/>
				
				<#assign columnlist="
				{
					    text: '${uiLabelMap.No}', sortable: false, filterable: false, editable: false,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 35,
					    cellsrenderer: function (row, column, value) {
					        return \"<div style='margin:4px;'>\" + (value + 1) + \"</div>\";
					    }"+
				"}," + columnText +"
				{ text: '${uiLabelMap.productPlanId}', hidden: true, datafield: 'productPlanId', editable: false, filterable: false, width: 80},
				{ text: '${uiLabelMap.contractId}', hidden: false, datafield: 'agreementId', editable: false, filterable: false, width: 120},
				{ text: '${uiLabelMap.contractName}', hidden: false, datafield: 'agreementName', editable: false, filterable: false, width: 200},
				{ text: '${uiLabelMap.agreementStatus}', hidden: false, datafield: 'statusDescription', editable: false, filterable: false, width: 150},
				{ text: '${uiLabelMap.orderId}', hidden: true, datafield: 'orderId', editable: false, filterable: false, width: 80}
				"/>
				<#if size != 0>
					<#list listProductPlanItem as list>
						<#assign dataField= dataField + "," />
						<#assign columnlist= columnlist + "," />
						<#assign dataField= dataField + "{name: 'quantity_${list.productId}', type: 'number'}" />
						<#assign columnlist = columnlist + "{ text: '${StringUtil.wrapString(list.internalName)}(${StringUtil.wrapString(list.uomName)})', datafield: 'quantity_${list.productId}', editable: false, filterable: false, columntype: 'numberinput', width: 200, cellsalign: 'right',
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
			     		   		return '<div style=\"text-align: right; margin-right:10px;margin-top: 5px;\">' +value.toLocaleString('${localeStr}')+ '</div>';
				            }
						}" />
						
						<#if size - 1 != list_index>
							<#assign dataField= dataField + "," />
							<#assign columnlist = columnlist + "," />
						</#if>
					</#list>
				</#if>
				<#assign dataField = dataField + "]" />
				<@jqGrid filtersimplemode="true" id="jqxgridContainerWeek" filterable="false" addType="" dataField=dataField editmode="selectedcell" editable="false" columnlist=columnlist clearfilteringbutton="false" showtoolbar="false" addrow="false"
				bindresize="false" pageable="true" showlist="true" sortable="false" viewSize="15" columnsresize="false"
				url="jqxGeneralServicer?sname=devideContainerJqx&productPlanId=${productPlanIdHeaderWeek?if_exists}" height="400" width="1060" statusbarheight="30"
				autoheight="false"
				/>
			</div>
			<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity: 0.4;">
			<div class="row-fluid" style="margin-top: 4px;">
				<div class="">
					<button id='exitView' class="btn btn-mini btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.Exit}</button>
					<button id='resetCont' class="btn btn-mini btn-primary form-action-button pull-right"><i class='icon-refresh'></i>${uiLabelMap.Refresh}</button>
				</div>
			</div>
		
	</div>
</div>



<div id="showPopupEditAgreement">
	
</div>

<div id="jqxWindowShowAgreement">
	<div>
			${uiLabelMap.EditAgreementToSendSupp}
	</div>
	<div class="ps-container ps-active-x ps-active-y" id="showDisplayAgreement">
		
	</div>
</div>


<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript">

$('#jqxWindowShowAgreement').jqxWindow({
    showCollapseButton: false, theme:'olbius', resizable: false,
    isModal: true, autoOpen: false, height: 600, width: 1200, maxWidth: '90%', position: 'center', modalOpacity: 0.7
});
$('#jqxWindowShowAgreement').on('close', function (event) {
	$('#pos-show-hold-cart').css('display','block');
});

$('#pos-show-hold-cart').jqxWindow({
    showCollapseButton: false, theme:'olbius', resizable: false, isModal: true, autoOpen: false, height: 500, width: 1100, maxWidth: 1100
});
var wtmp = window;
var tmpwidth = $('#pos-show-hold-cart').jqxWindow('width');
$('#pos-show-hold-cart').jqxWindow({
    position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 }
});

$('#pos-show-hold-cart').on('close', function (event) {
	$('#pos-show-hold-cart').jqxWindow('destroy');
});

$('#pos-show-hold-cart').on('open', function (event) {
//	console.log(111);
});

function showPopup2(lotId, agreementId, statusId){
	var url='showDisplayAgreementOnJqxWindow?agreementId='+agreementId+'';
	var fn = function(data){
		$('#pos-show-hold-cart').css('display','none');
		$("#showDisplayAgreement").html(data);
		$('#jqxWindowShowAgreement').jqxWindow('open');
	}
	if(statusId == "AGREEMENT_CREATED" || statusId == "" || statusId == null){
		url = 'showPopupEditPurchaseAgreement?orderMode=${orderMode}&lotId='+lotId+'&productPlanId=${productPlanIdHeaderWeek}&agreementId='+agreementId+'';
		fn = function(data){
			$('#pos-show-hold-cart').css('display','none');
			//$("#step1").html(data);
			$("#showPopupEditAgreement").html(data);
			$('#show-Aggree').jqxWindow('open');
		}
	}
	onLoadData();
	$.ajax({
		url: url,
    	type: "POST",
    	data: {},
    	async: false,
    	success: fn,
    	error: function(data){
    	}
		}).done(function() {
			onLoadDone();
		});
	
}

$('#show-Aggree').on('close', function() {
//	$('#pos-show-hold-cart').jqxWindow('open');
});

$(document).ready(function(){
	$('#showDisplayAgreement').perfectScrollbar({
	      wheelSpeed: 1,
	      wheelPropagation: false
	    });
//	$('.lotIdTooltip').jqxTooltip({ content: '${uiLabelMap.EditUpdateAgreement}', position: 'bottom', opacity: 1 });
//	$('.modal .modal-body').css('max-height', $(window).height() * 0.75);
//	$('#bodyCont').perfectScrollbar({
//	    wheelSpeed: 1,
//	    wheelPropagation: false
//	});
//	
});
</script>

<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
<script type="text/javascript">
$('#resetCont').on('click', function(){
	$('#pos-show-hold-cart').css('display','none');
	bootbox.confirm("${uiLabelMap.resetContConfirm}?",function(result){
		if(result){
			var productPlanId2 = "${productPlanIdHeaderWeek}";
			$.ajax({
				url: 'resetDevideContainer',
		    	type: "POST",
		    	data: {productPlanId: productPlanId2},
		    	async: false,
		    	success: function(data) {
		    		//$("#bodyCont").empty();
		    		window.setTimeout('location.reload()', 500);
		    	},
		    	error: function(data){
		//    		$('#test').html(data);
		    	}
				}).done(function() {
			    	onLoadDone();
				});
		}else{
			$('#pos-show-hold-cart').css('display','block');
		}
	});
});
$('#exitView').on('click', function(){
	$('#pos-show-hold-cart').jqxWindow('close');
});
//$('#resetCont').on('hover', function(){
//	var data = '${uiLabelMap.ResetContainer}';
	$('#resetCont').jqxTooltip({ content: '${uiLabelMap.ResetContainer}', position: 'top', opacity: 1 });
//});
function tooltipEditAgree(count){
	var data = '${uiLabelMap.EditUpdateAgreement}';
	$('#tdAgreeDetail_'+count).jqxTooltip({ content: data, position: 'top', opacity: 1 });
}
$('.lotIdTooltip').hover(function(){
//	$('.lotIdTooltip').jqxTooltip({ content: '${uiLabelMap.EditUpdateAgreement}', position: 'bottom', opacity: 1 });
});
</script>
