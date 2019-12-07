/*
 jQWidgets v4.0.0 (2016-Jan)
 Copyright (c) 2011-2016 jQWidgets.
 License: http://jqwidgets.com/license/
 */

(function(a) {
	a.extend(a.jqx._jqxListBox.prototype, {
		databind : function(b, d) {
			this.records = new Array();
			var f = b._source ? true : false;
			var c = new a.jqx.dataAdapter(b, {
				autoBind : false
			});
			if (f) {
				c = b;
				b = b._source
			}
			var e = function(k) {
				if (b.type != undefined) {
					c._options.type = b.type
				}
				if (b.formatdata != undefined) {
					c._options.formatData = b.formatdata
				}
				if (b.contenttype != undefined) {
					c._options.contentType = b.contenttype
				}
				if (b.async != undefined) {
					c._options.async = b.async
				}
			};
			var h = function(q, r) {
				var s = function(w) {
					var D = null;
					if ( typeof w === "string") {
						var z = w;
						var A = w;
						var C = ""
					} else {
						if (q.displayMember != undefined && q.displayMember != "") {
							var A = w[q.valueMember];
							var z = w[q.displayMember]
						}
					}
					var C = "";
					if (q.groupMember) {
						C = w[q.groupMember]
					} else {
						if (w && w.group != undefined) {
							C = w.group
						}
					}
					if (q.searchMember) {
						D = w[q.searchMember]
					} else {
						if (w && w.searchLabel != undefined) {
							D = w.searchLabel
						}
					}
					if (!q.valueMember && !q.displayMember) {
						// if (a.type(w) == "string") {
							//olbius fix if value & label == null;
							z = A = w;
						// }
					}
					if (w && w.label != undefined && !q.displayMember) {
						var z = w.label
					}
					if (w && w.value != undefined && !q.valueMember) {
						var A = w.value
					}
					var B = false;
					if (w && w.checked != undefined) {
						B = w.checked
					}
					var v = "";
					if (w && w.html != undefined) {
						v = w.html
					}
					var t = true;
					if (w && w.visible != undefined) {
						t = w.visible
					}
					var u = false;
					if (w && w.disabled != undefined) {
						u = w.disabled
					}
					var y = false;
					if (w && w.hasThreeStates != undefined) {
						y = w.hasThreeStates
					}
					var x = {};
					x.label = z;
					x.value = A;
					x.searchLabel = D;
					x.html = v;
					x.visible = t;
					x.originalItem = w;
					x.group = C;
					x.groupHtml = "";
					x.disabled = u;
					x.checked = B;
					x.hasThreeStates = y;
					return x
				};
				if (r != undefined) {
					var k = c._changedrecords[0];
					if (k) {
						a.each(c._changedrecords, function() {
							var t = this.index;
							var u = this.record;
							if (r != "remove") {
								var v = s(u)
							}
							switch(r) {
							case"update":
								q.updateAt(v, t);
								break;
							case"add":
								q.insertAt(v, t);
								break;
							case"remove":
								q.removeAt(t);
								break
							}
						});
						return
					}
				}
				q.records = c.records;
				var m = q.records.length;
				var p = new Array();
				for (var l = 0; l < m; l++) {
					var n = q.records[l];
					var o = s(n);
					o.index = l;
					p[l] = o
				}
				q.items = q.loadItems(p, true);
				q._render();
				q._raiseEvent("6")
			};
			e(this);
			var i = this;
			switch(b.datatype) {
			case"local":
			case"array":
			default:
				if (b.localdata != null || a.isArray(b)) {
					c.unbindBindingUpdate(this.element.id);
					if (this.autoBind || (!this.autoBind && !d)) {
						c.dataBind()
					}
					h(this);
					c.bindBindingUpdate(this.element.id, function(k) {
						h(i, k)
					})
				}
				break;
			case"json":
			case"jsonp":
			case"xml":
			case"xhtml":
			case"script":
			case"text":
			case"csv":
			case"tab":
				if (b.localdata != null) {
					c.unbindBindingUpdate(this.element.id);
					if (this.autoBind || (!this.autoBind && !d)) {
						c.dataBind()
					}
					h(this);
					c.bindBindingUpdate(this.element.id, function() {
						h(i)
					});
					return
				}
				var j = {};
				if (c._options.data) {
					a.extend(c._options.data, j)
				} else {
					if (b.data) {
						a.extend(j, b.data)
					}
					c._options.data = j
				}
				var g = function() {
					h(i)
				};
				c.unbindDownloadComplete(i.element.id);
				c.bindDownloadComplete(i.element.id, g);
				if (this.autoBind || (!this.autoBind && !d)) {
					c.dataBind()
				}
			}
		},
		loadItems : function(m, o) {
			if (m == null) {
				this.groups = new Array();
				this.items = new Array();
				this.visualItems = new Array();
				return
			}
			var t = this;
			var k = 0;
			var d = 0;
			var b = 0;
			this.groups = new Array();
			this.items = new Array();
			this.visualItems = new Array();
			var e = new Array();
			this.itemsByValue = new Array();
			a.map(m, function(x) {
				if (x == undefined) {
					return null
				}
				var j = new a.jqx._jqxListBox.item();
				var y = x.group;
				var i = x.groupHtml;
				var z = x.title;
				var v = null;
				if (t.searchMember) {
					v = x[t.searchMember]
				} else {
					if (x && x.searchLabel != undefined) {
						v = x.searchLabel
					}
				}
				if (z == null || z == undefined) {
					z = ""
				}
				if (y == null || y == undefined) {
					y = ""
				}
				if (t.groupMember) {
					y = x[t.groupMember]
				}
				if (i == null || i == undefined) {
					i = ""
				}
				if (!t.groups[y]) {
					t.groups[y] = {
						items : new Array(),
						index : -1,
						caption : y,
						captionHtml : i
					};
					k++;
					var u = k + "jqxGroup";
					t.groups[u] = t.groups[y];
					d++;
					t.groups.length = d
				}
				var w = t.groups[y];
				w.index++;
				w.items[w.index] = j;
				if ( typeof x === "string") {
					j.label = x;
					j.value = x;
					if (arguments.length > 1 && arguments[1] && a.type(arguments[1]) == "string") {
						j.label = x;
						j.value = arguments[1]
					}
				} else {
					if (x.label == null && x.value == null && x.html == null && x.group == null && x.groupHtml == null) {
						j.label = x.toString();
						j.value = x.toString()
					} else {
						j.label = x.label;
						j.value = x.value;
						if (j.label === undefined) {
							j.label = x.value
						}
						if (j.value === undefined) {
							j.value = x.label
						}
					}
				}
				if ( typeof x != "string") {
					//olbius fix value member display member without data adapter
					if(!o){
						if (t.displayMember != "") {
							if (x[t.displayMember] != undefined) {
								j.label = x[t.displayMember]
							} else {
								j.label = ""
							}
						}
						if (t.valueMember != "") {
							j.value = x[t.valueMember]
						}
					}
				}
				j.hasThreeStates = x.hasThreeStates != undefined ? x.hasThreeStates : true;
				j.originalItem = x;
				if (o) {
					j.originalItem = x.originalItem
				}
				j.title = z;
				if (z && j.value === undefined && j.label === undefined) {
					j.value = j.label = z
				}
				j.html = x.html || "";
				if (x.html && x.html != "") {
					if (z && z != "") {
					}
				}
				j.group = y;
				j.checked = x.checked || false;
				j.groupHtml = x.groupHtml || "";
				j.disabled = x.disabled || false;
				j.visible = x.visible != undefined ? x.visible : true;
				j.searchLabel = v;
				j.index = b;
				e[b] = j;
				b++;
				return j
			});
			var c = new Array();
			var p = 0;
			if (this.fromSelect == undefined || this.fromSelect == false) {
				for (var h = 0; h < d; h++) {
					var k = h + 1;
					var n = k + "jqxGroup";
					var r = this.groups[n];
					if (r == undefined || r == null) {
						break
					}
					if (h == 0 && r.caption == "" && r.captionHtml == "" && d <= 1) {
						for (var g = 0; g < r.items.length; g++) {
							var q = r.items[g].value;
							if (r.items[g].value == undefined || r.items[g].value == null) {
								q = g
							}
							this.itemsByValue[a.trim(q).split(" ").join("?")] = r.items[g]
						}
						return r.items
					} else {
						var l = new a.jqx._jqxListBox.item();
						l.isGroup = true;
						l.label = r.caption;
						if (r.caption == "" && r.captionHtml == "") {
							r.caption = this.emptyGroupText;
							l.label = r.caption
						}
						l.html = r.captionHtml;
						c[p] = l;
						p++
					}
					for (var f = 0; f < r.items.length; f++) {
						c[p] = r.items[f];
						var q = r.items[f].value;
						if (r.items[f].value == "" || r.items[f].value == null) {
							q = p
						}
						t.itemsByValue[a.trim(q).split(" ").join("?")] = r.items[f];
						p++
					}
				}
			} else {
				var p = 0;
				var s = new Array();
				a.each(e, function() {
					if (!s[this.group]) {
						if (this.group != "") {
							var i = new a.jqx._jqxListBox.item();
							i.isGroup = true;
							i.label = this.group;
							c[p] = i;
							p++;
							s[this.group] = true
						}
					}
					c[p] = this;
					var j = this.value;
					if (this.value == "" || this.value == null) {
						j = p - 1
					}
					t.itemsByValue[a.trim(j).split(" ").join("?")] = this;
					p++
				})
			}
			return c
		},
	});
})(jqxBaseFramework);