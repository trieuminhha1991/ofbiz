<div class='step-container' id="step3">
    <div class="success">
      <i class="iconcart-ss"></i>
      <label>Đặt mua thành công</label>
    </div>
    <div class="orderdetails">
      <img src="/dpc/DemoImg/thanks.png" alt="" width="800" height="225">
      <div class="thanks">
        <h3>Cảm ơn anh ${(profile.fullName)?if_exists} đã cho chúng tôi cơ hội phục vụ</h3>
        <p><b>Thông tin đặt hàng</b></p>
        <p>1. Mã đơn hàng <b>${request.getAttribute("orderId")?if_exists}</b> (<a href="#">chi tiết</a>) </p>
        <p>2. Địa chỉ nhận hàng: <b>${(profile.address)?if_exists}, ${(profile.districtGeoName)?if_exists}, ${(profile.cityGeoName)?if_exists}</b></p>
        <p>3. Thời gian nhận hàng dự kiến: <b> ${request.getAttribute("shipBeforeDate")?if_exists} </b></p>
        <p>4. <b> ${uiLabelMap[request.getAttribute("checkOutPaymentId")?if_exists]}</b></p>
        <br>
        <p>Chúng tôi sẽ gọi điện xác nhận đơn hàng trong 15 phút. Trước khi giao nhân viên sẽ gọi cho anh Hùng để xác nhận</p>
        <br>
        <p>Khi cần trợ giúp vui lòng gọi <b>1800.1666</b> (7h30 - 19h00)</p>
      </div>
      <a class="gohome" href="<@ofbizUrl>main</@ofbizUrl>" title="Trang chủ">&lt;&lt; Quay lại trang chủ</a>
    </div>
</div>
