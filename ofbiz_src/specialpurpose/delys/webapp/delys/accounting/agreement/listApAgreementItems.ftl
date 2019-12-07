<script>
	<#assign agreementItemTypeList = delegator.findList("AgreementItemType", null, null, null, null, false)/>
	var aITData = new Array();
	<#list agreementItemTypeList as item>
		<#assign description = StringUtil.wrapString(item.description)/>
		var row = {};
		row['agreementItemTypeId'] = '${item.agreementItemTypeId}';
		row['description'] = "${StringUtil.wrapString(item.get("description", locale))}";
		aITData[${item_index}] = row;
	</#list>
	<#assign uomList = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false)/>
	var uomData = new Array();
	<#list uomList as item>
		<#assign description = StringUtil.wrapString(item.description) + " - " + StringUtil.wrapString(item.abbreviation)/>
		var row = {};
		row['uomId'] = '${item.uomId}';
		row['description'] = "${description}";
		uomData[${item_index}] = row;
	</#list>
</script>
<style>
	.hideBt{
		display : none !important;
	}
</style>

<#assign dataField="[{ name: 'agreementId', type: 'string' },
					 { name: 'agreementItemSeqId', type: 'string'},
					 { name: 'agreementItemTypeId', type: 'string'},
					 { name: 'currencyUomId', type: 'string'},
					 { name: 'agreementText', type: 'string'},
					 { name: 'agreementImage', type: 'string'},
					 { name: 'linkAgreementImg', type: 'string'}
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
                            editor.jqxDropDownList({source: uomData, filterable: true, displayMember:\"description\", valueMember: \"uomId\",
                            renderer: function (index, label, value) {
			                    var datarecord = uomData[index];
			                    return datarecord.description;
			                  }
                        });
                        }
					 },
					 { text: '${uiLabelMap.FormFieldTitle_agreementText}', width: '40%', datafield: 'agreementText',cellsrenderer: function (row, column, value) {
        					var regex = /(<([^>]+)>)/ig;
       					 return '<span>' + value.replace(regex, '') + '</span>';
       				  }},
					 { text: '${uiLabelMap.FormFieldTitle_agreementImage}',editable : false, datafield: 'linkAgreementImg',filterable : false,cellsrenderer : function(row){
						 var data = $('#jqxgrid').jqxGrid('getrowdata',row);
						 if(data.linkAgreementImg != null){
							 return '<span><a href=\"javascript:viewImgs(&quot;'+ data.linkAgreementImg +'&quot;)\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i style=\"font-size : 20px;\" class=\"icon-picture\"></i></a></span>';
						 }else return '<span><a href=\"javascript:viewImgs()\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class=\"icon-plus-sign\"></i><i style=\"font-size : 20px;\" class=\"icon-picture\"></i></a></span>';
						 
					 }},
					 "/>
<@jqGrid filtersimplemode="false" addrefresh="true" autorowheight="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="true"
		 url="jqxGeneralServicer?sname=JQListAgreementItems&agreementId=${parameters.agreementId}"
		 createUrl="jqxGeneralServicer?sname=createAgreementItem&jqaction=C&agreementId=${parameters.agreementId}"
		 removeUrl="jqxGeneralServicer?sname=removeAgreementItem&jqaction=D&agreementId=${parameters.agreementId}"
		 updateUrl="jqxGeneralServicer?sname=updateAgreementItem&jqaction=U&agreementId=${parameters.agreementId}"
		 deleteColumn="agreementId[${parameters.agreementId}];agreementItemSeqId"
		 addColumns="agreementId[${parameters.agreementId}];agreementItemTypeId;currencyUomId;agreementText;agreementImage(java.nio.ByteBuffer);linkAgreementImg"
		 editColumns="agreementId[${parameters.agreementId}];agreementItemSeqId;agreementItemTypeId;currencyUomId;agreementText;agreementImage(java.nio.ByteBuffer)"
		 />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>		 
<div class="hide" id="agreementImgs">
	<div id="header">${StringUtil.wrapString(uiLabelMap.FormFieldTitle_agreementImage)}</div>
	<div class="body">
		<div class="row">
			<div class="span12">
				<img id="agImgs"/>
			</div>
		</div>
	</div>
</div>
<script>
	$(document).ready(function(){
		$('#agreementImgs').jqxWindow({
	        width: 600, height: 600, resizable: true,  isModal: true, autoOpen: false, modalOpacity: 0.7          
	    });
	});
	function setData(data){
		$('#agreementItemTypeIdAdd').jqxDropDownList('val',data.agreementItemTypeId);
		$('#currencyUomIdAdd').jqxDropDownList('val',data.currencyUomId);
		$('#agreementTextAdd').jqxInput('val',data.agreementText);
	}
	function viewImgs(path){
		var imgRender = $('#agImgs');
		var windowImgs = $('#agreementImgs');
		if(typeof(imgRender) != 'undefined' && typeof(windowImgs) != 'undefined') {
			if(typeof(path) != 'undefined'){
				imgRender.attr('src',path);
				imgRender.attr('width',$('#agreementImgs').width()-50);
				imgRender.attr('height',$('#agreementImgs').height()-50);
				windowImgs.jqxWindow('open');
			}else{
				setData(getDataSelect());
				var elm = $('#alterUpdate');
				if(typeof(elm) != 'undefined' && elm.hasClass('hideBt')){
					elm.removeClass('hideBt');
					if(typeof($('#alterSave')) != 'undefined')  $('#alterSave').addClass('hideBt');
					if(typeof($('#saveAndContinue')) != 'undefined')  $('#saveAndContinue').addClass('hideBt');
				}
				$('#alterpopupWindow').jqxWindow('open');
			}
			
		}
	}
	function getDataSelect(){
		var data;
		var index = $('#jqxgrid').jqxGrid('getselectedrowindex');
		if(index != -1){
			 data = $('#jqxgrid').jqxGrid('getrowdata',index);
		}
		return data;
	}
</script>
<#include "popupAddAgreementAcc.ftl"/>