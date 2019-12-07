<script type="text/javascript">
	<#assign purposeType = delegator.findList("ContactMechPurposeType", null, null, null, null, false)/>
	var purposeTypes = new Array();
	<#list purposeType as item>
		var row = {};
		<#assign descPurpose = StringUtil.wrapString(item.get("description",locale))>
		row['contactMechPurposeTypeId'] = '${item.contactMechPurposeTypeId}';
		row['description'] = '${descPurpose?if_exists}';
		purposeTypes.push(row);
	</#list>
</script>
<#assign dataField="[{name: 'contactMechId', type: 'string'},
					{name: 'toName', type: 'string'},
					{name: 'attnName', type: 'string'},
					{name: 'address1', type: 'string'},
					{name: 'city', type: 'string'},
					{name: 'stateProvinceGeoId', type: 'string'},
					{name: 'stateProvinceGeoName', type: 'string'},
					{name: 'postalCode', type: 'string'},
					{name: 'countryGeoId', type: 'string'},
					{name: 'countryGeoName', type: 'string'},
					{name: 'districtGeoId', type: 'string'},
					{name: 'districtGeoName', type: 'string'},
					{name: 'wardGeoId', type: 'string'},
					{name: 'contactMechPurposeTypeId', type: 'string'},
					{name: 'wardGeoName', type: 'string'}]"/>
<#assign columnlist = "{text: '${uiLabelMap.BSContactMechId}', datafield: 'contactMechId', width: '14%'},
					{text: '${uiLabelMap.BSContactMechPurposeTypeId}', datafield: 'contactMechPurposeTypeId', width: '14%',
					cellsrenderer: function(row, column, value){
                            var data = grid.jqxGrid('getrowdata', row);
                            console.log(purposeTypes);
                            console.log(value);
                            for (var i = 0; i < purposeTypes.length; i ++){
                                if (value && value == purposeTypes[i].contactMechPurposeTypeId){
                                    return '<span>' + purposeTypes[i].description + '<span>';
                                }
                            }

                            return '<span>' + 'NA' + '<span>';
                        }
                    },
					{text: '${uiLabelMap.BSReceiverName}', datafield: 'toName', width: '12%'},
					{text: '${uiLabelMap.BSOtherInfo}', datafield: 'attnName', width: '12%'},
					{text: '${uiLabelMap.BSAddress}', datafield: 'address1', width: '22%'},
					{text: '${uiLabelMap.BSWard}', datafield: 'wardGeoName', width: '10%'},
					{text: '${uiLabelMap.BSCounty}', datafield: 'districtGeoName', width: '10%'},
					{text: '${uiLabelMap.BSStateProvince}', datafield: 'stateProvinceGeoName', width: '10%'},
					{text: '${uiLabelMap.BSCountry}', datafield: 'countryGeoName', width: '10%'}"/>
<#assign customcontrol1 = ""/>
<#if !showtoolbar?exists>
<#assign customcontrol1 = "icon-plus open-sans@${uiLabelMap.BSAddNew}@javascript:addNewShippingAddress()"/>
</#if>

<@jqGrid id="shippingContactMechGrid" url="jqxGeneralServicer?sname=JQGetPartyAddress&partyId=${parameters.partyId?if_exists}&distinctAddress=Y" dataField=dataField columnlist=columnlist
	 showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addType="popup" customTitleProperties="BSAddress"
	 customcontrol1=customcontrol1 filterable="true" clearfilteringbutton="true"
	 addrow="false" contextMenuId="contextMenushippingContactMechGrid" mouseRightMenu="true"/>

<div id='contextMenushippingContactMechGrid' style="display:none">
	<ul>
		<li id="mnEdit"><i class="fa fa-pencil"></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.BSEdit)}</li>
		<li id="mnDelete"><i class="fa fa-trash"></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.BSDelete)}</li>
	    <li id="mnRefresh"><i class="fa fa-refresh"></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>
<div id="shipToCustomerPartyId" data-value="${parameters.partyId?if_exists}"></div>
<#if !showtoolbar?exists>
${screens.render("component://basesales/widget/PartyScreens.xml#PopupNewContactMechShippingAddress")}
<@jqOlbCoreLib />
<script type="text/javascript" src="/salesresources/js/party/contactNewShippingAddressPopup.js"></script>
<script>
	var loadedWindowAddShipping = false;
	$("#contextMenushippingContactMechGrid").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', theme: 'olbius'});
	$("#contextMenushippingContactMechGrid").on("itemclick", function (event) {
	    var args = event.args;
	    var itemId = $(args).attr("id");
	    switch (itemId) {
		case "mnRefresh":
			$("#shippingContactMechGrid").jqxGrid("updateBoundData");
			break;
		case "mnDelete":
			var rowindex = $("#shippingContactMechGrid").jqxGrid('getselectedrowindex');
			var data = $("#shippingContactMechGrid").jqxGrid("getrowdata", rowindex);
			if (data) {
				jOlbUtil.confirm.dialog(multiLang.ConfirmDelete, 
						function () {
							$.ajax({
								type: 'POST',
								url: 'deletePartyContactMechAndPurpose',
								data: {
									partyId: "${parameters.partyId?if_exists}",
									contactMechId: data.contactMechId,
								},
								success: function(data){
									$("#shippingContactMechGrid").jqxGrid("updateBoundData");
								},
								error: function(data){
									alert("Send request is error");
								},
							});
							
						}
					);
			}
			break;
		case "mnEdit":
			var rowindex = $("#shippingContactMechGrid").jqxGrid('getselectedrowindex');
			var data = $("#shippingContactMechGrid").jqxGrid("getrowdata", rowindex);
			if (data) {
				OlbContactMechEdit.openWindowEditContactMech(data.contactMechId);
			}
			break;
		default:
			break;
		}
	});
	$(function(){
		$('body').on('createContactmechComplete', function(event, contactMechIdNew){
			$('#alterpopupWindowContactMechNew').jqxWindow('close');
		});
	});
	function addNewShippingAddress() {
		if (typeof($("#alterpopupWindowContactMechNew")) != 'undefined') {
			var customerId = partyIdPram;
			if (!OlbCore.isNotEmpty(customerId)) {
				jOlbUtil.alert.error(uiLabelMap.BSYouNeedChooseCustomerIdBefore);
				return false;
			} else {
				var shipToPartyId = partyIdPram;
				if (!OlbCore.isNotEmpty(shipToPartyId)) {
					shipToPartyId = customerId;
				}
				$('#wn_partyId').val(shipToPartyId);
				$("#alterpopupWindowContactMechNew").jqxWindow('open');
				if (!loadedWindowAddShipping) {
					OlbShippingAdrNewPopup.initOther();
					loadedWindowAddShipping = true;
				}
			}
		}
	}
	
	
	var listAddressObj = (function(){
			
		function validate(){
			var listContactMechs = $('#shippingContactMechGrid').jqxGrid('getrows');
			if(listContactMechs.length < 1){
				return false;
			}
			return true;
		}
		
		function getValidate(){
			var val = validate();
			
			if(!val){
				message = multiLang.BSAddressIsNull;
				jOlbUtil.confirm.dialog(message);
				return false
			}else{
				return true;
			}
		}
		
		return{
			getValidate: getValidate,
		}
	
	}());
	
</script>
</#if>	