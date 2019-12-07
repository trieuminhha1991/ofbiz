<#if security.hasEntityPermission("IMPORT", "_ADMIN", session)>
 <script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
 <script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
 <script type="text/javascript" src="/delys/images/js/import/Underscore1.8.3.js"></script>
 <#assign dataField="[{ name: 'productId', type: 'string'},
					{ name: 'uomId', type: 'string'},
					{ name: 'uomIdTo', type: 'string'},
					{ name: 'fromDate', type: 'date', other: 'Timestamp'},
					{ name: 'thruDate', type: 'date', other: 'Timestamp'},
					{ name: 'conversionFactor', type: 'number'},
					]"/>
<#assign columnlist="
					{ text: '${StringUtil.wrapString(uiLabelMap.uomFromId)}', datafield: 'uomId', align: 'center', width: 230, editable: false },
					{ text: '${StringUtil.wrapString(uiLabelMap.uomToId)}', datafield: 'uomIdTo', align: 'center', width: 230, editable: false },
					{ text: '${StringUtil.wrapString(uiLabelMap.ConversionFactor)}', datafield: 'conversionFactor', columntype:'numberinput', align: 'center', cellsalign: 'right' },
					{ text: '${StringUtil.wrapString(uiLabelMap.AvailableFromDate)}', datafield: 'fromDate', align: 'center', width: 200, filtertype: 'range', cellsformat: 'dd/MM/yyyy', editable: false},
					{ text: '${StringUtil.wrapString(uiLabelMap.AvailableThruDate)}', datafield: 'thruDate', align: 'center', width: 200, filtertype: 'range', cellsformat: 'dd/MM/yyyy'}
					"/>
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" viewSize="15"
		showtoolbar="true" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="true"
		url="jqxGeneralServicer?sname=JQGetListConfigGeneralCapacitys"
		createUrl="jqxGeneralServicer?sname=createUomConversionDated&jqaction=C"
		updateUrl="jqxGeneralServicer?sname=updateUomConversionDated&jqaction=U"
		addColumns="uomId;uomIdTo;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);conversionFactor(java.lang.Double)"
		editColumns="uomId;uomIdTo;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);conversionFactor(java.lang.Double)"
		removeUrl="jqxGeneralServicer?sname=removeGeneralCapacitys&jqaction=D" deleteColumn="uomId;uomIdTo;fromDate(java.sql.Timestamp)"
		/>

<div id="alterpopupWindow"  style="display:none;">
<div>${uiLabelMap.ConfigGeneralCapacity}</div>
<div style="overflow-y: hidden;">
	<div class='row-fluid form-window-content'>
		<div class='span6'>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'>${uiLabelMap.uomFromId}<span style="color:red;"> *</span></div>
				<div class='span7'><div id="uomFromId1"></div></div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'>${uiLabelMap.QuantityConvert}<span style="color:red;"> *</span></div>
				<div class='span7'><div id="quantityConvert1"></div></div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'>${uiLabelMap.AvailableFromDate}<span style="color:red;"> *</span></div>
				<div class='span7'><div id="fromDate1"></div></div>
			</div>
		</div>
		
		<div class='span5'>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'>${uiLabelMap.uomToId}<span style="color:red;"> *</span></div>
				<div class='span7'><div id="uomToId1"></div></div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'></div>
				<div class='span7'></div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'>${uiLabelMap.AvailableThruDate}&nbsp;&nbsp;</div>
				<div class='span7'><div id="thruDate1"></div></div>
			</div>
		</div>
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

