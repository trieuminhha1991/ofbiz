var accbootbox = (function(){
	this.labelsUse;
	var getLabels = function(labelsUse){
		this.labelsUse = {
				'ok' : labelsUse.ok,
				'cancel' : labelsUse.cancel,
				'header1' : labelsUse.header1,
				'header2' : labelsUse.header2
		}
	};
	
	var getMessageAction = function(message,labels,formName,otherName){
		var objAct = {
				labels : labels,
				action : message,
				formName : formName,
				otherName :otherName
		};
		return objAct;
	};
	
	var initBootbox = function(labels,action,formName,otherName){
		if(labels.length == 1) labels = '';
		var temp = (labels  ? ('\"' +  labels  +'\"') : ' ');
		if(otherName){
			var str = '<div ><div class="row-fluid"><div class="span12" style="font-size : 13px;"><span style="color : #037c07;font-weight :bold;"><i class="fa-hand-o-right"></i>&nbsp;' + otherName +'</span> <span style="color:red;font-weight : bold;">' + temp + '&nbsp; <span style="color : #037c07;font-weight :bold;">' + accbootbox.labelsUse.header2  +'</span></span></div></div></div>';
		}else{
			var str = '<div ><div class="row-fluid"><div class="span12" style="font-size : 13px;"><span style="color : #037c07;font-weight :bold;"><i class="fa-hand-o-right"></i>&nbsp;' + accbootbox.labelsUse.header1 +'</span> <span style="color:red;font-weight : bold;">' + temp + '&nbsp; <span style="color : #037c07;font-weight :bold;">' + accbootbox.labelsUse.header2  +'</span></span></div></div></div>';
		};
		
		bootbox.dialog(str, [{
            "label"   : accbootbox.labelsUse.cancel,
            "icon"    : 'fa fa-remove',
            "class"   : 'btn  btn-danger form-action-button pull-right',
            "callback": function() {
            	bootbox.hideAll();
            }
        }, {
            "label"   : accbootbox.labelsUse.ok,
            "icon"    : 'fa-check',
            "class"   : 'btn btn-primary form-action-button pull-right',
            "callback": function() {
            	var forms = document.forms;
            	if(forms && forms.length > 0){
            		for(var i = 0;i < forms.length;i++){
            			if(forms[i].name == formName){
            				forms[i].submit();
            				break;
            			}
            		}
            	}
           	}
        }]);
	};
	
	var confirmAct = function(action,labels,formName,otherName){
		var direction;
		labels = labels.toLowerCase();
		if(action){
			direction =  getMessageAction(action,labels,formName,otherName);
			if(direction.hasOwnProperty('labels') && direction.hasOwnProperty('action') && direction.hasOwnProperty('formName')){
				initBootbox(direction.labels,direction.action,direction.formName,direction.otherName);
			}
		}
	};
	return {
		confirmAct : confirmAct,
		getLabels : getLabels
	};
}())