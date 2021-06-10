import store from "./store";

const {fetch: originalFetch} = window;

window.fetch = async (...args) => {
    const response = await originalFetch(...args);
    if (response.clone().status == 401) {
        console.log("unauthorized");
        store.commit('setSessionExpired', true);
        store.dispatch('logout');
    }

    return response;
}