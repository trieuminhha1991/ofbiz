<#assign statusIdVar = ""/>
<script type="text/javascript">
	<#assign statusList = delegator.findByAnd("StatusItem", {"statusTypeId" : "RETURNP_REQ_STATUS"}, null, false) />
	var statusData = new Array();
	var statusIndex = 0;
	<#list statusList as statusItem>
		<#if isSalesSup>
			<#assign statusIdVar = "RETURREQ_CREATED"/>
			<#if ("RETURREQ_CREATED" == statusItem.statusId || "RETURREQ_SUPAPPROVED" == statusItem.statusId || "RETURREQ_REJECTED" == statusItem.statusId)>
				<#assign description = StringUtil.wrapString(statusItem.get("description", locale)) />
				var row = {};
				row['statusId'] = '${statusItem.statusId}';
				row['description'] = "${description}";
				statusData[statusIndex] = row;
				statusIndex++;
			</#if>
		<#elseif isAsm>
			<#assign statusIdVar = "RETURREQ_SUPAPPROVED"/>
			<#if ("RETURREQ_ASMAPPROVED" == statusItem.statusId || "RETURREQ_SUPAPPROVED" == statusItem.statusId || "RETURREQ_REJECTED" == statusItem.statusId)>
				<#assign description = StringUtil.wrapString(statusItem.get("description", locale)) />
				var row = {};
				row['statusId'] = '${statusItem.statusId}';
				row['description'] = "${description}";
				statusData[statusIndex] = row;
				statusIndex++;
			</#if>
		<#elseif isRsm>
			<#assign statusIdVar = "RETURREQ_ASMAPPROVED"/>
			<#if ("RETURREQ_ASMAPPROVED" == statusItem.statusId || "RETURREQ_RSMAPPROVED" == statusItem.statusId || "RETURREQ_REJECTED" == statusItem.statusId)>
				<#assign description = StringUtil.wrapString(statusItem.get("description", locale)) />
				var row = {};
				row['statusId'] = '${statusItem.statusId}';
				row['description'] = "${description}";
				statusData[statusIndex] = row;
				statusIndex++;
			</#if>
		<#elseif isNbd>
			<#assign statusIdVar = "RETURREQ_RSMAPPROVED"/>
			<#if ("RETURREQ_APPROVED" == statusItem.statusId || "RETURREQ_RSMAPPROVED" == statusItem.statusId || "RETURREQ_REJECTED" == statusItem.statusId)>
				<#assign description = StringUtil.wrapString(statusItem.get("description", locale)) />
				var row = {};
				row['statusId'] = '${statusItem.statusId}';
				row['description'] = "${description}";
				statusData[statusIndex] = row;
				statusIndex++;
			</#if>
		</#if>
	</#list>
</script>
<#assign uomList = delegator.findByAnd("Uom", {"uomTypeId" : "PRODUCT_PACKING"}, null, false)/>
<script type="text/javascript">
	var uomData = new Array();
	<#list uomList as uomItem>
		<#assign description = StringUtil.wrapString(uomItem.get("description", locale)) />
		var row = {};
		row['uomId'] = '${uomItem.uomId}';
		row['description'] = "${description?default('')}";
		uomData[${uomItem_index}] = row;
	</#list>
</script>

