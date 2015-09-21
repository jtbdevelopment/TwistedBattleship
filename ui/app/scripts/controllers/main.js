'use strict';

angular.module('tbs.controllers').controller('MainCtrl',
    ['$scope', 'jtbPlayerService', 'jtbLiveGameFeed', '$state', 'ENV', '$document', 'tbsVersionNotes',
        function ($scope, jtbPlayerService, jtbLiveGameFeed, $state, ENV, $document, tbsVersionNotes) {

            function checkNetworkStatusAndLogin() {
                $state.go('network');
            }

            //  Set here to avoid causing circular dependency in app.js
            jtbLiveGameFeed.setEndPoint(ENV.apiEndpoint);

            $scope.theme = angular.isDefined(jtbPlayerService.currentPlayer()) ?
                jtbPlayerService.currentPlayer().gameSpecificPlayerAttributes.theme : 'default-theme';

            $scope.$on('playerLoaded', function () {
                $scope.theme = jtbPlayerService.currentPlayer().gameSpecificPlayerAttributes.theme;
                tbsVersionNotes.showReleaseNotes();
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

            $scope.$on('InvalidSession', function () {
                if ($state.$current.name !== 'signin') {
                    checkNetworkStatusAndLogin();
                }
            });
        }
    ]
);