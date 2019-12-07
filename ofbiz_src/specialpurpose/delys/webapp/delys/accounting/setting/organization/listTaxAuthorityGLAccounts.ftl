<script>
var tahArray = new Array();
	tahArray = [	
		<#list taxAuthorityHavingNoGlAccountList as acc>
			<#assign taxAuthPartyId = acc.taxAuthPartyId?if_exists />
			<#assign taxAuthGeoId = acc.taxAuthGeoId?if_exists />
			<#assign partyView = delegator.findOne("PartyNameView", {"partyId" : taxAuthPartyId}, true) !/>
			<#assign geo = delegator.findOne("Geo", {"geoId" : taxAuthGeoId}, true) />	
			{
				'taxAuthPartyId' : '${taxAuthPartyId}' + ';' + '${taxAuthGeoId}',
				<#if partyView?exists>
					'description' : '<span class="custom-style-word">[ ' + '${taxAuthPartyId}'+ ' ]'+'${StringUtil.wrapString(partyView.firstName?if_exists)}' +'${StringUtil.wrapString(partyView.middleName?if_exists)}' +'${StringUtil.wrapString(partyView.lastName?if_exists)}' +'${StringUtil.wrapString(partyView.groupName?if_exists)}' + '-' + '[ ' + '${geo.geoId?if_exists}' + ' ]' + '${StringUtil.wrapString(geo.geoName?if_exists)}</span>'
				<#else>	
					'description' :'<span class="custom-style-word">[ ' +'${taxAuthPartyId?if_exists}'+' ]'+ '-' + '[ ' + '${geo.geoId?if_exists}' + ' ]' + '${StringUtil.wrapString(geo.geoName?if_exists)}</span>'
				</#if>
			},
		</#list>	
		]
	


var listTAGRenderer = function (row, column, value) {
	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	if(data.geoName) return '<span class="custom-style-word">'+data.taxAuthGeoId  +' ['+  data.geoName +']</span>';
    return data.taxAuthGeoId;
}

var listGlAccountOrganizationAndClassRenderer = function (row, column, value) {
	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	if(data.accountName) return '<span class="custom-style-word">'+data.accountCode +'</span>';
    return '';
}
var updateListAfterDel = function(){
	var row = {
			taxAuthPartyId : (data ? (data.split('#;')[0].split('=')[2] + ';' + data.split('#;')[1]) : '')
		}
		updateListTax('delete',row,data.responseMessage);
}
</script>
<#assign dataField="[{ name: 'taxAuthPartyId', type: 'string'},
					 { name: 'taxAuthGeoId', type: 'string'},
					 { name: 'glAccountId', type: 'string'},
					 { name: 'organizationPartyId', type: 'string'},
					 { name: 'geoName', type: 'string'},
					 { name: 'accountName', type: 'string'},
					 { name: 'accountCode', type: 'string'}
					]"/>

