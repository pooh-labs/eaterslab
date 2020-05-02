from rest_framework import serializers

from .models import Cafeteria, FixedMenuOption, FixedMenuOptionReview, MenuOptionTag


class CafeteriaSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Cafeteria
        fields = ['id', 'name', 'description', 'sub_description', 'longitude', 'latitude',
                  'logo_url', 'address', 'opened_from', 'opened_to']


class FixedMenuOptionSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = FixedMenuOption
        fields = ['name', 'price', 'photo_url']


class FixedMenuOptionReviewSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = FixedMenuOptionReview
        fields = ['stars', 'author_nick', 'review_time']


class MenuOptionTagSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = MenuOptionTag
        fields = ['name']
