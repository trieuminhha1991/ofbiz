if (typeof (ProductInfo) == "undefined") {
	var ProductInfo = (function() {
		var pharmaFeatureId = new Object();

		var initJqxElements = function() {
			$("#txtEffects").jqxEditor({
			    theme: "olbiuseditor",
			    width: "100%",
			    height: 150
			});
			$("#txtComposition").jqxEditor({
				theme: "olbiuseditor",
				width: "100%",
				height: 150
			});
			$("#txtShelfLife").jqxEditor({
				theme: "olbiuseditor",
				width: "100%",
				height: 150
			});
			$("#txtUsers").jqxEditor({
				theme: "olbiuseditor",
				width: "100%",
				height: 150
			});
			$("#txtInstructions").jqxEditor({
				theme: "olbiuseditor",
				width: "100%",
				height: 150
			});
			$("#txtLicense").jqxEditor({
				theme: "olbiuseditor",
				width: "100%",
				height: 150
			});
			$("#txtPacking").jqxEditor({
				theme: "olbiuseditor",
				width: "100%",
				height: 150
			});
			$("#txtContraindications").jqxEditor({
				theme: "olbiuseditor",
				width: "100%",
				height: 150
			});
		};
		var getValue = function() {
			var value = new Object();
			value.effects = $("#txtEffects").jqxEditor('val');
			value.composition = $("#txtComposition").jqxEditor('val');
			value.shelfLife = $("#txtShelfLife").jqxEditor('val');
			value.users = $("#txtUsers").jqxEditor('val');
			value.instructions = $("#txtInstructions").jqxEditor('val');
			value.license = $("#txtLicense").jqxEditor('val');
			value.packing = $("#txtPacking").jqxEditor('val');
			value.contraindications = $("#txtContraindications").jqxEditor('val');
			var value = _.extend(value, pharmaFeatureId);
			return value;
		};
		var setValue = function(data) {
			$("#txtEffects").jqxEditor('val', data.effects);
			$("#txtComposition").jqxEditor('val', data.composition);
			$("#txtShelfLife").jqxEditor('val', data.shelfLife);
			$("#txtUsers").jqxEditor('val', data.users);
			$("#txtInstructions").jqxEditor('val', data.instructions);
			$("#txtLicense").jqxEditor('val', data.license);
			$("#txtPacking").jqxEditor('val', data.packing);
			$("#txtContraindications").jqxEditor('val', data.contraindications);

			pharmaFeatureId = new Object();
			pharmaFeatureId.productId = data.productId;
			pharmaFeatureId.effectsId = data.effectsId;
			pharmaFeatureId.compositionId = data.compositionId;
			pharmaFeatureId.shelfLifeId = data.shelfLifeId;
			pharmaFeatureId.usersId = data.usersId;
			pharmaFeatureId.instructionsId = data.instructionsId;
			pharmaFeatureId.licenseId = data.licenseId;
			pharmaFeatureId.packingId = data.packingId;
			pharmaFeatureId.contraindicationsId = data.contraindicationsId;
		};
		return {
			init: function() {
				initJqxElements();
			},
			getValue: getValue,
			setValue: setValue
		}
	})();
}