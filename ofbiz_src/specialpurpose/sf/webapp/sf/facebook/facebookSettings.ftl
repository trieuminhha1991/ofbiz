<div class="row-fluid">
	<div class="span12 widget-container-span">
		<div class="widget-box">
			<div class="widget-header">
				<h5 class="smaller">Cấu hình thông tin Facebook</h5>
			</div>
			<div class="widget-body">
				<div class="widget-main padding-6">
					<div class="row-fluid">
						<div class="span12">
							<form class="form-horizontal" />
								<div class="control-group">
									<label class="control-label" for="form-field-1">Main app id</label>
									<div class="controls">
										<input type="text" value="${facebookSettings.fbAppId}" id="appid" placeholder="ex: 699572693462910" class="span10"/>
									</div>
								</div>
								<div class="control-group">
									<label class="control-label" for="form-field-1">Facebook Page Url</label>
									<div class="controls">
										<input type="text" value="${facebookSettings.fbPageUrl}" id="pageurl" placeholder="ex: https://www.facebook.com/www.nhanhqua.vn" class="span10"/>
									</div>
								</div>
								<div class="control-group">
									<label class="control-label" for="form-field-1">Facebook theme</label>
									<div class="controls">
										<select id="fbTheme" class="span10">
											<option value="light" <#if facebookSettings.fbTheme == "light">selected</#if>/>Light
											<option value="dark" <#if facebookSettings.fbTheme == "dark">selected</#if>/>Dark
										</select>
									</div>
								</div>
								<div class="control-group">
									<label class="control-label" for="form-field-1">Comment width (px)</label>
									<div class="controls">
										<input type="number" value="${facebookSettings.fbCommentWidth}" id="commentWidth" placeholder="ex: 600" class="span10"/>
									</div>
								</div>
								<div class="control-group">
									<label class="control-label" for="form-field-tags">List Supplier Page Id</label>
									<div class="controls access_token">
										<input type="text" name="tags" id="supplierpage"
											value="<#if facebookSettings.fbSupplierPage?exists>${facebookSettings.fbSupplierPage}</#if>" placeholder="Enter page id (699572693462910) or FB web address (www.nhanhqua.vn)" class="span12"/>
									</div>
								</div>
								<div class="control-group">
									<label class="control-label" for="form-field-tags">List app access token</label>
									<div class="controls access_token">
										<input type="text" name="tags" id="accessToken" value="<#if facebookSettings.fbAccessToken?exists> ${facebookSettings.fbAccessToken}</#if>" placeholder="Enter access token ex: 773032816096571|ZRLcHd0clPDTMHE66GonCaQVSBM" class="span12"/>
									</div>
								</div>
								<div class="form-actions">
									<button class="btn right" type="reset" id="reset">
										<i class="icon-undo bigger-110"></i>
										Reset
									</button>
									<button class="btn btn-info right" type="button" style="margin-right: 10px;" id="submit">
										<i class="icon-ok bigger-110"></i>
										Submit
									</button>
								</div>
							</form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
	jQuery(document).ready(function($){
		var supplierPage = $('#supplierpage');
		var access_token = $('#accessToken');
		if(! ( /msie\s*(8|7|6)/.test(navigator.userAgent.toLowerCase())) ) {
			supplierPage.tag({placeholder:supplierPage.attr('placeholder')});
			access_token.tag({placeholder:access_token.attr('placeholder')});
		}else {
			//display a textarea for old IE, because it doesn't support this plugin or another one I tried!
			supplierPage.after('<textarea id="'+supplierPage.attr('id')+'" name="'+supplierPage.attr('name')+'" rows="3">'+supplierPage.val()+'</textarea>').remove();
			access_token.after('<textarea id="'+access_token.attr('id')+'" name="'+supplierPage.attr('name')+'" rows="3">'+access_token.val()+'</textarea>').remove();
			$('#form-field-tags').autosize({append: "\n"});
		}
		$("#submit").click(function(){
			submit();
		});

		$("#reset").click(function(){
			window.location.reload();
		});
		function submit(){
			var obj =  {
				fbsettingid: 1,
				appid: $('#appid').val(),
				pageurl : $('#pageurl').val(),
				fbTheme: $('#fbTheme').val(),
				commentWidth : $("#commentWidth").val(),
				accessToken : $("#accessToken").val(),
				supplierpage : $('#supplierpage').val()
			};
			$.ajax({
				url : "updateFacebookSettings",
				data: obj,
				success: function(res){
					if(res.msg == "success"){
						alert("Success!");
					}else{
						alert("Failure!");
					}
				}
			});
		}
	});
</script>