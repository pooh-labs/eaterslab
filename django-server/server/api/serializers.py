from rest_framework import serializers

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
