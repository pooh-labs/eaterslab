from django.conf import settings
from django.db import models
from django.utils.translation import gettext_lazy as _
from django.core.validators import MinValueValidator, MaxValueValidator, URLValidator
from django.db.models import F, Q

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
        return min(1.0, float(self.occupancy) / float(self.capacity))

    def __str__(self):
        return self.name

    name.verbose_name = _('name')
    description.verbose_name = _('description')
    sub_description.verbose_name = _('additional description')
    latitude.verbose_name = _('latitude')
    longitude.verbose_name = _('longitude')
    capacity.verbose_name = _('cafeteria capacity')
    logo_url.verbose_name = _('logo_url')
    address.verbose_name = _('address')
    opened_from.verbose_name = _('opened_from')
    opened_to.verbose_name = _('opened_to')
    owner.verbose_name = _('owner')
    occupancy.verbose_name = _('cafeteria occupancy')

    class Meta:
        verbose_name = _('cafeteria')
        verbose_name_plural = _('cafeterias')


class Camera(models.Model):
    class State(models.IntegerChoices):
        ONLINE = 0, _('online')
        OFFLINE = 1, _('offline')
        LOST_CONNECTION = 2, _('lost connection')

    LOSING_CONNECTION_INTERVAL = timedelta(minutes=3)

    state = models.IntegerField(choices=State.choices)
    cafeteria = models.ForeignKey(Cafeteria, on_delete=models.CASCADE)

    state.verbose_name = _('state')
    cafeteria.verbose_name = _('cafeteria')

    class Meta:
        verbose_name = _('camera')
        verbose_name_plural = _('cameras')


class CameraEventType(models.IntegerChoices):
    MONITORING_STARTED = 0, _('monitoring_started')
    MONITORING_ENDED = 1, _('monitoring_ended')
    PERSON_ENTERED = 2, _('person_entered')
    PERSON_LEFT = 3, _('person_left')
    OCCUPANCY_OVERRIDE = 4, _('occupancy_override')


# For enforcing event_type.camera is (non-)null 
def get_q_event_camera_nullability():
    null_cam_event_types = [
        CameraEventType.OCCUPANCY_OVERRIDE
    ]
    nonnull_cam_event_types = [
        CameraEventType.MONITORING_STARTED,
        CameraEventType.MONITORING_ENDED,
        CameraEventType.PERSON_ENTERED,
        CameraEventType.PERSON_LEFT,
    ]
    return Q(event_type__in=null_cam_event_types, camera__isnull=True) | Q(
        event_type__in=nonnull_cam_event_types, camera__isnull=False
    )


# For enforcing event_type.event_value is (non-)null 
def get_q_event_value_nullability():
    null_val_event_types = [
        CameraEventType.MONITORING_STARTED,
        CameraEventType.MONITORING_ENDED,
        CameraEventType.PERSON_ENTERED,
        CameraEventType.PERSON_LEFT,
    ]
    nonnull_val_event_types = [
        CameraEventType.OCCUPANCY_OVERRIDE
    ]
    return Q(event_type__in=null_val_event_types, event_value__isnull=True) | Q(
        event_type__in=nonnull_val_event_types, event_value__isnull=False
    )


