<link rel="stylesheet" type="text/css" href="/accresources/css/grid.css">
<style>
.zIndex999999{
	z-index: 999999 !important
}
</style>
<script>
    var cellclass = function (row, columnfield, value) {
		var data = $('#jqxgridDiscount').jqxGrid('getrowdata', row);
		if(data != undefined && data){
			if (data.newStatusId == 'INV_CANCELLED_NEW') {
	            return 'canceled';
	        }
	        else if (data.newStatusId == 'INVOICE_WRITEOFF') {
	            return 'writeoff';
	        }
	        else if (data.newStatusId == 'INV_NOT_APPR_NEW'){
	        	return 'ready';
	        }
	        else if (data.newStatusId == 'INV_APPR_NEW'){
	        	return 'approved';
	        }
	        else if (data.newStatusId == 'INV_IN_PROCESS_NEW'){
	        	return 'inprocess';
	        }
	        else if (data.statusId == 'INV_PAID_NEW'){
	        	return 'paid';
	        }
		}
    }

    var statusArr = [
    <#if newStatusList?exists>
        <#list newStatusList as status>
            {
                statusId : "${status.statusId}",
                description : "${StringUtil.wrapString(status.get('description',locale))}",
            },
        </#list>
    </#if>
    ];
</script>
<style>
	.canceled {
	    background-color: #ff6f4c !important;
	}
	.writeoff {
	    background-color: #FF6600 !important;
	}
	.ready {
	    background-color: #b3b3ff !important; 
	}
	.approved {
	    background-color: #00CC99 !important;
	}
	.inprocess {
	    background-color: #89EA89 !important;
	}
	.received {
	    background-color: #99cdff !important;
	}
	.sent {
	    background-color: #006666 !important;
	}
	.paid {
	    background-color: #b1e4cc !important;
	}
	.aggregates{
		font-weight: 600;
		text-align: right;
	}
</style>
<#--=================================Init Grid======================================================-->
<#assign dataField="[{ name: 'partyId', type: 'string' },
					 { name: 'partyCode', type: 'string'},
					 { name: 'partyName', type: 'string'},
					 { name: 'statusId', type: 'string'},
					 { name: 'newStatusId', type: 'string'},
					 { name: 'description', type: 'string'},
					 { name: 'totalAmount', type: 'string'},
					 { name: 'productPromoId', type: 'string'},
				 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.BACCCustomerId}', dataField: 'partyId', width: '9%', pinned: true,
						cellsrenderer: function(row, column, value){
							var data = $('#jqxgridDiscount').jqxGrid('getrowdata', row);
							if(data != undefined && data){
                                return '<span><a href=viewDetailDiscountAndPromoCustomer?&partyId=' + data.partyId + '&newStatusId=' + data.newStatusId + '&productPromoId=' + data.productPromoId +' style=\"color: blue !important;\">' + data.partyId + '</a></span>';
							}}
						},"/>
<#assign columnlist = columnlist + "{ text: '${uiLabelMap.BACCInvoiceFromParty}', width: '18%', dataField:'partyName',  pinned: true, cellclassname: cellclass,
					    },"/>
<#assign columnlist = columnlist + "
	                  { text: '${uiLabelMap.CommonStatus}', dataField: 'newStatusId', width: '13%', filtertype: 'checkedlist',
	                	  cellsrenderer: function(row, column, value){
	                		  for(var i = 0; i < statusArr.length; i++){
									if(value == statusArr[i].statusId){
										return '<span title=' + value + '>' + statusArr[i].description + '</span>';
									}
								}
								return '<span>' + value + '</span>';
	                	  },
	                	  createfilterwidget: function (column, columnElement, widget) {
				   				var uniqueRecords2 = [] ;
				   				if(statusArr && statusArr.length > 0 ){
				   					var filterBoxAdapter2 = new $.jqx.dataAdapter(statusArr,
							                {
							                    autoBind: true
							                });
					                uniqueRecords2 = filterBoxAdapter2.records;
				   				}
				   				widget.jqxDropDownList({ filterable:true,source: uniqueRecords2, displayMember: 'description', valueMember : 'statusId'});			   				
   						},
   						cellclassname: cellclass
	                  },
	                  { text: '${uiLabelMap.BACCDescription}', width: '18%', dataField:'description', cellclassname: cellclass,
					    },
	                  { text: '${uiLabelMap.BACCTotalDiscount}', dataField: 'totalAmount', filterable: false,
	                	  cellsrenderer: function(row, columns, value){
	                		  	var data = $('#jqxgridDiscount').jqxGrid('getrowdata',row);
	                		  	if(data != undefined && data){
	                		  		return '<span class=align-right>'+formatcurrency(value, data.currencyUomId)+'</span>';
	                		  	}
	                	  },
	                	  cellclassname: cellclass,
	                  },
					 "/>

<@jqGrid id="jqxgridDiscount" filtersimplemode="true" addrefresh="true" editable="false" addType="popup" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=jqxGetListDiscountAndPromotion&invoiceType=SALES_INVOICE" dataField=dataField columnlist=columnlist
		 jqGridMinimumLibEnable="false"
		 customcontrol1="fa fa-file-excel-o open-sans@${StringUtil.wrapString(uiLabelMap.BSExportExcel)}@javascript:void(0);exportExcel()"
		 isSaveFormData="true" formData="filterObjData" mouseRightMenu="true" contextMenuId="contextMenu"
		 />
<script>
	var filterObjData = new Object();
	var exportExcel = function(){
		var dataGrid = $("#jqxgridDiscount").jqxGrid('getrows');
		if (dataGrid.length == 0) {
			jOlbUtil.alert.error("${uiLabelMap.BSNoDataToExport}");
			return false;
		}
	
		var winURL = "exportDiscountsExcel";
		var form = document.createElement("form");
		form.setAttribute("method", "POST");
		form.setAttribute("action", winURL);
		form.setAttribute("target", "_blank");
		
		var hiddenField0 = document.createElement("input");
		hiddenField0.setAttribute("type", "hidden");
		hiddenField0.setAttribute("name", "invoiceType");
		form.appendChild(hiddenField0);

		if (OlbCore.isNotEmpty(filterObjData) && OlbCore.isNotEmpty(filterObjData.data)) {
			$.each(filterObjData.data, function(key, value) {
				var hiddenField1 = document.createElement("input");
				hiddenField1.setAttribute("type", "hidden");
				hiddenField1.setAttribute("name", key);
				hiddenField1.setAttribute("value", value);
				form.appendChild(hiddenField1);
			});
		}
		
		document.body.appendChild(form);
		form.submit();
	}
</script>