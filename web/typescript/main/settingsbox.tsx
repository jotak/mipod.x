/// <reference path="react-global.d.ts" />

class SettingsBoxProps extends BoxProps {
}

class SettingsBoxJSX extends BoxJSX<SettingsBoxProps> {

    constructor(props: SettingsBoxProps) {
        super(props);
    }

    renderInside() {
        return (<div>SETTINGS</div>);
    }
}
