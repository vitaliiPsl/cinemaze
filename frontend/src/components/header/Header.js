import React from "react";
import {NavLink} from "react-router-dom";

export default class Header extends React.Component{

    render(){
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
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}