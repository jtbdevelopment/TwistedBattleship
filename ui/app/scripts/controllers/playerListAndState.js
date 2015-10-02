'use strict';

angular.module('tbs.controllers').controller('PlayerListAndStateCtrl',
    ['$scope', 'tbsGameDetails', 'tbsActions', 'jtbGameCache', 'jtbPlayerService', '$state',
        function ($scope, tbsGameDetails, tbsActions, jtbGameCache, jtbPlayerService, $state) {

            $scope.gameID = $state.params.gameID;
            $scope.game = jtbGameCache.getGameForID($scope.gameID);
            $scope.gameDetails = tbsGameDetails;
            $scope.showActions = $scope.game.gamePhase === 'Challenged';
            $scope.player = {};
            $scope.player = jtbPlayerService.currentPlayer();

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
                tbsActions.accept($scope.game);
            };

            $scope.reject = function () {
                tbsActions.reject($scope.game);
            };

            $scope.$on('gameUpdated', function (event, oldGame, newGame) {
                if ($scope.gameID === newGame.id) {
                    $scope.game = newGame;
                    tbsActions.updateCurrentView(oldGame, newGame);
                }
            });
        }
    ]
);