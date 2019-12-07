<script type="text/javascript" src="/dpc/js/faq/faq.js"></script>
<script src="/crmresources/js/DataAccess.js"></script>

<#assign listContentType = delegator.findList("ContentType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", "FAQ_ROOT"), null, null, null, false) />

<div class="question">
<div class="send-cmt">
        <form method="post" ><div class="message-error"></div>
        <div class="box-cmt">
        <textarea id="description" rows="2" placeholder="Mời bạn đặt câu hỏi, vui lòng gõ tiếng Việt có dấu.." cols="20" class="txtCmt font-arial"></textarea>
        <div class="block-form-user form-group">
        <div class="col-lg-3">
            <select id="contentType">
		<#if listContentType?exists>
			<#list listContentType as contentType>
				<option value="${(contentType.contentTypeId)?if_exists}">${StringUtil.wrapString(contentType.get("description", locale)?if_exists)}</option>
			</#list>
			</#if>
            </select>
        </div>
        <div class="col-lg-3">
        <input type="text" id="author" placeholder="Nhập tên" class="font-arial form-control">
        </div>
        <div class="col-lg-4">
        <input type="text" id="email" placeholder="Nhập mail" class="font-arial form-control">
        </div>
        <div class="col-lg-2"><input type="button" value="Gửi câu hỏi" class="btn btn-ask font-arial" id="add-comment"></div>
        </div>
        </div>
        </form>
</div>

    <div class="faq-tabs">
    <ul id="myTabs" class="nav-tabs" role="tablist">
      <li  class="active"><a href="#new" id="new-tab" role="tab" data-toggle="tab" aria-controls="new" aria-expanded="true">Mới nhất</a></li>
      <li class=""><a href="#hot" role="tab" id="hot-tab" data-toggle="tab" aria-controls="hot" aria-expanded="false">Được quan tâm nhất</a></li>
    </ul>
    <div id="myTabContent" class="tab-content">
      <div role="tabpanel" class="tab-pane fade active in" id="new" aria-labelledby="new-tab">
        <ul class="listask">

	        <#assign resultValue = dispatcher.runSync("getListAsk", Static["org.ofbiz.base.util.UtilMisc"].toMap("sortBy", "date", "contentTypeId", parameters.contentTypeId))/>
		<#if Static["org.ofbiz.service.ServiceUtil"].isSuccess(resultValue)>
			<#assign listAsk = resultValue.get("listAsk") />
		</#if>

		<#if listAsk?exists>
				<#list listAsk as ask>
					<li>
						<#assign shortcut = ask.author?substring(0, 1) />
						<div class="icon-shortcut-larger linkimg">${shortcut}</div>
		                <a class="linktitle" href="<@ofbizUrl>ViewFAQ?cId=${(ask.contentId)?if_exists}</@ofbizUrl>" title=""><h3>${(ask.longDescription)?if_exists}</h3></a>
		                <span class="infomore">• 1 xem</span>
		                <span class="infomore">• ${(ask.createdStamp)?if_exists}</span>
		                <span class="infomore">• bởi ${(ask.author)?if_exists}</span>
		            </li>
				</#list>
			</#if>

        </ul>
      </div>
      <div role="tabpanel" class="tab-pane fade" id="hot" aria-labelledby="hot-tab">
        <ul class="listask">

        <#assign resultValueHot = dispatcher.runSync("getListAsk", Static["org.ofbiz.base.util.UtilMisc"].toMap("sortBy", "date", "contentTypeId", parameters.contentTypeId))/>
	<#if Static["org.ofbiz.service.ServiceUtil"].isSuccess(resultValueHot)>
		<#assign listAskHot = resultValueHot.get("listAsk") />
	</#if>

	<#if listAskHot?exists>
			<#list listAskHot as ask>

				<li>
					<#assign shortcut = ask.author?substring(0, 1) />
					<div class="icon-shortcut-larger linkimg">${shortcut}</div>
	                <a class="linktitle" href="<@ofbizUrl>ViewFAQ?cId=${(ask.contentId)?if_exists}</@ofbizUrl>" title=""><h3>${(ask.longDescription)?if_exists}</h3></a>
	                <span class="infomore">• 1 xem</span>
	                <span class="infomore">• ${(ask.createdStamp)?if_exists}</span>
	                <span class="infomore">• bởi ${(ask.author)?if_exists}</span>
	            </li>

			</#list>
		</#if>

        </ul>
      </div>
	<#--	<div class="pagiask">
                <a title="trang 4" href="#">«</a>
                <a title="trang 1" href="#">1</a>
                <a>...</a>
                <a title="trang 4" href="#">4</a>
                <a class="actpage">5</a><a title="trang 6" href="#">6</a>
                <a>...</a>
                <a title="trang 383" href="#">383</a>
                <a title="trang 6" href="#">»</a>
            </div> -->
    </div>
  </div>
  </div>