<script>
	var listRequirementData = [];
	<#assign requirementTypes = delegator.findList("RequirementType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].NOT_EQUAL, null), null, null, null, false) />
	var requirementTypeData = new Array();
	<#list requirementTypes as item>
		var row = {};
		row['requirementTypeId'] = "${item.requirementTypeId}";
		row['description'] = "${StringUtil.wrapString(item.get('description', locale)?if_exists)}";
		requirementTypeData.push(row);
	</#list>
	
	<#assign requirementStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, "REQUIREMENT_STATUS"), null, null, null, false) />
	var requirementStatusData = new Array();
	<#list requirementStatus as item>
		var row = {};
		row['statusId'] = "${item.statusId}";
		row['description'] = "${StringUtil.wrapString(item.get('description', locale)?if_exists)}";
		requirementStatusData.push(row);
	</#list>

	<#assign requirements = delegator.findList("Requirement", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, Static["org.ofbiz.base.util.UtilMisc"].toList("REQ_CREATED", "REQ_CONFIRMED", "REQ_APPROVED", "REQ_PROPOSED")), null, null, null, false) />
	
</script>
<div>
	<div class="titleTextReturn">
		<i class="fa-bullhorn"></i> <lable>${uiLabelMap.Requirement}</lable>
	</div>
	<div class="valueTotal">
		<div class="form-window-container">
			<div class="row-fluid">
				<div class="span12">
					<#list requirementTypes as type>
						<#assign count = 0>
						<script>
							var requirementData = [];
							<#list requirements as req>
								<#if req.requirementTypeId == type.requirementTypeId>
									<#assign count = count + 1>
										var row = {};
										row["requirementId"] = "${req.requirementId}";
										row["requirementStartDate"] = "${req.requirementStartDate}";
										row["requiredByDate"] = "${req.requiredByDate}";
										row["reasonEnumId"] = "${req.reasonEnumId}";
										row["statusId"] = "${req.statusId}";
										<#assign enum = delegator.findOne("Enumeration", {"enumId" : req.reasonEnumId?if_exists}, false)/>
										<#assign desEnum = StringUtil.wrapString(enum.get("description", locale)?if_exists) />
										row["enum"] = "${desEnum?if_exists}";
										row["createdByUserLogin"] = "${req.createdByUserLogin}";
										<#assign createdBy = delegator.findOne("UserLogin", {"userLoginId" : req.createdByUserLogin?if_exists}, false)/>
										<#assign createdByParty = delegator.findOne("PartyNameView", {"partyId" : createdBy.partyId?if_exists}, false)/>
										row["createdBy"] = "${createdByParty.lastName?if_exists} ${createdByParty.middleName?if_exists} ${createdByParty.firstName?if_exists}";
										requirementData.push(row);
								</#if>
							</#list>
							var data = {
								type: "${type.requirementTypeId}",
								data: requirementData,
							}
							listRequirementData.push(data);
						</script>
						<div class="row-fluid">	
							<div class="span9" style="text-align: left; word-wrap: break-word;">
								<#assign desType = StringUtil.wrapString(type.get("description", locale)?if_exists) />
								<label style="cursor: auto;">${desType} ${uiLabelMap.uncompleted}</label>
							</div>
							<div class="span3" style="text-align: right; word-wrap: break-word;" >
								<a style="text-decoration: underline !important; color: #333;" href="javascript:void(0)" title="${uiLabelMap.ViewDetail}" onclick="showListRequirement('${type.requirementTypeId}')">
									<div class="counting">${count}</div>
								</a>
							</div>
						</div>
					</#list>
				</div>
			</div>
		</div>
	</div>
</div>


<div id="alterpopupWindowRequirement" class="hide popup-bound">
	<div class="row-fluid">
		${uiLabelMap.ListRequirements}
	</div>
	<div class="form-window-container">
		<div class="row-fluid">
			<div class="span12">
				<div id="jqxgridRequirement"></div>
			</div>
		</div>
		<div class="form-action popup-footer">
			<button id="btnCancelRequirement" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonClose}</button>
		</div>
	</div>
</div>

