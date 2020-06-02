from django.contrib.auth import get_user_model
from django.core.management.base import BaseCommand
from django.db import transaction

class Command(BaseCommand):
    help = 'Reset all user passwords to match usernames'

    def handle(self, *args, **options):        
        with transaction.atomic():
            self.stdout.write('Starting job...')
            self.reset_passwords()
            self.stdout.write(self.style.SUCCESS('Job finished.'))

    def reset_passwords(self):
        users = get_user_model().objects.all()
        for user in users:
            user.set_password(user.get_username())
            user.save()
