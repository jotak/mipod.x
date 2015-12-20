/// <reference path="react-global.d.ts" />

class BoxProps {
    public pos: any;
}

class BoxJSX<P extends BoxProps> extends React.Component<P, any> {

    constructor(props: P) {
        super(props);
    }

    renderInside() {
    }

    render() {
        var style = {
            position: "absolute",
            background: "#25292C",
            margin: "5px",
            padding: "8px",
            textAlign: "center",
            boxShadow: "inset 1px 1px 2px rgba(255,255,255,0.05)," +
                "inset 3px 5px 20px rgba(255,255,255,0.1)," +
                "inset -1px -1px 2px rgba(0,0,0,0.3)," +
                "inset -3px -15px 45px rgba(0,0,0,0.2)," +
                "1px 2px 15px -4px rgba(0,0,0,1)",
            width: this.props.pos.width,
            height: this.props.pos.height,
            top: this.props.pos.top,
            left: this.props.pos.left
        };
        for (var prop in this.props.pos) {
            if (this.props.pos.hasOwnProperty(prop)) {
                style[prop] = this.props.pos[prop];
            }
        }
        return (<div style={style}>{this.renderInside()}</div>);
    }
}
