$.fn.spin = function(opts) {
	this.each(function() {
	  var $this = $(this),
		  data = $this.data();

	  if (data.spinner) {
		data.spinner.stop();
		delete data.spinner;
	  }
	  if (opts !== false) {
		data.spinner = new Spinner($.extend({color: $this.css('color')}, opts)).spin(this);
	  }
	});
	return this;
};

//function transformNumberIntoString(number){
//	var digits = number.split("");
//	var length = digits.length;
//}

function getMonthData(){
	var monthSort = [
     	{text : '${StringUtil.wrapString(uiLabelMap.CommonJanuary)}', value : 'jan'},
     	{text : '${StringUtil.wrapString(uiLabelMap.CommonFebruary)}', value : 'feb'},
     	{text : '${StringUtil.wrapString(uiLabelMap.CommonMarch)}', value : 'mar'},
     	{text : '${StringUtil.wrapString(uiLabelMap.CommonApril)}', value : 'apr'},
     	{text : '${StringUtil.wrapString(uiLabelMap.CommonMay)}', value : 'may'},
     	{text : '${StringUtil.wrapString(uiLabelMap.CommonJune)}', value : 'jun'},
     	{text : '${StringUtil.wrapString(uiLabelMap.CommonJuly)}', value : 'jul'},
     	{text : '${StringUtil.wrapString(uiLabelMap.CommonAugust)}', value : 'aug'},
     	{text : '${StringUtil.wrapString(uiLabelMap.CommonSepember)}', value : 'sep'},
     	{text : '${StringUtil.wrapString(uiLabelMap.CommonOctobor)}', value : 'oct'},
     	{text : '${StringUtil.wrapString(uiLabelMap.CommonNovember)}', value : 'nov'},
     	{text : '${StringUtil.wrapString(uiLabelMap.CommonDecember)}', value : 'dec'},
     ];
	return monthSort;
}

function validationNameWithoutHtml(val){
	var flag = false;
	//var char = "<>,\;:[]{}+=()-*&^%$#@|?!";
	var char = "<>+=-*^$#@|";
	for(var i=0 ; i< val.length; i++){
		if(char.indexOf(val[i]) != -1){
			flag = true;
			break;
		}
	}
	return flag;
}

function createJqxDropDownList(sourceArr, elemenDiv, valueMember, displayMember, height, width, renderer, placeHolder){
	var config = {valueMember: valueMember, displayMember: displayMember, height: height, width: width, renderer: renderer, placeHolder: placeHolder};
	createJqxDropDownListExt(elemenDiv, sourceArr, config);
}

function createJqxDropDownListExt(elemenDiv, sourceArr, config){
	var source = {
			localdata: sourceArr,
	        datatype: "array"	
	};
	var dataAdapter = new $.jqx.dataAdapter(source);
	
	config.source = dataAdapter;
	elemenDiv.jqxDropDownList(config);
	if(typeof renderer != "undefined"){
		elemenDiv.jqxDropDownList({renderer: renderer});
	};
	
	if(sourceArr.length < 8){
		elemenDiv.jqxDropDownList({autoDropDownHeight: true});
	}
}

function createJqxDropDownListBinding(elemenDiv, datafields, url, root, valueMember, displayMember, width, height, loadCompleteCallback){
	var source =
    {
        datatype: "json",
        datafields: datafields,
        url: url,
        root: root,
        type: 'POST',
        async: true,
    };
    var dataAdapter = new $.jqx.dataAdapter(source,{
    	loadComplete: function(records){
    		if(records[root] && records[root].length >= 8){
    			elemenDiv.jqxDropDownList({autoDropDownHeight: false});
    		}else{
    			elemenDiv.jqxDropDownList({autoDropDownHeight: true});
    		}
    		if(typeof(loadCompleteCallback) == "function"){
    			loadCompleteCallback();
    		}
    	}
    });
    elemenDiv.jqxDropDownList({
        source: dataAdapter, displayMember: displayMember, valueMember: valueMember, width: width, height: height
    });
}

function updateJqxDropDownListBinding(elemenDiv, newUrl){
	var source = elemenDiv.jqxDropDownList('source');
	elemenDiv.jqxDropDownList('clearSelection');
	source._source.url = newUrl;
	elemenDiv.jqxDropDownList('source', source);
}

function openJqxWindow(jqxWindowDiv){
	var wtmp = window;
	var tmpwidth = jqxWindowDiv.jqxWindow('width');
	jqxWindowDiv.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 30 } });
	jqxWindowDiv.jqxWindow('open');
}

