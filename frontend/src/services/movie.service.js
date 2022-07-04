import authHeader from './auth.header'

const API = process.env.REACT_APP_API

export const PREVIEW_IMAGE_API = API + 'movies/preview/'
export const POSTER_IMAGE_API = API + 'movies/poster/'

class MovieService {
	saveMovie(movie) {
		let formData = new FormData()

		formData.append('name', movie.name)
		formData.append('trailerUrl', movie.trailerUrl)
		formData.append('duration', movie.duration)
		formData.append('releaseDate', movie.releaseDate)
		formData.append('overview', movie.overview)
		formData.append('poster', movie.poster)

		movie.directors.forEach((director) =>
			formData.append('directors', director)
		)
		movie.actors.forEach((actor) => formData.append('actors', actor))
		movie.previews.forEach((preview) => formData.append('previews', preview))

		return fetch(API + 'movies', {
			method: 'post',
			headers: {
				Authorization: authHeader(),
			},
			body: formData,
		})
	}

	addGenreToMovie(movieId, genreId) {
		return fetch(API + `movies/${movieId}/genres/${genreId}`, {
			method: 'put',
			headers: {
				Authorization: authHeader(),
			},
		})
	}

	updateMovie(movie) {
		return fetch(API + 'movies/update', {
			method: 'post',
			headers: {
				'Content-Type': 'application/json',
				Authorization: authHeader(),
			},
			body: JSON.stringify(movie),
		})
	}

	getAllMovies() {
		return fetch(API + 'movies', {
			headers: {
				'Content-Type': 'application/json',
			},
		})
	}

	getMovie(id) {
		return fetch(API + `movies/${id}`, {
			headers: {
				'Content-Type': 'application/json',
			},
		})
	}
}

export default new MovieService()
