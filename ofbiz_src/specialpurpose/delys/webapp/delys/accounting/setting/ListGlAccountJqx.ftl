<#assign getAlGlAccount="getAll" />
<script>
	var initDropDown = function(dropdown,grid,config){
		GridUtils.initDropDownButton({url : 'getListGLAccountOACsData&getAlGlAccount=${getAlGlAccount?if_exists}',autoshowloadelement : true,width : config.wgrid,filterable : true,source : {pagesize : 5,cache  : false},dropdown : {width : config.wbt, dropDownHorizontalAlignment: true}},
		[
			{name : 'glAccountId',type : 'string'},
			{name : 'accountName',type : 'string'},
			{name : 'accountCode',type : 'string'}
		], 
		[
			{text : '${uiLabelMap.accountCode}',datafield : 'accountCode',width : '30%'},
			{text : '${uiLabelMap.accountName}',datafield : 'accountName'}
		]
		, null, grid,dropdown,'glAccountId');
	}
</script>
<#assign dataField="[{ name: 'glAccountId', type: 'string' },
               		{ name: 'glAccountTypeId', type: 'string' },
               		{ name: 'glAccountClassId', type: 'string' },
               		{ name: 'glResourceTypeId', type: 'string' },
               		{ name: 'glXbrlClassId', type: 'string' },
               		{ name: 'glTaxFormId', type: 'string' },
               		{ name: 'parentGlAccountId', type: 'string' },
                	{ name: 'accountCode', type: 'string' },
                	{ name: 'codeParent', type: 'string' },
                	{ name: 'accountName', type: 'string' },
                	{ name: 'description', type: 'string' },
                	{ name: 'productId', type: 'string' },
                	{ name: 'externalId', type: 'string' },
                	{ name: 'postedBalance', type: 'string' }]"/>
