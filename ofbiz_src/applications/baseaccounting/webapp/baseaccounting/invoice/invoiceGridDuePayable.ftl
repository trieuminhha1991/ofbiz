<link rel="stylesheet" type="text/css" href="/accresources/css/grid.css">
<style>
.zIndex999999{
	z-index: 999999 !important
}
</style>
<script>
<#assign listInvoiceType = Static["javolution.util.FastList"].newInstance() />
<#if businessType == "AP">
	${Static["com.olbius.acc.utils.UtilServices"].getAllInvoiceType(delegator,listInvoiceType,"PURCHASE_INVOICE")}
<#else>
	${Static["com.olbius.acc.utils.UtilServices"].getAllInvoiceType(delegator,listInvoiceType,"SALES_INVOICE")}
</#if>
	//Prepare Data
	var dataInvoiceType = [
       <#if listInvoiceType?exists>
           	<#list listInvoiceType as str>
           	<#assign type = delegator.findOne("InvoiceType",{"invoiceTypeId" : "${str?if_exists}"},false) !>
           		{
           			invoiceTypeId : "${type.invoiceTypeId}",
           			description : "${StringUtil.wrapString(type.get('description',locale))}"
   				},
       		</#list>
   		</#if>
	];
	
	var dataStatusType = [
      <#if listStatusItem?exists>
      	<#list listStatusItem as type>
      		{
      			statusId : "${type.statusId}",
      			description : "${StringUtil.wrapString(type.get('description',locale))}",
  			},
  		</#list>
  	  </#if>
	];
	
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
	var cellclass = function (row, columnfield, value) {
		var data = $('#jqxgridInvoice').jqxGrid('getrowdata', row);
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
<#--===================================/Prepare Data=====================================================-->
<#--=================================Init Grid======================================================-->
<#assign dataField="[{ name: 'invoiceId', type: 'string' },
					 { name: 'invoiceTypeId', type: 'string'},
					 { name: 'invoiceDate', type: 'date', other:'Timestamp'},
					 { name: 'verifiedDate', type: 'date', other:'Timestamp'},
					 { name: 'statusId', type: 'string'},
					 { name: 'newStatusId', type: 'string'},
					 { name: 'description', type: 'string'},
					 { name: 'partyIdFrom', type: 'string'},
					 { name: 'partyCodeFrom', type: 'string'},
					 { name: 'partyId', type: 'string'},
					 { name: 'partyCode', type: 'string'},
					 { name: 'total', type: 'number'},
					 { name: 'currencyUomId', type: 'string'},
					 { name: 'amountToApply', type: 'number'},
					 { name: 'fullNameTo', type: 'string'},
					 { name: 'fullNameFrom', type: 'string'},
					 { name: 'payrollAmount', type: 'number'},
					 { name: 'dueDate', type: 'date', other:'Timestamp'},
				 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.BACCInvoiceId}', dataField: 'invoiceId', width: '9%', pinned: true,
						cellsrenderer: function(row, column, value){
							var data = $('#jqxgridInvoice').jqxGrid('getrowdata', row);
							if(data != undefined && data){
                                return '<span><a href=ViewAPInvoice?selectedItemMenu=APInvoiceListDuePayable&invoiceId=' + data.invoiceId + ' style=\"color: blue !important;\">' + data.invoiceId + '</a></span>';
							}}
						},"/>
<#assign columnlist = columnlist + "{ text: '${uiLabelMap.BACCInvoiceFromParty}', width: '18%', dataField:'fullNameFrom',  pinned: true, cellclassname: cellclass,
					    cellsrenderer: function (row, column, value){
					        var data = $('#jqxgridInvoice').jqxGrid('getrowdata', row);
					        return '<span><a href=\"viewSupplier?partyId=' + data.partyIdFrom + '\">' + value + '</a></span>';
					    }},"/>
<#assign columnlist = columnlist + "
	                  { text: '${uiLabelMap.BACCInvoiceDate}', dataField: 'invoiceDate', width: '14%', cellsformat: 'dd/MM/yyyy HH:mm:ss', filtertype: 'range', cellclassname: cellclass},
                      { text: '${uiLabelMap.BACCDueDate}', dataField: 'dueDate', width: '14%', cellsformat: 'dd/MM/yyyy HH:mm:ss', filtertype: 'range', cellclassname: cellclass},
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
	                  { text: '${uiLabelMap.BACCTotal}', dataField: 'total', width: '12%',filterable: false,
	                	  cellsrenderer: function(row, columns, value){
	                		  var data = $('#jqxgridInvoice').jqxGrid('getrowdata',row);
	                		  if(data != undefined && data){
	                			  if(data.invoiceTypeId == 'PAYROL_INVOICE'){
	                				  return '<span class=align-right>' + formatcurrency(value, data.currencyUomId) + '( ${uiLabelMap.BACCEmplPayroll} ' + formatcurrency(data.payrollAmount, data.currencyUomId) + ')' + '</span>'
	                			  }
	                			  return '<span class=align-right>'+formatcurrency(value, data.currencyUomId)+'</span>';
	                		  }
	                	  },
	                	  cellclassname: cellclass
	                  },
	                  { text: '${uiLabelMap.BACCAmountToApply}', dataField: 'amountToApply', filterable: false,
	                	  cellsrenderer: function(row, columns, value){
	                		  	var data = $('#jqxgridInvoice').jqxGrid('getrowdata',row);
	                		  	if(data != undefined && data){
	                		  		return '<span class=align-right>'+formatcurrency(value, data.currencyUomId)+'</span>';
	                		  	}
	                	  },
	                	  cellclassname: cellclass,
                         aggregatesrenderer: function (aggregates, column, element) {
                        	var renderstring = \"<div class='jqx-widget-content jqx-widget-content-\" + theme + \"' style='float: left; width: 100%; height: 100%; '>\";
                        	var total = 0;
                        	if (totalMap && totalMap.totalAmount) {
                        		total = totalMap.totalAmount;
                        	}
                        	var data = $('#jqxgridInvoice').jqxGrid('getrows');
                        	var currencyUomId = 'VND';
                        	if (data && data.length > 0) {
                        		currencyUomId = data[0].currencyId;
                        	}
                          	renderstring += '<div style=\"position: relative; margin: 6px; text-align: right; overflow: hidden;\"><b>' + '${uiLabelMap.BACCTotal}' + ': ' + formatcurrency(total, currencyUomId) + '</b></div>';
                          	renderstring += \"</div>\";
                          	return renderstring;
                      	}
	                  },
					 "/>

