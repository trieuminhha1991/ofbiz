<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript">
	ctmId = null;
	address = null;
	var contactMechDataColumn = new Array();
	<#assign facilities = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("ownerPartyId", Static["com.olbius.util.MultiOrganizationUtil"].getCurrentOrganization(delegator)), null, null, null, false)>
	
	var facilityData = [
		<#if facilities?exists>
			<#list facilities as item>
				{
					facilityId: "${item.facilityId?if_exists}",
					description: "${StringUtil.wrapString(item.facilityName?if_exists)}"
				},
			</#list>
		</#if>
	    ];
	var mapFacility = {
		<#if facilities?exists>
			<#list facilities as item>
					"${item.facilityId?if_exists}": "${StringUtil.wrapString(item.facilityName?if_exists)}",
			</#list>
		</#if>
	};
	
	<#assign statusItems = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "REQUIREMENT_STATUS"), null, null, null, false) />
	var statusData = [
		<#if statusItems?exists>
			<#list statusItems as item>
				{
					statusId: "${item.statusId?if_exists}",
					description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
						
				},
			</#list>
		</#if>
      ];
	var mapStatus = {
		<#if statusItems?exists>
			<#list statusItems as item>
					"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get('description', locale)?if_exists)}",
			</#list>
		</#if>
	};
	
	<#assign postalAddress = delegator.findList("FacilityContactMechDetail", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("contactMechPurposeTypeId", "SHIPPING_LOCATION"), null, null, null, false) />
	var postalAddressData = [
     	<#if statusItems?exists>
         	<#list postalAddress as item>
         	{
         		contactMechId: "${item.contactMechId}",
        		description: "${StringUtil.wrapString(item.address1?if_exists)}"
         	},
    		</#list>
     	</#if>
     ];

	<#if (parameters.countryGeoId?has_content)>
	  <#assign countryGeoId = '${parameters.countryGeoId?if_exists}'/>
	<#else>
	  <#assign countryGeoId = ""/>
	</#if>
	
	<#assign listProduct = delegator.findList("Product", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("productTypeId", "FINISHED_GOOD"), null, null, null, false) />
	var mapProduct = {
			<#if listProduct?exists>
				<#list listProduct as item>
						"${item.productId?if_exists}": "${StringUtil.wrapString(item.internalName?if_exists)}",
				</#list>
			</#if>
	};
	var listProduct = [
	<#if listProduct?exists>
		<#list listProduct as item>
			{
				productId: "${item.productId?if_exists}",
				internalName: "${StringUtil.wrapString(item.internalName?if_exists)}"
			},
		</#list>
	</#if>
        ];
</script>
<div id="containerNotify" style="width: 100%; height: 20%; margin-top: 15px; overflow: auto;">
</div>
<#assign dataField="[{ name: 'requirementId', type: 'string'},
			   { name: 'agreementId', type: 'string'},
			   { name: 'orderId', type: 'string'},
			   { name: 'productStoreId', type: 'string'},
			   { name: 'requiredByDate', type: 'date', other: 'Timestamp'},
			   { name: 'statusId', type: 'string'},
			   { name: 'requirementDate', type: 'date', other: 'Timestamp'},
			   { name: 'partyIdTo', type: 'string'},
			   { name: 'sendMessage', type: 'string'},
			   { name: 'action', type: 'string'},
			   { name: 'facilityId', type: 'string'},
			   { name: 'contactMechId', type: 'string'},
			   { name: 'facilityContactMechs', type: 'string'}
			   ]"/>

<#assign columnlist="{text: '${uiLabelMap.SequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
		    cellsrenderer: function (row, column, value) {
		        return '<div style=margin:4px;>' + (row + 1) + '</div>';
		    }
		},{ text: '${StringUtil.wrapString(uiLabelMap.ReceiveDate)}', datafield: 'requirementDate', width: '13%', align: 'center', columntype: 'datetimeinput', editable: false, cellsformat: 'dd/MM/yyyy', filtertype:'range'},">
