<#--Import LIB-->
<#--/Import LIB-->
<#--===================================Prepare Data=====================================================-->
<script>
	//Prepare for uom data
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false)>
	var currencyUomData = [
		<#list uoms as item>
			{
				<#assign description = StringUtil.wrapString(item.description + "-" + item.abbreviation) />
				uomId : '${item.uomId}',
				description : '${description}',
			},
		</#list>
	]
	
	//Prepare for role type data
	<#assign termTypeList = delegator.findByAnd("TermType", {"parentTypeId" : "FINANCIAL_TERM"}, Static["org.ofbiz.base.util.UtilMisc"].toList("description DESC"), false) />
	termTypeData = [
	              <#list termTypeList as item>
					<#assign description = StringUtil.wrapString(item.description?if_exists) />
					{'termTypeId': '${item.termTypeId}', 'description': '${description}'},
				  </#list>
				];
</script>
<#--===================================/Prepare Data=====================================================-->
<#--=================================Init Grid======================================================-->
<#assign dataField="[{ name: 'billingAccountId', type: 'string'},
					 { name: 'billingAccountTermId', type: 'string'},
					 { name: 'termTypeId', type: 'string'},
					 { name: 'termValue', type: 'string'},
					 { name: 'uomId', type: 'string'},
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.PartyTermType}', datafield: 'termTypeId', columntype: 'dropdownlist', filtertype: 'checkedlist',
						cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							for(var i = 0; i < termTypeData.length; i++){
									if(value == termTypeData[i].termTypeId){
										return '<span title=' + value + '>' + termTypeData[i].description + '</span>';
									}
								}
								return '<span> ' + value + '</span>';
						},
						createeditor: function (row, value, editor) {
                            editor.jqxDropDownList({ source: termTypeData, displayMember: 'description', valueMember: 'termTypeId' });
                        },
                        createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(termTypeData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							widget.jqxDropDownList({source: records, displayMember: 'description', valueMember: 'termTypeId',
								renderer: function(index, label, value){
									for(var i = 0; i < termTypeData.length; i++){
										if(termTypeData[i].termTypeId == value){
											return '<span>' + termTypeData[i].description + '</span>';
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
			   			}
					 },
					 { text: '${uiLabelMap.CommonUom}', datafield: 'uomId', columntype: 'dropdownlist',filtertype: 'checkedlist', width: 250,
						cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							for(var i = 0; i < currencyUomData.length; i++){
								if(value == currencyUomData[i].uomId){
									return '<span title=' + value + '>' + currencyUomData[i].description + '</span>';
								}
							}
							return '<span> ' + value + '</span>';
						},
						createeditor: function (row, value, editor) {
                            editor.jqxDropDownList({ source: currencyUomData, displayMember: 'description', valueMember: 'uomId' });
                        },
                        createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(currencyUomData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							widget.jqxDropDownList({source: records, displayMember: 'description', valueMember: 'uomId',
								renderer: function(index, label, value){
									for(var i = 0; i < currencyUomData.length; i++){
										if(currencyUomData[i].uomId == value){
											return '<span>' + currencyUomData[i].description + '</span>';
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
			   			}
					 },
                     { text: '${uiLabelMap.CommonValue}', datafield: 'termValue', width: 250}
					 "/>

<@jqGrid id="jqxgrid" filtersimplemode="true" addrow="true" addrefresh="true" editable="true" deleterow="true" addType="popup" alternativeAddPopup="wdwNewBillingAccTerm" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListBillingAccountTerms&billingAccountId=${parameters.billingAccountId}" dataField=dataField columnlist=columnlist
		 createUrl="jqxGeneralServicer?sname=createBillingAccountTerm&jqaction=C" 
		 addColumns="billingAccountId[${parameters.billingAccountId}];termTypeId;uomId;termValue"
		 updateUrl="jqxGeneralServicer?sname=updateBillingAccountTerm&jqaction=U"
		 editColumns="billingAccountTermId;termTypeId;uomId;termValue"
		 removeUrl="jqxGeneralServicer?sname=removeBillingAccountTerm&jqaction=D"
		 deleteColumn="billingAccountTermId"
		 />
                     
<#--=================================/Init Grid======================================================-->
<div id="wdwNewBillingAccTerm" style="display: none;">
	<div id="wdwHeader">
		<span>
		   ${uiLabelMap.NewBillingAccountRoles}
		</span>
	</div>
	<div id="wdwContentNew">
		<div class="basic-form form-horizontal" style="margin-top: 10px">
			<form name="formNew" id="formNew">	
				<div class="row-fluid" >
					<div class="span12">
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.PartyTermType}:</label>  
							<div class="controls">
								<div id="termTypeIdAdd">
								</div>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label asterisk">${uiLabelMap.CommonUom}:</label>  
							<div class="controls">
								<div id="uomIdAdd"></div>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.PartyTermValue}:</label>  
							<div class="controls">
								<input id="termValueAdd">
								</input>
							</div>
						</div>
					</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
	</div>
</div>
<#--====================================================Setup JS======================================-->
<script>
	var JQXAction = function(){};
	JQXAction.prototype.theme = 'olbius';
	JQXAction.prototype.initWindow = function(){
		$('#wdwNewBillingAccTerm').jqxWindow({showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "75%", height: 350, minWidth: '40%', width: "50%", isModal: true, modalZIndex: 10000,theme:this.theme, collapsed:false, cancelButton: '#alterCancel',
            initContent: function () {
            	$("#uomIdAdd").jqxDropDownList({source: currencyUomData, valueMember: 'uomId', displayMember: 'description'});
            	$("#termTypeIdAdd").jqxDropDownList({source: termTypeData, valueMember: 'termTypeId', displayMember: 'description'});
            	$("#termValueAdd").jqxInput({width: 195});
            }
        });
	};
	
	JQXAction.prototype.bindEvent = function(){		
		$('#alterSave').on('click', function(){
			var row = {};
	    	row.uomId = $("#uomIdAdd").val();
			row.termTypeId = $("#termTypeIdAdd").val();
			row.termValue = $("#termValueAdd").val();
			$("#jqxgrid").jqxGrid('addRow', null, row, "first");
	        $("#jqxgrid").jqxGrid('clearSelection');
	        $("#jqxgrid").jqxGrid('selectRow', 0);
	        $("#wdwNewBillingAccTerm").jqxWindow('close');
		});
	};
	
	$(document).on('ready', function(){
		var jqxAction = new JQXAction();
		jqxAction.initWindow();
		jqxAction.bindEvent();
	});
</script>
<#--====================================================/Setup JS======================================-->