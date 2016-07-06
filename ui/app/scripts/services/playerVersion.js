'use strict';

var CURRENT_VERSION = '1.2';
var CURRENT_NOTES = 'Added new game play options - cruise missile attack and new ship options.  Also added a new pirate theme, see your profile in top bar.';
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
                            console.log('successfully updated version');
                        }).error(function (data, status, headers, config) {
                            //  TODO
                            console.error(data + status + headers + config);
                        });
                    }

                }
            };
        }
    ]
);
