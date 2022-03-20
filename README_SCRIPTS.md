# Scripts

The project contains several scripts and IntelliJ run configurations aimed at easing development.
This document contains explanations of said scripts, and highlights their use cases.
Since pretty much all these scripts depend on environment variables, executing them requires setting these variables
in your `.bash_profile`[^1]:

```shell
export DATABASE_HOST=localhost    # If you are testing locally, alter if hosting remotely
export DATABASE_PORT=5432         # This is the default Postgres port, alter if necessary
export DATABASE_NAME=dl4se        # Must coincide with the name of the DB you created
export DATABASE_USER=dl4se_admin  # Must coincide with the user you created
export DATABASE_PASS=Lugano2022   # Must coincide with the password of the create user
```

[^1]: The scripts are configured to use `bash` by default
