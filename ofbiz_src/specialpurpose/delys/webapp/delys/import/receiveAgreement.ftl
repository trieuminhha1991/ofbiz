<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>

<script>
	<#assign quantityUomList = delegator.findList("Uom", null, null, null, null, false) />
	var quData = new Array();
	<#list quantityUomList as itemUom >
		var row = {};
		row['quantityUomId'] = '${itemUom.uomId?if_exists}';
		row['weightUomId'] = '${itemUom.uomId?if_exists}';
		row['description'] = '${itemUom.description?if_exists}';
		quData[${itemUom_index}] = row;
	</#list>
	
	<#assign facilityList = delegator.findList("Facility", null, null, null, null, false) />
	var fData = new Array();
	<#list facilityList as itemFac >
		var row = {};
		row['facilityId'] = '${itemFac.facilityId?if_exists}';
		row['facilityName'] = '${itemFac.facilityName?if_exists}';
		fData[${itemFac_index}] = row;
	</#list>
	
	<#assign productTypeList = delegator.findList("ProductType", null, null, null, null, false) />
	var productTypeData = new Array();
	<#list productTypeList as productType>
		<#assign description = StringUtil.wrapString(productType.description) />
		var row = {};
		row['description'] = "${description}";
		row['productTypeId'] = '${productType.productTypeId}';
		productTypeData[${productType_index}] = row;
	</#list>
	var product = new Array();
	<#list listProducts as product>
		<#assign description = StringUtil.wrapString(product.internalName) />
		var row = {};
		row['productId'] = "${product.productId}";
		row['description'] = "${description}";
		product[${product_index}] = row;
	</#list>
	
	var listStatus1 = new Array();
	<#list listStatus as stt>
		<#assign description = StringUtil.wrapString(stt.description) />
		var row = {};
		row['statusId'] = "${stt.statusId}";
		row['description'] = "${stt.description}";
		listStatus1[${stt_index}] = row;
	</#list>
	
	$(document).ready(function(){
    	$("#jqxNotificationNested").jqxNotification({ width: "1358px", appendContainer: "#container", opacity: 0.9, autoClose: false, template: "info" });
    	getProductShelfLife();
	});
	var listProductShelfLife = [];
	function getProductShelfLife() {
		$.ajax({
            url: "getProductShelfLifeAjax",
            type: "POST",
            data: {},
            success: function(res) {
            	listProductShelfLife = res["listProductShelfLife"];
            }
        }).done(function() {

        });
	}
	function executeQualityPublication(data, value) {
		var productId = data.productId;
		var datetimeManufactured = lastTimeChoice;
		var expireDate = value;
		var validateDate = expireDate.getTime() - datetimeManufactured.getTime();
		validateDate = Math.ceil(validateDate/86400000);
		var qualityPublication = [];
		qualityPublication = hasQualityPublication(productId);
		if (qualityPublication == "null") {
			var header = "Tao cong bo chat luong cho " + getProductName(productId) + " [" + productId + "]";
			var message = "<h4>${uiLabelMap.QualityPublicationNotFound} <b>" + getProductName(productId) + " [<i>" + productId + "</i>]</b> ${uiLabelMap.confirmQAInsertQualityPublication}</h4>";
			confirmInsertQualityPublication(productId, message, header, "");
		}else {
			var thruDate = qualityPublication.thruDate;
			var timeNow = new Date();
			thruDate = thruDate.time;
			timeNow = timeNow.getTime();
			var leftTime = thruDate - timeNow;
			leftTime = Math.ceil(leftTime/86400000);
			if (0 < leftTime && leftTime < 10) {
					var header = "Cong bo chat luong san pham " + getProductName(productId) + " [" + productId + "] sap het han";
					var message = "<h4>${uiLabelMap.QualityPublicationPreExpire} <b>" + getProductName(productId) + " [<i>" + productId + "</i>]</b> ${uiLabelMap.confirmQualityPublicationPreExpire}</h4>";
					confirmInsertQualityPublication(productId, message, header, "");
			} 
			if(leftTime < 0){
				var header = "Cong bo chat luong san pham " + getProductName(productId) + " [" + productId + "] da het han";
				var message = "<h4>${uiLabelMap.QualityPublicationPreExpire} <b>" + getProductName(productId) + " [<i>" + productId + "</i>]</b> ${uiLabelMap.confirmQualityPublicationExpire}</h4>";
				confirmInsertQualityPublication(productId, message, header, "");
			}
			var expireDateProduct = qualityPublication.expireDate;
			if (validateDate != expireDateProduct) {
				var header = "Cong bo chat luong san pham " + getProductName(productId) + " [" + productId + "] co thay doi";
				var message = "<h4>${uiLabelMap.QualityPublicationNotFound} <b>" + getProductName(productId) + " [<i>" + productId + "</i>]</b> ${uiLabelMap.hasChangeProductShelfLife}</h4>";
				confirmInsertQualityPublication(productId, message, header, validateDate);
			}
		}
		return true;
	}
	function executeQualityPublicationFr(data, value) {
		var productId = data.productId;
		var datetimeManufactured = data.datetimeManufactured;
		var expireDate = value;
		var validateDate = expireDate.getTime() - datetimeManufactured.getTime();
		validateDate = Math.ceil(validateDate/86400000);
		var expireDateProduct = qualityPublication.expireDate;
		if (validateDate != expireDateProduct) {
			return { result: false, message: '${uiLabelMap.expireDateNotEquals}' };
		}
		return true;
	}
	function confirmInsertQualityPublication(productId, message, header, expireDateProduct) {
		var wd = "";
    	wd += "<div id='window01'><div>${uiLabelMap.AgreementScanFile}</div><div>";
    	wd += message;
    	wd += "<div class='row-fluid'>" +
			"<div class='span12 no-left-margin'>" +
				"<div class='span4'></div>" +
				"<div class='span8'><input style='margin-right: 5px;' type='button' id='alterSave5' value='${uiLabelMap.SentNotify}' /><input id='alterCancel5' type='button' value='${uiLabelMap.CommonCancel}' /></div>" +
			"</div>";
    	wd += "</div></div>";
    	$("#myImage").html(wd);
    	$("#alterCancel5").jqxButton();
        $("#alterSave5").jqxButton();
        $("#alterCancel5").click(function () {
       	 	$('#window01').jqxWindow('close');
//       	 	$("#myImage").html();
        });
        $("#alterSave5").click(function () {
        	createQuotaNotification(productId, "qaadmin", header, expireDateProduct);
        	$('#window01').jqxWindow('close');
//        	$("#myImage").html();
        });
       
    	$('#window01').jqxWindow({ height: 160, width: 700, maxWidth: 1200, isModal: true, modalOpacity: 0.7 });
    	$('#window01').on('close', function (event) {
        	 $('#window01').jqxWindow('destroy');
//        	 $("#myImage").html();
         });
	}
	function createQuotaNotification(productId, partyId, messages, expireDateProduct) {
			var targetLink = "productId=" + productId + ";expireDateProduct=" + expireDateProduct;
			if (expireDateProduct == "") {
				targetLink = "productId=" + productId;
			}
			var action = "CreateProductQuality";
			var header = messages;
			var d = new Date();
			var newDate = d.getTime() - (0*86400000);
			var dateNotify = new Date(newDate);
			var getFullYear = dateNotify.getFullYear();
			var getDate = dateNotify.getDate();
			var getMonth = dateNotify.getMonth() + 1;
			dateNotify = getFullYear + "-" + getMonth + "-" + getDate;
			var jsonObject = {partyId: partyId,
								header: header,
								openTime: dateNotify,
								action: action,
								targetLink: targetLink,};
			jQuery.ajax({
		        url: "createQuotaNotification",
		        type: "POST",
		        data: jsonObject,
		        success: function(res) {
		        	
		        }
		    }).done(function() {
		    	
			});
		}
	function hasQualityPublication(productId) {
		var result = "null";
		if (productId != null) {
			for ( var x in listProductShelfLife) {
				if (productId == listProductShelfLife[x].productId) {
					result = listProductShelfLife[x];
					return result;
				}
			}
		} else {
			result = "productIdnull";
		}
		return result;
	}
	function getProductName(productId) {
		if (productId != null) {
			for ( var x in product) {
				if (productId == product[x].productId) {
					return product[x].description;
				}
			}
		} else {
			return "";
		}
	}
	var lastTimeChoice = 0;
