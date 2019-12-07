var widzardObject = (function(){
	var init = function(){
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
	        if(info.step == 1 && (info.direction == "next")) {
	        	return trainingCourseInfo.validate();
	        }else if (info.step == 2 && (info.direction == "next")) {
	        	
	        }else if(info.step == 3 && (info.direction == "next")){
	        	var nbrEmplExpected = partyExpectedObj.getNbrEmplExpected();
	        	$("#nbrEmplEstimated" + globalVar.createNewSuffix).val(nbrEmplExpected);
	        }
	        if(info.direction == "previous"){
	        	trainingCourseInfo.hideValidate();
	        }
	    }).on('finished', function(e) {
	    	bootbox.dialog(uiLabelMap.ConfirmCreateNewTrainingCourse,
				[{
					"label" : uiLabelMap.CommonSubmit,
	    		    "class" : "btn-primary btn-small icon-ok open-sans",
	    		    "callback": function() {
	    		 		actionObj.createNewTrainingCourse();   	
	    		    }	
				},
				{
	    		    "label" : uiLabelMap.CommonClose,
	    		    "class" : "btn-danger btn-small icon-remove open-sans",
	    		}]		
			);
	    }).on('stepclick', function(e){
	        //return false;//prevent clicking on steps
	    });
	};
	
	var resetStep = function(){
		$('#fuelux-wizard').wizard('previous');
		$('#fuelux-wizard').wizard('previous');
		$('#fuelux-wizard').wizard('previous');
	};
	return{
		init: init,
		resetStep: resetStep
	}
	
}());

var actionObj = (function(){
	var createNewTrainingCourse = function(){
		var trainingCourseData = trainingCourseInfo.getData();
		var skillData = traininingSkillObject.getData();
		var providerCostData = providerTrainAndCostObj.getData();
		var partyExpectedData = partyExpectedObj.getData();
		var dataSubmit = $.extend({}, trainingCourseData, skillData, providerCostData, partyExpectedData);
		$("#btnNext").attr("disabled", "disabled");
		$("#btnPrev").attr("disabled", "disabled");
		$("#ajaxLoading").show()
		$.ajax({
			url: 'createNewTrainingCourse',
			data: dataSubmit,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == 'success'){
					commonObject.closeWindow();
					Grid.renderMessage('jqxgrid', response.successMessage, {autoClose: true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
					$("#jqxgrid").jqxGrid('updatebounddata');
				}else{
					bootbox.dialog(response.errorMessage,
						[{
							"label" : uiLabelMap.CommonClose,
			    		    "class" : "btn-danger btn-small icon-remove open-sans",    			    		    
						}]		
					);
				}
			},
			complete: function(jqXHR, textStatus){
    			$("#ajaxLoading").hide();
    			$("#btnNext").removeAttr("disabled");
    	    	$("#btnPrev").removeAttr("disabled");
    		}
		});
	};
	return{
		createNewTrainingCourse: createNewTrainingCourse
	}
}());

var commonObject = (function(){
	var init = function(){
		widzardObject.init();
		var initContent = function(){
			trainingCourseInfo.initInjqxWindow();
		};
		createJqxWindow($("#" + globalVar.createNewSuffix), 850, 590, initContent);
		create_spinner($("#spinner-ajax"));
		$("#" + globalVar.createNewSuffix).on('open', function(event){
			
			providerTrainAndCostObj.onWindowOpen();
		});
		$("#" + globalVar.createNewSuffix).on('close', function(event){
			trainingCourseInfo.reset();
			traininingSkillObject.reset();
			partyExpectedObj.reset();
			providerTrainAndCostObj.reset();
			widzardObject.resetStep();
			
		});
	};
	
	var closeWindow = function(){
		$("#" + globalVar.createNewSuffix).jqxWindow('close');
	};
	return{
		init: init,
		closeWindow: closeWindow
	}
}());

$(document).ready(function () {
	trainingCourseInfo.init();//trainingCourseInfo is defined in trainingCreateNewTrainingInfo.js
	traininingSkillObject.init();//traininingSkillObject is defined in trainingCreateTrainingSkill.js
	partyExpectedObj.init();//partyExpectedObj is defined in trainingCreatePartyExpectedJoin.js
	providerTrainAndCostObj.init();//providerTrainAndCostObj is defined in trainingCreateTrainingProvider.js
	commonObject.init();
});
