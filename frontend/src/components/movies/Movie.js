import React from "react";
import {withRouter} from "../../WithRouter";
import './Movie.css';
import defaultPoster from '../../images/default-poster.jpg';
import movieService, {POSTER_IMAGE_API, PREVIEW_IMAGE_API} from "../../services/movie.service";
// import Slider from "../slider/Slider";
import {MainContext} from "../App/MainContext";
import Slider from "../slider/Slider";

class Movie extends React.Component {
    static contextType = MainContext;

    constructor(props) {
        super(props);

        let id = this.props.params.id;
        let movie = null;
        this.state = {id, movie};

        this.getPreviewImages = this.getPreviewImages.bind(this);
    }

    componentDidMount() {
        this.loadMovie();
    }

    async loadMovie() {
        let response = await movieService.getMovie(this.state.id);

        if (!response.ok) {
            this.context.handleError(response);
            return;
        }

        let movie = await response.json();
        if (movie.trailerUrl) {
            movie.trailerUrl = movie.trailerUrl.replace("watch?v=", "embed/");
        }

        this.setState({movie});
    }

    getPreviewImages() {
        return this.state.movie.previewImages.map(preview =>
            <div key={preview} className="preview-image">
                <img src={PREVIEW_IMAGE_API + preview} alt=""/>
            </div>
        )
    }

    render() {
        return this.state.movie == null ? (<> </>) : (
            <div className="Movie">
                <div className="trailer-box">
                    {this.state.movie.trailerUrl &&
                        <iframe width="100%" height="100%"
                                src={`${this.state.movie.trailerUrl}?autoplay=1&showinfo=0&controls=0`}
                                title={this.state.movie.name}
                                allowFullScreen/>
                    }
                </div>

                <div className="container movie-description">
                    <div className="movie-description-box">
                        <div className="movie-poster">
                            <div className="movie-poster-wrapper">
                                {this.state.movie.posterImage &&
                                    <img src={POSTER_IMAGE_API + this.state.movie.posterImage} alt=""/>
                                }
                                {!this.state.movie.posterImage &&
                                    <img src={defaultPoster} alt=""/>
                                }
                            </div>
                        </div>

                        <div className="movie-description">
                            <h1 className="movie-name">{this.state.movie.name}</h1>

                            <div className="description-row">
                                <div className="duration-box description-box">
                                    <span className="duration">{this.state.movie.duration} min</span>
                                </div>
                            </div>

                            <div className="description-row">
                                <div className="released-box description-box">
                                    <label>Released:</label>
                                    <span className="released">{this.state.movie.releaseDate}</span>
                                </div>
                                {this.state.movie.directors &&
                                    <div className="directors-box description-box">
                                        <label>Directors:</label>
                                        <div className="description-list directors-list">
                                            {this.state.movie.directors.map(director =>
                                                <span className="director" key={director.id}>{director}</span>
                                            )}
                                        </div>
                                    </div>
                                }
                            </div>

                            <div className="description-row">
                                {this.state.movie.genres &&
                                    <div className="genres-box description-box">
                                        <label>Genres:</label>
                                        <div className="description-list genres-list">
                                            {this.state.movie.genres.map(genre =>
                                                <span className="genre" key={genre.id}>{genre.genre}</span>
                                            )}
                                        </div>
                                    </div>
                                }
                                {this.state.movie.actors &&
                                    <div className="actors-box description-box">
                                        <label>Cast:</label>
                                        <div className="description-list actors-list">
                                            {this.state.movie.actors.map(actor =>
                                                <span className="actor" key={actor.id}>{actor}</span>
                                            )}
                                        </div>
                                    </div>
                                }
                            </div>

                            <div className="description-row">
                                <div className="overview-box">
                                    <label>Overview:</label>
                                    <p className="movie-overview">
                                        {this.state.movie.overview}
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                {this.state.movie.previewImages.length !== 0 &&
                    <Slider slides={this.getPreviewImages()}/>
                }
            </div>);
    }
}

export default withRouter(Movie);