function setDropdownContent(element, jqxTree, dropdownBtn){
	var item = jqxTree.jqxTree('getItem', element);
	var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + item.label + '</div>';
	dropdownBtn.jqxDropDownButton('setContent', dropDownContent);
	dropdownBtn.jqxDropDownButton('close');
}

function clearDropDownContent(jqxTree, dropdownBtn){
	jqxTree.jqxTree('selectItem', null);
	dropdownBtn.jqxDropDownButton('setContent', "");
}

function restrictFomDateThruDate(fromDateEle, thruDateEle){
	fromDateEle.on('change', function (event){
		thruDateEle.jqxDateTimeInput('setMinDate', event.args.date);	
	});
	thruDateEle.on('change', function (event){
		fromDateEle.jqxDateTimeInput('setMaxDate', event.args.date);	
	});
}

function createJqxWindow(divEle, width, height, initContent){
	if(typeof(initContent) == null){
		initContent = function () {};
	}
	var config = {
	        showCollapseButton: false, width: width, theme:'olbius',
	        autoOpen: false, isModal: true, maxWidth: 1000, 
	        initContent: initContent
	};
	if(typeof(height) != 'undefined'){
		config.height = height;
	}
	divEle.jqxWindow(config);
}

function setJqxTreeDropDownSelectEvent(treeDiv, dropDownBtn){
	treeDiv.on('select', function(event){			
		var id = event.args.element.id;
	    var item = treeDiv.jqxTree('getItem', event.args.element);
    	setDropdownContent(event.args.element, treeDiv, dropDownBtn);
    });
}

function createJqxDropdownButton(dropdownBtnEle, width, height, theme){
	dropdownBtnEle.jqxDropDownButton({ width: width, height: height, theme: theme});
}

function updateSourceDropdownlist(dropdownEle, sourceArr){	
	var source = {
			localdata: sourceArr,
            datatype: "array"
	}
	var dataAdapter = new $.jqx.dataAdapter(source);
	dropdownEle.jqxDropDownList('clearSelection');
	dropdownEle.jqxDropDownList({source: dataAdapter});
	if(sourceArr.length < 8){
		dropdownEle.jqxDropDownList({autoDropDownHeight: true});
	}else{
		dropdownEle.jqxDropDownList({autoDropDownHeight: false});
	}
}

function createJqxMenu(id, itemHeight, width, config){
	var liElement = $("#" + id + ">ul>li").length;
	var contextMenuHeight = itemHeight* liElement;
	if(typeof(config) == 'undefined'){
		config = {};
	}
	config.width = width;
	config.height = contextMenuHeight;
	config.autoOpenPopup = false;
	config.mode = "popup";
	$("#" + id).jqxMenu(config);
}

function createExpandEventJqxTree(jqxTreeDiv, callbackGetExtData, async, expandCompleteFunc, parentKey){
	if(typeof(async) == 'undefined'){
		async = true;
	}
	jqxTreeDiv.on('expand', function (event) {
		 var item = jqxTreeDiv.jqxTree('getItem', event.args.element);
		 var label = item.label;
		 var value = item.value;
		 var idItem = item.id;
		 var $element = $(event.args.element);
		 var loader = false;
		 var loaderItem = null;
         var children = $element.find('ul:first').children();
         $.each(children, function () {
             var item = jqxTreeDiv.jqxTree('getItem', this);
             if (item && item.label == 'Loading...') {
                 loaderItem = item;
                 loader = true;
                 return false
             };
         });
         if (loader) {
        	var suffixIndex = idItem.lastIndexOf("_");
        	var suffix = idItem.substring(suffixIndex);   
        	var dataSubmit = {};
        	if(typeof(parentKey) == "undefined"){
        		parentKey = "partyIdFrom";
        	}
        	dataSubmit[parentKey] = value;
        	if(typeof(callbackGetExtData) == "function"){
        		var extData = callbackGetExtData();
        		$.extend(dataSubmit, extData);
        	}
            $.ajax({
                 url: loaderItem.value,
                 data: dataSubmit,
                 type: 'POST',
                 async: async,
                 success: function (data, status, xhr){ 
                	var listReturn = data.listReturn;
                	if(listReturn){
                		var tempItem = jqxTreeDiv.jqxTree('getItem', loaderItem.element);
                		var checked = tempItem.checked;
                		for(var i = 0; i < listReturn.length; i++){
                			var id = listReturn[i].id;
                			if(id){
                				listReturn[i].id = id + suffix;
                				listReturn[i].parentid = idItem;  
                				if(checked){
                					listReturn[i].checked = true;
                				}
                			}
                		}
                		var items = jQuery.parseJSON(JSON.stringify(data.listReturn));
                		jqxTreeDiv.jqxTree('addTo', items, $element[0]);
                		jqxTreeDiv.jqxTree('removeItem', loaderItem.element);
                	}
                 },
                 complete: function(jqXHR, textStatus){
                	 var items = jqxTreeDiv.jqxTree('getItems');
                	 if(items.length > 8 && !jqxTreeDiv.jqxTree('height')){
                		 jqxTreeDiv.jqxTree({height: 250});
                		 jqxTreeDiv.jqxTree('refresh');
                	 }
                	 if(typeof(expandCompleteFunc) == "function"){
                		 expandCompleteFunc();
                	 }
                 }
             });
         }else{
        	 if(typeof(expandCompleteFunc) == 'function'){
        		 expandCompleteFunc();
        	 }
         }
	});
}

