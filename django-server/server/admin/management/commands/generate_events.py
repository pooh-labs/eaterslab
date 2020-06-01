from django.core.management.base import BaseCommand
from django.db import transaction

from datetime import date, datetime, time, timedelta
from random import gauss, uniform
from math import floor

from api.models import Cafeteria, Camera, CameraEvent, CameraEventType as EventType


def as_datetime(date, time):
    return datetime.combine(date, time).astimezone()


def as_time_today(time):
    return datetime.now().astimezone().replace(hour=time.hour, minute=time.minute, second=time.second, microsecond=0)


def add_seconds(timestamp, seconds):
    whole_seconds = floor(seconds)
    microseconds = floor((seconds - whole_seconds)*1000000)
    return timestamp + timedelta(seconds = whole_seconds, microseconds = microseconds)


class Command(BaseCommand):
    help = 'Generate sample camera events. Drops all existing events.'

    def add_arguments(self, parser):
        parser.add_argument(
            '--from',
            help='First day of events to generate',
        )

        parser.add_argument(
            '--to',
            help='Last day of events to generate',
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
            self.delete_all_events()
            self.add_monitoring_starts()

            cafeterias = Cafeteria.objects.all()
            for cafe in cafeterias:
                self.generate_for_cafeteria(cafe)

            self.stdout.write(self.style.SUCCESS('Job finished.'))

    def delete_all_events(self):
        self.stdout.write('Deleting all events...')
        CameraEvent.objects.all().delete()

    def timestamps_to_events(self, cafeteria, camera, event_type, timestamps):
        return [CameraEvent(cafeteria=cafeteria, camera=camera, event_type=event_type, timestamp=t) for t in timestamps]

    def add_monitoring_starts(self):
        self.stdout.write('Adding MONITORING_STARTED events for each cafeteria...')
        fixed_timestamp = datetime(2000, 1, 1, 0, 0, 0, 0).astimezone()
        cafeterias = Cafeteria.objects.all()
        events = []
        for cafe in cafeterias:
            camera = self.get_first_camera(cafe)
            events.extend(self.timestamps_to_events(cafe, camera, EventType.MONITORING_STARTED, [fixed_timestamp]))
        CameraEvent.objects.bulk_create(events)          

    def get_first_camera(self, cafeteria):
        return Camera.objects.filter(cafeteria=cafeteria).first()    

    def generate_lunch_enters(self, seconds_open, entering_minutes, visitors):
        """Generate lunch enter times.
        
        Generated with Gaussian distribution. Timestamps not in order.
        
        Returns:
            list of enters as seconds since cafeteria opening.
        """
        # People come around lunch time
        lunch_time = seconds_open * 3/7
        # ~95% (two-sided 3 sigma) of lunch traffic comes in +-entering_minutes minutes
        lunch_int_95 = 60 * entering_minutes * 2
        return [gauss(lunch_time, lunch_int_95/6) for _ in range(visitors)]

    def generate_random_enters(self, seconds_open, visitors):
        """Generate random enter times.
        
        Generated with uniform distribution. Timestamps not in order.
        
        Returns:
            list of enters as seconds since cafeteria opening.
        """
        return [uniform(0, seconds_open) for _ in range(visitors)]

    def generate_for_day(self, cafeteria, seconds_open, date):
        open_timestamp = as_datetime(date, cafeteria.opened_from)

        single_visit_time = 15 * 60 # People eat exactly 15 minutes
        lunch_gathering_time = 20
        lunch_visitors = int(uniform(0.8, 1.2) * cafeteria.capacity)
        random_visitors = int(seconds_open / (60 * 5) * (50 / cafeteria.capacity))

        lunch_enters = self.generate_lunch_enters(seconds_open, lunch_gathering_time, lunch_visitors)
        random_enters = self.generate_random_enters(seconds_open, random_visitors)
        all_enters = lunch_enters + random_enters

        enter_timestamps =[add_seconds(open_timestamp, t) for t in all_enters]
        leave_timestamps =[add_seconds(open_timestamp, t + single_visit_time) for t in all_enters]
        
        camera = self.get_first_camera(cafeteria)
        enter_events = self.timestamps_to_events(cafeteria, camera, EventType.PERSON_ENTERED, enter_timestamps)
        leave_events = self.timestamps_to_events(cafeteria, camera, EventType.PERSON_LEFT, leave_timestamps)

        events = []
        events.extend(enter_events)
        events.extend(leave_events)
        return events

    def generate_for_cafeteria(self, cafeteria):
        timestamp_from = as_time_today(cafeteria.opened_from)
        timestamp_to = as_time_today(cafeteria.opened_to)
        seconds_open = int((timestamp_to - timestamp_from).total_seconds())

        delta = self.date_to - self.date_from
        events = []
        for i in range(delta.days + 1):
            day = self.date_from + timedelta(days=i)
            events.extend(self.generate_for_day(cafeteria, seconds_open, day))

        events.sort(key=lambda e: e.timestamp)
        CameraEvent.objects.bulk_create(events)
        self.stdout.write('Created {} events for cafeteria {}...'.format(len(events), cafeteria))
