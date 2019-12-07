<#assign dataField="[
				{name: 'opponentEventId', type: 'string'},
				{name: 'partyId', type: 'string'},
				{name: 'groupName', type: 'string'},
				{name: 'comment', type: 'string'},
				{name: 'description', type: 'string'},
                {name: 'image', type: 'string'}
			]"/>
<#assign columnlist = "
                {text: '${StringUtil.wrapString(uiLabelMap.BSOpponentId)}', datafield: 'opponentEventId', width: '10%', filterable: true, pinned: true},
                {text: '${StringUtil.wrapString(uiLabelMap.BSOpponentName)}', datafield: 'groupName', width: '20%', sortable: false},
                {text: '${StringUtil.wrapString(uiLabelMap.BSOPComment)}', datafield: 'comment', width: '25%', sortable: false},
                {text: '${StringUtil.wrapString(uiLabelMap.BSDescription)}', datafield: 'description', width: '20%', sortable: false},
                {text: '${StringUtil.wrapString(uiLabelMap.BSImage)}', datafield: 'image', width: '25%', sortable: false},
			"/>
<@jqGrid id="jqxgridListOpponentInfo" url="jqxGeneralServicer?sname=JQGetListOpponentInfo" dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
		 showtoolbar="true" filtersimplemode="true" />
