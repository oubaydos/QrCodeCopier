import {Dispatch, SetStateAction} from "react";

export const getUrlWithHttpOrDefault = (url: string): string => !/^https?:\/\//i.test(url) ? `http://${url}` : url;
export const redirect = (url: string): void => {
    window.location.href = getUrlWithHttpOrDefault(url);
}

<<<<<<< HEAD
export function showToken(token: string, setData: Dispatch<SetStateAction<string[]>>) {
    setData(data => [...data,token]);
}
=======
>>>>>>> main

export function showUrlToRedirectTo(url: string, setData: Dispatch<SetStateAction<string[]>>) {
    setData(data => [...data, "redirecting to: " + url]);
}