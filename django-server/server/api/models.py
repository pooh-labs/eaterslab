from django.db import models
from django.utils.translation import gettext_lazy as _
from django.core.validators import MinValueValidator, MaxValueValidator


class Cafeteria(models.Model):
    name = models.CharField(max_length=128)
    description = models.CharField(max_length=256)
    sub_description = models.CharField(max_length=512)
    latitude = models.FloatField(validators=[MinValueValidator(-90.0), MaxValueValidator(90.0)])
    longitude = models.FloatField(validators=[MinValueValidator(-180.0), MaxValueValidator(180.0)])
    capacity = models.IntegerField(validators=[MinValueValidator(1)])

    def __str__(self):
        return self.name


class Camera(models.Model):
    description = models.CharField(max_length=200)


class CameraEvent(models.Model):
    class EventType(models.TextChoices):
        MONITORING_STARTED = 'MS', _('monitoring_started')
        MONITORING_ENDED = 'ME', _('monitoring_ended')
        PERSON_ENTERED = 'PE', _('person_entered')
        PERSON_LEFT = 'PL', _('person_left')
    timestamp = models.DateTimeField()
    event_type = models.CharField(max_length=2, choices=EventType.choices)
    camera_id = models.ForeignKey(Camera, on_delete=models.CASCADE)

    def __str__(self):
        return "{} {} {}".format(self.camera_id, self.event_type, self.timestamp)
