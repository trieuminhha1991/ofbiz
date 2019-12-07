 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxEditor.js"></script>
 <#assign listEmplType = delegator.findList("EmplPositionType",null,null,null,null,false) !>
 <#assign listStatus = delegator.findList("StatusItem",null,null,null,null,false) !>
 <#assign listTimeJob = delegator.findList("TimeJobRating",null,null,null,null,false) !>
<#assign dataField="[
						{ name: 'jobRatingId', type: 'string'},
						{ name: 'jobRequirement', type: 'string'},
						{ name: 'jobIntent', type: 'string'},
						{ name: 'jobTime', type: 'string'}
					]"/>
<#assign columnlist= " {
						text : '${uiLabelMap.JobRatingId}',dataField : 'jobRatingId',filterable : false,width : '120px'
						},
						{ 
						text : '${uiLabelMap.jobRequirement}' ,dataField : 'jobRequirement',cellsrenderer : 
							function(row,columnfield,value){
							}
						},{
						text : '${uiLabelMap.jobIntent}' , dataField : 'jobIntent'
						},{
						text : '${uiLabelMap.jobTime}' , dataField : 'jobTime',filtertype : 'checkedlist',cellsrenderer : 
							function(row,columnfield,value){
								for(var i =0 ;i< listJob.length;i++){
									if(listJob[i].jobId == value){
										return '<span>'+listJob[i].description+'</span>';
									}
								}
							},createfilterwidget : function(column,columnElement,widget){
								var source = {
									localdata : listJob,
									datatype : \"array\"
								};
								var dataAdapter = new $.jqx.dataAdapter(source,{
										autoBind : true									
									});
								var records = dataAdapter.records;
								records.splice(0,0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								widget.jqxDropDownList({selectedIndex : 0,source : records,displayMember : 'description' , valueMember : 'jobId',dropDownWidth : 250,
									renderer : function(column,label,value){
										for(var i =0 ;i< listJob.length;i++){
												if(listJob[i].jobId == value){
													return '<span>'+listJob[i].description+'</span>';
												}
											}
										return value;
									}								
								});
								widget.jqxDropDownList('checkAll');
							}
						}" />
<#assign rowdetailstemplateAdvance = "<div style='margin-left : 10px;'><div class='jobRequirement'></div></div>"/>						
<@jqGrid filtersimplemode="true" rowdetailsheight="200" rowdetailstemplateAdvance=rowdetailstemplateAdvance initrowdetails="true" initrowdetailsDetail="initrowdetails" filterable="true" addrefresh="true" showtoolbar="true" addType="popup" alternativeAddPopup="alterpopupWindow" dataField=dataField columnlist=columnlist  clearfilteringbutton="true" addrow="true" deleterow="true"
		 url="jqxGeneralServicer?sname=JQgetListJobRating&standardRatingId=${parameters.standardRatingId}" autoheight="true"
		 createUrl="jqxGeneralServicer?sname=createJobRating&jqaction=C" addColumns="jobIntent;jobTime;jobRequirement;standardRatingId[${parameters.standardRatingId?if_exists}]"
		 removeUrl="jqxGeneralServicer?sname=deleteJobRating&jqaction=D" deleteColumn="jobRatingId"	 
		 />
		 
		 
		 
<script language="JavaScript" type="text/javascript">
		var data = '';
		var stt = [
			<#list listStatus as st>
					{
						sttId : "${st.statusId?if_exists}",
						description : "${StringUtil.wrapString(st.description?if_exists)}"
					},
			</#list>	
		];
		var listJob = [
			<#list listTimeJob as job>
					{
						jobId : "${job.timeJobRatingId?if_exists}",
						description : "${StringUtil.wrapString(job.description?if_exists)}"
					},
			</#list>
		];
</script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 					
<div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
    	<form id="formAdd" class="form-horizontal">
        	<div class="no-left-margin control-group">
    			<label class="control-label asterisk">${uiLabelMap.jobRequirement}</label>
    			<div class="controls">
    				<div id="jobRequirementAdd"></div>
    			</div>
    		</div>
    		<div class="no-left-margin control-group">
    			<label class="control-label asterisk">${uiLabelMap.jobIntent}</label>
    			<div class="controls">
    			<div id="jobIntentAdd"></div>
    			</div>
    		</div>
    		<div class="no-left-margin control-group">
    			<label class="control-label asterisk">${uiLabelMap.jobTime}</label>
    			<div class="controls">
    			<div id="jobTimeAdd"></div>
    			</div>
    		</div>
    		<div class="no-left-margin control-group">
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
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	$("#alterpopupWindow").jqxWindow({
        width: 700, height : 500,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel")  ,initContent : function(){
        	 $('#jobRequirementAdd').jqxEditor({
		    	width : '400px',
		    	height : '150px',
		    	theme : 'olbius'
		    });
		     $('#jobIntentAdd').jqxEditor({
		    	width : '400px',
		    	height : '150px',
		    	theme : 'olbius'
		    });
        }    
    });
   
    $("#jobTimeAdd").jqxDropDownList({source: listJob, width: 220 , displayMember:"description",autoDropDownHeight : true, selectedIndex: 0 ,valueMember: "jobId"});
	$("#alterSave").jqxButton({theme: theme});
	$("#alterCancel").jqxButton({theme: theme});
	//init validate 
	  $('#formAdd').jqxValidator({
                rules: [
	                       { input: '#jobRequirementAdd', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}', action: 'blur', rule: function(){
	                       		var valRequirement = $('#jobRequirementAdd').jqxEditor('val');
	                       		var str = valRequirement.substr(5,valRequirement.length - 11);
	                       		var validate = valRequirement != null && (str.length > 1)  && valRequirement !=='undefined';
	                       		return validate;
	                       	}
	                       },
	                       { input: '#jobIntentAdd', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}', action: 'blur', rule:function(){
	                       		var valjobIntent = $('#jobIntentAdd').jqxEditor('val');
	                       		var str = valjobIntent.substr(5,valjobIntent.length - 11);
	                       		return valjobIntent != null && (str.length > 1) && valjobIntent !=='undefined';
	                       	}}
                       ]
           			 });
    // update the edited row when the user clicks the 'Save' button.
    $("#alterSave").click(function () {
    	if(!$('#formAdd').jqxValidator('validate')){$('#jobRequirementAdd').show();$('#jobIntentAdd').show();return false;}
    });
    $("#alterCancel").click(function () {
      	$('#jobRequirementAdd').jqxEditor('val','');
 		$('#jobIntentAdd').jqxEditor('val','');
    });
    $('#formAdd').on('validationSuccess', function (event) {
			    		row = { 
			        		jobRequirement:$('#jobRequirementAdd').jqxEditor('val'),
			        		jobIntent:$('#jobIntentAdd').jqxEditor('val'),
			        		jobTime : $('#jobTimeAdd').val()
			        	  };
					   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
				        // select the first row and clear the selection.
				        $("#jqxgrid").jqxGrid('clearSelection');                        
				        $("#jqxgrid").jqxGrid('selectRow', 0);  
			        $("#alterpopupWindow").jqxWindow('close');  
			        $('#jobRequirementAdd').jqxEditor('val','');
			          $('#jobIntentAdd').jqxEditor('val','');
			        
 });
 //init row details
	var initrowdetails = function (index, parentElement, gridElement, datarecord) {
		var data = new Array();
		data  = [
			{
			"jobRatingId" : datarecord.jobRatingId,
			"jobRequirement"  : datarecord.jobRequirement,
			"jobIntent" : datarecord.jobIntent,
			"jobTime" : datarecord.jobTime
			}		
		];
			var parent = $($(parentElement).children()[0]);
			parent.css({
				margin: '14px 10px 20px',
				'padding-top': '10px',
				width: '90%',
				height: '70%',
				overflow: 'auto'
			});
			parent.addClass('boder-all-profile');
			var jobRequirement = null;
			jobRequirement = parent.find('.jobRequirement');
			var container = $('<div style="margin : 5px;width : 100%;"></div>');
			jobRequirement.append(container);
			var leftColumn = $('<div style="float:left;width :50%"></div>');
			var rightColumn = $('<div style="float:right;width :50%"></div>');
			var jobRating = $('<div style="width : 100%"><span style="color:red;">${uiLabelMap.JobRatingId}</span>: '+ datarecord.jobRatingId+'</div><hr>');
			var jobRq = $('<div><span style="color:red;">${uiLabelMap.jobRequirement}</span>: <span>'+ datarecord.jobRequirement+'</span></div>');
			var jobIntent = $('<div><span style="color:red;">${uiLabelMap.jobIntent}</span>: '+ datarecord.jobIntent+'</div>');
			var jobTime = $('<div style="margin : 5px;"><span style="color:red;">${uiLabelMap.jobTime}</span>: '+ datarecord.jobTime+'</div>');
			container.append(jobRating);
			container.append(leftColumn);
			container.append(rightColumn);
			leftColumn.append(jobRq);
			rightColumn.append(jobIntent);
		};	
</script>
