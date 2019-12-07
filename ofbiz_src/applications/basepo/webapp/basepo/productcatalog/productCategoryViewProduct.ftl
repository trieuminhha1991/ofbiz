<style type="text/css">
	.jqx-grid-cell-olbius.disableEdit {
		//background-color: #efefef;
		color: #dddddd !important;
		cursor: no-drop;
	}
	.jqx-grid-cell-olbius.disableEdit div {
	 	cursor: no-drop !important;
 	}
	.jqx-grid-cell-olbius.disableEdit .jqx-checkbox-default-olbius {
		border-color: #efefef;
	}
</style>
<script type="text/javascript">
	var cellClassjqxgridProducts = function (row, columnfield, value) {
		var editButton = $("#customcontroljqxgridProducts3");
		if ($(editButton).hasClass("editing")) {
			return "";
		} else {
			return "columnCheckEdit disableEdit";
		}
    }
</script>

<div id="jqxgridProducts"></div>

<#include "productCategoryAddProductPopup.ftl"/>

<script type="text/javascript">
	$(function(){
		OlbProdCategoryViewProduct.init();
	});
	
	var OlbProdCategoryViewProduct = (function(){
		var productCategoryMemberGRID;
		
		var init = function(){
			initElementComplex();
		};
		var initElementComplex = function(){
			var configCategory = {
				autoshowloadelement: true,
				showdefaultloadelement: true,
				localization: getLocalization(),
				datafields: [
					{name: "productId", type: "string"},
					{name: "productCode", type: "string"},
					{name: "mainCategoryId", type: "string"},
					{name: "primaryProductCategoryId", type: "string"},
					{name: "productName", type: "string"},
					{name: "productCategoryId", type: "string"},
					{name: "fromDate", type: "date", other: "Timestamp"},
					{name: "thruDate", type: "date", other: "Timestamp"},
					{name: "sequenceNum", type: "number"},
					{name: 'isBestSell', type: 'bool'},
					{name: 'isPromos', type: 'bool'},
					{name: 'isFeatured', type: 'bool'},
					{name: 'isNew', type: 'bool'},
					{name: 'bestSellFromDate', type: 'number'},
					{name: 'promosFromDate', type: 'number'},
					{name: 'newFromDate', type: 'number'},
					{name: 'featuredFromDate', type: 'number'}
				],
				columns: [
					{text: "${uiLabelMap.DmsCategoryId}", dataField: "productCategoryId", width: 150, editable: false},
	                {text: "${uiLabelMap.DmsProductId}", datafield: "productCode", width: 150, editable: false },
					{text: "${uiLabelMap.BSProductName}", datafield: "productName", editable: false},
					{text: "${uiLabelMap.DmsSequenceId}", datafield: "sequenceNum", width: 50, align: 'right', cellsalign: 'right', columntype: "numberinput", cellClassName: cellClassjqxgridProducts,
						validation: function (cell, value) {
							if (value < 0) {
								return { result: false, message: uiLabelMap.DmsQuantityNotValid };
							}
							return true;
						}
					},
					{text: "${uiLabelMap.BSInTheListSelling}", dataField: 'isBestSell', columntype: 'checkbox', align: 'center', width: 70, filterable: false, sortable: false, editable: true, cellClassName: cellClassjqxgridProducts},
					{text: "${uiLabelMap.BSIsPromosProduct}", dataField: 'isPromos', columntype: 'checkbox', align: 'center', width: 70, filterable: false, sortable: false, editable: true, cellClassName: cellClassjqxgridProducts},
					{text: "${uiLabelMap.BSIsNewProduct}", dataField: 'isNew', columntype: 'checkbox', align: 'center', width: 70, filterable: false, sortable: false, editable: true, cellClassName: cellClassjqxgridProducts},
					{text: "${uiLabelMap.BEFeaturedProducts}", dataField: 'isFeatured', columntype: 'checkbox', align: 'center', width: 70, filterable: false, sortable: false, editable: true, cellClassName: cellClassjqxgridProducts}
				],
				useUrl: true,
				//useUtilFunc: false,
				//root: 'listProductByProductCategoryId',
				//url: 'getProductByProductCategoryIdIncludeChild?productCategoryId=${productCategoryId}',
				//virtualmode: false,
				height: 425,
				autoheight: false,
				useUtilFunc: true,
				url: 'JQGetListProductInCategoryInclude&productCategoryId=${productCategoryId}',
            	showfilterrow: true,
		        filterable: true,
		        editable: false,
				width: "100%",
		        pagesize: 12,
		        pageable: true,
		        pagesizeoptions: [5, 10, 12, 15, 20, 25, 50, 100],
            	bindresize: true,
            	sortable: true,
		        enabletooltips: true,
		        selectionmode: "singlerow",
		        showtoolbar: true,
		        rendertoolbar: function(toolbar){
		        	$.jqx.theme = OlbCore.theme;
		        	<#assign customcontrol1 = ""/>
		        	<#assign customcontrol2 = ""/>
		        	<#assign customcontrol3 = ""/>
		        	<#if hasOlbPermission("MODULE", "CATEGORY_EDIT", "")>
		        		<#assign customcontrol1 = "fa-pencil open-sans@@javascript: void(0);@OlbProdCategoryViewProduct.toogleEditableGrid()">
		        	</#if>
		        	<#if hasOlbPermission("MODULE", "CATEGORY_NEW", "")>
		        		<#assign customcontrol2 = "fa-plus open-sans@@javascript: void(0);@OlbProdCategoryViewProduct.addNewProduct()">
		        		<#assign customcontrol3 = "icon-trash open-sans@@javascript: void(0);@OlbProdCategoryViewProduct.removeProduct()">
		        	</#if>
					<@renderToolbar id="jqxgridProducts" isShowTitleProperty="true" customTitleProperties="&quot;${productCategory.categoryName}&quot;&nbsp;&nbsp;>&nbsp;&nbsp;${uiLabelMap.BSProduct}" isCollapse="false" showlist="false" 
						customControlAdvance="" filterbutton="" clearfilteringbutton="true" 
						addrow="false" addType="popup" alternativeAddPopup="alterpopupWindow" 
						deleterow="false" deleteConditionFunction="" deleteConditionMessage="" 
						virtualmode="false" addinitvalue="" primaryColumn="ID" addmultiplerows="false" 
						updaterow="" updatemultiplerows="" excelExport="false" toPrint="false" 
						customcontrol1=customcontrol1 customcontrol2=customcontrol2 customcontrol3=customcontrol3 customtoolbaraction=""/>
                },
            	addrow: function (rowid, rowdata, position, commit) {
            		commit(true);
	            },
	            <#--
	            deleterow: function (rowid, commit) {
					commit(true);
	            },-->
	            updaterow: function (rowid, data, commit) {
					if (typeof (data.fromDate) == 'object') {
						data.fromDate ? data.fromDate = data.fromDate.time : data.fromDate;
					}
					<#--
					if (!$("#btnDeleteThisProduct").hasClass('hide')) {
						$("#btnDeleteThisProduct").addClass('hide');
					}
					-->
					commit(updateSequenceNumCategoryMember(data));
					//checkProductErasable(data.productId, data.productCategoryId);
            	},
			};
			productCategoryMemberGRID = new OlbGrid($("#jqxgridProducts"), null, configCategory, []);
		};
		var updateSequenceNumCategoryMember = function(dataMap){
			<#--
			DataAccess.execute({url: "configProductCategoryAjax", data: data}, Products.notify)
			-->
			$.ajax({
					type: 'POST',
					url: "configProductCategoryAjax",
					data: dataMap,
					beforeSend: function(){
						$("#loader_page_common").show();
					},
					success: function(data){
						jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
						        	$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'info'});
						        	$("#jqxNotification").html(errorMessage);
						        	$("#jqxNotification").jqxNotification("open");
						        	return false;
								}, function(){
									$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'info'});
						        	$("#jqxNotification").html("${uiLabelMap.wgupdatesuccess}");
						        	$("#jqxNotification").jqxNotification("open");
						        	
						        	//$("#alterpopupNewRootCategory").jqxWindow("close");
						        	//$("body").trigger("createRootCategoryComplete");
						        	//productCategoryMemberGRID.updateBoundData();
								}
						);
					},
					error: function(data){
						alert("Send request is error");
					},
					complete: function(data){
						$("#loader_page_common").hide();
					},
			});
		};
		var checkProductErasable = function(productId, productCategoryId) {
			if (!$("#btnDeleteThisProduct").hasClass('hide')) {
				$("#btnDeleteThisProduct").addClass('hide');
			}
			var erasable = DataAccess.getData({
						url: "checkProductErasable",
						data: {productId: productId, productCategoryId: productCategoryId},
						source: "erasable"});
			if (erasable == "Y") {
				$("#btnDeleteThisProduct").removeClass('hide');
			}
		};
		var toogleEditableGrid = function() {
			var editButton = $("#customcontroljqxgridProducts3");
			if ($(editButton).hasClass("editing")) {
				// disable
				$("#jqxgridProducts").jqxGrid({"editable": false});
				$(editButton).find("span").text("");
				$(editButton).removeClass("editing");
			} else {
				// enable
				$("#jqxgridProducts").jqxGrid({"editable": true});
				$(editButton).find("span").text("...");
				$(editButton).addClass("editing");
			}
		};
		var addNewProduct = function(){
			OlbProdCategoryAddProduct.openWindow();
		};
		var removeProduct = function(){
			var rowIndex = $("#jqxgridProducts").jqxGrid("getselectedrowindex");
			if (rowIndex > -1) {
				var rowData = $("#jqxgridProducts").jqxGrid("getrowdata", rowIndex);
				if (rowData) {
					var dataMap = {
						productCategoryId: rowData.productCategoryId,
						productId: rowData.productId,
					};
					$.ajax({
							type: 'POST',
							url: "removeProductCategoryMemberAjax",
							data: dataMap,
							beforeSend: function(){
								$("#loader_page_common").show();
							},
							success: function(data){
								jOlbUtil.processResultDataAjax(data, "default", function(){
											$('#container').empty();
								        	$('#jqxNotification').jqxNotification({ template: 'info'});
								        	$("#jqxNotification").html("${uiLabelMap.wgupdatesuccess}");
								        	$("#jqxNotification").jqxNotification("open");
								        	
								        	$("#jqxgridProducts").jqxGrid("updatebounddata");
										}
								);
							},
							error: function(data){
								alert("Send request is error");
							},
							complete: function(data){
								$("#loader_page_common").hide();
							},
					});
				}
			} else {
				jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseProduct}");
			}
		};
		var getObj = function(){
			return {
				productCategoryMemberGRID: productCategoryMemberGRID
			}
		};
		return {
			init: init,
			toogleEditableGrid: toogleEditableGrid,
			getObj: getObj,
			addNewProduct: addNewProduct,
			removeProduct: removeProduct,
		};
	}());
	
	<#--
	handlekeyboardnavigation: function (event) {
        var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
        if (key == 70 && event.ctrlKey) {
			$("#jqxgridProducts").jqxGrid("clearfilters");
			return true;
        }
	}
	-->
</script>