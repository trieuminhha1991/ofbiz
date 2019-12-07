<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<#assign dataField = "[{name: 'productStoreId', type: 'string'}, 
						{name: 'storeName', type: 'string'}, 
						{name: 'companyName', type: 'string'}, 
						{name: 'payToPartyId', type: 'string'}
						]"/>
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.DAProductStoreId)}', dataField: 'productStoreId', width: '16%',
							cellsrenderer: function(row, colum, value) {
	                        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	                        	return \"<span><a href='/delys/control/editProductStore?productStoreId=\" + data.productStoreId + \"'>\" + data.productStoreId + \"</a></span>\";
	                        }
						}, 
						{text: '${StringUtil.wrapString(uiLabelMap.DAStoreName)}', dataField: 'storeName'},
						{text: '${StringUtil.wrapString(uiLabelMap.DACompanyName)}', dataField: 'companyName', width: '30%'},
						{text: '${StringUtil.wrapString(uiLabelMap.DAPayToPartyId)}', dataField: 'payToPartyId', width: '18%'}
						"/>
<@jqGrid id="jqxgrid" addrow="true" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow1" columnlist=columnlist dataField=dataField
		viewSize="25" showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="false"  addType="popup"
		url="jqxGeneralServicer?sname=JQGetListProductStore" mouseRightMenu="true" contextMenuId="contextMenu"
		createUrl="jqxGeneralServicer?sname=createProductStoreDD&jqaction=C" addColumns="productStoreId;primaryStoreGroupId;storeName;companyName;payToPartyId;defaultCurrencyUomId;subtitle;title;inventoryFacilityId"
			/>

<div id="alterpopupWindow1" style="display : none;">
<div>${uiLabelMap.CommonAdd}</div>
<div style="overflow: hidden;">
	<form id="ProductStoreForm" class="form-horizontal">
		<div class="row-fluid no-left-margin">
			<div class="span6">
				<div class="row-fluid no-left-margin">
					<label class="span5 align-right asterisk">${uiLabelMap.DAProductStoreId}</label>
					<div class="span7" style="margin-bottom: 10px;">
						<input type="text" id="DAProductStoreIdAdd" />
					</div>
				</div>
		
		<div class="row-fluid no-left-margin">
			<label class="span5 align-right">${uiLabelMap.DAPrimaryStoreGroup}</label>
			<div class="span7" style="margin-bottom: 10px;">
				<div id="DAPrimaryStoreGroupAdd"></div>
			</div>
		</div>
		
		<div class="row-fluid no-left-margin">
			<label class="span5 align-right asterisk">${uiLabelMap.DAStoreName}</label>
			<div class="span7" style="margin-bottom: 10px;">
				<input type="text" id="DAStoreNameAdd"/>
			</div>
		</div>
		
		<div class="row-fluid no-left-margin">
		<label class="span5 align-right">${uiLabelMap.DAPayToParty}</label>
		<div class="span7" style="margin-bottom: 10px;">
			<div id="DAPayToPartyAdd">
				<div id="jqxPayToPartyGrid" ></div>
			</div>
		</div>
	</div>
		
	<div class="row-fluid no-left-margin">
	<label class="span5 align-right asterisk">${uiLabelMap.ProductTitle}</label>
	<div class="span7" style="margin-bottom: 10px;">
		<input type="text" id="ProductTitleAdd"/>
	</div>
