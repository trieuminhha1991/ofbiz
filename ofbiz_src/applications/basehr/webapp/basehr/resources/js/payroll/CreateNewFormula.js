var createNewFormulaObject = (function(){
	var _functionAfterBuildFormula = null;
	var init = function(){
		initJqxGridEvent();
		initJqxWindow();				
		initJqxNotification();
		initEvent();
	};
	
	var initJqxWindow = function(){
		var initContent = function(){
			initJqxSplitter();
			initJqxEditor();
		};
		createJqxWindow($("#settingFormula"), 850, 600, initContent);
		$("#settingFormula").on('close', function(event){
			$("#calc_des").val("");
			$("#hid_popdes").val("");
			$("#hid_cd_des").val("");
			$("#hid_cal_condition").val("");
			$("#jqxGridParam").jqxGrid('clearselection');
			$("#jqxGridParam").jqxGrid('gotopage', 0);
		});
	};
	var initEvent = function(){
		$("#cancelBuildFormula").click(function(event){
			$("#settingFormula").jqxWindow('close');
		});
		$("#saveBuildFormula").click(function(event){
			var value = $("#calc_des").val();
			if(typeof(_functionAfterBuildFormula) == "function"){
				_functionAfterBuildFormula(value);
			}
			$("#settingFormula").jqxWindow('close');
		});
	};
	
	var initJqxSplitter = function(){
		$('#mainSplitter').jqxSplitter({ width: '100%', height: 490, orientation: 'horizontal', panels: [{ size: 160 }, { size: 290}], splitBarSize: 3, });
		$("#nestedSplitter").jqxSplitter( {width: '100%', height: '100%',  orientation: 'horizontal', panels: [{ size: 120}], splitBarSize: 0})
	};

	var initJqxEditor = function (){
		$('#calc_des').jqxEditor({ 
			width: '100%',
	        theme: 'olbiuseditor',
	        tools: '',
	        height: 100,
	        disabled:true
	    });	
		$('#calc_des').val("");
		$('#calc_des').focus(function(){
			FocusColor2(this);
			fn_Selectitem(this);
		});
	};

	var initJqxNotification = function(){
		 $("#createFormulaNtf").jqxNotification({
	        width: "100%", opacity: 1, appendContainer: "#appendNotification",
	        autoOpen: false, autoClose: true
	    });
	};

	var initJqxGridEvent = function(){
		/*$("#jqxGridFormula").on('rowdoubleclick', function (event){
			var args = event.args;
		    var boundIndex = args.rowindex;
		    var data = $("#jqxGridFormula").jqxGrid('getrowdata', boundIndex);
		    cal_add(data.code, data.name, 1, true);
		});*/
		$("#jqxGridParam").on('rowdoubleclick', function (event){
			var args = event.args;
		    var boundIndex = args.rowindex;
		    var data = $("#jqxGridParam").jqxGrid('getrowdata', boundIndex);
		    cal_add(data.code, data.name, 1, false);
		});
	};
	
	var validate = function(){
		//return $('#createNewFormulaForm').jqxValidator('validate');
	};
	var setFunctionAfterBuildFormula = function(value){
		_functionAfterBuildFormula = value;
	};
	return{
		init: init,
		validate: validate,
		setFunctionAfterBuildFormula: setFunctionAfterBuildFormula
	}
}());


$(document).ready(function(){
	createNewFormulaObject.init();
});
