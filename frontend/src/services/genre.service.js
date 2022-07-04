import authHeader from "./auth.header";

const API = process.env.REACT_APP_API;

class GenreService {
    saveGenre(genreData) {
        return fetch(API + "genres", {
            method: "post",
            headers: {
                'Authorization': authHeader()
            },
            body: JSON.stringify(genreData),
        });
    }

    deleteGenre(genreId) {
        return  fetch(API + `genres/${genreId}`, {
            method: "delete",
            headers: {
                "Content-Type": "application/json",
                'Authorization': authHeader()
            }
        });
    }

    getAllGenres(){
        return fetch(API + 'genres', {
            headers: {
                "Content-Type": "application/json",
            }
        });
    }

    getGenre(genreId){
        return fetch(API + `genres/${genreId}`, {
            headers: {
                "Content-Type": "application/json",
            }
        });
    }
}

export default new GenreService();