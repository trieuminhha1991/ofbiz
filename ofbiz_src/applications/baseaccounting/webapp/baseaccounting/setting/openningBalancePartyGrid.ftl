<#assign dataField="[
	{name: 'partyId', type: 'string'},
	{name: 'fullName', type: 'string'},
	{name: 'glAccountId', type: 'string'},
	{name: 'organizationPartyId', type: 'string'},
	{name: 'accountType', type: 'string'},
	{name: 'accountName', type: 'string'},
	{name: 'openingCrBalance', type: 'number'},
	{name: 'openingDrBalance', type: 'number'}
 ]"/>

<#assign columnlist="
		{text: '${StringUtil.wrapString(uiLabelMap.CustomerId)}', dataField: 'partyId', width: '13%', editable: false},
		{text: '${StringUtil.wrapString(uiLabelMap.Customer)}', dataField: 'fullName', width: '30%', editable: false},
		{text: '${StringUtil.wrapString(uiLabelMap.BACCGlAccountId)}', dataField: 'glAccountId', width: '13%', editable: false},
		{text: '${StringUtil.wrapString(uiLabelMap.BACCAccountName)}', dataField: 'accountName', editable: false},
		{text: '${StringUtil.wrapString(uiLabelMap.BACCOpeningCrBalance)}', dataField: 'openingCrBalance', filtertype : 'number',editable: true, columntype: 'numberinput',
			cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
				if(!value) return '<span>' + formatcurrency(0) + '</span>';
				return '<span>' + formatcurrency(value) + '</span>';
			},
			initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
				editor.jqxNumberInput({inputMode: 'advanced',min : 0, decimalDigits: 0, digits: 12, max: 100000000000});
			},
			cellbeginedit: function (row, datafield, columntype) {
				var data = $('#openingBalGrid').jqxGrid('getrowdata', row);
		        if (data['accountType'] == 'DEBIT') return false;
		    },validation: function (cell, value) {
				 if (value < 0) {
					 return { result: false, message: '${uiLabelMap.BACCBalanceRequired}'};
				 }
				 else 
					 return true;
      		 }
		},
		{text: '${StringUtil.wrapString(uiLabelMap.BACCOpeningDrBalance)}', dataField: 'openingDrBalance', filtertype : 'number',editable: true, columntype: 'numberinput',
			cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
				if(!value) return '<span>' + formatcurrency(0) + '</span>';
				return '<span>' + formatcurrency(value) + '</span>';
			},
			initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
				editor.jqxNumberInput({inputMode: 'advanced',min : 0, decimalDigits: 0, digits: 12, max: 100000000000});
			},
			cellbeginedit: function (row, datafield, columntype) {
				var data = $('#openingBalGrid').jqxGrid('getrowdata', row);
		        if (data['accountType'] == 'CREDIT') return false;
		    },
		    validation: function (cell, value) {
				 if (value < 0) {
					 return { result: false, message: '${uiLabelMap.BACCBalanceRequired}'};
				 }
				 else 
					 return true;
      		 }
		}
 "/>
	<@jqGrid id="openingBalGrid" filtersimplemode="true" editable="true" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListGlAccountBalParty&organizationPartyId=${organizationPartyId?if_exists}" dataField=dataField columnlist=columnlist
		 updateUrl="jqxGeneralServicer?jqaction=U&sname=updateGlAccountBalParty" editColumns="glAccountId;organizationPartyId;partyId;openingCrBalance(java.math.BigDecimal);openingDrBalance(java.math.BigDecimal)"
			 removeUrl="jqxGeneralServicer?jqaction=D&sname=removeGlAccountBalParty" deleteColumn="glAccountId;organizationPartyId;partyId"
		 alternativeAddPopup="newOpeningBalPopup" addrow="true" addType="popup" editmode="selectedcell" deleterow="true"
	/>
