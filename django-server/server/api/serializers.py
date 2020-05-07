from rest_framework import serializers
from django.utils.translation import gettext_lazy as _

from .models import *


class FixedMenuOptionReviewSerializer(serializers.ModelSerializer):
    option = serializers.PrimaryKeyRelatedField(queryset=FixedMenuOption.objects.all(), many=False)

    class Meta:
        model = FixedMenuOptionReview
        fields = ['stars', 'author_nick', 'review_time', 'option']


class MenuOptionTagSerializer(serializers.ModelSerializer):
    class Meta:
        model = MenuOptionTag
        fields = ['name']


class FixedMenuOptionSerializer(serializers.ModelSerializer):
    menu_option_tags = MenuOptionTagSerializer(many=True, read_only=True)
    fixed_menu_option_reviews = FixedMenuOptionReviewSerializer(many=True, read_only=True)

    class Meta:
        model = FixedMenuOption
        fields = ['name', 'price', 'photo_url', 'menu_option_tags', 'fixed_menu_option_reviews']


class CafeteriaSerializer(serializers.ModelSerializer):
    fixed_menu_options = FixedMenuOptionSerializer(many=True, read_only=True)

    class Meta:
        model = Cafeteria
        fields = ['id', 'name', 'description', 'sub_description', 'longitude', 'latitude',
                  'logo_url', 'address', 'opened_from', 'opened_to', 'fixed_menu_options']


class CameraEventSerializer(serializers.ModelSerializer):
    class Meta:
        model = CameraEvent
        fields = ['camera_id', 'event_type', 'timestamp']

    def to_representation(self, instance):
        ret = super().to_representation(instance)
        print(ret)
        print(instance)
        print(CameraEvent.EventType.choices)
        num = 0
        while num != ret["event_type"]:
            print("loop")
            num += 1
        blank, ret["event_type"] = CameraEvent.EventType.choices[num]
        return ret

    def to_internal_value(self, data):
        print(data)
        print([i[1] for i in CameraEvent.EventType.choices])
        if data.get("event_type") in [i[1] for i in CameraEvent.EventType.choices]:
            num = 0
            while CameraEvent.EventType.choices[num][1] != data.get("event_type"):
                num += 1
            data["event_type"] = num
            print(data)
            return super().to_internal_value(data)
        raise serializers.ValidationError({"event_type": ["Incorrect event_type"]})
