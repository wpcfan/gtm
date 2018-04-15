export const emailPattern = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
export const urlPattern = /^(http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?$/;
export const mobilePattern = /^1[345678]\d{9}$/;
export const usernamePattern = /^[A-Za-z0-9]+(?:[_-][A-Za-z0-9]+)*$/;
export const namePattern = /^(?!_)(?!.*?[ |_]$)[a-zA-Z0-9_\u4e00-\u9fa5]+$/;
export const humanNamePattern = /^(?!.* $)[a-zA-Z ]+|[\u4e00-\u9fa5]+$/;
