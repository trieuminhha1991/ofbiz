/******************************************************Edit Kill**************************************************************************/
	//Handle alterSaveSkill
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
        showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "80%", height: 300, minWidth: '40%', width: "50%", isModal: true,
        theme:'olbius', collapsed:false
    });
	
	//Create skillTypeId
	$("#skillTypeId").jqxDropDownList({source: skillTypeData, selectedIndex: 0, valueMember: "skillTypeId", displayMember: "description"});
	
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
	$("#skillLevel").jqxDropDownList({selectedIndex: 0, valueMember: "skillLevel", displayMember: "description"});
	
	/******************************************************End Kill**********************************************************************/