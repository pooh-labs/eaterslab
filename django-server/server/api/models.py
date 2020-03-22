from django.db import models


class Cafeteria(models.Model):
    name = models.CharField(max_length=128)
    description = models.CharField(max_length=256)
    sub_description = models.CharField(max_length=512)
    latitude = models.FloatField()
    longitude = models.FloatField()
    capacity = models.IntegerField()

    def __str__(self):
        return self.name
