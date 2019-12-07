<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<#assign listEmplType = delegator.findList("EmplPositionType",null,null,null,null,false) !>
<#assign listEmplTypeCriteria = delegator.findList("EmplPositionTypeCriteria",null,null,null,null,false) !>
<script type="text/javascript">
	var ArrEmplType = [
			<#list listEmplType as type>
					{
						emplType : "${type.emplPositionTypeId?if_exists}",
						description : "${StringUtil.wrapString(type.description?if_exists)}"
					},
			</#list>
		];
	var emplPosCriteria = [
		<#list listEmplTypeCriteria as empl>
			{
				emplPositionTypeId : '${empl.emplPositionTypeId?default('')}',
				recruitmentCriteriaId : '${empl.recruitmentCriteriaId?default('')}'
			},
		</#list>
	];
</script>
<#assign dataField="[
			{name : 'recruitmentCriteriaId' ,type : 'string'},
			{name : 'description' , type : 'string'},
			{name : 'emplPositionTypeId', type  : 'string'}
]"/>

<#assign columnlist="
				{text : '${uiLabelMap.CommonId}',editable : false,width : '100px',dataField  : 'recruitmentCriteriaId'},
				{text : '${uiLabelMap.HRolbiusRecruitmentCriteriaDescription}',dataField  : 'description',width : '500px',filterable : false},
				{text : '${uiLabelMap.positionEmployee}',dataField : 'emplPositionTypeId',filtertype : 'checkedlist',columntype : 'dropdownlist',createeditor : 
					function(row,column,editor){
						editor.jqxDropDownList({source: ArrEmplType, displayMember:\"description\", selectedIndex: 0 ,valueMember: \"emplType\"});
					},cellsrenderer : function(row,columnfield,value){
						var data = $(\"#jqxgrid\").jqxGrid('getrowdata',row);
						for(var i = 0 ; i < ArrEmplType.length ; i ++){
							if(data.emplPositionTypeId == ArrEmplType[i].emplType) return '<span>'+ ArrEmplType[i].description +'</span>';
						}
					},createfilterwidget: function (column, columnElement, widget) {
							  		var sourceOrd =
								    {
								        localdata: ArrEmplType,
								        datatype: \"array\"
								    };
					   				var filterBoxAdapter = new $.jqx.dataAdapter(sourceOrd,
					                {
					                    autoBind: true
					                });
					                var uniqueRecords = filterBoxAdapter.records;
					   				uniqueRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
					   				
		        					widget.jqxDropDownList({selectedIndex: 0, source: uniqueRecords, displayMember: 'description', valueMember: 'emplType', dropDownWidth: 250,
		        						renderer: function (index, label, value) {
		                    				for(i = 0; i < ArrEmplType.length; i++){
												if(ArrEmplType[i].emplType == value){
													return '<span>' + ArrEmplType[i].description + '</span>';
												}
		                    				}
		                    			return value;
		        					}});
									widget.jqxDropDownList('checkAll');
    						}
				}
				"/>

<@jqGrid filtersimplemode="true"  deleterow="true" filterable="true" addrow="true"   addType="popup" addrefresh="true" alternativeAddPopup="alterpopupWindow" dataField=dataField columnlist=columnlist  clearfilteringbutton="true" 
		editable="true" 
		 url="jqxGeneralServicer?sname=JQgetListRescruitmentCriteria"
		 createUrl="jqxGeneralServicer?sname=JQCreateCriteriaAndEmplPositionType&jqaction=C" addColumns="recruitmentCriteriaId;description;ListEmplPos(java.util.List)"
		 removeUrl="jqxGeneralServicer?sname=JQDeleteCriteriaAndEmplPositionType&jqaction=D" deleteColumn="recruitmentCriteriaId;emplPositionTypeId"
		 updateUrl="jqxGeneralServicer?jqaction=U&sname=updateRecruitmentCriteria"  editColumns="recruitmentCriteriaId;description;emplPositionTypeId"
		/>		
		
<div class="row-fluid" id="alterpopupWindow" style="display:none;">
 	<div>${uiLabelMap.CommonAdd} ${uiLabelMap.HRolbiusRecruitmentCriteria}</div>
 	 <div style="overflow: hidden;">
 	 	<form id="formAdd" class="form-horizontal">
 	 		<div class="control-group no-left-margin">
				<label class="control-label asterisk">${uiLabelMap.CommonId}</label>
				<div class="controls">
					<input type="text" id="recruitmentCriteriaIdAdd"/>
				</div>
			</div>
 	 		<div class="control-group no-left-margin">
				<label class="control-label asterisk">${uiLabelMap.HRolbiusRecruitmentCriteriaDescription}</label>
				<div class="controls">
					<input type="text" id="descriptionAdd"/>
				</div>
			</div>
 	 		<div class="control-group no-left-margin">
				<label class="control-label asterisk">${uiLabelMap.positionEmployee}</label>
				<div class="controls">
					<div id="positionEmplAdd"></div>
				</div>
			</div>
 	 		<div class="control-group no-left-margin">
				<label class="control-label">&nbsp;</label>
				<div class="controls">
					<button type="button" class='btn btn-primary btn-mini' style="margin-right: 5px; margin-top: 10px;" id="alterSave"><i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
					<button type="button" class='btn btn-danger btn-mini' style="margin-right: 5px; margin-top: 10px;" id="alterCancel"><i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
				</div>
			</div>
 	 	</form>
 	 </div>
</div> 	 		
<script type="text/javascript">
	$.jqx.theme =  'olbius';
	theme = $.jqx.theme;
	
	var jqxRecruitmentGrid = $('#alterpopupWindow');
	jqxRecruitmentGrid.jqxWindow({ width: 600, height : 300,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7 });	
	
	$('#recruitmentCriteriaIdAdd').jqxInput({ width : '245px', height : '25px' });
	$('#descriptionAdd').jqxInput({ width : '245px' ,height : '25px' });
	//$("#positionEmplAdd").jqxDropDownList({source: ArrEmplType, width: 200 , displayMember:"description", selectedIndex: 0 ,valueMember: "emplType"});
	var source = new $.jqx.dataAdapter(ArrEmplType,{autoBind : true});
	$('#positionEmplAdd').jqxComboBox({source: source, multiSelect: true,displayMember : 'description',valueMember : 'emplType', width: '250px', height: '25px',theme: 'energyblue'});     
	$('#formAdd').jqxValidator({
			rules : [
				{input : '#recruitmentCriteriaIdAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
				{input : '#descriptionAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
				{input : '#positionEmplAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : function(){
					var listValue = $('#positionEmplAdd').jqxComboBox('getSelectedItems');
					if(listValue && listValue.length > 0){
						return true;
					}
					return false;
				}}
			]
	})
	$('#alterSave').click(function(){
		$('#formAdd').jqxValidator('validate');
	});
	$('#alterCancel').click(function(){
		$('#formAdd').trigger('reset');
		$("#positionEmplAdd").jqxComboBox('clearSelection'); 
	});
	$('#formAdd').on('validationSuccess',function(){
		var tmp = $('#positionEmplAdd').jqxComboBox('getSelectedItems');
		var arrEmplPos = Array();
		if(tmp && tmp.length > 0){
			for(var i =0 ;i < tmp.length ; i++){
				if(tmp[i].value){
					arrEmplPos.push({
						index : i,
						value : tmp[i].value
					});
				}
			}	
		}
		var row = {};
		row = {
			recruitmentCriteriaId : $('#recruitmentCriteriaIdAdd').val(),
			description : $('#descriptionAdd').val(),
			ListEmplPos : JSON.stringify(arrEmplPos)
		};
		 $("#jqxgrid").jqxGrid('addRow', null, row, "first");
		        // select the first row and clear the selection.
		        $("#jqxgrid").jqxGrid('clearSelection');                        
		        $("#jqxgrid").jqxGrid('selectRow', 0);  
		        $("#alterpopupWindow").jqxWindow('close');
		        $('#formAdd').trigger('reset');
		        $("#positionEmplAdd").jqxComboBox('clearSelection'); 
	});
</script>					