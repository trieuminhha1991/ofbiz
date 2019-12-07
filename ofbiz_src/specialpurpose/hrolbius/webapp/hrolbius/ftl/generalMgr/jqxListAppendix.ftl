<#--Import LIB-->
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<#--/Import LIB-->
<#--===================================Prepare Data=====================================================-->
<script>
	//Prepare for term type data
	<#assign termTypeList = delegator.findList("TermType", null, null, null, null, false) />
	termTypeData = [
	              <#list termTypeList as item>
					<#assign description = StringUtil.wrapString(item.description?if_exists) />
					{'termTypeId': '${item.termTypeId}', 'description': '${description}'},
				  </#list>
				];
	
</script>
<#--===================================/Prepare Data=====================================================-->
<#--=================================Init Grid======================================================-->
<#assign dataField="[{ name: 'agreementItemSeqId', type: 'string'},
					 { name: 'agreementText', type: 'string'}
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.CommonId}', datafield: 'agreementItemSeqId', width: 160, editable: false},
                     { text: '${uiLabelMap.agreementText}', datafield: 'agreementText'}
					 "/>

<@jqGrid id="jqxgridAppendix" filtersimplemode="true" addrow="true" addrefresh="true" editable="false" addType="popup" alternativeAddPopup="wdwNewAppendix" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListAppendix&agreementId=${parameters.agreementId}" dataField=dataField columnlist=columnlist	 
		/>
