'use strict';

describe('Controller: MainCtrl', function () {
    beforeEach(module('tbs.controllers'));

    var ctrl, $state, $rootScope, $scope, $timeout, $window, $q;
    var $ionicLoading, $ionicPopup;

    var url;
    $window = {
        location: {
            href: ''
        }
    };
    var tbsGameDetails = {
        x: 1
    };

    var env = {
        apiEndpoint: 'some endpoint'
    };

    var currentPlayer;
    var mockPlayerService = {
        currentPlayer: function () {
            return currentPlayer;
        }
    };

    var $document = {
        resumeFunction: undefined,
        pauseFunction: undefined,
        bind: function (event, fn) {
            if (event === 'pause') {
                this.pauseFunction = fn;
            }
            else {
                this.resumeFunction = fn;
            }
        }
    };
    //  Stuff that is pre-cache stuff
    var pushNotifications = {x: '334'};
    var features, circles, cells, ships, phases, ads, livefeed, jtbIonicVersionNotesService;
    beforeEach(inject(function (_$rootScope_, $controller, _$timeout_, _$q_) {
        url = 'http://xtz.com';
        $window.location.href = url;
        currentPlayer = undefined;
        $state = {go: sinon.spy()};
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
        $q = _$q_;
        $ionicLoading = {show: sinon.spy(), hide: sinon.spy()};
        $ionicPopup = {alert: sinon.spy()};
        $timeout = _$timeout_;
        features = {features: sinon.stub()};
        phases = {phases: sinon.stub()};
        ads = {initialize: sinon.spy()};
        cells = {cellStates: sinon.stub()};
        circles = {circles: sinon.stub()};
        ships = {ships: sinon.stub()};
        livefeed = {setEndPoint: sinon.spy(), suspendFeed: sinon.spy()};
        jtbIonicVersionNotesService = {displayVersionNotesIfAppropriate: sinon.spy()};

        ctrl = $controller('MainCtrl', {
            $scope: $scope,
            $state: $state,
            $document: $document,
            $window: $window,
            tbsAds: ads,
            ENV: env,
            $ionicLoading: $ionicLoading,
            $ionicPopup: $ionicPopup,
            jtbPlayerService: mockPlayerService,
            jtbGameFeatureService: features,
            jtbGamePhaseService: phases,
            tbsCellStates: cells,
            tbsCircles: circles,
            tbsShips: ships,
            jtbLiveGameFeed: livefeed,
            jtbIonicVersionNotesService: jtbIonicVersionNotesService,
            jtbPushNotifications: pushNotifications,
            tbsGameDetails: tbsGameDetails
        });
        expect(ctrl.gameDetails).to.equal(tbsGameDetails);
    }));

    it('initializes non-mobile', function () {
        expect($document.pauseFunction).to.be.defined;
        expect($document.resumeFunction).to.be.defined;
        assert(livefeed.setEndPoint.calledWithMatch(env.apiEndpoint));
        expect(ctrl.theme).to.equal('default-theme');
        expect(ctrl.mobile).to.be.false;
        expect(ctrl.adImport).to.equal('templates/ads/non-mobile.html');
        expect(ctrl.player).to.equal(currentPlayer);
        expect(ctrl.showAdmin).to.be.false;

        expect(ctrl.adminShowStats).to.be.true;
        expect(ctrl.adminShowSwitch).to.be.false;
    });

    it('switching admin to switch player', function () {
        ctrl.adminSwitchToSwitchPlayer();
        expect(ctrl.adminShowStats).to.be.false;
        expect(ctrl.adminShowSwitch).to.be.true;
    });

    it('switching admin to stats', function () {
        ctrl.adminSwitchToSwitchPlayer();
        ctrl.adminSwitchToStats();
        expect(ctrl.adminShowStats).to.be.true;
        expect(ctrl.adminShowSwitch).to.be.false;
    });

    describe('initialize with player and mobile', function () {
        beforeEach(inject(function ($controller) {
            currentPlayer = {id: 'initial', gameSpecificPlayerAttributes: {theme: 'initial'}, adminUser: false};
            $window.location.href = 'file://';
            $ionicLoading = {show: sinon.spy(), hide: sinon.spy()};
            $ionicPopup = {alert: sinon.spy()};
            ctrl = $controller('MainCtrl', {
                $scope: $scope,
                $state: $state,
                $document: $document,
                $window: $window,
                tbsAds: ads,
                ENV: env,
                $ionicLoading: $ionicLoading,
                $ionicPopup: $ionicPopup,
                jtbPlayerService: mockPlayerService,
                jtbGameFeatureService: features,
                jtbGamePhaseService: phases,
                tbsCellStates: cells,
                tbsCircles: circles,
                tbsShips: ships,
                jtbLiveGameFeed: livefeed,
                jtbIonicVersionNotesService: jtbIonicVersionNotesService,
                jtbPushNotifications: pushNotifications
            });
        }));

        it('initializes', function () {
            expect($document.pauseFunction).to.be.defined;
            expect($document.resumeFunction).to.be.defined;
            assert(livefeed.setEndPoint.calledWithMatch(env.apiEndpoint));
            expect(ctrl.theme).to.equal('initial');
            expect(ctrl.mobile).to.be.true;
            expect(ctrl.adImport).to.equal('templates/ads/mobile.html');
            expect(ctrl.player).to.equal(currentPlayer);
            expect(ctrl.showAdmin).to.be.false;
        });

        it('ignores player updates if id doesnt match', function () {
            var updatedPlayer = {
                id: currentPlayer.id + 'X',
                adminUser: true,
                gameSpecificPlayerAttributes: {theme: 'initialX'}
            };
            $rootScope.$broadcast('playerUpdate', updatedPlayer.id, updatedPlayer);
            expect(ctrl.player).to.equal(currentPlayer);
            expect(ctrl.showAdmin).to.be.false;
        });

        it('takes in player updates if id matches', function () {
            expect(ctrl.showAdmin).to.be.false;
            var updatedPlayer = {
                id: currentPlayer.id,
                adminUser: true,
                gameSpecificPlayerAttributes: {theme: 'new-theme'}
            };
            $rootScope.$broadcast('playerUpdate', updatedPlayer.id, updatedPlayer);
            expect(ctrl.player).to.equal(updatedPlayer);
            expect(ctrl.theme).to.equal(updatedPlayer.gameSpecificPlayerAttributes.theme);
            expect(ctrl.showAdmin).to.be.true;
        });

        it('player retains admin once has it', function () {
            expect(ctrl.showAdmin).to.be.false;
            var updatedPlayer = {
                id: currentPlayer.id,
                adminUser: true,
                gameSpecificPlayerAttributes: {theme: 'new-theme'}
            };
            $rootScope.$broadcast('playerUpdate', updatedPlayer.id, updatedPlayer);
            expect(ctrl.showAdmin).to.be.true;
            updatedPlayer = {
                id: currentPlayer.id,
                adminUser: false,
                gameSpecificPlayerAttributes: {theme: 'new-theme'}
            };
            $rootScope.$broadcast('playerUpdate', updatedPlayer.id, updatedPlayer);
            expect(ctrl.showAdmin).to.be.true;
        });
    });

    it('initializes on player loaded', function () {
        var circlePromise = $q.defer(), featurePromise = $q.defer(),
            cellPromise = $q.defer(), shipPromise = $q.defer(), phasePromise = $q.defer();

        currentPlayer = {id: 'replaced', adminUser: false, gameSpecificPlayerAttributes: {theme: 'replacedtheme'}};
        features.features.returns(featurePromise.promise);
        circles.circles.returns(circlePromise.promise);
        cells.cellStates.returns(cellPromise.promise);
        ships.ships.returns(shipPromise.promise);
        phases.phases.returns(phasePromise.promise);
        $rootScope.$broadcast('playerLoaded');

        featurePromise.resolve();
        phasePromise.resolve();
        cellPromise.resolve();
        circlePromise.resolve();
        shipPromise.resolve();
        $rootScope.$apply();
        expect(ctrl.theme).to.equal(currentPlayer.gameSpecificPlayerAttributes.theme);
        expect(ctrl.player).to.equal(currentPlayer);
        assert(jtbIonicVersionNotesService.displayVersionNotesIfAppropriate.calledWithMatch('1.2', 'Added new game play options - cruise missile attack and new ship options.  Also added a new pirate theme, see your profile in top bar.'));
        assert(ads.initialize.calledWithMatch());
        expect(ctrl.showAdmin).to.be.false;
    });

    it('initial load is admin, updated load is not like simulating person', function () {
        var circlePromise = $q.defer(), featurePromise = $q.defer(),
            cellPromise = $q.defer(), shipPromise = $q.defer(), phasePromise = $q.defer();

        features.features.returns(featurePromise.promise);
        circles.circles.returns(circlePromise.promise);
        cells.cellStates.returns(cellPromise.promise);
        ships.ships.returns(shipPromise.promise);
        phases.phases.returns(phasePromise.promise);
        currentPlayer = {id: 'replaced', adminUser: true, gameSpecificPlayerAttributes: {theme: 'replacedtheme'}};

        $rootScope.$broadcast('playerLoaded');
        $rootScope.$apply();
        expect(ctrl.player).to.equal(currentPlayer);
        expect(ctrl.showAdmin).to.be.true;

        currentPlayer = {id: 'new', adminUser: false, gameSpecificPlayerAttributes: {theme: 'update'}};
        expect(ctrl.player).not.to.equal(currentPlayer);
        $rootScope.$broadcast('playerLoaded');
        $rootScope.$apply();
        expect(ctrl.player).to.equal(currentPlayer);
        expect(ctrl.showAdmin).to.be.true;
    });

    it('show player', function () {
        ctrl.showPlayer();
        assert($state.go.calledWithMatch('app.playerDetails'));
    });

    it('show admin', function () {
        ctrl.showAdminScreen();
        assert($state.go.calledWithMatch('app.admin'));
    });

    it('if resume is called after no pauses, goes to network reconnect', function () {
        $document.resumeFunction();
        assert($state.go.calledWith('network'));
    });

    it('receiving pause sets up $timeout that goes to network after $timeout', function () {
        $document.pauseFunction();
        $timeout.flush();
        assert(livefeed.suspendFeed.calledWithMatch());
    });

    it('receiving pause and then resume cancels $timeout', function () {
        $document.pauseFunction();
        $document.resumeFunction();
        var exception = false;
        try {
            $timeout.flush();
        } catch (ex) {
            exception = true;
        }
        expect(exception).to.be.true;
        assert(!livefeed.suspendFeed.calledWithMatch());
    });

    it('receiving multiple pauses requires multiple resumes or $timeout fires', function () {
        $document.pauseFunction();
        $document.pauseFunction();
        $document.resumeFunction();
        $timeout.flush();
        assert(livefeed.suspendFeed.calledWithMatch());
    });

    it('refresh broadcasts to game cache', function () {
        $rootScope.$broadcast = sinon.spy();
        ctrl.refreshGames();
        expect($rootScope.$broadcast.callCount).to.equal(1);
        assert($rootScope.$broadcast.calledWithMatch('refreshGames', ''));
    });

});