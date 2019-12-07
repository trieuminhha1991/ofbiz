/*
 jQWidgets v4.0.0 (2016-Jan)
 Copyright (c) 2011-2016 jQWidgets.
 License: http://jqwidgets.com/license/
 */

(function(a) {
	a.extend(a.jqx._jqxGrid.prototype, {
		_geteditorvalue : function(h) {
			var o = new String();
			if (!this.editcell) {
				return null
			}
			var l = this.editcell.editor;
			if (this.editmode == "selectedrow") {
				if (this.editcell[h.datafield]) {
					var l = this.editcell[h.datafield].editor
				}
			}
			if (l) {
				switch(h.columntype) {
				case"textbox":
				default:
					o = l.val();
					if (h.cellsformat != "") {
						var n = "string";
						var e = this.source.datafields || ((this.source._source) ? this.source._source.datafields : null);
						if (e) {
							var p = "";
							a.each(e, function() {
								if (this.name == h.displayfield) {
									if (this.type) {
										p = this.type
									}
									return false
								}
							});
							if (p) {
								n = p
							}
						}
						var i = n === "number" || n === "float" || n === "int" || n === "integer";
						var f = n === "date" || n === "time";
						if (i || (n === "string" && (h.cellsformat.indexOf("p") != -1 || h.cellsformat.indexOf("c") != -1 || h.cellsformat.indexOf("n") != -1 || h.cellsformat.indexOf("f") != -1))) {
							if (o === "" && h.nullable) {
								return ""
							}
							if (o.indexOf(this.gridlocalization.currencysymbol) > -1) {
								o = o.replace(this.gridlocalization.currencysymbol, "")
							}
							var m = function(w, u, v) {
								var s = w;
								if (u == v) {
									return w
								}
								var t = s.indexOf(u);
								while (t != -1) {
									s = s.replace(u, v);
									t = s.indexOf(u)
								}
								return s
							};
							var k = o;
							k = new Number(k);
							if (!isNaN(k)) {
								return k
							}
							o = m(o, this.gridlocalization.thousandsseparator, "");
							o = o.replace(this.gridlocalization.decimalseparator, ".");
							if (o.indexOf(this.gridlocalization.percentsymbol) > -1) {
								o = o.replace(this.gridlocalization.percentsymbol, "")
							}
							var d = "";
							for (var r = 0; r < o.length; r++) {
								var b = o.substring(r, r + 1);
								if (b === "-") {
									d += "-"
								}
								if (b === ".") {
									d += "."
								}
								if (b.match(/^[0-9]+$/) != null) {
									d += b
								}
							}
							o = d;
							o = o.replace(/ /g, "");
							o = new Number(o);
							if (isNaN(o)) {
								o = ""
							}
						}
						if (f || (n === "string" && (h.cellsformat.indexOf("H") != -1 || h.cellsformat.indexOf("m") != -1 || h.cellsformat.indexOf("M") != -1 || h.cellsformat.indexOf("y") != -1 || h.cellsformat.indexOf("h") != -1 || h.cellsformat.indexOf("d") != -1))) {
							if (o === "" && h.nullable) {
								return ""
							}
							var c = o;
							if (a.jqx.dataFormat) {
								o = a.jqx.dataFormat.tryparsedate(c, this.gridlocalization)
							}
							if (o == "Invalid Date" || o == null) {
								o = ""
							}
						}
					}
					if (h.displayfield != h.datafield) {
						o = {
							label : o,
							value : o
						}
					}
					break;
				case"checkbox":
					if (l.jqxCheckBox) {
						o = l.jqxCheckBox("checked")
					}
					break;
				case"datetimeinput":
					if (l.jqxDateTimeInput) {
						l.jqxDateTimeInput({
							isEditing : false
						});
						l.jqxDateTimeInput("_validateValue");
						o = l.jqxDateTimeInput("getDate");
						if (o == null) {
							return null
						}
						o = new Date(o.toString());
						if (h.displayfield != h.datafield) {
							o = {
								label : o,
								value : o
							}
						}
					}
					break;
				case"dropdownlist":
					if (l.jqxDropDownList) {
						var g = l.jqxDropDownList("selectedIndex");
						var q = l.jqxDropDownList("listBox").getVisibleItem(g);
						if (h.displayfield != h.datafield) {
							if (q) {
								o = {
									label : q.label,
									value : q.value
								}
							} else {
								o = ""
							}
						} else {
							if (q) {
								o = q.value
							} else {
								o = ""
							}
						}
						if (o == null) {
							o = ""
						}
					}
					break;
				case"combobox":
					if (l.jqxComboBox) {
						o = l.jqxComboBox("val");
						if (h.displayfield != h.datafield) {
							var q = l.jqxComboBox("getSelectedItem");
							if (q != null) {
								o = {
									label : q.label,
									value : q.value
								}
							}
						}
						if (o == null) {
							o = ""
						}
					}
					break;
				case"numberinput":
					if (l.jqxNumberInput) {
						if (this.touchdevice) {
							l.jqxNumberInput("_doTouchHandling")
						}
						var j = l.jqxNumberInput("getDecimal");
						o = new Number(j);
						o = parseFloat(o);
						if (isNaN(o)) {
							o = 0
						}
						if (h.displayfield != h.datafield) {
							o = {
								label : o,
								value : o
							}
						}
					}
					break
				}
				if (h.geteditorvalue) {
					if (this.editmode == "selectedrow") {
						o = h.geteditorvalue(this.editcell.row, this.getcellvalue(this.editcell.row, h.datafield), l)
					} else {
						o = h.geteditorvalue(this.editcell.row, this.editcell.value, l)
					}
				}
			}
			return o
		},
	});
})(jqxBaseFramework);