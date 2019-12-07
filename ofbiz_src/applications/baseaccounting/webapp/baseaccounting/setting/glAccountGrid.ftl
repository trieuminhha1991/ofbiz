<#assign dataField="[{ name: 'glAccountId', type: 'string' },
               		{ name: 'glAccountTypeId', type: 'string' },
               		{ name: 'glAccountClassId', type: 'string' },
               		{ name: 'glResourceTypeId', type: 'string' },
               		{ name: 'glXbrlClassId', type: 'string' },
               		{ name: 'glTaxFormId', type: 'string' },
               		{ name: 'parentGlAccountId', type: 'string' },
                	{ name: 'accountCode', type: 'string' },
                	{ name: 'codeParent', type: 'string' },
                	{ name: 'accountName', type: 'string' },
                	{ name: 'description', type: 'string' },
                	{ name: 'productId', type: 'string' },
                	{ name: 'externalId', type: 'string' },
                	{ name: 'postedBalance', type: 'string' }]"/>

<#if parameters.organizationPartyId?exists && parameters.organizationPartyId?has_content>
    <#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_glAccountId}', dataField: 'glAccountId', width: 170, editable:false},
		{ text: '${uiLabelMap.FormFieldTitle_glAccountTypeId}', dataField: 'glAccountTypeId', width: 200, columntype: 'dropdownlist',
				createeditor: function (row, column, editor) {
			       var sourceGlat =
			       {
			           localdata: dataGLAT,
			           datatype: \"array\"
			       };
			       var dataAdapterGlat = new $.jqx.dataAdapter(sourceGlat);
			       editor.jqxDropDownList({source: dataAdapterGlat, displayMember:\"glAccountTypeId\", valueMember: \"glAccountTypeId\",
			       renderer: function (index, label, value) {
			           var datarecord = dataGLAT[index];
			           return datarecord.description;
			       }
			   });
			}
		},
		{ text: '${uiLabelMap.FormFieldTitle_glAccountClassId}', dataField: 'glAccountClassId', width: 150, columntype: 'dropdownlist',
				createeditor: function (row, column, editor) {
			       var sourceGlac =
			       {
			           localdata: dataGLAC,
			           datatype: \"array\"
			       };
			       var dataAdapterGlac = new $.jqx.dataAdapter(sourceGlac);
			       editor.jqxDropDownList({source: dataAdapterGlac, displayMember:\"glAccountClassId\", valueMember: \"glAccountClassId\",
			       renderer: function (index, label, value) {
			           var datarecord = dataGLAC[index];
			           return datarecord.description;
			       }
			   });
			}
		},
		{ text: '${uiLabelMap.FormFieldTitle_glResourceTypeId}', dataField: 'glResourceTypeId', width: 180, columntype: 'dropdownlist',
				createeditor: function (row, column, editor) {
			       var sourceGrt =
			       {
			           localdata: dataGRT,
			           datatype: \"array\"
			       };
			       var dataAdapterGrt = new $.jqx.dataAdapter(sourceGrt);
			       editor.jqxDropDownList({source: dataAdapterGrt, displayMember:\"glResourceTypeId\", valueMember: \"glResourceTypeId\",
			       renderer: function (index, label, value) {
			           var datarecord = dataGRT[index];
			           return datarecord.description;
			       }
			   });
			}
		},
		{ text: '${uiLabelMap.BACCGlTaxFormAccountId}', dataField: 'glTaxFormId', width: 400, columntype: 'dropdownlist',
				cellsrenderer: function(row, colum, value)
			   {
			   	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
			   	for(i=0; i<dataTFAI.length;i++){
			   		if(dataTFAI[i].glTaxFormId == value){
							return \"<div class='custom-cell-grid'>\" + dataTFAI[i].description + \"</div>\"
						}
			   	}
			   	return \"<div class='custom-cell-grid'>\" + value + \"</div>\";
			   },
				createeditor: function (row, column, editor) {
			       var sourceTFAI =
			       {
			           localdata: dataTFAI,
			           datatype: \"array\"
			       };
			       var dataAdapterTFAI = new $.jqx.dataAdapter(sourceTFAI);
			       editor.jqxDropDownList({source: dataAdapterTFAI, displayMember:\"description\", valueMember: \"glTaxFormId\",
			       renderer: function (index, label, value) {
			           var datarecord = dataTFAI[index];
			           return datarecord.description;
			       }
			   });
			}
		},
		{ text: '${uiLabelMap.FormFieldTitle_parentGlAccountId}', dataField: 'codeParent', width: 150 },
		{ text: '${uiLabelMap.BACCAccountCode}', dataField: 'accountCode'},
		{ text: '${uiLabelMap.BACCAccountName}', dataField: 'accountName', width: 300 },
		{ text: '${uiLabelMap.Description}', dataField: 'description', width: 350 } "/>

    <@jqGrid url="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&sname=JQGetListChartOfAccountOriginationTrans" defaultSortColumn="glAccountId" columnlist=columnlist
    dataField=dataField showtoolbar="true" clearfilteringbutton="true" editable="false" functionAfterAddRow="OlbGlAccountOrg.updateListGL"
    addColumns="glAccountId;organizationPartyId"
    id="jqxgrid"
    customcontrol2="fa fa-trash@${uiLabelMap.BACCDelete}@javascript: void(0);@OlbGlAccountOrg.deleteGlAccountOrganization()"
    customcontrol1="fa fa-file-excel-o@${uiLabelMap.BSExportExcel}@javascript:exportExcel()"
    createUrl="jqxGeneralServicer?jqaction=C&sname=createGlAccountOrganization" alternativeAddPopup="alterpopupWindow" addrow="true" addType="popup"
    />
