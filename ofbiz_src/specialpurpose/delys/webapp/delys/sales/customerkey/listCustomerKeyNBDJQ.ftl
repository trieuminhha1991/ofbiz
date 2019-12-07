<#assign dataField = "[{name: 'partyIdFrom', type: 'string'}, 
{name: 'partyId', type: 'string'},
{name: 'fullName', type: 'string'},
{name: 'relStatusId', type:'string'},
{name: 'fromDate', type: 'date', other: 'Date'},
{name: 'thruDate', type: 'date', other: 'Date'}

]"/>
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.partyIdFrom)}', dataField: 'partyIdFrom', width: '20%', editable: false,hidden: true}, 
{text: '${StringUtil.wrapString(uiLabelMap.partyId)}', dataField: 'partyId', width: '20%'},
{text: '${StringUtil.wrapString(uiLabelMap.fullName)}', dataField: 'fullName', width: '20%'},
{text: '${StringUtil.wrapString(uiLabelMap.statusId)}', dataField: 'relStatusId', width: '20%'},
{text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', dataField: 'fromDate', cellsformat: 'd', width: '20%', filtertype:'range', columntype: 'datetimeinput',
	createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
		editor.jqxDateTimeInput({ });
	}
},
{text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', dataField: 'thruDate', cellsformat: 'd', width: '20%', filtertype:'range', columntype: 'datetimeinput',
	createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
		editor.jqxDateTimeInput({ });
	}
},
"/>

<@jqGrid id="jqxgrid" addrow="false" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow1" columnlist=columnlist dataField=dataField
viewSize="25" showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="false"
url="jqxGeneralServicer?sname=JQGetListCKNBD" mouseRightMenu="false" viewSize="10" pagesizeoptions="['5', '15', '30', '50']" 
/> 