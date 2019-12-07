<#assign statusList = delegator.findByAnd("StatusItem", {"statusTypeId" : "REG_PROMO_STTS"}, null, false) />
<#assign statusMarkList = delegator.findByAnd("StatusItem", {"statusTypeId" : "EXH_MARKING_STTS"}, null, false)/>
<script type="text/javascript">
	var statusData = new Array();
	<#list statusList as statusItem>
		<#assign description = StringUtil.wrapString(statusItem.get("description", locale)) />
		var row = {};
		row['statusId'] = '${statusItem.statusId}';
		row['description'] = "${description}";
		statusData[${statusItem_index}] = row;
	</#list>
	
	var statusMarkData = new Array();
	<#list statusMarkList as statusItem>
		<#assign description = StringUtil.wrapString(statusItem.get("description", locale)) />
		var row = {};
		row['statusId'] = '${statusItem.statusId}';
		row['description'] = "${description}";
		statusMarkData[${statusItem_index}] = row;
	</#list>
</script>
<#assign dataField="[{name: 'productPromoRegisterId', type: 'string'}, 
						{name: 'partyId', type: 'string'}, 
						{name: 'productPromoId', type: 'string'}, 
						{name: 'productPromoRuleId', type: 'string'},
						{name: 'ruleName', type: 'string'}, 
						{name: 'registerStatus', type: 'string'}, 
						{name: 'promoMarkValue', type: 'string'}, 
						{name: 'createdDate', type: 'date', other: 'Timestamp'}, 
						{name: 'createdBy', type: 'string'}, 
						{name: 'agreementId', type: 'string'}
						]"/>
<#assign columnlist="{text: '${uiLabelMap.DAProductPromoRegisterId}', dataField: 'productPromoRegisterId', width: '180px', editable: false}, 
						{text: '${uiLabelMap.DACustomer}', dataField: 'partyId', width: '180px', editable: false},
						{text: '${uiLabelMap.DAProductPromoId}', dataField: 'productPromoId', width: '180px', editable: false}, 
						{text: '${uiLabelMap.DALevel}', dataField: 'ruleName', width: '100px', editable: 'false', editable: false}, 
						{text: '${uiLabelMap.DARegisterStatus}', dataField: 'registerStatus', width: '160px', editable: false, filtertype: 'checkedlist', 
							cellsrenderer: function(row, column, value){
						 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
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
						{text: '${uiLabelMap.DAMarkValue}', dataField: 'promoMarkValue', width: '120px', columntype:'dropdownlist', filterable:false, sortable:false, 
							cellsrenderer: function(row, column, value){
						 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	    						for(var i = 0 ; i < statusMarkData.length; i++){
	    							if (value == statusMarkData[i].statusId){
	    								return '<span title = ' + statusMarkData[i].description +'>' + statusMarkData[i].description + '</span>';
	    							}
	    						}
	    						return '<span title=' + value +'>' + value + '</span>';
						 	},
				   		 	createeditor: function (row, cellvalue, editor) {
						 		var sourceDataPacking = {
					                localdata: statusMarkData,
					                datatype: \"array\"
					            };
					            var dataAdapterPacking = new $.jqx.dataAdapter(sourceDataPacking);
					            editor.jqxDropDownList({ source: dataAdapterPacking, displayMember: 'description', valueMember: 'statusId'});
	                      	}
						}, 
						{text: '${uiLabelMap.DACreatedDate}', dataField: 'createdDate', width: '160px', cellsformat: 'dd/MM/yyyy - hh:mm:ss', editable: false},
						{text: '${uiLabelMap.DACreatedBy}', dataField: 'createdBy', width: '160px', editable: false},
						{text: '${uiLabelMap.DAAgreementId}', dataField: 'agreementId', width: '160px', editable: false}
              		"/>
<#assign tmpCreateUrl = ""/>
<#if security.hasPermission("REGPROMO_ROLE_VIEW", session)>
	<#assign tmpCreateUrl = "icon-zoom-in open-sans@${uiLabelMap.DAAbbListExhibitedRegister}@listExhibitedRegister"/>
</#if>
<@jqGrid id="jqxgrid" clearfilteringbutton="true" editable="true" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
		viewSize="25" showtoolbar="true" defaultSortColumn="partyId;createdDate" sortdirection="desc" filtersimplemode="true" showstatusbar="false" 
		customcontrol1=tmpCreateUrl editmode="click" 
		url="jqxGeneralServicer?sname=JQGetListExhibitedRegister&productPromoTypeId=EXHIBITED&statusId=REG_PROMO_ACCEPTED&promoMarkValue=N" selectionmode="checkbox"/>
<br/>
<div class="row-fluid">
	<div class="row-fluid wizard-actions">
		<button class="btn btn-primary btn-next btn-small" data-last="${uiLabelMap.DAFinish}" id="btnNextWizard">
			<i class="icon-ok"></i>
			${uiLabelMap.DASendConfirm}
		</button>
	</div>
</div>
<script src="/delys/images/js/bootbox.min.js"></script>
<script type="text/javascript">
	$('#btnNextWizard').on('click', function(){
		$("#btnNextWizard").addClass("disabled");
		var selectedRowIndexes = $('#jqxgrid').jqxGrid("selectedrowindexes");
		var postData = new Array();
		var index1 = 0;
		for(var index in selectedRowIndexes) {
			var data = $('#jqxgrid').jqxGrid('getrowdata', selectedRowIndexes[index]);
			if (data != undefined && data != null) {
				if (data.promoMarkValue != undefined && data.promoMarkValue != null && !(/^\s*$/.test(data.promoMarkValue))) {
					var row = {"productPromoRegisterId": data.productPromoRegisterId, "promoMarkValue" : data.promoMarkValue};
					postData[index1] = row;
					index1 = index1 + 1;
				}
			}
		}
		if (postData.length > 0) {
			bootbox.confirm("${uiLabelMap.DAAreYouSureSave}", function(result) {
				if(result) {
					$.ajax({
						url : "markingExhibitedRegisters",
						type : "POST",
						data :{
							listData: JSON.stringify(postData),
						},
						dataType : 'json', // Choosing a JSON datatype
						success : function(data) {// Variable data contains the data we get from serverside
							$("#jqxgrid").jqxGrid("updatebounddata");
							if (data.responseMessage == "error"){
					        	//commit(false);
					        	$('#jqxNotification').jqxNotification({ template: 'info'});
					        	$("#jqxNotification").text(data.errorMessage);
					        	$("#jqxNotification").jqxNotification("open");
					        } else{
					        	//commit(true);
					        	$('#container').empty();
					        	$('#jqxNotification').jqxNotification({ template: 'info'});
					        	$("#jqxNotification").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
					        	$("#jqxNotification").jqxNotification("open");
					        }
						},
						error : function(textStatus, errorThrown) {	
							//console.log("error", textStatus, errorThrown);
							$('#container').empty();
				        	$('#jqxNotification').jqxNotification({template: 'info'});
				        	$("#jqxNotification").text(textStatus);
				        	$("#jqxNotification").jqxNotification("open");
						},
						complete: function() {
					        $("#btnNextWizard").removeClass("disabled");
					    }
					});
				}
			});
		} else {
			bootbox.dialog("${uiLabelMap.DANotYetChooseItem}!", [{
				"label" : "OK",
				"class" : "btn-small btn-primary",
				}]
			);
			return false;
		}
	});
</script>