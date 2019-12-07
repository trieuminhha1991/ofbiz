<select name="partyId">
	<#list listCustomer as customer>
		<option value="${customer.partyIdTo}">${customer.groupName?if_exists}</option>
	</#list>
</select>