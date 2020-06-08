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

Before starting up the server User has to configure the environment. If setting up for the first time, copy `.env.sample` file from `server/.env.sample` to `server/server/.env` and change at least the `SECRET_KEY` value. [This site](https://miniwebtool.com/django-secret-key-generator/) can help with key generation.

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
```shell script
python manage.py migrate
python manage.py loaddata fixtures/demo-data.yaml
python manage.py reset_passwords  # Set password equal to username for each user
python manage.py generate_events  # Use --from/to=YYYY-MM-DD to select dates
python manage.py generate_reviews  # Use --from/to=YYYY-MM-DD to select dates
```
This will delete any existing camera events and reviews.

### Fresh data generation process

The process of loading sample data is setup on developer environment as a kind of a cronjob 
(which is called a Scheduled task on Heroku) and runs noe time per day. The whole data in system
is clean and then generated again using the commands
```shell script
psql $DATABASE_URL -t -c "select 'drop table \"' || tablename || '\" cascade;' from pg_tables where schemaname = 'public'" | psql $DATABASE_URL && 
python manage.py migrate && 
python manage.py loaddata fixtures/demo-data.yaml && 
python manage.py reset_passwords  && 
python manage.py generate_events && 
python manage.py generate_reviews 
```
which generates tables names to get removed, drops them, creates new one by migration and 
at the end inserts sample data to fresh database.
