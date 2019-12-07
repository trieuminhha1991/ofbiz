$(document).ready(function() {
	Header.init();
});
	var currentElements;
	if (typeof (Header) == "undefined") {
		var CreateMode = true;
		var Header = (function() {
			var render = function() {
				var data = header.toJson()['0'];
				for ( var x in data) {

					var li = $("<li></li>");
					li.data('label', data[x].label);
					li.data('class', data[x].class);
					li.data('link', data[x].link);

					var a = $("<a></a>");
					a.text(data[x].label);
					li.append(a);

					if (data[x].children) {
						var ol = $("<ol class=''></ol>");
						for ( var c in data[x].children) {
							for ( var v in data[x].children[c]) {
								var liItem = $("<li></li>");
								liItem.data('label', data[x].children[c][v].label);
								liItem.data('class', data[x].children[c][v].class);
								liItem.data('link', data[x].children[c][v].link);
								var aItem = $("<a></a>");
								aItem.text(data[x].children[c][v].label);
								liItem.append(aItem);
								ol.append(liItem);
								liItem.dblclick(function(event) {
									event.stopPropagation();
									bindEvents($(this));
								});
							}
						}
						li.append(ol);
					}
					$("#olMenu").append(li);

					li.dblclick(function() {
						bindEvents($(this));
					});
				}
			};
			var initSortable = function() {
				$("ol.nested_with_switch").sortable();

				$("#includeChildMenu").jqxCheckBox({width: 10});

				$("#jqxNotificationNested").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
			};
			var handleEvents = function() {

				$("#btnDeleteMenu").click(function() {
					if (currentElements) {
						bootbox.confirm(multiLang.BsConfirmDeleteMenu, multiLang.CommonCancel, multiLang.CommonSubmit, function(result) {
							if (result) {
								currentElements.remove();
								clean();
							}
						});
					} else {
						bootbox.alert(multiLang.BsChooseMenuFirst);
					}
				});
				$("#btnSaveMenu").click(function() {
					if ($('#editBox').jqxValidator('validate')) {
						var includeChildMenu = $("#includeChildMenu").jqxCheckBox('val');
						if (CreateMode) {
							var li = $("<li></li>");
							li.data('label', $("#txtText").val());
							li.data('class', $("#txtClassIcon").val());
							li.data('link', $("#txtLink").val());

							var a = $("<a></a>");
							a.text($("#txtText").val());
							li.append(a);
							if (includeChildMenu) {
								var ol = $("<ol class=''></ol>");
								li.append(ol);
							}
							$("#olMenu").append(li);

							li.dblclick(function() {
								bindEvents($(this));
							});
						} else {
							var li = currentElements;
							if (li) {
								li.data('label', $("#txtText").val());
								li.data('class', $("#txtClassIcon").val());
								li.data('link', $("#txtLink").val());
								var a = li.find('a').first();
								a.text($("#txtText").val());
								if (includeChildMenu) {
									var ol = $("<ol class=''></ol>");
									li.append(ol);
								} else {
									var ol = li.find('ol');
									ol.remove();
								}
							}
						}
						clean();
					}
				});
				$("#btnSave").click(function() {
					var longDescription = JSON.stringify($("ol.nested_with_switch").sortable("serialize"));
					if (longDescription) {
						DataAccess.execute({
								url: "updateHeader",
								data: { longDescription: longDescription.replaceAll('"', "'"),
										contentId: contentId}
									}, Header.notify);
					}
				});
			};
			var validator = function() {
				$('#editBox').jqxValidator({
				    rules: [{input: '#txtText', message: multiLang.DmsFieldRequired, action: 'keyup, blur', rule: 'required'},
				            {input: '#txtLink', message: multiLang.DmsFieldRequired, action: 'keyup, blur', rule: 'required'}]
				});
			};
			var clean = function() {
				CreateMode = true;
				$("#txtText").val("");
				$("#txtClassIcon").val("");
				$("#txtLink").val("#");
				$("#includeChildMenu").jqxCheckBox('val', false);
				$("#btnSaveMenu").html("<i class='icon-ok'></i>" + multiLang.BSCreateMenu);
				currentElements = null;
			};
			var bindEvents = function(element) {
				Header.clean();
				currentElements = element;
				$("#btnSaveMenu").html("<i class='icon-ok'></i>" + multiLang.BSUpdateMenu);
				CreateMode = false;
				currentElements = element;
				$("#txtText").val(currentElements.data('label'));
				$("#txtClassIcon").val(currentElements.data('class'));
				$("#txtLink").val(currentElements.data('link'));
				if (currentElements.find('ol').length > 0) {
					$("#includeChildMenu").jqxCheckBox('val', true);
				}
			};
			var notify = function(res) {
				$('#jqxNotificationNested').jqxNotification('closeLast');
				if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
					$("#jqxNotificationNested").jqxNotification({ template: 'error'});
				$("#notificationContentNested").text(multiLang.updateError);
				$("#jqxNotificationNested").jqxNotification("open");
				}else {
					$("#jqxNotificationNested").jqxNotification({ template: 'info'});
				$("#notificationContentNested").text(multiLang.updateSuccess);
				$("#jqxNotificationNested").jqxNotification("open");
				}
			};
			return {
				init: function() {
					render();
					initSortable();
					handleEvents();
					validator();
				},
				clean: clean,
				notify: notify
			};
		})();
	}