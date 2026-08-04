# TikTok-MetaSec-Encryption

unidbg wrapper around `libmetasec_ov` (musical_ly 46.x).

gives you the long Argus / Gorgon / Ladon headers phones actually send.
the python repos stop at the old ~300 char stuff — this hits 700+.

for research only. don't be dumb with it.

## build

needs jdk 17 + maven

```
mvn -q package
java -jar target/tiktok-metasec-encryption.jar 5099
```

or docker:

```
docker build -t metasec-sign .
docker run --rm -p 5099:5099 metasec-sign
```

## /sign

POST json to `http://127.0.0.1:5099/sign`

```json
{
  "params": "aid=1233&device_id=...&iid=...&version_name=46.2.3",
  "data": "body=null",
  "device_id": "...",
  "iid": "...",
  "path": "/aweme/v1/commit/item/digg/"
}
```

response:

```json
{
  "success": true,
  "result": {
    "x-argus": "...",
    "x-gorgon": "...",
    "x-ladon": "...",
    "x-khronos": "...",
    "x-ss-stub": "..."
  }
}
```

same rough shape as https://github.com/tr4cex/TikTok-Encryption so you can point existing clients at it.

```
python example.py
```

GET `/health` if you want a ready check.

## layout

```
pom.xml
Dockerfile
example.py
src/main/java/com/ide/tiktok/metasec/
  SignServer.java
  MetaSecEngine.java
  ...
src/main/resources/native/
  libmetasec_ov.so
  libc++_shared.so
```

## notes

- first request is slow, engine has to boot
- so is from a 46.x apk dump, swap it if you target something newer
- windows/mac/linux all fine as long as unicorn loads

MIT — see LICENSE
