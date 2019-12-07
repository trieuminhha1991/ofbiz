
/*$(document).ready(function() {
	$('#nav a').each(function (i) { $(this).attr('tabindex', -1); });
});
*/
var actionEnum = {
		create: "create",
		update: "update",
		copy: "copy"
	};

var commonObject = (function(){
	var showNotification = function(message, template){
		$('#jqxNotificationNested').jqxNotification('closeLast');
		$("#jqxNotificationNested").jqxNotification({ template: template});
    	$("#notificationContentNested").html(message);
		$("#jqxNotificationNested").jqxNotification('open');
	};
	var cellCanEdit = function (row, columnfield, value) {
		return 'yellow';
	};
	var renderFilterInGrid = function(source, wgfilterselectallstring, widget, displayMember, valueMember, functionRenderer){
		var localSource =
	    {
	        localdata: source,
	        datatype: "array"
	    };
		var filterBoxAdapter = new $.jqx.dataAdapter(localSource,
        {
            autoBind: true
        });
        var uniqueRecords = filterBoxAdapter.records;
			uniqueRecords.splice(0, 0, wgfilterselectallstring);
			
		widget.jqxDropDownList({selectedIndex: 0,  source: uniqueRecords, displayMember: displayMember, valueMember: valueMember, autoDropDownHeight:false, dropDownHeight:200,
			renderer: function (index, label, value) {
			return functionRenderer(value);
		}});
	};
	var changeDisplayWrapActions = function(action){
		if(action === actionEnum.create){
			$("#createAction").css("display", "block");
			$("#updateAction").css("display", "none");
			$("#copyAction").css("display", "none");
		}else if(action === actionEnum.update){
			$("#createAction").css("display", "none");
			$("#updateAction").css("display", "block");
			$("#copyAction").css("display", "none");
		}else if(action === actionEnum.copy){
			$("#createAction").css("display", "none");
			$("#updateAction").css("display", "none");
			$("#copyAction").css("display", "block");
		}
	};
	var getServerError = function (data) {
	    var serverErrorHash = [];
	    var serverError = "";
	    if (data._ERROR_MESSAGE_LIST_ != undefined) {
	        serverErrorHash = data._ERROR_MESSAGE_LIST_;
	        $.each(serverErrorHash, function(i, error) {
	          if (error != undefined) {
	              if (error.message != undefined) {
	                  serverError += error.message;
	              } else {
	                  serverError += error;
	              }
	            }
	        });
	    }
	    if (data._ERROR_MESSAGE_ != undefined) {
	        serverError = data._ERROR_MESSAGE_;
	    }
	    return serverError;
	};
	var formatcurrency = function(num, uom){
		decimalseparator = ",";
	 	thousandsseparator = ".";
	 	currencysymbol = "đ";
	 	if(typeof(uom) == "undefined" || uom == null){
	 		uom = "${currencyUomId?if_exists}";
	 	}
		if(uom == "USD"){
			currencysymbol = "$";
			decimalseparator = ".";
	 		thousandsseparator = ",";
		}else if(uom == "EUR"){
			currencysymbol = "€";
			decimalseparator = ".";
	 		thousandsseparator = ",";
		}
		if (num < 0){
			numT = num*(-1);
		} else {
			numT = num;
		}
	    var str = numT.toString().replace(currencysymbol, ""), parts = false, output = [], i = 1, formatted = null;
	    if(str.indexOf(".") > 0) {
	        parts = str.split(".");
	        str = parts[0];
	    }
	    str = str.split("").reverse();
	    for(var j = 0, len = str.length; j < len; j++) {
	        if(str[j] != ",") {
	            output.push(str[j]);
	            if(i%3 == 0 && j < (len - 1)) {
	                output.push(thousandsseparator);
	            }
	            i++;
	        }
	    }
	    if (num < 0) output.push('-');
	    formatted = output.reverse().join("");
	    return(formatted + ((parts) ? decimalseparator + parts[1].substr(0, 2) : "") + "&nbsp;" + currencysymbol);
	};
	function formatFullDate(value) {
		if (value != undefined && value != null && !(/^\s*$/.test(value))) {
			var dateStr = "";
			dateStr += addZero(value.getFullYear()) + '-';
			dateStr += addZero(value.getMonth()+1) + '-';
			dateStr += addZero(value.getDate()) + ' ';
			dateStr += addZero(value.getHours()) + ':';
			dateStr += addZero(value.getMinutes()) + ':';
			dateStr += addZero(value.getSeconds());
			return dateStr;
		} else {
			return "";
		}
	};
	function addZero(i) {
	    if (i < 10) {i = "0" + i;}
	    return i;
	};
	var setTitleGrid = function(idGrid, title){
		/*var title = $("#toolbarcontainer .widget-header").find('h4');*/
		var toolbar = $("#toolbarcontainer"+idGrid).find("h4");
		toolbar.text(title);
	};
	
	
	return {
		formatcurrency: formatcurrency,
		getServerError: getServerError,
		changeDisplayWrapActions: changeDisplayWrapActions,
		formatFullDate: formatFullDate,
		cellCanEdit: cellCanEdit,
		renderFilterInGrid: renderFilterInGrid,
		showNotification: showNotification,
		setTitleGrid: setTitleGrid
	}
	
}());

