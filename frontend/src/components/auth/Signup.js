import React from "react";
import "./Auth.css";
import {withRouter} from "../../WithRouter";
import authService from "../../services/auth.service";
import {MainContext} from "../App/MainContext";

class Signup extends React.Component {
    static contextType = MainContext;

    constructor(props) {
        super(props);

        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleSubmit(e) {
        e.preventDefault();
        let form = e.target;

        let firstName = form.firstName.value;
        let lastName = form.lastName.value;
        let email = form.email.value;
        let password = form.password.value;

        this.signUp(firstName, lastName, email, password);
    }

    async signUp(firstName, lastName, email, password) {
        let response = await authService.signUp({firstName, lastName, email, password});
        console.log(response);
        if (!response.ok) {
            this.context.handleError(response);
            return;
        }

        this.props.navigate('/login');
    }

    render() {
        return (
            <div className="Auth Signup">
                <div className="auth-wrapper">
                    <h1>Sign up</h1>

                    <form onSubmit={this.handleSubmit}>
                        <div className="form-row">
                            <input type="text" name="firstName" placeholder="First name" autoComplete="off" required/>
                        </div>
                        <div className="form-row">
                            <input type="text" name="lastName" placeholder="Last name" autoComplete="off" required/>
                        </div>
                        <div className="form-row">
                            <input type="email" name="email" placeholder="Email" autoComplete="off" required/>
                        </div>
                        <div className="form-row">
                            <input type="password" name="password" placeholder="Password" required/>
                        </div>
                        <div className="form-row">
                            <button type="submit">Sign up</button>
                        </div>
                    </form>

                    <div className="questions-box">
                        <a className="question" href="/login">Already have an account?</a>
                    </div>
                </div>
            </div>
        );
    }
}

export default withRouter(Signup);