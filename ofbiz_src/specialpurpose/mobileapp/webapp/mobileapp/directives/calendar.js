/*global todomvc, angular */
'use strict';

/**
 * The main controller for the app. The controller:
 * - retrieves and persists the model via the todoStorage service
 * - exposes the model to the template and provides event handlers
 */
olbius.directive('calendar', ['$compile', 'CategoryService',
    function($compile, CategoryService) {
        function init(scope, element, attrs) {
            $('#calendar').fullCalendar({
                buttonText: {
                    prev: '<i class="icon-chevron-left"></i>',
                    next: '<i class="icon-chevron-right"></i>'
                },
                //defaultView: 'agendaDay',
                header: {
                    left: 'prev,next today',
                    center: 'title',
                    right: 'month,agendaWeek,agendaDay'
                },
                loading: function(isLoading, view) {
                    if (!isLoading) {
                        $("#loading").hide();
                    }
                },
                events: function(start, end, callback) {
                },
                editable: true,
                droppable: true, // this allows things to be dropped onto the calendar !!!
                drop: function(date, allDay) {// this function is called when something is dropped
                    // retrieve the dropped element's stored Event Object
                    var originalEventObject = $(this).data('eventObject');
                    var $extraEventClass = $(this).attr('data-class');
                    // we need to copy it, so that multiple events don't have a reference to the same object
                    var copiedEventObject = $.extend({}, originalEventObject);
                    // assign it the date that was reported
                    copiedEventObject.start = date;
                    copiedEventObject.allDay = allDay;
                    if ($extraEventClass)
                        copiedEventObject['className'] = [$extraEventClass];
                    $('#calendar').fullCalendar('renderEvent', copiedEventObject, true);
                    if ($('#drop-remove').is(':checked')) {
                        // if so, remove the element from the "Draggable Events" list
                        $(this).remove();
                    }
                },
                selectable: true,
                selectHelper: true,
                select: function(start, end, allDay) {
                    bootbox.prompt("Tạo sự kiện mới:", function(title) {
                        if (title) {
                            var estimatedStartDate = new Date(start);
                            var estimatedCompletionDate = new Date(end);
                            if (allDay) {
                                var completionHours = '23';
                                var completionMinutues = '59';
                            } else {
                                completionHours = estimatedCompletionDate.getHours();
                                var completionMinutes = estimatedCompletionDate.getMinutes();
                            }
                            var estimatedStartDate_c_date_i18n = estimatedStartDate.getFullYear() + "-" + ("0" + (estimatedStartDate.getMonth() + 1)).slice(-2) + "-" + ("0" + estimatedStartDate.getDate()).slice(-2);
                            var estimatedCompletionDate_c_date_i18n = estimatedCompletionDate.getFullYear() + "-" + ("0" + (estimatedCompletionDate.getMonth() + 1)).slice(-2) + "-" + ("0" + estimatedCompletionDate.getDate()).slice(-2);
                            var workEffortName = title;
                            $("#loading").show();

                        }
                    });
                    calendar.fullCalendar('unselect');
                },
                eventClick: function(calEvent, jsEvent, view) {
                    var form = $("<form class='form-inline'><label>Đổi tên sự kiện&nbsp;</label></form>");
                    form.append("<input autocomplete=off type=text value='" + calEvent.title + "' /> ");
                    form.append("<button type='submit' class='btn btn-small btn-success'><i class='icon-ok'></i> Save</button>");
                    var div = bootbox.dialog(form, [{
                            "label": "<i class='icon-trash'></i> Xóa sự kiện",
                            "class": "btn-small btn-danger",
                            "callback": function() {
                                $("#loading").show();
                            }
                        }, {
                            "label": "<i class='icon-remove'></i> Close",
                            "class": "btn-small"
                        }], {
                        // prompts need a few extra options
                        "onEscape": function() {
                            div.modal("hide");
                        }
                    });

                    form.on('submit', function() {
                        var newTitle = form.find("input[type=text]").val();
                        if (newTitle) {
                            $("#loading").show();
                            var startDate = calEvent._start.getFullYear() + "-" + ("0" + (calEvent._start.getMonth() + 1)).slice(-2) + "-" + ("0" + calEvent._start.getDate()).slice(-2) + " " + calEvent._start.getHours() + ":" + calEvent._start.getMinutes() + ":00";
                            var endDate = calEvent._end.getFullYear() + "-" + ("0" + (calEvent._end.getMonth() + 1)).slice(-2) + "-" + ("0" + calEvent._end.getDate()).slice(-2) + " " + calEvent._end.getHours() + ":" + calEvent._end.getMinutes() + ":00";
                            var workEffortId = calEvent._id;
                        }
                        div.modal("hide");
                        return false;
                    });

                },
                eventResize: function(event, delta, revertFunc) {
                    var startDate = formatDateUtils(event._start) + event._start.getHours() + ":" + event._start.getMinutes() + ":00";
                    var endDate = formatDateUtils(event._end);
                    if (event.allDay) {
                        endDate += "23:59:00";
                    } else {
                        endDate += event._end.getHours() + ":" + event._end.getMinutes() + ":00";
                    }
                },
                eventDrop: function(event, delta, revertFunc) {
                    var startDate = formatDateUtils(event._start) + event._start.getHours() + ":" + event._start.getMinutes() + ":00";
                    var endDate = formatDateUtils(event._end);
                    if (event.allDay) {
                        endDate += "23:59:00";
                    } else {
                        endDate += event._end.getHours() + ":" + event._end.getMinutes() + ":00";
                    }
                }
            });
        }

        return {
            restrict: 'ACE',
            template: '<div id="calendar"></div>',
            scope: {
            },
            link: init
        };
    }]);
