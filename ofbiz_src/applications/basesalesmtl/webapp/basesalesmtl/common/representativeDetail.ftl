<#-- TODO deleted -->
<script src="/salesmtlresources/js/common/representativeDetail.js"></script>

<h3>${uiLabelMap.BERepresentative}</h3>
<div class="row-fluid">
	<div class="row-fluid">
		<div class="span6" style="padding-left: 15px;">
			<div class="row-fluid">
				<div class="div-inline-block">
					<label>${uiLabelMap.FullName}:</label>
				</div>
				<div class="div-inline-block">
					<span id="txtRFullName"></span>
				</div>
			</div>
			<div class="row-fluid">
				<div class="div-inline-block">
					<label>${uiLabelMap.DmsPartyBirthDate}:</label>
				</div>
				<div class="div-inline-block">
					<span id="txtRBirthDate"></span>
				</div>
			</div>
			<div class="row-fluid">
				<div class="div-inline-block">
					<label>${uiLabelMap.DmsCountry}:</label>
				</div>
				<div class="div-inline-block">
					<span id="txtRCountry"></span>
				</div>
			</div>
			<div class="row-fluid">
				<div class="div-inline-block">
					<label>${uiLabelMap.DmsCounty}:</label>
				</div>
				<div class="div-inline-block">
					<span id="txtRCounty"></span>
				</div>
			</div>
			<div class="row-fluid">
				<div class="div-inline-block">
					<label>${uiLabelMap.DmsAddress1}:</label>
				</div>
				<div class="div-inline-block">
					<span id="tarRAddress"></span>
				</div>
			</div>
		</div>
		<div class="span6" style="padding-left: 50px;">
			<div class="row-fluid">
				<div class="div-inline-block">
					<label>${uiLabelMap.DmsPartyGender}:</label>
				</div>
				<div class="div-inline-block">
					<span id="txtRGender"></span>
				</div>
			</div>
			<div class="row-fluid">
				<div class="div-inline-block">
					<label>${uiLabelMap.PhoneNumber}:</label>
				</div>
				<div class="div-inline-block">
					<span id="txtRPhoneNumber"></span>
				</div>
			</div>
			<div class="row-fluid">
				<div class="div-inline-block">
					<label>${uiLabelMap.DmsProvince}:</label>
				</div>
				<div class="div-inline-block">
					<span id="txtRProvince"></span>
				</div>
			</div>
			<div class="row-fluid">
				<div class="div-inline-block">
					<label>${uiLabelMap.DmsWard}:</label>
				</div>
				<div class="div-inline-block">
					<span id="txtRWard"></span>
				</div>
			</div>
			<div class="row-fluid">
				<div class="div-inline-block">
					<label>${uiLabelMap.EmailAddress}:</label>
				</div>
				<div class="div-inline-block">
					<span id="txtREmailAddress"></span>
				</div>
			</div>
		</div>
	</div>
</div>

<script>
var mapGender = {
		M: '${StringUtil.wrapString(uiLabelMap.DmsMale)}',
		F: '${StringUtil.wrapString(uiLabelMap.DmsFemale)}'
	};
</script>