# Environment

To ease development, a good chunk of this project's configuration has been parametrized. Modules have their own
dedicated environment variables, which are used to configure everything from the ports used, to the address and password
of the mailing service. Since a good number of project scripts and run configurations inherit from the system
environment, getting them working requires adding specific variables to your `.bash_profile`[^1].

## Crawler

In order to establish a connection with the database, we need to define several environment variables:

```shell
export DATABASE_HOST=localhost   # If you are testing locally, alter if hosting remotely
export DATABASE_PORT=5432        # This is the default Postgres port, alter if necessary
export DATABASE_NAME=dl4se       # The name for your database
export DATABASE_USER=dl4se_admin # Postgres user that owns the database
export DATABASE_PASS=Lugano2022  # The password for the database owner
```

## Platform

In addition to the variables above, the platform also needs the following:

```shell
export PLATFORM_PORT=8443                   # Optional, defaults to 8080 in app
export PLATFORM_MAIL_ADDR=example@gmail.com # Your Gmail account
export PLATFORM_MAIL_PASS=randomlettersabc  # 16-character generated app password

export SECURITY_USER=dl4se_admin            # Name of the default user
export SECURITY_PASS=p4sSF0rDeF4u1t         # Password for the default user

export JWT_SECRET=JwTsEcReTKey12345         # JWT secret signing key
```

## Website

Finally, the platform front-end needs the following environment variables:

```shell
export WEBSITE_PORT=3000 # The default website port
```

[^1]: Or whatever other file your shell uses for configuring environment variables. Note that the helper shell scripts
are configured to use `bash` by default.
