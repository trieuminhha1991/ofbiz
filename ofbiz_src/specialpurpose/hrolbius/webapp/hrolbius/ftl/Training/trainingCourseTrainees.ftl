<div class="row-fluid">
	<div class="span12">
		<form action="" class="form-horizontal" method="post">
			<div class="control-group">
				<label class="control-label">${uiLabelMap.EmplPositionTypeTrained}</label>
				<div class="controls">
					<div style='' id='emplPosTypeDropDownList'>
            		</div>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">${uiLabelMap.PartyGroupTrained}</label>
				<div class="controls">
					<div id="dropDownButtonpartyTrainee">
						<div style=";" id='jqxTreeTrainee'>
			                
			            </div>
					</div>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">${uiLabelMap.CommonIsCompulsory}</label>
				<div class="controls">
					<div id='IsCompulsoryCheckBox'></div>
				</div>
			</div>
			
			<!-- <div class="control-group no-left-margin">
				<label class="control-label">
					${uiLabelMap.EmplListAttendanceTrainingCourse}	
				</label>
				<div class="controls">
					<a href="<@ofbizUrl>EmplAttendanceTrainCourse?trainingCourseId=${parameters.trainingCourseId}</@ofbizUrl>">${uiLabelMap.ViewDetails}</a>
				</div>
			</div> -->
			<div class="control-group">
				<label class="control-label">&nbsp;</label>
				<div class="controls">
					<input type="button" value="${uiLabelMap.CommonSubmit}" id='submitTrained' />
				</div>
			</div>
		</form>
	</div>
	<div class="row-fluid">
		<div id="notiUpdateTraineeSuccess">
			<div id="windowHeaderUpdateTrainee">
                <span>
                   ${uiLabelMap.ResultUpdate}
                </span>
            </div>	
            <div style="overflow: hidden; padding: 0" id="windowContentUpdateTrainee">
            	<div class="row-fluid">
		           	<div class="span12">
		           		<form action="" class="form-horizontal" method="post">
		           			<div class="control-group" style="margin-left: 10px">
		           				<div id="updateTraineeMessage"></div>
		           			</div>            			
		           			<div class="control-group">
		           				<div class="span2"></div>
		           				<div class="span5">
		           					<div id='sendNotify'>
		           						${uiLabelMap.SendNotifyToTraineeInTraining}
		           					</div>	
		           				</div>
		           				
		           			</div>
		           			<div class="control-group" style="">
		           				<div class="span2"></div>
		           				<div class="span5">
									<div id='addTraineeToTrainingCourse'>
										${uiLabelMap.AddTraineeToTrainingCourse}
									</div>
								</div>
		           			</div>
		           			<div class="control-group">
		           				<div class="span4"></div>
		           				<div class="span7">
		           					<input type="button" value="${uiLabelMap.CommonSubmit}" id='submitWindowTrainee'/>
									<input type="button" value="${uiLabelMap.CommonClose}" id='closeWindowTrainee' />
		           				</div>
		           			</div>
						</form>
		           	</div>
            	</div>
            </div>	
		</div>
	</div>