<#if security.hasPermission("DELIVERY_CREATE", session)>	
<#assign columnlist = columnlist + "{ text: '${StringUtil.wrapString(uiLabelMap.OrderId)}', datafield: 'orderId', width: '10%', align: 'center', editable: false,
						cellsrenderer: function (row, column, value){
							return '<a href=purchaseOrderView?orderId='+value+'>'+value+'</a>';
						}
					},">
	<#else>
<#assign columnlist = columnlist + "{ text: '${StringUtil.wrapString(uiLabelMap.OrderId)}', datafield: 'orderId', width: '10%', align: 'center', editable: false,},">
	</#if>
	<#assign columnlist = columnlist + "{ text: '${StringUtil.wrapString(uiLabelMap.FacilityToReceive)}', datafield: 'facilityId', minwidth: '15%', align: 'center', columntype: 'dropdownlist',
						cellsrenderer: function(row, column, value){
							if (value == 'null') {
								value = null;
							}
							value?value=mapFacility[value]:value='';
							return '<span title=' + value + '>' + value + '</span>'
						},
						initeditor: function (row, cellvalue, editor) {
							var curFacility;
							var index;
							for(var i = 0; i < facilityData.length; i++){
								if(facilityData[i].facilityId == cellvalue){
									curFacility = facilityData[i];
									index = facilityData.indexOf(facilityData[i]);
								}
							}
							if (index > -1) {
								facilityData.splice(index, 1);
							}
							facilityData.unshift(curFacility);
							var sourceDataFacility =
							{
			                   localdata: facilityData,
			                   datatype: 'array'
							};
							var dataAdapterFacility = new $.jqx.dataAdapter(sourceDataFacility);
							editor.jqxDropDownList({ selectedIndex: 0, source: dataAdapterFacility, displayMember: 'description', valueMember: 'facilityId'});
							editor.on('change', function (event){
								var args = event.args;
					     	    if (args) {
					     		    var index = args.index;
					     		    var item = args.item;
					     		    if(item){
					     		       update({
						     			   facilityId: item.value,
						     			   contactMechPurposeTypeId: 'SHIPPING_LOCATION',
					   					}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1');
					     		    }
					     	    }
					        	
					        });
						 },
						 cellbeginedit: function (row, datafield, columntype) {
							 var data = $('#jqxgridReceiptRequirement').jqxGrid('getrowdata', row);
							 if (data.statusId != 'REQ_PROPOSED'){
								 return false;
							 }
					    }
					},"/>
	<#if security.hasPermission("REQ_RECEIVE_UPDATE", session)>
			<#assign columnlist = columnlist + "{ text: '${StringUtil.wrapString(uiLabelMap.FacilityAddress)}', datafield: 'contactMechId', minwidth: '20%', align: 'center', columntype: 'dropdownlist',
						cellsrenderer: function(row, column, value){
							if (value == 'null') {
								return '';
							}
							var data = $('#jqxgridReceiptRequirement').jqxGrid('getrowdata', row);
							if (data.contactMechId){
								for(var i = 0; i < postalAddressData.length; i++){
									if(postalAddressData[i].contactMechId == value){
										return '<span title=' + value + '>' + postalAddressData[i].description + '</span>'
									}
								}
							} else {
								var curFacility = data.facilityId;
								if (!curFacility) {
									return '<span></span>';
								}
								jQuery.ajax({
							        url: 'getFacilityContactMechs',
							        type: 'POST',
							        data: {
							        	facilityId: curFacility,
							        	contactMechPurposeTypeId: 'SHIPPING_LOCATION',
							        },
							        async: false,
							        success: function(res) {
							        	var json = res['listFacilityContactMechs'];
							        	ctmId = json[0]['contactMechId'];
							        	address = json[0]['address1'];
							        }
								});
								$('#jqxgridReceiptRequirement').jqxGrid('setcellvalue', row, 'contactMechId', ctmId);
								return '<span>' + address + '</span>'
							}
						},
						 createeditor: function (row, cellvalue, editor) {
			                   var contactMechData = new Array();
	                   			if(contactMechDataColumn && contactMechDataColumn.length){
	                   				contactMechData = contactMechDataColumn;
	                   			}else {
	                   				var data = $('#jqxgridReceiptRequirement').jqxGrid('getrowdata', row);
	 			                   var contactMechArray = data['facilityContactMechs'];
	 			                   for (var i = 0; i < contactMechArray.length; i++) {
	 				                    var contactMechItem = contactMechArray[i];
	 				                    var row = {};
	 				                    row['contactMechId'] = '' + contactMechItem.contactMechId;
	 				                    row['description'] = '' + contactMechItem.address1;
	 				                    contactMechData[i] = row;
	 			                   }
	                   			}
			                   var sourceDataContactMech =
			                   {
				                   localdata: contactMechData,
				                   datatype: 'array'
			                   };
			                   var dataAdapterContactMech = new $.jqx.dataAdapter(sourceDataContactMech);
			                   editor.jqxDropDownList({ selectedIndex: 0, source: dataAdapterContactMech, autoDropDownHeight: true, displayMember: 'description', valueMember: 'contactMechId'});
						 },
						 cellbeginedit: function (row, datafield, columntype) {
							 var data = $('#jqxgridReceiptRequirement').jqxGrid('getrowdata', row);
							 if (data.statusId != 'REQ_PROPOSED'){
								 return false;
							 }
					    }
					},
					"/>
	<#else>
			<#assign columnlist = columnlist + "{ text: '${StringUtil.wrapString(uiLabelMap.FacilityAddress)}', datafield: 'contactMechId', minwidth: '20%', align: 'center', columntype: 'dropdownlist',
					cellsrenderer: function(row, column, value){
						if (value == 'null') {
							return '';
						}
						for(var i = 0; i < postalAddressData.length; i++){
							if(postalAddressData[i].contactMechId == value){
								return '<span title=' + value + '>' + postalAddressData[i].description + '</span>'
							}
						}
					},
				},
				"/>
	</#if>
