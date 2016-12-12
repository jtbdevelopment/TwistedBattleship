'use strict';

describe('Controller: CreateGameCtrl', function () {
    beforeEach(module('tbs.controllers'));

    var ctrl, stateSpy, $rootScope, $scope, $q;
    var currentPlayer = {id: 'myid', source: 'facebook'};

    var $http;
    var gameCache, facebook, playerService;
    var $ionicModal, $ionicLoading, $ionicPopup, $ionicSlideBox;
    var features = [
        {
            'feature': {
                'groupType': 'Difficulty',
                'feature': 'GridSize',
                'label': 'Grid',
                'description': 'Size of the ocean each player is in.'
            },
            'options': [{
                'groupType': 'Difficulty',
                'feature': 'Grid10x10',
                'label': '10x10',
                'description': '10 x 10 square grid'
            }, {
                'groupType': 'Difficulty',
                'feature': 'Grid15x15',
                'label': '15x15',
                'description': '15 x 15 square grid'
            }, {
                'groupType': 'Difficulty',
                'feature': 'Grid20x20',
                'label': '20x20',
                'description': '20 x 20 square grid'
            }]
        }, {
            'feature': {
                'groupType': 'Difficulty',
                'feature': 'ActionsPerTurn',
                'label': 'Actions Per Turn',
                'description': 'How many actions can a player take per turn?'
            },
            'options': [{
                'groupType': 'Difficulty',
                'feature': 'PerShip',
                'label': 'Per Ship',
                'description': 'A player has as many action points as they have ships.  Firing takes 1 action point, specials (ECM, Spying, etc.) take 2.'
            }, {
                'groupType': 'Difficulty',
                'feature': 'Single',
                'label': 'Single Action',
                'description': 'A player can fire a single shot or take a single special action (ECM, Spying, etc) per turn.'
            }]
        }, {
            'feature': {
                'groupType': 'Difficulty',
                'feature': 'FogOfWar',
                'label': 'Fog of War',
                'description': 'How much information is shared between players?'
            },
            'options': [{
                'groupType': 'Difficulty',
                'feature': 'SharedIntel',
                'label': 'Shared Intel',
                'description': 'Players can see the results of all actions taken by all players.'
            }, {
                'groupType': 'Difficulty',
                'feature': 'IsolatedIntel',
                'label': 'Isolated Views',
                'description': 'Each player can only see the impact of their own actions and the defensive actions of other players.'
            }]
        }, {
            'feature': {
                'groupType': 'Defensive',
                'feature': 'ECM',
                'label': 'Electronic Countermeasures',
                'description': 'ECM devices allow player to scramble opponent views of their ocean.'
            },
            'options': [{
                'groupType': 'Defensive',
                'feature': 'ECMEnabled',
                'label': 'Enabled',
                'description': 'Players can use ECM device to cloak their grid.  Bigger grids hide bigger areas.'
            }, {
                'groupType': 'Defensive',
                'feature': 'ECMDisabled',
                'label': 'Disabled',
                'description': 'ECM option disabled.'
            }]
        }, {
            'feature': {
                'groupType': 'Defensive',
                'feature': 'EvasiveManeuvers',
                'label': 'Evasive Maneuvers',
                'description': 'Allows defensive evasive maneuvers of a ship.'
            },
            'options': [{
                'groupType': 'Defensive',
                'feature': 'EMEnabled',
                'label': 'Enabled',
                'description': 'The captain will take emegency evasive actions, moving a few spaces away and possibly turning 90 degrees.  This also scrambles opponent records of the area somewhat randomly.'
            }, {
                'groupType': 'Defensive',
                'feature': 'EMDisabled',
                'label': 'Disabled',
                'description': 'Evasive maneuvers disabled.'
            }]
        }, {
            'feature': {
                'groupType': 'Defensive',
                'feature': 'EmergencyRepairs',
                'label': 'Emergency Repairs',
                'description': 'Allows a damaged ship to be repaired.'
            },
            'options': [{
                'groupType': 'Defensive',
                'feature': 'EREnabled',
                'label': 'Enabled',
                'description': 'Ship crew are able to salvage parts to repair a ship to full sea-worthiness.'
            }, {
                'groupType': 'Defensive',
                'feature': 'ERDisabled',
                'label': 'Disabled',
                'description': 'Emergency repairs disabled.'
            }]
        }, {
            'feature': {
                'groupType': 'Offensive',
                'feature': 'Spy',
                'label': 'Spying',
                'description': "Allows player's drones to spy on opponents."
            },
            'options': [{
                'groupType': 'Offensive',
                'feature': 'SpyEnabled',
                'label': 'Enabled',
                'description': "Players can use spy drones to get a glimpse of an area of an opponent's grid.  Bigger grids show more area."
            }, {
                'groupType': 'Offensive',
                'feature': 'SpyDisabled',
                'label': 'Disabled',
                'description': 'Spy drones disabled.'
            }]
        }, {
            'feature': {
                'groupType': 'Offensive',
                'feature': 'CruiseMissile',
                'label': 'Cruise Missile',
                'description': 'Single use attack that sinks a ship with a single hit on any location.'
            },
            'options': [{
                'groupType': 'Offensive',
                'feature': 'CruiseMissileDisabled',
                'label': 'Disabled',
                'description': 'Cruise missile disabled.'
            }, {
                'groupType': 'Offensive',
                'feature': 'CruiseMissileEnabled',
                'label': 'Enabled',
                'description': 'Single use per game, sinks a ship by hitting any single ship location.'
            }]
        }];
    var friendsPromise, modalPromiseHelp, modalPromiseInvite, alertPromise;
    var helpModal, inviteModal;
    var playerUrl = 'http://game.com/u/r/l';
    var defaultOptions = ['Grid10x10', 'PerShip', 'SharedIntel', true, true, true, true, false];
    var friends = {
        'maskedFriends': {
            md1: 'friend1',
            md3: 'friend3',
            md2: 'friend2'
        },
        'invitableFriends': [
            {id: 'i1', name: 'invite1', picture: {url: 'http://aPic'}},
            {id: 'i2', name: 'invite2'}
        ]
    };
    beforeEach(inject(function (_$rootScope_, $controller, _$q_, $httpBackend) {
        $http = $httpBackend;
        stateSpy = {go: sinon.spy()};
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
        $q = _$q_;
        modalPromiseHelp = $q.defer();
        modalPromiseInvite = $q.defer();
        alertPromise = $q.defer();
        $ionicModal = {fromTemplateUrl: sinon.stub()};
        $ionicModal.fromTemplateUrl.withArgs(
            sinon.match('templates/help/help-create.html'),
            sinon.match(function (val) {
                return $scope == val.scope && val.animation === 'slide-in-up'
            })
        ).returns(modalPromiseHelp.promise);
        $ionicModal.fromTemplateUrl.withArgs(
            sinon.match('templates/friends/invite.html'),
            sinon.match(function (val) {
                return $scope == val.scope && val.animation === 'slide-in-up'
            })
        ).returns(modalPromiseInvite.promise);

        helpModal = {hide: sinon.spy(), show: sinon.spy(), remove: sinon.spy()};
        inviteModal = {hide: sinon.spy(), show: sinon.spy(), remove: sinon.spy()};
        $ionicLoading = {show: sinon.spy(), hide: sinon.spy()};
        $ionicPopup = {alert: sinon.spy()};
        $ionicSlideBox = {next: sinon.spy(), previous: sinon.spy()};
        friendsPromise = $q.defer();
        facebook = {inviteFriends: sinon.spy()};
        gameCache = {putUpdatedGame: sinon.spy()};
        playerService = {
            initializeFriendsForController: function () {
                return friendsPromise.promise;
            },
            currentPlayer: function () {
                return currentPlayer;
            },
            currentPlayerBaseURL: function () {
                return playerUrl;
            }
        };


        ctrl = $controller('CreateGameCtrl', {
            $scope: $scope,
            $state: stateSpy,
            jtbPlayerService: playerService,
            jtbGameCache: gameCache,
            jtbFacebook: facebook,
            $ionicModal: $ionicModal,
            $ionicLoading: $ionicLoading,
            $ionicPopup: $ionicPopup,
            $ionicSlideBoxDelegate: $ionicSlideBox,
            features: features
        });
        expect($scope.create).to.equal(ctrl);
    }));

    it('initializes', function () {
        expect(ctrl.helpModal).to.be.undefined;
        expect(ctrl.inviteModal).to.be.undefined;
        expect(ctrl.helpIndex).to.be.undefined;

        expect([0, 1, 2, 3, 4]).to.deep.equal(ctrl.inviteArray);
        expect([{}, {}, {}, {}, {}]).to.deep.equal(ctrl.playerChoices);
        expect([[], [], [], [], []]).to.deep.equal(ctrl.friendInputs);
        expect([]).to.deep.equal(ctrl.friends);
        expect([]).to.deep.equal(ctrl.invitableFBFriends);  // not testing after - responsibility of services
        modalPromiseHelp.resolve(helpModal);
        modalPromiseInvite.resolve(inviteModal);

        ctrl.friends = [
            {
                "md5": "md1",
                "displayName": "friend1"
            },
            {
                "md5": "md3",
                "displayName": "friend3"
            },
            {
                "md5": "md2",
                "displayName": "friend2"
            }
        ];
        friendsPromise.resolve(friends);
        $rootScope.$apply();

        expect([{}, {}, {}, {}, {}]).to.deep.equal(ctrl.playerChoices);
        expect([
            [
                {
                    "md5": "md1",
                    "displayName": "friend1",
                    "checked": false
                },
                {
                    "md5": "md3",
                    "displayName": "friend3",
                    "checked": false
                },
                {
                    "md5": "md2",
                    "displayName": "friend2",
                    "checked": false
                }
            ],
            [
                {
                    "md5": "md1",
                    "displayName": "friend1",
                    "checked": false
                },
                {
                    "md5": "md3",
                    "displayName": "friend3",
                    "checked": false
                },
                {
                    "md5": "md2",
                    "displayName": "friend2",
                    "checked": false
                }
            ],
            [
                {
                    "md5": "md1",
                    "displayName": "friend1",
                    "checked": false
                },
                {
                    "md5": "md3",
                    "displayName": "friend3",
                    "checked": false
                },
                {
                    "md5": "md2",
                    "displayName": "friend2",
                    "checked": false
                }
            ],
            [
                {
                    "md5": "md1",
                    "displayName": "friend1",
                    "checked": false
                },
                {
                    "md5": "md3",
                    "displayName": "friend3",
                    "checked": false
                },
                {
                    "md5": "md2",
                    "displayName": "friend2",
                    "checked": false
                }
            ],
            [
                {
                    "md5": "md1",
                    "displayName": "friend1",
                    "checked": false
                },
                {
                    "md5": "md3",
                    "displayName": "friend3",
                    "checked": false
                },
                {
                    "md5": "md2",
                    "displayName": "friend2",
                    "checked": false
                }
            ]
        ]).to.deep.equal(ctrl.friendInputs);
        expect([
            {
                'md5': 'md1',
                'displayName': 'friend1',
                'checked': false
            },
            {
                'md5': 'md3',
                'displayName': 'friend3',
                'checked': false
            },
            {
                'md5': 'md2',
                'displayName': 'friend2',
                'checked': false
            }]).to.deep.equal(ctrl.friends);

        expect(ctrl.featureData).to.equal(features);
        expect(ctrl.currentOptions).to.deep.equal(defaultOptions);
        expect(ctrl.inviteModal).to.equal(inviteModal);
        expect(ctrl.helpModal).to.equal(helpModal);
        expect(ctrl.helpIndex).to.equal(0);
        expect(ctrl.submitEnabled).to.be.false;
    });

    describe('nav buttons on main screen, invite, and help', function () {
        beforeEach(function () {
            modalPromiseHelp.resolve(helpModal);
            modalPromiseInvite.resolve(inviteModal);
            $rootScope.$apply();
        });

        it('next on main screen', function () {
            ctrl.next();
            assert($ionicSlideBox.next.calledWithMatch());
        });
        it('previous on main screen', function () {
            ctrl.previous();
            assert($ionicSlideBox.previous.calledWithMatch());
        });

        it('previous help on first help page', function () {
            ctrl.helpIndex = 0;
            ctrl.previousHelp();
            expect(ctrl.helpIndex).to.equal(defaultOptions.length - 1);
        });

        it('previous help on non first help page', function () {
            ctrl.helpIndex = 1;
            ctrl.previousHelp();
            expect(ctrl.helpIndex).to.equal(0);
        });

        it('previous help on last help page', function () {
            ctrl.helpIndex = defaultOptions.length - 1;
            ctrl.nextHelp();
            expect(ctrl.helpIndex).to.equal(0);
        });

        it('previous help on non last help page', function () {
            ctrl.helpIndex = 1;
            ctrl.nextHelp();
            expect(ctrl.helpIndex).to.equal(2);
        });

        it('show help', function () {
            ctrl.helpIndex = 2;
            ctrl.showHelp();
            assert(helpModal.show.calledWithMatch());
            expect(ctrl.helpIndex).to.equal(0);
        });

        it('hide help', function () {
            ctrl.closeHelp();
            assert(helpModal.hide.calledWithMatch());
        });

        it('show invite friends', function () {
            ctrl.showInviteFriends();
            assert(inviteModal.show.calledWithMatch());
        });

        it('close invite friends', function () {
            ctrl.cancelInviteFriends();
            assert(inviteModal.hide.calledWithMatch());
        });

        it('invites friends', function () {
            var friendsToInvite = [{id: '1'}, {id: '2', other: 'ignore'}, {id: '3'}];
            ctrl.inviteFriends(friendsToInvite);
            assert(facebook.inviteFriends.calledWithMatch(['1', '2', '3'], 'Come play Twisted Naval Battles with me!'));
            assert(inviteModal.hide.calledWithMatch());
        });
        it('on $destroy, destroys modals', function () {
            $rootScope.$broadcast('$destroy');
            assert(inviteModal.remove.calledWithMatch());
            assert(helpModal.remove.calledWithMatch());
        });
    });

    describe('choosing and unchoosing opponents', function () {
        beforeEach(function () {
            modalPromiseHelp.resolve(helpModal);
            modalPromiseInvite.resolve(inviteModal);
            ctrl.friends = [
                {
                    "md5": "md1",
                    "displayName": "friend1"
                },
                {
                    "md5": "md3",
                    "displayName": "friend3"
                },
                {
                    "md5": "md2",
                    "displayName": "friend2"
                }
            ];
            friendsPromise.resolve(friends);

            $rootScope.$apply();
        });

        it('choosing a player removes from other lists and enables submit', function () {
            expect([{}, {}, {}, {}, {}]).to.deep.equal(ctrl.playerChoices);
            expect([
                [
                    {
                        "md5": "md1",
                        "displayName": "friend1",
                        "checked": false
                    },
                    {
                        "md5": "md3",
                        "displayName": "friend3",
                        "checked": false
                    },
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ],
                [
                    {
                        "md5": "md1",
                        "displayName": "friend1",
                        "checked": false
                    },
                    {
                        "md5": "md3",
                        "displayName": "friend3",
                        "checked": false
                    },
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ],
                [
                    {
                        "md5": "md1",
                        "displayName": "friend1",
                        "checked": false
                    },
                    {
                        "md5": "md3",
                        "displayName": "friend3",
                        "checked": false
                    },
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ],
                [
                    {
                        "md5": "md1",
                        "displayName": "friend1",
                        "checked": false
                    },
                    {
                        "md5": "md3",
                        "displayName": "friend3",
                        "checked": false
                    },
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ],
                [
                    {
                        "md5": "md1",
                        "displayName": "friend1",
                        "checked": false
                    },
                    {
                        "md5": "md3",
                        "displayName": "friend3",
                        "checked": false
                    },
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ]
            ]).to.deep.equal(ctrl.friendInputs);

            ctrl.playerChoices[2] = {
                "md5": "md3",
                "displayName": "friend3",
                "checked": false
            };
            expect(ctrl.submitEnabled).to.be.false;
            ctrl.playersChanged();

            expect(ctrl.submitEnabled).to.be.true;

            expect([
                [
                    {
                        "md5": "md1",
                        "displayName": "friend1",
                        "checked": false
                    },
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ],
                [
                    {
                        "md5": "md1",
                        "displayName": "friend1",
                        "checked": false
                    },
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ],
                [
                    {
                        "md5": "md1",
                        "displayName": "friend1",
                        "checked": false
                    },
                    {
                        "md5": "md3",
                        "displayName": "friend3",
                        "checked": true
                    },
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ],
                [
                    {
                        "md5": "md1",
                        "displayName": "friend1",
                        "checked": false
                    },
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ],
                [
                    {
                        "md5": "md1",
                        "displayName": "friend1",
                        "checked": false
                    },
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ]
            ]).to.deep.equal(ctrl.friendInputs);
        });

        it('choosing a player and then unchoosing them puts them back on other lists', function () {
            expect([{}, {}, {}, {}, {}]).to.deep.equal(ctrl.playerChoices);
            expect([
                [
                    {
                        "md5": "md1",
                        "displayName": "friend1",
                        "checked": false
                    },
                    {
                        "md5": "md3",
                        "displayName": "friend3",
                        "checked": false
                    },
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ],
                [
                    {
                        "md5": "md1",
                        "displayName": "friend1",
                        "checked": false
                    },
                    {
                        "md5": "md3",
                        "displayName": "friend3",
                        "checked": false
                    },
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ],
                [
                    {
                        "md5": "md1",
                        "displayName": "friend1",
                        "checked": false
                    },
                    {
                        "md5": "md3",
                        "displayName": "friend3",
                        "checked": false
                    },
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ],
                [
                    {
                        "md5": "md1",
                        "displayName": "friend1",
                        "checked": false
                    },
                    {
                        "md5": "md3",
                        "displayName": "friend3",
                        "checked": false
                    },
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ],
                [
                    {
                        "md5": "md1",
                        "displayName": "friend1",
                        "checked": false
                    },
                    {
                        "md5": "md3",
                        "displayName": "friend3",
                        "checked": false
                    },
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ]
            ]).to.deep.equal(ctrl.friendInputs);

            ctrl.playerChoices[2] = {
                "md5": "md3",
                "displayName": "friend3",
                "checked": false
            };
            ctrl.playerChoices[4] = {
                "md5": "md1",
                "displayName": "friend1",
                "checked": false
            };
            expect(ctrl.submitEnabled).to.be.false;

            ctrl.playersChanged();

            expect(ctrl.submitEnabled).to.be.true;
            expect([
                [
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ],
                [
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ],
                [
                    {
                        "md5": "md3",
                        "displayName": "friend3",
                        "checked": true
                    },
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ],
                [
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ],
                [
                    {
                        "md5": "md1",
                        "displayName": "friend1",
                        "checked": true
                    },
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ]
            ]).to.deep.equal(ctrl.friendInputs);

            ctrl.playerChoices[4] = {};
            ctrl.playersChanged();
            expect(ctrl.submitEnabled).to.be.true;

            expect([
                [
                    {
                        "md5": "md1",
                        "displayName": "friend1",
                        "checked": false
                    },
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ],
                [
                    {
                        "md5": "md1",
                        "displayName": "friend1",
                        "checked": false
                    },
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ],
                [
                    {
                        "md5": "md1",
                        "displayName": "friend1",
                        "checked": false
                    },
                    {
                        "md5": "md3",
                        "displayName": "friend3",
                        "checked": true
                    },
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ],
                [
                    {
                        "md5": "md1",
                        "displayName": "friend1",
                        "checked": false
                    },
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ],
                [
                    {
                        "md5": "md1",
                        "displayName": "friend1",
                        "checked": false
                    },
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ]
            ]).to.deep.equal(ctrl.friendInputs);

            ctrl.playerChoices[2] = {};
            ctrl.playersChanged();
            expect(ctrl.submitEnabled).to.be.false;
            expect([
                [
                    {
                        "md5": "md1",
                        "displayName": "friend1",
                        "checked": false
                    },
                    {
                        "md5": "md3",
                        "displayName": "friend3",
                        "checked": false
                    },
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ],
                [
                    {
                        "md5": "md1",
                        "displayName": "friend1",
                        "checked": false
                    },
                    {
                        "md5": "md3",
                        "displayName": "friend3",
                        "checked": false
                    },
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ],
                [
                    {
                        "md5": "md1",
                        "displayName": "friend1",
                        "checked": false
                    },
                    {
                        "md5": "md3",
                        "displayName": "friend3",
                        "checked": false
                    },
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ],
                [
                    {
                        "md5": "md1",
                        "displayName": "friend1",
                        "checked": false
                    },
                    {
                        "md5": "md3",
                        "displayName": "friend3",
                        "checked": false
                    },
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ],
                [
                    {
                        "md5": "md1",
                        "displayName": "friend1",
                        "checked": false
                    },
                    {
                        "md5": "md3",
                        "displayName": "friend3",
                        "checked": false
                    },
                    {
                        "md5": "md2",
                        "displayName": "friend2",
                        "checked": false
                    }
                ]
            ]).to.deep.equal(ctrl.friendInputs);
        });
    });

    describe('submitting game', function () {
        beforeEach(function () {
            friendsPromise.resolve(friends);
            modalPromiseHelp.resolve(helpModal);
            modalPromiseInvite.resolve(inviteModal);
            $rootScope.$apply();
        });

        it('submit a game with friends and default options', function () {
            ctrl.playerChoices[2] = {
                "md5": "md3",
                "displayName": "friend3",
                "checked": false
            };
            ctrl.playerChoices[4] = {
                "md5": "md1",
                "displayName": "friend1",
                "checked": false
            };


            var expectedOptions = {
                'players': ['md3', 'md1'],
                'features': ['Grid15x15', 'PerShip', 'IsolatedIntel', 'ECMEnabled', 'EMEnabled', 'EREnabled', 'SpyDisabled', 'CruiseMissileEnabled']
            };
            var newGame = {id: 'someid'};
            ctrl.currentOptions = ['Grid15x15', 'PerShip', 'IsolatedIntel', true, true, true, false, true];

            $http.expectPOST(playerUrl + '/new', expectedOptions).respond(200, newGame);
            ctrl.createGame();
            assert($ionicLoading.show.calledWithMatch({
                template: 'Creating game and issuing challenges..'
            }));
            $http.flush();
            assert(gameCache.putUpdatedGame.calledWithMatch(newGame));
            assert($ionicLoading.hide.calledWithMatch());
            assert(stateSpy.go.calledWithMatch('app.games'));
        });

        it('submit a game with friends and non-default options', function () {
            ctrl.playerChoices[2] = {
                "md5": "md3",
                "displayName": "friend3",
                "checked": false
            };
            ctrl.playerChoices[4] = {
                "md5": "md1",
                "displayName": "friend1",
                "checked": false
            };


            var expectedOptions = {
                'players': ['md3', 'md1'],
                'features': ['Grid10x10', 'PerShip', 'SharedIntel', 'ECMEnabled', 'EMEnabled', 'EREnabled', 'SpyEnabled', 'CruiseMissileDisabled']
            };
            var newGame = {id: 'someid'};
            $http.expectPOST(playerUrl + '/new', expectedOptions).respond(200, newGame);
            ctrl.createGame();
            assert($ionicLoading.show.calledWithMatch({
                template: 'Creating game and issuing challenges..'
            }));
            $http.flush();
            assert(gameCache.putUpdatedGame.calledWithMatch(newGame));
            assert($ionicLoading.hide.calledWithMatch());
            assert(stateSpy.go.calledWithMatch('app.games'));
        });

        it('submit a game with friends and default options and fail', function () {
            ctrl.playerChoices[2] = {
                "md5": "md3",
                "displayName": "friend3",
                "checked": false
            };
            ctrl.playerChoices[4] = {
                "md5": "md1",
                "displayName": "friend1",
                "checked": false
            };


            var expectedOptions = {
                'players': ['md3', 'md1'],
                'features': ['Grid10x10', 'PerShip', 'SharedIntel', 'ECMEnabled', 'EMEnabled', 'EREnabled', 'SpyEnabled', 'CruiseMissileDisabled']
            };
            var error = 'Its a problem';
            $http.expectPOST(playerUrl + '/new', expectedOptions).respond(400, error);
            ctrl.createGame();
            assert($ionicLoading.show.calledWithMatch({
                template: 'Creating game and issuing challenges..'
            }));
            $http.flush();
            assert($ionicLoading.hide.calledWithMatch());
            $ionicPopup.alert.calledWithMatch({
                title: 'There was a problem creating the game!',
                template: error
            });
        });
    });
});