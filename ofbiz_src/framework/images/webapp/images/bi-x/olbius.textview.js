(function (window, OLBIUS) {

    var color = ['#6FB3E0', '#1b926c', '#D68CDF'];
    var i = 0;

    function getColor() {
        if(i >= color.length) {
            i = 0;
        }
        return color[i++];
    }

    function init(config) {

        var uuid = OLBIUS.generateUUID();

        var containerId = 'container-' + uuid;
        var containerContentId = 'containerContent-' + uuid;
        var iconId = 'icon-' + uuid;
        var textId = 'text-' + uuid;
        var textContentId = 'textContent-' + uuid;
        var valueId = 'value-' + uuid;
        var valueContentId = 'valueContent-' + uuid;
        var descriptionId = 'description-' + uuid;
        var descriptionContentId = 'descriptionContent-' + uuid;

        function appendHtml(id, icon) {

            if ($(id).is('div')) {

                var text = '<div id="?">';

                if (icon) {
                    text += '<i class="' + icon + '" id="' + iconId + '"></i>'
                }

                text +=
                    '<div id="?"><div id="?"><span id="?"></span></div><div id="?"><span id="?"></span></div><div id="?"><span id="?"></span></div></div></div>';

                OLBIUS.appendHtmlUUID(id, text, uuid, ['container', 'containerContent', 'text', 'textContent', 'value', 'valueContent', 'description', 'descriptionContent']);

                var contentWidth = "100%";

                if (icon) {
                    $('#' + iconId).css({
                    	"padding": "20px 0px 0px 10px",
                        "font-size": "40px",
                        "color": "white",
                        "width": "50px",
                        "float": "left",
                        "display": "inline-block"
                    });
                    contentWidth = "70%";
                }

                $('#' + containerId).css({
                    "background": getColor(),
                    "-moz-user-select": "-moz-none",
                    "-o-user-select": "none",
                    "-khtml-user-select": "none",
                    "-webkit-user-select": "none",
                    "-ms-user-select": "none",
                    "height": "100%",
                    "cursor": "pointer"
                });
                $('#' + containerId + ':hover').css({background: "#1B9288"});
                $('#' + containerContentId).css({
                    "width": contentWidth,
                    "float": "left",
                    "font-size": "14px",
                    "padding": "5px",
                    "display": "inline-block",
                });
                $('#' + textId).css({
                    "width": "100%",
                });
                $('#' + textContentId).css({
                    "font-weight": "bold",
                    "text-transform": "uppercase",
                    "color": "#FFFFFF",
                    "font-size": "16px!important"
                });
                $('#' + valueId).css({
                    "color": "#FFFFFF", "font-weight": "bold"
                });
                $('#' + valueContentId).css({
                    "font-weight": "bold",
                    "color": "#FFFFFF",
                    "font-size": "14px",
                    "font-style": "italic"
                });
                $('#' + descriptionId).css({
                    "margin-bottom": "0",
                    "bottom": "0",
                    "font-style": "italic",
                    "color": "#FFFFFF"
                });

            } else {
                appendHtml($(id).parent(), icon);
            }
        }

        if (config.id) {
            appendHtml('#' + config.id, config.icon);
        } else {
            return null;
        }

        function load(url, data) {
            jQuery.ajax({
                url: url,
                type: 'POST',
                data: data,
                success: function (data) {
                    if(typeof config.renderTitle == 'function') {
                        $('#' + textContentId).html(config.renderTitle(data));
                    }
                    if(typeof config.renderValue == 'function') {
                        $('#' + valueContentId).html(config.renderValue(data));
                    }
                    if(typeof config.renderDescription == 'function') {
                        $('#' + descriptionContentId).html(config.renderDescription(data));
                    }
                }
            });
        }

        function textHtml() {

        }

        textHtml.prototype = {
            init : function() {
                if(config.url) {
                    load(config.url, config.data);
                }
                return $("#" + containerId);
            },
            reload: function() {
                this.init();
            }
        }

        return new textHtml;
    }

    OLBIUS.olbius().textView = function(config) {
        return init(config);
    }
})(window, OLBIUS);