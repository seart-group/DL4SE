# DL4SE

## Diagrams

### Sequence Diagrams

Although hosted on the plantuml server, these can also be found locally under `diagrams`.
To generate these diagrams locally, make sure you install plantuml via Homebrew:
```shell
brew install --cask temurin
brew install graphviz
brew install plantuml
```
And run the following:
```shell
plantuml -tsvg ./* -output "out"
```

![crawling](https://www.plantuml.com/plantuml/svg/VP3FJiCm3CRlUGfhEq-jq4uxeD5j5AGX8Oq31ovUwxNHqgGaBcn2l3jUDbL5GrXA__Zx_7FiUM3qt3L6iMP3i4J6G0nFg2tBGLkGKc6lROiU6zZGUqUIoxthsioLnmoBVIK_jbmuupoCxsEawQ0er5YwJsqhsA49mjkw0uoMw-bcDOBnMbSrvnwF2YIaWdMXMxGi5cJSOg0HO81bTcrO649BFkgHfyYM87YbVldoXmtSFJz6dtmhPPnhWJx87-4LQjVv4E6H0PCtQJQ3dHOM6dDMCfWabnun4Gaxvu6mgC5Jwu9cvyzJ35YtfuOpo5Sla62h4vzyMMpek_UXuNu9H7Q00Zhem47180Gcp3Dy1ZTfsgja0br02-VH344b6M8g_o5DqsbKhsuVDs6m-cj4iX4PtwSpbqVNc1y0)

![cleanup](https://www.plantuml.com/plantuml/svg/RLB1Rjim3BthAuZakY8zxEJ3q2NfqW8z5CYEkp0sOmkLHOsYbkNl9tcEOMTxW6s9V7fy5FqK5KLJxw0Ml3ZhMwDM7S5ktCAE6p8cVbWVKB278_rCv6iw2AUmvUQwnVgZpHlVxDYnmFhRWCV7sQ7OOSEN8dT65xFUYN_3Ql_s-VYyWlMRRJiz25uDPEGJQciRq6kceE898wq08-ot_n86doH3VbmRyWta0cP1FBmSuUlxwq3CwxkarPTq6dF6Z4BmyALGApWyVuSD1hkHJIIKkZFYMGca63VlfSLOcJKuz6t2blAo5aAb1dhhyowoA9okjsYEX48hHekGAUPoiR5k1K8nEOt6CGSuim1XtOrH0ncJmn_POGMrOqyv7xXaSmWUDZVE45aAdQqZ0oFGwJG28Zyr0J65m5BimWopkzblgpACI8CWOEucAWj1WuhJ8NAHnW8x5dGBjR_toBz8PdQNU6rzqTJEnidu0gKjpvTFS27eyNgV7orQ1OVw7_qcIVanKyYkb-BJU6O1S_kMvoc_Kk_-0000)

### Database Schema

[Link to current relational rendition](https://dbdiagram.io/d/6202862685022f4ee55b0274)

## Maven

### Adding modules

You can auto-generate a `quickstart` Maven module through the use of the following archetype command:
```shell
mvn archetype:generate \
-DarchetypeGroupId=org.apache.maven.archetypes \
-DarchetypeArtifactId=maven-archetype-quickstart \
-DarchetypeVersion=RELEASE \
-DgroupId=usi.si.seart \
-Dversion=0.1-SNAPSHOT
```
