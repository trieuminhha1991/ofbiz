<#assign dataField="[{ name: 'glJournalId', type: 'string' },
               		{ name: 'glJournalName', type: 'string' },
               		{ name: 'isPosted', type: 'string' },
               		{ name: 'postedDate', type: 'date', other: 'Timestamp'}
               		]"/>
               		
<#assign columnlist="{ text: '${uiLabelMap.Journals}', datafield: 'glJournalId', width: 200},
					 { text: '${uiLabelMap.JournalName}', datafield: 'glJournalName', width: 300},
                     { text : '${uiLabelMap.JournalIsPosted}', datafield: 'isPosted', width: 190},
                     { text : '${uiLabelMap.JournalPostedDate}', datafield: 'postedDate'}
					 "/>          
<@jqGrid url="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&sname=JQListJournals" dataField=dataField columnlist=columnlist
		 id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true" 
		 addrefresh="true"
		 addrow="true" addType="popup" alternativeAddPopup="alterpopupWindow" createUrl="jqxGeneralServicer?jqaction=C&sname=createGlJournal"
		 addColumns="glJournalName;organizationPartyId[${parameters.organizationPartyId}]"
		 deleterow="true" removeUrl="jqxGeneralServicer?sname=deleteGlJournal&jqaction=D" 
		 deleteColumn="glJournalId;organizationPartyId[${parameters.organizationPartyId}]" 
 />	
 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxinput.js"></script>

<div id="alterpopupWindow" class="hide">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
		 <form id="formAdd">	
			<div class="row-fluid margin-bottom10">
				<div class="span5 align-right">
					<label class="asterisk">${uiLabelMap.JournalName}</label>
				</div>
				<div class="span7">
					<input id="glJournalName" type="text"></input>
				</div>
			</div>
		 </form>
    		<div class="form-action">
				<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
				<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
    		</div>
    </div>
</div> 		

<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	var action = (function(){
		var initElement = function(){
				$('#glJournalName').jqxInput({width : 250,height : 25});
				$("#alterpopupWindow").jqxWindow({
			        width: 500,height : 140, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme:theme           
			    });
			}
		var bindEvent = function(){
				$("#save").click(function () {
				if(!$('#formAdd').jqxValidator('validate')){return;}
					var row;
			        row = {
			        		glJournalName: $('#glJournalName').val()
			        	  };
				   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
			       $("#alterpopupWindow").jqxWindow('close');
				});
			$('#alterpopupWindow').on('close',function(){
				$('#glJournalName').jqxInput('val','');
			})	
		}
	    var initRules = function(){
	    $('#formAdd').jqxValidator({
	    	rules : [
	    		{
	    			input : '#glJournalName',
	    			message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',
	    			action : 'change,blur',
	    			rule : function(input){
	    				var val = input.jqxInput('val');
	    				if(!val) return false;
	    				return true;	
	    			}
	    		}
	    	]
	    })
	    }
		return {
				init : function(){
					initElement();
					bindEvent();
					initRules();
				}
			}
	}())
	$(document).ready(function(){
		action.init();
	})
	
</script>		      		