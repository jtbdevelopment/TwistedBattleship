'use strict';

angular.module('tbs.services').factory('tbsVersionNotes',
    ['$http', 'jtbPlayerService', '$ionicPopup',
        function ($http, jtbPlayerService, $ionicPopup) {
            var currentVersion = '0.8';
            var currentNotes = 'Fixed several bugs and changed layout of game list.';
            return {
                showReleaseNotes: function () {
                    if (jtbPlayerService.currentPlayer().lastVersionNotes < currentVersion) {
                        $ionicPopup.alert({
                            title: 'Welcome to version ' + currentVersion + '!',
                            template: currentNotes
                        });
                        $http.post('/api/player/lastVersionNotes/' + currentVersion).success(function () {

                        }).error(function () {
                            //  TODO
                        });
                    }

                }
            };
        }
    ]
);