<#assign columnlist = columnlist + "{ text: '${StringUtil.wrapString(uiLabelMap.RequiredByDate)}', datafield: 'requiredByDate', width: '13%', align: 'center', columntype: 'datetimeinput', editable: false, cellsformat: 'dd/MM/yyyy',},
		{ text: '${StringUtil.wrapString(uiLabelMap.Status)}', datafield: 'statusId', width: '13%', align: 'center', editable: false,
			cellsrenderer: function(row, column, value){
				value?value=mapStatus[value]:value;
				return '<span title=' + value + '>' + value + '</span>'
			}
		},
		"/>
<#if security.hasPermission("REQ_RECEIVE_CREATE", session)> 	
	<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" initrowdetails="false"
		showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false"
		customcontrol1="icon-plus-sign open-sans@${uiLabelMap.CommonCreateNew}@editReceiptRequirement"
		url="jqxGeneralServicer?sname=JQGetListReceiptRequirements&statusId=${parameters.statusId?if_exists}" contextMenuId="contextMenu" mouseRightMenu="true"
	/>
<#else>
	<#if security.hasPermission("REQ_RECEIVE_UPDATE", session)>	
		<#if parameters.statusId?has_content && parameters.statusId == "REQ_CONFIRMED">
			<@jqGrid filtersimplemode="true" id="jqxgridReceiptRequirement" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
				showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false" editmode="click" updateoffline="true" offmode="true"
				url="jqxGeneralServicer?sname=JQGetListReceiptRequirements&statusId=${parameters.statusId?if_exists}&requirementTypeId=RECEIVE_ORDER_REQ&listAll=${parameters.listAll?if_exists}&countryGeoId=${countryGeoId}" 
			/>
		<#elseif parameters.statusId?has_content && parameters.statusId == "REQ_PROPOSED">
			<@jqGrid selectionmode="checkbox" filtersimplemode="true" id="jqxgridReceiptRequirement" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
				showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true" editmode="click" updateoffline="true" offmode="true"
				url="jqxGeneralServicer?sname=JQGetListReceiptRequirements&statusId=${parameters.statusId?if_exists}&requirementTypeId=RECEIVE_ORDER_REQ&listAll=${parameters.listAll?if_exists}&countryGeoId=${countryGeoId}" 
				customcontrol1="icon-ok@${uiLabelMap.Accepted}@javascript:void(0);@acceptReceiptRequirements()"
				otherParams="facilityContactMechs:S-getFacilityContactMechs(facilityId,contactMechPurposeTypeId*SHIPPING_LOCATION)<listFacilityContactMechs>" 
			/>
		<#else>
    		<@jqGrid selectionmode="checkbox" filtersimplemode="true" id="jqxgridReceiptRequirement" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
                showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true" editmode="click" updateoffline="true" offmode="true"
                url="jqxGeneralServicer?sname=JQGetListReceiptRequirements&requirementTypeId=RECEIVE_ORDER_REQ&listAll=${parameters.listAll?if_exists}&countryGeoId=${countryGeoId}" 
                customcontrol1="icon-ok@${uiLabelMap.Accepted}@javascript:void(0);@acceptReceiptRequirements()"
                otherParams="facilityContactMechs:S-getFacilityContactMechs(facilityId,contactMechPurposeTypeId*SHIPPING_LOCATION)<listFacilityContactMechs>" 
            />
		</#if>
	</#if>
