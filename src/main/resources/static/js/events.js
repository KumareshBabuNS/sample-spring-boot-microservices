angular.module('events', ['ngResource', 'ui.bootstrap']).
    factory('Events', function ($resource) {
        return $resource('events');
    }).
    factory('Event', function ($resource) {
        return $resource('events/:id', {id: '@id'});
    }).
    factory("EditorStatus", function () {
        var editorEnabled = {};

        var enable = function (id, fieldName) {
            editorEnabled = { 'id': id, 'fieldName': fieldName };
        };

        var disable = function () {
            editorEnabled = {};
        };

        var isEnabled = function(id, fieldName) {
            return (editorEnabled['id'] == id && editorEnabled['fieldName'] == fieldName);
        };

        return {
            isEnabled: isEnabled,
            enable: enable,
            disable: disable
        }
    });

function EventsController($scope, $modal, Events, Event, Status) {
    function list() {
        $scope.events = Events.query();
    }

    function clone (obj) {
        return JSON.parse(JSON.stringify(obj));
    }

    function saveEvent(event) {
        Events.save(event,
            function () {
                Status.success("Event saved");
                list();
            },
            function (result) {
                Status.error("Error saving event: " + result.status);
            }
        );
    }

    $scope.addEvent = function () {
        var addModal = $modal.open({
            templateUrl: 'templates/albumForm.html',
            controller: EventModalController,
            resolve: {
                album: function () {
                    return {};
                },
                action: function() {
                    return 'add';
                }
            }
        });

        addModal.result.then(function (event) {
            saveEvent(event);
        });
    };

    $scope.updateEvent = function (album) {
        var updateModal = $modal.open({
            templateUrl: 'templates/albumForm.html',
            controller: EventModalController,
            resolve: {
                album: function() {
                    return clone(album);
                },
                action: function() {
                    return 'update';
                }
            }
        });

        updateModal.result.then(function (event) {
            saveAlbum(event);
        });
    };

    $scope.deleteEvent = function (event) {
        Event.delete({id: event.id},
            function () {
                Status.success("Event deleted");
                list();
            },
            function (result) {
                Status.error("Error deleting event: " + result.status);
            }
        );
    };

    $scope.setEventsView = function (viewName) {
        $scope.eventsView = "templates/" + viewName + ".html";
    };

    $scope.init = function() {
        list();
        $scope.setEventsView("grid");
        $scope.sortField = "name";
        $scope.sortDescending = false;
    };
}

function EventModalController($scope, $modalInstance, event, action) {
    $scope.albumAction = action;
    $scope.yearPattern = /^[1-2]\d{3}$/;
    $scope.event = event;

    $scope.ok = function () {
        $modalInstance.close($scope.event);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
};

function EventEditorController($scope, Events, Status, EditorStatus) {
    $scope.enableEditor = function (event, fieldName) {
        $scope.newFieldValue = event[fieldName];
        EditorStatus.enable(event.id, fieldName);
    };

    $scope.disableEditor = function () {
        EditorStatus.disable();
    };

    $scope.isEditorEnabled = function (event, fieldName) {
        return EditorStatus.isEnabled(event.id, fieldName);
    };

    $scope.save = function (event, fieldName) {
        if ($scope.newFieldValue === "") {
            return false;
        }

        event[fieldName] = $scope.newFieldValue;

        Events.save({}, event,
            function () {
                Status.success("Event saved");
                list();
            },
            function (result) {
                Status.error("Error saving event: " + result.status);
            }
        );

        $scope.disableEditor();
    };

    $scope.disableEditor();
}

angular.module('events').
    directive('inPlaceEdit', function () {
        return {
            restrict: 'E',
            transclude: true,
            replace: true,

            scope: {
                ipeFieldName: '@fieldName',
                ipeInputType: '@inputType',
                ipeInputClass: '@inputClass',
                ipePattern: '@pattern',
                ipeModel: '=model'
            },

            template:
                '<div>' +
                    '<span ng-hide="isEditorEnabled(ipeModel, ipeFieldName)" ng-click="enableEditor(ipeModel, ipeFieldName)">' +
                        '<span ng-transclude></span>' +
                    '</span>' +
                    '<span ng-show="isEditorEnabled(ipeModel, ipeFieldName)">' +
                        '<div class="input-append">' +
                            '<input type="{{ipeInputType}}" name="{{ipeFieldName}}" class="{{ipeInputClass}}" ' +
                                'ng-required ng-pattern="{{ipePattern}}" ng-model="newFieldValue" ' +
                                'ui-keyup="{enter: \'save(ipeModel, ipeFieldName)\', esc: \'disableEditor()\'}"/>' +
                            '<div class="btn-group btn-group-xs" role="toolbar">' +
                                '<button ng-click="save(ipeModel, ipeFieldName)" type="button" class="btn"><span class="glyphicon glyphicon-ok"></span></button>' +
                                '<button ng-click="disableEditor()" type="button" class="btn"><span class="glyphicon glyphicon-remove"></span></button>' +
                            '</div>' +
                        '</div>' +
                    '</span>' +
                '</div>',

            controller: 'EventEditorController'
        };
    });
