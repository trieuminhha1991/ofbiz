<#assign dataField="[{ name: 'webSiteId', type: 'string' },
					 { name: 'siteName', type: 'string'}]"/>

<#assign columnlist="{ text: '${uiLabelMap.BSWebSiteId}', datafield: 'webSiteId', width: 350, editable: false,
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					        return '<div style=margin:4px;><a href=activeWebsite?webSiteId=' + data.webSiteId + '>' + value + '</div>';
					    }
					},
					{ text: '${uiLabelMap.BSSiteName}', datafield: 'siteName'}"/>

<@jqGrid dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" editable="true"
	url="jqxGeneralServicer?sname=JQGetListWebsites"
	updateUrl="jqxGeneralServicer?sname=updateWebSite&jqaction=U"
	editColumns="webSiteId;siteName"/>