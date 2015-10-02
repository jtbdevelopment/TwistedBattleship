'use strict';

angular.module('tbs.controllers').controller('PlayerDetailsCtrl',
    ['$scope', 'jtbPlayerService',
        function ($scope, jtbPlayerService) {
            $scope.$on('$ionicView.enter', function () {
                $scope.player = jtbPlayerService.currentPlayer();
            });
        }
    ]
);