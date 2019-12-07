	var destContactData = new Array();
	var originContactData = new Array();
	contactMechPurposeTypeId = null;
	$("#addPostalAddressWindow").jqxWindow({
		maxWidth: 640, minWidth: 630, minHeight: 345, maxHeight: 600, cancelButton: $("#newAddrCancelButton"), resizable: true,  isModal: true, autoOpen: false,
	});
	function updateStateProvince(){
		var request = $.ajax({
			  url: "loadGeoAssocListByGeoId",
			  type: "POST",
			  data: {geoId : $("#countryGeoId").val(),
				  },
			  dataType: "json",
			  success: function(data) {
				  var listcontactMechPurposeTypeMap = data["listGeoAssocMap"];
				  var contactMechPurposeTypeId = new Array();
				  var description = new Array();
				  var array_keys = new Array();
				  var array_values = new Array();
				  for(var i = 0; i < listcontactMechPurposeTypeMap.length; i++){
					  
					  for (var key in listcontactMechPurposeTypeMap[i]) {
					      array_keys.push(key);
					      array_values.push(listcontactMechPurposeTypeMap[i][key]);
					  }
					  
				  }
				  
				  var dataTest = new Array();
				  for (var j =0; j < array_keys.length; j++){
							var row = {};
							row['id'] = array_keys[j];
							row['value'] = array_values[j];
							dataTest[j] = row;
				  }
				  if (dataTest.length == 0){
					  var dataEmpty = new Array();
					  $("#stateProvinceGeoId").jqxDropDownList({source: dataEmpty, autoDropDownHeight: true});
					  $("#stateProvinceGeoId").jqxDropDownList('setContent', '${uiLabelMap.CommonNoStatesProvincesExists}'); 
				  } else {
					  $("#stateProvinceGeoId").jqxDropDownList({source: dataEmpty, autoDropDownHeight: false});
					  $("#stateProvinceGeoId").jqxDropDownList({selectedIndex: 0,  source: dataTest, displayMember: 'value', valueMember: 'id'});
					  if ("VNM" == $("#countryGeoId").val()){
						  $("#stateProvinceGeoId").jqxDropDownList('val', "VN-HN");
					  }
				  }
			  }
		}); 
	}
	$("#countryGeoId").on('change', function (event) {
		updateStateProvince();
	});
	$('#addPostalAddressWindow').jqxValidator({
		rules: [
               { input: '#address1', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required' },
               { input: '#postalCode', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required' },
               ]
    });
	
	$('#addPostalAddressWindow').on('close', function (event) {
		$('#address1').val("");
		$('#address2').val("");
		$('#postalCode').val("");
	});
	
	$('#destContactMechId').jqxDropDownList({source: destContactData, selectedIndex: 0, width: 200, theme: theme, displayMember: 'description', valueMember: 'contactMechId'});	
	$('#originContactMechId').jqxDropDownList({source: originContactData, selectedIndex: 0, width: 200, theme: theme, displayMember: 'description', valueMember: 'contactMechId'});
	$('#listWeightUomId').jqxDropDownList({source: weightUomData, selectedIndex: 0, width: 90, theme: theme, displayMember: 'description', valueMember: 'uomId'});
	$('#listWeightUomId').jqxDropDownList('val','WT_kg');
			
	var listImage = [];
	var pathScanFile = null;
	$('#document').ready(function(){
		$('#totalProductWeight').text('0');
		var stateProvinceGeoData = new Array();
		$("#countryGeoId").jqxDropDownList({source: countryData, width: 200, displayMember: "geoName", valueMember: "geoId"});
		$("#countryGeoId").jqxDropDownList('val', 'VNM');
		$("#stateProvinceGeoId").jqxDropDownList({source: stateProvinceGeoData, width: 200, displayMember: "value", valueMember: "id"});
		
		$("#postalCode").jqxInput({width: 195});
		$("#address1").jqxInput({width: 195});
		$("#address2").jqxInput({width: 195});
		updateStateProvince();
	
		$('#originProductStoreId').val(prodStoreData[0].productStoreId);
			
		$('#jqxFileScanUpload').jqxWindow({ width: 400, modalZIndex: 10000, height: 220, isModal: true, autoOpen: false });
		initAttachFile();
		update({
			facilityId: $("#originFacilityId").val(),
			contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
			}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'originContactMechId');
		update({
			facilityId: $("#destFacilityId").val(),
			contactMechPurposeTypeId: "SHIPPING_LOCATION",
			}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'destContactMechId');
	});
	$("#originFacilityId").on('change', function(event){
		update({
			facilityId: $("#originFacilityId").val(),
			contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
			}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'originContactMechId');
	});
	$("#destFacilityId").on('change', function(event){
		update({
			facilityId: $("#destFacilityId").val(),
			contactMechPurposeTypeId: "SHIPPING_LOCATION",
			}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'destContactMechId');
	});
	
	function addOriginFacilityAddress(){
		var originFacilityId = $('#originFacilityId').val();
		if (originFacilityId){
			$('#seletedFacilityId').text($('#originFacilityId').text());
			contactMechPurposeTypeId = "SHIP_ORIG_LOCATION";
			$("#addPostalAddressWindow").jqxWindow("open");
		} else {
			bootbox.dialog("${uiLabelMap.PleaseChooseFacilityBefore}", [{
                "label" : "${uiLabelMap.CommonOk}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
		}
	}
	
	function addDestFacilityAddress(){
		var destFacilityId = $('#destFacilityId').val();
		if (destFacilityId){
			$('#seletedFacilityId').text($('#destFacilityId').text());
			contactMechPurposeTypeId = "SHIPPING_LOCATION";
			$("#addPostalAddressWindow").jqxWindow("open");
		} else {
			bootbox.dialog("${uiLabelMap.PleaseChooseFacilityBefore}", [{
                "label" : "${uiLabelMap.CommonOk}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
		}
	}
	$("#newAddrOkButton").click(function (event) {
		if (!$("#countryGeoId").val()){
			bootbox.dialog("${uiLabelMap.PleaseChooseCountryBefore}", [{
                "label" : "${uiLabelMap.CommonOk}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
		} else if (!$("#countryGeoId").val()){
			bootbox.dialog("${uiLabelMap.PleaseChooseProvinceBefore}", [{
                "label" : "${uiLabelMap.CommonOk}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
		} else {
			var validate = $('#addPostalAddressWindow').jqxValidator('validate');
			if (validate){
				var facilityIdTemp = null;
				if ("SHIP_ORIG_LOCATION" == contactMechPurposeTypeId){
					facilityIdTemp = $("#originFacilityId").val();
				} else {
					facilityIdTemp = $("#destFacilityId").val();
				}
				if (facilityIdTemp){
					bootbox.confirm("${uiLabelMap.DAAreYouSureSave}", "${uiLabelMap.CommonCancel}", "${uiLabelMap.CommonSave}",function(result){ 
						if(result){
							jQuery.ajax({
								url: "createFacilityContactMechPostalAddress",
								type: "POST",
								async: false,
								data: {
									facilityId: facilityIdTemp,
									contactMechTypeId: "POSTAL_ADDRESS", 
									contactMechPurposeTypeId : contactMechPurposeTypeId, 
									address1: $('#address1').val(), 
									address2: $('#address2').val(),
									countryGeoId: $('#countryGeoId').val(),
									stateProvinceGeoId: $('#stateProvinceGeoId').val(),
									postalCode: $('#postalCode').val(),
									},
								success: function(res) {
									$('#addPostalAddressWindow').jqxWindow('close');
									if ("SHIP_ORIG_LOCATION" == contactMechPurposeTypeId){
										update({
											facilityId: $("#originFacilityId").val(),
											contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
											}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'originContactMechId');
									} else {
										update({
											facilityId: $("#destFacilityId").val(),
											contactMechPurposeTypeId: "SHIPPING_LOCATION",
											}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'destContactMechId');
									}
					       	  	}
							});
						}
					});
				} else {
					bootbox.dialog("${uiLabelMap.PleaseChooseFacilityBefore}", [{
		                "label" : "${uiLabelMap.CommonOk}",
		                "class" : "btn btn-primary standard-bootbox-bt",
		                "icon" : "fa fa-check",
		                }]
		            );
				}
			}
		}
	});
	function update(jsonObject, url, data, key, value, id) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        success: function(res) {
	        	var json = res[data];
	            renderHtml(json, key, value, id);
	        }
	    });
	}
	function renderHtml(data, key, value, id){
		var y = "";
		var source = new Array();
		var index = 0;
		for (var x in data){
			index = source.length;
			var row = {};
			row[key] = data[x][key];
			row['description'] = data[x][value];
			source[index] = row;
		}
		if($("#"+id).length){
			$("#"+id).jqxDropDownList('clear');
			$("#"+id).jqxDropDownList({source: source, selectedIndex: 0});
		}
	}
	// upload scan file
	function initAttachFile(){
		$('#attachFile').html('');
		listImage = [];
		$('#attachFile').ace_file_input({
			style:'well',
			btn_choose:'${StringUtil.wrapString(uiLabelMap.DropFileOrClickToChoose)}',
			btn_change:null,
			no_icon:'icon-cloud-upload',
			droppable:true,
			onchange:null,
			thumbnail:'small',
			before_change:function(files, dropped) {
				listImage = [];
				var count = files.length;
				for (var int = 0; int < files.length; int++) {
					var imageName = files[int].name;
					var hashName = imageName.split(".");
					var extended = hashName.pop();
					if (extended == "JPG" || extended == "jpg" || extended == "jpeg" || extended == "gif" || extended == "png") {
						listImage.push(files[int]);
					}
				}
				return true;
			},
			before_remove : function() {
				listImage = [];
				return true;
			}
		});
	}
	$('#uploadOkButton').click(function(){
		saveFileUpload();
	});
	$('#uploadCancelButton').click(function(){
		$('#jqxFileScanUpload').jqxWindow('close');
	});
	$('#jqxFileScanUpload').on('close', function(event){
		initAttachFile();
	});
	function saveFileUpload (){
		var folder = "/delys/logDelivery";
		for ( var d in listImage) {
			var file = listImage[d];
			var dataResourceName = file.name;
			var path = "";
			var form_data= new FormData();
			form_data.append("uploadedFile", file);
			form_data.append("folder", folder);
			jQuery.ajax({
				url: "uploadDemo",
				type: "POST",
				data: form_data,
				cache : false,
				contentType : false,
				processData : false,
				success: function(res) {
					path = res.path;
					pathScanFile = path;
					$('#linkId').html("");
					$('#linkId').attr('onclick', null);
					$('#linkId').append("<a href='"+path+"' onclick='' target='_blank'><i class='fa-file-text-o'></i>'"+dataResourceName+"'</a> <a onclick='removeScanFile()'><i class='fa-remove'></i></a>");
		        }
			}).done(function() {
			});
		}
		$('#jqxFileScanUpload').jqxWindow('close');
	}
	function removeScanFile (){
		pathScanFile = null;
		$('#linkId').html("");
		$('#linkId').attr('onclick', null);
		$('#linkId').append("<a id='linkId' onclick='showAttachFilePopup()'><i class='icon-upload'></i>${uiLabelMap.AttachFileScan}</a>");
	}
	function showAttachFilePopup(){
		$('#jqxFileScanUpload').jqxWindow('open');
	}
	
	function formatFullDate(value) {
		if (value != undefined && value != null && !(/^\s*$/.test(value))) {
			var dateStr = "";
			dateStr += addZero(value.getFullYear()) + '-';
			dateStr += addZero(value.getMonth()+1) + '-';
			dateStr += addZero(value.getDate()) + ' ';
			dateStr += addZero(value.getHours()) + ':';
			dateStr += addZero(value.getMinutes()) + ':';
			dateStr += addZero(value.getSeconds());
			return dateStr;
		} else {
			return "";
		}
	}
	function addZero(i) {
	    if (i < 10) {i = "0" + i;}
	    return i;
	}
    function selectFacility(facilityId){
        $("#originFacilityId").jqxDropDownList('selectItem',facilityId);
        $("#facilityWindow").jqxWindow('close');
    }
    function getFacilityList(){
        var tmpS = $("#jqxgridFAINV").jqxGrid('source');
        tmpS._source.url = "jqxGeneralServicer?sname=getAvailableINV&orderId=${parameters.orderId}";
        $("#jqxgridFAINV").jqxGrid('source', tmpS);
        $("#facilityWindow").jqxWindow('open');
    }
//    var test = false;
    function getInventoryItemTotalByFacility(){
    	var facId = $("#originFacilityId").val();
    	if (facId){
    		var rows = $('#jqxgrid1').jqxGrid('getrows');
    		if (rows && rows.length > 0){
        		for(var i = 0; i < rows.length; i++){
        			(function(i){
        				var data = $('#jqxgrid1').jqxGrid('getrowdata', i);
            			var exp = data.expireDate;
        				var expireDate = formatFullDate(exp);
        				jQuery.ajax({
            				url: "getDetailQuantityInventory",
            				type: "POST",
            				data: {
            					productId: data.productId,
            					expireDate: expireDate,
            					originFacilityId: facId,
            				},
            				success: function(res){
            					setTimeout(function(){
            						if (parseInt(res.availableToPromiseTotal) <= 0){
//                						if (!test){
//                							test = true;
//                							bootbox.dialog("${uiLabelMap.FacilityNotEnoughProduct}!", [{
//                				                "label" : "${uiLabelMap.CommonOk}",
//                				                "class" : "btn btn-primary standard-bootbox-bt",
//                				                "icon" : "fa fa-check",
//                				                }]
//                				            );
//                						}
                						var id = $('#jqxgrid1').jqxGrid('getrowid', i);
                    					$("#jqxgrid1").jqxGrid('setcellvaluebyid', id, "quantityOnHandTotal", res.quantityOnHandTotal);
                    					$("#jqxgrid1").jqxGrid('setcellvaluebyid', id, "availableToPromiseTotal", res.availableToPromiseTotal);
                					} else if(res != undefined){
                						var id = $('#jqxgrid1').jqxGrid('getrowid', i);
                    					$("#jqxgrid1").jqxGrid('setcellvaluebyid', id, "quantityOnHandTotal", res.quantityOnHandTotal);
                    					$("#jqxgrid1").jqxGrid('setcellvaluebyid', id, "availableToPromiseTotal", res.availableToPromiseTotal);
                					}
            					}, 200);
                            },
                            error: function(response){
                            }
            			});
        			}(i));
        		}
    		}
    	}
    }
    
	//Create orderId 
	$("#orderId").text('${parameters.orderId}');
	$('#alterpopupWindow input[name=orderId]').val('${parameters.orderId?if_exists}');
	
	//Set Value for statusId
	$('#alterpopupWindow input[name=statusId]').val('DLV_CREATED');
	
	//Create CurrencyUom
	$('#alterpopupWindow input[name=currencyUomId]').val('${orderHeader.currencyUom?if_exists}');
	
	//Create orderDate
	$('#alterpopupWindow input[name=orderDate]').val('${orderHeader.orderDate?if_exists}');
	
//	//Create ProductStore
//	var prodStoreData = [];
//	<#list listProStore as item>
//		var row = {};
//		<#assign description = StringUtil.wrapString(item.storeName) >
//		row['productStoreId'] = '${item.productStoreId}';
//		row['description'] = '${description}';
//		prodStoreData[${item_index}] = row;
//	</#list>
//	$("#originProductStoreId").jqxDropDownList({source: prodStoreData, selectedIndex: 0, displayMember: 'description', valueMember: 'productStoreId'});
//	
	//Create Order date
	<#assign orderDateDisplay = StringUtil.wrapString(Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderHeader.orderDate?if_exists, "dd/MM/yyyy", locale, timeZone))>
	$('#orderDateDisplay').text('${orderDateDisplay?if_exists}');
	
	//Create partyIdTo Input
	var partyToData = [];
	<#list listPartyTo as item>
		<#assign description = StringUtil.wrapString(StringUtil.wrapString(item.firstName?if_exists) + StringUtil.wrapString(item.middleName?if_exists) + StringUtil.wrapString(item.lastName?if_exists) + StringUtil.wrapString(item.groupName?if_exists))>
		var row = {};
		row['partyId'] = '${item.partyId}';
		row['description'] = '${description}';
		partyToData[${item_index}] = row;
	</#list>
	$("#partyIdTo").jqxDropDownList({selectedIndex: 0, width: 200, dropDownWidth: 200, source: partyToData, theme: theme, displayMember: 'description', valueMember: 'partyId'});
	
	//Create partyIdFrom Input
	var partyFromData = [];
	<#list listPartyFrom as item>
		<#assign description = StringUtil.wrapString(StringUtil.wrapString(item.firstName?if_exists) + StringUtil.wrapString(item.middleName?if_exists) + StringUtil.wrapString(item.lastName?if_exists) + StringUtil.wrapString(item.groupName?if_exists))>
		var row = {};
		row['partyId'] = '${item.partyId}';
		row['description'] = '${description}';
		partyFromData[${item_index}] = row;
	</#list>
	$("#partyIdFrom").jqxDropDownList({selectedIndex: 0, width: 200, dropDownWidth: 200, source: partyFromData, theme: theme, displayMember: 'description', valueMember: 'partyId'});
	
	//Create Origin Facility
	facilityData = [];
	var index = 0;
	for(var i = 0; i < faciData.length; i++){
		if(prodStoreData[0].productStoreId == faciData[i].productStoreId){
			var row = {};
			row['facilityId'] = faciData[i].facilityId;
			row['description'] = faciData[i].description;
			facilityData[index] = row;
			index++;
		}
	}
	$('#originFacilityId').jqxDropDownList({selectedIndex: 0, width: 200, source: facilityData, theme: theme, displayMember: 'description', valueMember: 'facilityId',
	});
	//Load data grid for jqxGrid1
    /*var tmpS = $("#jqxgrid1").jqxGrid('source');
    tmpS._source.url = "jqxGeneralServicer?sname=getListDeliveryItem&facilityId=" + facilityData[0].facilityId + "&orderId=" + "${parameters.orderId?if_exists}";
    $("#jqxgrid1").jqxGrid('source', tmpS);*/
	$('#originFacilityId').on('change', function(event){
		var tmpS = $("#jqxgrid1").jqxGrid('source');
        tmpS._source.url = "jqxGeneralServicer?sname=getListOrderItemDelivery&orderId=${parameters.orderId?if_exists}&facilityId=" + $('#originFacilityId').val();
        $("#jqxgrid1").jqxGrid('source', tmpS);
        $("#jqxgrid1").on("bindingComplete", function (event) {
    		getInventoryItemTotalByFacility();
        });
	});
	
	//Create Destination Facility
	var destFacilitySource = [];
	var index = 0;
	for(var i = 0; i < faciData.length; i++){
		for(var j = 0; j < partyToData.length; j++){
			if(faciData[i].ownerPartyId == partyToData[j].partyId){
				destFacilitySource[index] = faciData[i];
				index +=index;
			}
		}
	}
	$('#destFacilityId').jqxDropDownList({selectedIndex: 0, width: 200, source: destFacilitySource, theme: theme, displayMember: 'description', valueMember: 'facilityId'});
	
	//Create OrderItemType
	var orderItemTypeData = [];
	<#list listOrderItemTypes as item>
		var row = {};
		<#assign orderItemType = delegator.findOne("OrderItemType", {"orderItemTypeId" : item}, true)>
		<#assign description = StringUtil.wrapString(orderItemType.description?if_exists) >
		row['orderItemTypeId'] = '${item}';
		row['description'] = '${description}';
		orderItemTypeData[${item_index}] = row;
	</#list>
	
	$('#estimatedStartDate').jqxDateTimeInput({width: 200});
	$('#estimatedArrivalDate').jqxDateTimeInput({width: 200});
	
	//Create createDate
//	$('#createDate').jqxDateTimeInput({width: 200});
	
//	//Create destContactMechId
//	var destContactData = [];
//	<#list destContacts as item>
//		var row = {};
//		<#assign description = StringUtil.wrapString(item.address1?if_exists) >
//		row['contactMechId'] = "${item.contactMechId?if_exists}";
//		row['description'] = "${description?if_exists}";
//		destContactData[${item_index}] = row;
//	</#list>
//	$('#destContactMechId').jqxDropDownList({source: destContactData, selectedIndex: 0, width: 200, theme: theme, displayMember: 'description', valueMember: 'contactMechId'});
//	
//	//Create originContactMechId
//	var originContactData = [];
//	<#list listPartyFrom as item>
//		<#assign mapCondition = Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", item.partyId, "contactMechTypeId", "POSTAL_ADDRESS", "contactMechPurposeTypeId", "BILLING_LOCATION")>
//		<#assign originContacts = delegator.findList("PartyContactDetailByPurpose", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(mapCondition), null, null, null, false)>
//		<#list originContacts as contact>
//			var row = {};
//			<#assign description = StringUtil.wrapString(contact.address1?if_exists) >
//			row['contactMechId'] = "${contact.contactMechId?if_exists}";
//			row['description'] = "${description}";
//			originContactData[${contact_index}] = row;
//		</#list>
//	</#list>
//	$('#originContactMechId').jqxDropDownList({source: originContactData, selectedIndex: 0, width: 200, theme: theme, displayMember: 'description', valueMember: 'contactMechId'});
//	
	//Create deliveryDate
	$("#deliveryDate").jqxDateTimeInput({width: 200, formatString: 'dd/MM/yyyy', disabled: true});
	$("#deliveryDate").jqxDateTimeInput('val', estimatedDeliveryDate);
	
	//Create noNumber
	$('#no').jqxInput({width: 195});
	$("#alterSave2").click(function () {
	    var row;
        //Get List Delivery Item
        var selectedIndexs = $('#jqxgrid2').jqxGrid('getselectedrowindexes');
        if(selectedIndexs.length == 0){
            bootbox.dialog("${uiLabelMap.DAYouNotYetChooseProduct}!", [{
                "label" : "${uiLabelMap.CommonOk}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
            return false;
        }
        if("DLV_CONFIRMED" == glDeliveryStatusId && !pathScanFile){
            bootbox.dialog("${uiLabelMap.MustUploadScanFile}!", [{
                "label" : "${uiLabelMap.CommonOk}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
            return false;
        }
        bootbox.confirm("${uiLabelMap.DAAreYouSureSave}",function(result){ 
            if(result){  
                $("#popupDeliveryDetailWindow").jqxWindow('close');
        	    var listDeliveryItems = [];
        	    var curDeliveryId = null;
                for(var i = 0; i < selectedIndexs.length; i++){
                    var data = $('#jqxgrid2').jqxGrid('getrowdata', selectedIndexs[i]);
                    var map = {};
                    // Make sure data is completed
                    // FIXME create detail message for following cases
                    /*if(data.statusId == 'DELI_ITEM_EXPORTED'){
                        if(data.actualDeliveredQuantity == 0){
                            bootbox.dialog("${uiLabelMap.DAYouNotYetChooseProduct}!", [{
                                "label" : "${uiLabelMap.CommonOk}",
                                "class" : "btn btn-primary standard-bootbox-bt",
                                "icon" : "fa fa-check",
                                }]
                            );
                            return false;
                        }
                    }else */
                    if(data.statusId == 'DELI_ITEM_CREATED'){
                        if(data.inventoryItemId == null && data.actualExportedQuantity == 0 && data.actualDeliveredQuantity==0){
                            bootbox.dialog("${uiLabelMap.DItemMissingFieldsExp}", [{
                                "label" : "${uiLabelMap.CommonOk}",
                                "class" : "btn btn-primary standard-bootbox-bt",
                                "icon" : "fa fa-check",
                                }]
                            );
                            return false;
                        }
                    }
                    map.fromOrderId = '${parameters.orderId}';
                    map.fromOrderItemSeqId = data.fromOrderItemSeqId;
                    map.inventoryItemId = data.inventoryItemId;
                    map.deliveryId = data.deliveryId;
                    map.deliveryItemSeqId = data.deliveryItemSeqId;
                    map.actualExportedQuantity = data.actualExportedQuantity;
                    map.actualDeliveredQuantity = data.actualDeliveredQuantity;
                    
                    curDeliveryId = data.deliveryId;
                    listDeliveryItems[i] = map;
                }
                $('#jqxgrid2').jqxGrid('showloadelement');
                var listDeliveryItems = JSON.stringify(listDeliveryItems);
                var actualStartDateTmp;
                var actualArrivalDateTmp;
                if ("DLV_CREATED" == glDeliveryStatusId){
                	var tmp = $('#actualStartDate').jqxDateTimeInput('getDate');
                	if (tmp){
                		actualStartDateTmp = tmp.getTime();
                	}
                }
                if ("DLV_CONFIRMED" == glDeliveryStatusId){
                	var tmp = actualArrivalDateTmp = $('#actualArrivalDate').jqxDateTimeInput('getDate');
                	if (tmp){
                		actualArrivalDateTmp = tmp.getTime();
                	}
                }
                row = { 
                        listDeliveryItems:listDeliveryItems,
                        pathScanFile: pathScanFile,
                        deliveryId: curDeliveryId,
                        actualStartDate: actualStartDateTmp,
                    	actualArrivalDate: actualArrivalDateTmp,
                      };
                // call Ajax request to Update Exported or Delivered value
                $.ajax({
                    type: "POST",
                    url: "updateDeliveryItemList",
                    data: row,
                    dataType: "json",
                    async: false,
                    success: function(data){
                        $('#jqxgrid2').jqxGrid('updatebounddata');
                    },
                    error: function(response){
                        $('#jqxgrid2').jqxGrid('hideloadelement');
                    }
                });
                displayEditSuccessMessage('jqxgrid');
            }
        });
	});
	// update the edited row when the user clicks the 'Save' button: Create new Delivery.
	$("#alterCancel").click(function () {
	     $("#alterpopupWindow").jqxWindow('close');
	});
	$("#alterCancel2").click(function () {
	    $("#popupDeliveryDetailWindow").jqxWindow('close');
	});
    $("#alterSave").click(function () {
		var row;
		//Get List Order Item
		var selectedIndexs = $('#jqxgrid1').jqxGrid('getselectedrowindexes');
		if(selectedIndexs.length == 0){
		    bootbox.dialog("${uiLabelMap.DAYouNotYetChooseProduct}!", [{
                "label" : "${uiLabelMap.CommonOk}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
		    return false;
		} 
		bootbox.confirm("${uiLabelMap.DAAreYouSureCreate}",function(result){ 
		    if(result){   
		        $("#alterpopupWindow").jqxWindow('close');
        		var listOrderItems = [];
        		for(var i = 0; i < selectedIndexs.length; i++){
        			var data = $('#jqxgrid1').jqxGrid('getrowdata', selectedIndexs[i]);
        			var map = {};
        			map['orderItemSeqId'] = data.orderItemSeqId;
        			map['orderId'] = "${parameters.orderId}";
        			map['quantity'] = data.requiredQuantityTmp;
        			var exp = data.expireDate;
        			if (exp){
        				map['expireDate'] = exp.getTime();
        			}
        			listOrderItems[i] = map;
        		}
        		var listOrderItems = JSON.stringify(listOrderItems);
        		row = { 
        				orderId: "${parameters.orderId}",
        				currencyUomId:$('#alterpopupWindow input[name=currencyUomId]').val(),
        				statusId:$('#alterpopupWindow input[name=statusId]').val(),
        				originContactMechId:$('#originContactMechId').val(),
        				destFacilityId:$('#destFacilityId').val(),
        				originProductStoreId:$('#originProductStoreId').val(),
        				partyIdTo:$('#partyIdTo').val(),
        				partyIdFrom:$('#partyIdFrom').val(), 
//        				createDate:new Date(nowTimestamp.getTime()),	
        				destContactMechId:$('#destContactMechId').val(),
        				originFacilityId:$('#originFacilityId').val(),
        				deliveryDate:$('#deliveryDate').jqxDateTimeInput('getDate'),
        				estimatedStartDate:$('#estimatedStartDate').jqxDateTimeInput('getDate'),
        				estimatedArrivalDate:$('#estimatedArrivalDate').jqxDateTimeInput('getDate'),
        				no:$('#no').val(),
        				defaultWeightUomId : $('#listWeightUomId').val(),
        				listOrderItems:listOrderItems
            	};
        		$("#jqxgrid").jqxGrid('addRow', null, row, "first");
        		/* Disable for refreshing purpose: 
        		// select the first row and clear the selection.
        		$("#jqxgrid").jqxGrid('clearSelection');                        
        		$("#jqxgrid").jqxGrid('selectRow', 0);  
            	$("#alterpopupWindow").jqxWindow('close');*/
        		var tmpUrl = window.location.href;
        		if(tmpUrl.indexOf("orderId") < 0){
        		    tmpUrl += "?orderId=${orderId}";
        		}
        		if(tmpUrl.indexOf("deliveries-tab") < 0){
        		    tmpUrl += "&activeTab=deliveries-tab";
        		}
        		//window.location.href = tmpUrl; disable for demo.
        		$("#jqxgrid").jqxGrid('updatebounddata'); 
		    }
		});
	});
	
	//handle on change originProductStoreId
//	$("#originProductStoreId").on('change', function(event){
//		 var args = event.args;
//		 var item;
//		 if(args){
//			var item = args.item;
//		 }
//		 var facilityData = [];
//		 var index = 0;
//		 for(var i = 0; i < faciData.length; i++){
//			 if(item.value == faciData[i].productStoreId){
//				 var row = {};
//				 row['facilityId'] = faciData[i].facilityId;
//				 row['description'] = faciData[i].description;
//				 facilityData[index] = row;
//				 index++;
//			 }
//		 }
//		 $('#originFacilityId').jqxDropDownList('clear');
//		 $('#originFacilityId').jqxDropDownList({source: facilityData, selectedIndex: 0});
//	});
	
	//handle on change originFacilityId
	/* Disable for new purpose: $("#originFacilityId").on('change', function(event){
		 var args = event.args;
		 var item;
		 if(args){
			var item = args.item;
			//Create Grid
			alterData.pagenum = "0";
	        alterData.pagesize = "20";
	        alterData.noConditionFind = "Y";
	        alterData.conditionsFind = "N";
	        alterData.orderId = '${parameters.orderId?if_exists}';
	        alterData.facilityId = item.value;
	        $("#jqxgrid1").jqxGrid("updatebounddata");
		 }
	});*/
</script>
<script type="text/javascript">
	$("#popupDeliveryDetailWindow").jqxWindow({
	    maxWidth: 1500, minWidth: 945, modalZIndex: 10000, zIndex:10000, minHeight: 680, maxHeight: 1200, resizable: true, cancelButton: $("#alterCancel2"), keyboardNavigation: true, keyboardCloseKey: 15,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:theme           
	});
	initGridjqxgrid2();
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "DELIVERY_ITEM_STATUS"), null, null, null, false)>
	var dlvItemStatusData = [];
	<#list statuses as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("description", locale)) />
		row['statusId'] = '${item.statusId}';
		row['description'] = '${description}';
		dlvItemStatusData[${item_index}] = row;
	</#list>
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var quantityUomData = [];
	<#list uoms as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("description", locale)) />
		row['uomId'] = '${item.uomId}';
		row['description'] = '${description}';
		quantityUomData[${item_index}] = row;
	</#list>
		
	$('#popupDeliveryDetailWindow').on('close', function (event) { 
		if($("#jqxgrid").is('*[class^="jqx"]')){
			$("#jqxgrid").jqxGrid('updatebounddata');
		}
		if($("#jqxgridDlv").is('*[class^="jqx"]')){
			$("#jqxgridDlv").jqxGrid('updatebounddata');
		}
		$('#jqxgrid2').jqxGrid('clearselection');
		$('#actualStartLabel').html("");
		$('#actualStartDate').html("");
//		$('#actualStartDate').css("border-width", "0px");
		$('#actualArrivalLabel').html("");
		$('#actualArrivalDate').html("");
//		$('#actualArrivalDate').css("border-width", "0px");
	});
	
	function functionAfterUpdate2(){
	    var tmpS = $("#jqxgrid2").jqxGrid('source');
	    tmpS._source.url = "jqxGeneralServicer?sname=getListDeliveryItem&deliveryId=" + glDeliveryId;
	    $("#jqxgrid2").jqxGrid('source', tmpS);
	}
	function rowselectfunction(event){
	    if(typeof event.args.rowindex != 'number'){
	        var tmpArray = event.args.rowindex;
	        for(i = 0; i < tmpArray.length; i++){
	            if(checkRequiredData2(tmpArray[i])){
	                $('#jqxgrid2').jqxGrid('clearselection');
	                break; // Stop for first item
	            }
	        }
	    }else{
	        if(checkRequiredData2(event.args.rowindex)){
	            $('#jqxgrid2').jqxGrid('unselectrow', event.args.rowindex);
	        }
	    }
	}
	function checkRequiredData2(rowindex){
	    var data = $('#jqxgrid2').jqxGrid('getrowdata', rowindex);
	    if(data.statusId == 'DELI_ITEM_EXPORTED'){
	        if(data.actualDeliveredQuantity == 0){
	            $('#jqxgrid2').jqxGrid('unselectrow', rowindex);
	            bootbox.dialog("${uiLabelMap.DLYItemMissingFieldsDlv}", [{
	                "label" : "${uiLabelMap.CommonOk}",
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                "callback": function() {
	                        $("#jqxgrid2").jqxGrid('begincelledit', rowindex, "actualDeliveredQuantity");
	                    }
	                }]
	            );
	            return true;
	        }
	        if(data.actualDeliveredQuantity > data.actualExportedQuantity){
	            $('#jqxgrid2').jqxGrid('unselectrow', rowindex);
	            bootbox.dialog("${uiLabelMap.LogCheckActuallyExportedGreaterRealCommunication}", [{
	                "label" : "${uiLabelMap.CommonOk}",
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                "callback": function() {
	                        $("#jqxgrid2").jqxGrid('begincelledit', rowindex, "actualDeliveredQuantity");
	                    }
	                }]
	            );
	            return true;
	        }
	    }
	    if(data.statusId == 'DELI_ITEM_DELIVERED'){
	        bootbox.dialog("${uiLabelMap.DLYItemComplete}", [{
	            "label" : "${uiLabelMap.CommonOk}",
	            "class" : "btn btn-primary standard-bootbox-bt",
	            "icon" : "fa fa-check",
	            }]
	        );
	        return true;
	    }
	    if(data.statusId == 'DELI_ITEM_CREATED' && (data.inventoryItemId == null || data.actualExportedQuantity == 0)){
	        if(data.inventoryItemId == null){
	            bootbox.dialog("${uiLabelMap.DItemMissingFieldsExp}", [{
	                "label" : "${uiLabelMap.CommonOk}",
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                "callback": function() {
	                    $("#jqxgrid2").jqxGrid('begincelledit', rowindex, "inventoryItemId");
	                }
	                }]
	            );
	            return true;
	        }else{
	            bootbox.dialog("${uiLabelMap.DItemMissingFieldsExp}", [{
	                "label" : "${uiLabelMap.CommonOk}",
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                "callback": function() {
	                    $("#jqxgrid2").jqxGrid('begincelledit', rowindex, "actualExportedQuantity");
	                }
	            }]
	            );
	            return true;
	        }
	    }
	    return false;
	}
	function confirmExportNumber(rowid, rowdata){
	    var tmpRowData = new Object();
	    tmpRowData.productId = rowdata.productId;
	    tmpRowData.quantityUomId = rowdata.quantityUomId;
	    tmpRowData.fromOrderId = rowdata.fromOrderId;
	    tmpRowData.fromOrderItemSeqId = rowdata.fromOrderItemSeqId;
	    tmpRowData.inventoryItemId = rowdata.inventoryItemId;
	    tmpRowData.deliveryId = rowdata.deliveryId;
	    tmpRowData.deliveryItemSeqId = rowdata.deliveryItemSeqId;
	    tmpRowData.actualExportedQuantity = rowdata.actualExportedQuantity;
	    tmpRowData.actualDeliveredQuantity = rowdata.actualDeliveredQuantity;
	    tmpRowData.actualExpireDate = rowdata.actualExpireDate;
	    tmpRowData.expireDate = rowdata.expireDate;
	    for(i = 0; i < listInv.length;i++){
	        if(listInv[i].productId == tmpRowData.productId){
	            var tmpDate = new Date(listInv[i].expireDate.time);
	            var tmpValue = new Object();
	            tmpRowData.expireDate =  $.datepicker.formatDate('dd/mm/yy', tmpDate);
	            break;
	        }
	    }
	    var strMsg;
	    if(tmpRowData.actualDeliveredQuantity != null && tmpRowData.actualDeliveredQuantity > 0){
	        strMsg = "${uiLabelMap.ConfirmToDelivery} #" +  tmpRowData.productId + ' ${uiLabelMap.WithExpireDate} ' + tmpRowData.expireDate + ' ${uiLabelMap.LogIs} ' +
	        tmpRowData.actualDeliveredQuantity + ' [' + tmpRowData.quantityUomId + '] ?';
	    }else{
	        strMsg = "${uiLabelMap.ConfirmToExport} #" +  tmpRowData.productId + ' ${uiLabelMap.WithExpireDate} ' + tmpRowData.expireDate + ' ${uiLabelMap.LogIs} ' +
	        tmpRowData.actualExportedQuantity + ' [' + tmpRowData.quantityUomId + '] ?';
	    }
	    bootbox.confirm(strMsg, function(result) {
	        if(result){
	            editPending = true;
	            $("#jqxgrid2").jqxGrid('updaterow', rowid, tmpRowData);
	        }else{
	            editPending = false;
	        }
	    });
	}
</script>
<script type="text/javascript">
		$("#jqxgrid2").on("bindingComplete", function (event) {
			var rows = $("#jqxgrid2").jqxGrid('getrows');
			var total = 0;
			var defaultWeightUomId = null;
			if (rows.length > 0){
				defaultWeightUomId = rows[0].defaultWeightUomId;
			}
			var desc = "";
			if (rows.length > 0 && defaultWeightUomId != null){
				for (var i=0; i<rows.length; i++){
					if (rows[0].defaultWeightUomId == rows[0].weightUomId){
						total = total + rows[i].weight;
					} else {
						for (var j=0; j<uomConvertData.length; j++){
							if ((uomConvertData[j].uomId == rows[i].baseWeightUomId && uomConvertData[j].uomIdTo == rows[i].defaultWeightUomId) || (uomConvertData[j].uomId == rows[i].defaultWeightUomId && uomConvertData[j].uomIdTo == rows[i].baseWeightUomId)){
								total = total + (uomConvertData[j].conversionFactor)*rows[i].weight;
								break;
							}
						}
					}
				}
				for(var i = 0; i < weightUomData.length; i++){
					if(weightUomData[i].uomId == rows[0].defaultWeightUomId){
						desc = weightUomData[i].description;
					}
				}
				var value = parseInt(total); 
				$('#totalWeight').text(value.toLocaleString('${localeStr}') + " " +(desc));
			} else {
				$('#totalWeight').text(total + " " +(desc));
			}
		});
		function afterAdd(){
			$("#jqxgrid1").jqxGrid('updatebounddata');
		}
		var listInv = [];
	    var tmpValue;
	    var glDeliveryId;
	    var glOriginFacilityId;
	    var glDeliveryStatusId;
		function showDetailPopup(deliveryId){
			var deliveryDT;
			glDeliveryId = deliveryId;
			//Create theme
			$.jqx.theme = 'olbius';
			theme = $.jqx.theme;
			//Cache delivery
	        $.ajax({
	               type: "POST",
	               url: "getDeliveryById",
	               data: {'deliveryId': deliveryId},
	               dataType: "json",
	               async: false,
	               success: function(response){
	                   deliveryDT = response;
	                   $.ajax({
	                       type: "POST",
	                       url: "getINVByOrderAndDlv",
	                       data: {'orderId': '${parameters.orderId}', 'facilityId':deliveryDT.originFacilityId, 'deliveryId': deliveryDT.deliveryId},
	                       dataType: "json",
	                       async: false,
	                       success: function(response){
	                           listInv = response.listData
	                       },
	                       error: function(response){
	                         alert("Error:" + response);
	                       }
	                   });
	               },
	               error: function(response){
	                 alert("Error:" + response);
	               }
	        });
	        glOriginFacilityId = deliveryDT.originFacilityId;
	        glDeliveryStatusId = deliveryDT.statusId;
			//Set deliveryId for target print pdf
			var href = "/delys/control/delivery.pdf?deliveryId=";
			href += deliveryId
			$("#printPDF").attr("href", href);
			
			//Create deliveryIdDT
			$("#deliveryIdDT").text(deliveryDT.deliveryId);
			
			//Create statusIdDT
			var stName = "";
	        for(i=0; i < statusData.length; i++){
	            if(statusData[i].statusId==deliveryDT.statusId){
	                stName = statusData[i].description;
	            }
	        }
			$("#statusIdDT").text(stName);
			
			//Create orderIdDT 
			$("#orderIdDT").text(deliveryDT.orderId);
			
			
			//Create originFacilityIdDT
			var faName = "";
			for(i=0; i < faciData.length; i++){
			    if(faciData[i].facilityId==deliveryDT.originFacilityId){
			        faName = faciData[i].description;
			    }
			}
			$("#originFacilityIdDT").text(faName);
			
			//Create originFacilityIdDT
			var faName2 = "";
			for(i=0; i < faciData.length; i++){
			    if(faciData[i].facilityId==deliveryDT.destFacilityId){
			        faName2 = faciData[i].description;
			    }
			}
			$("#destFacilityIdDT").text(faName2);
			
			//Create originProductStoreIdDT
			var originProductStoreId = deliveryDT.originProductStoreId;
			var productStoreName;
			for(var i = 0; i < prodStoreData.length; i++){
				if(originProductStoreId == prodStoreData[i].productStoreId){
					productStoreName = prodStoreData[i].description;
					break;
				}
			}
			$("#originProductStoreIdDT").text(productStoreName);
			
			//Create createDateDT
//			var createDate = formatDate(deliveryDT.createDate);
			var createDate = new Date(deliveryDT.createDate);
			if (createDate.getMonth()+1 < 10){
				$("#createDateDT").text(createDate.getDate() + '/0' + (createDate.getMonth()+1) + '/' + createDate.getFullYear());
			} else {
				$("#createDateDT").text(createDate.getDate() + '/' + (createDate.getMonth()+1) + '/' + createDate.getFullYear());
			}
			
			//Create partyIdToDT
			var partyIdTo = deliveryDT.partyIdTo;
			var partyNameTo;
			for(var i = 0; i < partyData.length; i++){
				if(partyIdTo == partyData[i].partyId){
					partyNameTo = partyData[i].description;
					break;
				}
			}
			$("#partyIdToDT").text(partyNameTo);
			
			//Create destContactMechIdDT
			var destContactMechId = deliveryDT.destContactMechId;
			var destContactMech;
			for(var i = 0; i < pstAddrData.length; i++){
				if(destContactMechId == pstAddrData[i].contactMechId){
					destContactMechId = pstAddrData[i].description;
					break;
				}
			}
			$("#destContactMechIdDT").text(destContactMechId);
			//Create partyIdFromDT
			var partyIdFrom = deliveryDT.partyIdFrom;
			var partyNameFrom;
			for(var i = 0; i < partyData.length; i++){
				if(partyIdFrom == partyData[i].partyId){
					partyNameFrom = partyData[i].description;
					break;
				}
			}
			$("#partyIdFromDT").text(partyNameFrom);
			
			//Create originContactMechIdDT
			var originAddr;
			for(var i = 0; i < pstAddrData.length; i++){
				if(deliveryDT.originContactMechId == pstAddrData[i].contactMechId){
					originAddr = pstAddrData[i].description;
					break;
				}
			}
			$("#originContactMechIdDT").text(originAddr);
			
			//Create deliveryDateDT
			var deliveryDate = new Date(deliveryDT.deliveryDate);
			if (deliveryDate.getMonth()+1 < 10){
				$("#deliveryDateDT").text(deliveryDate.getDate() + '/0' + (deliveryDate.getMonth()+1) + '/' + deliveryDate.getFullYear());
			} else {
				$("#deliveryDateDT").text(deliveryDate.getDate() + '/' + (deliveryDate.getMonth()+1) + '/' + deliveryDate.getFullYear());
			}
			
			//Create noDT
			$("#noDT").text(deliveryDT.no);
			
			//Create pathScanfile
			var path = "";
			if (deliveryDT.pathScanFile){
				$('#scanLabel').html("");
				$('#scanLabel').append('${uiLabelMap.FileScan}:');
				path = deliveryDT.pathScanFile;
				var fileName = path.split('/')[7]; 
				$('#scanfile').html("");
				$('#scanfile').append("<a href="+path+" target='_blank'><i class='fa-file-text-o'></i>'"+fileName+"'</a>");
			} else {
				if ("DLV_CONFIRMED" == deliveryDT.statusId){
					$('#scanLabel').html("");
					$('#scanLabel').append('${uiLabelMap.FileScan}:');
					$('#scanfile').html("");
					$('#scanfile').append("<a id='linkId' onclick='showAttachFilePopup()'><i class='icon-upload'></i>${uiLabelMap.AttachFileScan}</a>");
				} else {
					$('#scanLabel').html("");
					$('#scanfile').html("");
				}
			}
			if ("DLV_CREATED" == deliveryDT.statusId){
				$('#actualStartLabel').html("");
				$('#actualStartLabel').append('${uiLabelMap.ActualExportedDate}:');
				$('#actualStartDate').html("");
				$('#actualStartDate').css("border-width", "1px");
				$('#actualStartDate').jqxDateTimeInput({width: 200});
			}
			if ("DLV_CONFIRMED" == deliveryDT.statusId){
				$('#actualArrivalLabel').html("");
				$('#actualArrivalLabel').append('${uiLabelMap.ActualDeliveredDate}:');
				$('#actualArrivalDate').html("");
				$('#actualArrivalDate').jqxDateTimeInput({width: 200});
				
				$('#actualStartLabel').html("");
				$('#actualStartLabel').append('${uiLabelMap.ActualExportedDate}:');
				$('#actualStartDate').html("");
				var date = deliveryDT.actualStartDate;
				var temp = date.split(" ");
				var d = temp[0].split("-");
				$('#actualStartDate').append(d[2]+'/'+d[1]+'/'+d[0]);
				$('#actualStartDate').css("border-width", "1px");
				$('#actualArrivalDate').css("border-width", "1px");
			}
			if ("DLV_EXPORTED" == deliveryDT.statusId){
				$('#actualStartDate').css("border-width", "0px");
				$('#actualArrivalDate').css("border-width", "0px");
				$('#actualStartLabel').html("");
				$('#actualStartLabel').append('${uiLabelMap.ActualExportedDate}:');
				$('#actualStartDate').html("");
				var date = deliveryDT.actualStartDate;
				var temp = date.split(" ");
				var d = temp[0].split("-");
				$('#actualStartDate').append(d[2]+'/'+d[1]+'/'+d[0]);
			}
			if ("DLV_DELIVERED" == deliveryDT.statusId){
				$('#actualStartDate').css("border-width", "0px");
				$('#actualArrivalDate').css("border-width", "0px");
				$('#actualStartLabel').html("");
				$('#actualStartLabel').append('${uiLabelMap.ActualExportedDate}:');
				$('#actualStartDate').html("");
				var date = deliveryDT.actualStartDate;
				var temp = date.split(" ");
				var d = temp[0].split("-");
				$('#actualStartDate').append(d[2]+'/'+d[1]+'/'+d[0]);
				
				$('#actualArrivalLabel').html("");
				$('#actualArrivalLabel').append('${uiLabelMap.ActualDeliveredDate}:');
				$('#actualArrivalDate').html("");
				var arrDate = deliveryDT.actualArrivalDate;
				var temp2 = arrDate.split(" ");
				var d2 = temp[0].split("-");
				$('#actualArrivalDate').append(d2[2]+'/'+d2[1]+'/'+d2[0]);
				
			}
			//Create Grid
			
	        var tmpS = $("#jqxgrid2").jqxGrid('source');
	        tmpS._source.url = "jqxGeneralServicer?sname=getListDeliveryItem&deliveryId=" + deliveryId;
	        $("#jqxgrid2").jqxGrid('source', tmpS);
	        
			//Open Window
			$("#popupDeliveryDetailWindow").jqxWindow('open');
		}
	//Create Window
    var checkStorekeeper = false;
	$("#alterpopupWindow").jqxWindow({
		maxWidth: 1500, minWidth: 950, minHeight: 680, maxHeight: 1200, resizable: true,  isModal: true, modalZIndex: 10000, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
	});
	initGridjqxgrid1();
	$("#alterpopupWindow").on('open', function (event) {
		var tmpS = $("#jqxgrid1").jqxGrid('source');
	    tmpS._source.url = "jqxGeneralServicer?sname=getListOrderItemDelivery&orderId=${parameters.orderId?if_exists}&facilityId=" + $('#originFacilityId').val();
	    $("#jqxgrid1").jqxGrid('source', tmpS);
	    $("#jqxgrid1").on("bindingComplete", function (event) {
			getInventoryItemTotalByFacility();
	    });
	});
		
	//Prepare data for order item type
	<#assign orderItemTypes = delegator.findList("OrderItemType", null, null, null, null, false) >
	var orderItemTypeData = [];
	<#list orderItemTypes as item>
	    <#assign description = StringUtil.wrapString(item.get('description', locale)?if_exists) />
		var row = {};
		row['orderItemTypeId'] = '${item.orderItemTypeId}';
		row['description'] = '${description}';
		orderItemTypeData[${item_index}] = row;
	</#list>
	
	<#assign facilities = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("facilityTypeId", "WAREHOUSE"), null, null, null, false)>
	var facilityData = [];
	<#list facilities as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.facilityName?if_exists)/>
		row['facilityId'] = '${item.facilityId?if_exists}';
		row['description'] = '${description?if_exists}';
		facilityData[${item_index}] = row;
	</#list>
	$('#listWeightUomId').on('change', function(event){
		updateTotalWeight(event.args.rowindex);
	});
	function rowsunelectfunction2(event){
		updateTotalWeight();
	}
	function updateTotalWeight(){
		var totalProductWeight = 0;
		var selectedIndexs = $('#jqxgrid1').jqxGrid('getselectedrowindexes');
		for(var i = 0; i < selectedIndexs.length; i++){
			var data = $('#jqxgrid1').jqxGrid('getrowdata', selectedIndexs[i]);
			var baseWeightUomId = data.baseWeightUomId;
			var defaultWeightUomId = $('#listWeightUomId').val();
			var itemWeight = 0;
			if (data.availableToPromiseTotal < 1){
				itemWeight = 0;
			} else {
				itemWeight = (data.requiredQuantityTmp)*(data.weight);
			}
			if (baseWeightUomId == defaultWeightUomId){
				totalProductWeight = totalProductWeight + itemWeight;
			} else {
				for (var j=0; j<uomConvertData.length; j++){
					if ((uomConvertData[j].uomId == baseWeightUomId && uomConvertData[j].uomIdTo == defaultWeightUomId) || (uomConvertData[j].uomId == defaultWeightUomId && uomConvertData[j].uomIdTo == baseWeightUomId)){
						totalProductWeight = totalProductWeight + (uomConvertData[j].conversionFactor)*itemWeight;
						break;
					}
				}
			}
		}
		var n = parseFloat(totalProductWeight)
		totalProductWeight = Math.round(n * 1000)/1000;
		$('#totalProductWeight').text(totalProductWeight);
	}
	function rowselectfunction2(event){
	    if (typeof event.args.rowindex != 'number'){
	        var tmpArray = event.args.rowindex;
	        for(i = 0; i < tmpArray.length; i++){
	            if(checkRequiredData(tmpArray[i])){
	                $('#jqxgrid1').jqxGrid('clearselection');
	                break; // Stop for first item
	            }
	        }
	    } else{
	        checkRequiredData(event.args.rowindex);
	    }
	    updateTotalWeight();
	}
	function checkRequiredData(rowindex){
	    var data = $('#jqxgrid1').jqxGrid('getrowdata', rowindex);
	    if(data == undefined){
	        return true; // to break the loop
	    } 
	    if (data.availableToPromiseTotal < 1){
	    	displayNotEnough(rowindex, "${uiLabelMap.FacilityNotEnoughProduct}");
	    	return true;
	    }
	    if(data.requiredQuantityTmp < 1){
	        displayAlert(rowindex, "${uiLabelMap.NumberGTZ}");
	        return true;
	    }else if(data.requiredQuantityTmp > (data.requiredQuantity - data.createdQuantity)){
	        displayAlert(rowindex, "${uiLabelMap.ExportValueLTZRequireValue}");
	        return true;
	    }
	    return false;
	}
	function displayNotEnough(rowindex, message){
	    bootbox.dialog(message, [{
	        "label" : "${uiLabelMap.CommonOk}",
	        "class" : "btn btn-primary standard-bootbox-bt",
	        "icon" : "fa fa-check",
	        "callback": function() {
	        	 $("#jqxgrid1").jqxGrid('unselectrow', rowindex);
	        }
	        }]
	    );
	}
	function displayAlert(rowindex, message){
	    bootbox.dialog(message, [{
	        "label" : "${uiLabelMap.CommonOk}",
	        "class" : "btn btn-primary standard-bootbox-bt",
	        "icon" : "fa fa-check",
	        "callback": function() {
	            //$('#jqxgrid1').jqxGrid('unselectrow', rowindex);
	            $("#jqxgrid1").jqxGrid('begincelledit', rowindex, "requiredQuantityTmp");
	        }
	        }]
	    );
	}
	<#assign localeStr = "VI" />
	<#if locale = "en">
	    <#assign localeStr = "EN" />
	</#if>
	<#assign storeKeeper = delegator.findList("FacilityParty", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("roleTypeId", "LOG_STOREKEEPER", "partyId", userLogin.partyId)), null, null, null, false)/>
	var listFacilityManage = [];
	<#list storeKeeper as item>
		listFacilityManage.push('${item.facilityId}');
	</#list>
	<#assign specialist = delegator.findList("FacilityParty", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("roleTypeId", "LOG_SPECIALIST", "partyId", userLogin.partyId)), null, null, null, false)/>
	<#list specialist as item>
	listFacilityManage.push('${item.facilityId}');
	</#list>
