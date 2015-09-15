'use strict';

angular.module('tbs.controllers').controller('PlayHelpCtrl',
    ['$scope', 'jtbPlayerService',
        function ($scope, jtbPlayerService) {
            $scope.theme = jtbPlayerService.currentPlayer().gameSpecificPlayerAttributes.theme;

            $scope.switchToGeneral = function() {
                $scope.showGeneral = true;
                $scope.showAttackDefend = false;
                $scope.showGrid = false;
            };

            $scope.switchToGrid = function() {
                $scope.showGeneral = false;
                $scope.showAttackDefend = false;
                $scope.showGrid = true;
            };

            $scope.switchToAttackDefend = function() {
                $scope.showGeneral = false;
                $scope.showAttackDefend = true;
                $scope.showGrid = false;
            };

            $scope.switchToGeneral();
        }
    ]
);