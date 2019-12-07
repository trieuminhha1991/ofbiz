<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/assets/jsdelys/formatCurrency.js"></script>
<#assign datafield = "[{name: 'emplTerminationProposalId', type: 'string'},
					   {name: 'terminationTypeId', type: 'string'},
					   {name: 'partyId', type: 'string'},
					   {name: 'fullName', type: 'string'},
					   {name: 'dateTermination', type: 'date'},
					   {name: 'statusId', type: 'string'},
					   {name: 'terminationReasonId', type: 'string'},
					   {name: 'comment', type: 'string'},
					   {name: 'otherReason', type: 'string'},
					   {name: 'createdDate', type: 'date'}]"/>
<script type="text/javascript">
var statusArr = [
	<#list statusList as status>
		{
			statusId: "${status.statusId}",
			description: "${StringUtil.wrapString(status.description?if_exists)}"
		},
	</#list>             
];

var statusArrTransferAsset = [
	<#list statusTransferAssetList as status >
		{
			'statusId': '${status.statusId?if_exists}',
			'description': '${status.description?if_exists}' 	
		},
	</#list>                 
];

var dataStatusType = [
	<#if listStatusItem?exists>
		<#list listStatusItem as type>
			{statusId : "${type.statusId}",description : "${StringUtil.wrapString(type.description)}"},
		</#list>
	</#if>
];

var dataInvoiceType = [
	<#list listInvoiceType as item>
		<#assign description = item.get("description", locale)/>
		{
			invoiceTypeId: '${item.invoiceTypeId}',
			description: "${description}"
		},
	</#list>                       
];

var dataPaymentType = [
		<#list 0..(listPaymentType.size() - 1) as i>
		{
			paymentTypeId:"${listPaymentType.get(i).paymentTypeId}",
			description: "${StringUtil.wrapString(listPaymentType.get(i).description?if_exists)}"	
		},
		</#list>	                       
];

var dataStatusPaymentItem = [
	<#list listStatusItemPayment as status >
	{
		'statusId': '${status.statusId?if_exists}',
		'description': '${StringUtil.wrapString(status.description?if_exists)}' 	
	},
	</#list>                             
];

