import React from "react";
import './AdminPanel.css';
import AddMovie from "./addMovie/AddMovie";

export default class AdminPanel extends React.Component {

    render() {
        return (
            <div className="AdminPanel">
                <div className="container">
                    <div className="admin-nav-panel">
                        <div className="admin-option add-movie">
                            <div className="option-icon add"></div>
                            <span>Add movie</span>
                        </div>
                        <div className="admin-option movies">
                            <div className="option-icon movies"></div>
                            <span>Movies</span>
                        </div>
                        <div className="admin-option schedule">
                            <div className="option-icon schedule"></div>
                            <span>Schedule</span>
                        </div>
                    </div>

                    <div className="admin-content">
                        <AddMovie/>
                    </div>
                </div>
            </div>
        );
    }
}