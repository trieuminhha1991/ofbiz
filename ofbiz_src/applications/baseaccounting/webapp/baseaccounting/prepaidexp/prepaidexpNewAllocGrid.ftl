<script>
	<#assign listPartyGlAcc = delegator.findByAnd("FAPartyGlAccountView", null, null, false)>
	var partyGlAccountData = [
      <#if listPartyGlAcc?exists>
      	<#list listPartyGlAcc as item>
      		{
      			partyId : "${item.partyId}",
      			glAccountId : "${item.glAccountId}",
      			accountName : "${StringUtil.wrapString(item.get('accountName'))}",
  			},
  		</#list>
  	  </#if>
	];
</script>
<#include "prepaidexpNewAlloc.ftl" >
<div id="newAllocGrid"></div>
<#--===================================Prepare Data=====================================================--> 
<script>
	OLBNewAlloc = function (){
		allocParty = new OLBNewAllocParty();
		allocParty.initWindow();
		allocParty.bindEvent();
	};
	
	OLBNewAlloc.prototype = {
			attr : {
				grid_item : $("#newAllocGrid"),
				editor : {},
				INDEX : 0,
				SEQ : 1,
				ITEM_DATA : new Array(),
				string : new String()
			},
			getData : function(){
				return this.attr.ITEM_DATA;
			},
			initGrid :  function(){
				var parent = this;
				//Prepare Data
				source = {
		            localdata: this.attr.ITEM_DATA,
		            datatype: "array",
		            datafields: [
		                 		 {name: 'seqId', type: 'string'},
		                         {name: 'allocPartyId', type: 'string'},
		                         { name: 'allocPartyName', type: 'string' },
		                         { name: 'allocRate', type: 'number' },
		                         { name: 'allocGlAccountId', type: 'string' },
		                     ],
					updaterow: function (rowid, rowdata, commit) {
						parent.attr.ITEM_DATA[rowid] = rowdata;
				        commit(true);
				    }
		        };
		        var dataAdapter = new $.jqx.dataAdapter(source);
		        
		        //Tool bar of grid
				var rendertoolbar = function (toolbar){
					var container = $("<div id='toolbarcontainer' class='widget-header'></div>");
					container.append('<h4>${uiLabelMap.BACCAllocationSetting}</h4>');
					container.append('<button id="btnAdd" style="margin-left:20px; margin-top:5px; margin-right:5px;"><i class="icon-plus-sign"></i>${uiLabelMap.BACCAddNewRow}</button>');
			        container.append('<button id="btnDel" style="margin-left:20px; margin-top:5px; margin-right:5px;"><i class="icon-trash"></i>${uiLabelMap.BACCDelRow}</button>');
			        toolbar.append(container);
			        $("#btnAdd").jqxButton({theme: 'olbius'});
			        $("#btnDel").jqxButton({theme: 'olbius'});
			        $("#btnDel").click(function(){
			        	var rowindex = $('#newAllocGrid').jqxGrid('getselectedrowindex');
			        	var row = $('#newAllocGrid').jqxGrid('getrowdata', rowindex);
			        	for(var i = 0; i < alloc.attr.ITEM_DATA.length; i++){
			        		if(alloc.attr.ITEM_DATA[i].seqId == row.seqId){
			        			alloc.attr.ITEM_DATA.splice(i, 1);
			        			alloc.attr.INDEX--;
			        			alloc.attr.SEQ--;
			        		}
			        	}
			        	source.localdata = alloc.attr.ITEM_DATA;
						$("#newAllocGrid").jqxGrid('updatebounddata');
					});
			        
			        $('#btnAdd').on('click', function(event){
			    		allocParty.openWindow();
					});
			   	}
				
		        $("#newAllocGrid").jqxGrid({
		        	width: '100%',
		        	theme: 'olbius',
			   		virtualmode: false,
			   		showfilterrow: false,
			   		showtoolbar: true,
			   		source: dataAdapter,
			   		rendertoolbar: rendertoolbar,
			   		selectionmode: 'singlerow',
			   		editmode: 'selectedcell',
			   		pageable: true,
			   		sortable: false,
			        filterable: false,
			        editable: true,
			        autoheight: true,
			        localization: getLocalization(),
		            columns: [
		                { text: '${uiLabelMap.BACCSeqId}',filterable : false, datafield: 'seqId',  width: 150, editable: false},
						{ text: '${uiLabelMap.BACCAllocPartyId} ',filterable : false, datafield: 'allocPartyId',width: 250, columntype: 'template',
		                	createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
					 	   		  editor.append('<div id="partyGrid"></div>');
							 	  var configParty = {
											useUrl: true,
											root: 'results',
											widthButton: '100%',
											dropDownHorizontalAlignment: 'right',
											showdefaultloadelement: false,
											autoshowloadelement: false,
											datafields: [{name: 'partyId', type: 'string'}, {name: 'fullName', type: 'string'}],
											columns: [
												{text: '${uiLabelMap.BACCPartyId}', datafield: 'partyId', width: '30%'},
												{text: '${uiLabelMap.BACCFullName}', datafield: 'fullName'}
											],
											url: "JqxGetParties",
											useUtilFunc: true,
											
											key: 'partyId',
											description: ['fullName'],
									};
									accutils.initDropDownButton(editor, $("#partyGrid"), null, configParty, []);
									EDITOR_GRID = editor;
		                	}
						},
						{ text: '${uiLabelMap.BACCAllocPartyName} ', datafield: 'allocPartyName', columntype: 'template',
							cellsrenderer: function(row, column, value){
								  var data =  $("#newAllocGrid").jqxGrid('getrowdata', row);
								  var partyName = data.allocPartyId;
					    		  $.ajax({
					    				url: 'getPartyName',
					    				type: 'POST',
					    				data: {partyId: data.allocPartyId},
					    				dataType: 'json',
					    				async: false,
					    				success : function(data) {
					    					if(!data._ERROR_MESSAGE_){
					    						partyName = data.partyName;
					    					}
					    		        }
					    			});
					    		  return '<span title' + value + '>' + partyName + '</span>';
							}
						},
						{ text: '${uiLabelMap.BACCAllocRate} ', dataField: 'allocRate', columntype: 'numberinput', width: 150, filtertype  :'number'},                    	                     	 
						{ text: '${uiLabelMap.BACCAllocGlAccoutId}', dataField: 'allocGlAccountId', columntype: 'template', width: 150,
							createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
					 	   		  editor.append('<div id="glAccountGrid"></div>');
					 	   		  var configGlAccount = {
					 					useUrl: true,
					 					root: 'results',
					 					widthButton: '100%',
					 					dropDownHorizontalAlignment: 'right',
					 					showdefaultloadelement: false,
					 					autoshowloadelement: false,
					 					datafields: [{name: 'glAccountId', type: 'string'}, {name: 'accountName', type: 'string'}],
					 					columns: [
					 						{text: '${uiLabelMap.BACCGlAccountId}', datafield: 'glAccountId', width: '30%'},
					 						{text: '${uiLabelMap.BACCAccountName}', datafield: 'accountName'}
					 					],
					 					url: "JqxGetListGlAccounts",
					 					useUtilFunc: true,
					 					
					 					key: 'glAccountId',
					 					description: ['accountName'],
					 	   		  };
					 	   		  accutils.initDropDownButton(editor, $("#glAccountGrid"), null, configGlAccount, []);
					 	   		  EDITOR_GRID = editor;
		                	}
						},
					]
		    	});
			},
			
			bindEvent : function(){
				
			},
	}
</script>