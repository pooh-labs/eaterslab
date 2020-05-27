from django.conf import settings
from django.db import models
from django.utils.translation import gettext_lazy as _
from django.core.validators import MinValueValidator, MaxValueValidator, URLValidator
from django.db.models import F

from datetime import timedelta


class Cafeteria(models.Model):
    name = models.CharField(max_length=128)
    description = models.CharField(max_length=256)
    sub_description = models.CharField(max_length=512)
    latitude = models.FloatField(validators=[MinValueValidator(-90.0), MaxValueValidator(90.0)])
    longitude = models.FloatField(validators=[MinValueValidator(-180.0), MaxValueValidator(180.0)])
    capacity = models.IntegerField(validators=[MinValueValidator(1)])
    logo_url = models.CharField(max_length=2048, validators=[URLValidator])
    address = models.CharField(max_length=256)
    opened_from = models.TimeField()
    opened_to = models.TimeField()
    owner = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE)
    occupancy = models.IntegerField(validators=[MinValueValidator(0)])

    @property
    def occupancy_relative(self):
        return float(self.occupancy) / float(self.capacity)

    def __str__(self):
        return self.name


class Camera(models.Model):
    class State(models.IntegerChoices):
        ONLINE = 0, _('online')
        OFFLINE = 1, _('offline')
        LOST_CONNECTION = 2, _('lost connection')

    LOSING_CONNECTION_INTERVAL = timedelta(minutes=3)

    description = models.CharField(max_length=200)
    state = models.IntegerField(choices=State.choices)
    cafeteria = models.ForeignKey(Cafeteria, on_delete=models.CASCADE)


class CameraEvent(models.Model):
    class EventType(models.IntegerChoices):
        MONITORING_STARTED = 0, _('monitoring_started')
        MONITORING_ENDED = 1, _('monitoring_ended')
        PERSON_ENTERED = 2, _('person_entered')
        PERSON_LEFT = 3, _('person_left')
        OCCUPANCY_OVERRIDE = 4, _('occupancy_override')

    timestamp = models.DateTimeField()
    event_type = models.IntegerField(choices=EventType.choices)
    camera = models.ForeignKey(Camera, on_delete=models.CASCADE, null=True, blank=True, default=None)
    cafeteria = models.ForeignKey(Cafeteria, on_delete=models.CASCADE)
    event_value = models.IntegerField(validators=[MinValueValidator(0)], null=True, blank=True, default=None)

    def __str__(self):
        return "{} {} {}".format(self.camera, self.event_type, self.timestamp)

    def save(self, *args, **kwargs):
        self.update_cafeteria_occupancy()
        super().save(*args, **kwargs)

    def update_cafeteria_occupancy(self):
        cafeteria = Cafeteria.objects.filter(pk=self.cafeteria.pk)
        if self.event_type == CameraEvent.EventType.OCCUPANCY_OVERRIDE.value \
                and self.event_value is not None and self.event_value <= self.cafeteria.capacity:
            cafeteria.update(occupancy=self.event_value)
        elif self.event_type == CameraEvent.EventType.PERSON_ENTERED.value \
                and self.cafeteria.occupancy < self.cafeteria.capacity:
            print(self.cafeteria.occupancy)
            cafeteria.update(occupancy=self.cafeteria.occupancy + 1)
            print(self.cafeteria.occupancy)
        elif self.event_type == CameraEvent.EventType.PERSON_LEFT.value \
                and self.cafeteria.occupancy > 0:
            cafeteria.update(occupancy=self.cafeteria.occupancy - 1)
        elif self.event_type != CameraEvent.EventType.MONITORING_ENDED.value \
                and self.event_type != CameraEvent.EventType.MONITORING_ENDED.value:
            raise ValueError('Invalid CameraEvent fields specification')

    class Meta:
        indexes = [
            # For selecting last event per camera
            models.Index(fields=['camera', 'timestamp']),
        ]


class FixedMenuOption(models.Model):
    name = models.CharField(max_length=128)
    price = models.FloatField(validators=[MinValueValidator(0.0)])
    cafeteria = models.ForeignKey(Cafeteria, on_delete=models.CASCADE, related_name='fixed_menu_options')
    photo_url = models.CharField(max_length=2048, validators=[URLValidator])
    avg_review_stars = models.FloatField(validators=[MinValueValidator(0), MaxValueValidator(5)],
                                         default=0, editable=False)

    def __str__(self):
        return self.name


class MenuOptionTag(models.Model):
    name = models.CharField(max_length=32, editable=False)
    option = models.ManyToManyField(FixedMenuOption, related_name='menu_option_tags')

    def __str__(self):
        return self.name


class FixedMenuOptionReview(models.Model):
    option = models.ForeignKey(FixedMenuOption, on_delete=models.CASCADE, related_name='fixed_menu_option_reviews')
    stars = models.IntegerField(validators=[MinValueValidator(0), MaxValueValidator(5)])
    author_nick = models.CharField(max_length=64)
    review_time = models.DateTimeField()

    def __str__(self):
        return f'{self.option} by {self.author_nick} with {self.stars}'

    def save(self, *args, **kwargs):
        curr_reviews = FixedMenuOptionReview.objects.count()
        if not self.pk:
            FixedMenuOption.objects.filter(pk=self.option.pk)\
                .update(avg_review_stars=self.calculate_new_avg_review(F('avg_review_stars'), curr_reviews))
        super().save(*args, **kwargs)

    def calculate_new_avg_review(self, old_review_stars, number_of_reviews):
        current_sum = old_review_stars * number_of_reviews + self.stars
        return current_sum / (number_of_reviews + 1)
