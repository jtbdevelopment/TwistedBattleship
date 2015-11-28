'use strict';

var MAX_OPPONENTS = 5;

angular.module('tbs.controllers').controller('CreateGameCtrl',
    ['friends', 'features', '$scope', 'jtbGameCache', 'jtbPlayerService', 'jtbFacebook', '$http', '$state',
        'tbsGameDetails', '$ionicModal', '$ionicHistory', '$ionicLoading', '$ionicPopup', '$ionicSlideBoxDelegate', // 'twAds',
        function (friends, features, $scope, jtbGameCache, jtbPlayerService, jtbFacebook, $http, $state,
                  tbsGameDetails, $ionicModal, $ionicHistory, $ionicLoading, $ionicPopup, $ionicSlideBoxDelegate /*, twAds*/) {

            $scope.gameDetails = tbsGameDetails;
            $scope.featureData = features;

            $scope.playerChoices = [];
            $scope.inviteArray = [];
            $scope.friendInputs = [];
            $scope.friends = [];
            $scope.invitableFriends = [];
            for (var i = 0; i < MAX_OPPONENTS; ++i) {
                $scope.inviteArray.push(i);
                $scope.playerChoices.push({});
                $scope.friendInputs.push([]);

            }

            $scope.currentOptions = [];
            $scope.submitEnabled = false;

            var count = 0;
            angular.forEach($scope.featureData, function (feature) {
                feature.index = count++;
                var defaultOption = feature.options[0];
                feature.checkBox = defaultOption.feature.indexOf('Enabled') >= 0 || defaultOption.feature.indexOf('Disabled') >= 0;
                feature.groupType = feature.feature.groupType;
                if (feature.checkBox) {
                    $scope.currentOptions.push(defaultOption.feature.indexOf('Enabled') >= 0);
                } else {
                    $scope.currentOptions.push(defaultOption.feature);
                }
            });
            angular.forEach(friends.maskedFriends, function (displayName, hash) {
                var friend = {
                    md5: hash,
                    displayName: displayName,
                    checked: false
                };
                $scope.friends.push(friend);
            });
            angular.forEach($scope.inviteArray, function (index) {
                angular.copy($scope.friends, $scope.friendInputs[index]);
            });

            $scope.playersChanged = function () {
                var chosenPlayers = $scope.playerChoices.filter(function (value) {
                    return angular.isDefined(value.md5) && value.md5 != "";
                });
                $scope.submitEnabled = chosenPlayers.length > 0;
                /*
                 for(var i = 0; i < $scope.inviteArray.length; ++i) {
                 var otherChosenPlayers = $scope.playerChoices.filter(function(value, index) {
                 return index != i && angular.isDefined(value.md5) && value.md5 != "";
                 });
                 var thisChosenPlayer = $scope.playerChoices.filter(function(value, index) {
                 return index === i;
                 })[0];
                 var otherChosenMD5s = [];
                 angular.forEach(otherChosenPlayers, function(otherChosenPlayer) {
                 otherChosenMD5s.push(otherChosenPlayer.md5);
                 });
                 var newFriends = $scope.friends.filter(function(value){
                 return otherChosenMD5s.indexOf(value.md5) < 0;
                 });
                 console.log('B' + i + ':' + JSON.stringify($scope.friendInputs[i]));
                 console.log('B' + i + ':' + JSON.stringify(thisChosenPlayer));
                 if(angular.isDefined(thisChosenPlayer) && angular.isDefined(thisChosenPlayer.md5)) {
                 var selected = newFriends.filter(function(value) {
                 return value.md5 === thisChosenPlayer.md5;
                 });
                 selected[0].checked = true;
                 }
                 console.log('A' + i + ':' + JSON.stringify(newFriends));
                 angular.copy(newFriends, $scope.friendInputs[i]);
                 console.log('A' + i + ':' + JSON.stringify($scope.friendInputs[i]));
                 console.log('A' + i + ':' + JSON.stringify(thisChosenPlayer));
                 console.log($scope.playerChoices);
                 }
                 */
            };


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
            }

            $ionicModal.fromTemplateUrl('templates/help/help-create.html', {
                scope: $scope,
                animation: 'slide-in-up'
            }).then(function (modal) {
                $scope.helpModal = modal;
                $scope.helpIndex = 0;
            });
            $ionicModal.fromTemplateUrl('templates/friends/invite.html', {
                scope: $scope,
                animation: 'slide-in-up'
            }).then(function (modal) {
                $scope.inviteModal = modal;
            });

            $scope.next = function () {
                $ionicSlideBoxDelegate.next();
            };
            $scope.previous = function () {
                $ionicSlideBoxDelegate.previous();
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

            $scope.showInviteFriends = function () {
                $scope.inviteModal.show();
            };

            $scope.inviteFriends = function (friendsToInvite) {
                var ids = [];
                angular.forEach(friendsToInvite, function (friend) {
                    ids.push(friend.id);
                });
                jtbFacebook.inviteFriends(ids, 'Come play Twisted Naval Battles with me!');
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

                var players = $scope.playerChoices.filter(function (value) {
                    return angular.isDefined(value.md5) && value.md5 != "";
                }).map(function (player) {
                    return player.md5;
                }).filter(function (value, index, self) {
                    return self.indexOf(value) == index;
                });
                var playersAndFeatures = {'players': players, 'features': features};
                $ionicLoading.show({
                    template: 'Creating game and issuing challenges..'
                });
                $http.post(jtbPlayerService.currentPlayerBaseURL() + '/new', playersAndFeatures).success(function (data) {
                    $ionicLoading.hide();
                    jtbGameCache.putUpdatedGame(data);
                    $state.go('app.games');
                }).error(function (data, status, headers, config) {
                    $ionicLoading.hide();
                    $ionicPopup.alert({
                        title: 'There was a problem creating the game!',
                        template: data
                    }).then(function () {
                    });

                    console.error(data + status + headers + config);
                });
            };
        }
    ]
);
