<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/delys/images/js/util/DateUtil.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/delys/images/js/bootbox.min.js"></script>
<script>
	var statusId = 'LABEL_ITEM_PROPOSAL';
	<#assign facilities = delegator.findList("Facility", null, null, null, null, false)>
	var faciData = new Array();
	<#list facilities as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.facilityName?if_exists)/>
		row['facilityId'] = '${item.facilityId?if_exists}';
		row['description'] = '${description?if_exists}';
		faciData[${item_index}] = row;
	</#list>
	
	function getDescriptionByFacilityId(facilityId) {
		for ( var x in faciData) {
			if (facilityId == faciData[x].facilityId) {
				return faciData[x].description;
			}
		}
	}
	
	<#assign packingUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_LABEL_ITEM"), null, null, null, false)>
	var packingData = new Array();
	<#list packingUoms as item>
		var row = {};
		row['uomId'] = '${item.uomId?if_exists}';
		row['description'] = '${item.description?if_exists}';
		packingData[${item_index}] = row;
	</#list>
	
	function getDescriptionByUomId(uomId) {
		for ( var x in packingData) {
			if (uomId == packingData[x].uomId) {
				return packingData[x].description;
			}
		}
	}
	
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "LABEL_ITEM_STATUS"), null, null, null, false)>
	var statusData = new Array();
	<#list statuses as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("description", locale))/>
		row['statusId'] = '${item.statusId?if_exists}';
		row['description'] = '${description?if_exists}';
		statusData[${item_index}] = row;
	</#list>
	
	function getDescriptionByStatusId(statusId) {
		for ( var x in statusId) {
			if (statusId == statusData[x].statusId) {
				return statusData[x].description;
			}
		}
	}
	
	<#assign listProduct = delegator.findList("Product", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("productTypeId", "RAW_MATERIAL"), null, null, null, false) />
	var productData = 
	[
		<#list listProduct as product>
		{
			productId: "${product.productId}",
			internalName: "${StringUtil.wrapString(product.get('internalName', locale)?if_exists)}"
		},
		</#list>
	];
	
	function getDescriptionByProductId(productId) {
		for ( var x in productData) {
			if (productId == productData[x].productId) {
				return productData[x].internalName;
			}
		}
	}
	
	function reponsiveRowDetails(grid, parentElement) {
	    $(window).bind('resize', function() {
	    	$(grid).jqxGrid({ width: "96%"});
	    });
	    $('#sidebar').bind('resize', function() {
	    	$(grid).jqxGrid({ width: "96%"});
	    });
	}
	