</div>
	
			</div>
		<div class="span6">
		<div class="row-fluid no-left-margin">
		<label class="span5 align-right asterisk">${uiLabelMap.ProductSubTitle}</label>
		<div class="span7" style="margin-bottom: 10px;">
			<input type="text" id="ProductSubTitleAdd"/>
		</div>
	</div>

	<div class="row-fluid no-left-margin">
		<label class="span5 align-right asterisk">${uiLabelMap.DACompanyName}</label>
		<div class="span7" style="margin-bottom: 10px;">
			<input type="text" id="DACompanyNameAdd"/>
		</div>
	</div>
	
	

	<div class="row-fluid no-left-margin">
		<label class="span5 align-right">${uiLabelMap.DAPrimaryInventoryFacility}</label>
		<div class="span7" style="margin-bottom: 10px;">
			<div id="DAPrimaryInventoryFacilityAdd"></div>
		</div>
	</div>
	
	<div class="row-fluid no-left-margin">
		<label class="span5 align-right">${uiLabelMap.DADefaultCurrencyUom}</label>
		<div class="span7" style="margin-bottom: 10px;">
			<div id="DADefaultCurrencyUomAdd"></div>
		</div>
	</div>
		
		</div>
		</div>		
		<#--	
		<div class="control-group no-left-margin" style="float:right">
			<div class="" style="width:166px;margin:0 auto;">
				<button type="button" class="jqx-rc-all jqx-rc-all-olbius jqx-button jqx-button-olbius jqx-widget jqx-widget-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius" style="margin-right: 5px; margin-top: 10px;" id="alterSave1"><i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
				<button type="button" class="jqx-rc-all jqx-rc-all-olbius jqx-button jqx-button-olbius jqx-widget jqx-widget-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius btn-danger" style="margin-right: 5px; margin-top: 10px;" id="alterCancel1"><i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
			</div>
		</div>   -->
			
		<div class="form-action">
		<div class='row-fluid'>
			<div class="span12 margin-top10">
				<button type="button" id="alterCancel1" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
				<button type="button" id="alterSave1" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
			</div>
		</div>
	</div>
	
		
		
		
	</form>
</div>
</div>

<div id='contextMenu' style="display:none">
	<ul>
	    <li><i class="icon-refresh open-sans"></i>${StringUtil.wrapString(uiLabelMap.DARefresh)}</li>
	    <li><i class="fa fa-search"></i>${StringUtil.wrapString(uiLabelMap.DAViewDetail)}</li>
	    <li><i class="fa fa-search"></i>${StringUtil.wrapString(uiLabelMap.DAViewDetailInNewTab)}</li>
	</ul>
</div>

<#assign productStoreGroup = delegator.findByAnd("ProductStoreGroup", null, null, false)>
<#assign facilityIn = delegator.findByAnd("Facility", null ,null , false)>
<#assign uomCurrency = delegator.findByAnd("Uom", Static["org.ofbiz.base.util.UtilMisc"].toMap("uomTypeId", "CURRENCY_MEASURE"), null, false)>
<#assign facilityIn = delegator.findByAnd("Facility", null ,null , false)>

