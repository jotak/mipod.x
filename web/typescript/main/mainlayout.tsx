/// <reference path="react-global.d.ts" />

class MainLayoutProps {
    public controls: any;
}

class MainLayoutJSX extends React.Component<MainLayoutProps, any> {

    constructor(props: MainLayoutProps) {
        super(props);
    }

    render() {
        return <HeaderBarJSX controls={this.props.controls} />;
    }
}

function initJSX(mipod: any) {
    React.render(<MainLayoutJSX controls={mipod.controls} />, document.getElementById('main'));
}
