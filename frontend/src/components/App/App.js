import React from "react";
import './App.css';
import '../header/Header.css';
import {withRouter} from "../../WithRouter";
import Header from "../header/Header";

class App extends React.Component {
    constructor(props) {
        super(props);

        let user = authService.loadCurrentUser();
        this.state = {
            user: user,
            setUser: this.setUser,
            removeUser: this.removeUser,
        }
    }

    setUser = (user) => {
        this.setState({user: user});
    }

    removeUser = () => {
        this.setState({user: null});
    }

    render() {
        return (
            <div className="App">
                <Header/>
            </div>
        );
    }
}

export default withRouter(App);