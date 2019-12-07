<script type="text/javascript">
<#assign listVRGC = delegator.findByAnd("VarianceReason",null,null,false) !>
	var VRGC = new Array();
	VRGC = [
		<#list listVRGC as acc>
			{
				'varianceReasonId' : '${acc.varianceReasonId?if_exists}',
				'description' : "<span class='custom-style-word'>${StringUtil.wrapString(acc.get('description',locale)?default(''))}</span>"
			},
		</#list>	
		];
		
	var dataVR = new Array();
	dataVR = [
		<#list varianceReasons as acc>
			{
				'varianceReasonId' : '${acc.varianceReasonId?if_exists}',
				'description' : "<span class='custom-style-word'>${StringUtil.wrapString(acc.get('description',locale)?default(''))}</span>"
			},
		</#list>	
		]
		
	var dataGAOAC = new Array();
	dataGAOAC = [
		<#list glAccountOrganizationAndClasses as acc>
			{
				'glAccountId' : '${acc.glAccountId?if_exists}',
				'description' : "<span class='custom-style-word'> [${acc.accountCode?if_exists} ]" + " - " +  "${StringUtil.wrapString(acc.accountName?default(''))}</span>"
			},
		</#list>	
		]
</script>

<script type="text/javascript">
	var linkVRrenderer = function (row, column, value) {
		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        for(i=0;i < dataVR.length; i++){
        	if(dataVR[i].varianceReasonId == data.varianceReasonId){
        		return "<span>" + dataVR[i].description + "</span>";
        	}
        }
        return data.varianceReasonId;
    }
    var linkGOACrenderer = function (row, column, value) {
		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        for(i=0;i < dataGAOAC.length; i++){
        	if(dataGAOAC[i].glAccountId == data.glAccountId){
        		return "<span>" + dataGAOAC[i].description + "</span>";
        	}
        }
        return data.glAccountId;
    }
</script>
<#assign dataField="[{ name: 'varianceReasonId', type: 'string' },
					 { name: 'glAccountId', type: 'string'},
					 { name: 'accountCode', type: 'string'},
					 { name: 'accountName', type: 'string'}
					 ]
					 "/>

<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_varianceReasonId}',filtertype : 'checkedlist', datafield: 'varianceReasonId', cellsrenderer:function (row, column, value) {
				var data = $('#jqxgrid').jqxGrid('getrowdata', row);
		        for(i=0;i < VRGC.length; i++){
					if(typeof(data) != 'undefined' && VRGC[i].varianceReasonId == data.varianceReasonId){
						return '<span>' + VRGC[i].description + '</span>';
					}
		        }
		        return '<span>' + data.varianceReasonId + '</span>';
		    },createfilterwidget : function(column,columnElement,widget){
			    	var source = {
			    		localdata : VRGC,
			    		datatype : 'array'
			    	};
			    	var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
			    	var uniRecords = filterBoxAdapter.records;
			    	uniRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			    	widget.jqxDropDownList({source: uniRecords, displayMember: 'description', valueMember: 'varianceReasonId',placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}',renderer: function (index, label, value) 
										{
											for(i=0;i < VRGC.length; i++){
												if(VRGC[i].varianceReasonId == value){
													return VRGC[i].description;
												}
											}
										    return value;
										}});
			   		 }},
			   		 { text: '${uiLabelMap.accountCode}', datafield: 'accountCode'},
			  	 	{ text: '${uiLabelMap.accountName}', datafield: 'accountName',width : '40%'} 
				 "/>
	
