function Mipod() {
    var self = this;
    this.eventBus = new vertx.EventBus(window.location + "eventbus");
    var ebDefer = Q.defer();
    this.eventBus.onopen = function() {
        ebDefer.resolve(self.eventBus);
        self.eventBus.publish("WEB_InitConnection", {});
    }
    this.eventBusReady = ebDefer.promise;
    this.messages = new Messages(this.eventBusReady);
    this.controls = new Controls(this.eventBusReady);
}
