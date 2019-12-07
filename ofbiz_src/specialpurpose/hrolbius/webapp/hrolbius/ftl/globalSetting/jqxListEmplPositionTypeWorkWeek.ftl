<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script>
var emplPositionTypeArray = [
   					<#if emplPositionTypes?has_content>
   						<#list emplPositionTypes as emplPositionType>
   						{
   							emplPositionTypeId: "${emplPositionType.emplPositionTypeId?if_exists}",
   							description: "${StringUtil.wrapString(emplPositionType.description?if_exists)}"
   						},
   						</#list>
   					</#if>
                          ];

var emplPositionTypeWorkWeekArray = [
            					<#if emplPositionTypeWorkWeekList?has_content>
            						<#list emplPositionTypeWorkWeekList as emplPositionTypeWorkWeek>
            						{
            							emplPositionTypeId: "${emplPositionTypeWorkWeek.emplPositionTypeId?if_exists}",
            							dayOfWeek: "${StringUtil.wrapString(emplPositionTypeWorkWeek.dayOfWeek?if_exists)}",
            							workingShiftId: "${StringUtil.wrapString(emplPositionTypeWorkWeek.workingShiftId?if_exists)}"
            						},
            						</#list>
            					</#if>
                                   ];

var workingShiftArray = [ 
							{
									workingShiftId: "CA_NGAY",
									description: "${uiLabelMap.HrolbiusFullTime}"
							},
							<#if workingShiftList?has_content>
							<#list workingShiftList as workingShift>
							{
								workingShiftId: "${workingShift.workingShiftId?if_exists}",
								description: "${StringUtil.wrapString(workingShift.description?if_exists)}"
							},
							</#list>
							</#if>
                        ];

var mapEmplPositionType = {
		<#if emplPositionTypes?has_content>
			<#list emplPositionTypes as emplPositionType>
				"${emplPositionType.emplPositionTypeId?if_exists}": "${StringUtil.wrapString(emplPositionType.description?if_exists)}",
			</#list>
		</#if>
};

var mapEmplPositionTypeWorkWeek = {
		<#if emplPositionTypeWorkWeekList?has_content>
			<#list emplPositionTypeWorkWeekList as emplPositionTypeWorkWeek>
				"${emplPositionTypeWorkWeek.emplPositionTypeId?if_exists}": "${StringUtil.wrapString(emplPositionTypeWorkWeek.dayOfWeek?if_exists)}",
			</#list>
		</#if>
};

var mapWorkingShift = {
		<#if workingShiftList?has_content>
			<#list workingShiftList as mapWorkingShift>
				"${mapWorkingShift.workingShiftId?if_exists}": "${StringUtil.wrapString(mapWorkingShift.description?if_exists)}",
			</#list>
		</#if>
};

function fixSelectAll(dataList) {
	var sourceST = {
	        localdata: dataList,
	        datatype: "array"
	    };
		var filterBoxAdapter2 = new $.jqx.dataAdapter(sourceST, {autoBind: true});
    var uniqueRecords2 = filterBoxAdapter2.records;
		uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
return uniqueRecords2;
}
</script>

<#assign dataField="[{ name: 'emplPositionTypeId', type: 'string' },
						 { name: 'MONDAY', type: 'string'},
						 { name: 'TUESDAY', type: 'string'},
						 { name: 'WEDNESDAY', type: 'string'},
						 { name: 'THURSDAY', type: 'string'},
						 { name: 'FRIDAY', type: 'string'},
						 { name: 'SATURDAY', type: 'string'},
						 { name: 'SUNDAY', type: 'string' }
						 ]
						 "/>
						 
