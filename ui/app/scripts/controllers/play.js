'use strict';

angular.module('tbs.controllers').controller('PlayGameCtrl',
    ['$scope', 'tbsGameDetails', 'tbsActions', 'jtbGameCache', 'jtbPlayerService', '$state', '$ionicSideMenuDelegate', 'tbsShips', 'tbsShipGrid', // 'twAds',
        function ($scope, tbsGameDetails, tbsActions, jtbGameCache, jtbPlayerService, $state, $ionicSideMenuDelegate, tbsShips, tbsShipGrid /*, twAds*/) {
            $ionicSideMenuDelegate.canDragContent(false);
            $scope.theme = 'default';
            $scope.gameID = $state.params.gameID;
            $scope.game = jtbGameCache.getGameForID($scope.gameID);
            $scope.gameDetails = tbsGameDetails;
            $scope.player = {};
            $scope.player = angular.copy(jtbPlayerService.currentPlayer(), $scope.player);
            $scope.showing = $scope.player.md5;
            $scope.generalShipInfo = [];

            $scope.quit = function () {
                tbsActions.quit($scope);
            };

            $scope.changePlayer = function (md5) {
                if (md5 === $scope.player.md5) {
                    tbsShipGrid.placeShips(computeShipLocations());
                    tbsShipGrid.placeCellMarkers($scope.game.maskedPlayersState.consolidatedOpponentView.table);
                } else {
                    tbsShipGrid.placeShips([]);
                    tbsShipGrid.placeCellMarkers($scope.game.maskedPlayersState.opponentGrids[md5].table);
                }
                $scope.showing = md5;
            };

            function computeShipLocations() {
                //  TODO - overlap with setup
                var shipLocations = [];
                angular.forEach($scope.game.maskedPlayersState.shipStates, function (value, key) {
                    var shipInfo = $scope.generalShipInfo.find(function (info) {
                        return info.ship === key;
                    });
                    var horizontal = value.shipGridCells[0].row === value.shipGridCells[1].row;
                    var row = value.shipGridCells[0].row;
                    var column = value.shipGridCells[0].column;
                    shipLocations.push({horizontal: horizontal, row: row, column: column, shipInfo: shipInfo});
                });
                return shipLocations;
            }

            tbsShips.ships().then(
                function (generalShipInfo) {
                    $scope.generalShipInfo = generalShipInfo;
                    tbsShipGrid.initialize($scope.theme, $scope.game, [], [], function () {
                        $scope.changePlayer($scope.showing);
                    });
                },
                function () {
                    //  TODO
                }
            );

        }
    ]
);