</script>
	<div id="contentNotificationSendRequestSuccess">
	</div>
	<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
		var grid = $($(parentElement).children()[0]);
		$(grid).attr('id','jqxgridDetail' + index);
		reponsiveRowDetails(grid);
		if(datarecord.rowDetail){
			var sourceGridDetail =
	        {
	            localdata: datarecord.rowDetail,
	            datatype: 'local',
	            datafields:
	            [
	             	{ name: 'requirementId', type: 'string' },
	             	{ name: 'reqItemSeqId', type: 'string' },
	             	{ name: 'productId', type: 'string' },
	                { name: 'quantity', type: 'number' },
	                { name: 'quantityUomId', type: 'string'},
	            ]
	        };
	        var dataAdapterGridDetail = new $.jqx.dataAdapter(sourceGridDetail);
	        grid.jqxGrid({
	            width: '98%',
	            height: '92%',
	            theme: 'olbius',
	            localization: {emptydatastring: '${StringUtil.wrapString(uiLabelMap.DANoDataToDisplay)}'},
	            source: dataAdapterGridDetail,
	            sortable: true,
	            pagesize: 5,
	            selectionmode: 'checkbox',
		 		pageable: true,
	            columns: [
							{ text: '${uiLabelMap.accProductName}', datafield: 'productId', align: 'center', width: 200, pinned: true,
								cellsrenderer: function(row, colum, value){
									if(value){
										return '<span>' + getDescriptionByProductId(value) + '</span>';
									}
							    }, 
							},
							{ text: '${uiLabelMap.Quantity}', datafield: 'quantity', align: 'center', cellsalign: 'right',
								cellsrenderer: function(row, colum, value){
									if(value){
										return '<span style=\"text-align: right;\">' + value.toLocaleString('${locale}') + '</span>';
									}
							    }, 
							},
							{ text: '${uiLabelMap.UnitProduct}', datafield: 'quantityUomId', align: 'center',
								cellsrenderer: function(row, colum, value){
									if(value){
										return '<span>' + getDescriptionByUomId(value) + '</span>';
									}
							    },
							},
						]
	        });
	        grid.on('rowselect', function (event) 
    		{
    		    var args = event.args;
    		    if(typeof event.args.rowindex != 'number'){
    		    	var rowBoundIndex = args.rowindex;
    		    	if(rowBoundIndex.length == 0){
    		    		dataSource = [];
    		    	}else{
    		    		for ( var x in rowBoundIndex) {
        		    		var rowID = grid.jqxGrid('getRowId', rowBoundIndex[x]);
            		        var data = grid.jqxGrid('getrowdatabyid', rowID);
            		        dataSourceByGirdSelect(data);
    					}
    		    	}
    		    }else{
    		    	var tmpArray = event.args.rowindex;
    		        var rowID = grid.jqxGrid('getRowId', tmpArray);
    		        var data = grid.jqxGrid('getrowdatabyid', rowID);
    		        dataSourceByGirdSelect(data);
    		    }
    		});
	        grid.on('rowunselect', function (event) 
    		{
    		    var args = event.args;
    		    if(typeof event.args.rowindex != 'number'){
    		    	var rowBoundIndex = args.rowindex;
    		    	for ( var x in rowBoundIndex) {
    		    		var rowID = grid.jqxGrid('getRowId', rowBoundIndex[x]);
    		    		var data = grid.jqxGrid('getrowdatabyid', rowID);
        		        dataSourceByGirdUnSelect(data);
					}
    		    }else{
    		    	var tmpArray = event.args.rowindex;
    		        var rowID = grid.jqxGrid('getRowId', tmpArray);
    		        var data = grid.jqxGrid('getrowdatabyid', rowID);
    		        dataSourceByGirdUnSelect(data);
    		    }

    		});
		}else {
			grid.jqxGrid({
	            width: '98%',
	            height: '92%',
	            theme: 'olbius',
	            localization: {emptydatastring: '${StringUtil.wrapString(uiLabelMap.DANoDataToDisplay)}'},
	            source: [],
	            sortable: true,
	            pagesize: 5,
		 		pageable: true,
	            selectionmode: 'checkbox',
	            columns: [
							{ text: '${uiLabelMap.accProductName}', datafield: 'productId', align: 'center'},
							{ text: '${uiLabelMap.Quantity}', datafield: 'quantity', align: 'center', width: 150, cellsalign: 'right'},
							{ text: '${uiLabelMap.UnitProduct}', datafield: 'quantityUomId', align: 'center', cellsalign: 'right'},
						]
	        });
			
		}
	}"/>
	<#assign dataField="[
					{ name: 'facilityId', type: 'string'},
					{ name: 'requirementId', type: 'string'},
					{ name: 'createdDate', type: 'date', other: 'Timestamp' },
					{ name: 'requiredByDate', type: 'date', other: 'Timestamp' },
					{ name: 'requirementStartDate', type: 'date', other: 'Timestamp' },
					{ name: 'description', type: 'string'},
					{ name: 'requirementTypeId', type: 'string'},
					{ name: 'statusId', type: 'string'},
					{ name: 'rowDetail', type: 'string'}
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
					{ text: '${uiLabelMap.LogRequiremtId}', datafield: 'requirementId', align: 'center',
					},
					{ text: '${uiLabelMap.LogRequirePurchaseForFacility}', datafield: 'facilityId', align: 'center',
						cellsrenderer: function (row, column, value){
							if(value){
								return '<span>' + getDescriptionByFacilityId(value) + '<span>';
							}
						}
						
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.LogRequirePurchaseCreatedDate)}', datafield: 'createdDate', align: 'left', columntype: 'datetimeinput', editable: false, cellsformat: 'dd/MM/yyyy', filtertype:'range'},
					{ text: '${uiLabelMap.Status}', dataField: 'statusId',
						cellsrenderer: function (row, column, value){
							if(value){
								return '<span>' + getDescriptionByStatusId(value) + '<span>';
							}
						}
					},
				"/>
	
	<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true"
		id="jqxgirdRequirementPurchaseLableItem" 
		initrowdetails = "true" initrowdetailsDetail=initrowdetailsDetail selectionmode	= "none"
		customcontrol1="fa-check open-sans@${uiLabelMap.LogTitleApprovalRequest}@javascript:approvalRequirememtByLogSend()"
		url="jqxGeneralServicer?sname=JQGetListRequirementPurchaseLableByStatus&statusId=LABEL_ITEM_PROPOSAL"
	/>	
	<div id="alterpopupWindowPurchaseApporal" class='hide'>
		<div>${uiLabelMap.LogRequirementToTalSendPO}</div>
		<div>
			<div id="jqxPurchaseApporal"></div>
			<div class="form-action">
				<button id="alterCancel" class='btn btn-danger form-action-button pull-right' style='height: 30px; line-height: 8px; margin-left: 5px;'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
				<button id="alterGoBack" class='btn btn-success form-action-button pull-right' style='height: 30px; line-height: 8px; margin-left: 5px;'><i class='icon-undo'></i> ${uiLabelMap.LogButtonGoBack}</button>
				<button id="alterSave" class='btn btn-primary form-action-button pull-right' style='height: 30px; line-height: 8px;'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
			</div>
		</div> 
	</div>
	
	<div id="jqxNotificationSendRequestSuccess" >
		<div id="notificationSendRequestSuccess">
		</div>
	</div>
	
