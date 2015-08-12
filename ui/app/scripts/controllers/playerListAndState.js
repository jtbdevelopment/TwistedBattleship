'use strict';

angular.module('tbs.controllers').controller('PlayerListAndStateCtrl',
    ['$scope', 'tbsGameDetails', 'tbsActions', 'jtbGameCache', 'jtbPlayerService', '$state',
        function ($scope, tbsGameDetails, tbsActions, jtbGameCache, jtbPlayerService, $state) {

            $scope.gameID = $state.params.gameID;
            $scope.game = jtbGameCache.getGameForID($scope.gameID);
            $scope.gameDetails = tbsGameDetails;
            $scope.showActions = $scope.game.gamePhase === 'Challenged';
            $scope.player = {};
            $scope.player = angular.copy(jtbPlayerService.currentPlayer(), $scope.player);

            $scope.statusColor = function (md5) {
                if (angular.isDefined(md5)) {
                    return $scope.game.playerStates[md5].toLowerCase();
                }
                return '';
            };

            $scope.statusIcon = function (md5) {
                if (angular.isDefined(md5)) {
                    return $scope.game.playerStates[md5] === 'Pending' ? 'help-circled' :
                        $scope.game.playerStates[md5] === 'Accepted' ? 'checkmark-circled' :
                            $scope.game.playerStates[md5] === 'Quit' ? 'flag' :
                                'close-circled';
                }
                return '';
            };

            $scope.accept = function () {
                tbsActions.accept($scope);
            };

            $scope.reject = function () {
                tbsActions.reject($scope);
            };

        }
    ]
);