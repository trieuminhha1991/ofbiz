(function(c, b, a, d) {
	c.fn.imageBlob = function(e) {
		this.blob = function() {
			var n = f(this);
			if (!n) {
				return null
			}
			return g(n)
		};
		this.formData = function(p) {
			if ( typeof p == "object") {
				var o = new FormData();
				for (var n in p) {
					o.append(n, p[n])
				}
				h = o
			}
			return this
		};
		this.ajax = function(p, q) {
			var o = this.blob();
			if (!o) {
				return null
			}
			if ( typeof p == "object") {
				q = p;
				p = d
			}
			q = q || {};
			var r = c.extend({}, c.fn.imageBlob.ajaxSettings, q);
			var n = i(this);
			if ( typeof h == "undefined") {
				h = new FormData()
			}
			h.append(n, o, n);
			r.data = h;
			if ( typeof p == "string") {
				return c.ajax(p, r)
			}
			return c.ajax(r)
		};
		var h;
		var j = /data:(image\/[^;]+);base64,(.+)/;
		var k = /.*\.jpe?g/g;
		function f(n) {
			if (n.length == 0 || "IMG" != (n.prop("tagName"))) {
				return null
			}
			return n.get(0)
		}

		function i(o) {
			var n = o.attr("name");
			if ( typeof n == "undefined") {
				n = c.fn.imageBlob.defaultImageName
			}
			return n
		}

		function g(n) {
			var o = m(n);
			return l(o[1], o[2])
		}

		function m(o) {
			var r = c(o).attr("src");
			r = r.replace(/\s/g, "");
			var q = r.match(j);
			if (q == null) {
				if ( typeof e != "string") {
					if (r.match(k) != null) {
						e = "image/jpeg"
					} else {
						e = "image/png"
					}
				}
				var p = a.createElement("canvas");
				var n = p.getContext("2d");
				p.width = o.width;
				p.height = o.height;
				n.drawImage(o, 0, 0);
				r = p.toDataURL(e);
				q = r.match(j)
			}
			return q
		}

		function l(r, n) {
			var o = atob(n);
			var q = [];
			for (var p = 0; p < o.length; p++) {
				q.push(o.charCodeAt(p))
			}
			return new Blob([new Uint8Array(q)], {
				type : r
			})
		}
		return this
	};
	c.fn.imageBlob.ajaxSettings = c.extend({}, c.ajaxSettings, {
		cache : false,
		processData : false,
		contentType : false,
		type : "POST"
	});
	c.fn.imageBlob.defaultImageName = "IMG_Upload"
})(jQuery, window, document);