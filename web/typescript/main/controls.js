/// <reference path="Q.d.ts" />
/// <reference path="vertx.d.ts" />
var Mipod;
(function (Mipod) {
    var Controls = (function () {
        function Controls(ebReady) {
            this.ebReady = ebReady;
        }
        Controls.prototype.play = function () {
            this.ebReady.then(function (eventBus) {
                eventBus.publish("WEB_AudioPlay", {});
            });
        };
        Controls.prototype.stop = function () {
            this.ebReady.then(function (eventBus) {
                eventBus.publish("WEB_AudioStop", {});
            });
        };
        Controls.prototype.pause = function () {
            this.ebReady.then(function (eventBus) {
                eventBus.publish("WEB_AudioPause", {});
            });
        };
        Controls.prototype.prev = function () {
            this.ebReady.then(function (eventBus) {
                eventBus.publish("WEB_AudioPrev", {});
            });
        };
        Controls.prototype.next = function () {
            this.ebReady.then(function (eventBus) {
                eventBus.publish("WEB_AudioNext", {});
            });
        };
        return Controls;
    })();
    Mipod.Controls = Controls;
})(Mipod || (Mipod = {}));
//# sourceMappingURL=controls.js.map