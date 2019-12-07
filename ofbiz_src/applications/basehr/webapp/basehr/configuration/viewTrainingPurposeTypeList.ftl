<#include "script/viewTrainingTypeListScript.ftl"/>
<div id="container" class="container-noti"></div>
<div id="jqxNotification" style="margin-bottom:5px">
	<div id="notificationContent"></div>
</div>

<#assign dataField="[
				{ name: 'trainingPurposeTypeId', type: 'string'},
				{ name: 'parentTypeId', type: 'string'},
				{ name: 'description', type: 'string' },
				]"/>
<#assign columnlist="
				{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (value + 1) + '</div>';
				    }
				},
				{ text: '${uiLabelMap.TrainingPurposeTypeId}', datafield: 'trainingPurposeTypeId', align: 'left', width: 250, pinned: true,
				},
				{ text: '${uiLabelMap.Description}', datafield: 'description', align: 'left', minwidth: 150},
				{ text: '${uiLabelMap.ParentPurpose}', datafield: 'parentTypeId', align: 'center', minwidth: 150,
					cellsrenderer: function (row, colum, value){
						for (var i = 0; i < trainingPurposeTypeData.length; i ++){
							if (trainingPurposeTypeData[i].trainingPurposeTypeId == value){
								return '<span>' + trainingPurposeTypeData[i].description + '</span>';
							}
						}
						return '<span></span>';
				 	}
				},
			"/>
<#if hasOlbPermission("MODULE", "HR_CONFIG", "CREATE")>
    <#assign customcontrol1="icon-plus open-sans@${uiLabelMap.AddNew}@javascript:TrainingPurposeTypeObj.openPopupTrainingPurposeType();"/>
</#if>
<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true"
	url="jqxGeneralServicer?sname=JQListTrainingPurposeType" customTitleProperties="TrainingPurposeTypeList" id="jqxgridTrainingPurposeType"
	customcontrol1=customcontrol1 mouseRightMenu="true" contextMenuId="trainingPurposeTypeMenu"
/>
<div id='trainingPurposeTypeMenu' style="display:none;">
	<ul>
        <#if hasOlbPermission("MODULE", "HR_CONFIG", "DELETE")>
            <li><i class="fa fa-trash red"></i>${StringUtil.wrapString(uiLabelMap.CommonDelete)}</li>
        </#if>
        <#if hasOlbPermission("MODULE", "HR_CONFIG", "UPDATE")>
            <li><i class="fa fa-pencil-square-o"></i>${StringUtil.wrapString(uiLabelMap.Edit)}</li>
        </#if>
		<li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>

<div id="alterpopupWindow" class="hide popup-bound">
	<div class="row-fluid">
	</div>
	<div class='form-window-container'>
		<div class="row-fluid margin-top10">
			<div class="row-fluid margin-bottom10">	
				<div class="span4" style="text-align: right">
					<label class="asterisk"> ${uiLabelMap.TrainingPurposeTypeId} </label>
				</div>
				<div class="span8">
					<input id="trainingPurposeTypeId" style="width: 100%">
					</input>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">	
				<div class="span4" style="text-align: right">
					<label> ${uiLabelMap.ParentPurpose} </label>
				</div>
				<div class="span8">
					<div id="parentTypeId" style="width: 100%">
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