import React from "react";

export default class InputListElement extends React.Component {
    constructor(props) {
        super(props);

    }

    remove(){
        this.props.remove(this.props.index);
    }

    render() {
        return (
            <div className="input-element">
                <input type="text" name={this.props.inputName} placeholder={this.props.placeholder}/>
                <div className={'remove-input'} onClick={() => this.remove()}></div>
            </div>
        );
    }
}