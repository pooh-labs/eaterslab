from rest_framework import serializers


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
                  'logo_url', 'address', 'opened_from', 'opened_to', 'occupancy', 'occupancy_relative']


class CameraSerializer(serializers.ModelSerializer):
    class Meta:
        model = Camera
        fields = ['description', 'state', 'cafeteria']


class CameraEventSerializer(serializers.ModelSerializer):
    class Meta:
        model = CameraEvent
        fields = ['event_type', 'timestamp', 'event_value']

    def create(self, validated_data):
        camera = Camera.objects.get(pk=self.context['view'].kwargs['camera_pk'])
        validated_data['camera'] = camera
        validated_data['cafeteria'] = camera.cafeteria
        return CameraEvent.objects.create(**validated_data)
