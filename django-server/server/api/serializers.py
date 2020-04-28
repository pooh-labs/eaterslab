from rest_framework import serializers

from .models import Cafeterias


class CafeteriaSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Cafeterias
        fields = ['id', 'name', 'description', 'sub_description', 'longitude', 'latitude', 'capacity']
