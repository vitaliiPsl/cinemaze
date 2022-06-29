import React from "react";
import './AddMovie.css';
import movieService from "../../../services/movie.service";
import {MainContext} from "../../App/MainContext";
import InputList from "./InputList";
import PreviewImage from "./PreviewImage";
import genreService from "../../../services/genre.service";
import SelectList from "./SelectList";

export default class AddMovie extends React.Component {
    static contextType = MainContext;

    constructor(props) {
        super(props);

        let genres = [];
        let posterImage = null;
        let previewImages = [];

        this.state = {
            genres,
            posterImage,
            previewImages,
        }

        this.posterInput = React.createRef();
        this.previewInput = React.createRef();

        this.submitForm = this.submitForm.bind(this);
        this.handlePosterInput = this.handlePosterInput.bind(this);
        this.handlePreviewInput = this.handlePreviewInput.bind(this);
    }

    componentDidMount() {
        this.loadGenres();
    }

    async loadGenres() {
        let response = await genreService.getAllGenres();

        if (!response.ok) {
            this.context.handleError(response);
            return;
        }

        let genres = await response.json();
        this.setState({genres: genres});
    }

    submitForm(e) {
        e.preventDefault();
        let form = e.target;

        let name = form.name.value;
        let trailerUrl = form.trailerUrl.value;
        let duration = form.duration.value;
        let releaseDate = form.releaseDate.value;
        let overview = form.overview.value;
        let poster = this.state.posterImage;
        let directors = this.getInputsValue(form.directors);
        let actors = this.getInputsValue(form.actors);
        let previews = this.state.previewImages;

        let genres = this.getInputsValue(form.genres);
        let movie = {name, trailerUrl, duration, releaseDate, overview, directors, actors, poster, previews};

        this.saveMovie(movie, genres);
    }

    async saveMovie(movieData, genres) {
        let response = await movieService.saveMovie(movieData);

        if (!response.ok) {
            this.context.handleError(response);
            return;
        }

        let movie = await response.json();
        let movieId = movie.id;

        for(let genreId of genres){
            response = await movieService.addGenreToMovie(movieId, genreId);

            if (!response.ok) {
                this.context.handleError(response);
                return;
            }
        }
    }

    getInputsValue(inputs) {
        if (!inputs) {
            return [];
        }

        if (inputs.length)
            return [...inputs].map(input => input.value).filter(val => val && val.length !== 0);
        else {
            return [inputs.value];
        }
    }

    handlePosterInput(e) {
        let poster = e.target.files[e.target.files.length - 1];
        this.setState({posterImage: poster});
    }

    removePosterImage() {
        this.setState({posterImage: null});
    }

    handlePreviewInput(e) {
        let previewImages = this.state.previewImages;
        previewImages.push(e.target.files[e.target.files.length - 1]);

        this.setState({previewImages: previewImages});
    }

    removePreviewImage(index) {
        let previewImages = this.state.previewImages;
        previewImages.splice(index, 1);

        this.setState({previewImages: previewImages});
    }

    getPosterImage() {
        return <PreviewImage image={this.state.posterImage} className={'poster-image'}
                             remove={() => this.removePosterImage()}/>
    }

    getPreviewImage(imageFile, index) {
        return <PreviewImage image={imageFile} key={index} className={'preview-image'}
                             remove={() => this.removePreviewImage(index)}/>
    }

    render() {
        return (
            <div className="AddMovie">
                <form onSubmit={this.submitForm}>

                    <div className="add-movie-description-box">
                        <div className="add-movie-description">
                            <h1>New movie</h1>
                            <div className="form-row">
                                <label htmlFor="name">Movie</label>
                                <input type="text" id={'name'} name={"name"} placeholder={"Movie name"} required autoComplete={false}/>
                            </div>
                            <div className="form-row">
                                <label htmlFor="trailerUrl">Trailer url</label>
                                <input type="text" id={'trailerUrl'} name={"trailerUrl"}
                                       placeholder={"https://www.youtube.com/watch?v=trailer"}/>
                            </div>
                            <div className="form-row">
                                <label htmlFor="duration">Duration</label>
                                <input type="number" id={'duration'} name={"duration"} placeholder={"Duration"}
                                       step={1} required/>
                            </div>
                            <div className="form-row">
                                <label htmlFor="releaseDate">Release date</label>
                                <input type="date" id={'releaseDate'} name={"releaseDate"}
                                       placeholder={"Release date"} required/>
                            </div>
                            <div className="form-row">
                                <label htmlFor="overview">Short overview</label>
                                <textarea name="overview" id={'overview'} cols="10" rows="10"
                                          placeholder={"Movie overview"} minLength={24} maxLength={1024}
                                          required></textarea>
                            </div>
                        </div>
                        <div className="add-movie-poster-box">
                            <div className="poster-wrapper" onClick={() => this.posterInput.current.click()}>
                                {
                                    this.state.posterImage && this.getPosterImage()
                                }
                                <input type="file" name={"poster"} id={"poster-image-input"} ref={this.posterInput}
                                       onInput={this.handlePosterInput} hidden/>
                            </div>
                        </div>
                    </div>

                    <div className="genres-list-box">
                        <SelectList label={'Genres'} name={'genres'}
                                    options={this.state.genres.map(genre => {
                                        return {id: genre.id, name: genre.genre};
                                    })}/>
                    </div>

                    <div className="directors-list-box">
                        <InputList label={'Directors'} name={'directors'} placeholder={'Director'}/>
                    </div>

                    <div className="actors-list-box">
                        <InputList label={'Actors'} name={'actors'} placeholder={'Actor'}/>
                    </div>

                    <div className="preview-images-box">
                        <label>Preview images:</label>
                        <div className="preview-images-wrapper">
                            {this.state.previewImages.map((image, index) => this.getPreviewImage(image, index))}
                            <div className="preview-image new" onClick={() => this.previewInput.current.click()}>
                                <input type="file" id={'previewImageInput'} ref={this.previewInput}
                                       onInput={this.handlePreviewInput} hidden/>
                            </div>
                        </div>
                    </div>

                    <div className="submit-box">
                        <button type={'submit'} className={'submit'}>Save movie</button>
                    </div>
                </form>
            </div>
        );
    }
}