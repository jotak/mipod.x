/// <reference path="react-global.d.ts" />

class ControlsProps {
    public controls: any;
}

class ControlsJSX extends React.Component<ControlsProps, any> {

    private clickPlay;
    private clickStop;
    private clickPrev;
    private clickNext;

    constructor(props: ControlsProps) {
        super(props);
        this.clickPlay = () => props.controls.play();
        this.clickStop = () => props.controls.stop();
        this.clickPrev = () => props.controls.prev();
        this.clickNext = () => props.controls.next();
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
