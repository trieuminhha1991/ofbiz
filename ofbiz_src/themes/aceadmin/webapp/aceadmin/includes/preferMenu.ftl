<#if userLogin?has_content>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
	<#--<script type="text/javascript">
		var jqxCoreLoaded = true;
	</script>-->
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdragdrop.js"></script>
	<div id="preferOverlay" style="display: none;z-index:10000;overflow: 'hidden';"></div>
	<div id='preferMenu' style="display:none;position: fixed;z-index:10000;overflow: 'hidden';" class="widget-box preferMenuCss">
		<div class="widget-header" style="margin: 0px;background: #438eb9;">
			<h4 class="widget-title lighter smaller" style="color:white;"><i class="fa fa-cog"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.FrequentTasks)}</h4>
			<div  style="margin-top:8px;margin-right:5px;"><a id="pmncol" href="javascript:void(0)" onclick="changePreferMenuState();"><i class="fa-chevron-right white"></i></a></div>
		</div>
		<div>
			<ul class="nav nav-list prefmenuright">
				<#if listFuncs?has_content>
					<#list listFuncs as item>
						<li id="mnli${item_index}"><a href="${item.link?if_exists}" class="focusPP"><i class="${item.icon?if_exists}"></i><span class="menu-text">${StringUtil.wrapString(uiLabelMap[item.title?if_exists])}</span><i class="fa-times ipreremove" onclick="removeElementFromLeftMenu(this);return false;"></i></a></li>
					</#list>
				</#if>
			</ul>
		</div>
		<div id="freEditmode" style="position: absolute;bottom: 0; left: 0;width:100%;height:40px;display:none;">
			<button onclick="saveFreConfig()" style="margin:0 auto;position:relative;left:40%;" type="button" class="btn btn-primary btn-mini" name="submitButton"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
			<img id="preloader" style="display:none;float:right;" src="/aceadmin/jqw/jqwidgets/styles/images/loader.gif"/>
			<div id="rspre"></div>
		</div>
	</div>
	<script type="text/javascript">
		function removeElementFromLeftMenu(elm){
			$("#" + $(elm)[0].parentElement.parentElement.id).remove();
		}
		function saveFreConfig(){
			var tmpData = [];
			var varMenuLink = $('.nav.nav-list.prefmenuright').find('a.focusPP');
			for(i = 0; i < varMenuLink.length; i++){
				var tmpV = {
								"href": $(varMenuLink[i])[0].pathname,
								"text": $(varMenuLink[i])[0].innerText
							};
				tmpData[i] = tmpV;
			}
			$('#preloader').attr('style', 'display: inline-block !important');
			$('#rspre').text('');
			$.ajax({
			  url: "saveFreMenu",
			  cache: false,
			  dataType: 'json',
			  type: 'post',
			  success: function(html){
				  $('#preloader').attr('style', 'display: none !important');
				  $('#rspre').attr('style', 'display: inline-block !important;color:blue;');
				  $('#rspre').text('Ok!');
			  },
			  error: function(html){
				  $('#preloader').attr('style', 'display: none !important');
				  $('#rspre').attr('style', 'display: inline-block !important;color:red;');
				  $('#rspre').text('Error:' + html);
			  },
			  data: {"data": JSON.stringify(tmpData)}
			}); 
		}
	</script>
	<style type="text/css">
			.ipreremove{
				float:right;
				display: none !important;
				color: red;
			}
			#preferMenu{
				width: 299px;
				height:90%;
				border: solid 1px #ccc;
				background-color:white;
			}
			.preferOverlay {
			  position: absolute;
			  top: 0;
			  left: 0;
			  width: 100%;
			  height: 100%;
			  background: #000;
			  opacity: 0.5;
			  filter: alpha(opacity=50);
			}
			.nav-list>li>a:focus {
			  background-color: #FFF;
			  color: #1963aa;
			}
			.nav-list>li>a:focus:before {
			  display: block;
			  content: "";
			  position: absolute;
			  top: -1px;
			  bottom: 0;
			  left: 0;
			  width: 3px;
			  max-width: 3px;
			  overflow: hidden;
			  background-color: #3382af;
			}
	</style>
</#if>