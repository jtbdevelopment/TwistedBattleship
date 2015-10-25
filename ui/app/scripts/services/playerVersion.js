'use strict';

var CURRENT_VERSION = '1.0';
var CURRENT_NOTES = 'Added background notifications when you are offline for a while.';
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
