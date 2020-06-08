from rest_framework import serializers


from .models import *


class FixedMenuOptionReviewSerializer(serializers.ModelSerializer):
    option = serializers.PrimaryKeyRelatedField(queryset=FixedMenuOption.objects.all(), many=False)

    class Meta:
        model = FixedMenuOptionReview
        fields = ['id', 'stars', 'review_text', 'author_nick', 'review_time', 'option']


class FixedMenuOptionSerializer(serializers.ModelSerializer):
    class Meta:
        model = FixedMenuOption
        fields = ['id', 'name', 'price', 'photo_url', 'avg_review_stars']


class CafeteriaSerializer(serializers.ModelSerializer):
    open_from = serializers.TimeField(format='%H:%M')
    open_to = serializers.TimeField(format='%H:%M')

    class Meta:
        model = Cafeteria
        fields = ['id', 'name', 'description', 'sub_description', 'longitude', 'latitude',
                  'logo_url', 'address', 'open_from', 'open_to', 'occupancy', 'occupancy_relative']


class CameraSerializer(serializers.ModelSerializer):
    name = serializers.RegexField('^[a-zA-Z0-9-_]+$')

    class Meta:
        model = Camera
        fields = ['id', 'name', 'state', 'cafeteria']


class CameraEventSerializer(serializers.ModelSerializer):
    class Meta:
        model = CameraEvent
        fields = ['event_type', 'timestamp', 'event_value']

    def create(self, validated_data):
        camera = Camera.objects.get(pk=self.context['view'].kwargs['camera_pk'])
        if validated_data['event_type'] != CameraEventType.OCCUPANCY_OVERRIDE.value:
            validated_data['camera'] = camera
        validated_data['cafeteria'] = camera.cafeteria
        return CameraEvent.objects.create(**validated_data)


class OccupancyStatsSerializer(serializers.Serializer):
    id = serializers.IntegerField(read_only=True)
    timestamp = serializers.DateTimeField(read_only=True)
    occupancy = serializers.IntegerField(read_only=True)
    occupancy_relative = serializers.FloatField(read_only=True)

    def create(self, validated_data):
        return OccupancyStatsData(**validated_data)

    def update(self, instance, validated_data):
        for field, value in validated_data.items():
            setattr(instance, field, value)
        return instance


class AverageDishReviewStatsSerializer(serializers.Serializer):
    id = serializers.IntegerField(read_only=True)
    timestamp = serializers.DateTimeField(read_only=True)
    value = serializers.FloatField(read_only=True)

    def create(self, validated_data):
        return AverageDishReviewStatsData(**validated_data)

    def update(self, instance, validated_data):
        for field, value in validated_data.items():
            setattr(instance, field, value)
        return instance
