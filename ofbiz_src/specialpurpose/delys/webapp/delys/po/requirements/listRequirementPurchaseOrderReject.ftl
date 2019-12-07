<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/delys/images/js/util/DateUtil.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/delys/images/js/bootbox.min.js"></script>
<script>
	function reponsiveRowDetails(grid, parentElement) {
	    $(window).bind('resize', function() {
	    	$(grid).jqxGrid({ width: "96%"});
	    });
	    $('#sidebar').bind('resize', function() {
	    	$(grid).jqxGrid({ width: "96%"});
	    });
	}
	
	<#assign facilities = delegator.findList("Facility", null, null, null, null, false)>
	var listFacilityData = [
							<#if facilities?exists>
								<#list facilities as item>
									{
										facilityId: "${item.facilityId?if_exists}",
										description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
									},
								</#list>
							</#if>
		                      ];
	var mapFacilityData = {
						<#if listFacilityData?exists>
							<#list listFacilityData as item>
								"${item.facilityId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
							</#list>
						</#if>	
					};
	
	var facilityData = new Array();
	<#list facilities as item>
		var row = {};
		row['facilityId'] = '${item.facilityId?if_exists}';
		row['description'] = '${item.description?if_exists}';
		facilityData[${item_index}] = row;
	</#list>
	
	function getDescriptionByFacilityId(facilityId) {
		for ( var x in facilityData) {
			if (facilityId == facilityData[x].facilityId) {
				return facilityData[x].description;
			}
		}
	}
	
	<#assign packingUoms = delegator.findList("Uom", null, null, null, null, false) />
	var listUomData = [
							<#if packingUoms?exists>
								<#list packingUoms as item>
									{
										uomId: "${item.uomId?if_exists}",
										description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
									},
								</#list>
							</#if>
		                      ];
	var mapUomData = {
						<#if listUomData?exists>
							<#list listUomData as item>
								"${item.uomId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
							</#list>
						</#if>	
					};
	
	
	<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "REQUIREMENT_STATUS"), null, null, null, false) />
	
	var listStatusItem = [
							<#if listStatusItem?exists>
								<#list listStatusItem as item>
									{
										statusId: "${item.statusId?if_exists}",
										description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
									},
								</#list>
							</#if>
		                      ];
	var mapStatusItem = {
						<#if listStatusItem?exists>
							<#list listStatusItem as item>
								"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
							</#list>
						</#if>	
					};
	
	<#assign listProductItem = delegator.findList("Product", null, null, null, null, false) />
	
	var listProductData = [
							<#if listProductItem?exists>
								<#list listProductItem as item>
									{
										productId: "${item.statusId?if_exists}",
										internalName: "${StringUtil.wrapString(item.get("internalName", locale)?if_exists)}"
									},
								</#list>
							</#if>
		                      ];
	var mapProductIdData = {
						<#if listProductData?exists>
							<#list listProductData as item>
								"${item.productId?if_exists}": "${StringUtil.wrapString(item.get("internalName", locale)?if_exists)}",
							</#list>
						</#if>	
					};
	
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
	
	var productData = 
	[
		<#list listProductItem as product>
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
	var listUomTranferAddRequirementItem = [];
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
	            showtoolbar:true,
		 		 editable:false,
		 		 editmode:\"click\",
		 		 theme: 'olbius',
		 		 showheader: true,
		 		 localization: getLocalization(),
		 		 rendertoolbar: function (toolbar) {
		 			var container = $(\"<div style='overflow: hidden; position: relative; margin: 5px;'></div>\");
                   var editButton = $(\"<div style='float: right;' class='btn btn-mini btn-primary icon-edit open-sans'>${uiLabelMap.DSEdit}</span></div>\");
                   var deleteButton = $(\"<div style='float: right; margin-right: 10px;' class='btn btn-mini btn-danger icon-trash open-sans'>${uiLabelMap.DSDelete}</span></div>\");
                   container.append(editButton);
                   container.append(deleteButton);
                   toolbar.append(container);
                   editButton.jqxButton();
                   deleteButton.jqxButton();
                   // edit row.
                   editButton.click(function (event) {
                   	var selectedrowindex = grid.jqxGrid('getselectedrowindex');
                       var rowscount = grid.jqxGrid('getdatainformation').rowscount;
                       var id = grid.jqxGrid('getrowid', selectedrowindex);
                       if(id == null){
                       	bootbox.dialog(\"${uiLabelMap.LogCheckEditItemInContactMech}!\", [{
                               \"label\" : \"OK\",
                               \"class\" : \"btn btn-primary standard-bootbox-bt\",
                               \"icon\" : \"fa fa-check\",
                               }]
                           );
                           return false;
                       }else{
                    	   	var data = grid.jqxGrid('getrowdatabyid', id);
                    	   	var requirementId = data.requirementId;
                    	   	var reqItemSeqId = data.reqItemSeqId;
                    	   	var productId = data.productId;
                    	   	var quantity = data.quantity;
                    	   	var quantityUomId = data.quantityUomId;
                    	   	editRequirementItemPurchaseOrderToPo(requirementId, reqItemSeqId, productId, quantity, quantityUomId);
		                    grid.jqxGrid('clearselection');
                       }
                   });
                   // delete selected row.
                   deleteButton.click(function (event) {
                       var selectedrowindex = grid.jqxGrid('getselectedrowindex');
                       var rowscount = grid.jqxGrid('getdatainformation').rowscount;
                       var id = grid.jqxGrid('getrowid', selectedrowindex);
                       if(id == null){
                       	bootbox.dialog(\"${uiLabelMap.LogCheckDeleteItemInContactMech}!\", [{
                               \"label\" : \"OK\",
                               \"class\" : \"btn btn-primary standard-bootbox-bt\",
                               \"icon\" : \"fa fa-check\",
                               }]
                           );
                           return false;
                       }else{
                    	   var data = grid.jqxGrid('getrowdatabyid', id);
                    	   var requirementId = data.requirementId;
                    	   var reqItemSeqId = data.reqItemSeqId;
                    	   deleteRequirementItemPurchaseOrderToPO(requirementId, reqItemSeqId);
	                       grid.jqxGrid('clearselection');
                       }
                   });
		 		 },
		 		 selectionmode:\"singlerow\",
		 		 pageable: true,
	            columns: [
							{ text: '${uiLabelMap.accProductName}', datafield: 'productId', align: 'center', width: 300, pinned: true,
								cellsrenderer: function (row, column, value){
									if(value){
										return '<span>' + getDescriptionByProductId(value) + '<span>';
									}
								}
							},
							{ text: '${uiLabelMap.Quantity}', datafield: 'quantity', align: 'center', cellsalign: 'right',
								cellsrenderer: function(row, colum, value){
									if(value){
										return '<span style=\"text-align: right;\">' + value.toLocaleString('${locale}') + '</span>';
									}
							    }, 
							},
							{ text: '${uiLabelMap.UnitProduct}', datafield: 'quantityUomId', align: 'center',
								cellsrenderer: function (row, column, value){
									if(value){
										return '<span>' + getDescriptionByUomId(value) + '<span>';
									}
								}
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
					{ name: 'partyId', type: 'string'},
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
					{ text: '${uiLabelMap.LogRequirePurchaseByPartyGroup}', datafield: 'partyId', align: 'center',
						cellsrenderer: function(row, column, value){
							  var partyName = value;
							  $.ajax({
									url: 'getPartyName',
									type: 'POST',
									data: {partyId: value},
									dataType: 'json',
									async: false,
									success : function(data) {
										if(!data._ERROR_MESSAGE_){
											partyName = data.partyName;
										}
							        }
								});
							  return '<span title' + value + '>' + partyName + '</span>';
			        	}, 
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.LogRequirePurchaseCreatedDate)}', datafield: 'createdDate', align: 'left', columntype: 'datetimeinput', editable: false, cellsformat: 'dd/MM/yyyy', filtertype:'range'},
					{ text: '${uiLabelMap.Status}', dataField: 'statusId',
						cellsrenderer: function (row, column, value){
							if(value){
								return '<span>' + mapStatusItem[value] + '<span>';
							}
						}
					},
				"/>
	<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true"
		id="jqxgirdRequirement" addrefresh="true"  filterable="true"
		url="jqxGeneralServicer?sname=JQGetListRequirementPurchaseOrderRejectByPO"
		mouseRightMenu="true" contextMenuId="menuSendRequest" selectionmode= "checkbox" rowselectfunction="rowselectfunction2(event);"
		rowunselectfunction="rowunselectfunction2(event);"
		customcontrol1="fa-envelope open-sans@${uiLabelMap.LogSendRequestPurchaseTotalTitle}@javascript:sendRequestPurchaseTotal()"
		initrowdetails = "true" initrowdetailsDetail=initrowdetailsDetail
	/>	
				
					
<div id="alterpopupWindowEditRequirementItem" class="hide">
	<div class="row-fluid">
		${uiLabelMap.LogAddRequestPurchaseLabelProduct}
	</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
	    	<div class="row-fluid">
		    	<div class="row-fluid">
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<label class="asterisk"> ${uiLabelMap.ProductName}</label>
						</div>
						<div class="span7">
							<div id="productIdEdit" style="width: 100%" class="green-label">
								<div id="jqxgridListProduct">
					            </div>
							</div>
						</div>
					</div>
	    		</div>
	    		<div class="row-fluid">
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<label class="asterisk">${uiLabelMap.LogQuantityPurchaseLabelItem} </label>
						</div>
						<div class="span7">
							<div id="quantityEdit" style="width: 100%"></div>
						</div>
					</div>
				</div>
				<div class="row-fluid">
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<label class="asterisk">${uiLabelMap.Unit} </label>
						</div>
						<div class="span7">
							<div id="quantityUomIdEdit" style="width: 100%"></div>
						</div>
					</div>
				</div>
		    </div>
		</div>
		<div class="form-action">
	        <div class='row-fluid'>
	            <div class="span12 margin-top20" style="margin-bottom:10px;">
	                <button id="editButtonCancel" class='btn btn-danger form-action-button pull-right' style="margin-right:20px !important;"><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	                <button id="editButtonSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	            </div>
	        </div>
	    </div>
	</div>
</div>

<div id="alterpopupWindowAddProduct" class="hide">
	<div class="row-fluid">
		${uiLabelMap.LogAddRequestPurchaseLabelProduct}
	</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
	    	<div class="row-fluid">
	    		<div>
	    		    <div style="margin: 20px 0px 10px 20px !important;"><div id="jqxgridProductListAddRequirement"></div></div>
	    	    </div>
		    </div>
		</div>
		<div class="form-action">
	        <div class='row-fluid'>
	            <div class="span12 margin-top20" style="margin-bottom:10px;">
	                <button id="addProductButtonCancel" class='btn btn-danger form-action-button pull-right' style="margin-right:20px !important;"><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	                <button id="addProductButtonSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	            </div>
	        </div>
	    </div>
	</div>
</div>

<div id='menuSendRequest' style="display:none;">
	<ul>
	    <li><i class="fa-trash"></i>&nbsp;&nbsp;${uiLabelMap.DSDeleteRowGird}</li>
	    <li><i class="fa fa-plus"></i>&nbsp;&nbsp;${uiLabelMap.AddNewProductIdForLocation}</li>
	</ul>
</div>

<div id="jqxNotificationSendRequestSuccess" >
	<div id="notificationSendRequestSuccess">
	</div>
</div>

<script>
	$('#document').ready(function(){
		loadProduct();
		loadProductListAddRequiremtItem();
	});
	$("#jqxNotificationSendRequestSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationSendRequestSuccess", opacity: 0.9, autoClose: true, template: "success" });
	$("#menuSendRequest").jqxMenu({ width: 170, autoOpenPopup: false, mode: 'popup', theme: theme});
	
	$("#alterpopupWindowAddProduct").jqxWindow({
		maxWidth: 1500, minWidth: 950, height:350 ,minHeight: 300, maxHeight: 1200, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#addProductButtonCancel"), modalOpacity: 0.7, theme:theme           
	});
	
	$("#alterpopupWindowEditRequirementItem").jqxWindow({
		maxWidth: 700, minWidth: 550, height:220 ,minHeight: 100, maxHeight: 1200, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#editButtonCancel"), modalOpacity: 0.7, theme:theme           
	});
	
	
	var listUomIdByLabelItemProduct = [];
	function loadUomIdByLabelItemProduct(){
		listUomIdByLabelItemProduct = [];
		$.ajax({
			url: "loadUomIdByLabelItemProduct",
			type: "POST",
			data: {},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			listUomIdByLabelItemProduct = data["listUomIdByLabelItemProduct"];
		});
	}
	
	var requirementIdToAddProductId = "";
	$("#menuSendRequest").on('itemclick', function (event) {
        var args = event.args;
        var rowindex = $("#jqxgirdRequirement").jqxGrid('getselectedrowindex');
        var dataRecord = $("#jqxgirdRequirement").jqxGrid('getrowdata', rowindex);
        var reqId = dataRecord.requirementId;
        if ($.trim($(args).text()) == "${StringUtil.wrapString(uiLabelMap.DSDeleteRowGird)}") {
        	bootbox.confirm("${uiLabelMap.LogNotificationBeforeDelete}",function(result){ 
    			if(result){	
    				deleteRequirementPurchaseOrderToPO(reqId);
    			}
    		});
        }
        else{
        	requirementIdToAddProductId = reqId;
        	$('#alterpopupWindowAddProduct').jqxWindow('setTitle', '${StringUtil.wrapString(uiLabelMap.LogAddProductForRequirement)}: ' + reqId);
        	$("#alterpopupWindowAddProduct").jqxWindow('open'); 
        }
        $('#jqxgirdRequirement').jqxGrid('clearselection');
    });
	 
	function deleteRequirementPurchaseOrderToPO(reqId){
		$.ajax({
			url: "deleteRequirementPurchaseOrderToPO",
			type: "POST",
			data: {requirementId: reqId},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			$("#jqxgirdRequirement").jqxGrid('updatebounddata');
        	$("#notificationSendRequestSuccess").text('${StringUtil.wrapString(uiLabelMap.DSNotifiDeleteSucess)}');
			$("#jqxNotificationSendRequestSuccess").jqxNotification('open');
		});
	} 
	 
	function sendRequirement(jsonObject, url, jqxgrid) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        success: function(res) {
	        	$("#jqxgirdRequirement").jqxGrid('updatebounddata');
	        	$("#notificationSendRequestSuccess").text('${StringUtil.wrapString(uiLabelMap.sentSuccessfully)}');
				$("#jqxNotificationSendRequestSuccess").jqxNotification('open');
	        }
	    });
	}
	 
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
    					sendMessage: '${uiLabelMap.NewPurchaseOrderToPOMustBeApprove}',
    					action: "getListRequirementPurchaseOrder",
    				}, 'sendRequirementPurchaseOrderToPO', 'jqxgrid');
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
        		$("#jqxgirdRequirement").jqxGrid('updatebounddata');
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
		    		var rowID = $('#jqxgirdRequirement').jqxGrid('getRowId', rowBoundIndex[x]);
    		        var data = $('#jqxgirdRequirement').jqxGrid('getrowdatabyid', rowID);
    		        requirementIdTotal.push(data);
				}
	    	}
        }else{
        	var tmpArray = event.args.rowindex;
	        var rowID = $('#jqxgirdRequirement').jqxGrid('getRowId', tmpArray);
	        var data = $('#jqxgirdRequirement').jqxGrid('getrowdatabyid', rowID);
	        requirementIdTotal.push(data);
        }
    }
	
	function rowunselectfunction2(event){
		var args = event.args;
	    if(typeof event.args.rowindex != 'number'){
	    	var rowBoundIndex = args.rowindex;
	    	for ( var x in rowBoundIndex) {
	    		var rowID = $('#jqxgirdRequirement').jqxGrid('getRowId', rowBoundIndex[x]);
	    		var data = $('#jqxgirdRequirement').jqxGrid('getrowdatabyid', rowID);
	    		var ii = requirementIdTotal.indexOf(data);
	    		requirementIdTotal.splice(ii, 1);
			}
	    }else{
	    	var tmpArray = event.args.rowindex;
	        var rowID = $('#jqxgirdRequirement').jqxGrid('getRowId', tmpArray);
	        var data = $('#jqxgirdRequirement').jqxGrid('getrowdatabyid', rowID);
	        var ii = requirementIdTotal.indexOf(data);
    		requirementIdTotal.splice(ii, 1);
	    }
    }
	
	
	function deleteRequirementItemPurchaseOrderToPO(requirementId, reqItemSeqId){
		bootbox.confirm("${uiLabelMap.LogNotificationBeforeDelete}",function(result){ 
			if(result){	
				$.ajax({
					url: "deleteRequirementItemPurchaseOrderToPO",
					type: "POST",
					data: {requirementId: requirementId, reqItemSeqId: reqItemSeqId},
					dataType: "json",
					success: function(data) {
					}
				}).done(function(data) {
					$("#jqxgirdRequirement").jqxGrid('updatebounddata');
		        	$("#notificationSendRequestSuccess").text('${StringUtil.wrapString(uiLabelMap.DSNotifiDeleteSucess)}');
					$("#jqxNotificationSendRequestSuccess").jqxNotification('open');
				});
			}
		});
	}
	
	function loadProduct(){
    	var listProduct;
    	$.ajax({
			url: "loadProduct",
			type: "POST",
			data: {},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			listProduct = data["listProduct"];
			bindingDataToJqxGirdProductList(listProduct);
		});
    }
	
	function bindingDataToJqxGirdProductList(listProduct){
 	    var sourceProduct =
 	    {
 	        datafields:[{name: 'productId', type: 'string'},
 	            		{name: 'internalName', type: 'string'},
 	            		{name: 'QOH', type: 'number'},
 	            		{ name: 'ATP', type: 'number' },
         				],
 	        localdata: listProduct,
 	        datatype: "array",
 	    };
 	    var dataAdapterProduct = new $.jqx.dataAdapter(sourceProduct);
 	    $("#jqxgridListProduct").jqxGrid({
 	        source: dataAdapterProduct,
 	        filterable: true,
	        showfilterrow: true,
	        theme: theme,
	        autoheight:true,
	        pageable: true,
 	        columns: [	
 	                  	{text: '${uiLabelMap.LogProductId}', datafield: 'productId', width: '150'},
 	          			{text: '${uiLabelMap.ProductName}', datafield: 'internalName', width: '200'},
 	          			{text: '${uiLabelMap.LogATPTotal}', datafield: 'ATP',
 	          				cellsrenderer: function(row, column, value){
 	                      	 if (value){
 	                      		 return '<span style=\"text-align: right\">' + value.toLocaleString(value) +'</span>';
 	                      	 }
 	                       },
 	          			},
 	          			{text: '${uiLabelMap.LogQOHTotal}', datafield: 'QOH',
 	          				cellsrenderer: function(row, column, value){
 	                      	 if (value){
 	                      		 return '<span style=\"text-align: right\">' + value.toLocaleString(value) +'</span>';
 	                      	 }
 	                       },
 	          			},
 	          		 ]
 	    });
    }
	
	function loadProductListAddRequiremtItem(){
    	var listProduct;
    	$.ajax({
			url: "loadProduct",
			type: "POST",
			data: {},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			listProduct = data["listProduct"];
			bindingDataToJqxGirdProductListAddRequiremtItem(listProduct);
		});
    }
	
	function bindingDataToJqxGirdProductListAddRequiremtItem(listProduct){
 	    var sourceProduct =
 	    {
 	        datafields:[{name: 'productId', type: 'string'},
 	            		{name: 'internalName', type: 'string'},
 	            		{name: 'QOH', type: 'number'},
 	            		{name: 'ATP', type: 'number' },
 	            		{name: 'quantity', type: 'number' },
 	            		{name: 'quantityUomId', type: 'string' },
         				],
 	        localdata: listProduct,
 	        datatype: "array",
 	    };
 	    var dataAdapterProduct = new $.jqx.dataAdapter(sourceProduct);
 	    $("#jqxgridProductListAddRequirement").jqxGrid({
 	        source: dataAdapterProduct,
 	        filterable: true,
	        showfilterrow: true,
	        theme: theme,
	        width: 890,
	        autoheight: true,
	        pageable: true,
	        editable: true,
	        selectionmode: 'checkbox',
 	        columns: [	
 	                  	{text: '${uiLabelMap.LogProductId}', datafield: 'productId', width: '200', editable: false,},
 	          			{text: '${uiLabelMap.ProductName}', datafield: 'internalName', width: '200', editable: false,},
 	          			{text: '${uiLabelMap.LogATPTotal}', datafield: 'ATP', editable: false,
 	          				cellsrenderer: function(row, column, value){
 	                      	 if (value){
 	                      		 return '<span style=\"text-align: right\">' + value.toLocaleString(value) +'</span>';
 	                      	 }
 	                       },
 	          			},
 	          			{text: '${uiLabelMap.LogQOHTotal}', datafield: 'QOH',
 	          				cellsrenderer: function(row, column, value){
 	                      	 if (value){
 	                      		 return '<span style=\"text-align: right\">' + value.toLocaleString(value) +'</span>';
 	                      	 }
 	                       },
 	          			},
 	          			{text: '${uiLabelMap.LogQuantityPurchaseLabelItem}', datafield: 'quantity', width: '150', align: 'center', editable: true, filterable: false, cellsalign: 'right', columntype: 'numberinput',
 	          				validation: function (cell, value) {
 	          					if (value < 0) {
 	          						return { result: false, message: '${uiLabelMap.QuantityMustBeGreateThanZero}'};
 	          					}
 	          					return true;
 	          				},
 	          				createeditor: function (row, cellvalue, editor) {
 	          					editor.jqxNumberInput({spinButtons: true , spinMode: 'simple',  min:0, decimalDigits: 0 });
 	          				},
 	          				cellsrenderer: function(row, column, value){
 	          					if (value){
 	          						return '<span style=\"text-align: right\">' + value.toLocaleString(value) +'</span>';
 	          					}
 	          				},
 	          			},
 	          			{ text: '${uiLabelMap.Unit}', dataField: 'quantityUomId', align: 'center', cellsalign: 'right', filterable: false, editable: true ,columntype: 'dropdownlist', 
	 	  					 cellsrenderer: function (row, column, value) {
	 	  						 if (value){
	 	                      		 return '<span style=\"text-align: right\">' + getDescriptionByUomId(value) +'</span>';
	 	                      	 }
	 	  					 },
	 	  					 cellbeginedit: function (row, datafield, columntype) {
	 	  						 var productIdDataSource = $('#jqxgridProductListAddRequirement').jqxGrid('getrowdata', row);
	 	  						 var productId = productIdDataSource.productId;
	 	  						 $.ajax({
	 	  								url: 'getConfigPackingUomIdByProductId',
	 	  								type: 'POST',
	 	  								data: {productId: productId},
	 	  								dataType: 'json',
	 	  								async: false,
	 	  								success : function(data) {
	 	  									listUomTranferAddRequirementItem = data['listUomTranfer'];
	 	  						        }
	 	  						 });
	 	  					 },
	 	  					 initeditor: function (row, cellvalue, editor) {
	 	  						 editor.jqxDropDownList({ source: listUomTranferAddRequirementItem, displayMember: 'description', valueMember: 'uomId', placeHolder: '${StringUtil.wrapString(uiLabelMap.LogPleaseSelect)}',
	 	  						 });
	 	  					 },
	 	  				 },
 	          		 ]
 	    });
    }
	
	$("#productIdEdit").jqxDropDownButton({width: 200});
	$('#productIdEdit').jqxDropDownButton('setContent', '${StringUtil.wrapString(uiLabelMap.LogPleaseSelect)}'); 
	$("#quantityEdit").jqxNumberInput({ width: 200,  spinButtons: true, spinMode: 'simple',  min:0, decimalDigits: 0 });
	$("#quantityUomIdEdit").jqxDropDownList({disabled: true});
	var productIdEditData = "";
	$("#jqxgridListProduct").on('rowselect', function (event) {
        var args = event.args;
        var row = $("#jqxgridListProduct").jqxGrid('getrowdata', args.rowindex);
        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ getDescriptionByProductId(row['productId']) +'</div>';
        $('#productIdEdit').jqxDropDownButton('setContent', dropDownContent);
        productIdEditData = row['productId'];
    });
	
	$('#alterpopupWindowEditRequirementItem').jqxValidator({
	    rules: [
					{ input: '#productIdEdit', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
						rule: function (input, commit) {
							var value = $("#productIdEdit").val();
							if (value) {
								return true;
							}
							return false;
						}
					},
					{ input: '#quantityEdit', message: '${StringUtil.wrapString(uiLabelMap.DSCheckQuantityNegative)}', action: 'change', 
						rule: function (input, commit) {
							var value = $("#quantityEdit").val();
							if (value != 0) {
								return true;
							}
							return false;
						}
					},
					{ input: '#quantityUomIdEdit', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
						rule: function (input, commit) {
							var value = $("#quantityUomIdEdit").val();
							if (value) {
								return true;
							}
							return false;
						}
					},
	           ]
	});
	
	$('#productIdEdit').on('close', function () { 
		$.ajax({
			url: 'getConfigPackingUomIdByProductId',
			type: 'POST',
			data: {productId: productIdEditData},
			dataType: 'json',
			async: false,
			success : function(data) {
				var uomIdDataByProductIdData = data['listUomTranfer'];
				$("#quantityUomIdEdit").jqxDropDownList({placeHolder: '${StringUtil.wrapString(uiLabelMap.LogPleaseSelect)}', autoDropDownHeight: true});
				$("#quantityUomIdEdit").jqxDropDownList({ source: uomIdDataByProductIdData, displayMember: 'description', valueMember: 'uomId', disabled: false});
	        }
		});
	}); 
	
	var reqItemSeqIdData = "";
	var requirementIdData = "";
	var quantityUomIdData = "";
	function editRequirementItemPurchaseOrderToPo(requirementId, reqItemSeqId, productId, quantity, quantityUomId){
		productIdEditData = productId;
		reqItemSeqIdData = reqItemSeqId;
		requirementIdData = requirementId;
		quantityUomIdData = quantityUomId;
		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ getDescriptionByProductId(productId) +'</div>';
		$('#productIdEdit').jqxDropDownButton('setContent', dropDownContent);
        $('#quantityEdit').jqxNumberInput('setDecimal', quantity);
        $("#quantityUomIdEdit").jqxDropDownList('setContent', getDescriptionByUomId(quantityUomId)); 
        $('#alterpopupWindowEditRequirementItem').jqxWindow('setTitle', '${StringUtil.wrapString(uiLabelMap.LogEditRequirement)}: ' + requirementId);
		$("#alterpopupWindowEditRequirementItem	").jqxWindow('open');
	}
	
	$("#editButtonSave").click(function () {
		var validate = $('#alterpopupWindowEditRequirementItem').jqxValidator('validate');
		if(validate == true){
			var quantityUomId = $("#quantityUomIdEdit").val();
			var quantity = $("#quantityEdit").val();
			var productId = productIdEditData;
			bootbox.confirm("${uiLabelMap.LogUpdateBootboxConfirmSure}",function(result){ 
				if(result){	
					editRequirementItemByRequirementId(requirementIdData, reqItemSeqIdData, productId, quantity, quantityUomId);
				}
			});
		}
	});
	
	function editRequirementItemByRequirementId(requirementIdData, reqItemSeqIdData, productId, quantity, quantityUomId){
		$.ajax({
			url: "editRequirementItemPurchaseOrderToPo",
			type: "POST",
			data: {requirementId: requirementIdData, reqItemSeqId: reqItemSeqIdData, productId: productId, quantity: quantity, quantityUomId: quantityUomId},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			var value = data["value"];
			if(value == "notEdit"){
				$("#alterpopupWindowEditRequirementItem	").jqxWindow('close');
			}
			if(value == "success"){
				$("#alterpopupWindowEditRequirementItem	").jqxWindow('close');
				$("#notificationSendRequestSuccess").text('${StringUtil.wrapString(uiLabelMap.DSNotifiUpdateSucess)}');
				$("#jqxNotificationSendRequestSuccess").jqxNotification('open');
			}
			if(value == "error"){
				$("#alterpopupWindowEditRequirementItem	").jqxWindow('close');
			}
			$("#jqxgirdRequirement").jqxGrid('updatebounddata');
		});
	}
	
	$('#alterpopupWindowEditRequirementItem').on('open', function (event) { 
		$.ajax({
			url: 'getConfigPackingUomIdByProductId',
			type: 'POST',
			data: {productId: productIdEditData},
			dataType: 'json',
			async: false,
			success : function(data) {
				var uomIdDataByProductId = data['listUomTranfer'];
				$("#quantityUomIdEdit").jqxDropDownList({autoDropDownHeight: true});
				$("#quantityUomIdEdit").jqxDropDownList({ source: uomIdDataByProductId, displayMember: 'description', valueMember: 'uomId', disabled: false});
	        }
		});
		$('#quantityUomIdEdit').val(quantityUomIdData);
	});
	
	$('#alterpopupWindowEditRequirementItem').on('close', function (event) { 
		$('#alterpopupWindowEditRequirementItem').jqxValidator('hide');
		$("#quantityUomIdEdit").jqxDropDownList('clearSelection'); 
		productIdEditData = "";
		reqItemSeqIdData = "";
		requirementIdData = "";
	});
	
	var productIdTotalByRequirementId = [];
	$('#jqxgridProductListAddRequirement').on('rowselect', function (event) 
	{
	    var args = event.args;
	    var rowBoundIndex = args.rowindex;
	    var rowData = args.row;
	    var gridId = $(event.currentTarget).attr("id");
		if(typeof event.args.rowindex != 'number'){
			var tmpArray = event.args.rowindex;
	        for(i = 0; i < tmpArray.length; i++){
	            if(checkRequiredPurchaseOrderAddProduct(tmpArray[i]), gridId){
	                $('#jqxgridProductListAddRequirement').jqxGrid('clearselection');
	                break; // Stop for first item
	            }
	        }
	        
	        if(rowBoundIndex.length == 0){
	        	productIdTotalByRequirementId = [];
	    	}else{
	    		for ( var x in rowBoundIndex) {
		    		var rowID = $('#jqxgridProductListAddRequirement').jqxGrid('getrowid', rowBoundIndex[x]);
    		        var data = $('#jqxgridProductListAddRequirement').jqxGrid('getrowdatabyid', rowID);
    		        productIdTotalByRequirementId.push(data);
				}
	    	}
	        
		}else{
			if(checkRequiredPurchaseOrderAddProduct(event.args.rowindex, gridId)){
	            $('#jqxgridProductListAddRequirement').jqxGrid('unselectrow', event.args.rowindex);
	        }
			
			var tmpArray = event.args.rowindex;
	        var rowID = $('#jqxgridProductListAddRequirement').jqxGrid('getrowid', tmpArray);
	        var data = $('#jqxgridProductListAddRequirement').jqxGrid('getrowdatabyid', rowID);
	        productIdTotalByRequirementId.push(data);
		}
	});
	
	$('#jqxgridProductListAddRequirement').on('rowunselect', function (event) 
	{
		var args = event.args;
	    if(typeof event.args.rowindex != 'number'){
	    	var rowBoundIndex = args.rowindex;
	    	for ( var x in rowBoundIndex) {
	    		var rowID = $('#jqxgridProductListAddRequirement').jqxGrid('getrowid', rowBoundIndex[x]);
	    		var data = $('#jqxgridProductListAddRequirement').jqxGrid('getrowdatabyid', rowID);
	    		var ii = requirementIdTotal.indexOf(data);
	    		requirementIdTotal.splice(ii, 1);
			}
	    }else{
	    	var tmpArray = event.args.rowindex;
	        var rowID = $('#jqxgridProductListAddRequirement').jqxGrid('getrowid', tmpArray);
	        var data = $('#jqxgridProductListAddRequirement').jqxGrid('getrowdatabyid', rowID);
	        var ii = requirementIdTotal.indexOf(data);
	        productIdTotalByRequirementId.splice(ii, 1);
	    }
	});
	
	function checkRequiredPurchaseOrderAddProduct(rowindex, gridId){
		var data = $('#jqxgridProductListAddRequirement').jqxGrid('getrowdata', rowindex);
		if(data == undefined){
	        bootbox.dialog("${uiLabelMap.DLYItemMissingFieldsDlv}", [{
	            "label" : "${uiLabelMap.CommonOk}",
	            "class" : "btn btn-primary standard-bootbox-bt",
	            "icon" : "fa fa-check",
	            "callback": function() {
	            		$('#jqxgridProductListAddRequirement').jqxGrid('begincelledit', rowindex, "quantity");
	                }
	            }]
	        );
	        return true;
		}else{
			var quantity = data.quantity;
			var quantityUomIdToTransfer = data.quantityUomId;
	        if(quantity == 0 || quantity == undefined){
	        	$('#jqxgridProductListAddRequirement').jqxGrid('unselectrow', rowindex);
	            bootbox.dialog("${uiLabelMap.DLYItemMissingFieldsDlv}", [{
	                "label" : "${uiLabelMap.CommonOk}",
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                "callback": function() {
	                		$('#jqxgridProductListAddRequirement').jqxGrid('begincelledit', rowindex, "quantity");
	                    }
	                }]
	            );
	            return true;
	        }else{
	        	if(quantityUomIdToTransfer == undefined){
	        		$('#jqxgridProductListAddRequirement').jqxGrid('unselectrow', rowindex);
	                bootbox.dialog("${uiLabelMap.LogSelectQuantiyUomId}", [{
	                    "label" : "${uiLabelMap.CommonOk}",
	                    "class" : "btn btn-primary standard-bootbox-bt",
	                    "icon" : "fa fa-check",
	                    "callback": function() {
	                    		$('#jqxgridProductListAddRequirement').jqxGrid('begincelledit', rowindex, "quantityUomId");
	                        }
	                    }]
	                );
	                return true;
	            }
	        }
		}
	}
	
	$("#addProductButtonSave").click(function () {
		var row;
		var selectedIndexs = $('#jqxgridProductListAddRequirement').jqxGrid('getselectedrowindexes');
		if(selectedIndexs.length == 0){
			bootbox.dialog("${uiLabelMap.DAYouNotYetChooseProduct}!", [{
                "label" : "${uiLabelMap.CommonOk}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
            return false;
		}else{
			bootbox.confirm("${uiLabelMap.LogAreYouSureAddProductForRequirement}",function(result){ 
				if(result){	
					addProductToRequirementItemByRequirementId();
				}
			});
		}
	});
	
	function addProductToRequirementItemByRequirementId(){
		var productIdDataAdd = [];
		var quantityDataAdd = [];
		var quantityUomIdDataAdd = [];
		for(var i in productIdTotalByRequirementId){
			productIdDataAdd.push(productIdTotalByRequirementId[i].productId);
			quantityDataAdd.push(productIdTotalByRequirementId[i].quantity);
			quantityUomIdDataAdd.push(productIdTotalByRequirementId[i].quantityUomId);
		}
		
		$.ajax({
			url: "addProductToRequirementItemByRequirementId",
			type: "POST",
			data: {requirementId: requirementIdToAddProductId, productIdData: productIdDataAdd, quantityData: quantityDataAdd, quantityUomIdData: quantityUomIdDataAdd},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			$("#alterpopupWindowAddProduct").jqxWindow('close');
			$("#jqxgirdRequirement").jqxGrid('updatebounddata');
        	$("#notificationSendRequestSuccess").text('${StringUtil.wrapString(uiLabelMap.NotifiAddSucess)}');
			$("#jqxNotificationSendRequestSuccess").jqxNotification('open');
		});
	}
	
	$('#alterpopupWindowAddProduct').on('close', function (event) { 
		loadProductListAddRequiremtItem();
		requirementIdToAddProductId = "";
		productIdTotalByRequirementId = [];
		$('#jqxgridProductListAddRequirement').jqxGrid('clearselection');
    });
</script>