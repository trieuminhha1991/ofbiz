<div class='row-fluid'>
	<div class='span7'>
		<#assign customLoadFunction="true"/>
		<#assign selectionmode="multiplerows"/>
		<#include "listContactAllGroup.ftl"/>
	</div>
	<div class='span5'>
		<#assign dataField="[{name: 'groupName', type: 'string'},
		            {name: 'organizationId', type: 'string'},
		            {name: 'address', type: 'string'},
		            {name: 'contactNumber', type: 'string'},
		            {name: 'geoId', type: 'string'},
		            {name: 'contactMechId', type: 'string'},
		            {name: 'fromDate', type: 'date'},
		            {name: 'thruDate', type: 'date'},]"/>


		<#assign columnlist = "{text: '${uiLabelMap.PlaceName}', datafield: 'name', width: 200,editable: false,
			cellsrenderer: function(row, column, value, a, b, data){
				var groupName = data.groupName ? data.groupName : '';
				var child = '<p><b>'+groupName+'</b></p>';
				/*var address = data.address ? data.address : '';
				var contactNumber = data.contactNumber ? data.contactNumber : '';
				if(address){
					child += '<p><i class=\"fa fa-map-marker\"></i>&nbsp;'+address+'</p>';
				}
				if(contactNumber){
					child += '<p><i class=\"fa fa-phone\"></i>&nbsp;'+contactNumber+'</p>';
				}*/
				var par = '<div class=\"custom-cell-grid\">'+child+'</div>';
				return par;
			}
		},
		{ text: '${uiLabelMap.fromDate}', datafield: 'fromDate', width: 100, cellsformat:'dd/MM/yyyy', columntype: 'datetimeinput',
			initeditor : function(row, cellvalue, editor, celltext, cellwidth, cellheight){
				var date;
				if(cellvalue){
					var tmp = cellvalue.split('/');
					date = new Date(tmp[2] + '-' + tmp[1] + '-' + tmp[0]);
				}
				editor.jqxDateTimeInput({ formatString: 'dd-MM-yyyy', allowNullDate: true, dropDownHorizontalAlignment: 'right', value: date});
			},
			validation : function(cell,value){
				 var data = $(\"#MarketingPlace\").jqxGrid('getrowdata',cell.row);
				 if(data.thruDate <= value){
					return {message : \"${StringUtil.wrapString(uiLabelMap.NotifromDateBiggerthruDate)}\",result : false};
				 }
				return true;
			}
		},
		{ text: '${uiLabelMap.thruDate}', datafield: 'thruDate', width: 100, cellsformat:'dd/MM/yyyy',
			columntype: 'datetimeinput',
			initeditor : function(row, cellvalue, editor, celltext, cellwidth, cellheight){
				var date;
				if(cellvalue){
					var tmp = cellvalue.split('/');
					date = new Date(tmp[2] + '-' + tmp[1] + '-' + tmp[0]);
				}
				editor.jqxDateTimeInput({ formatString: 'dd-MM-yyyy', allowNullDate: true,
											dropDownHorizontalAlignment: 'right', value: date});
			},
			validation : function(cell,value) {
				var data = $(\"#MarketingPlace\").jqxGrid('getrowdata',cell.row);
				if(value <= data.fromDate){
					return {message : \"${StringUtil.wrapString(uiLabelMap.NotithruDateSmallerfromDate)}\",result : false};
				}
				return true;
			}
		}," />

		<@jqGrid url="" id="MarketingPlace" customLoadFunction="true" jqGridMinimumLibEnable="false" selectionmode="checkbox" showlist="false"
				dataField=dataField columnlist=columnlist virtualmode="false" pageable="false" editable="true"
				width="100%" autoshowloadelement="false" showdefaultloadelement="false"
				showtoolbar="true" autorowheight="true" deleterow="true"  filterable="false" editmode="click" sourceId="organizationId"
				isShowTitleProperty="false" addType="popup" alternativeAddPopup="popupAddProduct" sortable="false"/>
	</div>
</div>
