export const getUrlWithHttpOrDefault = (url: string): string => !/^https?:\/\//i.test(url) ? `http://${url}` : url;
export const redirect = (url: string): void => {
    window.location.href = getUrlWithHttpOrDefault(url)
}
