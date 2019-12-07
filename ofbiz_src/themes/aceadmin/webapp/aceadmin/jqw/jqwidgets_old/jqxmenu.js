/*
jQWidgets v3.5.0 (2014-Sep-15)
Copyright (c) 2011-2014 jQWidgets.
License: http://jqwidgets.com/license/
 */

(function(a) {
	a.jqx.jqxWidget("jqxMenu", "", {});
	a
			.extend(
					a.jqx._jqxMenu.prototype,
					{
						defineInstance : function() {
							var b = {
								items : new Array(),
								mode : "horizontal",
								width : null,
								height : null,
								minimizeWidth : "auto",
								easing : "easeInOutSine",
								animationShowDuration : 200,
								animationHideDuration : 200,
								autoCloseInterval : 0,
								animationHideDelay : 100,
								animationShowDelay : 100,
								menuElements : new Array(),
								autoSizeMainItems : false,
								autoCloseOnClick : true,
								autoCloseOnMouseLeave : true,
								enableRoundedCorners : true,
								disabled : false,
								autoOpenPopup : true,
								enableHover : true,
								autoOpen : true,
								autoGenerate : true,
								clickToOpen : false,
								showTopLevelArrows : false,
								touchMode : "auto",
								source : null,
								popupZIndex : 17000,
								rtl : false,
								title : "",
								events : [ "shown", "closed", "itemclick",
										"initialized" ]
							};
							a.extend(true, this, b)
						},
						createInstance : function(c) {
							var b = this;
							this.host.attr("role", "menubar");
							a.jqx.utilities.resize(this.host, function() {
								b.refresh()
							}, false, this.mode != "popup");
							this.host.css("outline", "none");
							if (this.source) {
								if (this.source != null) {
									var d = this.loadItems(this.source);
									this.element.innerHTML = d
								}
							}
							this._tmpHTML = this.element.innerHTML;
							if (this.element.innerHTML.indexOf("UL")) {
								var e = this.host.find("ul:first");
								if (e.length > 0) {
									this._createMenu(e[0])
								}
							}
							this.host.data("autoclose", {});
							this._render();
							this.setSize();
							if (a.jqx.browser.msie && a.jqx.browser.version < 8) {
								this.host.attr("hideFocus", true)
							}
						},
						focus : function() {
							try {
								this.host.focus()
							} catch (b) {
							}
						},
						loadItems : function(c, e) {
							if (c == null) {
								return
							}
							if (c.length == 0) {
								return ""
							}
							var b = this;
							this.items = new Array();
							var d = "<ul>";
							if (e) {
								d = '<ul style="width:' + e + ';">'
							}
							a.map(c, function(f) {
								if (f == undefined) {
									return null
								}
								d += b._parseItem(f)
							});
							d += "</ul>";
							return d
						},
						_parseItem : function(f) {
							var c = "";
							if (f == undefined) {
								return null
							}
							var b = f.label;
							if (!f.label && f.html) {
								b = f.html
							}
							if (!b) {
								b = "Item"
							}
							if (typeof f === "string") {
								b = f
							}
							var e = false;
							if (f.selected != undefined && f.selected) {
								e = true
							}
							var d = false;
							if (f.disabled != undefined && f.disabled) {
								d = true
							}
							c += "<li";
							if (d) {
								c += ' item-disabled="true" '
							}
							if (f.label && !f.html) {
								c += ' item-label="' + b + '" '
							}
							if (f.value != null) {
								c += ' item-value="' + f.value + '" '
							}
							if (f.id != undefined) {
								c += ' id="' + f.id + '" '
							}
							c += ">" + b;
							if (f.items) {
								if (f.subMenuWidth) {
									c += this
											.loadItems(f.items, f.subMenuWidth)
								} else {
									c += this.loadItems(f.items)
								}
							}
							c += "</li>";
							return c
						},
						setSize : function() {
							if (this.width != null
									&& this.width.toString().indexOf("%") != -1) {
								this.host.width(this.width)
							} else {
								if (this.width != null
										&& this.width.toString().indexOf("px") != -1) {
									this.host.width(this.width)
								} else {
									if (this.width != undefined
											&& !isNaN(this.width)) {
										this.host.width(this.width)
									}
								}
							}
							if (this.height != null
									&& this.height.toString().indexOf("%") != -1) {
								this.host.height(this.height)
							} else {
								if (this.height != null
										&& this.height.toString().indexOf("px") != -1) {
									this.host.height(this.height)
								} else {
									if (this.height != undefined
											&& !isNaN(this.height)) {
										this.host.height(this.height)
									}
								}
							}
							if (this.height === null) {
								this.host.height("auto")
							}
							var g = this;
							if (this.minimizeWidth != null
									&& this.mode != "popup") {
								var f = a(window).width();
								if (!a.jqx.response) {
									var e = false;
									if (navigator.userAgent
											.match(/Windows|Linux|MacOS/)) {
										var b = navigator.userAgent
												.indexOf("Windows Phone") >= 0
												|| navigator.userAgent
														.indexOf("WPDesktop") >= 0
												|| navigator.userAgent
														.indexOf("IEMobile") >= 0
												|| navigator.userAgent
														.indexOf("ZuneWP7") >= 0;
										if (!b) {
											e = true
										}
									}
									var c = this.minimizeWidth;
									if (e && this.minimizeWidth == "auto") {
										return
									}
								}
								if (this.minimizeWidth == "auto"
										&& a.jqx.response) {
									var d = new a.jqx.response();
									if (d.device.type == "Phone"
											|| d.device.type == "Tablet") {
										if (!this.minimized) {
											this.minimize()
										}
									}
								} else {
									if ((f < c) && !this.minimized) {
										this.minimize()
									} else {
										if (this.minimized && f >= c) {
											this.restore()
										}
									}
								}
							}
						},
						minimize : function() {
							if (this.minimized) {
								return
							}
							var e = this;
							this.host.addClass(this
									.toThemeProperty("jqx-menu-minimized"));
							this.minimized = true;
							this._tmpMode = this.mode;
							this.mode = "simple";
							var h = this.host.closest("div.jqx-menu-wrapper");
							h.remove();
							a("#menuWrapper" + this.element.id).remove();
							a.each(this.items, function() {
								var l = this;
								var k = a(l.element);
								var j = a(l.subMenuElement);
								var m = j.closest("div.jqx-menu-popup");
								m.remove()
							});
							if (this.source) {
								var d = this.loadItems(this.source);
								this.element.innerHTML = d;
								this._tmpHTML = this.element.innerHTML
							}
							this.element.innerHTML = this._tmpHTML;
							if (this.element.innerHTML.indexOf("UL")) {
								var g = this.host.find("ul:first");
								if (g.length > 0) {
									this._createMenu(g[0])
								}
							}
							this._render();
							var c = this.host.find("ul:first");
							c
									.wrap('<div class="jqx-menu-wrapper" style="z-index:'
											+ this.popupZIndex
											+ '; padding: 0px; display: none; margin: 0px; height: auto; width: auto; position: absolute; top: 0; left: 0; display: block; visibility: visible;"></div>');
							var h = c.closest("div.jqx-menu-wrapper");
							h[0].id = "menuWrapper" + this.element.id;
							h.detach();
							h.appendTo(a(document.body));
							h.addClass(this.toThemeProperty("jqx-widget"));
							h.addClass(this.toThemeProperty("jqx-menu"));
							h.addClass(this
									.toThemeProperty("jqx-menu-minimized"));
							h.addClass(this
									.toThemeProperty("jqx-widget-header"));
							c.children().hide();
							h.hide();
							h
									.find("ul")
									.addClass(
											this
													.toThemeProperty("jqx-menu-ul-minimized"));
							this.minimizedItem = a("<div></div>");
							this.minimizedItem.addClass(this
									.toThemeProperty("jqx-item"));
							this.minimizedItem.addClass(this
									.toThemeProperty("jqx-menu-item-top"));
							this.minimizedItem
									.addClass(this
											.toThemeProperty("jqx-menu-minimized-button"));
							this.minimizedItem.prependTo(this.host);
							this.titleElement = a("<div>" + this.title
									+ "</div>");
							this.titleElement.addClass(this
									.toThemeProperty("jqx-item"));
							this.titleElement.addClass(this
									.toThemeProperty("jqx-menu-title"));
							this.titleElement.prependTo(this.host);
							a("<div style='clear:both;'></div>").insertAfter(
									this.minimizedItem);
							e.minimizedHidden = true;
							var b = function(k) {
								e.minimizedHidden = true;
								e.minimizedItem.show();
								var j = false;
								if (e.minimizedItem.css("float") == "right") {
									j = true
								}
								h.animate({
									left : !j ? -h.outerWidth() : e.host
											.coord().left
											+ e.host.width() + h.width(),
									opacity : 0
								}, e.animationHideDuration, function() {
									h.find("ul:first").children().hide();
									h.hide()
								})
							};
							var f = function(l) {
								if (e.minimizedHidden) {
									h.find("ul:first").children().show();
									e.minimizedHidden = false;
									h.show();
									h.css("opacity", 0);
									h.css("left", -h.outerWidth());
									var k = false;
									var j = h.width();
									if (e.minimizedItem.css("float") == "right") {
										h.css("left", e.host.coord().left
												+ e.host.width() + j);
										k = true
									}
									h.css("top", e.host.coord().top
											+ e.host.height());
									h.animate({
										left : !k ? e.host.coord().left
												: e.host.coord().left
														+ e.host.width() - j,
										opacity : 0.95
									}, e.animationShowDuration, function() {
									})
								} else {
									b(l)
								}
								e._raiseEvent("2", {
									item : e.minimizedItem[0],
									event : l
								});
								e.setSize()
							};
							this
									.addHandler(
											a(window),
											"orientationchange.jqxmenu"
													+ this.element.id,
											function(j) {
												setTimeout(
														function() {
															if (!e.minimizedHidden) {
																var k = h
																		.width();
																var l = false;
																var k = h
																		.width();
																if (e.minimizedItem
																		.css("float") == "right") {
																	l = true
																}
																h
																		.css(
																				"top",
																				e.host
																						.coord().top
																						+ e.host
																								.height());
																h
																		.css({
																			left : !l ? e.host
																					.coord().left
																					: e.host
																							.coord().left
																							+ e.host
																									.width()
																							- k
																		})
															}
														}, 25)
											});
							this.addHandler(this.minimizedItem, "click",
									function(j) {
										f(j)
									})
						},
						restore : function() {
							if (!this.minimized) {
								return
							}
							this.host
									.find("ul")
									.removeClass(
											this
													.toThemeProperty("jqx-menu-ul-minimized"));
							this.host.removeClass(this
									.toThemeProperty("jqx-menu-minimized"));
							this.minimized = false;
							this.mode = this._tmpMode;
							if (this.minimizedItem) {
								this.minimizedItem.remove()
							}
							var d = a("#menuWrapper" + this.element.id);
							d.remove();
							if (this.source) {
								var b = this.loadItems(this.source);
								this.element.innerHTML = b;
								this._tmpHTML = b
							}
							this.element.innerHTML = this._tmpHTML;
							if (this.element.innerHTML.indexOf("UL")) {
								var c = this.host.find("ul:first");
								if (c.length > 0) {
									this._createMenu(c[0])
								}
							}
							this.setSize();
							this._render()
						},
						isTouchDevice : function() {
							if (this._isTouchDevice != undefined) {
								return this._isTouchDevice
							}
							var b = a.jqx.mobile.isTouchDevice();
							if (this.touchMode == true) {
								b = true
							} else {
								if (this.touchMode == false) {
									b = false
								}
							}
							if (b) {
								this.host.addClass(this
										.toThemeProperty("jqx-touch"));
								a(".jqx-menu-item").addClass(
										this.toThemeProperty("jqx-touch"))
							}
							this._isTouchDevice = b;
							return b
						},
						refresh : function(b) {
							if (!b) {
								this.setSize()
							}
						},
						resize : function(c, b) {
							this.width = c;
							this.height = b;
							this.refresh()
						},
						_closeAll : function(f) {
							var d = f != null ? f.data : this;
							var b = d.items;
							a.each(b, function() {
								var e = this;
								if (e.hasItems == true) {
									if (e.isOpen) {
										d._closeItem(d, e)
									}
								}
							});
							if (d.mode == "popup") {
								if (f != null) {
									var c = d._isRightClick(f);
									if (!c) {
										d.close()
									}
								}
							}
						},
						closeItem : function(e) {
							if (e == null) {
								return false
							}
							var b = e;
							var c = document.getElementById(b);
							var d = this;
							a.each(d.items, function() {
								var f = this;
								if (f.isOpen == true && f.element == c) {
									d._closeItem(d, f);
									if (f.parentId) {
										d.closeItem(f.parentId)
									}
								}
							});
							return true
						},
						openItem : function(e) {
							if (e == null) {
								return false
							}
							var b = e;
							var c = document.getElementById(b);
							var d = this;
							a.each(d.items, function() {
								var f = this;
								if (f.isOpen == false && f.element == c) {
									d._openItem(d, f);
									if (f.parentId) {
										d.openItem(f.parentId)
									}
								}
							});
							return true
						},
						_getClosedSubMenuOffset : function(c) {
							var b = a(c.subMenuElement);
							var f = -b.outerHeight();
							var e = -b.outerWidth();
							var d = c.level == 0 && this.mode == "horizontal";
							if (d) {
								e = 0
							} else {
								f = 0
							}
							switch (c.openVerticalDirection) {
							case "up":
							case "center":
								f = b.outerHeight();
								break
							}
							switch (c.openHorizontalDirection) {
							case this._getDir("left"):
								if (d) {
									e = 0
								} else {
									e = b.outerWidth()
								}
								break;
							case "center":
								if (d) {
									e = 0
								} else {
									e = b.outerWidth()
								}
								break
							}
							return {
								left : e,
								top : f
							}
						},
						_closeItem : function(m, p, g, c) {
							if (m == null || p == null) {
								return false
							}
							var k = a(p.subMenuElement);
							var b = p.level == 0 && this.mode == "horizontal";
							var f = this._getClosedSubMenuOffset(p);
							var n = f.top;
							var e = f.left;
							var j = a(p.element);
							var l = k.closest("div.jqx-menu-popup");
							if (l != null) {
								var h = m.animationHideDelay;
								if (c == true) {
									h = 0
								}
								if (k.data("timer").show != null) {
									clearTimeout(k.data("timer").show);
									k.data("timer").show = null
								}
								var o = function() {
									p.isOpen = false;
									if (b) {
										k
												.stop()
												.animate(
														{
															top : n
														},
														m.animationHideDuration,
														function() {
															a(p.element)
																	.removeClass(
																			m
																					.toThemeProperty("jqx-fill-state-pressed"));
															a(p.element)
																	.removeClass(
																			m
																					.toThemeProperty("jqx-menu-item-top-selected"));
															a(p.element)
																	.removeClass(
																			m
																					.toThemeProperty("jqx-rc-b-expanded"));
															l
																	.removeClass(m
																			.toThemeProperty("jqx-rc-t-expanded"));
															var q = a(p.arrow);
															if (q.length > 0
																	&& m.showTopLevelArrows) {
																q.removeClass();
																if (p.openVerticalDirection == "down") {
																	q
																			.addClass(m
																					.toThemeProperty("jqx-menu-item-arrow-down"));
																	q
																			.addClass(m
																					.toThemeProperty("jqx-icon-arrow-down"))
																} else {
																	q
																			.addClass(m
																					.toThemeProperty("jqx-menu-item-arrow-up"));
																	q
																			.addClass(m
																					.toThemeProperty("jqx-icon-arrow-up"))
																}
															}
															a.jqx
																	.aria(
																			a(p.element),
																			"aria-expanded",
																			false);
															l
																	.css({
																		display : "none"
																	});
															if (m.animationHideDuration == 0) {
																k.css({
																	top : n
																})
															}
															m._raiseEvent("1",
																	p)
														})
									} else {
										if (!a.jqx.browser.msie) {
										}
										k
												.stop()
												.animate(
														{
															left : e
														},
														m.animationHideDuration,
														function() {
															if (m.animationHideDuration == 0) {
																k.css({
																	left : e
																})
															}
															if (p.level > 0) {
																a(p.element)
																		.removeClass(
																				m
																						.toThemeProperty("jqx-fill-state-pressed"));
																a(p.element)
																		.removeClass(
																				m
																						.toThemeProperty("jqx-menu-item-selected"));
																var q = a(p.arrow);
																if (q.length > 0) {
																	q
																			.removeClass();
																	if (p.openHorizontalDirection != "left") {
																		q
																				.addClass(m
																						.toThemeProperty("jqx-menu-item-arrow-"
																								+ m
																										._getDir("right")));
																		q
																				.addClass(m
																						.toThemeProperty("jqx-icon-arrow-"
																								+ m
																										._getDir("right")))
																	} else {
																		q
																				.addClass(m
																						.toThemeProperty("jqx-menu-item-arrow-"
																								+ m
																										._getDir("left")));
																		q
																				.addClass(m
																						.toThemeProperty("jqx-icon-arrow-"
																								+ m
																										._getDir("left")))
																	}
																}
															} else {
																a(p.element)
																		.removeClass(
																				m
																						.toThemeProperty("jqx-fill-state-pressed"));
																a(p.element)
																		.removeClass(
																				m
																						.toThemeProperty("jqx-menu-item-top-selected"));
																var q = a(p.arrow);
																if (q.length > 0) {
																	q
																			.removeClass();
																	if (p.openHorizontalDirection != "left") {
																		q
																				.addClass(m
																						.toThemeProperty("jqx-menu-item-arrow-top-"
																								+ m
																										._getDir("right")));
																		q
																				.addClass(m
																						.toThemeProperty("jqx-icon-arrow-"
																								+ m
																										._getDir("right")))
																	} else {
																		q
																				.addClass(m
																						.toThemeProperty("jqx-menu-item-arrow-top-"
																								+ m
																										._getDir("left")));
																		q
																				.addClass(m
																						.toThemeProperty("jqx-icon-arrow-"
																								+ m
																										._getDir("left")))
																	}
																}
															}
															a.jqx
																	.aria(
																			a(p.element),
																			"aria-expanded",
																			false);
															l
																	.css({
																		display : "none"
																	});
															m._raiseEvent("1",
																	p)
														})
									}
								};
								if (h > 0) {
									k.data("timer").hide = setTimeout(
											function() {
												o()
											}, h)
								} else {
									o()
								}
								if (g != undefined && g) {
									var d = k.children();
									a
											.each(
													d,
													function() {
														if (m.menuElements[this.id]
																&& m.menuElements[this.id].isOpen) {
															var q = a(m.menuElements[this.id].subMenuElement);
															m
																	._closeItem(
																			m,
																			m.menuElements[this.id],
																			true,
																			true)
														}
													})
								}
							}
						},
						getSubItems : function(j, h) {
							if (j == null) {
								return false
							}
							var g = this;
							var c = new Array();
							if (h != null) {
								a.extend(c, h)
							}
							var d = j;
							var f = this.menuElements[d];
							var b = a(f.subMenuElement);
							var e = b.find(".jqx-menu-item");
							a.each(e, function() {
								c[this.id] = g.menuElements[this.id];
								var k = g.getSubItems(this.id, c);
								a.extend(c, k)
							});
							return c
						},
						disable : function(g, d) {
							if (g == null) {
								return
							}
							var c = g;
							var f = this;
							if (this.menuElements[c]) {
								var e = this.menuElements[c];
								e.disabled = d;
								var b = a(e.element);
								e.element.disabled = d;
								a.each(b.children(), function() {
									this.disabled = d
								});
								if (d) {
									b
											.addClass(f
													.toThemeProperty("jqx-menu-item-disabled"));
									b
											.addClass(f
													.toThemeProperty("jqx-fill-state-disabled"))
								} else {
									b
											.removeClass(f
													.toThemeProperty("jqx-menu-item-disabled"));
									b
											.removeClass(f
													.toThemeProperty("jqx-fill-state-disabled"))
								}
							}
						},
						_setItemProperty : function(g, c, f) {
							if (g == null) {
								return
							}
							var b = g;
							var e = this;
							if (this.menuElements[b]) {
								var d = this.menuElements[b];
								if (d[c]) {
									d[c] = f
								}
							}
						},
						setItemOpenDirection : function(d, c, e) {
							if (d == null) {
								return
							}
							var k = d;
							var g = this;
							var f = a.jqx.browser.msie
									&& a.jqx.browser.version < 8;
							if (this.menuElements[k]) {
								var j = this.menuElements[k];
								if (c != null) {
									j.openHorizontalDirection = c;
									if (j.hasItems && j.level > 0) {
										var h = a(j.element);
										if (h != undefined) {
											var b = a(j.arrow);
											if (j.arrow == null) {
												b = a('<span id="arrow'
														+ h[0].id + '"></span>');
												if (!f) {
													b.prependTo(h)
												} else {
													b.appendTo(h)
												}
												j.arrow = b[0]
											}
											b.removeClass();
											if (j.openHorizontalDirection == "left") {
												b
														.addClass(g
																.toThemeProperty("jqx-menu-item-arrow-"
																		+ g
																				._getDir("left")));
												b
														.addClass(g
																.toThemeProperty("jqx-icon-arrow-"
																		+ g
																				._getDir("left")))
											} else {
												b
														.addClass(g
																.toThemeProperty("jqx-menu-item-arrow-"
																		+ g
																				._getDir("right")));
												b
														.addClass(g
																.toThemeProperty("jqx-icon-arrow-"
																		+ g
																				._getDir("right")))
											}
											b.css("visibility", "visible");
											if (!f) {
												b.css("display", "block");
												b.css("float", "right")
											} else {
												b
														.css("display",
																"inline-block");
												b.css("float", "none")
											}
										}
									}
								}
								if (e != null) {
									j.openVerticalDirection = e;
									var b = a(j.arrow);
									var h = a(j.element);
									if (!g.showTopLevelArrows) {
										return
									}
									if (h != undefined) {
										if (j.arrow == null) {
											b = a('<span id="arrow' + h[0].id
													+ '"></span>');
											if (!f) {
												b.prependTo(h)
											} else {
												b.appendTo(h)
											}
											j.arrow = b[0]
										}
										b.removeClass();
										if (j.openVerticalDirection == "down") {
											b
													.addClass(g
															.toThemeProperty("jqx-menu-item-arrow-down"));
											b
													.addClass(g
															.toThemeProperty("jqx-icon-arrow-down"))
										} else {
											b
													.addClass(g
															.toThemeProperty("jqx-menu-item-arrow-up"));
											b
													.addClass(g
															.toThemeProperty("jqx-icon-arrow-up"))
										}
										b.css("visibility", "visible");
										if (!f) {
											b.css("display", "block");
											b.css("float", "right")
										} else {
											b.css("display", "inline-block");
											b.css("float", "none")
										}
									}
								}
							}
						},
						_getSiblings : function(c) {
							var d = new Array();
							var b = 0;
							for (i = 0; i < this.items.length; i++) {
								if (this.items[i] == c) {
									continue
								}
								if (this.items[i].parentId == c.parentId
										&& this.items[i].hasItems) {
									d[b++] = this.items[i]
								}
							}
							return d
						},
						_openItem : function(t, s, r) {
							if (t == null || s == null) {
								return false
							}
							if (s.isOpen) {
								return false
							}
							if (s.disabled) {
								return false
							}
							if (t.disabled) {
								return false
							}
							var m = t.popupZIndex;
							if (r != undefined) {
								m = r
							}
							var e = t.animationHideDuration;
							t.animationHideDuration = 0;
							t._closeItem(t, s, true, true);
							t.animationHideDuration = e;
							this.host.focus();
							var f = [ 5, 5 ];
							var u = a(s.subMenuElement);
							if (u != null) {
								u.stop()
							}
							if (u.data("timer").hide != null) {
								clearTimeout(u.data("timer").hide)
							}
							var p = u.closest("div.jqx-menu-popup");
							var h = a(s.element);
							var j = s.level == 0 ? this._getOffset(s.element)
									: h.position();
							if (s.level > 0 && this.hasTransform) {
								var q = parseInt(h.coord().top)
										- parseInt(this._getOffset(s.element).top);
								j.top += q
							}
							if (s.level == 0 && this.mode == "popup") {
								j = h.coord()
							}
							var k = s.level == 0 && this.mode == "horizontal";
							var b = k ? j.left
									: this.menuElements[s.parentId] != null
											&& this.menuElements[s.parentId].subMenuElement != null ? parseInt(a(
											a(
													this.menuElements[s.parentId].subMenuElement)
													.closest(
															"div.jqx-menu-popup"))
											.outerWidth())
											- f[0]
											: parseInt(u.outerWidth());
							p.css({
								visibility : "visible",
								display : "block",
								left : b,
								top : k ? j.top + h.outerHeight() : j.top,
								zIndex : m
							});
							u.css("display", "block");
							if (this.mode != "horizontal" && s.level == 0) {
								var d = this._getOffset(this.element);
								p.css("left", -1 + d.left
										+ this.host.outerWidth());
								u.css("left", -u.outerWidth())
							} else {
								var c = this._getClosedSubMenuOffset(s);
								u.css("left", c.left);
								u.css("top", c.top)
							}
							p.css({
								height : parseInt(u.outerHeight())
										+ parseInt(f[1]) + "px"
							});
							var o = 0;
							var g = 0;
							switch (s.openVerticalDirection) {
							case "up":
								if (k) {
									u.css("top", u.outerHeight());
									o = f[1];
									var l = parseInt(u.parent().css(
											"padding-bottom"));
									if (isNaN(l)) {
										l = 0
									}
									if (l > 0) {
										p
												.addClass(this
														.toThemeProperty("jqx-menu-popup-clear"))
									}
									u.css("top", u.outerHeight() - l);
									p.css({
										display : "block",
										top : j.top - p.outerHeight(),
										zIndex : m
									})
								} else {
									o = f[1];
									u.css("top", u.outerHeight());
									p.css({
										display : "block",
										top : j.top - p.outerHeight() + f[1]
												+ h.outerHeight(),
										zIndex : m
									})
								}
								break;
							case "center":
								if (k) {
									u.css("top", 0);
									p.css({
										display : "block",
										top : j.top - p.outerHeight() / 2
												+ f[1],
										zIndex : m
									})
								} else {
									u.css("top", 0);
									p.css({
										display : "block",
										top : j.top + h.outerHeight() / 2
												- p.outerHeight() / 2 + f[1],
										zIndex : m
									})
								}
								break
							}
							switch (s.openHorizontalDirection) {
							case this._getDir("left"):
								if (k) {
									p
											.css({
												left : j.left
														- (p.outerWidth()
																- h
																		.outerWidth() - f[0])
											})
								} else {
									g = 0;
									u.css("left", p.outerWidth());
									p.css({
										left : j.left - (p.outerWidth()) + 2
												* s.level
									})
								}
								break;
							case "center":
								if (k) {
									p
											.css({
												left : j.left
														- (p.outerWidth()
																/ 2
																- h
																		.outerWidth()
																/ 2 - f[0] / 2)
											})
								} else {
									p
											.css({
												left : j.left
														- (p.outerWidth()
																/ 2
																- h
																		.outerWidth()
																/ 2 - f[0] / 2)
											});
									u.css("left", p.outerWidth())
								}
								break
							}
							if (k) {
								if (parseInt(u.css("top")) == o) {
									s.isOpen = true;
									return
								}
							} else {
								if (parseInt(u.css("left")) == g) {
									s.isOpen == true;
									return
								}
							}
							a.each(t._getSiblings(s), function() {
								t._closeItem(t, this, true, true)
							});
							var n = a.data(t.element, "animationHideDelay");
							t.animationHideDelay = n;
							if (this.autoCloseInterval > 0) {
								if (this.host.data("autoclose") != null
										&& this.host.data("autoclose").close != null) {
									clearTimeout(this.host.data("autoclose").close)
								}
								if (this.host.data("autoclose") != null) {
									this.host.data("autoclose").close = setTimeout(
											function() {
												t._closeAll()
											}, this.autoCloseInterval)
								}
							}
							u.data("timer").show = setTimeout(
									function() {
										if (p != null) {
											if (k) {
												u.stop();
												u.css("left", g);
												if (!a.jqx.browser.msie) {
												}
												h
														.addClass(t
																.toThemeProperty("jqx-fill-state-pressed"));
												h
														.addClass(t
																.toThemeProperty("jqx-menu-item-top-selected"));
												if (s.openVerticalDirection == "down") {
													a(s.element)
															.addClass(
																	t
																			.toThemeProperty("jqx-rc-b-expanded"));
													p
															.addClass(t
																	.toThemeProperty("jqx-rc-t-expanded"))
												} else {
													a(s.element)
															.addClass(
																	t
																			.toThemeProperty("jqx-rc-t-expanded"));
													p
															.addClass(t
																	.toThemeProperty("jqx-rc-b-expanded"))
												}
												var v = a(s.arrow);
												if (v.length > 0
														&& t.showTopLevelArrows) {
													v.removeClass();
													if (s.openVerticalDirection == "down") {
														v
																.addClass(t
																		.toThemeProperty("jqx-menu-item-arrow-down-selected"));
														v
																.addClass(t
																		.toThemeProperty("jqx-icon-arrow-down"))
													} else {
														v
																.addClass(t
																		.toThemeProperty("jqx-menu-item-arrow-up-selected"));
														v
																.addClass(t
																		.toThemeProperty("jqx-icon-arrow-up"))
													}
												}
												if (t.animationShowDuration == 0) {
													u.css({
														top : o
													});
													s.isOpen = true;
													t._raiseEvent("0", s);
													a.jqx.aria(a(s.element),
															"aria-expanded",
															true)
												} else {
													u
															.animate(
																	{
																		top : o
																	},
																	t.animationShowDuration,
																	t.easing,
																	function() {
																		s.isOpen = true;
																		a.jqx
																				.aria(
																						a(s.element),
																						"aria-expanded",
																						true);
																		t
																				._raiseEvent(
																						"0",
																						s)
																	})
												}
											} else {
												u.stop();
												u.css("top", o);
												if (!a.jqx.browser.msie) {
												}
												if (s.level > 0) {
													h
															.addClass(t
																	.toThemeProperty("jqx-fill-state-pressed"));
													h
															.addClass(t
																	.toThemeProperty("jqx-menu-item-selected"));
													var v = a(s.arrow);
													if (v.length > 0) {
														v.removeClass();
														if (s.openHorizontalDirection != "left") {
															v
																	.addClass(t
																			.toThemeProperty("jqx-menu-item-arrow-"
																					+ t
																							._getDir("right")
																					+ "-selected"));
															v
																	.addClass(t
																			.toThemeProperty("jqx-icon-arrow-"
																					+ t
																							._getDir("right")))
														} else {
															v
																	.addClass(t
																			.toThemeProperty("jqx-menu-item-arrow-"
																					+ t
																							._getDir("left")
																					+ "-selected"));
															v
																	.addClass(t
																			.toThemeProperty("jqx-icon-arrow-"
																					+ t
																							._getDir("left")))
														}
													}
												} else {
													h
															.addClass(t
																	.toThemeProperty("jqx-fill-state-pressed"));
													h
															.addClass(t
																	.toThemeProperty("jqx-menu-item-top-selected"));
													var v = a(s.arrow);
													if (v.length > 0) {
														v.removeClass();
														if (s.openHorizontalDirection != "left") {
															v
																	.addClass(t
																			.toThemeProperty("jqx-menu-item-arrow-"
																					+ t
																							._getDir("right")
																					+ "-selected"));
															v
																	.addClass(t
																			.toThemeProperty("jqx-icon-arrow-"
																					+ t
																							._getDir("right")))
														} else {
															v
																	.addClass(t
																			.toThemeProperty("jqx-menu-item-arrow-"
																					+ t
																							._getDir("left")
																					+ "-selected"));
															v
																	.addClass(t
																			.toThemeProperty("jqx-icon-arrow-"
																					+ t
																							._getDir("left")))
														}
													}
												}
												if (!a.jqx.browser.msie) {
												}
												if (t.animationShowDuration == 0) {
													u.css({
														left : g
													});
													t._raiseEvent("0", s);
													s.isOpen = true;
													a.jqx.aria(a(s.element),
															"aria-expanded",
															true)
												} else {
													u
															.animate(
																	{
																		left : g
																	},
																	t.animationShowDuration,
																	t.easing,
																	function() {
																		t
																				._raiseEvent(
																						"0",
																						s);
																		s.isOpen = true;
																		a.jqx
																				.aria(
																						a(s.element),
																						"aria-expanded",
																						true)
																	})
												}
											}
										}
									}, this.animationShowDelay)
						},
						_getDir : function(b) {
							switch (b) {
							case "left":
								return !this.rtl ? "left" : "right";
							case "right":
								return this.rtl ? "left" : "right"
							}
							return "left"
						},
						_applyOrientation : function(j, d) {
							var g = this;
							var f = 0;
							this.host.removeClass(g
									.toThemeProperty("jqx-menu-horizontal"));
							this.host.removeClass(g
									.toThemeProperty("jqx-menu-vertical"));
							this.host
									.removeClass(g.toThemeProperty("jqx-menu"));
							this.host.removeClass(g
									.toThemeProperty("jqx-widget"));
							this.host.addClass(g.toThemeProperty("jqx-widget"));
							this.host.addClass(g.toThemeProperty("jqx-menu"));
							if (j != undefined && d != undefined
									&& d == "popup") {
								if (this.host.parent().length > 0
										&& this.host.parent().parent().length > 0
										&& this.host.parent().parent()[0] == document.body) {
									var h = a.data(document.body,
											"jqxMenuOldHost" + this.element.id);
									if (h != null) {
										var e = this.host
												.closest("div.jqx-menu-wrapper");
										e.remove();
										e.appendTo(h);
										this.host.css("display", "block");
										this.host.css("visibility", "visible");
										e.css("display", "block");
										e.css("visibility", "visible")
									}
								}
							} else {
								if (j == undefined && d == undefined) {
									a.data(document.body, "jqxMenuOldHost"
											+ this.element.id, this.host
											.parent()[0])
								}
							}
							if (this.autoOpenPopup) {
								if (this.mode == "popup") {
									this.addHandler(a(document), "contextmenu."
											+ this.element.id, function(k) {
										return false
									});
									this.addHandler(a(document),
											"mousedown.menu" + this.element.id,
											function(k) {
												g._openContextMenu(k)
											})
								} else {
									this.removeHandler(a(document),
											"contextmenu." + this.element.id);
									this.removeHandler(a(document),
											"mousedown.menu" + this.element.id)
								}
							} else {
								this.removeHandler(a(document), "contextmenu."
										+ this.element.id);
								this.removeHandler(a(document),
										"mousedown.menu" + this.element.id)
							}
							if (this.rtl) {
								this.host.addClass(this
										.toThemeProperty("jqx-rtl"))
							}
							switch (this.mode) {
							case "horizontal":
								this.host.addClass(g
										.toThemeProperty("jqx-widget-header"));
								this.host
										.addClass(g
												.toThemeProperty("jqx-menu-horizontal"));
								a
										.each(
												this.items,
												function() {
													var m = this;
													$element = a(m.element);
													var l = a(m.arrow);
													l.removeClass();
													if (m.hasItems
															&& m.level > 0) {
														var l = a('<span style="border: none; background-color: transparent;" id="arrow'
																+ $element[0].id
																+ '"></span>');
														l.prependTo($element);
														l
																.css(
																		"float",
																		g
																				._getDir("right"));
														l
																.addClass(g
																		.toThemeProperty("jqx-menu-item-arrow-"
																				+ g
																						._getDir("right")));
														l
																.addClass(g
																		.toThemeProperty("jqx-icon-arrow-"
																				+ g
																						._getDir("right")));
														m.arrow = l[0]
													}
													if (m.level == 0) {
														a(m.element)
																.css(
																		"float",
																		g
																				._getDir("left"));
														if (!m.ignoretheme
																&& m.hasItems
																&& g.showTopLevelArrows) {
															var l = a('<span style="border: none; background-color: transparent;" id="arrow'
																	+ $element[0].id
																	+ '"></span>');
															var k = a.jqx.browser.msie
																	&& a.jqx.browser.version < 8;
															if (m.arrow == null) {
																if (!k) {
																	l
																			.prependTo($element)
																} else {
																	l
																			.appendTo($element)
																}
															} else {
																l = a(m.arrow)
															}
															if (m.openVerticalDirection == "down") {
																l
																		.addClass(g
																				.toThemeProperty("jqx-menu-item-arrow-down"));
																l
																		.addClass(g
																				.toThemeProperty("jqx-icon-arrow-down"))
															} else {
																l
																		.addClass(g
																				.toThemeProperty("jqx-menu-item-arrow-up"));
																l
																		.addClass(g
																				.toThemeProperty("jqx-icon-arrow-up"))
															}
															l.css("visibility",
																	"visible");
															if (!k) {
																l
																		.css(
																				"display",
																				"block");
																l
																		.css(
																				"float",
																				"right")
															} else {
																l
																		.css(
																				"display",
																				"inline-block")
															}
															m.arrow = l[0]
														} else {
															if (!m.ignoretheme
																	&& m.hasItems
																	&& !g.showTopLevelArrows) {
																if (m.arrow != null) {
																	var l = a(m.arrow);
																	l.remove();
																	m.arrow = null
																}
															}
														}
														f = Math
																.max(
																		f,
																		$element
																				.height())
													}
												});
								break;
							case "vertical":
							case "popup":
							case "simple":
								this.host.addClass(g
										.toThemeProperty("jqx-menu-vertical"));
								a
										.each(
												this.items,
												function() {
													var l = this;
													$element = a(l.element);
													if (l.hasItems
															&& !l.ignoretheme) {
														if (l.arrow) {
															a(l.arrow).remove()
														}
														if (g.mode == "simple") {
															return true
														}
														var k = a('<span style="border: none; background-color: transparent;" id="arrow'
																+ $element[0].id
																+ '"></span>');
														k.prependTo($element);
														k.css("float", "right");
														if (l.level == 0) {
															k
																	.addClass(g
																			.toThemeProperty("jqx-menu-item-arrow-top-"
																					+ g
																							._getDir("right")));
															k
																	.addClass(g
																			.toThemeProperty("jqx-icon-arrow-"
																					+ g
																							._getDir("right")))
														} else {
															k
																	.addClass(g
																			.toThemeProperty("jqx-menu-item-arrow-"
																					+ g
																							._getDir("right")));
															k
																	.addClass(g
																			.toThemeProperty("jqx-icon-arrow-"
																					+ g
																							._getDir("right")))
														}
														l.arrow = k[0]
													}
													$element.css("float",
															"none")
												});
								if (this.mode == "popup") {
									this.host
											.addClass(g
													.toThemeProperty("jqx-widget-content"));
									this.host
											.wrap('<div class="jqx-menu-wrapper" style="z-index:'
													+ this.popupZIndex
													+ '; border: none; background-color: transparent; padding: 0px; margin: 0px; position: absolute; top: 0; left: 0; display: block; visibility: visible;"></div>');
									var e = this.host
											.closest("div.jqx-menu-wrapper");
									this.host.addClass(g
											.toThemeProperty("jqx-popup"));
									e[0].id = "menuWrapper" + this.element.id;
									e.appendTo(a(document.body))
								} else {
									this.host
											.addClass(g
													.toThemeProperty("jqx-widget-header"))
								}
								if (this.mode == "popup") {
									var b = this.host.height();
									this.host.css("position", "absolute");
									this.host.css("top", "0");
									this.host.css("left", "0");
									if (this.mode != "simple") {
										this.host.height(b);
										this.host.css("display", "none")
									}
								}
								break
							}
							var c = this.isTouchDevice();
							if (this.autoCloseOnClick) {
								this.removeHandler(a(document),
										"mousedown.menu" + this.element.id,
										g._closeAfterClick);
								this.addHandler(a(document), "mousedown.menu"
										+ this.element.id, g._closeAfterClick,
										g);
								if (c) {
									this
											.removeHandler(
													a(document),
													a.jqx.mobile
															.getTouchEventName("touchstart")
															+ ".menu"
															+ this.element.id,
													g._closeAfterClick, g);
									this.addHandler(a(document), a.jqx.mobile
											.getTouchEventName("touchstart")
											+ ".menu" + this.element.id,
											g._closeAfterClick, g)
								}
							}
						},
						_getBodyOffset : function() {
							var c = 0;
							var b = 0;
							if (a("body").css("border-top-width") != "0px") {
								c = parseInt(a("body").css("border-top-width"));
								if (isNaN(c)) {
									c = 0
								}
							}
							if (a("body").css("border-left-width") != "0px") {
								b = parseInt(a("body").css("border-left-width"));
								if (isNaN(b)) {
									b = 0
								}
							}
							return {
								left : b,
								top : c
							}
						},
						_getOffset : function(c) {
							var e = a.jqx.mobile.isSafariMobileBrowser();
							var h = a(c).coord(true);
							var g = h.top;
							var f = h.left;
							if (a("body").css("border-top-width") != "0px") {
								g = parseInt(g) + this._getBodyOffset().top
							}
							if (a("body").css("border-left-width") != "0px") {
								f = parseInt(f) + this._getBodyOffset().left
							}
							var d = a.jqx.mobile.isWindowsPhone();
							if (this.hasTransform || (e != null && e) || d) {
								var b = {
									left : a.jqx.mobile.getLeftPos(c),
									top : a.jqx.mobile.getTopPos(c)
								};
								return b
							} else {
								return {
									left : f,
									top : g
								}
							}
						},
						_isRightClick : function(c) {
							var b;
							if (!c) {
								var c = window.event
							}
							if (c.which) {
								b = (c.which == 3)
							} else {
								if (c.button) {
									b = (c.button == 2)
								}
							}
							return b
						},
						_openContextMenu : function(d) {
							var c = this;
							var b = c._isRightClick(d);
							if (b) {
								c.open(parseInt(d.clientX) + 5,
										parseInt(d.clientY) + 5)
							}
						},
						close : function() {
							var c = this;
							var d = a.data(this.element, "contextMenuOpened"
									+ this.element.id);
							if (d) {
								var b = this.host;
								a.each(c.items, function() {
									var e = this;
									if (e.hasItems) {
										c._closeItem(c, e)
									}
								});
								a.each(c.items, function() {
									var e = this;
									if (e.isOpen == true) {
										$submenu = a(e.subMenuElement);
										var f = $submenu
												.closest("div.jqx-menu-popup");
										f.hide(this.animationHideDuration)
									}
								});
								this.host.hide(this.animationHideDuration);
								a.data(c.element, "contextMenuOpened"
										+ this.element.id, false);
								c._raiseEvent("1", c)
							}
						},
						open : function(e, d) {
							if (this.mode == "popup") {
								var c = 0;
								if (this.host.css("display") == "block") {
									this.close();
									c = this.animationHideDuration
								}
								var b = this;
								if (e == undefined || e == null) {
									e = 0
								}
								if (d == undefined || d == null) {
									d = 0
								}
								setTimeout(function() {
									b.host.show(b.animationShowDuration);
									b.host.css("visibility", "visible");
									a.data(b.element, "contextMenuOpened"
											+ b.element.id, true);
									b._raiseEvent("0", b);
									b.host.css("z-index", 9999);
									if (e != undefined && d != undefined) {
										b.host.css({
											left : e,
											top : d
										})
									}
								}, c)
							}
						},
						_renderHover : function(c, e, b) {
							var d = this;
							if (!e.ignoretheme) {
								this
										.addHandler(
												c,
												"mouseenter",
												function() {
													if (!e.disabled
															&& !e.separator
															&& d.enableHover
															&& !d.disabled) {
														if (e.level > 0) {
															c
																	.addClass(d
																			.toThemeProperty("jqx-fill-state-hover"));
															c
																	.addClass(d
																			.toThemeProperty("jqx-menu-item-hover"))
														} else {
															c
																	.addClass(d
																			.toThemeProperty("jqx-fill-state-hover"));
															c
																	.addClass(d
																			.toThemeProperty("jqx-menu-item-top-hover"))
														}
													}
												});
								this
										.addHandler(
												c,
												"mouseleave",
												function() {
													if (!e.disabled
															&& !e.separator
															&& d.enableHover
															&& !d.disabled) {
														if (e.level > 0) {
															c
																	.removeClass(d
																			.toThemeProperty("jqx-fill-state-hover"));
															c
																	.removeClass(d
																			.toThemeProperty("jqx-menu-item-hover"))
														} else {
															c
																	.removeClass(d
																			.toThemeProperty("jqx-fill-state-hover"));
															c
																	.removeClass(d
																			.toThemeProperty("jqx-menu-item-top-hover"))
														}
													}
												})
							}
						},
						_closeAfterClick : function(c) {
							var b = c != null ? c.data : this;
							var d = false;
							if (b.autoCloseOnClick) {
								a
										.each(
												a(c.target).parents(),
												function() {
													if (this.className.indexOf) {
														if (this.className
																.indexOf("jqx-menu") != -1) {
															d = true;
															return false
														}
													}
												});
								if (!d) {
									c.data = b;
									b._closeAll(c)
								}
							}
						},
						_autoSizeHorizontalMenuItems : function() {
							var c = this;
							if (c.autoSizeMainItems
									&& this.mode == "horizontal") {
								var b = this.maxHeight;
								if (parseInt(b) > parseInt(this.host.height())) {
									b = parseInt(this.host.height())
								}
								b = parseInt(this.host.height());
								a
										.each(
												this.items,
												function() {
													var m = this;
													$element = a(m.element);
													if (m.level == 0 && b > 0) {
														var d = $element
																.children().length > 0 ? parseInt($element
																.children()
																.height())
																: $element
																		.height();
														var g = c.host
																.find("ul:first");
														var h = parseInt(g
																.css("padding-top"));
														var n = parseInt(g
																.css("margin-top"));
														var k = b - 2 * (n + h);
														var j = parseInt(k) / 2
																- d / 2;
														var e = parseInt(j);
														var l = parseInt(j);
														$element.css(
																"padding-top",
																e);
														$element
																.css(
																		"padding-bottom",
																		l);
														if (parseInt($element
																.outerHeight()) > k) {
															var f = 1;
															$element
																	.css(
																			"padding-top",
																			e
																					- f);
															e = e - f
														}
													}
												})
							}
							a.each(this.items, function() {
								var f = this;
								$element = a(f.element);
								if (f.hasItems && f.level > 0) {
									if (f.arrow) {
										var e = a(f.arrow);
										var d = a(f.element).height();
										if (d > 15) {
											e.css("margin-top", (d - 15) / 2)
										}
									}
								}
							})
						},
						_render : function(f, g) {
							if (this.disabled) {
								this.host
										.addClass(this
												.toThemeProperty("jqx-fill-state-disabled"));
								this.host.addClass(this
										.toThemeProperty("jqx-menu-disabled"))
							}
							var j = this.popupZIndex;
							var d = [ 5, 5 ];
							var h = this;
							a.data(h.element, "animationHideDelay",
									h.animationHideDelay);
							var e = this.isTouchDevice();
							var c = e
									&& (a.jqx.mobile.isWindowsPhone() || navigator.userAgent
											.indexOf("Touch") >= 0);
							var k = false;
							if (navigator.platform.toLowerCase().indexOf("win") != -1) {
								if (navigator.userAgent
										.indexOf("Windows Phone") >= 0
										|| navigator.userAgent
												.indexOf("WPDesktop") >= 0
										|| navigator.userAgent
												.indexOf("IEMobile") >= 0
										|| navigator.userAgent
												.indexOf("ZuneWP7") >= 0) {
									this.touchDevice = true
								} else {
									if (navigator.userAgent.indexOf("Touch") >= 0) {
										var b = ("MSPointerDown" in window);
										if (b
												|| a.jqx.mobile
														.isWindowsPhone()
												|| navigator.userAgent
														.indexOf("ARM") >= 0) {
											k = true;
											c = true;
											h.clickToOpen = true;
											h.autoCloseOnClick = false;
											h.enableHover = false
										}
									}
								}
							}
							a.data(document.body, "menuel", this);
							this.hasTransform = a.jqx.utilities
									.hasTransform(this.host);
							this._applyOrientation(f, g);
							if (h.enableRoundedCorners) {
								this.host.addClass(h
										.toThemeProperty("jqx-rc-all"))
							}
							a
									.each(
											this.items,
											function() {
												var s = this;
												var o = a(s.element);
												o.attr("role", "menuitem");
												if (h.enableRoundedCorners) {
													o
															.addClass(h
																	.toThemeProperty("jqx-rc-all"))
												}
												h.removeHandler(o, "click");
												h
														.addHandler(
																o,
																"click",
																function(x) {
																	if (s.disabled) {
																		return
																	}
																	if (h.disabled) {
																		return
																	}
																	h
																			._raiseEvent(
																					"2",
																					{
																						item : s.element,
																						event : x
																					});
																	if (!h.autoOpen) {
																		if (s.level > 0) {
																			if (h.autoCloseOnClick
																					&& !e
																					&& !h.clickToOpen) {
																				x.data = h;
																				h
																						._closeAll(x)
																			}
																		}
																	} else {
																		if (h.autoCloseOnClick
																				&& !e
																				&& !h.clickToOpen) {
																			if (s.closeOnClick) {
																				x.data = h;
																				h
																						._closeAll(x)
																			}
																		}
																	}
																	if (e
																			&& h.autoCloseOnClick) {
																		x.data = h;
																		if (!s.hasItems) {
																			h
																					._closeAll(x)
																		}
																	}
																	if (x.target.tagName != "A"
																			&& x.target.tagName != "a") {
																		var v = s.anchor != null ? a(s.anchor)
																				: null;
																		if (v != null
																				&& v.length > 0) {
																			var u = v
																					.attr("href");
																			var w = v
																					.attr("target");
																			if (u != null) {
																				if (w != null) {
																					window
																							.open(
																									u,
																									w)
																				} else {
																					window.location = u
																				}
																			}
																		}
																	}
																});
												h
														.removeHandler(o,
																"mouseenter");
												h
														.removeHandler(o,
																"mouseleave");
												if (!c && h.mode != "simple") {
													h._renderHover(o, s, e)
												}
												if (s.subMenuElement != null) {
													var p = a(s.subMenuElement);
													if (h.mode == "simple") {
														p.show();
														return true
													}
													p
															.wrap('<div class="jqx-menu-popup '
																	+ h
																			.toThemeProperty("jqx-menu-popup")
																	+ '" style="border: none; background-color: transparent; z-index:'
																	+ j
																	+ '; padding: 0px; margin: 0px; position: absolute; top: 0; left: 0; display: block; visibility: hidden;"><div style="background-color: transparent; border: none; position:absolute; overflow:hidden; left: 0; top: 0; right: 0; width: 100%; height: 100%;"></div></div>');
													p.css({
														overflow : "hidden",
														position : "absolute",
														left : 0,
														display : "inherit",
														top : -p.outerHeight()
													});
													p.data("timer", {});
													if (s.level > 0) {
														p.css("left", -p
																.outerWidth())
													} else {
														if (h.mode == "horizontal") {
															p.css("left", 0)
														}
													}
													j++;
													var r = a(s.subMenuElement)
															.closest(
																	"div.jqx-menu-popup")
															.css(
																	{
																		width : parseInt(a(
																				s.subMenuElement)
																				.outerWidth())
																				+ parseInt(d[0])
																				+ "px",
																		height : parseInt(a(
																				s.subMenuElement)
																				.outerHeight())
																				+ parseInt(d[1])
																				+ "px"
																	});
													var t = o
															.closest("div.jqx-menu-popup");
													if (t.length > 0) {
														var l = p
																.css("margin-left");
														var n = p
																.css("margin-right");
														var m = p
																.css("padding-left");
														var q = p
																.css("padding-right");
														r.appendTo(t);
														p.css("margin-left", l);
														p
																.css(
																		"margin-right",
																		n);
														p
																.css(
																		"padding-left",
																		m);
														p.css("padding-right",
																q)
													} else {
														var l = p
																.css("margin-left");
														var n = p
																.css("margin-right");
														var m = p
																.css("padding-left");
														var q = p
																.css("padding-right");
														r
																.appendTo(a(document.body));
														p.css("margin-left", l);
														p
																.css(
																		"margin-right",
																		n);
														p
																.css(
																		"padding-left",
																		m);
														p.css("padding-right",
																q)
													}
													if (!h.clickToOpen) {
														if (e || c) {
															h
																	.removeHandler(
																			o,
																			a.jqx.mobile
																					.getTouchEventName("touchstart"));
															h
																	.addHandler(
																			o,
																			a.jqx.mobile
																					.getTouchEventName("touchstart"),
																			function(
																					u) {
																				clearTimeout(p
																						.data("timer").hide);
																				if (p != null) {
																					p
																							.stop()
																				}
																				if (s.level == 0
																						&& !s.isOpen
																						&& h.mode != "popup") {
																					u.data = h;
																					h
																							._closeAll(u)
																				}
																				if (!s.isOpen) {
																					h
																							._openItem(
																									h,
																									s)
																				} else {
																					h
																							._closeItem(
																									h,
																									s,
																									true)
																				}
																				return false
																			})
														}
														if (!c) {
															h
																	.addHandler(
																			o,
																			"mouseenter",
																			function() {
																				if (h.autoOpen
																						|| (s.level > 0 && !h.autoOpen)) {
																					clearTimeout(p
																							.data("timer").hide)
																				}
																				if (s.parentId
																						&& s.parentId != 0) {
																					if (h.menuElements[s.parentId]) {
																						var u = h.menuElements[s.parentId].isOpen;
																						if (!u) {
																							return
																						}
																					}
																				}
																				if (h.autoOpen
																						|| (s.level > 0 && !h.autoOpen)) {
																					h
																							._openItem(
																									h,
																									s)
																				}
																				return false
																			});
															h
																	.addHandler(
																			o,
																			"mousedown",
																			function() {
																				if (!h.autoOpen
																						&& s.level == 0) {
																					clearTimeout(p
																							.data("timer").hide);
																					if (p != null) {
																						p
																								.stop()
																					}
																					if (!s.isOpen) {
																						h
																								._openItem(
																										h,
																										s)
																					} else {
																						h
																								._closeItem(
																										h,
																										s,
																										true)
																					}
																				}
																			});
															h
																	.addHandler(
																			o,
																			"mouseleave",
																			function(
																					v) {
																				if (h.autoCloseOnMouseLeave) {
																					clearTimeout(p
																							.data("timer").hide);
																					var y = a(s.subMenuElement);
																					var u = {
																						left : parseInt(v.pageX),
																						top : parseInt(v.pageY)
																					};
																					var x = {
																						left : parseInt(y
																								.coord().left),
																						top : parseInt(y
																								.coord().top),
																						width : parseInt(y
																								.outerWidth()),
																						height : parseInt(y
																								.outerHeight())
																					};
																					var w = true;
																					if (x.left - 5 <= u.left
																							&& u.left <= x.left
																									+ x.width
																									+ 5) {
																						if (x.top <= u.top
																								&& u.top <= x.top
																										+ x.height) {
																							w = false
																						}
																					}
																					if (w) {
																						h
																								._closeItem(
																										h,
																										s,
																										true)
																					}
																				}
																			});
															h
																	.removeHandler(
																			r,
																			"mouseenter");
															h
																	.addHandler(
																			r,
																			"mouseenter",
																			function() {
																				clearTimeout(p
																						.data("timer").hide)
																			});
															h
																	.removeHandler(
																			r,
																			"mouseleave");
															h
																	.addHandler(
																			r,
																			"mouseleave",
																			function(
																					u) {
																				if (h.autoCloseOnMouseLeave) {
																					clearTimeout(p
																							.data("timer").hide);
																					clearTimeout(p
																							.data("timer").show);
																					if (p != null) {
																						p
																								.stop()
																					}
																					h
																							._closeItem(
																									h,
																									s,
																									true)
																				}
																			})
														}
													} else {
														h.removeHandler(o,
																"mousedown");
														h
																.addHandler(
																		o,
																		"mousedown",
																		function(
																				u) {
																			clearTimeout(p
																					.data("timer").hide);
																			if (p != null) {
																				p
																						.stop()
																			}
																			if (s.level == 0
																					&& !s.isOpen) {
																				u.data = h;
																				h
																						._closeAll(u)
																			}
																			if (!s.isOpen) {
																				h
																						._openItem(
																								h,
																								s)
																			} else {
																				h
																						._closeItem(
																								h,
																								s,
																								true)
																			}
																		})
													}
												}
											});
							if (this.mode == "simple") {
								this._renderSimpleMode()
							}
							this._autoSizeHorizontalMenuItems();
							this._raiseEvent("3", this)
						},
						_renderSimpleMode : function() {
							this.host.show()
						},
						createID : function() {
							var b = Math.random() + "";
							b = b.replace(".", "");
							b = "99" + b;
							b = b / 1;
							while (this.items[b]) {
								b = Math.random() + "";
								b = b.replace(".", "");
								b = b / 1
							}
							return "menuItem" + b
						},
						_createMenu : function(c, f) {
							if (c == null) {
								return
							}
							if (f == undefined) {
								f = true
							}
							if (f == null) {
								f = true
							}
							var o = this;
							var t = a(c).find("li");
							var q = 0;
							for (var j = 0; j < t.length; j++) {
								var m = t[j];
								var r = a(m);
								if (m.className.indexOf("jqx-menu") == -1
										&& this.autoGenerate == false) {
									continue
								}
								var p = m.id;
								if (!p) {
									p = this.createID()
								}
								if (f) {
									m.id = p;
									this.items[q] = new a.jqx._jqxMenu.jqxMenuItem();
									this.menuElements[p] = this.items[q]
								}
								q += 1;
								var s = 0;
								var v = this;
								var h = r.children();
								h
										.each(function() {
											if (!f) {
												this.className = "";
												if (v.autoGenerate) {
													a(v.items[q - 1].subMenuElement)[0].className = "";
													if (!v.minimized) {
														a(
																v.items[q - 1].subMenuElement)
																.addClass(
																		v
																				.toThemeProperty("jqx-widget-content"))
													}
													a(
															v.items[q - 1].subMenuElement)
															.addClass(
																	v
																			.toThemeProperty("jqx-menu-dropdown"));
													a(
															v.items[q - 1].subMenuElement)
															.addClass(
																	v
																			.toThemeProperty("jqx-popup"))
												}
											}
											if (this.className
													.indexOf("jqx-menu-dropdown") != -1) {
												if (f) {
													v.items[q - 1].subMenuElement = this
												}
												return false
											} else {
												if (v.autoGenerate
														&& (this.tagName == "ul" || this.tagName == "UL")) {
													if (f) {
														v.items[q - 1].subMenuElement = this
													}
													this.className = "";
													if (!v.minimized) {
														a(this)
																.addClass(
																		v
																				.toThemeProperty("jqx-widget-content"))
													}
													a(this)
															.addClass(
																	v
																			.toThemeProperty("jqx-menu-dropdown"));
													a(this)
															.addClass(
																	v
																			.toThemeProperty("jqx-popup"));
													a(this)
															.attr("role",
																	"menu");
													if (v.rtl) {
														a(this)
																.addClass(
																		v
																				.toThemeProperty("jqx-rc-l"))
													} else {
														a(this)
																.addClass(
																		v
																				.toThemeProperty("jqx-rc-r"))
													}
													a(this)
															.addClass(
																	v
																			.toThemeProperty("jqx-rc-b"));
													return false
												}
											}
										});
								var u = r.parents();
								u
										.each(function() {
											if (this.className
													.indexOf("jqx-menu-item") != -1) {
												s = this.id;
												return false
											} else {
												if (v.autoGenerate
														&& (this.tagName == "li" || this.tagName == "LI")) {
													s = this.id;
													return false
												}
											}
										});
								var e = false;
								var d = m.getAttribute("type");
								var b = m.getAttribute("ignoretheme")
										|| m.getAttribute("data-ignoretheme");
								if (b) {
									if (b == "true" || b == true) {
										b = true
									}
								} else {
									b = false
								}
								if (!d) {
									d = m.type
								} else {
									if (d == "separator") {
										var e = true
									}
								}
								if (!e) {
									if (s) {
										d = "sub"
									} else {
										d = "top"
									}
								}
								var g = this.items[q - 1];
								if (f) {
									g.id = p;
									g.parentId = s;
									g.type = d;
									g.separator = e;
									g.element = t[j];
									var l = r.children("a");
									g.disabled = m
											.getAttribute("item-disabled") == "true" ? true
											: false;
									g.level = r.parents("li").length;
									g.anchor = l.length > 0 ? l : null
								}
								g.ignoretheme = b;
								var n = this.menuElements[s];
								if (n != null) {
									if (n.ignoretheme) {
										g.ignoretheme = n.ignoretheme;
										b = n.ignoretheme
									}
								}
								if (this.autoGenerate) {
									if (d == "separator") {
										r.removeClass();
										r
												.addClass(this
														.toThemeProperty("jqx-menu-item-separator"));
										r.attr("role", "separator")
									} else {
										if (!b) {
											r[0].className = "";
											if (this.rtl) {
												r
														.addClass(this
																.toThemeProperty("jqx-rtl"))
											}
											if (g.level > 0 && !v.minimized) {
												r
														.addClass(this
																.toThemeProperty("jqx-item"));
												r
														.addClass(this
																.toThemeProperty("jqx-menu-item"))
											} else {
												r
														.addClass(this
																.toThemeProperty("jqx-item"));
												r
														.addClass(this
																.toThemeProperty("jqx-menu-item-top"))
											}
										}
									}
								}
								if (g.disabled) {
									r
											.addClass(v
													.toThemeProperty("jqx-menu-item-disabled"));
									r
											.addClass(v
													.toThemeProperty("jqx-fill-state-disabled"))
								}
								if (f && !b) {
									g.hasItems = r.find("li").length > 0;
									if (g.hasItems) {
										if (g.element) {
											a.jqx.aria(a(g.element),
													"aria-haspopup", true);
											if (!g.subMenuElement.id) {
												g.subMenuElement.id = a.jqx.utilities
														.createId()
											}
											a.jqx.aria(a(g.element),
													"aria-owns",
													g.subMenuElement.id)
										}
									}
								}
							}
						},
						destroy : function() {
							a.jqx.utilities.resize(this.host, null, true);
							var d = this.host.closest("div.jqx-menu-wrapper");
							d.remove();
							a("#menuWrapper" + this.element.id).remove();
							var b = this;
							this.removeHandler(a(document), "mousedown.menu"
									+ this.element.id, b._closeAfterClick);
							this.removeHandler(a(document), "mouseup.menu"
									+ this.element.id, b._closeAfterClick);
							a.data(document.body, "jqxMenuOldHost"
									+ this.element.id, null);
							if (this.isTouchDevice()) {
								this.removeHandler(a(document), a.jqx.mobile
										.getTouchEventName("touchstart")
										+ ".menu" + this.element.id,
										this._closeAfterClick, this)
							}
							if (a(window).off) {
								a(window).off("resize.menu" + b.element.id)
							}
							a.each(this.items, function() {
								var g = this;
								var f = a(g.element);
								b.removeHandler(f, "click");
								b.removeHandler(f, "selectstart");
								b.removeHandler(f, "mouseenter");
								b.removeHandler(f, "mouseleave");
								b.removeHandler(f, "mousedown");
								b.removeHandler(f, "mouseleave");
								var e = a(g.subMenuElement);
								var h = e.closest("div.jqx-menu-popup");
								h.remove();
								delete this.subMenuElement;
								delete this.element
							});
							a.data(document.body, "menuel", null);
							delete this.menuElements;
							this.items = new Array();
							delete this.items;
							var c = a.data(this.element, "jqxMenu");
							if (c) {
								delete c.instance
							}
							this.host.removeClass();
							this.host.remove();
							delete this.host;
							delete this.element
						},
						_raiseEvent : function(f, c) {
							if (c == undefined) {
								c = {
									owner : null
								}
							}
							var d = this.events[f];
							args = c;
							args.owner = this;
							var e = new a.Event(d);
							if (f == "2") {
								args = c.item;
								args.owner = this;
								a.extend(e, c.event);
								e.type = "itemclick"
							}
							e.owner = this;
							e.args = args;
							var b = this.host.trigger(e);
							return b
						},
						propertyChangedHandler : function(b, d, g, f) {
							if (this.isInitialized == undefined
									|| this.isInitialized == false) {
								return
							}
							if (d == "disabled") {
								if (b.disabled) {
									b.host
											.addClass(b
													.toThemeProperty("jqx-fill-state-disabled"));
									b.host
											.addClass(b
													.toThemeProperty("jqx-menu-disabled"))
								} else {
									b.host
											.removeClass(b
													.toThemeProperty("jqx-fill-state-disabled"));
									b.host
											.removeClass(b
													.toThemeProperty("jqx-menu-disabled"))
								}
							}
							if (f == g) {
								return
							}
							if (d == "touchMode") {
								this._isTouchDevice = null;
								b._render(f, g)
							}
							if (d == "source") {
								if (b.source != null) {
									var c = b.loadItems(b.source);
									b.element.innerHTML = c;
									var e = b.host.find("ul:first");
									if (e.length > 0) {
										b.refresh();
										b._createMenu(e[0]);
										b._render()
									}
								}
							}
							if (d == "autoCloseOnClick") {
								if (f == false) {
									b.removeHandler(a(document),
											"mousedown.menu" + this.element.id,
											b._closeAll)
								} else {
									b.addHandler(a(document), "mousedown.menu"
											+ this.element.id, b, b._closeAll)
								}
							} else {
								if (d == "mode" || d == "width"
										|| d == "height"
										|| d == "showTopLevelArrows") {
									b.refresh();
									if (d == "mode") {
										b._render(f, g)
									} else {
										b._applyOrientation()
									}
								} else {
									if (d == "theme") {
										a.jqx.utilities.setTheme(g, f, b.host)
									}
								}
							}
						}
					})
})(jqxBaseFramework);
(function(a) {
	a.jqx._jqxMenu.jqxMenuItem = function(e, d, c) {
		var b = {
			id : e,
			parentId : d,
			parentItem : null,
			anchor : null,
			type : c,
			disabled : false,
			level : 0,
			isOpen : false,
			hasItems : false,
			element : null,
			subMenuElement : null,
			arrow : null,
			openHorizontalDirection : "right",
			openVerticalDirection : "down",
			closeOnClick : true
		};
		return b
	}
})(jqxBaseFramework);