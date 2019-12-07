<script>
	<#assign agreementItemTypeList = delegator.findList("AgreementItemType", null, null, null, null, false)/>
	var aITData = new Array();
	<#list agreementItemTypeList as item>
		<#assign description = StringUtil.wrapString(item.get("description", locale)) />
		var row = {};
		row['agreementItemTypeId'] = '${item.agreementItemTypeId}';
		row['description'] = '${description}';
		aITData[${item_index}] = row;
	</#list>
	<#assign uomList = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false)/>
	var uomData = new Array();
	<#list uomList as item>
		<#assign description = StringUtil.wrapString(item.description) + "-" + StringUtil.wrapString(item.abbreviation)/>
		var row = {};
		row['uomId'] = '${item.uomId}';
		row['description'] = "${description}";
		uomData[${item_index}] = row;
	</#list>
</script>


<#assign dataField="[{ name: 'agreementId', type: 'string' },
					 { name: 'agreementItemSeqId', type: 'string'},
					 { name: 'agreementItemTypeId', type: 'string'},
					 { name: 'currencyUomId', type: 'string'},
					 { name: 'agreementText', type: 'string'},
					 { name: 'agreementImage', type: 'string'}
					 ]"/>
<#assign columnlist="{ text: '${uiLabelMap.SequenceId}', datafield: 'agreementItemSeqId', editable: false, width: '10%',
					 	cellsrenderer: function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        					return '<a style = \"margin-left: 10px\" href=' + 'accApEditAgreementItem?agreementId=' + data.agreementId + '&agreementItemSeqId=' + data.agreementItemSeqId +  '>' +  data.agreementItemSeqId + '</a>'
					 	}
					 },
					 { text: '${uiLabelMap.FormFieldTitle_agreementItemType}',  width: '20%', datafield: 'agreementItemTypeId', columntype: 'dropdownlist',
					   cellsrenderer: function(row, colum, value){
					 		for(i = 0; i < aITData.length; i++){
					 			if(value == aITData[i].agreementItemTypeId){
					 				return \"<span>\" + aITData[i].description + \"</span>\";
					 			}
					 		}
					 		return \"<span>\" + value + \"</span>\";
					 	},
					 	
					 	createeditor: function (row, column, editor) {
                            editor.jqxDropDownList({source: aITData, displayMember:\"description\", valueMember: \"agreementItemTypeId\",
                            renderer: function (index, label, value) {
			                    var datarecord = aITData[index];
			                    return datarecord.description;
			                  }
                        });
                        }
					 },
					 { text: '${uiLabelMap.currencyUomId}',  width: '20%', datafield: 'currencyUomId', columntype: 'dropdownlist',
					   cellsrenderer: function(row, colum, value){
					 		for(i = 0; i < uomData.length; i++){
					 			if(value == uomData[i].uomId){
					 				return \"<span>\" + uomData[i].description + \"</span>\";
					 			}
					 		}
					 		return \"<span>\" + value + \"</span>\";
					 	},
					 	createeditor: function (row, column, editor) {
                            editor.jqxDropDownList({source: uomData, displayMember:\"description\", valueMember: \"uomId\",
                            renderer: function (index, label, value) {
			                    var datarecord = uomData[index];
			                    return datarecord.description;
			                  }
                        });
                        }
					 },
					 { text: '${uiLabelMap.FormFieldTitle_agreementText}', width: '30%', datafield: 'agreementText'},
					 { text: '${uiLabelMap.FormFieldTitle_agreementImage}', width: '20%',datafield: 'agreementImage'}
					 "/>
<@jqGrid filtersimplemode="false" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" editable="true"
		 url="jqxGeneralServicer?sname=JQListAgreementItems&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 updateUrl="jqxGeneralServicer?sname=updateAgreementItem&jqaction=U&agreementId=${parameters.agreementId}"
		 editColumns="agreementId[${parameters.agreementId}];agreementItemSeqId;agreementItemTypeId;currencyUomId;agreementText;agreementImage(java.nio.ByteBuffer)"
		 />		 