<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_glAccountId}', dataField: 'glAccountId', width: 170, editable:false, hidden: true},
					 { text: '${uiLabelMap.FormFieldTitle_glAccountTypeId}', dataField: 'glAccountTypeId', width: 200, columntype: 'dropdownlist', hidden: true,
					 	createeditor: function (row, column, editor) {
                            editor.jqxDropDownList({source: dataGLAT, displayMember:\"glAccountTypeId\", valueMember: \"glAccountTypeId\",
                            renderer: function (index, label, value) {
			                    var datarecord = dataGLAT[index];
			                    return datarecord.description;
			                } 
                        }); 
					 }},
					 { text: '${uiLabelMap.FormFieldTitle_glAccountClassId}', dataField: 'glAccountClassId', width: 150, columntype: 'dropdownlist', hidden: true,
					 	createeditor: function (row, column, editor) {
                            editor.jqxDropDownList({source: dataGLAC, displayMember:\"glAccountClassId\", valueMember: \"glAccountClassId\",
                            renderer: function (index, label, value) {
			                    var datarecord = dataGLAC[index];
			                    return datarecord.description;
			                } 
                        }); 
					 }},
					 
					 { text: '${uiLabelMap.FormFieldTitle_glResourceTypeId}', dataField: 'glResourceTypeId', width: 180, columntype: 'dropdownlist', hidden: true,
					 	createeditor: function (row, column, editor) {
                            editor.jqxDropDownList({source: dataGRT, displayMember:\"glResourceTypeId\", valueMember: \"glResourceTypeId\",
                            renderer: function (index, label, value) {
			                    var datarecord = dataGRT[index];
			                    return datarecord.description;
			                } 
                        }); 
					 }},
              		 { text: '${uiLabelMap.glTaxFormAccountId}', dataField: 'glTaxFormId', width: 400, columntype: 'dropdownlist',					 	
					 	cellsrenderer: function(row, colum, value)
                        {
                        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
                        	for(i=0; i<dataTFAI.length;i++){
                        		if(dataTFAI[i].glTaxFormId==value){                        			
									return '<span>' + dataTFAI[i].description + '</span>';
                    			}
                        	}
                        	return \"<span class='custom-style-word'>\" + value + \"</span>\";
                        },
					 	createeditor: function (row, column, editor) {
					 	var data = $('#jqxgrid').jqxGrid('getrowdata',row);
                            editor.jqxDropDownList({source: dataTFAI,displayMember:\"description\", valueMember: \"glTaxFormId\",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'
                        });
					 }},					 
              		 { text: '${uiLabelMap.FormFieldTitle_parentGlAccountId}', dataField: 'parentGlAccountId', width: 400,columntype: 'template',
					 	createeditor : function(row,cellvalue,editor){
					 	var container = $('<div id=\"glAccountId\"><div id=\"jqxgridGlAccount\"></div></div>');
					 	editor.append(container);
					 	editor.jqxDropDownButton('setContent',cellvalue);
					 	initDropDown($('#glAccountId'),$('#jqxgridGlAccount'),{wgrid : 400,wbt : 400});
					 },geteditorvalue: function (row, cellvalue, editor) {																						
					 		 editor.jqxDropDownButton(\"close\");
	                           var ini = $('#jqxgridGlAccount').jqxGrid('getselectedrowindex');
	                            if(ini != -1){
		                            var item = $('#jqxgridGlAccount').jqxGrid('getrowdata', ini);
		                            var selectedPro = item.glAccountId;
		                            return selectedPro;	
	                            }
	                            return cellvalue;
							},
						cellsrenderer  : function(row){
							var data = $('#jqxgrid').jqxGrid('getrowdata',row);
							if(typeof(data) != 'undefined'){
							var name = data.codeParent ? data.codeParent :   ''; 
								return '<span>' + name+ '</span>';								
							}
						return '';
						}
					},
              		 { text: '${uiLabelMap.accountCode}', dataField: 'accountCode', width: 140 ,editable : false},
              		 { text: '${uiLabelMap.accountName}', dataField: 'accountName', filterable : true,editable : false,width: 300 ,cellsrenderer : function(row){
              		 	var data = $('#jqxgrid').jqxGrid('getrowdata',row);
              		 	return '<span class=\"custom-style-word\">' + data.accountName+ '</span>';
              		 }},
              		 { text: '${uiLabelMap.description}', dataField: 'description', width: 200 },
              		 { text: '${uiLabelMap.FormFieldTitle_postedBalance}', dataField: 'postedBalance', editable:false ,width : '15%'}"/>
<@jqGrid url="jqxGeneralServicer?sname=JQGetListChartOfAccount" defaultSortColumn="glAccountId" columnlist=columnlist 
			  dataField=dataField showtoolbar="true" clearfilteringbutton="true" editable="true" addrefresh="true"
			  addColumns="externalId;productId;description;accountName;accountCode;glTaxFormId;parentGlAccountId;glXbrlClassId;glResourceTypeId;glAccountClassId;glAccountTypeId;glAccountId"
			  createUrl="jqxGeneralServicer?jqaction=C&sname=createGlAccount" alternativeAddPopup="alterpopupWindow" addrow="true" addType="popup"
			  updateUrl="jqxGeneralServicer?jqaction=U&sname=updateGlAccount" editColumns="externalId;productId;description;glTaxFormId;accountName;accountCode;parentGlAccountId;glXbrlClassId;glResourceTypeId;glAccountClassId;glAccountTypeId;glAccountId"
			  showlist="glAccountId;glAccountTypeId;glAccountClassId;glResourceTypeId"/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
 <div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
		<div class='row-fluid form-window-content'>
			<form id="formAdd">
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.FormFieldTitle_glAccountId}
    				</div>
    				<div class='span7'>
						<input type="text" id="glAccountId2"></input>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.FormFieldTitle_glAccountTypeId}
    				</div>
    				<div class='span7'>
						<div id="glAccountTypeId2"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.FormFieldTitle_glAccountClassId}
    				</div>
    				<div class='span7'>
						<div id="glAccountClassId2"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.FormFieldTitle_glResourceTypeId}
    				</div>
    				<div class='span7'>
    					<div id="glResourceTypeId2"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.glTaxFormAccountId}
    				</div>
    				<div class='span7'>
    					<div id="glTaxFormId2"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.FormFieldTitle_parentGlAccountId}
    				</div>
    				<div class='span7'>
    					<div id="parentGlAccountId2">
    						<div id="jqxgridGlAccount"></div>
    					</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.accountCode}
    				</div>
    				<div class='span7'>
    					<input type="text" id="accountCode2"></input>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk '>
    					${uiLabelMap.accountName}
    				</div>
    				<div class='span7'>
						<input type="text" id="accountName2"></input>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.description}
    				</div>
    				<div class='span7'>
						<input type="text" id="description2"></input>
    				</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="saveAndContinue" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
					<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>	
	</div>
