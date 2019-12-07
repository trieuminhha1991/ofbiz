<#include "script/periodData.ftl">
<#include "liabilityReceivableFilter.ftl">

<#assign dataField="[{ name: 'partyId', type: 'string' },
					 { name: 'partyCode', type: 'string'},
					 { name: 'partyName', type: 'string'},
					 { name: 'glAccountId', type: 'string'},
					 { name: 'accountCode', type: 'string'},
					 { name: 'openingCreditAmount', type: 'number'},
					 { name: 'openingDebitAmount', type: 'number'},
					 { name: 'postingCreditAmount', type: 'number'},
					 { name: 'postingDebitAmount', type: 'number'},
					 { name: 'endingCreditAmount', type: 'number'},
					 { name: 'endingDebitAmount', type: 'number'},
					 { name: 'currencyUomId', type: 'string'},
					 { name: 'voucherDate', type: 'date', other:'Timestamp'},
					 { name: 'voucherCode', type: 'string'},
					 { name: 'voucherType', type: 'string'},
					 { name: 'voucherDescription', type: 'string'},
					 { name: 'facilityId', type: 'string'},
					 { name: 'facilityName', type: 'string'}
				 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					 },
					 { text: '${uiLabelMap.VoucherNumber}', dataField: 'voucherCode', width: 120, columngroup: 'voucher'},
					 { text: '${uiLabelMap.BACCDay}', dataField: 'voucherDate', width: 120, cellsformat: 'dd/MM/yyyy', columngroup: 'voucher'},
					 { text: '${uiLabelMap.VoucherType}', dataField: 'voucherType', width: 250, columngroup: 'voucher'},	
					 { text: '${uiLabelMap.BACCDescription}', dataField: 'voucherDescription', width: 400,
					 	cellsrenderer: function(row, column, value){
					 		if (row == 0) {
					 			return '<span class=align-right><b>' + value + '</b></span>';
					 		} else {
					 			return '<span class=align-left>' + value + '</span>';
					 		}
						},
					 	aggregatesrenderer: function (aggregates, column, element) {
                        	var renderstring = \"<div class='jqx-widget-content jqx-widget-content-\" + theme + \"' style='float: left; width: 100%; height: 100%; '>\";
                          	renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '<b>' + '${uiLabelMap.BACCTongPhatSinh}' + '</b>' + '</div>';
                          	renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '<b>' + '${uiLabelMap.BACCSoDuCuoiKy}' + '</b>' + '</div>';
                          	renderstring += \"</div>\";
                          	return renderstring;
                      	}
					 },
					 { text: '${uiLabelMap.BACCProductStoreDemension}', dataField: 'facilityName', width: 300},	
					 { text: '${uiLabelMap.BACCDebitAmount}', dataField: 'openingDebitAmount', width: 150, columngroup: 'openingAmount',
						 cellsrenderer: function(row, column, value){
						 	if ((row == 0 && value == 0) || value == 0) {
						 		return '';
						 	} 
							var data = $('#liabilityReceivableGrid').jqxGrid('getrowdata',row);
        		  			if(data != undefined && data){
        		  				if (row == 0) {
						 			return '<span class=align-right><b>' + formatcurrency(value, data.currencyId) + '</b></span>';
						 		} else {
						 			return '<span class=align-right>' + formatcurrency(value, data.currencyId) + '</span>';
						 		}
        		  			}
						 },
						 aggregatesrenderer: function (aggregates, column, element) {
                        	var renderstring = \"<div class='jqx-widget-content jqx-widget-content-\" + theme + \"' style='float: left; width: 100%; height: 100%; '>\";
                          	renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '-' + '</div>';
                          	renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '</div>';
                          	renderstring += \"</div>\";
                          	return renderstring;
                      	}
					 },
					 { text: '${uiLabelMap.BACCCreditAmount}', dataField: 'openingCreditAmount', width: 150, columngroup: 'openingAmount',
						 cellsrenderer: function(row, column, value){
						 	if (row != 0 && value == 0) {
						 		return '';
						 	}
							var data = $('#liabilityReceivableGrid').jqxGrid('getrowdata',row);
        		  			if(data != undefined && data){
                		  		if (row == 0) {
						 			return '<span class=align-right><b>' + formatcurrency(value, data.currencyId) + '</b></span>';
						 		} else {
						 			return '<span class=align-right>' + formatcurrency(value, data.currencyId) + '</span>';
						 		}
        		  			}
						 },
						 aggregatesrenderer: function (aggregates, column, element) {
                        	var renderstring = \"<div class='jqx-widget-content jqx-widget-content-\" + theme + \"' style='float: left; width: 100%; height: 100%; '>\";
                          	renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '-' + '</div>';
                          	renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '</div>';
                          	renderstring += \"</div>\";
                          	return renderstring;
                      	}
					 },
					 { text: '${uiLabelMap.BACCDebitAmount}', dataField: 'postingDebitAmount', width: 150, columngroup: 'postingAmount',
						 cellsrenderer: function(row, column, value){
							if (value == 0) {
						 		return '';
						 	} 
					 		var data = $('#liabilityReceivableGrid').jqxGrid('getrowdata',row);
        		  			if(data != undefined && data){
                		  		return '<span class=align-right>' + formatcurrency(value, data.currencyId) + '</span>';
        		  			}
						 },
						 aggregatesrenderer: function (aggregates, column, element) {
                        	var renderstring = \"<div class='jqx-widget-content jqx-widget-content-\" + theme + \"' style='float: left; width: 100%; height: 100%; '>\";
                        	var total = 0;
                        	if (totalMap && totalMap.totalPostingDebit) {
                        		total = totalMap.totalPostingDebit;
                        	}
                        	var data = $('#liabilityReceivableGrid').jqxGrid('getrows');
                        	var currencyUomId = 'VND';
                        	if (data && data.length > 0) {
                        		currencyUomId = data[0].currencyId;
                        	}
                          	renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\"><b>' + formatcurrency(total, currencyUomId) + '</b></div>';
                          	renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '</div>';
                          	renderstring += \"</div>\";
                          	return renderstring;
                      	}
					 },
					 { text: '${uiLabelMap.BACCCreditAmount}', dataField: 'postingCreditAmount', width: 150, columngroup: 'postingAmount',
						 cellsrenderer: function(row, column, value){
							if (value == 0) {
						 		return '';
						 	}
					 		var data = $('#liabilityReceivableGrid').jqxGrid('getrowdata',row);
        		  			if(data != undefined && data){
                		  		return '<span class=align-right>' + formatcurrency(value, data.currencyId) + '</span>';
        		  			}
						 },
						 aggregatesrenderer: function (aggregates, column, element) {
                        	var renderstring = \"<div class='jqx-widget-content jqx-widget-content-\" + theme + \"' style='float: left; width: 100%; height: 100%; '>\";
                        	var total = 0;
                        	if (totalMap && totalMap.totalPostingCredit) {
                        		total = totalMap.totalPostingCredit;
                        	}
                        	var data = $('#liabilityReceivableGrid').jqxGrid('getrows');
                        	var currencyUomId = 'VND';
                        	if (data && data.length > 0) {
                        		currencyUomId = data[0].currencyId;
                        	}
                          	renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\"><b>' + formatcurrency(total, currencyUomId) + '</b></div>';
                          	renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '</div>';
                          	renderstring += \"</div>\";
                          	return renderstring;
                      	}
					 },
					 { text: '${uiLabelMap.BACCDebitAmount}', dataField: 'endingDebitAmount', width: 150, columngroup: 'endingAmount',
						 cellsrenderer: function(row, column, value){
							if (value == 0) {
						 		return '';
						 	}
					 		var data = $('#liabilityReceivableGrid').jqxGrid('getrowdata',row);
        		  			if(data != undefined && data){
                		  		return '<span class=align-right>' + formatcurrency(value, data.currencyId) + '</span>';
        		  			}
						 },
						 aggregatesrenderer: function (aggregates, column, element) {
                        	var renderstring = \"<div class='jqx-widget-content jqx-widget-content-\" + theme + \"' style='float: left; width: 100%; height: 100%; '>\";
                          	renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '-' + '</div>';
                          	var total = 0;
                          	if (totalMap && totalMap.totalEnding < 0) {
                          		total = totalMap.totalEnding * (-1);
                          	}
                          	var data = $('#liabilityReceivableGrid').jqxGrid('getrows');
                        	var currencyUomId = 'VND';
                        	if (data && data.length > 0) {
                        		currencyUomId = data[0].currencyId;
                        	}
                        	renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\"><b>' + formatcurrency(total, currencyUomId) + '</b></div>';
                          	renderstring += \"</div>\";
                          	return renderstring;
                      	}
					 },
					 { text: '${uiLabelMap.BACCCreditAmount}', dataField: 'endingCreditAmount', width: 150, columngroup: 'endingAmount',
						 cellsrenderer: function(row, column, value){
						 	if (value == 0) {
						 		return '';
						 	}
					 		var data = $('#liabilityReceivableGrid').jqxGrid('getrowdata',row);
        		  			if(data != undefined && data){
                		  		return '<span class=align-right>' + formatcurrency(value, data.currencyId) + '</span>';
        		  			}
						 },
						 aggregatesrenderer: function (aggregates, column, element) {
                        	var renderstring = \"<div class='jqx-widget-content jqx-widget-content-\" + theme + \"' style='float: left; width: 100%; height: 100%; '>\";
                          	renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '-' + '</div>';
                          	var total = 0;
                          	if (totalMap && totalMap.totalEnding >= 0) {
                          		total = totalMap.totalEnding;
                          	}
                          	var data = $('#liabilityReceivableGrid').jqxGrid('getrows');
                        	var currencyUomId = 'VND';
                        	if (data && data.length > 0) {
                        		currencyUomId = data[0].currencyId;
                        	}
                          	renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\"><b>' + formatcurrency(total, currencyUomId) + '</b></div>';
                          	renderstring += \"</div>\";
                          	return renderstring;
                      	}
					 }
				"/>
 <#assign columngrouplist=" { text: '${uiLabelMap.BACCOpeningAmount}', name: 'openingAmount', align: 'center'},
  							{ text: '${uiLabelMap.BACCEndingAmount}', name: 'endingAmount', align: 'center'},
  							{ text: '${uiLabelMap.BACCPostingAmount}', name: 'postingAmount', align: 'center'},
  							{ text: '${uiLabelMap.CommonVoucher}', name: 'voucher', align: 'center'}
						">

 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].nowDateString("dd-MM-yyyy") /> 
 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].nowDateString("dd-MM-yyyy") />
 
