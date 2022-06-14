import React from "react";
import './App.css';
import '../header/Header.css';
import {withRouter} from "../../WithRouter";
import Header from "../header/Header";

class App extends React.Component {

    render() {
        return (
            <div className="App">
                <Header/>
            </div>
        );
    }
}

export default withRouter(App);