var terminationReasonArr = [
	<#list terminationReasonList as termination>
		{
			terminationReasonId: "${termination.terminationReasonId}",
			description: "${StringUtil.wrapString(termination.description?if_exists)}"
		},
	</#list>
];	
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyId', width: 120},
						{text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'fullName', width: 150,
						},
						{text: '${StringUtil.wrapString(uiLabelMap.HREmplReasonResign)}', datafield: 'terminationReasonId', width: 200,
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								for(var i = 0; i < terminationReasonArr.length; i++){
									if(value == terminationReasonArr[i].terminationReasonId){
										return '<span title=' + value + '>' + terminationReasonArr[i].description + '</span>';
									}
								}
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								if(data.otherReason){
									return '<span>' + data.otherReason + '</span>';									
								}
								return '<span>' + value + '</span>';
							}	
						},
						{text: '${StringUtil.wrapString(uiLabelMap.HREmplResignDate)}', datafield: 'dateTermination', width: 130, editable: false, cellsformat: 'dd/MM/yyyy'},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonStatus)}', datafield: 'statusId', width: 130, editable: false,
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								for(var i = 0; i < statusArr.length; i++){
									if(value == statusArr[i].statusId){
										return '<span title=' + value + '>' + statusArr[i].description + '</span>';
									}
								}
								return '<span>' + value + '</span>';
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.DateApplication)}', datafield: 'createdDate', width: 130, editable: false, cellsformat: 'dd/MM/yyyy'},
						{text: '${StringUtil.wrapString(uiLabelMap.HRNotes)}', datafield: 'comment'},
						{datafield: 'emplTerminationProposalId', hidden: true},
						{datafield: 'otherReason', hidden: true},
						{datafield: 'terminationTypeId', hidden: true}"/>
	
							
	$(document).ready(function(){
		var jqxWindow = $("#emplTerminationPpsl");
		initJqxWindow();
		$('#jqxgrid').on('rowDoubleClick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var data = $('#jqxgrid').jqxGrid('getrowdata', boundIndex);
			fillDataInWindow(data);
			var invoiceSource = $("#invoicePaid").jqxGrid('source');
			invoiceSource._source.data = {emplTerminationProposalId: data.emplTerminationProposalId};
			$("#invoicePaid").jqxGrid('source', invoiceSource);
			
			var paymentSource = $("#paymentPaid").jqxGrid('source');
			paymentSource._source.data = {emplTerminationProposalId: data.emplTerminationProposalId};
			$("#paymentPaid").jqxGrid('source', paymentSource);
			
			var fixedAssetSource = $("#transferredAssetJqx").jqxGrid('source');
			fixedAssetSource._source.data = {emplTerminationProposalId: data.emplTerminationProposalId};
			$("#transferredAssetJqx").jqxGrid('source', fixedAssetSource);
			openJqxWindow(jqxWindow);
		}); 
		createInvoiceJqxGrid();
		createPaymentJqxGrid();
		createTransferredAssetJqxGrid();
		addBtnEvent();
	});	
	
	function createInvoiceJqxGrid(){
		var invoiceDataFieldSource = {
				datafields: [
					{name: 'invoiceId', type: 'string'},
					{name: 'invoiceTypeId', type: 'string'},
					{name: 'partyIdFrom', type: 'string'},
					{name: 'invoiceDate', type: 'date', other:'Timestamp'},
					{name: 'statusId', type: 'string'},
					{name: 'total', type: 'number'},
					{name: 'currencyUomId', type: 'string'},
					{name: 'amountToApply', type: 'number'},
					{name: 'description', type: 'string'}				             
				],
				cache: false,
        		datatype: 'json',
        		type: 'POST',
        		data: {},
        		url: 'getPartyInvoicePaid',
        		id: 'invoiceId',
        		beforeprocessing: function (data) {
        			invoiceDataFieldSource.totalrecords = data.TotalRows;
		        },
		        pagenum: 0,
		        pagesize: 15,
		        root: 'listReturn'
		};
		var invoiceDataFieldAdapter = new $.jqx.dataAdapter(invoiceDataFieldSource);
		
		var invoiceDataColumn = [
			{ text: '${uiLabelMap.FormFieldTitle_invoiceId}', width:100, datafield: 'invoiceId', cellsrenderer:
			 	function(row, colum, value)
			    {
			    	var data = $('#invoicePaid').jqxGrid('getrowdata', row);
			    	return "<span><a href='/delys/control/accArinvoiceOverview?invoiceId=" + data.invoiceId + "'>" + data.invoiceId + "</a></span>";
			    }
			},
			{text: '${uiLabelMap.accAccountingFromParty}', filtertype: 'olbiusdropgrid', width:180, datafield: 'partyIdFrom', 
				cellsrenderer: function(row, colum, value){
						return "<span>" + value + "</span>";
					},
				},
				{text: '${uiLabelMap.FormFieldTitle_invoiceTypeId}', filtertype: 'checkedlist', width:130, datafield: 'invoiceTypeId', 
					cellsrenderer: function(row, colum, value)
			    {
			    	for(i=0; i < dataInvoiceType.length;i++){
			    		if(value==dataInvoiceType[i].invoiceTypeId){
			    			return "<span>" + dataInvoiceType[i].description + "</span>";
			    		}
			    	}
			    	return "<span>" + value + "</span>";
			    }
				},
				{text: '${uiLabelMap.FormFieldTitle_invoiceDate}', width:130, datafield: 'invoiceDate', cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput',
					cellsrenderer: function(row, colum, value){
						if(value){
							var date = new Date(value);
							return '<span>' + date.getDate() + "/" + (date.getMonth() + 1) + "/" + date.getFullYear() + '</span>';
						}
					}	
				},
				{ text: '${uiLabelMap.CommonStatus}', filtertype: 'checkedlist', width:120, datafield: 'statusId', 
					cellsrenderer: function(row, colum, value){
			    	for(i=0; i < dataStatusType.length;i++){
			    		if(value==dataStatusType[i].statusId){
			    			return "<span>" + dataStatusType[i].description + "</span>";
			    		}
			    	}
			    	return value;
			    }
			},
			{text: '${uiLabelMap.description}', width:150, datafield: 'description'},
			{text: '${uiLabelMap.FormFieldTitle_total}', sortable:false, filterable: false, width:120, datafield: 'total', 
				cellsrenderer: function(row, colum, value){
			 		var data = $('#invoicePaid').jqxGrid('getrowdata', row);
			 		return "<span>" + formatcurrency(data.total,data.currencyUomId) + "</span>";
			 	}
			},
			 {text: '${uiLabelMap.FormFieldTitle_amountToApply}', sortable:false, filterable: false, datafield: 'amountToApply', 
					cellsrenderer: function(row, colum, value){
						var data = $('#invoicePaid').jqxGrid('getrowdata', row);
						return "<span>" + formatcurrency(data.amountToApply,data.currencyUomId) + "</span>";
					}
			 }		                         
		];
		initJqxGrid($("#invoicePaid"), invoiceDataFieldAdapter, invoiceDataColumn,true, "${uiLabelMap.InvoiceMustPaid}");
	}
	
	function createPaymentJqxGrid(){
		var source = {
				datafield: [{ name: 'paymentId', type: 'string' },
							{ name: 'paymentTypeId', type: 'string'},
							{ name: 'statusId', type: 'string'},
							{ name: 'comments', type: 'string'},
							{ name: 'partyIdTo', type: 'string'},
							{ name: 'effectiveDate', type: 'date'},
							{ name: 'amount', type: 'number'},
							{ name: 'amountToApply', type: 'number'},
							{ name: 'currencyUomId', type: 'string'}
				],
				cache: false,
        		datatype: 'json',
        		type: 'POST',
        		url: 'getPartyPaymentPaid',
        		id: 'paymentId',
        		data: {},
        		beforeprocessing: function (data) {
        			source.totalrecords = data.TotalRows;
		        },
		        pagenum: 0,
		        pagesize: 15,
		        root: 'listReturn'
		};
		var dataAdapter = new $.jqx.dataAdapter(source);
		var columnlist = [
				{ text: '${uiLabelMap.FormFieldTitle_paymentId}', width:100, filtertype:'input', datafield: 'paymentId', 
					cellsrenderer: function(row, colum, value){
							return "<span><a href='/delys/control/accArpaymentOverview?paymentId=" + value + "'>" + value + "</a></span>";
						}
					},
				{text: '${uiLabelMap.OrderPaymentType}', filtertype: 'checkedlist', width:200, datafield: 'paymentTypeId', 
						cellsrenderer: function(row, colum, value){
				   		for(i=0; i < dataPaymentType.length;i++){
				   			if(dataPaymentType[i].paymentTypeId==value){
				   				return "<span>" + dataPaymentType[i].description +"</span>";
								}
				   		}
				   		return "<span>" + value + "</span>";
				   }
				},
				{text: '${uiLabelMap.FormFieldTitle_statusId}', filtertype: 'checkedlist', width: 130, datafield: 'statusId', 
					cellsrenderer: function(row, colum, value){
				       	for(i=0; i < dataStatusPaymentItem.length;i++){
				       		if(dataStatusPaymentItem[i].statusId==value){
				       			return "<span>" + dataStatusPaymentItem[i].description +"</span>";
				   			}
				       	}
				       	return "<span>" + value + "</span>";
				    },
						
				},
				{text: '${uiLabelMap.HRNotes}', filtertype:'input', width:130, datafield: 'comments'},
				{text: '${uiLabelMap.accAccountingToParty}', filtertype: 'olbiusdropgrid', datafield: 'partyIdFrom', hidden: true},
				{text: '${uiLabelMap.accAccountingFromParty}', filtertype: 'olbiusdropgrid', width:150, datafield: 'partyIdTo'},
				{text: '${uiLabelMap.FormFieldTitle_effectiveDate}', filtertype: 'range', hidden:true, datafield: 'effectiveDate', cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput',
					cellsrenderer: function(row, colum, value){
						if(value){
							var date = new Date(value);
							return '<span>' + date.getDate() + "/" + (date.getMonth() + 1) + "/" + date.getFullYear() + '</span>';
						}
					}	
				},
				{text: '${uiLabelMap.DAAmount}', sortable: false, filterable: false, width:130, datafield: 'amount', cellsrenderer:
				 	function(row, colum, value){
				 		var data = $('#paymentPaid').jqxGrid('getrowdata', row);
				 		return "<span>" + formatcurrency(data.amount,data.currencyUomId) + "</span>";
				 	}
				},
				{text: '${uiLabelMap.FormFieldTitle_amountToApply}', sortable: false, filterable: false, datafield: 'amountToApply', 
						cellsrenderer: function(row, colum, value){
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							return "<span>" + formatcurrency(data.amountToApply,data.currencyUomId) + "</span>";
						}
				}		                  
		];
		initJqxGrid($("#paymentPaid"), dataAdapter, columnlist, true, "${uiLabelMap.PaymentMustPaid}");
	}
	
	function createTransferredAssetJqxGrid(){
		var source = {
				datafield: [
					{name: 'partyId', type: 'string'},
					{name: 'roleTypeId', type: 'string'},
					{name: 'fixedAssetId', type: 'string'},
					{name: 'fixedAssetName', type: 'string'},
					{name: 'fromDate', type: 'date'},
					{name: 'thruDate', type: 'date'},
					{name: 'statusId', type: 'string'},
					{name: 'comments', type: 'string'}
				],
				cache: false,
        		datatype: 'json',
        		type: 'POST',
        		url: 'getPartyAssetAssignment',
        		id: 'id',
        		data: {},
        		beforeprocessing: function (data) {
        			source.totalrecords = data.TotalRows;
		        },
		        pagenum: 0,
		        pagesize: 15,
		        root: 'listReturn'
		};
		var dataAdapter = new $.jqx.dataAdapter(source);
		var columnlist = [
				{datafield: 'partyId', hidden: true},
				{datafield: 'fixedAssetId', text: '${uiLabelMap.fixedAssetId}', editable: false, width: 130},  
				{datafield: 'fixedAssetName', text: '${uiLabelMap.fixedAssetName}', editable: false, width: 150},
				{datafield: 'roleTypeId', text: '${uiLabelMap.CommonRole}', editable: false, width: 150},
				{datafield: 'statusId', text: '${uiLabelMap.CommonStatus}', editable: false, width: 150,
					cellsrenderer: function(row, colum, value){
						for(i = 0 ; i < statusArrTransferAsset.length; i++){
							if(value == statusArrTransferAsset[i].statusId){
								return '<span title=' + value +'>' + statusArrTransferAsset[i].description + '</span>';
							}
						}
						return '<span title=' + value +'>' + value + '</span>';
					}	
				},
				{datafield: 'fromDate', text: '${uiLabelMap.DateHandover}', editable: false, width:150, cellsformat: 'dd/MM/yyyy',  
					cellsrenderer: function(row, colum, value){
						if(value){
							var date = new Date(value);
							return '<span>' + date.getDate() + "/" + (date.getMonth() + 1) + "/" + date.getFullYear() + '</span>';
						}
					}
				},
				{datafield: 'thruDate', text: '${uiLabelMap.CommonThruDate}', editable: false, width:150, cellsformat: 'dd/MM/yyyy', 
					cellsrenderer: function(row, colum, value){
						if(value){
							var date = new Date(value);
							return '<span>' + date.getDate() + "/" + (date.getMonth() + 1) + "/" + date.getFullYear() + '</span>';
						}
					}
				},
				{datafield: 'comments', text: '${uiLabelMap.HRNotes}', editable: false}
		];
		initJqxGrid($("#transferredAssetJqx"), dataAdapter, columnlist, false, "");
	}
	
	function initJqxGrid(gridEle, gridAdapter, gridColumn, showToolbar, title){
		gridEle.jqxGrid({
			source: gridAdapter, 
    		width: '100%', 
    		autoheight: true,
    		virtualmode: true,
    		showtoolbar: showToolbar,
    		rendertoolbar: function (toolbar) {
    			var container = $("<div id='toolbarcontainer' class='widget-header'><h4>" + title + "</h4></div>");
    			toolbar.append(container);
    		},
    		rendergridrows: function () {
	            return gridAdapter.records;
	        },
	        pageSizeOptions: ['15', '30', '50', '100'],
	        pagerMode: 'advanced',
	        columnsResize: true,
	        pageable: true,
	        editable: false,
	        columns: gridColumn,
	        selectionmode: 'singlerow',
	        theme: 'olbius'
		});
	}
	
	function fillDataInWindow(data){
		var emplTerminationProposalId = data.emplTerminationProposalId;
		var partyId = data.partyId;
		$("#employeeId").text(partyId);		
		$("#employeeName").text(data.fullName);
		var reason = data.terminationReasonId;
		for(var i = 0; i < terminationReasonArr.length; i++){
			if(reason == terminationReasonArr[i].terminationReasonId){
				reason = terminationReasonArr[i].description;
			}
		}
		$("#reasonResign").text(reason);
		var resignDate = data.dateTermination;
		$("#resignDate").text(resignDate.getDate() + "/" + (resignDate.getMonth()+ 1) + "/" + resignDate.getFullYear());
		if(data.comment){
			$("#comment").html(data.comment);
		}
		$("#emplTerminationPpsl").jqxWindow({ disabled:true });
		$("#notApproval").attr("disabled", "disabled");
		$("#approval").attr("disabled", "disabled");
		$.ajax({
			url: "getEmplTerminationPpslInfo",
			data: {emplTerminationProposalId: emplTerminationProposalId},
			type: 'POST',
			success: function(data){
				if(data.responseMessage == "success"){
					if(data.agreementId){
						$("#agreementId").text(data.agreementId);
					}
					if(data.gender == "M"){
						$("#gender").text("${StringUtil.wrapString(uiLabelMap.CommonMale)}")	
					}else if(data.gender == "F"){
						$("#gender").text("${StringUtil.wrapString(uiLabelMap.CommonFemale)}")
					}
					if(data.birthDate){
						var birthDate = new Date(data.birthDate);
						$("#birthDate").text(birthDate.getDate() + "/" + (birthDate.getMonth() + 1) + "/" + birthDate.getFullYear());
					}
					if(data.department){
						$("#department").text(data.department);
					}
					if(data.emplPositionType){
						$("#emplPositionTypeId").text(data.emplPositionType);
					}
					if(data.dateJoinCompany){
						var dateJoinCompany = new Date(data.dateJoinCompany);
						$("#dateJoinCompany").text(dateJoinCompany.getDate() + "/" + (dateJoinCompany.getMonth() + 1) + "/" + dateJoinCompany.getFullYear());
					}
					if(data.agreementId){
						$("#agreementId").text(data.agreementId);
					}
					if(data.fromDate){
						var fromDateAgreement = new Date(data.fromDate);
						$("#fromDate").text(fromDateAgreement.getDate() + "/" + (fromDateAgreement.getMonth() + 1) + "/" + fromDateAgreement.getFullYear());
					}
					if(data.thruDate){
						var thruDateAgreement = new Date(data.thruDate);
						$("#thruDate").text(thruDateAgreement.getDate() + "/" + (thruDateAgreement.getMonth() + 1) + "/" + thruDateAgreement.getFullYear());
					}
				}else{
					bootbox.dialog(data.errorMessage,[
						{
							"label": "${uiLabelMap.CommonSubmit}",
							"class" : "icon-ok btn btn-mini btn-primary"
						}					                                  
					]);
				}
			},
			complete: function(jqXHR, textStatus){
				$("#emplTerminationPpsl").jqxWindow({ disabled:false });
				$("#notApproval").removeAttr("disabled");
				$("#approval").removeAttr("disabled");
			}
		});
	}
	
	function clearDataInWindow(){
		$("#agreementId").text("${StringUtil.wrapString(uiLabelMap.HRCommonNotSetting)}");
		$("#employeeId").text("${StringUtil.wrapString(uiLabelMap.HRCommonNotSetting)}");
		$("#employeeName").text("${StringUtil.wrapString(uiLabelMap.HRCommonNotSetting)}");
		$("#reasonResign").text("${StringUtil.wrapString(uiLabelMap.HRCommonNotSetting)})");
		$("#resignDate").text("${StringUtil.wrapString(uiLabelMap.HRCommonNotSetting)}");
		$("#comment").text("${StringUtil.wrapString(uiLabelMap.HRCommonNotSetting)}");
		$("#gender").text("${StringUtil.wrapString(uiLabelMap.HRCommonNotSetting)}");
		$("#birthDate").text("${StringUtil.wrapString(uiLabelMap.HRCommonNotSetting)}");
		$("#department").text("${StringUtil.wrapString(uiLabelMap.HRCommonNotSetting)}");
		$("#emplPositionTypeId").text("${StringUtil.wrapString(uiLabelMap.HRCommonNotSetting)}");
		$("#dateJoinCompany").text("${StringUtil.wrapString(uiLabelMap.HRCommonNotSetting)}");
		$("#agreementId").text("${StringUtil.wrapString(uiLabelMap.HRCommonNotSetting)}");
		$("#fromDate").text("${StringUtil.wrapString(uiLabelMap.HRCommonNotSetting)}");
		$("#thruDate").text("${StringUtil.wrapString(uiLabelMap.HRCommonNotSetting)}");
	}
	
	function openJqxWindow(jqxWindowDiv){
		var wtmp = window;
		var tmpwidth = jqxWindowDiv.jqxWindow('width');
		jqxWindowDiv.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 } });
		jqxWindowDiv.jqxWindow('open');
	}
	
	function initJqxWindow(){
		var jqxWindow = $("#emplTerminationPpsl");
		jqxWindow.jqxWindow({
			showCollapseButton: false, isModal:true, maxHeight: 500, minHeight: 500, height: 500, width: 900, theme:'olbius',
	        autoOpen: false,
	        initContent: function () {
	        	initJqxTabs();
	        	$("#jqxNotificationPpsl").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: false, template: "info", appendContainer: "#notifyContainer"});
	        }
		});
		jqxWindow.on('close', function(event){
			clearDataInWindow();
		});
	}		
	
	function initJqxTabs(){
		$('#jqxTab').jqxTabs({ height: '99%', width:  '100%', theme: 'olbius'});
	}
	
	function addBtnEvent(){
		<#if parameters.requestId?exists>
			$("#approval").click(function(event){
				//var rowindex = $('#jqxgrid').jqxGrid('getselectedrowindex');
				var data = $('#jqxgrid').jqxGrid('getrowdatabyid', parameters.requestId);
				$("#approval").attr("disabled", "disabled");
				$.ajax({
					url: 'approvalTerminationProposal',
					data: {
							emplTerminationProposalId: data.emplTerminationProposalId, 
							actionTypeId: 'APPROVE', requestId: "${parameters.requestId}",
							ntfId: "${parameters.ntfId?if_exists}"
						   },
					type:'POST',
					success: function(data){
						$("#jqxNotificationPpsl").jqxNotification('closeLast');
						if(data.responseMessage == "success"){
							$("#submitAction").hide();
							$("#jqxNotificationContentPpsl").text(data.successMessage);
        					$("#jqxNotificationPpsl").jqxNotification({template: 'info'});
        					$("#jqxNotificationPpsl").jqxNotification("open");
						}else{
							$("#jqxNotificationContentPpsl").text(data.errorMessage);
        					$("#jqxNotificationPpsl").jqxNotification({template: 'error'});
        					$("#jqxNotificationPpsl").jqxNotification("open");
						}		
					},
					complete: function(jqXHR, textStatus){
						$("#approval").removeAttr("disabled");
					}
				});
			});
			$("#notApproval").click(function(event){
				//var rowindex = $('#jqxgrid').jqxGrid('getselectedrowindex');
				var data = $('#jqxgrid').jqxGrid('getrowdatabyid', parameters.requestId);
				$("#notApproval").attr("disabled", "disabled");
				$.ajax({
					url: 'approvalTerminationProposal',
					data: {emplTerminationProposalId: data.emplTerminationProposalId, actionTypeId: 'DENY', requestId: "${parameters.requestId}", ntfId: "${parameters.ntfId?if_exists}"},
					type:'POST',
					success: function(data){
						$("#jqxNotification").jqxNotification('closeLast');
						if(data.responseMessage == "success"){
							$("#submitAction").hide();	
							$("#jqxNotificationContentPpsl").text(data.successMessage);
        					$("#jqxNotificationPpsl").jqxNotification({template: 'info'});
        					$("#jqxNotificationPpsl").jqxNotification("open");
						}else{
							$("#jqxNotificationContentPpsl").text(data.errorMessage);
        					$("#jqxNotificationPpsl").jqxNotification({template: 'error'});
        					$("#jqxNotificationPpsl").jqxNotification("open");
						}		
					},
					complete: function(jqXHR, textStatus){
						$("#notApproval").removeAttr("disabled");
					}
				});
			});
		</#if>
	}
