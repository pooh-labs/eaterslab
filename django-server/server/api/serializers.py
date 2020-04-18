from rest_framework import serializers

from .models import Cafeteria, CameraEvent


class CafeteriaSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Cafeteria
        fields = ['id', 'name', 'description', 'sub_description', 'longitude', 'latitude', 'capacity']


class CameraEventSerializer(serializers.ModelSerializer):
    class Meta:
        model = CameraEvent
        fields = ['camera_id', 'event_type', 'timestamp']
