from django.db import models
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
