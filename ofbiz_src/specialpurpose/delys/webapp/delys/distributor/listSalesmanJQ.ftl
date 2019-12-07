<script type="text/javascript">
</script>
<#assign dataField = "[{name: 'partyId', type: 'string'}, 
{name: 'fullName', type: 'string'},
{name: 'fullAddress', type:'string'}
]"/>
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.DASalesmanId)}', dataField: 'partyId', width: '30%'},
{text: '${StringUtil.wrapString(uiLabelMap.DAName)}', dataField: 'fullName', width: '30%'},
{text: '${StringUtil.wrapString(uiLabelMap.DAAddress)}', dataField: 'fullAddress', filterable: false 

}
"/>

<@jqGrid id="jqxgrid" addrow="false" clearfilteringbutton="true" editable="false" columnlist=columnlist dataField=dataField
viewSize="25" showtoolbar="false" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="false" addType="popup"
url="jqxGeneralServicer?sname=JQGetListSalesmans" mouseRightMenu="false" 
/> 