<script>
	$(document).ready(function() {
		$("#alterpopupWindowRequirement").jqxWindow($.extend( {cancelButton: $("#btnCancelRequirement")}, LocalData.config.jqxwindow ));
	});
	function showListRequirement(requirementTypeId) {
		var data = null;
		for (var i = 0; i < listRequirementData.length; i ++) {
			if (requirementTypeId == listRequirementData[i].type) {
				data = listRequirementData[i].data;
				break;
			}
		}
		if (data != null) {
			var descTmp = null;
			for (var i = 0; i < requirementTypeData.length; i ++) {
				if (data.requirementTypeId == requirementTypeData[i].requirementTypeId) {
					descTmp = listRequirementData[i].description;
					break;
				}
			}
			if (descTmp != null) {
				$("#alterpopupWindowRequirement").jqxWindow("setTitle", "${StringUtil.wrapString(uiLabelMap.List)} " + descTmp.toLowerCase());
			}
			bindingData(data);
		}
	}
	
	function bindingData(data){
		var source =
		{
			datafields:
			[
				{ name: "requirementId", type: "string" },
				{ name: "requiredByDate", type: "date" },
				{ name: "requirementStartDate", type: "date" },
				{ name: "reasonEnumId", type: "string" },
				{ name: "enum", type: "string" },
				{ name: "createdBy", type: "string" },
				{ name: "statusId", type: "string" }
			],
			localdata: data,
			datatype: "array"
		}; 
		
		var dataAdapter = new $.jqx.dataAdapter(source);
		$("#jqxgridRequirement").jqxGrid($.extend({
			source: dataAdapter,
			columns:
			[
				{ text: "${uiLabelMap.SequenceId}", sortable: false, filterable: false, editable: false, pinned: true,
					groupable: false, draggable: false, resizable: false,
					datafield: "", columntype: "number", width: 50,
					cellsrenderer: function (row, column, value) {
						return "<div style=margin:4px;>" + (value + 1) + "</div>";
					}
				},
				{ text: "${uiLabelMap.requirementId}", datafield: "requirementId", width: 120,
					cellsrenderer: function(row, column, value){
						return "<span><a href=\"viewRequirementDetail?requirementId="+value+"\" onclick=\"\" target=\"_blank\"> " + value  + "</a></span>";
					}
				},
				{ text: "${uiLabelMap.Status}", datafield: "statusId", width: 150, filtertype: "checkedlist",
					cellsrenderer: function(row, column, value){
						if (value){
							for (var i = 0; i < requirementStatusData.length; i ++){
								if (value == requirementStatusData[i].statusId){
									return "<span>" + requirementStatusData[i].description + "<span>";
								}
							}
						}
						return "<span>" + value + "<span>";
					},
					createfilterwidget: function (column, columnElement, widget) {
						var filterDataAdapter = new $.jqx.dataAdapter(requirementStatusData, {
							autoBind: true
						});
						var records = filterDataAdapter.records;
						widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
							renderer: function(index, label, value){
					        	if (requirementStatusData.length > 0) {
									for(var i = 0; i < requirementStatusData.length; i++){
										if(requirementStatusData[i].statusId == value){
											return '<span>' + requirementStatusData[i].description + '</span>';
										}
									}
								}
								return value;
							}
						});
						widget.jqxDropDownList('checkAll');
		   			}
					
				},
				{ text: "${uiLabelMap.RequiredByDate}", dataField: "requiredByDate", width: "200", cellsformat: "dd/MM/yyyy HH:mm:ss", filtertype:"range", cellsalign: "right" },
				{ text: "${uiLabelMap.RequirementStartDate}", dataField: "requirementStartDate", width: "200", cellsformat: "dd/MM/yyyy HH:mm:ss", filtertype:"range",  cellsalign: "right" },
				{ text: "${uiLabelMap.CreatedBy}", datafield: "createdBy", width: 200 }
			] 
		}, LocalData.config.jqxgrid));
		var wtmp = window;
		var tmpwidth = $("#alterpopupWindowRequirement").jqxWindow("width");
		$("#alterpopupWindowRequirement").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
		$("#alterpopupWindowRequirement").jqxWindow("open");
	}
</script>
