import React from "react";
import {NavLink} from "react-router-dom";
import {MainContext} from "../App/MainContext";
import authService from "../../services/auth.service";

export default class Header extends React.Component {
    static contextType = MainContext;

    constructor(props) {
        super(props);

        this.state = {
            dropdown: false,
        }

        this.handleLogout = this.handleLogout.bind(this);
        this.toggleDropdown = this.toggleDropdown.bind(this);
    }

    handleLogout() {
        authService.logout();
        this.context.removeUser();

        window.location.href = '/';
    }

    toggleDropdown() {
        this.setState({dropdown: !this.state.dropdown})
    }

    render() {
        return (
            <div className="Header">
                <div className="container">
                    <div className="header-wrapper">
                        <div className="title-box">
                            <NavLink to={'/'}>
                                <h1 className="title">Cinemaze</h1>
                            </NavLink>
                        </div>
                        <div className="menu-box">
                            <div className="menu-item home">
                                <NavLink to={'/'} className={'menu-link'}>
                                    <span>Home</span>
                                </NavLink>
                            </div>
                            <div className="menu-item home">
                                <NavLink to={'/movies'} className={'menu-link'}>
                                    <span>Movies</span>
                                </NavLink>
                            </div>

                            {!this.context.user &&
                                <>
                                    <div className="menu-item">
                                        <NavLink to={'/login'} className={'menu-link'}>
                                            <span>Log in</span>
                                        </NavLink>
                                    </div>
                                    <div className="menu-item">
                                        <NavLink to={'/signup'} className={'menu-link'}>
                                            <span>Sign up</span>
                                        </NavLink>
                                    </div>
                                </>
                            }

                            {this.context.user &&
                                <>
                                    {this.context.user.roles.indexOf('ADMIN') !== -1 &&
                                        <div className="menu-item">
                                            <NavLink to={'/admin'} className={'menu-link'}>
                                                <span>ADMIN</span>
                                            </NavLink>
                                        </div>
                                    }
                                    <div className="profile-dropdown" onClick={this.toggleDropdown}>
                                        <div className="profile-dropdown-control">
                                            <div className="profile-dropdown-img">
                                                {this.context.user.firstName[0]}
                                            </div>
                                            <span className="profile-dropdown-name">{this.context.user.firstName}</span>
                                            <div className="profile-dropdown-arrow"></div>
                                        </div>

                                        {this.state.dropdown &&
                                            <div className="profile-dropdown-menu">
                                                <div className="dropdown-menu-item profile">
                                                    <NavLink to={`/profile`} className={'menu-link'}>
                                                        <div className="icon profile-icon"></div>
                                                        <span>Profile</span>
                                                    </NavLink>
                                                </div>
                                                <div className="dropdown-menu-item profile">
                                                    <NavLink to={`/tickets`} className={'menu-link'}>
                                                        <div className="icon tickets-icon"></div>
                                                        <span>My tickets</span>
                                                    </NavLink>
                                                </div>
                                                <div className="dropdown-menu-item log-out">
                                                    <NavLink onClick={this.handleLogout} to={`/logout`}
                                                             className={'menu-link'}>
                                                        <div className="icon log-out-icon"></div>
                                                        <span>Log out</span>
                                                    </NavLink>
                                                </div>
                                            </div>
                                        }
                                    </div>
                                </>
                            }
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}