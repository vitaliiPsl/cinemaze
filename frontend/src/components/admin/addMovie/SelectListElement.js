import React from "react";

export default class SelectListElement extends React.Component {

    render() {
        return (
            <div className="select-element">
                <select name={this.props.name}>
                    <option disabled defaultValue={`Select ${this.props.name}`}>Select {this.props.name}</option>

                    {this.props.options.map(
                        option => <option value={option.id} key={option.id}>{option.name}</option>
                    )}

                </select>
                <div className={'remove-select'} onClick={this.props.remove}></div>
            </div>
        );
    }
}