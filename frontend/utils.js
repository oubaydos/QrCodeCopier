function insertMessage(message) {
    document.getElementById("messageBoard").insertAdjacentHTML("afterbegin", "<p>" + message + "</p>");
}

const getUrlWithHttpOrDefault = (url) => !/^https?:\/\//i.test(url) ? `http://${url}` : url;
const redirect = (url) => {
    window.location.href = getUrlWithHttpOrDefault(url)
}