</script>

<div id="jqxNotificationNested">
	<div id="notificationContentNested">
	</div>
</div>
<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	 	var ordersDataAdapter = new $.jqx.dataAdapter(datarecord.rowDetail, { autoBind: true });
        orders = ordersDataAdapter.records;
		
		 var nestedGrids = new Array();
         var id = datarecord.uid.toString();
        
         var grid = $($(parentElement).children()[0]);
         $(grid).attr(\"id\",\"jqxgridDetail\");
         nestedGrids[index] = grid;
       
         var ordersbyid = [];
        
        
         for (var m = 0; m < orders.length; m++) {
            
                 ordersbyid.push(orders[m]);
         }
         var orderssource = {
        	 datafields: [
	        	 { name: \'orderId\', type:\'string\' },
	             { name: \'orderItemSeqId\', type: \'string\' },
	             { name: \'productId\', type: \'string\' },
	             { name: \'quantity\', type: \'number\' },
	             { name: \'datetimeManufactured\', type: 'date', other: 'Timestamp'},
	             { name: \'expireDate\', type: 'date', other: 'Timestamp'},
            ],
             localdata: ordersbyid,
             updaterow: function (rowid, newdata, commit) {
            	 commit(true);
            	 var orderId = newdata.orderId;
            	 var orderItemSeqId = newdata.orderItemSeqId;
            	 var quantity = newdata.quantity;
            	 var productId = newdata.productId;
            	 var datetimeManufacturedStr = newdata.datetimeManufactured;
            	 var datetimeManufactured = new Date(datetimeManufacturedStr);
            	 var expireDateStr = newdata.expireDate;
            	 var expireDate = new Date(expireDateStr);
            	 if(typeof expireDateStr != 'undefined' || typeof datetimeManufacturedStr != 'undefined'){
            		 $.ajax({
                         type: \"POST\",                        
                         url: 'updateOrderItemWhenReceiveDoc',
                         data: {orderId: orderId, orderItemSeqId: orderItemSeqId, quantity: quantity, productId: productId, datetimeManufactured: datetimeManufactured.getTime(), expireDate: expireDate.getTime()},
                         success: function (data, status, xhr) {
                             // update command is executed.
                             if(data.responseMessage == \"error\")
                             {
                             	commit(false);
                             	$(\"#jqxNotificationNested\").jqxNotification({ template: 'error'});
                             	$(\"#notificationContentNested\").text(data.errorMessage);
                             	$(\"#jqxNotificationNested\").jqxNotification(\"open\");
                         			if(orderItemSeqId == null){
                         				grid.jqxGrid('setcellvaluebyid', rowid, 'orderItemSeqId', 'error');
                         			}
                             }else{
                             	commit(true);
//                             	grid.jqxGrid('updatebounddata');
                             	$(\"#container\").empty();
                             	$(\"#jqxNotificationNested\").jqxNotification({ template: 'info'});
                             	$(\"#notificationContentNested\").text(\"${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}\");
                             	$(\"#jqxNotificationNested\").jqxNotification(\"open\");
	                 			 if(orderItemSeqId == null){
	                        		 grid.jqxGrid('setcellvaluebyid', rowid, 'orderItemSeqId', data.orderItemSeqId);
	                        	 }
                             }
                         },
                         error: function () {
                             commit(false);
                         }
                     });
            	 }
            	}
         }
         var nestedGridAdapter = new $.jqx.dataAdapter(orderssource);
        
         if (grid != null) {
        	 var a ;
             grid.jqxGrid({
                 source: nestedGridAdapter, width: '98%', height: 200,
                 showtoolbar:false,
		 		 editable: true,
		 		 editmode:\'selectedrow\',
		 		 showheader: true,
		 		 selectionmode:\'singlerow\',
		 		 theme: 'olbius',
                 columns: [
                   { text: \'${uiLabelMap.OrderOrderId}\', datafield: \'orderId\', editable: false, width: 200, hidden: true},
                   { text: \'${uiLabelMap.ProductName}\', datafield: \'productId\', columntype:\'dropdownlist\', editable: true, minwidth: 200,
                	   createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
	                	   var data = grid.jqxGrid('getrowdata', row);
	                	   var selectedIndex = 0;
	                	   for(var j = 0; j < product.length; j++){
	                		   if(product[j].productId == data.productId){
	                			   selectedIndex = j;
	                			   break;
	                		   }
	                	   }
	                	   var sourcePro = {
	                			   localdata: product,
	                			   datatype: \'array\'
	                	   };
	                	   var dataAdapterPro = new $.jqx.dataAdapter(sourcePro);
				            editor.jqxDropDownList({source: dataAdapterPro, displayMember:\"description\", valueMember: \"productId\",
//	                           renderer: function (index, label, value) {
//				                    var datarecord = product[index];
//				                    return datarecord.description;
//				                } ,
	                       });
//				            var aa = editor.jqxDropDownList('selectIndex', selectedIndex);
//				            editor.on('open', function (event) {
//				            	aa;
//	                	   	});
                	   },

                	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
                		var vlreturn = value;
                		for(var i = 0; i < product.length; i++){
                    	   var pro = product[i];
                    	   if(value == pro.productId){
                    		   vlreturn = pro.description;
                    	   }
                       }
                		return vlreturn;
                    },
                   },
                   { text: \'${uiLabelMap.OrderQuantity}\', datafield: \'quantity\',editable: true, width: 200,columntype: \'numberinput\', cellsalign :'right' },
                   { text: \'${uiLabelMap.dateOfManufacture}\', datafield: \'datetimeManufactured\', columntype: \'datetimeinput\',width: \'150\', editable: true, cellsformat: 'dd/MM/yyyy',
                	   validation: function (cell, value) {
                		   lastTimeChoice = value;
                		   var thisRow = cell.row;
                		   var data = grid.jqxGrid('getrowdata', thisRow);
            		       var expireDate = data.expireDate;
            		       if (expireDate == null) {
            		    	   return true;
            		       }
                           if (expireDate < value) {
                        	   $('#inputdatetimeeditorjqxgridOrderViewerexpireDate').val('');
                            }
                	       return true;
                	    },},
                   { text: \'${uiLabelMap.ProductExpireDate}\', datafield: \'expireDate\', columntype: \'datetimeinput\',width: \'150\', editable: true, cellsformat: 'dd/MM/yyyy',
                	   validation: function (cell, value) {
                		   if (lastTimeChoice == null) {
                			   return { result: false, message: '${uiLabelMap.ChoicedatetimeManufacturedFirst}' };
                		   }
                		   var thisRow = cell.row;
                		   var data = grid.jqxGrid('getrowdata', thisRow);
                		   lastTimeChoice==0?datetimeManufactured=data.datetimeManufactured:datetimeManufactured=lastTimeChoice;
                           if (datetimeManufactured > value) {
                        	   return { result: false, message: '${uiLabelMap.DateExpirecannotbeforeDateManufactured}' };
                            }
                	       return executeQualityPublication(data, value);
                	    },
                   },
                   { text: \'${uiLabelMap.CommonAdd}\', width: 50,columntype: \'button\', editable: false,
                	   cellsrenderer: function(){
                		return '${StringUtil.wrapString(uiLabelMap.CommonAdd)}';
                	   }, buttonclick: function(row){
                		   
                		   var dataRecordOfRow = grid.jqxGrid('getrowdata', row);
                		   
                		   var row1 = {orderId: dataRecordOfRow.orderId, productId: dataRecordOfRow.productId, datetimeManufactured: dataRecordOfRow.datetimeManufactured, expireDate: dataRecordOfRow.expireDate}; 
                		   grid.jqxGrid('addrow', null, row1, 'first');
                		   grid.jqxGrid('clearSelection');
                		   grid.jqxGrid('selectRow',0);
                  		   grid.jqxGrid('beginrowedit', 0);
                		   grid.jqxGrid('begincelledit', 0, 'datetimeManufactured');
                	   }
                   
                   },
                   { text: \'${uiLabelMap.CommonDelete}\', width: 50,columntype: \'button\', editable: false,
                	   cellsrenderer: function(row, column, value){
                		return '${StringUtil.wrapString(uiLabelMap.CommonDelete)}';
                	   }, buttonclick: function(row){
                		  var idrow = grid.jqxGrid('getrowid', row);
                		  var datarecord1 = grid.jqxGrid('getrowdata', row);
                		  alert(datarecord1.datetimeManufactured);
                	   }
                   },
                ]
             });
         }
 }"/>


<#assign dataField="[{ name: 'agreementId', type: 'string' },
					 { name: 'orderId', type: 'string'},
					 { name: 'agreementDate', type: 'string'},
					 { name: 'shippingLineId', type: 'string'},
					 { name: 'partyIdFrom', type: 'string'},
					 { name: 'billNumber', type: 'string'},
					 { name: 'billId', type: 'string'},
					 { name: 'containerId', type: 'string'},
					 { name: 'containerNumber', type: 'string'},
					 { name: 'departureDate', type: 'date'},
					 { name: 'arrivalDate', type: 'date'},
					 { name: 'partyRentId', type: 'string'},
					 { name: 'statusId', type: 'string'},
					 { name: 'rowDetail', type: 'string'},
					 ]"/>
<#--{ name: 'facilityId', type: 'string'}, { name: 'quantityOnHandTotal', type: 'string'},-->
<#assign columnlist="					 
						{ text: '${uiLabelMap.AgreementId}', datafield: 'agreementId', width: '100px', editable: false},
						{ text: '${uiLabelMap.OrderOrderId}', datafield: 'orderId', width: '100px', editable: false, hidden: true},
						{ text: '${uiLabelMap.AgreementDate}', datafield: 'agreementDate', columntype: 'datetimeinput',width: '150px', editable: false,cellsformat: 'd'},
						{ text: '${uiLabelMap.shippingLineId}', datafield: 'shippingLineId', width: '150px', editable: false, 
							createeditor: function (row, column, editor) {
                            // assign a new data source to the dropdownlist.
// var list = ['Germany', 'Brazil', 'France'];
// editor.jqxDropDownList({ autoDropDownHeight: true, source: list });
                        },
                        	cellsrenderer: function(){
                        		return 'HYUNDAI_COMPANY';
                        	},
                        // update the editor's value before saving it.
                        
						},
						{ text: '${uiLabelMap.billNumber}', datafield: 'billNumber', width: '150px', editable: true},
						{ text: '${uiLabelMap.containerNumber}', datafield: 'containerNumber', width: '150px', editable: true},
						{ text: '${uiLabelMap.departureDate}', datafield: 'departureDate', editable: true, columntype: 'datetimeinput', width: '150px', cellsformat: 'dd/MM/yyyy'},
						{ text: '${uiLabelMap.arrivalDate}', datafield: 'arrivalDate', editable: true,  columntype: 'datetimeinput', width: '150px', cellsformat: 'dd/MM/yyyy'},
						{ text: '${uiLabelMap.Status}', datafield: 'statusId', width: '150px', columntype: 'dropdownlist', editable: true,cellsrenderer:
							function(row, colum, value){
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							var statusId = data.statusId;
							var status = getStatus(statusId);
							return '<span>' + status + '</span>';
							}, createeditor: 
								function(row, column, editor){
								editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(listStatus), displayMember: 'statusId', valueMember: 'statusId' ,
								    renderer: function (index, label, value) {
								    	if (index == 0) {
		                            		return value;
										}
									    return getStatus(value);
								    } });
								}
						},
					 "/>
<@jqGrid filtersimplemode="false" addType="popup" initrowdetails = "true" dataField=dataField initrowdetailsDetail=initrowdetailsDetail editmode="selectedrow" editable="true" columnlist=columnlist clearfilteringbutton="false" showtoolbar="true" addrow="false" deleterow="false"
		 	url="jqxGeneralServicer?sname=JQGetAgreeToUpdate"
		 	removeUrl="jqxGeneralServicer?sname=removeOrderItem&jqaction=D" deleteColumn="orderId"
		 	updateUrl="jqxGeneralServicer?sname=updateAgreemenReceive&jqaction=U" editColumns="agreementId;partyRentId;containerId;billId;orderId;agreementDate;shippingLineId;partyIdFrom;billNumber;containerNumber;arrivalDate(java.sql.Timestamp);departureDate(java.sql.Timestamp);statusId"
		 />
							<div id="myImage"></div>
		<script>		
							var listStatus = new Array();
							<#if listStatus?exists>
							<#list listStatus as item>
								var row = {};
								row['description'] = '${item.get("description", locale)?if_exists}';
								row['statusId'] = '${item.statusId?if_exists}';
								listStatus[${item_index}] = row;
							</#list>
							</#if>
			    			function getStatus(statusId) {
			    				if (statusId != null) {
			    					for ( var x in listStatus) {
			        					if (statusId == listStatus[x].statusId) {
			        						return listStatus[x].description;
			        					}
			        				}
								} else {
									return "";
								}
			    			}
			    			function fixSelectAll(dataList) {
			    		    	var sourceST = {
			    				        localdata: dataList,
			    				        datatype: "array"
			    				    };
			    				var filterBoxAdapter2 = new $.jqx.dataAdapter(sourceST, {autoBind: true});
			    				var uniqueRecords2 = filterBoxAdapter2.records;
			    				uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			    				return uniqueRecords2;
			    			}
	</script>
	<style>     
    .green1 {
        color: #black;
        background-color: #F0FFFF;
    }
    .yellow1 {
        color: black\9;
        background-color: yellow\9;
    }
    .red1 {
        color: black\9;
        background-color: #e83636\9;
    }
    .green1:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .green:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
        color: black;
        background-color: #F0FFFF;
    }
    .yellow1:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .yellow:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
        color: black;
        background-color: yellow;
    }
    .red1:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .red:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
        color: black;
        background-color: #e83636;
    }
    
    #pagerjqxgridDetail{
    	display: none;
    }
</style>