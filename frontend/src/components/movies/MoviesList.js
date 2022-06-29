import React from "react";
import './MoviesList.css';
import MovieCard from "./MovieCard";
import movieService from "../../services/movie.service";
import {NavLink} from "react-router-dom";


export default class MoviesList extends React.Component {
    constructor(props) {
        super(props);

        let movies = [];
        this.state = {movies};
    }

    componentDidMount() {
        this.loadMovies();
    }

    async loadMovies() {
        let response = await movieService.getAllMovies();

        if (!response.ok) {
            this.context.handleError(response);
            return;
        }

        let movies = await response.json();
        this.setState({movies: movies});
    }

    render() {
        return (
            <div className="MoviesList">
                <div className="container">
                    <div className="movies-list-box movies-available">
                        <h1 className="list-title">Available movies</h1>

                        <div className="movies-list movies-available-list">
                            {this.state.movies.map(movie =>
                                <NavLink to={`/movies/${movie.id}`}  key={movie.id} >
                                    <MovieCard movie={movie}/>
                                </NavLink>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}