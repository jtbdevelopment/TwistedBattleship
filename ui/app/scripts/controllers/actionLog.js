'use strict';

angular.module('tbs.controllers').controller('ActionLogCtrl',
    ['$scope', '$state', 'tbsGameDetails', 'jtbGameCache',
        function ($scope, $state, tbsGameDetails, jtbGameCache) {
            $scope.gameID = $state.params.gameID;
            $scope.game = jtbGameCache.getGameForID($scope.gameID);
            $scope.gameDetails = tbsGameDetails;

            $scope.$on('gameUpdated', function (event, oldGame, newGame) {
                if ($scope.gameID === newGame.id) {
                    $scope.game = newGame;
                }
            });
        }
    ]
);