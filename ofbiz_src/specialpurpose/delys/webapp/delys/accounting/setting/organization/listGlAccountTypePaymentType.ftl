<script type="text/javascript" language="Javascript">
	<#assign listPM = delegator.findByAnd("PaymentType",null,null,false)/>
	
	var listPM = [
		<#list listPM as pm>
		{
			paymentTypeId : '${pm.paymentTypeId?if_exists}' ,
			description : "<span class='custom-style-word'>${StringUtil.wrapString(pm.get("description",locale)?if_exists)}</span>"
		},
		</#list>
	];	
	var dataPT = new Array();
	dataPT = [
		<#list listPaymentType as acc>
			{
				'paymentTypeId' : '${acc.paymentTypeId?if_exists}',
				'description' : "<span class='custom-style-word'> [ ${StringUtil.wrapString(acc.paymentTypeId?default(''))} ]" + " - " + "${StringUtil.wrapString(acc.get('description',locale)?default(''))}</span>"
			},
		</#list>	
		]
	var dataGLAT = new Array();
	dataGLAT = [
		<#list listGlAccountType as acc>
			{
				'glAccountTypeId' : '${acc.glAccountTypeId?if_exists}',
				'description' : "<span class='custom-style-word'>${StringUtil.wrapString(acc.get('description',locale)?default(''))}</span>"
			},
		</#list>	
		]
</script>


<#assign dataField="[{ name: 'paymentTypeId', type: 'string' },
					 { name: 'glAccountTypeId', type: 'string' }
					 ]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.AccountingPaymentType}', datafield: 'paymentTypeId',filtertype : 'checkedlist',cellsrenderer : function(row){
						var data = $('#jqxgrid').jqxGrid('getrowdata',row);
						if(typeof(data) == 'undefined') return '';
						for(var i = 0 ;i <listPM.length;i++){
							if(listPM[i].paymentTypeId == data.paymentTypeId){
								return '<span>' + listPM[i].description + '</span>';
							}
						}
						return '<span>' + data.paymentTypeId+ '</span>';
					},createfilterwidget : function(column,columnElement,widget){
				    	var source = {
				    		localdata : listPM,
				    		datatype : 'array'
				    	};
				    	var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
				    	var uniRecords = filterBoxAdapter.records;
				    	uniRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
				    	widget.jqxDropDownList({source: uniRecords, displayMember: 'description', valueMember: 'paymentTypeId',placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}',renderer: function (index, label, value) 
											{
												for(i=0;i < listPM.length; i++){
													if(listPM[i].paymentTypeId == value){
														return listPM[i].description;
													}
												}
											    return value;
											}});
				    }},
					 { text: '${uiLabelMap.GlAccountType}', datafield: 'glAccountTypeId',filtertype : 'checkedlist',cellsrenderer : function(row){
						var data = $('#jqxgrid').jqxGrid('getrowdata',row);
						if(typeof(data) == 'undefined') return '';
						for(var index in dataGLAT){
							if(dataGLAT[index].glAccountTypeId == data.glAccountTypeId){
								return '<span>' + dataGLAT[index].description  + '</span>';
							}
						}
						return data.glAccountTypeId;
					},createfilterwidget : function(row,cellvalue,widget){
						var source  = {
							localdata : dataGLAT,
							datatype : 'array',
						};
						var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
				    	var uniRecords = filterBoxAdapter.records;
				    	uniRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
						widget.jqxDropDownList({source: uniRecords, displayMember: 'description', valueMember: 'glAccountTypeId',placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}',renderer: function (index, label, value) 
											{
												for(i=0;i < dataGLAT.length; i++){
													if(dataGLAT[i].glAccountTypeId == value){
														return dataGLAT[i].description;
													}
												}
											    return value;
											}});
					}}
					"/>
<@jqGrid url="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&sname=JQListGlAccountTypePaymentType" dataField=dataField columnlist=columnlist 
		 id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true" 
		 addrow="true" addType="popup" alternativeAddPopup="alterpopupWindow"
		 createUrl="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&jqaction=C&sname=addPaymentTypeGlAssignment"
		 addColumns="paymentTypeId;glAccountTypeId;organizationPartyId[${parameters.organizationPartyId}]"
		deletesuccessfunction="updateData" functionAfterAddRow="updateData"
		 deleterow="true" removeUrl="jqxGeneralServicer?sname=removePaymentTypeGlAssignment&jqaction=D" 
		 deleteColumn="paymentTypeId;organizationPartyId[${parameters.organizationPartyId}]"
 />				

<div id="alterpopupWindow" class ='hide'>
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
    	<div class='row-fluid form-window-content'>
			<form id="formAdd">
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.AccountingPaymentType}
    				</div>
    				<div class='span7'>
						<div id="paymentTypeId">
							<div id="jqxgridPaymentType"></div>
 						</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.GlAccountType}
    				</div>
    				<div class='span7'>
						<div id="glAccountTypeId">
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
	var updateData = function(){
		$('#jqxgridPaymentType').jqxGrid('updatebounddata');
	}
	var action = (function(){
		var initElement = function(){
			 $('#glAccountTypeId').jqxDropDownList({filterable : true,dropDownWidth : 250,width  :250, source: dataGLAT, displayMember: "description", valueMember: "glAccountTypeId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    //$('#paymentTypeId').jqxDropDownList({ source: dataPT, dropDownWidth : 250,width  :250,displayMember: "description", valueMember: "paymentTypeId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    initDropDownPM( $('#paymentTypeId'), $('#jqxgridPaymentType'));
		    $("#alterpopupWindow").jqxWindow({
		        width: 500,height : 180, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme:theme           
		    });
		}
		
		var initRules = function(){
			$('#formAdd').jqxValidator({
				rules : [
					{input : '#paymentTypeId',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(input){
						var val = input.val();
						if(!val) return false;
						return true;
					}},
					{input : '#glAccountTypeId',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(input){
						var val = input.jqxDropDownList('val');
						if(!val) return false;
						return true;
					}}
				]
			})
			
		}
		
		var initDropDownPM = function(dropdown,grid){
			GridUtils.initDropDownButton({url : 'getPaymentTypeGlAccountTypeNotDedault&organizationPartyId=${parameters.organizationPartyId}',autoshowloadelement : true,width : 400,filterable : true,source : {pagesize : 5,cache  : false},dropdown : {width : 250, dropDownHorizontalAlignment: true}},
			[
				{name : 'paymentTypeId',type : 'string'},
				{name : 'description',type : 'string'}
			], 
			[
				{text : '${uiLabelMap.paymentTypeId}',datafield : 'paymentTypeId',width : '30%'},
				{text : '${uiLabelMap.CommonDescription}',datafield : 'description'}
			]
			, null, grid,dropdown,'paymentTypeId');
		}
		
		var bindEvent = function(){
			// update the edited row when the user clicks the 'Save' button.
		    $("#save").click(function () {
		    	if(!$('#formAdd').jqxValidator('validate')){return;}
		    	var row;
		           row = {
		        		paymentTypeId: $('#paymentTypeId').val(),
		        		glAccountTypeId: $('#glAccountTypeId').val()
		        	  };
			   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
			   $("#jqxgrid").jqxGrid('clearSelection');                        
      			$("#jqxgrid").jqxGrid('selectRow', 0);  
		        $("#alterpopupWindow").jqxWindow('close');
		    });
		    $('#alterpopupWindow').on('close',function(){
		    	 $('#glAccountTypeId').jqxDropDownList('clearSelection');
		    	  $('#paymentTypeId').jqxDropDownButton('val','');
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