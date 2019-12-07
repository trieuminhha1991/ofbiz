<#include "script/periodData.ftl">
<#include "liabilitySupplierFilter.ftl">

<#assign dataField="[{ name: 'partyId', type: 'string' },
					 { name: 'partyCode', type: 'string'},
					 { name: 'partyName', type: 'string'},
					 { name: 'openingCreditAmount', type: 'number'},
					 { name: 'openingDebitAmount', type: 'number'},
					 { name: 'postingCreditAmount', type: 'number'},
					 { name: 'postingDebitAmount', type: 'number'},
					 { name: 'endingCreditAmount', type: 'number'},
					 { name: 'endingDebitAmount', type: 'number'}
				 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					 },
					 { text: '${uiLabelMap.BACCCustomerId}', dataField: 'partyCode', width: 120, cellsformat: 'dd/MM/yyyy'},
					 { text: '${uiLabelMap.BACCCustomerName}', dataField: 'partyName', width: 400,
					 	aggregatesrenderer: function (aggregates, column, element) {
                        	var renderstring = \"<div class='jqx-widget-content jqx-widget-content-\" + theme + \"' style='float: left; width: 100%; height: 100%; '>\";
                          	renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '<b>' + '${uiLabelMap.BACCAmountTotal}' + '</b>' + '</div>';
                          	renderstring += \"</div>\";
                          	return renderstring;
                      	}
					 },
					 { text: '${uiLabelMap.BACCDebitAmount}', dataField: 'openingDebitAmount', width: 150, columngroup: 'openingAmount',
						 cellsrenderer: function(row, column, value){
						 	if (value == 0) {
						 		return '';
						 	}
							var data = $('#liabilitySupplierGrid').jqxGrid('getrowdata',row);
        		  			if(data != undefined && data){
                		  		return '<span class=align-right>' + formatcurrency(value, data.currencyId) + '</span>';
        		  			}
						 },
						 aggregatesrenderer: function (aggregates, column, element) {
                        	var renderstring = \"<div class='jqx-widget-content jqx-widget-content-\" + theme + \"' style='float: left; width: 100%; height: 100%; '>\";
                        	var total = 0;
                          	if (totalMap && totalMap.totalOpeningDebit) {
                          		total = totalMap.totalOpeningDebit;
                          	}
                          	var data = $('#liabilitySupplierGrid').jqxGrid('getrows');
                        	var currencyUomId = 'VND';
                        	if (data && data.length > 0) {
                        		currencyUomId = data[0].currencyId;
                        	}
                        	if (total == 0) {
                        		renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '-' + '</div>';
                        	} else {
                        		renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\"><b>' + formatcurrency(total, currencyUomId) + '</b></div>';
                        	}
                          	renderstring += \"</div>\";
                          	return renderstring;
                      	}
					 },
					 { text: '${uiLabelMap.BACCCreditAmount}', dataField: 'openingCreditAmount', width: 150, columngroup: 'openingAmount',
						 cellsrenderer: function(row, column, value){
						 	if (value == 0) {
						 		return '';
						 	}
							var data = $('#liabilitySupplierGrid').jqxGrid('getrowdata',row);
        		  			if(data != undefined && data){
                		  		return '<span class=align-right>' + formatcurrency(value, data.currencyId) + '</span>';
        		  			}
						 },
						 aggregatesrenderer: function (aggregates, column, element) {
                        	var renderstring = \"<div class='jqx-widget-content jqx-widget-content-\" + theme + \"' style='float: left; width: 100%; height: 100%; '>\";
                          	var total = 0;
                          	if (totalMap && totalMap.totalOpeningCredit) {
                          		total = totalMap.totalOpeningCredit;
                          	}
                          	var data = $('#liabilitySupplierGrid').jqxGrid('getrows');
                        	var currencyUomId = 'VND';
                        	if (data && data.length > 0) {
                        		currencyUomId = data[0].currencyId;
                        	}
                        	if (total == 0) {
                        		renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '-' + '</div>';
                        	} else {
                        		renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\"><b>' + formatcurrency(total, currencyUomId) + '</b></div>';
                        	}
                          	renderstring += \"</div>\";
                          	return renderstring;
                      	}
					 },
					 { text: '${uiLabelMap.BACCDebitAmount}', dataField: 'postingDebitAmount', width: 150, columngroup: 'postingAmount',
						 cellsrenderer: function(row, column, value){
						 	if (value == 0) {
						 		return '';
						 	}
							var data = $('#liabilitySupplierGrid').jqxGrid('getrowdata',row);
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
                          	var data = $('#liabilitySupplierGrid').jqxGrid('getrows');
                        	var currencyUomId = 'VND';
                        	if (data && data.length > 0) {
                        		currencyUomId = data[0].currencyId;
                        	}
                        	if (total == 0) {
                        		renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '-' + '</div>';
                        	} else {
                        		renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\"><b>' + formatcurrency(total, currencyUomId) + '</b></div>';
                        	}
                          	renderstring += \"</div>\";
                          	return renderstring;
                      	}
					 },
					 { text: '${uiLabelMap.BACCCreditAmount}', dataField: 'postingCreditAmount', width: 150, columngroup: 'postingAmount',
						 cellsrenderer: function(row, column, value){
						 	if (value == 0) {
						 		return '';
						 	}
							var data = $('#liabilitySupplierGrid').jqxGrid('getrowdata',row);
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
                          	var data = $('#liabilitySupplierGrid').jqxGrid('getrows');
                        	var currencyUomId = 'VND';
                        	if (data && data.length > 0) {
                        		currencyUomId = data[0].currencyId;
                        	}
                        	if (total == 0) {
                        		renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '-' + '</div>';
                        	} else {
                        		renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\"><b>' + formatcurrency(total, currencyUomId) + '</b></div>';
                        	}
                          	renderstring += \"</div>\";
                          	return renderstring;
                      	}
					 },
					 { text: '${uiLabelMap.BACCDebitAmount}', dataField: 'endingDebitAmount', width: 150, columngroup: 'endingAmount',
						 cellsrenderer: function(row, column, value){
						 	if (value == 0) {
						 		return '';
						 	}
							var data = $('#liabilitySupplierGrid').jqxGrid('getrowdata',row);
        		  			if(data != undefined && data){
                		  		return '<span class=align-right>' + formatcurrency(value, data.currencyId) + '</span>';
        		  			}
						 },
						 aggregatesrenderer: function (aggregates, column, element) {
                        	var renderstring = \"<div class='jqx-widget-content jqx-widget-content-\" + theme + \"' style='float: left; width: 100%; height: 100%; '>\";
                          	var total = 0;
                          	if (totalMap && totalMap.totalEndingDebit) {
                          		total = totalMap.totalEndingDebit;
                          	}
                          	var data = $('#liabilitySupplierGrid').jqxGrid('getrows');
                        	var currencyUomId = 'VND';
                        	if (data && data.length > 0) {
                        		currencyUomId = data[0].currencyId;
                        	}
                        	if (total == 0) {
                        		renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '-' + '</div>';
                        	} else {
                        		renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\"><b>' + formatcurrency(total, currencyUomId) + '</b></div>';
                        	}
                          	renderstring += \"</div>\";
                          	return renderstring;
                      	}
					 },
					 { text: '${uiLabelMap.BACCCreditAmount}', dataField: 'endingCreditAmount', width: 150, columngroup: 'endingAmount',
						 cellsrenderer: function(row, column, value){
						 	if (value == 0) {
						 		return '';
						 	}
							var data = $('#liabilitySupplierGrid').jqxGrid('getrowdata',row);
        		  			if(data != undefined && data){
                		  		return '<span class=align-right>' + formatcurrency(value, data.currencyId) + '</span>';
        		  			}
						 },
						 aggregatesrenderer: function (aggregates, column, element) {
                        	var renderstring = \"<div class='jqx-widget-content jqx-widget-content-\" + theme + \"' style='float: left; width: 100%; height: 100%; '>\";
                          	var total = 0;
                          	if (totalMap && totalMap.totalEndingCredit) {
                          		total = totalMap.totalEndingCredit;
                          	}
                          	var data = $('#liabilitySupplierGrid').jqxGrid('getrows');
                        	var currencyUomId = 'VND';
                        	if (data && data.length > 0) {
                        		currencyUomId = data[0].currencyId;
                        	}
                        	if (total == 0) {
                        		renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '-' + '</div>';
                        	} else {
                        		renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\"><b>' + formatcurrency(total, currencyUomId) + '</b></div>';
                        	}
                          	renderstring += \"</div>\";
                          	return renderstring;
                      	}
					 }
				"/>
 <#assign columngrouplist=" { text: '${uiLabelMap.BACCOpeningAmount}', name: 'openingAmount', align: 'center'},
  							{ text: '${uiLabelMap.BACCEndingAmount}', name: 'endingAmount', align: 'center'},
  							{ text: '${uiLabelMap.BACCPostingAmount}', name: 'postingAmount', align: 'center'}
						">

 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].nowDateString("dd-MM-yyyy") /> 
 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].nowDateString("dd-MM-yyyy") />

