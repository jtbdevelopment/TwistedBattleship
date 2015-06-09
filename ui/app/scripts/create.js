'use strict';

angular.module('tbs').controller('CreateGameCtrl',
    ['$scope', 'jtbGameCache', 'tbsGameFeatureService', 'jtbPlayerService', '$http', '$location', '$ionicModal',// 'twAds',
        function ($scope, jtbGameCache, tbsGameFeatureService, jtbPlayerService, $http, $location, $ionicModal/*, twAds*/) {

            function calcSubmitEnabled() {
                $scope.submitEnabled = ($scope.playerChoices.length > 0) && ($scope.playerChoices.length < 6);
            }

            $ionicModal.fromTemplateUrl('help-modal.html', {
                scope: $scope,
                animation: 'slide-in-up'
            }).then(function (modal) {
                $scope.modal = modal;
                $scope.helpIndex = 0;
            });

            $scope.alerts = [];
            $scope.featureData = [];
            $scope.currentOptions = [];
            tbsGameFeatureService.features().then(function (data) {
                $scope.featureData = data;
                $scope.currentOptions = [];
                var count = 0;
                angular.forEach($scope.featureData, function (feature) {
                    feature.index = count++;
                    var defaultOption = feature.options[0];
                    $scope.currentOptions.push(defaultOption.feature.indexOf('Enabled') >= 0);
                    feature.checkBox = defaultOption.feature.indexOf('Enabled') >= 0 || defaultOption.feature.indexOf('Disabled') >= 0;
                });
            }, function () {
                //  TODO
                $location.path('/error');
            });

            $scope.friends = [];
            //  TODO
            $scope.invitableFBFriends = [];
            jtbPlayerService.currentPlayerFriends().then(function (data) {
                angular.forEach(data.maskedFriends, function (displayName, hash) {
                    var friend = {
                        md5: hash,
                        displayName: displayName
                    };
                    $scope.friends.push(friend);
                });
                if (jtbPlayerService.currentPlayer().source === 'facebook') {
                    angular.forEach(data.invitableFriends, function (friend) {
                        var invite = {
                            id: friend.id,
                            name: friend.name
                        };
                        if (angular.isDefined(friend.picture) && angular.isDefined(friend.picture.url)) {
                            invite.url = friend.picture.url;
                        }
                        $scope.invitableFBFriends.push(invite);
                    });
                }
            }, function () {
                //  TODO
                $location.path('/error');
            });

            $scope.friend = {};
            $scope.submitEnabled = false;
            $scope.playerChoices = [];
            $scope.$watchCollection('playerChoices', calcSubmitEnabled);
            $scope.queryFriends = function (query) {
                var match = [];
                //  TODO - filter existing choices
                angular.forEach($scope.friends, function (friend) {
                    if (friend.displayName.search(new RegExp(query, 'i')) >= 0) {
                        match.push(friend);
                    }
                });
                return match;
            };

            $scope.clearPlayers = function () {
                $scope.playerChoices = [];
                calcSubmitEnabled();
            };

            $scope.previousHelp = function () {
                $scope.helpIndex--;
                if ($scope.helpIndex < 0) {
                    $scope.helpIndex = $scope.currentOptions.length - 1;
                }
            };

            $scope.nextHelp = function () {
                $scope.helpIndex++;
                if ($scope.helpIndex === $scope.currentOptions.length) {
                    $scope.helpIndex = 0;
                }
            };

            $scope.showHelp = function () {
                $scope.helpIndex = 0;
                $scope.modal.show();
            };

            $scope.closeHelp = function () {
                $scope.modal.hide();
            };

            $scope.$on('$destroy', function () {
                $scope.modal.remove();
            });

            $scope.createGame = function () {
                //  TODO
//                twAds.showAdPopup().result.then(function () {
                var featureNames = ['wordPhraseSetter', 'desiredPlayerCount', 'thieving', 'drawGallows', 'drawFace', 'gamePace', 'winners'];
                var featureSet = [];
                featureSet = featureSet.concat(featureNames.map(function (name) {
                        var data = $scope[name];
                        if ((angular.isDefined(data)) && (data !== '')) {
                            return data;
                        }
                        return '';
                    }
                ).filter(function (item) {
                        return item !== '';
                    }));

                var players = $scope.playerChoices.map(function (player) {
                    return player.md5;
                });
                var playersAndFeatures = {'players': players, 'features': featureSet};
                $http.post(jtbPlayerService.currentPlayerBaseURL() + '/new', playersAndFeatures).success(function (data) {
                    jtbGameCache.putUpdatedGame(data);
                    $location.path('/show/' + data.id);
                }).error(function (data, status, headers, config) {
                    $scope.alerts.push({type: 'danger', msg: 'Error creating game:' + data});
                    console.error(data + status + headers + config);
                });
//                });
            };

            //  TODO
            $scope.showInvite = function () {
                $ionicModal.open({
                    templateUrl: 'views/inviteDialog.html',
                    controller: 'CoreInviteCtrl',
                    size: 'lg',
                    resolve: {
                        invitableFriends: function () {
                            return $scope.invitableFBFriends;
                        }
                    }
                });
            };

            //  TODO
            $scope.closeAlert = function (index) {
                if (angular.isDefined(index) && index >= 0 && index < $scope.alerts.length) {
                    $scope.alerts.splice(index, 1);
                }
            };
        }
    ]
);
