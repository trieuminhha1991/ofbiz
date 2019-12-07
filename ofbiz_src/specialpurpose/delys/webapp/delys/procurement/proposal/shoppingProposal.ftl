<#include "../item/uiLabelMap.ftl"/>
<#if listUom?exists>
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	var theme = $.jqx.theme;
	var dataLU = [<#list listUom as uom>{uomId:"${uom.uomId}", description:"${StringUtil.wrapString(uom.description?default(""))}"},</#list>]
	var statuses = [<#list listStatus as status>{statusId:"${status.statusId}", description:"${StringUtil.wrapString(status.description?default(""))}"},</#list>]
	var dataLC = [<#list listCurrency as item>{uomId : "${item.uomId}",description : "${StringUtil.wrapString(item.uomId + ' : '+ item.description?default(""))}"},</#list>];
</script>
</#if>
<#assign dataField="[{ name: 'requirementId', type: 'string' },
					 { name: 'requirementTypeId', type: 'string' },
					 { name: 'statusId', type: 'string' },
					 { name: 'description', type: 'string' },
					 { name: 'requirementStartDate', type: 'date', other: 'Timestamp' },
					 { name: 'requiredByDate', type: 'date', other: 'Timestamp' },
					 { name: 'currencyUomId', type: 'string' },
					 { name: 'reason', type: 'string' },
					 { name: 'createdByUserLogin', type: 'string'},
					 { name: 'createdDepartment', type: 'string' },
					 ]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.proposalId}', datafield: 'requirementId', editable: false, width:100},
					 { text: '${uiLabelMap.statusId}', datafield: 'statusId', editable: false, width: 150,  columntype: 'dropdownlist',
					 	cellsrenderer: function(row, column, value){
					 		for(var x in statuses){
					 			if(statuses[x].statusId == value){
					 				return '<span style = \"margin-left: 10px\">' +  statuses[x].description + '</span>';
					 			}
					 		}
					 		return '<span style = \"margin-left: 10px\">' +  value + '</span>';
					 	}
					 },
					 { text: '${uiLabelMap.currencyUomId}', datafield: 'currencyUomId', width: 100,columntype: 'dropdownlist',
					 	createeditor: function (row, column, editor) {
                            var sourceGlat =
				            {
				                localdata: dataLC,
				                datatype: 'array'
				            };
				            var current = $('#jqxgrid').jqxGrid('getrowdata', row);
				            var dataAdapterGlat = new $.jqx.dataAdapter(sourceGlat);
                            editor.jqxDropDownList({source: dataAdapterGlat, filterable: true, displayMember: 'description',valueMember:'uomId', dropDownHeight:200, dropDownWidth: 250}); 
                        }
                     },
					 { text: '${uiLabelMap.requirementStartDate}', datafield: 'requirementStartDate',filtertype: 'range',cellsformat: 'dd/MM/yyyy',  width: 150},
					 { text: '${uiLabelMap.requiredByDate}', datafield: 'requiredByDate',filtertype: 'range',cellsformat: 'dd/MM/yyyy',  width: 150},
					 { text: '${uiLabelMap.Description}', datafield: 'description'}"/>
<#if showColumns>
	<#assign columnlist = columnlist + 
	",{ text: '${uiLabelMap.CreatedBy}', datafield: 'createdByUserLogin', width: 150},
 	{ text: '${uiLabelMap.Department}', datafield: 'createdDepartment', width: 150}
 	"/>
</#if>		
<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord){
	var requirementId = datarecord.requirementId;
	var urlStr = 'jqxGeneralServicer?sname=JQListRequirementItem';
	var id = datarecord.uid.toString();
	var grid = $($(parentElement).children()[0]);
    $(grid).attr('id', 'jqxgridDetail_' + id);
  		var emplPosTypeSalarySource = {datafields: [
            {name: 'reqItemSeqId', type: 'string'},
            {name: 'productId', type: 'date'},
            {name: 'quantity', type: 'number'},
            {name: 'quantityUomId', type: 'string'},
            {name: 'currencyUomId', type: 'string'}           		            
		],
		cache: false,
		datatype: 'json',
		type: 'POST',
		data: {
			requirementId: requirementId,
			noConditionFind: 'N',
	        conditionsFind: 'N',
	        dictionaryColumns: '',
	        otherCondition: ''
		},
        addrow: function (rowid, rowdata, position, commit) {
           	var data = {
  				columnList: 'requirementId;productId;quantity;quantityUomId', 
  				columnValues: requirementId + '#;' + rowdata.productId + '#;' + rowdata.quantity + '#;' + rowdata.quantityUomId  
           	};
            addRow(grid, 'jqxGeneralServicer?sname=createRequirementItemPO&jqaction=C', data, commit);
        },
        deleterow: function (rowid, commit) {
           	var dataRecord = grid.jqxGrid('getrowdata', rowid);
           	var data = {columnList: 'requirementId;reqItemSeqId', columnValues: requirementId + '#;' + dataRecord.reqItemSeqId};
           	deleteRow(grid, 'jqxGeneralServicer?sname=deleteRequirementItemPO&jqaction=D', data, commit);
        },
        updaterow: function (rowid, newdata, commit) {
           	var data = {
           		rl: 5,
  				columnList: 'requirementId;reqItemSeqId;quantity;quantityUomId;currencyUomId', 
  				columnValues: requirementId + '#;' + newdata.reqItemSeqId + '#;' + newdata.quantity + '#;' + newdata.quantityUomId + '#;' + newdata.currencyUomId 
           	};
            updateRow(grid, 'jqxGeneralServicer?sname=updateRequirementItemPO&jqaction=U', data, commit);
        },
        url: urlStr
     };
    var nestedGridAdapter = new $.jqx.dataAdapter(emplPosTypeSalarySource);
    if (grid != null) {
    	grid.jqxGrid({
   			source: nestedGridAdapter,
   			width: '95%', height: 170,
            showtoolbar:true,
			editable: false,
			editmode:'selectedrow',
			showheader: true,
			selectionmode:'singlerow',
			theme: 'energyblue',
			rendertoolbar: function (toolbar) {
                var me = this;
                var container = $('<div style=\"margin: 5px; float: right\"></div>');
                toolbar.append(container);
                container.append('<input id=\"addrowbutton'+id+'\"  type=\"button\" value=\"Add New Row\" />');
                container.append('<input style=\"margin-left: 5px;\" id=\"deleterowbutton'+id+'\" type=\"button\" value=\"Delete Selected Row\" />');
                $('#addrowbutton'+id).jqxButton();
                $('#deleterowbutton'+id).jqxButton();
                // create new row.
                $('#addrowbutton'+id).on('click', function () {
                	currentGrid = grid;
                    $('#jqxwindowproduct').jqxWindow('open');
                });
                // delete row.
                $('#deleterowbutton'+id).on('click', function () {
                    var selectedrowindex = grid.jqxGrid('getselectedrowindex');
                    var rowscount = grid.jqxGrid('getdatainformation').rowscount;
                    if (selectedrowindex >= 0 && selectedrowindex < rowscount) {
                        var id = grid.jqxGrid('getrowid', selectedrowindex);
                        var commit = grid.jqxGrid('deleterow', id);
                    }
                });
            },
			columns: [{text: '${uiLabelMap.productName}', datafield: 'productId', cellsalign: 'left', width: '25%', cellsformat: 'dd/MM/yyyy ', columntype: 'template'},
				 	{text: '${uiLabelMap.quantity}',datafield: 'quantity', cellsalign: 'right', width: '25%',
				 		cellsrenderer: function(row, colum, value){
					 		var str = convertLocalNumber(value);
					 		return '<div class=\"custom-cell-grid align-right\">' + str + '</div>';
		              	 }
				 	},
				 	{text: '${uiLabelMap.QuantityUom}', datafield: 'quantityUomId', cellsalign: 'left', width: '25%', 
				 		cellsrenderer: function(row, colum, value){
					 		var uom = '';
					 		for(var x in dataLU){
					 			if(dataLU[x].uomId == value){
					 				uom = dataLU[x].description;
					 			}
					 		}
					 		if(!uom){uom = value;}
					 		return '<div class=\"custom-cell-grid\">' + uom + '</div>';
		              	  }
				 	},
				 	{text: '${uiLabelMap.currencyUomId}', datafield: 'currencyUomId', cellsalign: 'right', width: '25%',
				 		cellsrenderer: function(row, colum, value){
					 		var uom = '';
					 		for(var x in dataLC){
					 			if(dataLC[x].uomId == value){
					 				uom = dataLC[x].description;
					 			}
					 		}
					 		if(!uom){uom = value;}
					 		return '<div class=\"custom-cell-grid\">' + uom + '</div>';
		              	 }
				 	}]
   		});
    }
}">				
<@jqGrid url="jqxGeneralServicer?sname=JQListShoppingProposal&userLogin=${userLogin}" 
		 dataField=dataField columnlist=columnlist
		 id="jqxgrid" filtersimplemode="true"
		 initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail
		 clearfilteringbutton="true"
		 editable="true" 
		 editrefresh ="true"
		 editmode="click"
		 showtoolbar = "true" deleterow="true" autorowheight="true"
		 otherParams="createdDepartment:S-getDepartmentFromUserLogin(createdByUserLogin)<departmentName>;"
		 removeUrl="jqxGeneralServicer?sname=deletePOProposal&jqaction=D" deleteColumn="requirementId"
		 createUrl="jqxGeneralServicer?jqaction=C&sname=createPOProposal" alternativeAddPopup="alterpopupWindow" addrow="true" addType="popup" 
		 addColumns="products(java.util.List);reason;currencyUomId;requirementStartDate(java.sql.Timestamp);requiredByDate(java.sql.Timestamp)" addrefresh="true"
		 updateUrl="jqxGeneralServicer?jqaction=U&sname=updateRequirementPO"  editColumns="requirementId;description;currencyUomId;requirementStartDate(java.sql.Timestamp);requiredByDate(java.sql.Timestamp)"	 
 />
 <@useLocalizationNumberFunction/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<#include "shoppingProposalPopup.ftl"/>
<#include "../item/popupProduct.ftl"/>
<script type="text/javascript" src="/delys/images/js/procurement/shoppingProposal.js"></script>
<script src="/delys/images/js/procurement/gridAction.js"></script> 