function getMonth(date) {
    var month = date.getMonth() + 1;
    return month < 10 ? '0' + month : '' + month; // ('' + month) for string result
}

function getHours(date) {
	var hours = date.getHours();
	return hours < 10 ? '0' + hours : '' + hours; 
}

function getMinutes(date){
	var minutes = date.getMinutes();
	return minutes < 10 ? '0' + minutes : '' + minutes;
}

function getDate(date) {
	var date = date.getDate();
	return date < 10 ? '0' + date: '' + date;
}

function create_spinner(spinnerEle, opts){
	if(typeof(opts) == "undefined" || !opts){
		opts = {
			corners: 0.2,
			length: 7,
			lines: 11,
			radius: 8,
			rotate: 8,
			speed: 1,
			trail: 60,
			width: 4
		};
	}	
	spinnerEle.spin(opts);
}

function convertDate(date) {
 	if (!date) {
		return null;
	}
	var dateArray = date.split("/");
	var newDate = new Date(dateArray[2] + "-" + dateArray[1] + "-" + dateArray[0]);
	return newDate.getTime();
}

function clearAceInputFile(divAce){
	divAce.parent().find('a.remove').trigger('click');
}

function createJqxTooltip(element, message){
	element.jqxTooltip({ content: message, name: 'movieTooltip', position: 'mouseenter', theme: 'olbius'});
}

function createJqxGridSearchEmpl(grid, properties){
	var datafield =  [
		{name: 'partyId', type: 'string'},
		{name: 'partyCode', type: 'string'},
		{name: 'firstName', type: 'string'},
		{name: 'fullName', type: 'string'},
		{name: 'emplPositionType', type: 'string'},
		{name: 'department', type: 'string'},
		{name: 'dateJoinCompnay', type: 'date'},
	];
	var uiLabelMap = typeof(properties.uiLabelMap) != "undefined"? properties.uiLabelMap: {};
	var columnlist = [
      {text: uiLabelMap.EmployeeId, datafield: 'partyCode' , editable: false, cellsalign: 'left', width: 110, filterable: true},
	  {text: uiLabelMap.EmployeeName, datafield: 'firstName', editable: false, cellsalign: 'left', width: 160, filterable: true,
    	  cellsrenderer: function(row, column, value){
				var rowData = grid.jqxGrid('getrowdata', row);
				if(rowData){
					return '<span>' + rowData.fullName + '</span>'; 
				}
			}
	  },
	  {text: uiLabelMap.HrCommonPosition, datafield: 'emplPositionType', editable: false, cellsalign: 'left', width: 150, filterable: true},
	  {text: uiLabelMap.CommonDepartment, datafield: 'department', editable: false, cellsalign: 'left', filterable: true},
	  {text: uiLabelMap.DateJoinCompany, hidden: true, datafield: 'dateJoinCompnay', cellsformat: 'dd/MM/yyyy', editable: false, cellsalign: 'left', filterable: true,
		  filtertype: 'range'  
	  },
	];
	
	if(typeof(properties.selectionmode) == 'undefined'){
		properties.selectionmode = 'singlerow';
	}
	
	if(typeof(properties.width) == 'undefined'){
		properties.width = '100%';
	}
	if(typeof(properties.height) == 'undefined'){
		properties.height = 467;
	}
	
	if(typeof(properties.url) == 'undefined'){
		properties.url = 'JQGetEmplListInOrg&hasrequest=Y';
	}
	var source = {};
	source.pagesize = 15;
	if(typeof(properties.sourceId) != 'undefined'){
		source.id = properties.sourceId;
	}
	var config = {
		width: properties.width, 
		height: properties.height,
		autoheight: false,
		virtualmode: true,
		showfilterrow: false,
		showtoolbar: false,
		selectionmode: properties.selectionmode,
		pageable: true,
		sortable: false,
        filterable: true,
        editable: false,
        url: properties.url,
        source: source
	};
	Grid.initGrid(config, datafield, columnlist, null, grid);
}

