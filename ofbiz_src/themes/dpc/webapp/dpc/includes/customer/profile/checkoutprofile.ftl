<form action="<@ofbizUrl>createCustomerProfileCheckout</@ofbizUrl>" method="POST" id="confirmcart">
	<input type="hidden" name="productStoreId" value="${productStore.productStoreId}"/>
	<input type="hidden" name="shipBeforeDate"/>
	<div class="choosesex" id="choosesex">
		<span>1. Thông tin người mua:</span>
		<div class="choosegender" id="choosegender">
			<div class="Male">
				<input id="Radio1" type="radio" name="gender" value="M" <#if (profile.gender)?exists && profile.gender == "M" >checked</#if>>
				<label>Anh</label>
			</div>
			<div class="Female">
				<input id="Radio2" type="radio" name="gender" value="F" <#if (profile.gender)?exists && profile.gender == "F" >checked</#if>>
				<label>Chị</label>
			</div>
		</div>
		<div class="infochoose">
			<div class="form_male">
				<input type="hidden" name="partyId" maxlength="50" placeholder="Họ tên của bạn (Bắt buộc)" class="form-control" id="partyId" value="${(userLogin.partyId)?if_exists}"/>
				<input type="text" name="name" maxlength="50" placeholder="Họ tên của bạn (Bắt buộc)" class="form-control" id="name" value="${(profile.fullName)?if_exists}"/>
				<input type="text" name="phone" maxlength="11" class="form-control" placeholder="Số di động (Bắt buộc)" id="phone" value="${(profile.phoneNumber)?if_exists}"/>
				<input type="text" name="email" placeholder="Email (Để nhận thông tin đặt hàng)" class="form-control" id="email" value="${(profile.email)?if_exists}"/>
			</div>
		</div>

		<div class="infochoose">
			<span>2. Địa chỉ giao hàng</span>
			<div class="location">
				<select class="selectcity" name="city" id="city">
					<#if province?exists>
						<#list province as city>
						<option <#if (profile.city)?exists && profile.city == city.geoId>selected<#elseif city.geoId=="VN-HN2">selected</#if>
							value="${city.geoId}">${city.geoName}</option>
						</#list>
					</#if>
				</select>
				<select class="selectdist" name="district" id="district">
					<#if defaultDistrict?exists>
					<#list defaultDistrict as district>
					<option <#if (profile.district)?exists && profile.district == district.geoId>selected</#if> value="${district.geoId}">${district.geoName}</option>
					</#list>
					</#if>
				</select>
				<input type="text" class="form-control" placeholder="Số nhà và đường phố" name="address" id="address" value="${(profile.address)?if_exists}">
				<!-- phần hẹn thời gian nhận hàng chỉ có tại Hà Nội và Hồ Chí Minh -->
				<span>Bạn muốn nhận hàng khi nào</span>
				<select class="selectdate" id="date">
					<#if days?exists>
						<#list days as day>
							<option value="${day.key}">${day.day}</option>
						</#list>
					</#if>
				</select>
				<select class="selectbetween" id="hour">
					<option value="10">Trước 10 giờ</option>
					<option value="11">Trước 11 giờ</option>
					<option value="12">Trước 12 giờ</option>
					<option value="13">Trước 13 giờ</option>
					<option value="14">Trước 14 giờ</option>
					<option value="15">Trước 15 giờ</option>
					<option value="16">Trước 16 giờ</option>
					<option value="17">Trước 17 giờ</option>
					<option value="18">Trước 18 giờ</option>
					<option value="19">Trước 19 giờ</option>
					<option value="20">Trước 20 giờ</option>
					<option value="21">Trước 21 giờ</option>
				</select>
			</div>
			<div class="clearfix"></div>
			<button type="submit" id="SubmitOrder">
				Đặt hàng &gt;&gt;
			</button>
		</div>
	</div>
</form>