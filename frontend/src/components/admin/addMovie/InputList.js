import React from "react";
import InputListElement from "./InputListElement";

export default class InputList extends React.Component {
    constructor(props) {
        super(props);

        let inputElements = [];
        this.state = {inputElements};

        this.addInputElement = this.addInputElement.bind(this);
        this.removeInputElement = this.removeInputElement.bind(this);
    }

    addInputElement() {
        let inputElements = this.state.inputElements;

        let inputListElement =
            <InputListElement inputName={this.props.name} placeholder={this.props.placeholder}
                              index={inputElements.length} remove={this.removeInputElement}
                              key={inputElements.length}/>

        inputElements.push(inputListElement);

        this.setState({inputElements: inputElements});
    }

    removeInputElement(index) {
        let inputElements = this.state.inputElements;
        inputElements.splice(index, 1);

        this.setState({inputElements: inputElements});
    }

    render() {
        return (<div className="InputList">
            <label>{this.props.label}</label>
            <div className="input-list-wrapper">
                {[...this.state.inputElements]}
                <div className="add-input-element" onClick={this.addInputElement}>
                    <button type={"button"}>Add field</button>
                </div>
            </div>
        </div>);
    }
}