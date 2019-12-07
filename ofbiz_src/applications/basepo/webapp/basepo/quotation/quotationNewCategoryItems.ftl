<#assign updateMode = false/>
<#if productQuotation?exists && productQuotation.productQuotationId?exists>
	<#assign updateMode = true/>
</#if>
<div class="row-fluid">
	<div class="span12">
		<#assign dataFieldCategoryItem = "[
					{name: 'productCategoryId', type: 'string'},
					{name: 'categoryName', type: 'string'},
					{name: 'amount', type: 'number', formatter: 'float'},
				]"/>
		<#assign columnlistCategoryItem = "
					{text: '${uiLabelMap.BSCategoryId}', dataField: 'productCategoryId', width: '24%', editable: false}, 
					{text: '${uiLabelMap.BSCategoryName}', dataField: 'categoryName', editable: false}, 
					{text: '${uiLabelMap.BSAmount}', dataField: 'amount', width: '20%',
						columntype: 'numberinput', cellsformat: 'd2',
				 		cellsrenderer: function(row, column, value){
					 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
				   			returnVal += formatnumber(value) + '</div>';
			   				return returnVal;
					 	},
						createeditor: function (row, cellvalue, editor) {
							editor.jqxNumberInput({spinButtons: true, digits: 8, decimalDigits: 3, inputMode: 'simple'});
						}
					}, 
				"/>
		<div id="jqxgridCategoryItem"></div>
	</div>
</div>

<#include "quotationNewCategoryItemAddPopup.ftl"/>

<script type="text/javascript">
	$(function(){
		OlbQuotationNewCategoryItem.init();
	});
	var OlbQuotationNewCategoryItem = (function(){
		var init = function(){
			initElementComplex();
		};
		var initElementComplex = function(){
			var configGridCategoryItem = {
				datafields: ${dataFieldCategoryItem},
				columns: [${columnlistCategoryItem}],
				width: '100%',
				height: 200,
				editable: true,
				sortable: false,
				filterable: false,
				pageable: true,
				pagesize: 10,
				showfilterrow: false,
				useUtilFunc: false,
				<#if updateMode>
				useUrl: true,
				url: 'jqxGeneralServicer?sname=JQListQuotationCategory&pageSize=0&productQuotationId=${productQuotation.productQuotationId}',
				<#else>
				useUrl: false,
				url: '',
				</#if>
				groupable: false,
				showdefaultloadelement:true,
				autoshowloadelement:true,
				selectionmode:'singlerow',
				virtualmode: false,
				showtoolbar:true,
				rendertoolbar: function(toolbar){
					<#assign customcontrol1 = "fa fa-plus@@javascript: void(0);@OlbQuotationNewCategoryItem.addCategoryItem()">
					<#assign customcontrol2 = "fa fa-minus@@javascript: void(0);@OlbQuotationNewCategoryItem.removeCategoryItem()">
					<@renderToolbar id="jqxgridCategoryItem" isShowTitleProperty="false" customTitleProperties="" isCollapse="false" showlist="false" 
						customControlAdvance="" filterbutton="" clearfilteringbutton="false" 
						addrow="false" addType="popup" alternativeAddPopup="alterpopupWindow" 
						deleterow="false" deleteConditionFunction="" deleteConditionMessage="" 
						virtualmode="false" addinitvalue="" primaryColumn="ID" addmultiplerows="false" 
						updaterow="" updatemultiplerows="" excelExport="false" toPrint="false" 
						customcontrol1=customcontrol1 customcontrol2=customcontrol2 customcontrol3="" customtoolbaraction=""/>
				},
			};
			new OlbGrid($("#jqxgridCategoryItem"), null, configGridCategoryItem, []);
		};
		var addCategoryItem = function(){
			$("#alterpopupWindowCategoryItemNew").jqxWindow("open");
		};
		var removeCategoryItem = function(){
			var rowIndex = $("#jqxgridCategoryItem").jqxGrid('getselectedrowindex');
			if (rowIndex == null || rowIndex < 0) {
				jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseRow}");
			} else {
				var rowData = $("#jqxgridCategoryItem").jqxGrid('getrowdata', rowIndex);
				if (rowData) {
					$("#jqxgridCategoryItem").jqxGrid('deleterow', rowData.uid);
				}
			}
		};
		return {
			init: init,
			addCategoryItem: addCategoryItem,
			removeCategoryItem: removeCategoryItem
		}
	}());
</script>