</#if>
<div id='contextMenu' style="display:none;">
	<ul>
		<li id='menuTask'></li>
		<li id='viewDetailOrder'><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.viewDetailOrder}</li>
	</ul>
</div>
	
	
<div id="jqxwindowOrderViewer" style="display: none;">
	<div style="font-size: 20px">${uiLabelMap.ListOrders}</div>
	<div style="overflow: hidden;">
		<div class="row-fluid">
		<div class="span12">
			<div id="jqxGridOrderViewer">
			</div>
		</div>
		</div>
		<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;">
		<div class="row-fluid">
			<div class="span12 margin-top10">
				<button class="btn btn-danger form-action-button pull-right" id='alterCancelViewer'><i class='icon-remove'></i>${uiLabelMap.close}</button>
			</div>
		</div>
    </div>
</div>
    
<div id="confirmPopupWindow" style="display:none; overflow: hidden;">
    <div>${uiLabelMap.confirmRequirement}</div>
    <div style="overflow: hidden;">
    	<div class="row-fluid">
			<div class="span12" style="margin-top: 15px;">
	 			<div class="span4" style="text-align: right;">${uiLabelMap.DateTime}<span style="color:red;"> *</span></div>
	 			<div class="span7"><div id="txtRequirementDate"></div></div>
 			</div>
 			
			<div class="row-fluid">
			<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity: 0.4;">
		 		<div class="span12 margin-top10 no-left-margin">
		 			<button id='alterCancel'  class="btn btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
		 			<button id='saveRequirement'  class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
	            </div>
            </div>
        </div>
    </div>
</div>
    
<div id="jqxNotificationNested">
	<div id="notificationContentNested">
	</div>
</div>
<div id="notifyAcceptId" style="display: none;">
	<div>
		${uiLabelMap.updateSuccessfully}
	</div>
