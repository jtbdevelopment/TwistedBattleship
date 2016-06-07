'use strict';

//  TODO - unsaved changes warning
angular.module('tbs.controllers').controller('SetupGameV2Ctrl',
    ['$scope', 'tbsGameDetails', 'tbsActions', 'jtbGameCache', '$state', '$ionicSideMenuDelegate', 'shipInfo', 'tbsShipGridV2', '$ionicModal', '$ionicLoading', '$timeout',
        function ($scope, tbsGameDetails, tbsActions, jtbGameCache, $state, $ionicSideMenuDelegate, shipInfo, tbsShipGridV2, $ionicModal, $ionicLoading, $timeout) {
            $ionicSideMenuDelegate.canDragContent(false);
            $scope.gameID = $state.params.gameID;
            $scope.game = jtbGameCache.getGameForID($scope.gameID);
            $scope.gameDetails = tbsGameDetails;

            $scope.movingShip = null;
            $scope.movingPointerRelativeToShip = {x: 0, y: 0};
            $scope.submitDisabled = true;

            $ionicModal.fromTemplateUrl('templates/help/help-setup.html', {
                scope: $scope,
                animation: 'slide-in-up'
            }).then(function (modal) {
                $scope.helpModal = modal;
            });

            $scope.showDetails = function () {
                $state.go('app.gameDetails', {gameID: $scope.gameID});
            };

            $scope.showHelp = function () {
                $scope.helpModal.show();
            };

            $scope.closeHelp = function () {
                $scope.helpModal.hide();
            };

            $scope.$on('$destroy', function () {
                $scope.helpModal.remove();
            });

            $scope.quit = function () {
                tbsActions.quit($scope.game);
            };

            $scope.submit = function () {
                var info = [];
                angular.forEach(tbsShipGridV2.currentShipsOnGrid(), function (ship) {
                    var cells = [];
                    var startX = ship.startX;
                    var startY = ship.startY;
                    var i = 0;
                    if (ship.shipState.horizontal) {
                        for (i = 0; i < ship.info.gridSize; ++i) {
                            cells.push({column: startX + i, row: startY});
                        }
                    } else {
                        for (i = 0; i < ship.info.gridSize; ++i) {
                            cells.push({column: startX, row: startY + i});
                        }
                    }
                    info.push({ship: ship.info.ship, coordinates: cells});
                });

                tbsActions.setup($scope.game, info);
            };

            $scope.$on('$ionicView.leave', function () {
                tbsShipGridV2.stop();
            });

            $scope.$on('$ionicView.enter', function () {
                $ionicLoading.show({
                    template: 'Loading...'
                });
                tbsShipGridV2.initialize($scope.game, angular.copy($scope.game.maskedPlayersState.shipStates), [], function () {
                    $timeout(function () {
                        tbsShipGridV2.enableShipMovement(function (hasOverlappingShips) {
                            $timeout(function () {
                                $scope.submitDisabled = hasOverlappingShips;
                            });
                        });
                        $ionicLoading.hide();
                    }, this);
                });
            });

            $scope.$on('gameUpdated', function (event, oldGame, newGame) {
                if ($scope.gameID === newGame.id) {
                    $scope.game = newGame;
                    tbsActions.updateCurrentView(oldGame, newGame);
                }
            });
        }
    ]
);