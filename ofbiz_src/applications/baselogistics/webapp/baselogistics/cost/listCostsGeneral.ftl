<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
<script type="text/javascript" src="/logresources/js/viewFileScanOrder.js"></script>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<#assign localeStr = "VI" />
<#if locale = "en">
    <#assign localeStr = "EN" />
</#if>

<script type="text/javascript">
	var colorArr = [10];
	colorArr[0] = "rgb(94, 234, 248)";
	colorArr[1] = "rgb(191, 219, 147)";
	colorArr[2] = "rgb(250, 197, 246)";
	colorArr[3] = "rgb(190, 255, 0)";
	colorArr[4] = "rgb(54, 193, 56)";
	colorArr[5] = "rgb(229, 149, 135)";
	colorArr[6] = "greenyellow";
	colorArr[7] = "rgb(144, 181, 204)";
	colorArr[8] = "rgb(222, 227, 14)";
	colorArr[9] = "rgb(219, 218, 133)";
	
	<#assign company = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	<#assign partyAcctg = delegator.findOne("PartyAcctgPreference", {"partyId" : company}, false)/>
	<#assign costAccBases = Static["com.olbius.baselogistics.cost.CostServices"].getCostAccBase(delegator, parameters.invoiceItemTypeId?if_exists)>
	var costAccBaseData = [];	
	<#if costAccBases?has_content>
		<#list costAccBases as item>
			var row = {};
			<#assign description = StringUtil.wrapString(item.description?if_exists)/>
			row['costAccBaseId'] = "${item.costAccBaseId?if_exists}";
			row['invoiceItemTypeId']= "${item.invoiceItemTypeId?if_exists}";
			row['description'] = "${description?if_exists}";
			costAccBaseData[${item_index}] = row;
		</#list>
	</#if>
	
	<#assign currencyUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false) />
	var currencyUomData = new Array();
	<#list currencyUoms as item>
		var row = {};
		row['currencyUomId'] = '${item.uomId?if_exists}';
		row['description'] = '${item.abbreviation?if_exists}';
		currencyUomData[${item_index}] = row;
	</#list>
	
	<#assign timePeriod = delegator.findList("CustomTimePeriod", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("periodTypeId", "SALES_YEAR"), null, null, null, false) />
	var yearData = new Array();
	<#list timePeriod as item>
		var row = {};
		row['yearNumber'] = '${item.periodName?if_exists}';
		row['description'] = '${item.periodName?if_exists}';
		yearData[${item_index}] = row;
	</#list>
	
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "COST_ACC_STATUS"), null, null, null, false)>
	var statusData = [];
	<#list statuses as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("description", locale)) />
		row['statusId'] = '${item.statusId}';
		row['description'] = '${description}';
		statusData[${item_index}] = row;
	</#list>
	
	var baseCurrencyUomId = '${partyAcctg.baseCurrencyUomId}';
</script>
<div id="yearFilter">
	<div class="row-fluid margin-bottom10">	
		<div class="span4" style="text-align: right">
			<div class="asterisk"> ${uiLabelMap.Year}: </div>
		</div>
		<div class="span7">	
			<div id="yearId" class="green-label"></div>
		</div>
	</div>
