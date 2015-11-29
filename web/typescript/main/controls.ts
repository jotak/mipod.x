/// <reference path="Q.d.ts" />
/// <reference path="vertx.d.ts" />

module Mipod {
    export class Controls {
        constructor(public ebReady:Q.Promise<vertx.EventBus>) {
        }

        play() {
            this.ebReady.then(eventBus => {
                eventBus.publish("WEB_AudioPlay", {});
            });
        }

        stop() {
            this.ebReady.then(eventBus => {
                eventBus.publish("WEB_AudioStop", {});
            });
        }

        prev() {
            this.ebReady.then(eventBus => {
                eventBus.publish("WEB_AudioPrev", {});
            });
        }

        next() {
            this.ebReady.then(eventBus => {
                eventBus.publish("WEB_AudioNext", {});
            });
        }
    }
}
