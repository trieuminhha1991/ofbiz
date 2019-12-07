<#assign quotationStatusList = delegator.findByAnd("StatusItem", {"statusTypeId" : "SALES_COMM_STATUS"}, null, false) />
<script type="text/javascript">
	<#if quotationStatusList?exists>
		var statusData = [
		<#list quotationStatusList as quotationStatus>
		<#assign description = StringUtil.wrapString(quotationStatus.get("description", locale)) />
		{
			statusId: "${quotationStatus.statusId}",
			description: "${description}"
		}, 
		</#list>
		];
	<#else>
		var statusData = [];
	</#if>
</script>
<#assign dataField="[{ name: 'salesCommissionId', type: 'string'},
					 { name: 'partyId', type: 'string'},
					 { name: 'salesSatementId', type: 'string'},
					 { name: 'fromDate', type: 'date', other:'Timestamp'},
					 { name: 'thruDate', type: 'date', other:'Timestamp'},
					 { name: 'amount', type: 'number', formatter: 'float'},
					 { name: 'hasQuantity', type: 'string'},
					 { name: 'statusId', type: 'string'}, 
	 		 	]"/>
<#assign columnlist="{text: '${uiLabelMap.DASalesCommissionId}', dataField: 'salesCommissionId', width: '14%'},
					 {text: '${uiLabelMap.DAPartyId}', dataField: 'partyId'},
					 {text: '${uiLabelMap.DASalesStatementId}', dataField: 'salesStatementId', width: '14%'},
				 	 {text: '${uiLabelMap.DAFromDate}', dataField: 'fromDate', cellsformat: 'dd/MM/yyyy', width: '12%'},
				 	 {text: '${uiLabelMap.DAThruDate}', dataField: 'thruDate', cellsformat: 'dd/MM/yyyy', width: '12%'},
				 	 {text: '${uiLabelMap.DAAmount}', dataField: 'amount', cellsformat: 'c2', width: '14%'},
				 	 {text: '${uiLabelMap.DAStatus}', dataField: 'statusId', width: '14%', filtertype: 'checkedlist', 
							cellsrenderer: function(row, column, value){
						 		var data = $('#jqxgridCommissionData').jqxGrid('getrowdata', row);
	    						for(var i = 0 ; i < statusData.length; i++){
	    							if (value == statusData[i].statusId){
	    								return '<span title = ' + statusData[i].description +'>' + statusData[i].description + '</span>';
	    							}
	    						}
	    						return '<span title=' + value +'>' + value + '</span>';
						 	}, 
						 	createfilterwidget: function (column, columnElement, widget) {
								var filterDataAdapter = new $.jqx.dataAdapter(statusData, {
									autoBind: true
								});
								var records = filterDataAdapter.records;
								records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
									renderer: function(index, label, value){
										for(var i = 0; i < statusData.length; i++){
											if(statusData[i].statusId == value){
												return '<span>' + statusData[i].description + '</span>';
											}
										}
										return value;
									}
								});
								widget.jqxDropDownList('checkAll');
				   			}
				   		},
				 "/>
<@jqGrid id="jqxgridCommissionData" url="jqxGeneralServicer?sname=JQGetListSalesCommissionData&statusId=SALES_COMM_CREATED" dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
 	showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="false" addrow="false" addType="popup" deleterow="false" editable="false" filtersimplemode="true" showstatusbar="false" 
 	mouseRightMenu="true" contextMenuId="contextMenu" showtoolbar="true" selectionmode="checkbox" 
/>
<#-- initrowdetailsDetail=initrowdetailsDetail initrowdetails="true" rowdetailstemplateAdvance=rowdetailstemplateAdvance rowdetailsheight="300" -->
<div>
	<div style="text-align:right">
		<span class="widget-toolbar none-content">
			<a class="btn btn-primary btn-mini" href="javascript:acceptSalesCommissionData();" style="font-size:13px; padding:0 8px">
				<i class="icon-ok open-sans">${uiLabelMap.DAApproveAccept}</i></a>
          	<a class="btn btn-danger btn-mini" href="javascript:cancelSalesCommissionData();" style="font-size:13px; padding:0 8px">
				<i class="icon-remove open-sans">${uiLabelMap.DAApproveCancel}</i></a>
		</span>
	</div>
