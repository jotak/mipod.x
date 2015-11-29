/// <reference path="Q.d.ts" />
/// <reference path="vertx.d.ts" />
/// <reference path="./messages.ts" />
/// <reference path="./controls.ts" />

module Mipod {

    class Starter {
        private eventBus: vertx.EventBus;
        private eventBusReady: Q.Promise<vertx.EventBus>;
        private messages: Messages;
        public controls: Controls;

        constructor() {
            var self = this;
            this.eventBus = new vertx.EventBus(window.location + "eventbus");
            var ebDefer = Q.defer<vertx.EventBus>();
            this.eventBus.onopen = function () {
                ebDefer.resolve(self.eventBus);
                self.eventBus.publish("WEB_InitConnection", {});
            };
            this.eventBusReady = ebDefer.promise;
            this.messages = new Messages(this.eventBusReady);
            this.controls = new Controls(this.eventBusReady);
        }
    }

    export function start(): Starter {
        return new Starter();
    }
}
