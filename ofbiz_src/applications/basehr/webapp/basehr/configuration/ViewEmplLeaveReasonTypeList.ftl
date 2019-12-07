<#include "script/ViewEmplLeaveReasonTypeListScript.ftl"/>
<#assign datafield = "[{name: 'emplLeaveReasonTypeId', type: 'string'},
						{name: 'sign', type: 'string'},
						{name: 'emplTimekeepingSignId', type: 'string'},
						{name: 'isBenefitSocialIns', type: 'bool'},
						{name: 'isBenefitSal', type: 'bool'},
						{name: 'rateBenefit', type: 'number'},
						{name: 'description', type: 'string'},
					   ]
"/>
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.HRCommonReasonId)}', datafield: 'emplLeaveReasonTypeId', width: '13%', editable: false,
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
								return \"<span><a href='javascript:void(0)' onclick='viewEmplLeaveReasonTypeObject.executeEdit(\" + row + \")'>\" + value + \"</a></span>\";
							} 
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonDescription)}', datafield: 'description', width: '20%', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.HRCommonEmplTimekeepingSign)}', datafield: 'sign', width: '12%', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.SocialInsuranceBenefits)}', datafield: 'isBenefitSocialIns', width: '14%', columntype: 'checkbox', editable: false,filterType : 'checkedlist',
							createfilterwidget : function(column, columnElement, widget){
								var source = {
										localdata : datalocal,
										datatype : 'array'
								};
								var fitlerBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
								var dataFilter = fitlerBoxAdapter.records;
								//dataFilter.splice(0,0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								widget.jqxDropDownList({source: dataFilter, valueMember: 'value', displayMember : 'description'});
								if(dataFilter.length <=8){
									widget.jqxDropDownList({autoDropDownHeight : true});
								}else{
									widget.jqxDropDownList({autoDropDownHeight : false})
								}
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.OrganizationSalaryBenefit)}', datafield: 'isBenefitSal', width: '12%', columntype: 'checkbox', editable: false,filterType : 'checkedlist',
								createfilterwidget : function(column, columnElement, widget){
									var source = {
											localdata : datalocal,
											datatype : 'array'
									};
									var fitlerBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
									var dataFilter = fitlerBoxAdapter.records;
									//dataFilter.splice(0,0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
									widget.jqxDropDownList({source: dataFilter, valueMember: 'value', displayMember : 'description'});
									if(dataFilter.length <=8){
										widget.jqxDropDownList({autoDropDownHeight : true});
									}else{
										widget.jqxDropDownList({autoDropDownHeight : false})
									}
								}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.HRPayRate)}', datafield: 'rateBenefit', editable: false,filterType : 'number',
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
								if(value){
									var renderValue = value * 100;
									return '<span>' + renderValue + '%</span>';
								}
							}
						}
						"/>
</script>
<#if hasOlbPermission("MODULE", "HR_CONFIG", "CREATE")>
    <#assign addrow="true"/>
<#else>
    <#assign addrow="false"/>
</#if>

<#if hasOlbPermission("MODULE", "HR_CONFIG", "UPDATE")>
    <#assign editable="true"/>
    <div id='contextMenu' class="hide">
        <ul>
            <li><i class="fa fa-pencil-square-o"></i>${StringUtil.wrapString(uiLabelMap.Edit)}</li>
        </ul>
    </div>
<#else>
    <#assign editable="false"/>
</#if>

<@jqGrid url="jqxGeneralServicer?sname=JQgetListEmplLeaveReasonType&hasrequest=Y" dataField=datafield columnlist=columnlist
	clearfilteringbutton="true" id="jqxgrid" addrow=addrow filterable="true" sortable="false" filtersimplemode="true"
	editable=editable width="100%" filterable="true" showlist="true" id="jqxgrid"
	addType="popup" alternativeAddPopup="alterpopupWindow"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updateEmplLeaveReasonTypeBase" 
	editColumns="emplLeaveReasonTypeId;description;isBenefitSocialIns;isBenefitSal;rateBenefit(java.lang.Double);emplTimekeepingSignId"
	addColumns="description;isBenefitSocialIns;isBenefitSal;rateBenefit(java.lang.Double);emplTimekeepingSignId"
	createUrl="jqxGeneralServicer?jqaction=C&sname=createEmplLeaveReasonTypeBase"
	showtoolbar="true" deleterow="false" jqGridMinimumLibEnable="false"
	 mouseRightMenu=editable contextMenuId="contextMenu"
/>	

<div id="alterpopupWindow" class="hide">
	<div>${uiLabelMap.CommonAddNew}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${uiLabelMap.CommonDescription}</label>
				</div>
				<div class="span7">
					<input type="text" id="descriptionAddNew">
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="asterisk">${uiLabelMap.HRCommonEmplTimekeepingSign}</label>
					</div>
					<div class="span7">
						<div id="emplTimekeepingSignIdAddNew"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="">${uiLabelMap.SocialInsuranceBenefits}</label>
					</div>
					<div class="span7">
						<div style="margin-left: 16px; margin-top: 4px">
							<div id="socialInsuranceBenefits"></div>
						</div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="">${uiLabelMap.OrganizationSalaryBenefit}</label>
					</div>
					<div class="span7">
						<div style="margin-left: 16px; margin-top: 4px">
							<div id="benefitSal"></div>
						</div>	
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="">${uiLabelMap.HRPayRate}</label>
					</div>
					<div class="span7">
						<div id="rateBenefit"></div>
					</div>
				</div>
			</div>
			<div class="form-action">
				<button id="cancelCreate" class='btn btn-danger form-action-button pull-right'>
					<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
				<button type="button" class='btn btn-primary form-action-button pull-right' id="saveCreate">
					<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>	
</div>
<div id="editEmplLeaveReasonWindow" class="hide">
	<div>${uiLabelMap.HrCommonEdit}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${uiLabelMap.CommonDescription}</label>
				</div>
				<div class="span7">
					<input type="text" id="descriptionUpdate">
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="asterisk">${uiLabelMap.HRCommonEmplTimekeepingSign}</label>
					</div>
					<div class="span7">
						<div id="emplTimekeepingSignIdUpdate"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="">${uiLabelMap.SocialInsuranceBenefits}</label>
					</div>
					<div class="span7">
						<div style="margin-left: 16px; margin-top: 4px">
							<div id="socialInsuranceBenefitsUpdate"></div>
						</div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="">${uiLabelMap.OrganizationSalaryBenefit}</label>
					</div>
					<div class="span7">
						<div style="margin-left: 16px; margin-top: 4px">
							<div id="benefitSalUpdate"></div>
						</div>	
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="">${uiLabelMap.HRPayRate}</label>
					</div>
					<div class="span7">
						<div id="rateBenefitUpdate"></div>
					</div>
				</div>
			</div>
			<div class="form-action">
				<button id="cancelUpdate" class='btn btn-danger form-action-button pull-right'>
					<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
				<button type="button" class='btn btn-primary form-action-button pull-right' id="saveUpdate">
					<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/hrresources/js/configuration/AddEmplLeaveReasonType.js"></script>
<script type="text/javascript" src="/hrresources/js/configuration/ViewEmplLeaveReasonTypeList.js"></script>