<#assign columnlist="{ text: '${uiLabelMap.EmplPositionTypeId}', datafield: 'emplPositionTypeId', width: 250 ,  filterable : true ,filtertype : 'checkedlist',
					 cellsrenderer : function(row, column, value){
							value?value=mapEmplPositionType[value]:value='';
							return '<div style=\"margin-top: 6px; margin-left: 4px;\">' + value + '</div>';
						},
						createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
					        editor.jqxDropDownList({source: emplPositionTypeArray ,valueMember: 'emplPositionTypeId', displayMember: 'description' });
						},
					    createfilterwidget: function (column, htmlElement, editor) {
	    		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(emplPositionTypeArray), displayMember: 'description', valueMember: 'emplPositionTypeId' ,
	                            renderer: function (index, label, value) {
	                            	if (index == 0) {
	                            		return value;
									}
	                            	for(var i = 0; i < emplPositionTypeArray.length; i++){
										if(value == emplPositionTypeArray[i].emplPositionTypeId){
											return emplPositionTypeArray[i].description; 
										}
									}
				                } });
	    		        	editor.jqxDropDownList('checkAll');
	                    }
					 },
					 { text: '${uiLabelMap.ProjectMgrMon}', datafield: 'MONDAY', width: 125, filterable : false,
						 cellsrenderer : function(row, column, value){
							 var ret = new String;
							 var temp = value.split(', ');
							 if(value!= null ){
								 temp[0]?temp[0]=mapWorkingShift[temp[0]]:temp[0]='';
								 ret+= temp[0];
								 temp[1]?temp[1]=mapWorkingShift[temp[1]]:temp[1]='';
								 if(temp[1] != '') {
									 ret+=', ' + temp[1];
								 }
							 }
							 
							return '<div style=\"margin-top: 6px; margin-left: 4px;\">' + ret + '</div>';
					 }
					 },
					 { text: '${uiLabelMap.ProjectMgrTue}', datafield: 'TUESDAY', width: 125, filterable : false,
						 cellsrenderer : function(row, column, value){
							 var ret = new String;
							 var temp = value.split(', ');
							 if(value!= null ){
								 temp[0]?temp[0]=mapWorkingShift[temp[0]]:temp[0]='';
								 ret+= temp[0];
								 temp[1]?temp[1]=mapWorkingShift[temp[1]]:temp[1]='';
								 if(temp[1] != '') {
									 ret+=', ' + temp[1];
								 }
							 }
							return '<div style=\"margin-top: 6px; margin-left: 4px;\">' + ret + '</div>';
						 }
					 },
					 { text: '${uiLabelMap.ProjectMgrWen}', datafield: 'WEDNESDAY', width: 125, filterable : false,
						 cellsrenderer : function(row, column, value){
							 var ret = new String;
							 var temp = value.split(', ');
							 if(value!= null ){
								 temp[0]?temp[0]=mapWorkingShift[temp[0]]:temp[0]='';
								 ret+= temp[0];
								 temp[1]?temp[1]=mapWorkingShift[temp[1]]:temp[1]='';
								 if(temp[1] != '') {
									 ret+=', ' + temp[1];
								 }
							 }
							return '<div style=\"margin-top: 6px; margin-left: 4px;\">' + ret + '</div>';
						 }
					 },
					 { text: '${uiLabelMap.ProjectMgrThu}', datafield: 'THURSDAY', width: 125, filterable : false,
						 cellsrenderer : function(row, column, value){
							 var ret = new String;
							 var temp = value.split(', ');
							 if(value!= null ){
								 temp[0]?temp[0]=mapWorkingShift[temp[0]]:temp[0]='';
								 ret+= temp[0];
								 temp[1]?temp[1]=mapWorkingShift[temp[1]]:temp[1]='';
								 if(temp[1] != '') {
									 ret+=', ' + temp[1];
								 }
							 }
							return '<div style=\"margin-top: 6px; margin-left: 4px;\">' + ret + '</div>';
						 }
					 },
					 { text: '${uiLabelMap.ProjectMgrFri}', datafield: 'FRIDAY', width: 125, filterable : false,
						 cellsrenderer : function(row, column, value){
							 var ret = new String;
							 var temp = value.split(', ');
							 if(value!= null ){
								 temp[0]?temp[0]=mapWorkingShift[temp[0]]:temp[0]='';
								 ret+= temp[0];
								 temp[1]?temp[1]=mapWorkingShift[temp[1]]:temp[1]='';
								 if(temp[1] != '') {
									 ret+=', ' + temp[1];
								 }
							 }
							return '<div style=\"margin-top: 6px; margin-left: 4px;\">' + ret + '</div>';
						 }
					 },
					 { text: '${uiLabelMap.ProjectMgrSat}', datafield: 'SATURDAY', width: 125, filterable : false,
						 cellsrenderer : function(row, column, value){
							 var ret = new String;
							 var temp = value.split(', ');
							 if(value!= null ){
								 temp[0]?temp[0]=mapWorkingShift[temp[0]]:temp[0]='';
								 ret+= temp[0];
								 temp[1]?temp[1]=mapWorkingShift[temp[1]]:temp[1]='';
								 if(temp[1] != '') {
									 ret+=', ' + temp[1];
								 }
							 }
							return '<div style=\"margin-top: 6px; margin-left: 4px;\">' + ret + '</div>';
						 }
					 },
					 { text: '${uiLabelMap.ProjectMgrSun}', datafield: 'SUNDAY', width: 120, filterable : false,
						 cellsrenderer : function(row, column, value){
							 var ret = new String;
							 var temp = value.split(', ');
							 if(value!= null ){
								 temp[0]?temp[0]=mapWorkingShift[temp[0]]:temp[0]='';
								 ret+= temp[0];
								 temp[1]?temp[1]=mapWorkingShift[temp[1]]:temp[1]='';
								 if(temp[1] != '') {
									 ret+=', ' + temp[1];
								 }
							 }
							return '<div style=\"margin-top: 6px; margin-left: 4px;\">' + ret + '</div>';
						 }
					 }
					 "/>

