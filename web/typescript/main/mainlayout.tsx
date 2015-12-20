/// <reference path="react-global.d.ts" />

class MainLayoutProps {
    public controls: any;
}

class MainLayoutJSX extends React.Component<MainLayoutProps, any> {

    constructor(props: MainLayoutProps) {
        super(props);
    }

    render() {
        var style = {
            position: "absolute",
            backgroundColor: "#25292c",
            width: "100%",
            height: "100%",
            top: 0,
            left: 0,
            color: "rgb(106, 218, 123)",
            fontFamily: "sans-serif"
        };
        return (<div style={style}>
                <HeaderBarJSX controls={this.props.controls} />
                <PlayBoxJSX pos={{ left: "33.34%", top: "30px", right: "33.34%", bottom: "0" }} />
                <LibBoxJSX pos={{ left: "0", top: "30px", right: "66.67%", bottom: "33.34%" }} />
                <FSBoxJSX pos={{ left: "0", top: "66.67%", right: "66.67%", bottom: "0" }} />
                <QueueBoxJSX pos={{ left: "66.67%", top: "33.34%", right: "0", bottom: "0" }} />
                <SettingsBoxJSX pos={{ left: "66.67%", top: "30px", right: "0", bottom: "66.66%" }} />
            </div>);
    }
}

function initJSX(mipod: any) {
    React.render(<MainLayoutJSX controls={mipod.controls} />, document.getElementById('main'));
}
