<#assign dataField="[{name: 'marketingRoleId', type: 'string'},
					{name: 'description', type: 'string'},
		            {name: 'partyId', type: 'string'},
		            {name: 'firstName', type: 'string'},
		            {name: 'middleName', type: 'string'},
		            {name: 'lastName', type: 'string'},
		            {name: 'roleTypeId', type: 'string'}]"/>
<#assign columnlist = "{text: '${uiLabelMap.FormTitle_fullName}',datafield: 'partyId', cellsalign: 'left', columntype: 'dropdownlist', width: 150,
							cellsrenderer: function(row, colum, value){
								value?value=mapEmployee[value]:value;
								return '<span>' + value + '</span>';
							}, createeditor: function(row, column, editor){
								editor.jqxDropDownList({ source: listEmployee, displayMember: 'partyId', valueMember: 'partyId', placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}',
						            renderer: function (index, label, value) {
						                var datarecord = listEmployee[index];
						                return datarecord.partyFullName;
						            },selectionRenderer: function () {
						                var item = editor.jqxDropDownList('getSelectedItem');
						                if (item) {
											return '<span title=' + item.value +'>' + mapEmployee[item.value] + '</span>';
						                }
						                return '<span>${StringUtil.wrapString(uiLabelMap.filterchoosestring)}</span>';
						            }
						        });
							}
					},
					{text: '${uiLabelMap.FormTitle_fullName}',datafield: 'fullName', cellsalign: 'left', width: 150,hidden: true,
						cellsrenderer: function(row, column, value, a, b, data){
							var first = data.firstName ? data.firstName : '';
							var middle = data.middleName ? data.middleName : '';
							var last = data.lastName ? data.lastName : '';
							var full = first + ' ' + middle + ' ' + last;
							return '<div class=\"cell-grid-custom\">'+full+'</div>';
						}
					},
					{text: '${uiLabelMap.FormTitle_roleTypeId}', datafield: 'roleTypeId', columntype: 'dropdownlist', width: '150',
						cellsrenderer: function(row, colum, value){
							for ( var x in roleType) {
								if (value == roleType[x].roleTypeId) {
									value = roleType[x].description;
								}
							}
							return '<span>' + value + '</span>';
						}, createeditor: function(row, column, editor){
							editor.jqxDropDownList({ autoDropDownHeight: true, source: roleType, displayMember: 'roleTypeId', valueMember: 'roleTypeId', placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}',
					            renderer: function (index, label, value) {
					                var datarecord = roleType[index];
					                return datarecord.description;
					            },selectionRenderer: function () {
					                var item = editor.jqxDropDownList('getSelectedItem');
					                if (item) {
								for ( var x in roleType) {
											if (item.value == roleType[x].roleTypeId) {
												item.label = roleType[x].description;
											}
										}
											return '<span title=' + item.value +'>' + item.label + '</span>';
					                }
					                return '<span>${StringUtil.wrapString(uiLabelMap.filterchoosestring)}</span>';
					            }
					        });
						}
					},
					{text: '${uiLabelMap.description}',datafield: 'description', cellsalign: 'left'},
					{text: '${uiLabelMap.CommonPlace}', datafield: 'marketingPlaceId', width: '150', hidden: true}"
					/>

<@jqGrid url="" id="MarketingRole" customLoadFunction="true" jqGridMinimumLibEnable="false" selectionmode="checkbox" showlist="false"
				dataField=dataField columnlist=columnlist virtualmode="false" pageable="false" editable="true"
				autoshowloadelement="false" showdefaultloadelement="false" addrow="true"
				showtoolbar="true" autorowheight="true" deleterow="true"  filterable="false" editmode="click"
				isShowTitleProperty="false" addType="popup" alternativeAddPopup="popupAddRole" sortable="false"
				customcontrol1="fa fa-bolt@${uiLabelMap.QuickAdd}@javascript: void(0);@MKRole.quickAddRole()"/>
