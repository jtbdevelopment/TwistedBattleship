'use strict';

angular.module('tbs.controllers').controller('PlayerListAndStateCtrl',
    ['$scope', 'tbsGameDetails', 'tbsActions', 'jtbGameCache', 'jtbPlayerService', '$state',
        function ($scope, tbsGameDetails, tbsActions, jtbGameCache, jtbPlayerService, $state) {
            var controller = this;

            function initialize() {
                controller.gameID = $state.params.gameID;
                controller.game = jtbGameCache.getGameForID(controller.gameID);
                controller.gameDetails = tbsGameDetails;
                controller.showActions = controller.game.gamePhase === 'Challenged';
                controller.player = {};
                controller.player = jtbPlayerService.currentPlayer();
            }

            initialize();

            controller.statusColor = function (md5) {
                if (angular.isDefined(md5)) {
                    return controller.game.playerStates[md5].toLowerCase();
                }
                return '';
            };

            controller.statusIcon = function (md5) {
                if (angular.isDefined(md5)) {
                    return controller.game.playerStates[md5] === 'Pending' ? 'help-circled' :
                        controller.game.playerStates[md5] === 'Accepted' ? 'checkmark-circled' :
                            controller.game.playerStates[md5] === 'Quit' ? 'flag' :
                                'close-circled';
                }
                return '';
            };

            controller.showDetails = function () {
                $state.go('app.gameDetails', {gameID: controller.gameID});
            };

            controller.accept = function () {
                tbsActions.accept(controller.game);
            };

            controller.reject = function () {
                tbsActions.reject(controller.game);
            };

            $scope.$on('$ionicView.enter', function () {
                initialize();
            });

            $scope.$on('gameUpdated', function (event, oldGame, newGame) {
                if (controller.gameID === newGame.id) {
                    controller.game = newGame;
                    tbsActions.updateCurrentView(oldGame, newGame);
                }
            });
        }
    ]
);