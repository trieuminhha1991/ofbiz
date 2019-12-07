
<#assign dataField="[{ name: 'uomId', type: 'string' },	
					 { name: 'uomIdTo', type: 'string' },
					 { name: 'uomIdDes', type: 'string' },
					 { name: 'uomIdToDes', type: 'string' },
					 { name: 'purposeEnumId', type: 'string' },
					 { name: 'bankId', type: 'string' },
					 { name: 'bankName', type: 'string' },
					 { name: 'conversionFactor', type: 'number' ,other : 'Double'},
					 { name: 'purchaseExchangeRate', type: 'number' ,other : 'Double'},
					 { name: 'sellingExchangeRate', type: 'number' ,other : 'Double'},
					 { name: 'fromDate', type: 'date', other:'Timestamp'},
					 { name: 'thruDate', type: 'date', other:'Timestamp' }
					]"/>
<#assign columnlist="
					 {
					    text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					 },
                     { text: '${uiLabelMap.accFromUomId}', datafield: 'uomIdDes', width: 200},
					 { text: '${uiLabelMap.accToUomId}', datafield: 'uomIdToDes', width: 200},
					 { text: '${uiLabelMap.BACCBankName}', datafield: 'bankName', width: 200},
                     { text: '${uiLabelMap.BACCPurchaseRate}', datafield: 'purchaseExchangeRate', width: 140,filtertype : 'number' },
                     { text: '${uiLabelMap.BACCSellingRate}', datafield: 'sellingExchangeRate', width: 140,filtertype : 'number' },
                     { text: '${uiLabelMap.BACCConversionFactor}', datafield: 'conversionFactor', width: 140,filtertype : 'number' },
                     { text: '${uiLabelMap.fromDate}', datafield: 'fromDate', filtertype: 'range', width: 200, cellsformat: 'dd/MM/yyyy HH:mm:ss'},
                     { text: '${uiLabelMap.accThruDate}', datafield: 'thruDate', filtertype: 'range', width: 200, cellsformat: 'dd/MM/yyyy HH:mm:ss'},
                     { text: '${uiLabelMap.CommonPurpose}', datafield: 'purposeEnumId', filtertype : 'checkedlist', width: 150, cellsrenderer:
                     	function(row, colum, value)
                        {
                        	for(i = 0; i < leData.length;i++){
                        		if(leData[i].enumId == value){
                        			return \"<span>\" + leData[i].description + \"</span>\";
                        		}
                        	}
                        	return \"<span>\" + value + \"</span>\";
                        },createfilterwidget : function(column,columnElement,widget){
				    	var filterBoxAdapter = new $.jqx.dataAdapter(leData,{autoBind : true});
				    	var uniRecords = filterBoxAdapter.records;
				    	widget.jqxDropDownList({source: uniRecords, displayMember: 'description', valueMember: 'enumId',placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}',renderer: function (index, label, value) 
											{
												for(i = 0;i < leData.length; i++){
													if(leData[i].enumId == value){
														return leData[i].description;
													}
												}
											    return value;
											}});
				    }
                     }
					 "/>
<@jqGrid addrow="true" addType="popup" id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListConversions" dataField=dataField columnlist=columnlist defaultSortColumn="fromDate" sortdirection="desc"
		 createUrl="jqxGeneralServicer?sname=updateFXConversion&jqaction=C" addColumns="uomId;uomIdTo;bankId;purposeEnumId;purchaseExchangeRate;sellingExchangeRate;conversionFactor;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"
		 alternativeAddPopup="alterpopupWindow" mouseRightMenu="true" contextMenuId="menuForForiegnExchange"
		 />

<div id='menuForForiegnExchange' style="display:none;">
    <ul>
        <li><i class="fa fa-edit"></i>${uiLabelMap.Edit}</li>
        <li id="addThruDate"><i class="fa red fa-ban"></i>${uiLabelMap.BSFinish}</li>
        <li><i class="fa red fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.CommonRefresh)}</li>
    </ul>
</div>
<script>
    $(function () {
        initMenu();
        initEvents();
    });

    var initMenu = function() {
        $("#menuForForiegnExchange").jqxMenu({ theme: theme, width: 240, autoOpenPopup: false, mode: "popup" });
    };
    var initEvents = function() {
        $("#menuForForiegnExchange").on('itemclick', function (event) {
            var agrgs = event.args;
            var x = agrgs.id;
            var data = $('#jqxgrid').jqxGrid('getRowData', $("#jqxgrid").jqxGrid('selectedrowindexes'));
            var tmpStr = $.trim($(args).text());
            if (tmpStr ==  "${StringUtil.wrapString(uiLabelMap.Edit)}") {
                accutils.openJqxWindow($("#editPopupWindow"));
                EditForeign.initData(data);
            } else if (tmpStr == "${StringUtil.wrapString(uiLabelMap.BSRefresh)}") {
                updateGridData();
            }
            if(x == 'addThruDate') {
                closeFxConversion(data);
            }
        });
    };

    var closeFxConversion = function (data) {
        $.ajax({
            url: 'closeFXConversion',
            data: {
                uomId: data.uomId,
                uomIdTo: data.uomIdTo,
                fromDate: data.fromDate.getTime()
            },
            type: "POST",
            success: function(response) {
                if(response._ERROR_MESSAGE_){
                    bootbox.dialog(response._ERROR_MESSAGE_,
                            [
                                {
                                    "label" : uiLabelMap.CommonClose,
                                    "class" : "btn-danger btn-small icon-remove open-sans",
                                }]
                    );
                    return false;
                }
                $("#jqxgrid").jqxGrid('updatebounddata');
                return true;
            }
        });
    }

    var updateGridData = function () {
        $("#jqxgrid").jqxGrid('updatebounddata');
    };
</script>