<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	if(datarecord.rowDetail == null || datarecord.rowDetail.length < 1){
		return 'Not Data';
	}
	var dataAdapter = new $.jqx.dataAdapter(datarecord.rowDetail, { autoBind: true });
    var recordData = dataAdapter.records;
		var nestedGrids = new Array();
        var id = datarecord.uid.toString();
        
         var grid = $($(parentElement).children()[0]);
         $(grid).attr(\"id\",\"jqxgridDetail\");
         nestedGrids[index] = grid;
       
         var recordDataById = [];
         for (var ii = 0; ii < recordData.length; ii++) {
             recordDataById.push(recordData[ii]);
         }
         var recordDataSource = { datafields: [	
         			{ name: 'requirementId', type: 'string' },
         	 		{ name: 'reqItemSeqId', type: 'string' },
					{ name: 'productId', type: 'string' },
       				{ name: 'expireDate', type: 'date', other: 'Timestamp'},
       				{ name: 'quantityUomId', type: 'string'},
       				{ name: 'quantity', type: 'number', formatter: 'integer'},
       				{ name: 'quantityAccepted', type: 'number', formatter: 'integer'}
         		],
           		updaterow: function (rowid, rowdata, commit) {
           			commit(true);
           			var quantity = rowdata.quantity;
           			var quantityAccepted = rowdata.quantityAccepted;
           			if (quantityAccepted != null && quantityAccepted != undefined && quantityAccepted <= quantity) {
           				var data = {'requirementId' : rowdata.requirementId, 'reqItemSeqId' : rowdata.reqItemSeqId, 'quantityAccepted' : rowdata.quantityAccepted};
	            		$.ajax({
	                        type: 'POST',
	                        url: 'updateReturnReqItem',
	                        data: data,
	                        success: function (data, status, xhr) {
	                            // update command is executed.
	                            if(data.responseMessage == 'error'){
	                            	commit(false);
	                            	$('#jqxNotification').jqxNotification({ template: 'info'});
	                            	$('#jqxNotification').text(data.ERROR_MESSAGE);
	                            	$('#jqxNotification').jqxNotification(\"open\");
	                            }else{
	                            	commit(true);
	                            	$('#container').empty();
	                            	$('#jqxNotification').jqxNotification({ template: 'info'});
	                            	$('#jqxNotification').text(\"${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}\");
	                            	$('#jqxNotification').jqxNotification(\"open\");
	                        		//$(grid).jqxGrid('updatebounddata');
	                            }
	                        },
	                        error: function () {
	                            commit(false);
	                        }
	                    });
           			} else {
           				bootbox.dialog('${uiLabelMap.DAQuantityAcceptedNotValid}!', [{
							'label' : 'OK',
							'class' : 'btn-small btn-primary',
							}]
						);
						commit(false);
           			}
                },
             	localdata: recordDataById
         }
         var nestedGridAdapter = new $.jqx.dataAdapter(recordDataSource);
        
         if (grid != null) {
             grid.jqxGrid({
                 source: nestedGridAdapter, 
                 width: '96%', 
                 height: 150,
                 showtoolbar:false,
		 		 editable:true,
		 		 editmode:\"click\",
		 		 showheader: true,
		 		 selectionmode:\"singlecell\",
		 		 theme: 'energyblue',
                 columns: [
           					{ text: '${uiLabelMap.DASeqId}', dataField: 'reqItemSeqId', width: '80px', editable: false},
						 	{ text: '${uiLabelMap.DAProductId}', dataField: 'productId', editable: false},
						 	{ text: '${uiLabelMap.DAExpireDate}', dataField: 'expireDate', width: '180px', cellsformat: 'dd/MM/yyyy', editable: false},
						 	{ text: '${uiLabelMap.DAQuantityUomId}', dataField: 'quantityUomId', width: '180px', editable: false,
						 		cellsrenderer: function(row, column, value){
		    						for (var i = 0 ; i < uomData.length; i++){
		    							if (value == uomData[i].uomId){
		    								return '<span title = ' + uomData[i].description +'>' + uomData[i].description + '</span>';
		    							}
		    						}
		    						return '<span title=' + value +'>' + value + '</span>';
								}
						 	},
				 			{ text: '${uiLabelMap.DAQuantity}', dataField: 'quantity', width: '180px', cellsalign: 'right', editable: false},
						 	{ text: '${uiLabelMap.DAQuantityAccepted}', dataField: 'quantityAccepted', width: '180px', cellsalign: 'right', editable:'true'}
                 ]
             });
         }
 }"/>

<#assign dataField="[{ name: 'requirementId', type: 'string'},
					{name: 'facilityId', type: 'string'},
	             	{name: 'productStoreId', type: 'string'},
	             	{name: 'contactMechId', type: 'string'},
	             	{name: 'deliverableId', type: 'string'},
					{name: 'fixedAssetId', type: 'string'},
	             	{name: 'productId', type: 'string'},
	             	{name: 'statusId', type: 'string'},
	             	{name: 'description', type: 'string'},
	             	{name: 'createdDate', type: 'date', other: 'Timestamp'},
	             	{name: 'requirementStartDate', type: 'date', other: 'Timestamp'},
	             	{name: 'requiredByDate', type: 'date', other: 'Timestamp'},
					{name: 'estimatedBudget', type: 'string'},
					{name: 'currencyUomId', type: 'string'},
					{name: 'quantity', type: 'string'},
					{name: 'useCase', type: 'string'},
					{name: 'reason', type: 'string'},
					{name: 'createdByUserLogin', type: 'string'},
					{name: 'rowDetail', type: 'string'}
	 		 	]"/>
<#assign columnlist="{text: '${uiLabelMap.requirementId}', dataField: 'requirementId', width: 150, editable: false,
						cellsrenderer: function(row, colum, value) {
                        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
                        	return \"<span><a href='/delys/control/viewReturnProductReq?requirementId=\" + data.requirementId + \"'>\" + data.requirementId + \"</a></span>\";
                        }
					},
				 {text: '${uiLabelMap.Status}', dataField: 'statusId', width: 150, columntype:'dropdownlist', filterable:false, sortable:false, 
					cellsrenderer: function(row, column, value){
						for(var i = 0; i < statusData.length; i++){
							if(statusData[i].statusId == value){
								return '<span title=' + value + '>' + statusData[i].description + '</span>'
							}
						}
					}, 
		   		 	createeditor: function (row, cellvalue, editor) {
				 		var sourceDataPacking = {
			                localdata: statusData,
			                datatype: \"array\"
			            };
			            var dataAdapterPacking = new $.jqx.dataAdapter(sourceDataPacking);
			            editor.jqxDropDownList({ source: dataAdapterPacking, displayMember: 'description', valueMember: 'statusId'});
                  	}
				 },
				 {text: '${uiLabelMap.DACreatedDate}', dataField: 'createdDate', width: 150, cellsformat: 'd', filtertype: 'range', editable: false},
				 {text: '${uiLabelMap.Requestor}', dataField: 'createdByUserLogin', width: 150, editable: false},
				 {text: '${uiLabelMap.DAContactMechId}', dataField: 'contactMechId', width: 150, editable: false},
				 {text: '${uiLabelMap.DADescription}', dataField: 'description', width: 150, editable: false},
				 {text: '${uiLabelMap.DARequirementStartDateOrigin}', dataField: 'requirementStartDate', width: 150, cellsformat: 'd', filtertype: 'range', editable: false},
				 {text: '${uiLabelMap.DARequiredByDateOrigin}', dataField: 'requiredByDate', width: 150, cellsformat: 'd', filtertype: 'range', editable: false},
				 {text: '${uiLabelMap.DAReason}', dataField: 'reason', width: 150, editable: false},
				 "/>

<@jqGrid url="jqxGeneralServicer?sname=JQGetListRequirementAndItemDetail&requirementTypeId=RETURN_PRODDIS_REQ&statusId=${statusIdVar}" dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
		 showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addrow="false" addType="popup" deleterow="false" editable="true" 
		 mouseRightMenu="true" contextMenuId="contextMenu" initrowdetailsDetail=initrowdetailsDetail initrowdetails="true" editmode="click" selectionmode="checkbox" 
		 customcontrol1="fa fa-arrow-left@${uiLabelMap.DABack}@listReturnProductReq" 
		 />
<br/>
<div class="row-fluid">
	<div class="row-fluid wizard-actions">
		<button class="btn btn-primary btn-next btn-small" data-last="${uiLabelMap.DAFinish}" id="btnNextWizard">
			<i class="icon-ok"></i>
			${uiLabelMap.DASendConfirm}
		</button>
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
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	$("#contextMenu").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#contextMenu").on('itemclick', function (event) {
		var args = event.args;
        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
        var tmpKey = $.trim($(args).text());
        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DARefresh)}") {
        	$("#jqxgrid").jqxGrid('updatebounddata');
        }
	});
</script>
<script src="/delys/images/js/bootbox.min.js"></script>
<script type="text/javascript">
	$('#btnNextWizard').on('click', function(){
		$("#btnNextWizard").addClass("disabled");
		var selectedRowIndexes = $('#jqxgrid').jqxGrid("selectedrowindexes");
		var postData = new Array();
		var index1 = 0;
		for(var index in selectedRowIndexes) {
			var data = $('#jqxgrid').jqxGrid('getrowdata', selectedRowIndexes[index]);
			if (data != undefined && data != null && data.statusId != null && data.statusId != "") {
				var row = {"requirementId": data.requirementId, "statusId" : data.statusId};
				postData[index1] = row;
				index1 = index1 + 1;
			}
		}
		if (postData.length > 0) {
			bootbox.confirm("${uiLabelMap.DAAreYouSureSave}", function(result) {
				if(result) {
					$.ajax({
						url : "updateReturnProductReqs",
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
			$("#btnNextWizard").removeClass("disabled");
			return false;
		}
	});
</script>