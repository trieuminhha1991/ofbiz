<#assign datafields = "[
				{ name: 'salesForecastId', type: 'string' },
				{ name: 'parentSalesForecastId', type: 'string' },
				{ name: 'organizationPartyId', type: 'string' },
				{ name: 'internalPartyId', type: 'string' },
				{ name: 'customTimePeriodId', type: 'string' },
				{ name: 'periodName', type: 'string' },
				{ name: 'fromDate', type: 'date' },
				{ name: 'thruDate', type: 'date' },
				{ name: 'quotaAmount', type: 'string' },
				{ name: 'forecastAmount', type: 'string' },
				{ name: 'closedAmount', type: 'string' },
				{ name: 'percentOfQuotaForecast', type: 'string' },
				{ name: 'percentOfQuotaClosed', type: 'string' },
				{ name: 'pipelineAmount', type: 'string' }]">

<#assign columns = "[
				{ text: '${StringUtil.wrapString(uiLabelMap.BSSalesForecastId)}', datafield: 'salesForecastId', width: '14%',
					cellsrenderer: function(row, colum, value) {
						return '<span><a href=\"viewSalesFCDetailForPO?salesForecastId=' + value + '\">' + value + '</a></span>';
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSSalesPeriodName)}', datafield: 'periodName'},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSFromDate)}', datafield: 'fromDate', width: '14%', cellsformat: 'dd/MM/yyyy', filtertype: 'range',
					cellsrenderer: function(row, colum, value) {
						return \"<span>\" + jOlbUtil.dateTime.formatDate(value) + \"</span>\";
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSThruDate)}', datafield: 'thruDate', width: '14%', cellsformat: 'dd/MM/yyyy', filtertype: 'range',
					cellsrenderer: function(row, colum, value) {
						return \"<span>\" + jOlbUtil.dateTime.formatDate(value) + \"</span>\";
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSOrganizationId)}', datafield: 'organizationPartyId', width: '14%' },
				{ text: '${StringUtil.wrapString(uiLabelMap.BSSalesCustomTimePeriodId)}', datafield: 'customTimePeriodId', width: '14%', hidden: true }]">

<@jqGrid id="jqxgrid" addrow="false" clearfilteringbutton="true" editable="false" alternativeAddPopup="addRival"
	columnlist=columns dataField=datafields addType="popup"
	showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true"
	url="jqxGeneralServicer?sname=JQListSalesForecast"/>