</script>
<@jqGrid filtersimplemode="true" addType="popup" dataField=datafield columnlist=columnlist 
		clearfilteringbutton="false" showtoolbar="true" sourceId="emplTerminationProposalId"
		filterable="false" deleterow="false" editable="false" addrow="false"
		url="jqxGeneralServicer?hasrequest=Y&sname=getEmplTerminationPpslList" id="jqxgrid"
		removeUrl="" deleteColumn="" updateUrl="" editColumns="" jqGridMinimumLibEnable="false" />	
<script type="text/javascript">
$(document).ready(function () {
	<#if parameters.requestId?exists>
		
		$("#jqxgrid").on("bindingComplete", function (event) {
			var data = $('#jqxgrid').jqxGrid('getrowdatabyid', ${parameters.requestId});			
			if(data){
				fillDataInWindow(data);
				openJqxWindow($("#emplTerminationPpsl"));			
			}
		});
	</#if>	
});

</script>		
<div class="row-fluid">
	<div id="emplTerminationPpsl" class="hide">
		<div>${uiLabelMap.PageTitleResignationApplication}</div>
		<div class="form-window-container">
				<div id="notifyContainer">
					<div id="jqxNotificationPpsl">
						<div id="jqxNotificationContentPpsl"></div>
					</div>
				</div>
			
			<div class="row-fluid">
				<div id="jqxTab">
					<ul >
	                    <li><b>${uiLabelMap.TerminationInfo}</b></li>
	                    <li><b>${uiLabelMap.Invoice}</b></li>
	                    <li><b>${uiLabelMap.PaymentInfo}</b></li>
	                    <li><b>${uiLabelMap.TransferredAsset}</b></li>
	                </ul>
	                <div id="TerminationInfo"  style="margin-top: 20px">
	                	<div class="row-fluid">
	                		<div class="span12">
			                	<div class="span6">
			                		<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											${uiLabelMap.EmployeeId}:
										</div>
										<div class="span7">
											<span id="employeeId"></span>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											${uiLabelMap.EmployeeName}:
										</div>
										<div class="span7">
											<span id="employeeName"></span>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											${uiLabelMap.gender}:
										</div>
										<div class="span7">
											<span id="gender"></span>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											${uiLabelMap.DABirthday}:
										</div>
										<div class="span7">
											<span id="birthDate"></span>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											${uiLabelMap.Department}:
										</div>
										<div class="span7">
											<span id="department"></span>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											${uiLabelMap.FormFieldTitle_position}:
										</div>
										<div class="span7">
											<span id="emplPositionTypeId"></span>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											${uiLabelMap.DateJoinCompany}:
										</div>
										<div class="span7">
											<span id="dateJoinCompany"></span>
										</div>
									</div>
			                	</div>
			                	<div class="span6">
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											${uiLabelMap.EmplAgreementId}:
										</div>
										<div class="span7">
											<span id="agreementId"></span>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											${uiLabelMap.agreementTypeId}:
										</div>
										<div class="span7">
											<span id="agreementId"></span>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											${uiLabelMap.AvailableFromDate}:
										</div>
										<div class="span7">
											<span id="fromDate"></span>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											${uiLabelMap.AvailableThruDate}:
										</div>
										<div class="span7">
											<span id="thruDate"></span>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											${uiLabelMap.HREmplReasonResign}:
										</div>
										<div class="span7">
											<span id="reasonResign"></span>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											${uiLabelMap.HREmplResignDate}:
										</div>
										<div class="span7">
											<span id="resignDate"></span>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											${uiLabelMap.HRNotes}:
										</div>
										<div class="span7">
											<span id="comment"></span>
										</div>
									</div>
			                	</div>
		                	</div>
		                </div>	
	                </div>
	                <div id="accLiabilities">
	                	<div id="invoicePaid"></div>
	                </div>
	                <div>
	                	<div id="paymentPaid"></div>
	                </div>
	                <div id="transferredAsset">
	                	<div id="transferredAssetJqx"></div>
	                </div>
				</div>
			</div>
			<#if parameters.requestId?exists>
				<#assign checkApprvalPerm = Static["com.olbius.workflow.WorkFlowUtils"].checkApprvalPerm(delegator, userLogin.partyId, parameters.requestId)/>
					<#if checkApprvalPerm>
						<div class="form-action" id="submitAction">
							<button id="notApproval" type="button" class="btn btn-danger form-action-button pull-right icon-remove">${uiLabelMap.HrCommonNotApproval}</button>
							<button id="approval" type="button" class="btn btn-primary form-action-button pull-right icon-ok">${uiLabelMap.HREmplApproval}</button>
						</div>
					</#if>
			</#if>
		</div>
	</div>
</div>						   