<button class="btn btn-primary btn-small" id="apply">${StringUtil.wrapString(uiLabelMap.BACCCreateQuickPayment)}</button> 
<#if invoice.newStatusId?exists && invoice.newStatusId == "INV_APPR_NEW">
	<button class="btn btn-success btn-small" id="posible">${StringUtil.wrapString(uiLabelMap.BACCPossiblePayment)}</button>
</#if>
<#include "../payment/script/paymentViewScript.ftl"/>
<#include "../payment/paymentQuickNewPopup.ftl"/>
<div id="wdwPosibleApplication" style="display: none;">
	<div id="wdwHeader">
		<span>
		   ${uiLabelMap.BACCPayForInvoice}[${parameters.invoiceId}]
		</span>
	</div>
	<div id="wdwContentNew">
		<div class="basic-form form-horizontal" style="margin-top: 10px">
			<form name="formNew" id="formNew">
				<!--<div class="row-fluid">
					<div id="jqxNotification"></div>
					<div id="container" style="width : 100%"></div>
				</div>-->
				<div class="row-fluid">
					<div id="jqxgridPosiblePay"></div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="btnCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.wgcancel}</button>
					<button id="btnSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.wgok}</button>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
	var OLBApply = function(){
	}
	
	OLBApply.initGrid = function(){
		//Data fields 
		var datafields = [{ name: 'paymentId', type: 'string' },
			    	      { name: 'effectiveDate', type: 'date' },
			    	      { name: 'amount', type: 'number' },
			    	      { name: 'amountToApply', type: 'number' },
			    	      { name: 'amountApplied', type: 'number' },
			    	      { name: 'currencyUomId', type: 'string' },
 	 		 		  ];
		//Column of grid
		var columnlist = [{ text: '${uiLabelMap.BACCPaymentId}', datafield: 'paymentId', width: 150, editable: false },
			    	      { text: '${uiLabelMap.BACCEffectiveDate}', datafield: 'effectiveDate', cellsformat: 'dd/MM/yyyy', width: 150, editable: false },
			    	      { text: '${uiLabelMap.BACCAmount}', datafield: 'amount', width: 150, editable: false,
			    	    	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
			    	    		  var data = $('#jqxgridPosiblePay').jqxGrid('getrowdata', row);
					 			  return "<span>" + formatcurrency(data.amount,data.currencyUomId) + "</span>";
			    	    	  }
			    	      },
			    	      { text: '${uiLabelMap.BACCAmountToApply}', datafield: 'amountToApply', editable: true,
			    	    	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
			    	    		  var data = $('#jqxgridPosiblePay').jqxGrid('getrowdata', row);
					 			  return "<span>" + formatcurrency(data.amountToApply,data.currencyUomId) + "</span>";
			    	    	  }  
			    	      },
			    	      { text: '${uiLabelMap.BACCAmountApplied}', datafield: 'amountApplied', editable: true,columntype : 'numberinput',
			    	    	  aggregates: ['sum'],
    	    	              aggregatesrenderer: function (aggregates, column, element, summaryData) {
    	    	            	  var renderstring = "<div class='jqx-widget-content jqx-widget-content-olbius' style='float: left; width: 100%; height: 100%;'>";
    	    	            	  $.each(aggregates, function (key, value) {
    	    	            		  renderstring += '<div style="color: red; font-weight: bold; position: relative; margin: 6px; text-align: right; overflow: hidden;"> ${uiLabelMap.BACCTotal}: ' + formatcurrency(value) + '</div>';
    	                          });
    	    	            	  renderstring += "</div>";
    	    	            	  return renderstring;
    	    	              },
			    	    	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
			    	    		  var data = $('#jqxgridPosiblePay').jqxGrid('getrowdata', row);
					 			  return "<span>" + formatcurrency(data.amountApplied, data.currencyUomId) + "</span>";
			    	    	  },
			    	    	  validation : function(cell,value){
			    	    		var data = $('#jqxgridPosiblePay').jqxGrid('getrowdata', cell.row);
			    	    		if(value > data.amountToApply){
			    	    			return {result : false,message : '${StringUtil.wrapString(uiLabelMap.BACCValueIsInvalid)}'};
			    	    		}
			    	    		return true;
			    	    	  }
			    	      },
		                ];
		//Tool bar of grid
		var rendertoolbar = function (toolbar){
			var container = $("<div id='toolbarcontainer' class='widget-header'></div>");
            toolbar.append(container);
            container.append('<h4>${uiLabelMap.BACCListPaymentsNotYetApplied}</h4>');
	   	}
		//Configuration for grid
		var config = {
	   		width: '100%', 
	   		virtualmode: true,
	   		showfilterrow: true,
	   		showtoolbar: true,
	   		rendertoolbar: rendertoolbar,
	   		editmode: 'click',
	   		selectionmode: 'singlecell',
	   		pageable: true,
	   		sortable: false,
	        filterable: false,
	        editable: true,
	        rowsheight: 26,
            altrows: true,
	        localization: getLocalization(),
	        selectionmode: 'singlerow',
	        url: 'JqxGetListPaymentNotApplied&invoiceId=${parameters.invoiceId?if_exists}',
	        updateUrl: 'updateInvoiceApplication',
	        editColumns: 'paymentId;amount(java.math.BigDecimal);invoiceId[${parameters.invoiceId}];effectiveDate(java.sql.Timestamp);amountApplied(java.math.BigDecimal);amountToApply(java.math.BigDecimal)'
	   	};
		//Create grid
	   	Grid.initGrid(config, datafields, columnlist, null, $("#jqxgridPosiblePay"));
	}
	
	OLBApply.prototype.initWindow = function(){
		$('#wdwPosibleApplication').jqxWindow({showCollapseButton: false, theme: 'olbius',maxHeight: 1000, autoOpen: false, maxWidth: "75%", height: 550, minWidth: '40%', width: "90%", isModal: true, modalZIndex: 10000, cancelButton: $('#btnCancel'), collapsed:false,
		    initContent: function () {
		    	OLBApply.initGrid();
			}
		});
	}
	OLBApply.prototype.bindEvent = function(){
		/*bind event*/
		$("#posible").on('click', function(){
			$('#wdwPosibleApplication').jqxWindow('open');
			$("#jqxgridPosiblePay").jqxGrid('updatebounddata');
		});
		
		$("#apply").on('click', function(){
			paymentApplObj.openWindow();
		});
		$("#btnSave").on('click', function(){
			var rows = $('#jqxgridPosiblePay').jqxGrid('getrows');
			var listPayments = new Array();
			var index = 0;
			var submitData = {};
			for(var i = 0; i < rows.length; i++){
				if(rows[i].amountApplied != undefined && rows[i].amountApplied != 0){
					listPayments[index++] = rows[i];
				}
			}
			submitData['listPayments'] = JSON.stringify(listPayments);
			submitData['invoiceId'] = '${parameters.invoiceId}';
			//Send Ajax Request
			$.ajax({
				url: 'updateInvoiceApplication',
				type: "POST",
				data: submitData,
				dataType: 'json',
				async: false,
				success : function(data) {
					if(data._ERROR_MESSAGE_LIST_ || data._ERROR_MESSAGE_){
						if(data._ERROR_MESSAGE_LIST_){
							accutils.confirm.confirm(data._ERROR_MESSAGE_LIST_[0], function(){}, '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
						}
						if(data._ERROR_MESSAGE_){
							accutils.confirm.confirm(data._ERROR_MESSAGE_, function(){}, '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
						}
					}else{
						$('#wdwPosibleApplication').jqxWindow('close');
						$('#jqxgridInvAppl').jqxGrid('updatebounddata');
					}
				}
			});
		});
	}
</script>
