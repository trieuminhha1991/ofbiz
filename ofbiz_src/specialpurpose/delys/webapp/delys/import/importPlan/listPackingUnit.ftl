<#if security.hasEntityPermission("PRODUCT", "_ADMIN", session)>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/delys/images/js/import/Underscore1.8.3.js"></script>

<#assign dataField="[{ name: 'uomId', type: 'string'},
					{ name: 'uomTypeId', type: 'string'},
					{ name: 'abbreviation', type: 'string'},
					{ name: 'description', type: 'string'},
					]"/>

<#assign columnlist="{ text: '${StringUtil.wrapString(uiLabelMap.DAFormFieldTitle_quantityUomId)}', datafield: 'uomId', width: 250, align: 'center', editable:false},
					{ text: '${StringUtil.wrapString(uiLabelMap.CommonUomAbbreviation)}', datafield: 'abbreviation', width: 300, align: 'center' },
					{ text: '${StringUtil.wrapString(uiLabelMap.description)}', datafield: 'description', align: 'center' },
					"/>

<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" addrow="true" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false"
		url="jqxGeneralServicer?sname=JQGetListPackingUnit" viewSize="15"
		createUrl="jqxGeneralServicer?sname=createUom&jqaction=C"
		updateUrl="jqxGeneralServicer?sname=updateUom&jqaction=U"
		editColumns="uomId;abbreviation;description"
		addColumns="uomId;uomTypeId;abbreviation;description"
	/>

<div id="alterpopupWindow" style="display:none;">
	<div style="font-size:18px!important;">${uiLabelMap.AddNewPackingUom}</div>
	<div style="overflow-y: hidden;">
				<div class="row-fluid">
		 			<div class="span12" style="margin-top: 18px;">
			 			<div class="span4" style="text-align: right;"><label>${uiLabelMap.DAFormFieldTitle_quantityUomId}<span style="color:red;"> *</span></label></div>
			 			<div class="span8"><input type="text" id="txtUomId" /></div>
		 			</div>
				</div>
				<div class="row-fluid">
		 			<div class="span12" style="margin-top: 18px;">
			 			<div class="span4" style="text-align: right;"><label>${uiLabelMap.CommonUomAbbreviation}<span style="color:red;"> *</span></label></div>
			 			<div class="span8"><input type="text" id="txtAbbreviation" /></div>
		 			</div>
	 			</div>
				<div class="row-fluid">
		 			<div class="span12" style="margin-top: 8px;">
			 			<div class="span4" style="text-align: right;"><label>${uiLabelMap.description}<span style="color:red;"> *</span></label></div>
				 		<div class="span8"><input type="text" id="txtDescription" /></div>
		 			</div>
				</div>
		 		<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity: 0.4;">
		 		<div class="row-fluid">
		            <div class="span12 margin-top10">
		            	<div class="span12">
		            		<button id='alterCancel' class="btn btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.btnCancel}</button>
		            		<button id='alterSave' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
		        		</div>
		            </div>
	        	</div>
	</div>
</div>
<#assign packingUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>	
<script>
var packingUoms = [
    				<#if packingUoms?exists>
    					<#list packingUoms as item>
    						"${item.uomId?if_exists}".toLowerCase(),
    					</#list>
    				</#if>
                        ];
$("#alterpopupWindow").jqxWindow({
    width: 450, maxWidth: 1000, theme: "olbius", minHeight: 250, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7
});
$('#alterpopupWindow').on('open', function () {
	$("#txtUomId").val("");
	$("#txtAbbreviation").val("");
	$("#txtDescription").val("");
});
$('#alterpopupWindow').on('close', function () {
	$('#alterpopupWindow').jqxValidator('hide');
});

$("#alterSave").click(function () {
    if ($('#alterpopupWindow').jqxValidator('validate')) {
    	var row = {};
    	row.uomId = $("#txtUomId").val();
		row.uomTypeId = "PRODUCT_PACKING";
		row.abbreviation = $("#txtAbbreviation").val();
		row.description = $("#txtDescription").val();
    	$("#jqxgrid").jqxGrid('addRow', null, row, "first");
        $("#jqxgrid").jqxGrid('clearSelection');
        $("#jqxgrid").jqxGrid('selectRow', 0);
        $("#alterpopupWindow").jqxWindow('close');
    }
});
$('#alterpopupWindow').jqxValidator({
    rules: [
            	{ input: '#txtUomId', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' },
            	{ input: '#txtUomId', message: '${StringUtil.wrapString(uiLabelMap.ProductUomPackingAlreadyExists)}', action: 'keyup, blur',
            		rule: function (input, commit) {
						var value = $("#txtUomId").val().toLowerCase();
						if (_.indexOf(packingUoms, value) === -1) {
							return true;
						}
						return false;
					}
    			},
            	{ input: '#txtAbbreviation', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' },
            	{ input: '#txtDescription', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' }
           ]
});
</script>
	<#else>   
		<h2> You do not have permission</h2>
</#if>