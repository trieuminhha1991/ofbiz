<script src="../images/js/generalUtils.js"></script> 
<script type="text/javascript" language="Javascript">
	<#assign listCardTypeGl = delegator.findByAnd("Enumeration",null,null,false) !>
	var dataCTGL = [
		<#list listCardTypeGl as acc>
			{
				'enumId' : '${acc.enumId?if_exists}',
				'description' : "<span class='custom-style-word'>${StringUtil.wrapString(acc.enumCode?default(''))}</span>"
			},
		</#list>	
		];
		
	var dataLCT = new Array();
	dataLCT = [
		<#list listCardType as acc>
			{
				'enumId' : '${acc.enumId?if_exists}',
				'description' : "<span class='custom-style-word'> [ ${acc.enumId?if_exists} ]" + " - " +  "${StringUtil.wrapString(acc.enumCode?default(''))}</span>"
			},
		</#list>	
		];
		
		
	var initDropDown = function(dropdown,grid,width1,width2){
			GridUtils.initDropDownButton({url : 'getListGLAccountOACsData',autoshowloadelement : true,width : width1,filterable : true,source : {pagesize : 5,cache  : false},dropdown : {width : width2, dropDownHorizontalAlignment: true}},
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
	
</script>


<#assign dataField="[{ name: 'cardType', type: 'string' },
					 { name: 'glAccountId', type: 'string' },
					 { name: 'accountCode', type: 'string' },
					 { name: 'accountName', type: 'string' }
					 ]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.CardType}', datafield: 'cardType',editable: false, cellsrenderer:function (row, column, value) {
						var data = $('#jqxgrid').jqxGrid('getrowdata', row);
				        for(i=0;i < dataCTGL.length; i++){
							if(typeof(data) != 'undefined' && dataCTGL[i].enumId == data.cardType){
								return '<span>' + dataCTGL[i].description + '</span>';
							}
				        }
				        return '<span>' + data.cardType + '</span>';
				    }},
					 { text: '${uiLabelMap.GLAccountId}', datafield: 'glAccountId', columntype: 'template',width : '400',
					 	createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
					 	editor.append('<div id=\"jqxgridGlAccountEdit\"></div>');
					 	initDropDown(editor,$('#jqxgridGlAccountEdit'),400,400);
                        editor.jqxDropDownButton('setContent',cellvalue); 
                       },geteditorvalue : function(row,cellvalue,editor){
			     			editor.jqxDropDownButton(\"close\");
			                   var ini = $('#jqxgridGlAccountEdit').jqxGrid('getselectedrowindex');
			                    if(ini != -1){
			                        var item = $('#jqxgridGlAccountEdit').jqxGrid('getrowdata', ini);
			                        var selectedPro = item.glAccountId;
			                        return selectedPro;	
			                    }
			                    return cellvalue;
			         	},cellsrenderer : function(row){
			         		var data = $('#jqxgrid').jqxGrid('getrowdata',row);
			         		if(typeof(data) != 'undefined'){
			         			var code = data.accountCode ? data.accountCode : '';
			         			return '<span>' + code+ '</span>';
			         		}
			         		return '';
			         	}
					 },
					 {text : '${uiLabelMap.accountName}',datafield : 'accountName',width : '30%',editable : false}
					"/>
<@jqGrid url="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&sname=JQListCreditCardTypeGlAccount" dataField=dataField columnlist=columnlist
		 deletesuccessfunction="updateData" functionAfterAddRow="updateData"
		 id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true"
		 addrow="true" addType="popup" alternativeAddPopup="alterpopupWindow"
		 createUrl="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&jqaction=C&sname=createCreditCardTypeGlAccount"
		 addColumns="cardType;glAccountId;organizationPartyId[${parameters.organizationPartyId}]"
		 editable="true" editrefresh="true"
		 updateUrl="jqxGeneralServicer?sname=updateCreditCardTypeGlAccount&jqaction=U&organizationPartyId=${parameters.organizationPartyId}" 
		 editColumns="cardType;glAccountId;organizationPartyId[${parameters.organizationPartyId}]"
		 deleterow="true" removeUrl="jqxGeneralServicer?sname=deleteCreditCardTypeGlAccount&jqaction=D" 
		 deleteColumn="cardType;organizationPartyId[${parameters.organizationPartyId}]"
 />			
 <div id="alterpopupWindow" class ='hide'>
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
    	<div class='row-fluid form-window-content'>
			<form id="formAdd">
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.accAccountingCreditCardType}
    				</div>
    				<div class='span7'>
						<div id="CardType">
							<div id="jqxgridCardType"></div>
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
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	var updateData = function(){
		$('#jqxgridCardType').jqxGrid('updatebounddata');
	};
	var action = (function(){
		var initElement = function(){
			 //$('#GlAccountId').jqxDropDownList({ source: dataGAOAC,filterable:true,dropDownWidth : 250,width  :250, displayMember: "description", valueMember: "glAccountId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    //$('#CardType').jqxDropDownList({ source: dataLCT, filterable : true,dropDownWidth : 250,width  :250,displayMember: "description", valueMember: "enumId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    initDropDownGlTypeNotDf($('#CardType'),$('#jqxgridCardType'));
		    initDropDown($('#GlAccountId'),$('#jqxgridGlAccount'),400,250);
		    $("#alterpopupWindow").jqxWindow({
		        width: 500,height : 180, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme:theme           
		    });
		}
		
		
		var initDropDownGlTypeNotDf = function(dropdown,grid){
			GridUtils.initDropDownButton({url : 'jqgetListCreditCardTypeNotGlAccount&organizationPartyId=${parameters.organizationPartyId?if_exists}',autoshowloadelement : true,width : 400,filterable : true,source : {pagesize : 5,cache  : false},dropdown : {width : 250, dropDownHorizontalAlignment: true}},
			[
				{name : 'cardType',type : 'string'},
				{name : 'description',type : 'string'}
			], 
			[
				{text : '${uiLabelMap.accAccountingCreditCardType}',datafield : 'cardType',width : '30%'},
				{text : '${uiLabelMap.CommonDescription}',datafield : 'description'}
			]
			, null, grid,dropdown,'cardType');
		}
		
		
		var initRules = function(){
			$('#formAdd').jqxValidator({
				rules : [
					{input : '#GlAccountId',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(input){
						var val = input.val();
						if(!val) return false;
						return true;
					}},
					{input : '#CardType',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(input){
						var val = input.val();
						if(!val) return false;
						return true;
					}}
				]
			})
			
		}
		
		var bindEvent = function(){
			// update the edited row when the user clicks the 'Save' button.
		    $("#save").click(function () {
		    	if(!$('#formAdd').jqxValidator('validate')){return;}
		    	var row;
		           row = {
		        		cardType:$('#CardType').val(),
        				glAccountId:$('#GlAccountId').val()        
		        	  };
			   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
			   $("#jqxgrid").jqxGrid('clearSelection');                        
      			$("#jqxgrid").jqxGrid('selectRow', 0);  
		        $("#alterpopupWindow").jqxWindow('close');
		    });
		    $('#alterpopupWindow').on('close',function(){
		    	 $('#GlAccountId').jqxDropDownButton('val','');
		    	  $('#CardType').jqxDropDownButton('val','');
		    	  $('#jqxgridGlAccount').jqxGrid('clearSelection');
		    	  $('#jqxgridCardType').jqxGrid('clearSelection');
		    });
		}
		return {
			init : function(){
				initElement();
				bindEvent();
				initRules();
			}
		}
	}());
	
	$(document).ready(function(){
		action.init();
	});
</script>		
 	


</script> 	
