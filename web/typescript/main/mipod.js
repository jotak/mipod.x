/// <reference path="Q.d.ts" />
/// <reference path="vertx.d.ts" />
/// <reference path="./messages.ts" />
/// <reference path="./controls.ts" />
var Mipod;
(function (Mipod) {
    var Starter = (function () {
        function Starter() {
            var self = this;
            this.eventBus = new vertx.EventBus(window.location + "eventbus");
            var ebDefer = Q.defer();
            this.eventBus.onopen = function () {
                ebDefer.resolve(self.eventBus);
                self.eventBus.publish("WEB_InitConnection", {});
            };
            this.eventBusReady = ebDefer.promise;
            this.messages = new Mipod.Messages(this.eventBusReady);
            this.controls = new Mipod.Controls(this.eventBusReady);
        }
        return Starter;
    })();
    function start() {
        return new Starter();
    }
    Mipod.start = start;
})(Mipod || (Mipod = {}));
//# sourceMappingURL=mipod.js.map