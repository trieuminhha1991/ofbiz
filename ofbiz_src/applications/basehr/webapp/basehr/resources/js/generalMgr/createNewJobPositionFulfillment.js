var createNewJobPosFulfillmentObject = (function(){
	var init = function(){
		initJqxInput();
		initBtnEvent();
		initJqxValidator();
		initJqxWindow();
		initJqxGrid();
		initJqxDateTimeInput();
	};
	
	var initJqxDateTimeInput = function(){
		$("#fulfillFromDate, #fulfillThruDate").jqxDateTimeInput({width: '97%', height: 25, theme: 'olbius'});
		restrictFomDateThruDate($("#fulfillFromDate"), $("#fulfillThruDate"));
	};
	
	var initJqxGrid = function(){
		createJqxGridSearchEmpl($("#EmplListInOrg"), {uiLabelMap: uiLabelMap});
       	$("#EmplListInOrg").on('rowdoubleclick', function(event){
       		var args = event.args;
    	    var boundIndex = args.rowindex;
    	    var data = $("#EmplListInOrg").jqxGrid('getrowdata', boundIndex);
    	    $('#popupWindowEmplList').jqxWindow('close');
    	    $("#partyIdFulfill").jqxInput('val', {value: data.partyId, label: data.fullName + ' [' + data.partyCode + ']'});
       	});
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#assignEmplPosFulWindow"), 450, 260);
		createJqxWindow($("#popupWindowEmplList"), 850, 540);
		$("#assignEmplPosFulWindow").on('close', function(event){
			Grid.clearForm($(this));
			$("#EmplListInOrg").jqxGrid('clearselection');
			//delete globalVar.partyGroupId;
		});
		$("#assignEmplPosFulWindow").on('open', function(event){
			var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
			$("#fulfillFromDate").val(selection.from);
		});
	};
	
	var initJqxInput = function(){
		$("#emplPositionTypeIdFulfill").jqxInput({width: '96%', height: 20, theme: 'olbius', disabled: true});
		
		$("#partyIdFulfill").jqxInput({ placeHolder: uiLabelMap.EnterEmployeeId,
			height: 23, width: '85%', minLength: 1, theme: 'olbius', valueMember: 'partyId', displayMember:'partyName', items: 12});
	};
	
	var initBtnEvent = function(){
		$("#alterCancelFulfillment").click(function(event){
			$("#assignEmplPosFulWindow").jqxWindow('close');
		});
		$("#alterSaveFulfillment").click(function(event){
			var valid = $("#assignEmplPosFulWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.AssignEmplToPositionConfirm,
				[
					{
					    "label" : uiLabelMap.CommonSubmit,
					    "class" : "icon-ok btn btn-small btn-primary",
					    "callback": function() {
					    	createEmplPositionFulfillment();
					    	$("#assignEmplPosFulWindow").jqxWindow('close');
					    }
					},
					{
						  "label" : uiLabelMap.CommonCancel,
			    		   "class" : "btn-danger icon-remove btn-small",
			    		   "callback": function() {
			    		   
			    		   }
					}
				]		
			);
		});
		
		$("#searchBtnId").click(function(event){
			var tmpS = $("#EmplListInOrg").jqxGrid('source');
			tmpS._source.url = 'jqxGeneralServicer?sname=JQGetEmplListInOrg&hasrequest=Y&partyGroupId=' + globalVar.partyGroupId;
			$("#EmplListInOrg").jqxGrid('source', tmpS);
			openJqxWindow($("#popupWindowEmplList"));
		});
	};
	
	var createEmplPositionFulfillment = function(){
		var dataSubmit = {};
		dataSubmit.emplPositionId = $("#emplPositionId").val();
		var partyId = $("#partyIdFulfill").val();
		if(partyId.value){
			partyId = partyId.value; 
		}
		dataSubmit.partyId = partyId;
		dataSubmit.fromDate = $("#fulfillFromDate").jqxDateTimeInput('val', 'date').getTime();
		var thruDate = $("#fulfillThruDate").jqxDateTimeInput('val', 'date');
		if(thruDate != null){
			dataSubmit.thruDate = thruDate.getTime();
		}
		$("#jqxNotify").jqxNotification('closeLast');
		$('#jqxgrid').jqxGrid({ disabled: true});
		$('#jqxgrid').jqxGrid('showloadelement');
		$.ajax({
			url: 'createEmplPositionFulfillment',
			data: dataSubmit,
			type: 'POST',
			success: function(response){
				$("#jqxNotify").jqxNotification('closeLast');
				$("#jqxWindowPositionDetail").jqxWindow('close');
				if(response.responseMessage == 'success'){
					$("#ntfContent").html(response.successMessage);
					$("#jqxNotify").jqxNotification({template: 'info'});
    				$("#jqxNotify").jqxNotification("open");
    				$('#jqxgrid').jqxGrid('updatebounddata');
				}else{
					$("#ntfContent").html(response.errorMessage);
					$("#jqxNotify").jqxNotification({template: 'error'});
    				$("#jqxNotify").jqxNotification("open");
				}
			},
			complete: function(jqXHR, textStatus){
				$('#jqxgrid').jqxGrid({ disabled: false});
				$('#jqxgrid').jqxGrid('hideloadelement');
			}
		});
	};
	
	var initJqxValidator = function(){
		$("#assignEmplPosFulWindow").jqxValidator({
			rules: [
		    {
				input: "#searchBtnId", message: uiLabelMap.FieldRequired, 
				action: 'blur',
			    rule: function (input, commit) {
					var value = $("#partyIdFulfill").val();
					if(!value){
						return false;
					}
					return true;
			   }
		    },	        
		    {
				input: '#fulfillFromDate',
				message: uiLabelMap.FieldRequired,
				action: 'blur',
				rule: function (input, commit) {
					if(!input.val()){
						return false;
					}
					return true;
				}
			}
		    ],
		 });
	};
	
	var getSourceJqxInputText = function(parameters, searchUrl){
		var source = function(query, response){
			var dataApdapter = new $.jqx.dataAdapter(
				{
					datatype: "json",
					datafields:
	                [
	                    { name: 'partyId' },
	                    { name: 'partyName'},
	                ],
	                url: searchUrl,
	                data: parameters
				},
				{
					autoBind: true,
					formatData: function (data) {
	                    data.partyId_startsWith = query;
	                    return data;
	                },
	                loadComplete: function(data) {
	                    if (data.listParty.length > 0) {
	                        response($.map(data.listParty, function (item) {
	                            return {
	                                label: item.partyName + ' [' + item.partyId + ']',
	                                value: item.partyId
	                            }
	                        }));
	                    }
	                }
				}
			);
		};
		return source;
	}
	
	return{
		init: init,
		getSourceJqxInputText: getSourceJqxInputText
	};
}());

