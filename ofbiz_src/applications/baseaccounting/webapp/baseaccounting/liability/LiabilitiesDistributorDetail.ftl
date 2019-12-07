<#--init PurchaseTab-->
<#assign dataFieldS="[{ name: 'invoiceId', type: 'string' },
					 { name: 'invoiceTypeId', type: 'string'},
					 { name: 'invoiceDate', type: 'date', other:'Timestamp'},
					 { name: 'statusId', type: 'string'},
					 { name: 'description', type: 'string'},
					 { name: 'partyIdFrom', type: 'string'},
					 { name: 'partyId', type: 'string'},
					 { name: 'total', type: 'number'},
					 { name: 'amountToApply', type: 'number'},
					 { name: 'fullNameFrom', type: 'string'},
					 { name: 'fullNameTo', type: 'string'}
					 ]
					 "/>
<#assign columnlistS="{ text: '${uiLabelMap.FormFieldTitle_invoiceId}', width: 120, filtertype:'input', pinned: true, datafield: 'invoiceId', cellsrenderer:
                     	 function(row, colum, value)
                        {
                        	var data = $('#${typeLiabilityS}').jqxGrid('getrowdata', row);
                        	return \"<span>\" + data.invoiceId + \"</span>\";
                        }},
					 { text: '${uiLabelMap.FormFieldTitle_invoiceTypeId}',hidden  :true, filtertype: 'checkedlist', width:150, datafield: 'invoiceTypeId',
                        	cellsrenderer:
                             	function(row, colum, value)
                                {
                                	for(i=0; i < dataInvoiceTypeS.length;i++){
                                		if(value==dataInvoiceTypeS[i].statusId){
                                			return \"<span>\" + dataInvoiceTypeS[i].description + \"</span>\";
                                		}
                                	}
                                	return value;
                                },
			   			createfilterwidget: function (column, columnElement, widget) {
			   				widget.jqxDropDownList({ source: dataInvoiceTypeS, displayMember: 'invoiceTypeId', valueMember : 'description'});
			   			},
					 },
					 { text: '${uiLabelMap.FormFieldTitle_invoiceDate}', filtertype: 'range', width:100, datafield: 'invoiceDate', cellsformat: 'dd/MM/yyyy'},
					 { text: '${uiLabelMap.CommonStatus}', filtertype: 'checkedlist', width:150, datafield: 'statusId', cellsrenderer:
                     	function(row, colum, value)
                        {
                        	for(i=0; i < dataStatusType.length;i++){
                        		if(value==dataStatusType[i].statusId){
                        			return \"<span>\" + dataStatusType[i].description + \"</span>\";
                        		}
                        	}
                        	return value;
                        },
			   			createfilterwidget: function (column, columnElement, widget) {
			   				widget.jqxDropDownList({ source: dataStatusType, dropDownWidth: 170, displayMember: 'description', valueMember : 'statusId'});
			   			}},
					 { text: '${uiLabelMap.description}', width:140, filtertype:'input', datafield: 'description'},
					 { text: '${uiLabelMap.BACCInvoiceFromParty}', datafield: 'partyIdFrom', cellsrenderer:
						 	function(row, colum, value){
						 		var data = $('#${typeLiabilityS}').jqxGrid('getrowdata', row);
						 		return \"<span>\" + data.fullNameFrom + '[' + data.partyIdFrom + ']' + \"</span>\";
						 	}
				 	 },
					 { text: '${uiLabelMap.accDistributors}', hidden: true,  datafield: 'partyId', cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#${typeLiabilityS}').jqxGrid('getrowdata', row);
					 		return \"<span>\" + data.fullNameTo + '[' + data.partyId + ']' + \"</span>\";
					 	}
				 	 },
					 { text: '${uiLabelMap.FormFieldTitle_total}(1)', width:160, filterable: false, sortable: false, datafield: 'total',
					 	cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#${typeLiabilityS}').jqxGrid('getrowdata', row);
					 		return \"<div class='align-right'>\" + formatcurrency(data.total,data.currencyUomId) + \"</div>\";
					 	}},
					 { text: '${uiLabelMap.FormFieldTitle_amountToApply}(2)', width:220, sortable: false, filterable: false, datafield: 'amountToApply',
						 cellsrenderer:
						 	function(row, colum, value){
						 		var data = $('#${typeLiabilityS}').jqxGrid('getrowdata', row);
								return \"<div class='align-right'>\" + formatcurrency(data.amountToApply,data.currencyUomId) + \"</div>\";
						 	}
	                 }"
