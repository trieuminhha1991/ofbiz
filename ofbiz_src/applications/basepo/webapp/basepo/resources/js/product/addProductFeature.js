if (typeof (Feature) == "undefined") {
	var Feature = (function() {
		var initJqxElements = function() {
			for ( var x in productFeatureTypes) {
				var source =
				{
					datatype: "json",
					datafields: [{ name: "productFeatureId" },
					             { name: "description" }],
		            url: "getProductFeature?productFeatureTypeId=" + productFeatureTypes[x],
		            async: false
	            };
				var dataAdapter = new $.jqx.dataAdapter(source);
				$("#txt" + productFeatureTypes[x]).jqxComboBox({ theme: "olbius", source: dataAdapter, width: 218, height: 30, displayMember: "description",
					valueMember: "productFeatureId", dropDownHeight: 150});
			}
		};
		var handleEvents = function() {
			$(".add-feature").click(function() {
				QuickAddFeature.open($(this).data("featuretype"), $("#divVariantFromProduct").jqxDropDownList("val"));
			});
		};
		var activate = function() {
			$(".feature").removeClass("hidden");
			Feature.refresh();
			if (!updateMode) {
				Feature.clearSelection();
			}
		};
		var deactivate = function() {
			$(".feature").removeClass("hidden");
			$(".feature").addClass("hidden");
		};
		var refresh = function() {
			for ( var x in productFeatureTypes) {
				$("#txt" + productFeatureTypes[x]).jqxComboBox("refresh");
			}
		};
		var clearSelection = function() {
			for ( var x in productFeatureTypes) {
				$("#txt" + productFeatureTypes[x]).jqxComboBox("clearSelection");
			}
		};
		var multiSelect = function(allow) {
			for ( var x in productFeatureTypes) {
				$("#txt" + productFeatureTypes[x]).jqxComboBox({ multiSelect: allow });
			}
		};
		var reloadSource = function(productId) {
			if (productId) {
				for ( var x in productFeatureTypes) {
					var source =
					{
						datatype: "json",
						datafields: [{ name: "productFeatureId" },
						             { name: "description" }],
			            url: "getProductFeature?productFeatureTypeId=" + productFeatureTypes[x] + "&productId=" + productId,
			            async: false
		            };
					var dataAdapter = new $.jqx.dataAdapter(source);
					$("#txt" + productFeatureTypes[x]).jqxComboBox({ source: dataAdapter });
				}
			}
		};
		var setValue = function(data) {
			if (data.feature) {
				setTimeout(function() {
					for ( var x in productFeatureTypes) {
						var thisFeature = data.feature[productFeatureTypes[x]];
						for ( var z in thisFeature) {
							$("#txt" + productFeatureTypes[x]).jqxComboBox("selectItem", thisFeature[z]);
						}
					}
				}, 100);
			}
		};
		var getValue = function() {
			var value = new Object();
			for ( var x in productFeatureTypes) {
				var input = $("#txt" + productFeatureTypes[x]);
				if (input.jqxComboBox("multiSelect")) {
					var items = input.jqxComboBox("getSelectedItems");
					var values = new Array();
					for ( var i in items) {
						values.push(items[i].value);
					}
					value[productFeatureTypes[x]] = values;
				} else {
					value[productFeatureTypes[x]] = new Array(input.jqxComboBox("getSelectedItem")?input.jqxComboBox("getSelectedItem").value:null);
				}
			}
			return value;
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
				QuickAddFeature.init();
			},
			activate: activate,
			deactivate: deactivate,
			refresh: refresh,
			clearSelection: clearSelection,
			multiSelect: multiSelect,
			reloadSource: reloadSource,
			setValue: setValue,
			getValue: getValue
		}
	})();
}
if (typeof (QuickAddFeature) == "undefined") {
	var QuickAddFeature = (function() {
		var jqxwindow;
		var initJqxElements = function() {
			jqxwindow.jqxWindow({
			    width: 550, maxWidth: 1000, theme: "olbius", minHeight: 120, height: 220, resizable: false,
			    isModal: true, autoOpen: false, cancelButton: $("#alterCancelProductFeature"), modalOpacity: 0.7
			});
			$("#txtAddProductFeature").jqxComboBox({source: [], displayMember: "description", valueMember: "productFeatureId", multiSelect: true, width: 218, height: 30});
			
			$("#jqxNotificationAddFeature").jqxNotification({ opacity: 0.9, autoClose: true, template: "info" });
		};
		var handleEvents = function() {
			$("#alterSaveProductFeature").click(function() {
				var featureSelected = LocalUtil.getValueSelectedJqxComboBox($("#txtAddProductFeature"));
				DataAccess.execute({
				    	url: "applyFeatureToProductCustom",
				    	data: {productId: jqxwindow.data("productId"), productFeatureApplTypeId: "SELECTABLE_FEATURE",
				    			arrayProductFeatureId: featureSelected}
					},
					QuickAddFeature.notify);
				if (jqxwindow.data("FeatureType")) {
					var source =
					{
						datatype: "json",
						datafields: [{ name: "productFeatureId" },
						             { name: "description" }],
			            url: "getProductFeature?productFeatureTypeId=" + jqxwindow.data("FeatureType") + "&productId=" + jqxwindow.data("productId"),
			            async: false
		            };
					var dataAdapter = new $.jqx.dataAdapter(source);
					$("#txt" + jqxwindow.data("FeatureType")).jqxComboBox({ source: dataAdapter });
				}
				jqxwindow.jqxWindow('close');
			});
		};
		var setValue = function(FeatureType, productId) {
			if (FeatureType && productId) {
				var source =
				{
					datatype: "json",
					datafields: [{ name: "productFeatureId" },
					             { name: "description" }],
		            url: "getProductFeature?productFeatureTypeId=" + FeatureType,
		            async: false
	            };
				var dataAdapter = new $.jqx.dataAdapter(source);
				$("#txtAddProductFeature").jqxComboBox({ source: dataAdapter });
				
				var data = DataAccess.getData({
					url: "getProductFeature",
					data: {productFeatureTypeId: FeatureType, productId: productId},
					source: "productFeatures"});
				if (data) {
					for ( var x in data) {
						$("#txtAddProductFeature").jqxComboBox('selectItem', data[x].productFeatureId);
					}
				}
			}
		};
		var open = function(FeatureType, productId) {
			jqxwindow.data("productId", productId);
			jqxwindow.data("FeatureType", FeatureType);
			QuickAddFeature.setValue(FeatureType, productId);
			var wtmp = window;
			var tmpwidth = jqxwindow.jqxWindow("width");
			jqxwindow.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
			jqxwindow.jqxWindow("open");
			$("#txtAddProductFeature").jqxComboBox("refresh");
		};
		var notify = function(res) {
			$('#jqxNotificationAddFeature').jqxNotification('closeLast');
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				$("#jqxNotificationAddFeature").jqxNotification({ template: 'error'});
				$("#notificationContentAddFeature").text(multiLang.updateError);
				$("#jqxNotificationAddFeature").jqxNotification("open");
			}else {
				$("#jqxNotificationAddFeature").jqxNotification({ template: 'info'});
				$("#notificationContentAddFeature").text(multiLang.updateSuccess);
				$("#jqxNotificationAddFeature").jqxNotification("open");
			}
		};
		return {
			init: function() {
				jqxwindow = $("#jqxwindowAddFeature");
				initJqxElements();
				handleEvents();
			},
			setValue: setValue,
			open: open,
			notify: notify
		}
	})();
}