</div>
<div id='contextMenu'>
	<ul>
	    <li><i class="icon-refresh open-sans"></i>${StringUtil.wrapString(uiLabelMap.DARefresh)}</li>
	</ul>
</div>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript">
	jQuery(document).ready(function(){
		$.jqx.theme = 'olbius';
		theme = $.jqx.theme;
		$("#contextMenu").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
	        var rowindex = $("#jqxgridCommissionData").jqxGrid('getselectedrowindex');
	        var tmpKey = $.trim($(args).text());
	        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DARefresh)}") {
	        	$("#jqxgridCommissionData").jqxGrid('updatebounddata');
	        }
		});
	});
	function acceptSalesCommissionData() {
		bootbox.confirm("${uiLabelMap.DAAreYouSureAccept}", function(result) {
			if(result) {
				submitData("SALES_COMM_ACCEPTED");
			}
		});
	}
	function cancelSalesCommissionData() {
		bootbox.confirm("${uiLabelMap.DAAreYouSureCancelNotAccept}", function(result) {
			if(result) {
				submitData("SALES_COMM_CANCELED");
			}
		});
	}
	
	function submitData(statusId) {
		var selectedRowIndexes = $('#jqxgridCommissionData').jqxGrid("selectedrowindexes");
		if (selectedRowIndexes == null || selectedRowIndexes.length <= 0) {
			var message0 = "<i class='fa-times-circle open-sans icon-modal-alert-danger'></i> <span class='message-content-alert-danger'>${uiLabelMap.DAYouNotYetChooseRow}!</span>";
			bootbox.dialog(message0, [{
				"label" : "OK",
				"class" : "btn-mini btn-primary width60px",
				}]
			);
			return false;
		} else {
			var listItems = new Array();
			for(var index in selectedRowIndexes) {
				var data = $('#jqxgridCommissionData').jqxGrid('getrowdata', selectedRowIndexes[index]);
				if (data != undefined && data != null) {
					if (data.salesCommissionId != null) {
						listItems.push(data.salesCommissionId);
					}
				}
			}
			jQuery.ajax({
	            url: 'changeListSalesCommissionDataStatus',
	            type: 'POST',
	            data: {
	            	"statusId": statusId,
	            	"listItems": listItems,
	            },
	            beforeSend: function () {
					$("#info_loader").show();
				},
	            success: function (data) {
	                if (data.thisRequestUri == "json") {
	            		var errorMessage = "";
				        if (data._ERROR_MESSAGE_LIST_ != null) {
				        	for (var i = 0; i < data._ERROR_MESSAGE_LIST_.length; i++) {
				        		//errorMessage += "<p><b>${uiLabelMap.DAErrorUper}</b>: " + data._ERROR_MESSAGE_LIST_[i] + "</p>";
				        		errorMessage += "<p>" + data._ERROR_MESSAGE_LIST_[i] + "</p>";
				        	}
				        }
				        if (data._ERROR_MESSAGE_ != null) {
				        	//errorMessage += "<p><b>${uiLabelMap.DAErrorUper}</b>: " + data._ERROR_MESSAGE_ + "</p>";
				        	errorMessage = data._ERROR_MESSAGE_;
				        }
				        if (errorMessage != "") {
				        	$('#container').empty();
				        	$('#jqxNotification').jqxNotification({ template: 'error'});
				        	$("#jqxNotification").html(errorMessage);
				        	$("#jqxNotification").jqxNotification("open");
				        	$("#btnPrevWizard").removeClass("disabled");
							$("#btnNextWizard").removeClass("disabled");
				        } else {
			        		$('#container').empty();
				        	$('#jqxNotification').jqxNotification({ template: 'info'});
				        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
				        	$("#jqxNotification").jqxNotification("open");
				        	$("#jqxgridCommissionData").jqxGrid("updatebounddata");
				        }
	            	}
	            },
	            error: function (e) {
	            },
	            complete: function() {
			        $("#info_loader").hide();
			    }
	        });
		}
	}
</script>