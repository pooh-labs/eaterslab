from django.db import models
from random import random


class Cafeteria(models.Model):
    name = models.CharField(max_length=128)
    description = models.CharField(max_length=256)
    sub_description = models.CharField(max_length=512)
    latitude = models.FloatField(default=0.0)
    longitude = models.FloatField(default=0.0)
    capacity = models.IntegerField(default=30 * random())

    def __str__(self):
        return self.name
