<#include "script/viewSkillTypeListScript.ftl"/>
<div id="container" class="container-noti"></div>
<div id="jqxNotification" style="margin-bottom:5px">
	<div id="notificationContent"></div>
</div>

<#assign dataField="[
				{ name: 'skillTypeId', type: 'string'},
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
				{ text: '${uiLabelMap.SkillTypeId}', datafield: 'skillTypeId', align: 'left', width: 250, pinned: true,
				},
				{ text: '${uiLabelMap.ParentSkillType}', datafield: 'parentTypeId', align: 'left', width: 250, pinned: true,
					cellsrenderer: function (row, colum, value){
						for (var i = 0; i < parentSkillTypeData.length; i ++){
							if (parentSkillTypeData[i].skillTypeId == value){
								return '<div style=\"text-align: left\" title=\"' +value+ '\">' + parentSkillTypeData[i].description + '</div>';
							}
						}
						return '<div></div>';
				 	}
				},
				{ text: '${uiLabelMap.Description}', datafield: 'description', align: 'left', minwidth: 150},
			"/>
<#if hasOlbPermission("MODULE", "HR_CONFIG", "CREATE")>
    <#assign customcontrol1 = "icon-plus open-sans@${uiLabelMap.AddNew}@javascript:SkillTypeObj.openPopupSkillType();"/>
</#if>
<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true"
	url="jqxGeneralServicer?sname=JQListSkillType" customTitleProperties="TrainingCourseSkillTypeList" id="jqxgridSkillType"
	customcontrol1=customcontrol1 mouseRightMenu="true" contextMenuId="skillTypeMenu"
/>
<div id='skillTypeMenu' class="hide">
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
		${uiLabelMap.AddTrainingCourseSkillType}
	</div>
	<div class='form-window-container'>
		<div class="row-fluid margin-top10">
			<div class="row-fluid margin-bottom10">	
				<div class="span4" style="text-align: right">
					<label class="asterisk"> ${uiLabelMap.SkillTypeId} </label>
				</div>
				<div class="span8">
					<input id="skillTypeId" style="width: 100%">
					</input>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">	
				<div class="span4" style="text-align: right">
					<label class="asterisk"> ${uiLabelMap.ParentSkillTypeId} </label>
				</div>
				<div class="span8">
					<div class="row-fluid">
						<div class="span12">
							<div class="span10">
								<div id="parentTypeId" style="width:100%"></div>
							</div>
							<div class="span2">
								<button id="addButtonParentTypeId" class='btn btn-primary form-action-button' style="width: 31px; height: 27px"><i class='fa-plus'></i></button>
							</div>
						</div>
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

<div id="addParentTypeIdWindow" class="hide popup-bound">
	<div class="row-fluid">
		${uiLabelMap.AddTrainingCourseSkillType}
	</div>
	<div class='form-window-container'>
		<div class="row-fluid margin-top10">
			<div class="row-fluid margin-bottom10">	
				<div class="span4" style="text-align: right">
					<label class="asterisk"> ${uiLabelMap.SkillTypeId} </label>
				</div>
				<div class="span8">
					<input id="parentSkillTypeId" style="width: 100%">
					</input>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">	
				<div class="span4" style="text-align: right">
					<label class="asterisk"> ${uiLabelMap.Description} </label>
				</div>
				<div class="span8">
					<input id="parentDescription" style="width: 100%">
					</input>
				</div>
			</div>
		</div>
		<div class="form-action popup-footer">
			<button id="buttonCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="buttonSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>	