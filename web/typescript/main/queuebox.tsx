/// <reference path="react-global.d.ts" />

class QueueBoxProps extends BoxProps {
}

class QueueBoxJSX extends BoxJSX<QueueBoxProps> {

    constructor(props: QueueBoxProps) {
        super(props);
    }

    renderInside() {
        return (<div>CURRENT PLAYLIST</div>);
    }
}
