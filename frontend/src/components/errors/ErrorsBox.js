import React from "react";
import Error from "./Error";
import './Errors.css'

export default class ErrorsBox extends React.Component {

    render() {
        if (this.props.errors.length > 0) {
            return <div className="ErrorsBox">
                {this.props.errors.map((error, index) => {
                    return <Error key={index} error={error} index={index} removeError={this.props.removeError}/>
                })}
            </div>
        }
    }
}