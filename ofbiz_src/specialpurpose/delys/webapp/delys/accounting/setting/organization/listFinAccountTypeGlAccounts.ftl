<entity-engine-xml><script src="../images/js/generalUtils.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" language="Javascript">
	var initDropDown = function(dropdown,grid,config1,config2){
	GridUtils.initDropDownButton({url : 'getListGLAccountOACsData',autoshowloadelement : true,width : config1,filterable : true,source : {pagesize : 5,cache  : false},dropdown : {width : config2, dropDownHorizontalAlignment: true}},
	[
		{name : 'glAccountId',type : 'string'},
		{name : 'accountCode',type : 'string'},
		{name : 'accountName',type : 'string'}
	], 
	[
		{text : '${uiLabelMap.accountCode}',datafield : 'accountCode',width : '30%'},
		{text : '${uiLabelMap.accountName}',datafield : 'accountName'}
	]
	, null, grid,dropdown,'glAccountId');
	}
	var dataGLOAC = new Array();
	dataGLOAC = [
		<#list listGlAccountOrganizationAndClass as acc>
			{
				'glAccountId' : '${acc.glAccountId?if_exists}',
				'description' : "${StringUtil.wrapString(acc.accountName?default(''))}[${StringUtil.wrapString(acc.accountCode?default(''))}]"
			},
		</#list>	
		]
		
	var listlistGlAccountOrganizationAndClassRender = function (row, column, value) {
		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	    for(i=0;i < dataGLOAC.length; i++){
	    	if(dataGLOAC[i].glAccountId == data.glAccountId){
	    		return "<span>" + dataGLOAC[i].description + "</span>";
	    	}
	    }
	    return "";
	}
	
	var dataFLAT = new Array();
		dataFLAT = [
		<#list finAccountTypes as acc>
			{
				'finAccountTypeId' : '${acc.finAccountTypeId?if_exists}',
				'description' : "<span class='custom-style-word'>${StringUtil.wrapString(acc.get('description',locale)?default(''))}</span>"
			},
		</#list>	
		]
</script>

<#assign dataField="[{ name: 'finAccountTypeId', type: 'string' },
               		{ name: 'organizationPartyId', type: 'string' },
               		{ name: 'glAccountId', type: 'string' }
               		]"/>
               		
<#assign columnlist="{ text: '${uiLabelMap.AccountingFinAccountTypeGlAccount}', datafield: 'finAccountTypeId',filtertype : 'checkedlist',editable : false,cellsrenderer : function(row){
						var data = $('#jqxgrid').jqxGrid('getrowdata',row);
						for(var i = 0 ;i < dataFLAT.length; i++){
							if(dataFLAT[i].finAccountTypeId == data.finAccountTypeId){
								return '<span>' + dataFLAT[i].description + '</span>';
							}	
						}
						return data.finAccountTypeId;
					},createfilterwidget : function(column,columnElement,widget){
					    	var source = {
					    		localdata : dataFLAT,
					    		datatype : 'array'
					    	};
					    	var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
					    	var uniRecords = filterBoxAdapter.records;
					    	uniRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
					    	widget.jqxDropDownList({source: uniRecords, displayMember: 'description', valueMember: 'finAccountTypeId',placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}',renderer: function (index, label, value) 
												{
													for(i=0;i < dataFLAT.length; i++){
														if(dataFLAT[i].finAccountTypeId == value){
															return dataFLAT[i].description;
														}
													}
												    return value;
												}});
					    }},					 
					    	{ text: '${uiLabelMap.accountCode}', dataField: 'glAccountId', columntype: 'template', width: '600',
				         	cellsrenderer: function(row, columns, value){
				         		var data = $('#jqxgrid').jqxGrid('getrowdata',row);
								for(i=0;i < dataGLOAC.length; i++){
									if(dataGLOAC[i].glAccountId == value){
										return '<span>' + dataGLOAC[i].description + '</span>';
									}
								}
				         		return '';
				         	},createeditor : function(row, cellvalue, editor, celltext, cellwidth, cellheight){
				         		editor.append('<div id=\"jqxgridEditGlAccount\"></div>');
				         		initDropDown(editor,$('#jqxgridEditGlAccount'),600,600);
				         		editor.jqxDropDownButton('setContent',cellvalue);
				         	},geteditorvalue : function(row,cellvalue,editor){
				     			editor.jqxDropDownButton(\"close\");
				                   var ini = $('#jqxgridEditGlAccount').jqxGrid('getselectedrowindex');
				                    if(ini != -1){
				                        var item = $('#jqxgridEditGlAccount').jqxGrid('getrowdata', ini);
				                        var selectedPro = item.glAccountId;
				                        return selectedPro;	
				                    }
				                    return cellvalue;
				         	},
				         },
					 "/>          
<@jqGrid url="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&sname=JQListFinAccountTypeGlAccount" dataField=dataField columnlist=columnlist
		 id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true" 
		 addColumns="finAccountTypeId;glAccountId;organizationPartyId[${parameters.organizationPartyId}]"
		 editable="true"  editrefresh="true" filterable="false"
		 deletesuccessfunction="updateData" functionAfterAddRow="updateData"
		 createUrl="jqxGeneralServicer?jqaction=C&sname=createFinAccountTypeGlAccount" alternativeAddPopup="alterpopupWindow" addrow="true" addType="popup"
		 addColumns="finAccountTypeId;glAccountId;organizationPartyId[${parameters.organizationPartyId}]"
		 updateUrl="jqxGeneralServicer?jqaction=U&sname=updateFinAccountTypeGlAccount"
		 editColumns="finAccountTypeId;glAccountId;organizationPartyId[${parameters.organizationPartyId}]"
		 deleterow="true" removeUrl="jqxGeneralServicer?sname=deleteFinAccountTypeGlAccount&jqaction=D" 
		 deleteColumn="finAccountTypeId;organizationPartyId[${parameters.organizationPartyId}]" 
 />	
 <div id="alterpopupWindow" class ='hide'>
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
    	<div class='row-fluid form-window-content'>
			<form id="formAdd">
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.AccountingFinAccountTypeGlAccount}
    				</div>
    				<div class='span7'>
						<div id="accountTypeId">
							<div id="jqxgridFinGlAccount"></div>
 						</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.GLAccountId}
    				</div>
    				<div class='span7'>
						<div id="GlAccountId">
							<div id="jqxgridGlAccount"></div>
 						</div>
    				</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
    </div>