</div>
<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
<script type="text/javascript">
	$('#document').ready(function(){
		$("#notifyAcceptId").jqxNotification({ width: "100%", opacity: 0.9, appendContainer: "#containerNotify",
		    autoOpen: false, animationOpenDelay: 800, autoClose: true, template: "success"
		});
	});
	$("#jqxNotificationNested").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
	
  	var contextMenu = $("#contextMenu").jqxMenu({ theme:'olbius', width: 250, height: 60, autoOpenPopup: false, mode: 'popup'});
    $("#jqxgrid").on('contextmenu', function () {
        return false;
    });
    $('#contextMenu').on('shown', function () {
    	var rowIndexSelected = $('#jqxgrid').jqxGrid('getSelectedRowindex');
		var statusId = $('#jqxgrid').jqxGrid('getcellvalue', rowIndexSelected, "statusId");
		$('#contextMenu').jqxMenu('disable', 'menuTask', false);
		switch (statusId) {
		case "REQ_CREATED":
			$("#menuTask").html("<i class='icon-ok'></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.Approved)}");
			$("#menuTask").attr("onclick","approvedMenuClick()");
//			$("#menuTask").css("display","block");
			break;
		case "REQ_PROPOSED":
			$("#menuTask").text("${StringUtil.wrapString(uiLabelMap.WaitingForAccept)}");
//			$("#menuTask").attr("onclick","sentMenuClick()");
			break;
		case "REQ_ACCEPTED":
			$("#menuTask").html("<i class='icon-edit'></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.confirmRequirement)}");
			$("#menuTask").attr("onclick","confirmRequirement()");
			break;
		case "REQ_CONFIRMED":
			$("#menuTask").html("<i class='icon-edit'></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.confirmRequirementEdit)}");
			$("#menuTask").attr("onclick","confirmRequirement()");
			break;
//		default:
//			$("#menuTask").html("<i class='fa-clock-o'></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.WaitingForImport)}");
//			$('#contextMenu').jqxMenu('disable', 'menuTask', true);
//			break;
		}
    });
    function confirmRequirement(){
    	var rowIndexSelected = $('#jqxgrid').jqxGrid('getSelectedRowindex');
    	var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowIndexSelected);
    	var requirementDate = rowData.requirementDate;
    	$("#confirmPopupWindow").jqxWindow('open');
    	$("#txtRequirementDate").jqxDateTimeInput('setDate', requirementDate);
    }
    
    
    function getListOrderItems(orderId) {
		var listOrderItems = [];
		jQuery.ajax({
	        url: "getListOrderItemsAjax",
	        type: "POST",
	        async: false,
	        data: {orderId: orderId},
	        dataType: 'json',
	        success: function(res) {
	        	listOrderItems = res["listOrderItems"];
	        }
	    }).done(function() {
	    	bindOrderItemsPopup(listOrderItems);
		});
	}
    function bindOrderItemsPopup(listOrderItems) {
		for ( var d in listOrderItems) {
			listOrderItems[d].datetimeManufactured == undefined?listOrderItems[d].datetimeManufactured = null : listOrderItems[d].datetimeManufactured = listOrderItems[d].datetimeManufactured['time'];
			listOrderItems[d].expireDate == undefined?listOrderItems[d].expireDate = null : listOrderItems[d].expireDate = listOrderItems[d].expireDate['time'];
		}
		var orderssource = { 
			  datafields: [
                          	  { name: 'orderId', type:'string' },
                              { name: 'orderItemSeqId', type: 'string' },
                              { name: 'productId', type: 'string' },
                              { name: 'quantity', type: 'number' },
                              { name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
                              { name: 'expireDate', type: 'date', other: 'Timestamp'},
		                  ],
		                                
              localdata: listOrderItems
          }
          var OrderViewerGridAdapter = new $.jqx.dataAdapter(orderssource);
		 $('#jqxGridOrderViewer').jqxGrid({
             source: OrderViewerGridAdapter
		 });
	}
    
    $('#jqxGridOrderViewer').jqxGrid({
        width: '100%',
        height: 312,
        editable: false,
 		selectionmode:"singlerow",
 		theme: 'olbius',
 		pageable: true,
 		pagesize: 9,
        columns: [
           { text: "${uiLabelMap.OrderOrderId}", datafield: "orderId", editable: false, width: 140, hidden: true},
           { text: "${uiLabelMap.orderItemSeqId}", datafield: "orderItemSeqId", editable: false, width: 140, hidden: true},
           { text: "${uiLabelMap.ProductName}", datafield: "productId", minwidth: 200, 
        	   cellsrenderer: function(row, colum, value){
			        return '<span title=' + value + '>' + mapProduct[value] + '</span>';
        	   }
           },
           { text: "${uiLabelMap.OrderQuantityEdit}", datafield: "quantity", editable: true, columntype: "numberinput",width: 150, cellsalign: 'right',
        	   cellsrenderer: function(row, colum, value){
        		   return '<div style=\"text-align: right; margin-right:10px;margin-top: 5px;\">' +value.toLocaleString('${locale}')+ '</div>';
		        },   
           },
           { text: "${uiLabelMap.dateOfManufacture}", datafield: "datetimeManufactured", columntype: "datetimeinput", filtertype: 'date', width: "140", cellsformat: 'dd/MM/yyyy' },
           { text: "${uiLabelMap.ProductExpireDate}", datafield: "expireDate", columntype: "datetimeinput", filtertype: 'date', width: "140", editable: true, cellsformat: 'dd/MM/yyyy' }
        	   
        ]
     });
    
	function acceptReceiptRequirements() {
		var row;
		var selectedIndexs = $('#jqxgridReceiptRequirement').jqxGrid('getselectedrowindexes');
		if (selectedIndexs.length > 0){
			bootbox.confirm("${uiLabelMap.AreYouSureWantToAccepted}",function(result){ 
				if(result){
					var listReceiptRequirements = new Array();
					for(var i = 0; i < selectedIndexs.length; i++){
						var data = $('#jqxgridReceiptRequirement').jqxGrid('getrowdata', selectedIndexs[i]);
						var map = {};
						map['requirementId'] = data.requirementId;
						map['orderId'] = data.orderId;
						map['facilityId'] = data.facilityId;
						map['agreementId'] = data.agreementId;
						map['contactMechId'] = data.contactMechId;
						listReceiptRequirements[i] = map;
					}
					listReceiptRequirements = JSON.stringify(listReceiptRequirements);
					row = { 
							listReceiptRequirements:listReceiptRequirements
			    	  };
					acceptReceiptRequirement({
						listReceiptRequirements: listReceiptRequirements,
					}, 'acceptReceiptRequirements');
				}
			});
		} else {
			bootbox.dialog("${uiLabelMap.MissingItemSeleted}", [{
                "label" : "OK",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
		}
	}
	function acceptReceiptRequirement(jsonObject, url) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        success: function(res) {
	        	$('#jqxgridReceiptRequirement').jqxGrid('updatebounddata');
	        }
	    }).done(function() { 
	    	$("#notifyAcceptId").jqxNotification("open");
		});
	}
	function sentNotify(dataNtf) {
		jQuery.ajax({
		    url: "createNotification",
		    type: "POST",
		    data: dataNtf,
		    success: function(res) {
		    	
		    }
		}).done(function() {
			
		});
	}
	function update(jsonObject, url, data, key, value){
		jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        success: function(res) {
	        	var json = res[data];
	        	contactMechDataColumn = new Array();
	        	for (var x in json){
	        		var row = {};
                    row[key] = '' + json[x][key];
                    row['description'] = '' + json[x][value];
                    contactMechDataColumn.push(row);
	        	}
	        }
		});
	}
	function createReceiptNoteClick(data) {
		data = data.toJson();
		data.requiredByDate = new Date(data.requiredByDate).toTimeStamp();
		data.requirementDate = new Date(data.requirementDate).toTimeStamp();
		executeTask(data, "createReceiptFromRequirement");
	}
	function approvedMenuClick() {
		var rowindex = $('#jqxgrid').jqxGrid('getselectedrowindex');
		var data = $('#jqxgrid').jqxGrid('getrowdata', rowindex);
    	data.action = "getReceiptRequirements";
    	data.sendMessage = "${uiLabelMap.NewReceiptRequirement}";
    	bootbox.confirm("${uiLabelMap.confirmOrderProposed}",function(result){
			if(result){
				executeTask(data, "approveReceiptRequirement");
			}
		});
	}
	function executeTask(data, url) {
		var requirementId;
		jQuery.ajax({
			url: url,
			type: "POST",
			data: data,
			success: function(res) {
				requirementId = res["requirementId"];
	        }
		}).done(function() {
			$('#jqxNotificationNested').jqxNotification('closeLast');
			if (requirementId) {
				$("#jqxNotificationNested").jqxNotification({ template: 'info'});
              	$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
              	$("#jqxNotificationNested").jqxNotification("open");
			}else {
				$("#jqxNotificationNested").jqxNotification({ template: 'error'});
    			$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.DAUpdateError)}");
              	$("#jqxNotificationNested").jqxNotification("open");
			}
			$("#clearfilteringbuttonjqxgrid").click();
		});
	}