<@jqGrid id="liabilityReceivableGrid" filtersimplemode="true" filterable="false" addrefresh="true" editable="false" addType="popup" showtoolbar="true" clearfilteringbutton="false"
		 url="jqxGeneralServicer?sname=JqxGetLiabilityReceivable&glAccountId=${receipGlAccount.glAccountId}&fromDate=${fromDate}&thruDate=${thruDate}" sortable="false"
		 dataField=dataField columnlist=columnlist showstatusbar="false" statusbarheight="30" columngrouplist=columngrouplist showstatusbar="true" statusbarheight="50"
		 customcontrol2="fa fa-file-excel-o open-sans@@javascript: void(0);exportExcel()" customtoolbaraction="updateData" displayTotal="true"
	 />
  							
<script>
	$( document ).ready(function(){
		filter.initFilter();
		filter.bindEvent();
	});
	
	$('#liabilityReceivableGrid').on("bindingcomplete", function (event) {
        var fromDate1 = $("#fromDate").jqxDateTimeInput('getDate');
        var thruDate1 = $("#toDate").jqxDateTimeInput('getDate');

        var fromDate = fromDate1.getDate() + '-' + (fromDate1.getMonth() + 1) + '-' + fromDate1.getFullYear();
        var thruDate = thruDate1.getDate() + '-' + (thruDate1.getMonth() + 1) + '-' + thruDate1.getFullYear();

		jQuery.ajax({
            url: 'getTotalLiabilityReceivable',
            async: false,
            type: 'POST',
            data: {
            	glAccountId: $("#glAccountId").attr('data-value'),
            	fromDate: fromDate,
            	thruDate: thruDate,
            	partyId: $('#partyId').attr('data-value')
            },
            success: function (data) {
                totalMap.totalOpeningCredit = data.totalOpeningCredit;
                totalMap.totalOpeningDedit = data.totalOpeningCredit;
                totalMap.totalPostingCredit = data.totalPostingCredit;
                totalMap.totalPostingDebit = data.totalPostingDebit;
                totalMap.totalEnding = data.totalEnding;
            }
        });
	}); 
	
	var _service = "acctgTransTotal";
	var _updating = false;
	var exportExcel = function(){
		var allData = $('#liabilityReceivableGrid').jqxGrid('getrows');
		if(allData.length > 0){
			var glId = $("#glAccountId").attr('data-value');			
			var partyId = $("#partyId").attr('data-value');			
			glId = glId != null && glId !== undefined && glId ? glId : '${receipGlAccount.glAccountId?if_exists}';

            var fromDate1 = $("#fromDate").jqxDateTimeInput('getDate');
            var thruDate1 = $("#toDate").jqxDateTimeInput('getDate');

            var fromDate = fromDate1.getDate() + '-' + (fromDate1.getMonth() + 1) + '-' + fromDate1.getFullYear();
            var thruDate = thruDate1.getDate() + '-' + (thruDate1.getMonth() + 1) + '-' + thruDate1.getFullYear();

 		    var url = "exportLiabilityReceivableExcel?glAccountId=" + glId + "&fromDate=" + fromDate + "&thruDate=" + thruDate;
 		    if (partyId != null && partyId !== undefined && partyId != "") {
 		    	url += "&partyId=" + partyId;
 		    }
			
			window.location.href = url;
		} else {
    		bootbox.alert("${StringUtil.wrapString(uiLabelMap.ReportCheckNotData)}");
    	}
	}
	
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
                    $("#liabilityReceivableGrid").jqxGrid('updatebounddata');
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