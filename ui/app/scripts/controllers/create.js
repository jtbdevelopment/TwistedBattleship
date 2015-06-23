'use strict';

angular.module('tbs.controllers').controller('CreateGameCtrl',
    ['friends', 'features', '$scope', 'jtbGameCache', 'jtbPlayerService', 'jtbFacebook', '$http', '$state', '$location', '$ionicModal',// 'twAds',
        function (friends, features, $scope, jtbGameCache, jtbPlayerService, jtbFacebook, $http, $state, $location, $ionicModal/*, twAds*/) {

            $scope.playerChoices = [];

            $scope.playersChanged = function (callback) {
                $scope.playerChoices = callback.selectedItems;
                $scope.submitEnabled = $scope.playerChoices.length > 0 && $scope.playerChoices.length < 6;
            };

            $ionicModal.fromTemplateUrl('help-modal.html', {
                scope: $scope,
                animation: 'slide-in-up'
            }).then(function (modal) {
                $scope.helpModal = modal;
                $scope.helpIndex = 0;
            });
            $ionicModal.fromTemplateUrl('invite-modal.html', {
                scope: $scope,
                animation: 'slide-in-up'
            }).then(function (modal) {
                $scope.inviteModal = modal;
            });

            $scope.alerts = [];
            $scope.featureData = [];
            $scope.currentOptions = [];
            $scope.featureData = features;
            $scope.currentOptions = [];
            var count = 0;
            angular.forEach($scope.featureData, function (feature) {
                feature.index = count++;
                var defaultOption = feature.options[0];
                feature.checkBox = defaultOption.feature.indexOf('Enabled') >= 0 || defaultOption.feature.indexOf('Disabled') >= 0;
                if (feature.checkBox) {
                    $scope.currentOptions.push(defaultOption.feature.indexOf('Enabled') >= 0);
                } else {
                    $scope.currentOptions.push(defaultOption.feature);
                }
            });

            $scope.friends = [];
            //  TODO
            $scope.invitableFriends = [];
            angular.forEach(friends.maskedFriends, function (displayName, hash) {
                var friend = {
                    md5: hash,
                    displayName: displayName
                };
                $scope.friends.push(friend);
            });
            if (jtbPlayerService.currentPlayer().source === 'facebook') {
                angular.forEach(friends.invitableFriends, function (friend) {
                    var invite = {
                        id: friend.id,
                        name: friend.name
                    };
                    if (angular.isDefined(friend.picture) && angular.isDefined(friend.picture.url)) {
                        invite.url = friend.picture.url;
                    }
                    $scope.invitableFriends.push(invite);
                });
            } else {
                $scope.invitableFriends.push({id: 'anid1', name: 'a friend!'});
                $scope.invitableFriends.push({id: 'anid2', name: 'another friend!'});
            }

            $scope.submitEnabled = false;

            $scope.queryInvitableFriends = function (query) {
                var match = [];
                //  TODO - filter existing choices
                angular.forEach($scope.invitableFriends, function (friend) {
                    if (friend.name.search(new RegExp(query, 'i')) >= 0) {
                        match.push(friend);
                    }
                });
                return match;
            };

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
                $scope.helpModal.show();
            };

            $scope.closeHelp = function () {
                $scope.helpModal.hide();
            };

            $scope.friendsToInvite = [];
            $scope.showInviteFriends = function () {
                $scope.inviteModal.show();
            };

            //  TODO - similar scope issue to playerChoices
            $scope.inviteFriends = function (friendsToInvite) {
                jtbFacebook.inviteFriends(friendsToInvite);
                $scope.inviteModal.hide();
            };

            $scope.cancelInviteFriends = function () {
                $scope.inviteModal.hide();
            };

            $scope.$on('$destroy', function () {
                $scope.helpModal.remove();
                $scope.inviteModal.remove();
            });

            $scope.createGame = function () {
                //  TODO
//                twAds.showAdPopup().result.then(function () {
                var features = [];
                angular.forEach($scope.featureData, function (feature) {
                    if (feature.checkBox) {
                        var defaultOption = feature.options[0];
                        if (
                            ($scope.currentOptions[feature.index] && defaultOption.feature.indexOf('Enabled') >= 0) ||
                            (!$scope.currentOptions[feature.index] && defaultOption.feature.indexOf('Enabled') === -1)
                        ) {
                            features.push(defaultOption.feature);
                        } else {
                            features.push(feature.options[1].feature);
                        }
                    } else {
                        features.push($scope.currentOptions[feature.index]);
                    }
                });

                var players = $scope.playerChoices.map(function (player) {
                    return player.md5;
                });
                var playersAndFeatures = {'players': players, 'features': features};
                $http.post(jtbPlayerService.currentPlayerBaseURL() + '/new', playersAndFeatures).success(function (data) {
                    jtbGameCache.putUpdatedGame(data);
                    $state.go('app.game', {gameId: data.id});
                    //TODO
                    //$location.path('/show/' + data.id);
                }).error(function (data, status, headers, config) {
                    //  TODO
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
                            return $scope.invitableFriends;
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