//	function createNotification(messages) {
////		var jsonObject = {header: messages};
//		jQuery.ajax({
//	        url: "createNotificationReq",
//	        type: "POST",
//	        data: {header: messages},
//	        async: false,
//	        success: function(res) {
//	        	
//	        }
//	    });
//	}
	$('#toolbarcontainer').append('<button id="aaa" class="btn btn-mini"><i class="fa-cogs"></i></button>');
    $("#txtRequirementDate").jqxDateTimeInput({width: '100%'})
    $('#contextMenu').on('closed', function () {
    	$("#contextMenu").jqxMenu({disabled: false});
    });
//    $("#menuTask").on("click", function() {
//		var rowIndexSelected = $('#jqxgrid').jqxGrid('getSelectedRowindex');
//		var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowIndexSelected);
//		var statusId = rowData.statusId;
//	});
    $("#viewDetailOrder").on("click", function() {
    	var rowIndexSelected = $('#jqxgrid').jqxGrid('getSelectedRowindex');
    	var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowIndexSelected);
    	var orderId = rowData.orderId;
    	getListOrderItems(orderId);
    	$("#jqxwindowOrderViewer").jqxWindow('open');
    });
    $("#jqxwindowOrderViewer").jqxWindow({theme: 'olbius',
	    width: 900, maxWidth: 1845, minHeight: 430,  resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancelViewer"), modalOpacity: 0.7, position: "center"
	});
    $("#confirmPopupWindow").jqxWindow({
        width: 400, height: 145, resizable: false,  isModal: true, autoOpen: false, okButton: $("#saveRequirement"), cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme: 'olbius'
    });
    $("#saveRequirement").on('click', function () {
    	var confirmDate = $("#txtRequirementDate").jqxDateTimeInput('getDate').getTime();
    	var txtDate = $("#txtRequirementDate").val();
    	var rowIndexSelected = $('#jqxgrid').jqxGrid('getSelectedRowindex');
    	var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowIndexSelected);
    	var orderId = rowData.orderId;
    	var requirementId = rowData.requirementId;
    	var facilityId = rowData.facilityId;
    	var facilityName = mapFacility[facilityId];
    	var headerConfirmDate = "${uiLabelMap.headerConfirmDate}";
    	var headerConfirmFacility = "${uiLabelMap.headerConfirmFacility}";
    	$("#confirmPopupWindow").jqxWindow('close');
    	var success;
    	jQuery.ajax({
	        url: "updateOrderRequirement",
	        type: "POST",
	        async: false,
	        data: {orderId: orderId, txtDate: txtDate, requirementId: requirementId, requirementDate: confirmDate, facilityName: facilityName, headerConfirmDate: headerConfirmDate, headerConfirmFacility: headerConfirmFacility},
	        dataType: 'json',
	        success: function(res) {
//	        	listOrderItems = res["listOrderItems"];
	        	success = res["_ERROR_MESSAGE_"];
//	        	var messages = "xac nhan ngay:" +txtDate+"ve kho:" +mapFacility[facilityId];
//	        	createNotification(messages);
	        }
	    }).done(function() {
	    	$('#jqxNotificationNested').jqxNotification('closeLast');
	    	if (success) {
	    		$("#jqxNotificationNested").jqxNotification({ template: 'error'});
    			$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.DAUpdateError)}");
              	$("#jqxNotificationNested").jqxNotification("open");
			} else {
				$("#jqxNotificationNested").jqxNotification({ template: 'info'});
              	$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
              	$("#jqxNotificationNested").jqxNotification("open");
			}
	    	$('#jqxgrid').jqxGrid('updatebounddata');
		});
    });
</script>		  