</div>		

<script src="../images/js/generalUtils.js"></script>
<script type="text/javascript" language="Javascript">
	var dataGLAT = new Array();
	dataGLAT = [
		<#list listGlAccountType as type>
			{
				'glAccountTypeId' : '${StringUtil.wrapString(type.glAccountTypeId?if_exists)}',
				'description' : "<span class='custom-style-word'>[ ${StringUtil.wrapString(type.glAccountTypeId?if_exists)} ]" + ":" + "${StringUtil.wrapString(type.get('description',locale)?if_exists)}</span>"
			
			},
		</#list>
		
	]
	var dataGLAC = new Array();
	dataGLAC = [
		<#list listGlAccountClass as type>
			{
				'glAccountClassId' : '${StringUtil.wrapString(type.glAccountClassId?if_exists)}',
				'description' : "<span class='custom-style-word'>[ ${StringUtil.wrapString(type.glAccountClassId?if_exists)} ]" + ":" + "${StringUtil.wrapString(type.get('description',locale)?if_exists)}</span>"
			},
		</#list>
	]
	
	var dataGRT = new Array();
	dataGRT = [
		<#list listGlResourceType as type>
			{
				'glResourceTypeId' : '${StringUtil.wrapString(type.glResourceTypeId?if_exists)}',
				'description' : "<span class='custom-style-word'>[ ${StringUtil.wrapString(type.glResourceTypeId?if_exists)} ]" + ":" + "${StringUtil.wrapString(type.get('description',locale)?if_exists)}</span>"
			},
		</#list>
	]
	var dataGC = new Array();
	dataGC = [
		<#list listGlXbrlClass as type>
			{
				'glXbrlClassId' : '${StringUtil.wrapString(type.glXbrlClassId?if_exists)}',
				'description' : "<span class='custom-style-word'>[ ${StringUtil.wrapString(type.glXbrlClassId?if_exists)} ]" + ":" + "${StringUtil.wrapString(type.get('description',locale)?if_exists)}</span>"
			},
		</#list>
	]
	
	var dataTFAI = new Array();
	dataTFAI = [
		<#list listTaxFormId as type>
			{
				'glTaxFormId' : '${StringUtil.wrapString(type.enumId?if_exists)}',
				'description' : "<span class='custom-style-word'>[ ${StringUtil.wrapString(type.enumId?if_exists)} ]" + "-" + "${StringUtil.wrapString(type.get('description',locale)?if_exists)}"+ "["  + "${type.enumId?if_exists}"  +  "]</span>"
			},
		</#list>
	]
