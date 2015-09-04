'use strict';

//  TODO - need to control background/foreground etc
angular.module('tbs.controllers').controller('MainCtrl',
    ['$scope', 'jtbPlayerService', 'jtbLiveGameFeed', '$ionicHistory', '$state', 'ENV',
        function ($scope, jtbPlayerService, jtbLiveGameFeed, $ionicHistory, $state, ENV) {

            //  Set here to avoid causing circular dependency in app.js
            jtbLiveGameFeed.setServiceBase(ENV.apiEndpoint);

            $scope.theme = angular.isDefined(jtbPlayerService.currentPlayer()) ?
                jtbPlayerService.currentPlayer().gameSpecificPlayerAttributes.theme : 'default-theme';

            $scope.$on('playerLoaded', function () {
                $scope.theme = jtbPlayerService.currentPlayer().gameSpecificPlayerAttributes.theme;
            });

            $scope.$on('$cordovaNetwork:offline', function () {
                $ionicHistory.nextViewOptions({
                    disableBack: true
                });
                $state.go('network', {}, {reload: true});
            });
        }
    ]
);