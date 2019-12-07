<div class="row-fluid">
	<div class="span12">
		<div class="form-horizontal form-window-content-custom">
			<div class="row-fluid">
				<div class="span6">
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSPSSalesChannel}</label>
						</div>
						<div class="span7">
							<div id="productStoreId"><div id="productStoreGrid"></div></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSRangeDate}</label>
						</div>
						<div class="span7">
							<div class="row-fluid">
								<div class="span12">
									<div id="changeDateTypeId"></div>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span6">
									<div id="fromDate"></div>
								</div>
								<div class="span6">
									<div id="thruDate"></div>
								</div>
							</div>
				   		</div>
					</div>
				</div><!-- .span6 -->
				<div class="span6">
					<div class="row-fluid">
				   		<div class="span2">
				   			<div class="pull-left">
								<button type="button" id="btnFindProductPrice" class="btn btn-small btn-primary"><i class="fa fa-search"></i>&nbsp;${uiLabelMap.BSActionFind}</button>
					   		</div>
				   		</div>
				   		<div class="span10">
				   			<span class="help-inline" style="color: #657ba0; font-style: italic"><b>${uiLabelMap.BSNoteY}</b>: ${uiLabelMap.BSCurrentPriceWhichInTwoColumnTwoIsCalculatedAtNowTimestamp}</span>
				   		</div>
					</div>
				</div><!-- .span6 -->
			</div><!-- .row-fluid -->
		</div>
	</div>
</div>

<div class="row-fluid">
	<div class="span12">
		<#include "productSalesPriceChangeProdItems.ftl"/>
	</div>
</div><!-- .row-fluid -->


<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true/>

<script type="text/javascript">
	var urlSNameFindProdPrice = "JQGetListProductSalesPriceChange";
	
	$(function(){
		OlbProdPriceFind.init();
	});
	var OlbProdPriceFind = (function(){
		var changeDateTypeDDL;
		var productStoreDDB;
		
		var init = function(){
			initElement();
			initComplexElement();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.dateTimeInput.create("#fromDate", {width: '100%', allowNullDate: true, value: null, showFooter: true, formatString: 'dd/MM/yyyy', disabled: true});
			jOlbUtil.dateTimeInput.create("#thruDate", {width: '100%', allowNullDate: true, value: null, showFooter: true, formatString: 'dd/MM/yyyy', disabled: true});
		};
		var initComplexElement = function(){
			var changeDateTypeData = [
				{typeId: "TODAY", description: "${uiLabelMap.BSToday}"},
				{typeId: "TOMORROW", description: "${uiLabelMap.BSTomorrow}"},
				{typeId: "YESTERDAY", description: "${uiLabelMap.BSYesterday}"}
			];
			var configChangeDateType = {
				width: '100%',
				placeHolder: "${uiLabelMap.BSClickToChoose}",
				useUrl: false,
				key: 'typeId',
				value: 'description',
				autoDropDownHeight: true,
				addNullItem: true,
			}
			changeDateTypeDDL = new OlbDropDownList($("#changeDateTypeId"), changeDateTypeData, configChangeDateType, ["TODAY"]);
			
			var configProductStore = {
				useUrl: true,
				root: 'results',
				widthButton: '100%',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [
					{name: 'productStoreId', type: 'string'}, 
					{name: 'storeName', type: 'string'},
				],
				columns: [
					{text: "${uiLabelMap.BSPSChannelId}", datafield: 'productStoreId', width: '30%'},
					{text: "${uiLabelMap.BSPSChannelName}", datafield: 'storeName', width: '70%'},
				],
				url: 'JQGetListProductStorePriceRule',
				useUtilFunc: true,
				
				key: 'productStoreId',
				description: ['storeName'],
				autoCloseDropDown: true,
				filterable: true,
				sortable: true,
			};
			productStoreDDB = new OlbDropDownButton($("#productStoreId"), $("#productStoreGrid"), null, configProductStore, []);
		};
		var initEvent = function(){
			$("#btnFindProductPrice").on("click", function(){
				var changeDateTypeId = changeDateTypeDDL.getValue();
				var productStoreId = productStoreDDB.getValue();
				var fromDate;
				var thruDate;
				if (typeof($('#fromDate').jqxDateTimeInput('getDate')) != 'undefined' && $('#fromDate').jqxDateTimeInput('getDate') != null) {
					fromDate = $('#fromDate').jqxDateTimeInput('getDate').getTime();
				}
				if (typeof($('#thruDate').jqxDateTimeInput('getDate')) != 'undefined' && $('#thruDate').jqxDateTimeInput('getDate') != null) {
					thruDate = $('#thruDate').jqxDateTimeInput('getDate').getTime();
				}
				
				var otherParam = "";
				if (OlbCore.isNotEmpty(changeDateTypeId)) otherParam += "&changeDateTypeId=" + changeDateTypeId;
				if (OlbCore.isNotEmpty(productStoreId)) otherParam += "&productStoreId=" + productStoreId;
				if (OlbCore.isNotEmpty(fromDate)) otherParam += "&fromDate=" + fromDate;
				if (OlbCore.isNotEmpty(thruDate)) otherParam += "&thruDate=" + thruDate;
				
				productItemsOLBG.updateSource("jqxGeneralServicer?sname=" + urlSNameFindProdPrice + otherParam);
			});
			
			changeDateTypeDDL.selectListener(function(itemData){
				var changeDateTypeId = itemData.value;
				if (OlbCore.isNotEmpty(changeDateTypeId)) {
					$("#fromDate").jqxDateTimeInput("disabled", true);
					$("#thruDate").jqxDateTimeInput("disabled", true);
				} else {
					$("#fromDate").jqxDateTimeInput("disabled", false);
					$("#thruDate").jqxDateTimeInput("disabled", false);
				}
			});
		};
		var getObj = function() {
			return {
				changeDateTypeDDL: changeDateTypeDDL,
				productStoreDDB: productStoreDDB
			}
		};
		return {
			init: init,
			getObj: getObj
		};
	}());
</script>
