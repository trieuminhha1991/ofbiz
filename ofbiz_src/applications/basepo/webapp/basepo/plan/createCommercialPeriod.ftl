<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/imexresources/js/notify.js"></script>

<script>
	var commercialPeriodArray = [<#if listPeriod?has_content><#list listPeriod as item>
		${item.periodName?if_exists},
	</#list></#if>];
	var currentTime = new Date();
	var currentYear = currentTime.getFullYear();
</script>

<#assign dataField="[{ name: 'periodName', type: 'string' },
					{ name: 'fromDate', type: 'date', other: 'date' },
					{ name: 'thruDate', type: 'date', other: 'date' }]"/>

<#assign columnlist="{ text: '${StringUtil.wrapString(uiLabelMap.STT)}', sortable: false, filterable: false,
						groupable: false, draggable: false, resizable: false, datafield: '', columntype: 'number', width: 50,
						cellsrenderer: function(row, column, value){
							return \"<div style='margin-top:3px;text-align:left;'>\" + (value+1) + \"</div>\";
						}
					},
					{ text: '${uiLabelMap.CommercialPeriodYear}', datafield: 'periodName' },
					{ text: '${uiLabelMap.DAFromDate}', datafield: 'fromDate', filtertype: 'range', width: 200, cellsformat: 'dd/MM/yyyy' },
					{ text: '${uiLabelMap.DAThruDate}', datafield: 'thruDate', filtertype: 'range', width: 200, cellsformat: 'dd/MM/yyyy' }"/>

<#if hasOlbPermission("MODULE", "PLANPO_COMMERCIALS_NEW", "CREATE")>
	<#assign addrow="true"/>
<#else>
	<#assign addrow="false"/>
</#if>

<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist showtoolbar="true" addrow=addrow
	editable="false" deleterow="false" addColumns="yearPeriod" addrefresh="true" clearfilteringbutton="true" addType="popup"
	defaultSortColumn="periodName" alternativeAddPopup="alterpopupWindow"
	createUrl="jqxGeneralServicer?sname=createCommercialPeriodPO&jqaction=C"
	url="jqxGeneralServicer?sname=jqGetListCommercialPeriod"/>

<div id="alterpopupWindow" style="display:none;">
	<div>${uiLabelMap.CreateCommercialPeriod}</div>
	<div style="overflow:hidden;">
		<div id="formAdd" class="form-horizontal">
			<div class="row-fluid margin-top10">
				<div class="span12">
					<div class="span5">
						<label class="pull-right asterisk"style="margin-top:4px;">${uiLabelMap.CreateCommercialPeriodYear}</label>
					</div>
					<div class="span7">
						<div id="yearPeriod" name="yearPeriod"></div>
					</div>
				</div>
			</div>
			<hr style="margin:10px!important;"/>
			<div class="" style="">
			<button type="button" class="btn btn-danger form-action-button pull-right" id="alterCancel">
				<i class="icon-remove"></i>&nbsp;${uiLabelMap.CommonCancel}
			</button>
			<button type="button" class="btn btn-primary form-action-button pull-right" id="alterSave">
				<i class="fa fa-check"></i>&nbsp;${uiLabelMap.CommonSave}
			</button>
			</div>
		</div>
	</div>
</div>

<script>
	$(document).ready(function(){
		$("#yearPeriod").jqxNumberInput({ width: 270, height: 30, inputMode: "simple", spinButtons: true, decimal: currentYear, decimalDigits: 0 });
		
		$("#formAdd").jqxValidator({
			rules:[]
		});
		
		$("#formAdd").on("validationSuccess", function(event){
			var yearPeriod = $("#yearPeriod").val();
			if (commercialPeriodArray.indexOf(yearPeriod) >= 0){
				$("#yearPeriod").notify("${StringUtil.wrapString(uiLabelMap.CreateCommercialPeriodCoincidence)}", "error");
			} else {
				if (yearPeriod < currentYear){
					$("#yearPeriod").notify("${StringUtil.wrapString(uiLabelMap.ValueIsInvalid)}", "error");
				} else {
					commercialPeriodArray.push(yearPeriod);
					var row = {
						yearPeriod: $("#yearPeriod").val()
					};
					$("#jqxgrid").jqxGrid("addRow", null, row, "first");
					$("#alterpopupWindow").jqxWindow("close");
				}
			}
		});
					
		$("#alterpopupWindow").jqxWindow({
			width: 600, height: 150, resizable: false, isModal: true, autoOpen: false, cancelButton: $("#alterCancel"),
			modalOpacity: 0.7, theme: theme
		});

		$("#alterSave").click(function(){
			$("#formAdd").jqxValidator("validate");
		});
	});
</script>