<#assign columnlist="{ text: '${uiLabelMap.taxAuthPartyId}', datafield: 'taxAuthPartyId', editable: false, width: 200,cellsrenderer : function(row){
							var data = $('#jqxgrid').jqxGrid('getrowdata',row);
							return '<span class=\"custom-style-word\">' +data.taxAuthPartyId + '</span>';
						}},
					 { text: '${uiLabelMap.taxAuthGeoId}', datafield: 'taxAuthGeoId', editable: false, cellsrenderer:listTAGRenderer,width: 350},
					 { text: '${uiLabelMap.GLAccountId}', datafield: 'glAccountId', columntype: 'template',width : 450, cellsrenderer:listGlAccountOrganizationAndClassRenderer,
					 	createeditor: function (row, column, editor) {
					 		editor.append('<div id=\"jqxgridEditor\"></div>');
					 		action.initDropDown(editor,$(\"#jqxgridEditor\"),{wgrid : 450,wbutton : 450,dropDownHorizontalAlignment  : false });
                        },
                       geteditorvalue : function(row,cellvalue,editor){
			     			editor.jqxDropDownButton(\"close\");
			                   var ini = $('#jqxgridEditor').jqxGrid('getselectedrowindex');
			                    if(ini != -1){
			                        var item = $('#jqxgridEditor').jqxGrid('getrowdata', ini);
			                        var selectedPro = item.glAccountId;
			                        return selectedPro;	
			                    }
			                    return cellvalue;
			         	}},
			         	{text : '${uiLabelMap.accountName}',datafield : 'accountName',editable : false}
					"/>
					

<@jqGrid  filtersimplemode="true" editrefresh="true" deletesuccessfunction=updateListAfterDel addType="popup" dataField=dataField columnlist=columnlist addrefresh="true" clearfilteringbutton="true" showtoolbar="true" addrow="true" deleterow="true" editable="true" alternativeAddPopup="alterpopupWindow"
		 url="jqxGeneralServicer?sname=JQListTaxAuthorityGLAccounts&organizationPartyId=${parameters.organizationPartyId}"
		 removeUrl="jqxGeneralServicer?sname=deleteTaxAuthorityGlAccount&jqaction=D&organizationPartyId=${parameters.organizationPartyId}"
		 updateUrl="jqxGeneralServicer?sname=updateTaxAuthorityGlAccount&jqaction=U&organizationPartyId=${parameters.organizationPartyId}"
		 createUrl="jqxGeneralServicer?sname=createTaxAuthorityGlAccount&jqaction=C&organizationPartyId=${parameters.organizationPartyId}"
		 editColumns="taxAuthPartyId;taxAuthGeoId;glAccountId;organizationPartyId[${parameters.organizationPartyId}]"
		 deleteColumn="taxAuthPartyId;taxAuthGeoId;glAccountId;organizationPartyId[${parameters.organizationPartyId}]"
		 addColumns="taxAuthPartyId;taxAuthGeoId;glAccountId;organizationPartyId[${parameters.organizationPartyId}]" 
		 />
	 
<div id="alterpopupWindow" class ='hide'>
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
    	<div class='row-fluid form-window-content'>
			<form id="formAdd">
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.taxAuthPartyId}
    				</div>
    				<div class='span7'>
						<div id="taxAuthPartyGeoIdAdd">
 						</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.GLAccountId}
    				</div>
    				<div class='span7'>
						<div id="glAccountIdAdd">
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

<script src="/delys/images/js/generalUtils.js"></script>
 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	var rowdataAdd = {};
	function updateListTax(action,row,status){
		if(action == 'delete' && status != 'error'){
			var data = {
					taxAuthPartyId : row.taxAuthPartyId ?  row.taxAuthPartyId : '',
					description : getDescription(row.taxAuthPartyId)
				}
			tahArray.splice(0,0,data);
			$('#taxAuthPartyGeoIdAdd').jqxDropDownList('source',tahArray);
		}
	};	
	
	function getDescription(id){
		var des;
		if(id) {
				$.ajax({
					url : 'getDescriptionTax',
					data : {
						id : id
					},
					async : false,
					datatype : 'json',
					type : 'POST',
					success : function(response){
						des = response.description;
					}
				})
			};
			return des;
		}
	
	var action = (function(){
		var initElement = function(){
			initDropDown($('#glAccountIdAdd'),$('#jqxgridGlAccount'),{wgrid : 450, wbutton : 250,dropDownHorizontalAlignment : true});
			 //$('#glAccountIdAdd').jqxDropDownList({ source: gaoaData, displayMember: "description", valueMember: "glAccountId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    $('#taxAuthPartyGeoIdAdd').jqxDropDownList({ dropDownWidth : 250,width  :250,source: tahArray,autoDropDownHeight : true, displayMember: "description", valueMember: "taxAuthPartyId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    
		    $("#alterpopupWindow").jqxWindow({
		        width: 500,height : 180, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme:theme           
		    });
		}
		
		var initDropDown = function(dropdown,grid,configWidth){
			GridUtils.initDropDownButton({url : 'getListGLAccountOACsData',autoshowloadelement : true,width : configWidth.wgrid,filterable : true,source : {pagesize : 5,cache  : false},dropdown : {width : configWidth.wbutton, dropDownHorizontalAlignment : configWidth.dropDownHorizontalAlignment}},
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
		
		var initRules = function(){
			$('#formAdd').jqxValidator({
				rules : [
					{input : '#taxAuthPartyGeoIdAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(input){
						var val = input.jqxDropDownList('val');
						if(!val) return false;
						return true;
					}},
					{input : '#glAccountIdAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(input){
						var val = input.jqxDropDownButton('val');
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
		    	var taxAuthPartyGeoIdAdd = $('#taxAuthPartyGeoIdAdd').val();
				var temp = taxAuthPartyGeoIdAdd.split(";");
		    	var taxAuthPartyId = temp[0];
		    	var taxAuthGeoId = temp[1];
		        row = { 
		        		taxAuthPartyId:taxAuthPartyId,
		        		taxAuthGeoId:taxAuthGeoId,
		        		glAccountId:$('#glAccountIdAdd').jqxDropDownButton('val')           
		        	  };
		      	rowdataAdd = row;
			   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
			   $("#jqxgrid").jqxGrid('clearSelection');                        
      			$("#jqxgrid").jqxGrid('selectRow', 0);  
		        $("#alterpopupWindow").jqxWindow('close');
		    });
		    
		    $('#alterpopupWindow').on('close',function(){
		    	 $('#taxAuthPartyGeoIdAdd').jqxDropDownList('clearSelection');
		    	  $('#glAccountIdAdd').jqxDropDownButton('val','');
		    });
		    
		    //listener tahArray change and update dropDownList source
			$('#jqxgrid').on('myEvent',function(handlerObj,param){
				if(param == 'delete'){
					
				}else {
					var obj = JSON.parse(param);
					if(rowdataAdd && obj){
						if(obj.results.responseMessage == 'success'){
							$.each(tahArray,function(index){
								var arrTmp = tahArray[index].taxAuthPartyId.split(";");
								if(arrTmp[0] == rowdataAdd['taxAuthPartyId']){
									tahArray.splice(index,1);
									$('#taxAuthPartyGeoIdAdd').jqxDropDownList({ source: tahArray});
									return false;
								}
							})
						}
					}
				}
			})
		}
		
		
		return {
			initDropDown : initDropDown,
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
		 
