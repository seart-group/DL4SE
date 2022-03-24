# Environment

To ease development, a good chunk of this project's configuration has been parametrized. Modules have their own
dedicated environment variables, which are used to configure everything from the ports used, to the address and password
of the mailing service. Since a good number of project scripts and run configurations inherit from the system
environment, getting them working requires adding the following variables to your `.bash_profile`[^1]:

## Crawler
```shell
export DATABASE_HOST=localhost    # If you are testing locally, alter if hosting remotely
export DATABASE_PORT=5432         # This is the default Postgres port, alter if necessary
export DATABASE_NAME=dl4se        # Must coincide with the name of the DB you created
export DATABASE_USER=dl4se_admin  # Must coincide with the user you created
export DATABASE_PASS=Lugano2022   # Must coincide with the password of the create user
```

## Platform
```shell
export PLATFORM_PORT=3333                   # Optional, defaults to 8080 in app
export PLATFORM_MAIL_ADDR=example@gmail.com # Your Gmail account
export PLATFORM_MAIL_PASS=randomlettersabc  # 16-character generated app password

export KEYSTORE_PASS=133TH4xx0RP4sS         # Password for your PKCS12 keystore
```

[^1]: Or whatever other file your shell uses for configuring environment variables. Note that the helper shell scripts
are configured to use `bash` by default.
