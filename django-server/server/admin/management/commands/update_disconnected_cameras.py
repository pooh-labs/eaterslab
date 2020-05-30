from django.core.management.base import BaseCommand, CommandError
from django.db import transaction
from django.db.models import Max
from api.models import Camera, CameraEvent

from datetime import datetime


class Command(BaseCommand):
    help = 'Find cameras with did not send message in set interval and mark them as losing connection'

    def handle(self, *args, **options):
        time_threshold = datetime.now().astimezone() - Camera.LOSING_CONNECTION_INTERVAL
        
        with transaction.atomic():
            self.stdout.write('Starting job...')
            online_ids = Camera.objects.filter(state=Camera.State.ONLINE).values_list('id', flat=True)
            losing_conn_ids = CameraEvent.objects.values('camera').annotate(last_event=Max('timestamp')).filter(pk__in=set(online_ids), last_event__lt=time_threshold).values_list('camera_id', flat=True)
            update_count = Camera.objects.filter(pk__in=set(losing_conn_ids)).update(state=Camera.State.LOST_CONNECTION)
            self.stdout.write(self.style.SUCCESS('Job finished. Updated {0} cameras.'.format(update_count)))
