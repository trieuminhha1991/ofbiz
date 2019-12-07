$(function(window, OLBIUS) {
	OLBIUS.olbius().popup.addType('dropDownMultil',
			{
				val : function(object, cur) {
					var item = object.object.element
							.jqxDropDownList('getCheckedItems');
					var valueData = [];
					for ( var i in item) {
						valueData.push(item[i].value);
					}
					cur[object.object.id] = {
						index : item.index,
						hide : object.hide
					};
					return valueData;
				},
				clear : function(object, cur) {
					object.object.element.jqxDropDownList('selectIndex',
							cur[object.object.id].index);
				}
			});

	function appendDropDownListMultil(id, label, object) {
		var text = '<div id="?" class="span6" style="float:left; margin-bottom: 10px;"><div id="?" style="margin-top: 5px;float: left;" class="align-right">'
				+ label + '</div><div id="?" style="float: right"></div></div>';
		var element = object.jqxElement();
		var _id = object.id();
		OLBIUS.appendHtmlUUID(element, text, _id, [ id.concat('-span'),
				id.concat('-text'), id ]);
		return object.returnObject(id, label, 'dropDownMultil');
	}

	OLBIUS.olbius().popup.addConfig('addDropDownListMultil',
			function(config) {

				var tmp = appendDropDownListMultil(config['id'],
						config['label'], this);
				$(tmp.element).jqxDropDownList({
					source : config['data'],
					selectedIndex : config['index'],
					theme : OLBIUS.getTheme(),
					width : config['width'],
					height : config['height'],
					displayMember : 'text',
					valueMember : 'value',
					placeHolder : config['placeHolder']
				});
				tmp.span.css('min-height', config['height'] + 'px');

				$(tmp.element).jqxDropDownList({
					checkboxes : true
				});

				var $id = $(tmp.element);
				$id.on('checkChange', function(event) {
					var value = event.args.value;
					var checked = event.args.checked;
					if (value == null) {
						if (checked == true) {
							$id.jqxDropDownList('checkAll');
						} else {
							$id.jqxDropDownList('uncheckAll');
						}
					}
				});
				return tmp;
			},

			function(config) {
				return {
					index : config.index,
					hide : config.hide
				};
			});

}(window, OLBIUS));
