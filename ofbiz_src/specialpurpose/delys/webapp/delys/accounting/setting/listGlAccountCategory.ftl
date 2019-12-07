<script type="text/javascript" language="Javascript">
	
	var dataGLACT = new Array();
	dataGLACT = [
		<#if listGlAccountCategoryType?exists && listGlAccountCategoryType?has_content>
			<#list listGlAccountCategoryType as type>
				{
					'glAccountCategoryTypeId' : '${type.glAccountCategoryTypeId?if_exists}',
					'description' : "${StringUtil.wrapString(type.get("description",locale))?default("")}"
				},
			</#list>
		</#if>
	]
</script>
<script src="/delys/images/js/generalUtils.js"></script>
<#assign dataField="[{ name: 'glAccountCategoryId', type: 'string' },
						 { name: 'glAccountCategoryTypeId', type: 'string'},
						 { name: 'description', type: 'string' }
						 ]
						 "/>
<#assign columnlist="{ text: '${uiLabelMap.GlAccountCategoryID}', datafield: 'glAccountCategoryId', width: 250,editable : false},
					 { text: '${uiLabelMap.FormFieldTitle_glAccountCategoryTypeId}', datafield: 'glAccountCategoryTypeId', width: 250, columntype: 'dropdownlist',
					 		createeditor: function (row, column, editor) {
                            editor.jqxDropDownList({source: dataGLACT, displayMember:\"glAccountCategoryTypeId\", valueMember: \"glAccountCategoryTypeId\",
                            renderer: function (index, label, value) {
			                    var datarecord = dataGLACT[index];
			                    return datarecord.description;
			                } 
                        	}); 
					 	},cellsrenderer : function(row){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata',row);
					 		if(typeof(data) != 'undefined'){
					 			for(var k in dataGLACT){
					 				if(dataGLACT[k].glAccountCategoryTypeId == data.glAccountCategoryTypeId){
					 					var des = dataGLACT[k].description ? dataGLACT[k].description : '';
					 					return '<span>'  +  des +  '</span>';	
					 				}
					 			}
					 			
					 		}
					 	}},
					 { text: '${uiLabelMap.description}', datafield: 'description'}
                      "/>		
 <script src="/delys/images/js/filterDate.js"></script>                     
 <#include "component://delys/webapp/delys/accounting/rowdetail/initRowDetailGlAccountMember.ftl"/>
<@jqGrid url="jqxGeneralServicer?sname=JQListGlAccountCategory" dataField=dataField columnlist=columnlist initrowdetails="true" 
		 id="jqxgrid" filtersimplemode="true" showtoolbar="true" initrowdetailsDetail=initrowdetailsDetail
		 editable="true"  editrefresh="true" addrefresh="true"
		 addrow="true" addType="popup" alternativeAddPopup="alterpopupWindow" 
		 createUrl="jqxGeneralServicer?jqaction=C&sname=createGlAccountCategory"
		 addColumns="description;glAccountCategoryTypeId" clearfilteringbutton="true"
		 updateUrl="jqxGeneralServicer?jqaction=U&sname=updateGlAccountCategory" 
		 editColumns="glAccountCategoryId;glAccountCategoryTypeId;description"
 /> 
 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
 <div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
		<div class='row-fluid form-window-content'>
			<form id="formAdd">
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.FormFieldTitle_glAccountCategoryTypeId}
    				</div>
    				<div class='span7'>
    					<div id="glAccountCategoryTypeID"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.description}
    				</div>
    				<div class='span7'>
						<input id="descriptionAdd" ></input>
    				</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelCategory" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="saveAndContinueCategory" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
					<button id="saveCategory" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>	
	</div>
</div>	
 
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
    
	var action = (function(){
		 var initElement = function(){
		 	$('#glAccountCategoryTypeID').jqxDropDownList({theme:theme,autoDropDownHeight : true,width:250,height : 25,  source: dataGLACT, displayMember: "description", valueMember: "glAccountCategoryTypeId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    $('#descriptionAdd').jqxInput({width:245,height : 25,theme:theme, placeHolder: "${StringUtil.wrapString(uiLabelMap.description)}"} );
		    $("#alterpopupWindow").jqxWindow({
		        width: 500,height : 200, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancelCategory"), modalOpacity: 0.7, theme:theme           
		    });
		 }
		 var initRules = function(){
			$("#formAdd").jqxValidator({
					rules : [
						{input : '#glAccountCategoryTypeID',message : '${StringUtil.wrapString(uiLabelMap.CommonRequired?default(''))}',action : 'change,close',
							rule : function(input,commit){
								var value = $('#glAccountCategoryTypeID').jqxDropDownList('val');
								if(!value) return false;
								return true;
							}
						},
						{input : '#descriptionAdd',message : '${StringUtil.wrapString(uiLabelMap.CommonRequired?default(''))}',action : 'blur',
							rule : function(input,commit){
								var value = input.val();
								if(!value) return false;
								return true;
							}
						},
					]
					});
				};	
		var save = function(){
			if(!$("#formAdd").jqxValidator('validate')){return;}
					var row;
			        row = {
			        		glAccountCategoryTypeId: $('#glAccountCategoryTypeID').val(),
			        		description: $('#descriptionAdd').val()
			        	  };
				   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
		      return true;
		}
		
		var clear = function(){
			$('#glAccountCategoryTypeID').jqxDropDownList('clearSelection');
			$('#descriptionAdd').val('');
			$("#formAdd").jqxValidator('hide');
		}
		
		var bindEvent = function(){
			$("#saveCategory").click(function () {
				if(save()) $("#alterpopupWindow").jqxWindow('close');
			});
			$("#saveAndContinueCategory").click(function () {
				if(save()) return;
			});
			
			$('#alterpopupWindow').on('close',function(){
				clear();
			})
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
	})
</script>	                     				