</div>	

<script type="text/javascript">
	
	var updateData = function(){
		$('#jqxgridFinGlAccount').jqxGrid('updatebounddata');
	};

	var action  = (function(){
		
		var initElement = function(){
			$.jqx.theme = 'olbius';  
			theme = $.jqx.theme;
			$("#alterpopupWindow").jqxWindow({
		        width: 500,height : 180, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme:theme           
		    });
		    //$('#accountTypeId').jqxDropDownList({theme:theme, filterable : true,width : 250,dropDownWidth : 250, source: dataFLAT, displayMember: "description", valueMember: "finAccountTypeId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    //$('#GlAccountId').jqxDropDownList({theme:theme,filterable : true,width : 250,dropDownWidth : 250, source: dataGLOAC, displayMember: "description", valueMember: "glAccountId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});						
			initDropDown($('#GlAccountId'),$('#jqxgridGlAccount'),400,250);
			initDropDownGlTypeNotDf($('#accountTypeId'),$('#jqxgridFinGlAccount'));
		}
		
		var initDropDownGlTypeNotDf = function(dropdown,grid){
			GridUtils.initDropDownButton({url : 'getFinAccountTypeNotGlAccount&organizationPartyId=${parameters.organizationPartyId?if_exists}',autoshowloadelement : true,width : 400,filterable : true,source : {pagesize : 5,cache  : false},dropdown : {width : 250, dropDownHorizontalAlignment: true}},
			[
				{name : 'finAccountTypeId',type : 'string'},
				{name : 'description',type : 'string'}
			], 
			[
				{text : '${uiLabelMap.FormFieldTitle_finAccountTypeId}',datafield : 'finAccountTypeId',width : '30%'},
				{text : '${uiLabelMap.AccountingFinAccountTypeGlAccount}',datafield : 'description'}
			]
			, null, grid,dropdown,'finAccountTypeId');
		}
		
		
		
		
		var initRules = function(){
			$('#formAdd').jqxValidator({
				rules : [
					{input : '#accountTypeId',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(input){
						var val = input.val();
						if(!val) return false;
						return true;
					}},
					{input : '#GlAccountId',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(input){
						var val = input.jqxDropDownButton('val');
						if(!val) return false;
						return true;
					}}
				]
			})
			
		}
		
		var bindEvent = function(){
			$("#save").click(function () {
				if(!$('#formAdd').jqxValidator('validate')){return;}
					var row;
			        row = {
			        		finAccountTypeId: $('#accountTypeId').val(),
			        		glAccountId: $('#GlAccountId').val()
			        	  };
			        
				   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
			       $("#alterpopupWindow").jqxWindow('close');
			});
			
			$("#alterpopupWindow").bind('close',function(){
				$('#accountTypeId').jqxDropDownButton('val','');
				$('#GlAccountId').jqxDropDownButton('val','');
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
		action.init();
	});
    
</script>		      		  
