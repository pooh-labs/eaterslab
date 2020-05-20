from django.db import models
from django.utils.translation import gettext_lazy as _
from django.core.validators import MinValueValidator, MaxValueValidator, URLValidator
from django.db.models import F


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
    current_person_inside = models.IntegerField(validators=[MinValueValidator(0)], default=0)

    @property
    def occupancy(self):
        return float(min(self.current_person_inside, self.capacity)) / float(self.capacity) * 100.0

    def __str__(self):
        return self.name


class Camera(models.Model):
    description = models.CharField(max_length=200)
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
    event_value = models.IntegerField(default=None, blank=True, null=True)
    cafeteria = models.ForeignKey(Cafeteria, on_delete=models.CASCADE)
    camera = models.ForeignKey(Camera, on_delete=models.CASCADE)

    def save(self, *args, **kwargs):
        if self.event_type == CameraEvent.EventType.PERSON_ENTERED:
            self.cafeteria += 1
        elif self.event_type == CameraEvent.EventType.PERSON_LEFT and self.cafeteria.current_person_inside > 0:
            self.cafeteria.current_person_inside -= 1
        elif self.event_type == CameraEvent.EventType.OCCUPANCY_OVERRIDE:
            self.cafeteria.current_person_inside = self.event_value if self.event_value is not None else 0
        super().save(*args, **kwargs)

    def __str__(self):
        return "Event {} for: {} [{}]".format(self.event_type, self.cafeteria, self.timestamp)


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
