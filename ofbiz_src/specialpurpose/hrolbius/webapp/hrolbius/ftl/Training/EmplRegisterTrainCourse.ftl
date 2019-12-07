<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxpanel.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>

<script type="text/javascript">
	<#assign dataFields = "[{name:'partyId', type:'string'},
						{name: 'partyIdFrom', type: 'string'},
						{name: 'emplPositionTypeId', type: 'string'},
						{name: 'statusId', type:'string'}]">

	var statusRegister = new Array();
	<#list statusList as status>
		var row = {};
		row["statusId"] = "${status.statusId}";
		row["description"] = "${status.description}";
		statusRegister[${status_index}] = row;
	</#list>
	<#--/* statusRegister[${statusAttendanceResult?size}] = {statusId: "notDefinded", description: "${uiLabelMap.CommonNotDefined}"}; */-->
	var sourceStatusType = {
	        localdata: statusRegister,
	        datatype: "array"
	};
	
	
	var partyGroupArr = new Array();
	<#list listDepartment as department>
		var row = {};
		row["partyId"] = "${department.partyId}";
		row["groupName"] = "${department.groupName?if_exists}";
		partyGroupArr[${department_index}] = row;
	</#list>
	
	var sourcePartyGroup = {
	        localdata: partyGroupArr,
	        datatype: "array"
	};
	
	var emplPosType = new Array();
	<#list emplPositionType as posType>
		var row = {};
		row["emplPositionTypeId"] = "${posType.emplPositionTypeId}";
		row["description"] = "${posType.description?if_exists}";
		emplPosType[${posType_index}] = row;
	</#list>
	
	var sourceEmplPosType = {
	        localdata: emplPosType,
	        datatype: "array"
	};
	
	
    <#assign columnlist = "{text: '${uiLabelMap.EmployeeId}', datafield: 'partyId',  editable: false,  cellsalign: 'left', width: 130},
	{text: '${uiLabelMap.Department}', datafield: 'partyIdFrom', editable: false, cellsalign: 'left', width: 200, columntype: 'custom',filtertype: 'checkedlist',
		cellsrenderer: function (row, column, value){
			for(var i = 0; i < partyGroupArr.length; i++){
				if(partyGroupArr[i].partyId == value){
					return '<div style=\"\">' + partyGroupArr[i].groupName + '</div>';		
				}
			}
		},
		createfilterwidget: function(column, columnElement, widget){
		    //var selectAll = {'trainingTypeId': 'selectAll', 'description': '(Select All)'};
		    var filterBoxAdapter = new $.jqx.dataAdapter(sourcePartyGroup, {autoBind: true});
			var dataSoureList = filterBoxAdapter.records;
		    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
		    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'partyId', valueMember : 'partyId',  autoDropDownHeight: false,
				renderer: function (index, label, value) {
					for(i=0; i < partyGroupArr.length; i++){
						if(partyGroupArr[i].partyId == value){
							return partyGroupArr[i].groupName;
						}
					}
				    return value;
				}
			});
		}
	},
	{text: '${uiLabelMap.HREmployeePosition}', datafield: 'emplPositionTypeId', editable: false, cellsalign: 'left', width: 240, columntype: 'custom',filtertype: 'checkedlist',
		cellsrenderer: function (row, column, value){
			for(var i = 0; i < emplPosType.length; i++){
				if(emplPosType[i].emplPositionTypeId == value){
					return '<div style=\"\">' + emplPosType[i].description + '</div>';		
				}
			}
		},
		createfilterwidget: function(column, columnElement, widget){
		    //var selectAll = {'trainingTypeId': 'selectAll', 'description': '(Select All)'};
		    var filterBoxAdapter = new $.jqx.dataAdapter(sourceEmplPosType, {autoBind: true});
			var dataSoureList = filterBoxAdapter.records;
		    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
		    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'emplPositionTypeId', valueMember : 'emplPositionTypeId', autoDropDownHeight: false,
				renderer: function (index, label, value) {
					for(i=0; i < emplPosType.length; i++){
						if(emplPosType[i].emplPositionTypeId == value){
							return emplPosType[i].description;
						}
					}
				    return value;
				}
			});
		}	
	},
	{text: '${uiLabelMap.CommonStatus}', datafield: 'statusId', editable: false, cellsalign: 'left', columntype: 'custom',filtertype: 'checkedlist',
		cellsrenderer: function (row, column, value){
			for(var i = 0; i < statusRegister.length; i++){
				if(statusRegister[i].statusId == value){
					return '<div style=\"\">' + statusRegister[i].description + '</div>';		
				}
			}
		},
		createfilterwidget: function(column, columnElement, widget){
		    //var selectAll = {'trainingTypeId': 'selectAll', 'description': '(Select All)'};
		    var filterBoxAdapter = new $.jqx.dataAdapter(sourceStatusType, {autoBind: true});
			var dataSoureList = filterBoxAdapter.records;
		    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
		    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'statuId', valueMember : 'statusId', autoDropDownHeight: true,
				renderer: function (index, label, value) {
					for(i=0; i < statusRegister.length; i++){
						if(statusRegister[i].statusId == value){
							return statusRegister[i].description;
						}
					}
				    return value;
				}
			});
		}
	}"/>
	
