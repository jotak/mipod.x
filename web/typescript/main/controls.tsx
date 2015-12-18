/// <reference path="react-global.d.ts" />

class ControlsProps {
    public controls: any;
}

class ControlsJSX extends React.Component<ControlsProps, any> {

    constructor(props: ControlsProps) {
        super(props);
        this.clickPlay = this.clickPlay.bind(this);
        this.clickStop = this.clickStop.bind(this);
        this.clickPrev = this.clickPrev.bind(this);
        this.clickNext = this.clickNext.bind(this);
    }

    private clickPlay() {
        this.props.controls.play();
    }

    private clickStop() {
        this.props.controls.stop();
    }

    private clickPrev() {
        this.props.controls.prev();
    }

    private clickNext() {
        this.props.controls.next();
    }

    render() {
        return (<div>
            <input type="button" value="Play" onClick={this.clickPlay} />
            <input type="button" value="Stop" onClick={this.clickStop} />
            <input type="button" value="Prev" onClick={this.clickPrev} />
            <input type="button" value="Next" onClick={this.clickNext} />
        </div>);
    }
}
