/// <reference path="react-global.d.ts" />

class LibBoxProps extends BoxProps {
}

class LibBoxJSX extends BoxJSX<LibBoxProps> {

    constructor(props: LibBoxProps) {
        super(props);
    }

    renderInside() {
        return (<div>LIBRARY</div>);
    }
}