/>

<#--init SalesTab-->
<#assign dataFieldP="[{ name: 'invoiceId', type: 'string' },
		{ name: 'invoiceTypeId', type: 'string'},
		{ name: 'invoiceDate', type: 'date', other:'Timestamp'},
		{ name: 'statusId', type: 'string'},
		{ name: 'description', type: 'string'},
		{ name: 'partyIdFrom', type: 'string'},
		{ name: 'partyId', type: 'string'},
		{ name: 'total', type: 'number'},
		{ name: 'amountToApply', type: 'number'},
		{ name: 'fullNameFrom', type: 'string'},
		{ name: 'fullNameTo', type: 'string'}
]
"/>
<#assign columnlistP="{ text: '${uiLabelMap.FormFieldTitle_invoiceId}', width:120, pinned: true, datafield: 'invoiceId', cellsrenderer:
		 function(row, colum, value)
	   {
	   	var data = $('#${typeLiabilityP}').jqxGrid('getrowdata', row);
	   	return \"<span>\" + data.invoiceId + \"</a></span>\";
	   }},
	{ text: '${uiLabelMap.FormFieldTitle_invoiceTypeId}',hidden  :true, filtertype: 'checkedlist', width:150, datafield: 'invoiceTypeId',
		cellsrenderer:
	        	function(row, colum, value)
	           {
	           	for(i=0; i < dataInvoiceTypeP.length;i++){
	           		if(value==dataInvoiceTypeP[i].statusId){
	           			return \"<span>\" + dataInvoiceTypeP[i].description + \"</span>\";
	           		}
	           	}
	           	return value;
	           },
			createfilterwidget: function (column, columnElement, widget) {
				widget.jqxDropDownList({ source: dataInvoiceTypeP, displayMember: 'invoiceTypeId', valueMember : 'description'});
			},
	},
	{ text: '${uiLabelMap.FormFieldTitle_invoiceDate}', filtertype: 'range', width:100, datafield: 'invoiceDate', cellsformat: 'dd/MM/yyyy'},
	{ text: '${uiLabelMap.CommonStatus}', filtertype: 'checkedlist', width:150, datafield: 'statusId', cellsrenderer:
		function(row, colum, value)
	   {
	   	for(i=0; i < dataStatusType.length;i++){
	   		if(value==dataStatusType[i].statusId){
	   			return \"<span>\" + dataStatusType[i].description + \"</span>\";
	   		}
	   	}
	   	return value;
	   },
			createfilterwidget: function (column, columnElement, widget) {
				widget.jqxDropDownList({ source: dataStatusType, dropDownWidth: 170, displayMember: 'description', valueMember : 'statusId'});
			}},
	{ text: '${uiLabelMap.description}', width:140, datafield: 'description'},
	{ text: '${uiLabelMap.accDistributors}', hidden: true, datafield: 'partyIdFrom', cellsrenderer:
		function(row, colum, value){
			var data = $('#${typeLiabilityP}').jqxGrid('getrowdata', row);
			return \"<span>\" + data.fullNameFrom + '[' + data.partyIdFrom + ']' + \"</span>\";
		}
	 },
	{ text: '${uiLabelMap.BACCInvoiceToParty}', datafield: 'partyId', cellsrenderer:
		function(row, colum, value){
			var data = $('#${typeLiabilityP}').jqxGrid('getrowdata', row);
			return \"<span>\" + data.fullNameTo + '[' + data.partyId + ']' + \"</span>\";
		}
	 },
	{ text: '${uiLabelMap.FormFieldTitle_total}(1)', width:160, cellsalign: 'right', filterable: false, sortable: false, datafield: 'total',
        cellsrenderer:
        function(row, colum, value){
            var data = $('#${typeLiabilityP}').jqxGrid('getrowdata', row);
            return \"<div class='align-right'>\" + formatcurrency(data.total,data.currencyUomId) + \"</div>\";
        }
    },
	{ text: '${uiLabelMap.FormFieldTitle_amountToApply}(2)', width:220, cellsalign: 'right', sortable: false, filterable: false, datafield: 'amountToApply',
        cellsrenderer: function(row, colum, value){
                var data = $('#${typeLiabilityP}').jqxGrid('getrowdata', row);
                return \"<div class='align-right'>\" + formatcurrency(data.amountToApply,data.currencyUomId) + \"</div>\";
        }
    }"
