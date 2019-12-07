<#include "script/listVarianceReasonsScript.ftl"/>
<div id="container" class="container-noti"></div>
<div id="jqxNotification" style="margin-bottom:5px">
	<div id="notificationContent"></div>
</div>

<#assign dataField="[
				{ name: 'varianceReasonId', type: 'string'},
				{ name: 'description', type: 'string' },
				{ name: 'negativeNumber', type: 'string' },
				]"/>
<#assign columnlist="
				{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (value + 1) + '</div>';
				    }
				},
				{ text: '${uiLabelMap.ReasonId}', datafield: 'varianceReasonId', align: 'left', width: 250, pinned: true,
					cellsrenderer: function(row, column, value){
						 return '<span>' + value  + '</span>';
					}
				},
				{ text: '${uiLabelMap.Description}', datafield: 'description', align: 'left', minwidth: 150},
				{ text: '${uiLabelMap.MakeIncreaseDecrease}', datafield: 'negativeNumber', align: 'left', width: 200, filtertype: 'checkedlist',
					cellsrenderer: function(row, column, value){
						 if (value){
						 	if ('Y' == value) {
						 		return '<span>${uiLabelMap.Decrease}</span>';
						 	} else if('N' == value) {
						 		return '<span>${uiLabelMap.Increase}</span>';
						 	}
						 }
						 return value;
					},
					createfilterwidget: function (column, columnElement, widget) {
						var filterDataAdapter = new $.jqx.dataAdapter(yesNoData, {
							autoBind: true
						});
						var records = filterDataAdapter.records;
						widget.jqxDropDownList({source: records, displayMember: 'typeId', valueMember: 'typeId',
							renderer: function(index, label, value){
					        	if (yesNoData.length > 0) {
									for(var i = 0; i < yesNoData.length; i++){
										if(yesNoData[i].typeId == value){
											return '<span>' + yesNoData[i].description + '</span>';
										}
									}
								}
								return value;
							}
						});
						widget.jqxDropDownList('checkAll');
		   			}
				},
			"/>
<#if hasOlbPermission("MODULE", "LOG_CONFIG_VARIANCE_REASON", "CREATE")>
	<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true"
		url="jqxGeneralServicer?sname=jqGetVarianceReasons" customTitleProperties="ListVarianceReasons" id="jqxgridVarianceReason"
		customcontrol1="icon-plus open-sans@${uiLabelMap.AddNew}@javascript:VarReasonObj.openPopupVarianceReason();" mouseRightMenu="true" contextMenuId="varReasonMenu"
	/>
<#else>
	<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true" mouseRightMenu="true" contextMenuId="varReasonMenu"
		url="jqxGeneralServicer?sname=jqGetVarianceReasons" customTitleProperties="ListVarianceReasons" id="jqxgridVarianceReason"
	/>			
</#if>
<div id='varReasonMenu' style="display:none;">
	<ul>
		<#if hasOlbPermission("MODULE", "LOG_CONFIG_VARIANCE_REASON", "CREATE")>
		<li><i class="fa fa-trash red"></i>${StringUtil.wrapString(uiLabelMap.CommonDelete)}</li>
		<li><i class="fa fa-pencil-square-o"></i>${StringUtil.wrapString(uiLabelMap.Edit)}</li>
		</#if>
		<li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>

<div id="alterpopupWindow" class="hide popup-bound">
	<div class="row-fluid">
		${uiLabelMap.LogAddReturnsReason}
	</div>
	<div class='form-window-container'>
		<div class="row-fluid margin-top10">
			<div class="row-fluid margin-bottom10">	
				<div class="span4" style="text-align: right">
					<label class="asterisk"> ${uiLabelMap.ReasonId} </label>
				</div>
				<div class="span8">
					<input id="varianceReasonId" style="width: 100%">
					</input>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">	
				<div class="span4" style="text-align: right">
					<label class="asterisk"> ${uiLabelMap.MakeIncreaseDecrease} </label>
				</div>
				<div class="span8">
					<div id="negativeNumber" style="width: 100%">
					</div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">	
				<div class="span4" style="text-align: right">
					<label class="asterisk"> ${uiLabelMap.Description} </label>
				</div>
				<div class="span8">
					<textarea id="description" data-maxlength="250" rows="2" style="resize: vertical;margin-top:0px" class="span12"></textarea>
				</div>
			</div>
		</div>
		<div class="form-action popup-footer">
			<button id="addButtonCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="addButtonSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>	