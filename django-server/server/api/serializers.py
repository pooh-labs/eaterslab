from rest_framework import serializers

from .models import Cafeteria


class CafeteriaSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Cafeteria
        fields = ['id', 'name', 'description', 'sub_description', 'longitude', 'latitude', 'capacity']
