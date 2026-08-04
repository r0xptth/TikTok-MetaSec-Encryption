import json
import requests

r = requests.post(
    "http://127.0.0.1:5099/sign",
    json={
        "params": "aid=1233&device_id=7123456789012345678&iid=7123456789012345679&version_name=46.2.3&device_platform=android",
        "data": "body=null",
        "device_id": "7123456789012345678",
        "iid": "7123456789012345679",
        "path": "/aweme/v1/commit/item/digg/",
    },
    timeout=60,
)

print(json.dumps(r.json(), indent=2))
argus = (r.json().get("result") or {}).get("x-argus") or ""
print("argus len:", len(argus))