</div>
<div id="grid">

	<#assign dataFieldCost="[{ name: 'invoiceItemTypeId', type: 'string'},
	{ name: 'description', type: 'string'},
	{ name: 'month01', type: 'string'},
	{ name: 'month02', type: 'string'},
	{ name: 'month03', type: 'string'},
	{ name: 'month04', type: 'string'},
	{ name: 'month05', type: 'string'},
	{ name: 'month06', type: 'string'},
	{ name: 'month07', type: 'string'},
	{ name: 'month08', type: 'string'},
	{ name: 'month09', type: 'string'},
	{ name: 'month10', type: 'string'},
	{ name: 'month11', type: 'string'},
	{ name: 'month12', type: 'string'},
	{ name: 'organizationPartyId', type: 'string'},
	{ name: 'departmentId', type: 'string'},
	{ name: 'isParent', type: 'boolean'},
	{ name: 'deep', type: 'number'},
	{ name: 'maxDeep', type: 'number'},
	]"/>
	<#assign columnlistCost="
	{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true, 
	        groupable: false, draggable: false, resizable: false,
	        datafield: '', columntype: 'number', width: 50,
	        cellsrenderer: function (row, column, value) {
	            return '<span style=margin:4px;>' + (value + 1) + '</span>';
	        }
    },
	{ text: '${StringUtil.wrapString(uiLabelMap.InvoiceItemType)}' , datafield: 'description', minwidth: 200, align: 'left', pinned: true, cellsalign: 'left',
		cellsrenderer: function(row, colum, value){
	        var data = $('#jqxgridCost').jqxGrid('getrowdata', row);
	        var isParent = data.isParent;
	        var deep = data.deep;
	        var maxDeep = data.maxDeep;
	        for (var i = 0; i < colorArr.length; i ++){
	        	if (i == deep && deep < maxDeep){
	        		return '<span style=\"text-align: left;\" title=' + value + '>'+value+'</span>';
	        	}
	        }
	        return '<span style=\"text-align: left\" title=' + value + '>' + value + '</span>'
		}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.Month)} 1' , datafield: 'month01', width: 100, align: 'left', cellsalign: 'right',
		cellsrenderer: function(row, colum, value){
		        var data = $('#jqxgridCost').jqxGrid('getrowdata', row);
		        var isParent = data.isParent;
		        var deep = data.deep;
		        var maxDeep = data.maxDeep;
		        for (var i = 0; i < colorArr.length; i ++){
		        	if (i == deep && deep < maxDeep){
		        		return '<span style=\"text-align: right; background-color: '+colorArr[i]+'\" title=' + formatcurrency(value, baseCurrencyUomId) + '>'+formatcurrency(value, baseCurrencyUomId)+'</span>';
		        	}
		        }
		        return '<span style=\"text-align: right\" title=' + formatcurrency(value, baseCurrencyUomId) + '>' + formatcurrency(value, baseCurrencyUomId) + '</span>'
		}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.Month)} 2' , datafield: 'month02', width: 100, align: 'left', cellsalign: 'right',
		cellsrenderer: function(row, colum, value){
	        var data = $('#jqxgridCost').jqxGrid('getrowdata', row);
	        var isParent = data.isParent;
	        var deep = data.deep;
	        var maxDeep = data.maxDeep;
	        for (var i = 0; i < colorArr.length; i ++){
	        	if (i == deep && deep < maxDeep){
	        		return '<span style=\"text-align: right; background-color: '+colorArr[i]+'\" title=' + formatcurrency(value, baseCurrencyUomId) + '>'+formatcurrency(value, baseCurrencyUomId)+'</span>';
	        	}
	        }
	        return '<span style=\"text-align: right\" title=' + formatcurrency(value, baseCurrencyUomId) + '>' + formatcurrency(value, baseCurrencyUomId) + '</span>'
		}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.Month)} 3' , datafield: 'month03', width: 100, align: 'left', cellsalign: 'right',
		cellsrenderer: function(row, colum, value){
	        var data = $('#jqxgridCost').jqxGrid('getrowdata', row);
	        var isParent = data.isParent;
	        var deep = data.deep;
	        var maxDeep = data.maxDeep;
	        for (var i = 0; i < colorArr.length; i ++){
	        	if (i == deep && deep < maxDeep){
	        		return '<span style=\"text-align: right; background-color: '+colorArr[i]+'\" title=' + formatcurrency(value, baseCurrencyUomId) + '>'+formatcurrency(value, baseCurrencyUomId)+'</span>';
	        	}
	        }
	        return '<span style=\"text-align: right\" title=' + formatcurrency(value, baseCurrencyUomId) + '>' + formatcurrency(value, baseCurrencyUomId) + '</span>'
		}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.Month)} 4' , datafield: 'month04', width: 100, align: 'left', cellsalign: 'right',
		cellsrenderer: function(row, colum, value){
	        var data = $('#jqxgridCost').jqxGrid('getrowdata', row);
	        var isParent = data.isParent;
	        var deep = data.deep;
	        var maxDeep = data.maxDeep;
	        for (var i = 0; i < colorArr.length; i ++){
	        	if (i == deep && deep < maxDeep){
	        		return '<span style=\"text-align: right; background-color: '+colorArr[i]+'\" title=' + formatcurrency(value, baseCurrencyUomId) + '>'+formatcurrency(value, baseCurrencyUomId)+'</span>';
	        	}
	        }
	        return '<span style=\"text-align: right\" title=' + formatcurrency(value, baseCurrencyUomId) + '>' + formatcurrency(value, baseCurrencyUomId) + '</span>'
		}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.Month)} 5' , datafield: 'month05', width: 100, align: 'left', cellsalign: 'right',
		cellsrenderer: function(row, colum, value){
	        var data = $('#jqxgridCost').jqxGrid('getrowdata', row);
	        var isParent = data.isParent;
	        var deep = data.deep;
	        var maxDeep = data.maxDeep;
	        for (var i = 0; i < colorArr.length; i ++){
	        	if (i == deep && deep < maxDeep){
	        		return '<span style=\"text-align: right; background-color: '+colorArr[i]+'\" title=' + formatcurrency(value, baseCurrencyUomId) + '>'+formatcurrency(value, baseCurrencyUomId)+'</span>';
	        	}
	        }
	        return '<span style=\"text-align: right\" title=' + formatcurrency(value, baseCurrencyUomId) + '>' + formatcurrency(value, baseCurrencyUomId) + '</span>'
		}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.Month)} 6' , datafield: 'month06', width: 100, align: 'left', cellsalign: 'right',
		cellsrenderer: function(row, colum, value){
	        var data = $('#jqxgridCost').jqxGrid('getrowdata', row);
	        var isParent = data.isParent;
	        var deep = data.deep;
	        var maxDeep = data.maxDeep;
	        for (var i = 0; i < colorArr.length; i ++){
	        	if (i == deep && deep < maxDeep){
	        		return '<span style=\"text-align: right; background-color: '+colorArr[i]+'\" title=' + formatcurrency(value, baseCurrencyUomId) + '>'+formatcurrency(value, baseCurrencyUomId)+'</span>';
	        	}
	        }
	        return '<span style=\"text-align: right\" title=' + formatcurrency(value, baseCurrencyUomId) + '>' + formatcurrency(value, baseCurrencyUomId) + '</span>'
		}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.Month)} 7' , datafield: 'month07', width: 100, align: 'left', cellsalign: 'right',
		cellsrenderer: function(row, colum, value){
	        var data = $('#jqxgridCost').jqxGrid('getrowdata', row);
	        var isParent = data.isParent;
	        var deep = data.deep;
	        var maxDeep = data.maxDeep;
	        for (var i = 0; i < colorArr.length; i ++){
	        	if (i == deep && deep < maxDeep){
	        		return '<span style=\"text-align: right; background-color: '+colorArr[i]+'\" title=' + formatcurrency(value, baseCurrencyUomId) + '>'+formatcurrency(value, baseCurrencyUomId)+'</span>';
	        	}
	        }
	        return '<span style=\"text-align: right\" title=' + formatcurrency(value, baseCurrencyUomId) + '>' + formatcurrency(value, baseCurrencyUomId) + '</span>'
		}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.Month)} 8' , datafield: 'month08', width: 100, align: 'left', cellsalign: 'right',
		cellsrenderer: function(row, colum, value){
	        var data = $('#jqxgridCost').jqxGrid('getrowdata', row);
	        var isParent = data.isParent;
	        var deep = data.deep;
	        var maxDeep = data.maxDeep;
	        for (var i = 0; i < colorArr.length; i ++){
	        	if (i == deep && deep < maxDeep){
	        		return '<span style=\"text-align: right; background-color: '+colorArr[i]+'\" title=' + formatcurrency(value, baseCurrencyUomId) + '>'+formatcurrency(value, baseCurrencyUomId)+'</span>';
	        	}
	        }
	        return '<span style=\"text-align: right\" title=' + formatcurrency(value, baseCurrencyUomId) + '>' + formatcurrency(value, baseCurrencyUomId) + '</span>'
		}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.Month)} 9' , datafield: 'month09', width: 100, align: 'left', cellsalign: 'right',
		cellsrenderer: function(row, colum, value){
	        var data = $('#jqxgridCost').jqxGrid('getrowdata', row);
	        var isParent = data.isParent;
	        var deep = data.deep;
	        var maxDeep = data.maxDeep;
	        for (var i = 0; i < colorArr.length; i ++){
	        	if (i == deep && deep < maxDeep){
	        		return '<span style=\"text-align: right; background-color: '+colorArr[i]+'\" title=' + formatcurrency(value, baseCurrencyUomId) + '>'+formatcurrency(value, baseCurrencyUomId)+'</span>';
	        	}
	        }
	        return '<span style=\"text-align: right\" title=' + formatcurrency(value, baseCurrencyUomId) + '>' + formatcurrency(value, baseCurrencyUomId) + '</span>'
		}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.Month)} 10' , datafield: 'month10', width: 100, align: 'left', cellsalign: 'right',
		cellsrenderer: function(row, colum, value){
	        var data = $('#jqxgridCost').jqxGrid('getrowdata', row);
	        var isParent = data.isParent;
	        var deep = data.deep;
	        var maxDeep = data.maxDeep;
	        for (var i = 0; i < colorArr.length; i ++){
	        	if (i == deep && deep < maxDeep){
	        		return '<span style=\"text-align: right; background-color: '+colorArr[i]+'\" title=' + formatcurrency(value, baseCurrencyUomId) + '>'+formatcurrency(value, baseCurrencyUomId)+'</span>';
	        	}
	        }
	        return '<span style=\"text-align: right\" title=' + formatcurrency(value, baseCurrencyUomId) + '>' + formatcurrency(value, baseCurrencyUomId) + '</span>'
		}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.Month)} 11' , datafield: 'month11', width: 100, align: 'left', cellsalign: 'right',
		cellsrenderer: function(row, colum, value){
	        var data = $('#jqxgridCost').jqxGrid('getrowdata', row);
	        var isParent = data.isParent;
	        var deep = data.deep;
	        var maxDeep = data.maxDeep;
	        for (var i = 0; i < colorArr.length; i ++){
	        	if (i == deep && deep < maxDeep){
	        		return '<span style=\"text-align: right; background-color: '+colorArr[i]+'\" title=' + formatcurrency(value, baseCurrencyUomId) + '>'+formatcurrency(value, baseCurrencyUomId)+'</span>';
	        	}
	        }
	        return '<span style=\"text-align: right\" title=' + formatcurrency(value, baseCurrencyUomId) + '>' + formatcurrency(value, baseCurrencyUomId) + '</span>'
		}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.Month)} 12' , datafield: 'month12', width: 100, align: 'left', cellsalign: 'right',
		cellsrenderer: function(row, colum, value){
	        var data = $('#jqxgridCost').jqxGrid('getrowdata', row);
	        var isParent = data.isParent;
	        var deep = data.deep;
	        var maxDeep = data.maxDeep;
	        for (var i = 0; i < colorArr.length; i ++){
	        	if (i == deep && deep < maxDeep){
	        		return '<span style=\"text-align: right; background-color: '+colorArr[i]+'\" title=' + formatcurrency(value, baseCurrencyUomId) + '>'+formatcurrency(value, baseCurrencyUomId)+'</span>';
	        	}
	        }
	        return '<span style=\"text-align: right\" title=' + formatcurrency(value, baseCurrencyUomId) + '>' + formatcurrency(value, baseCurrencyUomId) + '</span>'
		}
	},
	"/>
	
	<@jqGrid id="jqxgridCost" addType="popup" dataField=dataFieldCost columnlist=columnlistCost clearfilteringbutton="true" customTitleProperties="GeneralCostOfLogistics"
	showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false"
	url="jqxGeneralServicer?sname=getCostAccounting&invoiceItemTypeId=${parameters.invoiceItemTypeId?if_exists}&year=${parameters.year?if_exists}&organizationPartyId=${parameters.organizationPartyId?if_exists}&departmentId=${parameters.departmentId?if_exists}"
	customcontrol1="icon-plus-sign@${uiLabelMap.AddCosts}@javascript: void(0);@showPopupAddCosts()"
	/>