/>



<div class="tab-pane active" id="Payable">
    <div class="form-horizontal label-text-left font-arial content-description">
        <div class="row-fluid">
            <div class="span4"></div>
            <div class="span8 boder-all-profile">
                <div class="row-fluid">
                    <div class="span6">
                        <div class="row-fluid">
                            <div class="span7">
                                <div class="row-fluid">
                                    <div class="green-label bold-label">
                                    ${uiLabelMap.accTotalInvoicesValue}(1):
                                    </div>
                                </div>
                            </div>
                            <div class="span5 bold-label" style="color: red;">
                            <span id="spanTotalInvoiceS">
                            </span>
                            </div>
                        </div>
                    </div>
                    <div class="span6">
                        <div class="row-fluid">
                            <div class="span7">
                                <div class="row-fluid">
                                    <div class="bold-label green-label">
                                    ${uiLabelMap.accPayableToApplyTotal}(2):
                                    </div>
                                </div>
                            </div>
                            <div class="span5 bold-label" style="color: red;">
                            <span id="spanTotalAmountToApplyS">
                            </span>
                            </div>
                        </div>
                    </div><!--.span6-->
                </div>

                <div class="row-fluid">
                    <div class="span6">
                        <div class="row-fluid">
                            <div class="span7">
                                <div class="row-fluid">
                                    <div class="green-label bold-label">
                                    ${uiLabelMap.BACCTotalInvoiceRows}:
                                    </div>
                                </div>
                            </div>
                            <div class="span5 bold-label" style="color: red;">
                            <span id="spanTotalRowsS">
                            </span>
                            </div>
                        </div>
                    </div>
                    <div class="span6">
                    </div><!--.span6-->
                </div>
            </div>
        </div>
    </div><!-- .form-horizontal -->

    <@jqGrid id="${typeLiabilityS}" isShowTitleProperty="false" clearfilteringbutton="true" url="jqxGeneralServicer?sname=jqGetLiabilityDetailDistributor&type=${typeLiabilityS}&distributorId=${parameters.disId?if_exists}" dataField=dataFieldS columnlist=columnlistS
    sortdirection="desc" defaultSortColumn="invoiceDate" jqGridMinimumLibEnable="true" filterable="true" filtersimplemode="true" addType="popup" showstatusbar="false" showtoolbar="true"
    />
</div>

