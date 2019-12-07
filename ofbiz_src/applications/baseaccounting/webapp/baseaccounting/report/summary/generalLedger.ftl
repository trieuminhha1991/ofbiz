<#include "script/periodData.ftl">
<#include "generalLedgerFilter.ftl">

<#assign dataField="[{ name: 'transDate', type: 'date', other:'Timestamp' },
					 { name: 'acctgTransId', type: 'string'},
					 { name: 'documentId', type: 'string'},
					 { name: 'voucherCode', type: 'string'},
					 { name: 'documentNumber', type: 'string'},
					 { name: 'voucherDate', type: 'date', other:'Timestamp'},
					 { name: 'voucherDescription', type: 'string'},
					 { name: 'recipGlAccountCode', type: 'string'},
					 { name: 'creditAmount', type: 'number'},
					 { name: 'debitAmount', type: 'number'},
					 { name: 'glAccountCode', type: 'string'},
					 { name: 'currencyUomId', type: 'string'},
					 { name: 'note', type: 'string'}
				 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.BACCTransDate}', dataField: 'transDate', width: 150, cellsformat: 'dd/MM/yyyy'},
					 { text: '${uiLabelMap.BACCAcctgTransId}', dataField: 'acctgTransId', width: 100, columngroup: 'glVoucher'},
					 { text: '${uiLabelMap.BACCVoucherId}', dataField: 'documentId', width: 100, columngroup: 'glVoucher'},
					 { text: '${uiLabelMap.BACCVoucherNumber}', dataField: 'voucherCode', width: 100, columngroup: 'glVoucher'},
					 { text: '${uiLabelMap.BACCVoucherNumberSystem}', dataField: 'documentNumber', width: 155, columngroup: 'glVoucher'},
					 { text: '${uiLabelMap.BACCVoucherDate}', dataField: 'voucherDate', width: 150, cellsformat: 'dd/MM/yyyy', columngroup: 'glVoucher'},
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
                          	renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '<b>' + '${uiLabelMap.BACCTongPhatSinh}' + '</b>' + '</div>';
                          	renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '<b>' + '${uiLabelMap.BACCSoDuCuoiKy}' + '</b>' + '</div>';
                          	renderstring += \"</div>\";
                          	return renderstring;
                      	}
					 },
					 { text: '${uiLabelMap.BACCRecipGlAccountId}', dataField: 'recipGlAccountCode', width: 150},
					 { text: '${uiLabelMap.BACCDebitAmount}', dataField: 'debitAmount', width: 150, columngroup: 'glAmount',
						 cellsrenderer: function(row, column, value){
							 var data = $('#genLedgerGrid').jqxGrid('getrowdata',row);
        		  			 if(data != undefined && data){
        		  				return '<span class=align-right>' + formatcurrency(value, data.currencyId) + '</span>';
        		  			 }
						 },
						 aggregatesrenderer: function (aggregates, column, element) {
                        	var renderstring = \"<div class='jqx-widget-content jqx-widget-content-\" + theme + \"' style='float: left; width: 100%; height: 100%; '>\";
                        	var total = 0; var totalEnding = '-';
                        	if (totalMap && totalMap.totalPostingDebit) {
                        		total = totalMap.totalPostingDebit;
                        	}
                        	if (totalMap && totalMap.totalEnding < 0) {
                          		totalEnding = formatcurrency(totalMap.totalEnding * (-1), currencyUomId);
                          	}
                        	var data = $('#genLedgerGrid').jqxGrid('getrows');
                        	var currencyUomId = 'VND';
                        	if (data && data.length > 0) {
                        		currencyUomId = data[0].currencyId;
                        	}
                          	renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\"><b>' + formatcurrency(total, currencyUomId) + '</b></div>';
                          	renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\"><b>' + totalEnding + '</b></div>';
                          	renderstring += \"</div>\";
                          	return renderstring;
                      	}
					 },
					 { text: '${uiLabelMap.BACCCreditAmount}', dataField: 'creditAmount', width: 150, columngroup: 'glAmount',
						 cellsrenderer: function(row, column, value){
							 var data = $('#genLedgerGrid').jqxGrid('getrowdata',row);
        		  			 if(data != undefined && data){
                		  		return '<span class=align-right>' + formatcurrency(value, data.currencyId) + '</span>';
        		  			 }
						 },
						 aggregatesrenderer: function (aggregates, column, element) {
                        	var renderstring = \"<div class='jqx-widget-content jqx-widget-content-\" + theme + \"' style='float: left; width: 100%; height: 100%; '>\";
                        	var total = 0; var totalEnding = '-';
                        	if (totalMap && totalMap.totalPostingCredit) {
                        		total = totalMap.totalPostingCredit;
                        	}
                        	if (totalMap && totalMap.totalEnding >= 0) {
                          		totalEnding = formatcurrency(totalMap.totalEnding, currencyUomId);
                          	}
                        	var data = $('#genLedgerGrid').jqxGrid('getrows');
                        	var currencyUomId = 'VND';
                        	if (data && data.length > 0) {
                        		currencyUomId = data[0].currencyId;
                        	}
                          	renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\"><b>' + formatcurrency(total, currencyUomId) + '</b></div>';
                          	renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\"><b>' + totalEnding + '</b></div>';
                          	renderstring += \"</div>\";
                          	return renderstring;
                      	}
					 },
					 { text: '${uiLabelMap.BACCNote}', dataField: 'note', width: 100},
					 { text: '${uiLabelMap.BACCGlAccountId}', dataField: 'glAccountCode', width: 150}
				"/>
 <#assign columngrouplist=" { text: '${uiLabelMap.BACCGLVoucher}', name: 'glVoucher', align: 'center'},
  							{ text: '${uiLabelMap.BACCGLAmount}', name: 'glAmount', align: 'center'}
						">

 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].nowDateString("dd-MM-yyyy") />
 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].nowDateString("dd-MM-yyyy") />

 <#assign url = "jqxGeneralServicer?sname=JqxGetGeneralLedger&glAccountId=${glAccount.glAccountId}&fromDate=" + '${fromDate}' + "&thruDate=" + '${thruDate}' />
 
 <@jqGrid id="genLedgerGrid" filtersimplemode="true" filterable="false" addrefresh="true" editable="false" addType="popup" showtoolbar="true" clearfilteringbutton="true"
		 url=url dataField=dataField columnlist=columnlist showstatusbar="false" isSaveFormData="true" formData="filterObjData" customtoolbaraction="updateData"
		 statusbarheight="30" columngrouplist=columngrouplist customcontrol1="fa fa-file-excel-o open-sans@${StringUtil.wrapString(uiLabelMap.BSExportExcel)}@javascript:void(0);exportExcel()"
	 	 displayTotal="true" showstatusbar="true" statusbarheight="50"
	 />
 
