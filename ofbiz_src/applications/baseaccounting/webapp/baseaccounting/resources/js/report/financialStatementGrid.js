$(function(){
	FinancialStatementObj.init();
});

var FinancialStatementObj = (function(){
	var dataFinancialStatement;
	var periodName;
	var previousPeriodName;
	var _service = "acctgTransTotal";
	var _updating = false;
	
	var init = function(){
		initEvent();
		if (flag === 'T' || isClosed == 'Y') {
			$("#treeGrid").css("display", "block");
			$("#errorNotify").css("display", "none");
			getDataFinancialStatement(customTimePeriodDefault, organizationPartyId, reportTypeId, flag);
    	} else {
    		$("#errorNotify").css("display", "block");
    		$("#treeGrid").css("display", "none");
		}
	};
	
	var initEvent = function(){
		$('#treeGrid').on('bindingComplete', function (event) {
			var data = $("#treeGrid").jqxTreeGrid('getRows');
			for (var i = 0; i < data.length; i++) {
        		if (data[i]['children'].length > 0) {
        			$("#treeGrid").jqxTreeGrid('expandRow', data[i]['targetId']);
        			var childData = data[i]['children'];
        			for (var j = 0; j < childData.length; j++) {
        				if (childData[j]['children'].length > 0) {
        					$("#treeGrid").jqxTreeGrid('expandRow', childData[j]['targetId']);
        				}
        			}
        		}
        	}
		}); 
	};
	
	var renderTreeGridFinancialStatement = function(periodName, previousPeriodName) {
    	var source =
        {
            dataType: "json",
            dataFields: [
				{ name: "targetId", type: "string" },
                { name: "name", type: "string" },
                { name: "displaySign", type: "string" },
                { name: "demonstration", type: "string" },
                { name: "value1", type: "string" },
                { name: "value2", type: "string" },
                { name: "children", type: "array" },
                { name: "code", type: "string" }
            ],
            hierarchy:
            {
                root: "children"
            },
            id: 'targetId',
            localData: dataFinancialStatement
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        
        var cellClassName = function (row, column, value, data) {
        	if(data['children'].length > 0)
        		return 'brand';
        	else
        		return 'leaf';
        }
        
        $("#treeGrid").jqxTreeGrid({
            source: dataAdapter,
            localization: getLocalization(),
            width: '100%',
            theme: 'olbius',
            autoRowHeight: true,
            altRows: true,
            showToolbar: true,
            columnsResize: true,
            columns:
            [
                  { text: uiLabelMap.BACCTarget, dataField: "name", align: 'center', width: '45%', cellclassname: cellClassName, resizable: true},
                  { text: uiLabelMap.BACCCode, cellsAlign: "center", dataField: "code", align: 'center', width: '7.5%', cellclassname: cellClassName, resizable: true },
                  { text: uiLabelMap.BACCDemonstration, cellsAlign: "center", dataField: "demonstration", align: 'center', width: '9%', cellclassname: cellClassName, resizable: true },
                  { text: periodName, dataField: "value1", cellsAlign: "right", align: 'center', width: '20%', resizable: true, cellsrenderer:
    				 	function(row, colum, value) {
    				 		var data = $('#treeGrid').jqxTreeGrid('getRow', row);
    				 		var tmpCurr = formatcurrency(data.value1.toFixed(2),'${currencyUomId}');
    				 		if (data['displaySign'] == 'S') {
    				 			tmpCurr = '(' + tmpCurr + ')';
    				 			return "<span  style=\"text-align: right;\">" + tmpCurr + "</span>";
    				 		} else {
    				 			if (tmpCurr.indexOf('-') > -1) {
    				 				tmpCurr = tmpCurr.replace("-", "");
    				 				tmpCurr = '(' + tmpCurr + ')';
    				 			}
    				 			return "<span  style=\"text-align: right;\">" + tmpCurr + "</span>";
    				 		}
    				 	},
    				 	cellclassname: cellClassName
                  },
                  { text: previousPeriodName, dataField: "value2", cellsAlign: "right", align: 'center', width: '20%', resizable: true, cellsrenderer:
                	  	function(row, colum, value){
    				 		var data = $('#treeGrid').jqxTreeGrid('getRow', row);
    				 		var tmpCurr = formatcurrency(data.value2.toFixed(2),'${currencyUomId}');
    				 		if (data['displaySign'] == 'S') {
    				 			tmpCurr = '(' + tmpCurr + ')';
    				 			return "<span  style=\"text-align: right;\">" + tmpCurr + "</span>";
    				 		} else {
    				 			if (tmpCurr.indexOf('-') > -1) {
    				 				tmpCurr = tmpCurr.replace("-", "");
    				 				tmpCurr = '(' + tmpCurr + ')';
    				 			}
    				 			return "<span  style=\"text-align: right;\">" + tmpCurr + "</span>";
    				 		}
    				 	},
    				 	cellclassname: cellClassName
                  }
            ],
            renderToolbar: function rendertoolbar(toolbar) {
            	toolbar.html("");
            	var container = $("<div style='margin: 3px;'></div>");
                var span = $("<h4 style='float: left; margin-top: 5px; margin-right: 4px; color: #4383b4'>" + titleProperty + " </h4>");
                toolbar.append(container);
                container.append(span);
                
                container.append('<button id="expandAll" class="btn btn-mini btngridsetting"><i class="fa-expand"></i></button>');
                container.append('<button id="collapseAll" class="btn btn-mini btngridsetting"><i class="fa-compress"></i></button>');
                container.append('<button id="exportExcel" class="btn btn-mini btngridsetting"><i class="fa-file-excel-o"></i></button>');
                
                $('#expandAll').on('click', function(){
                	$("#treeGrid").jqxTreeGrid("expandAll");
                });
                
                $('#collapseAll').on('click', function(){
                	$("#treeGrid").jqxTreeGrid("collapseAll");
                });
                
                $('#exportExcel').on('click', function(){
                	exportExcel();
                });
                
                container.append('<button id="update-financial" title="' + uiLabelMap.AggregateLabel + '" class="btn btn-mini btngridsetting"><i class="fa-play"></i></button>');
        		container.append('<span id="status-financial" style="float: right; margin-top: 9px; margin-right: 4px; color: #4383b4"><image src="/images/ajax-loader.gif"></span>');
        		container.append('<h5 id="lastupdate-financial" style="float: right; margin-top: 9px; margin-right: 4px; color: #4383b4"></h5>');
        		
        		$("#update-financial").on('click', function () {
                    bootbox.dialog(uiLabelMap.olap_warn_grid,
                        [{
                            "label": uiLabelMap.olap_ok_grid,
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
                                "label": uiLabelMap.olap_cancel_grid,
                                "class": "btn-danger btn-small icon-remove",
                                "callback": function () {
                                    checkStatus();
                                }
                            }]
                    );
                });
                checkStatus();
            }
        });
	};
	
	var exportExcel = function(){
		var allData = $('#treeGrid').jqxTreeGrid('getRows');
		if (allData.length > 0) {
			var customTimePeriodId = $('#customTimePeriods').jqxDropDownList('getSelectedItem').originalItem.customTimePeriodId;
			if (customTimePeriodId == undefined || customTimePeriodId == null || customTimePeriodId == '') {
				customTimePeriodId = customTimePeriodDefault;
			}
 		    
			var url = "exportFinancialExcel?organizationPartyId=" + organizationPartyId + "&reportTypeId=" + reportTypeId + "&flag=" + flag + "&customTimePeriodId=" + customTimePeriodId;
			window.open(url, "_blank");
		} else {
    		bootbox.alert(uiLabelMap.ReportCheckNotData);
    	}
	};
	
	var checkStatus = function(){
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
    
    var getLastUpdated = function(){
        jQuery.ajax({
            url: 'getLastupdatedOlapServices',
            async: true,
            type: 'POST',
            data: {'service': _service},
            success: function (data) {
                if (data.lastupdated > 0) {
                    var tmp = $.jqx.dataFormat.formatdate(new Date(data.lastupdated), 'yyyy-MM-dd HH:mm:ss');
                    _serviceTimestamp = data.lastupdated;
                    $("#lastupdate-financial").html(uiLabelMap.olap_lastupdated_grid + ': ' + tmp);
                    $("#status-financial").hide();
                    if ($("#update-financial")) {
                        $("#update-financial").show();
                    }
                    $("#lastupdate-financial").show();
                } else {
                    $("#status-financial").hide();
                    $("#lastupdate-financial").hide();
                    if ($("#update-financial")) {
                        $("#update-financial").show();
                    }
                }
            },
            complete: function(){
            	if (_updating) {
                    _updating = false;
                    getDataFinancialStatement(customTimePeriodDefault, organizationPartyId, reportTypeId, flag);
                    $('body').trigger('runolapservicedone');
                }
            }
        });
    }
    
    var getUpdating = function() {
        $("#status-financial").show();
        $("#lastupdate-financial").hide();
        if ($("#update-financial")) {
            $("#update-financial").hide();
        }
        _updating = true;
    }
	
    var getDataFinancialStatement = function(customTimePeriodId, organizationPartyId, reportTypeId, flag) {
    	Loading.show('loadingMacro');
    	$.ajax({
    		url: "getDataFinancialStatement",
    		type: "POST",
    		aync: false,
    		data: {
    			customTimePeriodId: customTimePeriodId,
    			organizationPartyId: organizationPartyId,
    			reportTypeId: reportTypeId,
    			flag: flag
    		},
    		dataType: "json",
    		success: function(res) {
    			dataFinancialStatement = res["listData"];
    			periodName = res["periodName"];
    			previousPeriodName = res["previousPeriodName"];
    		}
    	}).done(function() {
    		Loading.hide('loadingMacro');
    		renderTreeGridFinancialStatement(periodName, previousPeriodName);
    	});
    }
	
	return {
		init: init,
		getDataFinancialStatement: getDataFinancialStatement
	};
}());