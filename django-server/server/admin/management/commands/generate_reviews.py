from django.core.management.base import BaseCommand
from django.db import transaction

from datetime import date, datetime, time, timedelta
from random import uniform

from api.models import FixedMenuOption, FixedMenuOptionReview


class Command(BaseCommand):
    help = 'Generate sample reviews for all menu entries. Drops all existing reviews.'

    def add_arguments(self, parser):
        parser.add_argument(
            '--from',
            help='First day of generated review date',
        )

        parser.add_argument(
            '--to',
            help='Last day of generated review date',
        )

    def handle(self, *args, **options):

        # Parse dates
        if options['from']:
            self.date_from = date.fromisoformat(options['from'])
        else:
            self.date_from = date.today() - timedelta(days=7)

        if options['to']:
            self.date_to = date.fromisoformat(options['to'])
        else:
            self.date_to = date.today()

        with transaction.atomic():
            self.stdout.write('Starting job...')
            self.delete_all_reviews()

            entrees = FixedMenuOption.objects.all()
            for entree in entrees:
                self.generate_for_entree(entree, 20)

            self.stdout.write(self.style.SUCCESS('Job finished.'))

    def delete_all_reviews(self):
        self.stdout.write('Deleting all reviews...')
        FixedMenuOptionReview.objects.all().delete()

    def generate_review(self, option, date):
        review = int(uniform(1, 8))
        if review > 5:
            review = 5
        author = 'review-author'
        hour = int(uniform(10, 20))
        minute = int(uniform(0, 60))
        timestamp = datetime.combine(date.today(), datetime.min.time()).replace(hour=hour, minute=minute).astimezone()
        return FixedMenuOptionReview(option=option, stars=review, author_nick=author, review_time=timestamp)

    def generate_for_entree(self, entry, count):
        # Create reviews
        delta = (self.date_to - self.date_from).days
        reviews = []
        for i in range(count):
            day = self.date_from + timedelta(days=int(uniform(0, delta+1)))
            reviews.append(self.generate_review(entry, day))

        reviews.sort(key=lambda e: e.review_time)
        FixedMenuOptionReview.objects.bulk_create(reviews)
        self.stdout.write('Created {} reviews for entry {}...'.format(len(reviews), entry))

        # Update average review in database
        sum = 0.0
        for r in reviews:
            sum += r.stars
        entry.avg_review_stars = sum / len(reviews)
        entry.save()
        self.stdout.write('Updated entry {} average rating: {}...'.format(entry, entry.avg_review_stars))
