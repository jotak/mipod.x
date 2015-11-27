function Controls(ebReady) {
    this.ebReady = ebReady;
};

Controls.prototype.play = function() {
    this.ebReady.then(eventBus => {
        eventBus.publish("WEB_AudioPlay", {});
    });
};

Controls.prototype.stop = function() {
    this.ebReady.then(eventBus => {
        eventBus.publish("WEB_AudioStop", {});
    });
};

Controls.prototype.prev = function() {
    this.ebReady.then(eventBus => {
        eventBus.publish("WEB_AudioPrev", {});
    });
};

Controls.prototype.next = function() {
    this.ebReady.then(eventBus => {
        eventBus.publish("WEB_AudioNext", {});
    });
};
