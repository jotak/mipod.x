/// <reference path="Q.d.ts" />
/// <reference path="vertx.d.ts" />
var Mipod;
(function (Mipod) {
    var Messages = (function () {
        function Messages(ebReady) {
            this.ebReady = ebReady;
            ebReady.then(function (eventBus) {
                eventBus.registerHandler("WEB_Info", function (evt) {
                    console.log(evt);
                    var main = document.getElementById("main");
                    var node = document.createElement("p");
                    node.innerHTML = evt.line;
                    main.appendChild(node);
                });
                eventBus.registerHandler("WEB_Current", function (track) {
                    var main = document.getElementById("playing");
                    var str = "Nothing";
                    if (track) {
                        str = Messages.getDisplayName(track);
                    }
                    main.innerHTML = str;
                });
            });
        }
        Messages.getDisplayName = function (track) {
            if (track.title !== undefined) {
                var str = track.title;
                if (track.track !== undefined) {
                    return (track.track < 10 ? "0" + track.track : String(track.track)) + " - " + str;
                }
                return str;
            }
            return track.filepath.split("/").pop();
        };
        return Messages;
    })();
    Mipod.Messages = Messages;
})(Mipod || (Mipod = {}));
//# sourceMappingURL=messages.js.map