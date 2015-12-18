/// <reference path="react-global.d.ts" />

class HeaderBarProps {
    public controls: any;
}

class HeaderBarJSX extends React.Component<HeaderBarProps, any> {

    constructor(props: HeaderBarProps) {
        super(props);
    }

    render() {
        return <ControlsJSX controls={this.props.controls} />;
    }
}
