<#assign dataField="[{ name: 'repPartyId', type: 'string' },
					{ name: 'repLastName', type: 'string' },
					{ name: 'repMiddleName', type: 'string' },
					{ name: 'repFirstName', type: 'string' },
					{ name: 'partyIdFrom', type: 'string' },
					{ name: 'partyCode', type: 'string' },
					{ name: 'partyIdTo', type: 'string' },
					{ name: 'repGender', type: 'string' },
					{ name: 'repBirthDate', type: 'date' },
					{ name: 'contactNumber', type: 'string' },
					{ name: 'address', type: 'string' },
					{ name: 'fromDate', type: 'date' }]"/>
<#assign columnlist="{text: '${StringUtil.wrapString(uiLabelMap.DmsSequenceId)}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
						cellsrenderer: function (row, column, value) {
							return '<div style=margin:4px;>' + (row + 1) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DACustomerId)}', datafield: 'partyCode', width: 150 },
					{ text: '${StringUtil.wrapString(uiLabelMap.FullName)}', datafield: 'repFirstName', minwidth: 200,
						cellsrenderer: function(row, column, label, a, b, data){
							var first = data.repFirstName ? data.repFirstName : '';
							var middle = data.repMiddleName ? data.repMiddleName : '';
							var last = data.repLastName ? data.repLastName : '';
							var full = ((last + ' ').concat(middle) + ' ').concat(first);
							if(!full){
								full = label;
							}
							var str = '<div class=\"cell-grid-custom\"><a target=\"_blank\" href=\"Callcenter?partyId='+data.partyIdFrom+ '&familyId=' + data.partyIdTo + '\">'+full+'</a></div>';
							return str;
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsTelecom)}', datafield: 'contactNumber', width: 200, sortable: false },
					{ text: '${StringUtil.wrapString(uiLabelMap.Address)}', datafield: 'address', minwidth: 200, sortable: false },
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsPartyGender)}', datafield: 'repGender', width: 150, filtertype: 'checkedlist', sortable: false,
						cellsrenderer: function (row, column, value) {
							value?value=mapGender[value]:value;
							return '<div style=margin:4px;>' + value + '</div>';
						}, createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: listGender, displayMember: 'label', valueMember: 'value' });
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsPartyBirthDate)}', datafield: 'repBirthDate', cellsformat: 'dd/MM/yyyy', width: 200, filtertype: 'range',
						cellsrenderer: function (row, column, value) {
							value?value=new Date(value).toTimeOlbius() + getPersonAge(value):value;
							return '<div style=margin:4px;>' + value + '</div>';
						}
					}"/>

<@jqGrid id="ListResource" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="false" url="" mouseRightMenu="true" customLoadFunction="true" mouseRightMenu="true"
		contextMenuId="ContactContext" selectionmode="checkbox" allGridMenu="true"/>

<div id="ContactContext" style="display:none;">
	<ul>
	</ul>
</div>

<div id="jqxwindowAssignContact" style="display:none;">
	<div>${uiLabelMap.AssignContact}</div>
	<div>
		<div class="row-fluid" style="overflow-x: auto;height: 208px;">
			<div class="span12" id="divAssignContent">
				
			</div>
		</div>
		<div class="pull-right"><span>${uiLabelMap.Total}: </span><span class="green" id="spTotal"></span><span> ${uiLabelMap.DmsCustomer}.</span></div>
		<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;">
		<div class="row-fluid">
			<div class="span12 margin-top10">
				<button id="cancelAssignContact" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
				<button id="saveAssignContact" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>