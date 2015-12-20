/// <reference path="react-global.d.ts" />

class PlayBoxProps extends BoxProps {
}

class PlayBoxJSX extends BoxJSX<PlayBoxProps> {

    constructor(props: PlayBoxProps) {
        super(props);
    }

    renderInside() {
        return (<div>CURRENT TRACK</div>);
    }
}
