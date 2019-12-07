<#assign localeStr = "VI" />

<#if locale = "en">
    <#assign localeStr = "EN" />
<#elseif locale= "en_US">
	<#assign localeStr = "EN" />
</#if>
<script>
	var locale = '${locale}';
	$(document).ready(function () {
		locale == "vi_VN"?locale="vi":locale=locale;
	});
</script>
<div id="detailItems">
	<#assign dataFieldVehicle="[
					{ name: 'employeePartyId', type: 'string'},
					{ name: 'employeePartyCode', type: 'string'},
					{ name: 'fullName', type: 'string' },
					{ name: 'fromDate', type: 'date', other: 'Timestamp'},
					{ name: 'thruDate', type: 'date', other: 'Timestamp'},
				]"/>
	<#assign columnlistVehicle="  
					{  
					    text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.BLEmployeeId}', datafield: 'employeePartyCode', align: 'left', width: 180, pinned: true},
					{ text: '${uiLabelMap.BLEmployeeName}', datafield: 'fullName', align: 'left'},
					{ text: '${uiLabelMap.BLEffectiveDate}', dataField: 'fromDate', cellsformat: 'dd/MM/yyyy', filtertype:'range', width: 180,
	                },
	                { text: '${uiLabelMap.BLExpiryDate}', dataField: 'thruDate', cellsformat: 'dd/MM/yyyy', filtertype:'range', width: 180,
	                },
				"/> 
	
	<@jqGrid filtersimplemode="true" filterable="true" dataField=dataFieldVehicle columnlist=columnlistVehicle editable="false" showtoolbar="true"
		url="jqxGeneralServicer?sname=JQGetListShiper" customTitleProperties="BLListShiper" id="jqxgridShiper" mouseRightMenu="true" contextMenuId="menuShipper"
	/> 
</div>
<div id='menuShipper' style="display:none;">
	<ul>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>
<script>
	$(document).ready(function (){
		$("#menuShipper").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
	});
	$("#menuShipper").on('itemclick', function (event) {
		var tmpStr = $.trim($(args).text());
		if(tmpStr == "${StringUtil.wrapString(uiLabelMap.BSRefresh)}"){
			$('#jqxgridShiper').jqxGrid('updatebounddata');
		}
	});
</script>