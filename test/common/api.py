from requests import Session


def request(context, url, method, json=None, headers=None):
    headers = headers or {}
    headers["x-token"] = context.token
    return Session().request(method, url, json=json, headers=headers)
