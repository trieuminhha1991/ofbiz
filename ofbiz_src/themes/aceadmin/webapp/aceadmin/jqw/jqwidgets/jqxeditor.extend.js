(function(b) {
	b.extend(b.jqx._jqxEditor.prototype, {
		createInstance: function(f) {
            var j = this;
            j.textArea = j.host;
            var d = j.host.attr("contenteditable");
            j.host.addClass(j.toThemeProperty("jqx-widget"));
            if (d == true || d == "true") {
                j.inline = true;
                j.widget = j.host;
                j.editorDocument = document;
                j.selection = new a(j.editorDocument);
                var i = b("<div class='jqx-editor-toolbar-container' unselectable='on' aria-label='Formatting options' role='toolbar'><div class='jqx-editor-toolbar'></div>");
                i.insertBefore(j.host);
                j.toolbarContainer = i;
                j.toolbar = i.find(".jqx-editor-toolbar");
                j.editor = j.host;
                j.contentEditableElement = j.element
            } else {
                var g = b("<div class='jqx-editor'><div class='jqx-editor-container'><div class='jqx-editor-toolbar-container' aria-label='Formatting options' role='toolbar'><div class='jqx-editor-toolbar'></div></div><div class='jqx-editor-content'><iframe  src='javascript:\"<html></html>\"' allowtransparency='true' frameborder='0'></iframe></div></div></div>");
                j.widget = g;
                j.widget[0].className = j.widget[0].className + " " + j.element.className;
                try {
                    j.widget[0].style = j.element.style
                } catch (e) {}
                //var h = b.trim(j.host.html()) + "&#8203;"; //TODOCHANGE
                var h = b.trim(j.host.html());
                if (j.lineBreak == "default" || j.lineBreak == "div") {
                    h = "<div>" + h + "</div>"
                } else {
                    if (j.lineBreak == "p") {
                        h = "<p>" + h + "</p>"
                    }
                }
                h = h.replace(/&lt;/ig, "<");
                h = h.replace(/&gt;/ig, ">");
                j.host.css("display", "none");
                j.host.after(g);
                g.find("iframe").after(j.host);
                j.container = g.find(".jqx-editor-container");
                j.toolbarContainer = g.find(".jqx-editor-toolbar-container");
                j.toolbar = g.find(".jqx-editor-toolbar");
                j.iframe = g.find("iframe");
                j.content = g.find(".jqx-editor-content");
                var k = function() {
                    j.editorDocument = j.iframe[0].contentWindow.document;
                    j.selection = new a(j.editorDocument);
                    var o = 0;
                    j.addHandler(j.iframe, "load", function() {
                        o++;
                        if (o > 1) {
                            j.iframe.off("load");
                            j.content.find("iframe").remove();
                            var q = b("<iframe  src='javascript:\"<html></html>\"' allowtransparency='true' frameborder='0'></iframe>").appendTo(j.content);
                            j.iframe = g.find("iframe");
                            k()
                        }
                    });
                    if (!b.jqx.browser.mozilla) {
                        j.editorDocument.designMode = "On"
                    }
                    j.editorDocument.open();
                    var m = j.rtl ? "direction:rtl;" : "";
                    var l = b.jqx.browser.msie ? "::selection{color: #fff; background: #328EFD;};::-moz-selection{color: #fff; background: #328eD;};::selection:window-inactive {background: #c7c7c7; color: #000;}::-moz-selection:window-inactive {background: #c7c7c7; color: #000;}html{font-size:13px; height:100%;}body{padding-top:1px;margin-top:-1px; padding-right: 1px; overflow-x: hidden;word-wrap: break-word;-webkit-nbsp-mode: space;-webkit-line-break: after-white-space;" : "";
                    j.editorDocument.write("<!DOCTYPE html><html><head><meta charset='utf-8' /><style>html,body{padding:0; margin:0; font-size: 13px; font-family: " + j.fontFamily + "; background:#fff; min-height:100%; " + m + "}" + l + "}h1{font-size:2em;margin:.67em 0}h2{font-size: 1.5em; margin: .75em 0}h3{font-size: 1.17em; margin: .83em 0}h4{font-size:1em; margin: 1.12em 0 }h5{font-size: .83em; margin: 1.5em 0}h6{font-size: .75em; margin: 1.67em 0}p{margin: 0px;padding:0 .2em}ul,ol{padding-left:2.5em}a{color:#00a}code{font-size:1.23em}.jqx-editor-paste-element {position: absolute; left: -1000px; height: 1px; overflow: hidden; top: -1000px;}.jqx-editor-focus {border: 1px solid #aaa !important;}</style>" + b.map(j.stylesheets, function(q) {
                        return "<link rel='stylesheet' href='" + q + "'>"
                    }).join("") + "</head><body autocorrect='off' contenteditable='true'></body></html>");
                    j.contentEditableElement = j.editorDocument.body;
                    if (j.host.is("textarea")) {
                        j._textArea = j.element;
                        var n = j.host.data();
                        n.jqxEditor.host = g;
                        n.jqxEditor.element = g[0];
                        j.element = g[0];
                        g[0].id = j._textArea.id;
                        j._textArea.id = j._textArea.id + "TextArea";
                        b(j.element).addClass(j.toThemeProperty("jqx-widget"));
                        j.host = b(j.element);
                        j.host.data(n)
                    } else {
                        var n = j.host.data();
                        n.jqxEditor.host = g;
                        n.jqxEditor.element = g[0];
                        j.element = g[0];
                        j.host = b(j.element);
                        j.host.data(n)
                    }
                    try {
                        j.editorDocument.execCommand("useCSS", false, false);
                        j.editorDocument.execCommand("enableInlineTableEditing", null , false)
                    } catch (p) {}
                    try {
                        j.editorDocument.execCommand("styleWithCSS", 0, true)
                    } catch (p) {}
                    j.editorDocument.close();
                    j.editor = b(j.editorDocument.body);
                    j.editor.html(h).attr("spellcheck", false).attr("autocorrect", "off")
                }
                ;
                k();
                b.jqx.utilities.resize(j.host, function() {
                    j.widget.css("width", j.width);
                    j.widget.css("height", j.height);
                    j._arrange()
                })
            }
        },
	});
	var a = function(d) {
        var e = {
            initialize: function(f) {
                this.document = f
            },
            getSelection: function() {
                return (this.document.getSelection) ? this.document.getSelection() : this.document.selection
            },
            getRange: function() {
                var f = this.getSelection();
                if (!f) {
                    return null
                }
                try {
                    return f.rangeCount > 0 ? f.getRangeAt(0) : (this.document.createRange ? this.document.createRange() : this.document.selection.createRange())
                } catch (g) {
                    return this.document.body.createTextRange()
                }
            },
            selectRange: function(f) {
                if (f.select) {
                    f.select()
                } else {
                    var g = this.getSelection();
                    if (g.addRange) {
                        g.removeAllRanges();
                        g.addRange(f)
                    }
                }
            },
            selectNode: function(i, j) {
                var h = this.getRange();
                var g = this.getSelection();
                if (h.moveToElementText) {
                    h.moveToElementText(i);
                    h.select()
                } else {
                    if (g.addRange) {
                        try {
                            j ? h.selectNodeContents(i) : h.selectNode(i);
                            g.removeAllRanges();
                            g.addRange(h)
                        } catch (f) {
                            var k = f
                        }
                    } else {
                        g.setBaseAndExtent(i, 0, i, 1)
                    }
                }
                return i
            },
            isCollapsed: function() {
                var f = this.getRange();
                if (f.item) {
                    return false
                }
                return f.boundingWidth == 0 || this.getSelection().isCollapsed
            },
            collapse: function(f) {
                var h = this.getRange();
                var g = this.getSelection();
                if (h.select) {
                    h.collapse(f);
                    h.select()
                } else {
                    f ? g.collapseToStart() : g.collapseToEnd()
                }
            },
            getContent: function() {
                var h = this.getRange();
                var f = b("<div>")[0];
                if (this.isCollapsed()) {
                    return ""
                }
                if (h.cloneContents) {
                    f.appendChild(h.cloneContents())
                } else {
                    if (h.item != undefined || h.htmlText != undefined) {
                        b(f).html(h.item ? h.item(0).outerHTML : h.htmlText)
                    } else {
                        b(f).html(h.toString())
                    }
                }
                var g = b(f).html();
                return g
            },
            getText: function() {
                var g = this.getRange();
                var f = this.getSelection();
                return this.isCollapsed() ? "" : g.text || (f.toString ? f.toString() : "")
            },
            getNode: function() {
                var g = this.getRange();
                if (!b.jqx.browser.msie || b.jqx.browser.version >= 9) {
                    var f = null ;
                    if (g) {
                        f = g.commonAncestorContainer;
                        if (!g.collapsed) {
                            if (g.startContainer == g.endContainer) {
                                if (g.startOffset - g.endOffset < 2) {
                                    if (g.startContainer.hasChildNodes()) {
                                        f = g.startContainer.childNodes[g.startOffset]
                                    }
                                }
                            }
                        }
                        while (typeof (f) != "element") {
                            f = f.parentNode
                        }
                    }
                    return d.id(f)
                }
                return d.id(g.item ? g.item(0) : g.parentElement())
            },
            insertContent: function(i) {
                var h = this.getRange();
                if (h.pasteHTML) {
                    h.pasteHTML(i);
                    h.collapse(false);
                    h.select()
                } else {
                    if (h.insertNode) {
                        h.deleteContents();
                        if (h.createContextualFragment) {
                            h.insertNode(h.createContextualFragment(i))
                        } else {
                            var j = this.document;
                            var g = j.createDocumentFragment();
                            var f = j.createElement("div");
                            g.appendChild(f);
                            f.outerHTML = i;
                            h.insertNode(g)
                        }
                    }
                }
            }
        };
        e.initialize(d);
        return e
    }
})(jqxBaseFramework);