function assignPositionForEmpl(gridEle, rowIndex){
	var datarecord = $(gridEle).jqxGrid('getrowdata', rowIndex); 
	var emplPositionId = datarecord.emplPositionId;
	$("#emplPositionId").val(emplPositionId);
	$("#fulfillThruDate").val(null);
	var minDate = datarecord.actualFromDate;
	$("#fulfillFromDate").jqxDateTimeInput('setMinDate', minDate);
	$("#fulfillThruDate").jqxDateTimeInput('setMinDate', minDate);
	var maxDate = datarecord.actualThruDate;
	if(maxDate){
		$("#fulfillFromDate").jqxDateTimeInput('setMaxDate', maxDate);
		$("#fulfillThruDate").jqxDateTimeInput('setMaxDate', maxDate);
	}
	$("#emplPositionTypeIdFulfill").val(datarecord.description);	
	var sourceSearchParty = $("#partyIdFulfill").jqxInput('source');
	var parameters = {maxRows: 12, partyGroupId: datarecord.partyId, fromDate: datarecord.actualFromDate.getTime()};
	if(datarecord.actualThruDate){
		parameters.thruDate = datarecord.actualThruDate.getTime();
	}
	sourceSearchParty = createNewJobPosFulfillmentObject.getSourceJqxInputText(parameters, 'searchPartyId');
	$("#partyIdFulfill").jqxInput({source:  sourceSearchParty});
	//globalVar.partyGroupId = datarecord.partyId;
	openJqxWindow($("#assignEmplPosFulWindow"));
};

$(document).ready(function () {
	createNewJobPosFulfillmentObject.init();
});