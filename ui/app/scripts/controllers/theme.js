'use strict';

angular.module('tbs.controllers').controller('ThemeCtrl',
    ['$scope', 'jtbPlayerService', 'jtbLiveGameFeed', 'ENV',
        function ($scope, jtbPlayerService, jtbLiveGameFeed, ENV) {

            //  Set here to avoid causing circular dependency in app.js
            jtbLiveGameFeed.setServiceBase(ENV.apiEndpoint);

            $scope.theme = angular.isDefined(jtbPlayerService.currentPlayer()) ?
                jtbPlayerService.currentPlayer().gameSpecificPlayerAttributes.theme : 'default-theme';

            $scope.$on('playerLoaded', function () {
                $scope.theme = jtbPlayerService.currentPlayer().gameSpecificPlayerAttributes.theme;
            });
        }
    ]
);