import React from "react";
import SelectListElement from "./SelectListElement";

export default class SelectList extends React.Component {
    constructor(props) {
        super(props);

        let selectElements = [];
        this.state = {selectElements};

        this.addSelectElement = this.addSelectElement.bind(this);
        this.removeSelectElement = this.removeSelectElement.bind(this);
    }

    addSelectElement() {
        let selectElements = this.state.selectElements;

        let index = selectElements.length;

        let selectElement =
            <SelectListElement name={this.props.name}
                               options={this.props.options}
                               remove={() => this.removeSelectElement(index)}
                               key={index}/>

        selectElements.push(selectElement);

        this.setState({selectElements: selectElements});
    }

    removeSelectElement(index) {
        console.log(index);
        let selectElements = this.state.selectElements;
        console.log(selectElements);
        selectElements.splice(index, 1);
        console.log(selectElements);

        this.setState({selectElements: selectElements});

    }

    render() {
        return (<div className="SelectList">
            <label>{this.props.label}</label>
            <div className="select-list-wrapper">
                {[...this.state.selectElements]}
                <div className="add-select-element" onClick={this.addSelectElement}>
                    <button type={"button"}>Add option</button>
                </div>
            </div>
        </div>);
    }
}