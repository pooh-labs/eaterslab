from rest_framework import serializers

from .models import Cafeteria, FixedMenuOption, FixedMenuOptionReview, MenuOptionTag


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
    class Meta:
        model = FixedMenuOption
        fields = ['name', 'price', 'photo_url', 'avg_review_stars']


class CafeteriaSerializer(serializers.ModelSerializer):
    class Meta:
        model = Cafeteria
        fields = ['id', 'name', 'description', 'sub_description', 'longitude', 'latitude',
                  'logo_url', 'address', 'opened_from', 'opened_to']
