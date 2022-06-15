import React from "react";
import './App.css';
import '../header/Header.css';
import {withRouter} from "../../WithRouter";
import Header from "../header/Header";
import authService from "../../services/auth.service";
import {MainContext} from "./MainContext";
import ErrorsBox from "../errors/ErrorsBox";

class App extends React.Component {
    constructor(props) {
        super(props);

        let user = authService.loadCurrentUser();
        this.state = {
            user: user,
            setUser: this.setUser,
            removeUser: this.removeUser,
            errors: [],
            handleError: this.handleApiError,
        }
    }

    setUser = (user) => {
        this.setState({user: user});
    }

    removeUser = () => {
        this.setState({user: null});
    }

    handleApiError = async (response) => {
        let apiError = await response.json();

        if (apiError.subErrors.length === 0) {
            this.addError(apiError.message)
        } else {
            for (let subError of apiError.subErrors) {
                this.addError(subError.message);
            }
        }
    }

    addError = (error) => {
        let errors = this.state.errors;
        errors.push(error);

        this.setState({errors: errors});
    }

    removeError = (index) => {
        let errors = this.state.errors;
        errors.splice(index, 1);

        this.setState({errors: errors});
    }

    render() {
        return (
            <MainContext.Provider value={this.state}>
                <div className="App">
                    <Header/>
                    <ErrorsBox errors={this.state.errors} removeError={this.removeError}/>
                </div>
            </MainContext.Provider>
        );
    }
}

export default withRouter(App);