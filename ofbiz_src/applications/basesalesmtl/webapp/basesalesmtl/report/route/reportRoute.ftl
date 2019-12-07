<script type="text/javascript" src="/logresources/js/report/olbius.popup.extend.js"></script>
<div class="grid">
	<script id="gridTest">
		$(function(){
			var column = [
			{text: '${StringUtil.wrapString(uiLabelMap.OrgUnitName)}', datafield: {name: 'party_name', type: 'string'}, pinned: true, minWidth: '15%'},
			{text: '${StringUtil.wrapString(uiLabelMap.OrgUnitId)}', datafield: {name: 'party_id', type: 'string'}, pinned: true, width: '2%', hidden: true},
			{ text: '${StringUtil.wrapString(uiLabelMap.BSAgents)}', datafield: {name: 'party_id_to', type: 'string'}, width: '16%'},
            { text: '${StringUtil.wrapString(uiLabelMap.BSMRoute)}', datafield: {name: 'route_id', type: 'string'}, width: '25%'},
            { text: '${StringUtil.wrapString(uiLabelMap.BSMVisits)}', datafield: {name: '_count', type: 'string'}, width: '12%',
            	cellsrenderer: function (row, column, value) {
			        return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
			    }
            },
            { text: '${StringUtil.wrapString(uiLabelMap.BSMOrderVolume)}', datafield: {name: '_order', type: 'string'}, width: '12%',
            	cellsrenderer: function (row, column, value) {
			        return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
			    }
            },
            { text: '${StringUtil.wrapString(uiLabelMap.BACCCustomerIncomeStatement)}', datafield: {name: '_total', type: 'string'}, width: '12%',
            	cellsrenderer: function (row, column, value) {
            		return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
            	}
            }];
			
			OlbiusUtil.treeGrid({
				id: 'gridTest',
				columns: column,
				service: 'salesOrder',
				button: true,
				url: 'routeHistory',
				hierarchy: {
					keyDataField: {name: 'party_id'},
					parentDataField: 'levelId'
				},
				pageable: true,
				pagerMode: 'advanced',
				theme: 'olbius',
				width: '100%',
				title: '${StringUtil.wrapString(uiLabelMap.BSRouteHistory)}',
				columnsHeight: 50,
				showStatusbar: false,
				popup: [
					<#if !Static["com.olbius.basesales.util.SalesPartyUtil"].isSalesman(delegator, userLogin.getString("partyId"))>
					{
					    action: 'jqxGridMultipleUrl',
					    params: {
					    	id : 'parties',
					        label : '${StringUtil.wrapString(uiLabelMap.Employee)}',
					        grid: {
					        	url: "JQGetListSalesexecutive",
					        	datafields:	[
									{name: 'partyId', type: 'string'},
									{name: 'partyCode', type: 'string'},
									{name: 'fullName', type: 'string'}
								],
					        	id: "partyId",
					        	width: 550,
					        	sortable: true,
					            columnsresize: true,
					            pageable: true,
					            altrows: true,
					            showfilterrow: true,
					            filterable: true,
					        	columns: [
						          	{ text: "${StringUtil.wrapString(uiLabelMap.EmployeeId)}", datafield: 'partyCode', width: 150 },
						          	{ text: "${StringUtil.wrapString(uiLabelMap.EmployeeName)}", datafield: 'fullName' }
						        ]
					        }
					    }
					},</#if>
					{
					    group: 'dateTime',
					    id: 'dateTime'
					}
				],
				apply: function (grid, popup) {
					return $.extend({
						parties: popup.val("parties")
	                }, popup.group('dateTime').val());
				},
			});
	    });
	</script>
</div>