function getDateBetween(fromDate, thruDate){
	var ONE_DAY_MILLIS = 24*60*60*1000;
	var diffMillis = thruDate.getTime() - fromDate.getTime();
	if(diffMillis < 0){
		diffMillis += ONE_DAY_MILLIS;
	}
	return new Date(fromDate.getTime() + diffMillis/2);
}

function getPartyAgreementInfo(partyId, agreementNbrDiv, btnEle, agreementEffectiveDiv, agreementSignDateDiv, agreementDurationDiv){
	agreementNbrDiv.jqxInput({disabled: true});
	if(agreementEffectiveDiv){
		agreementEffectiveDiv.jqxDateTimeInput({disabled: true});
	}
	if(agreementSignDateDiv){
		agreementSignDateDiv.jqxDateTimeInput({disabled: true});
	}
	if(agreementDurationDiv){
		agreementDurationDiv.jqxDropDownList({disabled: true});
	}
	btnEle.attr("disabled", "disabled");
	$.ajax({
		url: 'getPartyAgreementInfo',
		data: {partyId: partyId},
		type: 'POST',
		success: function(response){
			if(response.responseMessage == 'success'){
				if(response.agreementNotSet){
					bootbox.dialog(response.agreementNotSet,
							[{
				    		    "label" : uiLabelMap.CommonClose,
				    		    "class" : "btn-danger btn-small icon-remove open-sans",
				    		    "callback": function() {
				    		    }
				    		}]		
						);
				}else{
					agreementNbrDiv.val(response.agreementCode);
					if(agreementEffectiveDiv){
						agreementEffectiveDiv.val(new Date(response.fromDate));
					}
					if(agreementSignDateDiv){
						agreementSignDateDiv.val(new Date(response.agreementSignDate));
					}
					if(agreementDurationDiv){
						agreementDurationDiv.val(response.agreementDuration);
					}
				}
			}else{
				bootbox.dialog(response.errorMessage,
						[{
			    		    "label" : uiLabelMap.CommonClose,
			    		    "class" : "btn-danger btn-small icon-remove open-sans",
			    		    "callback": function() {
			    		    }
			    		}]		
					);
			}
		},
		complete: function(jqXHR, textStatus){
			agreementNbrDiv.jqxInput({disabled: false});
			agreementEffectiveDiv.jqxDateTimeInput({disabled: false});
			agreementSignDateDiv.jqxDateTimeInput({disabled: false});
			if(agreementDurationDiv){
				agreementDurationDiv.jqxDropDownList({disabled: false});
			}
			btnEle.removeAttr("disabled");
		}
	});
}

function disabledJqxGrid(gridEle){
	gridEle.jqxGrid({ disabled: true});
	gridEle.jqxGrid('showloadelement');
}
function enableJqxGrid(gridEle){
	gridEle.jqxGrid({ disabled: false});
	gridEle.jqxGrid('hideloadelement');
}

function resizeHeightJqxWindow(jqxWindowDiv, heigthIncrement){
	var oldHeight = jqxWindowDiv.jqxWindow('height');
	var maxHeight = jqxWindowDiv.jqxWindow('maxHeight');
	var newHeight = oldHeight +  heigthIncrement;
	if(newHeight > maxHeight){
		newHeight = maxHeight;
	}
	jqxWindowDiv.jqxWindow({height: newHeight});
}

function formatNumber(number, fixed, thousandsseparator){
	if(typeof(fixed) == 'undefined'){
		fixed = 2;
	}
	if(typeof(thousandsseparator) == 'undefined'){
		thousandsseparator = ",";
	}
    var number = number.toFixed(fixed) + '';
    var x = number.split('.');
    var x1 = x[0];
    var x2 = x.length > 1 ? '.' + x[1] : '';
    var rgx = /(\d+)(\d{3})/;
    while (rgx.test(x1)) {
        x1 = x1.replace(rgx, '$1' + thousandsseparator + '$2');
    }
    return x1 + x2;
}

function refreshBeforeReloadGrid(gridEle){
	var tmpS = gridEle.jqxGrid('source');
	tmpS.pagenum = 0;
	tmpS._source.url = '';
	gridEle.jqxGrid('gotopage', 0);
	gridEle.jqxGrid('source', tmpS);
}

