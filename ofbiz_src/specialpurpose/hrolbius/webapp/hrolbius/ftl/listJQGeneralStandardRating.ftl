 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
 <#assign listEmplType = delegator.findList("EmplPositionType",null,null,null,null,false) !>
 <#assign listStatus = delegator.findList("StatusItem",null,null,null,null,false) !>
 <#assign listStandardRating = delegator.findList("StandardRating",null,null,null,null,false) !>
 <#assign listRatingCommon = delegator.findList("StandardRating",Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("standardType","General"),null,null,null,false) !>
 
 
<#assign dataField="[
						{ name: 'standardRatingId', type: 'string'},
						{ name: 'standardName', type: 'string'},
						{ name: 'weight', type: 'number',other : 'BigDecimal'}
					]"/>
<#assign columnlist= " {
						text : '${uiLabelMap.StandardRatingId}',dataField : 'standardRatingId',filterable : false,width : '150px',cellsrenderer : 
							function(row,columnfield,value){
									var data = $(\"#jqxgrid\").jqxGrid(\"getrowdata\", row);
									return '<a href=\"'+ 'ListGeneralJobRating?standardRatingId=' + data.standardRatingId  +'\">'+ value +'</a>';	
								}
						},
						{ 
						text : '${uiLabelMap.StandardName}' ,dataField : 'standardName'
						},{
						text : '${uiLabelMap.jobWeight}' , dataField : 'weight',filtertype : 'number'
						}" />
<@jqGrid filtersimplemode="true" filterable="true" addrefresh="true" showtoolbar="true" addType="popup" alternativeAddPopup="alterpopupWindow" dataField=dataField columnlist=columnlist  clearfilteringbutton="true" addrow="true" deleterow="true"
		 url="jqxGeneralServicer?sname=JQgetListStandardRating" 
		 createUrl="jqxGeneralServicer?sname=createGeneralStandardRating&jqaction=C" addColumns="standardName;weight"
		 removeUrl="jqxGeneralServicer?sname=deleteGeneralStandardRating&jqaction=D" deleteColumn="standardRatingId"	
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
		var ArrEmplType = [
			<#list listEmplType as type>
					{
						emplType : "${type.emplPositionTypeId?if_exists}",
						description : "${StringUtil.wrapString(type.description?if_exists)}"
					},
			</#list>
		];
		var WeightCommon = [
			<#list listRatingCommon as cm>
				{
					weight : '${cm.weight?if_exists}'
				},
			</#list>
		];
</script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 					
<div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
    	<input type="hidden" name="standardRatingId"/>
        <form id="formAdd" class="form-horizontal">
        <div class="no-left-margin control-group">
    			<label class="control-label asterisk">${uiLabelMap.StandardName}</label>
    			<div class="controls">
    				<input type="text" id="StandardNameAdd"/>
    			</div>
    		</div>
    		<div class="no-left-margin control-group">
    			<label class="control-label asterisk">${uiLabelMap.jobWeight}</label>
    			<div class="controls">
    				<input type="text" id="jobWeightAdd"/>
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
        width: 500, height : 220,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7          
    });
	$("#StandardNameAdd").jqxInput({ width: '195px', height: '25px'});
	$("#jobWeightAdd").jqxInput({ width: '195px', height: '25px'});
	$("#alterSave").jqxButton({theme: theme});
	$("#alterCancel").jqxButton({theme: theme});
	//init validate 
	  $('#formAdd').jqxValidator({
                rules: [
	                       { input: '#StandardNameAdd', message: '${StringUtil.wrapString(uiLabelMap.RequirementStandardName?default(''))}', action: 'blur', rule: 'required' },
	                       { input: '#jobWeightAdd', message: '${StringUtil.wrapString(uiLabelMap.RequirementWeight?default(''))}', action: 'blur', rule: function(){
	                       				var value = $("#jobWeightAdd").val();
	                       				var result = value != '' && value !== 'undefined';
	                       				return result;
	                       } },
	                     { input: '#jobWeightAdd', message: '${StringUtil.wrapString(uiLabelMap.RequirementNumber?default(''))}', action: 'keyup,blur', rule: function(){
                       				var value = $("#jobWeightAdd").val();
                       				var result = !isNaN(value);
                       				return result;
	                       } },
	                       { input: '#jobWeightAdd', message: '${StringUtil.wrapString(uiLabelMap.RequirementWeightFormat?default(''))}', action: 'keyup,blur', rule: function(){
	                       				var value = $("#jobWeightAdd").val();
	                       				var result  = value >= 0;
	                       				return result;
	                       } },  
	                        { input: '#jobWeightAdd', message: '${StringUtil.wrapString(uiLabelMap.RequirementTotalWeight?default(''))}', action: 'keyup,blur', rule: function(){
                   				var value = $("#jobWeightAdd").val();
                   				var totalWeightCommon = 0;
                   				for(var w in WeightCommon){
                   					if(WeightCommon[w].weight){
                   						totalWeightCommon += parseInt(WeightCommon[w].weight);
                   					}
                   				}
                   				var result;
                   				if(value.length == 0 ){
                   					return true;
                   				}
                   				if(totalWeightCommon){
                   					result =  (totalWeightCommon + parseInt(value)) <= 100;
                   				}else{
                   					result =  parseInt(value) <= 100;
                   				}
                   				return result;
	                       } }    
                       ]
           			 });
    // update the edited row when the user clicks the 'Save' button.
    $("#alterSave").click(function () {
    	$('#formAdd').jqxValidator('validate');
    });
    $("#alterCancel").click(function () {
    	$('#formAdd').trigger('reset');
    });
    $('#formAdd').on('validationSuccess', function (event) {
    		row = { 
        		standardName:$('#StandardNameAdd').val(),
        		weight:$('#jobWeightAdd').val()
        	  };
		   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
	        // select the first row and clear the selection.
	        $("#jqxgrid").jqxGrid('clearSelection');                        
	        $("#jqxgrid").jqxGrid('selectRow', 0);  
	       $('#formAdd').trigger('reset');
        $("#alterpopupWindow").jqxWindow('close');  
 });
</script>