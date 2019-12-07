<#assign id1="groupContacts"/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmaskedinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script src="/delys/images/js/generalUtils.js"></script>
<script>
	<#assign roleType = delegator.findList("RoleType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", "DELYS_CUSTOMER"), null, null, null, true) />
	var roleTypeData =  [<#list roleType as item>{<#assign description = StringUtil.wrapString(item.get("description", locale)?if_exists)>'roleTypeId' : "${item.roleTypeId?if_exists}",'description' : "${description?if_exists}"	},</#list>]; 
	var uiLabelMap = {
		sender : '${uiLabelMap.sender}',
		receiver: '${uiLabelMap.receiver}',
		InTime : '${uiLabelMap.InTime}',
		communicationType : '${uiLabelMap.communicationType}'
	};
	var locale = "${locale}";
	var SearchCustomerType = function(container){
		var str = "<div id='groupCustomerId' class='pull-right margin-top5'></div>";
		container.append(str);
		setTimeout(function(){
			var obj = $('#groupCustomerId'); 
			obj.jqxDropDownList({
				height: 24, 
				dropDownWidth: 300, 
				source: roleTypeData, 
				displayMember: "description", 
				valueMember: "roleTypeId", 
				placeHolder: "${uiLabelMap.ChooseCustomerGroup}",
				theme: 'olbius',
				autoDropDownHeight: true
				});
			obj.on('change', function () {
				outFilterCondition = ChangeFilterCondition($(this).val());
				$('#${id1}').jqxGrid('updatebounddata');	 
			});
		}, 100);
	};
	var ChangeFilterCondition = function(val){
		var str = "|OLBIUS|roleTypeIdTo"
			   	+ "|SUIBLO|" + val
				+ "|SUIBLO|" + "EQUAL"
				+ "|SUIBLO|" + "or";
		return str;
	};
</script>
<script>
	<#include "initContact.ftl" />
</script>
<#include "initRowDetailContact.ftl"/>
<div id="group" class="tab-pane in active">
	<#assign dataField="[{ name: 'groupName', type: 'string' },
			 { name: 'partyId', type: 'string' },
			 { name: 'contactNumber', type: 'string'},
			 { name: 'address', type: 'string'},
			 { name: 'assign', type: 'string'}]"/>
	<#assign columnlist="{ text: '${uiLabelMap.storeName}', filtertype: 'input', datafield: 'groupName', width: '350'},
					 { text: '${uiLabelMap.phoneNumber}', filtertype: 'input', datafield: 'contactNumber', columntype:'custom', editable: false, width: '150'},
                     { text: '${uiLabelMap.address}', filtertype: 'input', datafield: 'address', columntype:'custom', editable: false,  
					    cellsrenderer: function (row, column, value) {
					    	var addr = value.address1 ? value.address1 : '';
					    	var city = value.city ? value.city : '';
					    	var x = addr + ' ' + city;
				    		return \"<div class='custom-cell-grid'>\"+x+\"</div>\";
                     }}"/>

<@jqGrid id="${id1}" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" 
		showtoolbar="true" autoMeasureHeight="true" autoheight="false" scrollmode='logical'
		selectionmode="singlerow"
		filtersimplemode="true"  
		editable="false" 
		editmode="click"
		showtoolbar = "true" autorowheight="true"
		initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="300"
		url="jqxGeneralServicer?sname=getListCustomerMT"
		/>	
	</div>
<#include "initModalContact.ftl"/>
<#include "initIssueForm.ftl"/>