<@jqGrid filtersimplemode="true"  addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" alternativeAddPopup="alterpopupWindow" deleterow="true" editable="false"
		 url="jqxGeneralServicer?sname=JQGetListVarianceReasonGlAccounts&organizationPartyId=${parameters.organizationPartyId}" 
		 createUrl="jqxGeneralServicer?sname=createVarianceReasonGlAccount&jqaction=C&organizationPartyId=${parameters.organizationPartyId}" addColumns="glAccountId;varianceReasonId;organizationPartyId[${parameters.organizationPartyId}]"
		 removeUrl="jqxGeneralServicer?sname=removeVarianceReasonGlAccount&jqaction=D&organizationPartyId=${parameters.organizationPartyId}" deleteColumn="varianceReasonId;organizationPartyId[${parameters.organizationPartyId}]"
		 updateUrl="jqxGeneralServicer?sname=updateVarianceReasonGlAccount&jqaction=U&organizationPartyId=${parameters.organizationPartyId}" editColumns="glAccountId;varianceReasonId;organizationPartyId[${parameters.organizationPartyId}]"
 		deletesuccessfunction="updateData" functionAfterAddRow="updateData"		
/>
		 
<div id="alterpopupWindow" class ='hide'>
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
    	<div class='row-fluid form-window-content'>
			<form id="formAdd">
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.FormFieldTitle_varianceReasonId}
    				</div>
    				<div class='span7'>
						<div id="varianceReasonIdPop">
							<div id="jqxgridReason"></div>
 						</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.AccountingGlAccountId}
    				</div>
    				<div class='span7'>
						<div id="glAccountIdPop">
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
<script src="../images/js/generalUtils.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	var rowAdd = {};
	var updateData = function(){
		$('#jqxgridReason').jqxGrid('updatebounddata');
	}
	var action = (function(){
		var initElement = function(){
			 //$('#glAccountIdPop').jqxDropDownList({ filterable : true,dropDownWidth : 250,width  :250,source: dataGAOAC, displayMember: "description", valueMember: "glAccountId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    //$('#varianceReasonIdPop').jqxDropDownList({ filterable : true,source: dataVR,dropDownWidth : 250,width  :250, displayMember: "description", valueMember: "varianceReasonId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		     initDropDownPM($('#varianceReasonIdPop'),$('#jqxgridReason'));
		       initDropDown($('#glAccountIdPop'),$('#jqxgridGlAccount'));
		    $("#alterpopupWindow").jqxWindow({
		        width: 500,height : 180, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme:theme           
		    });
		}
		
		
		var initDropDown = function(dropdown,grid){
			GridUtils.initDropDownButton({url : 'getListGLAccountOACsData',autoshowloadelement : true,width : 400,filterable : true,source : {pagesize : 5,cache  : false},dropdown : {width : 250, dropDownHorizontalAlignment: true}},
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
		
		var initDropDownPM = function(dropdown,grid){
			GridUtils.initDropDownButton({url : 'getVarianceReasonNotGlAccounts&organizationPartyId=${parameters.organizationPartyId}',autoshowloadelement : true,width : 400,filterable : true,source : {pagesize : 5,cache  : false},dropdown : {width : 250, dropDownHorizontalAlignment: true}},
			[
				{name : 'varianceReasonId',type : 'string'},
				{name : 'description',type : 'string'}
			], 
			[
				{text : '${uiLabelMap.FormFieldTitle_varianceReasonId}',datafield : 'varianceReasonId',width : '30%'},
				{text : '${uiLabelMap.CommonDescription}',datafield : 'description'}
			]
			, null, grid,dropdown,'varianceReasonId');
		}
		
		
		var initRules = function(){
			$('#formAdd').jqxValidator({
				rules : [
					{input : '#varianceReasonIdPop',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(input){
						var val = input.val();
						if(!val) return false;
						return true;
					}},
					{input : '#glAccountIdPop',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(input){
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
		        		varianceReasonId:$('#varianceReasonIdPop').val(),
        				glAccountId:$('#glAccountIdPop').val()        
		        	  };
		        rowAdd = row;
			   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
			   $("#jqxgrid").jqxGrid('clearSelection');                        
      			$("#jqxgrid").jqxGrid('selectRow', 0);  
		        $("#alterpopupWindow").jqxWindow('close');
		    });
		    $('#alterpopupWindow').on('close',function(){
		    	 $('#glAccountIdPop').jqxDropDownButton('val','');
		    	  $('#varianceReasonIdPop').jqxDropDownButton('val','');
		    	  $('#jqxgridReason').jqxGrid('clearSelection');
		    	   $('#jqxgridGlAccount').jqxGrid('clearSelection');
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