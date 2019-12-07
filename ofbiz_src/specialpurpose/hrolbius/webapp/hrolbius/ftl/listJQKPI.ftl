
 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
 <#assign listEmplType = delegator.findList("EmplPositionType",null,null,null,null,false) !>
 <#assign listStatus = delegator.findList("StatusItem",null,null,null,null,false) !>
 <#assign listDept = delegator.findList("DeptPositionTypeDetail",null,null,null,null,false) !>
 <script type="text/javascript">
 	var listDetailEmplType = [];
	<#assign MapEmplOfManager = Static["com.olbius.util.PartyUtil"].getListEmplPositionInOrgOfManager(delegator,userLogin.partyId) !>
 	<#list MapEmplOfManager.entrySet() as entry>
 			var tmp = {
			 		emplPositionTypeId :  '${entry.key}',
			 		description : '${entry.value}'	
			 	};
		if(tmp){
			listDetailEmplType.push(tmp);
		}	 	
 	</#list>
 	var arrDept = [
		<#list listDept as dp>
 		{
 			emplPosId : '${dp.emplPositionTypeId?if_exists}',
 			deptName : '${StringUtil.wrapString(dp.groupName?default(''))}'
 		},
 		</#list>
 	]
 </script> 		
<#assign dataField="[
						{ name: 'KPIId', type: 'string'},
						{ name: 'description', type: 'string'},
						{ name: 'emplPositionTypeId', type: 'string'},
						{ name: 'fromDate', type: 'date',other : 'Timestamp'},
						{ name: 'thruDate', type: 'date',other : 'Timestamp'},
						{ name: 'jobStatus', type: 'string'}
					]"/>
