<#assign dataField="[
	{name: 'glAccountId', type: 'string'},
	{name: 'organizationPartyId', type: 'string'},
	{name: 'accountType', type: 'string'},
	{name: 'accountName', type: 'string'},
	{name: 'openingCrBalance', type: 'string'},
	{name: 'openingDrBalance', type: 'string'}
 ]"/>

<#assign columnlist="
		{text: '${StringUtil.wrapString(uiLabelMap.BACCGlAccountId)}', dataField: 'glAccountId', width: '13%', editable: false},
		{text: '${StringUtil.wrapString(uiLabelMap.BACCAccountName)}', dataField: 'accountName', editable: false},
		{text: '${StringUtil.wrapString(uiLabelMap.BACCOpeningCrBalance)}', dataField: 'openingCrBalance', editable: true, columntype: 'numberinput',
			cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
				return '<span>' + formatcurrency(value) + '</span>';
			},
			initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
				editor.jqxNumberInput({inputMode: 'advanced', decimalDigits: 0, digits: 12, max: 100000000000});
			},
			cellbeginedit: function (row, datafield, columntype) {
				var data = $('#openingBalGrid').jqxGrid('getrowdata', row);
		        if (data['accountType'] == 'DEBIT') return false;
		    }
		},
		{text: '${StringUtil.wrapString(uiLabelMap.BACCOpeningDrBalance)}', dataField: 'openingDrBalance', editable: true, columntype: 'numberinput',
			cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
				return '<span>' + formatcurrency(value) + '</span>';
			},
			initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
				editor.jqxNumberInput({inputMode: 'advanced', decimalDigits: 0, digits: 12, max: 100000000000});
			},
			cellbeginedit: function (row, datafield, columntype) {
				var data = $('#openingBalGrid').jqxGrid('getrowdata', row);
		        if (data['accountType'] == 'CREDIT') return false;
		    }
		}
 "/>
<#assign listBals = delegator.findList("GlAccountBalance", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("organizationPartyId", '${userLogin.lastOrg}' ), null, null, null, false) />
<#if listBals?has_content>
	<@jqGrid id="openingBalGrid" filtersimplemode="true" alternativeAddPopup="newOpeningBalPopup" editable="true" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListGlAccountBal&organizationPartyId=${userLogin.lastOrg}" dataField=dataField columnlist=columnlist
		 updateUrl="jqxGeneralServicer?jqaction=U&sname=updateGlAccountBal" editColumns="glAccountId;organizationPartyId;customTimePeriodId;openingCrBalance(java.math.BigDecimal);openingDrBalance(java.math.BigDecimal)"
		 alternativeAddPopup="newOpeningBalPopup" addrow="false" addType="popup" editmode="selectedcell"
	/>
<#else>
	<@jqGrid id="openingBalGrid" filtersimplemode="true" alternativeAddPopup="newOpeningBalPopup" editable="true" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListGlAccountBal&organizationPartyId=${userLogin.lastOrg}" dataField=dataField columnlist=columnlist
		 updateUrl="jqxGeneralServicer?jqaction=U&sname=updateGlAccountBal" editColumns="glAccountId;organizationPartyId;customTimePeriodId;openingCrBalance(java.math.BigDecimal);openingDrBalance(java.math.BigDecimal)"
		 alternativeAddPopup="newOpeningBalPopup" addrow="true" addType="popup" editmode="selectedcell"
	/>
</#if>