'use strict';

var ALL = 'ALL';

angular.module('tbs.controllers').controller('ThemeCtrl',
    ['$scope', 'jtbPlayerService',
        function ($scope, jtbPlayerService) {
            $scope.theme = angular.isDefined(jtbPlayerService.currentPlayer()) ?
                jtbPlayerService.currentPlayer().gameSpecificPlayerAttributes.theme : 'default-theme';

            $scope.$on('playerLoaded', function () {
                $scope.theme = jtbPlayerService.currentPlayer().gameSpecificPlayerAttributes.theme;
            });
        }
    ]
);