</div>
<script type="text/javascript">
	$(document).ready(function () {
		$.jqx.theme = 'olbius';
		theme = $.jqx.theme;   
		var emplPosTypeArr = new Array();	
		<#list emplPosType as posType>
			var row = {};
			row["emplPositionTypeId"] = "${posType.emplPositionTypeId}";
			row["description"] = "${StringUtil.wrapString(posType.description)}";
			emplPosTypeArr[${posType_index}] = row;
		</#list>
		var emplPosSource = {
		        localdata: emplPosTypeArr,
		        datatype: "array"
		};
		var filterBoxAdapter = new $.jqx.dataAdapter(emplPosSource, {autoBind: true});
	    var dataSoureList = filterBoxAdapter.records;
	   
	    $("#emplPosTypeDropDownList").jqxComboBox({source: dataSoureList, placeHolder:'', displayMember: 'description',dropDownHeight:'200px',  
	    	valueMember : 'emplPositionTypeId', height: '25px', width: 500,checkboxes: false, theme: 'olbius',multiSelect: true,  
			renderer: function (index, label, value) {
				for(i=0; i < emplPosTypeArr.length; i++){
					if(emplPosTypeArr[i].emplPositionTypeId == value){
						return emplPosTypeArr[i].description;
					}
				}
			    return value;
			}
		});
	    
	    $('#notiUpdateTraineeSuccess').jqxWindow({
            showCollapseButton: false, autoOpen: false, height: 200, width: "35%", isModal: true, 
            theme:theme, collapsed:false,
            initContent: function () {            	
                //jQuery('#trainingCourseDetailWindow').jqxWindow('focus');
                //jQuery('#trainingCourseDetailWindow').jqxWindow('close');
            }
        });
	    
	    $("#submitWindowTrainee").jqxButton({ width: 'auto', theme: theme});
		$("#closeWindowTrainee").jqxButton({ width: 'auto', theme: theme});
		$("#submitWindowTrainee").click(function(){
		$('#notiUpdateTraineeSuccess').jqxWindow('close');
			/* ========================submit data============================ */
		});
		
		$("#closeWindowTrainee").click(function(){
			$('#notiUpdateTraineeSuccess').jqxWindow('close');
		});
	   
	   /*  $("#emplPosTypeDropDownList").on('checkChange', function (event) {
	    	
	    }); */
	    <#list trainingCourseEmplPosType as emplPosType>
	    	$("#emplPosTypeDropDownList").jqxComboBox('selectItem', "${emplPosType.emplPositionTypeId}");
	    </#list>	    
	    <#if trainingCourse.isCompulsory?exists && trainingCourse.isCompulsory == "Y">
	    	var checked = true;
	    <#else>
	    	var checked = false;
	    </#if>
	    
	    $('#IsCompulsoryCheckBox').jqxCheckBox({ width: '200px', height: '25px', checked: checked, theme:'olbius'});
	    $('#sendNotify').jqxCheckBox({ width: '200px', height: '25px', checked: false, theme:'olbius'});
	    $('#addTraineeToTrainingCourse').jqxCheckBox({ width: '200px', height: '25px', checked: false, theme:'olbius'});
	    
	    $("#submitTrained").jqxButton({ width: '150', height: '30', theme: 'olbius'});
		$("#submitTrained").click(function(){
			var checked = $("#IsCompulsoryCheckBox").jqxCheckBox('checked');
			var partySelected = $('#jqxTreeTrainee').jqxTree('getCheckedItems');
			var emplPosTypeSelected = $("#emplPosTypeDropDownList").jqxComboBox('getSelectedItems');
			var partyIds = new Array();
			var emplPosTypes = new Array();
			var isCompulsory = "N";
			if(checked){
				isCompulsory = "Y";
			}
			for(var i = 0; i < partySelected.length; i++){
				partyIds.push({partyId: partySelected[i].id});
			}
			for(var i = 0; i< emplPosTypeSelected.length; i++){
				emplPosTypes.push({emplPositionTypeId: emplPosTypeSelected[i].value});
			}
			
			jQuery.ajax({
				url: "<@ofbizUrl>updateTrainingCourseTraineed</@ofbizUrl>",
				data:{trainingCourseId: "${parameters.trainingCourseId}", partyIds: JSON.stringify(partyIds), emplPositionTypes: JSON.stringify(emplPosTypes), isCompulsory: isCompulsory},
				type:'POST', 
				success: function(data){
					if(data._EVENT_MESSAGE_){
						jQuery("#updateTraineeMessage").html("<h5><b>" + data._EVENT_MESSAGE_ + "</b></h5>");
						/* var isCompulsory = $("#IsCompulsoryCheckBox").jqxCheckBox('checked');
						if(isCompulsory){
								
						}else{
							
						} */
					}else{
						if(data._ERROR_MESSAGE_){
							jQuery("#updateTraineeMessage").html(data._ERROR_MESSAGE_);
						}else{
							jQuery("#updateTraineeMessage").html("${uiLabelMap.ErrorWhenUpdate}");
						}
					}
					jQuery('#notiUpdateTraineeSuccess').jqxWindow('open');
				}
			});
		});
		
		var data = new Array();
		<#list treePartyGroup as tree>
			var row = {};
			row["id"] = "${tree.id}";
			row["text"] = "${tree.text}";
			row["parentId"] = "${tree.parentId}";
			data[${tree_index}] = row;
		</#list>
		 var source =
         {
             datatype: "json",
             datafields: [
                 { name: 'id' },
                 { name: 'parentId' },
                 { name: 'text' }                
             ],
             id: 'id',
             localdata: data
         };
		 var dataAdapter = new $.jqx.dataAdapter(source);
         // perform Data Binding.
         dataAdapter.dataBind();
         // get the tree items. The first parameter is the item's id. The second parameter is the parent item's id. The 'items' parameter represents 
         // the sub items collection name. Each jqxTree item has a 'label' property, but in the JSON data, we have a 'text' field. The last parameter 
         // specifies the mapping between the 'text' and 'label' fields.  
         var records = dataAdapter.getRecordsHierarchy('id', 'parentId', 'items', [{ name: 'text', map: 'label'}]);
         //$("#dropDownButtonpartyTrainee").jqxDropDownButton({ width: 500, height: '25px', theme: theme});
         $('#jqxTreeTrainee').jqxTree({ source: records,width: 500, height: 220, theme: 'energyblue', hasThreeStates: true, checkboxes: true});
         <#if expandedList?has_content>
         	
         	<#list expandedList as expandId>
         		
         		$('#jqxTreeTrainee').jqxTree('expandItem', $("#${expandId}")[0]);
         	</#list>
         </#if>
        // $('#jqxTreeTrainee').jqxTree('expandItem', $("#company")[0]);
         /* $('#jqxTreeTrainee').on('checkChange', function (event) {
			 var partySelected = $('#jqxTreeTrainee').jqxTree('getCheckedItems');
			 var strDisplay = "";
			 if(partySelected.length){
				 for(var i = 0; i < partySelected.length - 1; i++){				 
					 strDisplay += partySelected[i].label + ", ";
				 }
				 strDisplay += partySelected[partySelected.length - 1].label;
			 }
             var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + strDisplay + '</div>';
             $("#dropDownButtonpartyTrainee").jqxDropDownButton('setContent', dropDownContent);             
         }); */
         $('#jqxTreeTrainee').on('select',function (event){
        	 //console.log(event.args);
        	 $("#jqxTreeTrainee").jqxTree('checkItem', $("#" + event.args.element.id)[0], true);
         });
         <#list trainingCourseTrainee as trainee>
         	$("#jqxTreeTrainee").jqxTree('checkItem', $("#${trainee.partyId}")[0], true);         	
         </#list>
	});
</script>
