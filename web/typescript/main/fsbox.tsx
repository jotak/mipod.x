/// <reference path="react-global.d.ts" />

class FSBoxProps extends BoxProps {
}

class FSBoxJSX extends BoxJSX<FSBoxProps> {

    constructor(props: FSBoxProps) {
        super(props);
    }

    renderInside() {
        return (<div>FILES</div>);
    }
}