<#assign columnlist= " {
						text : '${uiLabelMap.KPIId}',	dataField : 'KPIId',width: '100px',editable : false,filterable : false,cellsrenderer : 
							function(row,columnfield,value){
									var data = $(\"#jqxgrid\").jqxGrid(\"getrowdata\", row);
									return '<a href=\"'+ 'StandardRating?emplPositionTypeId=' + data.emplPositionTypeId  +'\">'+ value +'</a>';	
								},cellbeginedit : cellbeginedit
						},
						{ 
						text : '${uiLabelMap.description}' ,dataField : 'description' ,filterable : true,width : '150px',cellbeginedit : cellbeginedit
						},{
						text : '${uiLabelMap.positionEmployee}' , dataField : 'emplPositionTypeId',width : '180px',filtertype : 'checkedlist',columntype : 'dropdownlist',cellsrenderer : 
							function(row,columnfield,value){
									for(var i =0 ;i<ArrEmplType.length; i++){
										if(ArrEmplType[i].emplType == value){
											return '<span>' +ArrEmplType[i].description +'</span>'
										}
									}
							
								},createfilterwidget: function (column, columnElement, widget) {
							  		var sourceOrd =
								    {
								        localdata: listDetailEmplType,
								        datatype: \"array\"
								    };
					   				var filterBoxAdapter = new $.jqx.dataAdapter(sourceOrd,
					                {
					                    autoBind: true
					                });
					                var uniqueRecords = filterBoxAdapter.records;
					   				uniqueRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
					   				
		        					widget.jqxDropDownList({selectedIndex: 0, source: uniqueRecords, displayMember: 'description', valueMember: 'emplPositionTypeId', dropDownWidth: 250,
		        						renderer: function (index, label, value) {
		                    				for(i = 0; i < listDetailEmplType.length; i++){
												if(listDetailEmplType[i].emplPositionTypeId == value){
													return '<span>' + listDetailEmplType[i].description + '</span>';
												}
		                    				}
		                    			return value;
		        					}});
									widget.jqxDropDownList('checkAll');
    						},createeditor : function(row,column,editor){
    							var dataSource = new $.jqx.dataAdapter(listDetailEmplType,{
    								autoBind : true
    							})
    							editor.jqxDropDownList({autoDropDownHeight : true,source : dataSource ,displayMember : 'emplPositionTypeId' , valueMember : 'emplPositionTypeId',selectedIndex : 0,
	    							renderer : function(index,label,value){
	    								var data = 	listDetailEmplType[index];
	    								return data.description;
    							},
								selectionRenderer: function () {
				                     var item = editor.jqxDropDownList('getSelectedItem');
				                     if (item) {
				                      for(var i = 0; i < listDetailEmplType.length; i++){
								             if(item.value == listDetailEmplType[i].emplPositionTypeId){
								              return '<span style=\"margin : 5px;display : block;\" title=' + item.value + '>' + listDetailEmplType[i].description + '</span>';
								             }
				            			}
                    				 }
					                     return '<span>Please Choose:</span>';
					                 }
    							});
    						},cellbeginedit :cellbeginedit
						},{
							text : '${uiLabelMap.Department}',editable : false ,width : '150px',filterable : false,cellsrenderer : 
							function(row,columnfield,value){
								var data = $(\"#jqxgrid\").jqxGrid(\"getrowdata\",row);
								var listDept = '';
								for(var i = 0 ; i < arrDept.length ; i++){
									if(arrDept[i].emplPosId == data.emplPositionTypeId){
										listDept += ' ' + arrDept[i].deptName + '<br>';
									}
								}
								return '<span>' +listDept + '</span>';
							},cellbeginedit : cellbeginedit					
						},
						{
							text : '${uiLabelMap.fromDate}' , dataField : 'fromDate', filtertype : 'range' , cellsformat : 'dd/MM/yyyy', columntype: 'datetimeinput',createeditor : 
							function(row,column,editor){
								editor.jqxDateTimeInput({ formatString: 'dd-MM-yyyy : HH:mm:ss', allowNullDate: true, value: null});
							},validation : function(cell,value){
							 var data = $(\"#jqxgrid\").jqxGrid('getrowdata',cell.row);
							 if(data.thruDate <= value){
							 	return {message : \"${StringUtil.wrapString(uiLabelMap.NotifromDateBiggerthruDate)}\",result : false};
							 }
								return true;
							},cellbeginedit : cellbeginedit
						},{
							text : '${uiLabelMap.thruDate}' ,dataField : 'thruDate',filtertype : 'range',cellsformat : 'dd/MM/yyyy', columntype: 'datetimeinput',createeditor : 
							function(row,column,editor){
								editor.jqxDateTimeInput({ formatString: 'dd-MM-yyyy : HH:mm:ss', allowNullDate: true, value: null});
							},validation : function(cell,value) {
								var data = $(\"#jqxgrid\").jqxGrid('getrowdata',cell.row);
								if(value <= data.fromDate){
									return {message : \"${StringUtil.wrapString(uiLabelMap.NotithruDateSmallerfromDate)}\",result : false};
								}
								return true;
							},cellbeginedit : cellbeginedit
						},{
							text : '${uiLabelMap.jobStatus}',editable : false , dataField : 'jobStatus',width : '150px',filterable: false,cellsrenderer : 
							function(row,columnfield,value){
							var data = $('#jqxgrid').jqxGrid('getrowdata',row);
								for(var i = 0 ;i < stt.length ; i++){
									if(value == stt[i].sttId &&  data.jobStatus == 'AWT_APPROVED'){
										return '<span style=\"color : red;\">' + stt[i].description +'</span>';
									}
									if(value == stt[i].sttId){
										return '<span>' + stt[i].description +'</span>';
									}
								}
							},cellbeginedit : cellbeginedit
						}" />
<@jqGrid filtersimplemode="true" filterable="true" editable="true" addrefresh="true" showtoolbar="true" addType="popup" alternativeAddPopup="alterpopupWindow" dataField=dataField columnlist=columnlist  clearfilteringbutton="true" addrow="true" deleterow="true"
		 url="jqxGeneralServicer?sname=JQgetListKPI" 
		 createUrl="jqxGeneralServicer?sname=createKPI&jqaction=C" addColumns="description;emplPositionTypeId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"
		 removeUrl="jqxGeneralServicer?sname=deleteKPI&jqaction=D" deleteColumn="emplPositionTypeId;KPIId"
		 updateUrl="jqxGeneralServicer?sname=updateKPI&jqaction=U" editColumns="KPIId;description;emplPositionTypeId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"
		 />
<script type="text/javascript">
		var data = '';
		var stt = [
			<#list listStatus as st>
					{
						sttId : "${st.statusId?if_exists}",
						description : "${StringUtil.wrapString(st.description?if_exists)}"
					},
			</#list>	
		];
		var ArrEmplType = [
			<#list listEmplType as type>
					{
						emplType : "${type.emplPositionTypeId?if_exists}",
						description : "${StringUtil.wrapString(type.description?if_exists)}"
					},
			</#list>
		];
		
</script>					
<div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
    	<form id="formAdd" class="form-horizontal">
    		<div class="no-left-margin control-group">
    			<label class="control-label asterisk">${uiLabelMap.description}</label>
    			<div class="controls">
    				<input type="text" id="descriptionAdd"/>
    			</div>
    		</div>
    		<div class="no-left-margin control-group">
    			<label class="control-label asterisk">${uiLabelMap.positionEmployee}</label>
    			<div class="controls">
    				<div id="positionEmployeeAdd"></div>
    			</div>
    		</div>
    		<div class="no-left-margin control-group">
    			<label class="control-label asterisk">${uiLabelMap.fromDate}</label>
    			<div class="controls">
    				<div id="fromDateAdd"></div>
    			</div>
    		</div>
    		<div class="no-left-margin control-group">
    			<label class="control-label asterisk">${uiLabelMap.thruDate}</label>
    			<div class="controls">
    				<div id="thruDateAdd"></div></td>
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
        width: 500, height : 300,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7          
    });
	$("#descriptionAdd").jqxInput({ width: '195px', height: '25px' ,source : data});
	$("#positionEmployeeAdd").jqxDropDownList({source: listDetailEmplType, width: 200 ,autoDropDownHeight : true, displayMember:"description", selectedIndex: 0 ,valueMember: "emplPositionTypeId"});
	$("#fromDateAdd").jqxDateTimeInput({height: '25px', width: 200,  formatString: 'dd-MM-yyyy : HH:mm:ss', allowNullDate: true, value: null});
	$("#thruDateAdd").jqxDateTimeInput({height: '25px', width: 200,  formatString: 'dd-MM-yyyy : HH:mm:ss', allowNullDate: true, value: null});
	$("#fromDateAdd").on("change", function(event){
			var tmp = event.args.date;
			if(tmp != null && tmp !== 'undefined'){
				var date = new Date((tmp.getYear() + 1900), tmp.getMonth(), tmp.getDate());
				$("#thruDateAdd").jqxDateTimeInput('setMinDate', date);
			}
		});
	$("#thruDateAdd").on("change", function(event){
		var tmp = event.args.date;
		if(tmp != null && tmp !== 'undefined'){
			var date = new Date((tmp.getYear() + 1900), tmp.getMonth(), tmp.getDate());
			$("#fromDateAdd").jqxDateTimeInput('setMaxDate', date);
		}
	});
	  $('#formAdd').jqxValidator({
                rules: [
	                       { input: '#descriptionAdd', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}', action: 'keyup, blur', rule: 'required' },
	                       { input: '#fromDateAdd', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}', action: 'blur', rule: function(){
	                       		var fromDate = $('#fromDateAdd').val();
	                       		if(!fromDate){
	                       			return false;
	                       		}
	                       		return true;
	                       } },
	                       { input: '#thruDateAdd', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}', action: 'blur', rule: function(input,commit){
	                       		var thruDate = $('#thruDateAdd').val();
	                       		if(!thruDate || thruDate.length == 0 ){
	                       			return false;
	                       		}
	                       		return true;
	                       } }
                       ]
           			 });
	$("#alterSave").jqxButton({theme: theme});
	$("#alterCancel").jqxButton({theme: theme});
    // update the edited row when the user clicks the 'Save' button.
      $("#alterSave").click(function () {
    	$('#formAdd').jqxValidator('validate');
    });
    var cellbeginedit = function(row,columntype,datafield,value){
    	var data = $('#jqxgrid').jqxGrid('getrowdata',row);
    	var check = false;
		 	<#list MapEmplOfManager.entrySet() as entry>
	 			var tmp = {
				 		emplPositionTypeId :  '${entry.key}',
				 		description : '${entry.value}'	
				 	};
				if(tmp.emplPositionTypeId == data.emplPositionTypeId){
					check = true;
				}	 	
		 	</#list>
    	if(data.jobStatus == 'AWT_APPROVED' || !check) return false;
    }
    $('#formAdd').on('validationSuccess', function (event) {
		 	var fromDate = Utils.formatDateYMD($('#fromDateAdd').jqxDateTimeInput('getDate'));
			var thruDate = Utils.formatDateYMD($('#thruDateAdd').jqxDateTimeInput('getDate'));    
		        row = { 
		        		description:$('#descriptionAdd').val(),
		        		emplPositionTypeId:$('#positionEmployeeAdd').val(),
		        		fromDate:fromDate,
		        		thruDate:thruDate
		        	  };
	   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid").jqxGrid('clearSelection');                        
        $("#jqxgrid").jqxGrid('selectRow', 0);  
        $("#alterpopupWindow").jqxWindow('close');
    });
    $("#alterpopupWindow").on('close',function(){
   		 $('#fromDateAdd').jqxDateTimeInput('val', null);
        $('#thruDateAdd').jqxDateTimeInput('val', null);
        $("#descriptionAdd").jqxInput('val',null);
    });
</script>