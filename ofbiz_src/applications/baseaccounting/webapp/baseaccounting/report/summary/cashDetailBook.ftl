<#include "script/periodData.ftl">
<#include "cashDetailBookFilter.ftl">

<#assign dataField="[{ name: 'transDate', type: 'date' },
					 { name: 'voucherDate', type: 'date', other:'Timestamp'},
					 { name: 'receiptVoucherID', type: 'string'},
					 { name: 'payVoucherID', type: 'string'},
					 { name: 'voucherDate', type: 'date', other:'Timestamp'},
					 { name: 'voucherDescription', type: 'string'},
					 { name: 'recipGlAccountCode', type: 'string'},
					 { name: 'creditAmount', type: 'number'},
					 { name: 'debitAmount', type: 'number'},
					 { name: 'balAmount', type: 'number'},
					 { name: 'currencyUomId', type: 'string'},
					 { name: 'voucherType', type: 'string'},				
					 { name: 'note', type: 'string'}
				 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.BACCTransDate}', dataField: 'transDate', width: 150, cellsformat: 'dd/MM/yyyy'},
					 { text: '${uiLabelMap.BACCCashVoucherDate}', dataField: 'voucherDate', width: 150, cellsformat: 'dd/MM/yyyy'},
					 { text: '${uiLabelMap.BACCReceipt}', dataField: 'receiptVoucherID', width: 150, columngroup: 'cashVoucher'},
					 { text: '${uiLabelMap.BACCPayment}', dataField: 'payVoucherID', width: 150, columngroup: 'cashVoucher'},
					 { text: '${uiLabelMap.BACCDescription}', dataField: 'voucherDescription', width: 250,
					 	cellsrenderer: function(row, column, value){
					 		if (row == 0) {
					 			return '<span class=align-right><b>' + value + '</b></span>';
					 		} else {
					 			return '<span class=align-left>' + value + '</span>';
					 		}
						},
					 	aggregatesrenderer: function (aggregates, column, element) {
                        	var renderstring = \"<div class='jqx-widget-content jqx-widget-content-\" + theme + \"' style='float: left; width: 100%; height: 100%; '>\";
                          	renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '<b>' + '${uiLabelMap.BACCSoDuCuoiKy}' + '</b>' + '</div>';
                          	renderstring += \"</div>\";
                          	return renderstring;
                      	}
					 },
					 { text: '${uiLabelMap.BACCRecipGlAccountId}', dataField: 'recipGlAccountCode', width: 150},
					 { text: '${uiLabelMap.BACCDebitAmount}', dataField: 'debitAmount', width: 150, columngroup: 'arisingAmount',
						 cellsrenderer: function(row, column, value){
							 var data = $('#cashDetailGrid').jqxGrid('getrowdata',row);
        		  			 if(data != undefined && data){
        		  				return '<span class=align-right>' + formatcurrency(value, data.currencyId) + '</span>';
        		  			 }
						 }
					 },
					 { text: '${uiLabelMap.BACCCreditAmount}', dataField: 'creditAmount', width: 150, columngroup: 'arisingAmount',
						 cellsrenderer: function(row, column, value){
							 var data = $('#cashDetailGrid').jqxGrid('getrowdata',row);
        		  			 if(data != undefined && data){
                		  		return '<span class=align-right>' + formatcurrency(value, data.currencyId) + '</span>';
        		  			 }
						 }
					 },
					 { text: '${uiLabelMap.BACCCashBalance}', dataField: 'balAmount', width: 150,
						 cellsrenderer: function(row, column, value){
							 var data = $('#cashDetailGrid').jqxGrid('getrowdata',row);
        		  			 if(data != undefined && data){
        		  				return '<span class=align-right>' + formatcurrency(value, data.balAmount) + '</span>';
        		  			 }
						 },
						 aggregatesrenderer: function (aggregates, column, element) {
                        	var renderstring = \"<div class='jqx-widget-content jqx-widget-content-\" + theme + \"' style='float: left; width: 100%; height: 100%; '>\";
                        	var total = 0;
                        	if (totalMap && totalMap.endingAmount) {
                        		total = totalMap.endingAmount;
                        	}
                        	var data = $('#cashDetailGrid').jqxGrid('getrows');
                        	var currencyUomId = 'VND';
                        	if (data && data.length > 0) {
                        		currencyUomId = data[0].currencyId;
                        	}
                          	renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\"><b>' + formatcurrency(total, currencyUomId) + '</b></div>';
                          	renderstring += \"</div>\";
                          	return renderstring;
                      	}
					 },
					 { text: '${uiLabelMap.BACCAcctgTransTypeId}', dataField: 'voucherType', width: 150},						 
					 { text: '${uiLabelMap.BACCNote}', dataField: 'note', width: 150}
				"/>
 <#assign columngrouplist=" { text: '${uiLabelMap.BACCCashVoucher}', name: 'cashVoucher', align: 'center'},
  							{ text: '${uiLabelMap.BACCArisingAmount}', name: 'arisingAmount', align: 'center'}
						">

 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].nowDateString("dd-MM-yyyy") /> 
 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].nowDateString("dd-MM-yyyy") />
 
 <#assign url = "jqxGeneralServicer?sname=JqxGetCashDetailBook&glAccountId=${cashGlAccount.glAccountId}&fromDate=" + '${fromDate}' + "&thruDate=" + '${thruDate}' />
 <@jqGrid id="cashDetailGrid" filtersimplemode="true" filterable="false" addrefresh="true" editable="false" addType="popup" showtoolbar="true" clearfilteringbutton="true"
		 url=url dataField=dataField columnlist=columnlist showstatusbar="false" customtoolbaraction="updateData" displayTotal="true" showstatusbar="true" statusbarheight="25"
		 statusbarheight="30" columngrouplist=columngrouplist customcontrol1="fa fa-file-excel-o open-sans@@javascript: void(0);exportExcel()"
	 />
 
 <script>
 	$( document ).ready(function(){
 		cashFilter.initFilter();
 		cashFilter.bindEvent();
 	});
 	
 	$('#cashDetailGrid').on("bindingcomplete", function (event) {
        var fromDate1 = $("#fromDate").jqxDateTimeInput('getDate');
        var thruDate1 = $("#toDate").jqxDateTimeInput('getDate');

        var fromDate = fromDate1.getDate() + '-' + (fromDate1.getMonth() + 1) + '-' + fromDate1.getFullYear();
        var thruDate = thruDate1.getDate() + '-' + (thruDate1.getMonth() + 1) + '-' + thruDate1.getFullYear();
        var glId = $("#glAccountId").attr('data-value');
		glId = glId != null && glId !== undefined && glId ? glId : '${glAccount.glAccountId?if_exists}';
		
		jQuery.ajax({
            url: 'getEndingAmountCashDetail',
            async: false,
            type: 'POST',
            data: {
            	glAccountId: glId,
            	fromDate: fromDate,
            	thruDate: thruDate
            },
            success: function (data) {
                totalMap.endingAmount = data.endingAmount;
            }
        });
	}); 
 	
 	var exportExcel = function(){
		if($('#cashDetailGrid').jqxGrid('getrows').length > 0){
			var glId = $("#glAccountId").attr('data-value');			
			glId = glId != null && glId !== undefined && glId ? glId : '${glAccount.glAccountId?if_exists}';

            var fromDate1 = $("#fromDate").jqxDateTimeInput('getDate');
            var thruDate1 = $("#toDate").jqxDateTimeInput('getDate');

            var fromDate = fromDate1.getDate() + '-' + (fromDate1.getMonth() + 1) + '-' + fromDate1.getFullYear();
            var thruDate = thruDate1.getDate() + '-' + (thruDate1.getMonth() + 1) + '-' + thruDate1.getFullYear();

            window.location.href = "exportCashDetailBookExcel?glAccountId=" + glId + "&fromDate=" + fromDate + "&thruDate=" + thruDate;
		} else {
    		bootbox.alert("${StringUtil.wrapString(uiLabelMap.ReportCheckNotData)}");
    	}
	}
	
	var _service = "acctgTransTotal";
	var _updating = false;
	
	var updateData = function(container){
		container.append('<button id="update-liability" title="${uiLabelMap.AggregateLabel}" class="btn btn-mini btngridsetting"><i class="fa-play"></i></button>');
		container.append('<span id="status-liability" style="float: right; margin-top: 9px; margin-right: 4px; color: #4383b4"><image src="/images/ajax-loader.gif"></span>');
		container.append('<h5 id="lastupdate-liability" style="float: right; margin-top: 9px; margin-right: 4px; color: #4383b4"></h5>');
		
		$("#update-liability").on('click', function () {
            bootbox.dialog("${uiLabelMap.olap_warn_grid}",
                [{
                    "label": "${uiLabelMap.olap_ok_grid}",
                    "class": "btn-primary btn-small icon-ok",
                    "callback": function () {
                        jQuery.ajax({
                            url: 'runOlapService',
                            async: true,
                            type: 'POST',
                            data: {'service': _service},
                            success: function () {
                                getUpdating();
                                setTimeout(checkStatus, 10000);
                            }
                        });
                    }
                },
                    {
                        "label": "${uiLabelMap.olap_cancel_grid}",
                        "class": "btn-danger btn-small icon-remove",
                        "callback": function () {
                            checkStatus();
                        }
                    }]
            );
        });
        checkStatus();
	}
	
	function checkStatus() {
        jQuery.ajax({
            url: 'getStatusOlapServices',
            async: true,
            type: 'POST',
            data: {'service': _service},
            success: function (data) {
                if (data.status && data.status === 'COMPLETED') {
                    getLastUpdated();
                } else {
                    getUpdating();
                    setTimeout(checkStatus, 10000);
                }
            }
        });
    }
    
    function getLastUpdated() {
        jQuery.ajax({
            url: 'getLastupdatedOlapServices',
            async: true,
            type: 'POST',
            data: {'service': _service},
            success: function (data) {
                if (data.lastupdated > 0) {
                    var tmp = $.jqx.dataFormat.formatdate(new Date(data.lastupdated), 'yyyy-MM-dd HH:mm:ss');
                    _serviceTimestamp = data.lastupdated;
                    $("#lastupdate-liability").html("${uiLabelMap.olap_lastupdated_grid}" + ': ' + tmp);
                    $("#status-liability").hide();
                    if ($("#update-liability")) {
                        $("#update-liability").show();
                    }
                    $("#lastupdate-liability").show();
                } else {
                    $("#status-liability").hide();
                    $("#lastupdate-liability").hide();
                    if ($("#update-liability")) {
                        $("#update-liability").show();
                    }
                }
            },
            complete: function(){
            	if (_updating) {
                    _updating = false;
                    $("#liabilityBalGrid").jqxGrid('updatebounddata');
                    $('body').trigger('runolapservicedone');
                }
            }
        });
    }
    
    function getUpdating() {
        $("#status-liability").show();
        $("#lastupdate-liability").hide();
        if ($("#update-liability")) {
            $("#update-liability").hide();
        }
        _updating = true;
    }
 </script>