class CameraEvent(models.Model):
    timestamp = models.DateTimeField()
    event_type = models.IntegerField(choices=CameraEventType.choices)
    camera = models.ForeignKey(Camera, on_delete=models.CASCADE, null=True, blank=True, default=None)
    cafeteria = models.ForeignKey(Cafeteria, on_delete=models.CASCADE)
    event_value = models.IntegerField(validators=[MinValueValidator(0)], null=True, blank=True, default=None)

    def __str__(self):
        return "[{}, {}, {}, {}]".format(self.cafeteria, self.camera, self.get_event_type_display(), self.timestamp)

    def save(self, *args, **kwargs):
        self.update_cafeteria_occupancy()
        super().save(*args, **kwargs)

    def update_cafeteria_occupancy(self):
        cafeteria = Cafeteria.objects.filter(pk=self.cafeteria.pk)
        if self.event_type == CameraEventType.OCCUPANCY_OVERRIDE.value \
                and self.event_value is not None and self.event_value <= self.cafeteria.capacity:
            cafeteria.update(occupancy=self.event_value)
        elif self.event_type == CameraEventType.PERSON_ENTERED.value:
            cafeteria.update(occupancy=self.cafeteria.occupancy + 1)
        elif self.event_type == CameraEventType.PERSON_LEFT.value \
                and self.cafeteria.occupancy > 0:
            cafeteria.update(occupancy=self.cafeteria.occupancy - 1)
        elif self.event_type != CameraEventType.MONITORING_STARTED.value \
                and self.event_type != CameraEventType.MONITORING_ENDED.value:
            raise ValueError('Invalid CameraEvent fields specification')

    timestamp.verbose_name = _('timestamp')
    event_type.verbose_name = _('event_type')
    camera.verbose_name = _('camera')
    cafeteria.verbose_name = _('cafeteria')
    event_value.verbose_name = _('event_value')

    class Meta:
        verbose_name = _('camera event')
        verbose_name_plural = _('camera events')

        constraints = [
            models.CheckConstraint(
                name = 'camera_nullable_per_type',
                check = get_q_event_camera_nullability(),
            ),
            models.CheckConstraint(
                name = 'event_value_nullable_per_type',
                check = get_q_event_value_nullability(),
            ),
        ]

        indexes = [
            # For selecting last event per camera
            models.Index(fields=['camera', 'timestamp']),
        ]


class FixedMenuOption(models.Model):
    name = models.CharField(max_length=128)
    price = models.FloatField(validators=[MinValueValidator(0.0)])
    vegetarian = models.BooleanField(default=False)
    cafeteria = models.ForeignKey(Cafeteria, on_delete=models.CASCADE, related_name='fixed_menu_options')
    photo_url = models.CharField(max_length=2048, validators=[URLValidator])
    avg_review_stars = models.FloatField(validators=[MinValueValidator(0), MaxValueValidator(5)],
                                         default=0, editable=False)

    def __str__(self):
        return self.name

    name.verbose_name = _('name')
    price.verbose_name = _('price')
    vegetarian.verbose_name = _('vegetarian')
    cafeteria.verbose_name = _('cafeteria')
    photo_url.verbose_name = _('photo_url')
    avg_review_stars.verbose_name = _('avg_review_stars')

    class Meta:
        verbose_name = _('fixed menu entry')
        verbose_name_plural = _('fixed menu entries')


class FixedMenuOptionReview(models.Model):
    option = models.ForeignKey(FixedMenuOption, on_delete=models.CASCADE, related_name='fixed_menu_option_reviews')
    stars = models.IntegerField(validators=[MinValueValidator(0), MaxValueValidator(5)])
    author_nick = models.CharField(max_length=64)
    review_time = models.DateTimeField()
    review_text = models.CharField(max_length=255, default="")

    def __str__(self):
        return f'{self.option} by {self.author_nick} with {self.stars} stars'

    def save(self, *args, **kwargs):
        curr_reviews = FixedMenuOptionReview.objects.filter(option_id=self.option.pk).count()
        if not self.pk:
            FixedMenuOption.objects.filter(pk=self.option.pk)\
                .update(avg_review_stars=self.calculate_new_avg_review(F('avg_review_stars'), curr_reviews))
        super().save(*args, **kwargs)

    def calculate_new_avg_review(self, old_review_stars, number_of_reviews):
        current_sum = old_review_stars * number_of_reviews + self.stars
        return current_sum / (number_of_reviews + 1)

    option.verbose_name = _('menu option')
    stars.verbose_name = _('rating')
    author_nick.verbose_name = _('author')
    review_time.verbose_name = _('timestamp')

    class Meta:
        verbose_name = _('menu entry review')
        verbose_name_plural = _('menu entry reviews')


class OccupancyStatsData(object):

    def __init__(self, **kwargs):
        for field in ('id', 'timestamp', 'occupancy', 'occupancy_relative'):
            setattr(self, field, kwargs.get(field, None))


class AverageDishReviewStatsData(object):

    def __init__(self, **kwargs):
        for field in ('id', 'timestamp', 'value'):
            setattr(self, field, kwargs.get(field, None))