<@jqGrid id="liabilitySupplierGrid" filtersimplemode="true" filterable="false" addrefresh="true" editable="false" addType="popup" showtoolbar="true" clearfilteringbutton="false"
		 url="jqxGeneralServicer?sname=JqxGetLiabilitySupplier&glAccountId=${receipGlAccount.glAccountId}&fromDate=${fromDate}&thruDate=${thruDate}" sortable="false"
		 dataField=dataField columnlist=columnlist showstatusbar="false" statusbarheight="30" columngrouplist=columngrouplist showstatusbar="true" displayTotal="true"
		 customcontrol1="fa fa-file-excel-o open-sans@@javascript: void(0);exportExcel()" customtoolbaraction="updateData"
	 />
  							
<script>
	$( document ).ready(function(){
		filter.initFilter();
		filter.bindEvent();
	});
	
	$('#liabilitySupplierGrid').on("bindingcomplete", function (event) {
        var fromDate1 = $("#fromDate").jqxDateTimeInput('getDate');
        var thruDate1 = $("#toDate").jqxDateTimeInput('getDate');

        var fromDate = fromDate1.getDate() + '-' + (fromDate1.getMonth() + 1) + '-' + fromDate1.getFullYear();
        var thruDate = thruDate1.getDate() + '-' + (thruDate1.getMonth() + 1) + '-' + thruDate1.getFullYear();

        jQuery.ajax({
            url: 'getTotalLiabilitySupplier',
            async: false,
            type: 'POST',
            data: {
            	glAccountId: $("#glAccountId").attr('data-value'),
            	fromDate: fromDate,
            	thruDate: thruDate
            },
            success: function (data) {
                totalMap.totalOpeningCredit = data.totalOpeningCredit;
                totalMap.totalOpeningDebit = data.totalOpeningDebit;
                totalMap.totalPostingCredit = data.totalPostingCredit;
                totalMap.totalPostingDebit = data.totalPostingDebit;
                totalMap.totalEndingCredit = data.totalEndingCredit;
                totalMap.totalEndingDebit = data.totalEndingDebit;
            }
        });
	});  
	
	var _service = "acctgTransTotal";
	var _updating = false;
	var exportExcel = function(){
		var allData = $('#liabilitySupplierGrid').jqxGrid('getrows');
		if(allData.length > 0){
			var glId = $("#glAccountId").attr('data-value');			
			glId = glId != null && glId !== undefined && glId ? glId : '${receipGlAccount.glAccountId?if_exists}';

            var fromDate1 = $("#fromDate").jqxDateTimeInput('getDate');
            var thruDate1 = $("#toDate").jqxDateTimeInput('getDate');

            var fromDate = fromDate1.getDate() + '-' + (fromDate1.getMonth() + 1) + '-' + fromDate1.getFullYear();
            var thruDate = thruDate1.getDate() + '-' + (thruDate1.getMonth() + 1) + '-' + thruDate1.getFullYear();

            window.location.href = "exportLiabilitySupplierExcel?glAccountId=" + glId + "&fromDate=" + fromDate + "&thruDate=" + thruDate;
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
                    $("#liabilitySupplierGrid").jqxGrid('updatebounddata');
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