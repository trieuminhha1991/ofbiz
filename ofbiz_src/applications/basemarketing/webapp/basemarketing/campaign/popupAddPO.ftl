<div id="popupAddPO" style="display : none;">
	<div>
		${uiLabelMap.DmsCreatePO}
	</div>
	<div style="overflow: hidden;">
		<div class="row-fluid">
			<div class="row-fluid">
				<div class="row-fluid no-left-margin">
					<label class="span5 line-height-25 align-right line-height-25"></label>
					<div class="span7 margin-bottom10">
						<div id="productTypeIdAdd"></div>
					</div>
				</div>
				<div class="row-fluid no-left-margin">
					<div class="span12 margin-bottom10">
						<#assign dataField="[{name: 'productId', type: 'string'},
								            {name: 'quantity', type: 'number'},
								            {name: 'inventoryQuantity', type: 'number'},
								            {name: 'poQuantity', type: 'number'},]"/>
						<#assign columnlist = "{text: '${uiLabelMap.DmsProductId}',datafield: 'productId', columntype: 'dropdownlist', cellsalign: 'left',
													cellsrenderer: function(row, colum, value){
														value?value=mapProducts[value]:value;
														return '<span>' + value + '</span>';
													}, createeditor: function(row, column, editor){
														editor.jqxDropDownList({ source: listProsucts, displayMember: 'productId', valueMember: 'productId', placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}',
												            renderer: function (index, label, value) {
												                var datarecord = listProsucts[index];
												                return datarecord.productName;
												            },selectionRenderer: function () {
												                var item = editor.jqxDropDownList('getSelectedItem');
												                if (item) {
																	return '<span title=' + item.value +'>' + mapProducts[item.value] + '</span>';
												                }
												                return '<span>${StringUtil.wrapString(uiLabelMap.filterchoosestring)}</span>';
												            }
												        });
													}
											},
											{text: '${uiLabelMap.quantity}', datafield: 'quantity', width: '150', columntype: 'numberinput', editable: false},
											{text: '${uiLabelMap.inventoryQuantity}', datafield: 'inventoryQuantity', width: '150', columntype: 'numberinput', editable: false},
											{text: '${uiLabelMap.poQuantity}', datafield: 'poQuantity', width: '150', columntype: 'numberinput'}"/>
						<@jqGrid url="" id="MarketingProductPO" customLoadFunction="true" jqGridMinimumLibEnable="false" selectionmode="checkbox"
								addrow="false" dataField=dataField columnlist=columnlist virtualmode="false" pageable="true" editable="true"
								width="100%" autoshowloadelement="false" showdefaultloadelement="false"
								showtoolbar="true" autorowheight="true" deleterow="true"  filterable="false" editmode="click"
								isShowTitleProperty="false" addType="popup" alternativeAddPopup="popupAddProduct" sortable="false"
								customtoolbaraction="SearchAccountReconciliation"/>
					</div>
				</div>
			</div>
		</div>

		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button type="button" id="cancelProductPO" class='btn btn-danger form-action-button pull-right'>
						<i class='fa fa-remove'> </i> ${uiLabelMap.Cancel}
					</button>
					<button type="button" id="saveProductPO" class='btn btn-primary form-action-button pull-right'>
						<i class='fa fa-check'> </i> ${uiLabelMap.Save}
					</button>
				</div>
			</div>
		</div>
	</div>
</div>

<script>
	var PO = (function() {
		var form = $('#popupAddPO');
		var initWindow = function() {
			form.jqxWindow({
				width : 800,
				height : 600,
				resizable : true,
				isModal : true,
				autoOpen : false,
				cancelButton : $("#cancelProductPO"),
				modalOpacity : 0.7,
				initContent: function(){
					initGridMarketingProductPO();
				}
			});
			form.on("close", function() {
				clearForm();
			});
		};
		var createPO = function() {
			bootbox.dialog(uiLabelMap.ConfirmCreateCampaign, [{
				"label" : uiLabelMap.Cancel,
				"icon" : 'fa fa-remove',
				"class" : 'btn  btn-danger form-action-button pull-right',
				"callback" : function() {
					bootbox.hideAll();
				}
			}, {
				"label" : uiLabelMap.OK,
				"icon" : 'fa-check',
				"class" : 'btn btn-primary form-action-button pull-right',
				"callback" : function() {
					send(url);
				}
			}]);
		};
		var send = function() {
			var data = {
				requirementTypeId : "PO_REQ_MKT_ACT",
				facilityId : "",
				description : "",
				requirementByDate : "",
				requirementStartDate : "",
				listProducts : []
			};
			$.ajax({
				url : "createRequirementPurchaseOrderToPO",
				type : "POST",
				data : data,
				success : function(res) {
					if (url == "createMarketingCampaignAndItem") {
						var id = res.marketingCampaignId;
						if (id) {
							window.location.href = "CreateCampaignMarketing?id=" + id;
						} else {
							bootbox.alert(uiLabelMap.CreateError);
						}
					} else {
						bootbox.alert(uiLabelMap.UpdateSuccessfully);
					}
				}
			});
		};
		var ShowFacility = function(container){
			var str = "<div id='facility' class='pull-right margin-top5'></div>";
			container.append(str);
			$('#glAccountIdList').jqxDropDownList({
				height: 24,
				dropDownWidth: 400,
				source: glAccountData,
				displayMember: "description",
				valueMember: "glAccountId",
				placeHolder: "${uiLabelMap.ChooseGlAccountId}",
				theme: theme
				});
			$("#glAccountIdList").on('change', function () {
				outFilterCondition = ChangeFilterCondition($(this).val());
				$('#jqxgrid').jqxGrid('updatebounddata');
			});
			$('#glAccountIdList').jqxDropDownList('selectedIndex', 0);
		};
		var init = function(){
			initWindow();
		};
		return {
			init: init
		};
	})();
	$(document).ready(function(){
		PO.init();
	});
</script>