<script>
	$( document ).ready(function(){
		filter.initFilter();
		filter.bindEvent();
	});
	
	$('#genLedgerGrid').on("bindingcomplete", function (event) {
		var selection = $("#dateTime").jqxDateTimeInput('getRange');
		var fromDate1 = selection.from;		
		var fromDate = fromDate1.getDate() + '-' + (fromDate1.getMonth() + 1) +  '-' +   fromDate1.getFullYear();
						
		var thruDate1 = selection.to;
		var thruDate = thruDate1.getDate() + '-' + (thruDate1.getMonth() + 1) +  '-' +   thruDate1.getFullYear();
		var glId = $("#glAccountId").attr('data-value');
		glId = glId != null && glId !== undefined && glId ? glId : '${glAccount.glAccountId?if_exists}';
		
		jQuery.ajax({
            url: 'getTotalGenLedger',
            async: false,
            type: 'POST',
            data: {
            	glAccountId: glId,
            	fromDate: fromDate,
            	thruDate: thruDate
            },
            success: function (data) {
                totalMap.totalPostingCredit = data.totalPostingCredit;
                totalMap.totalPostingDebit = data.totalPostingDebit;
                totalMap.totalEnding = data.totalEnding;
            }
        });
	});  
	
	var filterObjData = new Object();
	var exportExcel = function(){
		var dataGrid = $("#genLedgerGrid").jqxGrid('getrows');
		if (dataGrid.length == 0) {
			jOlbUtil.alert.error("${uiLabelMap.ReportCheckNotData}");
			return false;
		}
	
		var winURL = "exportGeneralLedgerExcelNew";
		var form = document.createElement("form");
		form.setAttribute("method", "POST");
		form.setAttribute("action", winURL);
		form.setAttribute("target", "_blank");
		
		var glId = $("#glAccountId").attr('data-value');
		glId = glId != null && glId !== undefined && glId ? glId : '${glAccount.glAccountId?if_exists}';
		var selection = $("#dateTime").jqxDateTimeInput('getRange');
		var fromDate1 = selection.from;  
		var fromDate = fromDate1.getDate() + '-' + (fromDate1.getMonth() + 1) +  '-' +   fromDate1.getFullYear();
		var thruDate1 = selection.to;
		var thruDate = thruDate1.getDate() + '-' + (thruDate1.getMonth() + 1) +  '-' +   thruDate1.getFullYear();
		
		var hiddenField0 = document.createElement("input");
		hiddenField0.setAttribute("type", "hidden");
		hiddenField0.setAttribute("name", "glAccountId");
		hiddenField0.setAttribute("value", glId);
		form.appendChild(hiddenField0);
		
		var hiddenField1 = document.createElement("input");
		hiddenField1.setAttribute("type", "hidden");
		hiddenField1.setAttribute("name", "fromDate");
		hiddenField1.setAttribute("value", fromDate);
		form.appendChild(hiddenField1);
		
		var hiddenField2 = document.createElement("input");
		hiddenField2.setAttribute("type", "hidden");
		hiddenField2.setAttribute("name", "thruDate");
		hiddenField2.setAttribute("value", thruDate);
		form.appendChild(hiddenField2);
		
		if (OlbCore.isNotEmpty(filterObjData) && OlbCore.isNotEmpty(filterObjData.data)) {
			$.each(filterObjData.data, function(key, value) {
				var hiddenField3 = document.createElement("input");
				hiddenField3.setAttribute("type", "hidden");
				hiddenField3.setAttribute("name", key);
				hiddenField3.setAttribute("value", value);
				form.appendChild(hiddenField3);
			});
		}
		
		document.body.appendChild(form);
		form.submit();
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