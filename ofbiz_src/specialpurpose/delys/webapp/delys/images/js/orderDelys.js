/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

function markOrderViewed() {
    jQuery.ajax({
        url: 'markOrderViewed',
        type: "POST",
        data: jQuery('#orderViewed').serialize(),
        success: function(data) {
        	$('#checkViewed').attr("checked", false);
            jQuery("#isViewed").fadeOut('fast');
            jQuery("#viewed").fadeIn('fast');
        }
    });
}
function markOrderUnViewed(){
	jQuery.ajax({
        url: 'markOrderUnViewed',
        type: "POST",
        data: jQuery('#orderUnViewed').serialize(),
        success: function(data) {
        	$('#checkViewed').attr("checked", false);
            jQuery("#viewed").fadeOut('fast');
            jQuery("#isViewed").fadeIn('fast');
        }
    });
}
$(document).ready(function() {
    // Active tab by parameter
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i=0;i<vars.length;i++) {
        var pair = vars[i].split("=");
        if(pair[0] == 'activeTab'){
            // remove current active tab
            var tmpStr = $("li.active a[data-toggle='tab']").attr("href");
            $(tmpStr).removeClass('active');
            $("li.active a[data-toggle='tab']").parent().removeAttr('class');
            // Active
            $('#' + pair[1]).addClass('active');
            $("a[href='#" + pair[1] + "']").parent().addClass("active");
            break;
        }
    }
});