<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	$("#contextMenu").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#contextMenu").on('itemclick', function (event) {
		var args = event.args;
        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
        var tmpKey = $.trim($(args).text());
        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DARefresh)}") {
        	$("#jqxgrid").jqxGrid('updatebounddata');
        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DAViewDetail)}") {
        	var data = $("#jqxgrid").jqxGrid("getrowdata", rowindex);
			if (data != undefined && data != null) {
				var productStoreId = data.productStoreId;
				var url = 'editProductStore?productStoreId=' + productStoreId;
				var win = window.open(url, '_self');
				win.focus();
			}
        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DAViewDetailInNewTab)}") {
        	var data = $("#jqxgrid").jqxGrid("getrowdata", rowindex);
			if (data != undefined && data != null) {
				var productStoreId = data.productStoreId;
				var url = 'editProductStore?productStoreId=' + productStoreId;
				var win = window.open(url, '_blank');
				win.focus();
			}
        }
	});
	
	var productStoreGroup = [
	                           <#list productStoreGroup as productSG>
	                           {
	                        	   productStoreGroupId : "${productSG.productStoreGroupId}",
	                        	   description : "${StringUtil.wrapString(productSG.description)}"
	                           },
	                           </#list>	
	                       ];
	var facilityIn = [
	                           <#list facilityIn as facilityI>
	                           {
	                        	   facilityId : "${facilityI.facilityId}",
	                        	   facilityName : "${StringUtil.wrapString(facilityI.facilityName)}"
	                           },
	                           </#list>	
	                       ];
	var uomCurrency = [
                      <#list uomCurrency as uomC>
                      {
                    	  uomId : "${uomC.uomId}",
                    	  description : "${StringUtil.wrapString(uomC.description)}"
                      },
                      </#list>	
                  ];
	
	var sourcePartyFrom = {
			datafields:[{name: 'partyId', type: 'string'},
				   		{name: 'firstName', type: 'string'},
				      	{name: 'lastName', type: 'string'},
				      	{name: 'middleName', type: 'string'},
				      	{name: 'groupName', type: 'string'},
		    ],
			cache: false,
			root: 'results',
			datatype: "json",
			updaterow: function (rowid, rowdata) {
				// synchronize with the server - send update command   
			},
			beforeprocessing: function (data) {
			    sourcePartyFrom.totalrecords = data.TotalRows;
			},
			filter: function () {
			   	// update the grid and send a request to the server.
			   	$("#jqxPayToPartyGrid").jqxGrid('updatebounddata');
			},
			pager: function (pagenum, pagesize, oldpagenum) {
			  	// callback called when a page or page size is changed.
			},
			sort: function () {
			  	$("#jqxPayToPartyGrid").jqxGrid('updatebounddata');
			},
			sortcolumn: 'partyId',
           	sortdirection: 'asc',
			type: 'POST',
			data: {
				noConditionFind: 'Y',
				conditionsFind: 'N',
			},
			pagesize:5,
			contentType: 'application/x-www-form-urlencoded',
			url: 'jqxGeneralServicer?sname=JQGetListParties',
};
	
	var dataAdapterPF = new $.jqx.dataAdapter(sourcePartyFrom,
		    {
		    	autoBind: true,
		    	formatData: function (data) {
		    		if (data.filterscount) {
		                var filterListFields = "";
		                for (var i = 0; i < data.filterscount; i++) {
		                    var filterValue = data["filtervalue" + i];
		                    var filterCondition = data["filtercondition" + i];
		                    var filterDataField = data["filterdatafield" + i];
		                    var filterOperator = data["filteroperator" + i];
		                    filterListFields += "|OLBIUS|" + filterDataField;
		                    filterListFields += "|SUIBLO|" + filterValue;
		                    filterListFields += "|SUIBLO|" + filterCondition;
		                    filterListFields += "|SUIBLO|" + filterOperator;
		                }
		                data.filterListFields = filterListFields;
		            }
		            return data;
		        },
		        loadError: function (xhr, status, error) {
		            alert(error);
		        },
		        downloadComplete: function (data, status, xhr) {
		                if (!sourcePartyFrom.totalRecords) {
		                    sourcePartyFrom.totalRecords = parseInt(data['odata.count']);
		                }
		        }
		    });	
	$('#DAPayToPartyAdd').jqxDropDownButton({ width: 198, height: 25});
	$("#jqxPayToPartyGrid").jqxGrid({
		width:600,
		source: dataAdapterPF,
		filterable: true,
		virtualmode: true, 
		sortable:true,
		editable: false,
		autoheight:true,
		pageable: true,
		showfilterrow: true,
		rendergridrows: function(obj) {	
			return obj.data;
		},
		columns:[{text: '${uiLabelMap.DAPartyId}', datafield: 'partyId', width: '20%'},
					{text: '${uiLabelMap.DAFirstName}', datafield: 'firstName', width: '20%'},
					{text: '${uiLabelMap.DAMiddleName}', datafield: 'middleName', width: '20%'},
					{text: '${uiLabelMap.DALastName}', datafield: 'lastName', width: '20%'},
					{text: '${uiLabelMap.DAGroupName}', datafield: 'groupName'},
				]
	});
	
	$("#jqxPayToPartyGrid").on('rowselect', function (event) {
        var args = event.args;
        var row = $("#jqxPayToPartyGrid").jqxGrid('getrowdata', args.rowindex);
        var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['partyId'] + '</div>';
        $("#DAPayToPartyAdd").jqxDropDownButton('setContent', dropDownContent);
    });
	
	$('#alterpopupWindow1').jqxWindow({ width: 864	, height : 320,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel1"), modalOpacity: 0.7 });
	
	
	
	$('#DAProductStoreIdAdd').jqxInput({width : '193px',height : '19px'});
	$("#DAPrimaryStoreGroupAdd").jqxDropDownList({ source: productStoreGroup, width: '198px', height: '25px',displayMember: "description", valueMember : "productStoreGroupId", selectedIndex: 0});
	$('#DAStoreNameAdd').jqxInput({width : '193px',height : '19px'});
	$('#ProductTitleAdd').jqxInput({width : '193px',height : '19px'});
	$('#ProductSubTitleAdd').jqxInput({width : '193px',height : '19px'});
	$('#DACompanyNameAdd').jqxInput({width : '193px',height : '19px'});
	$("#DAPrimaryInventoryFacilityAdd").jqxDropDownList({ source: facilityIn, width: '198px', height: '25px', displayMember: "facilityName", valueMember : "facilityId", selectedIndex: 0,dropDownHeight:200});
	$("#DADefaultCurrencyUomAdd").jqxDropDownList({ source: uomCurrency, width: '198px', height: '25px', displayMember: "description", valueMember : "uomId", selectedIndex: 158,dropDownHeight:200});
	
	
	
	$("#DAPrimaryStoreGroupAdd").jqxDropDownList({autoDropDownHeight: true}); 
	
	$('#ProductStoreForm').jqxValidator({
		rules : [
		         {input : '#DAProductStoreIdAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
		         {input : '#DAStoreNameAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
		         {input : '#ProductTitleAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
		         {input : '#DACompanyNameAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
		         {input : '#ProductSubTitleAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
		         {input: '#DAPayToPartyAdd', message: '${uiLabelMap.DANotYetChooseItem}', action: 'blur', rule: 
	        			function (input, commit) {
	        				var value = $(input).val();
							if(/^\s*$/.test(value)){
								return false;
							}
							return true;
						}
					},
		]
	});
	
	
	$('#alterSave1').click(function(){
		$('#ProductStoreForm').jqxValidator('validate');
	});

	$('#ProductStoreForm').on('validationSuccess',function(){
		var row = {};
		row = {
			productStoreId : $('#DAProductStoreIdAdd').val(),
			companyName : $('#DACompanyNameAdd').val(),
			primaryStoreGroupId : $('#DAPrimaryStoreGroupAdd').val(),
			storeName : $('#DAStoreNameAdd').val(),
			payToPartyId : $('#DAPayToPartyAdd').val(),
			defaultCurrencyUomId : $('#DADefaultCurrencyUomAdd').val(),
			subtitle : $('#ProductSubTitleAdd').val(),
			title : $('#ProductTitleAdd').val(),
			inventoryFacilityId : $('#DAPrimaryInventoryFacilityAdd').val(),	
			
		};
		$("#jqxgrid").jqxGrid('addRow', null, row, "first");
	// select the first row and clear the selection.
		$("#jqxgrid").jqxGrid('clearSelection');                        
		$("#jqxgrid").jqxGrid('selectRow', 0);  
		$("#alterpopupWindow1").jqxWindow('close');
	});


	$('#alterpopupWindow1').on('close',function(){
		$('#ProductStoreForm').jqxValidator('hide');
		$('#DAProductStoreIdAdd').val('');
		$('#DACompanyNameAdd').val('');
		$('#DAStoreNameAdd').val('');
		$('#ProductSubTitleAdd').val('');
		$('#ProductTitleAdd').val('');
//		$('#PersonEducationForm').trigger('reset');
	});
	
	
	
	
</script>
