<#assign glAccountTypes = delegator.findList("GlAccountType", null, null, null, null, true)/>
<#assign glAccountClasses = delegator.findList("GlAccountClass", null, null, null, null, true)/>
<script>
	var glAccountTypes = [
		<#list glAccountTypes as type>
			{
				<#assign description = StringUtil.wrapString(type.get("description", locale)?if_exists) />
				glAccountTypeId : "${type.glAccountTypeId}",
				description: "${description}"	
			},
		</#list>
	];
	
	var glAccountClasses = [
		<#list glAccountClasses as type>
			{
				<#assign description = StringUtil.wrapString(type.get("description", locale)?if_exists) />
				glAccountClassId : "${type.glAccountClassId}",
				description: "${description}"	
			},
		</#list>
	];
	var currentGrid;
	var currentAccTg = "";
	var currentIsPosted;
	var begineditrowdetail = function(row, datafield, columntype){
		GridUtils.hideGridMessage('rowdetail' + row);
		if(currentIsPosted == 'Y'){
			return false;
		}
		return true;
	};
	
var initDropDown = function(dropdown,grid){
		GridUtils.initDropDownButton({url : 'getListGLAccountOACsData',autoshowloadelement : true,width : 400,filterable : true,dropdown : {dropDownHorizontalAlignment : true},source: {cache : false, pagesize : 5}},
		[
			{name : 'glAccountId',type : 'string'},
			{name : 'accountCode',type : 'string'},									
			{name : 'accountName',type : 'string'}
		], 
		[
			{text : '${uiLabelMap.accountCode}',datafield : 'accountCode',width : '40%'},
			{text : '${uiLabelMap.accountName}',datafield : 'accountName'}
		]
		, null, grid,dropdown,'glAccountId');
	}
