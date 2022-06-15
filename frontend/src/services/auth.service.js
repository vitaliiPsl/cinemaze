const API = process.env.REACT_APP_API + 'auth/';

class AuthService {
    login(loginData) {
        return  fetch(API + "login", {
            method: "post",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(loginData),
        });
    }

    signUp(signupData){
        return fetch(API + 'signup', {
            method: 'post',
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(signupData),
        });
    }

    logout() {
        localStorage.removeItem('user');
    }

    saveCurrentUser(user){
        localStorage.setItem('user', JSON.stringify(user));
    }

    loadCurrentUser(){
        return JSON.parse(localStorage.getItem('user'));
    }
}

export default new AuthService();