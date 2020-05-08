from rest_framework import serializers
from django.utils.translation import gettext_lazy as _


from .models import *


class FixedMenuOptionReviewSerializer(serializers.ModelSerializer):
    option = serializers.PrimaryKeyRelatedField(queryset=FixedMenuOption.objects.all(), many=False)

    class Meta:
        model = FixedMenuOptionReview
        fields = ['id', 'stars', 'author_nick', 'review_time', 'option']


class MenuOptionTagSerializer(serializers.ModelSerializer):
    class Meta:
        model = MenuOptionTag
        fields = ['id', 'name']


class FixedMenuOptionSerializer(serializers.ModelSerializer):
    class Meta:
        model = FixedMenuOption
        fields = ['id', 'name', 'price', 'photo_url', 'avg_review_stars']


class CafeteriaSerializer(serializers.ModelSerializer):
    class Meta:
        model = Cafeteria
        fields = ['id', 'name', 'description', 'sub_description', 'longitude', 'latitude',
                  'logo_url', 'address', 'opened_from', 'opened_to', 'fixed_menu_options']


class CameraSerializer(serializers.ModelSerializer):
    class Meta:
        model = Camera
        fields = ['description']


class CameraEventSerializer(serializers.ModelSerializer):
    class Meta:
        model = CameraEvent
        fields = ['event_type', 'timestamp']

    def to_representation(self, instance):
        ret = super().to_representation(instance)
        print(ret)
        print(instance)
        print(CameraEvent.EventType.choices)
        num = 0
        while num != ret["event_type"]:  # TODO test this loop
            print("loop")
            num += 1
        blank, ret["event_type"] = CameraEvent.EventType.choices[num]
        return ret

    def to_internal_value(self, data):
        print(data)
        print([i[1] for i in CameraEvent.EventType.choices])

        if not data.get("event_type") in [i[1] for i in CameraEvent.EventType.choices]:
            raise serializers.ValidationError({"event_type": ["Incorrect event_type"]})

        num = 0
        while CameraEvent.EventType.choices[num][1] != data.get("event_type"):
            num += 1
        data["event_type"] = num
        print(data)
        return super().to_internal_value(data)