<#--=================================/Init Grid======================================================-->
<#include "jqxEditAppendix.ftl" />
<#include "jqxEditAppendixTerm.ftl" />
<#--====================================================Setup JS======================================-->
<script>
	var JQXAction = function(){};
	JQXAction.prototype.theme = 'olbius';
	JQXAction.appTermData = new Array();
	JQXAction.prototype.initWindow = function(){
		$('#wdwNewAppendix').jqxWindow({showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "99%", height: 550, minWidth: '40%', width: "80%", isModal: true, modalZIndex: 10000,theme:this.theme, collapsed:false,
            initContent: function () {
            	// Create jqxTabs.
		        $('#jqxTabs').jqxTabs({ width: '99%', height: 450, position: 'top', disabled:true,
		        	initTabContent:function (tab) {
		        		if(tab == 0){
	        			 $('#agreementText').jqxEditor({
	        	                width: '98%'
	        	            });
		        		}else if(tab == 1){
		        			var source = {
		                        localdata: JQXAction.appTermData,
		                        datatype: "array",
		                        datafields:
		                        [
	                            	{ name: 'termTypeId', type: 'string' },
									{ name: 'fromDate', type: 'date' },
									{ name: 'thruDate', type: 'date' },
									{ name: 'termValue', type: 'string' },
									{ name: 'textValue', type: 'string' },
									{ name: 'description', type: 'string' }
		                        ]
		                    };
		                	var dataAdapter = new $.jqx.dataAdapter(source);
		                    
		                	$("#jqxgridAppendixTerms").jqxGrid(
		                    {
		                        width: '98%',
		                        source: dataAdapter,
		                        columnsresize: true,
		                        pageable: true,
		                        autoheight: true,
		                        showtoolbar: true,
		                        rendertoolbar: function (toolbar) {
		                            var container = $("<div id='toolbarcontainer' class='widget-header'>");
		                            toolbar.append(container);
		                            container.append('<h4></h4>');
		                            container.append('<button id="btnAddRow" type="button" style="margin-left:20px; margin-top:5px; margin-right:5px;"><i class="icon-plus-sign"></i>${uiLabelMap.accAddNewRow}</button>');
		                            container.append('<button id="btnDelRow" type="button" style="margin-left:20px; margin-top:5px; margin-right:5px;"><i class="icon-trash"></i>${uiLabelMap.accDeleteSelectedRow}</button>');
				                    $("#btnAddRow").jqxButton();
				                    $("#btnDelRow").jqxButton();
		                            // create new row.
		                            $("#btnAddRow").on('click', function () {
		                            	$("#wdwNewAppendixTerm").jqxWindow('open');
		                            });
		                            
		                            // create new row.
		                            $("#btnDelRow").on('click', function () {
		                            	var selectedrowindex = $('#wdwNewAppendixTerm').jqxGrid('selectedrowindex'); 
		                            	JQXAction.appTermData.splice(selectedrowindex, 1);
		                            	$('#jqxgridAppendixTerms').jqxGrid('updatebounddata'); 
		                            	
		                            });
		        	            },
		                        columns: [
		                          { text: '${uiLabelMap.CommonId}', datafield: 'termTypeId', width: 150},
		                          { text: '${uiLabelMap.fromDate}', datafield: 'fromDate', width: 150},
		                          { text: '${uiLabelMap.thruDate}', datafield: 'thruDate', width: 150},
		                          { text: '${uiLabelMap.termValue}', datafield: 'termValue', width: 150},
		                          { text: '${uiLabelMap.textValue}', datafield: 'textValue', width: 150},
		                          { text: '${uiLabelMap.description}', datafield: 'description'}
		                        ]
		                    });
		        		}
		        	}
	        	});
            }
        });
		
		$('#wdwNewAppendixTerm').jqxWindow({showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "99%", height: 350, minWidth: '40%', width: "40%", 
			isModal: true, modalZIndex: 10000,theme:this.theme, collapsed:false, cancelButton: '#alterCancel',
            initContent: function () {
            	$("#termTypeId").jqxDropDownList({source: termTypeData, valueMember: 'termTypeId', displayMember: 'description'});
            	$("#fromDate").jqxDateTimeInput({});
            	$("#thruDate").jqxDateTimeInput({});
            	$("#termValue").jqxInput({width: 195});
            	$("#textValue").jqxInput({width: 195});
            }
		});
	};
	
	JQXAction.prototype.bindEvent = function(){
		$(".next").on('click', function(){
    		var selectedItem = $("#jqxTabs").jqxTabs('selectedItem');
    		$("#jqxTabs").jqxTabs('disableAt', selectedItem);
    		$("#jqxTabs").jqxTabs('enableAt', selectedItem + 1);
    		$("#jqxTabs").jqxTabs('next');
    	});
        
        $(".back").on('click', function(){
    		var selectedItem = $("#jqxTabs").jqxTabs('selectedItem');
    		$("#jqxTabs").jqxTabs('disableAt', selectedItem);
    		$("#jqxTabs").jqxTabs('enableAt', selectedItem - 1);
    		$("#jqxTabs").jqxTabs('previous');
    	});
        
        $("#alterSave").on('click', function(){
        	var term = {
	        	termTypeId:$("#termTypeId").val(),
	        	fromDate:$("#fromDate").jqxDateTimeInput('getDate').getTime(),
	        	thruDate:$("#thruDate").jqxDateTimeInput('getDate').getTime(),
	        	termValue:$("#termValue").val(),
	        	textValue:$("#textValue").val()
        	};
	        appTermIndex = JQXAction.appTermData.length - 1;
	        JQXAction.appTermData[++appTermIndex] = term;
	        $("#jqxgridAppendixTerms").jqxGrid('updatebounddata');
	        // select the first row and clear the selection.
	        $("#jqxgridAppendixTerms").jqxGrid('clearSelection');
	        $("#jqxgridAppendixTerms").jqxGrid('selectRow', 0);
	        $("#wdwNewAppendixTerm").jqxWindow('close');
        });
        
        $("#btnCreateAppendix").on('click', function(){
        	var submitData = {};
        	submitData['agreementText'] = $("#agreementText").val();
        	submitData['listAppendixTerm'] = JSON.stringify(JQXAction.appTermData);
        	submitData['agreementId'] = '${parameters.agreementId}';
        	$.ajax({
        		url: "createAppendix",
        		data: submitData,
        		type: 'POST',
        		async: false,
        		dataType: 'json',
        		success: function(data){
        			if(data.errorMessage){
        				bootbox.confirm(data.errorMessage, function(result) {
							return;
						});
        			}else{
        				$("#jqxgridAppendix").jqxGrid("updatebounddata");
        				$("#wdwNewAppendix").jqxWindow('close');
        			}
        		}
        	});
        });
	};
	
	JQXAction.prototype.initValidator = function(){
	};
	
	$(document).on('ready', function(){
		var jqxAction = new JQXAction();
		jqxAction.initWindow();
		jqxAction.bindEvent();
		jqxAction.initValidator();
	});
</script>
<#--====================================================/Setup JS======================================-->