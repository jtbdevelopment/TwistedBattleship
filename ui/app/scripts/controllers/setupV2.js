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

            /*
             function moveOutOfBoundShip(shipData) {
             if (shipData.startX < 0) {
             shipData.sprite.x -= shipData.startX;
             tbsShipGridV2.computeShipCorners(shipData);
             }
             if (shipData.endX >= tbsShipGridV2.gameWidth()) {
             shipData.sprite.x -= (shipData.endX - tbsShipGridV2.gameWidth() + 1);
             tbsShipGridV2.computeShipCorners(shipData);
             }
             if (shipData.startY < 0) {
             shipData.sprite.y -= shipData.startY;
             tbsShipGridV2.computeShipCorners(shipData);
             }
             if (shipData.endY >= tbsShipGridV2.gameHeight()) {
             shipData.sprite.y -= (shipData.endY - tbsShipGridV2.gameHeight() + 1);
             tbsShipGridV2.computeShipCorners(shipData);
             }
             }

             function roundPosition(shipData) {
             var xMod = shipData.startX % tbsShipGridV2.cellSize();
             if (xMod !== 0) {
             if (xMod < tbsShipGridV2.halfCellSize()) {
             shipData.sprite.x = shipData.sprite.x - xMod;
             } else {
             shipData.sprite.x = shipData.sprite.x - xMod + tbsShipGridV2.cellSize();
             }
             }
             var yMod = shipData.startY % tbsShipGridV2.cellSize();
             if (yMod !== 0) {
             if (yMod < tbsShipGridV2.halfCellSize()) {
             shipData.sprite.y = shipData.sprite.y - yMod;
             } else {
             shipData.sprite.y = shipData.sprite.y - yMod + tbsShipGridV2.cellSize();
             }
             }
             tbsShipGridV2.computeShipCorners(shipData);
             moveOutOfBoundShip(shipData);
             }

             function checkOverlap() {
             var overlapExists = false;
             var overlaps = [];
             var shipsOnGrid = tbsShipGridV2.currentShipsOnGrid();
             angular.forEach(shipsOnGrid, function () {
             overlaps.push(false);
             });

             for (var i = 0; i < shipsOnGrid.length; ++i) {
             var outerShip = shipsOnGrid[i];
             for (var j = i + 1; j < shipsOnGrid.length; ++j) {
             var innerShip = shipsOnGrid[j];
             if (outerShip.startX > innerShip.endX ||
             outerShip.endX < innerShip.startX ||
             outerShip.startY > innerShip.endY ||
             outerShip.endY < innerShip.startY) {
             continue;
             }

             overlapExists = true;
             overlaps[i] = true;
             overlaps[j] = true;
             }
             if (overlaps[i] === true) {
             shipsOnGrid[i].sprite.tint = 0xff0000;
             } else {
             shipsOnGrid[i].sprite.tint = 0xffffff;
             }
             }
             $scope.submitDisabled = overlapExists;
             }

             function onUp() {
             $timeout(function () {
             if ($scope.movingShip !== null) {
             $scope.movingShip.sprite.tint = 0xffffff;
             roundPosition($scope.movingShip);
             checkOverlap();
             $scope.movingShip = null;
             }
             });
             }

             //noinspection JSUnusedLocalSymbols
             function onMove(pointer, x, y) {
             $timeout(function () {
             if ($scope.movingShip !== null) {
             $scope.movingShip.sprite.x = x + $scope.movingPointerRelativeToShip.x;
             $scope.movingShip.sprite.y = y + $scope.movingPointerRelativeToShip.y;
             tbsShipGridV2.computeShipCorners($scope.movingShip);
             }
             });
             }

             function onDown(context) {
             $timeout(function () {
             var coords = tbsShipGridV2.currentContextCoordinates(context);
             $scope.movingShip = tbsShipGridV2.findShipByContextCoordinates(context);
             if ($scope.movingShip !== null) {
             $scope.movingPointerRelativeToShip.x = $scope.movingShip.centerX - coords.x;
             $scope.movingPointerRelativeToShip.y = $scope.movingShip.centerY - coords.y;
             $scope.movingShip.sprite.tint = 0x00ff00;
             }
             });
             }

             function onTap(context, double) {
             $timeout(function () {
             if (double) {
             var ship = tbsShipGridV2.findShipByContextCoordinates(context);

             if (ship) {
             ship.horizontal = !(ship.horizontal);
             tbsShipGridV2.computeShipCorners(ship);
             roundPosition(ship);
             checkOverlap();
             ship.sprite.angle = ship.horizontal ? 0 : 90;
             }
             }
             });
             }

             function computeShipLocations() {
             var shipLocations = [];
             angular.forEach($scope.game.maskedPlayersState.shipStates, function (shipState) {
             var shipDetails = null;
             angular.forEach(shipInfo, function (info) {
             if (info.ship === shipState.ship) {
             shipDetails = info;
             }
             });
             var horizontal = shipState.shipGridCells[0].row === shipState.shipGridCells[1].row;
             var row = shipState.shipGridCells[0].row;
             var column = shipState.shipGridCells[0].column;
             shipLocations.push({horizontal: horizontal, row: row, column: column, shipInfo: shipDetails});
             });
             if (shipLocations.length === 0) {
             var row = 0;
             angular.forEach($scope.game.startingShips, function (startingShip) {
             var shipDetails = null;
             angular.forEach(shipInfo, function (info) {
             if (info.ship === startingShip) {
             shipDetails = info;
             }
             });
             shipLocations.push({horizontal: true, row: row, column: 0, shipInfo: shipDetails});
             row = row + 1;
             });
             }
             return shipLocations;
             }
             */
            $scope.$on('$ionicView.leave', function () {
                tbsShipGridV2.stop();
            });

            $scope.$on('$ionicView.enter', function () {
                $ionicLoading.show({
                    template: 'Loading...'
                });
                tbsShipGridV2.initialize($scope.game, angular.copy($scope.game.maskedPlayersState.shipStates), [], function () {
                    $timeout(function () {
                        tbsShipGridV2.enableShipMovement();
                        /*
                         tbsShipGridV2.onDown(onDown);
                         tbsShipGridV2.onMove(onMove);
                         tbsShipGridV2.onTap(onTap);
                         tbsShipGridV2.onUp(onUp);
                         checkOverlap();
                         */
                        $ionicLoading.hide();
                    });
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