<@jqGrid 
dataField=dataField 
columnlist=columnlist
clearfilteringbutton="true"
filtersimplemode="true"
filterable="true"
defaultSortColumn="name" 
editrefresh="true"	
editmode="popup"
url="jqxGeneralServicer?sname=JQgetListEmplPositionTypeWorkWeek"
/>

 <div id="popupWindow" style="display : none;">
      <div>${uiLabelMap.EmplPositionTypeWorkweekSet}</div>
      <div style="overflow: hidden;">
          <form id="formAdd" class="form-horizontal">
          

          <div class="row-fluid no-left-margin">
              <label class="span4 asterisk" style="text-align:right;"> ${uiLabelMap.EmplPositionTypeId}</label>
              <div class="span8 control" style="margin-bottom: 10px;">
             <label type="text" class="control" id="emplPositionType"></label>
               
          </div>
          </div>
          
          <div class="row-fluid no-left-margin">
	          <label class="span4 asterisk" style="text-align:right;"> ${uiLabelMap.ProjectMgrMon}</label>
	          <div class="span8" style="margin-bottom: 10px;">
	          
			  <div id='monday'></div>
	          </div>
	      </div>
	      
	      <div class="row-fluid no-left-margin">
	          <label class="span4 asterisk" style="text-align:right;"> ${uiLabelMap.ProjectMgrTue}</label>
	          <div class="span8" style="margin-bottom: 10px;">
	          <div id='tuesday'></div>
	          </div>
	      </div>
	      
	      <div class="row-fluid no-left-margin">
	          <label class="span4 asterisk" style="text-align:right;"> ${uiLabelMap.ProjectMgrWen}</label>
	          <div class="span8" style="margin-bottom: 10px;">
	          <div id='wednesday'></div>
	          </div>
	      </div>
	  
		  <div class="row-fluid no-left-margin">
		      <label class="span4 asterisk" style="text-align:right;"> ${uiLabelMap.ProjectMgrThu}</label>
		      <div class="span8" style="margin-bottom: 10px;">
		      <div id='thursday'></div>
		      </div>
		  </div>
		  
		  <div class="row-fluid no-left-margin">
	          <label class="span4 asterisk" style="text-align:right;"> ${uiLabelMap.ProjectMgrFri}</label>
	          <div class="span8" style="margin-bottom: 10px;">
	          <div id='friday'></div>
	          </div>
	      </div>
	      
	      <div class="row-fluid no-left-margin">
	          <label class="span4 asterisk" style="text-align:right;"> ${uiLabelMap.ProjectMgrSat}</label>
	          <div class="span8" style="margin-bottom: 10px;">
	          <div id='saturday'></div>
	          </div>
	      </div>
	  
		  <div class="row-fluid no-left-margin">
		      <label class="span4 asterisk" style="text-align:right;"> ${uiLabelMap.ProjectMgrSun}</label>
		      <div class="span8" style="margin-bottom: 10px;">
		      <div id='sunday'></div>
		      </div>
		  </div>
          
          <div class="control-group no-left-margin" style="float:right">
          <div class="" style="width:166px;margin:0 auto;">
           <button type="button" class="jqx-rc-all jqx-rc-all-olbius jqx-button jqx-button-olbius jqx-widget jqx-widget-olbius btn btn-primary btn-mini" style="margin-right: 5px; margin-top: 10px;" id="alterSave"><i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
           <button type="button" class="jqx-rc-all jqx-rc-all-olbius jqx-button jqx-button-olbius jqx-widget jqx-widget-olbius  btn-danger btn-primary btn-mini" style="margin-right: 5px; margin-top: 10px;" id="alterCancel"><i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
          </div>
         </div>
              
          </form>
      </div>
 </div>
 
 <script>
 var editrow;
 
 
 $(document).ready(function () {
	 
		$('#jqxgrid').on('rowDoubleClick', function(event){
			 var visibleIndex = args.visibleindex;
			 editrow = visibleIndex;
			
			
            var offset = $('#jqxgrid').offset();
            $('#popupWindow').jqxWindow();
            // get the clicked row's data and initialize the input fields.
            var dataRecord = $('#jqxgrid').jqxGrid('getrowdata', editrow);
            
   
               	 
           	if (dataRecord.MONDAY == '')
           		 $('#monday').jqxDropDownList('clearSelection');
           	else
           		 {
           		 	for(i=0; i < workingShiftArray.length ; i ++)
           		 		{
           		 		
           		 		if(workingShiftArray[i].workingShiftId == dataRecord.MONDAY)
           		 			{
           		 			$('#monday').jqxDropDownList('val', dataRecord.MONDAY);
           		 			break;
           		 			}
           		 		else $('#monday').jqxDropDownList('val', workingShiftArray[0].workingShiftId);
           		 		}
       		 };
       		 if (dataRecord.TUESDAY == '')
           		 $('#tuesday').jqxDropDownList('clearSelection');
           	 else
           		 {
           		 	for(i=0; i < workingShiftArray.length ; i ++)
           		 		{
           		 		if(workingShiftArray[i].workingShiftId == dataRecord.TUESDAY)
           		 			{
           		 			$('#tuesday').jqxDropDownList('val', dataRecord.TUESDAY);
           		 			break;
           		 			}
           		 		else $('#tuesday').jqxDropDownList('val', workingShiftArray[0].workingShiftId);
           		 		}
       		 };
       		 if (dataRecord.WEDNESDAY == '')
           		 $('#wednesday').jqxDropDownList('clearSelection');
           	 else
           		 {
           		 	for(i=0; i < workingShiftArray.length ; i ++)
           		 		{
           		 		
           		 		if(workingShiftArray[i].workingShiftId == dataRecord.WEDNESDAY)
           		 			{
           		 			$('#wednesday').jqxDropDownList('val', dataRecord.WEDNESDAY);
           		 			break;
           		 			}
           		 		else $('#wednesday').jqxDropDownList('val', workingShiftArray[0].workingShiftId);
           		 		}
       		 };
       		 if (dataRecord.THURSDAY == '')
           		 $('#thursday').jqxDropDownList('clearSelection');
           	 else
           		 {
           		 	for(i=0; i < workingShiftArray.length ; i ++)
           		 		{
           		 		
           		 		if(workingShiftArray[i].workingShiftId == dataRecord.THURSDAY)
           		 			{
           		 			$('#thursday').jqxDropDownList('val', dataRecord.THURSDAY);
           		 			break;
           		 			}
           		 		else $('#thursday').jqxDropDownList('val', workingShiftArray[0].workingShiftId);
           		 		}
       		 };
       		 
       		 if (dataRecord.FRIDAY == '')
           		 $('#friday').jqxDropDownList('clearSelection');
           	 else
           		 {
           		 	for(i=0; i < workingShiftArray.length ; i ++)
           		 		{
           		 		
           		 		if(workingShiftArray[i].workingShiftId == dataRecord.FRIDAY)
           		 			{
           		 			$('#friday').jqxDropDownList('val', dataRecord.FRIDAY);
           		 			break;
           		 			}
           		 		else $('#friday').jqxDropDownList('val', workingShiftArray[0].workingShiftId);
           		 		}
       		 };
       		 
       		 if (dataRecord.SATURDAY == '')
           		 $('#saturday').jqxDropDownList('clearSelection');
           	 else
           		 {
           		 	for(i=0; i < workingShiftArray.length ; i ++)
           		 		{
           		 		
           		 		if(workingShiftArray[i].workingShiftId == dataRecord.SATURDAY)
           		 			{
           		 			$('#saturday').jqxDropDownList('val', dataRecord.SATURDAY);
           		 			break;
           		 			}
           		 		else $('#saturday').jqxDropDownList('val', workingShiftArray[0].workingShiftId);
           		 		}
       		 };
       		 
       		 if (dataRecord.SUNDAY == '')
           		 $('#sunday').jqxDropDownList('clearSelection');
           	 else
           		 {
           		 	for(i=0; i < workingShiftArray.length ; i ++)
           		 		{
           		 		if(workingShiftArray[i].workingShiftId == dataRecord.SUNDAY)
           		 			{
           		 			$('#sunday').jqxDropDownList('val', dataRecord.SUNDAY);
           		 			break;
           		 			}
           		 		else $('#sunday').jqxDropDownList('val', workingShiftArray[0].workingShiftId);
           		 		}
       		 };
           		 
            $('#emplPositionTypeId').text(dataRecord.emplPositionTypeId);
            
            var temp = dataRecord.emplPositionTypeId;
            temp?temp=mapEmplPositionType[temp]:temp='';
            $('#emplPositionType').text(temp);
			
			var wtmp = window;
         	var tmpwidth = $('#popupWindow').jqxWindow('width');
            $('#popupWindow').jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 30 } });
			$('#popupWindow').jqxWindow('open');
		}); 
	 
	 
	 
	    $("#monday").jqxDropDownList({ source: workingShiftArray,displayMember: "description",
	    	 valueMember : "workingShiftId", width: '225', height: '30',autoDropDownHeight: true});
	    $("#tuesday").jqxDropDownList({ source: workingShiftArray,displayMember: "description",
	    	 valueMember : "workingShiftId",  width: '225', height: '30',autoDropDownHeight: true});
	    $("#wednesday").jqxDropDownList({ source: workingShiftArray,displayMember: "description",
	    	 valueMember : "workingShiftId",  width: '225', height: '30',autoDropDownHeight: true});
	    $("#thursday").jqxDropDownList({ source: workingShiftArray,displayMember: "description",
	    	 valueMember : "workingShiftId",  width: '225', height: '30',autoDropDownHeight: true});
	    $("#friday").jqxDropDownList({ source: workingShiftArray,displayMember: "description",
	    	 valueMember : "workingShiftId", width: '225', height: '30',autoDropDownHeight: true});
	    $("#saturday").jqxDropDownList({ source: workingShiftArray,displayMember: "description",
	    	 valueMember : "workingShiftId", width: '225', height: '30',autoDropDownHeight: true});
	    $("#sunday").jqxDropDownList({ source: workingShiftArray,displayMember: "description",
	    	 valueMember : "workingShiftId",  width: '225', height: '30',autoDropDownHeight: true});
 });
 
 $('#formAdd').jqxValidator({
	    rules: [
	           ]});
	
 $('#formAdd').on('validationError', function (event) {
// There's even nothing to be validated
  });

 

 
 $('#formAdd').on('validationSuccess', function (event) {

	  if (editrow >= 0) {
		  
		  var emplPositionTypeId = $('#jqxgrid').jqxGrid('getrowdata', editrow).emplPositionTypeId;
          var mondayIndex = $("#monday").jqxDropDownList('getSelectedIndex');
          var tuesdayIndex = $("#tuesday").jqxDropDownList('getSelectedIndex');
          var wednesdayIndex = $("#wednesday").jqxDropDownList('getSelectedIndex');
          var thursdayIndex = $("#thursday").jqxDropDownList('getSelectedIndex');
          var fridayIndex = $("#friday").jqxDropDownList('getSelectedIndex');
          var saturdaydayIndex = $("#saturday").jqxDropDownList('getSelectedIndex');
          var sundayIndex = $("#sunday").jqxDropDownList('getSelectedIndex');
          
          var mondayShift;
          for(i=0 ; i < workingShiftArray.length; i ++)
        	  {
        	  if(i == mondayIndex)
        		  mondayShift = workingShiftArray[i].workingShiftId;
        	  };
          
          var tuesdayShift;
          for(i=0 ; i < workingShiftArray.length; i ++)
        	  {
        	  if(i == tuesdayIndex)
        		  tuesdayShift = workingShiftArray[i].workingShiftId;
        	  };
          
          var wednesdayShift;
          for(i=0 ; i < workingShiftArray.length; i ++)
        	  {
        	  if(i == wednesdayIndex)
        		  wednesdayShift = workingShiftArray[i].workingShiftId;
        	  };
          
          var thursdayShift;
          for(i=0 ; i < workingShiftArray.length; i ++)
        	  {
        	  if(i == thursdayIndex)
        		  thursdayShift = workingShiftArray[i].workingShiftId;
        	  };
          
          var fridayShift;
          for(i=0 ; i < workingShiftArray.length; i ++)
        	  {
        	  if(i == fridayIndex)
        		  fridayShift = workingShiftArray[i].workingShiftId;
        	  };
          
          var saturdayShift;
          for(i=0 ; i < workingShiftArray.length; i ++)
        	  {
        	  if(i == saturdaydayIndex)
        		  saturdayShift = workingShiftArray[i].workingShiftId;
        	  };
          
          var sundayShift;
          for(i=0 ; i < workingShiftArray.length; i ++)
        	  {
        	  if(i == sundayIndex)
        		  sundayShift = workingShiftArray[i].workingShiftId;
        	  };
          
          $.ajax({
      		url: 'updateEmplPositionTypeWorkWeek',
      		type: 'POST',
      		async: false,
      		data: {emplPositionTypeId: emplPositionTypeId, monday: mondayShift, tuesday: tuesdayShift, wednesday: wednesdayShift, thursday: thursdayShift, friday: fridayShift, saturday: saturdayShift, sunday: sundayShift,  },
      	 	success: function(data){
      	 	},
      	 	complete: function(){
//      	 		$("#popupWindow").jqxWindow('close');
//                $('#jqxgrid').jqxGrid('updatebounddata');
      	 	}
      	  });
          
          $("#popupWindow").jqxWindow('close');
          $('#jqxgrid').jqxGrid('updatebounddata');
// not always works, shoot it twice
//          $('#jqxgrid').jqxGrid('updatebounddata');
//// damn it,still alive sometimes, one more
//          $('#jqxgrid').jqxGrid('updatebounddata');
      }
 }); 
 
 $("#alterSave").click(function () {
	 $('#formAdd').jqxValidator('validate');
 });
 
 // initialize the popup window and buttons.
 $("#popupWindow").jqxWindow({
     width: 550, height: 440, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.8           
 });
</script>