<#else>
    <#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_glAccountId}', dataField: 'glAccountId', width: 170, editable:false},
					 { text: '${uiLabelMap.BACCAccountName}', dataField: 'accountName', filterable : true,editable : false,width: 300 ,
							cellsrenderer : function(row){
							 	var data = $('#jqxgrid').jqxGrid('getrowdata',row);
							 	return '<span class=\"custom-style-word\">' + data.accountName+ '</span>';
							 }
					 },
					 { text: '${uiLabelMap.BACCGlTaxFormAccountId}', dataField: 'glTaxFormId', width: 400, columntype: 'dropdownlist',
						cellsrenderer: function(row, colum, value){
						   	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						   	for(i=0; i < dataTFAI.length;i++){
						   		if(dataTFAI[i].glTaxFormId==value){
										return '<span>' + dataTFAI[i].description + '</span>';
									}
						   	}
						   	return \"<span class='custom-style-word'>\" + value + \"</span>\";
					    },
					 	createeditor: function (row, column, editor) {
						 	var data = $('#jqxgrid').jqxGrid('getrowdata',row);
					        editor.jqxDropDownList({source: dataTFAI,displayMember:\"description\", valueMember: \"glTaxFormId\",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
					 	},
					 	initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
					 		if(cellvalue){
						        editor.val(cellvalue);
					 		}
					    }
					 },
					 { text: '${uiLabelMap.Description}', dataField: 'description'} "/>

    <@jqGrid url="jqxGeneralServicer?sname=JQGetListChartOfAccount" defaultSortColumn="glAccountId" columnlist=columnlist
    dataField=dataField showtoolbar="true" clearfilteringbutton="true" editable="true" addrefresh="true"
    addColumns="externalId;productId;description;accountName;accountCode;glTaxFormId;parentGlAccountId;glXbrlClassId;glResourceTypeId;glAccountClassId;glAccountTypeId;glAccountId"
    createUrl="jqxGeneralServicer?jqaction=C&sname=createGlAccount" alternativeAddPopup="alterpopupWindow" addrow="true" addType="popup"
    customcontrol1="fa fa-file-excel-o open-sans@${StringUtil.wrapString(uiLabelMap.BSExportExcel)}@javascript:void(0);exportExcel()"
    updateUrl="jqxGeneralServicer?jqaction=U&sname=updateGlAccount" editColumns="externalId;productId;description;glTaxFormId;accountName;accountCode;parentGlAccountId;glXbrlClassId;glResourceTypeId;glAccountClassId;glAccountTypeId;glAccountId"
    jqGridMinimumLibEnable="false"
    />
</#if>

<script>
    var filterObjData = new Object();
    var exportExcel = function(){
        var dataGrid = $("#jqxgrid").jqxGrid('getrows');
        if (dataGrid.length == 0) {
            jOlbUtil.alert.error("${uiLabelMap.BSNoDataToExport}");
            return false;
        }

        var winURL = "exportExcelGlAccount";
        var form = document.createElement("form");
        form.setAttribute("method", "POST");
        form.setAttribute("action", winURL);
        form.setAttribute("target", "_blank");

        var hiddenField0 = document.createElement("input");
        hiddenField0.setAttribute("type", "hidden");
        hiddenField0.setAttribute("name", "organizationPartyId");
        hiddenField0.setAttribute("value", "${parameters.organizationPartyId?if_exists}");
        form.appendChild(hiddenField0);

        if (OlbCore.isNotEmpty(filterObjData) && OlbCore.isNotEmpty(filterObjData.data)) {
            $.each(filterObjData.data, function(key, value) {
                var hiddenField1 = document.createElement("input");
                hiddenField1.setAttribute("type", "hidden");
                hiddenField1.setAttribute("name", key);
                hiddenField1.setAttribute("value", value);
                form.appendChild(hiddenField1);
            });
        }

        document.body.appendChild(form);
        form.submit();
    }
</script>