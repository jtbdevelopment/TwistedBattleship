'use strict';

//  TODO - unsaved changes warning
angular.module('tbs.controllers').controller('SetupGameV2Ctrl',
    ['$scope', 'tbsActions', 'jtbGameCache', '$state', '$ionicSideMenuDelegate', 'tbsShipGridV2',
        '$ionicModal', '$ionicLoading', '$timeout',
        function ($scope, tbsActions, jtbGameCache, $state, $ionicSideMenuDelegate, tbsShipGridV2,
                  $ionicModal, $ionicLoading, $timeout) {
            var controller = this;
            $ionicSideMenuDelegate.canDragContent(false);
            controller.gameID = $state.params.gameID;
            controller.game = jtbGameCache.getGameForID(controller.gameID);

            controller.movingShip = null;
            controller.movingPointerRelativeToShip = {x: 0, y: 0};
            controller.submitDisabled = true;

            //  As of Ionic 1.3 cannot controllerAs modals themselves
            $scope.setup = controller;
            $ionicModal.fromTemplateUrl('templates/help/help-setup.html', {
                scope: $scope,
                animation: 'slide-in-up'
            }).then(function (modal) {
                controller.helpModal = modal;
            });

            controller.showDetails = function () {
                $state.go('app.gameDetails', {gameID: controller.gameID});
            };

            controller.showHelp = function () {
                controller.helpModal.show();
            };

            controller.closeHelp = function () {
                controller.helpModal.hide();
            };

            $scope.$on('$destroy', function () {
                if (angular.isDefined(controller.helpModal)) {
                    controller.helpModal.remove();
                }
            });

            controller.quit = function () {
                tbsActions.quit(controller.game);
            };

            controller.submit = function () {
                var info = [];
                angular.forEach(tbsShipGridV2.currentShipsOnGrid(), function (ship) {
                    info.push({ship: ship.ship, coordinates: ship.shipGridCells});
                });

                tbsActions.setup(controller.game, info);
            };

            $scope.$on('$ionicView.leave', function () {
                tbsShipGridV2.stop();
            });

            $scope.$on('$ionicView.enter', function () {
                $ionicLoading.show({
                    template: 'Loading...'
                });
                tbsShipGridV2.initialize(controller.game, controller.game.maskedPlayersState.shipStates, [], function () {
                    $timeout(function () {
                        tbsShipGridV2.enableShipMovement(function (hasOverlappingShips) {
                            $timeout(function () {
                                controller.submitDisabled = hasOverlappingShips;
                            });
                        });
                        $ionicLoading.hide();
                    }, this);
                });
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