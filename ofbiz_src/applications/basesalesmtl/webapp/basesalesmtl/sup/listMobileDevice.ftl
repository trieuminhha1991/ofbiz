<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>
<script>

</script>
<#assign dataField = "[
		{name : 'mobileDeviceId',type : 'string'},
		{name : 'deviceId',type : 'string'},
		{name : 'partyId',type : 'string'},
		{name : 'partyCode',type : 'string'},
		{name : 'partyName',type : 'string'},
		{name : 'createdByUserLogin',type : 'string'},
		{name : 'note',type : 'string'},
        {name : 'createdTime', type : 'date', other : 'Timestamp'},
        {name : 'updatedTime', type : 'date', other : 'Timestamp'},
    ]"/>

<#assign columnlist =  "{ text: '${uiLabelMap.DASeqId}', dataField: '', width: '5%', columntype: 'number',
                            cellsrenderer: function (row, column, value) {
                                return \"<div style='margin:4px;'>\" + (value + 1) + \"</div>\";
                            }
                        },
                        { text : '${uiLabelMap.BSSalesmanCode}',datafield : 'partyCode',width : '15%', filterable: true, sortable: true},
                        { text : '${uiLabelMap.BSSalesman}',datafield : 'partyName',width : '25%', filterable: true, sortable: true},
                        { text : '${uiLabelMap.BSDeviceId}',datafield : 'deviceId',width : '25%', filterable: true, sortable: true},
                        { text : '${uiLabelMap.BSCreatedDate}',datafield : 'createdTime', cellsformat: 'dd/MM/yyyy HH:mm:ss',filterable: true, sortable: true, width : '13%', filterType : 'range'},
                        { text : '${uiLabelMap.BSLastLoginTime}',datafield : 'updatedTime', cellsformat: 'dd/MM/yyyy HH:mm:ss',filterable: true, sortable: true, width : '17%', filterType : 'range'},
"/>
<div id="notification" style="width : 100%;"></div>
<@jqGrid id="jqxgridListMobileDevice" filtersimplemode="true" filterable="true" editable="false" addrefresh="true" editrefresh="true" updateoffline="false" showtoolbar="true" addType="popup" alternativeAddPopup="alterpopupWindow" dataField=dataField columnlist=columnlist  clearfilteringbutton="true" rowdetailsheight="275" initrowdetails="false"
url="jqxGeneralServicer?sname=JQGetListMobileDeviceLog"
/>