</script>			      
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	
	var actionPopup = (function(){
			
			var initElement = function(){
				initInput();
			    initjqxWindow();
			    initDropDownList();
			}
			
			var initInput = function(){
				 $('#glAccountId2').jqxInput({width:245, theme:theme, placeHolder: "${StringUtil.wrapString(uiLabelMap.glAccountId)}"});
				//    $('#externalId2').jqxInput({theme:theme, placeHolder: "${StringUtil.wrapString(uiLabelMap.externalId)}"});
				//    $('#productId2').jqxInput({theme:theme, placeHolder: "${StringUtil.wrapString(uiLabelMap.productId)}"});
			    $('#accountName2').jqxInput({width:245,theme:theme, placeHolder: "${StringUtil.wrapString(uiLabelMap.accountName)}"});
			    $('#accountCode2').jqxInput({width:245,theme:theme, placeHolder: "${StringUtil.wrapString(uiLabelMap.accountCode)}"});
			    $('#description2').jqxInput({width:245,theme:theme, placeHolder: "${StringUtil.wrapString(uiLabelMap.description)}"});
			}
			
			var initjqxWindow = function(){
				$("#alterpopupWindow").jqxWindow({
				        width: 550, height : 480,resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme:theme           
				    });
			}
	
			var initDropDownList = function(){
					 $('#glAccountTypeId2').jqxDropDownList({width:250,dropDownWidth : 250,theme:theme,   source: dataGLAT, displayMember: "description", valueMember: "glAccountTypeId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
					    $('#glAccountClassId2').jqxDropDownList({width:250,dropDownWidth : 250,theme:theme, source: dataGLAC, displayMember: "description", valueMember: "glAccountClassId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
					    $('#glResourceTypeId2').jqxDropDownList({theme:theme, source: dataGRT,width:250,dropDownWidth : 250, displayMember: "description", valueMember: "glResourceTypeId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
					//    $('#glXbrlClassId2').jqxDropDownList({theme:theme,   source: dataGC,width:250,dropDownWidth : 250, displayMember: "description", valueMember: "glXbrlClassId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
					    //$('#parentGlAccountId2').jqxDropDownList({theme:theme, source: dataGLA,width:250,dropDownWidth : 250, displayMember: "description", valueMember: "glAccountId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
					    $('#glTaxFormId2').jqxDropDownList({theme:theme, source: dataTFAI,width:250,dropDownWidth : 250, displayMember: "description", valueMember: "glTaxFormId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
						initDropDown($('#parentGlAccountId2'),$('#jqxgridGlAccount'),{wgrid : 400,wbt : 250});
			}
			
			var save = function(){
				var row;
				        row = {
				        		glAccountId:$('#glAccountId2').val(),
				        		glAccountTypeId: $('#glAccountTypeId2').val(),
				        		glResourceTypeId: $('#glResourceTypeId2').val(),
				        		//glXbrlClassId: $('#glXbrlClassId2').val(),
				        		parentGlAccountId: $('#parentGlAccountId2').val(),
				        		glTaxFormId: $('#glTaxFormId2').val(),
				        		accountCode: $('#accountCode2').val(),
				        		accountName: $('#accountName2').val(),
				        		description: $('#description2').val(),
				        		//productId: $('#productId2').val(),
				        		//externalId: $('#externalId2').val(),
				        		glAccountClassId:$('#glAccountClassId2').val()              
				        	  };
					   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
			}
			/*clear form*/
			var clear = function(){
				clearInput();
				clearDropdown();
			}
			var clearInput = function(input){
				var inputArr = $('input[role=textbox]');
				for(var key in inputArr){
					if(typeof(inputArr[key].id) != 'undefined' && inputArr[key].id){
						$('#'+inputArr[key].id).jqxInput('val','');
					}
				}
			}
			
			var clearDropdown = function(){
				$('#glAccountTypeId2').jqxDropDownList('clearSelection');
				$('#glAccountClassId2').jqxDropDownList('clearSelection');
				$('#glResourceTypeId2').jqxDropDownList('clearSelection');
				$('#parentGlAccountId2').jqxDropDownButton('val','');
				$('#glTaxFormId2').jqxDropDownList('clearSelection');
			}
			
			/*init event use*/
			var bindEvent = function(){
				 // update the edited row when the user clicks the 'Save' button.
			    $('#alterpopupWindow').on('close', function (event) {
			    	$('#formAdd').jqxValidator('hide');
			    });
			    
			    $("#save").click(function () {
			    	if($('#formAdd').jqxValidator('validate')){
			    		save();
			    		$("#alterpopupWindow").jqxWindow('close');
			    	}else{
			    		return;
			    	}
			    });
			    
			    $('#saveAndContinue').click(function(){
			    	if($('#formAdd').jqxValidator('validate')){
			    		save();
			    		clear();
			    	}else{
			    		return;
			    	}
			    });
			    
			    $('#alterpopupWindow').on('close',function(){
			    	clear();
			    	$('#formAdd').jqxValidator('hide');
			    })
			}
			
			var initRules = function(){
				$('#formAdd').jqxValidator({
				        rules: [
				                   { input: '#glAccountId2', message: '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}', action: 'change', rule: 'required' },
				               		{ input: '#glAccountTypeId2', message: '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}', action: 'change, close', rule: function(input){
										var val = input.val();
										if(!val) return false;
										return true;
										} },
				               		{ input: '#glTaxFormId2', message: '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}', action: 'change, close', rule: function(input){
										var val = input.val();
										if(!val) return false;
										return true;
										} },
				               		{ input: '#accountCode2', message: '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}', action: 'change', rule: 'required' },
									{ input: '#accountName2', message: '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}', action: 'change', rule: 'required' }
				               ]
				    });
			}
	
		return {
				init : function(){
					initElement();
					bindEvent();
					initRules();
				}
			}
	}())
	
	$(document).ready(function(){
		actionPopup.init();
	});
   	
</script>            	