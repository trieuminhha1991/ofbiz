<div id="jqxgridSkill"></div>
<div id="createNewSkillWindow">
	<div id="windowHeaderNewSkill">
		<span>
		   ${uiLabelMap.NewSkill}
		</span>
	</div>
	<div class="basic-form form-horizontal" style="margin-top: 10px">
		<form name="createNewSkill" id="createNewSkill">	
			<div class="row-fluid" >
				<div class="span12">
					<div class="control-group no-left-margin">
						<label class="control-label asterisk">${uiLabelMap.skillTypeId}:</label>
						<div class="controls">
							<div id="skillTypeId"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.skillLevel}:</label>  
						<div class="controls">
							<div id="skillLevel"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">&nbsp</label>
						<div class="controls">
							<button type="button" class="btn btn-mini btn-primary" id="alterSaveSkill"><i class="icon-ok"></i>${uiLabelMap.CommonSubmit}</button>
							<button type="button" class="btn btn-mini btn-danger" id="alterCancelSkill"><i class="icon-remove">${uiLabelMap.CommonCancel}</i></button>
						</div>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>
<script type="text/javascript">
//Prepare Skill Type
<#assign skillTypeList = delegator.findByAnd("SkillType", null, null, false)>
var skillTypeData = new Array();
<#list skillTypeList as item>
	var row = {};
	<#assign description = StringUtil.wrapString(item.description?if_exists) />
	row['skillTypeId'] = '${item.skillTypeId?if_exists}';
	row['description'] = '${description}';
	skillTypeData[${item_index}] = row;
</#list>

//Prepare Skill Level
<#assign skillLevelList = delegator.findByAnd("SkillLevel", null, null, false)>
var skillLevelData = new Array();
<#list skillLevelList as item>
	var row = {};
	<#assign description = StringUtil.wrapString(item.description?if_exists) />
	row['skillLevelId'] = '${item.skillLevelId?if_exists}';
	row['description'] = '${description}';
	skillLevelData[${item_index}] = row;
</#list>
$(document).ready(function () {
	var skillData = new Array();
	var skillIndex = 0;
	/******************************************************Edit Kill**************************************************************************/
	//Handle alterSaveSkill
	var theme = "olbius";
	$("#alterSaveSkill").click(function () {
		var row;
	        row = {
	        		skillTypeId:$('#skillTypeId').val(),
	        		skillLevel:$("#skillLevel").val(),
			  };
	        skillIndex = skillData.length;
	        skillData[++skillIndex] = row;
	        $("#jqxgridSkill").jqxGrid('updatebounddata');
	        // select the first row and clear the selection.
	        $("#jqxgridSkill").jqxGrid('clearSelection');
	        $("#jqxgridSkill").jqxGrid('selectRow', 0);
	        $("#createNewSkillWindow").jqxWindow('close');
	    });
	//Create createNewSkillWindow
	$("#createNewSkillWindow").jqxWindow({
        showCollapseButton: false, maxHeight: 200, autoOpen: false, maxWidth: "80%", height: 200, minWidth: '40%', width: "50%", isModal: true,
        theme:'olbius', collapsed:false
    });
	
	//Create skillTypeId
	$("#skillTypeId").jqxDropDownList({source: skillTypeData, selectedIndex: 0, valueMember: "skillTypeId", displayMember: "description", theme: theme});
	
	$('#skillTypeId').on('change', function (event){     
		    var args = event.args;
		    if (args) {
			    var index = 0;
			    var item = args.item;
			    var value = item.value;
			    var data = new Array();
			    for(var i = 0; i < skillLevelData.length; i++){
			    	if(skillLevelData[i].skillTypeId == value){
			    		data[index] = skillLevelData[i];
			    		index++;
			    	}
			    }
			    $("#skillLevel").jqxDropDownList({source: data});
		} 
	});
	//Create skillLevel
	$("#skillLevel").jqxDropDownList({selectedIndex: 0, valueMember: "skillLevel", displayMember: "description", theme: theme});
	
	/******************************************************End Kill**********************************************************************/
	var source =
    {
        localdata: skillData,
        datatype: "array",
        datafields:
        [
        	{ name: 'skillTypeId', type: 'string' },
			{ name: 'skillLevel', type: 'string' }
        ]
    };
	var dataAdapter = new $.jqx.dataAdapter(source);
    
	$("#jqxgridSkill").jqxGrid(
    {
        width: "99%",
        source: dataAdapter,
        columnsresize: true,
        pageable: true,
        autoheight: true,
        showtoolbar: true,
        theme: 'olbius',
        rendertoolbar: function (toolbar) {
            var container = $("<div id='toolbarcontainer' class='widget-header'>");
            toolbar.append(container);
            container.append('<h4></h4>');
            container.append('<button id="skillAddrowbutton" class="grid-action-button"><i class="icon-plus-sign"></i>${uiLabelMap.accAddNewRow}</button>');
            container.append('<button id="skillDelrowbutton" class="grid-action-button"><i class="icon-trash"></i>${uiLabelMap.accDeleteSelectedRow}</button>');
            
            // create new row.
            $("#skillAddrowbutton").on('click', function () {
            	$("#createNewSkillWindow").jqxWindow('open');
            });
            
            // create new row.
            $("#skillDelrowbutton").on('click', function () {
            	var selectedrowindex = $('#jqxgridSkill').jqxGrid('selectedrowindex'); 
            	skillData.splice(selectedrowindex, 1);
            	$('#jqxgridSkill').jqxGrid('updatebounddata'); 
            	
            });
        },
        columns: [
      	  { text: '${uiLabelMap.skillTypeId}', datafield: 'skillTypeId', width: 150},
          { text: '${uiLabelMap.skillLevel}', datafield: 'skillLevel'}
        ]
    });
});
</script>