function isContainSpecialCharAndNumb(value){
	var specialCharacter = "<>@!#$%^&*()_+[]{}?:;|'\"\\,./~`-=1234567890";
	if(typeof(value) == "undefined"){
		return false;
	}
	for(var i=0; i < specialCharacter.length; i++){
		 if(value.indexOf(specialCharacter[i]) > -1){
			 return true;
		 }
	 }
	return false;
}

function isContainSpecialChar(value){
	var specialCharacter = "<>@!#$%^&*()_+[]{}?:;|'\"\\,./~`-=";
	if(typeof(value) == "undefined"){
		return false;
	}
	for(var i=0; i < specialCharacter.length; i++){
		 if(value.indexOf(specialCharacter[i]) > -1){
			 return true;
		 }
	 }
	return false;
}

function checkRegex(value,regexUiLabel){
    if(OlbCore.isNotEmpty(regexUiLabel) && OlbCore.isNotEmpty(value)){
        var regexCheck = new RegExp(regexUiLabel);
        if(regexCheck.test(value)){
            return true;
        }
    }
    return false;
}

function displayTooltipOnRowDataTale(typeTooltip, gridId, rowIndex, columnIndex, contentMsg){
	var rowId = "#row" + rowIndex + gridId + " td[role='gridcell']";
	var rowObject = $('#' + gridId).find(rowId);
	if(rowObject) {
		if ("ERROR" == typeTooltip){
			$(rowObject[columnIndex]).jqxTooltip({theme: 'tooltip-validation', content: contentMsg, position: 'bottom', autoHide: false, theme: 'olbius'});
			// setTimeout(function(){$(rowObj[columnIndex]).jqxTooltip("open");}, 100);
		} else if ("INFO" == typeTooltip){
			$(rowObject[columnIndex]).jqxTooltip({content: contentMsg, position: 'mouse', name: 'movieTooltip', theme: 'olbius'});
		}
	}
}
function displayTooltipOnRowGrid(typeTooltip, gridId, rowIndex, columnIndex, contentMsg){
	var rowId = "#row" + rowIndex + gridId + " div[role='gridcell']";
	var rowObject = $('#' + gridId).find(rowId);
	if(rowObject) {
		if ("ERROR" == typeTooltip){
			$(rowObject[columnIndex]).jqxTooltip({theme: 'tooltip-validation', content: contentMsg, position: 'bottom', autoHide: false, theme: 'olbius'});
			// setTimeout(function(){$(rowObj[columnIndex]).jqxTooltip("open");}, 100);
		} else if ("INFO" == typeTooltip){
			$(rowObject[columnIndex]).jqxTooltip({content: contentMsg, position: 'mouse', name: 'movieTooltip', theme: 'olbius'});
		}
	}
}

function createDropDownGridApprover(dropDownEle, gridEle, uiLabelMap, config){
	if(typeof(config) == "undefined"){
		config = {};
	}
	var dropDownWidth = typeof(config.dropDownWidth) != "undefined"? config.dropDownWidth : '97%';
	var dropDownHeight = typeof(config.dropDownHeight) != "undefined"? config.dropDownHeight : 25;
	var datafield = [{name: 'partyId', type: 'string'},
	                 {name: 'partyCode', type: 'string'},
	                 {name: 'partyName', type: 'string'},
	                 {name: 'emplPositionType', type: 'string'}];
	var columns = [{text: uiLabelMap.EmployeeId, datafield : 'partyCode', width : '23%', editable: false},
	               {text: uiLabelMap.EmployeeName, datafield: 'partyName', width: '30%', editable: false},
	               {text: uiLabelMap.HrCommonPosition, datafield: 'emplPositionType', width: '47%', editable: false}];
	var gridHeight = typeof(config.gridHeight) != "undefined"? config.gridHeight: 500;
	var configGrid = {
	   		width: gridHeight, 
	   		rowsheight: 25,
	   		autoheight: true,
	   		virtualmode: true,
	   		showfilterrow: false,
	   		selectionmode: 'singlerow',
	   		pageable: true,
	   		sortable: false,
	        filterable: false,
	        editable: false,
	        url: 'JQGetManagerOfEmpl',    
   			showtoolbar: false,
        	source: {pagesize: 5, id: 'partyId'}
	 };
	dropDownEle.jqxDropDownButton({width: dropDownWidth, height: dropDownHeight});
	Grid.initGrid(configGrid, datafield, columns, null, gridEle);
	gridEle.on('rowselect', function (event) {
        var args = event.args;
        var row = gridEle.jqxGrid('getrowdata', args.rowindex);
        var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['partyName'] + '</div>';
        dropDownEle.jqxDropDownButton('setContent', dropDownContent);
        dropDownEle.jqxDropDownButton('close');
    });
}

