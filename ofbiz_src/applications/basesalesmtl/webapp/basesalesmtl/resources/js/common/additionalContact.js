if (typeof (AdditionalContact) == "undefined") {
	var AdditionalContact = (function() {
		var mainGrid;
		var initGrid = function(data) {
			var source =
            {
                datatype: "json",
                datafields: [
                    { name: "contactMechId", type: "string" },
                    { name: "name", type: "string" },
                    { name: "phone", type: "string" }
                ],
                id: "contactMechId",
                localdata: data
            };
            var dataAdapter = new $.jqx.dataAdapter(source);
            mainGrid.jqxGrid({
            	localization: getLocalization(),
		        width: "100%",
		        theme: "olbius",
		        pageable: true,
		        pagesize: 10,
		        editable: updatable,
		        autoheight: true,
		        showfilterrow: true,
                filterable: true,
		        source: dataAdapter,
                columns: [
                  { text: multiLang.FullName, datafield: "name", width: 350,
						validation: function (cell, value) {
							if (value) {
								return true;
							}
							return {result: false, message: multiLang.fieldRequired};
						}
                  },
                  { text: multiLang.PhoneNumber, datafield: "phone",
                	  validation: function (cell, value) {
							if (value) {
								return true;
							}
							return {result: false, message: multiLang.fieldRequired};
                	  }
                  }],
                handlekeyboardnavigation: function (event) {
  	                var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
  	                if (key == 70 && event.ctrlKey) {
  	                	mainGrid.jqxGrid("clearfilters");
  						return true;
  	                }
  				},
  				showtoolbar: true,
  			    rendertoolbar: rendertoolbar
            });
		};
		var rendertoolbar = function(toolbar) {
			var container = $("<div style='margin: 17px 4px 0px 0px;' class='pull-right'></div>");
	        var aTag = $("<a style='cursor: pointer;'><i class='fa-plus open-sans'></i>" + multiLang.CommonAddNew + "</a>");
	        var titleProperty = $("<h4 style='color: #4383b4;'>" + multiLang.AdditionalContact + "</h4>");
	        toolbar.append(container);
	        toolbar.append(titleProperty);
	        if (updatable) {
	        	container.append(aTag);
		        aTag.click(function() {
		        	AdditionalContact.addBlankRow();
				});
			}
		};
		var addBlankRow = function() {
			mainGrid.jqxGrid("addrow", null, {}, "first");
			mainGrid.jqxGrid("begincelledit", 0, "name");
		};
		var handleEvents = function() {
			mainGrid.on("cellendedit", function (event) {
			    var args = event.args;
			    var dataField = event.args.datafield;
			    var rowBoundIndex = event.args.rowindex;
			    var value = args.value;
			    var oldvalue = args.oldvalue;
			    var rowData = args.row;
			    if (value) {
					setTimeout(function() {
						switch (dataField) {
						case "name":
							if (!rowData.phone) {
								mainGrid.jqxGrid("begincelledit", 0, "phone");
							}
							break;
						case "phone":
							if (!rowData.name) {
								mainGrid.jqxGrid("begincelledit", 0, "name");
							}
							break;
						default:
							break;
						}
					}, 200);
				}
			});
		};
		var getValue = function() {
			return {contactPerson: JSON.stringify(mainGrid.jqxGrid("getboundrows"))};
		};
		return {
			init: function() {
				mainGrid = $("#jqxgridContact");
				handleEvents();
			},
			initGrid: initGrid,
			addBlankRow: addBlankRow,
			getValue: getValue
		}
	})();
}