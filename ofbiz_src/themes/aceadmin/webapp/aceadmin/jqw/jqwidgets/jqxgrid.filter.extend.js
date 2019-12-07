/*
 jQWidgets v4.0.0 (2016-Jan)
 Copyright (c) 2011-2016 jQWidgets.
 License: http://jqwidgets.com/license/
 */

(function(a) {
	a.extend(a.jqx._jqxGrid.prototype, {
		_updatefilterrow : function() {
			var b = a('<div style="position: relative;" id="row00' + this.element.id + '"></div>');
			var f = 0;
			var o = this.columns.records.length;
			var m = this.toThemeProperty("jqx-grid-cell");
			m += " " + this.toThemeProperty("jqx-grid-cell-pinned");
			m += " " + this.toThemeProperty("jqx-grid-cell-filter-row");
			var r = o + 10;
			var s = new Array();
			var n = this.that;
			this.filterrow[0].cells = s;
			b.height(this.filterrowheight);
			this.filterrow.children().detach();
			this.filterrow.append(b);
			if (!this._filterrowcache) {
				this._filterrowcache = new Array()
			}
			this._initcolumntypes();
			var g = false;
			var d = new Array();
			var q = document.createDocumentFragment();
			for (var h = 0; h < o; h++) {
				var e = this.columns.records[h];
				var key = this.element.id + e.datafield + 'filterwidget';
				var c = e.width;
				if (c < e.minwidth) {
					c = e.minwidth
				}
				if (c > e.maxwidth) {
					c = e.maxwidth
				}
				var l = document.createElement("div");
				l.style.overflow = "hidden";
				l.style.position = "absolute";
				l.style.height = "100%";
				l.className = m;
				l = a(l);
				l.attr('id', key);
				q.appendChild(l[0]);
				l[0].style.left = f + "px";
				if (this.rtl) {
					l.css("z-index", r++);
					l.css("border-left-width", "1px")
				} else {
					l.css("z-index", r--)
				}
				if (c == "auto") {
					c = 0
				}
				l[0].style.width = parseFloat(c) + "px";
				l[0].left = f;
				if (!(e.hidden && e.hideable)) {
					f += c
				} else {
					l.css("display", "none")
				}
				s[s.length] = l[0];
				var k = true;
				if (!this.rtl) {
					if (this.groupable) {
						var p = (this.showrowdetailscolumn && this.rowdetails) ? 1 : 0;
						if (this.groups.length + p > h) {
							k = false
						}
					}
					if (this.showrowdetailscolumn && this.rowdetails && h == 0) {
						k = false
					}
				} else {
					if (this.groupable) {
						var p = (this.showrowdetailscolumn && this.rowdetails) ? 1 : 0;
						if (this.groups.length + p + h > o - 1) {
							k = false
						}
					}
					if (this.showrowdetailscolumn && this.rowdetails && h == o - 1) {
						k = false
					}
				}
				if (k) {
					if (e.filtertype == "custom" && e.createfilterwidget) {
						var i = function() {
							n._applyfilterfromfilterrow()
						};
						e.createfilterwidget(e, l, i)
					} else {
						if (e.filterable) {
							if (this._filterrowcache[e.datafield]) {
								g = true;
								l.append(this._filterrowcache[e.datafield]);
								e._filterwidget = this._filterrowcache[e.datafield]
							} else {
								this._addfilterwidget(e, l, c);
								d[e.datafield] = e._filterwidget
							}
						}
					}
				}
			}
			b[0].appendChild(q);
			this._filterrowcache = d;
			if (a.jqx.browser.msie && a.jqx.browser.version < 8) {
				b.css("z-index", r--)
			}
			b.width(parseFloat(f) + 2);
			this.filterrow.addClass(m);
			this.filterrow.css("border-top-width", "1px");
			this.filterrow.css("border-right-width", "0px");
			if (g) {
				this._updatefilterrowui(true)
			}
		},
		_addfilterwidget : function(C, d, A) {
			var H = this.that;
			var z = "";
			var E = "";
			var key = '';
			var field = C.datafield;
			if (field) {
				var key = this.element.id + field + 'filterwidget';
				d.attr('id', key);
			}
			for (var F = 0; F < H.dataview.filters.length; F++) {
				var x = H.dataview.filters[F];
				if (x.datafield && x.datafield == C.datafield) {
					var F = x.filter.getfilters()[0];
					z = F.value;
					E = F.condition;
					C.filtercondition = E;
					break
				}
			}
			var fixFocus = function(input, key){
				//olbius fix lost focus in virtual mode
				var par = input.parent();
				par.attr('id', key);
				sessionStorage.setItem('previousInputFilter', key);
			};
			var g = function(I, J) {
				var f = a('<input autocomplete="off" type="textarea"/>');
				f[0].id = a.jqx.utilities.createId();
				f.addClass(I.toThemeProperty("jqx-widget"));
				f.addClass(I.toThemeProperty("jqx-input"));
				f.addClass(I.toThemeProperty("jqx-rc-all"));
				f.addClass(I.toThemeProperty("jqx-widget-content"));
				if (I.rtl) {
					f.css("direction", "rtl")
				}
				if (I.disabled) {
					f.attr("disabled", true)
				}
				f.attr("disabled", false);
				f.appendTo(J);
				f.width(A - 10);
				f.height(I.filterrowheight - 10);
				f.css("margin", "4px");
				if (C.createfilterwidget) {
					C.createfilterwidget(C, J, f)
				}
				C._filterwidget = f;
				f.focus(function() {
					I.content[0].scrollLeft = 0;
					setTimeout(function() {
						I.content[0].scrollLeft = 0
					}, 10);
					I.focusedfilter = f;
					f.addClass(I.toThemeProperty("jqx-fill-state-focus"));
					return false
				});
				f.blur(function() {
					f.removeClass(I.toThemeProperty("jqx-fill-state-focus"))
				});
				f.keydown(function(K) {
					if (K.keyCode == "13") {
						I._applyfilterfromfilterrow()
					}
					if (f[0]._writeTimer) {
						clearTimeout(f[0]._writeTimer)
					}
					fixFocus(f, key);
					f[0]._writeTimer = setTimeout(function() {
						if (!I._loading) {
							if (!I["_oldWriteText" + f[0].id]) {
								I["_oldWriteText" + f[0].id] = ""
							}
							if (I["_oldWriteText" + f[0].id].length > 0 && I["_oldWriteText" + f[0].id] != f.val()) {
								I._applyfilterfromfilterrow();
								I["_oldWriteText" + f[0].id] = f.val()
							} else {
								if (I["_oldWriteText" + f[0].id].length == 0) {
									I._applyfilterfromfilterrow();
									I["_oldWriteText" + f[0].id] = f.val()
								}
							}
						}
					}, C.filterdelay);
					I.focusedfilter = f
				});
				I.host.removeClass("jqx-disableselect");
				I.content.removeClass("jqx-disableselect");
				f.val(z)
			};
			if (C.datatype != null) {
				if (C.filtertype == "number") {
					if (C.datatype == "string" || C.datatype == "date" || C.datatype == "bool") {
						C.filtertype = "textbox"
					}
				}
				if (C.filtertype == "date") {
					if (C.datatype == "string" || C.datatype == "number" || C.datatype == "bool") {
						C.filtertype = "textbox"
					}
				}
				if (C.filtertype == "bool") {
					if (C.datatype == "string" || C.datatype == "number" || C.datatype == "date") {
						C.filtertype = "textbox"
					}
				}
			}
			switch(C.filtertype) {
			case"number":
			case"input":
				var m = a("<div></div>");
				m.width(d.width());
				m.height(this.filterrowheight);
				d.append(m);
				var A = d.width() - 20;
				var s = function(J, K, f) {
					var I = a('<input style="float: left;" autocomplete="off" type="textarea"/>');
					if (H.rtl) {
						I.css("float", "right");
						I.css("direction", "rtl")
					}
					I[0].id = a.jqx.utilities.createId();
					I.addClass(H.toThemeProperty("jqx-widget jqx-input jqx-rc-all jqx-widget-content"));
					I.appendTo(J);
					I.width(K - 16);
					if (H.disabled) {
						I.attr("disabled", true)
					}
					I.attr("disabled", false);
					I.height(H.filterrowheight - 10);
					I.css("margin", "4px");
					I.css("margin-right", "2px");
					I.focus(function() {
						H.focusedfilter = I;
						I.addClass(H.toThemeProperty("jqx-fill-state-focus"))
					});
					I.blur(function() {
						I.removeClass(H.toThemeProperty("jqx-fill-state-focus"))
					});
					I.keydown(function(L) {
						//olbius - add number input to filter widget
						if (C.filtertype == "number") {
							var x = L.keyCode == 190 || L.keyCode == 189 || L.metaKey || L.which <= 0 || L.which == 8 || /[0-9]/.test(String.fromCharCode(L.which));
							if (!x) {
								L.preventDefault();
							}
						}
						if (L.keyCode == "13") {
							H._applyfilterfromfilterrow()
						}
						fixFocus(I, key);
						if (I[0]._writeTimer) {
							clearTimeout(I[0]._writeTimer)
						}
						I[0]._writeTimer = setTimeout(function() {
							if (!H._loading) {
								if (H["_oldWriteText" + I[0].id] != I.val()) {
									H._applyfilterfromfilterrow();
									H["_oldWriteText" + I[0].id] = I.val()
								}
							}
						}, C.filterdelay);
						H.focusedfilter = I
					});
					I.val(z);
					return I
				};
				s(m, A);
				var B = H._getfiltersbytype(C.filtertype == "number" ? "number" : "string");
				var t = a("<div class='filter' style='float: left;'></div>");
				t.css("margin-top", "4px");
				t.appendTo(m);
				if (H.rtl) {
					t.css("float", "right")
				}
				var h = 0;
				if (!C.previousConditionIndex) {
					if (C.filtercondition != null) {
						var E = new a.jqx.filter();
						var r = E.getoperatorsbyfiltertype(C.filtertype == "number" ? "numericfilter" : "stringfilter");
						var e = r.indexOf(C.filtercondition.toUpperCase());
						if (e != -1) {
							h = e
						}
					}
					var D = 170;
					if (C.filtertype == "input") {
						D = 240;
						if (h == 0) {
							var e = B.indexOf("contains");
							if (e != -1 && C.filtercondition == null) {
								h = e
							}
						}
					}
				} else {
					h = C.previousConditionIndex;
				}
				t.jqxDropDownList({
					disabled : H.disabled,
					touchMode : H.touchmode,
					rtl : H.rtl,
					dropDownHorizontalAlignment : "right",
					enableBrowserBoundsDetection : true,
					selectedIndex : h,
					width : 18,
					height : 21,
					dropDownHeight : 150,
					dropDownWidth : D,
					source : B,
					theme : H.theme
				});
				t.jqxDropDownList({
					selectionRenderer : function(f) {
						return ""
					}
				});
				t.jqxDropDownList("setContent", "");
				t.find(".jqx-dropdownlist-content").hide();
				if (C.createfilterwidget) {
					C.createfilterwidget(C, d, m)
				}
				C._filterwidget = m;
				var j = null;
				this.addHandler(t, "select", function() {
					C.previousConditionIndex = t.jqxDropDownList("getSelectedIndex");
					var f = t.jqxDropDownList("getSelectedItem").label;
					if (C._filterwidget.find("input").val().length > 0 && !H.refreshingfilter) {
						H._applyfilterfromfilterrow()
					}
					if (C.filtertype == "input" && !H.refreshingfilter) {
						H._applyfilterfromfilterrow()
					} else {
						if (C._filterwidget.find("input").val().length == 0 && !H.refreshingfilter) {
							if (j == "null" || j == "not null" || f == "null" || f == "not null") {
								H._applyfilterfromfilterrow()
							}
						}
					}
					j = f
				});
				break;
			case"textbox":
			case"default":
			default:
				g(this, d);
				break;
			case"none":
				break;
			case"date":
			case"range":
				if (this.host.jqxDateTimeInput) {
					z = "";
					for (var E = 0; E < H.dataview.filters.length; E++) {
						var x = H.dataview.filters[E];
						if (x.datafield && x.datafield == C.datafield) {
							if (x.filter.getfilters()[1]) {
								z = x.filter.getfilters()[0].value + " - " + x.filter.getfilters()[1].value;
							}
						}
					}
					var b = a("<div></div>");
					b.css("margin", "4px");
					b.appendTo(d);
					var n = {
						calendar : this.gridlocalization,
						todayString : this.gridlocalization.todaystring,
						clearString : this.gridlocalization.clearstring
					};
					b.jqxDateTimeInput({
						readonly : true,
						disabled : H.disabled,
						localization : n,
						rtl : H.rtl,
						showFooter : true,
						formatString : C.cellsformat,
						selectionMode : C.filtertype,
						value : null,
						theme : this.theme,
						width : A - 10,
						height : this.filterrowheight - 10
					});
					if (C.createfilterwidget) {
						C.createfilterwidget(C, d, b)
					}
					if (z && z.toString().length > 1) {
						b.val(z)
					}
					C._filterwidget = b;
					this.addHandler(b, "valueChanged", function(f) {
						if (!H.refreshingfilter) {
							H._applyfilterfromfilterrow();
							H.focusedfilter = null
						}
					})
				} else {
					g(this, d)
				}
				break;
			case"list":
			case"checkedlist":
				if (this.host.jqxDropDownList) {
					var q = this._getfilterdataadapter(C);
					var l = false;
					var t = a("<div></div>");
					t.css("margin", "4px");
					var u = C.datafield;
					var v = C.filtertype == "checkedlist" ? true : false;
					var D = A < 150 ? 220 : "auto";
					q.dataBind();
					var p = q.records;
					var k = p.length < 8 ? true : false;
					l = k;
					t.appendTo(d);
					t.jqxDropDownList({
						placeHolder : H.gridlocalization.filterchoosestring,
						disabled : H.disabled,
						touchMode : H.touchmode,
						rtl : H.rtl,
						checkboxes : v,
						dropDownWidth : D,
						source : q.records,
						autoDropDownHeight : k,
						dropDownHeight : 220,
						theme : this.theme,
						width : A - 10,
						height : this.filterrowheight - 10,
						displayMember : C.displayfield,
						valueMember : u
					});
					if (C.createfilterwidget) {
						C.createfilterwidget(C, d, t)
					}
					var c = t.jqxDropDownList("listBox");
					if (v) {
						t.jqxDropDownList({
							selectionRenderer : function() {
								var f = '<span class="' + H.toThemeProperty("jqx-item") + '" style="top: 2px; position: relative; color: inherit; border: none; background-color: transparent;">' + H.gridlocalization.filterselectstring + "</span>";
								return f
							}
						});
						var y = a('<span style="top: 2px; position: relative; color: inherit; border: none; background-color: transparent;">' + H.gridlocalization.filterselectstring + "</span>");
						y.addClass(this.toThemeProperty("jqx-item"));
						if (c != undefined) {
							if (!l) {
								c.host.height(200)
							}
							c.insertAt(H.gridlocalization.filterselectallstring, 0);
							t.jqxDropDownList("setContent", y);
							var i = true;
							var G = new Array();
							c.checkAll(false);
							H.addHandler(c.host, "checkChange", function(J) {
								t[0]._selectionChanged = true;
								if (!i) {
									return
								}
								if (J.args.label != H.gridlocalization.filterselectallstring) {
									i = false;
									c.host.jqxListBox("checkIndex", 0, true, false);
									var f = c.host.jqxListBox("getCheckedItems");
									var I = c.host.jqxListBox("getItems");
									if (f.length == 1) {
										c.host.jqxListBox("uncheckIndex", 0, true, false)
									} else {
										if (I.length != f.length) {
											c.host.jqxListBox("indeterminateIndex", 0, true, false)
										}
									}
									i = true
								} else {
									i = false;
									if (J.args.checked) {
										c.host.jqxListBox("checkAll", false)
									} else {
										c.host.jqxListBox("uncheckAll", false)
									}
									i = true
								}
							})
						}
					} else {
						c.insertAt({
							label : this.gridlocalization.filterchoosestring,
							value : ""
						}, 0);
						t.jqxDropDownList({
							selectedIndex : 0
						})
					}
					C._filterwidget = t;
					var o = t.jqxDropDownList("dropdownlistWrapper");
					if (C.filtertype == "list") {
						this.addHandler(t, "select", function(f) {
							if (!H.refreshingfilter) {
								if (f.args && f.args.type != "none") {
									H._applyfilterfromfilterrow();
									H.focusedfilter = null
								}
							}
						})
					} else {
						this.addHandler(t, "close", function(f) {
							if (t[0]._selectionChanged) {
								H._applyfilterfromfilterrow();
								H.focusedfilter = null;
								t[0]._selectionChanged = false
							}
						})
					}
				} else {
					g(this, d)
				}
				break;
			case"bool":
			case"boolean":
				if (this.host.jqxCheckBox) {
					var w = a('<div tabIndex=0 style="opacity: 0.99; position: absolute; top: 50%; left: 50%; margin-top: -7px; margin-left: -10px;"></div>');
					w.appendTo(d);
					w.jqxCheckBox({
						disabled : H.disabled,
						enableContainerClick : false,
						animationShowDelay : 0,
						animationHideDelay : 0,
						hasThreeStates : true,
						theme : this.theme,
						checked : null
					});
					if (C.createfilterwidget) {
						C.createfilterwidget(C, d, w)
					}
					if (z === true || z == "true") {
						w.jqxCheckBox({
							checked : true
						})
					} else {
						if (z === false || z == "false") {
							w.jqxCheckBox({
								checked : false
							})
						}
					}
					C._filterwidget = w;
					this.addHandler(w, "change", function(f) {
						if (!H.refreshingfilter) {
							if (f.args) {
								H.focusedfilter = null;
								H._applyfilterfromfilterrow()
							}
						}
					})
				} else {
					g(this, d)
				}
				break
			}
		},
		_applyfilterfromfilterrow : function() {
			if (this._disablefilterrow == true) {
				return
			}
			if (this.disabled) {
				return
			}
			var z = this.columns.records.length;
			var D = this.that;
			for (var t = 0; t < z; t++) {
				var k = new a.jqx.filter();
				var u = this.columns.records[t];
				if (!u.filterable) {
					continue
				}
				if (u.datafield === null) {
					continue
				}
				var f = D._getcolumntypebydatafield(u);
				var d = D._getfiltertype(f);
				var l = 1;
				var E = true;
				var e = u.filtertype;
				var B = function(j, L, I) {
					var i = true;
					if (j._filterwidget) {
						var G = j._filterwidget.val();
						if (G != "") {
							var J = "equal";
							if (L == "stringfilter") {
								var J = "contains"
							}
							if (L == "numericfilter") {
								if (D.gridlocalization.decimalseparator == ",") {
									if (G.indexOf(D.gridlocalization.decimalseparator) >= 0) {
										G = G.replace(D.gridlocalization.decimalseparator, ".")
									}
								}
							}
							if (L != "stringfilter") {
								var K = 0;
								if (G.indexOf(">") != -1) {
									J = "greater_than";
									K = 1
								}
								if (G.indexOf("<") != -1) {
									J = "less_than";
									K = 1
								}
								if (G.indexOf("=") != -1) {
									if (J == "greater_than") {
										J = "greater_than_or_equal";
										K = 2
									} else {
										if (J == "less_than") {
											J = "less_than_or_equal";
											K = 2
										} else {
											J = "equal";
											K = 1
										}
									}
								}
								if (K != 0) {
									G = G.substring(K);
									if (G.length < 1) {
										return false
									}
								}
							}
							if (j.filtercondition != undefined) {
								J = j.filtercondition
							}
							if (L == "datefilter") {
								var H = I.createfilter(L, G, J, null, j.cellsformat, D.gridlocalization)
							} else {
								var H = I.createfilter(L, G, J)
							}
							I.addfilter(l, H)
						} else {
							i = false
						}
					}
					return i
				};
				switch(u.filtertype) {
				case"range":
				case"date":
					if (u._filterwidget.jqxDateTimeInput) {
						if (u.filtertype == "range") {
							var p = u._filterwidget.jqxDateTimeInput("getRange");
							if (p != null && p.from != null && p.to != null) {
								var o = "GREATER_THAN_OR_EQUAL";
								var r = new Date(0);
								r.setHours(0);
								r.setMinutes(0);
								r.setFullYear(p.from.getFullYear(), p.from.getMonth(), p.from.getDate());
								var q = new Date(0);
								q.setHours(0);
								q.setMinutes(0);
								q.setFullYear(p.to.getFullYear(), p.to.getMonth(), p.to.getDate());
								q.setHours(p.to.getHours());
								q.setMinutes(p.to.getMinutes());
								q.setSeconds(p.to.getSeconds());
								var y = k.createfilter(d, r, o);
								k.addfilter(0, y);
								var c = "LESS_THAN_OR_EQUAL";
								var x = k.createfilter(d, q, c);
								k.addfilter(0, x)
							} else {
								E = false
							}
						} else {
							var p = u._filterwidget.jqxDateTimeInput("getDate");
							if (p != null) {
								var r = new Date(0);
								r.setHours(0);
								r.setMinutes(0);
								r.setFullYear(p.getFullYear(), p.getMonth(), p.getDate());
								var o = "EQUAL";
								if (u.filtercondition != undefined) {
									o = u.filtercondition
								}
								var y = k.createfilter(d, r, o);
								k.addfilter(0, y)
							} else {
								E = false
							}
						}
					} else {
						E = B(u, d, k)
					}
					break;
				case"input":
					if (u._filterwidget) {
						var p = u._filterwidget.find("input").val();
						var h = u._filterwidget.find(".filter").jqxDropDownList("selectedIndex");
						var w = k.getoperatorsbyfiltertype(d)[h];
						if (D.updatefilterconditions) {
							var F = D.updatefilterconditions(d, k.getoperatorsbyfiltertype(d));
							if (F != undefined) {
								k.setoperatorsbyfiltertype(d, F)
							}
							var w = k.getoperatorsbyfiltertype(d)[h]
						}
						var n = w == "NULL" || w == "NOT_NULL";
						var s = w == "EMPTY" || w == "NOT_EMPTY";
						if (p != undefined && p.length > 0 || n || s) {
							y = k.createfilter(d, p, w, null, u.cellsformat, D.gridlocalization);
							k.addfilter(0, y)
						} else {
							E = false
						}
					} else {
						E = false
					}
					break;
				case"number":
					if (u._filterwidget) {
						var p = u._filterwidget.find("input").val();
						if (D.gridlocalization.decimalseparator == ",") {
							if (p.indexOf(D.gridlocalization.decimalseparator) >= 0) {
								p = p.replace(D.gridlocalization.decimalseparator, ".")
							}
						}
						var h = u._filterwidget.find(".filter").jqxDropDownList("selectedIndex");
						var w = k.getoperatorsbyfiltertype(d)[h];
						if (D.updatefilterconditions) {
							var F = D.updatefilterconditions(d, k.getoperatorsbyfiltertype(d));
							if (F != undefined) {
								k.setoperatorsbyfiltertype(d, F)
							}
							var w = k.getoperatorsbyfiltertype(d)[h]
						}
						var n = w == "NULL" || w == "NOT_NULL";
						var s = w == "EMPTY" || w == "NOT_EMPTY";
						if (p != undefined && p.length > 0 || n || s) {
							y = k.createfilter(d, new Number(p), w, null, u.cellsformat, D.gridlocalization);
							k.addfilter(0, y)
						} else {
							E = false
						}
					} else {
						E = false
					}
					break;
				case"textbox":
				case"default":
					E = B(u, d, k);
					break;
				case"bool":
				case"boolean":
					if (u._filterwidget.jqxCheckBox) {
						var p = u._filterwidget.jqxCheckBox("checked");
						if (p != null) {
							var o = "equal";
							var m = k.createfilter(d, p, o);
							k.addfilter(l, m)
						} else {
							E = false
						}
					} else {
						E = B(u, d, k)
					}
					break;
				case"list":
					var g = u._filterwidget.jqxDropDownList("listBox");
					if (g.selectedIndex > 0) {
						var b = g.getItem(g.selectedIndex);
						var p = b.label;
						var A = b.value;
						var o = "equal";
						if (p === "") {
							o = "NULL"
						}
						var m = k.createfilter(d, p, o);
						k.addfilter(l, m);
						if (A !== p) {
							m.data = A
						}
					} else {
						E = false
					}
					break;
				case"checkedlist":
					if (u._filterwidget.jqxDropDownList) {
						var g = u._filterwidget.jqxDropDownList("listBox");
						var C = g.getCheckedItems();
						if (C.length == 0) {
							for (var v = 1; v < g.items.length; v++) {
								var p = g.items[v].label;
								var A = g.items[v].value;
								var o = "not_equal";
								if (p === "") {
									o = "NULL"
								}
								var m = k.createfilter(d, A, o);
								if (A !== p) {
									m.data = A
								}
								k.addfilter(0, m)
							}
							E = true
						} else {
							if (C.length != g.items.length) {
								for (var v = 0; v < C.length; v++) {
									var p = C[v].label;
									var A = C[v].value;
									var o = "equal";
									if (p === "") {
										o = "NULL"
									}
									var m = k.createfilter(d, A, o);
									if (A !== p) {
										m.data = A
									}
									k.addfilter(l, m)
								}
							} else {
								E = false
							}
						}
					} else {
						E = B(u, d, k)
					}
					break
				}
				if (!this._loading) {
					if (E) {
						this.addfilter(u.displayfield, k, false)
					} else {
						this.removefilter(u.displayfield, false)
					}
				}
			}
			if (!this._loading) {
				this.applyfilters("filterrow")
			}
		},
	});
})(jqxBaseFramework);