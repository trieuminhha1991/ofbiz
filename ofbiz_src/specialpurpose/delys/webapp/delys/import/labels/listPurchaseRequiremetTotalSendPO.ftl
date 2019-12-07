<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/delys/images/js/util/DateUtil.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/delys/images/js/bootbox.min.js"></script>
<script>
	var statusId = 'LABEL_ITEM_SEND_PO';
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
	
	function reponsiveRowDetails(grid, parentElement) {
	    $(window).bind('resize', function() {
	    	$(grid).jqxGrid({ width: "96%"});
	    });
	    $('#sidebar').bind('resize', function() {
	    	$(grid).jqxGrid({ width: "96%"});
	    });
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
		id="jqxgirdLableItem" filterable="true"
		url="jqxGeneralServicer?sname=JQListPurchaseRequiremetLabelItemToTalSendPO&statusId=LABEL_ITEM_SEND_PO"
		initrowdetails = "true" initrowdetailsDetail=initrowdetailsDetail
		selectionmode= "checkbox" rowselectfunction="rowselectfunction2(event);"
		rowunselectfunction="rowunselectfunction2(event);"
		customcontrol1="fa-envelope open-sans@${uiLabelMap.LogSendRequestPurchaseTotalTitle}@javascript:sendRequestPurchaseTotal()"
	/>		
					

<div id="jqxNotificationSendRequestSuccess" >
	<div id="notificationSendRequestSuccess">
	</div>
</div>

<script>
	$("#jqxNotificationSendRequestSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationSendRequestSuccess", opacity: 0.9, autoClose: true, template: "success" });
	 
	var requirementIdTotal = [];
	function sendRequestPurchaseTotal(){
		if(requirementIdTotal.length == 0){
			bootbox.alert("${StringUtil.wrapString(uiLabelMap.LogNotSelectedRequirementAppore)}");
		}else{
			bootbox.confirm("${uiLabelMap.AreYouSureSent}",function(result){ 
    			if(result){	
    				var requirementData = [];
    				for(var i in requirementIdTotal){
    					requirementData.push(requirementIdTotal[i].requirementId);
    				}
    				
    				sendRequirementTotal({
    					requirementData: requirementData,
    	            	roleTypeId: 'PO_DEPT',
    					sendMessage: '${uiLabelMap.NewPurchaseRequirementMustBeApprove}',
    					action: "findOrderDis",
    				}, 'sendPurchaseRequirementTotalToPO', 'jqxgrid');
    			}
    		});
		}
	} 
	
	function sendRequirementTotal(jsonObject, url, jqxgrid) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        success: function(res) {
        		$("#jqxgirdLableItem").jqxGrid('updatebounddata');
	        	$("#notificationSendRequestSuccess").text('${StringUtil.wrapString(uiLabelMap.sentSuccessfully)}');
				$("#jqxNotificationSendRequestSuccess").jqxNotification('open');
				requirementIdTotal = [];
	        }
	    });
	}
	
	function rowselectfunction2(event){
		var args = event.args;
		if(typeof event.args.rowindex != 'number'){
            var rowBoundIndex = args.rowindex;
	    	if(rowBoundIndex.length == 0){
	    		requirementIdTotal = [];
	    	}else{
	    		for ( var x in rowBoundIndex) {
		    		var rowID = $('#jqxgirdLableItem').jqxGrid('getRowId', rowBoundIndex[x]);
    		        var data = $('#jqxgirdLableItem').jqxGrid('getrowdatabyid', rowID);
    		        requirementIdTotal.push(data);
				}
	    	}
        }else{
        	var tmpArray = event.args.rowindex;
	        var rowID = $('#jqxgirdLableItem').jqxGrid('getRowId', tmpArray);
	        var data = $('#jqxgirdLableItem').jqxGrid('getrowdatabyid', rowID);
	        requirementIdTotal.push(data);
        }
    }
	
	function rowunselectfunction2(event){
		var args = event.args;
	    if(typeof event.args.rowindex != 'number'){
	    	var rowBoundIndex = args.rowindex;
	    	for ( var x in rowBoundIndex) {
	    		var rowID = $('#jqxgirdLableItem').jqxGrid('getRowId', rowBoundIndex[x]);
	    		var data = $('#jqxgirdLableItem').jqxGrid('getrowdatabyid', rowID);
	    		var ii = requirementIdTotal.indexOf(data);
	    		requirementIdTotal.splice(ii, 1);
			}
	    }else{
	    	var tmpArray = event.args.rowindex;
	        var rowID = $('#jqxgirdLableItem').jqxGrid('getRowId', tmpArray);
	        var data = $('#jqxgirdLableItem').jqxGrid('getrowdatabyid', rowID);
	        var ii = requirementIdTotal.indexOf(data);
    		requirementIdTotal.splice(ii, 1);
	    }
    }
	
</script>