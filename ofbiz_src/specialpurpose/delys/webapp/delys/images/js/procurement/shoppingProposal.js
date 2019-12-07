var popup = $("#alterpopupWindow");
var productChosenGrid = $("#jqxgridProductChosen");
initWindow(popup);
initRule(popup);
function getDataEditor(ck, key) {
	if (ck[key]) {
		return ck[key].getData();
	}
	return "";
}
function initWindow(popup){
	var sourceLC =
	{
	    localdata: dataLC,
	    datatype: "array"
	};
	$("#requirementStartDate").jqxDateTimeInput({width: '265px', height: '25px'});
	$("#requirementStartDate").val("");
	$("#requiredByDate").jqxDateTimeInput({width: '265px', height: '25px'});
	$("#requiredByDate").val("");
	$("#estimatedBudget").jqxNumberInput({ width: '265px', disabled:true, height: '25px', max : 999999999999, digits: 12 });
	var dataAdapterLC = new $.jqx.dataAdapter(sourceLC);
	$('#currencyUomContainer').jqxDropDownList({theme:theme, source: dataAdapterLC,  width: '265px', displayMember: "description", valueMember: "uomId", filterable: true});
	for(var x in dataLC){
		if(dataLC[x].uomId == "VND"){
			$('#currencyUomContainer').jqxDropDownList("selectIndex", parseInt(x));
		}
	}	
	console.log(1231);
	CKEDITOR.replace( 'reason', {enableTabKeyToolsv: true, skin: 'office2013', height: "130px"});
	
	popup.jqxWindow({
        width: 800, height: 600, resizable: false,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:theme           
    });
	
    $("#alterSave").jqxButton();
    // update the edited row when the user clicks the 'Save' button.
    $("#alterSave").click(function () {
    	if(!popup.jqxValidator("validate")){
    		return;
    	}
    	var products = productChosenGrid.jqxGrid("getboundrows");
    	var index = $('#currencyUomContainer').jqxDropDownList("getSelectedItem");
    	var currencyUomId = index && index.value ? index.value : ""; 
    	var ck = CKEDITOR.instances;
    	var row = { 
       		requirementStartDate:$('#requirementStartDate').jqxDateTimeInput('getDate'),
       		requiredByDate:$('#requirementStartDate').jqxDateTimeInput('getDate'),
       		products: JSON.stringify(products),
       		reason: getDataEditor(ck, "reason"), 
       		currencyUomId: currencyUomId
       	};
    	console.log(row);
        var gr = $("#jqxgrid");
	    gr.jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        gr.jqxGrid('clearSelection');                        
        gr.jqxGrid('selectRow', 0);  
        popup.jqxWindow('close');
    });
}
function initRule(popup){
	var ck = CKEDITOR.instances;
	popup.jqxValidator({
	   	rules: [{
			input: '#reasoncontainer',
			message: uiLabelMap.FieldRequired,
			action: 'mouseleave',
			rule: function (input, commit) {
                var index = getDataEditor(ck, "reason");
                console.log(index);
                return index != "";
            }
		},{
			input: '#jqxgridProductChosen',
			message: uiLabelMap.FieldRequired,
			action: 'blur',
			rule: function (input, commit) {
                var products = productChosenGrid.jqxGrid("getboundrows");
                
                return products.length != 0;
            }
		},{
            input: "#currencyUomContainer", 
            message: uiLabelMap.FieldRequired, 
            action: 'blur', 
            rule: function (input, commit) {
                var index = input.jqxDropDownList('getSelectedIndex');
                return index != -1;
            }
        },{
            input: "#requirementStartDate", 
            message: uiLabelMap.FieldRequired, 
            action: 'blur', 
            rule: function (input, commit) {
                var index = input.jqxDateTimeInput('val');
                return index != "";
            }
        },{
            input: "#requiredByDate", 
            message: uiLabelMap.FieldRequired, 
            action: 'blur', 
            rule: function (input, commit) {
                var index = input.jqxDateTimeInput('val');
                return index != "";
            }
        }]
	 });
}