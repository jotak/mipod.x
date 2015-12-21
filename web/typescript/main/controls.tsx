/// <reference path="react-global.d.ts" />

class ControlsProps {
    public controls: any;
}

class ControlsJSX extends React.Component<ControlsProps, any> {

    private clickPlay;
    private clickStop;
    private clickPause;
    private clickPrev;
    private clickNext;

    constructor(props: ControlsProps) {
        super(props);
        this.clickPlay = () => props.controls.play();
        this.clickStop = () => props.controls.stop();
        this.clickPause = () => props.controls.pause();
        this.clickPrev = () => props.controls.prev();
        this.clickNext = () => props.controls.next();
    }

    render() {
        var style = {
            textAlign: "center"
        };
        return (<div style={style}>
            <span onClick={this.clickPlay}><i className="fa fa-play"></i></span>
            <span onClick={this.clickStop}><i className="fa fa-stop"></i></span>
            <span onClick={this.clickPause}><i className="fa fa-pause"></i></span>
            <span onClick={this.clickPrev}><i className="fa fa-fast-backward"></i></span>
            <span onClick={this.clickNext}><i className="fa fa-fast-forward"></i></span>
        </div>);
    }
}
