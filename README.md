# java-webserver-PCS3641
Simple didactic Java HTTP server for PCS3641 classes.

## Configuration
File `/etc/pcs3641_http.ini` holds configuration options. It must be a valid INI file.

The following sections are supported:

| Section       | Values              | Example                             | Notes
| ------------- |---------------------|-------------------------------------|---------------------------------------------------------------
| VirtualHosts  | hostname=serverRoot | `private.pcs3641=/srv/http_private` | Value `default` is reserved and is used if no other names match.
| Users         | user=password       | `admin=pcs3641admin`                | Used with BASIC authentication scheme.

Authentication is enforced only if the requested page contains `secure` on its path.

Sample complete configuration:

```
[VirtualHosts]
default=/srv/http
private.pcs3641=/srv/http_private
[Users]
admin=admin_password
```

All logs are saved in `/var/log/pcs3641_http/access.log`.
