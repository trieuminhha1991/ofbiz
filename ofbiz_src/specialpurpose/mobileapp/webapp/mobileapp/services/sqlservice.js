// olbius.service("SqlService", function($http, $location, $cordovaSQLite , $window) {
olbius.service("SqlService", function($http, $location, $window, $q) {
		// if($cordovaSQLite && $window.sqlitePlugin !== undefined){
			// this.db = $cordovaSQLite.openDB({
					// name : "olbius.delys",
					// bgType : 1
				// });
		// }else {
	var self = this;
	self.db = $window.openDatabase("olbius.delys","1.0","Sqlite",200000);
		// }
	this.query = function(query, bindings) {
        bindings = typeof bindings !== 'undefined' ? bindings : [];
        var deferred = $q.defer();

        self.db.transaction(function(transaction) {
            transaction.executeSql(query, bindings, function(transaction, result) {
                deferred.resolve(result);
            }, function(transaction, error) {
                deferred.reject(error);
            });
        });

        return deferred.promise;
	};
	this.getTable = function(name) {
		return $http.get('data/' + name + '.json').then(function(res) {
			return res.data;
		}, function(res) {
			console.log("cannot get table fields " + name + JSON.stringify(res));
		});
	};

	this.dropTable = function(name) {
		var query = 'DROP TABLE IF EXISTS ' + name;
		return self.query(query);
	};

	this.createTable = function(name, fields) {
		// for opening a background db:
		var query = "CREATE TABLE IF NOT EXISTS " + name + " (";
		var end = fields.length - 1;
		for (var x in fields) {
			query += fields[x].name + " " + fields[x].type;
			if (fields[x].primary) {
				query += " primary key";
			}
			if(fields[x].autoincrement){
				query +=" autoincrement";
			}
			if (x != end) {
				query += ",";
			}
		}
		query += ")";
		return self.query(query);
	};
	this.insert = function(table, fields, data) {
		var fieldsCl = "";
		var values = "";
		var ef = fields.length - 1;
		for (var k in fields) {
			fieldsCl += fields[k];
			if (k != ef) {
				fieldsCl += ",";
			}
		}
		var sql = "INSERT INTO " + table + "(" + fieldsCl + ") VALUES ";
		var end = data.length;
		if (data.length && end) {
			i = 1;
			for (var x in data) {
				values = " (";
				var cur = data[x];
				for (var y = 0; y <= ef; y++) {
					if (cur[y]) {
						values += "'" + cur[y] + "'";
					} else {
						values += "NULL";
					}

					if (ef != y) {
						values += ",";
					}
				}
				values += ") ";
				if (i != end) {
					values += ",";
				}
				i++;
				sql += values;
			}
			return self.query(query);
		}
	};
	this.select = function(table, join, where, fields, order, groupby, having) {
		var query = " SELECT ";
		var from = " FROM ";
		var fieldsCl = " ";
		var whereCl = " WHERE ";
		var orderCl = " ";
		var groupbyCl = " ";
		var havingCl = " ";
		var joinCl = " ";
		if (!fields) {
			fieldsCl += table + ".*";
		} else {
			fieldsCl = fields;
		}
		if (join) {
			joinCl += join.type + " " + join.table + " ON " + join.condition;
		}
		if (where) {
			whereCl += where;
		} else {
			whereCl += "1=1";
		}
		if (order) {
			orderCl = " ORDER BY " + order;
		} else {
			orderCl = " ";
		}
		if (groupby) {
			groupbyCl = " GROUP BY " + groupby;
		} else {
			groupbyCl = " ";
		}
		if (having) {
			havingCl = " HAVING " + having;
		} else {
			havingCl = " ";
		}
		query += fieldsCl + " " + from + " " + table + " " + joinCl + " " + whereCl + " " + groupbyCl + " " + orderCl + " " + havingCl;
		return self.query(query).then(function(data) {
			var res = Array();
			for (var i = 0; i < data.rows.length; i++) {
				var result = data.rows.item(i);
				res.push(result);
			}
			return res;
		});
	};
	this.deleteRow = function(table, where) {
		var sql = "DELETE FROM " + table;
		if (where) {
			sql += " WHERE " + where;
		}
		return self.query(query);
	};
	this.deleteAll = function(table) {
		var sql = "DELETE FROM " + table;
		return self.query(query);
	};
	this.closeDB = function() {
		self.db.close();
	};
});
