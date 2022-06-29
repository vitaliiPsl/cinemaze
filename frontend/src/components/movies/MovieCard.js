import React from "react";
import {POSTER_IMAGE_API} from "../../services/movie.service";
import defaultPoster from "../../images/default-poster.jpg";

export default class MovieCard extends React.Component{
    
    render() {
        return (
            <div className="MovieCard">
                <div className="movie-poster">
                    {this.props.movie.posterImage &&
                        <img src={POSTER_IMAGE_API + this.props.movie.posterImage} alt=""/>
                    }
                    {!this.props.movie.posterImage &&
                        <img src={defaultPoster} alt=""/>
                    }
                </div>
                <div className="movie-description">
                    <h3 className="name">{this.props.movie.name}</h3>
                </div>
            </div>
        );
    }
}