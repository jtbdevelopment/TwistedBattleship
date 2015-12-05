'use strict';

var CURRENT_VERSION = '1.1';
var CURRENT_NOTES = 'Revamped create game screens and small fixes.';
angular.module('tbs.services').factory('tbsVersionNotes',
    ['$http', 'jtbPlayerService', '$ionicPopup',
        function ($http, jtbPlayerService, $ionicPopup) {
            return {
                showReleaseNotes: function () {
                    if (jtbPlayerService.currentPlayer().lastVersionNotes < CURRENT_VERSION) {
                        $ionicPopup.alert({
                            title: 'Welcome to version ' + CURRENT_VERSION + '!',
                            template: CURRENT_NOTES
                        });
                        $http.post('/api/player/lastVersionNotes/' + CURRENT_VERSION).success(function () {

                        }).error(function () {
                            //  TODO
                        });
                    }

                }
            };
        }
    ]
);
