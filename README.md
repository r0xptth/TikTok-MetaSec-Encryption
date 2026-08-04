# TikTok-MetaSec-Encryption

Local `/sign` server that wraps TikTok's `libmetasec_ov` through unidbg (musical_ly 46.x).

Most public Python signers still emit the short Argus (~300 chars). This one runs the native MetaSec path and comes out closer to what a real 46.x client sends (~700+).

Research / education only. Don't use it to break ToS or the law.

## Build

JDK 17 + Maven:

```bash
mvn -q package
java -jar target/tiktok-metasec-encryption.jar 5099
```

Docker:

```bash
docker build -t metasec-sign .
docker run --rm -p 5099:5099 metasec-sign
```

Helper:

```bash
./run.sh          # default :5099
./run.sh 8080
```

## API

`POST http://127.0.0.1:5099/sign`

```json
{
  "params": "aid=1233&device_id=...&iid=...&version_name=46.2.3",
  "data": "body=null",
  "device_id": "...",
  "iid": "...",
  "path": "/aweme/v1/commit/item/digg/",
  "host": "https://api16-normal-useast5.tiktokv.us",
  "cookie": ""
}
```

```json
{
  "success": true,
  "ok": true,
  "result": {
    "x-argus": "...",
    "x-gorgon": "...",
    "x-ladon": "...",
    "x-khronos": "...",
    "x-ss-stub": "..."
  }
}
```

Request/response shape is close enough to [tr4cex/TikTok-Encryption](https://github.com/tr4cex/TikTok-Encryption) that you can usually just swap the URL.

```bash
pip install -r requirements.txt
python example.py
```

`GET /health` returns whether the engine finished loading.

## Layout

```
pom.xml
Dockerfile
example.py
run.sh
src/main/java/com/ide/tiktok/metasec/
  SignServer.java
  MetaSecEngine.java
  ...
src/main/resources/native/
  libmetasec_ov.so
  libc++_shared.so
```

## Notes

- Cold start takes a bit — unidbg has to load the SO and run init.
- The bundled `libmetasec_ov.so` is from a 46.x dump. Replace it if you're targeting a newer build.
- Works on Windows / macOS / Linux as long as Unicorn's native lib loads.

## License

MIT — see [LICENSE](LICENSE).