</script>
<@useLocalizationNumberFunction />
<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord){
	currentIsPosted = datarecord.isPosted;
	var isPosted = datarecord.isPosted;
	currentAccTg = datarecord.acctgTransId;
	var canEditEntry = true;
	if(isPosted == 'Y'){ canEditEntry = false;}
	var id = 'rowdetail' + datarecord.uid.toString();
	var grid = $($(parentElement).children()[0]);
	currentGrid = grid;
	var str = '<div id=\"container'+id+'\" style=\"background-color: transparent; overflow: auto;\"></div>'
			+ '<div id=\"jqxNotification'+id+'\"><div id=\"notificationContent'+id+'\"></div></div>';
	$(parentElement).prepend(str);
    if (grid != null) {
    	grid.attr('id', id);
    	var datafields = [{ name: 'acctgTransId', type: 'string' },
    				{ name: 'acctgTransEntrySeqId', type: 'string' },
                 	{ name: 'glAccountClassId', type: 'string' },
                 	{ name: 'reconcileStatusId', type: 'string' },
                 	{ name: 'glAccountId', type: 'string' },
                 	{ name: 'partyId', type: 'string' },
                 	{ name: 'productId', type: 'string' },
                 	{ name: 'groupName', type: 'string' },
                 	{ name: 'firstName', type: 'string' },
                 	{ name: 'middleName', type: 'string' },
                 	{ name: 'lastName', type: 'string' },
                 	{ name: 'glAccountTypeId', type: 'string' },
                 	{ name: 'debitCreditFlag', type: 'string' },
                 	{ name: 'origAmount', type: 'string' },
                 	{ name: 'accountName', type: 'string' },
                 	{ name: 'amount', type: 'string' },
                 	{ name: 'transDescription', type: 'string' }
                 	];
         var columns = [{ text: '${uiLabelMap.acctgTransEntrySeqId}', width: 150, dataField: 'acctgTransEntrySeqId', editable: false},
         { text: '${uiLabelMap.glAccountClassId}', width: 150, dataField: 'glAccountClassId',columntype: 'dropdownlist',
         	cellsrenderer: function(row, columns, value){
         		for(var x in glAccountClasses){
         			if(glAccountClasses[x].glAccountClassId == value){
         				return '<div class=\"cell-custom-grid\">'+glAccountClasses[x].description+'</div>';
         			}
         		}
         		return value;
         	},
			createeditor: function (row, column, editor) {
                editor.jqxDropDownList({ theme: theme, source: glAccountClasses, displayMember: 'description', valueMember: 'glAccountClassId', width: 150, dropDownWidth: 300, height: '25', filterable: true});
            },
			cellbeginedit: begineditrowdetail
         },{ text: '${uiLabelMap.reconcileStatusId}', width: 150, dataField: 'reconcileStatusId',columntype: 'dropdownlist',
         	cellsrenderer: function(row, columns, value){
         		for(var x in statusItemsData){
         			if(statusItemsData[x].statusId == value){
         				return '<div class=\"cell-custom-grid\">'+statusItemsData[x].description+'</div>';
         			}
         		}
         		return '<div class=\"cell-custom-grid\">'+value+'</div>';;
         	},
			createeditor: function (row, column, editor) {
                editor.jqxDropDownList({ theme: theme, source: statusItemsData, displayMember: 'description', valueMember: 'statusId', width: 150, dropDownWidth: 300, height: '25'});
            },
			cellbeginedit: begineditrowdetail
         },{ text: '${uiLabelMap.accountCode}', width: 150, dataField: 'glAccountId', columntype: 'template',
         	cellsrenderer: function(row, columns, value){
         		var data = $('#' + id).jqxGrid('getrowdata',row);
         		return '<span>'+ data.glAccountId + '-' + data.accountName  + '[' + data.glAccountId + ']'+'</span>';
         	},createeditor : function(row, cellvalue, editor, celltext, cellwidth, cellheight){
         		editor.append('<div id=\"jqxgridEditGlAccount\"></div>');
         		initDropDown(editor,$('#jqxgridEditGlAccount'));
         		editor.jqxDropDownButton('setContent',cellvalue);
         	},geteditorvalue : function(row,cellvalue,editor){
     			editor.jqxDropDownButton(\"close\");
                   var ini = $('#jqxgridEditGlAccount').jqxGrid('getselectedrowindex');
                    if(ini != -1){
                        var item = $('#jqxgridEditGlAccount').jqxGrid('getrowdata', ini);
                        var selectedPro = item.glAccountId;
                        return selectedPro;	
                    }
                    return cellvalue;
         	},
			cellbeginedit: begineditrowdetail
         },
         { text: '${uiLabelMap.organizationName}', width: 250, dataField: 'partyId',columntype: 'template',
         	cellsrenderer: function(row, columns, value, defaulthtml, columnproperties, rowdata){
         		var str = rowdata.groupName ? rowdata.groupName : '';
         		if(str){
         			return '<div class=\"cell-custom-grid\">'+str+'</div>';	
         		}
         		var first = rowdata.firstName ? rowdata.firstName : '';
         		var middle = rowdata.middleName ? rowdata.middleName : '';
         		var last = rowdata.lastName ? rowdata.lastName : '';
         		if(first || middle || last){
         			str = first + ' ' + middle + ' ' + last;	
         			return '<div class=\"cell-custom-grid\">'+str+'</div>';	
         		}
         		return '<div class=\"cell-custom-grid\">'+value+'</div>';	
         	},
         	createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
                editor.append('<div id=\"jqxPartyTranEntryEdit\"></div>');
                initPartySelect(editor, $('#jqxPartyTranEntryEdit'));
                editor.jqxDropDownButton('setContent', cellvalue);
              },
              geteditorvalue: function (row, cellvalue, editor) {
                  editor.jqxDropDownButton(\"close\");
                   var ini = $('#jqxPartyTranEntryEdit').jqxGrid('getselectedrowindex');
                    if(ini != -1){
                        var item = $('#jqxPartyTranEntryEdit').jqxGrid('getrowdata', ini);
                        var selectedPro = item.partyId;
                        return selectedPro;	
                    }
                    return cellvalue;
              },
			cellbeginedit: begineditrowdetail
         },{ text: '${uiLabelMap.glAccountTypeId}', width: 150, dataField: 'glAccountTypeId',columntype: 'dropdownlist',
         	cellsrenderer: function(row, columns, value){
         		for(var x in glAccountTypes){
         			if(glAccountTypes[x].glAccountTypeId == value){
         				return '<div class=\"cell-custom-grid\">'+glAccountTypes[x].description+'</div>';	
         			}
         		}
         		return '<div class=\"cell-custom-grid\">'+value+'</div>';
         	},
         	createeditor: function (row, column, editor) {
                editor.jqxDropDownList({ theme: theme, source: glAccountTypes, displayMember: 'description', valueMember: 'glAccountTypeId', width: 150, dropDownWidth: 300, height: '25', filterable: true});
            },
			cellbeginedit: begineditrowdetail
         }, { text: '${uiLabelMap.accProductId}', dataField: 'productId', width: 200, editable: canEditEntry , columntype: 'template',
                     	createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
                            editor.append('<div id=\"jqxProductTranEntryEdit\"></div>');
                            initProductSelect(editor, $('#jqxProductTranEntryEdit'));
                            editor.jqxDropDownButton('setContent', cellvalue);
	                      },
	                        geteditorvalue: function (row, cellvalue, editor) {
	                            // return the editor's value.
	                            editor.jqxDropDownButton(\"close\");
	                            var ini = $('#jqxProductTranEntryEdit').jqxGrid('getselectedrowindex');
	                            if(ini != -1){
		                            var item = $('#jqxProductTranEntryEdit').jqxGrid('getrowdata', ini);
		                            var selectedPro = item.productId;
		                            return selectedPro;	
	                            }
	                            return cellvalue;
	                        }
                      },
         { text: '${uiLabelMap.debitCreditFlag}', width: 150, dataField: 'debitCreditFlag',columntype: 'dropdownlist',
			cellsrenderer: function(row, columns, value){
				var x = value;
				if(value == 'C'){
					x = '${uiLabelMap.CREDIT}';
				}else{
					x = '${uiLabelMap.DEBIT}';
				}
         		return '<div class=\"cell-custom-grid\">'+x+'</div>';
         	},
			createeditor: function (row, column, editor) {
				var arr = [{description: '${uiLabelMap.CREDIT}', value : 'C'},{description: '${uiLabelMap.DEBIT}', value : 'D'}];
                editor.jqxDropDownList({ theme: theme, source: arr, displayMember: 'description', valueMember: 'value', width: 150, height: '25'});
            },
			cellbeginedit: begineditrowdetail
		 },
         { text: '${uiLabelMap.origAmount}', width: 150, dataField: 'origAmount', cellsalign: 'right',columntype: 'numberinput',
         	cellsrenderer: function(row, columns, value){
         		return '<div class=\"cell-custom-grid\">'+formatcurrency(value,null)+'</div>';
         	},
			createeditor: function (row, column, editor) {
                editor.jqxNumberInput({ width: 150,  max : 999999999999999999, digits: 18, decimalDigits:2, spinButtons: false, min: 0, height: 25});
				editor.jqxNumberInput('val', column);
            },
			cellbeginedit: begineditrowdetail
         },{ text: '${uiLabelMap.amount}', width: 150, dataField: 'amount', cellsalign: 'right',columntype: 'numberinput',editable: false,
         	cellsrenderer: function(row, columns, value){
         		return '<div class=\"cell-custom-grid\">'+formatcurrency(value,null)+'</div>';
         	},
			createeditor: function (row, column, editor) {
                editor.jqxNumberInput({ width: 150,  max : 999999999999999999, digits: 18, decimalDigits:2, spinButtons: false, min: 0,height: 25});
				editor.jqxNumberInput('val', column);
            },
			cellbeginedit: begineditrowdetail
         },{ text: '${uiLabelMap.description}', width: 350, dataField: 'transDescription',cellbeginedit: begineditrowdetail}];
		GridUtils.initGrid({
			url: 'JQListTransactionEntry&acctgTransId='+currentAccTg, width: '95%', autorowheight: false, 
			showtoolbar:true,
			virtualmode: true,
			autoheight : false,
			editable: canEditEntry,
			height: 180,
			rendertoolbar : function (toolbar) {
				var container = $(\"<div class='widget-header'></div>\");
                toolbar.append(container);
                container.append('<h4>${uiLabelMap.PageTitleListTranEntries}</h4>');
				if(isPosted != 'Y'){
	                container.append('<button id=\"deleteTransactionEntry\" class=\"grid-action-button pull-right\"><i class=\"fa fa-trash\"></i>${uiLabelMap.DADeleteSelectedRow}</button>');
	                container.append('<button id=\"addTransactionEntry\" class=\"grid-action-button pull-right\"><i class=\"fa fa-plus-circle\"></i>${uiLabelMap.DAAddNewRow}</button>');
	                $('#addTransactionEntry').on('click', function () {
	                    $('#popupAddTransactionEntry').jqxWindow('open');
	                });
	                $('#popupAddTransactionEntry').bind('close',function(){
	                	$('#popupAddTransactionEntry').jqxValidator('hide');
	                })
	                $('#deleteTransactionEntry').bind('click', function () {
	                	if(typeof(currentGrid) != 'undefined' && currentGrid != null){
	                		var selectedrowindex = currentGrid.jqxGrid('getselectedrowindex');
		                    var rowscount = currentGrid.jqxGrid('getdatainformation').rowscount;
		                    if (selectedrowindex >= 0 && selectedrowindex < rowscount) {
		                        var id = currentGrid.jqxGrid('getrowid', selectedrowindex);
		                        var commit = currentGrid.jqxGrid('deleterow', id);
		                    }
	                	} 
	                    
	                });
				}
            },
            source :{
            	sortcolumn:'acctgTransEntrySeqId',
            	pagesize:5,
            	editable: canEditEntry,
            	addColumns: 'acctgTransId;glAccountTypeId;organizationPartyId;glAccountId;reconcileStatusId;isSummary;productId;debitCreditFlag;origCurrencyUomId;partyId;origAmount;description;voucherRef;purposeEnumId',
            	createUrl : 'createAcctgTransEntry',
            	editColumns:'acctgTransId;acctgTransEntrySeqId;glAccountTypeId;glAccountId;reconcileStatusId;isSummary;productId;debitCreditFlag;origCurrencyUomId;partyId;origAmount;description;voucherRef;purposeEnumId',
            	updateUrl : 'updateAcctgTransEntry',
            	deleteColumns: 'acctgTransId;acctgTransEntrySeqId',
            	removeUrl : 'deleteAcctgTransEntry'
            }
		}, datafields, columns, null, grid);
    }
}">		

<script>
	 var formatcurrency = function(num, uom){
                if(num == null){
                    return "";
                }
                decimalseparator = ",";
                thousandsseparator = ".";
                currencysymbol = "đ";
                if(typeof(uom) == "undefined" || uom == null){
                    uom = "${defaultOrganizationPartyCurrencyUomId?if_exists}";
                }
                if(uom == "USD"){
                    currencysymbol = "$";
                    decimalseparator = ".";
                    thousandsseparator = ",";
                }else if(uom == "EUR"){
                    currencysymbol = "€";
                    decimalseparator = ".";
                    thousandsseparator = ",";
                }
                var str = num.toString().replace(currencysymbol, ""), parts = false, output = [], i = 1, formatted = null;
                if(str.indexOf(".") > 0) {
                    parts = str.split(".");
                    str = parts[0];
                }
                str = str.split("").reverse();
                for(var j = 0, len = str.length; j < len; j++) {
                    if(str[j] != ",") {
                        output.push(str[j]);
                        if(i%3 == 0 && j < (len - 1)) {
                            output.push(thousandsseparator);
                        }
                        i++;
                    }
                }
                formatted = output.reverse().join("");
                return(formatted + ((parts) ? decimalseparator + parts[1].substr(0, 2) : "") + "&nbsp;" + currencysymbol);
            };
</script>
