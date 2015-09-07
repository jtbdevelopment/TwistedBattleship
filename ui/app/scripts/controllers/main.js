'use strict';

angular.module('tbs.controllers').controller('MainCtrl',
    ['$scope', 'jtbPlayerService', 'jtbLiveGameFeed', '$ionicHistory', '$state', 'ENV', '$document',
        function ($scope, jtbPlayerService, jtbLiveGameFeed, $ionicHistory, $state, ENV, $document) {

            function checkNetworkStatusAndLogin() {
                $ionicHistory.nextViewOptions({
                    disableBack: true
                });
                $state.go('network', {}, {reload: true});
            }

            //  Set here to avoid causing circular dependency in app.js
            jtbLiveGameFeed.setEndPoint(ENV.apiEndpoint);

            $scope.theme = angular.isDefined(jtbPlayerService.currentPlayer()) ?
                jtbPlayerService.currentPlayer().gameSpecificPlayerAttributes.theme : 'default-theme';

            $scope.$on('playerLoaded', function () {
                $scope.theme = jtbPlayerService.currentPlayer().gameSpecificPlayerAttributes.theme;
            });

            $document.bind('pause', function () {
                jtbLiveGameFeed.suspendFeed();
            });

            $document.bind('resume', function () {
                checkNetworkStatusAndLogin();
            });

            $scope.$on('$cordovaNetwork:offline', function () {
                checkNetworkStatusAndLogin();
            });
        }
    ]
);