</script>
	
<script type="text/javascript">
$(document).ready(function () {
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;   
	$('#popupWindowAddPartyAttend').jqxWindow({
	    showCollapseButton: true, maxHeight: '67%', autoOpen: false, maxWidth: "80%", minHeight: '67%', minWidth: "50%", height: '67%', width: "80%", isModal: true, 
	    theme:theme, collapsed:false,
	    initContent: function () {            	
	    }
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
     $("#splitter").jqxSplitter({  width: '100%', height: '96%', panels: [{ size: '20%'}, {size: '80%'}] });
     $('#jqxTree').jqxTree({ source: records,width: "100%", height: "100%", theme: theme});
     <#if expandedList?has_content>
	 	<#list expandedList as expandId>
	 		$('#jqxTree').jqxTree('expandItem', $("#${expandId}")[0]);
	 	</#list>
	 </#if>     
     $('#jqxTree').on('select', function (event) {
          var id = event.args.element.id;
          jQuery.ajax({
        	 url: "<@ofbizUrl>getListEmplInOrg</@ofbizUrl>",
        	 type: "POST",
        	 async: true,
        	 data: {partyId: id, trainingCourseId: "${parameters.trainingCourseId}", ajaxUrl: "addPartyIdRegisTrainingCourse"},			
 			 success: function(data){
 				jQuery("#feedListContainer").html(data);
 			 }
          });
     });
     <#if expandedList?has_content>
	     $('#popupWindowAddPartyAttend').on('open', function (event) {
		 	 $('#jqxTree').jqxTree('selectItem', $("#${expandedList.get(expandedList?size - 1)}")[0]);
	     }); 
     </#if>  
     $('#jqxgrid').on('rowDoubleClick', function (event){ 
	     var args = event.args;
	     // row's bound index.
	     var boundIndex = args.rowindex;
	     // row's visible index.
	     var data = $('#jqxgrid').jqxGrid('getrowdata', boundIndex);
	     var statusId = data.statusId;
	     var trainingCourseId = "${parameters.trainingCourseId}";
	     var partyId = data.partyId;
	     if(statusId == "COURS_REGIS_PARTCE"){
	    	 bootbox.dialog({
	    		 message: "${uiLabelMap.AddPartyToTrainingCourse}", 
	    		 buttons:{
					main: {
						label: "${uiLabelMap.CommonSubmit}!",
						className: "btn-small btn-primary open-sans icon-ok",
						callback: function() {
							$.ajax({
								url: "AddPartyTrainingCouseAndUpdateRegister",
								data: {partyId: partyId, trainingCourseId: trainingCourseId, statusId: "COURS_ATT_PARTCE"},
								type: 'POST',
								success: function(data){
									$('#jqxgrid').jqxGrid('updatebounddata');
								}
							});
						}
					},
					cancel: {
						label: "${uiLabelMap.CancelRegisterTrainingCourse}",
							className: "btn-small btn-danger open-sans icon-remove",
							callback: function() {
								$.ajax({
									url: "updateEmplResgisterTrainingCourse",
									data: {partyId: partyId, trainingCourseId: trainingCourseId, statusId: "COURS_REGIS_CANCEL"},
									type: 'POST',
									success: function(data){
										$('#jqxgrid').jqxGrid('updatebounddata');
									}
								});
							}
					}
				}
	    	 });
	     }
	 });
});
</script>	
	
<div id="popupWindowAddPartyAttend">
	<div id="windowHeader">
        <span>
           ${uiLabelMap.AddPartyToTrainingCourse}
        </span>
    </div>
	<div id="windowContent">
		<div id="splitter">
			<div>
				<div id="jqxTree"></div>
			</div>
			<div id="ContentPanel" style="overflow: auto;">
               <div class="jqx-hideborder jqx-hidescrollbars" >
                   <div class="jqx-hideborder" id="feedListContainer">
                   </div>
               </div>
               
        	</div>
		</div>
	</div>
</div>	
	
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataFields columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" 
		 filterable="false" alternativeAddPopup="popupWindowAddPartyAttend" deleterow="true" editable="true" addrow="true"
		 url="jqxGeneralServicer?hasrequest=Y&sname=JQListPartyRegisTrainingCourse&trainingCourseId=${parameters.trainingCourseId}" id="jqxgrid" 
		 removeUrl="" deleteColumn="" updateUrl="" editColumns="" jqGridMinimumLibEnable="false"
	/>	