<div class="tab-pane" id="Receivable">
    <div class="form-horizontal label-text-left font-arial content-description">
        <div class="row-fluid">
            <div class="span4"></div>
            <div class="span8 boder-all-profile">
                <div class="row-fluid">
                    <div class="span6">
                        <div class="row-fluid">
                            <div class="span7">
                                <div class="row-fluid">
                                    <div class="green-label bold-label">
                                    ${uiLabelMap.accTotalInvoicesValue}(1):
                                    </div>
                                </div>
                            </div>
                            <div class="span5 bold-label" style="color: red;">
                            <span id="spanTotalInvoiceP">

                            </span>
                            </div>
                        </div>
                    </div>
                    <div class="span6">
                        <div class="row-fluid">
                            <div class="span7">
                                <div class="row-fluid">
                                    <div class="bold-label green-label">
                                    ${uiLabelMap.accReceivableToApplyTotal}(2):
                                    </div>
                                </div>
                            </div>
                            <div class="span5 bold-label" style="color: red;">
                            <span id="spanTotalAmountToApplyP">

                            </span>
                            </div>
                        </div>
                    </div><!--.span6-->
                </div>
                <div class="row-fluid">
                    <div class="span6">
                        <div class="row-fluid">
                            <div class="span7">
                                <div class="row-fluid">
                                    <div class="green-label bold-label">
                                    ${uiLabelMap.BACCTotalInvoiceRows}:
                                    </div>
                                </div>
                            </div>
                            <div class="span5 bold-label" style="color: red;">
                            <span id="spanTotalRowsP">
                            </span>
                            </div>
                        </div>
                    </div>
                    <div class="span6">
                    </div><!--.span6-->
                </div>
            </div>
        </div>
    </div><!-- .form-horizontal -->

    <@jqGrid id="${typeLiabilityP}"isShowTitleProperty="false" clearfilteringbutton="true" url="jqxGeneralServicer?sname=jqGetLiabilityDetailDistributor&type=${typeLiabilityP}&distributorId=${parameters.disId?if_exists}" dataField=dataFieldP columnlist=columnlistP
    sortdirection="desc" defaultSortColumn="invoiceDate" jqGridMinimumLibEnable="true" filterable="true" filtersimplemode="true" addType="popup" showstatusbar="false" showtoolbar="true"
    />
</div>

<script type="text/javascript">
    $(function () {
        LiabilitiesDistributorObj.init();
    });

    var LiabilitiesDistributorObj = (function () {
        var flagLoadedS = false;
        var flagLoadedP = false;
        var totalMap = {};
        var init = function () {
            initEvent();
        };
        var initEvent = function () {
            /*event on binddingcomplete PurchaseTab*/
            $('#${typeLiabilityS}').on('bindingcomplete', function (event) {
                if(!flagLoadedS) {
                    jQuery.ajax({
                        url: 'getTotalLiabilityDistributor',
                        async: false,
                        type: 'POST',
                        data: {
                            type: '${typeLiabilityS}',
                            distributorId: '${parameters.disId?if_exists}'
                        },
                        success: function (data) {
                            totalMap.totalInvoiceS = data.totalInvoice;
                            totalMap.totalAmountToApplyS = data.totalAmountToApply;
                            totalMap.TotalRowsS = data.TotalRows;
                            totalMap.totalInvoiceS = formatcurrency(totalMap.totalInvoiceS,'${defaultOrganizationPartyCurrencyUomId}');
                            totalMap.totalAmountToApplyS = formatcurrency(totalMap.totalAmountToApplyS,'${defaultOrganizationPartyCurrencyUomId}');
                            $("#spanTotalInvoiceS").html(totalMap.totalInvoiceS);
                            $("#spanTotalAmountToApplyS").html(totalMap.totalAmountToApplyS);
                            $("#spanTotalRowsS").html(totalMap.TotalRowsS);

                            flagLoadedS = true;
                        }
                    });
                }
            });
            /*event on binddingcomplete SaleTab*/
            $('#${typeLiabilityP}').on('bindingcomplete', function (event) {
                if(!flagLoadedP) {
                    jQuery.ajax({
                        url: 'getTotalLiabilityDistributor',
                        async: false,
                        type: 'POST',
                        data: {
                            type: '${typeLiabilityP}',
                            distributorId: '${parameters.disId?if_exists}'
                        },
                        success: function (data) {
                            totalMap.totalInvoiceP = data.totalInvoice;
                            totalMap.totalAmountToApplyP = data.totalAmountToApply;
                            totalMap.TotalRowsP = data.TotalRows;
                            totalMap.totalInvoiceP = formatcurrency(totalMap.totalInvoiceP,'${defaultOrganizationPartyCurrencyUomId}');
                            totalMap.totalAmountToApplyP = formatcurrency(totalMap.totalAmountToApplyP,'${defaultOrganizationPartyCurrencyUomId}');
                            $("#spanTotalInvoiceP").html(totalMap.totalInvoiceP);
                            $("#spanTotalAmountToApplyP").html(totalMap.totalAmountToApplyP);
                            $("#spanTotalRowsP").html(totalMap.TotalRowsP);
                            flagLoadedP = true;
                        }
                    });
                }
            });

        };
        return {
            init: init
        }
    }());
</script>