<@jqGrid id="jqxgridInvoice" filtersimplemode="true" addrefresh="true" editable="false" addType="popup" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=jqxGetListInvoiceByDueDate&invoiceType=${businessType}&fromDueDate=${monthStart}&toDueDate=${monthEnd}" dataField=dataField columnlist=columnlist
		 jqGridMinimumLibEnable="false"
         displayTotal="true" showstatusbar="true" statusbarheight="30"
		 customcontrol1="fa fa-file-excel-o open-sans@${StringUtil.wrapString(uiLabelMap.BSExportExcel)}@javascript:void(0);exportExcel()"
		 isSaveFormData="true" formData="filterObjData" mouseRightMenu="true" contextMenuId="contextMenu"
		 />

<div id='contextMenu' class="hide">
	<ul>
		<li action="viewVoucher">
			<i class="fa-file-text-o open-sans"></i>${StringUtil.wrapString(uiLabelMap.ListVoucherInvoice)}
        </li>        
	</ul>
</div>

<div id='voucherListWindow' class="hide">
	<div>${StringUtil.wrapString(uiLabelMap.ListVouchers)}</div>
	<div class='form-window-container' style="position: relative;">
		<div class='form-window-content'>
			<div id="voucherListGrid"></div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="closeViewVocherList">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>	
		</div>
	</div>
</div>    

<a class="hide" data-rel="colorbox" href="javascript:void(0)" id="viewImgVoucher">asdasd</a>                 
<script type="text/javascript" src="/accresources/js/invoice/invoiceList.js"></script>
<script>
	var filterObjData = new Object();
	var exportExcel = function(){
		var dataGrid = $("#jqxgridInvoice").jqxGrid('getrows');
		if (dataGrid.length == 0) {
			jOlbUtil.alert.error("${uiLabelMap.BSNoDataToExport}");
			return false;
		}
	
		var winURL = "exportInvoicesExcel";
		var form = document.createElement("form");
		form.setAttribute("method", "POST");
		form.setAttribute("action", winURL);
		form.setAttribute("target", "_blank");
		
		var hiddenField0 = document.createElement("input");
		hiddenField0.setAttribute("type", "hidden");
		hiddenField0.setAttribute("name", "invoiceType");
		hiddenField0.setAttribute("value", "${businessType}");
		form.appendChild(hiddenField0);

        var fromDueDate = $("#fromDueDate").jqxDateTimeInput('getDate').getTime();
        var toDueDate = $("#toDueDate").jqxDateTimeInput('getDate').getTime();

        var fromDueDateField = document.createElement("input");
        fromDueDateField.setAttribute("type", "hidden");
        fromDueDateField.setAttribute("name", "fromDueDate");
        fromDueDateField.setAttribute("value", fromDueDate);
        form.appendChild(fromDueDateField);

        var toDueDateField = document.createElement("input");
        toDueDateField.setAttribute("type", "hidden");
        toDueDateField.setAttribute("name", "toDueDate");
        toDueDateField.setAttribute("value", toDueDate);
        form.appendChild(toDueDateField);

        var statusField = document.createElement("input");
        statusField.setAttribute("type", "hidden");
        statusField.setAttribute("name", "status");
        statusField.setAttribute("value", "INVOICE_PAID,");
        form.appendChild(statusField);
		
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

<script>
    $('#jqxgridInvoice').on("bindingcomplete", function (event) {
        var fromDueDate = $("#fromDueDate").jqxDateTimeInput('getDate').getTime();
        var toDueDate = $("#toDueDate").jqxDateTimeInput('getDate').getTime();
        var invoiceType = "PURCHASE_INVOICE";
        jQuery.ajax({
            url: 'getTotalAmountUnPaidInTime',
            async: false,
            type: 'POST',
            data: {
                fromDueDate: fromDueDate,
                toDueDate: toDueDate,
                invoiceType: invoiceType
            },
            success: function (data) {
                totalMap.totalAmount = data.totalAmount;
            }
        });
    });
</script>