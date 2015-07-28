'use strict';

angular.module('tbs.controllers').controller('SetupGameCtrl',
    ['$scope', 'tbsGameDetails', 'tbsActions', 'jtbGameCache', 'jtbPlayerService', '$state', '$ionicSideMenuDelegate', 'tbsShips', 'tbsShipGrid', // 'twAds',
        function ($scope, tbsGameDetails, tbsActions, jtbGameCache, jtbPlayerService, $state, $ionicSideMenuDelegate, tbsShips, tbsShipGrid /*, twAds*/) {
            $ionicSideMenuDelegate.canDragContent(false);
            $scope.theme = 'default';
            $scope.gameID = $state.params.gameID;
            $scope.game = jtbGameCache.getGameForID($scope.gameID);
            $scope.gameDetails = tbsGameDetails;

            $scope.movingShip = null;
            $scope.movingPointerRelativeToShip = {x: 0, y: 0};
            $scope.submitDisabled = true;

            $scope.quit = function () {
                tbsActions.quit($scope);
            };

            $scope.submit = function () {
                var info = {};
                angular.forEach(tbsShipGrid.currentShipsOnGrid(), function (ship) {
                    var cells = [];
                    var startX = ship.startX / tbsShipGrid.cellSize();
                    var startY = ship.startY / tbsShipGrid.cellSize();
                    var i = 0;
                    if (ship.horizontal) {
                        for (i = 0; i < ship.info.gridSize; ++i) {
                            cells.push({column: startX + i, row: startY});
                        }
                    } else {
                        for (i = 0; i < ship.info.gridSize; ++i) {
                            cells.push({column: startX, row: startY + i});
                        }
                    }
                    info[ship.info.ship] = cells;
                });

                tbsActions.setup($scope, info);
            };

            function moveOutOfBoundShip(shipData) {
                if (shipData.startX < 0) {
                    shipData.sprite.x -= shipData.startX;
                    tbsShipGrid.computeShipCorners(shipData);
                }
                if (shipData.endX >= tbsShipGrid.gameWidth()) {
                    shipData.sprite.x -= (shipData.endX - tbsShipGrid.gameWidth() + 1);
                    tbsShipGrid.computeShipCorners(shipData);
                }
                if (shipData.startY < 0) {
                    shipData.sprite.y -= shipData.startY;
                    tbsShipGrid.computeShipCorners(shipData);
                }
                if (shipData.endY >= tbsShipGrid.gameHeight()) {
                    shipData.sprite.y -= (shipData.endY - tbsShipGrid.gameHeight() + 1);
                    tbsShipGrid.computeShipCorners(shipData);
                }
            }

            function roundPosition(shipData) {
                var xMod = shipData.startX % tbsShipGrid.cellSize();
                if (xMod !== 0) {
                    if (xMod < tbsShipGrid.halfCellSize()) {
                        shipData.sprite.x = shipData.sprite.x - xMod;
                    } else {
                        shipData.sprite.x = shipData.sprite.x - xMod + tbsShipGrid.cellSize();
                    }
                }
                var yMod = shipData.startY % tbsShipGrid.cellSize();
                if (yMod !== 0) {
                    if (yMod < tbsShipGrid.halfCellSize()) {
                        shipData.sprite.y = shipData.sprite.y - yMod;
                    } else {
                        shipData.sprite.y = shipData.sprite.y - yMod + tbsShipGrid.cellSize();
                    }
                }
                tbsShipGrid.computeShipCorners(shipData);
                moveOutOfBoundShip(shipData);
            }

            function checkOverlap() {
                var overlapExists = false;
                var overlaps = [];
                var shipsOnGrid = tbsShipGrid.currentShipsOnGrid();
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
                $scope.$apply();
            }

            function onUp() {
                if ($scope.movingShip !== null) {
                    $scope.movingShip.sprite.tint = 0xffffff;
                    roundPosition($scope.movingShip);
                    checkOverlap();
                    $scope.movingShip = null;
                }
            }

            function onMove() {
                if ($scope.movingShip !== null) {
                    var coords = tbsShipGrid.currentMouseCoordinates();
                    $scope.movingShip.sprite.x = coords.x + $scope.movingPointerRelativeToShip.x;
                    $scope.movingShip.sprite.y = coords.y + $scope.movingPointerRelativeToShip.y;
                    tbsShipGrid.computeShipCorners($scope.movingShip);
                }
            }

            function onDown() {
                var coords = tbsShipGrid.currentMouseCoordinates();
                $scope.movingShip = tbsShipGrid.findShipByMouseCoordinates();
                if ($scope.movingShip !== null) {
                    $scope.movingPointerRelativeToShip.x = $scope.movingShip.centerX - coords.x;
                    $scope.movingPointerRelativeToShip.y = $scope.movingShip.centerY - coords.y;
                    $scope.movingShip.sprite.tint = 0x00ff00;
                }
            }

            function onTap(pointer, double) {
                if (double) {
                    var ship = tbsShipGrid.findShipByMouseCoordinates();

                    if (ship) {
                        ship.horizontal = !(ship.horizontal);
                        tbsShipGrid.computeShipCorners(ship);
                        roundPosition(ship);
                        checkOverlap();
                        ship.sprite.angle = ship.horizontal ? 0 : 90;
                    }
                }
            }

            function computeShipLocations(generalShipInfo) {
                var shipLocations = [];
                angular.forEach($scope.game.maskedPlayersState.shipStates, function (value, key) {
                    var shipInfo = generalShipInfo.find(function (info) {
                        return info.ship === key;
                    });
                    var horizontal = value.shipGridCells[0].row === value.shipGridCells[1].row;
                    var row = value.shipGridCells[0].row;
                    var column = value.shipGridCells[0].column;
                    shipLocations.push({horizontal: horizontal, row: row, column: column, shipInfo: shipInfo});
                });
                if (shipLocations.length === 0) {
                    var row = 0;
                    angular.forEach(generalShipInfo, function (shipInfo) {
                        shipLocations.push({horizontal: true, row: row, column: 4, shipInfo: shipInfo});
                        row = row + 1;
                    });
                }
                return shipLocations;
            }

            tbsShips.ships().then(
                function (generalShipInfo) {
                    tbsShipGrid.initialize($scope.theme, $scope.game, computeShipLocations(generalShipInfo), [], function () {
                        tbsShipGrid.onDown(onDown);
                        tbsShipGrid.onMove(onMove);
                        tbsShipGrid.onTap(onTap);
                        tbsShipGrid.onUp(onUp);
                        checkOverlap();
                    });
                },
                function () {
                    //  TODO
                }
            );

        }
    ]
);