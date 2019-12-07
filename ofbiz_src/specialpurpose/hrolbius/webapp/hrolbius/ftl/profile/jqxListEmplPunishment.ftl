<script>
	//Prepare for punishment type data
	<#assign listPunishmentTypes = delegator.findList("PunishmentType", null, null, null, null, false) />
	var punTypeData = new Array();
	<#list listPunishmentTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['punishmentTypeId'] = '${item.punishmentTypeId}';
		row['description'] = '${description}';
		punTypeData[${item_index}] = row;
	</#list>

	//Prepare for party data
	<#assign listParties = delegator.findList("PartyNameView", null, null, null, null, false) />
	var partyData = new Array();
	<#list listParties as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.groupName?if_exists) + " " + StringUtil.wrapString(item.firstName?if_exists) + " " + StringUtil.wrapString(item.middleName?if_exists) + " " + StringUtil.wrapString(item.lastName?if_exists)>
		row['partyId'] = '${item.partyId}';
		row['description'] = '${description}';
		partyData[${item_index}] = row;
	</#list>
</script>

<#assign dataField="[{ name: 'punishmentTypeId', type: 'string' },
					 { name: 'datePunishment', type: 'date', other: 'Timestamp' },
					 { name: 'punishmentCount', type: 'number' },
					 { name: 'punishmentLevel', type: 'string'},
					 { name: 'partyPunishingId', type: 'string'}
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.HREmplPunishmentType}', datafield: 'punishmentTypeId', width: 200,filtertype: 'checkedlist',
						cellsrenderer: function(column, row, value){
							for(var i = 0; i < punTypeData.length; i++){
								if(value == punTypeData[i].punishmentTypeId){
									return '<span title =' + value + '>' + punTypeData[i].description + '</span>';
								}
							}
							return value;
						},
						createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(punTypeData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							widget.jqxDropDownList({source: records, displayMember: 'punishmentTypeId', valueMember: 'punishmentTypeId',
								renderer: function(index, label, value){
									for(var i = 0; i < punTypeData.length; i++){
										if(punTypeData[i].punishmentTypeId == value){
											return '<span>' + punTypeData[i].description + '</span>';
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
						}
					 },
                     { text: '${uiLabelMap.HREmplDatePunishment}', datafield: 'datePunishment', width: 200, cellsformat: 'd', filtertype:'range'},
                     { text: '${uiLabelMap.HREmplWarningLevel}', datafield: 'punishmentCount', width: 200},
                     { text: '${uiLabelMap.HREmplPunishmentLevel}', datafield: 'punishmentLevel', width: 200},
                     { text: '${uiLabelMap.partyPunishingId}', datafield: 'partyPunishingId',
			cellsrenderer: function(column, row, value){
				for(var i = 0; i < partyData.length; i++){
					if(value == partyData[i].partyId){
						return '<span title=' + value + '><a href=/hrolbius/control/EmployeeProfile?partyId=' + value + '>' + partyData[i].description + '[' + value + ']' + '</a></span>';
					}
				}
			}
                     }
					 "/>

<@jqGrid addrow="false" addType="popup" id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListEmplPunishment&partyId=${parameters.partyId}" dataField=dataField columnlist=columnlist
		 />