<script>
	
	var dataSource = [];
	var requirementIdData = []; 
	var reqItemSeqIdData = [];
	$("#alterpopupWindowPurchaseApporal").jqxWindow({
	    width: 640, height: 360, resizable: false,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:'olbius'           
	});
	$("#jqxNotificationSendRequestSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationSendRequestSuccess", opacity: 0.9, autoClose: true, template: "success" });
	function dataSourceByGirdSelect(data){
		requirementIdData.push(data.requirementId);
		reqItemSeqIdData.push(data.reqItemSeqId);
		dataSource.push(data);
	}
	
	function dataSourceByGirdUnSelect(row){
		var ii = dataSource.indexOf(row);
		dataSource.splice(ii, 1);
		requirementIdData.splice(ii, 1);
		reqItemSeqIdData.splice(ii, 1);
	}
	
	function bindingDataToJqxProductTranfer(data){
		var source =
        {
            localdata: data,
            datatype: "array",
            datafields:
            [
                { name: 'productId', type: 'string' },
                { name: 'quantity', type: 'number' },
                { name: 'quantityUomId', type: 'string' },
            ]
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        $("#jqxPurchaseApporal").jqxGrid(
        {
            source: dataAdapter,
            columnsresize: true,
            theme: 'olbius',
            height: 270,
            width: '100%',
            pageable: true,
            columns: [
              { text: '${StringUtil.wrapString(uiLabelMap.accProductName)}', datafield: 'productId',
            	  cellsrenderer: function (row, column, value) {
					var data = $('#jqxPurchaseApporal').jqxGrid('getrowdata', row);
					var productId = data.productId;
					var description = getDescriptionByProductId(productId);
					return '<span>' + description + '</span>';
            	  } 
              },
              { text: '${StringUtil.wrapString(uiLabelMap.Quantity)}', datafield: 'quantity',
              },
              { text: '${StringUtil.wrapString(uiLabelMap.UnitProduct)}', datafield: 'quantityUomId',
            	  cellsrenderer: function (row, column, value) {
  	        		var data = $('#jqxPurchaseApporal').jqxGrid('getrowdata', row);
  					var quantityUomId = data.quantityUomId;
  					var description = getDescriptionByUomId(quantityUomId);
  				  	return '<span style=\"text-align: right\">' + description + '</span>';
  				  } 
              },
            ]
        });
	}
	
	function approvalRequirememtByLogSend(){
		if(dataSource.length == 0){
			bootbox.alert("${StringUtil.wrapString(uiLabelMap.LogSelectedRequirementOrSelectProduct)}");
		}else{
			sumProductTotalByRequirementPO(dataSource);
		}
	}
	
	function sumProductTotalByRequirementPO(dataSource){
		var productId = [];
		for ( var x in dataSource) {
			var productIdCheck = dataSource[x].productId;
			if(productId.indexOf(productIdCheck) > -1){
				
			}else{
				productId.push(productIdCheck);
			}
		}
		var result = [];
		for(var pro in productId){
			var quantity = 0;
			var quantityUomId= "";
			for ( var x in dataSource) {
				if(productId[pro] == dataSource[x].productId){
					quantity += parseFloat(dataSource[x].quantity);
					quantityUomId = dataSource[x].quantityUomId;
				}else{}
			}
			var o = {productId: productId[pro], quantity: quantity, quantityUomId: quantityUomId};
			result.push(o);
		}
		
		bindingDataToJqxProductTranfer(result);
		$('#alterpopupWindowPurchaseApporal').jqxWindow('open');
	}
	
	$("#alterSave").click(function () {
		bootbox.confirm("${uiLabelMap.AreYouSureSent}",function(result){ 
			if(result){	
				var rowsData = $('#jqxPurchaseApporal').jqxGrid('getrows');
				var productIdData = [];
				var quantityData = [];
				var quantityUomIdData = [];
				for(var i in rowsData){
					productIdData.push(rowsData[i].productId);
					quantityData.push(rowsData[i].quantity);
					quantityUomIdData.push(rowsData[i].quantityUomId);
				}
				createRequirementToPOByPurchaseLabelItem(productIdData, quantityData, quantityUomIdData);
	            /*var reqId = dataRecord.requirementId;
				sendRequirement({
	            	requirementId: reqId,
	            	roleTypeId: 'PO_EMPLOYEE',
					sendMessage: 'NewPurchaseRequirementMustBeApprove',
					action: "getListPurchaseRequiremetLabelItemToQA",
				}, 'sendPurchaseRequirement', 'jqxgrid');*/
			}
		});
	});
	
	function createRequirementToPOByPurchaseLabelItem(productIdData, quantityData, quantityUomIdData){
		$.ajax({
			url: "createRequirementToPOByPurchaseLabelItem",
			type: "POST",
			data: {productIdData: productIdData, quantityData: quantityData, quantityUomIdData: quantityUomIdData, requirementIdData: requirementIdData, reqItemSeqIdData: reqItemSeqIdData},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			checkCloseWindown = 0;
        	$("#jqxgirdRequirementPurchaseLableItem").jqxGrid('updatebounddata');
        	$('#alterpopupWindowPurchaseApporal').jqxWindow('close');
        	$("#notificationSendRequestSuccess").text('${StringUtil.wrapString(uiLabelMap.LogNotifiCreateRequirementSucess)}');
			$("#jqxNotificationSendRequestSuccess").jqxNotification('open');
		});
	}
	
	function sendRequestPurchaseLabelItemTotalToPO(requirementId){
		sendRequirement({
        	requirementId: requirementId,
        	roleTypeId: 'PO_EMPLOYEE',
			sendMessage: '${uiLabelMap.NewPurchaseRequirementMustBeApprove}',
			action: "getListPurchaseRequiremetLabelItemToQA",
		}, 'sendRequestPurchaseLabelItemTotalToPO', 'jqxgrid');
	}
	
	function sendRequirement(jsonObject, url, jqxgrid) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        success: function(res) {
	        	checkCloseWindown = 0;
	        	$("#jqxgirdRequirementPurchaseLableItem").jqxGrid('updatebounddata');
	        	$('#alterpopupWindowPurchaseApporal').jqxWindow('close');
	        	$("#notificationSendRequestSuccess").text('${StringUtil.wrapString(uiLabelMap.sentSuccessfully)}');
				$("#jqxNotificationSendRequestSuccess").jqxNotification('open');
	        }
	    });
	}
	
	var checkCloseWindown = -1;
	$("#alterGoBack").click(function () {
		checkCloseWindown = 1;
		$('#alterpopupWindowPurchaseApporal').jqxWindow('close');
	});
	
	$("#alterCancel").click(function () {
		checkCloseWindown = 0;
		$('#alterpopupWindowPurchaseApporal').jqxWindow('close');
	});
	
	$('#alterpopupWindowPurchaseApporal').on('close', function (event) {
		if(checkCloseWindown == 0){
			dataSource = [];
			requirementIdData = [];
			reqItemSeqIdData = []; 
			$('#jqxgirdRequirementPurchaseLableItem').jqxGrid('clearSelection');
			$('#jqxgirdRequirementPurchaseLableItem').jqxGrid('updateBoundData');
		}
		if(checkCloseWindown == 1){
		}
	}); 
	
</script>
