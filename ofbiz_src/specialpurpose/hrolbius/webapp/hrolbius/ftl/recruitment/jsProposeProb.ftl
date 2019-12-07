//Create new family window

<#assign emplPositionTypeList = delegator.findByAnd("EmplPositionType", null, null, false)/>
var emplPositionTypeArr = [
	<#list emplPositionTypeList as emplPositionType>
		{emplPositionTypeId: '${emplPositionType.emplPositionTypeId}', description: '${StringUtil.wrapString(emplPositionType.description)?default("")}'}
		<#if emplPositionType_has_next>
		,
		</#if>
	</#list>
]; 

$("#proposeProbWindow").jqxWindow({
    showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "80%", height: 450, minWidth: '40%', width: "90%", isModal: true,
    theme:'olbius', collapsed:false, cancelButton:$("#alterCancelProb"),modalZIndex: 1000,
    initContent: function() {
    	var source = {
            localdata: offerProbData,
            datatype: "array",
            datafields:
            [
            	{ name: 'partyId', type: 'string' },
				{ name: 'partyIdWork', type: 'string' },
				{ name: 'emplPositionTypeId', type: 'string' },
				{ name: 'inductedStartDate', type: 'date', other: 'Timestamp'},
				{ name: 'inductedCompletionDate', type: 'date', other: 'Timestamp'},
				{ name: 'basicSalary', type: 'number'},
				{ name: 'trafficAllowance', type: 'number'},
				{ name: 'phoneAllowance', type: 'number'},
				{ name: 'otherAllowance', type: 'number'},
				{ name: 'percentBasicSalary', type: 'number'},
				{ name: 'comment', type: 'string'}
            ],
			updaterow: function (rowid, rowdata, commit) {
				offerProbData[rowid] = rowdata;
		        commit(true);
		    }
        };
    	var dataAdapter = new $.jqx.dataAdapter(source);
        
    	$("#jqxgridOfferProb").jqxGrid({
            width: '100%',
            source: dataAdapter,
            columnsresize: true,
            pageable: true,
            autoheight: true,
            editable: true,
            columns: [
                      { text: '${uiLabelMap.Applicant}', datafield: 'partyId', width: '150', editable: false,
                    	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
                    		  var partyName = value
                    		  $.ajax({
                    				url: 'getPartyName',
                    				type: "POST",
                    				data: {partyId: value},
                    				dataType: 'json',
                    				async: false,
                    				success : function(data) {
                    					if(!data._ERROR_MESSAGE_){
                    						partyName = data.partyName;
                    					}
                    		        }
                    			});
                    		  return '<span title' + value + '>' + partyName + '</span>'
                          } 
                      },
                      { text: '${uiLabelMap.Department}', datafield: 'partyIdWork', width: '200',editable: false,
                    	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
                    		  var partyName = value
                    		  $.ajax({
                    				url: 'getPartyName',
                    				type: "POST",
                    				data: {partyId: value},
                    				dataType: 'json',
                    				async: false,
                    				success : function(data) {
                    					if(!data._ERROR_MESSAGE_){
                    						partyName = data.partyName;
                    					}
                    		        }
                    			});
                    		  return '<span title' + value + '>' + partyName + '</span>'
                          } 
                      },
                      { text: '${uiLabelMap.Position}', datafield: 'emplPositionTypeId', width: '200', editable: false,
                    	  cellsrenderer: function (index, label, value) {
          					for(i=0; i < emplPositionTypeArr.length; i++){
          						if(emplPositionTypeArr[i].emplPositionTypeId == value){
          							return '<span title=' + value + '>' + emplPositionTypeArr[i].description + '</span>';
          						}
          					}
          				    return '<span title='+ value + '>' + value + '</span>';
          				}
                      },
                      { text: '${uiLabelMap.inductedStartDate}', datafield: 'inductedStartDate', width: '150',columntype: 'datetimeinput',cellsformat: 'dd/MM/yyyy',
                    	  createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
                    	        editor.jqxDateTimeInput({width: '100'});
                    	  },
                    	  validation: function (cell, value) {
                    	        if (!value) {
                    	            return { result: false, message: "${uiLabelMap.FieldRequired}" };
                    	        }
                    	        return true;
                    	    }
                      },
                      { text: '${uiLabelMap.inductedCompletionDate}', datafield: 'inductedCompletionDate', width: '150',columntype: 'datetimeinput',cellsformat: 'dd/MM/yyyy',
                    	  createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
                  	        editor.jqxDateTimeInput({width: '100'});
                    	  },
                    	  validation: function (cell, value) {
                  	        if (!value) {
                  	            return { result: false, message: "${uiLabelMap.FieldRequired}" };
                  	        }
                  	        return true;
                  	    }
                      },
                      { text: '${uiLabelMap.basicSalary}', datafield: 'basicSalary', width: '100', columntype: 'template',
                    	  validation: function (cell, value) {
                    	        if (!value) {
                    	            return { result: false, message: "${uiLabelMap.FieldRequired}" };
                    	        }
                    	        return true;
                    	    },
                    	    createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
                      	        editor.jqxNumberInput({ decimalDigits: 0, min: 0, width: 100, spinButtons: true });
                    	  },
                      },
                      { text: '${uiLabelMap.trafficAllowance}', datafield: 'trafficAllowance', width: '100', columntype: 'template',
                    	  validation: function (cell, value) {
                    	        if (!value) {
                    	            return { result: false, message: "${uiLabelMap.FieldRequired}" };
                    	        }
                    	        return true;
                    	    },
                    	    createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
                      	        editor.jqxNumberInput({ decimalDigits: 0, min: 0, width: 100, spinButtons: true });
                    	  },
                      },
                      { text: '${uiLabelMap.phoneAllowance}', datafield: 'phoneAllowance', width: '100', columntype: 'template',
                    	  validation: function (cell, value) {
                    	        if (!value) {
                    	            return { result: false, message: "${uiLabelMap.FieldRequired}" };
                    	        }
                    	        return true;
                    	    },
                    	    createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
                      	        editor.jqxNumberInput({ decimalDigits: 0, min: 0, width: 100, spinButtons: true });
                    	  },
                      },
                      { text: '${uiLabelMap.otherAllowance}', datafield: 'otherAllowance', width: '100', columntype: 'template',
                    	  validation: function (cell, value) {
                    	        if (!value) {
                    	            return { result: false, message: "${uiLabelMap.FieldRequired}" };
                    	        }
                    	        return true;
                    	    },
                    	    createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
                      	        editor.jqxNumberInput({ decimalDigits: 0, min: 0, width: 100, spinButtons: true });
                    	  },
                      },
                      { text: '${uiLabelMap.percentBasicSalary}', datafield: 'percentBasicSalary', width: '100', columntype: 'numberinput',
                    	  validation: function (cell, value) {
                    	        if (!value) {
                    	            return { result: false, message: "${uiLabelMap.FieldRequired}" };
                    	        }
                    	        return true;
                    	    }  
                      },
                      { text: '${uiLabelMap.comment}', datafield: 'comment'},
                 ]
        	});
    	}
});
$("#alterSaveProb").click(function(){
	var submitData = {};
	for(var i = 0; i < offerProbData.length; i++){
		submitData['emplPositionTypeId_o_' + i] = offerProbData[i].emplPositionTypeId;
		submitData['partyId_o_' + i] = offerProbData[i].partyId;
		var inductedCompletionDate = offerProbData[i].inductedCompletionDate;
		submitData['inductedCompletionDate_o_' + i] = inductedCompletionDate.getFullYear()+ "-" + (inductedCompletionDate.getMonth() + 1) + "-" + inductedCompletionDate.getDate() + " " + inductedCompletionDate.getHours() + ":" + inductedCompletionDate.getMinutes() + ":" + inductedCompletionDate.getSeconds();
		var inductedStartDate = offerProbData[i].inductedStartDate;
		submitData['inductedStartDate_o_' + i] = inductedStartDate.getFullYear()+ "-" + (inductedStartDate.getMonth() + 1) + "-" + inductedStartDate.getDate() + " " + inductedStartDate.getHours() + ":" + inductedStartDate.getMinutes() + ":" + inductedStartDate.getSeconds();
		submitData['basicSalary_o_' + i] =  offerProbData[i].basicSalary;
		submitData['statusId_o_' + i] = 'PROB_INIT';
		submitData['workEffortId_o_' + i] = '${parameters.workEffortId}';
		submitData['percentBasicSalary_o_' + i] = offerProbData[i].percentBasicSalary;
		submitData['trafficAllowance_o_' + i] = offerProbData[i].trafficAllowance;
		submitData['phoneAllowance_o_' + i] = offerProbData[i].phoneAllowance;
		submitData['otherAllowance_o_' + i] = offerProbData[i].otherAllowance;
		submitData['partyIdWork_o_' + i] = offerProbData[i].partyIdWork;
		submitData['comment_o_' + i] = offerProbData[i].comment;
	}
	//Sent request create applicant
	$.ajax({
		url: 'createOfferProbation',
		type: "POST",
		data: submitData,
		dataType: 'json',
		async: false,
		success : function(data) {
			if(data._ERROR_MESSAGE_){
				bootbox.confirm("Tạo mới đề xuất thử việc không thành công!", function(result) {
					return;
				});
			}else{
				bootbox.confirm("Tạo mới đề xuất thử việc thành công!", function(result) {
					return;
				});
				$("#jqxgrid").jqxGrid('updatebounddata');
				$("#proposeProbWindow").jqxWindow('close');
			}
        }
	});
});