</div>

<div id="alterpopupWindow" class="hide popup-bound">
	<div class="row-fluid">
	   ${uiLabelMap.CostsRecord}
	</div>
	<div class='form-window-container'>
		<div class="row-fluid">
			<input type="hidden" id="currencyUomId"/>
			<div class="span6">
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk"> ${uiLabelMap.InvoiceItemType}: </div>
					</div>
					<div class="span7">	
						<div id="costAccBaseId" class="green-label"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk"> ${uiLabelMap.AmountMoney}: </div>
					</div>
					<div class="span7">	
						<div id="costPriceActual" class="green-label"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk"> ${uiLabelMap.Unit}: </div>
					</div>
					<div class="span7">	
						<div id="currencyUomIdDT" class="green-label"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div style="margin-right: 10px"> ${uiLabelMap.Description}: </div>
					</div>
					<div class="span7">	
						<textarea id="description" class='text-popup' style="width: 600px; height: 60px"></textarea>
					</div>
				</div>
			</div>
			<div class="span6 no-left-margin">
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<div class="asterisk"> ${uiLabelMap.DatetimeCreated}: </div>
					</div>
					<div class="span7">	
						<div id="costAccDate" class="green-label"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<div class="asterisk"> ${uiLabelMap.BLPartyName}: </div>
					</div>
					<div class="span7">	
						<div id="partyId" style="width: 100%" class="green-label">
							<div id="jqxgridParty">
				            </div>
						</div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<div> ${uiLabelMap.UploadFileScan}: </div>
					</div>
					<div class="span3">	
						<button id="uploadImages" class='btn btn-primary btn-mini'><i class="fa fa-upload"></i>${uiLabelMap.Upload}</button>
					</div>
					<div class="span4">	
						<div class="green-label" id="txtImage"></div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action popup-footer">
			<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
			<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
		</div>
	</div>
