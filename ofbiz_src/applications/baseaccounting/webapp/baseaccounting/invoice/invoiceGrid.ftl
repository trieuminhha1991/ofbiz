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
								if('${businessType}' == 'AP'){
									return '<span><a href=ViewAPInvoice?invoiceId=' + data.invoiceId + ' style=\"color: blue !important;\">' + data.invoiceId + '</a></span>';
								}else{
									return '<span><a href=ViewARInvoice?invoiceId=' + data.invoiceId + ' style=\"color: blue !important;\">' + data.invoiceId + '</a></span>';
								}
							}
						},
						cellclassname: cellclass
					 },
              		 { text: '${uiLabelMap.BACCInvoiceFromParty}', dataField: 'fullNameFrom', width: '18%', pinned: true, cellclassname: cellclass},"/>
<#if businessType == "AP">
	<#assign columnlist = columnlist + "{text: '${StringUtil.wrapString(uiLabelMap.BACCOrganizationId)}', datafield: 'partyCodeFrom', width: '9%', cellclassname: cellclass, pinned: true},
	                                    { text: '${uiLabelMap.BACCInvoiceToParty}', width: '18%', dataField:'fullNameTo',  pinned: true, cellclassname: cellclass},
										"/> 
<#else>
	<#assign columnlist = columnlist + "{ text: '${uiLabelMap.BACCInvoiceToParty}', width: '18%', dataField:'fullNameTo',  pinned: true, cellclassname: cellclass},
										{text: '${StringUtil.wrapString(uiLabelMap.BACCOrganizationId)}', datafield: 'partyCode', width: '9%', cellclassname: cellclass, pinned: true},
										"/>
</#if>              		 
<#assign columnlist = columnlist + "{ text: '${uiLabelMap.BACCInvoiceTypeId}', filtertype: 'checkedlist', dataField: 'invoiceTypeId', width: '15%',
	                	  cellsrenderer: function(row, column, value){
	                		  for(var i = 0; i < dataInvoiceType.length; i++){
									if(value == dataInvoiceType[i].invoiceTypeId){
										return '<span title=' + value + '>' + dataInvoiceType[i].description + '</span>';
									}
	                		  }
	                		  return '<span>' + value + '</span>';
	                	  },
			   			  createfilterwidget: function (column, columnElement, widget) {
			   				  var uniqueRecords =  [];
			   				  if(dataInvoiceType && dataInvoiceType.length  > 0){
			   					var filterBoxAdapter = new $.jqx.dataAdapter(dataInvoiceType,
						                {
						                    autoBind: true
						                });
				                var uniqueRecords = filterBoxAdapter.records;
			   				  }
			   				  widget.jqxDropDownList({source: uniqueRecords, displayMember: 'invoiceTypeId', valueMember : 'invoiceTypeId',
				   				  renderer: function(index, label, value){
				   				  		if (dataInvoiceType.length > 0) {
											for(var i = 0; i < dataInvoiceType.length; i++){
												if(dataInvoiceType[i].invoiceTypeId == value){
													return '<span>' + dataInvoiceType[i].description + '</span>';
												}
											}
										}
										return value;
				   				  	}
			   				  });
			   				  widget.jqxDropDownList('checkAll');			   				  			   				  				   				  
			   			  },
			   			  cellclassname: cellclass
	                  },
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
				   				widget.jqxDropDownList({ filterable:true,source: uniqueRecords2, displayMember: 'statusId', valueMember : 'statusId',
				   					renderer: function(index, label, value){
				   				  		if (statusArr.length > 0) {
											for(var i = 0; i < statusArr.length; i++){
												if(statusArr[i].statusId == value){
													return '<span>' + statusArr[i].description + '</span>';
												}
											}
										}
										return value;
				   				  	}
				   				});
				   				widget.jqxDropDownList('checkAll');			   				  			   				  				   				  
				   							   				
   						},
   						cellclassname: cellclass
	                  },
	                  { text: '${uiLabelMap.BACCDescription}', dataField: 'description', width: '18%', cellclassname: cellclass},
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
	                  { text: '${uiLabelMap.BACCAmountToApply}', dataField: 'amountToApply', width: '15%',filterable: false,
	                	  cellsrenderer: function(row, columns, value){
	                		  	var data = $('#jqxgridInvoice').jqxGrid('getrowdata',row);
	                		  	if(data != undefined && data){
	                		  		return '<span class=align-right>'+formatcurrency(value, data.currencyUomId)+'</span>';
	                		  	}
	                	  },
	                	  cellclassname: cellclass
	                  },
					 "/>

<@jqGrid id="jqxgridInvoice" filtersimplemode="true" addrefresh="true" editable="false" addType="popup" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JqxGetListInvoices&invoiceType=${businessType}" dataField=dataField columnlist=columnlist
		 jqGridMinimumLibEnable="false" filterable="true"
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