'use strict';

angular.module('tbs.controllers').controller('PlayGameCtrl',
    ['$scope', 'tbsGameDetails', 'tbsActions', 'jtbGameCache', 'jtbPlayerService', '$state', '$ionicSideMenuDelegate', 'tbsShips', 'tbsShipGrid', // 'twAds',
        function ($scope, tbsGameDetails, tbsActions, jtbGameCache, jtbPlayerService, $state, $ionicSideMenuDelegate, tbsShips, tbsShipGrid /*, twAds*/) {
            $ionicSideMenuDelegate.canDragContent(false);
            $scope.theme = 'default';
            $scope.gameID = $state.params.gameID;
            $scope.game = jtbGameCache.getGameForID($scope.gameID);
            $scope.gameDetails = tbsGameDetails;

            $scope.quit = function () {
                tbsActions.quit($scope);
            };

            function computeShipLocations(generalShipInfo) {
                //  TODO - overlap with setup
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
                return shipLocations;
            }

            tbsShips.ships().then(
                function (generalShipInfo) {
                    tbsShipGrid.initialize($scope.theme, $scope.game, computeShipLocations(generalShipInfo), $scope.game.maskedPlayersState.consolidatedOpponentView.table, function () {
                    });
                },
                function () {
                    //  TODO
                }
            );

        }
    ]
);