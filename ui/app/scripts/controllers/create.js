'use strict';

var MAX_OPPONENTS = 5;

angular.module('tbs.controllers').controller('CreateGameCtrl',
    ['features', '$scope', 'jtbGameCache', 'jtbPlayerService', 'jtbFacebook', '$http', '$state',
        '$ionicModal', '$ionicLoading', '$ionicPopup', '$ionicSlideBoxDelegate',
        function (features, $scope, jtbGameCache, jtbPlayerService, jtbFacebook, $http, $state,
                  $ionicModal, $ionicLoading, $ionicPopup, $ionicSlideBoxDelegate) {
            var controller = this;
            controller.featureData = features;

            controller.playerChoices = [];
            controller.inviteArray = [];
            controller.friendInputs = [];
            controller.friends = [];
            controller.invitableFriends = [];
            for (var i = 0; i < MAX_OPPONENTS; ++i) {
                controller.inviteArray.push(i);
                controller.playerChoices.push({});
                controller.friendInputs.push([]);

            }

            controller.currentOptions = [];
            controller.submitEnabled = false;

            var count = 0;
            angular.forEach(controller.featureData, function (feature) {
                feature.index = count++;
                var defaultOption = feature.options[0];
                feature.checkBox = defaultOption.feature.indexOf('Enabled') >= 0 || defaultOption.feature.indexOf('Disabled') >= 0;
                feature.groupType = feature.feature.groupType;
                if (feature.checkBox) {
                    controller.currentOptions.push(defaultOption.feature.indexOf('Enabled') >= 0);
                } else {
                    controller.currentOptions.push(defaultOption.feature);
                }
            });

            jtbPlayerService.currentPlayerFriends().then(function (friends) {
                angular.forEach(friends.maskedFriends, function (displayName, hash) {
                    var friend = {
                        md5: hash,
                        displayName: displayName,
                        checked: false
                    };
                    controller.friends.push(friend);
                });
                angular.forEach(controller.inviteArray, function (index) {
                    angular.copy(controller.friends, controller.friendInputs[index]);
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
                        controller.invitableFriends.push(invite);
                    });
                }
            });

            function validMD5(value) {
                return angular.isDefined(value.md5) && value.md5 !== '';
            }

            controller.playersChanged = function () {
                var chosenPlayers = controller.playerChoices.filter(validMD5);
                controller.submitEnabled = chosenPlayers.length > 0;

                angular.forEach(controller.inviteArray, function (index) {
                    var otherMD5s = controller.playerChoices.filter(function (value, valueIndex) {
                        return index !== valueIndex && validMD5(value);
                    }).map(function (value) {
                        return value.md5;
                    });

                    var updatedList = angular.copy(controller.friends.filter(function (value) {
                        return otherMD5s.indexOf(value.md5) < 0;
                    }));

                    if (validMD5(controller.playerChoices[index])) {
                        var md5 = controller.playerChoices[index].md5;
                        updatedList.filter(function (value) {
                            return value.md5 === md5;
                        })[0].checked = true;
                    }

                    controller.friendInputs[index] = updatedList;
                });

            };

            //  As of Ionic 1.3 cannot controllerAs modals themselves
            $scope.create = controller;
            $ionicModal.fromTemplateUrl('templates/help/help-create.html', {
                scope: $scope,
                animation: 'slide-in-up'
            }).then(function (modal) {
                controller.helpModal = modal;
                controller.helpIndex = 0;
            });
            $ionicModal.fromTemplateUrl('templates/friends/invite.html', {
                scope: $scope,
                animation: 'slide-in-up'
            }).then(function (modal) {
                controller.inviteModal = modal;
            });

            controller.next = function () {
                $ionicSlideBoxDelegate.next();
            };
            controller.previous = function () {
                $ionicSlideBoxDelegate.previous();
            };

            controller.previousHelp = function () {
                controller.helpIndex--;
                if (controller.helpIndex < 0) {
                    controller.helpIndex = controller.currentOptions.length - 1;
                }
            };

            controller.nextHelp = function () {
                controller.helpIndex++;
                if (controller.helpIndex === controller.currentOptions.length) {
                    controller.helpIndex = 0;
                }
            };

            controller.showHelp = function () {
                controller.helpIndex = 0;
                controller.helpModal.show();
            };

            controller.closeHelp = function () {
                controller.helpModal.hide();
            };

            controller.showInviteFriends = function () {
                controller.inviteModal.show();
            };

            controller.inviteFriends = function (friendsToInvite) {
                var ids = [];
                angular.forEach(friendsToInvite, function (friend) {
                    ids.push(friend.id);
                });
                jtbFacebook.inviteFriends(ids, 'Come play Twisted Naval Battles with me!');
                controller.inviteModal.hide();
            };

            controller.cancelInviteFriends = function () {
                controller.inviteModal.hide();
            };

            $scope.$on('$destroy', function () {
                controller.helpModal.remove();
                controller.inviteModal.remove();
            });

            controller.createGame = function () {
                var features = [];
                angular.forEach(controller.featureData, function (feature) {
                    if (feature.checkBox) {
                        var defaultOption = feature.options[0];
                        if (
                            (controller.currentOptions[feature.index] && defaultOption.feature.indexOf('Enabled') >= 0) ||
                            (!controller.currentOptions[feature.index] && defaultOption.feature.indexOf('Enabled') === -1)
                        ) {
                            features.push(defaultOption.feature);
                        } else {
                            features.push(feature.options[1].feature);
                        }
                    } else {
                        features.push(controller.currentOptions[feature.index]);
                    }
                });

                var players = controller.playerChoices.filter(validMD5).map(function (player) {
                    return player.md5;
                }).filter(function (value, index, self) {
                    return self.indexOf(value) === index;
                });
                var playersAndFeatures = {'players': players, 'features': features};
                $ionicLoading.show({
                    template: 'Creating game and issuing challenges..'
                });
                $http.post(jtbPlayerService.currentPlayerBaseURL() + '/new', playersAndFeatures).then(function (response) {
                    $ionicLoading.hide();
                    jtbGameCache.putUpdatedGame(response.data);
                    $state.go('app.games');
                }, function (response) {
                    $ionicLoading.hide();
                    $ionicPopup.alert({
                        title: 'There was a problem creating the game!',
                        template: response.data
                    });

                    console.error(response.data + response.status + response.headers + response.config);
                });
            };
        }
    ]
);