</div>

<div id="jqxFileScanUpload" style="display: none" class="popup-bound">
	<div>
	    <span>
	        ${uiLabelMap.UploadFileScan}
	    </span>
	</div>
	<div style="overflow: hidden; text-align: center">
		<input multiple type="file" id="attachFile">
		</input>
		<div class="form-action popup-footer">
			<button id="uploadCancelButton" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="uploadOkButton" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<div id="detailPopupWindow" class="hide popup-bound">
	<div>
	   ${uiLabelMap.CostsDetail}
	</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid margin-top10">
				<div class="span6">
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.InvoiceItemType}: </div>
						</div>
						<div class="span7">	
							<div id="invoiceItemTypeId" class="green-label"></div>
						</div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.FromDate}: </div>
						</div>
						<div class="span7">	
							<div id="fromDate" class="green-label"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.Organization}: </div>
						</div>
						<div class="span7">	
							<div id="organizationPartyId" class="green-label"></div>
						</div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.Department}: </div>
						</div>
						<div class="span7">	
							<div id="departmentId" class="green-label"></div>
						</div>
					</div>
				</div>
			</div>
			<div>
				<div style="margin-left: 20px">
					<div id="jqxgridCostAcc"></div>
				</div>
			</div>
			<div class="form-action popup-footer">
				<button id="detailCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
				<button id="detailSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript">
	var now = new Date();
	var nowYearNumber = now.getFullYear();
	$('#yearId').jqxDropDownList({ width: 200, selectedIndex: 0, source: yearData, theme: theme, displayMember: 'description', valueMember: 'yearNumber', placeHolder: '${uiLabelMap.PleaseSelectTitle}'});
	$('#yearId').jqxDropDownList('val', nowYearNumber);
	$('#alterpopupWindow').jqxWindow({resizable: false, autoOpen: false, height: 300, width: 950, isModal: true, modalZIndex: 10000,theme:this.theme, collapsed:false, cancelButton: '#alterCancel'});
	initValidate($("#alterpopupWindow"));
	$('#costAccBaseId').jqxDropDownList({ width: 200, selectedIndex: 0, source: costAccBaseData, theme: theme, displayMember: 'description', valueMember: 'costAccBaseId', placeHolder: '${uiLabelMap.PleaseSelectTitle}'});
	$('#currencyUomId').jqxDropDownList({ width: 200, selectedIndex: 0, source: currencyUomData, theme: theme, displayMember: 'description', valueMember: 'currencyUomId', placeHolder: '${uiLabelMap.PleaseSelectTitle}'});
	$('#currencyUomIdDT').text('${partyAcctg.baseCurrencyUomId}');
	$("#currencyUomId").val('${partyAcctg.baseCurrencyUomId}');
	$("#costAccDate").jqxDateTimeInput({width: 200, formatString: 'dd/MM/yyyy', disabled: false});
	$("#costPriceActual").jqxNumberInput({ width: 200,  max : 999999999999999999, digits: 18, decimalDigits:2, spinButtons: false, min: 0});
	
	var urlTo = "JqxGetParties"
	var configCustomer = {
			useUrl: true,
			root: 'results',
			widthButton: '200',
			showdefaultloadelement: false,
			autoshowloadelement: false,
			width: '400',
			datafields: [{name: 'partyId', type: 'string'}, {name: 'fullName', type: 'string'}],
			columns: [
				{text: '${uiLabelMap.BACCCustomerId}', datafield: 'partyId', width: '30%'},
				{text: '${uiLabelMap.BACCFullName}', datafield: 'fullName', width: '70%'}
			],
			url: urlTo,
			useUtilFunc: true,
			
			key: 'partyId',
			description: ['fullName'],
			showDetail: true,
			pagesize: 10,
	};
	accutils.initDropDownButton($("#partyId"), $("#jqxgridParty"), null, configCustomer, []);
	
	$('#detailPopupWindow').jqxWindow({resizable: false, autoOpen: false, height: 450, width: 900, maxWidth: 1000, minWidth: 600, isModal: true, modalZIndex: 10000, theme:this.theme, collapsed:false, cancelButton: '#detailCancel'});
	$('#yearId').on("change", function(){
		var tmpS = $("#jqxgridCost").jqxGrid('source');
	 	var year = $("#yearId").val();
	 	tmpS._source.url = "jqxGeneralServicer?sname=getCostAccounting&invoiceItemTypeId=${parameters.invoiceItemTypeId?if_exists}&year="+year+"&organizationPartyId=${parameters.organizationPartyId?if_exists}&departmentId=${parameters.departmentId?if_exists}";
	 	$("#jqxgridCost").jqxGrid('source', tmpS);
	});
	
	$('#alterpopupWindow').on('close', function (event) {
		$('#alterpopupWindow').jqxValidator('hide');
	});
	
	$('#detailPopupWindow').on('close', function (event) {
		if(checkClickDetailSave == true){
			$("#jqxgridCost").jqxGrid("updatebounddata");
			displayEditSuccessMessage('jqxgridCost');
		}
		checkClickDetailSave = false;
	});
	
	/*$("#alterSave").on("click", function(){
		if (!$('#alterpopupWindow').jqxValidator('validate')){
			return false;
		}
		bootbox.dialog("${uiLabelMap.AreYouSureCreate}?", 
		[{"label": "${StringUtil.wrapString(uiLabelMap.CommonCancel)}", 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
            "callback": function() {bootbox.hideAll();}
        }, 
        {"label": "${StringUtil.wrapString(uiLabelMap.OK)}",
            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
            "callback": function() {
            	$.ajax({
					type: "POST",
					url: "createLogCostAccouting",
					data: {
						costAccBaseId: $("#costAccBaseId").val(),
						costAccDate: $('#costAccDate').jqxDateTimeInput('getDate').getTime(),
						description: $("#description").val(),
						costPriceActual: $("#costPriceActual").val(),
						currencyUomId: $("#currencyUomId").val(),
					},
					async: false,
					success: function (res){
						$("#jqxgridCost").jqxGrid("updatebounddata");
						displayEditSuccessMessage('jqxgridCost');
					}
				});
		    	$('#alterpopupWindow').jqxWindow('close');
            }
		}]);
	});*/
	
	function showPopupAddCosts(){
		$('#alterpopupWindow').jqxWindow('open');
	}
	
	function initValidate(element){
		element.jqxValidator({
			rules:[
			{
				input: '#costAccBaseId', 
	            message: '${StringUtil.wrapString(uiLabelMap.DmsFieldRequired?default(''))}', 
	            action: 'blur', 
	            rule: function (input) {	
	         	   	var tmp = $('#costAccBaseId').jqxDropDownList('getSelectedItem');
	                return tmp ? true : false;
	            }
			},
			{
				input: '#costPriceActual', 
	            message: '${StringUtil.wrapString(uiLabelMap.DmsFieldRequired?default(''))}', 
	            action: 'blur', 
	            rule: function (input) {	
	         	   	var tmp = $('#costPriceActual').val();
	                return tmp ? true : false;
	            }
			},
			{
				input: '#costAccDate', 
	            message: '${StringUtil.wrapString(uiLabelMap.DmsFieldRequired?default(''))}', 
	            action: 'blur', 
	            rule: function (input) {	
	         	   	var tmp = $('#costAccDate').jqxDateTimeInput('getDate');
	                return tmp ? true : false;
	            }
			},
			
			{
				input: '#currencyUomId', 
	            message: '${StringUtil.wrapString(uiLabelMap.DmsFieldRequired?default(''))}', 
	            action: 'blur', 
	            rule: function (input) {	
	         	   	var tmp = $('#currencyUomId').jqxDropDownList('getSelectedItem');
	                return tmp ? true : false;
	            }
			},
			{
				input: '#partyId', 
	            message: '${StringUtil.wrapString(uiLabelMap.DmsFieldRequired?default(''))}', 
	            action: 'blur', 
	            rule: function (input) {	
	         	   	var tmp = $('#partyId').jqxDropDownButton('getContent');
	                return tmp ? true : false;
	            }
			},
			],
		});
	}
	
	$('#jqxgridCost').on('rowdoubleclick', function (event) {
		var selectedrowindex = $('#jqxgridCost').jqxGrid('selectedrowindex');
   	 	var data = $("#jqxgridCost").jqxGrid('getrowdata', selectedrowindex);
   	 	var costObj = null;
		$.ajax({
			type: "POST",
			async: false,
			url: "getCostAccoutingDetail",
			data: {
				invoiceItemTypeId: data.invoiceItemTypeId,
				organizationPartyId: data.organizationPartyId,
				departmentId: data.departmentId,
			},
			success: function (res){
				costObj = res;
			}
		});
		$("#invoiceItemTypeId").text(costObj.description);
		$("#organizationPartyId").text(costObj.organizationName);
		$("#departmentId").text(costObj.departmentName);
		
		var fromDate = new Date(costObj.fromDate);
		if (fromDate.getMonth()+1 < 10){
			if (fromDate.getDate() < 10){
				$("#fromDate").text('0' + fromDate.getDate() + '/0' + (fromDate.getMonth()+1) + '/' + fromDate.getFullYear());
			} else {
				$("#fromDate").text(fromDate.getDate() + '/0' + (fromDate.getMonth()+1) + '/' + fromDate.getFullYear());
			}
		} else {
			if (fromDate.getDate() < 10){
				$("#fromDate").text('0' + fromDate.getDate() + '/' + (fromDate.getMonth()+1) + '/' + fromDate.getFullYear());
			} else {
				$("#fromDate").text(fromDate.getDate() + '/' + (fromDate.getMonth()+1) + '/' + fromDate.getFullYear());
			}
		}
		
		var listCostAccTmp = costObj.listCostAccs;
		var listCostAccs = [];
		
		if (listCostAccTmp != undefined && listCostAccTmp.length > 0){
			for (var i = 0; i < listCostAccTmp.length; i ++){
				var item = {};
				item["costAccountingId"] = listCostAccTmp[i].costAccountingId;
				item["costAccDate"] = new Date(listCostAccTmp[i].costAccDate.time);
				item["costPriceActual"] = listCostAccTmp[i].costPriceActual;
				item["createdByUserLogin"] = listCostAccTmp[i].createdByUserLogin;
				item["changedByUserLogin"] = listCostAccTmp[i].changedByUserLogin;
				item["currencyUomId"] = listCostAccTmp[i].currencyUomId;
				item["statusId"] = listCostAccTmp[i].statusId;
				item["description"] = listCostAccTmp[i].description;
				item["invoiceItemTypeId"] = data.invoiceItemTypeId;
			    item["organizationPartyId"] = data.organizationPartyId;
		        item["departmentId"] =  data.departmentId;
				
				$.ajax({
					type: "POST",
					async: false,
					url: "getUserLoginCreatedInfo",
					data: {
						partyId: listCostAccTmp[i].createdByUserLogin,
					},
					success: function (res){
						item["createdUserLoginName"] = res.partyName;
					}
				});
				
				$.ajax({
					type: "POST",
					async: false,
					url: "getUserLoginCreatedInfo",
					data: {
						partyId: listCostAccTmp[i].changedByUserLogin,
					},
					success: function (res){
						item["changedUserLoginName"] = res.partyName;
					}
				});
				
				listCostAccs.push(item);
			}
		}
		
		loadCostAcc(listCostAccs);
		$("#detailPopupWindow").jqxWindow('open');
	});
	
	var checkClickDetailSave = false;
	$("#detailSave").on("click", function(){
		var selectedIndexs = $('#jqxgridCostAcc').jqxGrid('getselectedrowindexes');
		if (selectedIndexs.length < 1){
			 bootbox.dialog("${uiLabelMap.YouNotYetChooseItem}!", [{
	                "label" : "${uiLabelMap.OK}",
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                }]
            );
			return false;
		}
		bootbox.dialog("${uiLabelMap.AreYouSureUpdate}?", 
		[{"label": "${StringUtil.wrapString(uiLabelMap.CommonCancel)}", 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
            "callback": function() {bootbox.hideAll();}
        }, 
        {"label": "${StringUtil.wrapString(uiLabelMap.OK)}",
            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
            "callback": function() {
            	var listToUpdate = [];
            	for(var i = 0; i < selectedIndexs.length; i++){
					var data = $('#jqxgridCostAcc').jqxGrid('getrowdata', selectedIndexs[i]);
					var item = {};
					item['costAccountingId'] = data.costAccountingId;
					item['costPriceActual'] = data.costPriceActual;
					item['description'] = data.description;
					listToUpdate.push(item);
				}
				checkClickDetailSave = true;
				var listCostAccounting = JSON.stringify(listToUpdate);
				$.ajax({
					type: "POST",
					async: false,
					url: "updateMultiCostAccounting",
					data: {
						listCostAccounting: listCostAccounting,
					},
					success: function (res){
						$("#jqxgridCost").jqxGrid("updatebounddata");
						displayEditSuccessMessage('jqxgridCost');
					}
				});
				$("#detailPopupWindow").jqxWindow('close');
            }
		}]);
	});
	
	$("#detailPopupWindow").on("close", function (event){
		$('#jqxgridCostAcc').jqxGrid('clearselection');
	});
	
	$("#alterpopupWindow").on("close", function (event){
		$('#costPriceActual').val('');
		$('#description').val('');
		$("#costAccDate").jqxDateTimeInput('val', new Date());
	});
	
	function loadCostAcc(valueDataSoure){
		var sourceData =
		    {
	        datafields:[{ name: 'costAccountingId', type: 'string' },
	                    { name: 'costPriceActual', type: 'string' },
	                 	{ name: 'createdByUserLogin', type: 'string' },
	                 	{ name: 'createdUserLoginName', type: 'string' },
	                 	{ name: 'changedByUserLogin', type: 'string' },
	                 	{ name: 'changedUserLoginName', type: 'string' },
	                 	{ name: 'currencyUomId', type: 'string' },
	                 	{ name: 'statusId', type: 'string' },
	                 	{ name: 'costAccDate', type: 'date', other: 'Timestamp' },
						{ name: 'description', type: 'string' },
						{ name: 'invoiceItemTypeId', type: 'string' },
						{ name: 'organizationPartyId', type: 'string' },
						{ name: 'departmentId', type: 'string' },
			 		 	],
	        localdata: valueDataSoure,
	        datatype: "array",
	    };
	    var dataAdapter = new $.jqx.dataAdapter(sourceData);
	    $("#jqxgridCostAcc").jqxGrid({
        source: dataAdapter,
        filterable: false,
        showfilterrow: false,
        theme: 'olbius',
        rowsheight: 25,
        width: '100%',
        height: 240,
        enabletooltips: true,
        autoheight: false,
        pageable: true,
        pagesize: 5,
        editable: true,
        selectionmode: 'checkbox',
        showtoolbar: true,
		rendergridrows: function(obj)
		{
			return obj.data;
		},
        rendertoolbar: function (toolbar) {
			if(toolbar.html()){
				return;
			}
         	var container = $("<div id='toolbarcontainer' class='widget-header'></div>");
             toolbar.append(container);
             container.append('<h4>${uiLabelMap.ListCostAcc}</h4>');
//             container.append('<button id="btnAdd" style="margin-left:5px; margin-top:5px; cursor: pointer;"><i class="icon-plus-sign"></i>${uiLabelMap.CommonAdd}</button>');
             container.append('<button id="btnDel" style="margin-left:10px; margin-top:5px; margin-right:10px; cursor: pointer;"><i class="icon-trash" style="color: red; margin-right: 2px"></i>${uiLabelMap.CommonDelete}</button>');
//             $("#btnAdd").jqxButton();
             $("#btnDel").jqxButton();
             // create new row.
//             $("#btnAdd").on('click', function () {
//            	 $("#alterpopupWindow").jqxWindow('open');
//             });
             
             // create new row.
             $("#btnDel").on('click', function () {
				var selectedIndexs = $('#jqxgridCostAcc').jqxGrid('getselectedrowindexes');
				if (selectedIndexs.length < 1){
					return false;
				}
				bootbox.dialog("${uiLabelMap.ConfirmDelete}", 
				[{"label": "${StringUtil.wrapString(uiLabelMap.CommonCancel)}", 
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		            "callback": function() {bootbox.hideAll();}
		        }, 
		        {"label": "${StringUtil.wrapString(uiLabelMap.OK)}",
		            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		            "callback": function() {
		            	var dataTmp = $('#jqxgridCostAcc').jqxGrid('getrowdata', selectedIndexs[0]);
         		    	var listToDelete = [];
         		    	for(var i = 0; i < selectedIndexs.length; i++){
         		    		var data = $('#jqxgridCostAcc').jqxGrid('getrowdata', selectedIndexs[i]);
         		    		var item = {};
         		    		item['costAccountingId'] = data.costAccountingId;
         		    		listToDelete.push(item);
         		    	}
         		    	var listCostAccounting = JSON.stringify(listToDelete);
         		    	checkClickDetailSave = true;
         		    	$.ajax({
		 					type: "POST",
		 					async: false,
		 					url: "deleteMultiCostAccounting",
		 					data: {
		 						listCostAccounting: listCostAccounting,
		 					},
		 					success: function (res){
		 						var costObj = null;
		 						$.ajax({
		 							type: "POST",
		 							async: false,
		 							url: "getCostAccoutingDetail",
		 							data: {
		 								invoiceItemTypeId: dataTmp.invoiceItemTypeId,
		 								organizationPartyId: dataTmp.organizationPartyId,
		 								departmentId: dataTmp.departmentId,
		 							},
		 							success: function (res){
		 								costObj = res;
		 							}
		 						});
		 						var listCostAccTmp = costObj.listCostAccs;
		 						var listCostAccs = [];
		 						
		 						if (listCostAccTmp != undefined && listCostAccTmp.length > 0){
		 							for (var i = 0; i < listCostAccTmp.length; i ++){
		 								var item = {};
		 								item["costAccountingId"] = listCostAccTmp[i].costAccountingId;
		 								item["costAccDate"] = new Date(listCostAccTmp[i].costAccDate.time);
		 								item["costPriceActual"] = listCostAccTmp[i].costPriceActual;
		 								item["createdByUserLogin"] = listCostAccTmp[i].createdByUserLogin;
		 								item["changedByUserLogin"] = listCostAccTmp[i].changedByUserLogin;
		 								item["currencyUomId"] = listCostAccTmp[i].currencyUomId;
		 								item["statusId"] = listCostAccTmp[i].statusId;
		 								item["description"] = listCostAccTmp[i].description;
		 								item["invoiceItemTypeId"] = data.invoiceItemTypeId;
		 							    item["organizationPartyId"] = data.organizationPartyId;
		 						        item["departmentId"] =  data.departmentId;
		 								
		 								$.ajax({
		 									type: "POST",
		 									async: false,
		 									url: "getUserLoginCreatedInfo",
		 									data: {
		 										partyId: listCostAccTmp[i].createdByUserLogin,
		 									},
		 									success: function (res){
		 										item["createdUserLoginName"] = res.partyName;
		 									}
		 								});
		 								
		 								$.ajax({
		 									type: "POST",
		 									async: false,
		 									url: "getUserLoginCreatedInfo",
		 									data: {
		 										partyId: listCostAccTmp[i].changedByUserLogin,
		 									},
		 									success: function (res){
		 										item["changedUserLoginName"] = res.partyName;
		 									}
		 								});
		 								
		 								listCostAccs.push(item);
		 							}
		 						}
		 						
		 						loadCostAcc(listCostAccs);
							}
		            	 });
 				
		            	 $("#jqxgridCostAcc").jqxGrid('updatebounddata');
		            }
				}]);
         	});
 	    },
 	    
        columns: [	
				{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true, 
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<span style=margin:4px;>' + (value + 1) + '</span>';
				    }
				},
				{ text: '${uiLabelMap.CostAccId}', dataField: 'costAccountingId', width: 150, editable: false, pinned: true},
				{ text: '${uiLabelMap.AmountMoney}', dataField: 'costPriceActual', width: 150, editable: true, cellsformat: 'd', columntype: 'numberinput', cellsalign: 'right',
					cellsrenderer: function(row, colum, value){
				        return '<span style=\"text-align: right\" title=' + formatcurrency(value) + '>' + formatcurrency(value) + '</span>'
					},
					createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
						editor.jqxNumberInput({decimalDigits: 0, digits: 9});
					},
					validation: function (cell, value) {
	        	        if (!value) {
	        	            return { result: false, message: "${uiLabelMap.FieldRequired}" };
	        	        }
	        	        return true;
					},
					cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
						$('#jqxgridCostAcc').jqxGrid('selectrow', row);
					},
					
				},
				{ text: '${uiLabelMap.DatetimeCreated}', dataField: 'costAccDate', width: 150, editable: false, cellsalign: 'right', cellsformat: 'dd/MM/yyyy',
				},
				{ text: '${uiLabelMap.Status}', dataField: 'statusId', width: 150, editable: false, cellsalign: 'right', cellsformat: 'dd/MM/yyyy',
					 cellsrenderer: function(row, column, value){
						 for(var i = 0; i < statusData.length; i++){
							 if(value == statusData[i].statusId){
								 return '<span title=' + value + '>' + statusData[i].description + '</span>';
							 }
						 }
					 }
				},
				{ text: '${uiLabelMap.Description}', dataField: 'description', minwidth: 120, editable: true,
					 cellsrenderer: function(row, column, value){
						if (value == null || value == undefined || value == ""){
							return '<span>_NA_</span>';
						} 
					 },
					createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
						editor.jqxInput({});
					},
					cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
						$('#jqxgridCostAcc').jqxGrid('selectrow', row);
					},
				},
				{ text: '${uiLabelMap.CreatedBy}', dataField: 'createdUserLoginName', width: 150, editable: false},
				{ text: '${uiLabelMap.LastChangedBy}', dataField: 'changedUserLoginName', width: 150, editable: false},
				]
	    });
	}
	
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.AreYouSureCreate = "${uiLabelMap.AreYouSureCreate}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.NameOfImagesMustBeLessThan50Character = "${StringUtil.wrapString(uiLabelMap.NameOfImagesMustBeLessThan50Character)}";
</script>