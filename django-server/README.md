#  Installation instructions:

The following apt commands will get you the packages you need:
```
sudo apt-get update
sudo apt-get install python3-pip
```
and 
```
python3 -m pip install --user pipenv
```
Now you should be able to run the `server-env-setup.sh` script successfully.

For django + postgres installation check instructions in the link below:
https://www.digitalocean.com/community/tutorials/how-to-use-postgresql-with-your-django-application-on-ubuntu-14-04

## Environment setup

To setup environment locally the .env.sample file can be used as sample.
User have to create his own .env file with needed from example fields.
Then the server can be run with
```bash
python manage.py runserver
```

## Admin user create

Admin user can be created in the environment using Django
```bash
python manage.py createsuperuser
```

### Token generation process

Then the token for admin authentication for example for sending artifacts can be generated for user named *admin* using
```bash
python manage.py drf_create_token admin
```
(the Django migration has to be run after token generation process).

and the it can be used as *generated_token* for example for files uplading with authentification with
```bash
curl -H 'Authorization: Token generated_token' -X PUT --data-binary @./local_file.upload https://server.example/file/upload/path/
```
when the authentication is needed for uploading files.

### Data dump to file

The data can be backed using the django `dumpdata` option by
```bash
python manage.py dumpdata --exclude=auth --exclude=contenttypes --exclude=sessions --exclude=authtoken > data.json
```

and then restored in the other environment using the 
```bash
python manage.py loaddata data.json
```

## Sample data

To load sample data in empty project, run:
```
python manage.py migrate
python manage.py loaddata fixtures/demo-data.yaml
python manage.py reset_passwords
python manage.py generate_events  # Use --from/to=YYYY-MM-DD to select dates
python manage.py generate_reviews  # Use --from/to=YYYY-MM-DD to select dates
```
