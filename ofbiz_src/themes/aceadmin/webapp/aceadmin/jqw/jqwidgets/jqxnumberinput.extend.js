(function(a) {
	a.extend(a.jqx._jqxNumberInput.prototype, {
		_insertKey : function(l) {
			this.numberInput[0].focus();
			var d = String.fromCharCode(l);
			var e = parseInt(d);
			if (isNaN(e)) {
				return
			}
			var q = 0;
			for ( i = 0; i < this.items.length; i++) {
				if (this.items[i].character.length == 0) {
					q++
				}
			}
			var g = this._selection();
			var b = this;
			if (g.start >= 0 && g.start <= this.items.length) {
				var f = false;
				var h = this._getFirstVisibleNonEmptyIndex();
				if (g.start < h && g.length == 0) {
					if (!isNaN(d) || d == " ") {
						this._setSelectionStart(h);
						g = this._selection()
					}
				}
				var c = this._getFirstEditableItemIndex();
				var o = this._getLastEditableItemIndex();
				var n = this._getVisibleItems();
				//olbius fix bug keypress when value null
				var end = n.length - 1;
				if (o == -1) {
					o = end;
				}
				a.each(n, function(x, B) {
					if (g.start > x && x != end) {
						return
					}
					var E = n[x];
					if (x > o) {
						E = n[o]
					}
					if (isNaN(d) || d == " ") {
						return
					}
					if (!E.canEdit) {
						return
					}
					var A = b._getSeparatorPosition();
					if (b._match(d, E.regex)) {
						if (!f && g.length > 0) {
							for ( j = g.start + q; j < g.end + q; j++) {
								if (b.items[j].canEdit) {
									if (j > A) {
										b.items[j].character = "0"
									} else {
										b.items[j].character = b.promptChar
									}
								}
							}
							var D = b._getString();
							f = true
						}
						var A = b._getSeparatorPosition();
						var y = b._hasEmptyVisibleItems();
						if (b.decimal == null) {
							g.start = A - 1;
							if (g.start < 0) {
								g.start = 0
							}
							g.end = g.start
						}
						if (g.start <= A && y) {
							var v = x;
							if (b.decimalSeparatorPosition == -1 && g.start == A) {
								v = x + 1
							}
							if (b.decimal == null) {
								v = g.start
							}
							var u = "";
							for ( p = 0; p < v; p++) {
								if (n[p].canEdit && n[p].character != b.promptChar) {
									u += n[p].character
								}
							}
							u += d;
							var w = b.decimal < 1 ? 1 : 0;
							if (g.start == A && b.decimalSeparatorPosition != -1) {
								u += b.decimalSeparator;
								w = 0
							}
							for ( p = v + w; p < n.length; p++) {
								if (n[p].character == b.decimalSeparator && n[p].isSeparator) {
									u += n[p].character
								} else {
									if (n[p].canEdit && n[p].character != b.promptChar) {
										u += n[p].character
									}
								}
							}
							if (b.decimalSeparator != ".") {
								u = b._parseDecimalValue(u)
							}
							u = parseFloat(u).toString();
							u = new Number(u);
							u = u.toFixed(b.decimalDigits);
							if (b.decimalSeparator != ".") {
								u = b._parseDecimalValueToEditorValue(u)
							}
							b.setvalue("decimal", u);
							var D = b._getString();
							if (g.end < A) {
								b._setSelectionStart(g.end + w)
							} else {
								b._setSelectionStart(g.end)
							}
							if (g.length >= 1) {
								b._setSelectionStart(g.end)
							}
							if (g.length == b.numberInput.val().length) {
								var r = b._moveCaretToDecimalSeparator();
								var C = b.decimalSeparatorPosition >= 0 ? 1 : 0;
								b._setSelectionStart(r - C)
							}
						} else {
							if (g.start < A || g.start > A) {
								if (b.numberInput.val().length == g.start && b.decimalSeparatorPosition != -1) {
									return false
								} else {
									if (b.numberInput.val().length == g.start && b.decimalSeparatorPosition == -1 && !y) {
										return false
									}
								}
								var u = "";
								var s = false;
								for ( p = 0; p < x; p++) {
									if (n[p].canEdit && n[p].character != b.promptChar) {
										u += n[p].character
									}
									if (n[p].character == b.decimalSeparator && n[p].isSeparator) {
										u += n[p].character;
										s = true
									}
								}
								u += d;
								var w = b.decimal < 1 ? 1 : 0;
								if (!s && g.start == A - 1) {
									u += b.decimalSeparator;
									s = true
								}
								for ( p = x + 1; p < n.length; p++) {
									if (!s && n[p].character == b.decimalSeparator && n[p].isSeparator) {
										u += n[p].character
									} else {
										if (n[p].canEdit && n[p].character != b.promptChar) {
											u += n[p].character
										}
									}
								}
								b.setvalue("decimal", u);
								var D = b._getString();
								if (b.decimalSeparatorPosition < 0 && E == n[o]) {
									b._setSelectionStart(x);
									return false
								}
								var z = D.indexOf(b.symbol);
								var t = !b.getvalue("negative") ? 0 : 1;
								if (z <= t) {
									z = D.length
								}
								if (g.start < z) {
									b._setSelectionStart(x + 1)
								} else {
									b._setSelectionStart(x)
								}
								if (g.length >= 1) {
								}
								if (g.length == b.numberInput.val().length) {
									var r = b._moveCaretToDecimalSeparator();
									b._setSelectionStart(r - 1)
								}
							}
						}
						return false
					}
				});
			}
		},
	});
})(jqxBaseFramework);