<#assign listUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "SHIPMENT_PACKING"), null, null, null, false)>
<script>

	var listUoms = [
					<#if listUoms?exists>
						<#list listUoms as item>
						{
							uomId: "${item.uomId?if_exists}",
							description: "${item.description?if_exists}"
						},
						</#list>
					</#if>
	                ];
	$("#alterpopupWindow").jqxWindow({
	    width: 900, maxWidth: 1000, theme: "olbius", minHeight: 250, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7
	});
	$("#uomFromId1").jqxDropDownList({ source: listUoms, width: '220px', displayMember: "description", valueMember: "uomId", theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', autoDropDownHeight: true });
	$("#uomToId1").jqxDropDownList({ source: [], width: '220px', displayMember: "description", valueMember: "uomId", theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', autoDropDownHeight: true });
	$("#quantityConvert1").jqxNumberInput({ inputMode: 'simple', spinButtons: true, theme: "olbius", width: '220px', decimalDigits: 0, min: 0  });
	$("#fromDate1").jqxDateTimeInput({theme: "olbius", width: '220px' });
	$("#thruDate1").jqxDateTimeInput({theme: "olbius", width: '220px' });
	
	$('#uomFromId1').on('change', function (event){     
	    var args = event.args;
	    if (args) {
		    var index = args.index;
		    var item = args.item;
		    var label = item.label;
		    var value = item.value;
		    var dataAvalible = mapRalationPacking[value];
		    var soucreToId = [];
		    for ( var x in listUoms) {
				if (_.indexOf(dataAvalible, listUoms[x].uomId) == -1) {
					soucreToId.push(listUoms[x]);
				}
			}
		    $("#uomToId1").jqxDropDownList({ source: soucreToId });
		}
	});
	var mapRalationPacking = {};
    function getListProductConfigPackingAjax() {
    	$.ajax({
  		  url: "getMapConfigCapacityGeneralsAjax",
  		  type: "POST",
  		  data: {},
  		  success: function(data) {
  			mapRalationPacking = data["mapRalationPacking"];
  		  }
  	  	}).done(function() {
  	  		
  	  	});
	}
	$('#alterpopupWindow').on('open', function () {
		getListProductConfigPackingAjax();
		$('#thruDate1').val(null);
		$("#uomFromId1").jqxDropDownList('clearSelection');
		$("#uomToId1").jqxDropDownList('clearSelection');
		$("#quantityConvert1").jqxNumberInput('val', 0);
	});
	$('#alterpopupWindow').on('close', function () {
		$('#alterpopupWindow').jqxValidator('hide');
	});
	$("#alterSave").click(function () {
        if ($('#alterpopupWindow').jqxValidator('validate')) {
        	var row = {};
        	row.uomId = $("#uomFromId1").val();
    		row.uomIdTo = $("#uomToId1").val();
			row.conversionFactor = $("#quantityConvert1").val();
			row.thruDate = $("#thruDate1").val().toMilliseconds();
			row.fromDate = $("#fromDate1").val().toMilliseconds();
        	$("#jqxgrid").jqxGrid('addRow', null, row, "first");
            $("#jqxgrid").jqxGrid('clearSelection');
            $("#jqxgrid").jqxGrid('selectRow', 0);
            $("#alterpopupWindow").jqxWindow('close');
            setTimeout(function(){
            	$("#jqxgrid").jqxGrid('updatebounddata');
            }, 500);
        }
    });
	$('#alterpopupWindow').jqxValidator({
        rules: [
                { input: '#uomFromId1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
                	rule: function (input, commit) {
                		var value = $("#uomFromId1").val();
                		if (value) {
                			return true;
                		}
                		return false;
                	}
                },
                { input: '#uomToId1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
                	rule: function (input, commit) {
                		var value = $("#uomToId1").val();
                		if (value) {
                			return true;
                		}
                		return false;
                	}
                },
                { input: '#quantityConvert1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'valueChanged', 
                	rule: function (input, commit) {
                		var value = $("#quantityConvert1").val();
                		if (value > 0) {
                			return true;
						}
                		return false;
                	}
                },
                { input: '#fromDate1', message: '${StringUtil.wrapString(uiLabelMap.DateNotValid)}', action: 'valueChanged', 
                	rule: function (input, commit) {
                		var value = $("#fromDate1").val().toMilliseconds();
                		if (value > 0) {
                			return true;
						}
                		return false;
                	}
                },
                { input: '#thruDate1', message: '${StringUtil.wrapString(uiLabelMap.DateNotValid)}', action: 'valueChanged', 
                	rule: function (input, commit) {
                		var thruDate = $("#thruDate1").val().toMilliseconds();
                		if (!thruDate) {
                			return true;
						}
                		var fromDate = $("#fromDate1").val().toMilliseconds();
                		if (fromDate <= thruDate) {
                			return true;
						}
                		return false;
                	}
                }
               ]
    });
	function fixSelectAll(dataList) {
    	var sourceST = {
		        localdata: dataList,
		        datatype: "array"
		    };
		var filterBoxAdapter2 = new $.jqx.dataAdapter(sourceST, {autoBind: true});
        var uniqueRecords2 = filterBoxAdapter2.records;
		uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
		return uniqueRecords2;
	}
</script>
		<#else>   
				<h2> You do not have permission</h2>
</#if>